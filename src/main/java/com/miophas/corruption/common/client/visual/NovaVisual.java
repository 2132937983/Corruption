package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * A star bursting, as a dying god or the end of a great spell is drawn: a blinding flash swells out of a point and
 * fades, a cross of light flaring through it; a ring of light races out flat round it, a second one tilted; and sparks
 * are flung every way but into the ground, trailing light and slowing as they go. The point is at the effect's height
 * over the spot (a block at the least); the rings and the sparks reach its radius. What would reach under the ground
 * is held above it.
 */
public final class NovaVisual implements SpellVisual {
    private static final int SPARKS = 28;
    /** Ticks the cross of light flares for. */
    private static final float CROSS = 12;
    /** How far the second ring is tilted from level, in degrees. */
    private static final float TILT = 62;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float radius = shown.radius(), age = shown.age();
        if (radius <= 0) return;
        float y = Math.max(1, shown.height()), progress = shown.progress(), fading = 1 - progress;
        Vector3f right = shown.right(), up = shown.up(), forward = shown.forward();
        PoseStack.Pose at = pose.last();

        // The flash: blinding at first, swelling, then fading; no larger than the room under it allows.
        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        float flash = 1 - VisualDraw.within(age, 1, shown.lifetime() * .45F), swell = VisualDraw.easeOutCubic(age / 6);
        float big = Math.min(radius * (.25F + .35F * swell), (y + 1.5F) / 1.45F);
        if (flash > 0) {
            VisualDraw.discIn(glow, at, 0, y, 0, right, up, big, age * 4, VisualDraw.glow(shown.colour(), flash * .8F));
            VisualDraw.discIn(glow, at, 0, y, 0, right, up, big * .55F, -age * 6, VisualDraw.glow(shown.accent(), flash));
            VisualDraw.discIn(glow, at, 0, y, 0, right, up, big * .3F, 0, VisualDraw.glow(0xFFFFFF, flash));
        }

        // A cross of light flaring through it, across the viewer's sight.
        VertexConsumer beam = VisualDraw.light(buffers, VisualDraw.BEAM);
        float cross = 1 - VisualDraw.within(age, 0, CROSS);
        if (cross > 0) {
            float length = radius * 1.1F * (.6F + .4F * swell), width = radius * .05F + .1F;
            int bright = VisualDraw.glow(shown.accent(), cross), dim = VisualDraw.glow(shown.colour(), cross * .2F);
            for (Vector3f way : new Vector3f[]{right, up, new Vector3f(right).negate(), new Vector3f(up).negate()}) {
                float reach = way.y < 0 ? Math.min(length, y / (-way.y + .05F)) : length;
                VisualDraw.beam(beam, at, forward, 0, y, 0, way.x * reach, y + way.y * reach, way.z * reach, width, bright, dim);
            }
        }

        // Sparks flung out, slowing as they go, a tail of light behind each: in the same light as the cross, so drawn before
        // the rings ask for theirs, which finishes it.
        float thin = radius * .025F + .08F;
        for (int spark = 0; spark < SPARKS; spark++) {
            float yaw = Mth.TWO_PI * VisualDraw.hash(shown.seed(), spark, 0), dy = -.35F + 1.3F * VisualDraw.hash(shown.seed(), spark, 1);
            float level = Mth.sqrt(1 - dy * dy), dx = Mth.cos(yaw) * level, dz = Mth.sin(yaw) * level;
            float out = radius * (.45F + .55F * VisualDraw.hash(shown.seed(), spark, 2)) * VisualDraw.easeOutCubic(Math.min(1, progress * 1.4F));
            if (dy < 0) out = Math.min(out, y / (-dy + .08F));
            float from = Math.max(0, out - radius * .18F * fading - .2F);
            if (out <= from) continue;
            VisualDraw.beam(beam, at, forward, dx * from, y + dy * from, dz * from, dx * out, y + dy * out, dz * out, thin,
                    VisualDraw.glow(shown.colour(), fading * .3F), VisualDraw.glow(shown.accent(), fading));
        }

        // The rings, racing out and thinning: one level, one tilted as far as the ground lets it reach.
        VertexConsumer band = VisualDraw.light(buffers, VisualDraw.BAND);
        float reach = radius * VisualDraw.easeOutCubic(progress), width = Math.max(.2F, radius * .14F * fading);
        pose.pushPose();
        pose.translate(0, y, 0);
        VisualDraw.ring(band, pose.last(), Math.max(0, reach - width), reach, 0, 10, age * .02F,
                VisualDraw.glow(shown.colour(), fading * .5F), VisualDraw.glow(shown.accent(), fading));
        float tilted = Math.min(reach, y / Mth.sin(TILT * Mth.DEG_TO_RAD));
        pose.mulPose(Axis.YP.rotationDegrees(360 * VisualDraw.hash(shown.seed(), 0, 9)));
        pose.mulPose(Axis.XP.rotationDegrees(TILT));
        VisualDraw.ring(band, pose.last(), Math.max(0, tilted - width * .7F), tilted, 0, 10, -age * .03F,
                0, VisualDraw.glow(shown.accent(), fading * .7F));
        pose.popPose();
    }
}
