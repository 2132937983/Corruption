package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Skeleton;

/** Dedicated appearance for remnants of ordinary skeletons, with their original equipment. */
public final class CorruptedSkeletonRenderer extends HumanoidMobRenderer<Skeleton, CorruptedSkeletonModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            Corruption.MOD_ID, "textures/entity/corrupted_skeleton.png");

    public CorruptedSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedSkeletonModel(context.bakeLayer(CorruptedSkeletonModel.LAYER_LOCATION)), 0.5F);
        addLayer(new HumanoidArmorLayer<>(this,
                new CorruptedSkeletonArmorModel(context.bakeLayer(CorruptedSkeletonArmorModel.INNER)),
                new CorruptedSkeletonArmorModel(context.bakeLayer(CorruptedSkeletonArmorModel.OUTER)), context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(Skeleton entity) { return TEXTURE; }
}
