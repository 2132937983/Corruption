package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.common.registry.ModBlocks;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.Test;

/** Ties the arcane pedestal to its files: its model to its faces and textures, and its outline to its model. */
class ArcanePedestalAssetsTest {
    private static final String MODEL = "/assets/corruption/models/block/corruption_arcane_pedestal.json";

    private static InputStream resource(String path) {
        InputStream in = ArcanePedestalAssetsTest.class.getResourceAsStream(path);
        assertNotNull(in, "Missing resource " + path);
        return in;
    }
    private static JsonObject json(String path) throws Exception {
        try (InputStream in = resource(path)) { return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject(); }
    }
    private static double[] corner(JsonObject part, String which) {
        JsonArray at = part.getAsJsonArray(which);
        return new double[]{at.get(0).getAsDouble(), at.get(1).getAsDouble(), at.get(2).getAsDouble()};
    }

    /** Faces of a block model that lie in the same plane over the same area, which the game draws flickering through each other. */
    private static List<String> fighting(JsonObject model) {
        record Face(String name, int axis, double at, double[] rect) {}
        List<Face> faces = new ArrayList<>();
        for (var element : model.getAsJsonArray("elements")) {
            JsonObject part = element.getAsJsonObject();
            double[] from = corner(part, "from"), to = corner(part, "to");
            for (String side : part.getAsJsonObject("faces").keySet()) {
                int axis = switch (side) { case "west", "east" -> 0; case "down", "up" -> 1; default -> 2; };
                double at = side.equals("west") || side.equals("down") || side.equals("north") ? from[axis] : to[axis];
                int u = axis == 0 ? 1 : 0, v = axis == 2 ? 1 : 2;
                faces.add(new Face(part.get("name").getAsString() + " " + side, axis, at, new double[]{from[u], to[u], from[v], to[v]}));
            }
        }
        List<String> fights = new ArrayList<>();
        for (int one = 0; one < faces.size(); one++)
            for (int other = one + 1; other < faces.size(); other++) {
                Face a = faces.get(one), b = faces.get(other);
                if (a.axis() == b.axis() && a.at() == b.at() && Math.min(a.rect()[1], b.rect()[1]) > Math.max(a.rect()[0], b.rect()[0]) && Math.min(a.rect()[3], b.rect()[3]) > Math.max(a.rect()[2], b.rect()[2]))
                    fights.add(a.name() + " / " + b.name());
            }
        return fights;
    }

    @Test void itsModelIsWholePixelsWithoutFlickerAndItsTexturesAreDrawn() throws Exception {
        JsonObject model = json(MODEL);
        assertEquals("minecraft:block/block", model.get("parent").getAsString(), "the item's display comes from the game's block");
        assertEquals(List.of(), fighting(model));
        for (var element : model.getAsJsonArray("elements")) {
            JsonObject part = element.getAsJsonObject();
            for (String which : new String[]{"from", "to"})
                for (double at : corner(part, which)) {
                    assertEquals(Math.rint(at), at, part.get("name") + " stands on whole pixels");
                    assertTrue(at >= 0 && at <= 16, part.get("name") + " stays within its block");
                }
            for (var face : part.getAsJsonObject("faces").entrySet()) {
                String texture = face.getValue().getAsJsonObject().get("texture").getAsString().substring(1);
                assertTrue(model.getAsJsonObject("textures").has(texture), part.get("name") + " " + face.getKey() + " uses #" + texture);
            }
        }
        for (var texture : model.getAsJsonObject("textures").entrySet()) {
            String id = texture.getValue().getAsString();
            assertTrue(id.startsWith("corruption:block/"), id);
            BufferedImage image;
            try (InputStream in = resource("/assets/corruption/textures/block/" + id.substring("corruption:block/".length()) + ".png")) { image = ImageIO.read(in); }
            assertEquals(16, image.getWidth(), id);
            assertEquals(16, image.getHeight(), id);
        }
        assertNull(ArcanePedestalAssetsTest.class.getResourceAsStream("/assets/corruption/textures/block/block_corruption_arcane_pedestal.png"), "the old draft is gone");
        assertEquals("corruption:block/corruption_arcane_pedestal",
                json("/assets/corruption/blockstates/corruption_arcane_pedestal.json").getAsJsonObject("variants").getAsJsonObject("").get("model").getAsString());
        assertEquals("corruption:block/corruption_arcane_pedestal", json("/assets/corruption/models/item/corruption_arcane_pedestal.json").get("parent").getAsString());
    }

    @Test void itsOutlineIsItsModel() throws Exception {
        VoxelShape parts = Shapes.empty();
        for (var element : json(MODEL).getAsJsonArray("elements")) {
            double[] from = corner(element.getAsJsonObject(), "from"), to = corner(element.getAsJsonObject(), "to");
            parts = Shapes.or(parts, Block.box(from[0], from[1], from[2], to[0], to[1], to[2]));
        }
        VoxelShape outline = ModBlocks.CORRUPTION_ARCANE_PEDESTAL.get().defaultBlockState().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        assertFalse(Shapes.joinIsNotEmpty(outline, parts, BooleanOp.NOT_SAME), "what is hit and walked into is what is seen");
    }
}
