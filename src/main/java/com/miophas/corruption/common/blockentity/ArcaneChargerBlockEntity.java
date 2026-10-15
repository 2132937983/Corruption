package com.miophas.corruption.common.blockentity;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyBuffer;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import com.miophas.corruption.api.energy.EnergyCharger;
import com.miophas.corruption.common.energy.EnergyCharging;
import com.miophas.corruption.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The arcane charger's contents and its work: one item lies on it, and the Corruption Energy its store holds is
 * poured into that item a step at a time — a wand, a tool of an add-on's, and only a tool: a battery, which
 * pours out what it carries rather than being filled, is left alone. It holds no fuel of its own and has no
 * screen: energy comes in from outside, through {@link #energyStorage()}, which is what pouring by hand (a
 * player right-clicking with a candle) and any other mod's cables fill; what it holds is open to them to fill,
 * never to drain.
 *
 * <p>What it holds, and how much it has left to give, are told to the clients, so whoever draws the charger sees
 * the item lying on it and the sparks standing over it.
 */
public final class ArcaneChargerBlockEntity extends BlockEntity implements EnergyCharger {
    /** What its store holds when full: five times what a wand can carry. */
    public static final long CAPACITY = CorruptionEnergyApi.kilo(100);
    /** How many sparks are sent after one drawing, however many steps have passed since the last one. */
    private static final int SPARKS_AT_ONCE = 4;
    private static final String ITEM = "Item", ENERGY = "Energy";

    private ItemStack charge = ItemStack.EMPTY;
    private final CorruptionEnergyBuffer energy = CorruptionEnergyApi.buffer(CAPACITY, Long.MAX_VALUE, Long.MAX_VALUE, this::changed);
    /** The store as other mods and pouring by hand see it: energy may be put in, never taken out. */
    private final CorruptionEnergyStorage outside = energy.view(Long.MAX_VALUE, 0);
    /** What the item held when it was last looked at, and how much of it has been charged but not yet shown. */
    private long seenItem = -1, owed;

    public ArcaneChargerBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ARCANE_CHARGER.get(), pos, state); }

    public long energy() { return energy.getEnergy(); }
    /** How full its store is, from none to one: what the sparks standing over it are counted from. */
    public double fill() { return (double) energy.getEnergy() / CAPACITY; }
    /** What other mods are offered through the capability. */
    public CorruptionEnergyStorage energyStorage() { return outside; }

    @Override public ItemStack charge() { return charge; }
    @Override public boolean isEmpty() { return charge.isEmpty(); }

    /** Sets this on it to be charged, one of it. */
    @Override public void put(ItemStack stack) { charge = stack.copyWithCount(1); changed(); }

    /** Takes what lies on it off, leaving nothing. */
    @Override public ItemStack take() { ItemStack lying = charge; charge = ItemStack.EMPTY; changed(); return lying; }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArcaneChargerBlockEntity charger) { charger.tick(); }

    /** One step of its store goes into what lies on it each tick, until the store is spent or the tool full. */
    private void tick() { if (EnergyCharging.step(charge, energy) > 0) changed(); }

    /**
     * How many sparks the item is owed for the energy it has taken since this was last asked: one for each step,
     * since every step is a moment of the charging worth showing. What the clients are sent is the item as it now
     * stands, so an item holding more than when it was last looked at is one the charger has just been filling.
     * Called from the block's animateTick, so only ever on the client.
     */
    @Override public int sparksOwed() {
        long held = CorruptionEnergyTool.held(charge);
        if (seenItem < 0) { seenItem = held; return 0; }
        owed += Math.max(0, held - seenItem);
        seenItem = held;
        long step = Math.max(1, CorruptionEnergyTool.of(charge).step());
        int sparks = (int) Math.min(SPARKS_AT_ONCE, owed / step);
        owed -= sparks * step;
        return sparks;
    }

    /** Told to the clients, so whoever draws the charger sees what lies on it and how full its store is. */
    private void changed() {
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        charge = ItemStack.parseOptional(registries, tag.getCompound(ITEM));
        energy.setEnergy(tag.getLong(ENERGY));
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!charge.isEmpty()) tag.put(ITEM, charge.save(registries));
        tag.putLong(ENERGY, energy.getEnergy());
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
