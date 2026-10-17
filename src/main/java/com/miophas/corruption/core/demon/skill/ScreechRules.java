package com.miophas.corruption.core.demon.skill;

/**
 * A screech, as rules: through the wind-up the boss draws breath; at its end it screams, and everything not of its side
 * within {@code radius} blocks of it is stunned for {@code stunTicks}: it can neither move, nor jump, nor strike, nor use
 * anything, until it wears off. A screech may leave its hearers slowed as well, for {@code slowTicks} (none for a screech
 * that only stuns), at {@code slowLevel} (1 for Slowness I).
 */
public record ScreechRules(SkillTiming timing, double minRange, double maxRange, double radius, int stunTicks, int slowTicks, int slowLevel) {
    public ScreechRules {
        maxRange = Math.max(minRange, maxRange);
        radius = Math.max(0, radius);
        stunTicks = Math.max(1, stunTicks);
        slowTicks = Math.max(0, slowTicks);
        slowLevel = Math.max(1, slowLevel);
    }

    /** A screech that only stuns. */
    public ScreechRules(SkillTiming timing, double minRange, double maxRange, double radius, int stunTicks) {
        this(timing, minRange, maxRange, radius, stunTicks, 0, 1);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether the scream reaches what is this far off. */
    public boolean reaches(double distance) { return distance <= radius; }

    /** Whether it leaves its hearers slowed as well as stunned. */
    public boolean slows() { return slowTicks > 0; }
}
