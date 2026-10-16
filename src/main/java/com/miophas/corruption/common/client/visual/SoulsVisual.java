package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * Souls called up out of the ground, as the restless dead are drawn rising from a necromancer's circle: a slow
 * whirl of light opens on the ground, and out of it, one after another, wailing wisps and skulls spiral up round the
 * spot, a trail of light behind each, narrowing as they climb and fading as they reach the top.
 */
public final class SoulsVisual implements SpellVisual {
    /** Points of a soul's trail behind it, and how far back along its climb each is. */
    private static final int TRAIL = 6;
    private static final float TRAIL_STEP = .025F;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();
        float tall = Math.max(2, shown.height()), open = VisualDraw.easeOutCubic(age / 10);

        VertexConsumer whirl = VisualDraw.light(buffers, VisualDraw.SPIRAL);
        VisualDraw.disc(whirl, at, radius * .95F * open, VisualDraw.LIFT, -age * 3, VisualDraw.glow(shown.colour(), strength * .7F));
        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        VisualDraw.disc(glow, at, radius * 1.2F * open, VisualDraw.LIFT * 2, 0, VisualDraw.glow(shown.accent(), strength * .35F));

        Random random = new Random(shown.seed());
        int count = Mth.clamp(Math.round(radius * 5), 8, 28);
        float[][] souls = new float[count][];
        for (int soul = 0; soul < count; soul++)
            souls[soul] = new float[]{random.nextFloat() * Mth.TWO_PI, radius * (.25F + .7F * random.nextFloat()), 30 + 30 * random.nextFloat(),
                    random.nextFloat(), (random.nextBoolean() ? 1 : -1) * (.03F + .05F * random.nextFloat()), .45F + .35F * random.nextFloat(),
                    random.nextFloat() < .25F ? 1 : 0};

        VertexConsumer trails = VisualDraw.light(buffers, VisualDraw.BEAM);
        Vector3f forward = shown.forward();
        for (int soul = 0; soul < count; soul++) {
            float[] it = souls[soul];
            float since = age - it[3] * it[2] * .5F;
            if (since < 0) continue;
            float t = since % it[2] / it[2];
            for (int point = 0; point < TRAIL; point++) {
                float t0 = t - point * TRAIL_STEP, t1 = t0 - TRAIL_STEP;
                if (t1 < 0) break;
                float[] a = place(it, since - point * TRAIL_STEP * it[2], t0, tall), b = place(it, since - (point + 1) * TRAIL_STEP * it[2], t1, tall);
                float fade = strength * Mth.sin(t0 * Mth.PI) * (1 - (float) point / TRAIL) * .6F;
                VisualDraw.beam(trails, at, forward, a[0], a[1], a[2], b[0], b[1], b[2], it[5] * .5F, VisualDraw.glow(shown.colour(), fade), 0);
            }
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        for (int soul = 0; soul < count; soul++) {
            float[] it = souls[soul];
            float since = age - it[3] * it[2] * .5F;
            if (since < 0) continue;
            float t = since % it[2] / it[2];
            float[] here = place(it, since, t, tall);
            int colour = VisualDraw.glow(soul % 3 == 0 ? shown.accent() : shown.colour(), strength * Mth.sin(t * Mth.PI));
            VisualDraw.sprite(sprites, at, right, up, here[0], here[1], here[2], it[5], it[5] * 1.3F, .2F * Mth.sin(since * .2F + soul),
                    it[6] > 0 ? VisualDraw.SKULL : VisualDraw.WISP, colour);
        }
    }

    /** Where a soul is, this far into its climb (t, 0 to 1): spiralling round, narrowing as it rises. */
    private static float[] place(float[] soul, float since, float t, float tall) {
        float angle = soul[0] + soul[4] * since + t * 2, far = soul[1] * (1 - .4F * t);
        return new float[]{Mth.cos(angle) * far, .3F + t * tall, Mth.sin(angle) * far};
    }
}
