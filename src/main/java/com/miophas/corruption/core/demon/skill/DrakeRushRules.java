package com.miophas.corruption.core.demon.skill;

/**
 * Small dragons rushing in from every side upon a quarry, as rules. At the end of the wind-up the place where the quarry
 * stands is marked, and {@code drakes} small dragons appear about it, evenly round it {@code distance} from it and
 * {@code height} above it, each facing it; {@code gather} ticks later they rush at once at {@code speed} a tick straight
 * through the mark and on the same distance beyond, each hurting what it passes within {@code width} of it that is not of
 * its side for {@code damage} (each once) and throwing it {@code knockback} on; past the far side they are gone.
 */
public record DrakeRushRules(SkillTiming timing, double minRange, double maxRange, int drakes, double distance, double height, int gather,
                             double speed, double width, double damage, double knockback) {
    public DrakeRushRules {
        maxRange = Math.max(minRange, maxRange);
        drakes = Math.max(1, drakes);
        distance = Math.max(1, distance);
        gather = Math.max(0, gather);
        speed = Math.max(.1, speed);
        width = Math.max(0, width);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Where a drake appears, {dx, dy, dz} from the mark: the compass round it, from the north, clockwise seen from above. */
    public double[] start(int index) {
        double angle = 2 * Math.PI * index / drakes;
        return new double[]{Math.sin(angle) * distance, height, -Math.cos(angle) * distance};
    }

    /** How many ticks a drake takes from where it appears to the far side, through the mark. */
    public int flight() { return (int) Math.ceil(2 * Math.hypot(distance, height) / speed); }

    /** How long a drake lives: gathering, then its flight. */
    public int lasts() { return gather + flight(); }
}
