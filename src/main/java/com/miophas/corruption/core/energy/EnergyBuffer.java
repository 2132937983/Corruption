package com.miophas.corruption.core.energy;

import com.miophas.corruption.api.energy.CorruptionEnergyBuffer;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;

/**
 * The rules of holding Corruption Energy: never below nothing, never above capacity, no more than the
 * limits allow in one move, and a word to the owner whenever the amount really changes, so it knows to save.
 */
public final class EnergyBuffer implements CorruptionEnergyBuffer {
    private final long capacity, maxInsert, maxExtract;
    private final Runnable changed;
    private long energy;

    /**
     * @param maxInsert the most one call may put in; maxExtract likewise taking out. Long.MAX_VALUE for no limit, 0 for none at all.
     * @param changed told after every move that changes the amount; not after a simulated one
     */
    public EnergyBuffer(long capacity, long maxInsert, long maxExtract, Runnable changed) {
        if (capacity < 0 || maxInsert < 0 || maxExtract < 0) throw new IllegalArgumentException("A store's capacity and limits cannot be negative");
        this.capacity = capacity; this.maxInsert = maxInsert; this.maxExtract = maxExtract;
        this.changed = changed == null ? () -> {} : changed;
    }

    @Override public long getEnergy() { return energy; }
    @Override public long getCapacity() { return capacity; }
    @Override public boolean canInsert() { return maxInsert > 0; }
    @Override public boolean canExtract() { return maxExtract > 0; }
    @Override public void setEnergy(long energy) { this.energy = Math.clamp(energy, 0, capacity); }

    @Override public long insert(long amount, boolean simulate) { return insert(amount, simulate, maxInsert); }
    @Override public long extract(long amount, boolean simulate) { return extract(amount, simulate, maxExtract); }

    private long insert(long amount, boolean simulate, long limit) {
        long taken = Math.max(0, Math.min(Math.min(amount, limit), capacity - energy));
        if (!simulate && taken > 0) { energy += taken; changed.run(); }
        return taken;
    }

    private long extract(long amount, boolean simulate, long limit) {
        long given = Math.max(0, Math.min(Math.min(amount, limit), energy));
        if (!simulate && given > 0) { energy -= given; changed.run(); }
        return given;
    }

    @Override public CorruptionEnergyStorage view(long maxInsert, long maxExtract) {
        long in = Math.min(maxInsert, this.maxInsert), out = Math.min(maxExtract, this.maxExtract);
        return new CorruptionEnergyStorage() {
            @Override public long getEnergy() { return energy; }
            @Override public long getCapacity() { return capacity; }
            @Override public boolean canInsert() { return in > 0; }
            @Override public boolean canExtract() { return out > 0; }
            @Override public long insert(long amount, boolean simulate) { return EnergyBuffer.this.insert(amount, simulate, in); }
            @Override public long extract(long amount, boolean simulate) { return EnergyBuffer.this.extract(amount, simulate, out); }
        };
    }
}
