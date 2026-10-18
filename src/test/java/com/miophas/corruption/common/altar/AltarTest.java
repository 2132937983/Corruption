package com.miophas.corruption.common.altar;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.common.client.AltarRenderers;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.altar.AltarForm;
import com.miophas.corruption.core.altar.AltarLayout;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

/** Ties the altar to its files: its rites to their readers, and what they ask of the ground and its players; its models to their faces; and its circle to its pillars. */
class AltarTest {
    private static InputStream resource(String path) {
        InputStream in = AltarTest.class.getResourceAsStream(path);
        assertNotNull(in, "Missing resource " + path);
        return in;
    }
    private static JsonObject json(String path) throws Exception {
        try (InputStream in = resource(path)) { return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject(); }
    }
    private static AltarRecipe rite(String name) throws Exception {
        JsonObject file = json("/data/corruption/recipe/altar/" + name + ".json");
        MapCodec<AltarRecipe> codec = file.get("type").getAsString().equals("corruption:altar_shaped") ? AltarRecipe.SHAPED_CODEC : AltarRecipe.SHAPELESS_CODEC;
        return codec.codec().parse(JsonOps.INSTANCE, file).getOrThrow();
    }
    private static AltarInput on(EntityType<?> creature, ItemStack... pillars) { return new AltarInput(Arrays.asList(pillars), creature); }
    private static ItemStack crystal() { return new ItemStack(ModItems.CORRUPTION_CRYSTAL.get()); }
    private static ItemStack brass() { return new ItemStack(ModItems.CORRUPTED_BRASS.get()); }
    private static ItemStack dust() { return new ItemStack(ModItems.CORRUPTION_DUST.get()); }

    @Test void shapedEachPillarByItsNumber() throws Exception {
        AltarRecipe rite = rite("ancient_corruption_crystal");
        assertTrue(rite.shaped());
        assertEquals(6, rite.pillars().size());
        assertTrue(rite.matches(on(null, crystal(), brass(), crystal(), brass(), crystal(), brass()), null));
        assertFalse(rite.matches(on(null, brass(), crystal(), brass(), crystal(), brass(), crystal()), null), "turned one pillar round, it is another rite");
        assertFalse(rite.matches(on(null, crystal(), brass(), crystal(), brass(), crystal(), ItemStack.EMPTY), null));
        assertTrue(rite.matches(on(EntityType.PIG, crystal(), brass(), crystal(), brass(), crystal(), brass()), null), "a creature bound does not spoil a rite that asks none");
        assertTrue(rite.result().item().orElseThrow().is(ModItems.ANCIENT_CORRUPTION_CRYSTAL.get()));
        assertEquals(60, rite.time(), "left out, a rite gathers for three seconds");
        // Pillars left out of a shaped rite must hold nothing; a rite asks for at least one, and an altar has six.
        AltarRecipe two = AltarRecipe.SHAPED_CODEC.codec().parse(JsonOps.INSTANCE, JsonParser.parseString(
                "{\"pillars\": {\"1\": {\"item\": \"minecraft:stick\"}, \"4\": {\"item\": \"minecraft:stone\"}}, \"result\": {\"id\": \"minecraft:torch\"}}")).getOrThrow();
        assertTrue(two.pillars().get(1).isEmpty() && two.pillars().get(3).test(new ItemStack(Items.STONE)));
        for (String bad : new String[]{"{\"7\": {\"item\": \"minecraft:stick\"}}", "{\"one\": {\"item\": \"minecraft:stick\"}}", "{}"})
            assertTrue(AltarRecipe.SHAPED_CODEC.codec().parse(JsonOps.INSTANCE, JsonParser.parseString("{\"pillars\": " + bad + ", \"result\": {\"id\": \"minecraft:torch\"}}")).isError(), bad);
    }

