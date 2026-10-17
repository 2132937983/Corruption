package com.miophas.corruption.core.elite;

import com.miophas.corruption.core.demon.skill.GrabRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpitRules;
import java.util.List;
import java.util.Set;

/**
 * The corrupted lurker, the sea's elite, as rules: a bloated, many-eyed horror trailing tentacles, the corruption
 * leaking out of it. In the water it is terribly fast, hunting ({@link #DART} blocks a tick, three times a swimming
 * player's), though it drifts slowly about when it has nothing to hunt; on land it drags itself along slowly
 * ({@link #CRAWL}), and makes for the water. It bites what it reaches (its basic blow, {@link #ATTACK_DAMAGE}); it shoots bolts of the
 * corruption's water under great pressure ({@link #BOLTS}), which throw back what they strike; and it lashes a tentacle
 * out at a quarry ({@link #GRAB}), takes hold, and drags it in to itself, down into the water.
 */
public final class LurkerRules {
    public static final double MAX_HEALTH = 100, ARMOR = 6, ATTACK_DAMAGE = 8, MOVEMENT_SPEED = 1, FOLLOW_RANGE = 32, KNOCKBACK_RESISTANCE = .5;
    public static final float WIDTH = 1.6F, HEIGHT = 1F;
    /**
     * How much of its speed it swims with as it drifts about, and crawls with on land (each a share of its movement speed,
     * as the game's swimmers reckon it); and how fast it darts at its quarry in the water, in blocks a tick, gathering
     * {@code THRUST} more each tick.
     */
    public static final float WANDER = .06F, CRAWL = .1F;
    public static final double DART = .6, THRUST = .12;
    /** How near its middle must be to its quarry's for it to bite, in blocks, and how often it bites, in ticks. */
    public static final double BITE_REACH = 2.4;
    public static final int BITE_EVERY = 20;

    public enum Skill { NONE, GRAB, BOLTS }

    /** Its skills, in the order of its set. */
    public static final List<Skill> SKILLS = List.of(Skill.GRAB, Skill.BOLTS);

    /** A tentacle lashed out up to 12 blocks: 6 as it takes hold, then drawn in .4 a tick (and .1 down) until within 2. */
    public static final GrabRules GRAB = new GrabRules(new SkillTiming(10, 60, 180), 12, 2, .4, .1, 6);
    /** Three bolts of water, 4 ticks apart, straight at a quarry 3 to 24 off, fast: 6 each, throwing what they strike back .9. */
    public static final SpitRules BOLTS = new SpitRules(new SkillTiming(10, 22, 50), 3, 24, 3, 4, 8, 1.7, 6, 0, false, 1)
            .looking(SpitRules.Look.WATER).pushing(.9);

    /**
     * What it does next, of the skills that would serve ({@code ready}), by a roll from 0 to 1: it drags a quarry that is
     * out of the water in more readily than one in it, and shoots the more the further off it is.
     */
    public static Skill choose(double distance, boolean quarryInWater, Set<Skill> ready, double roll) {
        double grab = ready.contains(Skill.GRAB) ? quarryInWater ? 2 : 6 : 0;
        double bolts = ready.contains(Skill.BOLTS) ? 3 + (distance > GRAB.reach() ? 5 : 0) : 0;
        int picked = EliteRules.pick(new double[]{grab, bolts}, roll);
        return picked < 0 ? Skill.NONE : SKILLS.get(picked);
    }

    private LurkerRules() {}
}
