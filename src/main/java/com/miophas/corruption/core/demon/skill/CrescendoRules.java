package com.miophas.corruption.core.demon.skill;

/**
 * A crescendo: rings of sound swelling out from the caster one after another, as rules. It serves against a quarry from
 * {@code minRange} to {@code maxRange} off. Through the wind-up the caster gathers itself; then {@code rings} rings go out
 * from where it stands, one every {@code every} ticks, each racing out at {@code speed} blocks a tick until it is
 * {@code radius} blocks across, and gone. A ring catches what it passes, once each: everything not of the caster's side
 * within {@code width} of its front, no more than {@code height} above or below the caster's feet, is hurt for
 * {@code damage} and thrown {@code knockback} away and {@code lift} up. Music is drawn on each ring as it goes.
 */
public record CrescendoRules(SkillTiming timing, double minRange, double maxRange, int rings, int every, double speed, double radius, double width,
                             double height, double damage, double knockback, double lift) {
    public CrescendoRules {
        maxRange = Math.max(minRange, maxRange);
        rings = Math.max(1, rings);
        every = Math.max(1, every);
        speed = Math.max(.05, speed);
        radius = Math.max(.5, radius);
        width = Math.max(.1, width);
        height = Math.max(0, height);
        damage = Math.max(0, damage);
        // It lasts until its last ring is gone.
        int last = timing.windup() + (rings - 1) * every + (int) Math.ceil(radius / speed);
        timing = new SkillTiming(timing.windup(), Math.max(timing.ticks(), last + 1), timing.cooldown());
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** The tick of the skill on which ring {@code ring} (the first is 0) goes out. */
    public int starts(int ring) { return timing.windup() + ring * every; }

    /** How many ticks a ring takes to run out its radius. */
    public int travel() { return (int) Math.ceil(radius / speed); }

    /** How far out ring {@code ring} is on this tick of the skill: less than none before it goes out, or once it is gone. */
    public double front(int ring, double elapsed) {
        double out = (elapsed - starts(ring)) * speed;
        return out < 0 || out > radius ? -1 : out;
    }

    /** Whether ring {@code ring} catches what is this far off (along the ground) on this tick of the skill. */
    public boolean catches(int ring, double elapsed, double distance) {
        double front = front(ring, elapsed);
        return front >= 0 && Math.abs(distance - front) <= width;
    }

    /** The most a quarry that stays in the way of every ring is hurt, before anything turns any of it aside. */
    public double most() { return rings * damage; }
}
