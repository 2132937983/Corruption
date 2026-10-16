package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.client.visual.DeathVisual;
import com.miophas.corruption.common.entity.Seere;
import com.miophas.corruption.core.demon.SeereRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.WeakHashMap;
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
 * Draws Seere, and then the pieces of him that glow again, unlit; tipped as he flies (nose up climbing, nose down in his
 * dive), about the middle of him; not at all while he is under the earth (only the ground trembling shows where he goes);
 * his death drawn about him as he dies ({@link DeathVisual}), and at the end of it his body gone. He does not tip over
 * as other creatures do: his own model lays him down.
 */
public final class SeereRenderer extends MobRenderer<Seere, SeereModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/seere.png");
    /** What of him glows (his eyes, his sigils, the tips of his feathers), painted alone: drawn over him again, unlit. */
    public static final ResourceLocation GLOW = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/seere_glow.png");
    /** Each Seere's wings, for as long as he is about. */
    private final Map<Seere, SeereWinding> windings = new WeakHashMap<>();

    public SeereRenderer(EntityRendererProvider.Context context) {
        super(context, new SeereModel(context.bakeLayer(SeereModel.LAYER_LOCATION)), 1.1F);
        addLayer(new Glow(this));
    }

    @Override public ResourceLocation getTextureLocation(Seere seere) { return TEXTURE; }
    @Override protected float getFlipDegrees(Seere seere) { return 0; }

    @Override public void render(Seere seere, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        SeereWinding winding = windings.computeIfAbsent(seere, key -> new SeereWinding());
        winding.wind(seere, partial);
        model.winding = winding;
        if (!buried(seere) && seere.deathTime < seere.death().ticks()) super.render(seere, yaw, partial, pose, buffers, light);
        if (seere.deathTime > 0) DeathVisual.draw(DeathVisual.Dying.of(seere, partial, entityRenderDispatcher.cameraOrientation()), pose, buffers, false);
    }

    /** Under the earth, there is nothing of him to draw. */
    static boolean buried(Seere seere) { return seere.mode() == SeereRules.Mode.UNDERGROUND && !seere.skill().shift() && seere.deathTime == 0; }

    /** In the air (or diving out of it), he is tipped as his flight is, about the middle of him. */
    @Override protected void setupRotations(Seere seere, PoseStack pose, float bob, float yaw, float partial, float scale) {
        super.setupRotations(seere, pose, bob, yaw, partial, scale);
        float pitch = Mth.lerp(partial, seere.xRotO, seere.getXRot());
        if (seere.mode() != SeereRules.Mode.SKY && seere.skill() != SeereRules.Skill.DIVE || pitch == 0) return;
        float middle = seere.getBbHeight() * .5F;
        pose.translate(0, middle, 0);
        pose.mulPose(Axis.XP.rotationDegrees(-pitch));
        pose.translate(0, -middle, 0);
    }

    /** No shadow under the earth, nor once his body is gone. */
    @Override protected float getShadowRadius(Seere seere) {
        return buried(seere) || seere.deathTime >= seere.death().ticks() ? 0 : 1.1F;
    }

    /** His glow texture, drawn over all of him again, unlit: only what is painted on it shows. */
    private static final class Glow extends RenderLayer<Seere, SeereModel> {
        Glow(RenderLayerParent<Seere, SeereModel> renderer) { super(renderer); }

        @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, Seere seere, float swing, float amount, float partial, float age, float yaw, float pitch) {
            getParentModel().renderToBuffer(poses, buffers.getBuffer(RenderType.eyes(GLOW)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }
}
