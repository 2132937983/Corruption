package com.miophas.corruption.core.demon.skill;

import java.util.List;

/**
 * Calling up the corruption's remnants, as rules. When the wind-up is over, {@code count} of them rise in a ring
 * {@code radius} blocks across (round the boss, or round its quarry where it stood as the call began:
 * {@code atQuarry}), each in the likeness of one of {@code looks} (creature ids, in turn); but never so many that more
 * than {@code most} it called are about at once. They stay, or (an elite's) crumble away {@code lasts} ticks after they
 * rise (0: they stay).
 */
public record SummonRules(SkillTiming timing, int count, int most, double radius, List<String> looks, boolean atQuarry, int lasts) {
    public SummonRules {
        count = Math.max(0, count);
        most = Math.max(0, most);
        looks = List.copyOf(looks);
        lasts = Math.max(0, lasts);
    }

    /** Remnants called up round the boss itself, that stay. */
    public SummonRules(SkillTiming timing, int count, int most, double radius, List<String> looks) { this(timing, count, most, radius, looks, false, 0); }

    /** Remnants that stay. */
    public SummonRules(SkillTiming timing, int count, int most, double radius, List<String> looks, boolean atQuarry) {
        this(timing, count, most, radius, looks, atQuarry, 0);
    }

    /** Whether what it calls up crumbles away in time. */
    public boolean passes() { return lasts > 0; }

    /** How many to call now, with this many it called still about. */
    public int toCall(int alive) { return Math.max(0, Math.min(count, most - Math.max(0, alive))); }

    /** Whether there is room for any more. */
    public boolean wanted(int alive) { return toCall(alive) > 0; }

    /** Where the index-th of this many rises, round the boss (or its quarry): {x, z} in blocks, evenly spaced. */
    public double[] place(int index, int of, double turn) {
        double angle = turn + Math.PI * 2 * index / Math.max(1, of);
        return new double[]{Math.cos(angle) * radius, Math.sin(angle) * radius};
    }

    /** The creature the index-th called takes the likeness of: the looks in turn, from where {@code start} says. */
    public String look(int index, int start) { return looks.isEmpty() ? "" : looks.get(Math.floorMod(start + index, looks.size())); }
}
