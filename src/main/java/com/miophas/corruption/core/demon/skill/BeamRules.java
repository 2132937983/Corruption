package com.miophas.corruption.core.demon.skill;

/**
 * A beam of the corruption poured out at the boss's quarry, as rules. Through the wind-up it gathers, aimed at the
 * quarry; then it fires for the rest of the skill, reaching {@code length} blocks or to the first block in its way, and
 * every {@code interval} ticks hurts whatever is within {@code width} of its line for {@code damage}. It follows its
 * quarry, but turns no faster than {@code turn} degrees a tick, so that what runs across it can outrun it.
 */
public record BeamRules(SkillTiming timing, double minRange, double maxRange, double length, double width, int interval, double damage, float turn) {
    public BeamRules {
        interval = Math.max(1, interval);
        maxRange = Math.max(minRange, maxRange);
        turn = Math.max(0, turn);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether it is firing on this tick of the skill. */
    public boolean firing(int elapsed) { return elapsed >= timing.windup() && elapsed < timing.ticks(); }

    /** Whether it hurts what it catches on this tick of the skill: on the first of its firing, and every interval after. */
    public boolean pulses(int elapsed) { return firing(elapsed) && (elapsed - timing.windup()) % interval == 0; }

    /** An angle (degrees) turned towards another by the turn at most, the short way round. */
    public float follow(float from, float to) {
        float delta = (float) wrap(to - from);
        return from + Math.max(-turn, Math.min(turn, delta));
    }

    /** An angle brought between -180 and 180 degrees. */
    public static double wrap(double degrees) {
        double d = degrees % 360;
        if (d > 180) d -= 360;
        if (d <= -180) d += 360;
        return d;
    }
}
