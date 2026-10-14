package com.miophas.corruption.common.transmutation;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyBuffer;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.common.block.ArcaneTransmuterBlock;
import com.miophas.corruption.common.energy.EnergyIntake;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The arcane transmuter's contents and its work. Fuel (whatever the corruption_fuel data map makes worth
 * something) burns into its store of Corruption Energy, a little each tick, but only when the whole of it
 * fits, so none is wasted. An item that carries energy, such as a Corruption Candle, goes in the fuel slot
 * too: once all it carries fits, one is taken in and burnt down bit by bit, a step each tick (a candle's step
 * is a KCE). Meanwhile whatever sits in the input is transmuted by its recipe, drawing the recipe's energy a
 * little each tick; with too little stored it waits, and if the input is taken away or
 * changed the energy drawn so far goes back into the store.
 *
 * Like a furnace's, its slots open to hoppers: the input from above, fuel from the sides, and the result
 * (and whatever fuel leaves behind, such as an empty bucket) from below. Its store is open to other mods
 * through CorruptionEnergyCapabilities.BLOCK: they may fill it from any side, but not drain it.
 */
public final class ArcaneTransmuterBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int INPUT = 0, FUEL = 1, OUTPUT = 2, SLOTS = 3;
    /**
     * What the menu is kept told. The game sends these numbers as 16 bits, so amounts of energy, which run
     * to 100 000, go as two halves.
     */
    public static final int DATA_ENERGY_LOW = 0, DATA_ENERGY_HIGH = 1, DATA_BURNT = 2, DATA_BURN_TICKS = 3, DATA_PROGRESS = 4,
            DATA_COST_LOW = 5, DATA_COST_HIGH = 6, DATA_COUNT = 7;
    private static final int[] FROM_ABOVE = {INPUT}, FROM_BELOW = {OUTPUT, FUEL}, FROM_SIDES = {FUEL};

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final CorruptionEnergyBuffer energy = CorruptionEnergyApi.buffer(CorruptionEnergyApi.transmuterCapacity(), Long.MAX_VALUE, Long.MAX_VALUE, this::setChanged);
    /** Fuel and items carrying energy, taken in through the fuel slot. */
    private final EnergyIntake intake = new EnergyIntake();
    /** The store as other mods see it: they may fill it but not drain it, nor take the room what burns still needs. */
    private final CorruptionEnergyStorage outside = intake.openTo(energy);
    /** The transmutation under way: which recipe, how many ticks done, and the energy drawn for it so far. */
    private ResourceLocation working;
    private int progress;
    private long drawn, cost;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ArcaneTransmutationRecipe> recipes = RecipeManager.createCheck(ModRecipes.ARCANE_TRANSMUTATION.get());
    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case DATA_ENERGY_LOW -> (int) (energy.getEnergy() & 0xFFFF);
                case DATA_ENERGY_HIGH -> (int) (energy.getEnergy() >>> 16 & 0xFFFF);
                case DATA_BURNT -> intake.burnt();
                case DATA_BURN_TICKS -> intake.burnTicks();
                case DATA_PROGRESS -> progress;
                case DATA_COST_LOW -> (int) (cost & 0xFFFF);
                case DATA_COST_HIGH -> (int) (cost >>> 16 & 0xFFFF);
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {}
        @Override public int getCount() { return DATA_COUNT; }
    };

    public ArcaneTransmuterBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ARCANE_TRANSMUTER.get(), pos, state); }

    /** Puts back together an amount the menu was sent in two halves. */
    public static long join(int low, int high) { return (low & 0xFFFFL) | (high & 0xFFFFL) << 16; }

    public long energy() { return energy.getEnergy(); }
    /** What other mods are offered through the capability. */
    public CorruptionEnergyStorage energyStorage() { return outside; }
    /** The item burning down in it, if any; it drops with the rest when the block is broken. */
    public ItemStack burningItem() { return intake.burningItem(); }
    public int progress() { return progress; }
    public boolean burning() { return intake.burning(); }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArcaneTransmuterBlockEntity machine) { machine.tick(level, pos, state); }

    private void tick(Level level, BlockPos pos, BlockState state) {
        boolean changed = intake.tick(items, FUEL, energy, level, pos);
        boolean working = transmute(level);
        changed |= working;
        boolean lit = burning() || working;
        if (state.getValue(ArcaneTransmuterBlock.LIT) != lit) level.setBlock(pos, state.setValue(ArcaneTransmuterBlock.LIT, lit), 3);
        if (changed) setChanged();
    }

    /** Moves the transmutation in the input on by a tick if it can. Answers whether it did. */
    private boolean transmute(Level level) {
        ItemStack input = items.get(INPUT);
        RecipeHolder<ArcaneTransmutationRecipe> found = input.isEmpty() ? null : recipes.getRecipeFor(new SingleRecipeInput(input), level).orElse(null);
        if (working != null && (found == null || !found.id().equals(working))) cancel();
        if (found == null) { cost = 0; return false; }
        ArcaneTransmutationRecipe recipe = found.value();
        working = found.id(); cost = recipe.energy();
        ItemStack result = recipe.assemble(new SingleRecipeInput(input), level.registryAccess());
        if (!fits(result)) return false;
        long due = CorruptionEnergyApi.share(recipe.energy(), progress, CorruptionEnergyApi.transmuteTicks());
        if (energy.getEnergy() < due) return false;
        energy.extract(due, false); drawn += due;
        if (++progress >= CorruptionEnergyApi.transmuteTicks()) {
            input.shrink(recipe.ingredient().count());
            ItemStack output = items.get(OUTPUT);
            if (output.isEmpty()) items.set(OUTPUT, result); else output.grow(result.getCount());
            progress = 0; drawn = 0; working = null;
        }
        return true;
    }

    /** Stops the transmutation under way and gives back the energy it had drawn. */
    private void cancel() {
        energy.insert(drawn, false);
        progress = 0; drawn = 0; working = null;
    }

    private boolean fits(ItemStack result) {
        ItemStack output = items.get(OUTPUT);
        return output.isEmpty() || ItemStack.isSameItemSameComponents(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    @Override protected Component getDefaultName() { return Component.translatable("container.corruption.corruption_arcane_transmuter"); }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override public int getContainerSize() { return SLOTS; }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) { return new ArcaneTransmuterMenu(id, inventory, this, data); }

    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == INPUT || slot == FUEL && intake.accepts(stack, items.get(FUEL));
    }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.UP ? FROM_ABOVE : side == Direction.DOWN ? FROM_BELOW : FROM_SIDES; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return canPlaceItem(slot, stack); }
    /** The result, and anything in the fuel slot with no energy left in it: what a burnt fuel or an emptied item left behind. */
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == OUTPUT || slot == FUEL && side == Direction.DOWN && !EnergyIntake.powers(stack);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        energy.setEnergy(tag.getLong("Energy"));
        intake.load(tag, registries);
        working = tag.contains("Recipe") ? ResourceLocation.tryParse(tag.getString("Recipe")) : null;
        progress = tag.getInt("Progress"); drawn = tag.getLong("Drawn");
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putLong("Energy", energy.getEnergy());
        intake.save(tag, registries);
        if (working != null) tag.putString("Recipe", working.toString());
        tag.putInt("Progress", progress); tag.putLong("Drawn", drawn);
    }
}
