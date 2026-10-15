package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

/**
 * A pillar of light come down from the heavens, as a god's beam or a summoning bridge is in the films: it drives
 * down from its full height to the ground in a few ticks, a white-hot core inside a broad glow, streaks of light
 * pouring down it; where it meets the ground it spreads in a pool of light; at the end it narrows to nothing.
 */
public final class LightPillarVisual implements SpellVisual {
    /** Ticks it takes to reach the ground. */
    private static final float DESCENT = 6;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age();
        float top = Math.max(4, shown.height());
        float bottom = top * (1 - VisualDraw.easeOutCubic(age / DESCENT));
        float flicker = .85F + .15F * Mth.sin(age * 1.7F) * Mth.sin(age * .53F + 1);
        float outer = shown.radius() * (.55F + .45F * strength) * flicker, core = outer * .35F;
        if (outer <= 0) return;
        PoseStack.Pose at = pose.last();
        float pour = age * .12F;

        VertexConsumer streaks = VisualDraw.light(buffers, VisualDraw.STREAKS);
        VisualDraw.wall(streaks, at, outer, bottom, top, 4, pour, pour + top / 6, VisualDraw.glow(shown.colour(), strength), VisualDraw.glow(shown.colour(), strength * .25F));
        VisualDraw.wall(streaks, at, outer * .7F, bottom, top, 3, -pour * .7F, -pour * .7F + top / 8, VisualDraw.glow(shown.colour(), strength * .6F), 0);
        VisualDraw.wall(streaks, at, core, bottom, top, 2, pour * 1.6F, pour * 1.6F + top / 4, VisualDraw.glow(shown.accent(), strength), VisualDraw.glow(0xFFFFFF, strength * .5F));

        if (bottom > .5F) return;
        // Where it meets the ground.
        VertexConsumer pool = VisualDraw.light(buffers, VisualDraw.GLOW);
        VisualDraw.disc(pool, at, shown.radius() * 2.2F * strength, VisualDraw.LIFT, 0, VisualDraw.glow(shown.colour(), strength * .8F));
        VisualDraw.disc(pool, at, shown.radius() * 1.1F * strength, VisualDraw.LIFT * 2, 0, VisualDraw.glow(shown.accent(), strength));
    }
}
