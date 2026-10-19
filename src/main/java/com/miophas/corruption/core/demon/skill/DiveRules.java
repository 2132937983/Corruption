package com.miophas.corruption.core.demon.skill;

/**
 * A dive out of the sky, as rules. Through the wind-up the boss climbs, and the place its quarry stood as the dive
 * began is marked on the ground; then it dives at that place at {@code speed} blocks a tick, for the rest of the skill
 * at the most. Where it strikes the ground, everything within {@code radius} is hurt for {@code damage} and thrown
 * {@code knockback} blocks a tick away and up; and the ground is torn up as by a blast: every block within
 * {@code debrisRadius} that can be broken at all (not bedrock, nor anything else that cannot be), and is no liquid, is
 * flung away from where it struck, out and up, at up to {@code debrisSpeed} blocks a tick, to fall where it may;
 * {@code debrisMost} at the most, the nearest first.
 */
public record DiveRules(SkillTiming timing, double minRange, double maxRange, double minHeight, double speed, double radius, double damage,
                        double knockback, double debrisRadius, int debrisMost, double debrisSpeed) {
    public DiveRules {
        maxRange = Math.max(minRange, maxRange);
        speed = Math.max(0, speed);
        radius = Math.max(0, radius);
        debrisRadius = Math.max(0, debrisRadius);
        debrisMost = Math.max(0, debrisMost);
        debrisSpeed = Math.max(0, debrisSpeed);
    }

    /** Whether it would serve against what is this far off, and this far below the boss (it dives on nothing above it, nor too near under it). */
    public boolean serves(double distance, double below) { return distance >= minRange && distance <= maxRange && below >= minHeight; }

    /** Whether it is diving on this tick of the skill. */
    public boolean diving(int elapsed) { return elapsed >= timing.windup() && elapsed < timing.ticks(); }

    /** Whether a block this hard is torn up: one that can be broken at all. Bedrock, barriers and the like have no hardness at all (below none). */
    public static boolean tears(double hardness) { return DebrisRules.tears(hardness); }

    /**
     * How a block torn up this far from where the boss struck (dx, dy, dz: from there to the block's middle) is flung,
     * {x, y, z} in blocks a tick: out from there along the ground and always up, the nearer the harder, up to
     * {@code debrisSpeed}.
     */
    public DebrisRules debris() { return new DebrisRules(debrisRadius, debrisSpeed); }
    public double[] fling(double dx, double dy, double dz) { return debris().fling(dx, dy, dz); }
}
