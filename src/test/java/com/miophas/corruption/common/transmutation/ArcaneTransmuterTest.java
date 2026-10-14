package com.miophas.corruption.common.transmutation;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionFuel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.common.client.ArcaneTransmuterScreen;
import com.miophas.corruption.common.client.GuiTiles;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModDataComponents;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModRecipes;
import com.mojang.serialization.JsonOps;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

/** Ties the transmuter's code to its files: the slots and bars to the GUI picture, and the data files to their readers. */
class ArcaneTransmuterTest {
    private static InputStream resource(String path) {
        InputStream in = ArcaneTransmuterTest.class.getResourceAsStream(path);
        assertNotNull(in, "Missing resource " + path);
        return in;
    }
    private static JsonObject json(String path) throws Exception {
        try (InputStream in = resource(path)) { return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject(); }
    }
    private static BufferedImage gui() throws Exception {
        try (InputStream in = resource("/assets/corruption/textures/gui/gui_corruption_arcane_transmuter.png")) { return ImageIO.read(in); }
    }
    private static int grey(BufferedImage gui, int x, int y) { return gui.getRGB(x, y) & 0xFFFFFF; }

    @Test void slotsSitInTheSlotsDrawnOnThePanel() throws Exception {
        BufferedImage gui = gui();
        int slotFill = 0x8B8B8B, inventoryFill = 0x4D4D4D;
        for (int[] slot : new int[][]{{ArcaneTransmuterMenu.INPUT_X, ArcaneTransmuterMenu.INPUT_Y}, {ArcaneTransmuterMenu.FUEL_X, ArcaneTransmuterMenu.FUEL_Y}}) {
            for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) assertEquals(slotFill, grey(gui, slot[0] + x, slot[1] + y), "slot at " + slot[0] + "," + slot[1]);
            assertNotEquals(slotFill, grey(gui, slot[0] - 1, slot[1] + 8), "and no wider");
        }
        // The result slot is drawn 24 pixels square, with the item in its middle.
        for (int y = -4; y < 20; y++) for (int x = -4; x < 20; x++) assertEquals(slotFill, grey(gui, ArcaneTransmuterMenu.OUTPUT_X + x, ArcaneTransmuterMenu.OUTPUT_Y + y));
        for (int row = 0; row < 4; row++)
            for (int column = 0; column < 9; column++) {
                int x = ArcaneTransmuterMenu.INVENTORY_X + column * 18, y = row < 3 ? ArcaneTransmuterMenu.INVENTORY_Y + row * 18 : ArcaneTransmuterMenu.HOTBAR_Y;
                assertEquals(inventoryFill, grey(gui, x, y)); assertEquals(inventoryFill, grey(gui, x + 15, y + 15), "inventory slot " + row + "," + column);
            }
    }

    @Test void theBarsLieOnTheBareStoneBetweenTheSlots() throws Exception {
        BufferedImage gui = gui();
        GuiTiles.Sheet tiles;
        try (InputStream in = resource("/assets/corruption/xml/gui_tiles.xml")) { tiles = GuiTiles.read(in); }
        int[][] bars = {{ArcaneTransmuterScreen.LOADING_X, ArcaneTransmuterScreen.LOADING_Y}, {ArcaneTransmuterScreen.PROGRESS_X, ArcaneTransmuterScreen.PROGRESS_Y}, {ArcaneTransmuterScreen.ENERGY_X, ArcaneTransmuterScreen.ENERGY_Y}};
        String[] names = {ArcaneTransmuterScreen.LOADING, ArcaneTransmuterScreen.PROGRESS, ArcaneTransmuterScreen.ENERGY};
        for (int bar = 0; bar < bars.length; bar++) {
            GuiTiles.Tile tile = tiles.tile(names[bar]);
            for (int y = 0; y < tile.height(); y++)
                for (int x = 0; x < tile.width(); x++) {
                    int rgb = grey(gui, bars[bar][0] + x, bars[bar][1] + y), red = rgb >> 16, green = rgb >> 8 & 255, blue = rgb & 255;
                    // The stone is dark, flecked with purple; slot frames are light grey and white, and the band above the inventory is red.
                    boolean frame = red > 100 && Math.abs(red - green) < 8 && Math.abs(green - blue) < 8, band = red - green > 25;
                    assertFalse(frame || band, names[bar] + " covers something at " + (bars[bar][0] + x) + "," + (bars[bar][1] + y));
                }
        }
        // The loading bar is centred between the two inputs; the progress bar on the line of the result.
        assertEquals(2 * ArcaneTransmuterMenu.INPUT_X + 16, 2 * ArcaneTransmuterScreen.LOADING_X + tiles.tile(ArcaneTransmuterScreen.LOADING).width());
        assertEquals(ArcaneTransmuterMenu.OUTPUT_Y + 8, ArcaneTransmuterScreen.PROGRESS_Y + tiles.tile(ArcaneTransmuterScreen.PROGRESS).height() / 2);
    }

    @Test void theModelUsesItsOwnTexturesAndTheBenchsBottom() throws Exception {
        JsonObject textures = json("/assets/corruption/models/block/corruption_arcane_transmuter.json").getAsJsonObject("textures");
        assertEquals("corruption:block/block_corruption_bench_bottom", textures.get("bottom").getAsString());
        for (String face : new String[]{"top", "front", "side"}) {
            String texture = textures.get(face).getAsString();
            assertEquals("corruption:block/block_corruption_arcane_transmuter_" + face, texture);
            resource("/assets/corruption/textures/" + texture.substring("corruption:".length()) + ".png").close();
        }
        assertEquals(8, json("/assets/corruption/blockstates/corruption_arcane_transmuter.json").getAsJsonObject("variants").size(), "four facings, lit or not");
        assertSame(ModBlocks.CORRUPTION_ARCANE_TRANSMUTER.get(), ((net.minecraft.world.item.BlockItem) ModItems.CORRUPTION_ARCANE_TRANSMUTER.get()).getBlock());
    }

    @Test void corruptionPowderIsWorthATenthOfAKceAndTakesTenTicks() throws Exception {
        JsonObject values = json("/data/corruption/data_maps/item/corruption_fuel.json").getAsJsonObject("values");
        assertEquals(1, values.size());
        CorruptionFuel powder = CorruptionFuel.CODEC.parse(JsonOps.INSTANCE, values.get("corruption:corruption_dust")).getOrThrow();
        assertEquals(new CorruptionFuel(100, 10), powder);
        assertEquals(new CorruptionFuel(2500, 40), CorruptionFuel.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"energy\": 2500, \"ticks\": 40}")).getOrThrow(), "plain CE works too");
        assertTrue(CorruptionFuel.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"energy\": \"lots\", \"ticks\": 40}")).isError());
        assertTrue(CorruptionFuel.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"energy\": 100, \"ticks\": 0}")).isError(), "it takes some time");
    }

    @Test void aWaterBucketAnd20KceMakeABucketOfFlowingCorruption() throws Exception {
        JsonElement file = json("/data/corruption/recipe/arcane_transmutation/flowing_corruption_bucket.json");
        assertEquals("corruption:arcane_transmutation", file.getAsJsonObject().get("type").getAsString());
        ArcaneTransmutationRecipe recipe = ArcaneTransmutationRecipe.CODEC.codec().parse(JsonOps.INSTANCE, file).getOrThrow();
        assertEquals(20_000, recipe.energy());
        assertTrue(ItemStack.isSameItemSameComponents(new ItemStack(ModItems.FLOWING_CORRUPTION_BUCKET.get()), recipe.result()));
        assertTrue(recipe.matches(new net.minecraft.world.item.crafting.SingleRecipeInput(new ItemStack(Items.WATER_BUCKET)), null));
        assertFalse(recipe.matches(new net.minecraft.world.item.crafting.SingleRecipeInput(new ItemStack(Items.LAVA_BUCKET)), null));
        assertSame(ModRecipes.ARCANE_TRANSMUTATION.get(), recipe.getType());
        assertTrue(recipe.isSpecial(), "it has no recipe book page");
        assertEquals(1, recipe.getIngredients().size());
    }

    @Test void aCandleAnd10KceMakeACorruptionCandleCarrying10Kce() throws Exception {
        JsonObject file = json("/data/corruption/recipe/arcane_transmutation/corruption_candle.json");
        ArcaneTransmutationRecipe recipe = ArcaneTransmutationRecipe.CODEC.codec().parse(JsonOps.INSTANCE, file).getOrThrow();
        assertEquals(10_000, recipe.energy());
        assertTrue(recipe.result().is(ModItems.CORRUPTION_CANDLE.get()));
        assertEquals(10_000L, recipe.result().get(CorruptionEnergyCarrier.ENERGY.get()), "the candle carries what its making cost");
        assertEquals("c:candles", file.getAsJsonObject("ingredient").get("tag").getAsString());
        // The common tag is the mod's to fill: it holds every vanilla candle, so it is never empty.
        assertTrue(json("/data/c/tags/item/candles.json").getAsJsonArray("values").contains(new com.google.gson.JsonPrimitive("#minecraft:candles")));
        assertSame(ModDataComponents.CORRUPTION_ENERGY.get(), CorruptionEnergyCarrier.ENERGY.get(), "the API names the component the mod registers");
    }

    @Test void theCandleCarriesUpTo10KceAndBurnsAwayOnceEmpty() throws Exception {
        JsonObject values = json("/data/corruption/data_maps/item/corruption_energy_carrier.json").getAsJsonObject("values");
        assertEquals(new CorruptionEnergyCarrier(10_000, true), CorruptionEnergyCarrier.CODEC.parse(JsonOps.INSTANCE, values.get("corruption:corruption_candle")).getOrThrow());
        assertEquals(new CorruptionEnergyCarrier(500, false), CorruptionEnergyCarrier.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"capacity\": 500}")).getOrThrow(), "other carriers stay once empty unless they say otherwise");
        assertTrue(CorruptionEnergyCarrier.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"consumed\": true}")).isError(), "a carrier says how much it carries");
        assertInstanceOf(com.miophas.corruption.api.energy.CorruptionEnergyItem.class, ModItems.CORRUPTION_CANDLE.get(), "the candle is one of the API's energy items");
        assertEquals("corruption:item/corruption_candle", json("/assets/corruption/models/item/corruption_candle.json").getAsJsonObject("textures").get("layer0").getAsString());
        resource("/assets/corruption/textures/item/corruption_candle.png").close();
        for (String language : new String[]{"en_us", "zh_cn"}) {
            JsonObject lang = json("/assets/corruption/lang/" + language + ".json");
            for (String key : new String[]{"item.corruption.corruption_candle", "tooltip.corruption.carried_energy", "tooltip.corruption.pour_energy",
                    "message.corruption.energy_poured", "message.corruption.energy_no_room"})
                assertTrue(lang.has(key), language + " is missing " + key);
        }
    }

    @Test void energyCrossesToTheScreenInTwoHalves() {
        for (long energy : new long[]{0, 1, 0xFFFF, 0x10000, 45_300, 100_000}) {
            int low = (int) (energy & 0xFFFF), high = (int) (energy >>> 16 & 0xFFFF);
            // The game sends each as a signed 16-bit number; whatever sign it arrives with, the halves join back up.
            assertEquals(energy, ArcaneTransmuterBlockEntity.join((short) low, (short) high));
        }
    }

    @Test void workingItShowsALitFront() throws Exception {
        JsonObject variants = json("/assets/corruption/blockstates/corruption_arcane_transmuter.json").getAsJsonObject("variants");
        for (String facing : new String[]{"north", "east", "south", "west"}) {
            assertEquals("corruption:block/corruption_arcane_transmuter", variants.getAsJsonObject("facing=" + facing + ",lit=false").get("model").getAsString());
            assertEquals("corruption:block/corruption_arcane_transmuter_on", variants.getAsJsonObject("facing=" + facing + ",lit=true").get("model").getAsString());
        }
        JsonObject idle = json("/assets/corruption/models/block/corruption_arcane_transmuter.json").getAsJsonObject("textures");
        JsonObject working = json("/assets/corruption/models/block/corruption_arcane_transmuter_on.json").getAsJsonObject("textures");
        assertEquals("corruption:block/block_corruption_arcane_transmuter_front_on", working.get("front").getAsString());
        for (String face : new String[]{"top", "side", "bottom"}) assertEquals(idle.get(face), working.get(face), "only the front changes");
        // Four frames stacked into one picture, and the file beside it telling the game to play them.
        BufferedImage front;
        try (InputStream in = resource("/assets/corruption/textures/block/block_corruption_arcane_transmuter_front_on.png")) { front = ImageIO.read(in); }
        assertEquals(16, front.getWidth()); assertEquals(64, front.getHeight());
        assertTrue(json("/assets/corruption/textures/block/block_corruption_arcane_transmuter_front_on.png.mcmeta").getAsJsonObject("animation").get("frametime").getAsInt() > 0);
        BufferedImage rest;
        try (InputStream in = resource("/assets/corruption/textures/block/block_corruption_arcane_transmuter_front.png")) { rest = ImageIO.read(in); }
        assertEquals(rest.getRGB(0, 0), front.getRGB(0, 0), "the stone round the mouth is the resting front's");
        assertNotEquals(rest.getRGB(7, 12), front.getRGB(7, 12), "the mouth glows");
    }

    @Test void itsMotesUseTheCorruptionParticle() throws Exception {
        assertEquals("corruption:corruption_particle", com.miophas.corruption.common.registry.ModParticles.CORRUPTION_PARTICLE.getId().toString());
        var textures = json("/assets/corruption/particles/corruption_particle.json").getAsJsonArray("textures");
        assertEquals(1, textures.size());
        assertEquals("corruption:corruption_particle", textures.get(0).getAsString());
        resource("/assets/corruption/textures/particle/corruption_particle.png").close();
    }

    @Test void noTwoThingsOnTheScreenWantTheMouseInTheSamePlace() throws Exception {
        GuiTiles.Sheet tiles;
        try (InputStream in = resource("/assets/corruption/xml/gui_tiles.xml")) { tiles = GuiTiles.read(in); }
        // The game counts the mouse as over a slot or bar from one pixel outside it, so each area is grown by one.
        java.util.Map<String, int[]> areas = new java.util.LinkedHashMap<>();
        for (String bar : new String[]{ArcaneTransmuterScreen.ENERGY, ArcaneTransmuterScreen.PROGRESS, ArcaneTransmuterScreen.LOADING}) {
            int[] at = switch (bar) {
                case ArcaneTransmuterScreen.ENERGY -> new int[]{ArcaneTransmuterScreen.ENERGY_X, ArcaneTransmuterScreen.ENERGY_Y};
                case ArcaneTransmuterScreen.PROGRESS -> new int[]{ArcaneTransmuterScreen.PROGRESS_X, ArcaneTransmuterScreen.PROGRESS_Y};
                default -> new int[]{ArcaneTransmuterScreen.LOADING_X, ArcaneTransmuterScreen.LOADING_Y};
            };
            areas.put(bar, new int[]{at[0] - 1, at[1] - 1, at[0] + tiles.tile(bar).width() + 1, at[1] + tiles.tile(bar).height() + 1});
        }
        areas.put("input", new int[]{ArcaneTransmuterMenu.INPUT_X - 1, ArcaneTransmuterMenu.INPUT_Y - 1, ArcaneTransmuterMenu.INPUT_X + 17, ArcaneTransmuterMenu.INPUT_Y + 17});
        areas.put("fuel", new int[]{ArcaneTransmuterMenu.FUEL_X - 1, ArcaneTransmuterMenu.FUEL_Y - 1, ArcaneTransmuterMenu.FUEL_X + 17, ArcaneTransmuterMenu.FUEL_Y + 17});
        areas.put("output", new int[]{ArcaneTransmuterMenu.OUTPUT_X - 1, ArcaneTransmuterMenu.OUTPUT_Y - 1, ArcaneTransmuterMenu.OUTPUT_X + 17, ArcaneTransmuterMenu.OUTPUT_Y + 17});
        var names = new java.util.ArrayList<>(areas.keySet());
        for (int one = 0; one < names.size(); one++)
            for (int other = one + 1; other < names.size(); other++) {
                int[] a = areas.get(names.get(one)), b = areas.get(names.get(other));
                assertFalse(a[0] < b[2] && b[0] < a[2] && a[1] < b[3] && b[1] < a[3], names.get(one) + " and " + names.get(other) + " both answer the mouse somewhere");
            }
    }
}
