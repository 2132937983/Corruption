package com.miophas.corruption.common.arcane;

import com.miophas.corruption.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What the arcane pedestal holds: one parchment bearing a string of the arcane, lying open on it. The pedestal
 * does nothing over time; a wand used on it copies the string into the wand's grimoire, and the parchment is
 * left where it lies, to be read again.
 */
public final class ArcanePedestalBlockEntity extends BlockEntity {
    private static final String PARCHMENT = "Parchment";

    private ItemStack parchment = ItemStack.EMPTY;

    public ArcanePedestalBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ARCANE_PEDESTAL.get(), pos, state); }

    /** The parchment lying on it, or nothing. */
    public ItemStack parchment() { return parchment; }
    public boolean isEmpty() { return parchment.isEmpty(); }

    /** Puts this parchment on, one of it. */
    public void put(ItemStack stack) { parchment = stack.copyWithCount(1); changed(); }

    /** Takes what lies there off, leaving nothing. */
    public ItemStack take() { ItemStack lying = parchment; parchment = ItemStack.EMPTY; changed(); return lying; }

    /** Told to the clients, so whoever draws the pedestal sees what lies on it. */
    private void changed() {
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        parchment = ItemStack.parseOptional(registries, tag.getCompound(PARCHMENT));
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!parchment.isEmpty()) tag.put(PARCHMENT, parchment.save(registries));
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
