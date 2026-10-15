package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.api.arcane.ArcaneKey;
import com.miophas.corruption.api.arcane.SpellCodecs;
import com.miophas.corruption.api.arcane.client.SpellVisuals;
import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.common.entity.SpellProjectile;
import com.miophas.corruption.common.item.CorruptionArcaneStickItem;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.arcane.SpellRule;
import com.mojang.serialization.JsonOps;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import org.junit.jupiter.api.Test;

/**
 * The books and the wands as data: which strings each book holds, that every one of them is a spell that loads,
 * names what exists and can be cast, that each is named in both languages, and that the wands rise tier by tier.
 */
class BooksAndWandsTest {
    /** The seventy-two spirits of the Ars Goetia, in the received order. */
    private static final List<String> GOETIA = List.of("baal", "agares", "vassago", "gamigin", "marbas", "valefor", "amon", "barbatos", "paimon",
            "buer", "gusion", "sitri", "beleth", "leraje", "eligos", "zepar", "botis", "bathin",
            "sallos", "purson", "marax", "ipos", "aim", "naberius", "glasya_labolas", "bune", "ronove",
            "berith", "astaroth", "forneus", "foras", "asmoday", "gaap", "furfur", "marchosias", "stolas",
            "phenex", "halphas", "malphas", "raum", "focalor", "vepar", "sabnock", "shax", "vine",
            "bifrons", "vual", "haagenti", "crocell", "furcas", "balam", "alloces", "caim", "murmur",
            "orobas", "gremory", "ose", "amy", "orias", "vapula", "zagan", "valac", "andras",
            "flauros", "andrealphus", "kimaris", "amdusias", "belial", "decarabia", "seere", "dantalion", "andromalius");
    private static final String[] VOLUMES = {"i", "ii", "iii", "iv", "v", "vi", "vii", "viii"};

