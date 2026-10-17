package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.client.visual.DeathVisual;
import com.miophas.corruption.common.entity.Belial;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.model.HierarchicalModel;
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
 * Draws Belial: the fair angel, or once he has risen again the hollow (the two share one texture), and then what of him
 * glows drawn over him again, unlit: the fair angel's halo, the gold of his stole and the rose of his tear and his palms;
 * the hollow angel's face of fire, the cracks of fire through him, his crown's edges, the eyes on his wings. He shudders
 * through his change and his stun; his death is drawn about him as he dies ({@link DeathVisual}), and at the end of it
 * his body is gone. He does not tip over as other creatures do.
 */
public final class BelialRenderer extends MobRenderer<Belial, HierarchicalModel<Belial>> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/belial.png");
    public static final ResourceLocation GLOW = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/belial_glow.png");
    private final BelialFairModel fair;
    private final BelialHollowModel hollow;
    /** Each Belial's wings, for as long as he is about. */
    private final Map<Belial, BelialWinding> windings = new WeakHashMap<>();

    public BelialRenderer(EntityRendererProvider.Context context) {
        super(context, new BelialFairModel(context.bakeLayer(BelialFairModel.LAYER_LOCATION)), 1F);
        fair = (BelialFairModel) model;
        hollow = new BelialHollowModel(context.bakeLayer(BelialHollowModel.LAYER_LOCATION));
        addLayer(new Glow(this));
    }

    @Override public ResourceLocation getTextureLocation(Belial entity) { return TEXTURE; }
    @Override protected float getFlipDegrees(Belial entity) { return 0; }

    @Override public void render(Belial entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        BelialWinding winding = windings.computeIfAbsent(entity, key -> new BelialWinding());
        winding.wind(entity, partial);
        fair.winding = winding;
        hollow.winding = winding;
        model = entity.hollow() ? hollow : fair;
        if (entity.deathTime < entity.death().ticks()) super.render(entity, yaw, partial, pose, buffers, light);
        if (entity.deathTime > 0) DeathVisual.draw(DeathVisual.Dying.of(entity, partial, entityRenderDispatcher.cameraOrientation()), pose, buffers, false);
    }

    /** No shadow once his body is gone. */
    @Override protected float getShadowRadius(Belial entity) { return entity.deathTime >= entity.death().ticks() ? 0 : 1F; }

    /** He shudders through his change, and hanging stunned. */
    @Override protected boolean isShaking(Belial entity) { return super.isShaking(entity) || entity.changing() > 0 || entity.stunned() > 0; }

    /** His glow texture, drawn over all of him again, unlit: only what is painted on it shows. */
    private static final class Glow extends RenderLayer<Belial, HierarchicalModel<Belial>> {
        Glow(RenderLayerParent<Belial, HierarchicalModel<Belial>> renderer) { super(renderer); }

        @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, Belial entity, float swing, float amount, float partial, float age, float yaw, float pitch) {
            getParentModel().renderToBuffer(poses, buffers.getBuffer(RenderType.eyes(GLOW)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }
}
