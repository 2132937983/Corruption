package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.common.entity.SpellBeamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Draws a spell's beam ({@link BeamVisual}) from its caster as this client sees them this very frame: from their
 * hand, along their gaze, to the first block in its way, so that it turns with them smoothly as they look about. A
 * beam whose caster this client cannot see is drawn from where the server last put it, the way it last faced.
 */
public final class SpellBeamRenderer extends EntityRenderer<SpellBeamEntity> {
    public SpellBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0;
    }

    @Override public void render(SpellBeamEntity beam, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        Entity caster = beam.caster();
        Vec3 here = new Vec3(Mth.lerp(partialTick, beam.xOld, beam.getX()), Mth.lerp(partialTick, beam.yOld, beam.getY()), Mth.lerp(partialTick, beam.zOld, beam.getZ()));
        Vec3 from = caster != null ? SpellBeamEntity.muzzle(caster, partialTick) : here;
        Vec3 way = caster != null ? caster.getViewVector(partialTick) : beam.getViewVector(partialTick);
        BlockHitResult reached = beam.reach(from, way);
        Vector3f struck = reached.getType() == HitResult.Type.MISS ? null : reached.getDirection().step();
        BeamVisual.Shape shape = new BeamVisual.Shape(from.subtract(here).toVector3f(), way.toVector3f(), (float) from.distanceTo(reached.getLocation()),
                beam.width(), struck, beam.tickCount + partialTick, beam.lifetime(), beam.colour(), beam.accent(), beam.getUUID().getLeastSignificantBits(),
                entityRenderDispatcher.cameraOrientation());
        pose.pushPose();
        BeamVisual.draw(shape, pose.last(), buffers);
        pose.popPose();
    }

    @Override public ResourceLocation getTextureLocation(SpellBeamEntity beam) { return VisualDraw.GLOW; }
}
