package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.Decarabia;
import com.miophas.corruption.core.demon.DecarabiaRules.Skill;
import com.miophas.corruption.core.demon.skill.SpinVolleyRules;
import net.minecraft.util.Mth;

/**
 * One Decarabia's turning things, as they stand, wound on frame by frame: how far round the star's points have turned
 * and how fast they turn now, how far the ring behind them has turned the other way, and how far through their beat the
 * goshawk's wings are. Posed from the time alone, a change of pace would jump them.
 *
 * <p>Through a volley the points are not his own to turn: they must stand where its bolts leave from
 * ({@link SpinVolleyRules#turned}). So through its wind-up the star eases onto the volley's own turning, and keeps to it
 * until the volley is over. The five points are alike, so a fifth of a turn off is no way off at all.
 */
final class DecarabiaWinding {
    /** The most ticks one frame winds on: a Decarabia long out of sight takes up where he was. */
    private static final float MOST = 4;
    /** How much of the way to the pace his pose asks for the points come, a tick. */
    private static final float EASE = .15F;
    /** How fast the ring turns the other way, for the points' pace. */
    private static final float RING = .3F;
    private float time = Float.NaN;
    /** Degrees the points have turned (towards his right), and degrees a tick they turn now; degrees the ring has turned (towards his left). */
    float spin, pace, ring;
    /** How far through their beat the goshawk's wings are, in radians. */
    float flap;
    /** Through a volley: how far the points stood off its own turning as it began, and how far into it the last frame was. */
    private float volleyOff, volleyAt = Float.NaN;

    /** Winds them on to this moment of this Decarabia. */
    void wind(Decarabia decarabia, float partial) {
        float now = decarabia.tickCount + partial;
        Skill skill = decarabia.posedSkill();
        float ticks = decarabia.skillTicks(partial);
        if (Float.isNaN(time)) {
            time = now;
            spin = decarabia.getId() * 29 % 360;
            ring = decarabia.getId() * 53 % 360;
            flap = decarabia.getId() * 1.3F % Mth.TWO_PI;
            pace = DecarabiaStarModel.pace(decarabia, skill, ticks);
            return;
        }
        float step = Mth.clamp(now - time, 0, MOST);
        time = now;
        if (step <= 0) return;
        SpinVolleyRules volley = DecarabiaStarModel.volley(decarabia);
        if (skill == Skill.VOLLEY && ticks < volley.timing().ticks() && decarabia.changing() == 0) {
            float turned = (float) volley.turned(ticks), fifth = 360F / volley.points();
            if (Float.isNaN(volleyAt) || ticks < volleyAt) {
                float off = spin - turned;
                volleyOff = off - fifth * Math.round(off / fifth);
            }
            volleyAt = ticks;
            float windup = Math.max(1, volley.timing().windup());
            spin = turned + volleyOff * (1 - DecarabiaStarModel.ease(ticks / windup));
            pace = (float) volley.spin() * Math.min(1, ticks / windup);
        } else {
            volleyAt = Float.NaN;
            float wanted = DecarabiaStarModel.pace(decarabia, skill, ticks);
            pace += (wanted - pace) * (1 - (float) Math.pow(1 - EASE, step));
            spin = (spin + pace * step) % 360;
        }
        ring = (ring + RING * pace * step) % 360;
        flap = (flap + step * DecarabiaHawkModel.flapRate(skill, ticks, decarabia.deathTime > 0)) % Mth.TWO_PI;
    }
}
