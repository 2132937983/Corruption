package com.miophas.corruption.api.energy;

import com.miophas.corruption.core.energy.ArcaneTransmuterRules;
import com.miophas.corruption.core.energy.CarriedEnergy;
import com.miophas.corruption.core.energy.CorruptionEnergy;
import com.miophas.corruption.core.energy.EnergyAcceptance;
import com.miophas.corruption.core.energy.EnergyBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Corruption Energy for this mod and its add-ons. Amounts are in CE; a thousand make a KCE, a million an MCE,
 * a billion a BCE, and so on up to QiCE (see {@link CorruptionEnergyUnit}). See the
 * package documentation for how the pieces fit together.
 */
public final class CorruptionEnergyApi {
    public static long kilo(double kce) { return CorruptionEnergy.kilo(kce); }
    /** This much of any unit, in CE: amount(1.5, CorruptionEnergyUnit.MCE) is 1 500 000. Throws IllegalArgumentException past what a long counts. */
    public static long amount(double amount, CorruptionEnergyUnit unit) { return CorruptionEnergy.of(amount, unit); }
    /** Reads "250", "250CE", "0.1KCE", "20 KCE", "1.5MCE", "2 BCE", "3QaCE" and the like; throws IllegalArgumentException for anything else. */
    public static long parse(String text) { return CorruptionEnergy.parse(text); }
    /** In the unit it reads best in, never smaller than KCE: "0.1 KCE", "20 KCE", "2.5 MCE". Exact, so {@link #parse} reads it back. */
    public static String format(long ce) { return CorruptionEnergy.format(ce); }
    /** In a given unit: "0.05 MCE". */
    public static String format(long ce, CorruptionEnergyUnit unit) { return CorruptionEnergy.format(ce, unit); }
    /** The unit {@link #format(long)} would choose. To show "x / y" in one unit, choose by the larger: {@code unit(capacity)}. */
    public static CorruptionEnergyUnit unit(long ce) { return CorruptionEnergy.unit(ce); }
    /** An amount in a unit without the unit: text(1_500_000, MCE) is "1.5". */
    public static String text(long ce, CorruptionEnergyUnit unit) { return CorruptionEnergy.text(ce, unit); }
    /** The same in KCE: "0.1", "20". */
    public static String kiloText(long ce) { return CorruptionEnergy.kiloText(ce); }

    /**
     * A new store for a block or item of your own. Keep the buffer, save its amount, and offer the world
     * the buffer itself or a {@link CorruptionEnergyBuffer#view view} of it through {@link CorruptionEnergyCapabilities}.
     *
     * @param maxInsert the most one call may put in, maxExtract take out; Long.MAX_VALUE for no limit, 0 for none
     * @param changed told whenever the amount really changes, for example a block entity's setChanged
     */
    public static CorruptionEnergyBuffer buffer(long capacity, long maxInsert, long maxExtract, Runnable changed) {
        return new EnergyBuffer(capacity, maxInsert, maxExtract, changed);
    }

    /** Moves up to this much from one store to another, only as much as both agree to. Answers how much moved. */
    public static long move(CorruptionEnergyStorage from, CorruptionEnergyStorage to, long most) { return CorruptionEnergy.move(from, to, most); }

    /** The store a block offers from this side, or null if it has none. */
    public static CorruptionEnergyStorage storage(Level level, BlockPos pos, Direction side) { return level.getCapability(CorruptionEnergyCapabilities.BLOCK, pos, side); }
    /** The store an item carries, or null if it has none. */
    public static CorruptionEnergyStorage storage(ItemStack stack) { return stack.getCapability(CorruptionEnergyCapabilities.ITEM); }
    /** The store an entity offers, or null if it has none. */
    public static CorruptionEnergyStorage storage(Entity entity) { return entity.getCapability(CorruptionEnergyCapabilities.ENTITY); }
    /** What an item is worth burnt for energy, or null if it is no fuel. */
    public static CorruptionFuel fuel(ItemStack stack) { return CorruptionFuel.of(stack); }

