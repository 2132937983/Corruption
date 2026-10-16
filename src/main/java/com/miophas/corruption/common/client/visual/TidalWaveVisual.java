package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * A tidal wave, as the sea rising at a sea-god's call is drawn: water swells up in a ring round the spot and rushes
 * outward, rising into a wall whose crest curls over, white with foam, spray flung off it; a smaller wave follows
 * the first; the ground behind them glistens with running water.
 */
public final class TidalWaveVisual implements SpellVisual {
    /**
     * The wave seen across, as shares of its height out of its front and up: its long back sloping up from behind to
     * the crest; and its steep front face, rising from its foot, hollowed, to the crest and curling over in a lip.
     */
    private static final float[][] OUT = {{-1.1F, -.55F, -.15F, .2F}, {.12F, .04F, .14F, .35F, .55F, .48F}};
    private static final float[][] UP = {{0, .5F, .88F, 1}, {0, .45F, .78F, .97F, .8F, .6F}};
    /** How bright each of those points is: dark water low down, foam at the crest. */
    private static final float[][] LIGHT = {{.2F, .4F, .65F, 1}, {.35F, .5F, .7F, 1, .85F, .5F}};
    private static final int SPRAY = 40;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();
        float tall = Math.max(.8F, shown.height());

        VertexConsumer water = VisualDraw.light(buffers, VisualDraw.WATER);
        float front = front(shown, 0), flow = age * .04F;
        VisualDraw.ring(water, at, 0, Math.max(.01F, front), VisualDraw.LIFT, 6, flow, VisualDraw.glow(shown.colour(), strength * .15F),
                VisualDraw.glow(shown.colour(), strength * .35F));
        wave(water, at, shown, 0, 1, tall, strength);
        wave(water, at, shown, .25F, .6F, tall, strength * .8F);

        VertexConsumer foam = VisualDraw.light(buffers, VisualDraw.BAND);
        float crest = height(shown, 0, 1, tall);
        if (crest > .05F)
            VisualDraw.ring(foam, at, front + .1F * crest, front + .45F * crest, crest * .97F, 8, -age * .05F, VisualDraw.glow(shown.accent(), strength),
                    VisualDraw.glow(shown.accent(), strength * .3F));

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        Random random = new Random(shown.seed());
        for (int drop = 0; drop < SPRAY; drop++) {
            float angle = random.nextFloat() * Mth.TWO_PI, when = random.nextFloat() * shown.lifetime() * .6F, out = .05F + .1F * random.nextFloat();
            float upward = .12F + .15F * random.nextFloat(), size = .12F + .14F * random.nextFloat();
            float since = age - when;
            if (since < 0 || since > 12) continue;
            float from = front(shown, 0, when), lifted = height(shown, 0, 1, tall, when);
            if (lifted < .1F) continue;
            float far = from + .4F * lifted + out * since, y = lifted + upward * since - .02F * since * since;
            if (y <= 0) continue;
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle) * far, y, Mth.sin(angle) * far, size, size, 0, VisualDraw.EMBER,
                    VisualDraw.glow(shown.accent(), strength * (1 - since / 12)));
        }
    }

    /** One wave: its back and its front face, each a ring of water for each stretch between two of its points. */
    private static void wave(VertexConsumer water, PoseStack.Pose at, Shown shown, float delay, float size, float tall, float strength) {
        float front = front(shown, delay), high = height(shown, delay, size, tall);
        if (high <= .05F) return;
        float flow = shown.age() * .04F, rise = -shown.age() * .06F;
        for (int face = 0; face < OUT.length; face++)
            for (int band = 0; band + 1 < OUT[face].length; band++)
                stretch(water, at, shown, front, high, flow, rise, OUT[face][band], UP[face][band], LIGHT[face][band],
                        OUT[face][band + 1], UP[face][band + 1], LIGHT[face][band + 1], band, strength);
    }

    /** One stretch of a wave's face, between two of its points, all round. */
    private static void stretch(VertexConsumer water, PoseStack.Pose at, Shown shown, float front, float high, float flow, float rise,
                                float out0, float up0, float light0, float out1, float up1, float light1, int band, float strength) {
        float r0 = Math.max(0, front + out0 * high), r1 = Math.max(0, front + out1 * high), y0 = up0 * high, y1 = up1 * high;
        int c0 = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), light0 - .3F), strength * light0);
        int c1 = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), light1 - .3F), strength * light1);
        float v0 = rise + band * .25F, v1 = rise + (band + 1) * .25F;
        for (int piece = 0; piece < VisualDraw.SEGMENTS; piece++) {
            float a0 = Mth.TWO_PI * piece / VisualDraw.SEGMENTS, a1 = Mth.TWO_PI * (piece + 1) / VisualDraw.SEGMENTS;
            float u0 = 8F * piece / VisualDraw.SEGMENTS + flow, u1 = 8F * (piece + 1) / VisualDraw.SEGMENTS + flow;
            VisualDraw.quad(water, at,
                    Mth.cos(a0) * r0, y0, Mth.sin(a0) * r0, u0, v0, c0,
                    Mth.cos(a0) * r1, y1, Mth.sin(a0) * r1, u0, v1, c1,
                    Mth.cos(a1) * r1, y1, Mth.sin(a1) * r1, u1, v1, c1,
                    Mth.cos(a1) * r0, y0, Mth.sin(a1) * r0, u1, v0, c0);
        }
    }

    /** How far out a wave's front is: racing out fast, slowing as it reaches the effect's edge. */
    private static float front(Shown shown, float delay) { return front(shown, delay, shown.age()); }

    private static float front(Shown shown, float delay, float age) {
        float travelled = VisualDraw.within(age / Math.max(1, shown.lifetime()), delay, .75F);
        return .3F + shown.radius() * VisualDraw.easeOutCubic(travelled);
    }

    /** How high a wave stands: rising as it goes, breaking and falling away before its end. */
    private static float height(Shown shown, float delay, float size, float tall) { return height(shown, delay, size, tall, shown.age()); }

    private static float height(Shown shown, float delay, float size, float tall, float age) {
        float travelled = VisualDraw.within(age / Math.max(1, shown.lifetime()), delay, .75F);
        if (travelled <= 0 || travelled >= 1) return 0;
        // Never so tall that its long back would begin behind the middle it rose from.
        float high = tall * size * (float) Math.pow(Mth.sin(Mth.PI * Math.min(1, travelled * 1.1F)), .6);
        return Math.min(high, front(shown, delay, age) / 1.15F);
    }
}
