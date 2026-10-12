package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.EnderMan;

public final class CorruptedEndermanRenderer extends MobRenderer<EnderMan, CorruptedEndermanModel> {
    public static final ResourceLocation TEXTURE = texture("corrupted_enderman.png");
    public static final ResourceLocation EYES = texture("corrupted_enderman_eyes.png");
    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/" + name);
    }

    public CorruptedEndermanRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedEndermanModel(context.bakeLayer(CorruptedEndermanModel.LAYER_LOCATION)), 0.5F);
        addLayer(new EyesLayer<EnderMan, CorruptedEndermanModel>(this) {
            @Override public RenderType renderType() { return RenderType.eyes(EYES); }
        });
        addLayer(new RenderLayer<EnderMan, CorruptedEndermanModel>(this) {
            @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, EnderMan entity,
                                         float swing, float amount, float partial, float age, float yaw, float pitch) {
                if (entity.getCarriedBlock() == null || entity.isInvisible()) return;
                poses.pushPose();
                getParentModel().translateToRightHand(poses);
                poses.scale(-0.5F, -0.5F, 0.5F);
                poses.translate(-0.5F, -0.85F, -0.5F);
                context.getBlockRenderDispatcher().renderSingleBlock(entity.getCarriedBlock(), poses, buffers, light, OverlayTexture.NO_OVERLAY);
                poses.popPose();
            }
        });
    }

    @Override public ResourceLocation getTextureLocation(EnderMan entity) { return TEXTURE; }
}
