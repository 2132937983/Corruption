package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.core.demon.SolomonRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

/**
 * Solomon's shapes as the game bakes them: every bone his renderers move is there; his dragons' heads reach as far forward
 * as their mouths do on the server, and the links of their bodies overlap as they bend; a giant hand falls palm first on the
 * place it stands over; a dragon is drawn without holding one buffer while it fetches another; and every picture, name and
 * tag he needs is there.
 */
class SolomonModelTest {
    private static ModelPart bake(String name) { return GeoModel.load(name).layer().bakeRoot(); }

    private static InputStream resource(String path) {
        InputStream in = SolomonModelTest.class.getResourceAsStream(path);
        assertNotNull(in, "Missing " + path);
        return in;
    }

    /** The corners of every cube whose bone's path passes, in blocks as drawn (twice its built size), the shape's root brought back to its middle. */
    private static List<Vector3f> corners(ModelPart root, Predicate<String> path) {
        List<Vector3f> found = new ArrayList<>();
        root.visit(new PoseStack(), (pose, at, index, cube) -> {
            if (!path.test(at)) return;
            for (float x : new float[]{cube.minX, cube.maxX}) for (float y : new float[]{cube.minY, cube.maxY}) for (float z : new float[]{cube.minZ, cube.maxZ}) {
                Vector3f corner = pose.pose().transformPosition(x / 16, y / 16, z / 16, new Vector3f());
                // As SolomonModels.place sets it: X and Y flipped, the root's 24 pixels taken off, twice as large.
                found.add(new Vector3f(-corner.x * SolomonModels.HALF, (1.5F - corner.y) * SolomonModels.HALF, corner.z * SolomonModels.HALF));
            }
        });
        assertFalse(found.isEmpty());
        return found;
    }

    @Test void everyBoneHisRenderersMoveIsThere() {
        Map<String, ModelPart> head = GeoModel.load("solomon_wyrm_head").parts(bake("solomon_wyrm_head"));
        for (String bone : List.of("jaw", "whisker_left", "whisker_right")) assertNotNull(head.get(bone), bone);
        Map<String, ModelPart> hand = GeoModel.load("solomon_hand").parts(bake("solomon_hand"));
        for (String bone : List.of("arm", "hand", "thumb", "finger_0", "finger_3", "finger_tip_0", "finger_tip_3")) assertNotNull(hand.get(bone), bone);
        Map<String, ModelPart> drake = GeoModel.load("solomon_drake").parts(bake("solomon_drake"));
        for (String bone : List.of("wing_left", "wing_right", "wingtip_left", "wingtip_right", "drake_jaw", "tail_0", "tail_1")) assertNotNull(drake.get(bone), bone);
        for (String shape : List.of("solomon_wyrm_body", "solomon_wyrm_legs", "solomon_wyrm_tail", "solomon_crown")) assertNotNull(bake(shape), shape);
    }

    @Test void aDragonsHeadReachesItsMouthAndItsLinksOverlap() {
        ModelPart head = bake("solomon_wyrm_head");
        float snout = corners(head, path -> true).stream().map(corner -> -corner.z).max(Float::compare).orElseThrow();
        assertEquals(SolomonRules.HEAD_LENGTH / 2, snout, .2, "its snout as far ahead of its middle as its mouth is on the server");
        float back = corners(head, path -> !path.contains("horn") && !path.contains("whisker")).stream().map(corner -> corner.z).max(Float::compare).orElseThrow();
        assertEquals(SolomonRules.HEAD_LENGTH / 2, back, .2, "its nape as far behind, where its body begins");
        List<Vector3f> link = corners(bake("solomon_wyrm_body"), path -> true);
        float length = link.stream().map(corner -> corner.z).max(Float::compare).orElseThrow() - link.stream().map(corner -> corner.z).min(Float::compare).orElseThrow();
        assertTrue(length > SolomonRules.SPACING, "a link longer than the spacing of its body, so they overlap: " + length);
        float width = link.stream().map(corner -> corner.x).max(Float::compare).orElseThrow() - link.stream().map(corner -> corner.x).min(Float::compare).orElseThrow();
        assertEquals(SolomonRules.LINK_WIDTH, width, .4, "about as wide as its hitbox");
    }

    @Test void aHandFallsPalmFirstOnThePlaceItStandsOver() {
        ModelPart root = bake("solomon_hand");
        Map<String, ModelPart> parts = GeoModel.load("solomon_hand").parts(root);
        List<Vector3f> upright = corners(root, path -> path.endsWith("/hand") || path.contains("finger"));
        assertTrue(upright.stream().map(corner -> corner.y).max(Float::compare).orElseThrow() > 5, "upright, six blocks from the ground to its claws");
        parts.get("arm").xRot = (float) Math.PI / 2;
        List<Vector3f> palm = new ArrayList<>();
        root.visit(new PoseStack(), (pose, at, index, cube) -> {
            if (!at.endsWith("/hand")) return;
            Vector3f middle = pose.pose().transformPosition((cube.minX + cube.maxX) / 32, (cube.minY + cube.maxY) / 32, (cube.minZ + cube.maxZ) / 32, new Vector3f());
            palm.add(new Vector3f(-middle.x * SolomonModels.HALF, (1.5F - middle.y) * SolomonModels.HALF, middle.z * SolomonModels.HALF));
        });
        Vector3f fallen = palm.get(0);
        assertTrue(Math.hypot(fallen.x, fallen.z) < 1.5, "its palm comes down on the place: " + fallen);
        assertTrue(fallen.y < 2, "on the ground: " + fallen.y);
    }

