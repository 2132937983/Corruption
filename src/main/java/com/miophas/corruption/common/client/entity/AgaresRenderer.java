package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.AgaresApi;
import com.miophas.corruption.common.entity.Agares;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Draws Agares, then its eyes, its veins and the fissure in its breast again, unlit, so they glow in the dark. */
public final class AgaresRenderer extends ColossusRenderer<Agares, AgaresModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/agares.png");

    public AgaresRenderer(EntityRendererProvider.Context context) {
        super(context, new AgaresModel(context.bakeLayer(AgaresModel.LAYER_LOCATION)), AgaresApi.scale(), AgaresApi.heightBlocks());
        addLayer(new Glow(this));
    }

    @Override public ResourceLocation getTextureLocation(Agares entity) { return TEXTURE; }

    /** The glowing pieces are their own bones in the model, so they need no second texture: the same one is drawn again over them. */
    private static final class Glow extends RenderLayer<Agares, AgaresModel> {
        Glow(RenderLayerParent<Agares, AgaresModel> renderer) { super(renderer); }

        @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, Agares entity, float swing, float amount, float partial, float age, float yaw, float pitch) {
            AgaresModel model = getParentModel();
            model.glowOnly(true);
            model.renderToBuffer(poses, buffers.getBuffer(FarView.glow(TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            model.glowOnly(false);
        }
    }
}
