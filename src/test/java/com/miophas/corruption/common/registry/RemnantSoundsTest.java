package com.miophas.corruption.common.registry;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

/** A sound only plays when its event, its sounds.json entry and its file all agree; nothing else checks that. */
class RemnantSoundsTest {
    private static JsonObject json(String path) throws Exception {
        try (InputStream in = RemnantSoundsTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing resource " + path);
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    @Test void everyRegisteredSoundHasADefinitionFilesAndSubtitlesInBothLanguages() throws Exception {
        JsonObject definitions = json("/assets/corruption/sounds.json");
        JsonObject english = json("/assets/corruption/lang/en_us.json"), chinese = json("/assets/corruption/lang/zh_cn.json");
        Set<String> registered = new HashSet<>();
        for (var holder : List.of(ModSounds.REMNANT_AMBIENT, ModSounds.REMNANT_ATTACK, ModSounds.REMNANT_STEP, ModSounds.REMNANT_HURT, ModSounds.REMNANT_DEATH)) {
            ResourceLocation id = BuiltInRegistries.SOUND_EVENT.getKey(holder.get());
            assertNotNull(id);
            assertEquals("corruption", id.getNamespace());
            assertEquals(id, holder.get().getLocation(), "The event must play the sounds.json entry of the same name");
            registered.add(id.getPath());
            assertTrue(definitions.has(id.getPath()), "sounds.json has no entry for " + id.getPath());
            JsonObject definition = definitions.getAsJsonObject(id.getPath());
            String subtitle = definition.get("subtitle").getAsString();
            // Footsteps reuse vanilla's own subtitle, which every language already has.
            if (!subtitle.startsWith("subtitles.block.")) {
                assertTrue(english.has(subtitle), "en_us is missing " + subtitle);
                assertTrue(chinese.has(subtitle), "zh_cn is missing " + subtitle);
            }
            var sounds = definition.getAsJsonArray("sounds");
            assertFalse(sounds.isEmpty());
            for (var sound : sounds) {
                ResourceLocation file = ResourceLocation.parse(sound.getAsString());
                String path = "/assets/" + file.getNamespace() + "/sounds/" + file.getPath() + ".ogg";
                try (InputStream in = RemnantSoundsTest.class.getResourceAsStream(path)) {
                    assertNotNull(in, "Missing sound file " + path);
                    assertEquals("OggS", new String(in.readNBytes(4), StandardCharsets.US_ASCII), path + " is not an Ogg file");
                }
            }
        }
        assertEquals(registered, definitions.keySet(), "sounds.json defines sounds that no event plays");
        assertEquals(4, definitions.getAsJsonObject("entity.corrupted_remnant.ambient").getAsJsonArray("sounds").size());
    }
}
