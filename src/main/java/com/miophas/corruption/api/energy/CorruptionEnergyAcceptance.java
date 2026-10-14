package com.miophas.corruption.api.energy;

/**
 * How a block or entity that holds Corruption Energy accepts what an item carries. Whoever takes energy from
 * an item says which with {@link CorruptionEnergyApi#importFrom}; the rules themselves are in
 * com.miophas.corruption.core.energy.EnergyAcceptance.
 */
public enum CorruptionEnergyAcceptance {
    /** As much as it has room for; the item keeps the rest. A player pouring a candle into a block by hand gives this way. */
    PARTIAL,
    /**
     * All of what the item carries, or nothing while that would not fit. The arcane transmuter loads items
     * from its fuel slot this way, as it starts a fuel only when all of it fits, so nothing is left half
     * given in the slot.
     */
    WHOLE
}
