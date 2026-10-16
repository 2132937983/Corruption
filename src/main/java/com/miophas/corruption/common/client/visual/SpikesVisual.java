package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * Spikes bursting up out of the ground, as a wave of ice or of bone or of crystal is drawn: ring after ring of them
 * erupts outward from the middle, each shooting up past its height and settling, leaning out, a flash at its foot;
 * they are faceted, glowing at their edges, brightest at their points; at the end they sink back into the ground.
 */
public final class SpikesVisual implements SpellVisual {
    /** Ticks a spike takes to burst up, and to sink. */
    private static final float BURSTING = 5;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = shown.radius();
        if (strength <= 0 || radius <= 0) return;
        PoseStack.Pose at = pose.last();
        Random random = new Random(shown.seed());
        int rings = Mth.clamp(Math.round(radius / 1.5F), 1, 5);
        float tallest = Mth.clamp(shown.height(), 1, 6), sinking = VisualDraw.within(shown.progress(), .75F, .25F);

        VertexConsumer facets = VisualDraw.light(buffers, VisualDraw.FACET);
        float[] flashX = new float[256], flashZ = new float[256], flashLight = new float[256];
        int flashCount = 0;
        for (int ring = 0; ring < rings; ring++) {
            float far = radius * (ring + 1) / rings;
            int count = Mth.clamp(Math.round(Mth.TWO_PI * far / 1.1F), 5, 28);
            for (int spike = 0; spike < count; spike++) {
                float angle = Mth.TWO_PI * (spike + random.nextFloat() * .6F) / count + ring * .7F, here = far * (.85F + .15F * random.nextFloat());
                float tall = tallest * (.5F + .5F * random.nextFloat()) * (1 - .3F * ring / rings), base = .18F + .16F * random.nextFloat();
                float lean = .25F * random.nextFloat(), erupts = 2 + 10 * far / radius + 2 * random.nextFloat();
                float grown = VisualDraw.easeOutBack(VisualDraw.within(age, erupts, BURSTING)) * (1 - sinking);
                if (grown <= 0) continue;
                float outX = Mth.cos(angle), outZ = Mth.sin(angle), x = outX * here, z = outZ * here;
                float apexX = x + outX * lean * tall * grown, apexY = tall * grown, apexZ = z + outZ * lean * tall * grown;
                // The foot: a square turned to face out, a little under the ground.
                float[] cornerX = {x + outX * base, x - outZ * base, x - outX * base, x + outZ * base};
                float[] cornerZ = {z + outZ * base, z + outX * base, z - outZ * base, z - outX * base};
                int foot = VisualDraw.glow(shown.colour(), strength * .5F), point = VisualDraw.glow(shown.accent(), strength);
                for (int side = 0; side < 4; side++) {
                    int next = (side + 1) % 4;
                    VisualDraw.triangle(facets, at,
                            cornerX[side], -.05F, cornerZ[side], 0, 1, foot,
                            cornerX[next], -.05F, cornerZ[next], 1, 1, foot,
                            apexX, apexY, apexZ, .5F, 0, point);
                }
                float flash = 1 - VisualDraw.within(age, erupts, 6);
                if (flash > 0 && flashCount < flashX.length) {
                    flashX[flashCount] = x;
                    flashZ[flashCount] = z;
                    flashLight[flashCount++] = flash * strength;
                }
            }
        }
        if (flashCount == 0) return;
        VertexConsumer flashes = VisualDraw.light(buffers, VisualDraw.GLOW);
        Vector3f east = new Vector3f(1, 0, 0), south = new Vector3f(0, 0, 1);
        for (int flash = 0; flash < flashCount; flash++)
            VisualDraw.discIn(flashes, at, flashX[flash], VisualDraw.LIFT, flashZ[flash], east, south, .9F, 0, VisualDraw.glow(shown.accent(), flashLight[flash]));
    }
}
