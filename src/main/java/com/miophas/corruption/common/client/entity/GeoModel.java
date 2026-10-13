package com.miophas.corruption.common.client.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * A model read from assets/corruption/geo/&lt;name&gt;.json instead of being written out as Java. A model of
 * thousands of cubes cannot be: the method that builds it would be larger than a class file allows.
 * gamemodels/geo_export.py writes the file from a Blockbench project, already in the game's model space.
 */
public final class GeoModel {
    /**
     * @param parent empty for a bone at the top
     * @param cubes each x, y, z, width, height, depth, texture u, texture v, and then, only where they are
     *        not zero, how far the cube is inflated and 1 if its texture is mirrored
     */
    public record Bone(String name, String parent, PartPose pose, float[][] cubes) {}

    private static final Map<String, GeoModel> LOADED = new ConcurrentHashMap<>();
    private final int textureWidth, textureHeight;
    private final List<Bone> bones;

    private GeoModel(int textureWidth, int textureHeight, List<Bone> bones) {
        this.textureWidth = textureWidth; this.textureHeight = textureHeight; this.bones = List.copyOf(bones);
    }

    /** The model shipped under this name, read once. */
    public static GeoModel load(String name) {
        return LOADED.computeIfAbsent(name, key -> {
            String path = "/assets/corruption/geo/" + key + ".json";
            try (var stream = GeoModel.class.getResourceAsStream(path)) {
                if (stream == null) throw new IllegalStateException("Missing model " + path);
                return read(new InputStreamReader(stream, StandardCharsets.UTF_8));
            } catch (IOException failure) { throw new UncheckedIOException(failure); }
        });
    }

    public static GeoModel read(Reader json) {
        JsonObject model = JsonParser.parseReader(json).getAsJsonObject();
        JsonArray texture = model.getAsJsonArray("texture");
        List<Bone> bones = new ArrayList<>();
        for (var entry : model.getAsJsonArray("bones")) {
            JsonArray bone = entry.getAsJsonArray(), at = bone.get(2).getAsJsonArray(), boxes = bone.get(3).getAsJsonArray();
            float[][] cubes = new float[boxes.size()][];
            for (int index = 0; index < cubes.length; index++) {
                JsonArray box = boxes.get(index).getAsJsonArray();
                cubes[index] = new float[box.size()];
                for (int value = 0; value < box.size(); value++) cubes[index][value] = box.get(value).getAsFloat();
            }
            bones.add(new Bone(bone.get(0).getAsString(), bone.get(1).getAsString(), PartPose.offsetAndRotation(at.get(0).getAsFloat(), at.get(1).getAsFloat(),
                    at.get(2).getAsFloat(), at.get(3).getAsFloat(), at.get(4).getAsFloat(), at.get(5).getAsFloat()), cubes));
        }
        return new GeoModel(texture.get(0).getAsInt(), texture.get(1).getAsInt(), bones);
    }

    /** Parents come before their children. */
    public List<Bone> bones() { return bones; }

    public LayerDefinition layer() {
        MeshDefinition mesh = new MeshDefinition();
        Map<String, PartDefinition> parts = new HashMap<>();
        parts.put("", mesh.getRoot());
        for (Bone bone : bones) {
            CubeListBuilder cubes = CubeListBuilder.create();
            for (float[] cube : bone.cubes)
                cubes.texOffs((int) cube[6], (int) cube[7]).mirror(cube.length > 9 && cube[9] != 0)
                        .addBox(cube[0], cube[1], cube[2], cube[3], cube[4], cube[5], new CubeDeformation(cube.length > 8 ? cube[8] : 0));
            parts.put(bone.name, parts.get(bone.parent).addOrReplaceChild(bone.name, cubes, bone.pose));
        }
        return LayerDefinition.create(mesh, textureWidth, textureHeight);
    }

    /** Every bone of a model baked from this, by name and in the order of {@link #bones()}. */
    public Map<String, ModelPart> parts(ModelPart root) {
        Map<String, ModelPart> parts = new LinkedHashMap<>();
        for (Bone bone : bones) parts.put(bone.name, (bone.parent.isEmpty() ? root : parts.get(bone.parent)).getChild(bone.name));
        return parts;
    }
}
