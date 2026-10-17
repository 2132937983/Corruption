package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.client.visual.DeathVisual;
import com.miophas.corruption.common.entity.Decarabia;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraft.util.Mth;

/**
 * Draws Decarabia: the star, or once he has changed the goshawk (the two shapes share one texture), and then what of him
 * glows (his eye, the star's rim and runes and points, the ring; the goshawk's eyes) drawn over him again, unlit; as big
 * as his scale makes him (his likenesses, and he among them, small); the goshawk tipped as he flies; shuddering through
 * his change and his stun; his death drawn about him as he dies ({@link DeathVisual}), and at the end of it his body gone.
 */
public final class DecarabiaRenderer extends MobRenderer<Decarabia, HierarchicalModel<Decarabia>> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/decarabia.png");
    public static final ResourceLocation GLOW = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/decarabia_glow.png");
    private final DecarabiaStarModel star;
    private final DecarabiaHawkModel hawk;
    /** Each Decarabia's turning things, for as long as he is about. */
    private final Map<Decarabia, DecarabiaWinding> windings = new WeakHashMap<>();

    public DecarabiaRenderer(EntityRendererProvider.Context context) {
        super(context, new DecarabiaStarModel(context.bakeLayer(DecarabiaStarModel.LAYER_LOCATION)), .9F);
        star = (DecarabiaStarModel) model;
        hawk = new DecarabiaHawkModel(context.bakeLayer(DecarabiaHawkModel.LAYER_LOCATION));
        addLayer(new Glow(this));
    }

    @Override public ResourceLocation getTextureLocation(Decarabia entity) { return TEXTURE; }
    @Override protected float getFlipDegrees(Decarabia entity) { return 0; }

    @Override public void render(Decarabia entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        DecarabiaWinding winding = windings.computeIfAbsent(entity, key -> new DecarabiaWinding());
        winding.wind(entity, partial);
        star.winding = winding;
        hawk.winding = winding;
        model = entity.hawk() ? hawk : star;
        if (entity.deathTime < entity.death().ticks()) super.render(entity, yaw, partial, pose, buffers, light);
        if (entity.deathTime > 0) DeathVisual.draw(DeathVisual.Dying.of(entity, partial, entityRenderDispatcher.cameraOrientation()), pose, buffers, false);
    }

    /** The goshawk is tipped as his flight is (nose down in a stoop, up as he climbs), about the middle of him. */
    @Override protected void setupRotations(Decarabia entity, PoseStack pose, float bob, float yaw, float partial, float scale) {
        super.setupRotations(entity, pose, bob, yaw, partial, scale);
        float pitch = Mth.lerp(partial, entity.xRotO, entity.getXRot());
        if (!entity.hawk() || pitch == 0) return;
        float middle = entity.getBbHeight() * .5F;
        pose.translate(0, middle, 0);
        pose.mulPose(Axis.XP.rotationDegrees(-pitch));
        pose.translate(0, -middle, 0);
    }

    /** No shadow once his body is gone; a smaller one for a small star. */
    @Override protected float getShadowRadius(Decarabia entity) {
        return entity.deathTime >= entity.death().ticks() ? 0 : (entity.hawk() ? .8F : .9F) * entity.getScale();
    }

    /** He shudders through his change, and hanging stunned. */
    @Override protected boolean isShaking(Decarabia entity) { return super.isShaking(entity) || entity.changing() > 0 || entity.stunned() > 0; }

    /** His glow texture, drawn over all of him again, unlit: only what is painted on it shows. */
    private static final class Glow extends RenderLayer<Decarabia, HierarchicalModel<Decarabia>> {
        Glow(RenderLayerParent<Decarabia, HierarchicalModel<Decarabia>> renderer) { super(renderer); }

        @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, Decarabia entity, float swing, float amount, float partial, float age, float yaw, float pitch) {
            getParentModel().renderToBuffer(poses, buffers.getBuffer(RenderType.eyes(GLOW)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }
}
