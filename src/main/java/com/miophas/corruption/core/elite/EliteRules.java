package com.miophas.corruption.core.elite;

/**
 * What the corruption's elites share, as rules. An elite stands between its remnants and its lords: it has a good deal
 * of health of its own (a remnant has twenty, a lord three hundred and more; an elite eighty to a hundred and twenty), skills
 * of its own, a basic blow that hits harder than a remnant's, and a blow from afar besides. Unlike a lord, it needs no
 * arcane to hurt it: any blow will do. It is of the corruption, so the corruption's curse does it no harm, nor does the
 * corruption's ground; it leaves what a remnant leaves. A novice's corrupted robes do not hide their wearer from it, though
 * better ones do. Each elite's own numbers are in its own rules ({@link AberrationRules}, {@link ScreecherRules},
 * {@link LurkerRules}).
 */
public final class EliteRules {
    /** The experience an elite gives when it is killed (a remnant's mob gives five, a lord a hundred). */
    public static final int EXPERIENCE = 20;
    /** Ticks an elite rests between one skill and the next, at the least, and how many more it may (at random). */
    public static final int REST = 14, REST_SPREAD = 12;
    /** How far over the water a sea elite's mouth comes as it surfaces at the edge for a quarry ashore, in blocks. */
    public static final double SURFACED = .3;

    /**
     * Which of some skills to use, each weighed by how well it would serve now (0 for one that will not), by a roll from 0
     * (inclusive) to 1: its place among them, or -1 if none would serve.
     */
    public static int pick(double[] weights, double roll) {
        double total = 0;
        for (double weight : weights) total += Math.max(0, weight);
        if (total <= 0) return -1;
        double at = Math.max(0, Math.min(roll, Math.nextDown(1.0))) * total;
        for (int index = 0; index < weights.length; index++) {
            double weight = Math.max(0, weights[index]);
            if (weight <= 0) continue;
            if (at < weight) return index;
            at -= weight;
        }
        for (int index = weights.length - 1; index >= 0; index--) if (weights[index] > 0) return index;
        return -1;
    }

    private EliteRules() {}
}
