package com.miophas.corruption.api.demon.skill;

import com.miophas.corruption.core.demon.skill.BarrageRules;
import com.miophas.corruption.core.demon.skill.BeamRules;
import com.miophas.corruption.core.demon.skill.BeamVolleyRules;
import com.miophas.corruption.core.demon.skill.BlightRules;
import com.miophas.corruption.core.demon.skill.BlinkRules;
import com.miophas.corruption.core.demon.skill.ChargeRules;
import com.miophas.corruption.core.demon.skill.ChariotRules;
import com.miophas.corruption.core.demon.skill.CrescendoRules;
import com.miophas.corruption.core.demon.skill.DiveChainRules;
import com.miophas.corruption.core.demon.skill.DiveRules;
import com.miophas.corruption.core.demon.skill.FanfareRules;
import com.miophas.corruption.core.demon.skill.GazeRules;
import com.miophas.corruption.core.demon.skill.GrabRules;
import com.miophas.corruption.core.demon.skill.MeleeRules;
import com.miophas.corruption.core.demon.skill.MindControlRules;
import com.miophas.corruption.core.demon.skill.PhaseDashRules;
import com.miophas.corruption.core.demon.skill.PounceRules;
import com.miophas.corruption.core.demon.skill.PsychicRules;
import com.miophas.corruption.core.demon.skill.PsychicStreamRules;
import com.miophas.corruption.core.demon.skill.ScreechRules;
import com.miophas.corruption.core.demon.skill.SnareRules;
import com.miophas.corruption.core.demon.skill.SpikeFieldRules;
import com.miophas.corruption.core.demon.skill.SpinRules;
import com.miophas.corruption.core.demon.skill.SpinVolleyRules;
import com.miophas.corruption.core.demon.skill.SpitRules;
import com.miophas.corruption.core.demon.skill.StrikeRules;
import com.miophas.corruption.core.demon.skill.SummonRules;
import com.miophas.corruption.core.demon.skill.UpheavalRules;
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
 *   <li>{@link #summon}: a band of the corruption's remnants, in the likenesses asked for, round the boss or round its quarry.</li>
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
 *   <li>{@link #spinVolley}: a star's volley: spinning, bolts of the corruption from each of its points at the quarry.</li>
 *   <li>{@link #spin}: a spinning charge: a wheel of blades flying at the quarry, striking all about it again and again.</li>
 *   <li>{@link #screech}: a scream that stuns everything near (ModEffects.STUNNED): it can neither move nor strike.</li>
 *   <li>{@link #barrage}: a rain of the corruption's bolts thrown up and over onto where the quarry stood, marked first.</li>
 *   <li>{@link #diveChain}: dives one after another, each on where the quarry stands as it begins; over at the last strike.</li>
 *   <li>{@link #dive} with {@code upAtOnce}: a dive that is over as soon as it strikes, the boss away again at once.</li>
 *   <li>{@link #chariot}: a chariot of fire driven through the quarry along a line marked first, breaking softer blocks,
 *       striking and burning what it meets.</li>
 *   <li>{@link #psychic}: a strike at the mind of the quarry and those near it, quick to come round: it hurts, and the
 *       sight swims and darkens.</li>
 *   <li>{@link #pounce}: a crouch, then a leap along a low arc at where the quarry stands, striking the first thing it meets.</li>
 *   <li>{@link #spit}: the corruption's bolts from the mouth, straight or swept across the quarry, or lobbed onto it: bolts,
 *       feathers, globs of acid that leave a cursed pool, bolts of water that throw back what they strike (from wherever a
 *       {@link SpitCaster} says, else from before the eyes).</li>
 *   <li>{@link #grab}: a tentacle lashed out at the quarry, which takes hold and drags it in, and down under the water.</li>
 *   <li>{@link #screech} with slowness in its rules: a scream that stuns and then leaves its hearers slowed.</li>
 *   <li>{@link #psychicStream}: a thread drawn out of the quarry's mind and pulled at again and again, hurting it in the mind
 *       (armour does not turn it aside) and healing the caster.</li>
 *   <li>{@link #blight}: the ground about the caster struck, what is near thrown off, and the ground turned as the blight
 *       runs out over it: to the dormant corruption, or taken by the infection.</li>
 *   <li>{@link #snare}: roots bursting up under where the quarry stood, marked first; they hurt and hold it fast.</li>
 *   <li>{@link #upheaval}: the ground under the quarry, marked first, heaved up into a pillar, broken open into a pit, or
 *       speared, flinging it up.</li>
 *   <li>{@link #fanfare}: a great many of the corruption's bolts out of a horn, sprayed back and forth across the quarry
 *       (from wherever a {@link SpitCaster} says).</li>
 *   <li>{@link #crescendo}: rings of music swelling out from the caster one after another, each hurting and throwing off
 *       what it passes.</li>
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
        BossSkill<Mob> spinVolley(SpinVolleyRules rules, int colour, int accent);
        BossSkill<Mob> spin(SpinRules rules);
        BossSkill<Mob> screech(ScreechRules rules, int colour, int accent);
        BossSkill<Mob> barrage(BarrageRules rules, int colour, int accent);
        BossSkill<Mob> diveChain(DiveChainRules rules, int colour, int accent);
        BossSkill<Mob> dive(DiveRules rules, int colour, int accent, boolean upAtOnce);
        BossSkill<Mob> chariot(ChariotRules rules, int colour, int accent);
        BossSkill<Mob> psychic(PsychicRules rules, int colour, int accent);
        BossSkill<Mob> pounce(PounceRules rules, int colour, int accent);
        BossSkill<Mob> spit(SpitRules rules, int colour, int accent);
        BossSkill<Mob> grab(GrabRules rules, int colour, int accent);
        BossSkill<Mob> psychicStream(PsychicStreamRules rules, int colour, int accent);
        BossSkill<Mob> blight(BlightRules rules, int colour, int accent);
        BossSkill<Mob> snare(SnareRules rules, int colour, int accent);
        BossSkill<Mob> upheaval(UpheavalRules rules, int colour, int accent);
        BossSkill<Mob> fanfare(FanfareRules rules, int colour, int accent);
        BossSkill<Mob> crescendo(CrescendoRules rules, int colour, int accent);
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

    public static BossSkill<Mob> spinVolley(SpinVolleyRules rules, int colour, int accent) { return maker().spinVolley(rules, colour, accent); }
    public static BossSkill<Mob> spin(SpinRules rules) { return maker().spin(rules); }
    public static BossSkill<Mob> screech(ScreechRules rules, int colour, int accent) { return maker().screech(rules, colour, accent); }
    public static BossSkill<Mob> barrage(BarrageRules rules, int colour, int accent) { return maker().barrage(rules, colour, accent); }
    public static BossSkill<Mob> diveChain(DiveChainRules rules, int colour, int accent) { return maker().diveChain(rules, colour, accent); }
    /** @param upAtOnce whether it is over as soon as it strikes (no rest after it), rather than running out its time */
    public static BossSkill<Mob> dive(DiveRules rules, int colour, int accent, boolean upAtOnce) { return maker().dive(rules, colour, accent, upAtOnce); }
    public static BossSkill<Mob> chariot(ChariotRules rules, int colour, int accent) { return maker().chariot(rules, colour, accent); }
    public static BossSkill<Mob> psychic(PsychicRules rules, int colour, int accent) { return maker().psychic(rules, colour, accent); }
    public static BossSkill<Mob> pounce(PounceRules rules, int colour, int accent) { return maker().pounce(rules, colour, accent); }
    public static BossSkill<Mob> spit(SpitRules rules, int colour, int accent) { return maker().spit(rules, colour, accent); }
    public static BossSkill<Mob> grab(GrabRules rules, int colour, int accent) { return maker().grab(rules, colour, accent); }
    public static BossSkill<Mob> psychicStream(PsychicStreamRules rules, int colour, int accent) { return maker().psychicStream(rules, colour, accent); }
    public static BossSkill<Mob> blight(BlightRules rules, int colour, int accent) { return maker().blight(rules, colour, accent); }
    public static BossSkill<Mob> snare(SnareRules rules, int colour, int accent) { return maker().snare(rules, colour, accent); }
    public static BossSkill<Mob> upheaval(UpheavalRules rules, int colour, int accent) { return maker().upheaval(rules, colour, accent); }
    public static BossSkill<Mob> fanfare(FanfareRules rules, int colour, int accent) { return maker().fanfare(rules, colour, accent); }
    public static BossSkill<Mob> crescendo(CrescendoRules rules, int colour, int accent) { return maker().crescendo(rules, colour, accent); }

    private BossSkillApi() {}
}
