package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.SolomonDrake;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
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
 * Draws one of Solomon's drakes: hanging where it was sent from, beating its wings and snapping its jaw, then rushing on with
 * its wings swept back and its tail streaming; its eyes and the edges of its wings burning in the light it was sent in.
 */
public final class SolomonDrakeRenderer extends EntityRenderer<SolomonDrake> {
    /** How much larger than it is built it is drawn. */
    private static final float SIZE = 1.25F;
    private final ModelPart root, wingLeft, wingRight, tipLeft, tipRight, jaw, tail0, tail1;

    public SolomonDrakeRenderer(EntityRendererProvider.Context context) {
        super(context);
        root = context.bakeLayer(SolomonModels.DRAKE);
        Map<String, ModelPart> parts = GeoModel.load("solomon_drake").parts(root);
        wingLeft = parts.get("wing_left");
        wingRight = parts.get("wing_right");
        tipLeft = parts.get("wingtip_left");
        tipRight = parts.get("wingtip_right");
        jaw = parts.get("drake_jaw");
        tail0 = parts.get("tail_0");
        tail1 = parts.get("tail_1");
        shadowRadius = 0;
    }

    @Override public ResourceLocation getTextureLocation(SolomonDrake drake) { return SolomonModels.DRAKE_TEXTURE; }

    @Override public void render(SolomonDrake drake, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        float age = drake.age(partial);
        boolean rushing = drake.rushing();
        float beat = rushing ? .15F * Mth.sin(age * .8F) - .5F : .7F * Mth.sin(age * .9F);
        wingLeft.zRot = beat;
        wingRight.zRot = -beat;
        tipLeft.zRot = beat * .6F;
        tipRight.zRot = -beat * .6F;
        jaw.xRot = .2F + .25F * Mth.sin(age * .5F);
        tail0.yRot = .25F * Mth.sin(age * .4F);
        tail1.yRot = .35F * Mth.sin(age * .4F - .8F);
        pose.pushPose();
        SolomonModels.place(pose, 0, drake.getBbHeight() / 2, 0, drake.getYRot(), drake.getXRot(), SIZE, SIZE);
        root.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(SolomonModels.DRAKE_TEXTURE)), light, OverlayTexture.NO_OVERLAY);
        root.render(pose, buffers.getBuffer(RenderType.eyes(SolomonModels.DRAKE_GLOW)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFF000000 | drake.colour());
        pose.popPose();
        super.render(drake, yaw, partial, pose, buffers, light);
    }
}
