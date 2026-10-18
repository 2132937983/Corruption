package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.common.client.visual.DeathVisual;
import com.miophas.corruption.common.entity.SolomonWyrm;
import com.miophas.corruption.core.demon.SolomonRules;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Draws one of Solomon's flood dragons: its head where it is, turned as it faces, its jaw open while it is at work and its
 * whiskers swaying; then every link of its body where its body lies (between where it lay a tick ago and where it lies now),
 * each turned along the body and narrowing towards the tail, its legs at two of them and its fluke at the last; each lit as
 * the place it lies in is lit. Over all of it, again and unlit, its glow, in the light of its book. Dying, it crumbles away from
 * the tail up, and its death is drawn about its head.
 */
public final class SolomonWyrmRenderer extends EntityRenderer<SolomonWyrm> {
    /** The links its legs are drawn at. */
    private static final int[] LEGGED = {3, 11};

    private final ModelPart head, body, legs, tail;
    private final ModelPart jaw, whiskerLeft, whiskerRight;
    /** How its jaw and whiskers lie at rest, as its shape has them; they move about these. */
    private final float jawRest, whiskerLeftRest, whiskerRightRest;

    public SolomonWyrmRenderer(EntityRendererProvider.Context context) {
        super(context);
        head = context.bakeLayer(SolomonModels.HEAD);
        body = context.bakeLayer(SolomonModels.BODY);
        legs = context.bakeLayer(SolomonModels.LEGS);
        tail = context.bakeLayer(SolomonModels.TAIL);
        Map<String, ModelPart> parts = GeoModel.load("solomon_wyrm_head").parts(head);
        jaw = parts.get("jaw");
        whiskerLeft = parts.get("whisker_left");
        whiskerRight = parts.get("whisker_right");
        jawRest = jaw.xRot;
        whiskerLeftRest = whiskerLeft.yRot;
        whiskerRightRest = whiskerRight.yRot;
        shadowRadius = 0;
    }

    @Override public ResourceLocation getTextureLocation(SolomonWyrm dragon) { return SolomonModels.WYRM; }

    @Override public void render(SolomonWyrm dragon, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        int overlay = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(dragon.hurtTime > 0));
        int colour = 0xFF000000 | dragon.accent();
        boolean whole = dragon.deathTime < dragon.death().ticks();
        if (whole) drawHead(dragon, partial, pose, buffers, light, overlay, colour);
        drawBody(dragon, partial, pose, buffers, overlay, colour);
        if (dragon.deathTime > 0) DeathVisual.draw(DeathVisual.Dying.of(dragon, partial, entityRenderDispatcher.cameraOrientation()), pose, buffers, false);
        super.render(dragon, yaw, partial, pose, buffers, light);
    }

    /**
     * Draws a shape in its skin and then in its glow. Each buffer is fetched just before it is drawn into: fetching another
     * kind of buffer closes the one fetched before it, so neither may be held while the other is drawn.
     */
    static void draw(ModelPart shape, PoseStack pose, MultiBufferSource buffers, int light, int overlay, int colour) {
        shape.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(SolomonModels.WYRM)), light, overlay);
        shape.render(pose, buffers.getBuffer(RenderType.eyes(SolomonModels.WYRM_GLOW)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, colour);
    }

    private void drawHead(SolomonWyrm dragon, float partial, PoseStack pose, MultiBufferSource buffers, int light, int overlay, int colour) {
        float age = dragon.tickCount + partial;
        boolean working = dragon.skill() != SolomonRules.Wyrm.NONE;
        float open = working ? Math.min(1, dragon.skillTicks(partial) / 10) : 0;
        jaw.xRot = jawRest + .08F + .1F * Mth.sin(age / 12) + .55F * open;
        whiskerLeft.yRot = whiskerLeftRest + .25F * Mth.sin(age / 9);
        whiskerRight.yRot = whiskerRightRest - .25F * Mth.sin(age / 9 + 1);
        pose.pushPose();
        SolomonModels.place(pose, 0, dragon.getBbHeight() / 2, 0, Mth.rotLerp(partial, dragon.yRotO, dragon.getYRot()), Mth.lerp(partial, dragon.xRotO, dragon.getXRot()),
                SolomonModels.HALF, SolomonModels.HALF);
        draw(head, pose, buffers, light, overlay, colour);
        pose.popPose();
    }

    private void drawBody(SolomonWyrm dragon, float partial, PoseStack pose, MultiBufferSource buffers, int overlay, int colour) {
        Vec3 origin = dragon.getPosition(partial);
        int links = dragon.chain().links();
        float crumbled = dragon.crumbled(partial);
        for (int link = 0; link < links; link++) {
            if (link >= links - crumbled) continue;
            double[] front = dragon.joint(link, partial), back = dragon.joint(link + 1, partial);
            double dx = front[0] - back[0], dy = front[1] - back[1], dz = front[2] - back[2];
            double flat = Math.sqrt(dx * dx + dz * dz);
            float yaw = flat < 1e-6 && Math.abs(dy) < 1e-6 ? dragon.getYRot() : (float) Math.toDegrees(Math.atan2(-dx, dz));
            float pitch = flat < 1e-6 && Math.abs(dy) < 1e-6 ? 0 : (float) Math.toDegrees(-Math.atan2(dy, flat));
            double x = (front[0] + back[0]) / 2, y = (front[1] + back[1]) / 2, z = (front[2] + back[2]) / 2;
            int lit = LevelRenderer.getLightColor(dragon.level(), BlockPos.containing(x, y, z));
            float taper = (float) dragon.chain().taper(link, SolomonRules.TAPER);
            boolean last = link == links - 1;
            pose.pushPose();
            SolomonModels.place(pose, x - origin.x, y - origin.y, z - origin.z, yaw, pitch, SolomonModels.HALF * taper, SolomonModels.HALF);
            draw(last ? tail : body, pose, buffers, lit, overlay, colour);
            for (int legged : LEGGED)
                if (legged == link) draw(legs, pose, buffers, lit, overlay, colour);
            pose.popPose();
        }
    }
}
