package com.miophas.corruption.core.demon.skill;

/**
 * The corruption's lightning called down on the boss's quarry, as rules. As soon as it begins, every place a bolt will
 * fall is marked on the ground with a circle as wide as the bolt's reach, so that the quarry can see it coming; when
 * the wind-up is over the bolts fall, one every {@code interval} ticks, each hurting everything within {@code radius}
 * of where it falls for {@code damage}. The first falls right where the quarry stood when the boss began; the rest, for a
 * storm, scattered round it as far as {@code scatter}, spread evenly over the ground.
 */
public record StrikeRules(SkillTiming timing, double minRange, double maxRange, int bolts, double scatter, int interval, double radius, double damage) {
    private static final double GOLDEN_ANGLE = 2.3999632297;

    public StrikeRules {
        bolts = Math.max(1, bolts);
        interval = Math.max(0, interval);
        maxRange = Math.max(minRange, maxRange);
        // The skill lasts at least until its last bolt has fallen.
        timing = new SkillTiming(timing.windup(), Math.max(timing.ticks(), timing.windup() + (bolts - 1) * interval + 1), timing.cooldown());
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** The tick of the skill, from 0, the index-th bolt falls on. */
    public int fallsOn(int index) { return timing.windup() + index * interval; }

    /** Which bolt falls on this tick of the skill, or -1 for none. */
    public int falling(int elapsed) {
        if (elapsed < timing.windup()) return -1;
        int since = elapsed - timing.windup();
        if (interval == 0) return since == 0 ? 0 : -1;
        return since % interval == 0 && since / interval < bolts ? since / interval : -1;
    }

    /** Where the index-th bolt falls, from where the quarry stood: {x, z} in blocks; the first right there, the rest spread round it. */
    public double[] mark(int index, double turn) {
        if (index == 0 || scatter <= 0) return new double[]{0, 0};
        double out = scatter * Math.sqrt((double) index / (bolts - 1)), angle = turn + index * GOLDEN_ANGLE;
        return new double[]{Math.cos(angle) * out, Math.sin(angle) * out};
    }
}
