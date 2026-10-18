package com.miophas.corruption.core.demon.skill;

/**
 * Giant hands growing out of the ground to smash a quarry, as rules. At the end of the wind-up {@code hands} circles of runes
 * open on the ground: the first under the quarry, the others round it, {@code spread} from it. Out of each a hand grows,
 * {@code rise} ticks pushing up out of the earth; it rears {@code hold} ticks; it comes down in {@code slam} ticks, palm
 * first; and everything within {@code radius} of where it falls that is not of its side is hurt for {@code damage} and
 * flung {@code launch} up ({@link #impact}). Then it sinks back into the ground ({@code sink} ticks) and is gone.
 */
public record HandRules(SkillTiming timing, double minRange, double maxRange, int hands, double spread, int rise, int hold, int slam, int sink,
                        double radius, double damage, double launch) {
    public HandRules {
        maxRange = Math.max(minRange, maxRange);
        hands = Math.max(1, hands);
        spread = Math.max(0, spread);
        rise = Math.max(1, rise);
        hold = Math.max(0, hold);
        slam = Math.max(1, slam);
        sink = Math.max(1, sink);
        radius = Math.max(0, radius);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Where a hand grows, {dx, dz} from the quarry: the first under it, the rest evenly round it, starting {@code turn} radians round. */
    public double[] place(int index, double turn) {
        if (index == 0 || hands == 1) return new double[]{0, 0};
        double angle = turn + 2 * Math.PI * (index - 1) / (hands - 1);
        return new double[]{Math.cos(angle) * spread, Math.sin(angle) * spread};
    }

    /** The tick of a hand's life on which it comes down. */
    public int impact() { return rise + hold + slam; }

    /** How long a hand lives, from when it begins to grow to when it has sunk away. */
    public int lasts() { return impact() + sink; }

    /** How far a hand has grown, 0 to 1 (out of the ground), at this tick of its life; it shrinks again as it sinks. */
    public double grown(double age) {
        if (age < rise) return age / rise;
        if (age <= impact()) return 1;
        return Math.max(0, 1 - (age - impact()) / sink);
    }

    /** How far up it is reared, 0 (flat on the ground) to 1 (fully raised), at this tick of its life. */
    public double reared(double age) {
        if (age < rise) return age / rise * .6;
        if (age < rise + hold) return .6 + .4 * (age - rise) / Math.max(1, hold);
        if (age < impact()) return 1 - (age - rise - hold) / slam;
        return 0;
    }

    /** Whether something this far from where a hand falls (along the ground) and this far above or below it is caught. */
    public boolean catches(double flat, double dy) { return flat <= radius && dy >= -1.5 && dy <= 3; }
}
