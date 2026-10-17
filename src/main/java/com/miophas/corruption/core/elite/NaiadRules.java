package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.PounceRules;
import com.miophas.corruption.core.demon.skill.ScreechRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted naiad, a sea elite, as rules: a drowned thing with a woman's gaunt body and a finned tail, gills cut in
 * its neck, a spear of bone grown out of its arm. In the water it is fast, as the lurker is. It thrusts with its spear at
 * what it reaches (its basic blow); it hurls bone spears ({@link #SPEAR}); it surges at its quarry ({@link #SURGE}); and
 * it sings, a song that slows what hears it ({@link #SONG}).
 */
public final class NaiadRules {
    public static final double MAX_HEALTH = 90, ARMOR = 6, ATTACK_DAMAGE = 9, MOVEMENT_SPEED = 1, FOLLOW_RANGE = 32, KNOCKBACK_RESISTANCE = .3;
    public static final float WIDTH = .8F, HEIGHT = 2F;
    public static final float WANDER = .06F, CRAWL = .08F;
    public static final double DART = .55, THRUST = .12, BITE_REACH = 2.4;
    public static final int BITE_EVERY = 20;

    public enum Skill { NONE, SPEAR, SURGE, SONG }

    public static final List<Skill> SKILLS = List.of(Skill.SPEAR, Skill.SURGE, Skill.SONG);

    /** A spear of bone hurled at a quarry 4 to 28 off, fast: 9, throwing it back .6. */
    public static final SpitRules SPEAR = new SpitRules(new SkillTiming(14, 16, 60), 4, 28, 1, 1, 0, 2, 9, 0, false, 1).pushing(.6);
    /** A surge at a quarry 3 to 10 off: 8 ticks, low; 10 to what it meets. */
    public static final PounceRules SURGE = new PounceRules(new SkillTiming(10, 20, 100), 3, 10, .8, 8, .6, 10, 1);
    /** A song at a quarry within 10: what is within 8 is stilled a moment and slowed (Slowness III) for 4 s. */
    public static final ScreechRules SONG = new ScreechRules(new SkillTiming(20, 30, 240), 0, 10, 8, 10, 80, 3);

    /** What it does next: its spear at a quarry off, a surge at one near, its song when it is close and the quarry not slowed. */
    public static Skill choose(double distance, boolean quarrySlowed, Set<Skill> ready, double roll) {
        double spear = ready.contains(Skill.SPEAR) ? 4 + (distance > 10 ? 3 : 0) : 0;
        double surge = ready.contains(Skill.SURGE) ? 4 : 0;
        double song = ready.contains(Skill.SONG) && !quarrySlowed ? 4 : 0;
        int picked = EliteRules.pick(new double[]{spear, surge, song}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private NaiadRules() {}
}
