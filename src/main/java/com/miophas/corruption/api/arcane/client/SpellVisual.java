package com.miophas.corruption.api.arcane.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

/**
 * How one kind of spell effect is drawn: a spell's "visual" action names it, the server sets it down on the ground
 * under the spell's origin for as long as the spell says, and each frame the client draws it with this. The pose
 * stands at the effect's foot, on the ground; up is up. Register one by its id with {@link SpellVisuals#register},
 * in client setup.
 */
@FunctionalInterface
public interface SpellVisual {
    void draw(Shown shown, PoseStack pose, MultiBufferSource buffers);

    /**
     * One effect as it stands at the frame being drawn.
     *
     * @param age      ticks since it appeared, the part-tick of this frame included
     * @param lifetime ticks it lasts in all
     * @param radius   how far round it reaches, in blocks
     * @param height   how high it reaches, in blocks
     * @param colour   its colour, 0xRRGGBB
     * @param accent   the colour of its brightest parts, 0xRRGGBB
     * @param yaw      the way its caster faced, in degrees, for an effect that turns with them
     * @param seed     the same for this effect every frame, different from the next one's, for its own variety
     */
    record Shown(float age, int lifetime, float radius, float height, int colour, int accent, float yaw, long seed) {
        /** How far through its life it is, 0 to 1. */
        public float progress() { return Mth.clamp(age / Math.max(1, lifetime), 0, 1); }

        /**
         * How strongly it shows, 0 to 1: rising over its first few ticks and falling over its last quarter, so
         * nothing appears or vanishes in a single frame.
         */
        public float strength() {
            float rise = Math.min(FADE_IN, lifetime * .2F);
            float in = rise <= 0 ? 1 : Mth.clamp(age / rise, 0, 1);
            float out = Mth.clamp((lifetime - age) / (lifetime * .25F), 0, 1);
            return Math.min(in, out);
        }

        /** How many ticks an effect takes to come up to full strength, at most. */
        public static final float FADE_IN = 6;
    }
}
