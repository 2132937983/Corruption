package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.Seere;
import com.miophas.corruption.core.demon.SeereRules;
import com.miophas.corruption.core.demon.SeereRules.Skill;
import net.minecraft.util.Mth;

/**
 * One Seere's wings, as they stand: how far through their beat they are, wound on frame by frame at the pace his pose
 * asks for; and how much he flies, eased towards the air or the ground frame by frame as he takes off and lands. Posed
 * from the time alone, a change of pace or of the way he goes would jump them.
 */
final class SeereWinding {
    /** The most ticks one frame winds on: a Seere long out of sight takes up where he was. */
    private static final float MOST = 4;
    /** How much of the way to the air (or the ground) he goes a tick. */
    private static final float EASE = .18F;
    private float time = Float.NaN;
    /** How far through their beat his wings are, in radians; how much he flies, 0 to 1. */
    float flap, flight;

    /** Winds them on to this moment of this Seere. */
    void wind(Seere seere, float partial) {
        float now = seere.tickCount + partial, wanted = flies(seere) ? 1 : 0;
        if (Float.isNaN(time)) {
            time = now;
            flight = wanted;
            flap = seere.getId() * 1.3F % Mth.TWO_PI;
            return;
        }
        float step = Mth.clamp(now - time, 0, MOST);
        time = now;
        if (step <= 0) return;
        flight += (wanted - flight) * (1 - (float) Math.pow(1 - EASE, step));
        boolean moving = seere.getDeltaMovement().horizontalDistanceSqr() > .04;
        flap = (flap + step * SeereModel.flapRate(seere.posedSkill(), seere.skillTicks(partial), flight, moving)) % Mth.TWO_PI;
    }

    /** Whether he is in the air, or leaving the ground for it. */
    static boolean flies(Seere seere) {
        if (seere.deathTime > 0) return false;
        Skill shift = seere.skill();
        return seere.mode() == SeereRules.Mode.SKY && shift != Skill.LAND || shift == Skill.TAKE_OFF;
    }
}
