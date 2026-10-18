package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.client.visual.DeathVisual;
import com.miophas.corruption.common.entity.Solomon;
import com.miophas.corruption.core.demon.SolomonRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Draws Solomon's seal: his crown of eight golden plates about a dark orb, turning slowly, the orb's tears and the gems of the
 * crown burning; and about it three rings of the octagram, turning each its own way, in his gold. Dying, his death is drawn
 * about it, and at the end of it the crown is gone.
 */
public final class SolomonRenderer extends EntityRenderer<Solomon> {
    private static final float RING = 7;
    private final ModelPart crown;

    public SolomonRenderer(EntityRendererProvider.Context context) {
        super(context);
        crown = context.bakeLayer(SolomonModels.CROWN);
        shadowRadius = 0;
    }

    @Override public ResourceLocation getTextureLocation(Solomon solomon) { return SolomonModels.CROWN_TEXTURE; }

    @Override public void render(Solomon solomon, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        float age = solomon.tickCount + partial;
        if (solomon.deathTime < solomon.death().ticks()) {
            float middle = solomon.getBbHeight() / 2;
            pose.pushPose();
            SolomonModels.place(pose, 0, middle + Mth.sin(age / 20) * .15, 0, Mth.rotLerp(partial, solomon.yRotO, solomon.getYRot()), 0, SolomonModels.HALF, SolomonModels.HALF);
            crown.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(SolomonModels.CROWN_TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            crown.render(pose, buffers.getBuffer(RenderType.eyes(SolomonModels.CROWN_GLOW)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            pose.popPose();
            // Dying, the rings turn ever faster.
            float speed = 1 + (solomon.deathTime > 0 ? (solomon.deathTime + partial) / 10 : 0);
            VertexConsumer seal = buffers.getBuffer(RenderType.eyes(SolomonModels.SEAL));
            ring(pose, seal, middle, age * .6F * speed, 0, RING);
            ring(pose, seal, middle, -age * .9F * speed, 90, RING * .8F);
            ring(pose, seal, middle, age * 1.2F * speed, 60, RING * .65F);
        }
        if (solomon.deathTime > 0) DeathVisual.draw(DeathVisual.Dying.of(solomon, partial, entityRenderDispatcher.cameraOrientation()), pose, buffers, false);
        super.render(solomon, yaw, partial, pose, buffers, light);
    }

    /** One ring of the octagram about his middle, tipped this far from level and turned this far about its own middle. */
    private static void ring(PoseStack pose, VertexConsumer buffer, float middle, float turn, float tip, float size) {
        int colour = 0xFF000000 | SolomonRules.GOLD;
        pose.pushPose();
        pose.translate(0, middle, 0);
        pose.mulPose(Axis.XP.rotationDegrees(tip));
        pose.mulPose(Axis.YP.rotationDegrees(turn));
        PoseStack.Pose at = pose.last();
        float half = size / 2;
        for (int side = 0; side < 2; side++) {
            float normal = side == 0 ? 1 : -1;
            float a = side == 0 ? -half : half, b = -a;
            buffer.addVertex(at, a, 0, -half).setColor(colour).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, normal, 0);
            buffer.addVertex(at, a, 0, half).setColor(colour).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, normal, 0);
            buffer.addVertex(at, b, 0, half).setColor(colour).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, normal, 0);
            buffer.addVertex(at, b, 0, -half).setColor(colour).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, normal, 0);
        }
        pose.popPose();
    }
}
