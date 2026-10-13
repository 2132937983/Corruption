package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.model.geom.ModelPart;
import org.junit.jupiter.api.Test;

class GeoModelTest {
    /** A file of the project, wherever the tests are run from. */
    static Path project(String file) {
        for (Path folder = Path.of("").toAbsolutePath(); folder != null; folder = folder.getParent())
            if (Files.exists(folder.resolve("build.gradle")) && Files.exists(folder.resolve(file))) return folder.resolve(file);
        throw new AssertionError("Cannot find " + file + " from " + Path.of("").toAbsolutePath());
    }

    /** Everything a model draws, as one line per face: its four corners with their texture coordinates, and its normal. Sorted, so bone order does not matter. */
    static List<String> faces(ModelPart root) {
        List<String> corners = new ArrayList<>();
        root.render(new PoseStack(), new VertexConsumer() {
            @Override public VertexConsumer addVertex(float x, float y, float z) { return this; }
            @Override public VertexConsumer setColor(int red, int green, int blue, int alpha) { return this; }
            @Override public VertexConsumer setUv(float u, float v) { return this; }
            @Override public VertexConsumer setUv1(int u, int v) { return this; }
            @Override public VertexConsumer setUv2(int u, int v) { return this; }
            @Override public VertexConsumer setNormal(float x, float y, float z) { return this; }
            @Override public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
                corners.add(String.format(Locale.ROOT, "%.4f %.4f %.4f uv %.6f %.6f n %.3f %.3f %.3f", x, y, z, u, v, normalX, normalY, normalZ));
            }
        }, 0, 0);
        List<String> faces = new ArrayList<>();
        for (int corner = 0; corner < corners.size(); corner += 4) faces.add(String.join(" | ", corners.subList(corner, corner + 4)));
        Collections.sort(faces);
        return faces;
    }

    @Test void theDataFormatDrawsExactlyWhatBlockbenchsOwnJavaExportDraws() throws Exception {
        // Baal exists both ways: as the Java that Blockbench exported, and as data written by gamemodels/geo_export.py
        // from the same project. If the two draw the same faces, the data path can be trusted for models too big for Java.
        GeoModel data;
        try (var reader = Files.newBufferedReader(project("src/test/resources/geo/baal.json"))) { data = GeoModel.read(reader); }
        List<String> expected = faces(BaalGeometry.createBodyLayer().bakeRoot()), actual = faces(data.layer().bakeRoot());
        assertEquals(250 * 6, expected.size());
        assertEquals(expected.size(), actual.size());
        for (int face = 0; face < expected.size(); face++) assertEquals(expected.get(face), actual.get(face), "face " + face);
        // Its bones keep their names, so the same rig drives either.
        new BaalModel(data.layer().bakeRoot()).pose(BaalPose.standing(0));
        assertEquals(data.bones().size(), data.parts(data.layer().bakeRoot()).size());
    }

    @Test void agaresLoadsFromTheModsOwnResources() {
        GeoModel agares = GeoModel.load("agares");
        ModelPart root = agares.layer().bakeRoot();
        var parts = agares.parts(root);
        assertEquals(agares.bones().size(), parts.size());
        assertTrue(parts.containsKey("hips") && parts.containsKey("left_wing_hand") && parts.containsKey("jaw") && parts.containsKey("tail_13"));
        int cubes = agares.bones().stream().mapToInt(bone -> bone.cubes().length).sum();
        assertTrue(cubes > 2000, "it is built from thousands of pieces: " + cubes);
        assertEquals(cubes * 6, faces(root).size());
        assertSame(agares, GeoModel.load("agares"), "read once");
    }
}
