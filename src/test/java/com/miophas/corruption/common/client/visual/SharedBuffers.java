package com.miophas.corruption.common.client.visual;

import static org.junit.jupiter.api.Assertions.*;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

/**
 * Buffers that behave as the game's do for the effects' light, keeping every corner drawn into them. Every kind of
 * light (render type) the effects draw with is drawn into the one shared buffer, so asking for another kind finishes
 * the one before it, and drawing into a finished one fails here, as it crashes the game ("Not building!"). Asking
 * for the same kind again goes on with the same buffer, as the game's does.
 */
final class SharedBuffers implements MultiBufferSource {
    final List<float[]> corners = new ArrayList<>();
    int textured;
    private RenderType current;
    private Writer open;

    @Override public VertexConsumer getBuffer(RenderType type) {
        if (type != current) {
            if (open != null) open.finished = true;
            current = type;
            open = new Writer(type);
        }
        return open;
    }

    private final class Writer implements VertexConsumer {
        private final RenderType type;
        private boolean finished;

        Writer(RenderType type) { this.type = type; }

        private void building() { assertFalse(finished, "drawn into " + type + " after asking for another kind of light, which finished it"); }

        @Override public VertexConsumer addVertex(float x, float y, float z) { building(); corners.add(new float[]{x, y, z}); return this; }
        @Override public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            building();
            assertTrue(red >= 0 && red < 256 && green >= 0 && green < 256 && blue >= 0 && blue < 256 && alpha >= 0 && alpha < 256);
            return this;
        }
        @Override public VertexConsumer setUv(float u, float v) {
            building();
            assertTrue(Float.isFinite(u) && Float.isFinite(v), "texture coordinates are real numbers");
            textured++;
            return this;
        }
        @Override public VertexConsumer setUv1(int u, int v) { building(); return this; }
        @Override public VertexConsumer setUv2(int u, int v) { building(); return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { building(); return this; }
    }
}
