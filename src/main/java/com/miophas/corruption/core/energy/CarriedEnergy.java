package com.miophas.corruption.core.energy;

import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

/**
 * The store of a stack of items that carry Corruption Energy, as its rules. Each item carries its own, up to
 * its capacity, and what one item carries is all the stack's data can say, so energy only moves in or out of
 * a stack of one. A stack of several still reports what all of them carry together, but moves nothing:
 * whoever takes from it takes one item off first. An item of a kind that is used up once empty, as a candle
 * burns down, is gone the moment it gives the last of its energy.
 *
 * Energy comes out in either of two ways: all at once ({@link #extractAll}) or bit by bit, a step at a time
 * ({@link #extractStep}), for whoever takes it to call again, each tick say, until the item is empty.
 */
public final class CarriedEnergy implements CorruptionEnergyStorage {
    /** How much an item gives in one step unless its kind says otherwise: a KCE. */
    public static final long DEFAULT_STEP = CorruptionEnergy.kilo(1);

    private final long capacity;
    private final boolean consumed;
    private final int count;
    private final LongSupplier read;
    private final LongConsumer write;
    private final Runnable usedUp;

    /**
     * @param capacity the most one item carries
     * @param consumed whether an item is used up once it has given all it carried
     * @param read what one item carries, as its data says
     * @param write records what one item now carries
     * @param usedUp takes one used up item off the stack
     */
    public CarriedEnergy(long capacity, boolean consumed, int count, LongSupplier read, LongConsumer write, Runnable usedUp) {
        this.capacity = Math.max(0, capacity); this.consumed = consumed; this.count = Math.max(0, count);
        this.read = read; this.write = write; this.usedUp = usedUp;
    }

    /** What one item carries. */
    public long each() { return EnergyAcceptance.carried(read.getAsLong(), capacity); }

    @Override public long getEnergy() { return times(each()); }
    @Override public long getCapacity() { return times(capacity); }
    @Override public boolean canInsert() { return count == 1 && capacity > 0; }
    @Override public boolean canExtract() { return count == 1; }

    @Override public long insert(long amount, boolean simulate) {
        if (!canInsert()) return 0;
        long each = each(), taken = Math.max(0, Math.min(amount, capacity - each));
        if (!simulate && taken > 0) write.accept(each + taken);
        return taken;
    }

    @Override public long extract(long amount, boolean simulate) {
        if (!canExtract()) return 0;
        long each = each(), given = Math.max(0, Math.min(amount, each));
        if (!simulate && given > 0) {
            write.accept(each - given);
            if (EnergyAcceptance.spent(each - given, consumed)) usedUp.run();
        }
        return given;
    }

    /**
     * All at once: everything one item carries, in one go. A stack of several can do this too if its items
     * are used up once empty: the one that gives leaves the stack, and the rest keep theirs.
     */
    public long extractAll(boolean simulate) {
        if (count == 1) return extract(Long.MAX_VALUE, simulate);
        if (count < 1 || !consumed) return 0;
        long each = each();
        if (!simulate && each > 0) usedUp.run();
        return each;
    }

    /** Bit by bit: one step of what one item carries, or what is left if that is less. Only a single item gives this way. */
    public long extractStep(long step, boolean simulate) { return extract(Math.max(0, step), simulate); }

    /** How many steps it takes to give this much: at least one, and a part of a step counts as a whole one. */
    public static int steps(long energy, long step) {
        if (step <= 0) return 1;
        if (energy <= step) return 1;
        return (int) Math.min(Integer.MAX_VALUE, (energy - 1) / step + 1);
    }

    private long times(long each) {
        try { return Math.multiplyExact(each, (long) count); }
        catch (ArithmeticException tooMuch) { return Long.MAX_VALUE; }
    }
}
