package com.miophas.corruption.common.client.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

/**
 * The archmage's beam, as the films draw one: three casting circles stand up before the caster's hand, turning
 * against each other; out of them the beam drives forward to where it stops in a few ticks, a white-hot core inside
 * layered glows, light rushing along it, energy coiling round it, shock rings racing down it; where it stops it
 * flares, throws sparks and burns a ring on what it struck; it swells and shivers as it fires, and at the end it thins
 * to a thread and is gone. It is drawn for as long as the beam fires (see SpellBeamRenderer); all of it is light.
 */
public final class BeamVisual {
    /**
     * One beam as it stands at the frame drawn.
     *
     * @param from     where it leaves the caster's hand, from the pose's origin
     * @param way      the way it runs, of length one
     * @param length   how far it runs before it stops, in blocks
     * @param width    how far round its line it catches, in blocks
     * @param struck   the way the face it stopped against looks out, or null if it stopped against nothing
     * @param age      ticks since it began firing, the part-tick of this frame included
     * @param lifetime ticks it fires in all
     * @param colour   its colour, 0xRRGGBB
     * @param accent   the colour of its white-hot core, 0xRRGGBB
     * @param seed     the same for this beam every frame
     * @param facing   how the viewer's camera is turned
     */
    public record Shape(Vector3f from, Vector3f way, float length, float width, Vector3f struck, float age, int lifetime,
                        int colour, int accent, long seed, Quaternionfc facing) {}

    /** Ticks it takes to drive out to where it stops, and at most to thin away at its end. */
    private static final float DRIVING = 4, THINNING = 6;
    /** Ticks between one shock ring setting out and the next, and that one ring takes to run the beam's length. */
    private static final float RING_EVERY = 5, RING_RUN = 10;
    /** Strands of energy coiling round it, sparks thrown from where it stops, motes rushing along it. */
    private static final int STRANDS = 3, SPARKS = 18, MOTES = 14;

