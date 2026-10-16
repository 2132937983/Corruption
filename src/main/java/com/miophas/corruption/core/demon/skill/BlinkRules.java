package com.miophas.corruption.core.demon.skill;

/**
 * Blinking out of danger, as rules. Every tick the boss weighs how safe it stands ({@link Danger}): it is not, if it is
 * standing in what hurts (water, lava, fire, a wall), has lost {@code hurtShare} of its health or more over the last
 * {@code hurtWindow} ticks ({@link Wounds}), is crowded ({@code crowd} or more things that would hurt it within
 * {@code crowdRadius}, or any player that close), or has its quarry nearer than {@code tooNear}. Not safe, with its blink
 * come round, it blinks: to the best of {@code tries} places tried round its quarry, from {@code nearest} to
 * {@code farthest} blocks off and from {@code lowest} to {@code highest} over the ground, where it fits and can see its
 * quarry, and which lies furthest from what crowded it. Then its blink takes its cooldown to come round again.
 */
public record BlinkRules(SkillTiming timing, double nearest, double farthest, double lowest, double highest, int tries, double crowdRadius,
                         int crowd, int hurtWindow, float hurtShare, double tooNear) {
    /** The golden angle: each place tried is turned this far round from the one before, so that a few go all round. */
    private static final double GOLDEN_ANGLE = 2.3999632297;

    public BlinkRules {
        farthest = Math.max(nearest, farthest);
        highest = Math.max(lowest, highest);
        tries = Math.max(1, tries);
        hurtWindow = Math.max(2, hurtWindow);
    }

    /**
     * How the boss stands, to weigh.
     *
     * @param standingInHarm whether it is in water, lava, fire or a wall
     * @param hurtShare      the share of its health it has lost of late ({@link Wounds#lost})
     * @param crowding       how many things that would hurt it are within the crowding radius
     * @param playerClose    whether a player who would hurt it is that close
     * @param quarryDistance how far its quarry is
     */
    public record Danger(boolean standingInHarm, float hurtShare, int crowding, boolean playerClose, double quarryDistance) {}

    public boolean unsafe(Danger danger) {
        return danger.standingInHarm() || danger.hurtShare() >= hurtShare || danger.crowding() >= crowd || danger.playerClose()
                || danger.quarryDistance() < tooNear;
    }

    /**
     * The index-th place to try, round the quarry: {angle in radians, distance, height over the ground}. Turned by the
     * golden angle from the one before, so that however few are tried they go all round; {@code roll} (0 to 1) varies
     * how far and how high.
     */
    public double[] trial(int index, double turn, double roll) {
        double angle = turn + index * GOLDEN_ANGLE, share = (index * .618034 + roll) % 1;
        return new double[]{angle, nearest + (farthest - nearest) * share, lowest + (highest - lowest) * ((share + .5) % 1)};
    }

    /** How good a place is to blink to: the further from what crowded the boss the better, and the nearer the middle of its distances. */
    public double score(double quarryDistance, double nearestThreat) {
        double middle = (nearest + farthest) / 2, off = Math.abs(quarryDistance - middle) / Math.max(1e-3, farthest - nearest);
        return Math.min(Math.max(0, nearestThreat), 24) - off * 6;
    }

    /** How much of its health a boss has lost over the last so many ticks: its health seen tick by tick. */
    public static final class Wounds {
        private final float[] seen;
        private int count, next;

        public Wounds(int window) { seen = new float[Math.max(2, window)]; }

        /** Its health this tick, as a share of its most (0 to 1). */
        public void see(float healthShare) {
            seen[next] = healthShare;
            next = (next + 1) % seen.length;
            count = Math.min(seen.length, count + 1);
        }

        public void forget() { count = 0; next = 0; }

        /** The share of its health lost from the oldest tick seen to now; none for health regained, and none until two ticks are seen. */
        public float lost() {
            if (count < 2) return 0;
            float oldest = seen[(next - count + seen.length) % seen.length], newest = seen[(next + seen.length - 1) % seen.length];
            return Math.max(0, oldest - newest);
        }
    }
}
