package com.miophas.corruption.core.demon.skill;

/**
 * A long body burrowing under its quarry and bursting up beneath it, as rules. Through the wind-up the caster rears; then
 * it dives into the ground a third of the way to its quarry ({@link #entry}), and runs under the ground {@code depth}
 * blocks down at {@code speed} a tick, following its quarry, until it is under it. There it marks the place its quarry
 * stood and holds still {@code warn} ticks, the ground there cracking, a warning; then it bursts straight up through that
 * place at {@code rise} a tick: everything within {@code radius} of it that is not of its side is hurt for {@code damage}
 * and flung {@code launch} up, and the ground there is torn up and thrown about (up to {@code debrisMost} blocks within
 * {@code debrisRadius}, where the world gives leave). It climbs on {@code climb} blocks above the ground, and is done.
 * Should it take longer than its timing's ticks, it gives up wherever it is.
 */
public record BurrowRules(SkillTiming timing, double minRange, double maxRange, double depth, double speed, int warn, double rise, double climb,
                          double radius, double damage, double launch, double debrisRadius, int debrisMost) {
    public BurrowRules {
        maxRange = Math.max(minRange, maxRange);
        depth = Math.max(1, depth);
        speed = Math.max(.1, speed);
        warn = Math.max(0, warn);
        rise = Math.max(.1, rise);
        climb = Math.max(0, climb);
        radius = Math.max(0, radius);
        debrisMost = Math.max(0, debrisMost);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Where it dives in, as a share of the way from where it is to its quarry. */
    public static final double ENTRY = 1 / 3D;

    /** Where it dives in, {dx, dz} from where it is, given its quarry {dx, dz} from where it is. */
    public double[] entry(double dx, double dz) { return new double[]{dx * ENTRY, dz * ENTRY}; }

    /** Whether, this far from under its quarry (along the ground), it has come under it. */
    public boolean under(double flat) { return flat <= Math.max(1.5, speed); }

    /** Whether something this far from where it burst up (along the ground) and this far above or below it is caught. */
    public boolean catches(double flat, double dy) { return flat <= radius && dy >= -2 && dy <= radius + 2; }
    /** Independent legacy defaults; callers may supply their own velocity policy to the skill. */
    public DebrisRules debris() { return new DebrisRules(3.5, .9); }
}
