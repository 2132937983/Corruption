package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * A shockwave, as an impact in an anime is drawn: a flash where it struck, then a ring of force racing out over
 * the ground, fast at first and slowing, thinning and dimming as it goes; a second, brighter ring outrunning it;
 * and a wall of light riding the first ring, as tall as the effect, sinking as it spreads.
 */
public final class ShockwaveVisual implements SpellVisual {
    /** Ticks the flash lasts. */
    private static final float FLASH = 5;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float progress = shown.progress(), fading = 1 - progress, age = shown.age();
        float reach = shown.radius() * VisualDraw.easeOutCubic(progress);
        float fast = shown.radius() * 1.15F * VisualDraw.easeOutCubic(Math.min(1, progress * 1.7F));
        float width = Math.max(.35F, shown.radius() * .3F * fading);
        PoseStack.Pose at = pose.last();

        VertexConsumer band = VisualDraw.light(buffers, VisualDraw.BAND);
        VisualDraw.ring(band, at, Math.max(0, reach - width), reach + width * .25F, VisualDraw.LIFT, 6, age * .02F,
                VisualDraw.glow(shown.colour(), fading * .6F), VisualDraw.glow(shown.colour(), fading));
        VisualDraw.ring(band, at, Math.max(0, fast - width * .35F), fast, VisualDraw.LIFT * 2, 8, -age * .03F,
                0, VisualDraw.glow(shown.accent(), fading * fading));
        float wall = Math.max(.5F, shown.height()) * fading;
        VisualDraw.wall(band, at, reach, 0, wall, 6, 0, 1, VisualDraw.glow(shown.colour(), fading * .8F), VisualDraw.glow(shown.accent(), fading * .3F));

        if (age < FLASH) {
            VertexConsumer flash = VisualDraw.light(buffers, VisualDraw.GLOW);
            float bright = 1 - age / FLASH;
            VisualDraw.disc(flash, at, shown.radius() * .45F * (1 + age / FLASH), VisualDraw.LIFT * 3, 0, VisualDraw.glow(shown.accent(), bright));
            VisualDraw.disc(flash, at, shown.radius() * .3F, .5F, 45, VisualDraw.glow(0xFFFFFF, bright * .8F));
        }
    }
}
