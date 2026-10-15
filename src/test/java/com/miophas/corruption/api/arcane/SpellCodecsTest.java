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
        assertTrue(unreadable("{\"actions\": [{\"type\": \"transmogrify\"}]}"), "no such action");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"apply_effect\", \"effect\": \"minecraft:speed\", \"target\": \"everyone\"}]}"));
        assertTrue(unreadable("{\"actions\": [{\"type\": \"summon\", \"entity\": \"minecraft:zombie\", \"count\": 17}]}"), "sixteen at most");
        assertTrue(unreadable("{\"min_corruption\": 21, \"actions\": []}"), "past the corruption scale");
    }

    @Test void volleysTheSkyCorruptedLightningPushesAndSightsAreData() {
        SpellAction.LaunchBolt volley = (SpellAction.LaunchBolt) action("{\"type\": \"launch_bolt\", \"count\": 7, \"fan\": 40}");
        assertEquals(7, volley.count());
        assertEquals(40, volley.fan());
        assertEquals(SpellAction.Source.CASTER, volley.from(), "left out, bolts set off from the caster");
        SpellAction.LaunchBolt one = (SpellAction.LaunchBolt) action("{\"type\": \"launch_bolt\"}");
        assertEquals(1, one.count(), "one bolt unless the spell says more");
        assertEquals(0, one.fan());
        assertEquals(SpellAction.LaunchBolt.SKY_HEIGHT, one.height());
        SpellAction.LaunchBolt sky = (SpellAction.LaunchBolt) action("{\"type\": \"launch_bolt\", \"from\": \"sky\", \"height\": 14, \"count\": 12, \"fan\": 6}");
        assertEquals(SpellAction.Source.SKY, sky.from());
        assertEquals(14, sky.height());

        assertEquals(new SpellAction.Lightning(4, 2), action("{\"type\": \"lightning\", \"count\": 2}"), "the game's own lightning, as it was written before");
        assertEquals(new SpellAction.Lightning(9, 12, true, 9, "corruption:corruption"),
                action("{\"type\": \"lightning\", \"radius\": 9, \"count\": 12, \"corrupt\": true, \"damage\": 9, \"damage_type\": \"corruption:corruption\"}"));
        assertEquals(new SpellAction.Push(10, -1.4, .2), action("{\"type\": \"push\", \"radius\": 10, \"strength\": -1.4}"), "a pull is a push the other way");
        assertEquals(new SpellAction.Spectacle(SpellAction.Shape.RING, 6, 2, List.of(new SpellAction.Particles("minecraft:flame", 8, 0, 0))),
                action("{\"type\": \"particles\", \"shape\": \"ring\", \"radius\": 6, \"particles\": [{\"particle\": \"minecraft:flame\", \"count\": 8}]}"));
        assertEquals(SpellAction.Shape.BURST, ((SpellAction.Spectacle) action("{\"type\": \"particles\", \"particles\": []}")).shape(), "left out, a burst");

        for (SpellAction written : List.of(volley, sky, new SpellAction.Lightning(9, 12, true, 9, "corruption:corruption"), new SpellAction.Push(5, 1, .2),
                new SpellAction.Spectacle(SpellAction.Shape.SPIRAL, 2, 4, List.of(new SpellAction.Particles("minecraft:dust", 30, .1, 0, 0x71334C)))))
            assertEquals(written, SpellCodecs.ACTION.parse(JsonOps.INSTANCE, SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, written).getOrThrow()).getOrThrow(), "and back");

        assertTrue(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"count\": 17}]}"), "sixteen bolts at a time at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"from\": \"ground\"}]}"), "from the caster or the sky");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"lightning\", \"count\": 33}]}"), "thirty-two strikes at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"push\", \"strength\": 5}]}"), "a push is no stronger than four");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"particles\", \"shape\": \"cube\", \"particles\": []}]}"), "no such shape");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"particles\"}]}"), "a sight must say what it shows");
    }
    @Test void aSpellsEffectIsData() {
        SpellAction.Visual plain = (SpellAction.Visual) action("{\"type\": \"visual\", \"effect\": \"corruption:magic_circle\"}");
        assertEquals(new SpellAction.Visual("corruption:magic_circle", 3, 3, 40, SpellAction.Visual.ROSE, SpellAction.Visual.GOLD, 0, ""), plain,
                "left out: three blocks round and up, two seconds, the corruption's rose and gold, no shaking, no sound");
        SpellAction.Visual full = (SpellAction.Visual) action("{\"type\": \"visual\", \"effect\": \"corruption:light_pillar\", \"radius\": 2.5, \"height\": 28,"
                + " \"ticks\": 30, \"color\": \"#8AB8FF\", \"accent\": \"#FFFFFF\", \"shake\": 0.6, \"sound\": \"minecraft:item.trident.thunder\"}");
        assertEquals(new SpellAction.Visual("corruption:light_pillar", 2.5, 28, 30, 0x8AB8FF, 0xFFFFFF, .6, "minecraft:item.trident.thunder"), full);
        assertEquals(full, SpellCodecs.ACTION.parse(JsonOps.INSTANCE, SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, full).getOrThrow()).getOrThrow(), "and back");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"visual\"}]}"), "an effect must say which");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"visual\", \"effect\": \"corruption:dome\", \"ticks\": 401}]}"), "twenty seconds at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"visual\", \"effect\": \"corruption:dome\", \"radius\": 33}]}"), "thirty-two blocks round at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"visual\", \"effect\": \"corruption:dome\", \"shake\": 2}]}"), "shaking is 0 to 1");
        assertTrue(com.miophas.corruption.api.arcane.SpellText.lines(new SpellRule(0, 0, 0, 0, List.of(full))).isEmpty(), "it only shows, so it says nothing");
    }

    private static SpellAction action(String text) { return SpellCodecs.ACTION.parse(JsonOps.INSTANCE, JsonParser.parseString(text)).getOrThrow(); }

    @Test void howABoltShowsItselfIsData() throws Exception {
        SpellAction.LaunchBolt fireball = (SpellAction.LaunchBolt) spell("baal").actions().get(0);
        assertTrue(fireball.trail().stream().anyMatch(particles -> particles.particle().equals("minecraft:flame")), "Baal's fireball trails fire");
        assertTrue(fireball.burst().stream().anyMatch(particles -> particles.particle().equals("minecraft:flame") && particles.count() > 10), "and bursts into it");
        SpellAction.LaunchBolt frost = (SpellAction.LaunchBolt) SpellCodecs.ACTION.parse(JsonOps.INSTANCE, JsonParser.parseString(
                "{\"type\": \"launch_bolt\", \"look\": \"minecraft:snowball\", \"trail\": [{\"particle\": \"minecraft:snowflake\", \"count\": 2, \"spread\": 0.1}],"
                        + " \"burst\": [{\"particle\": \"minecraft:dust\", \"count\": 12, \"spread\": 0.4, \"color\": \"#88CCFF\"}]}")).getOrThrow();
        assertEquals(List.of(new SpellAction.Particles("minecraft:snowflake", 2, .1, 0)), frost.trail());
        assertEquals(List.of(new SpellAction.Particles("minecraft:dust", 12, .4, 0, 0x88CCFF)), frost.burst(), "a colour for a particle that takes one");
        assertEquals(frost, SpellCodecs.ACTION.parse(JsonOps.INSTANCE, SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, frost).getOrThrow()).getOrThrow(), "and back");
        SpellAction.LaunchBolt plain = (SpellAction.LaunchBolt) SpellCodecs.ACTION.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"type\": \"launch_bolt\"}")).getOrThrow();
        assertEquals(SpellAction.LaunchBolt.CORRUPTION_TRAIL, plain.trail(), "left out, the corruption's motes");
        assertTrue(plain.burst().isEmpty(), "and no burst");
        SpellAction.LaunchBolt bare = (SpellAction.LaunchBolt) SpellCodecs.ACTION.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"type\": \"launch_bolt\", \"trail\": []}")).getOrThrow();
        assertTrue(bare.trail().isEmpty(), "an empty trail leaves none");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"trail\": [{\"particle\": \"minecraft:dust\", \"color\": \"#12\"}]}]}"), "a colour is #RRGGBB");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"burst\": [{\"particle\": \"minecraft:flame\", \"count\": 65}]}]}"), "sixty-four at a time at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"trail\": [{\"count\": 2}]}]}"), "a particle must be named");
    }
}
