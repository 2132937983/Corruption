package com.miophas.corruption.core.demon.skill;

/**
 * Spitting the corruption's bolts at a quarry from the mouth (acid, feathers, bolts of water), as rules. When the
 * wind-up is over it looses {@code shots} of them, one every {@code every} ticks, each at {@code speed} blocks a tick:
 * straight at the quarry, swept {@code fan} degrees across it from one side to the other (all at it, with no fan); or,
 * lobbed ({@code lob}), thrown up and over to come down where the quarry stood after {@code flight} ticks, falling
 * {@link #GRAVITY} blocks a tick faster each tick. Each hurts what it strikes for {@code damage}, and everything within
 * {@code splash} of where it strikes as well.
 *
 * <p>What it spits looks as {@code look} says; it may throw what it strikes back {@code push} blocks a tick along its
 * flight (a bolt of water under great pressure), and it may leave a {@code pool} where it strikes.
 */
public record SpitRules(SkillTiming timing, double minRange, double maxRange, int shots, int every, double fan, double speed, double damage,
                        double splash, boolean lob, int flight, Look look, double push, Pool pool) {
    /** How much faster a lobbed bolt falls each tick, in blocks a tick. */
    public static final double GRAVITY = .04;

    /** What is spat: a bolt of the corruption, a feather, a glob of its acid, a bolt of its water. */
    public enum Look { BOLT, FEATHER, GLOB, WATER }

    /**
     * A pool left where a bolt strikes, {@code radius} blocks across from its middle, lying {@code ticks}; it lays the
     * corruption's curse on whatever stands in it, for {@code curseTicks}. {@link #NONE} leaves nothing.
     */
    public record Pool(float radius, int ticks, int curseTicks) {
        public static final Pool NONE = new Pool(0, 0, 0);

        public Pool {
            radius = Math.max(0, radius);
            ticks = Math.max(0, ticks);
            curseTicks = Math.max(0, curseTicks);
        }

        public boolean any() { return radius > 0 && ticks > 0; }
    }

    public SpitRules {
        maxRange = Math.max(minRange, maxRange);
        shots = Math.max(1, shots);
        every = Math.max(1, every);
        fan = Math.max(0, fan);
        speed = Math.max(.05, speed);
        splash = Math.max(0, splash);
        flight = Math.max(1, flight);
        look = look == null ? Look.BOLT : look;
        push = Math.max(0, push);
        pool = pool == null ? Pool.NONE : pool;
        // It lasts at least until its last shot is loosed.
        timing = new SkillTiming(timing.windup(), Math.max(timing.ticks(), timing.windup() + (shots - 1) * every + 1), timing.cooldown());
    }

    /** Bolts of the corruption that neither push nor leave anything. */
    public SpitRules(SkillTiming timing, double minRange, double maxRange, int shots, int every, double fan, double speed, double damage,
                     double splash, boolean lob, int flight) {
        this(timing, minRange, maxRange, shots, every, fan, speed, damage, splash, lob, flight, Look.BOLT, 0, Pool.NONE);
    }

    /** The same, looking otherwise. */
    public SpitRules looking(Look as) { return new SpitRules(timing, minRange, maxRange, shots, every, fan, speed, damage, splash, lob, flight, as, push, pool); }

    /** The same, throwing back what it strikes so hard. */
    public SpitRules pushing(double by) { return new SpitRules(timing, minRange, maxRange, shots, every, fan, speed, damage, splash, lob, flight, look, by, pool); }

    /** The same, leaving this pool where it strikes. */
    public SpitRules pooling(Pool left) { return new SpitRules(timing, minRange, maxRange, shots, every, fan, speed, damage, splash, lob, flight, look, push, left); }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Which shot is loosed on this tick of the skill, or -1 for none. */
    public int shot(int elapsed) {
        if (elapsed < timing.windup()) return -1;
        int since = elapsed - timing.windup();
        return since % every == 0 && since / every < shots ? since / every : -1;
    }

    /** How far across the fan the index-th shot is aimed, in degrees from straight at the quarry: evenly from one side to the other. */
    public double across(int index) { return shots <= 1 ? 0 : -fan / 2 + fan * index / (shots - 1); }

    /**
     * How fast to throw a lobbed shot so that it comes down this far off (dx, dy, dz: from where it is thrown to where it
     * lands) after its flight: {x, y, z} in blocks a tick. Each tick it moves as fast as it goes, then falls GRAVITY faster.
     */
    public double[] lobbed(double dx, double dy, double dz) {
        double n = flight;
        return new double[]{dx / n, (dy + GRAVITY * n * (n - 1) / 2) / n, dz / n};
    }
}
