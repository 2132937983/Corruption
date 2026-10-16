package com.miophas.corruption.core.demon.skill;

import java.util.List;

/**
 * Calling up the corruption's remnants, as rules. When the wind-up is over, {@code count} of them rise round the boss
 * in a ring {@code radius} blocks across, each in the likeness of one of {@code looks} (creature ids, in turn); but
 * never so many that more than {@code most} it called are about at once.
 */
public record SummonRules(SkillTiming timing, int count, int most, double radius, List<String> looks) {
    public SummonRules {
        count = Math.max(0, count);
        most = Math.max(0, most);
        looks = List.copyOf(looks);
    }

    /** How many to call now, with this many it called still about. */
    public int toCall(int alive) { return Math.max(0, Math.min(count, most - Math.max(0, alive))); }

    /** Whether there is room for any more. */
    public boolean wanted(int alive) { return toCall(alive) > 0; }

    /** Where the index-th of this many rises, round the boss: {x, z} in blocks, evenly spaced. */
    public double[] place(int index, int of, double turn) {
        double angle = turn + Math.PI * 2 * index / Math.max(1, of);
        return new double[]{Math.cos(angle) * radius, Math.sin(angle) * radius};
    }

    /** The creature the index-th called takes the likeness of: the looks in turn, from where {@code start} says. */
    public String look(int index, int start) { return looks.isEmpty() ? "" : looks.get(Math.floorMod(start + index, looks.size())); }
}
