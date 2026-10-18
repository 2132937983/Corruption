package com.miophas.corruption.core.demon.skill;

/**
 * A blessing of Venus, as rules: the Pentacle of Venus, for grace, love and peace (the player's spell of that name heals,
 * restores and shields), worked by a boss on itself and its own. Through the wind-up a bloom of pale rose light opens about
 * it; at its end it and every one of its kind within {@code radius} is healed {@code heal} of its greatest health, and given
 * Absorption ({@code absorptionLevel}, 1 for Absorption I) for {@code absorptionTicks} and Regeneration
 * ({@code regenerationLevel}) for {@code regenerationTicks}. It is wanted only when the most hurt of them has fallen below
 * {@code below} of its greatest health.
 */
public record VenusRules(SkillTiming timing, double radius, double heal, int absorptionTicks, int absorptionLevel, int regenerationTicks,
                         int regenerationLevel, double below) {
    public VenusRules {
        radius = Math.max(0, radius);
        heal = Math.max(0, Math.min(1, heal));
        absorptionTicks = Math.max(0, absorptionTicks);
        absorptionLevel = Math.max(1, absorptionLevel);
        regenerationTicks = Math.max(0, regenerationTicks);
        regenerationLevel = Math.max(1, regenerationLevel);
        below = Math.max(0, Math.min(1, below));
    }

    /** Whether it is wanted, the most hurt of those it would bless having this share of its health left. */
    public boolean wanted(double lowestShare) { return lowestShare < below; }

    /** How much it heals one whose greatest health is this. */
    public double healed(double maxHealth) { return maxHealth * heal; }

    /** Whether it reaches one this far off. */
    public boolean reaches(double distance) { return distance <= radius; }
}
