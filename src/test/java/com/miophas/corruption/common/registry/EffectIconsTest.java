package com.miophas.corruption.common.registry;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

/**
 * Every effect the mod registers can be shown: its icon is there for the inventory and the screen's corner
 * (textures/mob_effect/&lt;id&gt;.png, drawn), and it is named in both languages.
 */
class EffectIconsTest {
    @Test void everyEffectHasItsIconAndItsName() throws Exception {
        JsonObject en = json("/assets/corruption/lang/en_us.json"), zh = json("/assets/corruption/lang/zh_cn.json");
        List<String> ids = ModEffects.EFFECTS.getEntries().stream().map(effect -> effect.getId().getPath()).toList();
        assertTrue(ids.contains("stunned") && ids.contains("corruption_curse"), "the effects are found: " + ids);
        for (String id : ids) {
            try (InputStream in = EffectIconsTest.class.getResourceAsStream("/assets/corruption/textures/mob_effect/" + id + ".png")) {
                assertNotNull(in, id + " has its icon");
                BufferedImage icon = ImageIO.read(in);
                int drawn = 0;
                for (int y = 0; y < icon.getHeight(); y++) for (int x = 0; x < icon.getWidth(); x++) if ((icon.getRGB(x, y) >>> 24) != 0) drawn++;
                assertTrue(drawn > icon.getWidth() * icon.getHeight() / 4, id + "'s icon is drawn: " + drawn);
            }
            for (JsonObject lang : List.of(en, zh)) assertTrue(lang.has("effect.corruption." + id), id + " is named");
        }
    }

    private static JsonObject json(String path) throws Exception {
        try (InputStream in = EffectIconsTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
