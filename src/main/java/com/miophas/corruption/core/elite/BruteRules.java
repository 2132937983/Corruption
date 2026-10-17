package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.ChargeRules;
import com.miophas.corruption.core.demon.skill.MeleeRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted brute, a land elite, as rules: a hunched mountain of flesh, one arm swollen into a club of bone and crystal.
 * It is slow and hard to stagger. It clubs what it reaches (its basic blow, {@link #ATTACK_DAMAGE}, every
 * {@link #BITE_EVERY} ticks); rears and brings its club down on everything before it ({@link #SLAM}); charges at a quarry
 * further off, bowling it over and breaking the soft ground in its way ({@link #CHARGE}); and tears up a lump of the
 * corrupted earth and hurls it at one further still ({@link #HURL}).
 */
public final class BruteRules {
    public static final double MAX_HEALTH = 150, ARMOR = 12, ATTACK_DAMAGE = 12, MOVEMENT_SPEED = .26, FOLLOW_RANGE = 32, KNOCKBACK_RESISTANCE = .9;
    public static final float WIDTH = 1.6F, HEIGHT = 2.4F;
    public static final double CHASE = 1.1;
    public static final int BITE_EVERY = 30;

    public enum Skill { NONE, SLAM, CHARGE, HURL }

    /** Its skills, in the order of its set. */
    public static final List<Skill> SKILLS = List.of(Skill.SLAM, Skill.CHARGE, Skill.HURL);

    /** Its club brought down on everything within 3.5 before it, across 120 degrees: 16, thrown 1.4 away and .6 up. */
    public static final MeleeRules SLAM = new MeleeRules(new SkillTiming(18, 30, 80), 3.5, 120, 16, 1.4, .6);
    /** A charge from 6 to 18 blocks off at 1 a tick: 12 to what it meets; it breaks only the soft ground (softer than 1: dirt, sand, leaves, glass). */
    public static final ChargeRules CHARGE = new ChargeRules(new SkillTiming(16, 36, 200), 6, 18, 1, 12, 1.5, 1F);
    /** A lump of earth torn up and hurled at a quarry 8 to 24 off, falling on it 24 ticks later: 10, and 10 within 2.5. */
    public static final SpitRules HURL = new SpitRules(new SkillTiming(20, 22, 120), 8, 24, 1, 1, 0, 1, 10, 2.5, true, 24).looking(SpitRules.Look.GLOB);

    /** What it does next: slam what is close, charge what is a little off, hurl at what is far. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double slam = ready.contains(Skill.SLAM) ? 6 : 0;
        double charge = ready.contains(Skill.CHARGE) ? 4 : 0;
        double hurl = ready.contains(Skill.HURL) ? 3 + (distance > 12 ? 3 : 0) : 0;
        int picked = EliteRules.pick(new double[]{slam, charge, hurl}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private BruteRules() {}
}