    public static void draw(Shape shape, PoseStack.Pose at, MultiBufferSource buffers) {
        float age = shape.age(), life = Math.max(1, shape.lifetime());
        float thinning = Math.min(THINNING, life * .3F);
        float rise = VisualDraw.within(age, 0, Math.min(3, life * .2F)), fall = 1 - VisualDraw.within(age, life - thinning, thinning);
        float strength = Math.min(rise, fall);
        float reach = shape.length() * VisualDraw.easeOutCubic(age / DRIVING);
        if (strength <= 0 || reach <= 1e-3F || shape.width() <= 0) return;
        float swell = 1 + .07F * Mth.sin(age * 1.7F) + .05F * Mth.sin(age * 3.1F + 1);
        float w = shape.width() * swell * (.15F + .85F * fall);
        Vector3f d = new Vector3f(shape.way()).normalize(), u = across(d), v = new Vector3f(u).cross(d).normalize();
        Vector3f a = new Vector3f(shape.from()), b = new Vector3f(d).mul(reach).add(a);
        Vector3f forward = new Quaternionf(shape.facing()).transform(new Vector3f(0, 0, -1));
        Vector3f right = new Quaternionf(shape.facing()).transform(new Vector3f(1, 0, 0)), up = new Quaternionf(shape.facing()).transform(new Vector3f(0, 1, 0));
        boolean stopped = shape.struck() != null && reach >= shape.length() * .98F;
        Vector3f n = stopped ? new Vector3f(shape.struck()).normalize() : new Vector3f(d).negate();
        Vector3f nu = across(n), nv = new Vector3f(nu).cross(n).normalize();
        float flicker = .8F + .2F * Mth.sin(age * 2.3F) * Mth.sin(age * .7F + 2);

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        VisualDraw.discIn(glow, at, a.x, a.y, a.z, u, v, w * 2.2F, 0, VisualDraw.glow(shape.colour(), strength * .7F));
        VisualDraw.discIn(glow, at, b.x + n.x * .05F, b.y + n.y * .05F, b.z + n.z * .05F, nu, nv, w * 3 * flicker, 0, VisualDraw.glow(shape.accent(), strength));

        // The casting circles: the largest at the hand, smaller ones out along the beam, turning against each other.
        float opening = VisualDraw.easeOutBack(age / 6);
        VertexConsumer outer = VisualDraw.light(buffers, VisualDraw.CIRCLE_OUTER);
        circle(outer, at, a, d, u, v, w * .15F, w * 2 * opening, age * 5, VisualDraw.glow(shape.colour(), strength));
        circle(outer, at, a, d, u, v, w * 1.2F, w * 1.5F * opening, -age * 7, VisualDraw.glow(shape.accent(), strength * .8F));
        circle(outer, at, a, d, u, v, w * 2.6F, w * 1.05F * opening, age * 10, VisualDraw.glow(shape.colour(), strength * .7F));
        VertexConsumer inner = VisualDraw.light(buffers, VisualDraw.CIRCLE_INNER);
        circle(inner, at, a, d, u, v, w * .16F, w * 1.3F * opening, -age * 4, VisualDraw.glow(shape.accent(), strength));

        // The beam itself: glows within glows, round a white-hot thread; and energy coiling round it.
        VertexConsumer beam = VisualDraw.light(buffers, VisualDraw.BEAM);
        VisualDraw.beam(beam, at, forward, a.x, a.y, a.z, b.x, b.y, b.z, w * 3.2F, VisualDraw.glow(shape.colour(), strength * .3F), VisualDraw.glow(shape.colour(), strength * .3F));
        VisualDraw.beam(beam, at, forward, a.x, a.y, a.z, b.x, b.y, b.z, w * 2, VisualDraw.glow(shape.colour(), strength * .75F), VisualDraw.glow(shape.colour(), strength * .75F));
        int core = VisualDraw.glow(VisualDraw.mix(shape.accent(), 0xFFFFFF, .3F), strength * flicker);
        VisualDraw.beam(beam, at, forward, a.x, a.y, a.z, b.x, b.y, b.z, w * .9F, core, core);
        VisualDraw.beam(beam, at, forward, a.x, a.y, a.z, b.x, b.y, b.z, w * .35F, VisualDraw.glow(0xFFFFFF, strength), VisualDraw.glow(0xFFFFFF, strength));
        int pieces = Mth.clamp(Math.round(reach * 1.5F), 12, 160);
        for (int strand = 0; strand < STRANDS; strand++) {
            float start = Mth.TWO_PI * strand / STRANDS;
            Vector3f last = coil(a, d, u, v, w, reach, start, 0, age);
            for (int piece = 1; piece <= pieces; piece++) {
                float t = (float) piece / pieces;
                Vector3f next = coil(a, d, u, v, w, reach, start, t, age);
                int colour = VisualDraw.glow(shape.accent(), strength * (1 - .6F * t) * .7F);
                VisualDraw.beam(beam, at, forward, last.x, last.y, last.z, next.x, next.y, next.z, w * .16F, colour, colour);
                last = next;
            }
        }

        // Light rushing along it.
        VertexConsumer streaks = VisualDraw.light(buffers, VisualDraw.STREAKS);
        float rush = -age * .45F;
        flow(streaks, at, forward, a, b, w * 2.2F, rush, rush + reach / 5, VisualDraw.glow(shape.colour(), strength * .55F));

        // Shock rings racing down it, and the ring it burns on what it struck.
        VertexConsumer band = VisualDraw.light(buffers, VisualDraw.BAND);
        int first = Math.max(0, Mth.floor((age - RING_RUN) / RING_EVERY)), last = Mth.floor(age / RING_EVERY);
        for (int ring = first; ring <= last; ring++) {
            float since = age - ring * RING_EVERY, along = since / RING_RUN * shape.length();
            if (since < 0 || along > reach) continue;
            float gone = since / RING_RUN, radius = w * (1.25F + .35F * gone);
            Vector3f c = new Vector3f(d).mul(along).add(a);
            ringIn(band, at, c, u, v, radius * .82F, radius, VisualDraw.glow(shape.accent(), strength * (1 - gone) * .8F),
                    VisualDraw.glow(shape.colour(), strength * (1 - gone) * .4F));
        }
        if (stopped) {
            float burn = 1 + .12F * Mth.sin(age * 1.3F);
            Vector3f c = new Vector3f(n).mul(.04F).add(b);
            ringIn(band, at, c, nu, nv, w * 1.6F * burn, w * 2.6F * burn, VisualDraw.glow(shape.accent(), strength), VisualDraw.glow(shape.colour(), strength * .3F));
        }

        // Where it leaves the hand and where it stops, flaring; sparks thrown back off what it struck; motes rushing along.
        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        VisualDraw.sprite(sprites, at, right, up, a.x, a.y, a.z, w * 2.4F, w * 2.4F, 0, VisualDraw.EMBER, VisualDraw.glow(shape.colour(), strength));
        VisualDraw.sprite(sprites, at, right, up, b.x, b.y, b.z, w * 4.5F * flicker, w * 4.5F * flicker, age * .1F, VisualDraw.TWINKLE,
                VisualDraw.glow(VisualDraw.mix(shape.accent(), 0xFFFFFF, .5F), strength));
        VisualDraw.sprite(sprites, at, right, up, b.x, b.y, b.z, w * 3.2F, w * 3.2F, 0, VisualDraw.EMBER, VisualDraw.glow(shape.accent(), strength));
        for (int spark = 0; spark < SPARKS; spark++) {
            float t = (age / 10 + (float) spark / SPARKS) % 1, turn = VisualDraw.hash(shape.seed(), spark, 0) * Mth.TWO_PI;
            float out = .4F + .8F * VisualDraw.hash(shape.seed(), spark, 1);
            Vector3f way = new Vector3f(n).mul(.7F).add(new Vector3f(nu).mul(Mth.cos(turn) * out)).add(new Vector3f(nv).mul(Mth.sin(turn) * out)).normalize();
            Vector3f p = new Vector3f(way).mul(t * w * 3).add(b);
            p.y -= t * t * 1.2F;
            float size = Math.max(.08F, .25F * w * (1 - t));
            VisualDraw.sprite(sprites, at, right, up, p.x, p.y, p.z, size, size, 0, VisualDraw.EMBER, VisualDraw.glow(shape.accent(), strength * (1 - t)));
        }
        for (int mote = 0; mote < MOTES; mote++) {
            float t = (age * .08F + (float) mote / MOTES) % 1, turn = VisualDraw.hash(shape.seed(), mote, 2) * Mth.TWO_PI;
            float off = w * .9F * VisualDraw.hash(shape.seed(), mote, 3);
            Vector3f p = new Vector3f(d).mul(t * reach).add(a).add(new Vector3f(u).mul(Mth.cos(turn) * off)).add(new Vector3f(v).mul(Mth.sin(turn) * off));
            float size = .1F + .18F * w;
            VisualDraw.sprite(sprites, at, right, up, p.x, p.y, p.z, size, size, 0, VisualDraw.TWINKLE, VisualDraw.glow(0xFFFFFF, strength * Mth.sin(t * Mth.PI)));
        }
    }

