package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * The heavens brought down, as a star-reader's spell is drawn: stars wink into being one by one in a dome over the
 * spot, then lines of light run from star to star, joining them into a figure as the old star maps do; the whole
 * sky turns slowly; star dust sifts down from it; a shooting star or two crosses it; and a star chart turns on the
 * ground below.
 */
public final class ConstellationVisual implements SpellVisual {
    /** Ticks between one star appearing and the next, and one line being drawn and the next; ticks a line takes. */
    private static final float STAR_STAGGER = 1.2F, LINE_STAGGER = 1.5F, LINE_DRAWING = 4;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = Math.max(1, shown.radius());
        if (strength <= 0) return;
        PoseStack.Pose at = pose.last();
        Random random = new Random(shown.seed());
        int count = Mth.clamp(Math.round(radius * 4), 10, 28);
        float low = 1.5F, high = Math.max(low + 1, shown.height());
        float[][] stars = new float[count][];
        float[] size = new float[count], phase = new float[count];
        float turn = (float) Math.toRadians(age * .6F), cos = Mth.cos(turn), sin = Mth.sin(turn);
        for (int star = 0; star < count; star++) {
            // Spread round the whole sky, each a little off its even place, so they never bunch on one side.
            float angle = Mth.TWO_PI * (star + .8F * random.nextFloat()) / count, rise = (float) Math.toRadians(25 + 55 * random.nextFloat());
            float x = Mth.cos(angle) * Mth.cos(rise) * radius * 1.15F, z = Mth.sin(angle) * Mth.cos(rise) * radius * 1.15F, y = low + Mth.sin(rise) * (high - low);
            stars[star] = new float[]{x * cos - z * sin, y, x * sin + z * cos};
            size[star] = star == 0 ? 1.4F : .6F + .4F * random.nextFloat();
            phase[star] = random.nextFloat() * Mth.TWO_PI;
        }
        int[][] figure = VisualPaths.figure(stars);

        VertexConsumer chart = VisualDraw.light(buffers, VisualDraw.CIRCLE_OUTER);
        VisualDraw.disc(chart, at, radius, VisualDraw.LIFT, -age * .8F, VisualDraw.glow(shown.colour(), strength * .4F));

        VertexConsumer lines = VisualDraw.light(buffers, VisualDraw.BEAM);
        Vector3f forward = shown.forward();
        int line = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), .3F), strength);
        float drawingFrom = count * STAR_STAGGER;
        for (int index = 0; index < figure.length; index++) {
            float drawn = VisualDraw.within(age, drawingFrom + index * LINE_STAGGER, LINE_DRAWING);
            if (drawn <= 0) continue;
            float[] a = stars[figure[index][0]], b = stars[figure[index][1]];
            VisualDraw.beam(lines, at, forward, a[0], a[1], a[2], Mth.lerp(drawn, a[0], b[0]), Mth.lerp(drawn, a[1], b[1]), Mth.lerp(drawn, a[2], b[2]),
                    .13F, line, line);
        }
        // Shooting stars: a bright head, a fading tail.
        for (int shooting = 0; shooting < 2; shooting++) {
            float when = shown.lifetime() * (.25F + .4F * shooting + .1F * random.nextFloat()), since = age - when;
            float angle = random.nextFloat() * Mth.TWO_PI;
            if (since < 0 || since > 10) continue;
            float fromX = Mth.cos(angle) * radius, fromZ = Mth.sin(angle) * radius, toX = -fromZ * .6F, toZ = fromX * .6F, y = high + 1;
            float head = since / 10, tail = Math.max(0, head - .35F);
            VisualDraw.beam(lines, at, forward, Mth.lerp(tail, fromX, toX), y - tail * 2, Mth.lerp(tail, fromZ, toZ),
                    Mth.lerp(head, fromX, toX), y - head * 2, Mth.lerp(head, fromZ, toZ), .14F, 0, VisualDraw.glow(shown.accent(), strength * (1 - head * .5F)));
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        for (int star = 0; star < count; star++) {
            float appear = VisualDraw.easeOutBack(VisualDraw.within(age, star * STAR_STAGGER, 4));
            if (appear <= 0) continue;
            float twinkle = .75F + .25F * Mth.sin(age * .4F + phase[star]), s = size[star] * appear * twinkle;
            int colour = VisualDraw.glow(star == 0 ? shown.accent() : VisualDraw.mix(shown.colour(), shown.accent(), .6F), strength);
            float[] it = stars[star];
            VisualDraw.sprite(sprites, at, right, up, it[0], it[1], it[2], s, s, phase[star] + age * .02F, VisualDraw.TWINKLE, colour);
            // Its dust sifting down.
            float fall = (age / 20 + phase[star]) % 1;
            VisualDraw.sprite(sprites, at, right, up, it[0], it[1] * (1 - fall), it[2], .22F, .22F, 0, VisualDraw.TWINKLE,
                    VisualDraw.glow(shown.colour(), strength * appear * Mth.sin(fall * Mth.PI) * .7F));
        }
    }
}
