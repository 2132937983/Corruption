package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted bloater, a land elite, as rules: a great sac of rot on four stubby legs, vents on its back seeping the
 * corruption's gas. It butts what it reaches (its basic blow); it swells and vents its gas all about it, a cloud that curses
 * and poisons whatever stands in it ({@link #VENT}); it lobs gobs of the gas that burst into wide pools ({@link #LOB}); and
 * when it dies it bursts, leaving the greatest cloud of all ({@link #BURST}).
 */
public final class BloaterRules {
    public static final double MAX_HEALTH = 110, ARMOR = 2, ATTACK_DAMAGE = 8, MOVEMENT_SPEED = .2, FOLLOW_RANGE = 28, KNOCKBACK_RESISTANCE = .7;
    public static final float WIDTH = 1.6F, HEIGHT = 1.8F;
    public static final double CHASE = 1.2;
    public static final int BITE_EVERY = 25;

    public enum Skill { NONE, VENT, LOB }

    public static final List<Skill> SKILLS = List.of(Skill.VENT, Skill.LOB);

    /**
     * A cloud of the corruption's gas: how wide, how long it lies, how long it curses and poisons what stands in it; and
     * what is within it as it vents is thrown {@code push} away.
     */
    public record Cloud(float radius, int ticks, int curseTicks, int poisonTicks, double push) {}

    /** Its vent, at a quarry within 6: swelling for a second, then a cloud 4 across for 5 s. */
    public static final SkillTiming VENT = new SkillTiming(20, 30, 200);
    public static final double VENT_RANGE = 6;
    public static final Cloud VENT_CLOUD = new Cloud(4, 100, 60, 60, .8);
    /** Its burst as it dies: a cloud 5 across for 6 s. */
    public static final Cloud BURST = new Cloud(5, 120, 60, 80, 1.2);
    /** Two gobs lobbed at a quarry 5 to 18 off, 8 ticks apart, each falling 20 ticks later: 4, and 4 within 2; each a pool 2.5 across for 5 s. */
    public static final SpitRules LOB = new SpitRules(new SkillTiming(16, 26, 120), 5, 18, 2, 8, 0, .8, 4, 2, true, 20)
            .looking(SpitRules.Look.GLOB).pooling(new SpitRules.Pool(2.5F, 100, 60));

    /** What it does next: it vents at a quarry close, and lobs at one further off. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double vent = ready.contains(Skill.VENT) && distance <= VENT_RANGE ? 6 : 0;
        double lob = ready.contains(Skill.LOB) ? 4 : 0;
        int picked = EliteRules.pick(new double[]{vent, lob}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private BloaterRules() {}
}
