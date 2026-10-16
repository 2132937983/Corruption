package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * A rift torn open in the air, as a sorcerer's portal is drawn opening by a ring of sparks: before the caster, the
 * way they faced, a point of sparks spins out into a standing ring of them, racing round it in streaks, sparks
 * dripping off it and falling; inside it space whirls; at the end it spins shut.
 */
public final class RiftVisual implements SpellVisual {
    /** How far before the caster it stands; sparks in each of its rings; sparks dripping off it. */
    private static final float BEFORE = 1.2F;
    private static final int SPARKS = 40, DRIPS = 30;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = Math.max(.5F, shown.radius());
        float size = radius * VisualDraw.easeOutCubic(age / 10) * (1 - VisualDraw.easeOutCubic(VisualDraw.within(shown.progress(), .82F, .18F)));
        if (strength <= 0 || size <= 0) return;
        PoseStack.Pose at = pose.last();
        float lookX = VisualDraw.lookX(shown.yaw()), lookZ = VisualDraw.lookZ(shown.yaw());
        Vector3f across = new Vector3f(-lookZ, 0, lookX), up = new Vector3f(0, 1, 0);
        float cx = lookX * BEFORE, cy = Math.max(shown.height(), radius * 1.2F + .1F), cz = lookZ * BEFORE;

        VertexConsumer inside = VisualDraw.light(buffers, VisualDraw.SPIRAL);
        VisualDraw.roundIn(inside, at, cx, cy, cz, across, up, size * .95F, -age * 8, VisualDraw.glow(shown.colour(), strength * .45F));
        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        VisualDraw.roundIn(glow, at, cx, cy, cz, across, up, size * 1.15F, 0, VisualDraw.glow(shown.accent(), strength * .35F));

        VertexConsumer sparks = VisualDraw.light(buffers, VisualDraw.BEAM);
        Vector3f forward = shown.forward();
        for (int ring = 0; ring < 3; ring++) {
            float speed = (ring == 1 ? -1 : 1) * (.2F + .06F * ring), trail = .2F + .05F * ring;
            for (int spark = 0; spark < SPARKS; spark++) {
                float start = VisualDraw.hash(shown.seed(), ring, spark) * Mth.TWO_PI, off = 1 + (VisualDraw.hash(shown.seed(), ring, spark + 50) - .5F) * .12F;
                float angle = start + age * speed, behind = angle - trail * Math.signum(speed), r = size * off;
                float[] a = place(cx, cy, cz, across, Mth.cos(angle) * r, Mth.sin(angle) * r), b = place(cx, cy, cz, across, Mth.cos(behind) * r, Mth.sin(behind) * r);
                VisualDraw.beam(sparks, at, forward, a[0], a[1], a[2], b[0], b[1], b[2], .09F, VisualDraw.glow(shown.accent(), strength),
                        VisualDraw.glow(shown.colour(), strength * .3F));
            }
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), facing = shown.up();
        for (int drip = 0; drip < DRIPS; drip++) {
            float period = 12 + 8 * VisualDraw.hash(shown.seed(), drip, 1), t = (age / period + VisualDraw.hash(shown.seed(), drip, 2)) % 1;
            float angle = Mth.PI + VisualDraw.hash(shown.seed(), drip, 3) * Mth.PI, fall = t * period;
            float[] from = place(cx, cy, cz, across, Mth.cos(angle) * size, Mth.sin(angle) * size);
            float flung = Mth.cos(angle) * fall * .04F;
            float y = from[1] - .012F * fall * fall;
            if (y <= 0) continue;
            VisualDraw.sprite(sprites, at, right, facing, from[0] + across.x * flung, y, from[2] + across.z * flung, .14F, .14F, 0, VisualDraw.EMBER,
                    VisualDraw.glow(shown.accent(), strength * (1 - t)));
        }
    }

    /** A point of the ring, x across and y up from its middle, in the world. */
    private static float[] place(float cx, float cy, float cz, Vector3f across, float x, float y) {
        return new float[]{cx + across.x * x, cy + y, cz + across.z * x};
    }
}
