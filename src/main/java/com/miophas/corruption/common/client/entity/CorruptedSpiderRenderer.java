package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;

/** Dedicated spider-remnant appearance; vanilla spiders retain their renderer and eye layer. */
public final class CorruptedSpiderRenderer extends MobRenderer<Spider, CorruptedSpiderModel> {
    public static final ResourceLocation TEXTURE = texture("corrupted_spider.png");
    public static final ResourceLocation EYES = texture("corrupted_spider_eyes.png");
    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/" + name);
    }

    public CorruptedSpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedSpiderModel(context.bakeLayer(CorruptedSpiderModel.LAYER_LOCATION)), 0.8F);
        addLayer(new EyesLayer<Spider, CorruptedSpiderModel>(this) {
            @Override public RenderType renderType() { return RenderType.eyes(EYES); }
        });
    }

    @Override protected float getFlipDegrees(Spider entity) { return 180; }
    @Override public ResourceLocation getTextureLocation(Spider entity) { return TEXTURE; }
}
