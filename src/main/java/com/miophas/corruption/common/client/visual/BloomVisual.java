package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * A flower of light blooming on the ground, as a healer's lotus is drawn: a bud of three rings of petals opens, the
 * outer ones lying back almost flat, the inner standing up round a glowing heart, the whole turning slowly; light
 * rises out of its heart, and pollen of light spirals up from it; as it fades it closes a little.
 */
public final class BloomVisual implements SpellVisual {
    /** Each ring of petals: how many, how long as a share of the radius, how far it lies back when open (radians above the ground). */
    private static final int[] PETALS = {8, 8, 6};
    private static final float[] LENGTH = {1, .72F, .45F}, OPEN = {.3F, .75F, 1.2F};
    /** Ticks it takes to open; motes of pollen. */
    private static final float OPENING = 16;
    private static final int POLLEN = 30;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();
        float open = VisualDraw.easeOutBack(age / OPENING) * (1 - .5F * VisualDraw.within(shown.progress(), .8F, .2F));
        float spin = (float) Math.toRadians(age * .4F), tall = Math.max(1.5F, shown.height());

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        VisualDraw.disc(glow, at, radius * 1.3F, VisualDraw.LIFT, 0, VisualDraw.glow(shown.colour(), strength * .5F));

        VertexConsumer light = VisualDraw.light(buffers, VisualDraw.STREAKS);
        float column = radius * .25F * Math.min(1, open);
        VisualDraw.wall(light, at, column, 0, tall * Math.min(1, open), 3, -age * .05F, -age * .05F + 1, VisualDraw.glow(shown.accent(), strength * .6F), 0);

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        for (int ring = 0; ring < PETALS.length; ring++) {
            float length = radius * LENGTH[ring], lean = Mth.lerp(Mth.clamp(open, 0, 1.1F), 1.5F, OPEN[ring]);
            int colour = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), ring / 2F), strength * (.75F + .1F * ring));
            for (int petal = 0; petal < PETALS[ring]; petal++) {
                float angle = Mth.TWO_PI * (petal + (ring % 2) * .5F) / PETALS[ring] + spin;
                float outX = Mth.cos(angle), outZ = Mth.sin(angle), base = length * .12F;
                float baseX = outX * base, baseZ = outZ * base, baseY = .05F + ring * .04F;
                float tipX = baseX + outX * Mth.cos(lean) * length, tipY = baseY + Mth.sin(lean) * length, tipZ = baseZ + outZ * Mth.cos(lean) * length;
                VisualDraw.spriteAlong(sprites, at, tipX, tipY, tipZ, baseX, baseY, baseZ, -outZ, 0, outX, length / 1.6F, VisualDraw.PETAL, colour);
            }
        }
        Vector3f right = shown.right(), up = shown.up();
        float heart = radius * .35F * (.9F + .1F * Mth.sin(age * .3F)) * Math.min(1, open);
        VisualDraw.sprite(sprites, at, right, up, 0, Math.max(.35F, heart * .55F), 0, heart, heart, 0, VisualDraw.EMBER, VisualDraw.glow(shown.accent(), strength));
        for (int mote = 0; mote < POLLEN; mote++) {
            float t = (age / (24 + 16 * VisualDraw.hash(shown.seed(), mote, 0)) + VisualDraw.hash(shown.seed(), mote, 1)) % 1;
            float angle = VisualDraw.hash(shown.seed(), mote, 2) * Mth.TWO_PI + t * 4, far = radius * .4F * (.3F + t) * VisualDraw.hash(shown.seed(), mote, 3);
            if (age < OPENING * .5F) continue;
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle) * far, .3F + t * tall * 1.2F, Mth.sin(angle) * far, .16F, .16F, 0, VisualDraw.TWINKLE,
                    VisualDraw.glow(shown.accent(), strength * Mth.sin(t * Mth.PI)));
        }
    }
}
