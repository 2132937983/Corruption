package com.miophas.corruption.common.alchemy;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.common.block.CorruptionBenchBlock;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModMenus;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.RenderShape;
import org.junit.jupiter.api.Test;

/** Ties the bench's code to its files: the slot positions to the GUI picture, and the registrations to their resources. */
class CorruptionBenchTest {
    private static InputStream resource(String path) {
        InputStream in = CorruptionBenchTest.class.getResourceAsStream(path);
        assertNotNull(in, "Missing resource " + path);
        return in;
    }

    private static JsonObject json(String path) throws Exception {
        try (InputStream in = resource(path)) {
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    private static BufferedImage image(String path) throws Exception {
        try (InputStream in = resource(path)) {
            return ImageIO.read(in);
        }
    }

    private static boolean red(BufferedImage gui, int x, int y) {
        int rgb = gui.getRGB(x, y), r = rgb >> 16 & 255, g = rgb >> 8 & 255, b = rgb & 255;
        return r > 110 && g < 90 && b < 90 && r - g > 60;
    }

    private static boolean redNear(BufferedImage gui, int x0, int x1, int y0, int y1) {
        for (int y = y0; y <= y1; y++) for (int x = x0; x <= x1; x++) if (red(gui, x, y)) return true;
        return false;
    }

    @Test void inputSlotsSitInsideTheFiveRingsOfThePentagram() throws Exception {
        BufferedImage gui = image("/assets/corruption/textures/gui/gui_corruption_bench.png");
        assertEquals(AlchemyRecipe.SLOTS, CorruptionBenchMenu.INPUT_POSITIONS.length);
        for (int[] slot : CorruptionBenchMenu.INPUT_POSITIONS) {
            int x = slot[0], y = slot[1], cx = x + 8, cy = y + 8;
            String where = "slot at " + x + "," + y;
            assertFalse(redNear(gui, x + 4, x + 11, y + 4, y + 11), where + " is not on clear parchment");
            // The ring is 9 pixels out from its centre, so its line must pass just outside each edge of the item.
            assertTrue(redNear(gui, x - 3, x + 1, cy - 1, cy), where + " has no ring on its left");
            assertTrue(redNear(gui, x + 14, x + 18, cy - 1, cy), where + " has no ring on its right");
            assertTrue(redNear(gui, cx - 1, cx, y - 3, y + 1), where + " has no ring above it");
            assertTrue(redNear(gui, cx - 1, cx, y + 14, y + 18), where + " has no ring below it");
        }
    }

    @Test void resultSlotSitsInTheClearCentreOfThePentagram() throws Exception {
        BufferedImage gui = image("/assets/corruption/textures/gui/gui_corruption_bench.png");
        int x = CorruptionBenchMenu.RESULT_X, y = CorruptionBenchMenu.RESULT_Y;
        assertFalse(redNear(gui, x, x + 15, y, y + 15), "The result item would cover the pentagram's lines");
        // The inner pentagon is closed: there are lines on every side of the result.
        assertTrue(redNear(gui, x - 12, x - 1, y + 8, y + 8) && redNear(gui, x + 16, x + 28, y + 8, y + 8));
        assertTrue(redNear(gui, x + 8, x + 8, y - 16, y - 1) && redNear(gui, x + 8, x + 8, y + 16, y + 26));
    }

    @Test void playerInventorySlotsMatchTheDrawnGrid() throws Exception {
        BufferedImage gui = image("/assets/corruption/textures/gui/gui_corruption_bench.png");
        int cell = gui.getRGB(CorruptionBenchMenu.INVENTORY_X, CorruptionBenchMenu.INVENTORY_Y);
        for (int row = 0; row < 4; row++) {
            int y = row < 3 ? CorruptionBenchMenu.INVENTORY_Y + row * 18 : CorruptionBenchMenu.HOTBAR_Y;
            for (int column = 0; column < 9; column++) {
                int x = CorruptionBenchMenu.INVENTORY_X + column * 18;
                for (int[] corner : new int[][] {{x, y}, {x + 15, y}, {x, y + 15}, {x + 15, y + 15}})
                    assertEquals(cell, gui.getRGB(corner[0], corner[1]), "Slot " + column + "," + row + " is not on a drawn cell");
                // One pixel further out is the cell's border, not another cell.
                assertNotEquals(cell, gui.getRGB(x - 1, y));
                assertNotEquals(cell, gui.getRGB(x, y - 1));
            }
        }
        assertEquals(42, CorruptionBenchMenu.END_PLAYER_SLOT);
    }

    @Test void blockItemAndMenuAreRegisteredAndTheBlockStoresNothing() {
        var block = ModBlocks.CORRUPTION_BENCH.get();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("corruption", "corruption_bench");
        assertInstanceOf(CorruptionBenchBlock.class, block);
        assertEquals(id, BuiltInRegistries.BLOCK.getKey(block));
        assertEquals(RenderShape.MODEL, block.defaultBlockState().getRenderShape());
        assertSame(block, assertInstanceOf(BlockItem.class, ModItems.CORRUPTION_BENCH.get()).getBlock());
        // Ingredients go back to the player when the screen closes, so there is nothing for a block entity to keep.
        assertFalse(block.defaultBlockState().hasBlockEntity());
        assertEquals(id, BuiltInRegistries.MENU.getKey(ModMenus.CORRUPTION_BENCH.get()));
    }

    @Test void modelTexturesLootTagsAndNamesExist() throws Exception {
        assertEquals("corruption:block/corruption_bench", json("/assets/corruption/blockstates/corruption_bench.json")
                .getAsJsonObject("variants").getAsJsonObject("").get("model").getAsString());
        JsonObject textures = json("/assets/corruption/models/block/corruption_bench.json").getAsJsonObject("textures");
        for (String face : new String[] {"top", "side", "bottom"}) {
            ResourceLocation texture = ResourceLocation.parse(textures.get(face).getAsString());
            BufferedImage drawn = image("/assets/" + texture.getNamespace() + "/textures/" + texture.getPath() + ".png");
            assertEquals(16, drawn.getWidth(), face);
            assertEquals(16, drawn.getHeight(), face);
        }
        assertEquals("corruption:block/corruption_bench", json("/assets/corruption/models/item/corruption_bench.json").get("parent").getAsString());
        assertTrue(json("/data/corruption/loot_table/blocks/corruption_bench.json").toString().contains("\"corruption:corruption_bench\""));
        assertTrue(json("/data/minecraft/tags/block/mineable/axe.json").toString().contains("\"corruption:corruption_bench\""));
        // Infection turns ordinary blocks into corrupted copies, which would destroy the bench and spill it.
        assertTrue(json("/data/corruption/tags/block/infection_immune.json").toString().contains("\"corruption:corruption_bench\""));
        for (String language : new String[] {"en_us", "zh_cn"}) {
            JsonObject names = json("/assets/corruption/lang/" + language + ".json");
            assertTrue(names.has("block.corruption.corruption_bench"), language);
            assertTrue(names.has("container.corruption.corruption_bench"), language);
            assertTrue(names.has("corruption.jei.alchemy"), language);
        }
    }
}
