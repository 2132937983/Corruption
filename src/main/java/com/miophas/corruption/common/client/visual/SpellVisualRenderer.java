package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.miophas.corruption.api.arcane.client.SpellVisuals;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/** Draws a spell's effect by whatever {@link SpellVisuals} has for its id; one nobody has registered is not drawn. */
public final class SpellVisualRenderer extends EntityRenderer<SpellVisualEntity> {
    public SpellVisualRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0;
    }

    @Override public void render(SpellVisualEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        SpellVisual visual = SpellVisuals.get(entity.effect());
        if (visual == null) return;
        SpellVisual.Shown shown = new SpellVisual.Shown(entity.tickCount + partialTick, entity.lifetime(), entity.radius(), entity.height(),
                entity.colour(), entity.accent(), entity.getYRot(), entity.getUUID().getLeastSignificantBits());
        pose.pushPose();
        visual.draw(shown, pose, buffers);
        pose.popPose();
    }

    @Override public ResourceLocation getTextureLocation(SpellVisualEntity entity) { return VisualDraw.GLOW; }

    /** The mod's own six, by their ids. Called in client setup. */
    public static void registerBuiltIn() {
        SpellVisuals.register(SpellVisuals.MAGIC_CIRCLE, new MagicCircleVisual());
        SpellVisuals.register(SpellVisuals.SHOCKWAVE, new ShockwaveVisual());
        SpellVisuals.register(SpellVisuals.LIGHT_PILLAR, new LightPillarVisual());
        SpellVisuals.register(SpellVisuals.VORTEX, new VortexVisual());
        SpellVisuals.register(SpellVisuals.DOME, new DomeVisual());
        SpellVisuals.register(SpellVisuals.RUNE_RING, new RuneRingVisual());
        SpellVisualEntity.appearing = CameraShake::appears;
    }
}
