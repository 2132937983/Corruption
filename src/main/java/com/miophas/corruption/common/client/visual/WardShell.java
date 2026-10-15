package com.miophas.corruption.common.client.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

/**
 * A ward drawn round a creature: a closed shell of hexagons, as tall as it is and as wide, light rippling over it
 * and up it, brightest along its middle, as the films draw a force field round someone. The same hexagons as a
 * spell's dome ({@link DomeVisual}), closed into a whole egg that turns slowly.
 */
public final class WardShell {
    private static final int BANDS = 14, PIECES = 28;

    /**
     * Draws the shell round the pose's origin: {@code radius} wide, {@code height} tall, its middle {@code middle}
     * up, at this strength (0 to 1), in this colour and accent.
     */
    public static void draw(PoseStack pose, MultiBufferSource buffers, float radius, float height, float middle, int colour, int accent, float age, float strength) {
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();
        float half = height / 2, rise = age * .02F, turn = age * .01F;
        VertexConsumer shell = VisualDraw.light(buffers, VisualDraw.HEXAGONS);
        for (int band = 0; band < BANDS; band++) {
            float p0 = -Mth.HALF_PI + Mth.PI * band / BANDS, p1 = -Mth.HALF_PI + Mth.PI * (band + 1) / BANDS;
            float ring0 = Mth.cos(p0) * radius, ring1 = Mth.cos(p1) * radius, y0 = middle + Mth.sin(p0) * half, y1 = middle + Mth.sin(p1) * half;
            int c0 = VisualDraw.glow(VisualDraw.mix(accent, colour, Math.abs(Mth.sin(p0))), strength * light(band, age));
            int c1 = VisualDraw.glow(VisualDraw.mix(accent, colour, Math.abs(Mth.sin(p1))), strength * light(band + 1, age));
            float v0 = 4F * band / BANDS - rise, v1 = 4F * (band + 1) / BANDS - rise;
            for (int piece = 0; piece < PIECES; piece++) {
                float a0 = Mth.TWO_PI * piece / PIECES + turn, a1 = Mth.TWO_PI * (piece + 1) / PIECES + turn;
                float u0 = 7F * piece / PIECES, u1 = 7F * (piece + 1) / PIECES;
                VisualDraw.quad(shell, at,
                        Mth.cos(a0) * ring0, y0, Mth.sin(a0) * ring0, u0, v0, c0,
                        Mth.cos(a0) * ring1, y1, Mth.sin(a0) * ring1, u0, v1, c1,
                        Mth.cos(a1) * ring1, y1, Mth.sin(a1) * ring1, u1, v1, c1,
                        Mth.cos(a1) * ring0, y0, Mth.sin(a1) * ring0, u1, v0, c0);
            }
        }
    }

    /** How bright a band is: a ripple running up the shell over a steady glow, brightest round its middle. */
    private static float light(int band, float age) {
        float middle = 1 - Math.abs(band - BANDS / 2F) / (BANDS / 2F);
        return .25F + .2F * middle + .25F * Math.max(0, Mth.sin(band * .8F - age * .3F));
    }

    private WardShell() {}
}
