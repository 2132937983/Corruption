package com.miophas.corruption.common.block;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.GeneratedCorruption;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The corruption arcane bricks' stairs, slab and wall, as vanilla's bricks have theirs: made of the same as the bricks,
 * in vanilla's tags, crafted and stonecut as vanilla's are, dropping as vanilla's do (a double slab two), a wall
 * joining its neighbours; the infection has a corrupted twin of each; and every model, texture and name they need is
 * there.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class ArcaneBrickFamilyServerTest {
    private static final List<String> SHAPES = List.of("stairs", "slab", "wall");

    private static Block block(String shape) {
        return switch (shape) {
            case "stairs" -> ModBlocks.CORRUPTION_ARCANE_BRICK_STAIRS.get();
            case "slab" -> ModBlocks.CORRUPTION_ARCANE_BRICK_SLAB.get();
            default -> ModBlocks.CORRUPTION_ARCANE_BRICK_WALL.get();
        };
    }
    private static JsonElement json(String path) throws Exception {
        try (InputStream in = ArcaneBrickFamilyServerTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }
    private static boolean exists(String path) throws Exception {
        try (InputStream in = ArcaneBrickFamilyServerTest.class.getResourceAsStream(path)) { return in != null; }
    }
    /** A model's resource path, from its name ("corruption:block/x" or "minecraft:block/stairs"); null for vanilla's own. */
    private static String modelPath(String name) {
        ResourceLocation id = ResourceLocation.parse(name);
        return id.getNamespace().equals("corruption") ? "/assets/corruption/models/" + id.getPath() + ".json" : null;
    }
    /** Every string under a key of this name anywhere in a JSON tree. */
    private static void collect(JsonElement element, String key, List<String> found) {
        if (element.isJsonObject()) for (var entry : element.getAsJsonObject().entrySet()) {
            if (entry.getKey().equals(key) && entry.getValue().isJsonPrimitive()) found.add(entry.getValue().getAsString());
            collect(entry.getValue(), key, found);
        } else if (element.isJsonArray()) for (JsonElement item : element.getAsJsonArray()) collect(item, key, found);
    }

    @Test void eachIsMadeOfTheBricksAndTaggedAsVanillasBrickKinAre(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BlockState bricks = ModBlocks.CORRUPTION_ARCANE_BRICK.get().defaultBlockState();
            for (String shape : SHAPES) {
                BlockState state = block(shape).defaultBlockState();
                assertEquals(bricks.getDestroySpeed(level, BlockPos.ZERO), state.getDestroySpeed(level, BlockPos.ZERO), shape + " is as hard as the bricks");
                assertEquals(ModBlocks.CORRUPTION_ARCANE_BRICK.get().getExplosionResistance(), block(shape).getExplosionResistance(), shape);
                assertEquals(bricks.getSoundType(), state.getSoundType(), shape + " sounds as the bricks");
                assertTrue(state.requiresCorrectToolForDrops(), shape + " needs a pickaxe");
                assertTrue(state.is(BlockTags.MINEABLE_WITH_PICKAXE), shape);
            }
            assertTrue(block("stairs").defaultBlockState().is(BlockTags.STAIRS) && new ItemStack(ModItems.CORRUPTION_ARCANE_BRICK_STAIRS.get()).is(ItemTags.STAIRS));
            assertTrue(block("slab").defaultBlockState().is(BlockTags.SLABS) && new ItemStack(ModItems.CORRUPTION_ARCANE_BRICK_SLAB.get()).is(ItemTags.SLABS));
            assertTrue(block("wall").defaultBlockState().is(BlockTags.WALLS) && new ItemStack(ModItems.CORRUPTION_ARCANE_BRICK_WALL.get()).is(ItemTags.WALLS));
            assertInstanceOf(StairBlock.class, block("stairs"));
            assertInstanceOf(SlabBlock.class, block("slab"));
            assertInstanceOf(WallBlock.class, block("wall"));
        });
    }

    @Test void theyAreCraftedAndStonecutAsVanillasAre(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Map<String, Integer> crafted = Map.of("stairs", 4, "slab", 6, "wall", 6), cut = Map.of("stairs", 1, "slab", 2, "wall", 1);
            for (String shape : SHAPES) {
                Recipe<?> crafting = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", "corruption_arcane_brick_" + shape))
                        .orElseThrow().value();
                assertEquals(RecipeType.CRAFTING, crafting.getType());
                ItemStack made = crafting.getResultItem(level.registryAccess());
                assertTrue(made.is(block(shape).asItem()) && made.getCount() == crafted.get(shape), shape + " crafted: " + made);
                assertTrue(crafting.getIngredients().stream().filter(ingredient -> !ingredient.isEmpty())
                        .allMatch(ingredient -> ingredient.test(new ItemStack(ModItems.CORRUPTION_ARCANE_BRICK.get()))), shape + " from the bricks");
                Recipe<?> cutting = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption",
                        "corruption_arcane_brick_" + shape + "_from_bricks_stonecutting")).orElseThrow().value();
                assertEquals(RecipeType.STONECUTTING, cutting.getType());
                ItemStack cutOut = cutting.getResultItem(level.registryAccess());
                assertTrue(cutOut.is(block(shape).asItem()) && cutOut.getCount() == cut.get(shape), shape + " stonecut: " + cutOut);
                assertTrue(cutting.getIngredients().get(0).test(new ItemStack(ModItems.CORRUPTION_ARCANE_BRICK.get())));
                assertFalse(cutting.getIngredients().get(0).test(new ItemStack(Items.BRICKS)), "not from vanilla's bricks");
            }
        });
    }

    @Test void theyDropAsVanillasDoAndAWallJoinsItsNeighbours(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ItemStack pickaxe = new ItemStack(Items.WOODEN_PICKAXE);
            for (String shape : List.of("stairs", "wall")) {
                List<ItemStack> drops = Block.getDrops(block(shape).defaultBlockState(), level, BlockPos.ZERO, null, null, pickaxe);
                assertTrue(drops.size() == 1 && drops.get(0).is(block(shape).asItem()) && drops.get(0).getCount() == 1, shape + " gives itself: " + drops);
            }
            BlockState slab = block("slab").defaultBlockState();
            assertEquals(1, Block.getDrops(slab, level, BlockPos.ZERO, null, null, pickaxe).get(0).getCount(), "a slab, one");
            assertEquals(2, Block.getDrops(slab.setValue(SlabBlock.TYPE, SlabType.DOUBLE), level, BlockPos.ZERO, null, null, pickaxe).get(0).getCount(),
                    "a double slab, two");
            // As vanilla's: a bare hand is not the tool that gets them (the harvest, not the loot table, says so).
            for (String shape : SHAPES) {
                assertTrue(pickaxe.isCorrectToolForDrops(block(shape).defaultBlockState()), shape + ": any pickaxe gets it");
                assertFalse(ItemStack.EMPTY.isCorrectToolForDrops(block(shape).defaultBlockState()), shape + ": a bare hand does not");
            }
            assertFalse(ItemStack.EMPTY.isCorrectToolForDrops(Blocks.BRICK_STAIRS.defaultBlockState()), "as with vanilla's brick stairs");

            BlockPos at = new BlockPos(150_000, -59, 3000);
            TestWorld.load(level, at.getX() - 4, at.getX() + 4, at.getZ() - 4, at.getZ() + 4);
            level.setBlock(at, block("wall").defaultBlockState(), 3);
            level.setBlock(at.north(), block("wall").defaultBlockState(), 3);
            level.setBlock(at.east(), Blocks.STONE_BRICK_WALL.defaultBlockState(), 3);
            BlockState wall = level.getBlockState(at);
            assertNotEquals(WallSide.NONE, wall.getValue(WallBlock.NORTH_WALL), "a wall joins its own kind");
            assertNotEquals(WallSide.NONE, wall.getValue(WallBlock.EAST_WALL), "and vanilla's walls");
            assertEquals(WallSide.NONE, wall.getValue(WallBlock.WEST_WALL), "and nothing where there is nothing");
        });
    }

    @Test void theInfectionHasACorruptedTwinOfEach(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BlockState stairs = block("stairs").defaultBlockState().setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP);
            for (BlockState state : List.of(stairs, block("slab").defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), block("wall").defaultBlockState())) {
                BlockState twin = GeneratedCorruption.convert(state);
                assertNotNull(twin, state + " has a corrupted twin");
                assertEquals(state.getValues().keySet().stream().map(property -> property.getName()).sorted().toList(),
                        twin.getValues().keySet().stream().map(property -> property.getName()).sorted().toList(), "keeping its shape's properties");
            }
            assertEquals(Direction.EAST, GeneratedCorruption.convert(stairs).getValue(StairBlock.FACING), "the twin of an east-facing stair faces east");
        });
    }

    @Test void everyModelTextureAndNameTheyNeedIsThere() throws Exception {
        JsonObject en = json("/assets/corruption/lang/en_us.json").getAsJsonObject(), zh = json("/assets/corruption/lang/zh_cn.json").getAsJsonObject();
        for (String shape : SHAPES) {
            String id = "corruption_arcane_brick_" + shape;
            assertTrue(en.has("block.corruption." + id) && zh.has("block.corruption." + id), id + " is named in both languages");
            List<String> models = new ArrayList<>();
            collect(json("/assets/corruption/blockstates/" + id + ".json"), "model", models);
            models.add(json("/assets/corruption/models/item/" + id + ".json").getAsJsonObject().get("parent").getAsString());
            assertFalse(models.isEmpty());
            for (String model : models) {
                String path = modelPath(model);
                assertNotNull(path, model + " is the mod's own");
                JsonElement json = json(path);
                List<String> textures = new ArrayList<>();
                if (json.getAsJsonObject().has("textures"))
                    for (var texture : json.getAsJsonObject().getAsJsonObject("textures").entrySet()) textures.add(texture.getValue().getAsString());
                for (String texture : textures) {
                    ResourceLocation at = ResourceLocation.parse(texture);
                    assertTrue(exists("/assets/" + at.getNamespace() + "/textures/" + at.getPath() + ".png"), model + " draws with " + texture);
                }
            }
        }
    }
}