    /** How this kind of item carries energy, as the corruption_energy_carrier data map says, or null if it does not. */
    public static CorruptionEnergyCarrier carrier(ItemStack stack) { return CorruptionEnergyCarrier.of(stack); }
    /** Whether one of these items has energy it would give up, so is worth offering to a store. */
    public static boolean carriesEnergy(ItemStack stack) {
        if (stack.isEmpty()) return false;
        CorruptionEnergyStorage one = storage(stack.copyWithCount(1));
        return one != null && one.canExtract() && one.extract(Long.MAX_VALUE, true) > 0;
    }

    /**
     * What came of offering one item's energy to a store.
     *
     * @param energy how much the store accepted, in CE; 0 if it took none
     * @param left the item as it is now, carrying what it kept, or empty if it was used up
     */
    public record Imported(long energy, ItemStack left) {}

    /**
     * Offers what one of these items carries to a block's or entity's store, which takes it in as the
     * acceptance says (the rules are in core.energy.EnergyAcceptance): all at once, as much as it accepts.
     * Works on a copy of one item: the stack itself is left alone, so whoever holds it takes one off and puts
     * back what is {@link Imported#left left}, and only if any energy moved. An item is used up when its
     * carrier data says so and it has nothing left.
     */
    public static Imported importFrom(ItemStack stack, CorruptionEnergyStorage receiver, CorruptionEnergyAcceptance acceptance) {
        ItemStack one = stack.copyWithCount(1);
        CorruptionEnergyStorage item = stack.isEmpty() || receiver == null ? null : storage(one);
        long moved = item == null ? 0 : EnergyAcceptance.accept(item, receiver, acceptance);
        return new Imported(Math.max(0, moved), one);
    }

    /**
     * All at once: takes everything one of these items carries, whatever kind of item it is. A
     * {@link CorruptionEnergyItem} decides for itself; any other item gives what its store gives.
     */
    public static long extractAll(ItemStack stack, boolean simulate) {
        if (stack.getItem() instanceof CorruptionEnergyItem item) return item.extractAll(stack, simulate);
        CarriedEnergy carried = CorruptionEnergyCarrier.carriedEnergy(stack);
        if (carried != null) return carried.extractAll(simulate);
        CorruptionEnergyStorage store = stack.isEmpty() ? null : storage(stack);
        return store == null || !store.canExtract() ? 0 : store.extract(Long.MAX_VALUE, simulate);
    }

    /** Bit by bit: takes one {@link #step step} of what a single item carries, whatever kind of item it is. */
    public static long extractStep(ItemStack stack, boolean simulate) {
        if (stack.getItem() instanceof CorruptionEnergyItem item) return item.extractStep(stack, simulate);
        CorruptionEnergyStorage store = stack.isEmpty() ? null : storage(stack);
        return store == null || !store.canExtract() ? 0 : store.extract(step(stack), simulate);
    }

    /** How much an item gives at a time, bit by bit: its own step if it is a {@link CorruptionEnergyItem}, otherwise a KCE. */
    public static long step(ItemStack stack) { return stack.getItem() instanceof CorruptionEnergyItem item ? item.getStep(stack) : CarriedEnergy.DEFAULT_STEP; }
    /** How many steps it takes to give this much, a part of a step counting as a whole one: how many ticks the arcane transmuter takes to burn it down. */
    public static int steps(long energy, long step) { return CarriedEnergy.steps(energy, step); }

    /** The arcane transmuter: how much it holds, and how long every transmutation takes. */
    public static long transmuterCapacity() { return ArcaneTransmuterRules.CAPACITY; }
    public static int transmuteTicks() { return ArcaneTransmuterRules.TRANSMUTE_TICKS; }
    /** How much of a total is due on one tick, counting from 0, of a job that takes this many ticks; the shares add up to the total. */
    public static long share(long total, int tick, int ticks) { return ArcaneTransmuterRules.share(total, tick, ticks); }
    /** How much of a total has been paid after this many ticks of such a job. */
    public static long paid(long total, int ticks, int done) { return ArcaneTransmuterRules.paid(total, ticks, done); }
    /** A share of something, 0 to 1, for drawing bars. */
    public static float fraction(long part, long whole) { return ArcaneTransmuterRules.fraction(part, whole); }

    private CorruptionEnergyApi() {}
}
