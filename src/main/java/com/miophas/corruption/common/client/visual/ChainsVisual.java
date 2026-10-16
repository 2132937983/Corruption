package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * Chains of binding, as a spirit-binder's are drawn: from small circles round the spot, chains of light shoot up one
 * after another, arc over and lash together over its middle, where a circle of binding turns; drawn taut, they
 * thrum; at the end they burst apart, link by link.
 */
public final class ChainsVisual implements SpellVisual {
    /** How far apart links are along a chain, and how long a link is. */
    private static final float SPACING = .32F, LINK = SPACING * 1.35F;
    /** Ticks a chain takes to reach the middle, and between one chain setting out and the next. */
    private static final float REACHING = 9, STAGGER = 1.5F;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = Math.max(1, shown.radius());
        if (strength <= 0) return;
        PoseStack.Pose at = pose.last();
        int count = Mth.clamp(Math.round(radius * 1.2F), 4, 8);
        float bound = Math.max(.5F, shown.height()), bursting = VisualDraw.within(shown.progress(), .82F, .18F);
        float allBound = VisualDraw.within(age, (count - 1) * STAGGER + REACHING, 4);
        Vector3f east = new Vector3f(1, 0, 0), south = new Vector3f(0, 0, 1);

        VertexConsumer anchors = VisualDraw.light(buffers, VisualDraw.CIRCLE_INNER);
        for (int chain = 0; chain < count; chain++) {
            float angle = angle(shown, chain, count), appear = VisualDraw.within(age, chain * STAGGER, 4);
            VisualDraw.discIn(anchors, at, Mth.cos(angle) * radius, VisualDraw.LIFT, Mth.sin(angle) * radius, east, south, .7F * appear, age * 4,
                    VisualDraw.glow(shown.colour(), strength * appear * (1 - bursting)));
        }
        VertexConsumer binding = VisualDraw.light(buffers, VisualDraw.CIRCLE_OUTER);
        VisualDraw.discIn(binding, at, 0, bound, 0, east, south, (.8F + .4F * bursting) * VisualDraw.easeOutBack(allBound), -age * 5,
                VisualDraw.glow(shown.accent(), strength * allBound));

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        int head = VisualDraw.glow(shown.accent(), strength);
        for (int chain = 0; chain < count; chain++) {
            float angle = angle(shown, chain, count), reached = VisualDraw.easeOutCubic(VisualDraw.within(age, chain * STAGGER, REACHING));
            if (reached <= 0) continue;
            float ax = Mth.cos(angle) * radius, az = Mth.sin(angle) * radius;
            // Over a curve from its anchor up and over to the middle.
            float qx = ax * .45F, qy = bound + radius * .7F, qz = az * .45F;
            int links = Math.max(2, Math.round(radius * 1.6F / SPACING));
            float taut = reached >= 1 ? 1 : 0;
            for (int link = 0; link < links; link++) {
                float t = (link + .5F) / links;
                if (t > reached) break;
                float u = 1 - t;
                float x = u * u * ax + 2 * u * t * qx, y = 2 * u * t * qy + t * t * bound, z = u * u * az + 2 * u * t * qz;
                float tx = 2 * u * (qx - ax) + 2 * t * -qx, ty = 2 * u * qy + 2 * t * (bound - qy), tz = 2 * u * (qz - az) + 2 * t * -qz;
                float along = Mth.sqrt(tx * tx + ty * ty + tz * tz);
                if (along < 1e-5F) continue;
                tx /= along;
                ty /= along;
                tz /= along;
                // Thrumming once taut, most in the middle of its length.
                float thrum = taut * .06F * Mth.sin(age * 2.2F + link) * Mth.sin(Mth.PI * t);
                y += thrum;
                // Flying apart at the end, each link its own way.
                float burst = bursting * bursting * 3;
                x += (VisualDraw.hash(shown.seed(), chain, link) - .5F) * burst;
                y += VisualDraw.hash(shown.seed(), chain, link + 1000) * burst * .6F;
                z += (VisualDraw.hash(shown.seed(), chain, link + 2000) - .5F) * burst;
                // Every other link turned a quarter round the chain, as a chain's links are.
                float wx = tz, wy = 0, wz = -tx, across = Mth.sqrt(wx * wx + wz * wz);
                if (across < 1e-4F) {
                    wx = 1;
                    across = 1;
                }
                wx /= across;
                wz /= across;
                if (link % 2 == 1) {
                    float nx = ty * wz - tz * wy, ny = tz * wx - tx * wz, nz = tx * wy - ty * wx;
                    wx = nx;
                    wy = ny;
                    wz = nz;
                }
                float half = LINK / 2;
                VisualDraw.spriteAlong(sprites, at, x - tx * half, y - ty * half, z - tz * half, x + tx * half, y + ty * half, z + tz * half, wx, wy, wz,
                        LINK / 1.6F, VisualDraw.LINK, VisualDraw.glow(shown.colour(), strength * (1 - bursting * .8F)));
                if (reached < 1 && t + 1F / links > reached)
                    VisualDraw.sprite(sprites, at, right, up, x, y, z, .7F, .7F, age * .3F, VisualDraw.TWINKLE, head);
            }
        }
    }

    /** Where round the spot a chain's anchor is: evenly round, a little uneven, turned with the caster. */
    private static float angle(Shown shown, int chain, int count) {
        return Mth.TWO_PI * (chain + (VisualDraw.hash(shown.seed(), chain, 7) - .5F) * .4F) / count + (float) Math.toRadians(shown.yaw());
    }
}
