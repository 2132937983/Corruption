package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.PhaseDashRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted stalker, a land elite, as rules: a thin, stooping hunter on long legs, its forearms grown into blades, its
 * face a single burning slit. It is fast. It slashes what it reaches (its basic blow); it fades from sight for a while,
 * only its slit and its edges glowing, and runs the faster ({@link #FADE_TICKS}); it flickers back and forth through its
 * quarry, cutting at every pass ({@link #PHASE}); and it flings spines of bone ({@link #SPINES}).
 */
public final class StalkerRules {
    public static final double MAX_HEALTH = 85, ARMOR = 5, ATTACK_DAMAGE = 9, MOVEMENT_SPEED = .36, FOLLOW_RANGE = 36, KNOCKBACK_RESISTANCE = .2;
    public static final float WIDTH = .7F, HEIGHT = 2.2F;
    public static final double CHASE = 1.2;
    public static final int BITE_EVERY = 18;

    public enum Skill { NONE, FADE, PHASE, SPINES }

    public static final List<Skill> SKILLS = List.of(Skill.FADE, Skill.PHASE, Skill.SPINES);

    /** Its fade: from a quarry at least {@code FADE_FROM} off, unseen for four seconds, and Speed II as long. */
    public static final SkillTiming FADE = new SkillTiming(8, 12, 300);
    public static final int FADE_TICKS = 80;
    public static final double FADE_FROM = 6;
    /** Three blinks through a quarry 3 to 12 off, 6 ticks apart, 3 blocks beyond it: 6 to what is within 1.2, cursed 1.5 s. */
    public static final PhaseDashRules PHASE = new PhaseDashRules(new SkillTiming(10, 24, 160), 3, 12, 3, 6, 3, 40, 1.2, 6, 30, 0, 2);
    /** Three spines of bone across 20 degrees at a quarry 5 to 24 off: 4 each. */
    public static final SpitRules SPINES = new SpitRules(new SkillTiming(10, 16, 70), 5, 24, 3, 2, 20, 1.5, 4, 0, false, 1);

    /** What it does next: it fades from a quarry far enough off, flickers through one near, and flings spines at one further. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double fade = ready.contains(Skill.FADE) && distance >= FADE_FROM ? 4 : 0;
        double phase = ready.contains(Skill.PHASE) ? 5 : 0;
        double spines = ready.contains(Skill.SPINES) ? 3 + (distance > 12 ? 3 : 0) : 0;
        int picked = EliteRules.pick(new double[]{fade, phase, spines}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private StalkerRules() {}
}
