package com.miophas.corruption.core.demon.skill;

/**
 * A volley of beams, as rules: each a beam of {@link BeamRules}, as many as {@code most} at once, each fired out of a
 * magic circle of its own. One beam leaves from before the boss itself; two or more leave from circles set evenly on a
 * ring {@code spread} blocks round that place, square to the way the boss faces. How many it fires at a time is the
 * boss's to say: by its health, say ({@link #byHealth}), the more the lower it is.
 */
public record BeamVolleyRules(BeamRules beam, int most, double spread) {
    public BeamVolleyRules {
        most = Math.max(1, most);
        spread = Math.max(0, spread);
    }

    /** How many beams a boss with this share of its health left fires: one at full health, and one more for each share of it lost, up to {@code most}. */
    public int byHealth(float share) {
        float left = Math.max(0, Math.min(1, share));
        return Math.max(1, Math.min(most, 1 + (int) Math.floor((1 - left) * most + 1e-6)));
    }

    /**
     * Where the index-th of so many beams leaves from, as an offset {right, up} in the boss's own frame from the place
     * before it: the one of one at that place; more set evenly round it, the first straight above.
     */
    public double[] circle(int index, int count) {
        if (count <= 1) return new double[]{0, 0};
        double angle = Math.PI / 2 + Math.PI * 2 * index / count;
        return new double[]{Math.cos(angle) * spread, Math.sin(angle) * spread};
    }
}
