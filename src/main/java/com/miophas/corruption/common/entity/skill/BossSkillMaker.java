package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
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

/** The mod's own boss skills, fitted to BossSkillApi as the game starts, so that an add-on's bosses can carry them. */
public final class BossSkillMaker implements BossSkillApi.Maker {
    /** Fits these as what carries the api's skills out. */
    public static void install() { BossSkillApi.install(new BossSkillMaker()); }

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
}
