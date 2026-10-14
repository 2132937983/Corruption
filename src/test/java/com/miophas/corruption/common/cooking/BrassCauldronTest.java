package com.miophas.corruption.common.cooking;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.miophas.corruption.common.client.BrassCauldronScreen;
import com.miophas.corruption.common.client.GuiTiles;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.transmutation.ArcaneTransmutationRecipe;
import com.miophas.corruption.core.cooking.BrassCauldronRules;
import com.mojang.serialization.JsonOps;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.Test;

/** Ties the brass cauldron and corrupted brass to their files: the slots to the GUI picture, and the recipes and tags to their readers. */
class BrassCauldronTest {
    private static InputStream resource(String path) {
        InputStream in = BrassCauldronTest.class.getResourceAsStream(path);
        assertNotNull(in, "Missing resource " + path);
        return in;
    }
    private static JsonObject json(String path) throws Exception {
        try (InputStream in = resource(path)) { return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject(); }
    }
    private static BrassCookingRecipe cooking(String json) { return BrassCookingRecipe.CODEC.codec().parse(JsonOps.INSTANCE, JsonParser.parseString(json)).getOrThrow(); }
    private static BrassCookingRecipe cookingFile(String name) throws Exception {
        return BrassCookingRecipe.CODEC.codec().parse(JsonOps.INSTANCE, json("/data/corruption/recipe/brass_cooking/" + name + ".json")).getOrThrow();
    }

    @Test void aRedMushroomTenMbOfWaterAndTenCeCookMushroomStew() throws Exception {
        BrassCookingRecipe stew = cookingFile("mushroom_stew");
        assertEquals(1, stew.ingredients().size());
        assertTrue(stew.ingredients().get(0).test(new ItemStack(Items.RED_MUSHROOM)));
        assertEquals(10, stew.fluid().orElseThrow().amount());
        assertTrue(stew.fluid().orElseThrow().test(new FluidStack(Fluids.WATER, 10)));
        assertFalse(stew.fluid().orElseThrow().test(new FluidStack(Fluids.WATER, 9)), "not with less water");
        assertEquals(10, stew.energy());
        assertEquals(BrassCauldronRules.DEFAULT_COOK_TICKS, stew.time(), "left out, the time is the cauldron's own");
        assertTrue(stew.result().is(Items.MUSHROOM_STEW));
        assertTrue(stew.isSpecial());
    }

    @Test void twoRedstoneInOneSlotAndTenMbOfFlowingCorruptionMakeCorruptionPowderForNoEnergy() throws Exception {
        BrassCookingRecipe powder = cookingFile("corruption_dust");
        assertEquals(2, powder.ingredients().get(0).count(), "two in the one slot");
        assertEquals("corruption:flowing_corruption", net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(powder.fluid().orElseThrow().getFluids()[0].getFluid()).toString());
        assertEquals(0, powder.energy(), "left out, it costs nothing");
        assertTrue(powder.result().is(ModItems.CORRUPTION_DUST.get()));
    }

    @Test void rottenFleshFlowingCorruptionTenCeAndTheRuneThatCorruptsAllMakeCorruptionRemain() throws Exception {
        BrassCookingRecipe remain = cookingFile("corruption_remain");
        assertTrue(remain.ingredients().get(0).test(new ItemStack(Items.ROTTEN_FLESH)));
        assertEquals(10, remain.fluid().orElseThrow().amount());
        assertEquals(10, remain.energy());
        assertEquals(com.miophas.corruption.api.arcane.ArcaneApi.key("corruption:corruption"), remain.inscription().orElseThrow(), "vitium omnia devorat");
        assertTrue(remain.result().is(ModItems.CORRUPTION_REMAIN.get()));
        assertTrue(cookingFile("mushroom_stew").inscription().isEmpty(), "left out, no parchment is asked for");
        // Matching: only the very string will do, and recipes that ask for none do not mind one being there.
        ItemStack[] slots = new ItemStack[BrassCauldronRules.INPUTS];
        java.util.Arrays.fill(slots, ItemStack.EMPTY);
        slots[0] = new ItemStack(Items.ROTTEN_FLESH);
        FluidStack corruption = new FluidStack(com.miophas.corruption.common.registry.ModFluids.FLOWING_CORRUPTION.get(), 10);
        var list = java.util.List.of(slots);
        assertFalse(remain.matches(new BrassCookingInput(list, corruption, null), null), "no parchment");
        assertFalse(remain.matches(new BrassCookingInput(list, corruption, com.miophas.corruption.api.arcane.ArcaneApi.key("corruption:baal")), null), "the wrong rune");
        assertTrue(remain.matches(new BrassCookingInput(list, corruption, com.miophas.corruption.api.arcane.ArcaneApi.key("corruption:corruption")), null));
        slots[0] = new ItemStack(Items.RED_MUSHROOM);
        assertTrue(cookingFile("mushroom_stew").matches(new BrassCookingInput(java.util.List.of(slots), new FluidStack(Fluids.WATER, 10), com.miophas.corruption.api.arcane.ArcaneApi.key("corruption:baal")), null));
    }

