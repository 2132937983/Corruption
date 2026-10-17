package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.DiveRules;
import com.miophas.corruption.core.demon.skill.ScreechRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted screecher, the air's elite, as rules: a flayed bird of bare bone and torn wing, its eyes burning. It
 * circles its quarry high up ({@link #KEEP_AT} blocks off, {@link #HOVER_LOW} to {@link #HOVER_HIGH} over the ground) and
 * swoops on it to bite (its basic blow, {@link #ATTACK_DAMAGE}); it looses fans of the corruption's feathers at it
 * ({@link #FEATHERS}); it folds its wings and dives on where it stands ({@link #DIVE}); and it screeches ({@link #SCREECH}),
 * stunning everything near it for a moment and leaving it slowed.
 */
public final class ScreecherRules {
    public static final double MAX_HEALTH = 80, ARMOR = 4, ATTACK_DAMAGE = 7, FLYING_SPEED = .55, FOLLOW_RANGE = 40, KNOCKBACK_RESISTANCE = .2;
    public static final float WIDTH = 1.4F, HEIGHT = 1F;
    /** How far from its quarry it circles, and how high over the ground. */
    public static final double KEEP_AT = 8, HOVER_LOW = 4, HOVER_HIGH = 7;

    public enum Skill { NONE, SWOOP, DIVE, FEATHERS, SCREECH }

    /** Its skills, in the order of its set. */
    public static final List<Skill> SKILLS = List.of(Skill.SWOOP, Skill.DIVE, Skill.FEATHERS, Skill.SCREECH);

    /** A swoop to bite: from up to {@code SWOOP_RANGE} off, straight at its quarry at {@code SWOOP_SPEED}, biting as it meets it. */
    public static final SkillTiming SWOOP = new SkillTiming(6, 40, 40);
    public static final double SWOOP_RANGE = 16, SWOOP_SPEED = .85;
    /** A dive from 5 to 24 blocks off and at least 3 over its quarry: 12 to what is within 2.2 of where it strikes. Nothing torn up. */
    public static final DiveRules DIVE = new DiveRules(new SkillTiming(16, 40, 140), 5, 24, 3, 1.3, 2.2, 12, 1.1, 0, 0, 0);
    /** Five feathers, 2 ticks apart, swept across 36 degrees at its quarry 4 to 24 blocks off: 4 each. */
    public static final SpitRules FEATHERS = new SpitRules(new SkillTiming(12, 22, 70), 4, 24, 5, 2, 36, 1.3, 4, 0, false, 1)
            .looking(SpitRules.Look.FEATHER);
    /** A screech at a quarry within 12: what is within 8 is stunned a second and a half, and slowed (Slowness II) five seconds. */
    public static final ScreechRules SCREECH = new ScreechRules(new SkillTiming(14, 26, 260), 0, 12, 8, 30, 100, 2);

    /**
     * What it does next, of the skills that would serve ({@code ready}), by a roll from 0 to 1. It swoops at a quarry
     * near enough, the more the nearer; looses feathers at one further off; dives the more readily the further off it is;
     * and screeches at one close, but not at one stunned already.
     */
    public static Skill choose(double distance, boolean quarryStunned, Set<Skill> ready, double roll) {
        double swoop = ready.contains(Skill.SWOOP) ? 5 + Math.max(0, 8 - distance) : 0;
        double dive = ready.contains(Skill.DIVE) ? 2 + Math.min(4, distance / 4) : 0;
        double feathers = ready.contains(Skill.FEATHERS) ? 4 + (distance > 10 ? 3 : 0) : 0;
        double screech = ready.contains(Skill.SCREECH) && !quarryStunned ? 4 : 0;
        int picked = EliteRules.pick(new double[]{swoop, dive, feathers, screech}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private ScreecherRules() {}
}
