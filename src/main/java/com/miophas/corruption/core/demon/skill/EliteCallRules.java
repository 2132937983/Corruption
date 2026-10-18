package com.miophas.corruption.core.demon.skill;

/**
 * Calling up the corruption's elites about a quarry, as rules. At the end of the wind-up {@code least} elites, or one more
 * half the time ({@link #count}), are brought forth on the ground about the quarry, {@code radius} from it, one after another;
 * never so many that more than {@code most} of the corruption's elites stand within reach of it.
 */
public record EliteCallRules(SkillTiming timing, double minRange, double maxRange, int least, int most, double radius) {
    public EliteCallRules {
        maxRange = Math.max(minRange, maxRange);
        least = Math.max(1, least);
        most = Math.max(least, most);
        radius = Math.max(0, radius);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** How many it calls with this many elites already about the quarry: {@code least}, or one more for a roll under a half, up to {@code most} in all. */
    public int count(int about, double roll) {
        int wanted = least + (roll < .5 ? 1 : 0);
        return Math.max(0, Math.min(wanted, most - Math.max(0, about)));
    }

    /** Whether it is wanted at all, with this many elites already about. */
    public boolean wanted(int about) { return about < most; }

    /** Where the elite of this place in the call stands, {dx, dz} from the quarry, evenly round it starting {@code turn} radians round. */
    public double[] place(int index, int of, double turn) {
        double angle = turn + 2 * Math.PI * index / Math.max(1, of);
        return new double[]{Math.cos(angle) * radius, Math.sin(angle) * radius};
    }
}
