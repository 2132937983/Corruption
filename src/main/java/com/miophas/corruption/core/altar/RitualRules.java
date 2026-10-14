package com.miophas.corruption.core.altar;

/**
 * How a rite unfolds, tick by tick, once what is on the altar makes one: it gathers for the rite's time, while
 * the pillars' offerings drift towards the middle; then corrupted lightning strikes the offering; and a moment
 * later what the rite brings forth appears there, and what it took is gone. Should what is on the altar change
 * before then, the rite comes to nothing and nothing is taken.
 */
public final class RitualRules {
    /** How long a rite gathers unless its recipe says otherwise, in ticks. */
    public static final int DEFAULT_TICKS = 60;
    /** How long after the lightning what the rite brings forth appears. */
    public static final int AFTER_STRIKE = 12;

    /** Where a rite is, this many ticks after it began. */
    public enum Phase { GATHERING, STRIKE, AFTERMATH, COMPLETE }

    public static Phase phase(int elapsed, int ticks) {
        if (elapsed < ticks) return Phase.GATHERING;
        if (elapsed == ticks) return Phase.STRIKE;
        if (elapsed < ticks + AFTER_STRIKE) return Phase.AFTERMATH;
        return Phase.COMPLETE;
    }

    private RitualRules() {}
}
