package com.miophas.corruption.common.altar;

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

/** What an Altar Pillar holds: one item, which players near it see floating over it. */
public final class AltarPillarBlockEntity extends BlockEntity {
    private ItemStack item = ItemStack.EMPTY;

    public AltarPillarBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ALTAR_PILLAR.get(), pos, state); }

    public ItemStack item() { return item; }
    public boolean isEmpty() { return item.isEmpty(); }

    /** Sets what it holds, one of the stack at most. */
    public void set(ItemStack stack) {
        item = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    /** Takes off what it holds, and answers it. */
    public ItemStack take() {
        ItemStack taken = item;
        set(ItemStack.EMPTY);
        return taken;
    }

    /**
     * What players near it are told: what it holds, written even when it is nothing. The game drops an update
     * with nothing in it, and an emptied pillar would go on showing what it last held.
     */
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("Item", item.isEmpty() ? new CompoundTag() : item.save(registries));
        return tag;
    }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        item = ItemStack.parseOptional(registries, tag.getCompound("Item"));
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!item.isEmpty()) tag.put("Item", item.save(registries));
    }
}
