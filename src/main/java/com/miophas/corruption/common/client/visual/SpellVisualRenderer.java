package com.miophas.corruption.common.client.visual;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.miophas.corruption.api.arcane.client.SpellVisuals;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
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
                entity.colour(), entity.accent(), entity.getYRot(), entity.getUUID().getLeastSignificantBits(), entityRenderDispatcher.cameraOrientation());
        pose.pushPose();
        visual.draw(shown, pose, buffers);
        pose.popPose();
    }

    @Override public ResourceLocation getTextureLocation(SpellVisualEntity entity) { return VisualDraw.GLOW; }

    /** How the mod draws each of its own effects, by their ids, in the order {@link SpellVisuals#BUILT_IN} lists them. */
    public static Map<ResourceLocation, Supplier<SpellVisual>> builtIn() {
        Map<ResourceLocation, Supplier<SpellVisual>> drawn = new LinkedHashMap<>();
        drawn.put(SpellVisuals.MAGIC_CIRCLE, MagicCircleVisual::new);
        drawn.put(SpellVisuals.SHOCKWAVE, ShockwaveVisual::new);
        drawn.put(SpellVisuals.LIGHT_PILLAR, LightPillarVisual::new);
        drawn.put(SpellVisuals.VORTEX, VortexVisual::new);
        drawn.put(SpellVisuals.DOME, DomeVisual::new);
        drawn.put(SpellVisuals.RUNE_RING, RuneRingVisual::new);
        drawn.put(SpellVisuals.HELLFIRE, HellfireVisual::new);
        drawn.put(SpellVisuals.WINGS, WingsVisual::new);
        drawn.put(SpellVisuals.FEATHERS, FeatherStormVisual::new);
        drawn.put(SpellVisuals.LIGHTNING, LightningVisual::new);
        drawn.put(SpellVisuals.FISSURE, FissureVisual::new);
        drawn.put(SpellVisuals.SPIKES, SpikesVisual::new);
        drawn.put(SpellVisuals.BLADES, BladesVisual::new);
        drawn.put(SpellVisuals.SOULS, SoulsVisual::new);
        drawn.put(SpellVisuals.EYE, EyeVisual::new);
        drawn.put(SpellVisuals.CONSTELLATION, ConstellationVisual::new);
        drawn.put(SpellVisuals.SIGIL, SigilVisual::new);
        drawn.put(SpellVisuals.TIDAL_WAVE, TidalWaveVisual::new);
        drawn.put(SpellVisuals.BLOOM, BloomVisual::new);
        drawn.put(SpellVisuals.CHAINS, ChainsVisual::new);
        drawn.put(SpellVisuals.RESONANCE, ResonanceVisual::new);
        drawn.put(SpellVisuals.RIFT, RiftVisual::new);
        drawn.put(SpellVisuals.MIASMA, MiasmaVisual::new);
        drawn.put(SpellVisuals.CROWN, CrownVisual::new);
        drawn.put(SpellVisuals.ALCHEMY, AlchemyVisual::new);
        drawn.put(SpellVisuals.SHARDS, ShardsVisual::new);
        drawn.put(SpellVisuals.RAYS, RaysVisual::new);
        drawn.put(SpellVisuals.NOVA, NovaVisual::new);
        drawn.put(SpellVisuals.CRESCENDO, CrescendoVisual::new);
        return drawn;
    }

    /** The mod's own, by their ids. Called in client setup. */
    public static void registerBuiltIn() {
        builtIn().forEach((id, visual) -> SpellVisuals.register(id, visual.get()));
        SpellVisualEntity.appearing = CameraShake::appears;
    }
}