    @Test void theThingsCookingFloatWithinItsWalls() {
        for (int slot = 0; slot < BrassCauldronRules.INPUTS; slot++) {
            float[] at = com.miophas.corruption.common.client.BrassCauldronRenderer.spot(slot);
            float half = com.miophas.corruption.common.client.BrassCauldronRenderer.ITEM_SIZE * 16 / 2;
            for (float edge : at) {
                assertTrue(edge - half >= com.miophas.corruption.common.client.BrassCauldronRenderer.INNER_MIN, "slot " + slot);
                assertTrue(edge + half <= com.miophas.corruption.common.client.BrassCauldronRenderer.INNER_MAX, "slot " + slot);
            }
        }
        assertArrayEquals(new float[]{8, 8}, com.miophas.corruption.common.client.BrassCauldronRenderer.spot(0), "the middle one in the middle");
    }

    @Test void recipesHoldOneToSevenThingsAndMayDoWithoutFluid() {
        BrassCookingRecipe dry = cooking("{\"ingredients\": [{\"item\": \"minecraft:wheat\"}, {\"item\": \"minecraft:egg\"}], \"time\": 40, \"energy\": \"1.5KCE\", \"result\": {\"id\": \"minecraft:cake\"}}");
        assertTrue(dry.fluid().isEmpty());
        assertEquals(1500, dry.energy());
        assertEquals(40, dry.time());
        String eight = "{\"item\": \"minecraft:stick\"}, ".repeat(8);
        assertThrows(IllegalStateException.class, () -> cooking("{\"ingredients\": [" + eight.substring(0, eight.length() - 2) + "], \"result\": {\"id\": \"minecraft:cake\"}}"), "no more than seven");
        assertThrows(IllegalStateException.class, () -> cooking("{\"ingredients\": [], \"result\": {\"id\": \"minecraft:cake\"}}"), "at least one");
        assertThrows(IllegalStateException.class, () -> cooking("{\"ingredients\": [{\"item\": \"minecraft:wheat\"}], \"time\": 0, \"result\": {\"id\": \"minecraft:cake\"}}"), "it takes some time");
    }

    @Test void inTheCauldronEveryThingIsAnIngredientInAnySlot() {
        BrassCookingRecipe cake = cooking("{\"ingredients\": [{\"item\": \"minecraft:wheat\", \"count\": 2}, {\"item\": \"minecraft:egg\"}], \"result\": {\"id\": \"minecraft:cake\"}}");
        ItemStack[] slots = new ItemStack[BrassCauldronRules.INPUTS];
        java.util.Arrays.fill(slots, ItemStack.EMPTY);
        slots[6] = new ItemStack(Items.EGG); slots[2] = new ItemStack(Items.WHEAT, 3);
        assertTrue(cake.matches(new BrassCookingInput(java.util.List.of(slots), FluidStack.EMPTY, null), null));
        assertArrayEquals(new int[]{2, 6}, cake.slots(new BrassCookingInput(java.util.List.of(slots), FluidStack.EMPTY, null)));
        slots[2] = new ItemStack(Items.WHEAT, 1); slots[3] = new ItemStack(Items.WHEAT, 1);
        assertFalse(cake.matches(new BrassCookingInput(java.util.List.of(slots), FluidStack.EMPTY, null), null), "two wheat must lie in one slot");
    }

