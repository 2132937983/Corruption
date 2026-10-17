package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.CorruptionElite;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * An elite's model posed from what its elite does, the same way for every elite: each of its skills' poses eased in and
 * out by a blend of its own (by the skill's place in its rules' Skill), the ticks since its last basic blow, and a state
 * of its own (in the water, faded, climbing) where it has one; then {@link #pose}, which the tests call directly. Its
 * helpers move the parts most elites have: legs that step, wings that beat, chains that wave.
 */
public abstract class PosedEliteModel<T extends CorruptionElite<T>> extends EliteModel<T> {
    /** The blend key its state takes (its skills take theirs from 1). */
    private static final int STATE = 15;
    private final int skills;

    /** A model of this geometry for an elite with this many skills. */
    protected PosedEliteModel(ModelPart root, String geometry, int skills) {
        super(root, geometry);
        this.skills = skills;
    }

    @Override public void setupAnim(T elite, float swing, float amount, float age, float yaw, float pitch) {
        posing(elite, age);
        float partial = age - elite.tickCount;
        float[] on = new float[skills + 1];
        for (int skill = 1; skill <= skills; skill++) on[skill] = blend(skill, elite.skill() == skill, .3F);
        float state = blend(STATE, state(elite), .12F);
        pose(swing, amount, age, yaw, pitch, elite.posedSkill(), elite.skillTicks(partial), on, elite.biteTicks(partial), state);
    }

    /** Whether its elite is in its state of its own (eased in and out as a blend); in none, unless it says. */
    protected boolean state(T elite) { return false; }

    /**
     * Its pose: walking or flying ({@code swing} and {@code amount}, as the game counts its steps), looking, at its age in
     * ticks; the skill it last began and the ticks since; how far it is into each skill's pose ({@code on}, by the skill's
     * place in its rules' Skill, 0 to 1); the ticks since its last basic blow; and how far it is into its state, 0 to 1.
     */
    abstract void pose(float swing, float amount, float age, float yaw, float pitch, int posed, float since, float[] on, float bite, float state);

    /** How far into its basic blow it is, 0 to 1 and back over ten ticks. */
    static float strike(float bite) { return bite >= 0 && bite < 10 ? Mth.sin(bite / 10 * Mth.PI) : 0; }

    /** Two legs (or arms) walking: swung forward and back by turns, about X. */
    static void walk(ModelPart left, ModelPart right, float swing, float amount, float reach) {
        float step = Mth.cos(swing * .6662F) * reach * Math.min(1, amount);
        left.xRot += step;
        right.xRot -= step;
    }

    /** Four legs walking: the fore-left with the hind-right, the fore-right with the hind-left. */
    static void trot(ModelPart foreLeft, ModelPart foreRight, ModelPart hindLeft, ModelPart hindRight, float swing, float amount, float reach) {
        walk(foreLeft, foreRight, swing, amount, reach);
        walk(hindRight, hindLeft, swing, amount, reach);
    }

    /** Two wings beating, about Z, the left up as the right goes up. */
    static void beat(ModelPart left, ModelPart right, float phase, float amplitude) {
        float up = Mth.sin(phase) * amplitude;
        left.zRot += up;
        right.zRot -= up;
    }

    /** A chain swaying, a wave running down it from its root: about Y ({@code sideways}) or about X, each piece after the last. */
    static void wave(ModelPart[] chain, float phase, float amplitude, float lag, boolean sideways) {
        for (int piece = 0; piece < chain.length; piece++) {
            float turn = Mth.sin(phase - piece * lag) * amplitude * (1 + piece * .3F);
            if (sideways) chain[piece].yRot += turn;
            else chain[piece].xRot += turn;
        }
    }

    /** A swell up and back down through a skill's wind-up and a little after, from what it began: 0 outside it. */
    static float gathering(float since, int windup) { return windup <= 0 ? 0 : ease(since / windup); }
}
