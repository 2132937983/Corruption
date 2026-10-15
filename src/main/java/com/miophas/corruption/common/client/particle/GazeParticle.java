package com.miophas.corruption.common.client.particle;

import com.miophas.corruption.Corruption;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/**
 * Andromalius's eye, flashed before a player's eyes as an elder guardian's face is (MobAppearanceParticle): not a
 * particle in the world but a picture over the view, fixed to the camera, that swells out of nothing, looms nearer
 * as it turns a little, and fades. The server sends it to the one player he stares at, at their own position.
 * The picture is textures/misc/andromalius_gaze.png, drawn by gamemodels/andromalius/draw_gaze.py.
 */
public final class GazeParticle extends Particle {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/misc/andromalius_gaze.png");
    private static final RenderType RENDER = RenderType.entityTranslucent(TEXTURE);
    /** Ticks it shows for: two seconds, a little longer than the guardian's face. */
    private static final int LIFETIME = 40;
    /** How strongly it shows at the height of the flash; the guardian's face reaches .55. */
    private static final float PEAK = .85F;

    private GazeParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        gravity = 0;
        lifetime = LIFETIME;
    }

    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.CUSTOM; }

    /** Fixed to the camera, so where it stands in the world means nothing: it is never culled. */
    @Override public AABB getRenderBoundingBox(float partialTicks) { return AABB.INFINITE; }

    @Override public void render(VertexConsumer ignored, Camera camera, float partialTicks) {
        float progress = (age + partialTicks) / lifetime;
        float strength = .05F + PEAK * Mth.sin(progress * Mth.PI);
        int colour = FastColor.ARGB32.colorFromFloat(strength, 1, 1, 1);
        PoseStack pose = new PoseStack();
        pose.mulPose(camera.rotation());
        // As the guardian's face is drawn: in front of the eyes, upright.
        pose.scale(1, -1, -1);
        pose.translate(0, 0, 1.5F);
        pose.mulPose(Axis.ZP.rotationDegrees(-10 + 20 * progress));
        // It looms: from most of the view to more than all of it.
        float half = .72F + .38F * progress;
        PoseStack.Pose at = pose.last();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(RENDER);
        corner(buffer, at, -half, -half, 0, 0, colour);
        corner(buffer, at, -half, half, 0, 1, colour);
        corner(buffer, at, half, half, 1, 1, colour);
        corner(buffer, at, half, -half, 1, 0, colour);
        buffers.endBatch();
    }

    private static void corner(VertexConsumer buffer, PoseStack.Pose at, float x, float y, float u, float v, int colour) {
        buffer.addVertex(at, x, y, 0).setColor(colour).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, 0, -1);
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new GazeParticle(level, x, y, z);
        }
    }
}
