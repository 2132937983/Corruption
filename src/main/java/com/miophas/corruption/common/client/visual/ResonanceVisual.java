package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * Music made visible, as a bard's or a herald's spell is drawn: on every beat a ring of sound pulses out from the
 * spot, a staff of music standing round it and swaying as it goes, notes riding its lines and then floating up off
 * them; at every beat the ground flashes and a ripple runs out over it.
 */
public final class ResonanceVisual implements SpellVisual {
    /** Ticks between beats, and how long each ring of sound lasts. */
    private static final float BEAT = 10, RING = 24;
    /** Notes riding each ring; pieces round a ring. */
    private static final int NOTES = 6, PIECES = 40;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = Math.max(1, shown.radius());
        if (strength <= 0) return;
        PoseStack.Pose at = pose.last();
        int last = (int) (age / BEAT), first = Math.max(0, last - (int) (RING / BEAT));
        float staffFoot = .6F, staffTall = Mth.clamp(shown.height() * .3F, .6F, 1.4F);

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        float pulse = 1 - (age % BEAT) / BEAT;
        VisualDraw.disc(glow, at, 1.5F + pulse, VisualDraw.LIFT, 0, VisualDraw.glow(shown.accent(), strength * pulse * pulse));

        VertexConsumer ripple = VisualDraw.light(buffers, VisualDraw.BAND);
        for (int beat = first; beat <= last; beat++) {
            float since = age - beat * BEAT, gone = since / RING;
            if (gone >= 1) continue;
            float reach = .6F + (radius - .6F) * VisualDraw.easeOutCubic(gone);
            VisualDraw.ring(ripple, at, Math.max(0, reach - .5F), reach, VisualDraw.LIFT * 2, 6, 0, 0, VisualDraw.glow(shown.colour(), strength * (1 - gone) * .7F));
        }

        VertexConsumer staff = VisualDraw.light(buffers, VisualDraw.STAFF);
        for (int beat = first; beat <= last; beat++) {
            float since = age - beat * BEAT, gone = since / RING;
            if (gone >= 1) continue;
            float reach = .6F + (radius - .6F) * VisualDraw.easeOutCubic(gone);
            int colour = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), .4F), strength * (1 - gone));
            for (int piece = 0; piece < PIECES; piece++) {
                float a0 = Mth.TWO_PI * piece / PIECES, a1 = Mth.TWO_PI * (piece + 1) / PIECES;
                float y0 = staffFoot + .2F * Mth.sin(a0 * 4 + since * .4F + beat), y1 = staffFoot + .2F * Mth.sin(a1 * 4 + since * .4F + beat);
                float u0 = 8F * piece / PIECES, u1 = 8F * (piece + 1) / PIECES;
                VisualDraw.quad(staff, at,
                        Mth.cos(a0) * reach, y0, Mth.sin(a0) * reach, u0, 1, colour,
                        Mth.cos(a0) * reach, y0 + staffTall, Mth.sin(a0) * reach, u0, 0, colour,
                        Mth.cos(a1) * reach, y1 + staffTall, Mth.sin(a1) * reach, u1, 0, colour,
                        Mth.cos(a1) * reach, y1, Mth.sin(a1) * reach, u1, 1, colour);
            }
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        for (int beat = first; beat <= last; beat++) {
            float since = age - beat * BEAT, gone = since / RING;
            if (gone >= 1) continue;
            float reach = .6F + (radius - .6F) * VisualDraw.easeOutCubic(gone);
            for (int note = 0; note < NOTES; note++) {
                float angle = VisualDraw.hash(shown.seed(), beat, note) * Mth.TWO_PI, line = VisualDraw.hash(shown.seed(), beat, note + 100);
                int which = VisualDraw.NOTE + (int) (VisualDraw.hash(shown.seed(), beat, note + 200) * 4) % 4;
                float floating = Math.max(0, since - RING * .5F) * .07F;
                float y = staffFoot + .2F * Mth.sin(angle * 4 + since * .4F + beat) + staffTall * (.2F + .6F * line) + floating;
                float size = .75F, sway = .3F * Mth.sin(since * .25F + note);
                VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle) * reach, y, Mth.sin(angle) * reach, size, size, sway, which,
                        VisualDraw.glow(shown.accent(), strength * (1 - gone)));
            }
        }
    }
}
