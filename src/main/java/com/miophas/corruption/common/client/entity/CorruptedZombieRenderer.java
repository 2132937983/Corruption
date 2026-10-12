package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

/** Dedicated appearance for remnants of ordinary zombies, including baby variants and equipment. */
public final class CorruptedZombieRenderer extends HumanoidMobRenderer<Zombie, CorruptedZombieModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            Corruption.MOD_ID, "textures/entity/corrupted_zombie.png");

    public CorruptedZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedZombieModel(context.bakeLayer(CorruptedZombieModel.LAYER_LOCATION)), 0.5F);
        addLayer(new HumanoidArmorLayer<>(this,
                new CorruptedZombieArmorModel(context.bakeLayer(CorruptedZombieArmorModel.INNER)),
                new CorruptedZombieArmorModel(context.bakeLayer(CorruptedZombieArmorModel.OUTER)), context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie entity) {
        return TEXTURE;
    }
}
