package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

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
            RUNES = texture("runes"), GLOW = texture("glow"),
            SPRITES = texture("sprites"), FLAMES = texture("flames"), BEAM = texture("beam"), FACET = texture("facet"), IRIS = texture("iris"), SCLERA = texture("sclera"),
            ALCHEMY = texture("alchemy_circle"), STAFF = texture("staff"), WATER = texture("water");
    /** How many sides a circle is drawn with. */
    static final int SEGMENTS = 48;
    /** The sprites on the sprites texture, four by four; the four notes are 4 to 7. */
    static final int FEATHER = 0, WISP = 1, PETAL = 2, TWINKLE = 3, NOTE = 4, LINK = 8, BLADE = 9, PUFF = 10, SHARD = 11, EMBER = 12, GEM = 13,
            BUBBLE = 14, SKULL = 15;
    /** How much of the sprites texture one sprite takes, each way. */
    static final float CELL = .25F;
    /** How far over the ground what lies flat on it is drawn, so as not to flicker into it. */
    static final float LIFT = .03F;

    /** How the effects' light is drawn: as eyes are, unless something seen from very far off is drawing them ({@link #drawnAs}). */
    private static Function<ResourceLocation, RenderType> lightType = RenderType::eyes;

    static VertexConsumer light(MultiBufferSource buffers, ResourceLocation texture) { return buffers.getBuffer(lightType.apply(texture)); }

    /**
     * Draws whatever {@code drawing} draws in this kind of light instead, each texture's own: a colossus's death is
     * drawn in the colossus's long fog, say, so that it is seen from as far off as the colossus is. Drawing is done on
     * the one thread that renders, so this is never seen half set.
     */
    static void drawnAs(Function<ResourceLocation, RenderType> type, Runnable drawing) {
        Function<ResourceLocation, RenderType> before = lightType;
        lightType = type;
        try { drawing.run(); } finally { lightType = before; }
    }

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

    /** A three-cornered face, seen from both sides (a four-cornered one with its last corner twice). */
    static void triangle(VertexConsumer buffer, PoseStack.Pose pose,
                         float x1, float y1, float z1, float u1, float v1, int c1,
                         float x2, float y2, float z2, float u2, float v2, int c2,
                         float x3, float y3, float z3, float u3, float v3, int c3) {
        quad(buffer, pose, x1, y1, z1, u1, v1, c1, x2, y2, z2, u2, v2, c2, x3, y3, z3, u3, v3, c3, x3, y3, z3, u3, v3, c3);
    }

    /**
     * One of the sprites texture's sprites turned to face the viewer, its middle at x, y, z, {@code width} across
     * and {@code height} tall, turned {@code roll} radians in the viewer's sight.
     */
    static void sprite(VertexConsumer buffer, PoseStack.Pose pose, Vector3f right, Vector3f up, float x, float y, float z,
                       float width, float height, float roll, int sprite, int argb) {
        float cos = Mth.cos(roll), sin = Mth.sin(roll), across = width / 2, tall = height / 2;
        float ax = (right.x * cos + up.x * sin) * across, ay = (right.y * cos + up.y * sin) * across, az = (right.z * cos + up.z * sin) * across;
        float bx = (up.x * cos - right.x * sin) * tall, by = (up.y * cos - right.y * sin) * tall, bz = (up.z * cos - right.z * sin) * tall;
        float u0 = (sprite % 4) * CELL, v0 = (sprite / 4) * CELL, u1 = u0 + CELL, v1 = v0 + CELL;
        quad(buffer, pose,
                x - ax + bx, y - ay + by, z - az + bz, u0, v0, argb,
                x - ax - bx, y - ay - by, z - az - bz, u0, v1, argb,
                x + ax - bx, y + ay - by, z + az - bz, u1, v1, argb,
                x + ax + bx, y + ay + by, z + az + bz, u1, v0, argb);
    }

    /**
     * One of the sprites texture's sprites laid along a line from a to b, its top at a, its bottom at b, {@code width}
     * across along w (a unit direction across the line).
     */
    static void spriteAlong(VertexConsumer buffer, PoseStack.Pose pose, float ax, float ay, float az, float bx, float by, float bz,
                            float wx, float wy, float wz, float width, int sprite, int argb) {
        float u0 = (sprite % 4) * CELL, v0 = (sprite / 4) * CELL, half = width / 2;
        quad(buffer, pose,
                ax - wx * half, ay - wy * half, az - wz * half, u0, v0, argb,
                bx - wx * half, by - wy * half, bz - wz * half, u0, v0 + CELL, argb,
                bx + wx * half, by + wy * half, bz + wz * half, u0 + CELL, v0 + CELL, argb,
                ax + wx * half, ay + wy * half, az + wz * half, u0 + CELL, v0, argb);
    }

    /**
     * A strip from a to b, reaching w (a direction already as long as half its width) either side; the texture's u
     * runs along it from {@code u0} to {@code u1} and its v across it.
     */
    static void segment(VertexConsumer buffer, PoseStack.Pose pose, float ax, float ay, float az, float bx, float by, float bz,
                        float wx, float wy, float wz, float u0, float u1, int ca, int cb) {
        quad(buffer, pose,
                ax - wx, ay - wy, az - wz, u0, 0, ca,
                ax + wx, ay + wy, az + wz, u0, 1, ca,
                bx + wx, by + wy, bz + wz, u1, 1, cb,
                bx - wx, by - wy, bz - wz, u1, 0, cb);
    }

    /**
     * A line of light from a to b, {@code width} wide, turned to face the viewer as well as a line can be: across it
     * and across the viewer's sight. Drawn with the beam texture, it is a bright thread in a soft glow.
     */
    static void beam(VertexConsumer buffer, PoseStack.Pose pose, Vector3f forward, float ax, float ay, float az, float bx, float by, float bz,
                     float width, int ca, int cb) {
        float dx = bx - ax, dy = by - ay, dz = bz - az;
        float wx = dy * forward.z - dz * forward.y, wy = dz * forward.x - dx * forward.z, wz = dx * forward.y - dy * forward.x;
        float length = Mth.sqrt(wx * wx + wy * wy + wz * wz);
        if (length < 1e-6F) {
            // Seen end on: any way across it will do.
            wx = dz;
            wy = 0;
            wz = -dx;
            length = Mth.sqrt(wx * wx + wz * wz);
            if (length < 1e-6F) {
                wx = 1;
                length = 1;
            }
        }
        float scale = width / 2 / length;
        segment(buffer, pose, ax, ay, az, bx, by, bz, wx * scale, wy * scale, wz * scale, 0, 1, ca, cb);
    }

    /** Lines of light through a path's points (x, y, z one after another), each piece as {@link #beam} draws it. */
    static void beams(VertexConsumer buffer, PoseStack.Pose pose, Vector3f forward, float[] path, float width, int argb) {
        for (int at = 0; at + 5 < path.length; at += 3)
            beam(buffer, pose, forward, path[at], path[at + 1], path[at + 2], path[at + 3], path[at + 4], path[at + 5], width, argb, argb);
    }

    /** A line of light lying on the ground from a to b, {@code width} wide. */
    static void groundLine(VertexConsumer buffer, PoseStack.Pose pose, float ax, float az, float bx, float bz, float y, float width, int ca, int cb) {
        float dx = bx - ax, dz = bz - az, length = Mth.sqrt(dx * dx + dz * dz);
        if (length < 1e-6F) return;
        float scale = width / 2 / length;
        segment(buffer, pose, ax, y, az, bx, y, bz, -dz * scale, 0, dx * scale, 0, 1, ca, cb);
    }

    /**
     * A whole texture laid in the plane of two directions a and b (each of length one, a square to b), its middle at
     * x, y, z, this far round, turned this many degrees in that plane: its u along a and its v along b.
     */
    static void discIn(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, Vector3f a, Vector3f b, float radius, float degrees, int argb) {
        float angle = (float) Math.toRadians(degrees), cos = Mth.cos(angle) * radius, sin = Mth.sin(angle) * radius;
        float ux = a.x * cos + b.x * sin, uy = a.y * cos + b.y * sin, uz = a.z * cos + b.z * sin;
        float vx = b.x * cos - a.x * sin, vy = b.y * cos - a.y * sin, vz = b.z * cos - a.z * sin;
        quad(buffer, pose,
                x - ux - vx, y - uy - vy, z - uz - vz, 0, 0, argb,
                x - ux + vx, y - uy + vy, z - uz + vz, 0, 1, argb,
                x + ux + vx, y + uy + vy, z + uz + vz, 1, 1, argb,
                x + ux - vx, y + uy - vy, z + uz - vz, 1, 0, argb);
    }

    /**
     * A round texture as {@link #discIn} lays it, but cut round: a fan of pieces reaching just its radius, so that a
     * disc standing up and turning never swings its corners past its circle (into the ground below it, say).
     */
    static void roundIn(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, Vector3f a, Vector3f b, float radius, float degrees, int argb) {
        float turn = (float) Math.toRadians(degrees);
        int pieces = SEGMENTS / 2;
        for (int piece = 0; piece < pieces; piece++) {
            float a0 = Mth.TWO_PI * piece / pieces, a1 = Mth.TWO_PI * (piece + 1) / pieces;
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0), c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            // The texture turned, not the fan: each corner shows what lies that far round the texture, turned back.
            float t0 = a0 - turn, t1 = a1 - turn;
            quad(buffer, pose,
                    x, y, z, .5F, .5F, argb,
                    x, y, z, .5F, .5F, argb,
                    x + (a.x * c0 + b.x * s0) * radius, y + (a.y * c0 + b.y * s0) * radius, z + (a.z * c0 + b.z * s0) * radius,
                    .5F + Mth.cos(t0) * .5F, .5F + Mth.sin(t0) * .5F, argb,
                    x + (a.x * c1 + b.x * s1) * radius, y + (a.y * c1 + b.y * s1) * radius, z + (a.z * c1 + b.z * s1) * radius,
                    .5F + Mth.cos(t1) * .5F, .5F + Mth.sin(t1) * .5F, argb);
        }
    }

    /**
     * As much of a whole round texture laid flat as a hand sweeping round from {@code start} degrees has passed
     * over in {@code sweep} degrees, as a circle is drawn by a pen going round.
     */
    static void wipe(VertexConsumer buffer, PoseStack.Pose pose, float radius, float y, float start, float sweep, int argb) {
        float swept = Mth.clamp(sweep, 0, 360);
        int pieces = Mth.ceil(SEGMENTS * swept / 360);
        for (int piece = 0; piece < pieces; piece++) {
            float a0 = (float) Math.toRadians(start + swept * piece / pieces), a1 = (float) Math.toRadians(start + swept * (piece + 1) / pieces);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0), c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            quad(buffer, pose,
                    0, y, 0, .5F, .5F, argb,
                    0, y, 0, .5F, .5F, argb,
                    c0 * radius, y, s0 * radius, .5F + c0 * .5F, .5F + s0 * .5F, argb,
                    c1 * radius, y, s1 * radius, .5F + c1 * .5F, .5F + s1 * .5F, argb);
        }
    }

    /** The way someone facing this yaw (degrees) looks, along the ground: x and z. */
    static float lookX(float yaw) { return -Mth.sin((float) Math.toRadians(yaw)); }
    static float lookZ(float yaw) { return Mth.cos((float) Math.toRadians(yaw)); }

    /** A number from 0 to 1, always the same for the same seed and the same two numbers: one piece of an effect's own variety. */
    static float hash(long seed, int a, int b) {
        long z = seed + a * 0x9E3779B97F4A7C15L + b * 0xC2B2AE3D27D4EB4FL;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        z ^= z >>> 31;
        return (z >>> 40) / (float) (1L << 24);
    }

    /** How far through a stretch of time from {@code start} for {@code length} a moment is, 0 to 1. */
    static float within(float time, float start, float length) { return Mth.clamp((time - start) / Math.max(1e-4F, length), 0, 1); }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/spell_visual/" + name + ".png");
    }

    private VisualDraw() {}
}
