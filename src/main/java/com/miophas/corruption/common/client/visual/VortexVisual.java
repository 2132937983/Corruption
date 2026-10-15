package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

/**
 * A vortex, as a portal or a maelstrom is drawn: a disc of spiral arms whirling on the ground, a smaller one
 * whirling the other way inside it, and ribbons of light corkscrewing up out of it, widening as they climb into a
 * funnel as tall as the effect, all of it spinning faster as the spell goes on.
 */
public final class VortexVisual implements SpellVisual {
    /** How many ribbons climb out of it, and how many pieces each is drawn in. */
    private static final int RIBBONS = 3, PIECES = 32;
    /** How many times a ribbon winds round on its way up. */
    private static final float TURNS = 1.75F;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age();
        float radius = shown.radius() * VisualDraw.easeOutBack(age / 8);
        if (radius <= 0 || strength <= 0) return;
        PoseStack.Pose at = pose.last();
        // It spins up: the angle grows faster than the time.
        float spin = age * 6 + age * age * .04F;

        VertexConsumer disc = VisualDraw.light(buffers, VisualDraw.SPIRAL);
        VisualDraw.disc(disc, at, radius, VisualDraw.LIFT, -spin, VisualDraw.glow(shown.colour(), strength));
        VisualDraw.disc(disc, at, radius * .5F, VisualDraw.LIFT * 2, spin * 1.6F, VisualDraw.glow(shown.accent(), strength * .8F));

        VertexConsumer ribbons = VisualDraw.light(buffers, VisualDraw.BAND);
        float height = Math.max(1, shown.height()) * VisualDraw.easeOutCubic(age / 12);
        for (int ribbon = 0; ribbon < RIBBONS; ribbon++) {
            float start = Mth.TWO_PI * ribbon / RIBBONS + (float) Math.toRadians(spin);
            for (int piece = 0; piece < PIECES; piece++) {
                float t0 = (float) piece / PIECES, t1 = (float) (piece + 1) / PIECES;
                float a0 = start + t0 * TURNS * Mth.TWO_PI, a1 = start + t1 * TURNS * Mth.TWO_PI;
                float r0 = radius * (.25F + .75F * t0), r1 = radius * (.25F + .75F * t1);
                float y0 = height * t0, y1 = height * t1, w0 = .15F + .45F * t0, w1 = .15F + .45F * t1;
                int c0 = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), t0), strength * (1 - t0 * .7F));
                int c1 = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), t1), strength * (1 - t1 * .7F));
                float x0 = Mth.cos(a0) * r0, z0 = Mth.sin(a0) * r0, x1 = Mth.cos(a1) * r1, z1 = Mth.sin(a1) * r1;
                VisualDraw.quad(ribbons, at,
                        x0, y0 - w0, z0, t0 * 4, 0, c0,
                        x0, y0 + w0, z0, t0 * 4, 1, c0,
                        x1, y1 + w1, z1, t1 * 4, 1, c1,
                        x1, y1 - w1, z1, t1 * 4, 0, c1);
            }
        }
    }
}
