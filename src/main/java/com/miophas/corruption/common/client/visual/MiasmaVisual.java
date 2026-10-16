package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * A miasma, as a plague-bringer's foul breath is drawn: billows of sickly, glowing cloud roll out low over the
 * ground, swelling and turning as they go; bubbles rise out of the fouled ground below them and burst; the
 * ground glows with it.
 */
public final class MiasmaVisual implements SpellVisual {
    /** Ticks a billow takes to roll out; bubbles; ticks a bubble rises before it bursts. */
    private static final float ROLLING = 20, RISING = 18;
    private static final int BUBBLES = 20;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        VisualDraw.disc(glow, at, radius * 1.1F * VisualDraw.easeOutCubic(age / ROLLING), VisualDraw.LIFT, 0, VisualDraw.glow(shown.colour(), strength * .45F));

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        Random random = new Random(shown.seed());
        int count = Mth.clamp(Math.round(radius * 6), 10, 40);
        float scale = Mth.clamp(radius / 3, .7F, 1.6F);
        for (int billow = 0; billow < count; billow++) {
            float angle = random.nextFloat() * Mth.TWO_PI, far = radius * (.2F + .8F * Mth.sqrt(random.nextFloat())), delay = random.nextFloat() * 6;
            float size = (.8F + .8F * random.nextFloat()) * scale, phase = random.nextFloat() * Mth.TWO_PI, spin = (random.nextFloat() - .5F) * .05F;
            float rising = random.nextFloat();
            float since = age - delay;
            if (since < 0) continue;
            float out = far * VisualDraw.easeOutCubic(since / ROLLING), y = .35F + .25F * Mth.sin(since * .1F + phase) + since * .008F * rising;
            float grown = size * (.6F + .6F * VisualDraw.within(since, 0, 25));
            int colour = VisualDraw.glow(billow % 3 == 0 ? VisualDraw.mix(shown.colour(), shown.accent(), .3F) : shown.colour(),
                    strength * .32F * VisualDraw.within(since, 0, 4));
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle) * out, y, Mth.sin(angle) * out, grown, grown, phase + since * spin, VisualDraw.PUFF, colour);
        }
        for (int bubble = 0; bubble < BUBBLES; bubble++) {
            float angle = random.nextFloat() * Mth.TWO_PI, far = radius * .9F * Mth.sqrt(random.nextFloat()), offset = random.nextFloat();
            float t = (age / RISING + offset) % 1;
            if (age < 4) continue;
            float size = .12F + .25F * t;
            int colour = VisualDraw.glow(shown.accent(), strength * (t > .9F ? 1 : .6F));
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle) * far, .1F + t * 1.2F, Mth.sin(angle) * far, size, size, 0,
                    t > .9F ? VisualDraw.TWINKLE : VisualDraw.BUBBLE, colour);
        }
    }
}
