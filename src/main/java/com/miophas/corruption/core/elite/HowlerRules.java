package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import com.miophas.corruption.core.demon.skill.SummonRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted howler, a land elite, as rules: a gaunt thing twice a man's height, its arms trailing to the ground, its
 * head split down the throat into a howling mouth. It rakes what it reaches (its basic blow); it throws its head back and
 * howls, and the corruption's remnants claw their way up out of the ground about it ({@link #HOWL}: they crumble away
 * after half a minute); and it spits bile that leaves a cursed pool ({@link #BILE}).
 */
public final class HowlerRules {
    public static final double MAX_HEALTH = 90, ARMOR = 4, ATTACK_DAMAGE = 7, MOVEMENT_SPEED = .3, FOLLOW_RANGE = 32, KNOCKBACK_RESISTANCE = .2;
    public static final float WIDTH = .8F, HEIGHT = 2.6F;
    public static final double CHASE = 1.1;
    public static final int BITE_EVERY = 20;

    public enum Skill { NONE, HOWL, BILE }

    public static final List<Skill> SKILLS = List.of(Skill.HOWL, Skill.BILE);

    /** What it howls up: the common dead of the overworld. */
    public static final List<String> LOOKS = List.of("minecraft:zombie", "minecraft:skeleton", "minecraft:spider", "minecraft:husk");
    /** Three remnants round it, never more than six of its about, each crumbling away 30 s after it rises. */
    public static final SummonRules HOWL = new SummonRules(new SkillTiming(24, 36, 400), 3, 6, 3.5, LOOKS, false, 600);
    /** Two gobs of bile at a quarry 4 to 18 off, 6 ticks apart: 5, and 5 within 1.5; each leaves a small pool, cursing for 1.5 s. */
    public static final SpitRules BILE = new SpitRules(new SkillTiming(12, 20, 80), 4, 18, 2, 6, 10, .9, 5, 1.5, false, 1)
            .looking(SpitRules.Look.GLOB).pooling(new SpitRules.Pool(1.2F, 50, 30));

    /** What it does next: it howls whenever it may, and spits the more the further off its quarry is. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double howl = ready.contains(Skill.HOWL) ? 5 : 0;
        double bile = ready.contains(Skill.BILE) ? 3 + (distance > 8 ? 3 : 0) : 0;
        int picked = EliteRules.pick(new double[]{howl, bile}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private HowlerRules() {}
}
