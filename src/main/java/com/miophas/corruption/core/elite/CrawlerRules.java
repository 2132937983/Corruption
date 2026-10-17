package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.PounceRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted crawler, a land elite, as rules: a long body of plated segments on many legs, mandibles and a cluster of
 * eyes at its head, a stinger at its tail. It climbs walls as a spider does. It bites what it reaches (its basic blow); it
 * springs at its quarry from a little off ({@link #POUNCE}); and it spits venom that leaves a small cursed pool
 * ({@link #VENOM}).
 */
public final class CrawlerRules {
    public static final double MAX_HEALTH = 80, ARMOR = 6, ATTACK_DAMAGE = 8, MOVEMENT_SPEED = .34, FOLLOW_RANGE = 32, KNOCKBACK_RESISTANCE = .3;
    public static final float WIDTH = 1.4F, HEIGHT = .8F;
    public static final double CHASE = 1.2;
    public static final int BITE_EVERY = 15;

    public enum Skill { NONE, POUNCE, VENOM }

    public static final List<Skill> SKILLS = List.of(Skill.POUNCE, Skill.VENOM);

    /** A spring from 3 to 10 off: crouched half a second, a leap of 10 ticks topping 1.5; 10 to what it meets. */
    public static final PounceRules POUNCE = new PounceRules(new SkillTiming(10, 22, 80), 3, 10, 1.5, 10, .5, 10, .9);
    /** One gob of venom at a quarry 4 to 20 off: 5, leaving a pool .8 across for 2 s that curses for 2 s. */
    public static final SpitRules VENOM = new SpitRules(new SkillTiming(8, 10, 60), 4, 20, 1, 1, 0, 1.2, 5, .8, false, 1)
            .looking(SpitRules.Look.GLOB).pooling(new SpitRules.Pool(.8F, 40, 40));

    /** What it does next: it springs at a quarry within its leap, and spits at one further off. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double pounce = ready.contains(Skill.POUNCE) ? 5 : 0;
        double venom = ready.contains(Skill.VENOM) ? 3 + (distance > POUNCE.maxRange() ? 4 : 0) : 0;
        int picked = EliteRules.pick(new double[]{pounce, venom}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private CrawlerRules() {}
}
