package com.miophas.corruption.common.transmutation;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.common.energy.EnergyIntake;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.common.registry.ModRecipes;
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

/**
 * The arcane transmuter's screen, laid out as a furnace's: what is to be transmuted above, fuel below, the
 * result to the right, then the player's inventory. The positions are those drawn in
 * gui_corruption_arcane_transmuter.png.
 */
public final class ArcaneTransmuterMenu extends AbstractContainerMenu {
    public static final int INPUT_X = 56, INPUT_Y = 17, FUEL_X = 56, FUEL_Y = 53, OUTPUT_X = 116, OUTPUT_Y = 35;
    public static final int INVENTORY_X = 8, INVENTORY_Y = 84, HOTBAR_Y = 142;
    public static final int FIRST_PLAYER_SLOT = ArcaneTransmuterBlockEntity.SLOTS, END_PLAYER_SLOT = FIRST_PLAYER_SLOT + 36;

    private final Container machine;
    private final ContainerData data;
    private final Level level;

    /** Client side: the server sends the contents and the numbers. */
    public ArcaneTransmuterMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(ArcaneTransmuterBlockEntity.SLOTS), new SimpleContainerData(ArcaneTransmuterBlockEntity.DATA_COUNT));
    }

    public ArcaneTransmuterMenu(int id, Inventory inventory, Container machine, ContainerData data) {
        super(ModMenus.ARCANE_TRANSMUTER.get(), id);
        checkContainerSize(machine, ArcaneTransmuterBlockEntity.SLOTS);
        checkContainerDataCount(data, ArcaneTransmuterBlockEntity.DATA_COUNT);
        this.machine = machine; this.data = data; this.level = inventory.player.level();
        addSlot(new Slot(machine, ArcaneTransmuterBlockEntity.INPUT, INPUT_X, INPUT_Y));
        addSlot(new Slot(machine, ArcaneTransmuterBlockEntity.FUEL, FUEL_X, FUEL_Y) {
            @Override public boolean mayPlace(ItemStack stack) { return EnergyIntake.powers(stack); }
            @Override public int getMaxStackSize(ItemStack stack) { return EnergyIntake.staysWhenEmpty(stack) ? 1 : super.getMaxStackSize(stack); }
        });
        addSlot(new Slot(machine, ArcaneTransmuterBlockEntity.OUTPUT, OUTPUT_X, OUTPUT_Y) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        for (int row = 0; row < 3; row++)
            for (int column = 0; column < 9; column++)
                addSlot(new Slot(inventory, 9 + row * 9 + column, INVENTORY_X + column * 18, INVENTORY_Y + row * 18));
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, INVENTORY_X + column * 18, HOTBAR_Y));
        addDataSlots(data);
    }

    public long energy() { return ArcaneTransmuterBlockEntity.join(data.get(ArcaneTransmuterBlockEntity.DATA_ENERGY_LOW), data.get(ArcaneTransmuterBlockEntity.DATA_ENERGY_HIGH)); }
    /** What the transmutation in the input costs in all, or 0 when nothing there can be transmuted. */
    public long cost() { return ArcaneTransmuterBlockEntity.join(data.get(ArcaneTransmuterBlockEntity.DATA_COST_LOW), data.get(ArcaneTransmuterBlockEntity.DATA_COST_HIGH)); }
    public float energyFraction() { return CorruptionEnergyApi.fraction(energy(), CorruptionEnergyApi.transmuterCapacity()); }
    /** How far the fuel now burning has been turned into energy. */
    public float burnFraction() { return CorruptionEnergyApi.fraction(data.get(ArcaneTransmuterBlockEntity.DATA_BURNT), data.get(ArcaneTransmuterBlockEntity.DATA_BURN_TICKS)); }
    public float progressFraction() { return CorruptionEnergyApi.fraction(data.get(ArcaneTransmuterBlockEntity.DATA_PROGRESS), CorruptionEnergyApi.transmuteTicks()); }

    /** Whether some arcane transmutation takes this item, in any amount. */
    private boolean transmutable(ItemStack stack) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.ARCANE_TRANSMUTATION.get()).stream().anyMatch(recipe -> recipe.value().ingredient().ingredient().test(stack));
    }

    /** Shift-click: out of the machine into the inventory; from the inventory to fuel or input, whichever takes it, else between inventory and hotbar. */
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index < FIRST_PLAYER_SLOT) {
            if (!moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_PLAYER_SLOT, index == ArcaneTransmuterBlockEntity.OUTPUT)) return ItemStack.EMPTY;
            if (index == ArcaneTransmuterBlockEntity.OUTPUT) slot.onQuickCraft(stack, original);
        } else if (EnergyIntake.powers(stack)) {
            if (!moveItemStackTo(stack, ArcaneTransmuterBlockEntity.FUEL, ArcaneTransmuterBlockEntity.FUEL + 1, false)) return ItemStack.EMPTY;
        } else if (transmutable(stack)) {
            if (!moveItemStackTo(stack, ArcaneTransmuterBlockEntity.INPUT, ArcaneTransmuterBlockEntity.INPUT + 1, false)) return ItemStack.EMPTY;
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

    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
}
