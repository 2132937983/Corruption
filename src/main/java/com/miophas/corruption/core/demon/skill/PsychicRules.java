package com.miophas.corruption.core.demon.skill;

/**
 * A strike at the mind, as rules: short to gather and quick to come round, so that a boss may strike again and again.
 * Through the wind-up the boss fixes its quarry with its eyes; when the wind-up is over, if it still sees it, every
 * creature within {@code radius} of the quarry (the quarry first) is struck in the mind. Nothing flies to it, so there
 * is nothing to step aside from; only breaking the boss's sight before it strikes will do. Each struck is hurt for
 * {@code damage} with the corruption's own hurt; its sight swims ({@code nauseaTicks} of Nausea) and darkens
 * ({@code darknessTicks} of Darkness), and its limbs are heavy ({@code slowTicks} of Slowness, at {@code slowLevel}, 0 for
 * Slowness I).
 */
public record PsychicRules(SkillTiming timing, double minRange, double maxRange, double radius, double damage, int nauseaTicks, int darknessTicks,
                           int slowTicks, int slowLevel) {
    public PsychicRules {
        maxRange = Math.max(minRange, maxRange);
        radius = Math.max(0, radius);
        damage = Math.max(0, damage);
        nauseaTicks = Math.max(0, nauseaTicks);
        darknessTicks = Math.max(0, darknessTicks);
        slowTicks = Math.max(0, slowTicks);
        slowLevel = Math.max(0, slowLevel);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether something this far from the quarry is struck with it. */
    public boolean reaches(double fromQuarry) { return fromQuarry <= radius; }
}
