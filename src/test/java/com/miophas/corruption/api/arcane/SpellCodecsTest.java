package com.miophas.corruption.api.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.arcane.SpellRule;
import com.mojang.serialization.JsonOps;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

/** How spells are written in data: the mod's own four read as they should, and what is written reads back the same. */
class SpellCodecsTest {
    private static JsonElement json(String path) throws Exception {
        try (InputStream in = SpellCodecsTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing resource " + path);
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }
    private static SpellRule spell(String name) throws Exception {
        return SpellCodecs.RULE.parse(JsonOps.INSTANCE, json("/data/corruption/corruption/arcane_spell/" + name + ".json")).getOrThrow();
    }
    private static boolean unreadable(String text) { return SpellCodecs.RULE.parse(JsonOps.INSTANCE, JsonParser.parseString(text)).isError(); }

    @Test void theModsSpellsReadAsTheyShouldAndWriteBackTheSame() throws Exception {
        for (String name : new String[]{"agares", "baal", "corruption", "transmutation"}) {
            SpellRule rule = spell(name);
            assertFalse(rule.actions().isEmpty(), name);
            JsonElement written = SpellCodecs.RULE.encodeStart(JsonOps.INSTANCE, rule).getOrThrow();
            assertEquals(rule, SpellCodecs.RULE.parse(JsonOps.INSTANCE, written).getOrThrow(), name + " reads back as it was written");
            json("/data/corruption/corruption/arcane_string/" + name + ".json");
        }
        SpellRule corruption = spell("corruption");
        assertEquals(1_000, corruption.energy());
        assertEquals(5, corruption.minCorruption());
        SpellAction.LaunchBolt bolt = (SpellAction.LaunchBolt) corruption.actions().get(0);
        assertEquals("corruption:spell_bolt", bolt.entity(), "left out, a bolt is the mod's own");
        assertEquals(List.of(new SpellAction.Damage(6, 2.5, "corruption:corruption")), bolt.onHit(), "what it does where it strikes");
        SpellAction.ApplyEffect resistance = (SpellAction.ApplyEffect) spell("agares").actions().get(0);
        assertEquals(new SpellAction.ApplyEffect("minecraft:resistance", SpellAction.Target.SELF, 0, 400, 1), resistance);
        SpellAction.LaunchBolt fireball = (SpellAction.LaunchBolt) spell("baal").actions().get(0);
        assertEquals("minecraft:fire_charge", fireball.look(), "a bolt may be drawn as an item of the game's own");
        assertEquals(new SpellAction.Damage(120, 3), fireball.onHit().get(0), "and does as much as the spell says where it strikes");
    }

    @Test void aBoltMayCarryABoltAndBadDataIsRefused() {
        SpellRule nested = SpellCodecs.RULE.parse(JsonOps.INSTANCE, JsonParser.parseString(
                "{\"actions\": [{\"type\": \"launch_bolt\", \"on_hit\": [{\"type\": \"launch_bolt\", \"on_hit\": [{\"type\": \"freeze_area\"}]}]}]}")).getOrThrow();
        SpellAction.LaunchBolt outer = (SpellAction.LaunchBolt) nested.actions().get(0);
        assertEquals(List.of(new SpellAction.Freeze(3, 200)), ((SpellAction.LaunchBolt) outer.onHit().get(0)).onHit());
        assertEquals(new SpellRule(0, 0, 0, 0, nested.actions()), nested, "costs left out cost nothing");
        assertTrue(unreadable("{\"energy\": \"2KCE\"}"), "a spell must say what it does");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"explode\"}]}"), "no such action");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"apply_effect\", \"effect\": \"minecraft:speed\", \"target\": \"everyone\"}]}"));
        assertTrue(unreadable("{\"actions\": [{\"type\": \"summon\", \"entity\": \"minecraft:zombie\", \"count\": 17}]}"), "sixteen at most");
        assertTrue(unreadable("{\"min_corruption\": 21, \"actions\": []}"), "past the corruption scale");
    }
}
