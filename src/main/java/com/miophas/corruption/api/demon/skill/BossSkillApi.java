package com.miophas.corruption.api.demon.skill;

import com.miophas.corruption.core.demon.skill.BeamRules;
import com.miophas.corruption.core.demon.skill.BeamVolleyRules;
import com.miophas.corruption.core.demon.skill.BlinkRules;
import com.miophas.corruption.core.demon.skill.ChargeRules;
import com.miophas.corruption.core.demon.skill.DiveRules;
import com.miophas.corruption.core.demon.skill.GazeRules;
import com.miophas.corruption.core.demon.skill.MeleeRules;
import com.miophas.corruption.core.demon.skill.MindControlRules;
import com.miophas.corruption.core.demon.skill.PhaseDashRules;
import com.miophas.corruption.core.demon.skill.SpikeFieldRules;
import com.miophas.corruption.core.demon.skill.StrikeRules;
import com.miophas.corruption.core.demon.skill.SummonRules;
import java.util.function.ToIntFunction;
import net.minecraft.world.entity.Mob;

/**
 * The mod's boss skills, for any boss to carry, an add-on's as much as the mod's own. Each is made from its rules in
 * core.demon.skill, where every number of it is (the presets the mod's bosses use are in AndromaliusRules and
 * DantalionRules, to copy or to build on), and carried out by the mod. Put what these make in a {@link SkillSet} with
 * the boss's other skills, and tick the set from the boss.
 *
 * <ul>
 *   <li>{@link #gaze}: the brainwashing gaze, its Eyes of Providence; slows, blinds, withers.</li>
 *   <li>{@link #charge}: a rush along a line that rams what it meets and smashes softer blocks.</li>
 *   <li>{@link #summon}: a band of the corruption's remnants, in the likenesses asked for.</li>
 *   <li>{@link #blink}: away from danger to a safer place round the quarry; the boss weighs when.</li>
 *   <li>{@link #strike}: the corruption's lightning, every place marked before it falls; one bolt or a storm.</li>
 *   <li>{@link #beam}: a beam of the corruption that follows the quarry, turning no faster than it may (from wherever a
 *       {@link BeamCaster} says, else from the boss's eyes).</li>
 *   <li>{@link #mindControl}: a player's view held on the boss, and their feet too, if the rules say.</li>
 *   <li>{@link #melee}: a blow struck close in, in an arc before the boss; reared or drawn back for it first.</li>
 *   <li>{@link #phaseDash}: a run of blinks back and forth through the quarry, the corruption's curse along every line.</li>
 *   <li>{@link #spikeField}: spikes thrown up all over a field of ground before the boss, marked first; they hurt and slow.</li>
 *   <li>{@link #dive}: a flying boss's dive on the place its quarry stood, marked first, tearing up the ground where it strikes.</li>
 *   <li>{@link #beamVolley}: beams out of magic circles, as many at once as the boss says (by its health, say).</li>
 * </ul>
 * Colours are 0xRRGGBB: a skill's colour, and its accent, for its brightest parts.
 */
public final class BossSkillApi {
    /** What carries the skills out: the mod's own, fitted as the game starts. */
    public interface Maker {
        BossSkill<Mob> gaze(GazeRules rules, int colour, int accent);
        BossSkill<Mob> charge(ChargeRules rules);
        BossSkill<Mob> summon(SummonRules rules, int colour, int accent);
        BossSkill<Mob> blink(BlinkRules rules, int colour, int accent);
        BossSkill<Mob> strike(StrikeRules rules, int colour, int accent);
        BossSkill<Mob> beam(BeamRules rules, int colour, int accent);
        BossSkill<Mob> mindControl(MindControlRules rules, int colour, int accent);
        BossSkill<Mob> melee(MeleeRules rules);
        BossSkill<Mob> phaseDash(PhaseDashRules rules, int colour, int accent);
        BossSkill<Mob> spikeField(SpikeFieldRules rules, int colour, int accent);
        BossSkill<Mob> dive(DiveRules rules, int colour, int accent);
        BossSkill<Mob> beamVolley(BeamVolleyRules rules, ToIntFunction<Mob> count, int colour, int accent);
    }

    private static Maker maker;

    /** Fits what carries the skills out. The mod does so as the game starts; a second fitting replaces the first. */
    public static void install(Maker made) { maker = made; }

    private static Maker maker() {
        if (maker == null) throw new IllegalStateException("The mod's boss skills are fitted as the game starts; ask for them after that");
        return maker;
    }

    public static BossSkill<Mob> gaze(GazeRules rules, int colour, int accent) { return maker().gaze(rules, colour, accent); }
    public static BossSkill<Mob> charge(ChargeRules rules) { return maker().charge(rules); }
    public static BossSkill<Mob> summon(SummonRules rules, int colour, int accent) { return maker().summon(rules, colour, accent); }
    public static BossSkill<Mob> blink(BlinkRules rules, int colour, int accent) { return maker().blink(rules, colour, accent); }
    public static BossSkill<Mob> strike(StrikeRules rules, int colour, int accent) { return maker().strike(rules, colour, accent); }
    public static BossSkill<Mob> beam(BeamRules rules, int colour, int accent) { return maker().beam(rules, colour, accent); }
    public static BossSkill<Mob> mindControl(MindControlRules rules, int colour, int accent) { return maker().mindControl(rules, colour, accent); }
    public static BossSkill<Mob> melee(MeleeRules rules) { return maker().melee(rules); }
    public static BossSkill<Mob> phaseDash(PhaseDashRules rules, int colour, int accent) { return maker().phaseDash(rules, colour, accent); }
    public static BossSkill<Mob> spikeField(SpikeFieldRules rules, int colour, int accent) { return maker().spikeField(rules, colour, accent); }
    public static BossSkill<Mob> dive(DiveRules rules, int colour, int accent) { return maker().dive(rules, colour, accent); }
    /** @param count how many beams the boss fires each time, asked as it begins (one to the rules' most) */
    public static BossSkill<Mob> beamVolley(BeamVolleyRules rules, ToIntFunction<Mob> count, int colour, int accent) {
        return maker().beamVolley(rules, count, colour, accent);
    }

    private BossSkillApi() {}
}
