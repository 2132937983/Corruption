package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneKey;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.common.client.ArcaneLecternScreen;
import com.miophas.corruption.common.registry.ModDataComponents;
import com.miophas.corruption.common.registry.ModItems;
import com.mojang.serialization.JsonOps;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

/** Ties the lectern's code to its files: the slots and list to the GUI picture, and the arcane's data to its readers. */
class ArcaneLecternTest {
    /** The Clavicula's strings, in its tag's order: the first ten spirits of the Ars Goetia, then the corruption's own two. */
    /** What the Prologue reveals: the corruption's own arts. The other books are BooksAndWandsTest's. */
    private static final String[] STRINGS = {"corruption", "transmutation", "corruption_barrage", "corruption_nova", "corruption_vortex", "corruption_tempest", "corruption_rain"};

    private static InputStream resource(String path) {
        InputStream in = ArcaneLecternTest.class.getResourceAsStream(path);
        assertNotNull(in, "Missing resource " + path);
        return in;
    }
    private static JsonObject json(String path) throws Exception {
        try (InputStream in = resource(path)) { return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject(); }
    }
    private static BufferedImage gui() throws Exception {
        try (InputStream in = resource("/assets/corruption/textures/gui/gui_corruption_arcane_lectern.png")) { return ImageIO.read(in); }
    }
    private static int rgb(BufferedImage image, int x, int y) { return image.getRGB(x, y) & 0xFFFFFF; }

