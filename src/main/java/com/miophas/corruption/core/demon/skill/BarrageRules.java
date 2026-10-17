package com.miophas.corruption.core.demon.skill;

/**
 * A rain of the corruption's bolts, as rules: as it begins, the place its quarry stands is marked on the ground, as wide
 * as the rain will fall; through the wind-up the boss gathers itself (a bird raises its wings); then it looses
 * {@code count} bolts, {@code perTick} a tick, each thrown up and over in an arc that comes down {@code flight} ticks
 * later somewhere within {@code radius} blocks of the mark, spread evenly over it. Each hurts what it strikes for
 * {@code damage}, and everything within {@code splash} blocks of where it strikes as well.
 *
 * <p>The arcs are worked out for bolts that fall {@code GRAVITY} blocks a tick faster each tick and keep the rest of their
 * speed: a bolt thrown with {@link #launch} lands where it was aimed, {@code flight} ticks later.
 */
public record BarrageRules(SkillTiming timing, double minRange, double maxRange, int count, int perTick, double radius, int flight, double damage,
                           double splash) {
    /** How much faster a bolt falls each tick, in blocks a tick. */
    public static final double GRAVITY = .04;
    private static final double GOLDEN_ANGLE = 2.3999632297;

    public BarrageRules {
        count = Math.max(1, count);
        perTick = Math.max(1, perTick);
        radius = Math.max(0, radius);
        flight = Math.max(1, flight);
        splash = Math.max(0, splash);
        maxRange = Math.max(minRange, maxRange);
        // It lasts at least until its last bolt is loosed.
        timing = new SkillTiming(timing.windup(), Math.max(timing.ticks(), timing.windup() + (count + perTick - 1) / perTick), timing.cooldown());
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** The bolts loosed on this tick of the skill, as {first, how many}; none before the wind-up is over or once all are gone. */
    public int[] loosed(int elapsed) {
        if (elapsed < timing.windup()) return new int[]{0, 0};
        int first = (elapsed - timing.windup()) * perTick;
        return new int[]{Math.min(first, count), Math.max(0, Math.min(perTick, count - first))};
    }

    /** Where the index-th bolt comes down, from the mark: {x, z} in blocks, the bolts spread evenly over the ground within the radius. */
    public double[] landing(int index, double turn) {
        double out = radius * Math.sqrt((index + .5) / count), angle = turn + index * GOLDEN_ANGLE;
        return new double[]{Math.cos(angle) * out, Math.sin(angle) * out};
    }

    /**
     * How fast to throw a bolt so that it comes down this far off (dx, dy, dz: from where it is thrown to where it lands)
     * {@code flight} ticks later: {x, y, z} in blocks a tick. Each tick it moves as fast as it goes, then falls GRAVITY faster.
     */
    public double[] launch(double dx, double dy, double dz) {
        int n = flight;
        return new double[]{dx / n, (dy + GRAVITY * n * (n - 1) / 2) / n, dz / n};
    }
}
