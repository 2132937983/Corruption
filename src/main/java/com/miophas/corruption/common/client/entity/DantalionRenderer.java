package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.client.visual.DeathVisual;
import com.miophas.corruption.common.entity.Dantalion;
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
 * Draws Dantalion: the book, or once he has changed the mirror (the two shapes share one texture), and then the pieces
 * of it that glow again, unlit; shuddering through his change; his death drawn about him as he dies
 * ({@link DeathVisual}), and at the end of it his body gone. He does not tip over as other creatures do.
 */
public final class DantalionRenderer extends MobRenderer<Dantalion, HierarchicalModel<Dantalion>> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/dantalion.png");
    private final DantalionBookModel book;
    private final DantalionMirrorModel mirror;
    /** Each Dantalion's turning things, for as long as he is about. */
    private final Map<Dantalion, DantalionWinding> windings = new WeakHashMap<>();

    public DantalionRenderer(EntityRendererProvider.Context context) {
        super(context, new DantalionBookModel(context.bakeLayer(DantalionBookModel.LAYER_LOCATION)), .9F);
        book = (DantalionBookModel) model;
        mirror = new DantalionMirrorModel(context.bakeLayer(DantalionMirrorModel.LAYER_LOCATION));
        addLayer(new Glow(this));
    }

    @Override public ResourceLocation getTextureLocation(Dantalion entity) { return TEXTURE; }
    @Override protected float getFlipDegrees(Dantalion entity) { return 0; }

    @Override public void render(Dantalion entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        DantalionWinding winding = windings.computeIfAbsent(entity, key -> new DantalionWinding());
        winding.wind(entity, partial);
        book.winding = winding;
        mirror.winding = winding;
        model = entity.mirror() ? mirror : book;
        if (entity.deathTime < entity.death().ticks()) super.render(entity, yaw, partial, pose, buffers, light);
        if (entity.deathTime > 0) DeathVisual.draw(DeathVisual.Dying.of(entity, partial, entityRenderDispatcher.cameraOrientation()), pose, buffers, false);
    }

    /** No shadow once his body is gone. */
    @Override protected float getShadowRadius(Dantalion entity) {
        return entity.deathTime >= entity.death().ticks() ? 0 : entity.mirror() ? .7F : .9F;
    }

    /** He shudders through his change. */
    @Override protected boolean isShaking(Dantalion entity) { return super.isShaking(entity) || entity.changing() > 0; }

    /** The glowing pieces are their own bones in the model, so they need no second texture: the same one is drawn again over them. */
    private static final class Glow extends RenderLayer<Dantalion, HierarchicalModel<Dantalion>> {
        Glow(RenderLayerParent<Dantalion, HierarchicalModel<Dantalion>> renderer) { super(renderer); }

        @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, Dantalion entity, float swing, float amount, float partial, float age, float yaw, float pitch) {
            HierarchicalModel<Dantalion> shape = getParentModel();
            glowOnly(shape, true);
            shape.renderToBuffer(poses, buffers.getBuffer(RenderType.eyes(TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            glowOnly(shape, false);
        }

        private static void glowOnly(HierarchicalModel<Dantalion> shape, boolean only) {
            if (shape instanceof DantalionBookModel book) book.glowOnly(only);
            else if (shape instanceof DantalionMirrorModel mirror) mirror.glowOnly(only);
        }
    }
}