    /**
     * The game's buffers as they behave while entities are drawn: kinds of buffer that are not kept apart share one builder, so
     * fetching another kind closes the one fetched before it, and drawing into that one fails as the game's does ("Not
     * building!"). Fetching the same kind again leaves it open.
     */
    private static final class SharedBuffers implements MultiBufferSource {
        private RenderType open;
        private int fetched, vertices;

        @Override public VertexConsumer getBuffer(RenderType type) {
            open = type;
            fetched++;
            return new VertexConsumer() {
                private VertexConsumer live() {
                    if (open != type) throw new IllegalStateException("Not building!");
                    return this;
                }

                @Override public VertexConsumer addVertex(float x, float y, float z) {
                    vertices++;
                    return live();
                }

                @Override public VertexConsumer setColor(int red, int green, int blue, int alpha) { return live(); }
                @Override public VertexConsumer setUv(float u, float v) { return live(); }
                @Override public VertexConsumer setUv1(int u, int v) { return live(); }
                @Override public VertexConsumer setUv2(int u, int v) { return live(); }
                @Override public VertexConsumer setNormal(float x, float y, float z) { return live(); }
            };
        }
    }

    @Test void aDragonIsDrawnWithoutHoldingOneBufferWhileItFetchesAnother() {
        ModelPart head = bake("solomon_wyrm_head");
        // What crashed the game: its skin's buffer fetched, then its glow's, then the skin drawn into the first.
        SharedBuffers held = new SharedBuffers();
        VertexConsumer skin = held.getBuffer(RenderType.entityCutoutNoCull(SolomonModels.WYRM));
        held.getBuffer(RenderType.eyes(SolomonModels.WYRM_GLOW));
        assertThrows(IllegalStateException.class, () -> head.render(new PoseStack(), skin, 0xF000F0, OverlayTexture.NO_OVERLAY));
        // As it is drawn now: every shape of it, skin and glow, each into a buffer fetched just before.
        SharedBuffers buffers = new SharedBuffers();
        for (String shape : List.of("solomon_wyrm_head", "solomon_wyrm_body", "solomon_wyrm_legs", "solomon_wyrm_tail"))
            assertDoesNotThrow(() -> SolomonWyrmRenderer.draw(bake(shape), new PoseStack(), buffers, 0xF000F0, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF), shape);
        assertEquals(8, buffers.fetched, "a skin and a glow for each of its four shapes");
        assertTrue(buffers.vertices > 0);
    }

    @Test void everyPictureNameAndTagHeNeedsIsThere() throws Exception {
        for (String texture : List.of("solomon_wyrm", "solomon_hand", "solomon_drake", "solomon_crown"))
            for (String layer : List.of("", "_glow")) resource("/assets/corruption/textures/entity/" + texture + layer + ".png").close();
        BufferedImage bar;
        try (InputStream in = resource("/assets/corruption/textures/gui/solomon_boss_bar.png")) { bar = ImageIO.read(in); }
        assertEquals(256, bar.getWidth());
        assertEquals(128, bar.getHeight());
        resource("/assets/corruption/textures/item/solomon_ring.png").close();
        resource("/assets/corruption/models/item/solomon_ring.json").close();
        resource("/assets/corruption/textures/misc/altar_circle_octagram.png").close();
        for (String language : List.of("en_us", "zh_cn")) {
            JsonObject lang;
            try (InputStream in = resource("/assets/corruption/lang/" + language + ".json")) { lang = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject(); }
            for (String key : List.of("entity.corruption.solomon", "entity.corruption.solomon_wyrm", "entity.corruption.solomon_hand", "entity.corruption.solomon_drake",
                    "entity.corruption.solomon_fireball", "death.attack.corruption.solomon", "death.attack.corruption.solomon.player", "death.attack.corruption.solomon.item",
                    "item.corruption.solomon_ring", "item.corruption.solomon_ring.desc"))
                assertTrue(lang.has(key), language + " is missing " + key);
        }
        for (String tag : List.of("bosses", "corrupted_remnants", "pierces_veil")) {
            String text;
            try (InputStream in = resource("/data/corruption/tags/entity_type/" + tag + ".json")) { text = new String(in.readAllBytes(), StandardCharsets.UTF_8); }
            assertTrue(text.contains("\"corruption:solomon\"") && text.contains("\"corruption:solomon_wyrm\""), tag);
        }
    }
}
