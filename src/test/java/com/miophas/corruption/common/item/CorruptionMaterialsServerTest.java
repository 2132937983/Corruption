package com.miophas.corruption.common.item;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModItems;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The corruption's raw materials: each is drawn, named and described in both languages, and made by exactly one
 * recipe of the mod's own systems (the transmuter, the cauldron, the bench or the altar), never the game's own
 * crafting, smelting or brewing; and on a server, every one of those recipes loads.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class CorruptionMaterialsServerTest {
    /** The mod's own recipe systems, by folder and by type. */
    private static final Map<String, Set<String>> SYSTEMS = Map.of(
            "arcane_transmutation", Set.of("corruption:arcane_transmutation"),
            "brass_cooking", Set.of("corruption:brass_cooking"),
            "alchemy", Set.of("corruption:alchemy_shaped", "corruption:alchemy_shapeless"),
            "altar", Set.of("corruption:altar_shaped", "corruption:altar_shapeless"));

    private static JsonElement json(String path) throws Exception {
        try (InputStream in = CorruptionMaterialsServerTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing resource " + path);
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }
    private static boolean exists(String path) throws Exception {
        try (InputStream in = CorruptionMaterialsServerTest.class.getResourceAsStream(path)) { return in != null; }
    }
    /** Walks up from wherever the tests run to the project folder. */
    private static Path source(String folder) {
        for (Path at = Path.of("").toAbsolutePath(); at != null; at = at.getParent())
            if (Files.isDirectory(at.resolve(folder))) return at.resolve(folder);
        throw new AssertionError("Cannot find " + folder);
    }
    /** Every recipe the mod's data holds, by its path under recipe/ ("alchemy/shadow_silk"), with what it makes, or "" for a creature. */
    private static Map<String, JsonObject> recipes() throws Exception {
        Map<String, JsonObject> recipes = new HashMap<>();
        Path root = source("src/main/resources/data/corruption/recipe");
        try (Stream<Path> files = Files.walk(root)) {
            for (Path file : files.filter(file -> file.toString().endsWith(".json")).toList()) {
                String path = root.relativize(file).toString().replace('\\', '/').replace(".json", "");
                recipes.put(path, JsonParser.parseString(Files.readString(file)).getAsJsonObject());
            }
        }
        return recipes;
    }
    private static String made(JsonObject recipe) {
        JsonObject result = recipe.getAsJsonObject("result");
        return result == null || !result.has("id") ? "" : result.get("id").getAsString();
    }

    @Test void thereAreADozenOrSoEachDrawnNamedAndDescribed() throws Exception {
        assertTrue(ModItems.MATERIALS.size() >= 12, "a dozen or so");
        JsonObject en = json("/assets/corruption/lang/en_us.json").getAsJsonObject(), zh = json("/assets/corruption/lang/zh_cn.json").getAsJsonObject();
        for (DeferredItem<CorruptionMaterialItem> material : ModItems.MATERIALS) {
            String id = material.getId().getPath();
            JsonObject model = json("/assets/corruption/models/item/" + id + ".json").getAsJsonObject();
            assertEquals("corruption:item/" + id, model.getAsJsonObject("textures").get("layer0").getAsString());
            assertTrue(exists("/assets/corruption/textures/item/" + id + ".png"), id + " is drawn");
            for (JsonObject lang : List.of(en, zh)) {
                assertTrue(lang.has("item.corruption." + id), id + " is named");
                assertTrue(lang.has("item.corruption." + id + ".desc"), id + " says what it is");
            }
        }
        assertEquals(16, ModItems.CORRUPTED_ICHOR.get().getDefaultMaxStackSize(), "bottled, it stacks as a honey bottle does");
        assertTrue(new ItemStack(ModItems.CORRUPTED_ICHOR.get()).getCraftingRemainingItem().is(net.minecraft.world.item.Items.GLASS_BOTTLE), "and leaves its bottle");
    }

    @Test void eachIsMadeOnlyByTheModsOwnSystems() throws Exception {
        Map<String, JsonObject> recipes = recipes();
        Set<String> systems = new HashSet<>();
        for (DeferredItem<CorruptionMaterialItem> material : ModItems.MATERIALS) {
            String id = material.getId().toString();
            List<String> making = new ArrayList<>();
            for (var recipe : recipes.entrySet()) if (made(recipe.getValue()).equals(id)) making.add(recipe.getKey());
            assertEquals(1, making.size(), id + " is made by one recipe: " + making);
            String folder = making.get(0).substring(0, making.get(0).indexOf('/'));
            assertTrue(SYSTEMS.containsKey(folder), id + " is made at the transmuter, the cauldron, the bench or the altar, not by " + making.get(0));
            assertTrue(SYSTEMS.get(folder).contains(recipes.get(making.get(0)).get("type").getAsString()), making.get(0) + " is of its system's type");
            systems.add(folder);
        }
        assertEquals(SYSTEMS.keySet(), systems, "every one of the systems makes some of them");
    }

    @Test void everyMaterialsRecipeLoads(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Map<String, JsonObject> recipes;
            try { recipes = recipes(); } catch (Exception failure) { throw new AssertionError(failure); }
            for (DeferredItem<CorruptionMaterialItem> material : ModItems.MATERIALS) {
                String path = recipes.entrySet().stream().filter(recipe -> made(recipe.getValue()).equals(material.getId().toString()))
                        .map(Map.Entry::getKey).findFirst().orElseThrow();
                RecipeHolder<?> loaded = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", path)).orElse(null);
                assertNotNull(loaded, path + " loads: every ingredient, tag, fluid and creature it names exists");
                assertTrue(loaded.value().getResultItem(level.registryAccess()).is(material.get()), path + " makes " + material.getId());
            }
        });
    }
}
