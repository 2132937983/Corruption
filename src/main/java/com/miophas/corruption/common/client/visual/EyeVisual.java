package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * An eye opening in the air, as the all-seeing eye is drawn: high over the spot, always turned to whoever looks at
 * it, its lids part; its iris glances about, turning, then fixes on the one who looks; it blinks once; rays of
 * glory and a triangle of light stand round it, as round the Eye of Providence; a searching beam of its light
 * sweeps the ground below; and at the end it closes.
 */
public final class EyeVisual implements SpellVisual {
    /** Slices the eye is drawn in, across; rays round it. */
    private static final int SLICES = 16, RAYS = 12;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), progress = shown.progress();
        float wide = Math.max(.6F, shown.radius() * .9F), tall = wide * .45F;
        float open = VisualDraw.easeOutBack(VisualDraw.within(age, 2, 10)) * (1 - VisualDraw.within(progress, .8F, .2F));
        float blink = Math.abs(age - shown.lifetime() * .5F) / 3;
        if (blink < 1) open *= .1F + .9F * blink;
        if (strength <= 0 || open <= 0) return;
        PoseStack.Pose at = pose.last();
        // High enough that its rays of glory, the longest reaching nearly twice its width, stay clear of the ground.
        float middle = Math.max(shown.height(), wide * 2 + 1);
        Vector3f right = shown.right(), up = shown.up(), forward = shown.forward();

        // The beam of its sight sweeping the ground, and where it falls.
        float searchX = Mth.cos(age * .06F) * shown.radius() * .6F, searchZ = Mth.sin(age * .06F) * shown.radius() * .6F;
        VertexConsumer beam = VisualDraw.light(buffers, VisualDraw.STREAKS);
        int pieces = 16;
        float spot = Math.max(.8F, shown.radius() * .3F);
        for (int piece = 0; piece < pieces; piece++) {
            float a0 = Mth.TWO_PI * piece / pieces, a1 = Mth.TWO_PI * (piece + 1) / pieces;
            int top = VisualDraw.glow(shown.colour(), strength * open * .25F), foot = VisualDraw.glow(shown.colour(), strength * open * .1F);
            VisualDraw.quad(beam, at,
                    0, middle, 0, (float) piece / pieces, age * .05F, top,
                    searchX + Mth.cos(a0) * spot, 0, searchZ + Mth.sin(a0) * spot, (float) piece / pieces, 1 + age * .05F, foot,
                    searchX + Mth.cos(a1) * spot, 0, searchZ + Mth.sin(a1) * spot, (float) (piece + 1) / pieces, 1 + age * .05F, foot,
                    0, middle, 0, (float) (piece + 1) / pieces, age * .05F, top);
        }

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        Vector3f east = new Vector3f(1, 0, 0), south = new Vector3f(0, 0, 1);
        VisualDraw.discIn(glow, at, searchX, VisualDraw.LIFT, searchZ, east, south, spot * 1.4F, 0, VisualDraw.glow(shown.accent(), strength * open * .5F));

        // Where the iris looks: glancing about, then fixed on whoever looks at it.
        float settle = 1 - VisualDraw.within(age, 22, 10);
        float lookX = wide * .3F * Mth.sin(age * .11F) * settle, lookY = tall * .2F * Mth.sin(age * .17F) * settle, irisSize = tall * .8F;
        // Its white, between its lids, brightest in the middle, with a hole where the iris is (light cannot darken a pupil).
        VertexConsumer white = VisualDraw.light(buffers, VisualDraw.SCLERA);
        for (int slice = 0; slice < SLICES; slice++) {
            float x0 = -1 + 2F * slice / SLICES, x1 = -1 + 2F * (slice + 1) / SLICES;
            int c0 = VisualDraw.glow(shown.colour(), strength * (.65F - .35F * x0 * x0)), c1 = VisualDraw.glow(shown.colour(), strength * (.65F - .35F * x1 * x1));
            slice(white, at, right, up, middle, wide, tall, open, x0, x1, c0, c1, lookX, lookY, irisSize, 0);
        }

        // The iris, cut by the lids as the white is: its texture placed by where in the eye each corner is.
        VertexConsumer iris = VisualDraw.light(buffers, VisualDraw.IRIS);
        float turn = age * .02F;
        int irisColour = VisualDraw.glow(shown.accent(), strength);
        for (int slice = 0; slice < SLICES; slice++) {
            float x0 = -1 + 2F * slice / SLICES, x1 = -1 + 2F * (slice + 1) / SLICES;
            slice(iris, at, right, up, middle, wide, tall, open, x0, x1, irisColour, irisColour, lookX, lookY, irisSize, turn);
        }

        VertexConsumer lines = VisualDraw.light(buffers, VisualDraw.BEAM);
        int lid = VisualDraw.glow(shown.accent(), strength), faint = VisualDraw.glow(shown.colour(), strength * open * .5F);
        for (int slice = 0; slice < SLICES; slice++) {
            float x0 = -1 + 2F * slice / SLICES, x1 = -1 + 2F * (slice + 1) / SLICES;
            for (int which = -1; which <= 1; which += 2) {
                float y0 = lid(x0, which) * tall * open, y1 = lid(x1, which) * tall * open;
                VisualDraw.beam(lines, at, forward,
                        right.x * x0 * wide + up.x * y0, middle + right.y * x0 * wide + up.y * y0, right.z * x0 * wide + up.z * y0,
                        right.x * x1 * wide + up.x * y1, middle + right.y * x1 * wide + up.y * y1, right.z * x1 * wide + up.z * y1, .14F, lid, lid);
            }
        }
        float glory = Math.min(1, open);
        for (int ray = 0; ray < RAYS; ray++) {
            float angle = Mth.TWO_PI * ray / RAYS + age * .01F, from = wide * 1.15F, to = wide * (ray % 2 == 0 ? 1.9F : 1.5F) * glory;
            float dx = Mth.cos(angle), dy = Mth.sin(angle);
            VisualDraw.beam(lines, at, forward,
                    (right.x * dx + up.x * dy) * from, middle + (right.y * dx + up.y * dy) * from, (right.z * dx + up.z * dy) * from,
                    (right.x * dx + up.x * dy) * to, middle + (right.y * dx + up.y * dy) * to, (right.z * dx + up.z * dy) * to, .2F, faint, 0);
        }
        for (int corner = 0; corner < 3; corner++) {
            float a0 = Mth.HALF_PI + Mth.TWO_PI * corner / 3, a1 = Mth.HALF_PI + Mth.TWO_PI * (corner + 1) / 3, size = wide * 1.35F * glory;
            float dx0 = Mth.cos(a0) * size, dy0 = Mth.sin(a0) * size, dx1 = Mth.cos(a1) * size, dy1 = Mth.sin(a1) * size;
            VisualDraw.beam(lines, at, forward,
                    right.x * dx0 + up.x * dy0, middle + right.y * dx0 + up.y * dy0, right.z * dx0 + up.z * dy0,
                    right.x * dx1 + up.x * dy1, middle + right.y * dx1 + up.y * dy1, right.z * dx1 + up.z * dy1, .12F, lid, lid);
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        float glintX = lookX - irisSize * .35F, glintY = Math.min(lookY + irisSize * .35F, lid(glintX / wide, 1) * tall * open);
        VisualDraw.sprite(sprites, at, right, up, right.x * glintX + up.x * glintY, middle + right.y * glintX + up.y * glintY, right.z * glintX + up.z * glintY,
                tall * .5F, tall * .5F, age * .05F, VisualDraw.TWINKLE, VisualDraw.glow(0xFFFFFF, strength * Math.min(1, open)));
    }

    /** How high a lid is across the eye, from one corner (x -1) to the other (1): the upper (1) or the lower (-1), at its widest open. */
    private static float lid(float x, int which) {
        float curve = (float) Math.pow(Math.max(0, 1 - x * x), .8);
        return which > 0 ? curve : -.85F * curve;
    }

    /**
     * One slice of the eye, from x0 to x1 across it, between its lids. Its texture is placed by where in the eye
     * each corner is: the whole texture is the square {@code size} round the point x, y of the eye, turned
     * {@code turn} radians.
     */
    private static void slice(VertexConsumer buffer, PoseStack.Pose at, Vector3f right, Vector3f up, float middle, float wide, float tall, float open,
                              float x0, float x1, int c0, int c1, float x, float y, float size, float turn) {
        float[] across = {x0, x0, x1, x1}, lids = {lid(x0, -1), lid(x0, 1), lid(x1, 1), lid(x1, -1)};
        float[] corner = new float[20];
        for (int at4 = 0; at4 < 4; at4++) {
            float cx = across[at4] * wide, cy = lids[at4] * tall * open;
            corner[at4 * 5] = right.x * cx + up.x * cy;
            corner[at4 * 5 + 1] = middle + right.y * cx + up.y * cy;
            corner[at4 * 5 + 2] = right.z * cx + up.z * cy;
            float dx = (cx - x) / size, dy = (cy - y) / size, c = Mth.cos(turn), s = Mth.sin(turn);
            corner[at4 * 5 + 3] = .5F + (dx * c - dy * s) * .5F;
            corner[at4 * 5 + 4] = .5F - (dx * s + dy * c) * .5F;
        }
        VisualDraw.quad(buffer, at,
                corner[0], corner[1], corner[2], corner[3], corner[4], c0,
                corner[5], corner[6], corner[7], corner[8], corner[9], c0,
                corner[10], corner[11], corner[12], corner[13], corner[14], c1,
                corner[15], corner[16], corner[17], corner[18], corner[19], c1);
    }
}
