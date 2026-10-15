package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * What the spell effects are drawn with. Every one of them is light: drawn additively ({@link RenderType#eyes}),
 * full bright, so that it glows over whatever is behind it, and dims by darkening, not by fading out (additive
 * light has no transparency). Every face is drawn from both sides, since an effect is seen from all round it.
 *
 * <p>Get a texture's buffer, draw all of that texture's faces, and only then get the next: getting another
 * texture's buffer finishes the one before it.
 */
final class VisualDraw {
    static final ResourceLocation CIRCLE_OUTER = texture("magic_circle_outer"), CIRCLE_INNER = texture("magic_circle_inner"),
            BAND = texture("band"), STREAKS = texture("streaks"), SPIRAL = texture("spiral"), HEXAGONS = texture("hexagons"),
            RUNES = texture("runes"), GLOW = texture("glow");
    /** How many sides a circle is drawn with. */
    static final int SEGMENTS = 48;
    /** How far over the ground what lies flat on it is drawn, so as not to flicker into it. */
    static final float LIFT = .03F;

    static VertexConsumer light(MultiBufferSource buffers, ResourceLocation texture) { return buffers.getBuffer(RenderType.eyes(texture)); }

    /** A colour as light of this strength: 0 is none, 1 the colour itself. */
    static int glow(int rgb, float strength) {
        float s = Mth.clamp(strength, 0, 1);
        int r = Math.round(((rgb >> 16) & 255) * s), g = Math.round(((rgb >> 8) & 255) * s), b = Math.round((rgb & 255) * s);
        return Math.round(255 * s) << 24 | r << 16 | g << 8 | b;
    }

    /** Between two colours, this far from the first. */
    static int mix(int from, int to, float t) {
        float k = Mth.clamp(t, 0, 1);
        int r = Math.round(Mth.lerp(k, (from >> 16) & 255, (to >> 16) & 255));
        int g = Math.round(Mth.lerp(k, (from >> 8) & 255, (to >> 8) & 255));
        int b = Math.round(Mth.lerp(k, from & 255, to & 255));
        return r << 16 | g << 8 | b;
    }

    static float easeOutCubic(float t) { float k = 1 - Mth.clamp(t, 0, 1); return 1 - k * k * k; }
    /** Overshooting a little before it settles, as a thing flung out does. */
    static float easeOutBack(float t) {
        float k = Mth.clamp(t, 0, 1) - 1;
        return 1 + 2.7F * k * k * k + 1.7F * k * k;
    }
    /** Springing past where it settles and back, as a ward snapping up does. */
    static float easeOutElastic(float t) {
        float k = Mth.clamp(t, 0, 1);
        if (k == 0 || k == 1) return k;
        return (float) (Math.pow(2, -10 * k) * Math.sin((k * 10 - .75) * (2 * Math.PI / 3)) + 1);
    }

    static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int argb) {
        buffer.addVertex(pose, x, y, z).setColor(argb).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
    }

    /** A four-cornered face, corners in order round it, seen from both sides. */
    static void quad(VertexConsumer buffer, PoseStack.Pose pose,
                     float x1, float y1, float z1, float u1, float v1, int c1,
                     float x2, float y2, float z2, float u2, float v2, int c2,
                     float x3, float y3, float z3, float u3, float v3, int c3,
                     float x4, float y4, float z4, float u4, float v4, int c4) {
        vertex(buffer, pose, x1, y1, z1, u1, v1, c1);
        vertex(buffer, pose, x2, y2, z2, u2, v2, c2);
        vertex(buffer, pose, x3, y3, z3, u3, v3, c3);
        vertex(buffer, pose, x4, y4, z4, u4, v4, c4);
        vertex(buffer, pose, x4, y4, z4, u4, v4, c4);
        vertex(buffer, pose, x3, y3, z3, u3, v3, c3);
        vertex(buffer, pose, x2, y2, z2, u2, v2, c2);
        vertex(buffer, pose, x1, y1, z1, u1, v1, c1);
    }

    /** A whole texture laid flat, this far round its middle, at this height, turned this many degrees. */
    static void disc(VertexConsumer buffer, PoseStack.Pose pose, float radius, float y, float degrees, int argb) {
        float a = (float) Math.toRadians(degrees), cos = Mth.cos(a) * radius, sin = Mth.sin(a) * radius;
        quad(buffer, pose,
                -cos + sin, y, -sin - cos, 0, 0, argb,
                -cos - sin, y, -sin + cos, 0, 1, argb,
                cos - sin, y, sin + cos, 1, 1, argb,
                cos + sin, y, sin - cos, 1, 0, argb);
    }

    /**
     * A flat band round the middle, from {@code inner} out to {@code outer}, the texture running round it
     * {@code repeat} times from {@code shift} and across it from the inner edge (v 0) to the outer (v 1).
     */
    static void ring(VertexConsumer buffer, PoseStack.Pose pose, float inner, float outer, float y, float repeat, float shift, int innerColour, int outerColour) {
        if (outer <= 0 || outer <= inner) return;
        for (int side = 0; side < SEGMENTS; side++) {
            float a0 = Mth.TWO_PI * side / SEGMENTS, a1 = Mth.TWO_PI * (side + 1) / SEGMENTS;
            float u0 = shift + repeat * side / SEGMENTS, u1 = shift + repeat * (side + 1) / SEGMENTS;
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0), c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            quad(buffer, pose,
                    c0 * inner, y, s0 * inner, u0, 0, innerColour,
                    c0 * outer, y, s0 * outer, u0, 1, outerColour,
                    c1 * outer, y, s1 * outer, u1, 1, outerColour,
                    c1 * inner, y, s1 * inner, u1, 0, innerColour);
        }
    }

    /**
     * An upright band round the middle, as a wall of light: from {@code bottom} up to {@code top}, the texture
     * running round it {@code repeat} times and up it from v {@code from} to v {@code to}.
     */
    static void wall(VertexConsumer buffer, PoseStack.Pose pose, float radius, float bottom, float top, float repeat, float from, float to,
                     int bottomColour, int topColour) {
        if (radius <= 0 || top <= bottom) return;
        for (int side = 0; side < SEGMENTS; side++) {
            float a0 = Mth.TWO_PI * side / SEGMENTS, a1 = Mth.TWO_PI * (side + 1) / SEGMENTS;
            float u0 = repeat * side / SEGMENTS, u1 = repeat * (side + 1) / SEGMENTS;
            float x0 = Mth.cos(a0) * radius, z0 = Mth.sin(a0) * radius, x1 = Mth.cos(a1) * radius, z1 = Mth.sin(a1) * radius;
            quad(buffer, pose,
                    x0, bottom, z0, u0, to, bottomColour,
                    x0, top, z0, u0, from, topColour,
                    x1, top, z1, u1, from, topColour,
                    x1, bottom, z1, u1, to, bottomColour);
        }
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/spell_visual/" + name + ".png");
    }

    private VisualDraw() {}
}
