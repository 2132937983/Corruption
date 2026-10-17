package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.client.visual.DeathVisual;
import com.miophas.corruption.common.entity.Amdusias;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Draws Amdusias, and then the pieces of him that glow again, unlit (his eyes, his sigils, the spiral of his horn, the tips
 * of his hair); his death drawn about him as he dies ({@link DeathVisual}), and at the end of it his body gone. He does not
 * tip over as other creatures do: his own model lays him down.
 */
public final class AmdusiasRenderer extends MobRenderer<Amdusias, AmdusiasModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/amdusias.png");
    /** What of him glows, painted alone: drawn over him again, unlit. */
    public static final ResourceLocation GLOW = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/amdusias_glow.png");

    public AmdusiasRenderer(EntityRendererProvider.Context context) {
        super(context, new AmdusiasModel(context.bakeLayer(AmdusiasModel.LAYER_LOCATION)), 1.1F);
        addLayer(new Glow(this));
    }

    @Override public ResourceLocation getTextureLocation(Amdusias amdusias) { return TEXTURE; }
    @Override protected float getFlipDegrees(Amdusias amdusias) { return 0; }

    @Override public void render(Amdusias amdusias, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        if (amdusias.deathTime < amdusias.death().ticks()) super.render(amdusias, yaw, partial, pose, buffers, light);
        if (amdusias.deathTime > 0) DeathVisual.draw(DeathVisual.Dying.of(amdusias, partial, entityRenderDispatcher.cameraOrientation()), pose, buffers, false);
    }

    /** No shadow once his body is gone. */
    @Override protected float getShadowRadius(Amdusias amdusias) { return amdusias.deathTime >= amdusias.death().ticks() ? 0 : 1.1F; }

    /** His glow texture, drawn over all of him again, unlit: only what is painted on it shows. */
    private static final class Glow extends RenderLayer<Amdusias, AmdusiasModel> {
        Glow(RenderLayerParent<Amdusias, AmdusiasModel> renderer) { super(renderer); }

        @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, Amdusias amdusias, float swing, float amount, float partial, float age,
                                     float yaw, float pitch) {
            getParentModel().renderToBuffer(poses, buffers.getBuffer(RenderType.eyes(GLOW)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }
}