    private static InputStream resource(String path) {
        InputStream in = BooksAndWandsTest.class.getResourceAsStream(path);
        assertNotNull(in, "Missing resource " + path);
        return in;
    }
    private static JsonElement json(String path) throws Exception {
        try (InputStream in = resource(path)) { return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)); }
    }
    private static boolean exists(String path) {
        try (InputStream in = BooksAndWandsTest.class.getResourceAsStream(path)) { return in != null; }
        catch (java.io.IOException failure) { throw new java.io.UncheckedIOException(failure); }
    }
    /** What a book's tag holds, its "corruption:" taken off. */
    private static List<String> book(String id) throws Exception {
        List<String> strings = new ArrayList<>();
        for (JsonElement string : json("/data/corruption/tags/corruption/arcane_string/" + id + ".json").getAsJsonObject().getAsJsonArray("values"))
            strings.add(string.getAsString().substring("corruption:".length()));
        return strings;
    }
    private static Map<String, List<String>> books() throws Exception {
        Map<String, List<String>> books = new LinkedHashMap<>();
        for (DeferredItem<Item> key : ModItems.KEYS) books.put(key.getId().getPath(), book(key.getId().getPath()));
        return books;
    }
    private static SpellRule spell(String name) throws Exception {
        return SpellCodecs.RULE.parse(JsonOps.INSTANCE, json("/data/corruption/corruption/arcane_spell/" + name + ".json")).getOrThrow();
    }
    /** Every action a spell does, those its bolts carry too. */
    private static void walk(List<SpellAction> actions, Consumer<SpellAction> visit) {
        for (SpellAction action : actions) {
            visit.accept(action);
            if (action instanceof SpellAction.LaunchBolt bolt) walk(bolt.onHit(), visit);
        }
    }
    private static Set<String> tag(String path) throws Exception {
        Set<String> values = new HashSet<>();
        for (JsonElement value : json(path).getAsJsonObject().getAsJsonArray("values")) values.add(value.getAsString());
        return values;
    }
    private static long capacity(DeferredItem<CorruptionArcaneStickItem> wand) { return wand.get().tool().capacity(); }

    /** Walks up from wherever the tests run to the project folder. */
    private static Path source(String folder) {
        for (Path at = Path.of("").toAbsolutePath(); at != null; at = at.getParent())
            if (Files.isDirectory(at.resolve(folder))) return at.resolve(folder);
        throw new AssertionError("Cannot find " + folder);
    }

    @Test void eachBookIsAKeyToItsOwnTag() throws Exception {
        JsonObject values = json("/data/corruption/data_maps/item/arcane_key.json").getAsJsonObject().getAsJsonObject("values");
        assertEquals(ModItems.KEYS.size(), values.size(), "every key, and nothing else");
        for (DeferredItem<Item> key : ModItems.KEYS) {
            ArcaneKey parsed = ArcaneKey.CODEC.parse(JsonOps.INSTANCE, values.get(key.getId().toString())).getOrThrow();
            assertEquals(key.getId(), parsed.strings().location(), "a book's tag is named after it");
            assertEquals(1, key.get().getDefaultMaxStackSize(), "there is one of each book");
        }
    }

    @Test void theEightBooksHoldTheSeventyTwoSpiritsNineEachInOrder() throws Exception {
        List<String> all = new ArrayList<>();
        for (int volume = 0; volume < VOLUMES.length; volume++) {
            List<String> held = book("clavicula_salomonis_" + VOLUMES[volume]);
            assertEquals(GOETIA.subList(volume * 9, volume * 9 + 9), held, "book " + VOLUMES[volume]);
            all.addAll(held);
        }
        assertEquals(GOETIA, all);
        assertEquals(72, new HashSet<>(all).size());
    }

    @Test void thePrologueIsTheCorruptionsAndTheOthersTheirOwn() throws Exception {
        List<String> prologue = book("clavicula_salomonis");
        assertEquals(List.of("corruption", "transmutation"), prologue.subList(0, 2), "the corruption's first two arts open it");
        for (String string : prologue) assertTrue(string.equals("transmutation") || string.startsWith("corruption"), string + " is the corruption's own");
        assertTrue(prologue.size() >= 6, "with the flashier arts after them");
        List<String> ascension = book("clavicula_salomonis_ascension");
        for (String planet : new String[]{"saturn", "jupiter", "mars", "sun", "venus", "mercury", "moon"})
            assertTrue(ascension.contains("pentacle_" + planet), "the pentacle of " + planet);
        assertEquals(List.of("ars_goetia", "ars_theurgia", "ars_paulina", "ars_almadel", "ars_notoria"), book("lemegeton"), "the Lesser Key's five books");
    }

    @Test void everyStringIsInExactlyOneBookAndIsASpell() throws Exception {
        Map<String, String> where = new LinkedHashMap<>();
        for (var book : books().entrySet())
            for (String string : book.getValue()) assertNull(where.put(string, book.getKey()), string + " is in " + where.get(string) + " and " + book.getKey());
        Set<String> files = new HashSet<>();
        try (Stream<Path> listed = Files.list(source("src/main/resources/data/corruption/corruption/arcane_string"))) {
            listed.forEach(file -> files.add(file.getFileName().toString().replace(".json", "")));
        }
        assertEquals(files, where.keySet(), "every string the data holds is in a book");
        for (String string : where.keySet()) {
            assertTrue(exists("/data/corruption/corruption/arcane_spell/" + string + ".json"), string + " is a spell");
            assertFalse(spell(string).actions().isEmpty(), string + " does something");
        }
    }

    @Test void everySpellNamesWhatExistsAndCanBeCast() throws Exception {
        Set<String> summons = tag("/data/corruption/tags/entity_type/spell_summons.json"), bolts = tag("/data/corruption/tags/entity_type/spell_bolts.json");
        for (var book : books().values()) for (String name : book) {
            SpellRule rule = spell(name);
            assertTrue(rule.minCorruption() + (rule.corrupts() ? rule.corruptionGain() : 0) < PlayerCorruptionApi.max(),
                    name + " asks so much corruption and adds so much that no one could ever cast it");
            List<String> wrong = new ArrayList<>();
            walk(rule.actions(), action -> {
                switch (action) {
                    case SpellAction.ApplyEffect effect -> { if (!BuiltInRegistries.MOB_EFFECT.containsKey(ResourceLocation.parse(effect.effect()))) wrong.add(effect.effect()); }
                    case SpellAction.Summon summon -> { if (!summons.contains(summon.entity())) wrong.add("summon " + summon.entity()); }
                    case SpellAction.GiveItem give -> { if (!BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(give.item()))) wrong.add(give.item()); }
                    case SpellAction.Damage damage -> { if (!damage.type().isEmpty() && !damageType(damage.type())) wrong.add(damage.type()); }
                    case SpellAction.Lightning lightning -> { if (!lightning.damageType().isEmpty() && !damageType(lightning.damageType())) wrong.add(lightning.damageType()); }
                    case SpellAction.Spectacle spectacle -> spectacle.particles().forEach(particles -> { if (SpellProjectile.options(particles) == null) wrong.add(particles.particle()); });
                    case SpellAction.Visual visual -> {
                        if (!SpellVisuals.BUILT_IN.contains(ResourceLocation.parse(visual.effect()))) wrong.add("effect " + visual.effect());
                        if (!visual.sound().isEmpty() && !BuiltInRegistries.SOUND_EVENT.containsKey(ResourceLocation.parse(visual.sound()))) wrong.add("sound " + visual.sound());
                    }
                    case SpellAction.LaunchBolt bolt -> {
                        if (!bolts.contains(bolt.entity())) wrong.add("bolt " + bolt.entity());
                        if (!bolt.look().isEmpty() && !BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(bolt.look()))) wrong.add(bolt.look());
                        for (var particles : bolt.trail()) if (SpellProjectile.options(particles) == null) wrong.add(particles.particle());
                        for (var particles : bolt.burst()) if (SpellProjectile.options(particles) == null) wrong.add(particles.particle());
                    }
                    default -> {}
                }
            });
            assertEquals(List.of(), wrong, name + " names what is not there");
        }
    }
    private static boolean damageType(String id) {
        ResourceLocation type = ResourceLocation.parse(id);
        return type.getNamespace().equals("minecraft") || exists("/data/" + type.getNamespace() + "/damage_type/" + type.getPath() + ".json");
    }

    @Test void theBooksAskForWandsTierByTier() throws Exception {
        long novice = capacity(ModItems.NOVICE_CORRUPTION_ARCANE_STICK), basic = capacity(ModItems.CORRUPTION_ARCANE_STICK);
        long intermediate = capacity(ModItems.INTERMEDIATE_CORRUPTION_ARCANE_STICK), advanced = capacity(ModItems.ADVANCED_CORRUPTION_ARCANE_STICK);
        for (String spirit : GOETIA) assertTrue(spell(spirit).energy() <= novice, spirit + " is cast with any wand");
        for (String art : book("clavicula_salomonis")) assertTrue(spell(art).energy() <= basic, art + " is cast with a Basic wand");
        for (String pentacle : book("clavicula_salomonis_ascension")) {
            long energy = spell(pentacle).energy();
            assertTrue(energy > basic && energy <= intermediate, pentacle + " needs an Intermediate wand");
        }
        for (String art : book("lemegeton")) {
            long energy = spell(art).energy();
            assertTrue(energy > intermediate && energy <= advanced, art + " needs an Advanced wand");
        }
    }

    @Test void thePrologueShowsOffTheCorruption() throws Exception {
        SpellAction.LaunchBolt barrage = spell("corruption_barrage").actions().stream().filter(SpellAction.LaunchBolt.class::isInstance)
                .map(SpellAction.LaunchBolt.class::cast).findFirst().orElseThrow();
        assertTrue(barrage.count() > 1 && barrage.fan() > 0, "a barrage is a fan of bolts");
        assertTrue(barrage.onHit().stream().anyMatch(action -> action instanceof SpellAction.Damage hurt && hurt.type().equals("corruption:corruption")),
                "that hurt with the corruption's curse");
        SpellAction.LaunchBolt tempest = spell("corruption_tempest").actions().stream().filter(SpellAction.LaunchBolt.class::isInstance)
                .map(SpellAction.LaunchBolt.class::cast).findFirst().orElseThrow();
        assertTrue(tempest.onHit().stream().anyMatch(action -> action instanceof SpellAction.Lightning lightning
                && lightning.corrupt() && lightning.count() > 8 && lightning.damageType().equals("corruption:corruption")),
                "a tempest is a corruption bolt, and many cursed bolts of lightning where it lands");
        SpellAction.LaunchBolt rain = spell("corruption_rain").actions().stream().filter(SpellAction.LaunchBolt.class::isInstance)
                .map(SpellAction.LaunchBolt.class::cast).findFirst().orElseThrow();
        assertTrue(rain.onHit().stream().anyMatch(action -> action instanceof SpellAction.LaunchBolt sky && sky.from() == SpellAction.Source.SKY && sky.count() > 1),
                "where the rain's bolt strikes, bolts fall from the sky");
        assertTrue(spell("corruption_nova").actions().stream().anyMatch(action -> action instanceof SpellAction.Spectacle), "and it is a sight");
        assertTrue(spell("corruption_nova").actions().stream().noneMatch(action -> action instanceof SpellAction.Corrupt), "that leaves its caster's ground as it was");
    }

    /**
     * Lightning, bolts from the sky and explosions fall where a spell's bolt lands, never about the caster: cast
     * straight from the wand, they would strike the caster, or the ground they stand on.
     */
    @Test void nothingStrikesTheCasterThemselves() throws Exception {
        for (var book : books().values()) for (String name : book)
            for (SpellAction action : spell(name).actions()) {
                assertFalse(action instanceof SpellAction.Lightning, name + " strikes lightning about its caster");
                assertFalse(action instanceof SpellAction.Explode, name + " explodes about its caster");
                assertFalse(action instanceof SpellAction.LaunchBolt bolt && bolt.from() == SpellAction.Source.SKY, name + " drops bolts on its caster");
            }
    }

    /**
     * The higher arts cost thirty to eighty KCE, so they must be worth it: each shows itself with at least one of the
     * client's effects, comes round again within twelve seconds, and, if it hurts at all, hurts hard somewhere.
     */
    @Test void theHigherArtsHitHardShowItAndComeRoundSoon() throws Exception {
        List<String> higher = new ArrayList<>(book("clavicula_salomonis_ascension"));
        higher.addAll(book("lemegeton"));
        for (String art : higher) {
            SpellRule rule = spell(art);
            List<SpellAction.Visual> shown = new ArrayList<>();
            double[] hardest = {0};
            walk(rule.actions(), action -> {
                if (action instanceof SpellAction.Visual visual) shown.add(visual);
                if (action instanceof SpellAction.Damage hurt) hardest[0] = Math.max(hardest[0], hurt.amount());
                if (action instanceof SpellAction.Lightning strike) hardest[0] = Math.max(hardest[0], strike.damage());
            });
            assertFalse(shown.isEmpty(), art + " shows itself");
            assertTrue(rule.cooldown() <= 240, art + " comes round within twelve seconds");
            assertTrue(hardest[0] == 0 || hardest[0] >= 24, art + " hurts hard if it hurts at all, not " + hardest[0]);
        }
        for (String art : book("clavicula_salomonis").subList(2, book("clavicula_salomonis").size())) {
            List<SpellAction.Visual> shown = new ArrayList<>();
            walk(spell(art).actions(), action -> { if (action instanceof SpellAction.Visual visual) shown.add(visual); });
            assertFalse(shown.isEmpty(), "the Prologue's " + art + " shows itself");
        }
    }

    @Test void theWandsRiseTierByTier() throws Exception {
        CorruptionArcaneStickItem.Tier[] tiers = CorruptionArcaneStickItem.Tier.values();
        assertEquals(tiers.length, ModItems.WANDS.size());
        for (int at = 0; at < tiers.length; at++) {
            CorruptionArcaneStickItem wand = ModItems.WANDS.get(at).get();
            assertSame(tiers[at], wand.tier(), "lowest first");
            assertEquals(tiers[at].tool(), wand.tool());
            assertEquals(tiers[at].spells(), wand.capacity());
            assertEquals(1, wand.getDefaultMaxStackSize());
            if (at == 0) continue;
            CorruptionArcaneStickItem lower = ModItems.WANDS.get(at - 1).get();
            assertTrue(wand.tool().capacity() > lower.tool().capacity(), wand + " holds more than " + lower);
            assertTrue(wand.tool().step() > lower.tool().step(), wand + " fills faster than " + lower);
            assertTrue(wand.capacity() > lower.capacity(), wand + " carries more spells than " + lower);
        }
        assertEquals(List.of(2, 3, 5, 8), ModItems.WANDS.stream().map(wand -> wand.get().capacity()).toList());
        assertEquals(List.of(10_000L, 20_000L, 50_000L, 100_000L), ModItems.WANDS.stream().map(wand -> wand.get().tool().capacity()).toList());
        ItemStack advanced = new ItemStack(ModItems.ADVANCED_CORRUPTION_ARCANE_STICK.get());
        var unit = ModItems.ADVANCED_CORRUPTION_ARCANE_STICK.get().unit(advanced);
        for (String spirit : GOETIA.subList(0, 8)) unit = unit.inscribing("corruption:" + spirit);
        assertEquals(8, unit.grimoire().size(), "an Advanced wand carries eight spells");
        assertEquals(com.miophas.corruption.core.arcane.Grimoire.Inscribe.FULL, unit.check("corruption:" + GOETIA.get(8)), "and no ninth");
    }

    @Test void theWandsAreMadeAndTheBooksAndWandsLookAndAreNamed() throws Exception {
        assertEquals("corruption:novice_corruption_arcane_stick", json("/data/corruption/recipe/alchemy/novice_corruption_arcane_stick.json").getAsJsonObject()
                .getAsJsonObject("result").get("id").getAsString());
        JsonObject intermediate = json("/data/corruption/recipe/alchemy/intermediate_corruption_arcane_stick.json").getAsJsonObject();
        assertEquals("corruption:corruption_arcane_stick", intermediate.getAsJsonObject("slots").getAsJsonObject("0").get("item").getAsString(), "a Basic wand, bettered");
        assertEquals("corruption:intermediate_corruption_arcane_stick", intermediate.getAsJsonObject("result").get("id").getAsString());
        JsonObject advanced = json("/data/corruption/recipe/altar/advanced_corruption_arcane_stick.json").getAsJsonObject();
        assertEquals("corruption:intermediate_corruption_arcane_stick", advanced.getAsJsonObject("pillars").getAsJsonObject("1").get("item").getAsString(), "an Intermediate wand, bettered");
        assertEquals("corruption:advanced_corruption_arcane_stick", advanced.getAsJsonObject("result").get("id").getAsString());

        List<String> items = new ArrayList<>();
        ModItems.KEYS.forEach(key -> items.add(key.getId().getPath()));
        ModItems.WANDS.forEach(wand -> items.add(wand.getId().getPath()));
        JsonObject en = json("/assets/corruption/lang/en_us.json").getAsJsonObject(), zh = json("/assets/corruption/lang/zh_cn.json").getAsJsonObject();
        for (String item : items) {
            JsonObject model = json("/assets/corruption/models/item/" + item + ".json").getAsJsonObject();
            if (item.endsWith("arcane_stick")) assertEquals("minecraft:item/handheld", model.get("parent").getAsString(), item + " is held as a tool is");
            String texture = model.getAsJsonObject("textures").get("layer0").getAsString();
            assertTrue(exists("/assets/corruption/textures/" + texture.substring("corruption:".length()) + ".png"), item + " is drawn");
            for (JsonObject lang : List.of(en, zh)) assertTrue(lang.has("item.corruption." + item), item + " is named");
        }
        assertEquals("The Key of Solomon - Prologue", en.get("item.corruption.clavicula_salomonis").getAsString());
        assertEquals("Basic Corrupted Arcane Wand", en.get("item.corruption.corruption_arcane_stick").getAsString());
        for (DeferredItem<Item> key : ModItems.KEYS)
            for (JsonObject lang : List.of(en, zh)) assertTrue(lang.has("tooltip.corruption.key_lore." + key.getId().getPath()), key.getId() + " says what it is");
        for (var book : books().values())
            for (String string : book)
                for (JsonObject lang : List.of(en, zh)) assertTrue(lang.has("arcane.corruption." + string), string + " means something in both languages");
        for (JsonObject lang : List.of(en, zh)) {
            assertTrue(lang.has("itemGroup.corruption_parchments"));
            assertTrue(lang.has("corruption.jei.spell_key"));
            assertFalse(lang.has("tooltip.corruption.unreadable"), "a copied parchment is always understood");
        }
        JsonArray prologue = json("/data/corruption/tags/corruption/arcane_string/clavicula_salomonis.json").getAsJsonObject().getAsJsonArray("values");
        assertFalse(prologue.isEmpty());
    }
}
