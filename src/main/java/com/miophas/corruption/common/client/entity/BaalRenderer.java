package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.BaalApi;
import com.miophas.corruption.common.entity.Baal;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Draws the model at Baal's scale, with its eyes, heart and the brightest cracks glowing in the dark. */
public final class BaalRenderer extends ColossusRenderer<Baal, BaalModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/baal.png");
    public static final ResourceLocation GLOW = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/baal_eyes.png");

    public BaalRenderer(EntityRendererProvider.Context context) {
        super(context, new BaalModel(context.bakeLayer(BaalModel.LAYER_LOCATION)), BaalApi.scale(), BaalApi.heightBlocks());
        addLayer(new EyesLayer<>(this) { @Override public RenderType renderType() { return FarView.glow(GLOW); } });
    }

    @Override public ResourceLocation getTextureLocation(Baal entity) { return TEXTURE; }
}
