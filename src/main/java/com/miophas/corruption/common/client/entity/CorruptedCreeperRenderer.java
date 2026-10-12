package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public final class CorruptedCreeperRenderer extends MobRenderer<Creeper, CorruptedCreeperModel> {
    public static final ResourceLocation TEXTURE = texture("corrupted_creeper.png");
    public static final ResourceLocation EYES = texture("corrupted_creeper_eyes.png");
    private static ResourceLocation texture(String name) { return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/" + name); }
    public CorruptedCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedCreeperModel(context.bakeLayer(CorruptedCreeperModel.LAYER_LOCATION)), 0.55F);
    }
    @Override public ResourceLocation getTextureLocation(Creeper entity) { return TEXTURE; }
}
