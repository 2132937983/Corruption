package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * A crown of light, as a king of the spirits is drawn revealing himself: a crown descends out of the air over the
 * spot and settles, turning, its points faceted and glowing, a gem at the foot of each; rays of glory stand out
 * round it; motes of light fall from it like gold dust; and a royal circle turns on the ground below.
 */
public final class CrownVisual implements SpellVisual {
    /** Ticks it takes to come down; rays of glory round it; motes falling. */
    private static final float DESCENDING = 12;
    private static final int RAYS = 16, MOTES = 18;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = Math.max(1, shown.radius());
        if (strength <= 0) return;
        PoseStack.Pose at = pose.last();
        float rest = Math.max(1.2F, shown.height()), y = rest + 2.5F * (1 - VisualDraw.easeOutBack(age / DESCENDING));
        float band = Mth.clamp(radius * .25F, .5F, 1.4F), turn = (float) Math.toRadians(age * 1.2F);
        int points = Mth.clamp(Math.round(radius * 2) + 5, 7, 12);

        VertexConsumer circle = VisualDraw.light(buffers, VisualDraw.CIRCLE_OUTER);
        VisualDraw.disc(circle, at, radius * VisualDraw.easeOutBack(age / DESCENDING), VisualDraw.LIFT, age * .7F, VisualDraw.glow(shown.colour(), strength * .6F));

        VertexConsumer ring = VisualDraw.light(buffers, VisualDraw.BAND);
        int gold = VisualDraw.glow(shown.accent(), strength), deep = VisualDraw.glow(shown.colour(), strength * .8F);
        VisualDraw.wall(ring, at, band, y, y + band * .35F, 6, 0, 1, deep, gold);

        VertexConsumer facets = VisualDraw.light(buffers, VisualDraw.FACET);
        for (int point = 0; point < points; point++) {
            float a0 = Mth.TWO_PI * point / points + turn, a1 = Mth.TWO_PI * (point + 1) / points + turn, middle = (a0 + a1) / 2;
            float tall = band * (point % 2 == 0 ? .95F : .6F), foot = y + band * .35F;
            VisualDraw.triangle(facets, at,
                    Mth.cos(a0) * band, foot, Mth.sin(a0) * band, 0, 1, deep,
                    Mth.cos(a1) * band, foot, Mth.sin(a1) * band, 1, 1, deep,
                    Mth.cos(middle) * band * 1.08F, foot + tall, Mth.sin(middle) * band * 1.08F, .5F, 0, gold);
        }

        VertexConsumer rays = VisualDraw.light(buffers, VisualDraw.BEAM);
        Vector3f right = shown.right(), up = shown.up(), forward = shown.forward();
        float middle = y + band * .5F, glory = VisualDraw.easeOutCubic(VisualDraw.within(age, DESCENDING * .5F, 10));
        for (int ray = 0; ray < RAYS; ray++) {
            // Never so long that one pointing down would reach the ground.
            float angle = Mth.TWO_PI * ray / RAYS + age * .015F, from = band * 1.3F;
            float to = Math.min(band * 1.3F + radius * (ray % 2 == 0 ? .9F : .6F) * glory, middle - .3F);
            float dx = Mth.cos(angle), dy = Mth.sin(angle);
            VisualDraw.beam(rays, at, forward,
                    (right.x * dx + up.x * dy) * from, middle + (right.y * dx + up.y * dy) * from, (right.z * dx + up.z * dy) * from,
                    (right.x * dx + up.x * dy) * to, middle + (right.y * dx + up.y * dy) * to, (right.z * dx + up.z * dy) * to, .25F,
                    VisualDraw.glow(shown.accent(), strength * .5F * glory), 0);
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        for (int point = 0; point < points; point++) {
            float angle = Mth.TWO_PI * (point + .5F) / points + turn, twinkle = .8F + .2F * Mth.sin(age * .5F + point);
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle) * band * 1.02F, y + band * .18F, Mth.sin(angle) * band * 1.02F, .25F * twinkle, .32F * twinkle,
                    0, VisualDraw.GEM, VisualDraw.glow(VisualDraw.mix(shown.accent(), 0xFFFFFF, .3F), strength));
        }
        for (int mote = 0; mote < MOTES; mote++) {
            float t = (age / 22 + VisualDraw.hash(shown.seed(), mote, 0)) % 1, angle = VisualDraw.hash(shown.seed(), mote, 1) * Mth.TWO_PI;
            float far = band * (.6F + .8F * VisualDraw.hash(shown.seed(), mote, 2));
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle) * far, y * (1 - t), Mth.sin(angle) * far, .16F, .16F, 0, VisualDraw.TWINKLE,
                    VisualDraw.glow(shown.accent(), strength * Mth.sin(t * Mth.PI) * .8F));
        }
    }
}