    @Test void theStringsAreDataAndThePrologueHoldsTheCorruptionsOwn() throws Exception {
        var tag = json("/data/corruption/tags/corruption/arcane_string/clavicula_salomonis.json").getAsJsonArray("values");
        for (String name : STRINGS) {
            ArcaneString string = ArcaneString.CODEC.parse(JsonOps.INSTANCE, json("/data/corruption/corruption/arcane_string/" + name + ".json")).getOrThrow();
            assertFalse(string.text().isBlank());
            assertEquals("arcane.corruption." + name, ((TranslatableContents) string.meaning().getContents()).getKey());
            assertTrue(tag.contains(new JsonPrimitive("corruption:" + name)), "the Prologue reveals " + name);
        }
        assertEquals(STRINGS.length, tag.size());
        assertTrue(ArcaneString.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"text\": \"\", \"meaning\": \"x\"}")).isError(), "a string has an arcane form");
        assertEquals("plain words work too", ArcaneString.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"text\": \"ab\", \"meaning\": \"plain words work too\"}")).getOrThrow().meaning().getString());
        assertEquals("corruption:arcane_string", ArcaneString.REGISTRY.location().toString());
    }

    @Test void theClaviculaIsAKeyByData() throws Exception {
        JsonObject values = json("/data/corruption/data_maps/item/arcane_key.json").getAsJsonObject("values");
        ArcaneKey key = ArcaneKey.CODEC.parse(JsonOps.INSTANCE, values.get("corruption:clavicula_salomonis")).getOrThrow();
        assertEquals(ResourceLocation.fromNamespaceAndPath("corruption", "clavicula_salomonis"), key.strings().location());
        assertEquals(1, ModItems.CLAVICULA_SALOMONIS.get().getDefaultMaxStackSize(), "there is one of each key");
    }

    @Test void parchmentIsMadeFromLeatherAndInkFromAnInkSacWaterAndABottle() throws Exception {
        JsonObject recipe = json("/data/corruption/recipe/parchment.json");
        assertEquals("minecraft:leather", recipe.getAsJsonArray("ingredients").get(0).getAsJsonObject().get("item").getAsString());
        assertEquals("corruption:parchment", recipe.getAsJsonObject("result").get("id").getAsString());
        assertTrue(json("/data/corruption/tags/item/parchment.json").getAsJsonArray("values").contains(new JsonPrimitive("corruption:parchment")));
        var ink = json("/data/corruption/tags/item/arcane_ink.json").getAsJsonArray("values");
        assertEquals(1, ink.size());
        assertTrue(ink.contains(new JsonPrimitive("corruption:ink")), "only the mod's ink is ink");
        JsonObject inkRecipe = json("/data/corruption/recipe/ink.json");
        java.util.Set<String> ingredients = new java.util.HashSet<>();
        inkRecipe.getAsJsonArray("ingredients").forEach(ingredient -> ingredients.add(ingredient.getAsJsonObject().get("item").getAsString()));
        assertEquals(java.util.Set.of("minecraft:ink_sac", "minecraft:water_bucket", "minecraft:glass_bottle"), ingredients);
        assertEquals("corruption:ink", inkRecipe.getAsJsonObject("result").get("id").getAsString());
        ItemStack bottle = new ItemStack(ModItems.INK.get());
        assertEquals(ModItems.INK_USES, bottle.getMaxDamage(), "a bottle is good for so many copies");
        assertTrue(bottle.getCraftingRemainingItem().is(net.minecraft.world.item.Items.GLASS_BOTTLE), "and leaves its bottle");
        assertEquals(ArcaneApi.PARCHMENT.location(), ResourceLocation.fromNamespaceAndPath("corruption", "parchment"));
        assertEquals(ArcaneApi.INK.location(), ResourceLocation.fromNamespaceAndPath("corruption", "arcane_ink"));
    }

    @Test void anInscribedParchmentSaysSoAndTheApiNamesTheModsComponent() {
        assertSame(ModDataComponents.INSCRIPTION.get(), ArcaneApi.INSCRIPTION.get());
        ItemStack blank = new ItemStack(ModItems.PARCHMENT.get());
        ItemStack written = ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get(), 5), ArcaneApi.key("corruption:baal"));
        assertEquals(1, written.getCount(), "one copy");
        assertEquals(ResourceKey.create(ArcaneString.REGISTRY, ResourceLocation.fromNamespaceAndPath("corruption", "baal")), ArcaneApi.inscription(written));
        assertNull(ArcaneApi.inscription(blank));
        assertEquals("item.corruption.parchment", ((TranslatableContents) blank.getHoverName().getContents()).getKey());
        assertEquals("item.corruption.parchment.inscribed", ((TranslatableContents) written.getHoverName().getContents()).getKey());
        assertNull(ArcaneApi.key("not an id!"));
    }

    @Test void itsModelsTexturesAndWordsAreThere() throws Exception {
        assertEquals(8, json("/assets/corruption/blockstates/corruption_arcane_lectern.json").getAsJsonObject("variants").size(), "four facings, with a key or not");
        // Shaped in Blockbench after the enchanting table: an altar twelve pixels high, candles on its corners, and with a key the key floating open above.
        JsonObject bare = json("/assets/corruption/models/block/corruption_arcane_lectern.json"), open = json("/assets/corruption/models/block/corruption_arcane_lectern_key.json");
        java.util.Set<String> bareParts = new java.util.HashSet<>(), openParts = new java.util.HashSet<>();
        bare.getAsJsonArray("elements").forEach(element -> bareParts.add(element.getAsJsonObject().get("name").getAsString()));
        open.getAsJsonArray("elements").forEach(element -> openParts.add(element.getAsJsonObject().get("name").getAsString()));
        assertTrue(openParts.containsAll(bareParts));
        assertTrue(openParts.containsAll(java.util.Set.of("cover_left", "pages_left", "cover_right", "pages_right")), "the key lies open");
        assertFalse(bareParts.contains("pages_left"));
        for (JsonObject model : new JsonObject[]{bare, open}) {
            assertEquals("minecraft:block/block", model.get("parent").getAsString(), "held and dropped as blocks are");
            for (var element : model.getAsJsonArray("elements")) {
                var part = element.getAsJsonObject();
                if (part.get("name").getAsString().equals("cap")) assertEquals(12, part.getAsJsonArray("to").get(1).getAsInt(), "as high as an enchanting table");
            }
            for (var texture : model.getAsJsonObject("textures").entrySet())
                resource("/assets/corruption/textures/" + texture.getValue().getAsString().substring("corruption:".length()) + ".png").close();
        }
        JsonObject parchment = json("/assets/corruption/models/item/parchment.json");
        assertEquals("corruption:item/parchment_inscribed", parchment.getAsJsonArray("overrides").get(0).getAsJsonObject().get("model").getAsString());
        for (String texture : new String[]{"item/clavicula_salomonis", "item/parchment", "item/parchment_inscribed", "item/ink"})
            resource("/assets/corruption/textures/" + texture + ".png").close();
        json("/data/corruption/loot_table/blocks/corruption_arcane_lectern.json");
        for (String language : new String[]{"en_us", "zh_cn"}) {
            JsonObject lang = json("/assets/corruption/lang/" + language + ".json");
            for (String key : new String[]{"item.corruption.clavicula_salomonis", "item.corruption.parchment", "item.corruption.parchment.inscribed",
                    "block.corruption.corruption_arcane_lectern", "container.corruption.corruption_arcane_lectern", "gui.corruption.transcribe",
                    "gui.corruption.lectern_empty", "tooltip.corruption.parchment_blank", "tooltip.corruption.unknown_string",
                    "tooltip.corruption.key_strings", "tooltip.corruption.key_hint", "item.corruption.ink", "tooltip.corruption.ink_left"})
                assertTrue(lang.has(key), language + " is missing " + key);
            for (var check : com.miophas.corruption.core.arcane.Transcription.Check.values())
                if (check != com.miophas.corruption.core.arcane.Transcription.Check.READY)
                    assertTrue(lang.has("gui.corruption.transcription." + check.name().toLowerCase(java.util.Locale.ROOT)), language + " says nothing of " + check);
            for (String name : STRINGS) assertTrue(lang.has("arcane.corruption." + name), language + " has no meaning for " + name);
        }
    }

    @Test void slotsListAndButtonSitWhereThePictureDrawsThem() throws Exception {
        BufferedImage gui = gui();
        int slotFill = 0x8B8B8B, inventoryFill = 0x4D4D4D;
        for (int[] slot : new int[][]{{ArcaneLecternMenu.KEY_X, ArcaneLecternMenu.KEY_Y}, {ArcaneLecternMenu.PARCHMENT_X, ArcaneLecternMenu.PARCHMENT_Y},
                {ArcaneLecternMenu.INK_X, ArcaneLecternMenu.INK_Y}, {ArcaneLecternMenu.OUTPUT_X, ArcaneLecternMenu.OUTPUT_Y}}) {
            for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) assertEquals(slotFill, rgb(gui, slot[0] + x, slot[1] + y), "slot at " + slot[0] + "," + slot[1]);
            assertNotEquals(slotFill, rgb(gui, slot[0] - 1, slot[1] + 8), "and no wider");
        }
        for (int row = 0; row < 4; row++)
            for (int column = 0; column < 9; column++) {
                int x = ArcaneLecternMenu.INVENTORY_X + column * 18, y = row < 3 ? ArcaneLecternMenu.INVENTORY_Y + row * 18 : ArcaneLecternMenu.HOTBAR_Y;
                assertEquals(inventoryFill, rgb(gui, x, y)); assertEquals(inventoryFill, rgb(gui, x + 15, y + 15), "inventory slot " + row + "," + column);
            }
        // The list's rows and scroll bar lie inside its sunken panel, which nothing else overlaps.
        int listFill = 0x140F18;
        for (int y = ArcaneLecternScreen.LIST_Y; y < ArcaneLecternScreen.LIST_Y + ArcaneLecternScreen.ROWS * ArcaneLecternScreen.ROW_HEIGHT; y++)
            for (int x = ArcaneLecternScreen.LIST_X - 1; x <= ArcaneLecternScreen.LIST_X + ArcaneLecternScreen.LIST_WIDTH; x++)
                assertEquals(listFill, rgb(gui, x, y), "list at " + x + "," + y);
        assertTrue(ArcaneLecternScreen.LIST_X + ArcaneLecternScreen.LIST_WIDTH + 1 < ArcaneLecternScreen.SCROLL_X);
        assertTrue(ArcaneLecternScreen.SCROLL_X + ArcaneLecternScreen.SCROLL_WIDTH < ArcaneLecternMenu.PARCHMENT_X - 1, "the scroll bar stops short of the slots");
        assertTrue(ArcaneLecternScreen.BUTTON_Y > ArcaneLecternScreen.LIST_Y + ArcaneLecternScreen.ROWS * ArcaneLecternScreen.ROW_HEIGHT + 2, "the button is below the list");
        assertTrue(ArcaneLecternScreen.BUTTON_Y + ArcaneLecternScreen.BUTTON_HEIGHT < ArcaneLecternMenu.INVENTORY_Y - 12, "and above the inventory's label");
        // The button's three looks are drawn below the panel.
        for (int look = 0; look < 3; look++) {
            int y = ArcaneLecternScreen.BUTTON_V + look * ArcaneLecternScreen.BUTTON_HEIGHT + ArcaneLecternScreen.BUTTON_HEIGHT / 2;
            assertEquals(255, gui.getRGB(ArcaneLecternScreen.BUTTON_U + ArcaneLecternScreen.BUTTON_WIDTH / 2, y) >>> 24, "button look " + look);
        }
        assertEquals(0, gui.getRGB(ArcaneLecternScreen.BUTTON_U + ArcaneLecternScreen.BUTTON_WIDTH + 2, ArcaneLecternScreen.BUTTON_V + 2) >>> 24, "and nothing beside them");
    }
}
