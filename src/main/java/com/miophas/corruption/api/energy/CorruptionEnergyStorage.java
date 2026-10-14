package com.miophas.corruption.api.energy;

/**
 * Something that holds Corruption Energy, measured in CE. This is what other code sees of a store: a block
 * offers one through {@link CorruptionEnergyCapabilities#BLOCK} and an item through
 * {@link CorruptionEnergyCapabilities#ITEM}. Amounts are longs because CE run large: the arcane transmuter
 * alone holds 100 000.
 *
 * Like NeoForge's own energy storage, every move can be tried first with simulate set: the answer is what
 * would happen, and nothing changes.
 */
public interface CorruptionEnergyStorage {
    long getEnergy();
    long getCapacity();

    /** Puts in up to this much; answers how much was (or, simulating, would be) taken. Never more than was offered. */
    long insert(long amount, boolean simulate);
    /** Takes out up to this much; answers how much came (or, simulating, would come) out. */
    long extract(long amount, boolean simulate);

    /** Whether it takes energy in at all, and whether it gives any out. A machine that only uses energy gives none. */
    default boolean canInsert() { return true; }
    default boolean canExtract() { return true; }
    /** How much more it could hold. */
    default long getSpace() { return Math.max(0, getCapacity() - getEnergy()); }
}
