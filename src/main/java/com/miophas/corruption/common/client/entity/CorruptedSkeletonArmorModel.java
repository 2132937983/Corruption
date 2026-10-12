package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Skeleton;

/** Segmented armor centered on the skeleton's thin limbs. */
public final class CorruptedSkeletonArmorModel extends CorruptedHumanoidArmorModel<Skeleton> {
    public static final ModelLayerLocation INNER = layer("inner_armor");
    public static final ModelLayerLocation OUTER = layer("outer_armor");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(
                Corruption.MOD_ID, "corrupted_skeleton"), name);
    }

    public CorruptedSkeletonArmorModel(ModelPart root) { super(root); }

    public static LayerDefinition createLayer(float inflation) {
        return CorruptedHumanoidArmorModel.createLayer(inflation, true);
    }
}