    @Test void shapelessOnAnyPillars() throws Exception {
        AltarRecipe rite = rite("corruption_remain");
        assertFalse(rite.shaped());
        ItemStack flesh = new ItemStack(Items.ROTTEN_FLESH), bone = new ItemStack(Items.BONE), eye = new ItemStack(Items.SPIDER_EYE);
        assertTrue(rite.matches(on(null, ItemStack.EMPTY, eye, dust(), ItemStack.EMPTY, flesh, bone), null));
        assertTrue(rite.matches(on(null, bone, flesh, ItemStack.EMPTY, dust(), ItemStack.EMPTY, eye), null));
        assertFalse(rite.matches(on(null, bone, flesh, crystal(), dust(), ItemStack.EMPTY, eye), null), "the crystal is left over");
        assertEquals(4, rite.result().item().orElseThrow().getCount());
        assertTrue(AltarRecipe.SHAPELESS_CODEC.codec().parse(JsonOps.INSTANCE, JsonParser.parseString(
                "{\"pillars\": [" + "{\"item\": \"minecraft:stick\"},".repeat(6) + "{\"item\": \"minecraft:stick\"}], \"result\": {\"id\": \"minecraft:torch\"}}")).isError(), "six pillars, no more");
    }

    @Test void aPigBoundAmidCorruptionPowderBecomesAZombifiedPiglin() throws Exception {
        AltarRecipe rite = rite("zombified_piglin");
        assertEquals(EntityType.PIG, rite.offering().orElseThrow());
        assertEquals(EntityType.ZOMBIFIED_PIGLIN, rite.result().creature().orElseThrow());
        assertTrue(rite.result().item().isEmpty());
        assertEquals(80, rite.time());
        assertTrue(rite.matches(on(EntityType.PIG, dust(), ItemStack.EMPTY, dust(), ItemStack.EMPTY, dust(), ItemStack.EMPTY), null));
        assertFalse(rite.matches(on(null, dust(), ItemStack.EMPTY, dust(), ItemStack.EMPTY, dust(), ItemStack.EMPTY), null), "no pig bound");
        assertFalse(rite.matches(on(EntityType.COW, dust(), ItemStack.EMPTY, dust(), ItemStack.EMPTY, dust(), ItemStack.EMPTY), null), "nor a cow");
        assertTrue(rite.assemble(on(EntityType.PIG), null).isEmpty(), "it brings forth no item");
    }

    private static AltarRecipe read(MapCodec<AltarRecipe> codec, String json) { return codec.codec().parse(JsonOps.INSTANCE, JsonParser.parseString(json)).getOrThrow(); }
    private static boolean unreadable(String blocksAndCorruption) {
        return AltarRecipe.SHAPELESS_CODEC.codec().parse(JsonOps.INSTANCE, JsonParser.parseString(
                "{\"pillars\": [{\"item\": \"minecraft:stick\"}], " + blocksAndCorruption + ", \"result\": {\"id\": \"minecraft:torch\"}}")).isError();
    }
    /** A ground bare but for these blocks, each at {east, north}. */
    private static AltarGround ground(Object... blocksAt) {
        return (east, north) -> {
            for (int at = 0; at < blocksAt.length; at += 2)
                if (Arrays.equals((int[]) blocksAt[at + 1], new int[]{east, north})) return ((Block) blocksAt[at]).defaultBlockState();
            return Blocks.AIR.defaultBlockState();
        };
    }

    @Test void theHarmonizedRiteAsksForEightCandlesRoundTheOffering() throws Exception {
        AltarRecipe rite = rite("harmonized_corruption_crystal");
        assertTrue(rite.shaped());
        Set<List<Integer>> places = new HashSet<>();
        for (GroundBlock block : rite.blocks()) {
            places.add(List.of(block.east(), block.north()));
            assertEquals(BlockTags.CANDLES, block.block().left().orElseThrow(), "candles of any colour");
        }
        assertEquals(Set.of(List.of(0, 1), List.of(1, 1), List.of(1, 0), List.of(1, -1), List.of(0, -1), List.of(-1, -1), List.of(-1, 0), List.of(-1, 1)), places);
        assertNotNull(rite.blockAt(-1, 1));
        assertNull(rite.blockAt(2, 2));
        assertEquals(0, rite.corruption(), "no player needed");
        assertTrue(rite.result().item().orElseThrow().is(ModItems.HARMONIZED_CORRUPTION_CRYSTAL.get()));
        ItemStack candle = new ItemStack(ModItems.CORRUPTION_CANDLE.get());
        assertFalse(rite.matches(on(null, candle, ItemStack.EMPTY, crystal(), ItemStack.EMPTY, crystal(), ItemStack.EMPTY), null), "on bare ground, no candles");
    }

