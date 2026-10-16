package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.common.entity.DemonBeamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Draws a boss's beam (DemonBeamEntity) from where it stands, the way it faces, to the first block in its way. While it
 * gathers: a thin line of light along its aim, flickering faster and brighter as it comes to fire, and a light swelling
 * where it will leave the boss, so that whoever it is aimed at can see it coming and get out of its way. Once it fires:
 * the beam itself ({@link BeamVisual}), its casting circles before the boss.
 */
public final class DemonBeamRenderer extends EntityRenderer<DemonBeamEntity> {
    public DemonBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0;
    }

    @Override public void render(DemonBeamEntity beam, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        Vec3 here = new Vec3(Mth.lerp(partialTick, beam.xOld, beam.getX()), Mth.lerp(partialTick, beam.yOld, beam.getY()), Mth.lerp(partialTick, beam.zOld, beam.getZ()));
        Vec3 way = beam.getViewVector(partialTick);
        BlockHitResult reached = beam.reach(here, way);
        float length = (float) here.distanceTo(reached.getLocation());
        if (beam.fired()) {
            Vector3f struck = reached.getType() == HitResult.Type.MISS ? null : reached.getDirection().step();
            BeamVisual.draw(new BeamVisual.Shape(new Vector3f(), way.toVector3f(), length, beam.width(), struck, beam.firedFor(partialTick), beam.firing(),
                    beam.colour(), beam.accent(), beam.getUUID().getLeastSignificantBits(), entityRenderDispatcher.cameraOrientation()), pose.last(), buffers);
            return;
        }
        // Gathering: a flickering thread of aim, and a light swelling at its source.
        float age = beam.tickCount + partialTick, coming = Mth.clamp(age / Math.max(1, beam.gathering()), 0, 1);
        float flicker = .5F + .5F * Mth.sin(age * (1.2F + 2.4F * coming));
        Vector3f forward = entityRenderDispatcher.cameraOrientation().transform(new Vector3f(0, 0, -1));
        VertexConsumer thread = VisualDraw.light(buffers, VisualDraw.BEAM);
        float thin = .06F + .1F * coming;
        VisualDraw.beam(thread, pose.last(), forward, 0, 0, 0, (float) way.x * length, (float) way.y * length, (float) way.z * length, thin,
                VisualDraw.glow(beam.accent(), (.25F + .55F * coming) * flicker), VisualDraw.glow(beam.colour(), (.1F + .3F * coming) * flicker));
        RaysVisual.heart(pose.last(), buffers, entityRenderDispatcher.cameraOrientation(), 0, 0, 0, beam.width() * (.3F + 1.1F * coming), age, .4F + .6F * coming,
                beam.colour(), beam.accent());
    }

    @Override public ResourceLocation getTextureLocation(DemonBeamEntity beam) { return VisualDraw.GLOW; }
}
