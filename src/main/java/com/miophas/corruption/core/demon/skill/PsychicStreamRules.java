package com.miophas.corruption.core.demon.skill;

/**
 * A thread drawn out of a quarry's mind, as rules: little at a time, but again and again, quick as the thoughts it
 * unpicks. Through the wind-up the caster fixes its quarry; then for the rest of the skill, every {@code interval} ticks
 * (a pulse), so long as it sees its quarry and the quarry is no further off than {@code holdRange}, it hurts it for
 * {@code damage} in the mind (psychic hurt, which armour does not turn aside, though Protection and Resistance do), and
 * the caster is healed {@code heal} by what it took. A pulse that finds its quarry out of sight or out of reach does
 * nothing; {@code breaksAfter} such pulses one after another and the thread breaks, the skill over.
 */
public record PsychicStreamRules(SkillTiming timing, double minRange, double maxRange, double holdRange, int interval, double damage, double heal,
                                 int breaksAfter) {
    public PsychicStreamRules {
        maxRange = Math.max(minRange, maxRange);
        holdRange = Math.max(maxRange, holdRange);
        interval = Math.max(1, interval);
        damage = Math.max(0, damage);
        heal = Math.max(0, heal);
        breaksAfter = Math.max(1, breaksAfter);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether the thread still holds a quarry this far off. */
    public boolean holds(double distance) { return distance <= holdRange; }

    /** Whether a pulse falls on this tick of the skill. */
    public boolean pulses(int elapsed) { return elapsed >= timing.windup() && elapsed < timing.ticks() && (elapsed - timing.windup()) % interval == 0; }

    /** How many pulses a whole run of it has. */
    public int pulses() { return (timing.ticks() - timing.windup() + interval - 1) / interval; }

    /** The most a whole run hurts its quarry, before anything turns any of it aside. */
    public double most() { return pulses() * damage; }
}
