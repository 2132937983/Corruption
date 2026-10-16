package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * A transmutation, as an alchemist's is drawn: a transmutation circle is drawn on the ground as by a hand going
 * round; once it is closed, crackling arcs of light leap up over it and across it, one after another; the
 * reaction flares in a column of light; and motes of the change rise out of it, like gold dust.
 */
public final class AlchemyVisual implements SpellVisual {
    /** Ticks the circle takes to draw, then each arc lasts; motes rising. */
    private static final float DRAWING = 14, ARC = 6;
    private static final int MOTES = 30;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();
        float drawn = VisualDraw.easeOutCubic(age / DRAWING), reacting = VisualDraw.within(age, DRAWING, 6), flare = reacting > 0 ? 1 - VisualDraw.within(age, DRAWING, 12) : 0;
        float tall = Math.max(1.5F, shown.height());

        VertexConsumer circle = VisualDraw.light(buffers, VisualDraw.ALCHEMY);
        VisualDraw.wipe(circle, at, radius, VisualDraw.LIFT, shown.yaw(), 360 * drawn, VisualDraw.glow(shown.colour(), strength * (.8F + .2F * flare)));

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        if (flare > 0) VisualDraw.disc(glow, at, radius * (1 + flare * .3F), VisualDraw.LIFT * 2, 0, VisualDraw.glow(shown.accent(), strength * flare));

        VertexConsumer column = VisualDraw.light(buffers, VisualDraw.STREAKS);
        if (reacting > 0)
            VisualDraw.wall(column, at, radius * .35F * (1 + flare * .4F), 0, tall * 1.5F * reacting, 4, -age * .1F, -age * .1F + 1.5F,
                    VisualDraw.glow(shown.accent(), strength * (.4F + .6F * flare)), 0);

        VertexConsumer arcs = VisualDraw.light(buffers, VisualDraw.BEAM);
        Vector3f forward = shown.forward();
        Random random = new Random(shown.seed());
        int count = Mth.clamp(Math.round(radius * 4), 10, 20);
        for (int arc = 0; arc < count; arc++) {
            float when = DRAWING + random.nextFloat() * Math.max(1, shown.lifetime() * .75F - DRAWING);
            float from = random.nextFloat() * Mth.TWO_PI, to = from + (.6F + 1.6F * random.nextFloat()) * (random.nextBoolean() ? 1 : -1);
            float arch = .8F + 1.6F * random.nextFloat();
            long path = random.nextLong();
            float since = age - when;
            if (since < 0 || since >= ARC) continue;
            float light = strength * (since < 1 ? 1 : (ARC - since) / ARC);
            float[] bolt = VisualPaths.bolt(new Random(path), Mth.cos(from) * radius * .9F, .05F, Mth.sin(from) * radius * .9F,
                    Mth.cos(to) * radius * .9F, .05F, Mth.sin(to) * radius * .9F, 4, .18F);
            // Leaping up over the circle between its two ends.
            int points = bolt.length / 3;
            for (int point = 0; point < points; point++) bolt[point * 3 + 1] += Mth.sin(Mth.PI * point / (points - 1)) * arch;
            VisualDraw.beams(arcs, at, forward, bolt, .45F, VisualDraw.glow(shown.colour(), light * .8F));
            VisualDraw.beams(arcs, at, forward, bolt, .13F, VisualDraw.glow(VisualDraw.mix(shown.accent(), 0xFFFFFF, .4F), light));
        }

        if (reacting <= 0) return;
        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        for (int mote = 0; mote < MOTES; mote++) {
            float t = ((age - DRAWING) / 26 + VisualDraw.hash(shown.seed(), mote, 0)) % 1, angle = VisualDraw.hash(shown.seed(), mote, 1) * Mth.TWO_PI;
            float far = radius * .8F * VisualDraw.hash(shown.seed(), mote, 2);
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle + t) * far, .2F + t * tall * 1.4F, Mth.sin(angle + t) * far, .18F, .18F, 0,
                    VisualDraw.TWINKLE, VisualDraw.glow(shown.accent(), strength * reacting * Mth.sin(t * Mth.PI)));
        }
    }
}
