package com.miophas.corruption.core.demon.skill;

/**
 * How a skill runs in time, in ticks counted from the one it begins on (0): its wind-up, at the end of which it takes
 * effect; how long it runs in all, the wind-up included; and how long it then takes to come round again.
 */
public record SkillTiming(int windup, int ticks, int cooldown) {
    public SkillTiming {
        windup = Math.max(0, windup);
        ticks = Math.max(windup + 1, ticks);
        cooldown = Math.max(0, cooldown);
    }

    /** Whether it takes effect on this tick of it: the first after its wind-up. */
    public boolean strikes(int elapsed) { return elapsed == windup; }

    /** Whether it is still winding up on this tick of it. */
    public boolean windingUp(int elapsed) { return elapsed < windup; }

    /** Whether it has run its course by this tick of it. */
    public boolean over(int elapsed) { return elapsed >= ticks; }

    /** How far through its wind-up it is on this tick, 0 to 1 (1 once it is past it). */
    public float wound(float elapsed) { return windup <= 0 ? 1 : Math.max(0, Math.min(1, elapsed / windup)); }
}
