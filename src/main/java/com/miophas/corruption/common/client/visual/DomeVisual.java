package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

/**
 * A dome of hexagons over the ground, as a ward or a force field is drawn: it springs up, past its size and back,
 * shimmers as waves of light run up it, glows brightest at its rim where it meets the ground, and flashes at its
 * crown as it closes.
 */
public final class DomeVisual implements SpellVisual {
    /** Bands up it and pieces round it. */
    private static final int BANDS = 10, PIECES = 32;
    /** Ticks it takes to spring up. */
    private static final float RAISING = 14;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age();
        float radius = shown.radius() * VisualDraw.easeOutElastic(age / RAISING);
        if (radius <= 0 || strength <= 0) return;
        PoseStack.Pose at = pose.last();
        float rise = age * .015F;

        VertexConsumer shell = VisualDraw.light(buffers, VisualDraw.HEXAGONS);
        for (int band = 0; band < BANDS; band++) {
            float p0 = Mth.HALF_PI * band / BANDS, p1 = Mth.HALF_PI * (band + 1) / BANDS;
            float ring0 = Mth.cos(p0) * radius, ring1 = Mth.cos(p1) * radius, y0 = Mth.sin(p0) * radius, y1 = Mth.sin(p1) * radius;
            // A wave of light climbing it; the rim always a little brighter.
            int c0 = VisualDraw.glow(shade(shown, band), strength * light(band, age));
            int c1 = VisualDraw.glow(shade(shown, band + 1), strength * light(band + 1, age));
            float v0 = (float) band / BANDS * 3 - rise, v1 = (float) (band + 1) / BANDS * 3 - rise;
            for (int piece = 0; piece < PIECES; piece++) {
                float a0 = Mth.TWO_PI * piece / PIECES, a1 = Mth.TWO_PI * (piece + 1) / PIECES;
                float u0 = 8F * piece / PIECES, u1 = 8F * (piece + 1) / PIECES;
                VisualDraw.quad(shell, at,
                        Mth.cos(a0) * ring0, y0, Mth.sin(a0) * ring0, u0, v0, c0,
                        Mth.cos(a0) * ring1, y1, Mth.sin(a0) * ring1, u0, v1, c1,
                        Mth.cos(a1) * ring1, y1, Mth.sin(a1) * ring1, u1, v1, c1,
                        Mth.cos(a1) * ring0, y0, Mth.sin(a1) * ring0, u1, v0, c0);
            }
        }

        VertexConsumer rim = VisualDraw.light(buffers, VisualDraw.BAND);
        VisualDraw.ring(rim, at, radius * .9F, radius * 1.08F, VisualDraw.LIFT, 6, age * .03F, 0, VisualDraw.glow(shown.accent(), strength));
        float closing = 1 - shown.strength();
        if (shown.progress() > .75F) {
            VertexConsumer crown = VisualDraw.light(buffers, VisualDraw.GLOW);
            VisualDraw.disc(crown, at, radius * .6F * (1 + closing), radius, 0, VisualDraw.glow(shown.accent(), closing));
        }
    }

    /** The rim the accent, the crown the colour. */
    private static int shade(Shown shown, int band) { return VisualDraw.mix(shown.accent(), shown.colour(), Math.min(1, band / 3F)); }

    /** How bright a band is: a wave running up it every second or so over a steady glow. */
    private static float light(int band, float age) {
        float wave = Mth.sin(band * .9F - age * .35F);
        return .35F + .25F * Math.max(0, wave) + (band == 0 ? .3F : 0);
    }
}
