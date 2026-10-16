package com.miophas.corruption.core.demon.skill;

/**
 * A brainwashing gaze, as rules. The boss fixes something within reach, neither too near nor too far; Eyes of
 * Providence open while the gaze gathers ({@code eyes} of them: the first over the boss, the rest in a ring round its
 * quarry, so that wherever it looks an eye looks back); and when the wind-up is over, if it still sees it, an eye
 * flashes before its eyes (a player's) and it is left slowed, blinded and, for a strong enough gaze, withering.
 *
 * @param slownessLevel the effect's amplifier, 0 for Slowness I
 * @param witherTicks   0 for a gaze that does not wither
 */
public record GazeRules(SkillTiming timing, double minRange, double maxRange, int slownessTicks, int slownessLevel, int blindnessTicks,
                        int witherTicks, int witherLevel, int eyes) {
    /** How far round the quarry its ring of eyes stands, and how high over its feet, in blocks. */
    public static final double RING = 4, RING_HEIGHT = 2.6;

    public GazeRules {
        eyes = Math.max(0, eyes);
        maxRange = Math.max(minRange, maxRange);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    public boolean withers() { return witherTicks > 0; }

    /** How many of its eyes stand round the quarry: all but the one over the boss. */
    public int ringed() { return Math.max(0, eyes - 1); }

    /** How far round the quarry the index-th of its ringed eyes stands, in radians: spread evenly, the first a little turned. */
    public double ringAngle(int index, double turn) {
        int count = Math.max(1, ringed());
        return turn + Math.PI * 2 * index / count;
    }
}