    @Test void copperIsTransmutedIntoCorruptedBrassAndTheCommonTagsNameIt() throws Exception {
        ArcaneTransmutationRecipe brass = ArcaneTransmutationRecipe.CODEC.codec().parse(JsonOps.INSTANCE, json("/data/corruption/recipe/arcane_transmutation/corrupted_brass.json")).getOrThrow();
        assertEquals(5_000, brass.energy());
        assertTrue(brass.result().is(ModItems.CORRUPTED_BRASS.get()));
        assertEquals("c:ingots/copper", json("/data/corruption/recipe/arcane_transmutation/corrupted_brass.json").getAsJsonObject("ingredient").get("tag").getAsString());
        assertTrue(json("/data/c/tags/item/ingots/corrupted_brass.json").getAsJsonArray("values").contains(new JsonPrimitive("corruption:corrupted_brass")));
        assertTrue(json("/data/c/tags/item/ingots.json").getAsJsonArray("values").contains(new JsonPrimitive("#c:ingots/corrupted_brass")));
        for (String kind : new String[]{"item", "block"}) {
            assertTrue(json("/data/c/tags/" + kind + "/storage_blocks/corrupted_brass.json").getAsJsonArray("values").contains(new JsonPrimitive("corruption:corrupted_brass_block")));
            assertTrue(json("/data/c/tags/" + kind + "/storage_blocks.json").getAsJsonArray("values").contains(new JsonPrimitive("#c:storage_blocks/corrupted_brass")));
        }
        assertEquals("#c:ingots/corrupted_brass".substring(1), json("/data/corruption/recipe/corrupted_brass_block.json").getAsJsonObject("key").getAsJsonObject("#").get("tag").getAsString());
        assertEquals(9, json("/data/corruption/recipe/corrupted_brass_from_block.json").getAsJsonObject("result").get("count").getAsInt());
    }

    @Test void itsModelsTexturesAndWordsAreThere() throws Exception {
        JsonObject cauldron = json("/assets/corruption/models/block/brass_cauldron.json");
        assertEquals("minecraft:block/block", cauldron.get("parent").getAsString());
        assertTrue(cauldron.getAsJsonArray("elements").size() > 10, "shaped in Blockbench, not a cube");
        for (var texture : cauldron.getAsJsonObject("textures").entrySet())
            resource("/assets/corruption/textures/" + texture.getValue().getAsString().substring("corruption:".length()) + ".png").close();
        assertEquals(2, json("/assets/corruption/blockstates/brass_cauldron.json").getAsJsonObject("variants").size());
        for (String texture : new String[]{"item/corrupted_brass", "block/block_corrupted_brass_block", "gui/gui_brass_cauldron"})
            resource("/assets/corruption/textures/" + texture + ".png").close();
        json("/data/corruption/loot_table/blocks/brass_cauldron.json");
        json("/data/corruption/loot_table/blocks/corrupted_brass_block.json");
        for (String language : new String[]{"en_us", "zh_cn"}) {
            JsonObject lang = json("/assets/corruption/lang/" + language + ".json");
            for (String key : new String[]{"item.corruption.corrupted_brass", "block.corruption.corrupted_brass_block", "block.corruption.brass_cauldron",
                    "container.corruption.brass_cauldron", "corruption.jei.brass_cooking", "corruption.jei.time", "gui.corruption.cooking",
                    "gui.corruption.tank_empty", "gui.corruption.fluid_amount", "tag.item.c.ingots.corrupted_brass",
                    "gui.corruption.parchment_slot", "gui.corruption.parchment_slot_hint", "corruption.jei.parchment_kept"})
                assertTrue(lang.has(key), language + " is missing " + key);
        }
    }

