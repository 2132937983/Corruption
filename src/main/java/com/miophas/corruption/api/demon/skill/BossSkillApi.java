package com.miophas.corruption.api.demon.skill;

import com.miophas.corruption.core.demon.skill.BarrageRules;
import com.miophas.corruption.core.demon.skill.BeamRules;
import com.miophas.corruption.core.demon.skill.BeamVolleyRules;
import com.miophas.corruption.core.demon.skill.BlightRules;
import com.miophas.corruption.core.demon.skill.BlinkRules;
import com.miophas.corruption.core.demon.skill.BurrowRules;
import com.miophas.corruption.core.demon.skill.ChargeRules;
import com.miophas.corruption.core.demon.skill.ChariotRules;
import com.miophas.corruption.core.demon.skill.CountedSummonRules;
import com.miophas.corruption.core.demon.skill.CrescendoRules;
import com.miophas.corruption.core.demon.skill.DiveChainRules;
import com.miophas.corruption.core.demon.skill.DiveRules;
import com.miophas.corruption.core.demon.skill.DrakeRushRules;
import com.miophas.corruption.core.demon.skill.EliteCallRules;
import com.miophas.corruption.core.demon.skill.FanfareRules;
import com.miophas.corruption.core.demon.skill.FireballRules;
import com.miophas.corruption.core.demon.skill.FlyingStrikeRules;
import com.miophas.corruption.core.demon.skill.GazeRules;
import com.miophas.corruption.core.demon.skill.GrabRules;
import com.miophas.corruption.core.demon.skill.HandRules;
import com.miophas.corruption.core.demon.skill.MeleeRules;
import com.miophas.corruption.core.demon.skill.MindControlRules;
import com.miophas.corruption.core.demon.skill.PhaseDashRules;
import com.miophas.corruption.core.demon.skill.PounceRules;
import com.miophas.corruption.core.demon.skill.PsychicRules;
import com.miophas.corruption.core.demon.skill.PsychicStreamRules;
import com.miophas.corruption.core.demon.skill.RamRules;
import com.miophas.corruption.core.demon.skill.ScreechRules;
import com.miophas.corruption.core.demon.skill.SkyBeamRules;
import com.miophas.corruption.core.demon.skill.SnareRules;
import com.miophas.corruption.core.demon.skill.SpikeFieldRules;
import com.miophas.corruption.core.demon.skill.SpinRules;
import com.miophas.corruption.core.demon.skill.SpinVolleyRules;
import com.miophas.corruption.core.demon.skill.SpitRules;
import com.miophas.corruption.core.demon.skill.StrikeRules;
import com.miophas.corruption.core.demon.skill.SummonRules;
import com.miophas.corruption.core.demon.skill.UpheavalRules;
import com.miophas.corruption.core.demon.skill.VenusRules;
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
 *   <li>{@link #skyBeams}: magic circles opening in the sky over the quarry, every beam's strike marked under its circle;
 *       then beams driven straight down out of them, lightning coiling down them: one circle over the mark, or a pentagram,
 *       a hexagram or an octagram of circles round it, the star turning and closing in on the mark as they pour.</li>
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
        BossSkill<Mob> skyBeams(SkyBeamRules rules, int colour, int accent);
    }

    public static final BossSkillRegistry.Type<BlightRules> TARGET_BLIGHT = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:target_blight"), BlightRules.class);
    public static final BossSkillRegistry.Type<ScreechRules> TARGET_SCREECH = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:target_screech"), ScreechRules.class);
    public static final BossSkillRegistry.Type<CountedSummonRules> COUNTED_SUMMON = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:counted_summon"), CountedSummonRules.class);
    public static final BossSkillRegistry.Type<FlyingStrikeRules> SWOOP = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:swoop"), FlyingStrikeRules.class);
    public static final BossSkillRegistry.Type<DrakeRushRules> DRAKE_RUSH = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:drake_rush"), DrakeRushRules.class);
    public static final BossSkillRegistry.Type<EliteCallRules> ELITE_CALL = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:elite_call"), EliteCallRules.class);
    public static final BossSkillRegistry.Type<HandRules> HANDS = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:hands"), HandRules.class);
    public static final BossSkillRegistry.Type<FireballRules> FIREBALL = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:fireball"), FireballRules.class);
    public static final BossSkillRegistry.Type<GazeRules> GAZE = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:gaze"), GazeRules.class);
    public static final BossSkillRegistry.Type<ChargeRules> CHARGE = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:charge"), ChargeRules.class);
    public static final BossSkillRegistry.Type<SummonRules> SUMMON = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:summon"), SummonRules.class);
    public static final BossSkillRegistry.Type<BlinkRules> BLINK = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:blink"), BlinkRules.class);
    public static final BossSkillRegistry.Type<StrikeRules> STRIKE = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:strike"), StrikeRules.class);
    public static final BossSkillRegistry.Type<BeamRules> BEAM = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:beam"), BeamRules.class);
    public static final BossSkillRegistry.Type<MindControlRules> MIND_CONTROL = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:mind_control"), MindControlRules.class);
    public static final BossSkillRegistry.Type<MeleeRules> MELEE = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:melee"), MeleeRules.class);
    public static final BossSkillRegistry.Type<PhaseDashRules> PHASE_DASH = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:phase_dash"), PhaseDashRules.class);
    public static final BossSkillRegistry.Type<SpikeFieldRules> SPIKE_FIELD = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:spike_field"), SpikeFieldRules.class);
    public static final BossSkillRegistry.Type<DiveRules> DIVE = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:dive"), DiveRules.class);
    public static final BossSkillRegistry.Type<BeamVolleyRules> BEAM_VOLLEY = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:beam_volley"), BeamVolleyRules.class);
    public static final BossSkillRegistry.Type<SpinVolleyRules> SPIN_VOLLEY = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:spin_volley"), SpinVolleyRules.class);
    public static final BossSkillRegistry.Type<SpinRules> SPIN = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:spin"), SpinRules.class);
    public static final BossSkillRegistry.Type<ScreechRules> SCREECH = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:screech"), ScreechRules.class);
    public static final BossSkillRegistry.Type<BarrageRules> BARRAGE = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:barrage"), BarrageRules.class);
    public static final BossSkillRegistry.Type<DiveChainRules> DIVE_CHAIN = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:dive_chain"), DiveChainRules.class);
    public static final BossSkillRegistry.Type<ChariotRules> CHARIOT = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:chariot"), ChariotRules.class);
    public static final BossSkillRegistry.Type<PsychicRules> PSYCHIC = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:psychic"), PsychicRules.class);
    public static final BossSkillRegistry.Type<PounceRules> POUNCE = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:pounce"), PounceRules.class);
    public static final BossSkillRegistry.Type<SpitRules> SPIT = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:spit"), SpitRules.class);
    public static final BossSkillRegistry.Type<GrabRules> GRAB = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:grab"), GrabRules.class);
    public static final BossSkillRegistry.Type<PsychicStreamRules> PSYCHIC_STREAM = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:psychic_stream"), PsychicStreamRules.class);
    public static final BossSkillRegistry.Type<BlightRules> BLIGHT = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:blight"), BlightRules.class);
    public static final BossSkillRegistry.Type<SnareRules> SNARE = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:snare"), SnareRules.class);
    public static final BossSkillRegistry.Type<UpheavalRules> UPHEAVAL = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:upheaval"), UpheavalRules.class);
    public static final BossSkillRegistry.Type<FanfareRules> FANFARE = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:fanfare"), FanfareRules.class);
    public static final BossSkillRegistry.Type<CrescendoRules> CRESCENDO = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:crescendo"), CrescendoRules.class);
    public static final BossSkillRegistry.Type<SkyBeamRules> SKY_BEAMS = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:sky_beams"), SkyBeamRules.class);
    public static final BossSkillRegistry.Type<BurrowRules> BURROW = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:burrow"), BurrowRules.class);
    public static final BossSkillRegistry.Type<RamRules> RAM = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:ram"), RamRules.class);
    public static final BossSkillRegistry.Type<VenusRules> VENUS = new BossSkillRegistry.Type<>(net.minecraft.resources.ResourceLocation.parse("corruption:venus"), VenusRules.class);

    /** Legacy whole-provider replacement. Prefer registering a single factory in BossSkillRegistry. */
    @Deprecated
    public static void install(Maker made) {
        java.util.Objects.requireNonNull(made);
        BossSkillRegistry.replace(GAZE, (rules, options) -> made.gaze(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(CHARGE, (rules, options) -> made.charge(rules));
        BossSkillRegistry.replace(SUMMON, (rules, options) -> made.summon(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(BLINK, (rules, options) -> made.blink(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(STRIKE, (rules, options) -> made.strike(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(BEAM, (rules, options) -> made.beam(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(MIND_CONTROL, (rules, options) -> made.mindControl(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(MELEE, (rules, options) -> made.melee(rules));
        BossSkillRegistry.replace(PHASE_DASH, (rules, options) -> made.phaseDash(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(SPIKE_FIELD, (rules, options) -> made.spikeField(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(BEAM_VOLLEY, (rules, options) -> made.beamVolley(rules, options.count(), options.colour(), options.accent()));
        BossSkillRegistry.replace(SPIN_VOLLEY, (rules, options) -> made.spinVolley(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(SPIN, (rules, options) -> made.spin(rules));
        BossSkillRegistry.replace(SCREECH, (rules, options) -> made.screech(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(BARRAGE, (rules, options) -> made.barrage(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(DIVE_CHAIN, (rules, options) -> made.diveChain(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(DIVE, (rules, options) -> made.dive(rules, options.colour(), options.accent(), options.upAtOnce()));
        BossSkillRegistry.replace(CHARIOT, (rules, options) -> made.chariot(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(PSYCHIC, (rules, options) -> made.psychic(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(POUNCE, (rules, options) -> made.pounce(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(SPIT, (rules, options) -> made.spit(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(GRAB, (rules, options) -> made.grab(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(PSYCHIC_STREAM, (rules, options) -> made.psychicStream(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(BLIGHT, (rules, options) -> made.blight(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(SNARE, (rules, options) -> made.snare(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(UPHEAVAL, (rules, options) -> made.upheaval(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(FANFARE, (rules, options) -> made.fanfare(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(CRESCENDO, (rules, options) -> made.crescendo(rules, options.colour(), options.accent()));
        BossSkillRegistry.replace(SKY_BEAMS, (rules, options) -> made.skyBeams(rules, options.colour(), options.accent()));
    }

    public static BossSkill<Mob> gaze(GazeRules rules, int colour, int accent) { return BossSkillRegistry.create(GAZE, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> charge(ChargeRules rules) { return BossSkillRegistry.create(CHARGE, rules, new BossSkillRegistry.Options(0, 0)); }
    public static BossSkill<Mob> summon(SummonRules rules, int colour, int accent) { return BossSkillRegistry.create(SUMMON, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> blink(BlinkRules rules, int colour, int accent) { return BossSkillRegistry.create(BLINK, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> strike(StrikeRules rules, int colour, int accent) { return BossSkillRegistry.create(STRIKE, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> beam(BeamRules rules, int colour, int accent) { return BossSkillRegistry.create(BEAM, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> mindControl(MindControlRules rules, int colour, int accent) { return BossSkillRegistry.create(MIND_CONTROL, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> melee(MeleeRules rules) { return BossSkillRegistry.create(MELEE, rules, new BossSkillRegistry.Options(0, 0)); }
    public static BossSkill<Mob> phaseDash(PhaseDashRules rules, int colour, int accent) { return BossSkillRegistry.create(PHASE_DASH, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> spikeField(SpikeFieldRules rules, int colour, int accent) { return BossSkillRegistry.create(SPIKE_FIELD, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> dive(DiveRules rules, int colour, int accent) { return BossSkillRegistry.create(DIVE, rules, new BossSkillRegistry.Options(colour, accent)); }
    /** @param count how many beams the boss fires each time, asked as it begins (one to the rules' most) */
    public static BossSkill<Mob> beamVolley(BeamVolleyRules rules, ToIntFunction<Mob> count, int colour, int accent) { return BossSkillRegistry.create(BEAM_VOLLEY, rules, new BossSkillRegistry.Options(colour, accent, count, false)); }

    public static BossSkill<Mob> spinVolley(SpinVolleyRules rules, int colour, int accent) { return BossSkillRegistry.create(SPIN_VOLLEY, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> spin(SpinRules rules) { return BossSkillRegistry.create(SPIN, rules, new BossSkillRegistry.Options(0, 0)); }
    public static BossSkill<Mob> screech(ScreechRules rules, int colour, int accent) { return BossSkillRegistry.create(SCREECH, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> barrage(BarrageRules rules, int colour, int accent) { return BossSkillRegistry.create(BARRAGE, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> diveChain(DiveChainRules rules, int colour, int accent) { return BossSkillRegistry.create(DIVE_CHAIN, rules, new BossSkillRegistry.Options(colour, accent)); }
    /** @param upAtOnce whether it is over as soon as it strikes (no rest after it), rather than running out its time */
    public static BossSkill<Mob> dive(DiveRules rules, int colour, int accent, boolean upAtOnce) { return BossSkillRegistry.create(DIVE, rules, new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, upAtOnce)); }
    public static BossSkill<Mob> chariot(ChariotRules rules, int colour, int accent) { return BossSkillRegistry.create(CHARIOT, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> psychic(PsychicRules rules, int colour, int accent) { return BossSkillRegistry.create(PSYCHIC, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> pounce(PounceRules rules, int colour, int accent) { return BossSkillRegistry.create(POUNCE, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> spit(SpitRules rules, int colour, int accent) { return BossSkillRegistry.create(SPIT, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> grab(GrabRules rules, int colour, int accent) { return BossSkillRegistry.create(GRAB, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> psychicStream(PsychicStreamRules rules, int colour, int accent) { return BossSkillRegistry.create(PSYCHIC_STREAM, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> blight(BlightRules rules, int colour, int accent) { return BossSkillRegistry.create(BLIGHT, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> snare(SnareRules rules, int colour, int accent) { return BossSkillRegistry.create(SNARE, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> upheaval(UpheavalRules rules, int colour, int accent) { return BossSkillRegistry.create(UPHEAVAL, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> fanfare(FanfareRules rules, int colour, int accent) { return BossSkillRegistry.create(FANFARE, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> crescendo(CrescendoRules rules, int colour, int accent) { return BossSkillRegistry.create(CRESCENDO, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> skyBeams(SkyBeamRules rules, int colour, int accent) { return BossSkillRegistry.create(SKY_BEAMS, rules, new BossSkillRegistry.Options(colour, accent)); }

    public static BossSkill<Mob> burrow(BurrowRules rules, int colour, int accent) { return BossSkillRegistry.create(BURROW, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> ram(RamRules rules, int colour, int accent) { return BossSkillRegistry.create(RAM, rules, new BossSkillRegistry.Options(colour, accent)); }
    public static BossSkill<Mob> venus(VenusRules rules, int colour, int accent) { return BossSkillRegistry.create(VENUS, rules, new BossSkillRegistry.Options(colour, accent)); }

    public static BossSkill<Mob> fireball(FireballRules rules, int colour, int accent) { return BossSkillRegistry.create(FIREBALL, rules, new BossSkillRegistry.Options(colour, accent)); }

    public static BossSkill<Mob> hands(HandRules rules, int colour, int accent) { return BossSkillRegistry.create(HANDS, rules, new BossSkillRegistry.Options(colour, accent)); }

    public static BossSkill<Mob> eliteCall(EliteCallRules rules, int colour, int accent) { return BossSkillRegistry.create(ELITE_CALL, rules, new BossSkillRegistry.Options(colour, accent)); }

    public static BossSkill<Mob> drakeRush(DrakeRushRules rules, int colour, int accent) { return BossSkillRegistry.create(DRAKE_RUSH, rules, new BossSkillRegistry.Options(colour, accent)); }

    public static BossSkill<Mob> swoop(FlyingStrikeRules rules, int colour, int accent) { return BossSkillRegistry.create(SWOOP, rules, new BossSkillRegistry.Options(colour, accent)); }

    public static BossSkill<Mob> blight(BlightRules rules, int colour, int accent, boolean atTarget) {
        return BossSkillRegistry.create(atTarget ? TARGET_BLIGHT : BLIGHT, rules, new BossSkillRegistry.Options(colour, accent));
    }
    public static BossSkill<Mob> screech(ScreechRules rules, int colour, int accent, boolean atTarget) {
        return BossSkillRegistry.create(atTarget ? TARGET_SCREECH : SCREECH, rules, new BossSkillRegistry.Options(colour, accent));
    }
    public static BossSkill<Mob> summon(SummonRules rules, int colour, int accent, double countRadius) {
        return BossSkillRegistry.create(COUNTED_SUMMON, new CountedSummonRules(rules, countRadius), new BossSkillRegistry.Options(colour, accent));
    }
    private BossSkillApi() {}
}
