package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.FieryChariot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Draws Belial's chariot of fire, larger than it is built ({@link #SIZE}): the chariot as the light falls on it, and then
 * its fire drawn over it again, unlit (the flames on its wheels, its horses, the gold of its front).
 */
public final class FieryChariotRenderer extends EntityRenderer<FieryChariot> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/fiery_chariot.png");
    public static final ResourceLocation GLOW = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/fiery_chariot_glow.png");
    /** How many times larger than it is built it is drawn. */
    public static final float SIZE = 1.6F;
    private final FieryChariotModel model;

    public FieryChariotRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new FieryChariotModel(context.bakeLayer(FieryChariotModel.LAYER_LOCATION));
        shadowRadius = 1.6F;
    }

    @Override public ResourceLocation getTextureLocation(FieryChariot chariot) { return TEXTURE; }

    @Override public void render(FieryChariot chariot, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180 - Mth.rotLerp(partial, chariot.yRotO, chariot.getYRot())));
        pose.scale(-SIZE, -SIZE, SIZE);
        pose.translate(0, -1.501, 0);
        model.setupAnim(chariot, 0, 0, chariot.tickCount + partial, 0, 0);
        model.renderToBuffer(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, OverlayTexture.NO_OVERLAY, -1);
        model.renderToBuffer(pose, buffers.getBuffer(RenderType.eyes(GLOW)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1);
        pose.popPose();
        super.render(chariot, yaw, partial, pose, buffers, light);
    }
}
