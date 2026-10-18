package com.miophas.corruption.core.demon.skill;

/**
 * Baal's fireballs as a boss looses them, as rules: the hellfire of the player's spell of Baal, made a volley. Through the
 * wind-up fire gathers in the caster's mouth; then it looses {@code count} fireballs, one every {@code every} ticks, at
 * {@code speed} a tick, each aimed where its quarry will be, leading it by up to {@code lead} ticks of its going. A fireball
 * bursts on whatever it meets: everything within {@code radius} of it that is not of its side is hurt for {@code damage} at
 * its heart, falling away to half at its edge ({@link #at}), and set alight; where the world gives leave the burst sets fire
 * about it and breaks the blocks within {@code crushRadius} that are softer than {@code hardest} ({@code crushMost} of them
 * at the most), as the spell's does.
 */
public record FireballRules(SkillTiming timing, double minRange, double maxRange, int count, int every, double speed, int lead, double damage,
                            double radius, int burnSeconds, double crushRadius, int crushMost, float hardest) {
    public FireballRules {
        maxRange = Math.max(minRange, maxRange);
        count = Math.max(1, count);
        every = Math.max(1, every);
        speed = Math.max(.1, speed);
        lead = Math.max(0, lead);
        radius = Math.max(0, radius);
        burnSeconds = Math.max(0, burnSeconds);
        crushRadius = Math.max(0, crushRadius);
        crushMost = Math.max(0, crushMost);
        // It lasts at least until the last fireball is loosed.
        timing = new SkillTiming(timing.windup(), Math.max(timing.ticks(), timing.windup() + (count - 1) * every + 1), timing.cooldown());
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Which fireball is loosed on this tick of the skill, counting from 0, or -1 if none. */
    public int shot(int elapsed) {
        int since = elapsed - timing.windup();
        if (since < 0 || since % every != 0) return -1;
        int index = since / every;
        return index < count ? index : -1;
    }

    /** How hard a burst hurts what is this far from its heart: all of it there, half at its edge, none beyond. */
    public double at(double distance) {
        if (distance > radius) return 0;
        return radius <= 0 ? damage : damage * (1 - .5 * distance / radius);
    }

    /** How many ticks ahead to lead a quarry this far off: as long as the fireball takes to get there, up to {@code lead}. */
    public int leading(double distance) { return (int) Math.min(lead, Math.ceil(distance / speed)); }
}
