package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.PounceRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted maw, a sea elite, as rules: a great eel whose whole front is a round mouth ringed with teeth, a glowing
 * lure dangling over it. In the water it is fast, as the lurker is. It bites what it reaches (its basic blow, the hardest
 * of the elites'); it lunges at its quarry, its mouth agape ({@link #LUNGE}); it spits a great bolt of water that throws
 * back what it strikes ({@link #SPOUT}); and it opens its mouth wide and sucks in the water about it, drawing everything
 * near towards its teeth ({@link #WHIRLPOOL}).
 */
public final class MawRules {
    public static final double MAX_HEALTH = 140, ARMOR = 8, ATTACK_DAMAGE = 11, MOVEMENT_SPEED = 1, FOLLOW_RANGE = 32, KNOCKBACK_RESISTANCE = .8;
    public static final float WIDTH = 2F, HEIGHT = 1.4F;
    public static final float WANDER = .05F, CRAWL = .06F;
    public static final double DART = .55, THRUST = .1, BITE_REACH = 2.8;
    public static final int BITE_EVERY = 25;

    public enum Skill { NONE, WHIRLPOOL, LUNGE, SPOUT }

    public static final List<Skill> SKILLS = List.of(Skill.WHIRLPOOL, Skill.LUNGE, Skill.SPOUT);

    /** Its whirlpool, in the water: a second gathering, then for 3 s everything within {@code WHIRL_RADIUS} is drawn towards it. */
    public static final SkillTiming WHIRLPOOL = new SkillTiming(20, 80, 300);
    public static final double WHIRL_RADIUS = 10, WHIRL_PULL = .22;
    /** A lunge from 4 to 14 off: 10 ticks, low; 14 to what it meets. */
    public static final PounceRules LUNGE = new PounceRules(new SkillTiming(12, 24, 120), 4, 14, .5, 10, .8, 14, 1.2);
    /** One great bolt of water at a quarry 4 to 24 off: 8, throwing it back 1.2. */
    public static final SpitRules SPOUT = new SpitRules(new SkillTiming(14, 16, 90), 4, 24, 1, 1, 0, 1.5, 8, 1, false, 1)
            .looking(SpitRules.Look.WATER).pushing(1.2);

    /** How hard its whirlpool draws in what is this far off: the nearer, the harder, nothing past its edge. */
    public static double pull(double distance) { return distance > WHIRL_RADIUS ? 0 : WHIRL_PULL * (.4 + .6 * (1 - distance / WHIRL_RADIUS)); }

    /** What it does next: its whirlpool only in the water, a lunge at a quarry near, a spout at one further. */
    public static Skill choose(double distance, boolean inWater, Set<Skill> ready, double roll) {
        double whirl = ready.contains(Skill.WHIRLPOOL) && inWater && distance <= WHIRL_RADIUS ? 5 : 0;
        double lunge = ready.contains(Skill.LUNGE) ? 5 : 0;
        double spout = ready.contains(Skill.SPOUT) ? 3 + (distance > 14 ? 3 : 0) : 0;
        int picked = EliteRules.pick(new double[]{whirl, lunge, spout}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private MawRules() {}
}
