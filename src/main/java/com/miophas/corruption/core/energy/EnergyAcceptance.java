package com.miophas.corruption.core.energy;

import com.miophas.corruption.api.energy.CorruptionEnergyAcceptance;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;

/**
 * Acceptance: the rules by which anything that holds Corruption Energy, a block or an entity, takes in the
 * energy an item carries. It takes from one item at a time. The item offers all it carries; the store
 * accepts as much of that as its {@link CorruptionEnergyAcceptance acceptance} allows and it has room for,
 * and that much moves across. The item keeps the rest, unless it is the kind that is used up and has
 * nothing left, as a candle burns down.
 */
public final class EnergyAcceptance {
    /**
     * How much a store accepts of what an item offers, given the room it has: as much as fits, or, accepting
     * only whole charges, all of it or nothing.
     */
    public static long accepted(long offered, long room, CorruptionEnergyAcceptance acceptance) {
        if (offered <= 0 || room <= 0) return 0;
        if (acceptance == CorruptionEnergyAcceptance.WHOLE) return room >= offered ? offered : 0;
        return Math.min(offered, room);
    }

    /**
     * Moves what one item's store carries into a receiver, as much as the acceptance allows. Both are asked
     * first, without doing anything, so a store that accepts only whole charges never takes part of one.
     * Answers how much moved.
     */
    public static long accept(CorruptionEnergyStorage item, CorruptionEnergyStorage receiver, CorruptionEnergyAcceptance acceptance) {
        if (!item.canExtract() || !receiver.canInsert()) return 0;
        long offered = item.extract(Long.MAX_VALUE, true);
        long agreed = accepted(offered, receiver.insert(offered, true), acceptance);
        return agreed <= 0 ? 0 : CorruptionEnergy.move(item, receiver, agreed);
    }

    /** What one item carries, as its data says: never less than nothing, never more than it can hold. */
    public static long carried(long recorded, long capacity) { return Math.clamp(recorded, 0, Math.max(0, capacity)); }

    /** Whether an item that has given its energy is used up: it is the kind that is, and it has nothing left. */
    public static boolean spent(long left, boolean consumed) { return consumed && left <= 0; }

    private EnergyAcceptance() {}
}