    /** Faces of a block model that lie in the same plane over the same area, which the game draws flickering through each other. */
    private static java.util.List<String> fighting(JsonObject model) {
        record Face(String name, int axis, double at, double[] rect) {}
        java.util.List<Face> faces = new java.util.ArrayList<>();
        for (var element : model.getAsJsonArray("elements")) {
            JsonObject part = element.getAsJsonObject();
            if (part.has("rotation") && part.getAsJsonObject("rotation").get("angle").getAsDouble() != 0) continue;
            double[] from = new double[3], to = new double[3];
            for (int axis = 0; axis < 3; axis++) { from[axis] = part.getAsJsonArray("from").get(axis).getAsDouble(); to[axis] = part.getAsJsonArray("to").get(axis).getAsDouble(); }
            for (String side : part.getAsJsonObject("faces").keySet()) {
                int axis = switch (side) { case "west", "east" -> 0; case "down", "up" -> 1; default -> 2; };
                double at = side.equals("west") || side.equals("down") || side.equals("north") ? from[axis] : to[axis];
                int u = axis == 0 ? 1 : 0, v = axis == 2 ? 1 : 2;
                faces.add(new Face(part.get("name").getAsString() + " " + side, axis, at, new double[]{from[u], to[u], from[v], to[v]}));
            }
        }
        java.util.List<String> fights = new java.util.ArrayList<>();
        for (int one = 0; one < faces.size(); one++)
            for (int other = one + 1; other < faces.size(); other++) {
                Face a = faces.get(one), b = faces.get(other);
                if (a.axis() != b.axis() || a.at() != b.at()) continue;
                if (Math.min(a.rect()[1], b.rect()[1]) > Math.max(a.rect()[0], b.rect()[0]) && Math.min(a.rect()[3], b.rect()[3]) > Math.max(a.rect()[2], b.rect()[2]))
                    fights.add(a.name() + " / " + b.name());
            }
        return fights;
    }

    @Test void noTwoFacesShareAPlaneSoNothingFlickers() throws Exception {
        JsonObject cauldron = json("/assets/corruption/models/block/brass_cauldron.json");
        assertEquals(java.util.List.of(), fighting(cauldron));
        for (var element : cauldron.getAsJsonArray("elements"))
            for (String end : new String[]{"from", "to"})
                for (var value : element.getAsJsonObject().getAsJsonArray(end))
                    assertEquals(Math.rint(value.getAsDouble()), value.getAsDouble(), "whole pixels only: " + element.getAsJsonObject().get("name"));
        for (String lectern : new String[]{"corruption_arcane_lectern", "corruption_arcane_lectern_key"})
            assertEquals(java.util.List.of(), fighting(json("/assets/corruption/models/block/" + lectern + ".json")), lectern);
        // The fluid inside is drawn above the floor however little there is, and below the rim however much.
        float floor = com.miophas.corruption.common.client.BrassCauldronRenderer.FLOOR / 16F;
        assertTrue(com.miophas.corruption.common.client.BrassCauldronRenderer.surface(1) > floor + .01F);
        assertTrue(com.miophas.corruption.common.client.BrassCauldronRenderer.surface(BrassCauldronRules.TANK) < 1);
    }

