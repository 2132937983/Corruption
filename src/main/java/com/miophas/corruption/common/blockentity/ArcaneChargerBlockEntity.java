package com.miophas.corruption.common.blockentity;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyBuffer;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.api.energy.EnergyCharger;
import com.miophas.corruption.common.energy.EnergyCharging;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModParticles;
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
 * <p>What it holds, and how much its store has, are told to the clients, so whoever draws the charger sees the
 * item over it and a ring of sparks about the item, as many as the store is full ({@link #sparks()}); while it
 * charges, the ring turns faster and a spark flies from it into the item every tick.
 */
public final class ArcaneChargerBlockEntity extends BlockEntity implements EnergyCharger {
    /** What its store holds when full: five times what a wand can carry. */
    public static final long CAPACITY = CorruptionEnergyApi.kilo(100);
    /** How many sparks stand about the item when its store is full. */
    public static final int SPARKS = 6;
    /** Where the item is held over the block, which is the middle of the ring of sparks, and how wide the ring is. */
    public static final double ITEM_HEIGHT = 1.15, RING_RADIUS = .42;
    /** How far the ring turns a tick, in degrees: slowly while it waits, fast while it charges. */
    private static final double IDLE_TURN = 1.5, CHARGING_TURN = 7;
    private static final String ITEM = "Item", ENERGY = "Energy";

    private ItemStack charge = ItemStack.EMPTY;
    private final CorruptionEnergyBuffer energy = CorruptionEnergyApi.buffer(CAPACITY, Long.MAX_VALUE, Long.MAX_VALUE, this::changed);
    /** The store as other mods and pouring by hand see it: energy may be put in, never taken out. */
    private final CorruptionEnergyStorage outside = energy.view(Long.MAX_VALUE, 0);
    /** How far round the ring has turned, now and a tick ago, in degrees; kept by the client alone, for drawing. */
    private double turned, turnedBefore;

    public ArcaneChargerBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ARCANE_CHARGER.get(), pos, state); }

    public long energy() { return energy.getEnergy(); }
    /** How full its store is, from none to one. */
    public double fill() { return (double) energy.getEnergy() / CAPACITY; }
    /** What other mods are offered through the capability. */
    public CorruptionEnergyStorage energyStorage() { return outside; }

    @Override public ItemStack charge() { return charge; }
    @Override public boolean isEmpty() { return charge.isEmpty(); }

    /** Sets this on it to be charged, one of it. */
    @Override public void put(ItemStack stack) { charge = stack.copyWithCount(1); changed(); }

    /** Takes what lies on it off, leaving nothing. */
    @Override public ItemStack take() { ItemStack lying = charge; charge = ItemStack.EMPTY; changed(); return lying; }

    /** Whether it is charging now: what lies there is a tool that is not yet full, and the store has something to give. */
    public boolean charging() { return energy.getEnergy() > 0 && EnergyCharging.chargeable(charge); }

    /** How many sparks stand about the item: none for an empty store, one for any energy at all, all of them when full. */
    public int sparks() {
        long held = energy.getEnergy();
        return held <= 0 ? 0 : (int) Math.max(1, Math.min(SPARKS, Math.ceil((double) held / CAPACITY * SPARKS)));
    }

    /** How far round the ring has turned, between the last tick and this one, in degrees. */
    public double turned(float partialTick) { return turnedBefore + (turned - turnedBefore) * partialTick; }

    /**
     * Where a spark of the ring stands, from the block's corner: evenly round the item, the ring turned this far,
     * each spark rising and falling a little after the one before it, so the ring ripples.
     */
    public static double[] ringPoint(int spark, int sparks, double turnedDegrees, double time) {
        double angle = Math.toRadians(turnedDegrees) + spark * Math.PI * 2 / Math.max(1, sparks);
        return new double[]{.5 + Math.cos(angle) * RING_RADIUS, ITEM_HEIGHT + Math.sin(time / 9.0 + spark * 1.7) * .05, .5 + Math.sin(angle) * RING_RADIUS};
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArcaneChargerBlockEntity charger) { EnergyCharging.step(charger.charge, charger.energy); }

    /** The ring turns, faster while it charges; and while it charges, a spark flies from the ring into the item each tick. */
    public static void clientTick(Level level, BlockPos pos, BlockState state, ArcaneChargerBlockEntity charger) {
        boolean charging = charger.charging();
        charger.turnedBefore = charger.turned;
        charger.turned += charging ? CHARGING_TURN : IDLE_TURN;
        if (charger.turned >= 360) { charger.turned -= 360; charger.turnedBefore -= 360; }
        if (!charging) return;
        int sparks = charger.sparks();
        long time = level.getGameTime();
        double[] from = ringPoint((int) (time % sparks), sparks, charger.turned, time);
        double speed = 1.0 / ModParticles.SPARK_FLIGHT;
        level.addParticle(ModParticles.CORRUPTION_SPARK_PARTICLE.get(), pos.getX() + from[0], pos.getY() + from[1], pos.getZ() + from[2],
                (.5 - from[0]) * speed, (ITEM_HEIGHT - from[1]) * speed, (.5 - from[2]) * speed);
    }

    /** Told to the clients, so whoever draws the charger sees what lies on it and how full its store is. */
    private void changed() {
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
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
