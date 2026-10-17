package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.Belial;
import com.miophas.corruption.core.demon.BelialRules;
import com.miophas.corruption.core.demon.BelialRules.Skill;
import net.minecraft.util.Mth;

/**
 * One Belial's wings, as they stand: how far through their beat they are, wound on frame by frame at the pace his pose
 * asks for, so that a change of pace never jumps them. Slow and deep as he hangs in the air; held still and spread to pour
 * his beams; quick as he looses a volley or calls his likenesses; a snap as he blinks; hardly at all stunned or dying.
 */
final class BelialWinding {
    /** The most ticks one frame winds on: a Belial long out of sight takes up where he was. */
    private static final float MOST = 4;
    /** How fast his wings beat as he hangs in the air, in radians a tick. */
    static final float HANG = .16F;
    private float time = Float.NaN;
    /** How far through their beat his wings are, in radians. */
    float flap;

    /** How fast his wings beat as his pose asks, in radians a tick. */
    static float flapRate(Belial belial, Skill skill, float ticks) {
        if (belial.deathTime > 0) return .03F;
        if (belial.stunned() > 0) return .04F;
        if (belial.changing() > 0) return .3F;
        return switch (skill) {
            case BEAMS -> ticks < BelialRules.BEAMS.beam().timing().ticks() ? .05F : HANG;
            case VOLLEY -> ticks < BelialRules.volley(belial.form()).timing().ticks() ? .26F : HANG;
            case BLINK -> ticks < BelialRules.BLINK.timing().ticks() ? .7F : HANG;
            case CLONES -> ticks < BelialRules.CLONES.ticks() ? .5F : HANG;
            default -> HANG;
        };
    }

    /** Winds them on to this moment of this Belial. */
    void wind(Belial belial, float partial) {
        float now = belial.tickCount + partial;
        if (Float.isNaN(time)) {
            time = now;
            flap = belial.getId() * 1.7F % Mth.TWO_PI;
            return;
        }
        float step = Mth.clamp(now - time, 0, MOST);
        time = now;
        if (step > 0) flap = (flap + step * flapRate(belial, belial.posedSkill(), belial.skillTicks(partial))) % Mth.TWO_PI;
    }
}
