package com.miophas.corruption.common.cooking;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyBuffer;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.common.block.BrassCauldronBlock;
import com.miophas.corruption.common.energy.EnergyIntake;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModRecipes;
import com.miophas.corruption.core.cooking.BrassCauldronRules;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * The brass cauldron's contents and its cooking. Seven things go in, in any order, and whatever recipe they
 * make with the fluid in its tank cooks: Corruption Energy, if the recipe costs any, is drawn a share each
 * tick, and when it is done the ingredients and the fluid are used up and the dish goes out to the right.
 * With too little energy it waits; if what is in it changes, the energy drawn so far goes back.
 *
 * Energy comes in through its energy slot, which burns fuel and burns down items that carry energy as the
 * arcane transmuter's fuel slot does, and from other mods through CorruptionEnergyCapabilities.BLOCK. Fluid
 * comes in by bucket, sneaking, or through NeoForge's fluid capability, on any side. A parchment inscribed
 * with a string of the arcane lies in its parchment slot for the recipes that ask for one, and is never used
 * up. Hoppers bring ingredients from above and energy items from the sides, and take the dish, and what a
 * burnt fuel left, from below; the parchment is placed by hand.
 *
 * Players near it are told what is in its tank and its seven slots, so they see the fluid inside it and the
 * things floating in it.
 */
