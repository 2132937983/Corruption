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
 * A lightning storm, as a thunder god's is drawn: a glowing eye of storm turns overhead, and out of it forked bolts
 * strike the ground round the spot one after another, the first in its middle, each flickering on, off and on
 * again as real lightning does, a white-hot thread in a broad glow, its forks thinner, lighting the cloud and
 * splashing light where it lands.
 */
public final class LightningVisual implements SpellVisual {
    /** Ticks one bolt shows for; billows of cloud round the eye of the storm. */
    private static final float STRIKE = 10;
    private static final int BILLOWS = 16;
    /** How a bolt's light goes over the ticks it shows, as lightning flickers: on, dim, on, then dying. */
    private static final float[] FLICKER = {1, .3F, 1, .8F, .6F, .45F, .3F, .2F, .1F, .05F};

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0) return;
        PoseStack.Pose at = pose.last();
        float sky = Math.max(6, shown.height());
        Random random = new Random(shown.seed());
        int count = Mth.clamp(Math.round(radius * 2.5F), 4, 16);
        // One after another through the first seven tenths of its life, each a little early or late.
        float spacing = shown.lifetime() * .7F / count;
        float[] when = new float[count], groundX = new float[count], groundZ = new float[count], topX = new float[count], topZ = new float[count];
        long[] paths = new long[count];
        float lit = 0;
        for (int strike = 0; strike < count; strike++) {
            float angle = random.nextFloat() * Mth.TWO_PI, far = radius * Mth.sqrt(random.nextFloat());
            when[strike] = strike == 0 ? 0 : (strike + (random.nextFloat() - .5F) * .8F) * spacing;
            groundX[strike] = strike == 0 ? 0 : Mth.cos(angle) * far;
            groundZ[strike] = strike == 0 ? 0 : Mth.sin(angle) * far;
            topX[strike] = groundX[strike] + (random.nextFloat() - .5F) * radius * .6F;
            topZ[strike] = groundZ[strike] + (random.nextFloat() - .5F) * radius * .6F;
            paths[strike] = random.nextLong();
            lit = Math.max(lit, flicker(age - when[strike]));
        }

        VertexConsumer cloud = VisualDraw.light(buffers, VisualDraw.SPIRAL);
        float cloudSize = Math.max(3, radius * 1.4F);
        VisualDraw.disc(cloud, at, cloudSize, sky, age * 2, VisualDraw.glow(shown.colour(), strength * (.3F + .5F * lit)));

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        Vector3f east = new Vector3f(1, 0, 0), south = new Vector3f(0, 0, 1);
        VisualDraw.discIn(glow, at, 0, sky + .1F, 0, east, south, cloudSize * 1.2F, 0, VisualDraw.glow(shown.accent(), strength * lit * .6F));
        for (int strike = 0; strike < count; strike++) {
            float light = flicker(age - when[strike]) * strength;
            if (light <= 0) continue;
            VisualDraw.discIn(glow, at, groundX[strike], VisualDraw.LIFT, groundZ[strike], east, south, 1.8F, 0, VisualDraw.glow(shown.accent(), light));
        }

        // Billows of cloud round the eye, so that it is a storm from the side too, lit up by every strike.
        VertexConsumer billows = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        for (int billow = 0; billow < BILLOWS; billow++) {
            float angle = VisualDraw.hash(shown.seed(), billow, 0) * Mth.TWO_PI + age * .01F, far = cloudSize * (.25F + .75F * VisualDraw.hash(shown.seed(), billow, 1));
            float size = cloudSize * (.45F + .3F * VisualDraw.hash(shown.seed(), billow, 2));
            VisualDraw.sprite(billows, at, right, up, Mth.cos(angle) * far, sky + .3F * VisualDraw.hash(shown.seed(), billow, 3), Mth.sin(angle) * far, size, size * .6F,
                    VisualDraw.hash(shown.seed(), billow, 4) * Mth.TWO_PI, VisualDraw.PUFF, VisualDraw.glow(shown.colour(), strength * (.12F + .45F * lit)));
        }

        VertexConsumer bolts = VisualDraw.light(buffers, VisualDraw.BEAM);
        Vector3f forward = shown.forward();
        for (int strike = 0; strike < count; strike++) {
            float light = flicker(age - when[strike]) * strength;
            if (light <= 0) continue;
            Random path = new Random(paths[strike]);
            float[] bolt = VisualPaths.bolt(path, topX[strike], sky, topZ[strike], groundX[strike], 0, groundZ[strike], 5, .22F);
            List<float[]> forks = VisualPaths.forks(path, bolt, 3, sky * .3F, 3, .25F);
            // What would strike down into the ground stops at it.
            aboveGround(bolt);
            forks.forEach(LightningVisual::aboveGround);
            VisualDraw.beams(bolts, at, forward, bolt, .9F, VisualDraw.glow(shown.colour(), light * .7F));
            VisualDraw.beams(bolts, at, forward, bolt, .28F, VisualDraw.glow(VisualDraw.mix(shown.accent(), 0xFFFFFF, .5F), light));
            for (float[] fork : forks) {
                VisualDraw.beams(bolts, at, forward, fork, .45F, VisualDraw.glow(shown.colour(), light * .5F));
                VisualDraw.beams(bolts, at, forward, fork, .12F, VisualDraw.glow(shown.accent(), light * .8F));
            }
        }
    }

    private static void aboveGround(float[] path) {
        for (int at = 1; at < path.length; at += 3) path[at] = Math.max(0, path[at]);
    }

    /** How bright a bolt is this long after it struck: nothing before or after it shows. */
    private static float flicker(float since) {
        if (since < 0 || since >= STRIKE) return 0;
        return FLICKER[Math.min(FLICKER.length - 1, (int) since)];
    }
}
