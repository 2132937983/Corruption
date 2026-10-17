package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.PounceRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted aberration, the land's elite, as rules: a low, twisted mass of flesh on six legs, crusted with the
 * corruption's crystals. It runs its quarry down and bites (its basic blow, {@link #ATTACK_DAMAGE}, every
 * {@link #BITE_EVERY} ticks); from further off it pounces ({@link #POUNCE}), or spits globs of the corruption's acid in
 * high arcs ({@link #ACID}), which burst where they land and leave a pool that lays the corruption's curse on whatever
 * stands in it. Brought low ({@link #berserk}), it roars, throwing back whatever is close, and goes berserk for the rest
 * of its life: faster, harder-hitting, and quicker to come round to its skills.
 */
public final class AberrationRules {
    public static final double MAX_HEALTH = 120, ARMOR = 8, ATTACK_DAMAGE = 9, MOVEMENT_SPEED = .3, FOLLOW_RANGE = 32,
            KNOCKBACK_RESISTANCE = .6;
    public static final float WIDTH = 1.8F, HEIGHT = 1.4F;
    /** How fast it runs at its quarry (times its speed), and how often it bites, in ticks. */
    public static final double CHASE = 1.15;
    public static final int BITE_EVERY = 20;

    public enum Skill { NONE, POUNCE, ACID, ROAR }

    /** Its skills, in the order of its set. */
    public static final List<Skill> SKILLS = List.of(Skill.POUNCE, Skill.ACID, Skill.ROAR);

    /** A pounce from four to twelve blocks off: crouched for 14 ticks, a leap of 14 topping 2.2 blocks; 14 to what it meets, thrown on. */
    public static final PounceRules POUNCE = new PounceRules(new SkillTiming(14, 30, 100), 4, 12, 2.2, 14, .6, 14, 1.1);
    /**
     * Three globs of acid lobbed 6 ticks apart at a quarry 5 to 20 blocks off, each falling on it 22 ticks later: 5, and 5
     * within 2.5; each leaves a pool 1.8 blocks across for three and a half seconds, cursing what stands in it for two.
     */
    public static final SpitRules ACID = new SpitRules(new SkillTiming(16, 34, 90), 5, 20, 3, 6, 30, .9, 5, 2.5, true, 22)
            .looking(SpitRules.Look.GLOB).pooling(new SpitRules.Pool(1.8F, 70, 40));
    /** Its roar as it goes berserk: what is within {@code ROAR_RADIUS} is thrown {@code ROAR_KNOCKBACK} blocks a tick away. */
    public static final SkillTiming ROAR = new SkillTiming(10, 30, 0);
    public static final double ROAR_RADIUS = 5, ROAR_KNOCKBACK = 1.2;
    /** It goes berserk at or below this share of its health. */
    public static final double BERSERK_BELOW = .35;
    /** Berserk, its speed and its blows are so much more (a share of what they were), and its cooldowns cut to this share. */
    public static final double BERSERK_SPEED = .35, BERSERK_DAMAGE = .4, BERSERK_COOLDOWNS = .5;

    /** Whether it should go berserk at this share of its health. */
    public static boolean berserk(double health) { return health <= BERSERK_BELOW; }

    /** A skill's cooldown, berserk or not. */
    public static int cooldown(int cooldown, boolean berserk) { return berserk ? (int) Math.round(cooldown * BERSERK_COOLDOWNS) : cooldown; }

    /**
     * What it does next, of the skills that would serve ({@code ready}: in reach, in sight, come round), by a roll from 0
     * to 1: it pounces the more readily the further off its quarry is within its leap, and spits the more the further off
     * it is besides; NONE runs on and bites. The roar is not chosen: it comes when it goes berserk.
     */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double pounce = ready.contains(Skill.POUNCE) ? 4 + Math.min(4, distance - POUNCE.minRange()) : 0;
        double acid = ready.contains(Skill.ACID) ? 3 + (distance > POUNCE.maxRange() ? 6 : 0) : 0;
        int picked = EliteRules.pick(new double[]{pounce, acid}, roll);
        return picked < 0 ? Skill.NONE : picked == 0 ? Skill.POUNCE : Skill.ACID;
    }

    private AberrationRules() {}
}
