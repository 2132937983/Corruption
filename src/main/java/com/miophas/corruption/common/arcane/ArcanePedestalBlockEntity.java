package com.miophas.corruption.common.arcane;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import com.miophas.corruption.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What the arcane pedestal holds: a parchment bearing a string of the arcane, and the wand to be taught its
 * spell, both laid on it through its screen (see {@link ArcanePedestalMenu}). The pedestal does nothing over
 * time; teaching is done from the screen, and costs nothing: the parchment is left where it lies.
 *
 * Players near it see what lies on it, floating over it, so whatever changes is sent to them. Hoppers have no
 * business with it: wands and parchment are laid on and taken off by hand.
 */
public final class ArcanePedestalBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int PARCHMENT = 0, WAND = 1, SLOTS = 2;
    private static final int[] NO_SLOTS = {};

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

    public ArcanePedestalBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ARCANE_PEDESTAL.get(), pos, state); }

    /** The parchment lying on it, or nothing. */
    public ItemStack parchment() { return items.get(PARCHMENT); }
    /** The wand lying on it, or nothing. */
    public ItemStack wand() { return items.get(WAND); }

    @Override protected Component getDefaultName() { return Component.translatable("container.corruption.corruption_arcane_pedestal"); }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override public int getContainerSize() { return SLOTS; }
    @Override public int getMaxStackSize() { return 1; }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ArcanePedestalMenu(id, inventory, this, level == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(level, worldPosition));
    }

    /** Whether an item belongs in a slot: parchment bearing a string, or a casting tool. */
    public static boolean fits(int slot, ItemStack stack) {
        return switch (slot) {
            case PARCHMENT -> ArcaneApi.inscription(stack) != null;
            case WAND -> stack.getItem() instanceof ArcaneCastingItem;
            default -> false;
        };
    }

    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return fits(slot, stack); }
    @Override public int[] getSlotsForFace(Direction side) { return NO_SLOTS; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }

    /** Whatever changes, players near it are told, so that they see it floating there. */
    @Override public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    /**
     * What players near it are told: what lies on it, written even when it is nothing. The game drops an update
     * with nothing in it, and a pedestal emptied would go on showing what it last held.
     */
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, items, true, registries);
        return tag;
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
