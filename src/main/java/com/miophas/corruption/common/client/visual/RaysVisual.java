package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

/**
 * Rays of light bursting out of a point, as a great creature's life pours out of it as it dies: one ray after another
 * shoots out every way but down into the ground, each a blade of light white-hot where it leaves the point and fading
 * to the effect's colour along it, flickering; the whole burst turns slowly; and a glow swells at its heart. The
 * point is high over the spot (at the effect's height, or half its radius if that is higher), the rays as long as its
 * radius; they come over the first half of its life, and are gone with it.
 */
public final class RaysVisual implements SpellVisual {
    /** How fast the whole burst turns, in radians a tick; and how many ticks a ray takes to shoot out to its length. */
    private static final float SPIN = .012F, SHOOT = 5;
    /** The golden angle and ratio: each ray turned and raised by these from the one before, so that rays come spread evenly, however many have come. */
    private static final float GOLDEN_ANGLE = 2.3999632F, GOLDEN = .618034F;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float radius = shown.radius(), strength = shown.strength();
        if (radius <= 0 || strength <= 0) return;
        float y = Math.max(1, Math.max(shown.height(), radius * .5F));
        int count = 8 + Math.min(16, Math.round(radius));
        burst(pose.last(), buffers, shown.facing(), 0, y, 0, count, shown.lifetime() * .5F, radius, shown.age(), strength, 0, shown.colour(), shown.accent(), shown.seed());
        float swell = .6F + .4F * VisualDraw.easeOutCubic(shown.progress() * 2);
        heart(pose.last(), buffers, shown.facing(), 0, y, 0, radius * .12F * swell, shown.age(), strength, shown.colour(), shown.accent());
    }

    /**
     * Rays out of the point x, y, z: of {@code count}, those come by {@code time} (one every {@code over / count}
     * ticks, the first at once), each shooting out over a few ticks to its own share of {@code length}, the lot
     * turning slowly about the upright; none reaching below {@code floor}, the height of the ground.
     */
    static void burst(PoseStack.Pose pose, MultiBufferSource buffers, Quaternionfc facing, float x, float y, float z, int count, float over,
                      float length, float time, float strength, float floor, int colour, int accent, long seed) {
        if (count <= 0 || length <= 0 || strength <= 0 || time < 0) return;
        VertexConsumer light = VisualDraw.light(buffers, VisualDraw.BEAM);
        Vector3f forward = facing.transform(new Vector3f(0, 0, -1));
        for (int ray = 0; ray < count; ray++) {
            float born = over * ray / count;
            if (time < born) break;
            float yaw = ray * GOLDEN_ANGLE + Mth.TWO_PI * VisualDraw.hash(seed, 0, 0) + .35F * VisualDraw.hash(seed, ray, 0) + time * SPIN;
            // Mostly outward and up; a few dip below level, as far as the ground lets them.
            float rise = (ray * GOLDEN + VisualDraw.hash(seed, 0, 1) + .15F * VisualDraw.hash(seed, ray, 1)) % 1;
            float dy = -.25F + rise, level = Mth.sqrt(1 - dy * dy);
            float dx = Mth.cos(yaw) * level, dz = Mth.sin(yaw) * level;
            float reach = length * (.55F + .45F * VisualDraw.hash(seed, ray, 2)) * VisualDraw.easeOutCubic((time - born) / SHOOT);
            if (dy < 0) reach = Math.min(reach, Math.max(0, y - floor) / (-dy + .08F));
            if (reach <= .01F) continue;
            float flicker = .7F + .3F * Mth.sin(time * (1.3F + VisualDraw.hash(seed, ray, 3)) + 6 * VisualDraw.hash(seed, ray, 4));
            float wide = reach * (.11F + .08F * VisualDraw.hash(seed, ray, 5));
            // Across the ray and across the viewer's sight, so the blade is always seen broadside.
            float wx = dy * forward.z - dz * forward.y, wy = dz * forward.x - dx * forward.z, wz = dx * forward.y - dy * forward.x;
            float across = Mth.sqrt(wx * wx + wy * wy + wz * wz);
            if (across < 1e-4F) {
                wx = -dz; wy = 0; wz = dx;
                across = Mth.sqrt(wx * wx + wz * wz);
                if (across < 1e-4F) { wx = 1; wz = 0; across = 1; }
            }
            float tx = x + dx * reach, ty = y + dy * reach, tz = z + dz * reach;
            // A broad blade of the colour, and a narrow white-hot one inside it.
            blade(light, pose, x, y, z, tx, ty, tz, wx / across, wy / across, wz / across, wide,
                    VisualDraw.glow(VisualDraw.mix(colour, accent, .5F), strength * flicker), VisualDraw.glow(colour, strength * flicker * .55F));
            blade(light, pose, x, y, z, tx, ty, tz, wx / across, wy / across, wz / across, wide * .35F,
                    VisualDraw.glow(VisualDraw.mix(accent, 0xFFFFFF, .5F), strength * flicker), VisualDraw.glow(accent, strength * flicker * .4F));
        }
    }

    /**
     * A blade of light from the point x, y, z to the tip t: narrow where it leaves the point, {@code wide} at its tip,
     * across along w (of length one); {@code root} colour at the point fading to {@code end} at the tip.
     */
    private static void blade(VertexConsumer light, PoseStack.Pose pose, float x, float y, float z, float tx, float ty, float tz,
                              float wx, float wy, float wz, float wide, int root, int end) {
        float tip = wide / 2, base = wide * .03F;
        VisualDraw.quad(light, pose,
                x - wx * base, y - wy * base, z - wz * base, 0, 0, root,
                x + wx * base, y + wy * base, z + wz * base, 0, 1, root,
                tx + wx * tip, ty + wy * tip, tz + wz * tip, 1, 1, end,
                tx - wx * tip, ty - wy * tip, tz - wz * tip, 1, 0, end);
    }

    /** A glow at the point x, y, z, this big: a soft ball of the accent inside a wider one of the colour, throbbing. */
    static void heart(PoseStack.Pose pose, MultiBufferSource buffers, Quaternionfc facing, float x, float y, float z, float size, float time,
                      float strength, int colour, int accent) {
        if (size <= 0 || strength <= 0) return;
        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        Vector3f right = facing.transform(new Vector3f(1, 0, 0)), up = facing.transform(new Vector3f(0, 1, 0));
        float throb = 1 + .12F * Mth.sin(time * .9F);
        VisualDraw.discIn(glow, pose, x, y, z, right, up, size * 1.9F * throb, time * 2, VisualDraw.glow(colour, strength * .55F));
        VisualDraw.discIn(glow, pose, x, y, z, right, up, size * throb, -time * 3, VisualDraw.glow(accent, strength));
    }
}
