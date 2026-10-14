package com.miophas.corruption.common.cooking;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.common.energy.EnergyIntake;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.common.registry.ModRecipes;
import com.miophas.corruption.common.transmutation.ArcaneTransmuterBlockEntity;
import com.miophas.corruption.core.cooking.BrassCauldronRules;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * The brass cauldron's screen: its tank on the left; the seven things it cooks laid out as a hexagon, its
 * six corners and its middle; under the hexagon, on the left, the slot that takes energy items, and on the
 * right the one that takes an inscribed parchment; the dish on the right; then the player's inventory. The
 * positions are those drawn in gui_brass_cauldron.png.
 */
public final class BrassCauldronMenu extends AbstractContainerMenu {
    /** Where each input lies: the middle first, then the corners from the top, going round to the right. */
    public static final int[][] INPUT_SLOTS = {{54, 40}, {54, 18}, {73, 29}, {73, 51}, {54, 62}, {35, 51}, {35, 29}};
    public static final int ENERGY_X = 35, ENERGY_Y = 84, OUTPUT_X = 126, OUTPUT_Y = 40, PARCHMENT_X = 73, PARCHMENT_Y = 84;
    /** The tank's window: where fluid is drawn, filling from the bottom. */
    public static final int TANK_X = 8, TANK_Y = 18, TANK_WIDTH = 16, TANK_HEIGHT = 56;
    public static final int INVENTORY_X = 8, INVENTORY_Y = 118, HOTBAR_Y = 176;
    public static final int FIRST_PLAYER_SLOT = BrassCauldronBlockEntity.SLOTS, END_PLAYER_SLOT = FIRST_PLAYER_SLOT + 36;

    private final Container cauldron;
    private final ContainerData data;
    private final Level level;

    /** Client side: the server sends the contents and the numbers. */
    public BrassCauldronMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(BrassCauldronBlockEntity.SLOTS), new SimpleContainerData(BrassCauldronBlockEntity.DATA_COUNT));
    }

    public BrassCauldronMenu(int id, Inventory inventory, Container cauldron, ContainerData data) {
        super(ModMenus.BRASS_CAULDRON.get(), id);
        checkContainerSize(cauldron, BrassCauldronBlockEntity.SLOTS);
        checkContainerDataCount(data, BrassCauldronBlockEntity.DATA_COUNT);
        this.cauldron = cauldron; this.data = data; this.level = inventory.player.level();
        for (int input = 0; input < BrassCauldronBlockEntity.INPUTS; input++) addSlot(new Slot(cauldron, input, INPUT_SLOTS[input][0], INPUT_SLOTS[input][1]));
        addSlot(new Slot(cauldron, BrassCauldronBlockEntity.ENERGY, ENERGY_X, ENERGY_Y) {
            @Override public boolean mayPlace(ItemStack stack) { return EnergyIntake.powers(stack); }
            @Override public int getMaxStackSize(ItemStack stack) { return EnergyIntake.staysWhenEmpty(stack) ? 1 : super.getMaxStackSize(stack); }
        });
        addSlot(new Slot(cauldron, BrassCauldronBlockEntity.OUTPUT, OUTPUT_X, OUTPUT_Y) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        addSlot(new Slot(cauldron, BrassCauldronBlockEntity.PARCHMENT, PARCHMENT_X, PARCHMENT_Y) {
            @Override public boolean mayPlace(ItemStack stack) { return BrassCauldronBlockEntity.inscribed(stack); }
            @Override public int getMaxStackSize() { return 1; }
        });
        for (int row = 0; row < 3; row++)
            for (int column = 0; column < 9; column++)
                addSlot(new Slot(inventory, 9 + row * 9 + column, INVENTORY_X + column * 18, INVENTORY_Y + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, INVENTORY_X + column * 18, HOTBAR_Y));
        addDataSlots(data);
    }

    private long join(int low, int high) { return ArcaneTransmuterBlockEntity.join(data.get(low), data.get(high)); }
    public long energy() { return join(BrassCauldronBlockEntity.DATA_ENERGY_LOW, BrassCauldronBlockEntity.DATA_ENERGY_HIGH); }
    /** What the dish cooking costs in all, or 0 when nothing in it makes a dish, or it costs nothing. */
    public long cost() { return join(BrassCauldronBlockEntity.DATA_COST_LOW, BrassCauldronBlockEntity.DATA_COST_HIGH); }
    /** Whether what is in it makes a dish. */
    public boolean cooking() { return data.get(BrassCauldronBlockEntity.DATA_TIME) > 0; }
    /** The fluid in its tank, as much as there is of it. */
    public FluidStack fluid() {
        Fluid fluid = BuiltInRegistries.FLUID.byId(data.get(BrassCauldronBlockEntity.DATA_FLUID));
        int amount = data.get(BrassCauldronBlockEntity.DATA_FLUID_AMOUNT);
        return amount <= 0 ? FluidStack.EMPTY : new FluidStack(fluid, amount);
    }
    public float fluidFraction() { return CorruptionEnergyApi.fraction(fluid().getAmount(), BrassCauldronRules.TANK); }
    public float energyFraction() { return CorruptionEnergyApi.fraction(energy(), BrassCauldronRules.CAPACITY); }
    /** How far the fuel or item now burning has been turned into energy. */
    public float burnFraction() { return CorruptionEnergyApi.fraction(data.get(BrassCauldronBlockEntity.DATA_BURNT), data.get(BrassCauldronBlockEntity.DATA_BURN_TICKS)); }
    public float progressFraction() { return CorruptionEnergyApi.fraction(data.get(BrassCauldronBlockEntity.DATA_PROGRESS), data.get(BrassCauldronBlockEntity.DATA_TIME)); }

    /** Whether some cooking recipe takes this item, in any amount. */
    private boolean cookable(ItemStack stack) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.BRASS_COOKING.get()).stream()
                .anyMatch(recipe -> recipe.value().ingredients().stream().anyMatch(ingredient -> ingredient.ingredient().test(stack)));
    }

    /** Shift-click: out of the cauldron into the inventory; from the inventory to the parchment slot, the energy slot or the inputs, whichever takes it, else between inventory and hotbar. */
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index < FIRST_PLAYER_SLOT) {
            if (!moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_PLAYER_SLOT, index == BrassCauldronBlockEntity.OUTPUT)) return ItemStack.EMPTY;
            if (index == BrassCauldronBlockEntity.OUTPUT) slot.onQuickCraft(stack, original);
        } else if (BrassCauldronBlockEntity.inscribed(stack)) {
            if (!moveItemStackTo(stack, BrassCauldronBlockEntity.PARCHMENT, BrassCauldronBlockEntity.PARCHMENT + 1, false)) return ItemStack.EMPTY;
        } else if (EnergyIntake.powers(stack)) {
            if (!moveItemStackTo(stack, BrassCauldronBlockEntity.ENERGY, BrassCauldronBlockEntity.ENERGY + 1, false)) return ItemStack.EMPTY;
        } else if (cookable(stack)) {
            if (!moveItemStackTo(stack, 0, BrassCauldronBlockEntity.INPUTS, false)) return ItemStack.EMPTY;
        } else {
            int hotbar = END_PLAYER_SLOT - 9;
            if (index < hotbar ? !moveItemStackTo(stack, hotbar, END_PLAYER_SLOT, false) : !moveItemStackTo(stack, FIRST_PLAYER_SLOT, hotbar, false))
                return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    @Override public boolean stillValid(Player player) { return cauldron.stillValid(player); }
}
