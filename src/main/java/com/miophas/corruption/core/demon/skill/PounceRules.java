package com.miophas.corruption.core.demon.skill;

/**
 * A pounce, as rules: a creature crouches through its wind-up, eyes on its quarry, then leaps at where it stands as the
 * wind-up ends, a long low arc that tops out {@code height} blocks over the straight line and comes down on the place in
 * {@code flight} ticks. The first thing not of its side it meets in the air, or lands on, within {@code reach} blocks of
 * its body, is struck for {@code damage} and thrown {@code knockback} blocks a tick on along the leap (and a little up);
 * then it is down. It springs only at a quarry neither too near nor too far.
 */
public record PounceRules(SkillTiming timing, double minRange, double maxRange, double height, int flight, double reach, double damage,
                          double knockback) {
    public PounceRules {
        maxRange = Math.max(minRange, maxRange);
        height = Math.max(0, height);
        flight = Math.max(1, flight);
        reach = Math.max(0, reach);
        damage = Math.max(0, damage);
        knockback = Math.max(0, knockback);
        // It lasts at least until it could have landed.
        timing = new SkillTiming(timing.windup(), Math.max(timing.ticks(), timing.windup() + flight + 1), timing.cooldown());
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether it is in the air on this tick of the skill, if nothing has stopped it. */
    public boolean leaping(int elapsed) { return elapsed >= timing.windup() && elapsed < timing.windup() + flight; }

    /** How far along the line it is this many ticks into the leap, 0 to 1. */
    public double along(int leapt) { return Math.max(0, Math.min(1, leapt / (double) flight)); }

    /** How far over the straight line it is this many ticks into the leap, in blocks: none at either end, {@code height} halfway. */
    public double over(int leapt) {
        double along = along(leapt);
        return 4 * height * along * (1 - along);
    }
}
