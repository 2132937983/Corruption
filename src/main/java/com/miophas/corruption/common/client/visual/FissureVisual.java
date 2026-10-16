package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * The ground splitting, as an earthquake spell is drawn: cracks race out from the middle, branching as they go,
 * molten light glowing in them and seeping up out of them; the middle glows like a crater; shards of the ground
 * are flung up and fall; then the cracks cool, from white-hot to the effect's colour to nothing.
 */
public final class FissureVisual implements SpellVisual {
    /** Ticks the cracks take to run their length, and shards thrown up. */
    private static final float RUNNING = 14;
    private static final int SHARDS = 14;
    private static final float GRAVITY = .04F;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();
        Random random = new Random(shown.seed());
        List<float[]> cracks = VisualPaths.cracks(random, Mth.clamp(Math.round(radius * 1.5F), 5, 10), radius);
        float longest = 0;
        for (float[] crack : cracks) longest = Math.max(longest, crack[5]);
        float front = longest * VisualDraw.easeOutCubic(age / RUNNING);
        float cooling = VisualDraw.within(shown.progress(), .5F, .5F), heat = strength * (1 - cooling * .6F);
        int hot = VisualDraw.mix(shown.accent(), shown.colour(), cooling);

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        VisualDraw.disc(glow, at, Math.max(1, radius * .45F), VisualDraw.LIFT, 0, VisualDraw.glow(hot, heat));

        VertexConsumer lines = VisualDraw.light(buffers, VisualDraw.BEAM);
        for (float[] crack : cracks) {
            if (crack[4] >= front) continue;
            float[] end = reached(crack, front);
            float width = .25F + .35F * (1 - crack[4] / longest);
            VisualDraw.groundLine(lines, at, crack[0], crack[1], end[0], end[1], VisualDraw.LIFT * 2, width * 1.6F, VisualDraw.glow(shown.colour(), heat * .8F),
                    VisualDraw.glow(shown.colour(), heat * .8F));
            VisualDraw.groundLine(lines, at, crack[0], crack[1], end[0], end[1], VisualDraw.LIFT * 3, width * .5F, VisualDraw.glow(hot, heat),
                    VisualDraw.glow(hot, heat));
        }

        // Light seeping up out of the cracks, tallest near the middle, sinking as they cool.
        VertexConsumer seep = VisualDraw.light(buffers, VisualDraw.STREAKS);
        float rise = -age * .05F, tall = Math.min(2, Math.max(.6F, shown.height() * .4F)) * (1 - cooling);
        for (float[] crack : cracks) {
            if (crack[4] >= front || tall <= 0) continue;
            float[] end = reached(crack, front);
            float h0 = tall * (1 - crack[4] / longest * .7F), h1 = tall * (1 - Math.min(front, crack[5]) / longest * .7F);
            int foot = VisualDraw.glow(hot, heat * .7F);
            VisualDraw.quad(seep, at,
                    crack[0], 0, crack[1], 0, rise + 1, foot,
                    crack[0], h0, crack[1], 0, rise, 0,
                    end[0], h1, end[1], 1, rise, 0,
                    end[0], 0, end[1], 1, rise + 1, foot);
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        for (int shard = 0; shard < SHARDS; shard++) {
            float angle = random.nextFloat() * Mth.TWO_PI, from = radius * .5F * random.nextFloat(), out = .05F + .07F * random.nextFloat();
            float upward = .25F + .2F * random.nextFloat(), size = .2F + .25F * random.nextFloat(), spin = (random.nextFloat() - .5F) * .8F;
            float y = upward * age - GRAVITY / 2 * age * age;
            if (y <= 0 && age > 1) continue;
            float far = from + out * age;
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle) * far, Math.max(0, y), Mth.sin(angle) * far, size, size, spin * age,
                    VisualDraw.SHARD, VisualDraw.glow(hot, strength * .8F));
        }
    }

    /** Where a crack piece has run to, x and z, once the cracks have run this far. */
    private static float[] reached(float[] crack, float front) {
        float share = Mth.clamp((front - crack[4]) / Math.max(1e-4F, crack[5] - crack[4]), 0, 1);
        return new float[]{Mth.lerp(share, crack[0], crack[2]), Mth.lerp(share, crack[1], crack[3])};
    }
}
