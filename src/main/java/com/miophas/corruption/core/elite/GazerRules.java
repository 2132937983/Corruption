package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.BeamRules;
import com.miophas.corruption.core.demon.skill.GazeRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted gazer, an air elite, as rules: one great eye ringed with lesser eyes on stalks, trailing nerves beneath
 * it. It hangs well off its quarry ({@link #KEEP_AT}), lashing with its nerves at what comes close (its basic blow); it
 * looses bolts from its lesser eyes ({@link #BOLTS}); it fixes its quarry with its great eye until it is slowed and blinded
 * ({@link #GAZE}); and it burns it with a beam from its great eye that follows it ({@link #BEAM}).
 */
public final class GazerRules {
    public static final double MAX_HEALTH = 70, ARMOR = 2, ATTACK_DAMAGE = 6, FLYING_SPEED = .4, FOLLOW_RANGE = 40, KNOCKBACK_RESISTANCE = .3;
    public static final float WIDTH = 1.2F, HEIGHT = 1.2F;
    /** How far from its quarry it hangs, and how high over the ground; how near it lashes, and how often. */
    public static final double KEEP_AT = 12, HOVER_LOW = 4, HOVER_HIGH = 7, LASH_REACH = 2.8;
    public static final int LASH_EVERY = 25;

    public enum Skill { NONE, BEAM, GAZE, BOLTS }

    public static final List<Skill> SKILLS = List.of(Skill.BEAM, Skill.GAZE, Skill.BOLTS);

    /** A beam at a quarry 3 to 28 off: 1.5 s to gather, then 30 long and 1.2 wide for 2.5 s, 4 every 4 ticks, following at 1.8 a tick. */
    public static final BeamRules BEAM = new BeamRules(new SkillTiming(30, 80, 200), 3, 28, 30, 1.2, 4, 4, 1.8F);
    /** A gaze at a quarry 3 to 20 off: Slowness II 5 s and blindness 3 s; four eyes open about it. */
    public static final GazeRules GAZE = new GazeRules(new SkillTiming(24, 25, 260), 3, 20, 100, 1, 60, 0, 0, 4);
    /** Three bolts from its lesser eyes across 16 degrees at a quarry 4 to 24 off: 4 each. */
    public static final SpitRules BOLTS = new SpitRules(new SkillTiming(10, 16, 60), 4, 24, 3, 3, 16, 1.2, 4, 0, false, 1);

    /** What it does next: its beam the most, its bolts at what is nearer, its gaze whenever it may. */
    public static Skill choose(double distance, boolean quarrySlowed, Set<Skill> ready, double roll) {
        double beam = ready.contains(Skill.BEAM) ? 5 : 0;
        double gaze = ready.contains(Skill.GAZE) && !quarrySlowed ? 4 : 0;
        double bolts = ready.contains(Skill.BOLTS) ? 3 + (distance < 10 ? 2 : 0) : 0;
        int picked = EliteRules.pick(new double[]{beam, gaze, bolts}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private GazerRules() {}
}
