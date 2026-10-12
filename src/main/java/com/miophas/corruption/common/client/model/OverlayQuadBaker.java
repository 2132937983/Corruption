package com.miophas.corruption.common.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.miophas.corruption.core.corruption.OverlayGeometry;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/** One cache per resource bake; bounded even for mods that generate fresh quads dynamically. */
public final class OverlayQuadBaker {
    private final TextureAtlasSprite overlay;
    private final Cache<BakedQuad, BakedQuad> cache = CacheBuilder.newBuilder().maximumSize(16384).build();

    public OverlayQuadBaker(TextureAtlasSprite overlay) { this.overlay = overlay; }

    public BakedQuad bake(BakedQuad base) {
        BakedQuad cached = cache.getIfPresent(base);
        if (cached != null) return cached;
        int[] vertices = base.getVertices().clone();
        int stride = vertices.length / 4;
        float[] positions = new float[12];
        for (int vertex = 0; vertex < 4; vertex++) {
            for (int axis = 0; axis < 3; axis++) positions[vertex * 3 + axis] = Float.intBitsToFloat(vertices[vertex * stride + axis]);
        }
        float[] normal = OverlayGeometry.faceNormal(positions, base.getDirection().getStepX(),
                base.getDirection().getStepY(), base.getDirection().getStepZ());
        TextureAtlasSprite sprite = base.getSprite();
        for (int vertex = 0; vertex < 4; vertex++) {
            int start = vertex * stride;
            for (int axis = 0; axis < 3; axis++) {
                vertices[start + axis] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[start + axis])
                        + normal[axis] * OverlayGeometry.OFFSET);
            }
            vertices[start + 3] = -1; // The overlay keeps its own color, independent of foliage/grass tint.
            vertices[start + 4] = Float.floatToRawIntBits(OverlayGeometry.remapUv(Float.intBitsToFloat(vertices[start + 4]),
                    sprite.getU0(), sprite.getU1(), overlay.getU0(), overlay.getU1()));
            vertices[start + 5] = Float.floatToRawIntBits(OverlayGeometry.remapUv(Float.intBitsToFloat(vertices[start + 5]),
                    sprite.getV0(), sprite.getV1(), overlay.getV0(), overlay.getV1()));
        }
        BakedQuad result = new BakedQuad(vertices, -1, base.getDirection(), overlay, base.isShade(), base.hasAmbientOcclusion());
        cache.put(base, result);
        return result;
    }
}
