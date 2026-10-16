package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * Hellfire, as the films draw a sorcerer's ring of fire: a flash, then a wall of flame bursts up out of the ground
 * in a ring, its tongues licking up it at heights of their own and racing up its face; a lower ring burns inside
 * it; the ground within glows as a forge does; and embers whirl up out of it all and drift away.
 */
public final class HellfireVisual implements SpellVisual {
    /** Pieces round each ring of flame, and embers rising. */
    private static final int PIECES = 40, EMBERS = 36;
    /** Ticks the flash lasts. */
    private static final float FLASH = 6;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age();
        float radius = shown.radius() * VisualDraw.easeOutBack(age / 8);
        if (radius <= 0 || strength <= 0) return;
        float tall = Math.max(1, shown.height()) * VisualDraw.easeOutCubic(age / 10) * (.6F + .4F * strength);
        PoseStack.Pose at = pose.last();

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        float breath = .8F + .2F * Mth.sin(age * .4F);
        VisualDraw.disc(glow, at, radius * 1.3F, VisualDraw.LIFT, 0, VisualDraw.glow(shown.colour(), strength * .7F * breath));
        if (age < FLASH) VisualDraw.disc(glow, at, radius * (1 + age / FLASH), .4F, 0, VisualDraw.glow(shown.accent(), 1 - age / FLASH));

        VertexConsumer rim = VisualDraw.light(buffers, VisualDraw.BAND);
        VisualDraw.ring(rim, at, radius * .85F, radius * 1.08F, VisualDraw.LIFT * 2, 6, age * .05F, 0, VisualDraw.glow(shown.accent(), strength));

        VertexConsumer fire = VisualDraw.light(buffers, VisualDraw.FLAMES);
        wall(fire, at, shown, radius, tall, age * .09F, 0, strength);
        wall(fire, at, shown, radius * .72F, tall * .65F, age * .12F, 2.1F, strength * .8F);

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        Random random = new Random(shown.seed());
        for (int ember = 0; ember < EMBERS; ember++) {
            float angle = random.nextFloat() * Mth.TWO_PI, from = .5F + .55F * random.nextFloat(), period = 18 + 18 * random.nextFloat();
            float phase = random.nextFloat(), size = .1F + .12F * random.nextFloat(), swirl = (random.nextFloat() - .5F) * 2;
            float t = (age / period + phase) % 1, turned = angle + swirl * t * 2, out = radius * from * (1 + .3F * t);
            int colour = VisualDraw.glow(VisualDraw.mix(shown.accent(), shown.colour(), t), strength * Mth.sin(t * Mth.PI));
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(turned) * out, t * tall * 1.6F, Mth.sin(turned) * out, size, size, 0, VisualDraw.EMBER, colour);
        }
    }

    /** A ring of flame: each piece of it as tall as its tongues are there and then, white-hot at its foot, dark at its tips. */
    private static void wall(VertexConsumer fire, PoseStack.Pose at, Shown shown, float radius, float tall, float rise, float phase, float strength) {
        int foot = VisualDraw.glow(shown.accent(), strength), middle = VisualDraw.glow(shown.colour(), strength * .8F);
        for (int piece = 0; piece < PIECES; piece++) {
            float a0 = Mth.TWO_PI * piece / PIECES, a1 = Mth.TWO_PI * (piece + 1) / PIECES;
            float h0 = tall * tongue(a0, shown.age(), phase), h1 = tall * tongue(a1, shown.age(), phase);
            float x0 = Mth.cos(a0) * radius, z0 = Mth.sin(a0) * radius, x1 = Mth.cos(a1) * radius, z1 = Mth.sin(a1) * radius;
            float u0 = 5F * piece / PIECES, u1 = 5F * (piece + 1) / PIECES;
            // The lower half from the hot foot to the flame's own colour, the upper from that to nothing.
            VisualDraw.quad(fire, at,
                    x0, 0, z0, u0, rise + 1, foot,
                    x0, h0 / 2, z0, u0, rise + .5F, middle,
                    x1, h1 / 2, z1, u1, rise + .5F, middle,
                    x1, 0, z1, u1, rise + 1, foot);
            VisualDraw.quad(fire, at,
                    x0, h0 / 2, z0, u0, rise + .5F, middle,
                    x0, h0, z0, u0, rise, 0,
                    x1, h1, z1, u1, rise, 0,
                    x1, h1 / 2, z1, u1, rise + .5F, middle);
        }
    }

    /** How tall the flames stand at this point round the ring, as a share of the whole: two waves running round it. */
    private static float tongue(float angle, float age, float phase) {
        return .55F + .25F * Mth.sin(angle * 5 + age * .21F + phase) + .2F * Mth.sin(angle * 9 - age * .33F + phase * 2);
    }
}
