package com.miophas.corruption.api.energy;

/**
 * A store of Corruption Energy as its owner sees it: what everyone else may do, plus setting the amount
 * outright (for loading a saved block) and handing out views with tighter limits. Make one with
 * {@link CorruptionEnergyApi#buffer}.
 */
public interface CorruptionEnergyBuffer extends CorruptionEnergyStorage {
    /** Sets the amount held, cut to between nothing and the capacity. For loading saved data, not for moving energy. */
    void setEnergy(long energy);

    /**
     * The same store seen through tighter limits: no more than this much in, or out, in one call. A machine
     * that only uses energy offers the world {@code view(Long.MAX_VALUE, 0)}, so cables can fill it but never
     * drain it, while it still draws on the buffer itself.
     */
    CorruptionEnergyStorage view(long maxInsert, long maxExtract);
}
