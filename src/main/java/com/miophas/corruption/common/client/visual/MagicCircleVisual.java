package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

/**
 * A magic circle, as the films draw a sorcerer's: it flings itself open on the ground, a little past its size and
 * back; its outer ring of runes turns one way and the sigil inside it the other, faster; a smaller echo of the
 * ring rises off it and climbs as the spell goes on; and a curtain of light stands up round its rim, rising with it.
 */
public final class MagicCircleVisual implements SpellVisual {
    /** Ticks it takes to open. */
    private static final float OPENING = 10;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age();
        float radius = shown.radius() * VisualDraw.easeOutBack(age / OPENING);
        if (radius <= 0 || strength <= 0) return;
        PoseStack.Pose at = pose.last();
        float spin = age * 1.6F + shown.yaw();
        // The echo rises a little over a block, or the effect's height if that is less.
        float rise = Math.min(1.4F, Math.max(.4F, shown.height() * .35F)) * VisualDraw.easeOutCubic(shown.progress() * 1.5F);

        VertexConsumer outer = VisualDraw.light(buffers, VisualDraw.CIRCLE_OUTER);
        VisualDraw.disc(outer, at, radius, VisualDraw.LIFT, spin, VisualDraw.glow(shown.colour(), strength));
        VisualDraw.disc(outer, at, radius * .55F, VisualDraw.LIFT + rise, -spin * 2.2F, VisualDraw.glow(shown.accent(), strength * .7F));

        VertexConsumer inner = VisualDraw.light(buffers, VisualDraw.CIRCLE_INNER);
        VisualDraw.disc(inner, at, radius * .72F, VisualDraw.LIFT * 2, -spin * 1.5F, VisualDraw.glow(shown.accent(), strength));
        // A pulse of the sigil, breathing in and out.
        float breath = .5F + .5F * Mth.sin(age * .25F);
        VisualDraw.disc(inner, at, radius * (.3F + .08F * breath), VisualDraw.LIFT * 3, spin * 3, VisualDraw.glow(shown.colour(), strength * (.4F + .4F * breath)));

        VertexConsumer curtain = VisualDraw.light(buffers, VisualDraw.STREAKS);
        float scroll = -age * .04F;
        VisualDraw.wall(curtain, at, radius * .98F, 0, (.6F + rise) * strength, 3, scroll, scroll + 1,
                VisualDraw.glow(shown.colour(), strength * .55F), 0);
    }
}
