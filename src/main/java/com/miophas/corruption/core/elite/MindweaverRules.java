package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.PsychicRules;
import com.miophas.corruption.core.demon.skill.PsychicStreamRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.List;
import java.util.Set;

/**
 * The corrupted mindweaver, a land elite, as rules: a great brain borne on a squat body, slow and hard to wear down (the
 * most health of any elite, more than the least of the lords, though any blow hurts it and a lord needs the arcane), and
 * all but harmless to strike at close in (its basic blow, {@link #ATTACK_DAMAGE}, every {@link #BITE_EVERY} ticks, is a
 * feeble slap). Its harm is in the mind, and it comes often: it draws a thread out of its quarry's mind and pulls at it
 * over and over, two and a half times a second ({@link #THREAD}), each pull healing it a little; and it wails, striking at
 * the minds of everything about its quarry, their sight swimming and darkening, their limbs heavy ({@link #WAIL}).
 */
public final class MindweaverRules {
    public static final double MAX_HEALTH = 240, ARMOR = 2, ATTACK_DAMAGE = 2, MOVEMENT_SPEED = .2, FOLLOW_RANGE = 32, KNOCKBACK_RESISTANCE = .8;
    public static final float WIDTH = 1.6F, HEIGHT = 2.2F;
    public static final double CHASE = 1;
    public static final int BITE_EVERY = 30;

    public enum Skill { NONE, THREAD, WAIL }

    /** Its skills, in the order of its set. */
    public static final List<Skill> SKILLS = List.of(Skill.THREAD, Skill.WAIL);

    /**
     * Its thread: on a quarry up to 18 off, held until it is 22 off or out of sight for two pulses running; a pulse every
     * 8 ticks for 4 s (ten), each 1 of psychic hurt, healing it 1.
     */
    public static final PsychicStreamRules THREAD = new PsychicStreamRules(new SkillTiming(12, 92, 40), 0, 18, 22, 8, 1, 1, 2);
    /** Its wail: everything within 4 of a quarry up to 10 off, 5 of the corruption's hurt, Nausea 5 s, Darkness 3 s, Slowness II 3 s. */
    public static final PsychicRules WAIL = new PsychicRules(new SkillTiming(16, 18, 180), 0, 10, 4, 5, 100, 60, 60, 1);

    /** What it does next: its thread whenever it can, its wail the more for a quarry close by. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double thread = ready.contains(Skill.THREAD) && THREAD.inRange(distance) ? 6 : 0;
        double wail = ready.contains(Skill.WAIL) && WAIL.inRange(distance) ? 4 + (distance <= 6 ? 3 : 0) : 0;
        int picked = EliteRules.pick(new double[]{thread, wail}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private MindweaverRules() {}
}