    @Test void maliceIsWroughtOnlyWithACorruptedPlayerThere() throws Exception {
        AltarRecipe rite = rite("malice");
        assertEquals(10, rite.corruption());
        List<ItemStack> pillars = List.of(new ItemStack(ModItems.CORRUPTION_REMAIN.get()), ItemStack.EMPTY, dust(), ItemStack.EMPTY, dust(), ItemStack.EMPTY);
        assertFalse(rite.matches(new AltarInput(pillars, null), null), "nobody there");
        assertFalse(rite.matches(new AltarInput(pillars, null, AltarGround.CLEAR, new int[]{9, 2}), null), "nobody corrupted enough");
        assertTrue(rite.matches(new AltarInput(pillars, null, AltarGround.CLEAR, new int[]{2, 10}), null), "one is enough");
        assertTrue(rite.result().item().orElseThrow().is(ModItems.MALICE.get()));
    }

    @Test void aVillagerAmidSoulSandBecomesAVex() throws Exception {
        AltarRecipe rite = rite("vex");
        assertEquals(EntityType.VILLAGER, rite.offering().orElseThrow());
        assertEquals(EntityType.VEX, rite.result().creature().orElseThrow());
        assertEquals(4, rite.blocks().size());
        for (GroundBlock block : rite.blocks()) {
            assertEquals(2, Math.abs(block.east()), "at the corners");
            assertEquals(2, Math.abs(block.north()));
            assertEquals(BlockTags.SOUL_FIRE_BASE_BLOCKS, block.block().left().orElseThrow(), "soul sand or soul soil");
        }
    }

