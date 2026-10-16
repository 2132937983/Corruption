package com.miophas.corruption.core.demon.skill;

/**
 * A charge, as rules. Through the wind-up the boss gathers itself, facing its quarry; then it rushes straight ahead
 * along the line it last saw it on, {@code speed} blocks a tick for the rest of the skill, ramming whatever it meets
 * once ({@code damage}, thrown {@code knockback} blocks a tick away and up) and breaking every block in its way softer
 * than {@code hardest}: by default obsidian's hardness, so that stone, wood and even iron give way, but obsidian, and
 * whatever cannot be broken at all, does not. Such a block stops the charge.
 */
public record ChargeRules(SkillTiming timing, double minRange, double maxRange, double speed, double damage, double knockback, float hardest) {
    /** Obsidian's hardness: the softest a block may be and still stand against a charge. */
    public static final float OBSIDIAN = 50;

    public ChargeRules {
        maxRange = Math.max(minRange, maxRange);
        speed = Math.max(0, speed);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether a block this hard gives way to the charge: one that can be broken at all (hardness 0 or more), softer than the hardest. */
    public boolean breaks(float hardness) { return hardness >= 0 && hardness < hardest; }

    /** Ticks it rushes for, after its wind-up. */
    public int rushTicks() { return timing.ticks() - timing.windup(); }

    /** How far it goes at the most, in blocks. */
    public double reach() { return speed * rushTicks(); }

    /** Whether it is rushing on this tick of the skill. */
    public boolean rushing(int elapsed) { return elapsed >= timing.windup() && elapsed < timing.ticks(); }
}
