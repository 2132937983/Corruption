package com.miophas.corruption.core.demon.skill;

/**
 * A spinning charge, as rules: a boss that is a wheel of blades (a star's points) spins up through the wind-up, then
 * flies at its quarry at {@code speed} blocks a tick for the rest of the skill, spinning; every {@code every} ticks it
 * strikes everything within {@code reach} blocks of it for {@code damage}, throwing it {@code knockback} blocks a tick
 * away. A creature in its way is struck again and again as it passes through.
 */
public record SpinRules(SkillTiming timing, double minRange, double maxRange, double speed, double reach, double damage, int every, double knockback) {
    public SpinRules {
        maxRange = Math.max(minRange, maxRange);
        speed = Math.max(0, speed);
        reach = Math.max(0, reach);
        every = Math.max(1, every);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether it strikes on this tick of the skill: once the wind-up is over, every so many ticks. */
    public boolean strikes(int elapsed) { return elapsed >= timing.windup() && (elapsed - timing.windup()) % every == 0; }

    /** How many times one use strikes in all. */
    public int blows() { return (timing.ticks() - 1 - timing.windup()) / every + 1; }
}
