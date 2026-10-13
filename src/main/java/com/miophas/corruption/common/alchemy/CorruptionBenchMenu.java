package com.miophas.corruption.common.alchemy;

import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.common.registry.ModRecipes;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The corruption bench screen's slots: five inputs on the points of the pentagram, the result in its
 * centre, then the player's inventory. As with a crafting table, the inputs belong to whoever has the
 * screen open and go back into their inventory when it closes.
 */
public final class CorruptionBenchMenu extends AbstractContainerMenu {
    /** Item positions inside gui_corruption_bench.png, measured from its five rings, in slot order: top left, top right, left, right, bottom. */
    public static final int[][] INPUT_POSITIONS = {{48, 35}, {115, 35}, {28, 97}, {135, 97}, {82, 139}};
    /** The texture draws no output slot; the empty inner pentagon is the only room for one. */
    public static final int RESULT_X = 82, RESULT_Y = 80;
    public static final int INVENTORY_X = 9, INVENTORY_Y = 174, HOTBAR_Y = 232;
    public static final int RESULT_SLOT = AlchemyRecipe.SLOTS, FIRST_PLAYER_SLOT = RESULT_SLOT + 1, END_PLAYER_SLOT = FIRST_PLAYER_SLOT + 36;

    private final SimpleContainer inputs = new SimpleContainer(AlchemyRecipe.SLOTS) {
        @Override public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };
    private final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    /** Client side: the server sends the contents. */
    public CorruptionBenchMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public CorruptionBenchMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.CORRUPTION_BENCH.get(), id);
        this.access = access;
        this.player = inventory.player;
        for (int slot = 0; slot < AlchemyRecipe.SLOTS; slot++) addSlot(new Slot(inputs, slot, INPUT_POSITIONS[slot][0], INPUT_POSITIONS[slot][1]));
        addSlot(new ResultSlot());
        for (int row = 0; row < 3; row++)
            for (int column = 0; column < 9; column++)
                addSlot(new Slot(inventory, 9 + row * 9 + column, INVENTORY_X + column * 18, INVENTORY_Y + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, INVENTORY_X + column * 18, HOTBAR_Y));
    }

    /** When a shaped and a shapeless recipe both fit, the exact arrangement wins. */
    private Optional<AlchemyRecipe> recipe(AlchemyInput input) {
        return player.level().getRecipeManager().getRecipesFor(ModRecipes.ALCHEMY.get(), input, player.level()).stream()
                .map(RecipeHolder::value).min(Comparator.comparing(recipe -> !(recipe instanceof ShapedAlchemyRecipe)));
    }

    /** Only the server decides what the result is; the client is told. */
    @Override public void slotsChanged(Container container) {
        if (!(player instanceof ServerPlayer)) return;
        AlchemyInput input = AlchemyInput.of(inputs);
        result.setItem(0, recipe(input).map(found -> found.assemble(input, player.level().registryAccess()))
                .filter(stack -> stack.isItemEnabled(player.level().enabledFeatures())).orElse(ItemStack.EMPTY));
    }

    /** Takes the ingredients for one craft and hands the player whatever containers did not fit back on the bench. */
    private void consume() {
        recipe(AlchemyInput.of(inputs)).map(found -> found.craft(inputs)).ifPresent(overflow -> {
            for (ItemStack returned : overflow) if (!player.getInventory().add(returned)) player.drop(returned, false);
        });
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == RESULT_SLOT) {
            stack.getItem().onCraftedBy(stack, player.level(), player);
            if (!moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_PLAYER_SLOT, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, original);
        } else if (index < RESULT_SLOT) {
            if (!moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_PLAYER_SLOT, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, AlchemyRecipe.SLOTS, false)) {
            // Nothing fits on the bench: move between the inventory rows and the hotbar instead.
            int hotbar = END_PLAYER_SLOT - 9;
            if (index < hotbar ? !moveItemStackTo(stack, hotbar, END_PLAYER_SLOT, false) : !moveItemStackTo(stack, FIRST_PLAYER_SLOT, hotbar, false))
                return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        if (index == RESULT_SLOT) player.drop(stack, false);
        return original;
    }

    @Override public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) { return slot.container != result && super.canTakeItemForPickAll(stack, slot); }
    @Override public boolean stillValid(Player player) { return stillValid(access, player, ModBlocks.CORRUPTION_BENCH.get()); }

    /** Closing the screen packs whatever is on the bench back into the player's inventory, dropping what does not fit. */
    @Override public void removed(Player player) {
        super.removed(player);
        access.execute((level, pos) -> clearContainer(player, inputs));
    }

    /** Counts what is taken the way the crafting table's result slot does, so crafting statistics and events still fire. */
    private final class ResultSlot extends Slot {
        private int removeCount;

        ResultSlot() { super(result, 0, RESULT_X, RESULT_Y); }

        @Override public boolean mayPlace(ItemStack stack) { return false; }
        @Override public ItemStack remove(int amount) {
            if (hasItem()) removeCount += Math.min(amount, getItem().getCount());
            return super.remove(amount);
        }
        @Override protected void onQuickCraft(ItemStack stack, int amount) {
            removeCount += amount;
            checkTakeAchievements(stack);
        }
        @Override protected void onSwapCraft(int numItemsCrafted) { removeCount += numItemsCrafted; }
        @Override protected void checkTakeAchievements(ItemStack stack) {
            if (removeCount > 0) {
                stack.onCraftedBy(player.level(), player, removeCount);
                net.neoforged.neoforge.event.EventHooks.firePlayerCraftingEvent(player, stack, inputs);
            }
            removeCount = 0;
        }
        @Override public void onTake(Player player, ItemStack stack) {
            checkTakeAchievements(stack);
            consume();
        }
    }
}
