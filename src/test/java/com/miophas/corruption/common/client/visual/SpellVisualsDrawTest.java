package com.miophas.corruption.common.client.visual;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.client.SpellVisual;
import com.miophas.corruption.api.arcane.client.SpellVisuals;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

/**
 * Every effect the mod draws, drawn through the whole of its life at sizes from none to the largest a spell may ask,
 * seen from a camera turned at a slant, into buffers that keep what they are given and finish one kind of light when
 * another is asked for, as the game's do: each draws, never into a finished buffer, every corner of every face is a
 * real number, inside what the effect is drawn for (so none of it is ever cut off), and in whole faces.
 */
class SpellVisualsDrawTest {
    /** Sizes as radius, height, and lifetime. */
    private static final float[][] SIZES = {{0, 0, 1}, {.5F, 0, 20}, {4, 5, 60}, {12, 3, 80}, {32, 64, 400}};


    @Test void theModDrawsEveryEffectItNames() {
        assertEquals(SpellVisuals.BUILT_IN, List.copyOf(SpellVisualRenderer.builtIn().keySet()), "every one of its own, in order, and no other");
        assertEquals(29, SpellVisuals.BUILT_IN.size());
    }

    @Test void everyEffectDrawsWithinItsReachThroughItsWholeLife() {
        Quaternionf camera = new Quaternionf().rotationYXZ((float) Math.PI - .7F, -.45F, 0);
        for (Map.Entry<ResourceLocation, Supplier<SpellVisual>> entry : SpellVisualRenderer.builtIn().entrySet()) {
            SpellVisual visual = entry.getValue().get();
            for (float[] size : SIZES) {
                float radius = size[0], height = size[1];
                int lifetime = (int) size[2];
                AABB reach = SpellVisualEntity.reach(0, 0, 0, radius, height);
                int drawnMidway = 0;
                List<Float> ages = new ArrayList<>(List.of(0F, .5F, 1F, 2F, 3F, 5F, 8F, 12F, 17F, 23F));
                for (float share = .05F; share < 1; share += .05F) ages.add(share * lifetime);
                ages.add(lifetime - .01F);
                for (float age : ages) {
                    SharedBuffers kept = new SharedBuffers();
                    SpellVisual.Shown shown = new SpellVisual.Shown(age, lifetime, radius, height, 0x8A0303, 0xFFB040, 37, 1234567L + lifetime, camera);
                    visual.draw(shown, new PoseStack(), kept);
                    String what = entry.getKey() + " at radius " + radius + ", height " + height + ", age " + age + " of " + lifetime;
                    assertEquals(0, kept.corners.size() % 4, what + " draws whole faces");
                    assertEquals(kept.corners.size(), kept.textured);
                    for (float[] corner : kept.corners) {
                        assertTrue(Float.isFinite(corner[0]) && Float.isFinite(corner[1]) && Float.isFinite(corner[2]), what + " draws at real places");
                        assertTrue(corner[0] >= reach.minX && corner[0] <= reach.maxX && corner[1] >= reach.minY && corner[1] <= reach.maxY
                                && corner[2] >= reach.minZ && corner[2] <= reach.maxZ, what + " draws within its reach: " + corner[0] + ", " + corner[1] + ", " + corner[2]);
                    }
                    if (Math.abs(age - lifetime * .5F) < lifetime * .03F) drawnMidway += kept.corners.size();
                }
                if (radius >= 4) assertTrue(drawnMidway > 0, entry.getKey() + " shows halfway through its life at radius " + radius);
            }
        }
    }

    /**
     * The archmage's beam, drawn through the whole of its life, short and long, thin and wide, level, aslant and
     * straight up, stopped by a face or by nothing: it draws whole faces at real places, all near its line.
     */
    @Test void theBeamDrawsAlongItsLineThroughItsWholeLife() {
        Quaternionf camera = new Quaternionf().rotationYXZ((float) Math.PI - .7F, -.45F, 0);
        Vector3f from = new Vector3f(.3F, 1.3F, .7F);
        Vector3f[] ways = {new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(.3F, -.2F, .93F).normalize()};
        Vector3f[] faces = {null, new Vector3f(0, 1, 0), new Vector3f(-1, 0, 0)};
        int drawn = 0;
        for (Vector3f way : ways) for (Vector3f face : faces) for (float length : new float[]{.5F, 12, 64}) for (float width : new float[]{.5F, 2, 8})
            for (int lifetime : new int[]{1, 60}) for (float share = 0; share <= 1.001F; share += .1F) {
                float age = share * lifetime;
                SharedBuffers kept = new SharedBuffers();
                BeamVisual.draw(new BeamVisual.Shape(from, way, length, width, face, age, lifetime, 0xFFD24A, 0xFFFFFF, 99L, camera), new PoseStack().last(), kept);
                String what = "the beam " + length + " long, " + width + " wide, along " + way + ", at " + age + " of " + lifetime;
                assertEquals(0, kept.corners.size() % 4, what + " draws whole faces");
                Vector3f end = new Vector3f(way).mul(length).add(from);
                AABB near = new AABB(from.x, from.y, from.z, end.x, end.y, end.z).inflate(width * 5 + 3);
                for (float[] corner : kept.corners) {
                    assertTrue(Float.isFinite(corner[0]) && Float.isFinite(corner[1]) && Float.isFinite(corner[2]), what + " draws at real places");
                    assertTrue(near.contains(corner[0], corner[1], corner[2]), what + " draws near its line: " + corner[0] + ", " + corner[1] + ", " + corner[2]);
                }
                if (lifetime == 60 && Math.abs(share - .5F) < .01F) drawn += kept.corners.size();
            }
        assertTrue(drawn > 0, "and it shows, midway through its firing");
    }

    @Test void everyTextureTheEffectsUseIsThere() throws Exception {
        for (ResourceLocation texture : List.of(VisualDraw.CIRCLE_OUTER, VisualDraw.CIRCLE_INNER, VisualDraw.BAND, VisualDraw.STREAKS, VisualDraw.SPIRAL,
                VisualDraw.HEXAGONS, VisualDraw.RUNES, VisualDraw.GLOW, VisualDraw.SPRITES, VisualDraw.FLAMES, VisualDraw.BEAM, VisualDraw.FACET, VisualDraw.IRIS, VisualDraw.SCLERA,
                VisualDraw.ALCHEMY, VisualDraw.STAFF, VisualDraw.WATER))
            try (InputStream in = SpellVisualsDrawTest.class.getResourceAsStream("/assets/" + texture.getNamespace() + "/" + texture.getPath())) {
                assertNotNull(in, "Missing " + texture);
            }
        try (InputStream in = SpellVisualsDrawTest.class.getResourceAsStream("/assets/corruption/textures/entity/spell_visual/iris.png.mcmeta")) {
            assertNotNull(in, "the iris is drawn clamped");
            assertTrue(new String(in.readAllBytes()).contains("\"clamp\": true"));
        }
    }
}
