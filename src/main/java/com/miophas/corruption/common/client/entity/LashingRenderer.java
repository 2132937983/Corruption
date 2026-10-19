package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.api.elite.CorruptionElite;
import com.miophas.corruption.common.entity.Lashing;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Draws an elite that lashes out (Lashing) as every elite is drawn, and its lash from where it leaves it to what it reaches
 * for or holds: piece after piece of it along the way, a wave running down it and sagging a little, its end at the end.
 * Through the lash's wind-up it reaches out only so far, the further the later.
 */
public class LashingRenderer<T extends CorruptionElite<T> & Lashing, M extends EliteModel<T> & LashModel> extends CorruptionEliteRenderer<T, M> {
    /** How long a piece of the lash is, in blocks (four pixels). */
    static final double PIECE = .25;

    protected LashingRenderer(EntityRendererProvider.Context context, M model, float shadow, String name, boolean tipped) {
        super(context, model, shadow, name, tipped);
    }

    @Override public void render(T elite, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        super.render(elite, yaw, partial, pose, buffers, light);
        Entity reaching = elite.reaching();
        if (reaching == null || elite.deathTime > 0) return;
        Vec3 at = elite.getPosition(partial);
        float turn = Mth.rotLerp(partial, elite.yBodyRotO, elite.yBodyRot) * Mth.DEG_TO_RAD;
        Vec3 forward = new Vec3(-Mth.sin(turn), 0, Mth.cos(turn)), right = new Vec3(-forward.z, 0, forward.x);
        double[] from = elite.lashFrom();
        Vec3 start = at.add(right.scale(from[0])).add(0, from[1], 0).add(forward.scale(from[2]));
        Vec3 end = reaching.getPosition(partial).add(0, reaching.getBbHeight() * .5, 0);
        lash(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(elite))), light, start.subtract(at), end.subtract(at),
                elite.lashOut(partial), elite.tickCount + partial);
    }

    /** The lash from {@code from} towards {@code to} (both from the elite's feet), {@code out} of the way there. */
    private void lash(PoseStack pose, VertexConsumer buffer, int light, Vec3 from, Vec3 to, float out, float age) {
        Vec3 line = to.subtract(from);
        double length = line.length() * out;
        if (length < 1e-3) return;
        Vec3 along = line.normalize(), side = along.cross(new Vec3(0, 1, 0));
        side = side.lengthSqr() < 1e-6 ? new Vec3(1, 0, 0) : side.normalize();
        int pieces = Math.max(1, (int) Math.ceil(length / PIECE));
        Vec3 last = from;
        for (int piece = 1; piece <= pieces; piece++) {
            double share = Math.min(1, piece * PIECE / length);
            // A wave down it, and a little sag, both fading to nothing at its ends.
            double hold = Math.sin(Math.PI * share);
            Vec3 next = from.add(along.scale(length * share)).add(side.scale(Math.sin(piece * .55 - age * .7) * .15 * hold)).add(0, -.25 * hold, 0);
            draw(pose, buffer, light, model.lash(), last, next, true);
            last = next;
        }
        draw(pose, buffer, light, model.lashEnd(), last, last.add(along.scale(.1)), false);
    }

    /** One piece of the lash (or its end), from one place to the next, turned along the way between them; stretched to reach the next, or not. */
    private static void draw(PoseStack pose, VertexConsumer buffer, int light, ModelPart part, Vec3 from, Vec3 to, boolean stretch) {
        Vec3 way = to.subtract(from);
        double length = way.length();
        if (length < 1e-4) return;
        pose.pushPose();
        pose.translate(from.x, from.y, from.z);
        pose.mulPose(Axis.YP.rotation((float) Math.atan2(way.x, way.z)));
        pose.mulPose(Axis.XP.rotation((float) -Math.asin(Mth.clamp(way.y / length, -1, 1))));
        if (stretch) pose.scale(1, 1, (float) Math.max(.5, length / PIECE));
        part.visible = true;
        part.render(pose, buffer, light, OverlayTexture.NO_OVERLAY);
        part.visible = false;
        pose.popPose();
    }
}