    /** A way across this one, of length one: across it and level, unless it runs straight up or down. */
    private static Vector3f across(Vector3f way) {
        Vector3f level = Math.abs(way.y) < .95F ? new Vector3f(0, 1, 0) : new Vector3f(1, 0, 0);
        return new Vector3f(way).cross(level).normalize();
    }

    /** A circle standing across the beam, this far out along it from where it leaves the hand. */
    private static void circle(VertexConsumer buffer, PoseStack.Pose at, Vector3f a, Vector3f d, Vector3f u, Vector3f v, float along, float radius,
                               float degrees, int argb) {
        if (radius <= 0) return;
        VisualDraw.discIn(buffer, at, a.x + d.x * along, a.y + d.y * along, a.z + d.z * along, u, v, radius, degrees, argb);
    }

    /** Where a strand coiling round the beam is, this far along it (t, 0 to 1). */
    private static Vector3f coil(Vector3f a, Vector3f d, Vector3f u, Vector3f v, float w, float reach, float start, float t, float age) {
        float turn = start + t * reach * .9F - age * .7F, round = w * 1.25F * (1 + .15F * Mth.sin(age * .9F + t * reach * .5F));
        return new Vector3f(d).mul(t * reach).add(a).add(new Vector3f(u).mul(Mth.cos(turn) * round)).add(new Vector3f(v).mul(Mth.sin(turn) * round));
    }

    /**
     * A ribbon from a to b turned to the viewer, {@code width} wide: the texture runs along it from {@code v0} to
     * {@code v1} (light pouring along a beam), and across it from u 0 to 1.
     */
    private static void flow(VertexConsumer buffer, PoseStack.Pose at, Vector3f forward, Vector3f a, Vector3f b, float width, float v0, float v1, int argb) {
        Vector3f side = new Vector3f(b).sub(a).cross(forward);
        if (side.lengthSquared() < 1e-8F) side = across(new Vector3f(b).sub(a).normalize());
        side.normalize().mul(width / 2);
        VisualDraw.quad(buffer, at,
                a.x - side.x, a.y - side.y, a.z - side.z, 0, v0, argb,
                a.x + side.x, a.y + side.y, a.z + side.z, 1, v0, argb,
                b.x + side.x, b.y + side.y, b.z + side.z, 1, v1, argb,
                b.x - side.x, b.y - side.y, b.z - side.z, 0, v1, argb);
    }

    /** A flat ring in the plane of u and v about c, from {@code inner} out to {@code outer}, the band texture across it. */
    private static void ringIn(VertexConsumer buffer, PoseStack.Pose at, Vector3f c, Vector3f u, Vector3f v, float inner, float outer, int innerColour, int outerColour) {
        if (outer <= inner || outer <= 0) return;
        int pieces = VisualDraw.SEGMENTS / 2;
        for (int piece = 0; piece < pieces; piece++) {
            float a0 = Mth.TWO_PI * piece / pieces, a1 = Mth.TWO_PI * (piece + 1) / pieces;
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0), c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            float x0 = u.x * c0 + v.x * s0, y0 = u.y * c0 + v.y * s0, z0 = u.z * c0 + v.z * s0;
            float x1 = u.x * c1 + v.x * s1, y1 = u.y * c1 + v.y * s1, z1 = u.z * c1 + v.z * s1;
            float uv0 = 4F * piece / pieces, uv1 = 4F * (piece + 1) / pieces;
            VisualDraw.quad(buffer, at,
                    c.x + x0 * inner, c.y + y0 * inner, c.z + z0 * inner, uv0, 0, innerColour,
                    c.x + x0 * outer, c.y + y0 * outer, c.z + z0 * outer, uv0, 1, outerColour,
                    c.x + x1 * outer, c.y + y1 * outer, c.z + z1 * outer, uv1, 1, outerColour,
                    c.x + x1 * inner, c.y + y1 * inner, c.z + z1 * inner, uv1, 0, innerColour);
        }
    }

    private BeamVisual() {}
}
