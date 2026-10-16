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
import java.util.ArrayList;
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
    /** The first of a spell's actions of this kind (its effects come before the rest). */
    private static <T extends SpellAction> T first(List<SpellAction> actions, Class<T> kind) {
        return actions.stream().filter(kind::isInstance).map(kind::cast).findFirst().orElseThrow(() -> new AssertionError("no " + kind.getSimpleName()));
    }
    /** Every action in these and in what their bolts and beams carry, however deep. */
    private static List<SpellAction> everything(List<SpellAction> actions) {
        List<SpellAction> all = new ArrayList<>();
        for (SpellAction action : actions) {
            all.add(action);
            if (action instanceof SpellAction.LaunchBolt bolt) all.addAll(everything(bolt.onHit()));
            if (action instanceof SpellAction.Beam beam) all.addAll(everything(beam.onHit()));
        }
        return all;
    }

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
        assertEquals(List.of(new SpellAction.Damage(12, 5, "corruption:corruption")), bolt.onHit(), "what it does where it strikes");
        SpellAction.ApplyEffect resistance = first(spell("agares").actions(), SpellAction.ApplyEffect.class);
        assertEquals(new SpellAction.ApplyEffect("minecraft:resistance", SpellAction.Target.SELF, 0, 1200, 1), resistance);
        SpellAction.LaunchBolt fireball = first(spell("baal").actions(), SpellAction.LaunchBolt.class);
        assertEquals("minecraft:fire_charge", fireball.look(), "a bolt may be drawn as an item of the game's own");
        assertEquals(new SpellAction.Damage(240, 6), first(fireball.onHit(), SpellAction.Damage.class), "and does as much as the spell says where it strikes");

        // The Prologue's own arts, beyond the first two, turn what they reach into remnants, and still curse.
        for (String art : List.of("corruption_barrage", "corruption_nova", "corruption_vortex", "corruption_tempest", "corruption_rain")) {
            List<SpellAction> all = everything(spell(art).actions());
            assertTrue(all.stream().anyMatch(action -> action instanceof SpellAction.CorruptCreatures turn && turn.chance() == 1), art + " turns every creature it reaches");
            assertTrue(all.stream().anyMatch(action -> action instanceof SpellAction.ApplyEffect curse && curse.effect().equals("corruption:corruption_curse")),
                    art + " lays the corruption's curse on what it cannot turn");
        }
        assertEquals(48, first(spell("corruption_barrage").actions(), SpellAction.LaunchBolt.class).count(), "forty-eight of the corruption's darts at once");
        assertEquals(new SpellAction.CorruptCreatures(12), first(first(spell("corruption_tempest").actions(), SpellAction.LaunchBolt.class).onHit(), SpellAction.CorruptCreatures.class),
                "every creature where the corruption's lightning lands is turned, as far as it strikes");
        SpellAction.LaunchBolt rain = first(first(spell("corruption_rain").actions(), SpellAction.LaunchBolt.class).onHit(), SpellAction.LaunchBolt.class);
        assertEquals(48, rain.count(), "forty-eight bolts rain down");
        assertEquals(new SpellAction.Damage(96, 4, "corruption:corruption"), first(rain.onHit(), SpellAction.Damage.class), "each three times as strong as before");
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

        assertFalse(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"count\": 64}]}"), "a volley of sixty-four bolts");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"count\": 65}]}"), "and no more");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"from\": \"ground\"}]}"), "from the caster or the sky");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"lightning\", \"count\": 129}]}"), "a hundred and twenty-eight strikes at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"push\", \"strength\": 5}]}"), "a push is no stronger than four");
        SpellAction.Beam beam = (SpellAction.Beam) action("{\"type\": \"beam\", \"length\": 64, \"width\": 2, \"ticks\": 60, \"interval\": 5,"
                + " \"color\": \"#FFD24A\", \"accent\": \"#FFFFFF\", \"shake\": 0.3, \"sound\": \"minecraft:block.beacon.activate\","
                + " \"on_hit\": [{\"type\": \"damage\", \"amount\": 24, \"radius\": 3}]}");
        assertEquals(new SpellAction.Beam(64, 2, 60, 5, List.of(new SpellAction.Damage(24, 3)), 0xFFD24A, 0xFFFFFF, .3, "minecraft:block.beacon.activate"), beam,
                "a beam: how far, how wide, how long, how often it pulses, and what it does at each creature it catches");
        assertEquals(beam, SpellCodecs.ACTION.parse(JsonOps.INSTANCE, SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, beam).getOrThrow()).getOrThrow(), "and back");
        assertEquals(new SpellAction.Beam(32, 1, 60, 5, List.of(), SpellAction.Visual.ROSE, SpellAction.Visual.GOLD, 0, ""), action("{\"type\": \"beam\"}"),
                "left out: 32 blocks, 1 round, three seconds, pulsing four times a second, in the corruption's colours");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"beam\", \"length\": 129}]}"), "a beam reaches 128 blocks at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"beam\", \"ticks\": 401}]}"), "and fires twenty seconds at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"beam\", \"interval\": 0}]}"), "and pulses at least once a tick apart");
        SpellAction.Explode crater = (SpellAction.Explode) action("{\"type\": \"explode\", \"power\": 4, \"breaks_blocks\": true, \"break_radius\": 4,"
                + " \"max_blocks\": 60, \"max_resistance\": 6}");
        assertEquals(new SpellAction.Explode(4, true, 4, 60, 6), crater, "how much an explosion breaks is data: how far, how many, how tough");
        assertTrue(crater.limited());
        assertEquals(crater, SpellCodecs.ACTION.parse(JsonOps.INSTANCE, SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, crater).getOrThrow()).getOrThrow(), "and back");
        assertEquals(new SpellAction.Explode(2, true), action("{\"type\": \"explode\", \"breaks_blocks\": true}"), "left out: all its force would break");
        assertFalse(new SpellAction.Explode(2, true).limited());
        assertTrue(unreadable("{\"actions\": [{\"type\": \"explode\", \"max_blocks\": -1}]}"), "no fewer than none");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"explode\", \"break_radius\": 129}]}"), "no further than any reach");
        SpellAction.CorruptCreatures turned = (SpellAction.CorruptCreatures) action("{\"type\": \"corrupt_creatures\", \"radius\": 12, \"chance\": 0.5}");
        assertEquals(new SpellAction.CorruptCreatures(12, .5), turned, "turning creatures into remnants: how far round, and how often");
        assertEquals(turned, SpellCodecs.ACTION.parse(JsonOps.INSTANCE, SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, turned).getOrThrow()).getOrThrow(), "and back");
        assertEquals(new SpellAction.CorruptCreatures(4), action("{\"type\": \"corrupt_creatures\"}"), "left out: every creature within four blocks");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"corrupt_creatures\", \"chance\": 1.5}]}"), "no likelier than certain");
        SpellAction.Push away = new SpellAction.Push(16, 1.4, .4, SpellAction.Push.From.CASTER);
        assertEquals(away, action("{\"type\": \"push\", \"radius\": 16, \"strength\": 1.4, \"lift\": 0.4, \"from\": \"caster\"}"),
                "a push may thrust away from the caster rather than from where it is");
        assertEquals(away, SpellCodecs.ACTION.parse(JsonOps.INSTANCE, SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, away).getOrThrow()).getOrThrow());
        assertEquals(SpellAction.Push.From.ORIGIN, ((SpellAction.Push) action("{\"type\": \"push\"}")).from(), "and from where it is, unless it says");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"push\", \"from\": \"sky\"}]}"), "from the origin or the caster");
        // What reaches round its origin reaches as far as it says, up to the one limit, and no data asks for more.
        for (String kind : List.of("damage", "push", "ignite_area", "freeze_area", "pacify", "lightning", "corrupt_creatures")) {
            assertFalse(unreadable("{\"actions\": [{\"type\": \"" + kind + "\", \"radius\": 128}]}"), kind + " may reach 128 blocks");
            assertTrue(unreadable("{\"actions\": [{\"type\": \"" + kind + "\", \"radius\": 129}]}"), kind + " reaches 128 blocks at most");
        }
        assertTrue(unreadable("{\"actions\": [{\"type\": \"apply_effect\", \"effect\": \"minecraft:speed\", \"target\": \"nearby\", \"radius\": 129}]}"));
        assertFalse(unreadable("{\"actions\": [{\"type\": \"corrupt_area\", \"radius\": 8}]}"), "the corruption spreads eight blocks a way");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"corrupt_area\", \"radius\": 9}]}"), "and no further, since every block it reaches it may change");
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
        assertTrue(unreadable("{\"actions\": [{\"type\": \"visual\", \"effect\": \"corruption:dome\", \"radius\": 129}]}"), "a hundred and twenty-eight blocks round at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"visual\", \"effect\": \"corruption:dome\", \"shake\": 2}]}"), "shaking is 0 to 1");
        assertTrue(com.miophas.corruption.api.arcane.SpellText.lines(new SpellRule(0, 0, 0, 0, List.of(full))).isEmpty(), "it only shows, so it says nothing");
    }

    private static SpellAction action(String text) { return SpellCodecs.ACTION.parse(JsonOps.INSTANCE, JsonParser.parseString(text)).getOrThrow(); }

    @Test void howABoltShowsItselfIsData() throws Exception {
        SpellAction.LaunchBolt fireball = first(spell("baal").actions(), SpellAction.LaunchBolt.class);
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
        assertEquals(-1, plain.colour(), "left out, the corruption's rose");
        assertEquals(SpellAction.LaunchBolt.DEFAULT_SIZE, plain.size(), "and half a block across");
        SpellAction.LaunchBolt great = (SpellAction.LaunchBolt) action("{\"type\": \"launch_bolt\", \"color\": \"#9B4562\", \"size\": 1.5}");
        assertEquals(0x9B4562, great.colour());
        assertEquals(1.5, great.size(), 1e-9);
        assertEquals(great, action(SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, great).getOrThrow().toString()), "and back");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"size\": 5}]}"), "four blocks across at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"launch_bolt\", \"size\": 0.1}]}"), "an eighth at least");
    }

    /** A blast may crush what its force would spare, as hard as obsidian, say; never what cannot be broken at all. */
    @Test void aBlastThatCrushesIsData() throws Exception {
        SpellAction.Explode crushing = (SpellAction.Explode) action("{\"type\": \"explode\", \"power\": 4, \"breaks_blocks\": true, \"break_radius\": 4,"
                + " \"max_blocks\": 60, \"crush_hardness\": 50}");
        assertTrue(crushing.crushes(SpellAction.Explode.OBSIDIAN), "obsidian");
        assertFalse(crushing.crushes(55), "but not reinforced deepslate");
        assertFalse(crushing.crushes(-1), "nor bedrock, nor anything that cannot be broken");
        assertEquals(4, crushing.crushReach(), 1e-9);
        assertEquals(crushing, action(SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, crushing).getOrThrow().toString()), "and back");
        SpellAction.Explode plain = (SpellAction.Explode) action("{\"type\": \"explode\", \"breaks_blocks\": true}");
        assertFalse(plain.crushes(), "left out, it crushes nothing");
        assertFalse(new SpellAction.Explode(4, false, 4, 60, 6, 50).crushes(), "nor does a blast that breaks no blocks");
        assertEquals(8, new SpellAction.Explode(4, true, 20, 60, 6, 50).crushReach(), 1e-9, "eight blocks round at most");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"explode\", \"crush_hardness\": -1}]}"), "no hardness below none");
        SpellAction.Explode baal = (SpellAction.Explode) everything(spell("baal").actions()).stream().filter(SpellAction.Explode.class::isInstance).findFirst().orElseThrow();
        assertTrue(baal.crushes(SpellAction.Explode.OBSIDIAN) && !baal.crushes(-1), "Baal's fireball crushes obsidian, never bedrock");
    }

    /** The corruption commanded: dormant blight, quelling, raised remnants, a prey marked, a truce sworn; each with what it falls back to. */
    @Test void theCorruptionCommandedIsData() {
        assertEquals(new SpellAction.Corrupt(3, .25, false), action("{\"type\": \"corrupt_area\"}"), "left out, the corruption that spreads");
        SpellAction.Corrupt dormant = (SpellAction.Corrupt) action("{\"type\": \"corrupt_area\", \"radius\": 4, \"chance\": 1, \"inert\": true}");
        assertTrue(dormant.inert());
        assertEquals(new SpellAction.Quell(6), action("{\"type\": \"quell_corruption\"}"));
        assertTrue(unreadable("{\"actions\": [{\"type\": \"quell_corruption\", \"radius\": 9}]}"), "eight blocks round at most, as corrupting");
        SpellAction.RaiseRemnants raised = (SpellAction.RaiseRemnants) action("{\"type\": \"raise_remnants\"}");
        assertEquals(new SpellAction.RaiseRemnants(4, 3, SpellAction.RaiseRemnants.LIKENESSES, 1200), raised, "the dead of a raid and a crypt, for a minute");
        assertEquals("minecraft:skeleton", raised.likeness(1));
        assertEquals("minecraft:zombie", raised.likeness(SpellAction.RaiseRemnants.LIKENESSES.size()), "round again");
        assertTrue(unreadable("{\"actions\": [{\"type\": \"raise_remnants\", \"count\": 17}]}"), "sixteen at most");
        assertEquals(new SpellAction.MarkPrey(24, 600), action("{\"type\": \"mark_prey\"}"), "twenty-four blocks round, for half a minute");
        assertEquals(new SpellAction.Truce(16, 1200), action("{\"type\": \"truce\"}"));
        for (SpellAction kind : List.of(dormant, new SpellAction.Quell(5), raised, new SpellAction.MarkPrey(20, 100), new SpellAction.Truce(8, 40)))
            assertEquals(kind, action(SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE, kind).getOrThrow().toString()), kind + " reads back");
        assertEquals(5, SpellText.lines(new SpellRule(0, 0, 0, 0, List.of(dormant, new SpellAction.Quell(5), raised, new SpellAction.MarkPrey(20, 100),
                new SpellAction.Truce(8, 40)))).size(), "each says what it does");
    }
}
