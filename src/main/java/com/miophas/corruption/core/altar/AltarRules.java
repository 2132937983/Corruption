package com.miophas.corruption.core.altar;

/**
 * How an Altar Offering's state changes. The offering looks over its altar every so often: broken, the altar
 * goes dormant; newly stood, it starts binding. A rite ends with the offering formed, holding nothing, so what
 * the rite brought forth walks free. Used by hand, at any time, the offering looks over its altar at once and,
 * if it stands, starts binding, or stops if it was binding.
 */
public final class AltarRules {
    /** How often, in ticks, an offering looks over its altar. */
    public static final int CHECK_TICKS = 10;

    /** What it becomes having looked over its altar: dormant if the altar is broken; binding if it was dormant and the altar now stands; otherwise as it was. */
    public static AltarState checked(AltarState now, boolean formed) {
        if (!formed) return AltarState.DORMANT;
        return now == AltarState.DORMANT ? AltarState.BINDING : now;
    }

    /** What it becomes used by hand: dormant if its altar does not stand; otherwise binding, or formed if it was binding. */
    public static AltarState used(AltarState now, boolean formed) {
        if (!formed) return AltarState.DORMANT;
        return now == AltarState.BINDING ? AltarState.FORMED : AltarState.BINDING;
    }

    /** What it becomes once a rite is done: formed, holding nothing. */
    public static AltarState afterRite(AltarState now) { return now == AltarState.DORMANT ? AltarState.DORMANT : AltarState.FORMED; }

    private AltarRules() {}
}
