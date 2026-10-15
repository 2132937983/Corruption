package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Runes in the air, as a spell being woven is drawn: glowing glyphs blink into being one after another round the
 * spot, circle it and climb as the spell goes on, each bobbing on its own; a second ring of them, smaller and
 * lower, turns the other way. Each glyph faces outward, and is seen from both sides.
 */
public final class RuneRingVisual implements SpellVisual {
    /** Glyphs on the runes texture: four by four. */
    private static final int GLYPHS = 16, ACROSS = 4;
    /** Ticks between one glyph appearing and the next. */
    private static final float STAGGER = 1.5F;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength();
        if (strength <= 0 || shown.radius() <= 0) return;
        int count = Mth.clamp(Math.round(shown.radius() * 3), 6, 18);
        RandomSource random = RandomSource.create(shown.seed());
        PoseStack.Pose at = pose.last();
        VertexConsumer runes = VisualDraw.light(buffers, VisualDraw.RUNES);
        float climb = Math.max(.6F, shown.height()) * VisualDraw.easeOutCubic(shown.progress());
        ring(runes, at, shown, random, count, shown.radius(), .6F + climb * .6F, 2.2F, .8F, shown.accent(), strength);
        ring(runes, at, shown, random, Math.max(4, count * 2 / 3), shown.radius() * .6F, .3F + climb * .25F, -3.4F, .55F, shown.colour(), strength * .85F);
    }

    private static void ring(VertexConsumer buffer, PoseStack.Pose at, Shown shown, RandomSource random, int count, float radius, float height,
                             float degreesPerTick, float size, int colour, float strength) {
        float age = shown.age();
        for (int rune = 0; rune < count; rune++) {
            int glyph = random.nextInt(GLYPHS);
            float bob = random.nextFloat() * Mth.TWO_PI;
            float appear = Mth.clamp((age - rune * STAGGER) / 4, 0, 1);
            if (appear <= 0) continue;
            float angle = Mth.TWO_PI * rune / count + (float) Math.toRadians(age * degreesPerTick + shown.yaw());
            float x = Mth.cos(angle) * radius, z = Mth.sin(angle) * radius, y = height + .2F * Mth.sin(age * .15F + bob);
            // Facing outward: the glyph lies across the circle's tangent.
            float tx = -Mth.sin(angle) * size * .5F, tz = Mth.cos(angle) * size * .5F, half = size * .5F;
            float u0 = (float) (glyph % ACROSS) / ACROSS, v0 = (float) (glyph / ACROSS) / ACROSS, u1 = u0 + 1F / ACROSS, v1 = v0 + 1F / ACROSS;
            float flicker = .8F + .2F * Mth.sin(age * .9F + bob * 3);
            int c = VisualDraw.glow(colour, strength * appear * flicker);
            VisualDraw.quad(buffer, at,
                    x - tx, y + half, z - tz, u0, v0, c,
                    x - tx, y - half, z - tz, u0, v1, c,
                    x + tx, y - half, z + tz, u1, v1, c,
                    x + tx, y + half, z + tz, u1, v0, c);
        }
    }
}
