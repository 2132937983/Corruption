package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.SpellProjectile;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Draws a spell bolt. One its spell draws as its own is {@link SpellBoltModel}: pointed the way it flies, as big as
 * the bolt is, its shell lit as the world is and its light unlit, in its spell's colour (the corruption's rose unless
 * the spell says otherwise), its heart nearly white-hot. One whose spell names an item to be drawn as (a fire charge,
 * say) is drawn as that item, as before, unlit.
 */
public final class SpellBoltRenderer extends EntityRenderer<SpellProjectile> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/spell_bolt.png");
    /**
     * The model is eleven pixels across, the ring about its point; drawn this much smaller, a bolt of the size bolts are
     * unless their spell says otherwise fills its half-block box, and a bigger one its bigger box.
     */
    static final float FIT = 8F / 11;
    /** How far the heart's colour is drawn from the spell's towards white. */
    static final float WHITE_HOT = .65F;

    private final SpellBoltModel model;
    private final ThrownItemRenderer<SpellProjectile> item;

    public SpellBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new SpellBoltModel(context.bakeLayer(SpellBoltModel.LAYER_LOCATION));
        item = new ThrownItemRenderer<>(context, 1.0F, true);
    }

    @Override public ResourceLocation getTextureLocation(SpellProjectile bolt) { return TEXTURE; }

    @Override public void render(SpellProjectile bolt, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        if (!bolt.drawnAsOwn()) {
            item.render(bolt, yaw, partial, pose, buffers, light);
            return;
        }
        pose.pushPose();
        pose.translate(0, bolt.getBbHeight() / 2, 0);
        // Its point is -Z: turned the way it flies, as a projectile's turn says (its yaw is along +Z at none), and tipped up as it climbs.
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial, bolt.yRotO, bolt.getYRot()) + 180));
        pose.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partial, bolt.xRotO, bolt.getXRot())));
        float scale = (float) (bolt.size() / SpellAction.LaunchBolt.DEFAULT_SIZE) * FIT;
        pose.scale(scale, scale, scale);
        // From the model's space (Y down, X mirrored, 24 pixels up) to the world's, as the game's living things are drawn.
        pose.scale(-1, -1, 1);
        pose.translate(0, -1.501F, 0);
        model.setupAnim(bolt, 0, 0, bolt.tickCount + partial, 0, 0);
        int colour = bolt.colour() < 0 ? SpellAction.Visual.ROSE : bolt.colour();
        model.only(SpellBoltModel.SHELL);
        model.renderToBuffer(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, OverlayTexture.NO_OVERLAY);
        model.only(SpellBoltModel.LIGHT);
        model.renderToBuffer(pose, buffers.getBuffer(RenderType.eyes(TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFF000000 | colour);
        model.only(SpellBoltModel.HEART);
        model.renderToBuffer(pose, buffers.getBuffer(RenderType.eyes(TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFF000000 | whiter(colour));
        model.only(-1);
        pose.popPose();
        super.render(bolt, yaw, partial, pose, buffers, light);
    }

    /** A colour drawn {@link #WHITE_HOT} of the way to white. */
    static int whiter(int colour) {
        int red = colour >> 16 & 255, green = colour >> 8 & 255, blue = colour & 255;
        return Math.round(red + (255 - red) * WHITE_HOT) << 16 | Math.round(green + (255 - green) * WHITE_HOT) << 8 | Math.round(blue + (255 - blue) * WHITE_HOT);
    }
}
