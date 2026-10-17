package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import com.miophas.corruption.core.demon.skill.SummonRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted hivemother, an air elite, as rules: a flying hive of flesh on four droning wings, its cells swollen with
 * brood, a stinger beneath. It circles its quarry and swoops to sting it (its basic blow, as the screecher's swoop); it
 * looses fans of stingers ({@link #STINGERS}); and it lets out a swarm, the corruption's remnants of bees and vexes,
 * which crumble away after twenty seconds ({@link #SWARM}).
 */
public final class HivemotherRules {
    public static final double MAX_HEALTH = 90, ARMOR = 4, ATTACK_DAMAGE = 7, FLYING_SPEED = .45, FOLLOW_RANGE = 40, KNOCKBACK_RESISTANCE = .2;
    public static final float WIDTH = 1.4F, HEIGHT = 1.2F;
    public static final double KEEP_AT = 9, HOVER_LOW = 4, HOVER_HIGH = 7;

    public enum Skill { NONE, SWOOP, STINGERS, SWARM }

    public static final List<Skill> SKILLS = List.of(Skill.SWOOP, Skill.STINGERS, Skill.SWARM);

    /** Its swoop, as the screecher's. */
    public static final SkillTiming SWOOP = new SkillTiming(6, 40, 50);
    public static final double SWOOP_RANGE = 14, SWOOP_SPEED = .8;
    /** Five stingers, 2 ticks apart, across 30 degrees at a quarry 4 to 22 off: 3.5 each. */
    public static final SpitRules STINGERS = new SpitRules(new SkillTiming(10, 20, 60), 4, 22, 5, 2, 30, 1.4, 3.5, 0, false, 1);
    /** What its swarm is: the corruption's bees and vexes. */
    public static final List<String> LOOKS = List.of("minecraft:bee", "minecraft:vex");
    /** Three of its swarm round it, never more than six about, each crumbling away 20 s after it comes. */
    public static final SummonRules SWARM = new SummonRules(new SkillTiming(20, 30, 360), 3, 6, 2, LOOKS, false, 400);

    /** What it does next: its swarm whenever there is room for it, stingers at a quarry off, a swoop at one near. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double swoop = ready.contains(Skill.SWOOP) ? 4 + Math.max(0, 8 - distance) : 0;
        double stingers = ready.contains(Skill.STINGERS) ? 4 : 0;
        double swarm = ready.contains(Skill.SWARM) ? 5 : 0;
        int picked = EliteRules.pick(new double[]{swoop, stingers, swarm}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private HivemotherRules() {}
}
