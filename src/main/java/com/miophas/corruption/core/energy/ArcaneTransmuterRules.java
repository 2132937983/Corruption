package com.miophas.corruption.core.energy;

/**
 * How the Corrupted Arcane Transmuter turns fuel into Corruption Energy and spends it. Both happen a little
 * each tick: fuel pays its energy into the store as it burns, and a transmutation draws its cost out as it
 * goes, so the energy bar fills and drains as the work is done.
 */
public final class ArcaneTransmuterRules {
    /** The most Corruption Energy it holds. */
    public static final long CAPACITY = CorruptionEnergy.kilo(100);
    /** Every transmutation takes this long, whatever it makes; what it costs is up to the recipe. */
    public static final int TRANSMUTE_TICKS = 200;

    /**
     * Whether a fuel worth this much may be started. It must fit whole: a fuel is never burnt into a store
     * too full to take all of it, so nothing is wasted.
     */
    public static boolean fits(long stored, long fuelEnergy) { return fuelEnergy >= 0 && stored + fuelEnergy <= CAPACITY; }

    /**
     * How much of a total is due on one tick of a job that takes this many ticks, counting the first tick as 0.
     * The shares are whole CE and add up to exactly the total.
     */
    public static long share(long total, int tick, int ticks) {
        if (ticks <= 0) return tick == 0 ? total : 0;
        if (tick < 0 || tick >= ticks) return 0;
        return total * (tick + 1) / ticks - total * tick / ticks;
    }

    /** How much of a total has been paid after this many ticks. */
    public static long paid(long total, int ticks, int done) {
        if (ticks <= 0) return done > 0 ? total : 0;
        return total * Math.clamp(done, 0, ticks) / ticks;
    }

    /** A share of something, 0 to 1, for drawing bars; nothing of nothing is empty. */
    public static float fraction(long part, long whole) { return whole <= 0 ? 0 : Math.clamp((float) part / whole, 0, 1); }

    private ArcaneTransmuterRules() {}
}
