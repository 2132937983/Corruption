package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * A storm of feathers, as a flock of crows bursting from a sorcerer is drawn: a gust rings out over the ground and
 * feathers burst from the middle, flung out and up on swirling paths, then drift down, rocking and turning as they
 * fall.
 */
public final class FeatherStormVisual implements SpellVisual {
    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();

        VertexConsumer gust = VisualDraw.light(buffers, VisualDraw.BAND);
        float reach = radius * VisualDraw.easeOutCubic(shown.progress() * 2), fading = 1 - Math.min(1, shown.progress() * 2);
        VisualDraw.ring(gust, at, Math.max(0, reach - .8F), reach, VisualDraw.LIFT, 6, age * .03F, 0, VisualDraw.glow(shown.colour(), fading * strength));

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        Random random = new Random(shown.seed());
        int count = Mth.clamp(Math.round(radius * 10), 20, 72);
        for (int feather = 0; feather < count; feather++) {
            float angle = random.nextFloat() * Mth.TWO_PI, far = radius * (.5F + .5F * random.nextFloat()), from = .8F + .8F * random.nextFloat();
            float swirl = (random.nextBoolean() ? 1 : -1) * (.5F + random.nextFloat()), lift = .8F + 1.6F * random.nextFloat();
            float size = .5F + .4F * random.nextFloat(), delay = random.nextFloat() * 4, phase = random.nextFloat() * Mth.TWO_PI;
            float spin = (random.nextFloat() - .5F) * .3F;
            boolean bright = random.nextFloat() < .3F;
            float time = age - delay;
            if (time < 0) continue;
            float out = far * (1 - (float) Math.exp(-time / 6)), turned = angle + swirl * 2.5F * (1 - (float) Math.exp(-time / 10));
            float y = Math.max(.05F, from + lift * (1 - (float) Math.exp(-time / 5)) - .03F * time + .2F * Mth.sin(time * .2F + phase));
            float sway = .25F * Mth.sin(time * .15F + phase);
            int colour = VisualDraw.glow(bright ? shown.accent() : shown.colour(), strength * Math.min(1, time / 3));
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(turned) * out + sway, y, Mth.sin(turned) * out, size / 3, size,
                    phase + time * spin, VisualDraw.FEATHER, colour);
        }
    }
}
