package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.entity.SolomonHand;
import com.miophas.corruption.core.demon.skill.HandRules;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
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
 * Draws a giant hand as its life goes ({@link HandRules}): pushing up out of the ground, rearing back with its fingers hooked
 * like claws, then coming down flat on the place it stands over, palm first and fingers spread; then sinking back into the
 * ground. Its veins, its seal and its knuckles burn in the light it was raised in.
 */
public final class SolomonHandRenderer extends EntityRenderer<SolomonHand> {
    private final ModelPart root, arm, hand, thumb;
    private final List<ModelPart> fingers = new ArrayList<>(), tips = new ArrayList<>();
    private final float armY;

    public SolomonHandRenderer(EntityRendererProvider.Context context) {
        super(context);
        root = context.bakeLayer(SolomonModels.HAND);
        Map<String, ModelPart> parts = GeoModel.load("solomon_hand").parts(root);
        arm = parts.get("arm");
        hand = parts.get("hand");
        thumb = parts.get("thumb");
        for (int finger = 0; finger < 4; finger++) {
            fingers.add(parts.get("finger_" + finger));
            tips.add(parts.get("finger_tip_" + finger));
        }
        armY = arm.y;
        shadowRadius = 1.5F;
    }

    @Override public ResourceLocation getTextureLocation(SolomonHand solomonHand) { return SolomonModels.HAND_TEXTURE; }

    @Override public void render(SolomonHand solomonHand, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        HandRules rules = solomonHand.rules();
        float age = solomonHand.age(partial);
        float grown = (float) rules.grown(age), reared = (float) rules.reared(age);
        // Rising and sinking, it is pushed down into the ground; reared, it stands upright; falling, it swings down flat.
        arm.y = armY + (1 - grown) * 48;
        arm.xRot = (1 - reared) * Mth.HALF_PI * (age < rules.rise() ? 0 : 1);
        hand.xRot = -.5F * reared;
        float curl = .9F * reared;
        // Reared, its fingers hook in towards its palm, a claw; as it falls they straighten.
        for (int finger = 0; finger < fingers.size(); finger++) {
            fingers.get(finger).xRot = curl * .6F;
            tips.get(finger).xRot = curl;
        }
        thumb.zRot = .6F - .3F * reared;
        pose.pushPose();
        SolomonModels.place(pose, 0, 0, 0, solomonHand.getYRot(), 0, SolomonModels.HALF, SolomonModels.HALF);
        root.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(SolomonModels.HAND_TEXTURE)), light, OverlayTexture.NO_OVERLAY);
        root.render(pose, buffers.getBuffer(RenderType.eyes(SolomonModels.HAND_GLOW)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                0xFF000000 | solomonHand.colour());
        pose.popPose();
        super.render(solomonHand, yaw, partial, pose, buffers, light);
    }
}
