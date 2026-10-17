package com.miophas.corruption.core.demon.skill;

/**
 * Roots bursting up out of the ground under a quarry, as rules. As it begins, the place where the quarry stands is
 * marked, and through the wind-up the ground there cracks and trembles, so a quarry that keeps moving may step off it in
 * time. At its end the roots burst up there: everything within {@code radius} of the mark that is not of the caster's
 * side is hurt for {@code damage}, held fast ({@code holdTicks} of Slowness at {@code holdLevel}, 0 for Slowness I) and
 * thrown {@code lift} up. On the corruption's own ground they hold it twice as long.
 */
public record SnareRules(SkillTiming timing, double minRange, double maxRange, double radius, double damage, int holdTicks, int holdLevel, double lift) {
    public SnareRules {
        maxRange = Math.max(minRange, maxRange);
        radius = Math.max(0, radius);
        damage = Math.max(0, damage);
        holdTicks = Math.max(0, holdTicks);
        holdLevel = Math.max(0, holdLevel);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether something this far from the mark is caught. */
    public boolean catches(double fromMark) { return fromMark <= radius; }

    /** How long the roots hold what they catch, standing on the corruption's ground or not. */
    public int holds(boolean onCorruption) { return onCorruption ? holdTicks * 2 : holdTicks; }
}
