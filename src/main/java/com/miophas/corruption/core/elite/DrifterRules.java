package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.BarrageRules;
import com.miophas.corruption.core.demon.skill.GrabRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.List;
import java.util.Set;

/**
 * The corrupted drifter, an air elite, as rules: a great bell of membrane, a glowing heart in it, a frilled skirt and long
 * tendrils hanging beneath. It drifts slowly and high over its quarry ({@link #HOVER_LOW} to {@link #HOVER_HIGH} over the
 * ground, {@link #KEEP_AT} off), stinging with its tendrils what comes under it (its basic blow); it lets acid rain down
 * onto where its quarry stood ({@link #RAIN}); and it takes hold of its quarry with its tendrils and draws it up to itself,
 * high over the ground, and lets it go ({@link #LIFT}).
 */
public final class DrifterRules {
    public static final double MAX_HEALTH = 75, ARMOR = 0, ATTACK_DAMAGE = 6, FLYING_SPEED = .3, FOLLOW_RANGE = 36, KNOCKBACK_RESISTANCE = .4;
    public static final float WIDTH = 1.4F, HEIGHT = 1.6F;
    public static final double KEEP_AT = 3, HOVER_LOW = 7, HOVER_HIGH = 10, STING_REACH = 3;
    public static final int STING_EVERY = 25;

    public enum Skill { NONE, LIFT, RAIN }

    public static final List<Skill> SKILLS = List.of(Skill.LIFT, Skill.RAIN);

    /** Tendrils lashed down at a quarry within 11: 4 as they take hold, then drawn up .35 a tick to within 1.5 of it. */
    public static final GrabRules LIFT = new GrabRules(new SkillTiming(12, 70, 220), 11, 1.5, .35, 0, 4);
    /** Eight drops of acid onto a mark 3 across round a quarry within 16, each falling 18 ticks later: 4, and 4 within 1.5. */
    public static final BarrageRules RAIN = new BarrageRules(new SkillTiming(16, 26, 160), 0, 16, 8, 1, 3, 18, 4, 1.5);

    /** What it does next: it lifts what it can reach, and rains acid on what it cannot. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double lift = ready.contains(Skill.LIFT) ? 5 : 0;
        double rain = ready.contains(Skill.RAIN) ? 4 : 0;
        int picked = EliteRules.pick(new double[]{lift, rain}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private DrifterRules() {}
}
