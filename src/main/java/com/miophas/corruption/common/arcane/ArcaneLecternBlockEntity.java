package com.miophas.corruption.common.arcane;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.common.block.ArcaneLecternBlock;
import com.miophas.corruption.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What the corrupted arcane lectern holds: a key to the arcane, such as the Clavicula Salomonis, whose strings
 * its screen shows; blank parchment and ink; and the copies made, each bearing one string. Copies are made
 * from the screen (see {@link ArcaneLecternMenu}); the lectern itself does nothing over time.
 *
 * Hoppers may bring parchment from above and ink from the sides, and take from below the copies and whatever
 * a used up ink left behind, such as its empty bottle. The key is placed and taken by hand.
 */
public final class ArcaneLecternBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int KEY = 0, PARCHMENT = 1, INK = 2, OUTPUT = 3, SLOTS = 4;
    private static final int[] FROM_ABOVE = {PARCHMENT}, FROM_BELOW = {OUTPUT, INK}, FROM_SIDES = {INK};

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

    public ArcaneLecternBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ARCANE_LECTERN.get(), pos, state); }

    @Override protected Component getDefaultName() { return Component.translatable("container.corruption.corruption_arcane_lectern"); }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override public int getContainerSize() { return SLOTS; }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ArcaneLecternMenu(id, inventory, this, level == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(level, worldPosition));
    }

    /** Whether an item belongs in a slot: a key, blank parchment, ink; nothing is put among the copies. */
    public static boolean fits(int slot, ItemStack stack) {
        return switch (slot) {
            case KEY -> ArcaneApi.isKey(stack);
            case PARCHMENT -> ArcaneApi.isBlankParchment(stack);
            case INK -> ArcaneApi.isInk(stack);
            default -> false;
        };
    }

    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return fits(slot, stack); }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.UP ? FROM_ABOVE : side == Direction.DOWN ? FROM_BELOW : FROM_SIDES; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return slot != KEY && canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == OUTPUT || slot == INK && side == Direction.DOWN && !ArcaneApi.isInk(stack); }

    /** Whatever changes, the block shows whether a key lies open on it. */
    @Override public void setChanged() {
        super.setChanged();
        if (level == null || level.isClientSide) return;
        BlockState state = getBlockState();
        boolean open = !items.get(KEY).isEmpty();
        if (state.hasProperty(ArcaneLecternBlock.HAS_KEY) && state.getValue(ArcaneLecternBlock.HAS_KEY) != open)
            level.setBlock(worldPosition, state.setValue(ArcaneLecternBlock.HAS_KEY, open), 3);
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
}
