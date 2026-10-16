package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * Great wings of light spreading behind the caster, as an angel's or a phoenix's are drawn when they reveal
 * themselves: folded at first, they sweep open past their span and settle, beat slowly, shed glowing feathers that
 * drift down, and fold a little as they fade. Each wing is two rows of feathers on an arm running up and out from
 * the shoulder, long flight feathers at its tip, short ones nearer the body, a row of coverts over them; behind
 * the shoulders a soft light. They turn with the way the caster faced; {@code height} is where the shoulders are,
 * {@code radius} how far each wing reaches.
 */
public final class WingsVisual implements SpellVisual {
    /** Feathers in each row of a wing, and feathers drifting down from both. */
    private static final int FEATHERS = 14, SHED = 12;
    /** Ticks they take to spread. */
    private static final float SPREADING = 12;

    @Override public void draw(Shown shown, PoseStack pose, MultiBufferSource buffers) {
        float strength = shown.strength(), age = shown.age(), span = shown.radius();
        if (strength <= 0 || span <= 0) return;
        PoseStack.Pose at = pose.last();
        float lookX = VisualDraw.lookX(shown.yaw()), lookZ = VisualDraw.lookZ(shown.yaw());
        // The caster's right hand, along the ground; behind them; their shoulders.
        float sideX = -lookZ, sideZ = lookX, backX = -lookX, backZ = -lookZ;
        float shoulder = Math.max(.6F, shown.height());
        float open = VisualDraw.easeOutBack(age / SPREADING) * (.65F + .35F * strength);
        float beat = Mth.sin(Math.max(0, age - SPREADING) * .3F) * .22F * Mth.clamp(open, 0, 1);

        VertexConsumer glow = VisualDraw.light(buffers, VisualDraw.GLOW);
        Vector3f side = new Vector3f(sideX, 0, sideZ), up = new Vector3f(0, 1, 0);
        VisualDraw.roundIn(glow, at, backX * .3F, shoulder, backZ * .3F, side, up, Math.min(shoulder, span * .55F) * Math.min(1, open), 0,
                VisualDraw.glow(shown.colour(), strength * .5F));

        VertexConsumer sprites = VisualDraw.light(buffers, VisualDraw.SPRITES);
        for (int hand = -1; hand <= 1; hand += 2) {
            // Swept back from straight out to the side, the more so the more folded, and rocking as it beats.
            float sweep = Mth.lerp(Mth.clamp(open, 0, 1), 1.35F, .35F) + beat * .6F;
            float outX = sideX * hand * Mth.cos(sweep) + backX * Mth.sin(sweep), outZ = sideZ * hand * Mth.cos(sweep) + backZ * Mth.sin(sweep);
            float rootX = backX * .3F + sideX * hand * .22F, rootZ = backZ * .3F + sideZ * hand * .22F;
            // The wing's flat: across it, the arm's way out crossed with up.
            float normalX = -outZ, normalZ = outX;
            wing(sprites, at, shown, rootX, shoulder, rootZ, outX, outZ, normalX, normalZ, span, open, beat, strength);
        }

        Vector3f right = shown.right(), facing = shown.up();
        for (int feather = 0; feather < SHED; feather++) {
            float hand = VisualDraw.hash(shown.seed(), feather, 0) < .5F ? -1 : 1, along = .3F + .7F * VisualDraw.hash(shown.seed(), feather, 1);
            float period = 26 + 20 * VisualDraw.hash(shown.seed(), feather, 2), t = (age / period + VisualDraw.hash(shown.seed(), feather, 3)) % 1;
            if (age < SPREADING) continue;
            float x = backX * .5F + sideX * hand * span * along * .8F, z = backZ * .5F + sideZ * hand * span * along * .8F;
            float y = shoulder + span * along * .3F - t * (shoulder + span * along * .3F) + .25F * Mth.sin(t * 9 + feather);
            float size = .3F + .15F * along;
            int colour = VisualDraw.glow(shown.accent(), strength * Mth.sin(t * Mth.PI) * .8F);
            VisualDraw.sprite(sprites, at, right, facing, x + Mth.sin(t * 6 + feather) * .3F, Math.max(.05F, y), z, size / 3, size,
                    Mth.sin(t * 7 + feather) * .8F, VisualDraw.FEATHER, colour);
        }
    }

    /** One wing: its flight feathers fanned out from the arm, and its coverts, shorter and brighter, over them. */
    private static void wing(VertexConsumer sprites, PoseStack.Pose at, Shown shown, float rootX, float rootY, float rootZ, float outX, float outZ,
                             float normalX, float normalZ, float span, float open, float beat, float strength) {
        float spread = Mth.clamp(open, 0, 1.15F);
        // The arm: up and out from the shoulder, to the wrist, where the longest feathers grow.
        float armUp = .45F + beat, armLength = span * .42F * Math.min(1, spread);
        float armX = outX * Mth.cos(armUp), armY = Mth.sin(armUp), armZ = outZ * Mth.cos(armUp);
        for (int row = 0; row < 2; row++) {
            for (int feather = 0; feather < FEATHERS; feather++) {
                float k = (float) feather / (FEATHERS - 1);
                float attach = Math.min(1, k * 1.5F) * armLength;
                // Fanned from straight down, by the body, to up and out at the tip; all down while folded.
                float angle = Mth.lerp(spread, -1.5F, Mth.lerp((float) Math.pow(k, 1.2), -1.45F, .7F)) + beat * k;
                float length = span * (row == 0 ? .38F + .42F * k : .2F + .2F * k) * Math.max(.3F, Math.min(1, spread));
                float dirX = outX * Mth.cos(angle), dirY = Mth.sin(angle), dirZ = outZ * Mth.cos(angle);
                float baseX = rootX + armX * attach, baseY = rootY + armY * attach, baseZ = rootZ + armZ * attach;
                // A feather hanging down reaches the ground at most, never into it.
                if (dirY < 0) length = Math.min(length, (baseY - .05F) / -dirY);
                if (length <= .05F) continue;
                float tipX = baseX + dirX * length, tipY = baseY + dirY * length, tipZ = baseZ + dirZ * length;
                // Across the feather and in the wing's flat.
                float wx = dirY * normalZ, wy = dirZ * normalX - dirX * normalZ, wz = -dirY * normalX;
                float across = Mth.sqrt(wx * wx + wy * wy + wz * wz);
                if (across < 1e-5F) continue;
                int colour = row == 0
                        ? VisualDraw.glow(VisualDraw.mix(shown.colour(), shown.accent(), k * .7F), strength * (.75F + .25F * k))
                        : VisualDraw.glow(shown.accent(), strength * .6F);
                VisualDraw.spriteAlong(sprites, at, tipX, tipY, tipZ, baseX, baseY, baseZ, wx / across, wy / across, wz / across, length / 3,
                        VisualDraw.FEATHER, colour);
            }
        }
    }
}
