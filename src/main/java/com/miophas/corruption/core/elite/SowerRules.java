package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.BlightRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SnareRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted sower, a land elite, as rules: a walking mound of the corruption's flesh on legs like roots, slow and
 * heavy. The ground it walks over is taken by the infection under its feet, as though the corruption had spread there
 * itself (at most {@link #SOW_MOST} blocks a tick); that ground is dormant unless the configuration says otherwise, an
 * ordinary corrupted variant that spreads no further. It mends on the corruption's own ground
 * ({@link #REGEN} every {@link #REGEN_EVERY} ticks). It strikes what it reaches ({@link #ATTACK_DAMAGE}, every
 * {@link #BITE_EVERY} ticks); roots itself and sows the ground all about it, throwing back whatever is near
 * ({@link #SEED}), leaving every block it takes dormant, as the ground under its feet; and sends roots up out of the ground
 * under a quarry further off, holding it fast, the longer on the corruption's own ground ({@link #SNARE}).
 */
public final class SowerRules {
    public static final double MAX_HEALTH = 130, ARMOR = 6, ATTACK_DAMAGE = 7, MOVEMENT_SPEED = .22, FOLLOW_RANGE = 32, KNOCKBACK_RESISTANCE = .7;
    public static final float WIDTH = 1.6F, HEIGHT = 1.5F;
    public static final double CHASE = 1;
    public static final int BITE_EVERY = 25;

    /** The most blocks its feet take in a tick. */
    public static final int SOW_MOST = 4;
    /** How much it mends on the corruption's ground, and how often. */
    public static final float REGEN = 1;
    public static final int REGEN_EVERY = 20;

    public enum Skill { NONE, SEED, SNARE }

    /** Its skills, in the order of its set. */
    public static final List<Skill> SKILLS = List.of(Skill.SEED, Skill.SNARE);

    /**
     * Its sowing: for a quarry within 6; rooted for a second, then 6 and thrown back within 4.5, and up to 48 blocks taken
     * by the infection within 4.5 over 0.6 s. Still: each block it takes keeps its own corrupted variant and is left
     * dormant where it lies, hurting as the corruption does but never spreading on from there (as the ground under its
     * feet).
     */
    public static final BlightRules SEED = new BlightRules(new SkillTiming(20, 36, 200), 6, 4.5, 48, 12, false, true, 6, .7, .3);
    /** Its roots: under a quarry 3 to 16 off, 0.7 s to come up; 6, Slowness IV for 2 s (4 s on the corruption's ground), thrown a little up. */
    public static final SnareRules SNARE = new SnareRules(new SkillTiming(14, 20, 120), 3, 16, 1.4, 6, 40, 3, .2);

    /** What it does next: sow the ground about a quarry close by, send roots up under one further off. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double seed = ready.contains(Skill.SEED) && SEED.inRange(distance) ? 5 + (distance <= 4 ? 3 : 0) : 0;
        double snare = ready.contains(Skill.SNARE) && SNARE.inRange(distance) ? 5 : 0;
        int picked = EliteRules.pick(new double[]{seed, snare}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private SowerRules() {}
}
