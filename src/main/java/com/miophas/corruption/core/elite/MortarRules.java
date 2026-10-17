package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.BarrageRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpikeFieldRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted mortar, a land elite, as rules: a squat, plated beast with a swollen tube of flesh on its back. It keeps
 * its distance ({@link #keep}: away from a quarry nearer than {@link #NEAR}, towards one further than {@link #FAR}); it
 * rams what corners it (its basic blow); it swells and throws up a rain of the corruption's bolts onto where its quarry
 * stood, the place marked first ({@link #BARRAGE}); and it drives spikes up out of the ground before it at what comes too
 * close ({@link #SPIKES}).
 */
public final class MortarRules {
    public static final double MAX_HEALTH = 120, ARMOR = 10, ATTACK_DAMAGE = 6, MOVEMENT_SPEED = .22, FOLLOW_RANGE = 40, KNOCKBACK_RESISTANCE = .8;
    public static final float WIDTH = 1.8F, HEIGHT = 1.6F;
    public static final double CHASE = 1.1;
    public static final int BITE_EVERY = 25;
    /** The distance it keeps from its quarry, in blocks: it backs away inside {@code NEAR}, closes outside {@code FAR}. */
    public static final double NEAR = 8, FAR = 24;

    public enum Skill { NONE, BARRAGE, SPIKES }

    public static final List<Skill> SKILLS = List.of(Skill.BARRAGE, Skill.SPIKES);

    /** Six bolts thrown onto a mark 3.5 across round a quarry 10 to 32 off, each falling 30 ticks later: 6, and 6 within 2. */
    public static final BarrageRules BARRAGE = new BarrageRules(new SkillTiming(30, 40, 140), 10, 32, 6, 1, 3.5, 30, 6, 2);
    /** Spikes from 1.5 to 7 blocks before it, 2 to 5 across, at a quarry within 7: 6, and Slowness II for 3 s. */
    public static final SpikeFieldRules SPIKES = new SpikeFieldRules(new SkillTiming(16, 22, 160), 0, 7, 1.5, 7, 2, 5, 2, 6, 60, 1);

    /** Which way it goes for its quarry this far off: -1 away, 1 towards, 0 it holds where it is. */
    public static int keep(double distance) { return distance < NEAR ? -1 : distance > FAR ? 1 : 0; }

    /** What it does next: spikes at what is close, its barrage at what is far. */
    public static Skill choose(double distance, Set<Skill> ready, double roll) {
        double barrage = ready.contains(Skill.BARRAGE) ? 5 : 0;
        double spikes = ready.contains(Skill.SPIKES) ? 6 : 0;
        int picked = EliteRules.pick(new double[]{barrage, spikes}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private MortarRules() {}
}
