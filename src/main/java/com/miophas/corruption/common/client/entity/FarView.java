package com.miophas.corruption.common.client.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Lets something hundreds of blocks tall be seen whole. The game stops drawing at four times the render
 * distance and hides everything past the render distance in fog, so by default the upper part of a colossus
 * is cut off or lost in the sky's colour.
 *
 * Two things fix that. Its far vertices are pulled towards the eye along their own line of sight, which
 * changes nothing on screen (a thing twice as far and twice as big looks the same) but brings them inside
 * the far limit. Nothing nearer than the world itself reaches is moved, so it still passes behind and in
 * front of terrain correctly. And it is drawn with its own, much longer fog, so distance hazes it instead
 * of erasing it.
 */
public final class FarView {
    /** Up to this share of the far limit a vertex is left alone; beyond, it is squeezed in under LIMIT. */
    private static final float UNTOUCHED = .6F, LIMIT = .95F;
    /** Its fog starts and ends at these multiples of the far limit, so the farthest of it is about a third hazed. */
    private static final float FOG_START = .3F, FOG_END = 2.6F;
    private static float fogStart, fogEnd;
    private static boolean fogChanged;

    /**
     * Swaps in the long fog while a colossus is drawn. Only over the ordinary open-air fog: under water, in
     * lava, blinded or in the Nether's murk the game's own, much shorter fog is left to hide it.
     */
    private static final RenderStateShard.TexturingStateShard LONG_FOG = new RenderStateShard.TexturingStateShard("corruption_long_fog", () -> {
        float far = far();
        fogStart = RenderSystem.getShaderFogStart(); fogEnd = RenderSystem.getShaderFogEnd();
        fogChanged = fogEnd >= far * .2F;
        if (fogChanged) { RenderSystem.setShaderFogStart(far * FOG_START); RenderSystem.setShaderFogEnd(far * FOG_END); }
    }, () -> {
        if (fogChanged) { RenderSystem.setShaderFogStart(fogStart); RenderSystem.setShaderFogEnd(fogEnd); }
    });

    private static final Function<ResourceLocation, RenderType> BODY = Util.memoize(texture -> RenderType.create("corruption_colossus", DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS, 1536, true, false, RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL).setLightmapState(RenderStateShard.LIGHTMAP).setOverlayState(RenderStateShard.OVERLAY)
                    .setTexturingState(LONG_FOG).createCompositeState(true)));
    private static final Function<ResourceLocation, RenderType> GLOW = Util.memoize(texture -> RenderType.create("corruption_colossus_glow", DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS, 1536, false, false, RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_EYES_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE).setTexturingState(LONG_FOG).createCompositeState(false)));

    /** How a colossus's body is drawn: as any mob's, but with the long fog. */
    public static RenderType body(ResourceLocation texture) { return BODY.apply(texture); }
    /** How its glowing parts are drawn over it: added to what is there, unlit, as a spider's eyes are. */
    public static RenderType glow(ResourceLocation texture) { return GLOW.apply(texture); }

    private static float far() { return Minecraft.getInstance().gameRenderer.getDepthFar(); }

    /**
     * How far from the eye to draw a vertex that is really this far away. Unchanged up to UNTOUCHED of the far
     * limit; past it the distance creeps up towards LIMIT of it and never arrives, and never doubles back, so
     * nearer parts still hide farther ones.
     */
    public static float squeeze(float distance, float far) {
        float from = far * UNTOUCHED, room = far * (LIMIT - UNTOUCHED);
        return distance <= from ? distance : from + room * (1 - (float) Math.exp(-(distance - from) / room));
    }

    /** Buffers that squeeze every vertex written to them. Vertices arrive as offsets from the eye. */
    public static MultiBufferSource wrap(MultiBufferSource buffers) {
        float far = far();
        return type -> new Squeezed(buffers.getBuffer(type), far);
    }

    private record Squeezed(VertexConsumer to, float far) implements VertexConsumer {
        private float scale(float x, float y, float z) {
            float distance = (float) Math.sqrt(x * x + y * y + z * z);
            return distance <= far * UNTOUCHED ? 1 : squeeze(distance, far) / distance;
        }
        @Override public VertexConsumer addVertex(float x, float y, float z) { float by = scale(x, y, z); to.addVertex(x * by, y * by, z * by); return this; }
        @Override public VertexConsumer setColor(int red, int green, int blue, int alpha) { to.setColor(red, green, blue, alpha); return this; }
        @Override public VertexConsumer setUv(float u, float v) { to.setUv(u, v); return this; }
        @Override public VertexConsumer setUv1(int u, int v) { to.setUv1(u, v); return this; }
        @Override public VertexConsumer setUv2(int u, int v) { to.setUv2(u, v); return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { to.setNormal(x, y, z); return this; }
        @Override public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
            float by = scale(x, y, z);
            to.addVertex(x * by, y * by, z * by, color, u, v, overlay, light, normalX, normalY, normalZ);
        }
    }

    private FarView() {}
}
