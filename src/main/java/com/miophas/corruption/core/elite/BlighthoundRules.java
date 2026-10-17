package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.corruption.CurseStackRules;
import com.miophas.corruption.core.demon.skill.PounceRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.List;
import java.util.Set;

/**
 * The corrupted blighthound, a land elite, as rules: a flayed hound, faster than anything a player can outrun, with little
 * health and a weak bite ({@link #ATTACK_DAMAGE}, every {@link #BITE_EVERY} ticks). Its harm is the corruption's curse:
 * every bite lays it on deeper ({@link #CURSE}), so a quarry it keeps biting is cursed longer and longer and at the last
 * twice as hard, and the curse goes on biting after the hound is dead. It springs through its quarry, laying the curse on
 * three bites deep at once ({@link #REND}); it worries a quarry close by, five bites in a second and a half
 * ({@link #FRENZY}); and as it runs it leaves a trail of the cursed gas behind it ({@link #TRAIL}).
 */
public final class BlighthoundRules {
    public static final double MAX_HEALTH = 60, ARMOR = 2, ATTACK_DAMAGE = 3, MOVEMENT_SPEED = .42, FOLLOW_RANGE = 40, KNOCKBACK_RESISTANCE = 0;
    public static final float WIDTH = .9F, HEIGHT = 1.1F;
    public static final double CHASE = 1.3;
    public static final int BITE_EVERY = 12;

    /**
     * How its bites curse: 2.5 s at first, a second more for every bite after, 6 s at the most; Curse II once it has more
     * than 5 s to run. A hound biting every 0.6 s brings it to Curse II with its eighth bite, a little over four seconds in.
     */
    public static final CurseStackRules CURSE = new CurseStackRules(50, 20, 120, 50, 1);

    public enum Skill { NONE, REND, FRENZY }

    /** Its skills, in the order of its set. */
    public static final List<Skill> SKILLS = List.of(Skill.REND, Skill.FRENZY);

    /** Its spring through its quarry: from 3 to 10 off, a low leap of 8 ticks, 4 and thrown a little; the curse laid on three bites deep. */
    public static final PounceRules REND = new PounceRules(new SkillTiming(8, 18, 100), 3, 10, .8, 8, .7, 4, .6);
    public static final int REND_BITES = 3;

    /** Its worrying: a quarry within 2.4, bitten every 6 ticks for a second and a half (five bites), each 2 and the curse laid on deeper. */
    public static final SkillTiming FRENZY = new SkillTiming(6, 36, 160);
    public static final int FRENZY_EVERY = 6;
    public static final double FRENZY_REACH = 2.4, FRENZY_DAMAGE = 2;

    /** Its trail: running faster than this (blocks a tick), it leaves a small cloud of the cursed gas behind it every so many ticks. */
    public static final double TRAIL_SPEED = .15;
    public static final int TRAIL_EVERY = 10;
    public static final BloaterRules.Cloud TRAIL = new BloaterRules.Cloud(1.2F, 60, 50, 0, 0);

    /** Whether a bite of its frenzy falls on this tick of it. */
    public static boolean frenzyBites(int elapsed) { return elapsed >= FRENZY.windup() && elapsed < FRENZY.ticks() && (elapsed - FRENZY.windup()) % FRENZY_EVERY == 0; }

    /** What it does next: worry a quarry close by, spring at one a little off. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double rend = ready.contains(Skill.REND) && distance >= REND.minRange() && distance <= REND.maxRange() ? 5 : 0;
        double frenzy = ready.contains(Skill.FRENZY) && distance <= FRENZY_REACH + .6 ? 6 : 0;
        int picked = EliteRules.pick(new double[]{rend, frenzy}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private BlighthoundRules() {}
}
