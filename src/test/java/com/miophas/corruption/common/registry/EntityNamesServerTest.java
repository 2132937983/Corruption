package com.miophas.corruption.common.registry;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Every creature the mod registers (every entity type but the MISC ones: bolts, beams, effects) is named in both
 * languages, so that its name shows over it, in death messages and on its boss bar; and the elites' textures are there
 * under their names, and their spawn eggs' names, models and pictures.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class EntityNamesServerTest {
    @Test void everyCreatureIsNamedInBothLanguages(MinecraftServer server) throws Exception {
        JsonObject en = json("/assets/corruption/lang/en_us.json"), zh = json("/assets/corruption/lang/zh_cn.json");
        List<String> creatures = ModEntities.TYPES.getEntries().stream().filter(type -> type.get().getCategory() != MobCategory.MISC)
                .map(type -> type.getId().getPath()).toList();
        assertTrue(creatures.containsAll(List.of("corrupted_aberration", "corrupted_screecher", "corrupted_lurker", "belial")), "the creatures are found: " + creatures);
        for (String id : creatures) for (JsonObject lang : List.of(en, zh)) assertTrue(lang.has("entity.corruption." + id), id + " is named");
        List<String> elites = ModItems.ELITE_EGGS.stream().map(egg -> egg.getId().getPath().replace("_spawn_egg", "")).toList();
        assertEquals(17, elites.size(), "every elite has its egg: " + elites);
        assertTrue(creatures.containsAll(elites), "each egg is an elite's: " + elites);
        for (String elite : elites) {
            for (String picture : List.of(elite, elite + "_glow"))
                try (InputStream in = EntityNamesServerTest.class.getResourceAsStream("/assets/corruption/textures/entity/" + picture + ".png")) {
                    assertNotNull(in, picture + " is there");
                }
            // Its spawn egg: named, with its item model and its picture.
            String egg = elite + "_spawn_egg";
            for (JsonObject lang : List.of(en, zh)) assertTrue(lang.has("item.corruption." + egg), egg + " is named");
            for (String resource : List.of("/assets/corruption/models/item/" + egg + ".json", "/assets/corruption/textures/item/" + egg + ".png"))
                try (InputStream in = EntityNamesServerTest.class.getResourceAsStream(resource)) {
                    assertNotNull(in, resource + " is there");
                }
        }
    }

    private static JsonObject json(String path) throws Exception {
        try (InputStream in = EntityNamesServerTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
