package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * One ring of a crescendo, as a herald's call swelling out is drawn: a staff of music racing out over the ground from
 * the spot at an even pace, out to the effect's radius over all but the last four ticks of its life (so a skill that
 * hurts what its ring passes can keep pace with it, ring for ring), notes riding its lines and lifting off them behind
 * it; a band of light on the ground at its foot, and a fainter echo of it following.
 */
public final class CrescendoVisual implements SpellVisual {
    /** How many ticks at the end of its life the ring has stopped and fades. */
    private static final float FADING = 4;
    /** Pieces round the ring. */
    private static final int PIECES = 48;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = Math.max(0, shown.radius());
        if (strength <= 0) return;
        float front = radius * Mth.clamp(age / Math.max(1, shown.lifetime() - FADING), 0, 1);
        PoseStack.Pose at = pose.last();
        float staffFoot = .5F, staffTall = Mth.clamp(shown.height() * .35F, .6F, 1.6F);

        VertexConsumer band = VisualDraw.light(buffers, VisualDraw.BAND);
        VisualDraw.ring(band, at, Math.max(0, front - .7F), front + .15F, VisualDraw.LIFT * 2, 6, age * .03F, VisualDraw.glow(shown.colour(), strength * .4F),
                VisualDraw.glow(shown.accent(), strength));
        float echo = front * .82F;
        VisualDraw.ring(band, at, Math.max(0, echo - .35F), echo, VisualDraw.LIFT * 3, 6, -age * .02F, 0, VisualDraw.glow(shown.colour(), strength * .45F));

        VertexConsumer staff = VisualDraw.light(buffers, VisualDraw.STAFF);
        int lines = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), .45F), strength);
        for (int piece = 0; piece < PIECES; piece++) {
            float a0 = Mth.TWO_PI * piece / PIECES, a1 = Mth.TWO_PI * (piece + 1) / PIECES;
            float y0 = staffFoot + .18F * Mth.sin(a0 * 5 + age * .35F), y1 = staffFoot + .18F * Mth.sin(a1 * 5 + age * .35F);
            float u0 = 12F * piece / PIECES, u1 = 12F * (piece + 1) / PIECES;
            VisualDraw.quad(staff, at,
                    Mth.cos(a0) * front, y0, Mth.sin(a0) * front, u0, 1, lines,
                    Mth.cos(a0) * front, y0 + staffTall, Mth.sin(a0) * front, u0, 0, lines,
                    Mth.cos(a1) * front, y1 + staffTall, Mth.sin(a1) * front, u1, 0, lines,
                    Mth.cos(a1) * front, y1, Mth.sin(a1) * front, u1, 1, lines);
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        int notes = Mth.clamp(Math.round(radius * 1.2F), 6, 28);
        for (int note = 0; note < notes; note++) {
            float angle = Mth.TWO_PI * (note + VisualDraw.hash(shown.seed(), note, 1) * .6F) / notes;
            float line = VisualDraw.hash(shown.seed(), note, 2);
            int which = VisualDraw.NOTE + (int) (VisualDraw.hash(shown.seed(), note, 3) * 4) % 4;
            // Each note rides the staff, then lifts off it a little behind the front.
            float riding = front * (.92F + .08F * VisualDraw.hash(shown.seed(), note, 4));
            float lifted = Math.max(0, age - shown.lifetime() * .3F) * .05F;
            float y = staffFoot + .18F * Mth.sin(angle * 5 + age * .35F) + staffTall * (.15F + .7F * line) + lifted;
            float size = .7F, sway = .3F * Mth.sin(age * .3F + note);
            VisualDraw.sprite(sprites, at, right, up, Mth.cos(angle) * riding, y, Mth.sin(angle) * riding, size, size, sway, which,
                    VisualDraw.glow(shown.accent(), strength));
        }
    }
}
