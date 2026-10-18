package com.miophas.corruption.common.loot;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.api.arcane.ArcaneKey;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModAttachments;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Where the Key of Solomon is found: the mod's copies of seven vanilla chest loot tables
 * (gamemodels/key_of_solomon_loot/write_loot_tables.py), with the loot conditions they rest on,
 * corruption:once_per_structure and corruption:structure_piece. The tables are the mod's own data, loaded as the game
 * loads them. A real bastion remnant is built and every chest in it opened; a real end ship and tower are placed.
 * Strongholds and fortresses stand in as starts with rooms of their own; all are filled through the same loot.
 *
 * <p>Where the book goes: a stronghold's library, the first fortress chest opened, the first bastion chest opened that
 * rolled a smithing template, the first of an end ship's two chests opened; once in each structure.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class KeyOfSolomonLootServerTest {
    private static final String BOOK = "corruption:chests/key_of_solomon";
    /** The tables the mod replaces: the four that gain a pool, and the bastion's, whose smithing templates bring the book. */
    private static final List<String> POOLED = List.of("stronghold_library", "nether_bridge", "end_city_treasure");
    private static final List<String> BASTION = List.of("bastion_treasure", "bastion_other", "bastion_bridge", "bastion_hoglin_stable");
    /** Every chest loot table vanilla fills inside these structures. */
    private static final List<String> TABLES = List.of("stronghold_corridor", "stronghold_crossing", "stronghold_library", "nether_bridge", "end_city_treasure",
            "bastion_bridge", "bastion_hoglin_stable", "bastion_other", "bastion_treasure");

    // ---- the tables as written ----

    @Test void eachTableIsVanillasWithTheBookAddedAndNothingElse(MinecraftServer server) throws Exception {
        for (String name : POOLED) {
            JsonObject ours = table(server, name, false), vanilla = table(server, name, true);
            JsonArray pools = ours.getAsJsonArray("pools");
            JsonObject book = pools.remove(pools.size() - 1).getAsJsonObject();
            assertEquals(BOOK, book.getAsJsonArray("entries").get(0).getAsJsonObject().get("value").getAsString(), name + ": its last pool is the book");
            JsonArray conditions = book.getAsJsonArray("conditions");
            assertEquals("corruption:once_per_structure", conditions.get(conditions.size() - 1).getAsJsonObject().get("condition").getAsString(),
                    name + ": once per structure, asked last");
            assertEquals(vanilla, ours, name + ": the rest is vanilla's table");
        }
        for (String name : BASTION) {
            JsonObject ours = table(server, name, false), vanilla = table(server, name, true);
            int templates = 0;
            for (JsonElement pool : ours.getAsJsonArray("pools")) {
                JsonArray entries = pool.getAsJsonObject().getAsJsonArray("entries");
                for (int index = 0; index < entries.size(); index++) {
                    JsonObject entry = entries.get(index).getAsJsonObject();
                    if (!entry.get("type").getAsString().equals("minecraft:loot_table") || !entry.get("value").isJsonObject()) continue;
                    // A template's own table: the template, then the book, once per bastion. Put back as vanilla had it.
                    JsonArray inner = entry.getAsJsonObject("value").getAsJsonArray("pools");
                    assertEquals(2, inner.size(), name);
                    JsonObject template = inner.get(0).getAsJsonObject().getAsJsonArray("entries").get(0).getAsJsonObject().deepCopy();
                    assertTrue(template.get("name").getAsString().endsWith("_smithing_template"), name + ": " + template);
                    JsonObject book = inner.get(1).getAsJsonObject();
                    assertEquals(BOOK, book.getAsJsonArray("entries").get(0).getAsJsonObject().get("value").getAsString(), name);
                    assertEquals("minecraft:bastion_remnant", book.getAsJsonArray("conditions").get(0).getAsJsonObject().get("structures").getAsString(), name);
                    for (String key : List.of("weight", "quality", "conditions")) if (entry.has(key)) template.add(key, entry.get(key));
                    entries.set(index, template);
                    templates++;
                }
            }
            assertEquals(2, templates, name + ": the netherite upgrade and the snout trim each bring it");
            assertEquals(vanilla, ours, name + ": the rest is vanilla's table");
        }
    }

    @Test void theBookTableGivesOneBookOfTheWholeCollection(MinecraftServer server) {
        Set<Item> keys = keys();
        assertEquals(12, keys.size(), "the twelve books of the Key of Solomon");
        LootTable table = server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(BOOK)));
        TestWorld.onServer(server, level -> {
            Set<Item> seen = new HashSet<>();
            for (int roll = 0; roll < 1200; roll++) {
                List<ItemStack> books = table.getRandomItems(params(level, BlockPos.ZERO));
                assertEquals(1, books.size(), "exactly one book");
                assertEquals(1, books.get(0).getCount());
                assertTrue(keys.contains(books.get(0).getItem()), books.get(0) + " is a book of the Key");
                seen.add(books.get(0).getItem());
            }
            assertEquals(keys, seen, "any of them can come");
        });
    }

    // ---- in the world ----

    @Test void eachStructureHoldsOneBookWhereItBelongs(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            aRealBastion(level);
            strongholdsGiveItInTheirLibrary(level);
            fortressesGiveItInTheFirstChestOpened(level);
            bastionsGiveItOnlyBesideASmithingTemplate(level);
            endCitiesGiveItOnTheirShip(level);
            noneOutsideThem(level);
        });
    }

    /**
     * A bastion remnant as the world builds one, every chest of it opened in turn: one book in all, in the first chest
     * opened that rolled a smithing template, or none if no chest of it rolled one.
     */
    private static void aRealBastion(ServerLevel level) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        Holder.Reference<Structure> bastion = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(BuiltinStructures.BASTION_REMNANT);
        StructureStart start = bastion.value().generate(level.registryAccess(), generator, generator.getBiomeSource(), level.getChunkSource().randomState(),
                level.getStructureManager(), level.getSeed(), new ChunkPos(300, 300), 0, level, biome -> true);
        assertTrue(start.isValid(), "a bastion was laid out");
        record(level, bastion.value(), start);
        place(level, start);
        List<RandomizableContainerBlockEntity> chests = chests(level, start.getBoundingBox());
        assertTrue(chests.size() >= 2, "a bastion has many chests: " + chests.size());
        List<Integer> books = new ArrayList<>();
        List<Boolean> templates = new ArrayList<>();
        for (RandomizableContainerBlockEntity chest : chests) {
            chest.unpackLootTable(null);
            books.add(books(chest));
            templates.add(holdsATemplate(chest));
        }
        int first = templates.indexOf(true);
        assertEquals(first < 0 ? 0 : 1, books.stream().mapToInt(Integer::intValue).sum(), "one book among the bastion's chests if any holds a template: " + books + " " + templates);
        if (first >= 0) assertEquals(1, books.get(first), "in the first one opened that holds a smithing template");
        System.out.println("MEASURE a built bastion remnant: " + chests.size() + " chests; template per chest " + templates + "; books " + books);
    }

    /** A stronghold's corridor and crossing chests never hold it, even opened first; its library does, once. */
    private static void strongholdsGiveItInTheirLibrary(ServerLevel level) {
        List<BlockPos> rooms = rooms(stand(level, BuiltinStructures.STRONGHOLD, 320, 300, 4));
        List<String> order = List.of("stronghold_corridor", "stronghold_crossing", "stronghold_library", "stronghold_library");
        List<Integer> found = new ArrayList<>();
        for (int room = 0; room < 4; room++) {
            RandomizableContainerBlockEntity chest = chest(level, rooms.get(room), order.get(room));
            chest.unpackLootTable(null);
            found.add(books(chest));
        }
        assertEquals(List.of(0, 0, 1, 0), found, "the first library chest opened holds it, and nothing else of the stronghold does");
    }

    /** Every fortress chest is filled from the one table: the first opened holds the book, in each fortress. */
    private static void fortressesGiveItInTheFirstChestOpened(ServerLevel level) {
        for (int which = 0; which < 2; which++) {
            List<BlockPos> rooms = rooms(stand(level, BuiltinStructures.FORTRESS, 330 + which * 4, 300, 2));
            RandomizableContainerBlockEntity first = chest(level, rooms.get(1 - which), "nether_bridge"), second = chest(level, rooms.get(which), "nether_bridge");
            first.unpackLootTable(null);
            second.unpackLootTable(null);
            assertEquals(1, books(first), "fortress #" + which + ": the first chest opened");
            assertEquals(0, books(second), "fortress #" + which + ": and no other");
        }
    }

    /**
     * Rolls of a bastion chest with no smithing template bring no book and take nothing; the first with one brings it,
     * and no later one. The treasure chest always holds its netherite upgrade template, so it holds the book; a fortress's
     * chest inside a bastion is not the bastion's.
     */
    private static void bastionsGiveItOnlyBesideASmithingTemplate(ServerLevel level) {
        StructureStart rolled = stand(level, BuiltinStructures.BASTION_REMNANT, 340, 300, 1);
        BlockPos at = rooms(rolled).get(0);
        LootTable other = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.withDefaultNamespace("chests/bastion_other")));
        int bare = 0, withTemplate = 0;
        for (int roll = 0; roll < 2000 && withTemplate < 3; roll++) {
            List<ItemStack> loot = other.getRandomItems(params(level, at));
            boolean template = loot.stream().anyMatch(KeyOfSolomonLootServerTest::isATemplate);
            int books = (int) loot.stream().filter(stack -> keys().contains(stack.getItem())).count();
            if (!template) {
                bare++;
                assertEquals(0, books, "no template, no book");
                if (withTemplate == 0) assertFalse(level.getData(ModAttachments.STRUCTURE_PRIZES).taken("corruption:key_of_solomon", "minecraft:bastion_remnant", 340, 300),
                        "and nothing taken by a roll that brought no book");
            } else assertEquals(withTemplate++ == 0 ? 1 : 0, books, "the first roll with a template brings the book, no later one");
        }
        assertTrue(bare > 0 && withTemplate >= 2, "rolls of both kinds: " + bare + " bare, " + withTemplate + " with a template");

        List<BlockPos> rooms = rooms(stand(level, BuiltinStructures.BASTION_REMNANT, 344, 300, 3));
        RandomizableContainerBlockEntity foreign = chest(level, rooms.get(0), "nether_bridge");
        foreign.unpackLootTable(null);
        assertEquals(0, books(foreign), "a fortress's chest in a bastion is not the bastion's");
        RandomizableContainerBlockEntity treasure = chest(level, rooms.get(1), "bastion_treasure"), again = chest(level, rooms.get(2), "bastion_treasure");
        treasure.unpackLootTable(null);
        again.unpackLootTable(null);
        assertTrue(holdsATemplate(treasure), "the treasure chest holds its netherite upgrade template");
        assertEquals(1, books(treasure), "and beside it, the book");
        assertEquals(0, books(again), "once in each bastion");
    }

    /** A real end city tower top and ship, placed: the tower's two chests never hold it, even opened first; the ship's first does. */
    private static void endCitiesGiveItOnTheirShip(ServerLevel level) {
        Structure city = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.END_CITY);
        BlockPos at = new BlockPos(350 * 16, 40, 300 * 16);
        EndCityPieces.EndCityPiece tower = new EndCityPieces.EndCityPiece(level.getStructureManager(), "fat_tower_top", at, Rotation.NONE, true);
        EndCityPieces.EndCityPiece ship = new EndCityPieces.EndCityPiece(level.getStructureManager(), "ship", at.offset(40, 0, 0), Rotation.NONE, true);
        StructureStart start = new StructureStart(city, new ChunkPos(at), 0, new PiecesContainer(List.of(tower, ship)));
        record(level, city, start);
        place(level, start);
        List<RandomizableContainerBlockEntity> towerChests = chests(level, tower.getBoundingBox()), shipChests = chests(level, ship.getBoundingBox());
        assertEquals(2, towerChests.size(), "the tower top's two chests");
        assertEquals(2, shipChests.size(), "the ship's two chests");
        for (RandomizableContainerBlockEntity chest : towerChests) {
            chest.unpackLootTable(null);
            assertEquals(0, books(chest), "a tower chest, though opened before the ship's");
        }
        shipChests.get(0).unpackLootTable(null);
        shipChests.get(1).unpackLootTable(null);
        assertEquals(1, books(shipChests.get(0)), "the first of the ship's chests opened");
        assertEquals(0, books(shipChests.get(1)), "and not the other");
    }

    /** The same tables in chests that stand in no structure: no book. */
    private static void noneOutsideThem(ServerLevel level) {
        int x = 6000;
        for (String table : TABLES) {
            TestWorld.load(level, x, x, 6000, 6000);
            RandomizableContainerBlockEntity chest = chest(level, new BlockPos(x, -50, 6000), table);
            chest.unpackLootTable(null);
            assertEquals(0, books(chest), table + " outside any structure");
            x += 16;
        }
    }

    // ---- the conditions alone ----

    @Test void onceForEachStructureAndPrizeAndOnlyInItsPieces(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            StructureStart stronghold = stand(level, BuiltinStructures.STRONGHOLD, 400, 300, 2);
            List<BlockPos> rooms = rooms(stronghold);
            BlockPos room = rooms.get(0), between = rooms.get(0).offset(5, 0, 0);
            assertTrue(stronghold.getPieces().stream().noneMatch(piece -> piece.getBoundingBox().isInside(between)), "the gap between its rooms is inside its box but in no piece");

            LootItemCondition tagged = condition(level, "\"#minecraft:eye_of_ender_located\"", "test:book");
            assertFalse(tagged.test(context(level, between)), "not between its pieces");
            assertTrue(tagged.test(context(level, room)), "named by a tag: holds the first time");
            assertFalse(tagged.test(context(level, room)), "and not again");
            assertFalse(tagged.test(context(level, rooms.get(1))), "nor in another room of the same stronghold");
            assertTrue(condition(level, "\"minecraft:stronghold\"", "test:relic").test(context(level, room)), "another prize is counted apart");
            assertTrue(condition(level, "[\"minecraft:end_city\", \"minecraft:stronghold\"]", "test:map").test(context(level, room)), "named in a list");
            assertFalse(condition(level, "\"minecraft:fortress\"", "test:coin").test(context(level, room)), "a stronghold is no fortress");
            assertFalse(condition(level, "\"minecraft:stronghold\"", "test:book").test(context(level, new BlockPos(7000, -50, 7000))), "outside any");

            StructureStart other = stand(level, BuiltinStructures.STRONGHOLD, 404, 300, 2);
            assertTrue(tagged.test(context(level, rooms(other).get(0))), "another stronghold has its own");

            CompoundTag saved = level.serializeAttachments(level.registryAccess());
            assertNotNull(saved);
            assertTrue(saved.contains("corruption:structure_prizes"), "what was taken is saved with the level: " + saved.getAllKeys());
            assertTrue(level.getData(ModAttachments.STRUCTURE_PRIZES).taken("test:book", "minecraft:stronghold", 400, 300));
        });
    }

    @Test void aPieceIsKnownByTheTemplateItWasBuiltFrom(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            // Template pieces: an end city's tower top and ship, standing (not placed).
            Structure city = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.END_CITY);
            BlockPos at = new BlockPos(410 * 16, 40, 300 * 16);
            EndCityPieces.EndCityPiece tower = new EndCityPieces.EndCityPiece(level.getStructureManager(), "fat_tower_top", at, Rotation.NONE, true);
            EndCityPieces.EndCityPiece ship = new EndCityPieces.EndCityPiece(level.getStructureManager(), "ship", at.offset(40, 0, 0), Rotation.NONE, true);
            assertEquals(ResourceLocation.withDefaultNamespace("end_city/ship"), InStructurePiece.template(ship));
            record(level, city, new StructureStart(city, new ChunkPos(at), 0, new PiecesContainer(List.of(tower, ship))));
            TestWorld.load(level, at.getX(), at.getX() + 80, at.getZ(), at.getZ() + 40);
            LootItemCondition onShip = piece(level, "\"minecraft:end_city\"", "minecraft:end_city/ship");
            assertTrue(onShip.test(context(level, ship.getBoundingBox().getCenter())), "on the ship");
            assertTrue(onShip.test(context(level, ship.getBoundingBox().getCenter())), "and again: it takes nothing");
            assertFalse(onShip.test(context(level, tower.getBoundingBox().getCenter())), "not in the tower");
            assertTrue(piece(level, "\"minecraft:end_city\"", "minecraft:end_city/fat_tower_top").test(context(level, tower.getBoundingBox().getCenter())));
            assertFalse(piece(level, "\"minecraft:stronghold\"", "minecraft:end_city/ship").test(context(level, ship.getBoundingBox().getCenter())), "an end city is no stronghold");

            // Jigsaw pieces: a bastion's, laid out (not placed), each known by its pool element's template.
            ChunkGenerator generator = level.getChunkSource().getGenerator();
            Holder.Reference<Structure> bastion = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(BuiltinStructures.BASTION_REMNANT);
            StructureStart start = bastion.value().generate(level.registryAccess(), generator, generator.getBiomeSource(), level.getChunkSource().randomState(),
                    level.getStructureManager(), level.getSeed(), new ChunkPos(470, 300), 0, level, b -> true);
            record(level, bastion.value(), start);
            StructurePiece first = start.getPieces().get(0);
            ResourceLocation template = InStructurePiece.template(first);
            assertNotNull(template, "a bastion's pieces are built from templates");
            assertTrue(template.getPath().startsWith("bastion/"), template.toString());
            BlockPos middle = first.getBoundingBox().getCenter();
            TestWorld.load(level, middle.getX(), middle.getX(), middle.getZ(), middle.getZ());
            assertTrue(piece(level, "\"minecraft:bastion_remnant\"", template.toString()).test(context(level, middle)), "inside the piece built from " + template);
            assertFalse(piece(level, "\"minecraft:bastion_remnant\"", "minecraft:bastion/no_such_piece").test(context(level, middle)));

            // A piece built in code has no template.
            assertNull(InStructurePiece.template(room(new BoundingBox(0, 0, 0, 1, 1, 1))));
        });
    }

    // ---- helpers ----

    /** A chest table as the server reads it: the mod's own copy (the top of the stack), or vanilla's (the bottom). */
    private static JsonObject table(MinecraftServer server, String name, boolean vanilla) throws IOException {
        List<Resource> stack = server.getResourceManager().getResourceStack(ResourceLocation.withDefaultNamespace("loot_table/chests/" + name + ".json"));
        assertTrue(stack.size() >= 2, name + " is vanilla's, and the mod's over it: " + stack.stream().map(Resource::sourcePackId).toList());
        Resource resource = vanilla ? stack.get(0) : stack.get(stack.size() - 1);
        assertEquals(vanilla, resource.sourcePackId().equals("vanilla"), name + " from " + resource.sourcePackId());
        try (Reader reader = resource.openAsReader()) { return JsonParser.parseReader(reader).getAsJsonObject(); }
    }

    private static LootItemCondition condition(ServerLevel level, String structures, String prize) {
        return parse(level, "{\"condition\": \"corruption:once_per_structure\", \"structures\": " + structures + ", \"prize\": \"" + prize + "\"}");
    }

    private static LootItemCondition piece(ServerLevel level, String structures, String template) {
        return parse(level, "{\"condition\": \"corruption:structure_piece\", \"structures\": " + structures + ", \"templates\": [\"" + template + "\"]}");
    }

    private static LootItemCondition parse(ServerLevel level, String json) {
        return LootItemCondition.DIRECT_CODEC.parse(level.registryAccess().createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(json)).getOrThrow();
    }

    /** What a chest's loot is rolled with: where it stands. */
    private static LootParams params(ServerLevel level, BlockPos at) {
        return new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(at)).create(LootContextParamSets.CHEST);
    }

    /** Loot rolled at a place, as a chest's is. */
    private static LootContext context(ServerLevel level, BlockPos at) { return new LootContext.Builder(params(level, at)).create(Optional.empty()); }

    /**
     * A structure of this kind started in this chunk, standing as one in the world does (its start kept by its chunk, and
     * every chunk it reaches pointing to it), with rooms 10 blocks apart: pieces with nothing in them.
     */
    private static StructureStart stand(ServerLevel level, ResourceKey<Structure> kind, int chunkX, int chunkZ, int count) {
        Structure structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(kind);
        int x = chunkX * 16 + 2, z = chunkZ * 16 + 2, y = -55;
        List<StructurePiece> pieces = new ArrayList<>();
        for (int room = 0; room < count; room++) pieces.add(room(new BoundingBox(x + room * 10, y, z, x + room * 10 + 4, y + 4, z + 4)));
        StructureStart start = new StructureStart(structure, new ChunkPos(chunkX, chunkZ), 0, new PiecesContainer(pieces));
        TestWorld.load(level, x, x + count * 10, z, z + 4);
        record(level, structure, start);
        return start;
    }

    private static StructurePiece room(BoundingBox box) {
        return new StructurePiece(StructurePieceType.STRONGHOLD_ROOM_CROSSING, 0, box) {
            @Override protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {}
            @Override public void postProcess(WorldGenLevel level, StructureManager structures, ChunkGenerator generator, RandomSource random, BoundingBox box,
                                              ChunkPos chunk, BlockPos pivot) {}
        };
    }

    /** The middle of each of a start's pieces. */
    private static List<BlockPos> rooms(StructureStart start) { return start.getPieces().stream().map(piece -> piece.getBoundingBox().getCenter()).toList(); }

    /** Keeps a start in the chunk it started in and points every chunk it reaches to it, as world generation does. */
    private static void record(ServerLevel level, Structure structure, StructureStart start) {
        ChunkPos origin = start.getChunkPos();
        level.getChunk(origin.x, origin.z).setStartForStructure(structure, start);
        BoundingBox box = start.getBoundingBox();
        for (int x = box.minX() >> 4; x <= box.maxX() >> 4; x++)
            for (int z = box.minZ() >> 4; z <= box.maxZ() >> 4; z++) level.getChunk(x, z).addReferenceForStructure(structure, origin.toLong());
    }

    /** Builds a start's pieces into the world, chunk by chunk, as world generation does. */
    private static void place(ServerLevel level, StructureStart start) {
        BoundingBox box = start.getBoundingBox();
        TestWorld.load(level, box.minX(), box.maxX(), box.minZ(), box.maxZ());
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        ChunkPos.rangeClosed(new ChunkPos(box.minX() >> 4, box.minZ() >> 4), new ChunkPos(box.maxX() >> 4, box.maxZ() >> 4)).forEach(chunk ->
                start.placeInChunk(level, level.structureManager(), generator, level.getRandom(),
                        new BoundingBox(chunk.getMinBlockX(), level.getMinBuildHeight(), chunk.getMinBlockZ(), chunk.getMaxBlockX(), level.getMaxBuildHeight(), chunk.getMaxBlockZ()),
                        chunk));
    }

    /** The containers waiting to be filled inside a box, in a fixed order. */
    private static List<RandomizableContainerBlockEntity> chests(ServerLevel level, BoundingBox box) {
        List<RandomizableContainerBlockEntity> chests = new ArrayList<>();
        ChunkPos.rangeClosed(new ChunkPos(box.minX() >> 4, box.minZ() >> 4), new ChunkPos(box.maxX() >> 4, box.maxZ() >> 4)).forEach(chunk -> {
            for (BlockEntity entity : level.getChunk(chunk.x, chunk.z).getBlockEntities().values())
                if (entity instanceof RandomizableContainerBlockEntity chest && chest.getLootTable() != null && box.isInside(chest.getBlockPos())) chests.add(chest);
        });
        chests.sort(Comparator.comparing(BlockEntity::getBlockPos));
        return chests;
    }

    /** A chest to be filled from a vanilla chest table the first time it is opened. */
    private static RandomizableContainerBlockEntity chest(ServerLevel level, BlockPos at, String table) {
        level.setBlock(at, Blocks.CHEST.defaultBlockState(), 3);
        RandomizableContainerBlockEntity chest = (RandomizableContainerBlockEntity) level.getBlockEntity(at);
        chest.setLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/" + table)), at.asLong());
        return chest;
    }

    private static int books(RandomizableContainerBlockEntity chest) {
        Set<Item> keys = keys();
        int books = 0;
        for (int slot = 0; slot < chest.getContainerSize(); slot++)
            if (keys.contains(chest.getItem(slot).getItem())) books += chest.getItem(slot).getCount();
        return books;
    }

    private static boolean isATemplate(ItemStack stack) { return stack.is(ItemTags.TRIM_TEMPLATES) || stack.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE); }

    private static boolean holdsATemplate(RandomizableContainerBlockEntity chest) {
        for (int slot = 0; slot < chest.getContainerSize(); slot++) if (isATemplate(chest.getItem(slot))) return true;
        return false;
    }

    /** Every book of the Key: the items of the arcane_key data map. */
    private static Set<Item> keys() {
        return BuiltInRegistries.ITEM.getDataMap(ArcaneKey.DATA_MAP).keySet().stream().map(key -> BuiltInRegistries.ITEM.get(key)).collect(Collectors.toSet());
    }
}
