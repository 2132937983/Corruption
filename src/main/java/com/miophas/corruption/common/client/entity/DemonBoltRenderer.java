package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.DemonBolt;
import com.miophas.corruption.core.demon.skill.SpitRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Draws a demon's bolt as a spell bolt is drawn (SpellBoltRenderer.drawOwn), in its colour; or, a feather (a screecher's),
 * as a feather flying point first: its sprite (the game's feather, gone the corruption's colours) laid along its flight on
 * two planes crossed, so it shows from every side.
 */
public final class DemonBoltRenderer extends EntityRenderer<DemonBolt> {
    public static final ResourceLocation FEATHER = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/entity/corrupted_feather.png");
    /** How long a feather is drawn, and how wide, in blocks. */
    private static final float LENGTH = .7F, WIDTH = .3F;
    private final SpellBoltModel model;

    public DemonBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new SpellBoltModel(context.bakeLayer(SpellBoltModel.LAYER_LOCATION));
    }

    @Override public ResourceLocation getTextureLocation(DemonBolt bolt) { return bolt.look() == SpitRules.Look.FEATHER ? FEATHER : SpellBoltRenderer.TEXTURE; }

    @Override public void render(DemonBolt bolt, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        if (bolt.look() == SpitRules.Look.FEATHER) feather(bolt, partial, pose, buffers, light);
        else SpellBoltRenderer.drawOwn(model, bolt, bolt.look() == SpitRules.Look.GLOB ? DemonBolt.SIZE * 1.3 : DemonBolt.SIZE, bolt.colour(), partial, pose, buffers, light);
        super.render(bolt, yaw, partial, pose, buffers, light);
    }

    /**
     * A feather along its flight, its point foremost. The sprite lies corner to corner (its quill at the lower left, its
     * point at the upper right), so each plane is the sprite's square turned onto its corner: the point ahead, the quill
     * behind, the other two corners to either side.
     */
    private static void feather(DemonBolt bolt, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.translate(0, bolt.getBbHeight() / 2, 0);
        // Its yaw turns +Z onto its flight; it is tipped up as it climbs.
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial, bolt.yRotO, bolt.getYRot())));
        pose.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partial, bolt.xRotO, bolt.getXRot())));
        VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutoutNoCull(FEATHER));
        PoseStack.Pose at = pose.last();
        float half = LENGTH / 2, side = WIDTH / 2;
        // Flat, and then on its edge.
        corner(buffer, at, 0, 0, half, 1, 0, light, 0, 1, 0);
        corner(buffer, at, -side, 0, 0, 0, 0, light, 0, 1, 0);
        corner(buffer, at, 0, 0, -half, 0, 1, light, 0, 1, 0);
        corner(buffer, at, side, 0, 0, 1, 1, light, 0, 1, 0);
        corner(buffer, at, 0, 0, half, 1, 0, light, 1, 0, 0);
        corner(buffer, at, 0, side, 0, 0, 0, light, 1, 0, 0);
        corner(buffer, at, 0, 0, -half, 0, 1, light, 1, 0, 0);
        corner(buffer, at, 0, -side, 0, 1, 1, light, 1, 0, 0);
        pose.popPose();
    }

    private static void corner(VertexConsumer buffer, PoseStack.Pose at, float x, float y, float z, float u, float v, int light, float nx, float ny, float nz) {
        buffer.addVertex(at, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(at, nx, ny, nz);
    }
}
