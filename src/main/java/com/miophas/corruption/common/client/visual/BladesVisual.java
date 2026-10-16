package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * Spectral blades, as a king's treasury of weapons opening in the sky is drawn: one after another, swords of light
 * glint into being high over the ground, point down, each out of a small turning circle; they hang there, bobbing,
 * then plunge one after another into the ground, a flash and a spray of sparks where each strikes, and stand there
 * buried a third of their length until they fade.
 */
public final class BladesVisual implements SpellVisual {
    /** Ticks a blade takes to appear, and to fall. */
    private static final float APPEARING = 4, FALLING = 4;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();
        Random random = new Random(shown.seed());
        int count = Mth.clamp(Math.round(radius * 3), 6, 16);
        float hang = Math.max(4, shown.height()), length = Mth.clamp(radius * .6F, 1.8F, 3.6F), width = length / 4;
        float[] x = new float[count], z = new float[count], appears = new float[count], falls = new float[count], turn = new float[count], tip = new float[count];
        for (int blade = 0; blade < count; blade++) {
            float angle = Mth.TWO_PI * (blade + random.nextFloat() * .5F) / count, far = radius * (.3F + .6F * random.nextFloat());
            x[blade] = Mth.cos(angle) * far;
            z[blade] = Mth.sin(angle) * far;
            appears[blade] = blade * 1.2F;
            falls[blade] = 14 + blade * 2.2F + random.nextFloat() * 2;
            turn[blade] = random.nextFloat() * Mth.PI;
            float bob = .12F * Mth.sin(age * .2F + blade);
            float fallen = VisualDraw.within(age, falls[blade], FALLING);
            tip[blade] = Mth.lerp(fallen * fallen, hang + bob, -length * .33F);
        }

        // The circles they come out of, while they hang.
        VertexConsumer gates = VisualDraw.light(buffers, VisualDraw.CIRCLE_OUTER);
        Vector3f east = new Vector3f(1, 0, 0), south = new Vector3f(0, 0, 1);
        for (int blade = 0; blade < count; blade++) {
            float open = VisualDraw.within(age, appears[blade], APPEARING) * (1 - VisualDraw.within(age, falls[blade], FALLING * 2));
            if (open <= 0) continue;
            VisualDraw.discIn(gates, at, x[blade], hang + length + .1F, z[blade], east, south, width * 2.6F * open, age * 6 + blade * 40,
                    VisualDraw.glow(shown.colour(), strength * open));
        }

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        for (int blade = 0; blade < count; blade++) {
            float struck = 1 - VisualDraw.within(age, falls[blade] + FALLING, 8);
            if (age < falls[blade] + FALLING || struck <= 0) continue;
            VisualDraw.discIn(glow, at, x[blade], VisualDraw.LIFT, z[blade], east, south, 1.4F, 0, VisualDraw.glow(shown.accent(), struck * strength));
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        for (int blade = 0; blade < count; blade++) {
            float seen = VisualDraw.within(age, appears[blade], APPEARING);
            if (seen <= 0) continue;
            int colour = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), .35F), strength * seen);
            float top = tip[blade] + length, c = Mth.cos(turn[blade]), s = Mth.sin(turn[blade]);
            // Two faces crossed, so it is a blade from every side.
            VisualDraw.spriteAlong(sprites, at, x[blade], top, z[blade], x[blade], tip[blade], z[blade], c, 0, s, width, VisualDraw.BLADE, colour);
            VisualDraw.spriteAlong(sprites, at, x[blade], top, z[blade], x[blade], tip[blade], z[blade], -s, 0, c, width, VisualDraw.BLADE, colour);
            float glint = 1 - VisualDraw.within(age, appears[blade], APPEARING * 2);
            if (glint > 0) VisualDraw.sprite(sprites, at, right, up, x[blade], top - length * .2F, z[blade], 1.2F * glint, 1.2F * glint, age * .2F,
                    VisualDraw.TWINKLE, VisualDraw.glow(shown.accent(), glint * strength));
            // Sparks thrown out where it struck.
            float since = age - falls[blade] - FALLING;
            if (since < 0 || since > 8) continue;
            for (int spark = 0; spark < 6; spark++) {
                float angle = Mth.TWO_PI * spark / 6 + blade, out = since * .18F;
                VisualDraw.sprite(sprites, at, right, up, x[blade] + Mth.cos(angle) * out, Math.max(.05F, .1F + since * .12F - since * since * .02F),
                        z[blade] + Mth.sin(angle) * out, .18F, .18F, 0, VisualDraw.EMBER, VisualDraw.glow(shown.accent(), (1 - since / 8) * strength));
            }
        }
    }
}