    @Test void riteAskingNothingOfTheGroundWantsItBare() throws Exception {
        for (String name : new String[]{"ancient_corruption_crystal", "corruption_remain", "zombified_piglin"}) {
            assertEquals(List.of(), rite(name).blocks(), name);
            assertEquals(0, rite(name).corruption(), name);
        }
        AltarRecipe rite = rite("corruption_remain");
        List<ItemStack> pillars = List.of(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.BONE), new ItemStack(Items.SPIDER_EYE), dust(), ItemStack.EMPTY, ItemStack.EMPTY);
        assertTrue(rite.matches(new AltarInput(pillars, null, AltarGround.CLEAR, new int[]{20}), null), "a player there does not spoil it");
        assertFalse(rite.matches(new AltarInput(pillars, null, ground(Blocks.STONE, new int[]{2, 0}), new int[0]), null), "a stray stone does");
        assertFalse(rite.matches(new AltarInput(pillars, null, ground(Blocks.TORCH, new int[]{-4, -4}), new int[0]), null), "even at the far corner");
        assertTrue(rite.matches(new AltarInput(pillars, null, ground(Blocks.STONE, new int[]{5, 0}, Blocks.STONE, new int[]{0, 0}), new int[0]), null), "beyond the ground, or the altar itself, is not looked at");
    }

    @Test void blocksAndCorruptionAreReadWithCare() {
        AltarRecipe rite = read(AltarRecipe.SHAPELESS_CODEC, "{\"pillars\": [{\"item\": \"minecraft:stick\"}], \"blocks\": [{\"at\": [1, -1], \"block\": \"minecraft:stone\"}],"
                + " \"corruption\": 20, \"result\": {\"id\": \"minecraft:torch\"}}");
        assertEquals(Blocks.STONE, rite.blocks().get(0).block().right().orElseThrow(), "one block, by its id");
        List<ItemStack> stick = List.of(new ItemStack(Items.STICK), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        assertTrue(rite.matches(new AltarInput(stick, null, ground(Blocks.STONE, new int[]{1, -1}), new int[]{20}), null));
        assertFalse(rite.matches(new AltarInput(stick, null, ground(Blocks.COBBLESTONE, new int[]{1, -1}), new int[]{20}), null), "another block");
        assertFalse(rite.matches(new AltarInput(stick, null, ground(Blocks.STONE, new int[]{-1, 1}), new int[]{20}), null), "elsewhere");
        assertFalse(rite.matches(new AltarInput(stick, null, ground(Blocks.STONE, new int[]{1, -1}), new int[]{19}), null), "too little corruption");
        assertTrue(unreadable("\"blocks\": [{\"at\": [0, 0], \"block\": \"minecraft:stone\"}]"), "the offering's place");
        assertTrue(unreadable("\"blocks\": [{\"at\": [4, 2], \"block\": \"minecraft:stone\"}]"), "a pillar's place");
        assertTrue(unreadable("\"blocks\": [{\"at\": [5, 0], \"block\": \"minecraft:stone\"}]"), "off the ground");
        assertTrue(unreadable("\"blocks\": [{\"at\": [1], \"block\": \"minecraft:stone\"}]"), "half a place");
        assertTrue(unreadable("\"blocks\": [{\"at\": [1, 1], \"block\": \"minecraft:stone\"}, {\"at\": [1, 1], \"block\": \"minecraft:dirt\"}]"), "two blocks in one place");
        assertTrue(unreadable("\"blocks\": [{\"at\": [1, 1], \"block\": \"minecraft:no_such_block\"}]"));
        assertTrue(unreadable("\"corruption\": 21"), "past the most a player can be corrupted");
        assertTrue(unreadable("\"corruption\": -1"));
        assertFalse(unreadable("\"blocks\": [{\"at\": [-4, 4], \"block\": \"#minecraft:candles\"}], \"corruption\": 0"));
    }

    /** Faces of a block model that lie in the same plane over the same area, which the game draws flickering through each other. */
    private static List<String> fighting(JsonObject model) {
        record Face(String name, int axis, double at, double[] rect) {}
        List<Face> faces = new ArrayList<>();
        for (var element : model.getAsJsonArray("elements")) {
            JsonObject part = element.getAsJsonObject();
            double[] from = new double[3], to = new double[3];
            for (int axis = 0; axis < 3; axis++) { from[axis] = part.getAsJsonArray("from").get(axis).getAsDouble(); to[axis] = part.getAsJsonArray("to").get(axis).getAsDouble(); }
            for (String side : part.getAsJsonObject("faces").keySet()) {
                int axis = switch (side) { case "west", "east" -> 0; case "down", "up" -> 1; default -> 2; };
                double at = side.equals("west") || side.equals("down") || side.equals("north") ? from[axis] : to[axis];
                int u = axis == 0 ? 1 : 0, v = axis == 2 ? 1 : 2;
                faces.add(new Face(part.get("name").getAsString() + " " + side, axis, at, new double[]{from[u], to[u], from[v], to[v]}));
            }
        }
        List<String> fights = new ArrayList<>();
        for (int one = 0; one < faces.size(); one++)
            for (int other = one + 1; other < faces.size(); other++) {
                Face a = faces.get(one), b = faces.get(other);
                if (a.axis() == b.axis() && a.at() == b.at() && Math.min(a.rect()[1], b.rect()[1]) > Math.max(a.rect()[0], b.rect()[0]) && Math.min(a.rect()[3], b.rect()[3]) > Math.max(a.rect()[2], b.rect()[2]))
                    fights.add(a.name() + " / " + b.name());
            }
        return fights;
    }

    @Test void itsModelsAreShapedWithoutFlickerAndTheBindingOneShowsIt() throws Exception {
        for (String model : new String[]{"altar_pillar", "altar_offering", "altar_offering_binding"}) {
            JsonObject json = json("/assets/corruption/models/block/" + model + ".json");
            assertEquals(List.of(), fighting(json), model);
            for (var texture : json.getAsJsonObject("textures").entrySet())
                resource("/assets/corruption/textures/" + texture.getValue().getAsString().substring("corruption:".length()) + ".png").close();
        }
        assertEquals("corruption:block/block_corruption_arcane_brick", json("/assets/corruption/models/block/altar_pillar.json").getAsJsonObject("textures").get("bricks").getAsString());
        JsonObject variants = json("/assets/corruption/blockstates/altar_offering.json").getAsJsonObject("variants");
        assertEquals(3 * 4, variants.size());
        String[] facings = {"north", "east", "south", "west"};
        for (int turns = 0; turns < 4; turns++) {
            JsonObject binding = variants.getAsJsonObject("altar=binding,facing=" + facings[turns]), formed = variants.getAsJsonObject("altar=formed,facing=" + facings[turns]);
            assertEquals("corruption:block/altar_offering_binding", binding.get("model").getAsString());
            assertEquals("corruption:block/altar_offering", formed.get("model").getAsString());
            for (JsonObject variant : new JsonObject[]{binding, formed, variants.getAsJsonObject("altar=dormant,facing=" + facings[turns])})
                assertEquals(90 * turns, variant.has("y") ? variant.get("y").getAsInt() : 0, "turned to face " + facings[turns]);
        }
        assertTrue(json("/assets/corruption/models/block/altar_offering_binding.json").getAsJsonArray("elements").size()
                > json("/assets/corruption/models/block/altar_offering.json").getAsJsonArray("elements").size(), "binding, it shows more");
        for (String language : new String[]{"en_us", "zh_cn"}) {
            JsonObject lang = json("/assets/corruption/lang/" + language + ".json");
            for (String key : new String[]{"block.corruption.altar_pillar", "block.corruption.altar_offering", "entity.corruption.corrupt_lightning", "corruption.jei.altar",
                    "corruption.jei.altar_shaped", "corruption.jei.altar_shapeless", "corruption.jei.altar_offering", "corruption.jei.altar_summons", "corruption.jei.altar_slot",
                    "corruption.jei.altar_numbering", "corruption.jei.altar_ground_clear", "corruption.jei.altar_ground_rest", "corruption.jei.altar_ground_range",
                    "corruption.jei.altar_ground_count", "corruption.jei.altar_clear", "corruption.jei.altar_corruption", "corruption.jei.altar_corruption_tip",
                    "corruption.altar.unformed", "corruption.altar.towards.north", "corruption.altar.towards.east", "corruption.altar.towards.south",
                    "corruption.altar.towards.west", "corruption.altar.binding", "corruption.altar.released", "corruption.jei.altar_shaped_octagram",
                    "corruption.jei.altar_shapeless_octagram", "corruption.jei.altar_octagram_tip"})
                assertTrue(lang.has(key), language + " is missing " + key);
        }
    }

    @Test void theCirclesSixPointsLieUnderTheSixPillars() throws Exception {
        BufferedImage circle;
        try (InputStream in = resource("/assets/corruption/textures/misc/altar_circle.png")) { circle = ImageIO.read(in); }
        for (int slot = 0; slot < AltarLayout.SLOTS; slot++) {
            float[] point = AltarRenderers.circlePoint(slot);
            int x = Math.round(point[0] * circle.getWidth()), y = Math.round(point[1] * circle.getHeight());
            int brightest = 0;
            for (int dy = -2; dy <= 2; dy++) for (int dx = -2; dx <= 2; dx++) brightest = Math.max(brightest, circle.getRGB(x + dx, y + dy) >>> 24);
            assertTrue(brightest > 150, "the hexagram reaches pillar " + (slot + 1));
        }
        assertEquals(0, circle.getRGB(2, 2) >>> 24, "nothing outside the circle");
    }

    @Test void theOctagramCirclesEightPointsLieUnderItsEightPillars() throws Exception {
        assertEquals("textures/misc/altar_circle_octagram.png", AltarRenderers.circle(AltarForm.OCTAGRAM).getPath());
        assertEquals(AltarRenderers.CIRCLE, AltarRenderers.circle(AltarForm.HEXAGRAM));
        BufferedImage circle;
        try (InputStream in = resource("/assets/corruption/textures/misc/altar_circle_octagram.png")) { circle = ImageIO.read(in); }
        for (int slot = 0; slot < AltarForm.OCTAGRAM.slots(); slot++) {
            float[] point = AltarRenderers.circlePoint(AltarForm.OCTAGRAM, slot);
            int x = Math.round(point[0] * circle.getWidth()), y = Math.round(point[1] * circle.getHeight());
            int brightest = 0;
            for (int dy = -2; dy <= 2; dy++) for (int dx = -2; dx <= 2; dx++) brightest = Math.max(brightest, circle.getRGB(x + dx, y + dy) >>> 24);
            assertTrue(brightest > 150, "the octagram reaches pillar " + (slot + 1));
        }
        assertEquals(0, circle.getRGB(2, 2) >>> 24, "nothing outside the circle");
        // Its star's lines cross the way to each pillar halfway out; the hexagram's do not between its own.
        float[] halfway = {.5F + 1.5F / AltarRenderers.CIRCLE_BLOCKS, .5F - 1.5F / AltarRenderers.CIRCLE_BLOCKS};
        assertTrue(circle.getRGB(Math.round(halfway[0] * circle.getWidth()), Math.round(halfway[1] * circle.getHeight())) >>> 24 > 0, "something drawn on the way to 3, 3");
    }

    @Test void anOctagramRiteIsPerformedOnlyAtAnAltarOfEightPillars() {
        AltarRecipe octagram = read(AltarRecipe.SHAPED_CODEC, "{\"form\": \"octagram\", \"pillars\": {\"1\": {\"item\": \"minecraft:stick\"}, \"8\": {\"item\": \"minecraft:stone\"}},"
                + " \"keep\": \"8\", \"result\": {\"id\": \"minecraft:torch\"}}");
        assertEquals(AltarForm.OCTAGRAM, octagram.form());
        assertEquals(8, octagram.pillars().size());
        List<ItemStack> eight = new ArrayList<>(Collections.nCopies(8, ItemStack.EMPTY));
        eight.set(0, new ItemStack(Items.STICK));
        eight.set(7, new ItemStack(Items.STONE));
        assertTrue(octagram.matches(new AltarInput(eight, null), null), "a stick on 1 and stone on 8");
        List<ItemStack> six = new ArrayList<>(Collections.nCopies(6, ItemStack.EMPTY));
        six.set(0, new ItemStack(Items.STICK));
        assertFalse(octagram.matches(new AltarInput(six, null), null), "not at a hexagram");

        AltarRecipe hexagram = read(AltarRecipe.SHAPED_CODEC, "{\"pillars\": {\"1\": {\"item\": \"minecraft:stick\"}}, \"result\": {\"id\": \"minecraft:torch\"}}");
        assertEquals(AltarForm.HEXAGRAM, hexagram.form(), "left unsaid, a hexagram's");
        assertTrue(hexagram.matches(new AltarInput(six, null), null));
        List<ItemStack> stickOfEight = new ArrayList<>(Collections.nCopies(8, ItemStack.EMPTY));
        stickOfEight.set(0, new ItemStack(Items.STICK));
        assertFalse(hexagram.matches(new AltarInput(stickOfEight, null), null), "not at an octagram, though the same lies on its pillar 1");

        String seven = "[" + String.join(", ", Collections.nCopies(7, "{\"item\": \"minecraft:stick\"}")) + "]";
        String nine = "[" + String.join(", ", Collections.nCopies(9, "{\"item\": \"minecraft:stick\"}")) + "]";
        assertEquals(7, read(AltarRecipe.SHAPELESS_CODEC, "{\"form\": \"octagram\", \"pillars\": " + seven + ", \"result\": {\"id\": \"minecraft:torch\"}}").pillars().size());
        assertTrue(unparsed(AltarRecipe.SHAPELESS_CODEC, "{\"pillars\": " + seven + ", \"result\": {\"id\": \"minecraft:torch\"}}"), "seven on a hexagram's six");
        assertTrue(unparsed(AltarRecipe.SHAPELESS_CODEC, "{\"form\": \"octagram\", \"pillars\": " + nine + ", \"result\": {\"id\": \"minecraft:torch\"}}"), "nine on eight");
        assertTrue(unparsed(AltarRecipe.SHAPED_CODEC, "{\"pillars\": {\"8\": {\"item\": \"minecraft:stick\"}}, \"result\": {\"id\": \"minecraft:torch\"}}"), "a hexagram has no pillar 8");
        assertTrue(unparsed(AltarRecipe.SHAPED_CODEC, "{\"form\": \"pentagram\", \"pillars\": {\"1\": {\"item\": \"minecraft:stick\"}}, \"result\": {\"id\": \"minecraft:torch\"}}"));
        String block = "{\"form\": \"%s\", \"pillars\": {\"1\": {\"item\": \"minecraft:stick\"}}, \"blocks\": [{\"at\": [%s], \"block\": \"minecraft:stone\"}], \"result\": {\"id\": \"minecraft:torch\"}}";
        assertTrue(unparsed(AltarRecipe.SHAPED_CODEC, block.formatted("octagram", "3, 3")), "an octagram's pillar stands at 3, 3");
        assertFalse(unparsed(AltarRecipe.SHAPED_CODEC, block.formatted("octagram", "4, 2")), "where a hexagram's would, it is ground");
        assertFalse(unparsed(AltarRecipe.SHAPED_CODEC, block.formatted("hexagram", "3, 3")));
        assertTrue(unparsed(AltarRecipe.SHAPED_CODEC, block.formatted("hexagram", "4, 2")));
    }

    private static boolean unparsed(MapCodec<AltarRecipe> codec, String json) { return codec.codec().parse(JsonOps.INSTANCE, JsonParser.parseString(json)).isError(); }
}
