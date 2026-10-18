package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.miophas.corruption.core.demon.skill.SkyBeamRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * Beams driven straight down out of magic circles in the sky (SkyBeamRules), lightning and a beam in one: every beam pours
 * from a circle of its own.
 *
 * <p>Through the first third of its life, its warning, the circles open one after another high over the spot, its height up.
 * A single beam has one, over the spot. A star has one at each of its points, its radius out: five for a pentagram, six for a
 * hexagram, eight for an octagram, with the star's figure drawn faintly in light from circle to circle (a pentagram's every
 * second point, a hexagram's two triangles, an octagram's every third). Lightning crackles from circle to circle, or round
 * the rim of a single beam's, more and more. On the ground a mark pulses under every circle, faster as the warning runs out.
 *
 * <p>Through the warning the edge of where each beam will strike is drawn on the ground as a ring, so that whoever is under
 * it can see how far they must go. Then every circle pours its beam down. A narrow beam is a white-hot thread in a broad
 * glow. A wide one (Solomon's, each as wide as a chunk) is a column of light pouring down, walls within walls round a
 * white-hot heart, as wide as it reaches. Lightning coils down each, struck afresh every other tick (many bolts round a wide
 * one), with a pool of light and a ring racing out where it strikes, and sparks flying up. A star's circles and beams turn and
 * close in together, just as {@link SkyBeamRules#points} has them, to meet over the spot at the end. The skill that hurts with
 * them has them the same way, so what is seen is what strikes.
 *
 * <p>Its radius is how far out a star's circles hang, or a single beam's reach; a star's beams reach as far as their
 * spread leaves them room to ({@link SkyBeamRules#width(int, double)}). Its way is where the star's first point is.
 */
public final class SkyBeamsVisual implements SpellVisual {
    /** Ticks a circle takes to open; the ticks everything fades over at its end; and how far round a beam must reach to be drawn as a column. */
    private static final float OPENING = 10, FADE = 6, WIDE = 3;
    private static final Vector3f EAST = new Vector3f(1, 0, 0), SOUTH = new Vector3f(0, 0, 1);
    private final int beams;

    /** One beam, or a star of this many points. */
    public SkyBeamsVisual(int beams) { this.beams = Math.max(1, beams); }

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float age = shown.age(), life = shown.lifetime();
        float warning = (float) (life * SkyBeamRules.WARNING), pouring = Math.max(1, life - warning), since = age - warning;
        float strength = Mth.clamp(age / 3, 0, 1) * Mth.clamp((life - age) / FADE, 0, 1);
        if (strength <= 0) return;
        PoseStack.Pose at = pose.last();
        boolean star = beams > 1, firing = since >= 0;
        float sky = Math.max(6, shown.height()), spread = star ? shown.radius() : 0, reach = Math.max(1, (float) SkyBeamRules.width(beams, shown.radius()));
        float circle = (float) SkyBeamRules.circle(reach), spin = shown.yaw() + age * 2.2F, charge = Mth.clamp(age / Math.max(1, warning), 0, 1);
        boolean wide = reach > WIDE;
        float flicker = .85F + .15F * Mth.sin(age * 2.1F) * Mth.sin(age * .7F + 1);
        int colour = shown.colour(), accent = shown.accent(), white = VisualDraw.mix(accent, 0xFFFFFF, .55F);

        // Where the circles hang and their beams strike (where they will first strike, through the warning), as the skill has them.
        float[][] points = points(SkyBeamRules.points(beams, spread, shown.yaw(), since, pouring));
        // Each circle opens in its turn, round the star.
        float stagger = Math.min(3, warning / (2 * beams));
        float[] open = new float[beams];
        for (int beam = 0; beam < beams; beam++) open[beam] = Math.max(0, VisualDraw.easeOutBack((age - beam * stagger) / OPENING));

        // The circles: an outer ring of runes, and an inner one turning against it.
        VertexConsumer outer = VisualDraw.light(buffers, VisualDraw.CIRCLE_OUTER);
        for (int beam = 0; beam < beams; beam++)
            if (open[beam] > 0) VisualDraw.discIn(outer, at, points[beam][0], sky, points[beam][1], EAST, SOUTH, circle * open[beam], spin + beam * 37,
                    VisualDraw.glow(colour, strength));
        VertexConsumer inner = VisualDraw.light(buffers, VisualDraw.CIRCLE_INNER);
        for (int beam = 0; beam < beams; beam++)
            if (open[beam] > 0) VisualDraw.discIn(inner, at, points[beam][0], sky - .05F, points[beam][1], EAST, SOUTH, circle * .62F * open[beam], -spin * 1.6F + beam * 53,
                    VisualDraw.glow(accent, strength * .8F));

        // Their glow; the marks, or the pools of light where the beams strike and where they leave their circles.
        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        float pulse = .5F + .5F * Mth.sin(age * (.4F + 1.6F * charge));
        for (int beam = 0; beam < beams; beam++) {
            float x = points[beam][0], z = points[beam][1];
            flat(glow, at, x, sky + .05F, z, circle * 1.15F * open[beam], VisualDraw.glow(colour, strength * (firing ? .7F : .25F + .35F * charge)));
            if (!firing) {
                flat(glow, at, x, VisualDraw.LIFT, z, reach * (1 + .25F * pulse) * Math.min(1, open[beam]), VisualDraw.glow(accent, strength * (.3F + .5F * pulse * charge)));
                continue;
            }
            flat(glow, at, x, VisualDraw.LIFT, z, reach * (wide ? 1.25F : 2.2F) * flicker, VisualDraw.glow(colour, strength * .9F));
            flat(glow, at, x, VisualDraw.LIFT * 2, z, reach * 1.1F, VisualDraw.glow(accent, strength));
            flat(glow, at, x, sky - .1F, z, reach * 1.6F, VisualDraw.glow(white, strength * .8F));
        }

        VertexConsumer light = VisualDraw.light(buffers, VisualDraw.BEAM);
        Vector3f forward = shown.forward();
        // The star's figure, faint, from circle to circle.
        if (star) {
            int step = step(beams);
            for (int beam = 0; beam < beams; beam++) {
                int next = (beam + step) % beams;
                float faint = strength * .45F * Math.min(1, Math.min(open[beam], open[next]));
                if (faint <= 0) continue;
                VisualDraw.groundLine(light, at, points[beam][0], points[beam][1], points[next][0], points[next][1], sky - .08F, .45F + reach * .08F,
                        VisualDraw.glow(accent, faint), VisualDraw.glow(accent, faint));
            }
        }
        if (!firing) {
            // Lightning crackling from circle to circle (round the rim of a single beam's), more and more as they gather.
            Random crackle = new Random(shown.seed() * 31 + (long) (age / 2));
            for (int arc = 0; arc < Math.max(3, beams); arc++) {
                if (crackle.nextFloat() > .25F + .6F * charge) continue;
                float ax, az, bx, bz;
                if (star) {
                    int from = arc % beams, to = (from + 1) % beams;
                    if (open[from] < .5F || open[to] < .5F) continue;
                    ax = points[from][0];
                    az = points[from][1];
                    bx = points[to][0];
                    bz = points[to][1];
                } else {
                    float from = crackle.nextFloat() * Mth.TWO_PI, to = from + 1.2F + crackle.nextFloat() * 2, rim = circle * .75F * Math.min(1, open[0]);
                    ax = Mth.cos(from) * rim;
                    az = Mth.sin(from) * rim;
                    bx = Mth.cos(to) * rim;
                    bz = Mth.sin(to) * rim;
                }
                float[] bolt = VisualPaths.bolt(crackle, ax, sky, az, bx, sky, bz, 3, .25F);
                VisualDraw.beams(light, at, forward, bolt, .35F, VisualDraw.glow(colour, strength * .8F));
                VisualDraw.beams(light, at, forward, bolt, .12F, VisualDraw.glow(white, strength));
            }
            // The edge of where each will strike, a ring on the ground.
            VertexConsumer edge = VisualDraw.light(buffers, VisualDraw.BAND);
            float thick = Math.max(.15F, reach * .05F);
            for (int beam = 0; beam < beams; beam++) {
                float shownEdge = strength * Math.min(1, open[beam]) * (.35F + .5F * pulse * charge);
                if (shownEdge <= 0) continue;
                pose.pushPose();
                pose.translate(points[beam][0], 0, points[beam][1]);
                VisualDraw.ring(edge, pose.last(), reach - thick, reach + thick, VisualDraw.LIFT * 2, Math.max(3, Math.round(reach)), age * .1F,
                        VisualDraw.glow(accent, shownEdge), VisualDraw.glow(accent, shownEdge));
                pose.popPose();
            }
            return;
        }

        // Every circle's beam, reaching down from it to the ground over the first ticks; lightning coiling down each (many bolts
        // round a wide one, struck within it).
        float bottom = sky * (1 - VisualDraw.easeOutCubic(since / 3));
        int bolts = wide ? Mth.clamp(Math.round(reach * .8F), 3, 10) : 1;
        for (int beam = 0; beam < beams; beam++) {
            float x = points[beam][0], z = points[beam][1];
            if (!wide) {
                VisualDraw.beam(light, at, forward, x, sky, z, x, bottom, z, reach * 2.4F * flicker, VisualDraw.glow(colour, strength * .5F), VisualDraw.glow(colour, strength * .5F));
                VisualDraw.beam(light, at, forward, x, sky, z, x, bottom, z, reach * 1.3F, VisualDraw.glow(colour, strength), VisualDraw.glow(colour, strength));
                VisualDraw.beam(light, at, forward, x, sky, z, x, bottom, z, reach * .45F, VisualDraw.glow(white, strength), VisualDraw.glow(white, strength));
            }
            Random path = new Random(shown.seed() + 977L * (long) (age / 2) + 31L * beam);
            for (int strike = 0; strike < bolts; strike++) {
                float ax, az, bx, bz;
                if (wide) {
                    float angle = path.nextFloat() * Mth.TWO_PI, turn = (path.nextFloat() - .5F) * 1.4F;
                    float from = reach * (.45F + .5F * path.nextFloat()), to = reach * (.45F + .5F * path.nextFloat());
                    ax = x + Mth.cos(angle) * from;
                    az = z + Mth.sin(angle) * from;
                    bx = x + Mth.cos(angle + turn) * to;
                    bz = z + Mth.sin(angle + turn) * to;
                } else {
                    ax = x + (path.nextFloat() - .5F) * reach;
                    az = z + (path.nextFloat() - .5F) * reach;
                    bx = x + (path.nextFloat() - .5F) * reach;
                    bz = z + (path.nextFloat() - .5F) * reach;
                }
                float[] bolt = VisualPaths.bolt(path, ax, sky, az, bx, bottom, bz, 4, .3F);
                for (int point = 1; point < bolt.length; point += 3) bolt[point] = Math.max(0, bolt[point]);
                VisualDraw.beams(light, at, forward, bolt, .45F, VisualDraw.glow(accent, strength * .8F));
                VisualDraw.beams(light, at, forward, bolt, .15F, VisualDraw.glow(white, strength));
            }
        }

        // A wide beam is a column of light: walls within walls pouring down round a white-hot heart.
        if (wide) {
            VertexConsumer streaks = VisualDraw.light(buffers, VisualDraw.STREAKS);
            float pour = age * .12F;
            for (float[] point : points) {
                pose.pushPose();
                pose.translate(point[0], 0, point[1]);
                PoseStack.Pose column = pose.last();
                VisualDraw.wall(streaks, column, reach * flicker, bottom, sky, 6, pour, pour + sky / 6, VisualDraw.glow(colour, strength), VisualDraw.glow(colour, strength * .3F));
                VisualDraw.wall(streaks, column, reach * .7F, bottom, sky, 4, -pour * .7F, -pour * .7F + sky / 8, VisualDraw.glow(colour, strength * .6F), 0);
                VisualDraw.wall(streaks, column, reach * .3F, bottom, sky, 3, pour * 1.6F, pour * 1.6F + sky / 4, VisualDraw.glow(white, strength), VisualDraw.glow(accent, strength * .6F));
                pose.popPose();
            }
        }

        // A ring racing out from where each strikes, one every few ticks.
        VertexConsumer band = VisualDraw.light(buffers, VisualDraw.BAND);
        float wave = (since % 5) / 5;
        for (float[] point : points) {
            pose.pushPose();
            pose.translate(point[0], 0, point[1]);
            VisualDraw.ring(band, pose.last(), reach * (1 + .8F * wave), reach * (1.3F + .9F * wave), VisualDraw.LIFT * 3, 3, age * .05F,
                    VisualDraw.glow(accent, strength * (1 - wave)), 0);
            pose.popPose();
        }

        // Sparks flying up where they strike.
        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        int sparks = wide ? 4 + Math.round(reach * 2) : 4;
        for (int beam = 0; beam < beams; beam++)
            for (int spark = 0; spark < sparks; spark++) {
                float flown = ((age * .9F + VisualDraw.hash(shown.seed(), beam, spark) * 10) % 10) / 10;
                float angle = VisualDraw.hash(shown.seed(), beam, spark + 7) * Mth.TWO_PI, out = reach * (.3F + 1.2F * flown);
                VisualDraw.sprite(sprites, at, right, up, points[beam][0] + Mth.cos(angle) * out, .2F + 2.5F * flown, points[beam][1] + Mth.sin(angle) * out,
                        .45F, .45F, angle, spark % 2 == 0 ? VisualDraw.EMBER : VisualDraw.TWINKLE, VisualDraw.glow(spark % 2 == 0 ? accent : white, strength * (1 - flown)));
            }
    }

    /** How a star's points are joined in its figure: each to the one this many on round it (a pentagram's every second, a hexagram's two triangles, an octagram's every third). */
    static int step(int beams) { return beams == 8 ? 3 : 2; }

    private static float[][] points(double[][] points) {
        float[][] at = new float[points.length][2];
        for (int point = 0; point < points.length; point++) {
            at[point][0] = (float) points[point][0];
            at[point][1] = (float) points[point][1];
        }
        return at;
    }

    /** A glowing disc lying flat at this place. */
    private static void flat(VertexConsumer buffer, PoseStack.Pose at, float x, float y, float z, float radius, int argb) {
        VisualDraw.discIn(buffer, at, x, y, z, EAST, SOUTH, radius, 0, argb);
    }
}
