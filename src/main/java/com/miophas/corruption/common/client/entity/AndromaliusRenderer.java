package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.client.visual.WardShell;
import com.miophas.corruption.common.entity.Andromalius;
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
 * Draws Andromalius; then his eyes, the eye on his brow, the marks on his hood and the embers along his sides
 * again, unlit, so they glow in the dark; and, while his ward stands, a shell of hexagons round him. He falls
 * as his model lays him down, not on his side as other creatures do.
 */
public final class AndromaliusRenderer extends MobRenderer<Andromalius, AndromaliusModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/andromalius.png");
    /** The ward's colours: the corruption's rose, and a pale gold where it is brightest. */
    private static final int WARD = 0xC6728C, WARD_ACCENT = 0xFFD9A0;

    public AndromaliusRenderer(EntityRendererProvider.Context context) {
        super(context, new AndromaliusModel(context.bakeLayer(AndromaliusModel.LAYER_LOCATION)), 1.4F);
        addLayer(new Glow(this));
    }

    @Override public ResourceLocation getTextureLocation(Andromalius entity) { return TEXTURE; }
    @Override protected float getFlipDegrees(Andromalius entity) { return 0; }

    @Override public void render(Andromalius serpent, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        super.render(serpent, yaw, partial, pose, buffers, light);
        float ward = serpent.wardStrength(partial);
        if (ward > 0) WardShell.draw(pose, buffers, 2.4F, 5F, 2.1F, WARD, WARD_ACCENT, serpent.tickCount + partial, ward);
    }

    /** The glowing pieces are their own bones in the model, so they need no second texture: the same one is drawn again over them. */
    private static final class Glow extends RenderLayer<Andromalius, AndromaliusModel> {
        Glow(RenderLayerParent<Andromalius, AndromaliusModel> renderer) { super(renderer); }

        @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, Andromalius entity, float swing, float amount, float partial, float age, float yaw, float pitch) {
            AndromaliusModel model = getParentModel();
            model.glowOnly(true);
            model.renderToBuffer(poses, buffers.getBuffer(RenderType.eyes(TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            model.glowOnly(false);
        }
    }
}
