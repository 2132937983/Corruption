package com.miophas.corruption.core.demon.skill;

/**
 * Blighting the ground about the caster, as rules. It serves against a quarry no further off than {@code within}.
 * Through the wind-up the caster rears or roots itself; at its end it strikes the ground, and everything within
 * {@code radius} of it that is not of its side is hurt for {@code damage} and thrown {@code knockback} away and
 * {@code lift} up. Then the blight runs out over the ground from where it stands, reaching {@code radius} blocks
 * {@code spread} ticks later: the top block of the ground at each place turns as the blight reaches it, the nearest
 * first, {@code most} blocks at the most. Dormant, it turns to the corruption's dormant form, which spreads no further
 * but hurts whatever stands on it; otherwise the infection takes it, as it takes any block, and spreads on from there.
 * Turning the ground needs the world's leave (the mobGriefing rule); without it the ground stays as it was.
 */
public record BlightRules(SkillTiming timing, double within, double radius, int most, int spread, boolean dormant, double damage, double knockback,
                          double lift) {
    public BlightRules {
        within = Math.max(0, within);
        radius = Math.max(0, radius);
        most = Math.max(0, most);
        spread = Math.max(0, spread);
        damage = Math.max(0, damage);
        // It lasts at least until the blight has run as far as it goes.
        timing = new SkillTiming(timing.windup(), Math.max(timing.ticks(), timing.windup() + spread + 1), timing.cooldown());
    }

    /** Whether it serves against a quarry this far off. */
    public boolean inRange(double distance) { return distance <= within; }

    /** Whether the blight reaches a place this far from the caster at all. */
    public boolean reaches(double distance) { return distance <= radius; }

    /** The tick of the skill on which the blight reaches a place this far from the caster. */
    public int reaching(double distance) {
        if (radius <= 0) return timing.windup();
        return timing.windup() + (int) Math.ceil(Math.max(0, Math.min(radius, distance)) / radius * spread);
    }
}
