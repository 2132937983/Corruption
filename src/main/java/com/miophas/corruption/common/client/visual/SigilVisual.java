package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * A spirit's seal traced in the air before the caster, as a sorcerer draws one with a burning finger: a ring of
 * runes stands up in front of them, facing the way they look; a point of light traces the seal's strokes inside it,
 * one after another, the fresh line bright behind it; finished, the seal flares; at the end its strokes fly apart.
 * The seal is the spell's own: it is drawn from the spell's colours and size, so a spell always traces the same one.
 */
public final class SigilVisual implements SpellVisual {
    /** How far before the caster it stands. */
    private static final float BEFORE = 1.4F;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = Math.max(.5F, shown.radius());
        if (strength <= 0) return;
        PoseStack.Pose at = pose.last();
        float lookX = VisualDraw.lookX(shown.yaw()), lookZ = VisualDraw.lookZ(shown.yaw());
        // Across it is the caster's right hand, so the seal reads as it should from where they stand.
        Vector3f across = new Vector3f(-lookZ, 0, lookX), up = new Vector3f(0, 1, 0);
        float cx = lookX * BEFORE, cy = Math.max(shown.height(), radius * 1.35F + .2F), cz = lookZ * BEFORE;
        float opening = VisualDraw.easeOutBack(age / 8), scattering = VisualDraw.within(shown.progress(), .8F, .2F);

        VertexConsumer circle = VisualDraw.light(buffers, VisualDraw.CIRCLE_OUTER);
        VisualDraw.roundIn(circle, at, cx, cy, cz, across, up, radius * opening * (1 + scattering * .2F), age * 1.5F,
                VisualDraw.glow(shown.colour(), strength));

        VisualPaths.Seal seal = VisualPaths.seal(new Random(shown.colour() * 31L + shown.accent() * 1_000_003L + Float.floatToIntBits(shown.radius())));
        List<float[]> pieces = new ArrayList<>();
        float total = 0;
        for (float[] stroke : seal.strokes())
            for (int point = 0; point + 3 < stroke.length; point += 2) {
                float length = Mth.sqrt(Mth.square(stroke[point + 2] - stroke[point]) + Mth.square(stroke[point + 3] - stroke[point + 1]));
                pieces.add(new float[]{stroke[point], stroke[point + 1], stroke[point + 2], stroke[point + 3], total, length});
                total += length;
            }
        float tracing = VisualDraw.within(age, 4, shown.lifetime() * .45F), traced = total * tracing;
        float done = 4 + shown.lifetime() * .45F, flare = age >= done ? 1 - VisualDraw.within(age, done, 8) : 0;

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        VisualDraw.roundIn(glow, at, cx, cy, cz, across, up, radius * (1 + flare * .3F), 0,
                VisualDraw.glow(shown.accent(), strength * (.25F + .6F * flare)));

        VertexConsumer lines = VisualDraw.light(buffers, VisualDraw.BEAM);
        Vector3f forward = shown.forward();
        float scale = radius * .8F, width = Mth.clamp(radius * .06F, .07F, .22F);
        float[] pen = null;
        for (float[] piece : pieces) {
            if (piece[4] >= traced) break;
            float share = Math.min(1, (traced - piece[4]) / Math.max(1e-4F, piece[5]));
            float x0 = piece[0], y0 = piece[1], x1 = Mth.lerp(share, piece[0], piece[2]), y1 = Mth.lerp(share, piece[1], piece[3]);
            // Flying apart at the end: each piece out from the middle the way it lies.
            float outX = (x0 + x1) / 2 * scattering * .5F, outY = (y0 + y1) / 2 * scattering * .5F;
            float fresh = 1 - Math.min(1, (traced - piece[4] - piece[5] * share) / (total * .15F + 1e-4F));
            int colour = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), .4F + .6F * fresh), strength * (1 - scattering));
            float[] a = place(cx, cy, cz, across, (x0 + outX) * scale, (y0 + outY) * scale), b = place(cx, cy, cz, across, (x1 + outX) * scale, (y1 + outY) * scale);
            VisualDraw.beam(lines, at, forward, a[0], a[1], a[2], b[0], b[1], b[2], width, colour, colour);
            if (share < 1) pen = b;
        }
        float ringing = VisualDraw.within(tracing, .9F, .1F) * (1 - scattering);
        if (ringing > 0) {
            int colour = VisualDraw.glow(shown.accent(), strength * ringing);
            for (float[] ring : seal.rings())
                for (int side = 0; side < 12; side++) {
                    float a0 = Mth.TWO_PI * side / 12, a1 = Mth.TWO_PI * (side + 1) / 12;
                    float[] a = place(cx, cy, cz, across, (ring[0] + Mth.cos(a0) * ring[2]) * scale, (ring[1] + Mth.sin(a0) * ring[2]) * scale);
                    float[] b = place(cx, cy, cz, across, (ring[0] + Mth.cos(a1) * ring[2]) * scale, (ring[1] + Mth.sin(a1) * ring[2]) * scale);
                    VisualDraw.beam(lines, at, forward, a[0], a[1], a[2], b[0], b[1], b[2], width * .8F, colour, colour);
                }
        }

        if (pen == null) return;
        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        VisualDraw.sprite(sprites, at, shown.right(), shown.up(), pen[0], pen[1], pen[2], width * 6, width * 6, age * .3F, VisualDraw.TWINKLE,
                VisualDraw.glow(0xFFFFFF, strength));
    }

    /** A point of the seal, x across and y up from its middle, in the world. */
    private static float[] place(float cx, float cy, float cz, Vector3f across, float x, float y) {
        return new float[]{cx + across.x * x, cy + y, cz + across.z * x};
    }
}
