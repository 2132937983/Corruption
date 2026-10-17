package com.miophas.corruption.core.demon.skill;

/**
 * Dives one after another, as rules: {@code dives} dives out of the sky as {@link DiveRules} has them, each on the place
 * its quarry stands as that dive begins (marked, as every dive's is). Before each the boss climbs for {@code climb} ticks;
 * then it dives, for {@code most} ticks at the most, until it strikes; and at once it climbs for the next, with no rest
 * between. The skill's own timing covers them all: its wind-up is the first climb, and it runs until the last could have
 * struck.
 */
public record DiveChainRules(DiveRules dive, int dives, int climb, int most, int cooldown) {
    public DiveChainRules {
        dives = Math.max(1, dives);
        climb = Math.max(1, climb);
        most = Math.max(1, most);
        cooldown = Math.max(0, cooldown);
    }

    /** The whole chain in time: the first climb its wind-up; long enough for every dive to climb and to fly its most. */
    public SkillTiming timing() { return new SkillTiming(climb, dives * (climb + most), cooldown); }

    /** Whether it would serve against what is this far off, and this far below the boss: as one dive would. */
    public boolean serves(double distance, double below) { return dive.serves(distance, below); }
}
