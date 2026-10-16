package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Shards of a mirror, as an illusionist's spell is drawn: glassy shards fly in from all round and gather in a ring
 * about the spot, circling it and tumbling, each glinting now and then as it catches the light; at the end they
 * burst outward and are gone.
 */
public final class ShardsVisual implements SpellVisual {
    /** Ticks they take to gather. */
    private static final float GATHERING = 14;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), radius = Math.max(.5F, shown.radius());
        if (strength <= 0) return;
        PoseStack.Pose at = pose.last();
        float gathered = VisualDraw.easeOutCubic(age / GATHERING), burst = VisualDraw.within(shown.progress(), .8F, .2F);
        float high = Math.max(1.2F, shown.height());

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        VisualDraw.disc(glow, at, radius * 1.2F, VisualDraw.LIFT, 0, VisualDraw.glow(shown.colour(), strength * .35F * gathered));

        VertexConsumer facets = VisualDraw.light(buffers, VisualDraw.FACET);
        Random random = new Random(shown.seed());
        int count = Mth.clamp(Math.round(radius * 5), 10, 30);
        Quaternionf tumble = new Quaternionf();
        Vector3f a = new Vector3f(), b = new Vector3f(), c = new Vector3f();
        float[] glints = new float[count * 4];
        for (int shard = 0; shard < count; shard++) {
            float orbit = radius * (.5F + .5F * random.nextFloat()), y = .8F + random.nextFloat() * (high - .8F), start = random.nextFloat() * Mth.TWO_PI;
            float speed = (random.nextBoolean() ? 1 : -1) * (.015F + .015F * random.nextFloat()), size = .45F + .5F * random.nextFloat();
            float axisX = random.nextFloat() - .5F, axisY = random.nextFloat() - .5F, axisZ = random.nextFloat() - .5F, spin = .05F + .1F * random.nextFloat();
            float lean = random.nextFloat() * .6F - .3F, phase = random.nextFloat() * Mth.TWO_PI, flash = .1F + .15F * random.nextFloat();
            float angle = start + age * speed, far = Mth.lerp(gathered, radius * 1.6F, orbit) + burst * burst * radius * 1.5F;
            float x = Mth.cos(angle) * far, z = Mth.sin(angle) * far, height = Math.max(size, y + burst * 1.5F * (y - high / 2));
            float axisLength = Mth.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
            tumble.rotationAxis(age * spin + phase, axisX / Math.max(1e-4F, axisLength), axisY / Math.max(1e-4F, axisLength), axisZ / Math.max(1e-4F, axisLength));
            tumble.transform(a.set(0, size, 0));
            tumble.transform(b.set(-size * .6F, -size * .5F, lean));
            tumble.transform(c.set(size * .7F, -size * .4F, -lean));
            // Glinting: dark mostly, flashing bright now and then as it turns to the light.
            float glint = (float) Math.pow(Math.max(0, Mth.sin(age * flash + phase)), 8);
            int colour = VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), glint), strength * (.5F + .5F * glint) * (1 - burst * .7F));
            VisualDraw.triangle(facets, at,
                    x + a.x, height + a.y, z + a.z, .5F, 0, colour,
                    x + b.x, height + b.y, z + b.z, 0, 1, colour,
                    x + c.x, height + c.y, z + c.z, 1, 1, colour);
            glints[shard * 4] = x + a.x;
            glints[shard * 4 + 1] = height + a.y;
            glints[shard * 4 + 2] = z + a.z;
            glints[shard * 4 + 3] = glint;
        }

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        Vector3f right = shown.right(), up = shown.up();
        for (int shard = 0; shard < count; shard++) {
            float glint = glints[shard * 4 + 3];
            if (glint < .3F) continue;
            VisualDraw.sprite(sprites, at, right, up, glints[shard * 4], glints[shard * 4 + 1], glints[shard * 4 + 2], .6F * glint, .6F * glint, age * .1F,
                    VisualDraw.TWINKLE, VisualDraw.glow(0xFFFFFF, strength * glint));
        }
        float bursting = 1 - VisualDraw.within(shown.progress(), .8F, .06F);
        if (burst <= 0 || bursting <= 0) return;
        float flash = radius * 1.5F * (1 - bursting * .5F);
        VisualDraw.sprite(sprites, at, right, up, 0, Math.max(high / 2 + .5F, flash * .55F), 0, flash, flash, 0, VisualDraw.TWINKLE,
                VisualDraw.glow(shown.accent(), strength * bursting));
    }
}
