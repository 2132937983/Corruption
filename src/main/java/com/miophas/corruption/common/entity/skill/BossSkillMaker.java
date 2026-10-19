package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
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
import com.miophas.corruption.core.demon.skill.SkyBeamRules;
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

/** The mod's own boss skills, fitted to BossSkillApi as the game starts, so that an add-on's bosses can carry them. */
public final class BossSkillMaker implements BossSkillApi.Maker {
    /** Fits these as what carries the api's skills out. */
    public static void install() {
        BossSkillRegistry.registerDefault(BossSkillApi.FIREBALL, (rules, options) -> new FireballSkill(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.HANDS, (rules, options) -> new HandSkill(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.ELITE_CALL, (rules, options) -> new EliteCallSkill(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.DRAKE_RUSH, (rules, options) -> new DrakeRushSkill(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.SWOOP, (rules, options) -> new SwoopSkill(rules.timing(), rules.range(), rules.speed()));
        BossSkillRegistry.registerDefault(BossSkillApi.TARGET_BLIGHT, (rules, options) -> new BlightSkill(rules, options.colour(), options.accent(), true));
        BossSkillRegistry.registerDefault(BossSkillApi.TARGET_SCREECH, (rules, options) -> new ScreechSkill(rules, options.colour(), options.accent(), true));
        BossSkillRegistry.registerDefault(BossSkillApi.COUNTED_SUMMON, (rules, options) -> new SummonSkill(rules.rules(), options.colour(), options.accent()).counting(rules.radius()));
        BossSkillMaker defaults = new BossSkillMaker();
        BossSkillRegistry.registerDefault(BossSkillApi.GAZE, (rules, options) -> defaults.gaze(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.CHARGE, (rules, options) -> defaults.charge(rules));
        BossSkillRegistry.registerDefault(BossSkillApi.SUMMON, (rules, options) -> defaults.summon(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.BLINK, (rules, options) -> defaults.blink(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.STRIKE, (rules, options) -> defaults.strike(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.BEAM, (rules, options) -> defaults.beam(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.MIND_CONTROL, (rules, options) -> defaults.mindControl(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.MELEE, (rules, options) -> defaults.melee(rules));
        BossSkillRegistry.registerDefault(BossSkillApi.PHASE_DASH, (rules, options) -> defaults.phaseDash(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.SPIKE_FIELD, (rules, options) -> defaults.spikeField(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.BEAM_VOLLEY, (rules, options) -> defaults.beamVolley(rules, options.count(), options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.SPIN_VOLLEY, (rules, options) -> defaults.spinVolley(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.SPIN, (rules, options) -> defaults.spin(rules));
        BossSkillRegistry.registerDefault(BossSkillApi.SCREECH, (rules, options) -> defaults.screech(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.BARRAGE, (rules, options) -> defaults.barrage(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.DIVE_CHAIN, (rules, options) -> defaults.diveChain(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.DIVE, (rules, options) -> defaults.dive(rules, options.colour(), options.accent(), options.upAtOnce()));
        BossSkillRegistry.registerDefault(BossSkillApi.CHARIOT, (rules, options) -> defaults.chariot(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.PSYCHIC, (rules, options) -> defaults.psychic(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.POUNCE, (rules, options) -> defaults.pounce(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.SPIT, (rules, options) -> defaults.spit(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.GRAB, (rules, options) -> defaults.grab(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.PSYCHIC_STREAM, (rules, options) -> defaults.psychicStream(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.BLIGHT, (rules, options) -> defaults.blight(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.SNARE, (rules, options) -> defaults.snare(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.UPHEAVAL, (rules, options) -> defaults.upheaval(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.FANFARE, (rules, options) -> defaults.fanfare(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.CRESCENDO, (rules, options) -> defaults.crescendo(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.SKY_BEAMS, (rules, options) -> defaults.skyBeams(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.BURROW, (rules, options) -> new BurrowSkill(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.RAM, (rules, options) -> new RamSkill(rules, options.colour(), options.accent()));
        BossSkillRegistry.registerDefault(BossSkillApi.VENUS, (rules, options) -> new VenusSkill(rules, options.colour(), options.accent()));
    }

    @Override public BossSkill<Mob> gaze(GazeRules rules, int colour, int accent) { return new GazeSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> charge(ChargeRules rules) { return new ChargeSkill(rules); }
    @Override public BossSkill<Mob> summon(SummonRules rules, int colour, int accent) { return new SummonSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> blink(BlinkRules rules, int colour, int accent) { return new BlinkSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> strike(StrikeRules rules, int colour, int accent) { return new StrikeSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> beam(BeamRules rules, int colour, int accent) { return new BeamSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> mindControl(MindControlRules rules, int colour, int accent) { return new MindControlSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> melee(MeleeRules rules) { return new MeleeSkill(rules); }
    @Override public BossSkill<Mob> phaseDash(PhaseDashRules rules, int colour, int accent) { return new PhaseDashSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> spikeField(SpikeFieldRules rules, int colour, int accent) { return new SpikeFieldSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> dive(DiveRules rules, int colour, int accent) { return new DiveSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> beamVolley(BeamVolleyRules rules, ToIntFunction<Mob> count, int colour, int accent) {
        return new BeamVolleySkill(rules, count, colour, accent);
    }
    @Override public BossSkill<Mob> spinVolley(SpinVolleyRules rules, int colour, int accent) { return new SpinVolleySkill(rules, colour, accent); }
    @Override public BossSkill<Mob> spin(SpinRules rules) { return new SpinSkill(rules); }
    @Override public BossSkill<Mob> screech(ScreechRules rules, int colour, int accent) { return new ScreechSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> barrage(BarrageRules rules, int colour, int accent) { return new BarrageSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> diveChain(DiveChainRules rules, int colour, int accent) { return new DiveChainSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> dive(DiveRules rules, int colour, int accent, boolean upAtOnce) { return new DiveSkill(rules, colour, accent, upAtOnce); }
    @Override public BossSkill<Mob> chariot(ChariotRules rules, int colour, int accent) { return new ChariotSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> psychic(PsychicRules rules, int colour, int accent) { return new PsychicSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> pounce(PounceRules rules, int colour, int accent) { return new PounceSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> spit(SpitRules rules, int colour, int accent) { return new SpitSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> grab(GrabRules rules, int colour, int accent) { return new GrabSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> psychicStream(PsychicStreamRules rules, int colour, int accent) { return new PsychicStreamSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> blight(BlightRules rules, int colour, int accent) { return new BlightSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> snare(SnareRules rules, int colour, int accent) { return new SnareSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> upheaval(UpheavalRules rules, int colour, int accent) { return new UpheavalSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> fanfare(FanfareRules rules, int colour, int accent) { return new FanfareSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> crescendo(CrescendoRules rules, int colour, int accent) { return new CrescendoSkill(rules, colour, accent); }
    @Override public BossSkill<Mob> skyBeams(SkyBeamRules rules, int colour, int accent) { return new SkyBeamSkill(rules, colour, accent); }
}
