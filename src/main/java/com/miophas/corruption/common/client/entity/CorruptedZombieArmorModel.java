package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

/** Vanilla armor UVs with forearm and shin parts that follow the remnant's joints. */
public final class CorruptedZombieArmorModel extends CorruptedHumanoidArmorModel<Zombie> {
    public static final ModelLayerLocation INNER = layer("inner_armor");
    public static final ModelLayerLocation OUTER = layer("outer_armor");
    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted_zombie"), name);
    }

    public CorruptedZombieArmorModel(ModelPart root) { super(root); }

    public static LayerDefinition createLayer(float inflation) {
        return CorruptedHumanoidArmorModel.createLayer(inflation, false);
    }
}