    @Test void slotsTankAndBarsSitWhereThePictureDrawsThemAndNeverOverlap() throws Exception {
        BufferedImage gui;
        try (InputStream in = resource("/assets/corruption/textures/gui/gui_brass_cauldron.png")) { gui = ImageIO.read(in); }
        int slotFill = 0x8B8B8B;
        Map<String, int[]> slots = new LinkedHashMap<>();
        for (int input = 0; input < BrassCauldronMenu.INPUT_SLOTS.length; input++) slots.put("input " + input, BrassCauldronMenu.INPUT_SLOTS[input]);
        slots.put("energy", new int[]{BrassCauldronMenu.ENERGY_X, BrassCauldronMenu.ENERGY_Y});
        slots.put("output", new int[]{BrassCauldronMenu.OUTPUT_X, BrassCauldronMenu.OUTPUT_Y});
        for (int row = 0; row < 4; row++)
            for (int column = 0; column < 9; column++)
                slots.put("inventory " + row + "," + column, new int[]{BrassCauldronMenu.INVENTORY_X + column * 18, row < 3 ? BrassCauldronMenu.INVENTORY_Y + row * 18 : BrassCauldronMenu.HOTBAR_Y});
        for (var slot : slots.entrySet())
            for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++)
                assertEquals(slotFill, gui.getRGB(slot.getValue()[0] + x, slot.getValue()[1] + y) & 0xFFFFFF, slot.getKey());
        // The parchment slot, with a faint scroll in it saying what goes there.
        assertEquals(slotFill, gui.getRGB(BrassCauldronMenu.PARCHMENT_X, BrassCauldronMenu.PARCHMENT_Y) & 0xFFFFFF);
        assertEquals(0x7A7A7A, gui.getRGB(BrassCauldronMenu.PARCHMENT_X + 2, BrassCauldronMenu.PARCHMENT_Y + 2) & 0xFFFFFF);
        assertNotEquals(slotFill, gui.getRGB(BrassCauldronMenu.PARCHMENT_X - 1, BrassCauldronMenu.PARCHMENT_Y + 8) & 0xFFFFFF, "framed");
        // The tank is dark until fluid is drawn in it.
        assertEquals(0x1A120A, gui.getRGB(BrassCauldronMenu.TANK_X + 8, BrassCauldronMenu.TANK_Y + 8) & 0xFFFFFF);
        // Nothing that answers the mouse overlaps anything else, a pixel's margin all round.
        GuiTiles.Sheet tiles;
        try (InputStream in = resource("/assets/corruption/xml/gui_tiles.xml")) { tiles = GuiTiles.read(in); }
        Map<String, int[]> areas = new LinkedHashMap<>();
        for (var slot : slots.entrySet()) if (!slot.getKey().startsWith("inventory")) areas.put(slot.getKey(), new int[]{slot.getValue()[0] - 1, slot.getValue()[1] - 1, slot.getValue()[0] + 17, slot.getValue()[1] + 17});
        areas.put("parchment", new int[]{BrassCauldronMenu.PARCHMENT_X - 1, BrassCauldronMenu.PARCHMENT_Y - 1, BrassCauldronMenu.PARCHMENT_X + 17, BrassCauldronMenu.PARCHMENT_Y + 17});
        areas.put("tank", new int[]{BrassCauldronMenu.TANK_X - 1, BrassCauldronMenu.TANK_Y - 1, BrassCauldronMenu.TANK_X + BrassCauldronMenu.TANK_WIDTH + 1, BrassCauldronMenu.TANK_Y + BrassCauldronMenu.TANK_HEIGHT + 1});
        int[][] bars = {{BrassCauldronScreen.LOADING_X, BrassCauldronScreen.LOADING_Y}, {BrassCauldronScreen.PROGRESS_X, BrassCauldronScreen.PROGRESS_Y}, {BrassCauldronScreen.ENERGY_X, BrassCauldronScreen.ENERGY_Y}};
        String[] names = {BrassCauldronScreen.LOADING, BrassCauldronScreen.PROGRESS, BrassCauldronScreen.ENERGY};
        for (int bar = 0; bar < bars.length; bar++)
            areas.put(names[bar], new int[]{bars[bar][0] - 1, bars[bar][1] - 1, bars[bar][0] + tiles.tile(names[bar]).width() + 1, bars[bar][1] + tiles.tile(names[bar]).height() + 1});
        var keys = new java.util.ArrayList<>(areas.keySet());
        for (int one = 0; one < keys.size(); one++)
            for (int other = one + 1; other < keys.size(); other++) {
                int[] a = areas.get(keys.get(one)), b = areas.get(keys.get(other));
                assertFalse(a[0] < b[2] && b[0] < a[2] && a[1] < b[3] && b[1] < a[3], keys.get(one) + " and " + keys.get(other) + " overlap");
            }
        for (int[] area : areas.values()) assertTrue(area[3] < BrassCauldronMenu.INVENTORY_Y - 12, "above the inventory's label");
    }
}