public final class BrassCauldronBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int INPUTS = BrassCauldronRules.INPUTS, ENERGY = 7, OUTPUT = 8, PARCHMENT = 9, SLOTS = 10;
    /** What the menu is kept told, in sixteen bits each: amounts of energy go as two halves, the fluid as its registry number. */
    public static final int DATA_ENERGY_LOW = 0, DATA_ENERGY_HIGH = 1, DATA_BURNT = 2, DATA_BURN_TICKS = 3, DATA_PROGRESS = 4, DATA_TIME = 5,
            DATA_COST_LOW = 6, DATA_COST_HIGH = 7, DATA_FLUID = 8, DATA_FLUID_AMOUNT = 9, DATA_COUNT = 10;
    private static final int[] FROM_ABOVE = {0, 1, 2, 3, 4, 5, 6}, FROM_BELOW = {OUTPUT, ENERGY}, FROM_SIDES = {ENERGY};

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final CorruptionEnergyBuffer energy = CorruptionEnergyApi.buffer(BrassCauldronRules.CAPACITY, Long.MAX_VALUE, Long.MAX_VALUE, this::setChanged);
    private final EnergyIntake intake = new EnergyIntake();
    private final CorruptionEnergyStorage outside = intake.openTo(energy);
    private final FluidTank tank = new FluidTank(BrassCauldronRules.TANK) {
        @Override protected void onContentsChanged() { tankChanged(); }
    };
    /** The cooking under way: which recipe, how many ticks done of how many, its cost, and the energy drawn for it so far. */
    private ResourceLocation working;
    private int progress, time;
    private long drawn, cost;
    private final RecipeManager.CachedCheck<BrassCookingInput, BrassCookingRecipe> recipes = RecipeManager.createCheck(ModRecipes.BRASS_COOKING.get());
    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case DATA_ENERGY_LOW -> (int) (energy.getEnergy() & 0xFFFF);
                case DATA_ENERGY_HIGH -> (int) (energy.getEnergy() >>> 16 & 0xFFFF);
                case DATA_BURNT -> intake.burnt();
                case DATA_BURN_TICKS -> intake.burnTicks();
                case DATA_PROGRESS -> progress;
                case DATA_TIME -> time;
                case DATA_COST_LOW -> (int) (cost & 0xFFFF);
                case DATA_COST_HIGH -> (int) (cost >>> 16 & 0xFFFF);
                case DATA_FLUID -> BuiltInRegistries.FLUID.getId(tank.getFluid().getFluid());
                case DATA_FLUID_AMOUNT -> tank.getFluidAmount();
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {}
        @Override public int getCount() { return DATA_COUNT; }
    };

    public BrassCauldronBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.BRASS_CAULDRON.get(), pos, state); }

    public long energy() { return energy.getEnergy(); }
    /** What other mods are offered through CorruptionEnergyCapabilities.BLOCK: energy in, none out. */
    public CorruptionEnergyStorage energyStorage() { return outside; }
    /** Its tank, as NeoForge's fluid capability offers it: filled and emptied from any side. */
    public IFluidHandler fluidHandler() { return tank; }
    public FluidStack fluid() { return tank.getFluid(); }
    public int progress() { return progress; }
    public boolean burning() { return intake.burning(); }
    public ItemStack burningItem() { return intake.burningItem(); }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BrassCauldronBlockEntity cauldron) { cauldron.tick(level, pos, state); }

    private void tick(Level level, BlockPos pos, BlockState state) {
        boolean changed = intake.tick(items, ENERGY, energy, level, pos);
        boolean cooking = cook(level);
        changed |= cooking;
        boolean lit = cooking;
        if (state.getValue(BrassCauldronBlock.LIT) != lit) level.setBlock(pos, state.setValue(BrassCauldronBlock.LIT, lit), 3);
        if (changed) setChanged();
    }

    /** What is in it, as its recipes see it. */
    private BrassCookingInput input() { return new BrassCookingInput(List.copyOf(items.subList(0, INPUTS)), tank.getFluid(), inscription(items.get(PARCHMENT))); }

    /** The string a parchment bears, or null if the item is no parchment or bears nothing. */
    private static ResourceKey<ArcaneString> inscription(ItemStack stack) { return stack.is(ArcaneApi.PARCHMENT) ? ArcaneApi.inscription(stack) : null; }

    /** What the parchment slot takes: a parchment bearing a string. */
    public static boolean inscribed(ItemStack stack) { return inscription(stack) != null; }

    /** The seven things in it, as players near it see them floating in it; on the client, as last told. */
    public List<ItemStack> shownItems() { return items.subList(0, INPUTS); }

    /** Moves the cooking on by a tick if it can. Answers whether it did. */
    private boolean cook(Level level) {
        BrassCookingInput input = input();
        RecipeHolder<BrassCookingRecipe> found = input.isEmpty() ? null : recipes.getRecipeFor(input, level).orElse(null);
        if (working != null && (found == null || !found.id().equals(working))) cancel();
        if (found == null) { cost = 0; time = 0; return false; }
        BrassCookingRecipe recipe = found.value();
        working = found.id(); cost = recipe.energy(); time = recipe.time();
        ItemStack result = recipe.assemble(input, level.registryAccess());
        if (!fits(result)) return false;
        long due = CorruptionEnergyApi.share(cost, progress, time);
        if (energy.getEnergy() < due) return false;
        energy.extract(due, false); drawn += due;
        if (++progress >= time) finish(recipe, input, result);
        return true;
    }

    /** Uses up what the recipe took, each slot's crafting leftovers staying behind (a bucket for a bucket of water), and puts out the dish. */
    private void finish(BrassCookingRecipe recipe, BrassCookingInput input, ItemStack result) {
        int[] slots = recipe.slots(input);
        for (int ingredient = 0; slots != null && ingredient < slots.length; ingredient++) {
            SizedIngredient wanted = recipe.ingredients().get(ingredient);
            ItemStack stack = items.get(slots[ingredient]);
            ItemStack left = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem().copyWithCount(stack.getCraftingRemainingItem().getCount() * wanted.count()) : ItemStack.EMPTY;
            stack.shrink(wanted.count());
            if (left.isEmpty()) continue;
            if (stack.isEmpty()) items.set(slots[ingredient], left);
            else if (level != null) Block.popResourceFromFace(level, worldPosition, Direction.UP, left);
        }
        recipe.fluid().ifPresent(fluid -> tank.drain(fluid.amount(), IFluidHandler.FluidAction.EXECUTE));
        ItemStack output = items.get(OUTPUT);
        if (output.isEmpty()) items.set(OUTPUT, result); else output.grow(result.getCount());
        progress = 0; drawn = 0; working = null;
    }

    /** Stops the cooking under way and gives back the energy it had drawn. */
    private void cancel() {
        energy.insert(drawn, false);
        progress = 0; drawn = 0; working = null;
    }

    private boolean fits(ItemStack result) {
        ItemStack output = items.get(OUTPUT);
        return output.isEmpty() || ItemStack.isSameItemSameComponents(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    /** The tank changed: saved with the block, and shown to the players who can see it, who see its fluid inside. */
    private void tankChanged() {
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    /** What players near it are told: its tank and its seven things, which is all they see of it. */
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("Tank", tank.writeToNBT(registries, new CompoundTag()));
        NonNullList<ItemStack> shown = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        for (int slot = 0; slot < INPUTS; slot++) shown.set(slot, items.get(slot));
        ContainerHelper.saveAllItems(tag, shown, registries);
        return tag;
    }

    /** What players near it were last told of its seven things: which item, not how many, as that is all they see. */
    private final ItemStack[] told = new ItemStack[INPUTS];

    /** Whatever changes is saved; if what can be seen in it changed, players near it are told. */
    @Override public void setChanged() {
        super.setChanged();
        if (level == null || level.isClientSide) return;
        boolean differs = false;
        for (int slot = 0; slot < INPUTS; slot++) {
            ItemStack now = items.get(slot);
            if (told[slot] == null || !ItemStack.isSameItemSameComponents(told[slot], now) || told[slot].isEmpty() != now.isEmpty()) {
                told[slot] = now.copyWithCount(1);
                differs = true;
            }
        }
        if (differs) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override protected Component getDefaultName() { return Component.translatable("container.corruption.brass_cauldron"); }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override public int getContainerSize() { return SLOTS; }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) { return new BrassCauldronMenu(id, inventory, this, data); }

    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot < INPUTS || slot == ENERGY && intake.accepts(stack, items.get(ENERGY)) || slot == PARCHMENT && inscribed(stack);
    }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.UP ? FROM_ABOVE : side == Direction.DOWN ? FROM_BELOW : FROM_SIDES; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return canPlaceItem(slot, stack); }
    /** The dish, and anything in the energy slot with no energy left in it: what a burnt fuel or an emptied item left behind. */
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == OUTPUT || slot == ENERGY && side == Direction.DOWN && !EnergyIntake.powers(stack);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        energy.setEnergy(tag.getLong("Energy"));
        intake.load(tag, registries);
        tank.readFromNBT(registries, tag.getCompound("Tank"));
        working = tag.contains("Recipe") ? ResourceLocation.tryParse(tag.getString("Recipe")) : null;
        progress = tag.getInt("Progress"); drawn = tag.getLong("Drawn");
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putLong("Energy", energy.getEnergy());
        intake.save(tag, registries);
        tag.put("Tank", tank.writeToNBT(registries, new CompoundTag()));
        if (working != null) tag.putString("Recipe", working.toString());
        tag.putInt("Progress", progress); tag.putLong("Drawn", drawn);
    }
}
