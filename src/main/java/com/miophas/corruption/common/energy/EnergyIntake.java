package com.miophas.corruption.common.energy;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyBuffer;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.api.energy.CorruptionFuel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * A machine's intake of Corruption Energy through one slot. Fuel (whatever the corruption_fuel data map makes
 * worth something) burns into the machine's store a little each tick; an item that carries energy, such as a
 * Corruption Candle, is taken in and burnt down a step each tick. Either starts only once all it is worth
 * fits, so nothing is wasted, and the store keeps the room for what is still to come. The arcane transmuter
 * and the brass cauldron each keep one for their energy slot.
 */
public final class EnergyIntake {
    /** The fuel burning now, or the item burning down: what it is worth, how long it takes, and how far it has got. */
    private long burnEnergy;
    private int burnTicks, burnt;
    /** The item burning down, one taken off the slot; empty while a fuel burns, or nothing does. */
    private ItemStack burningItem = ItemStack.EMPTY;

    /** Whether an item belongs in an energy slot: fuel, or something carrying energy to load. */
    public static boolean powers(ItemStack stack) { return CorruptionFuel.of(stack) != null || CorruptionEnergyApi.carriesEnergy(stack); }

    /** Whether an item carrying energy stays in the slot once loaded, emptied; the slot takes one of these at a time. */
    public static boolean staysWhenEmpty(ItemStack stack) {
        CorruptionEnergyCarrier carrier = CorruptionEnergyApi.carrier(stack);
        return CorruptionFuel.of(stack) == null && CorruptionEnergyApi.carriesEnergy(stack) && (carrier == null || !carrier.consumed());
    }

    /** Whether the slot, holding what it holds, takes this item. */
    public boolean accepts(ItemStack stack, ItemStack inSlot) {
        return powers(stack) && (!staysWhenEmpty(stack) || inSlot.isEmpty() && burningItem.isEmpty());
    }

    public boolean burning() { return burnTicks > 0; }
    public int burnt() { return burnt; }
    public int burnTicks() { return burnTicks; }
    /** The item burning down, if any; a machine drops it with the rest when broken. */
    public ItemStack burningItem() { return burningItem; }

    /** What the fuel or item now burning has still to pay in. */
    public long unpaid() {
        if (burnTicks == 0) return 0;
        return burningItem.isEmpty() ? burnEnergy - CorruptionEnergyApi.paid(burnEnergy, burnTicks, burnt) : CorruptionEnergyApi.extractAll(burningItem, true);
    }

    /**
     * The store as other mods see it: they may put energy in but not take it out, and never into the room what
     * is burning has yet to fill, so that is still never wasted.
     */
    public CorruptionEnergyStorage openTo(CorruptionEnergyBuffer energy) {
        return new CorruptionEnergyStorage() {
            @Override public long getEnergy() { return energy.getEnergy(); }
            @Override public long getCapacity() { return energy.getCapacity(); }
            @Override public boolean canExtract() { return false; }
            @Override public long insert(long amount, boolean simulate) { return energy.insert(Math.min(amount, Math.max(0, energy.getSpace() - unpaid())), simulate); }
            @Override public long extract(long amount, boolean simulate) { return 0; }
        };
    }

    /**
     * Starts the next fuel or item in the slot if all of it fits, then pays this tick's share of whatever is
     * burning into the store. Answers whether anything changed.
     */
    public boolean tick(NonNullList<ItemStack> items, int slot, CorruptionEnergyBuffer energy, Level level, BlockPos pos) {
        if (burnTicks == 0 && !startFuel(items, slot, energy) && !startLoading(items, slot, energy)) return false;
        if (burningItem.isEmpty()) {
            energy.insert(CorruptionEnergyApi.share(burnEnergy, burnt, burnTicks), false);
            if (++burnt >= burnTicks) { burnTicks = 0; burnt = 0; burnEnergy = 0; }
        } else {
            long given = CorruptionEnergyApi.extractStep(burningItem, false);
            energy.insert(given, false);
            burnt++;
            if (given <= 0 || !CorruptionEnergyApi.carriesEnergy(burningItem)) burntDown(items, slot, level, pos);
        }
        return true;
    }

    /** Starts burning the fuel in the slot, if it is fuel and all of its energy fits. Fuel leaves behind what it is made to, a bucket of lava its bucket. */
    private boolean startFuel(NonNullList<ItemStack> items, int slot, CorruptionEnergyBuffer energy) {
        ItemStack stack = items.get(slot);
        CorruptionFuel fuel = CorruptionFuel.of(stack);
        if (fuel == null || energy.insert(fuel.energy(), true) != fuel.energy()) return false;
        burnEnergy = fuel.energy(); burnTicks = fuel.ticks(); burnt = 0;
        if (stack.hasCraftingRemainingItem()) items.set(slot, stack.getCraftingRemainingItem()); else stack.shrink(1);
        return true;
    }

    /** Takes in one item from the slot that carries energy, if all it carries fits; it then burns down a step each tick. */
    private boolean startLoading(NonNullList<ItemStack> items, int slot, CorruptionEnergyBuffer energy) {
        ItemStack stack = items.get(slot);
        if (CorruptionFuel.of(stack) != null || !CorruptionEnergyApi.carriesEnergy(stack)) return false;
        long charge = CorruptionEnergyApi.extractAll(stack.copyWithCount(1), true);
        if (energy.insert(charge, true) != charge) return false;
        burningItem = stack.split(1);
        burnEnergy = charge; burnTicks = CorruptionEnergyApi.steps(charge, CorruptionEnergyApi.step(burningItem)); burnt = 0;
        return true;
    }

    /** The item has given all it had. One that is used up is gone; one that stays goes back to the slot, or out on top if that is taken. */
    private void burntDown(NonNullList<ItemStack> items, int slot, Level level, BlockPos pos) {
        burnTicks = 0; burnt = 0; burnEnergy = 0;
        ItemStack left = burningItem;
        burningItem = ItemStack.EMPTY;
        if (left.isEmpty()) return;
        if (items.get(slot).isEmpty()) items.set(slot, left);
        else if (level != null) Block.popResourceFromFace(level, pos, Direction.UP, left);
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        burnEnergy = tag.getLong("BurnEnergy"); burnTicks = tag.getInt("BurnTicks"); burnt = tag.getInt("Burnt");
        burningItem = ItemStack.parseOptional(registries, tag.getCompound("BurningItem"));
    }

    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong("BurnEnergy", burnEnergy); tag.putInt("BurnTicks", burnTicks); tag.putInt("Burnt", burnt);
        if (!burningItem.isEmpty()) tag.put("BurningItem", burningItem.save(registries));
    }
}
