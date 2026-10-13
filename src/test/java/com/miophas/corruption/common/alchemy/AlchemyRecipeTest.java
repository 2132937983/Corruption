package com.miophas.corruption.common.alchemy;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonParser;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModRecipes;
import com.mojang.serialization.JsonOps;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.junit.jupiter.api.Test;

class AlchemyRecipeTest {
    private static AlchemyInput input(ItemStack... stacks) {
        ItemStack[] slots = new ItemStack[AlchemyRecipe.SLOTS];
        for (int slot = 0; slot < slots.length; slot++) slots[slot] = slot < stacks.length ? stacks[slot] : ItemStack.EMPTY;
        return new AlchemyInput(List.of(slots));
    }

    private static SimpleContainer bench(ItemStack... stacks) {
        SimpleContainer bench = new SimpleContainer(AlchemyRecipe.SLOTS);
        for (int slot = 0; slot < stacks.length; slot++) bench.setItem(slot, stacks[slot]);
        return bench;
    }

    private static AlchemyRecipe load(String name) throws Exception {
        try (var in = AlchemyRecipeTest.class.getResourceAsStream("/data/corruption/recipe/alchemy/" + name + ".json")) {
            assertNotNull(in, name);
            Recipe<?> parsed = Recipe.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))).getOrThrow();
            AlchemyRecipe recipe = assertInstanceOf(AlchemyRecipe.class, parsed);
            assertSame(ModRecipes.ALCHEMY.get(), recipe.getType(), "Both kinds must be found by the bench's one recipe type");
            return recipe;
        }
    }

    private static final AlchemyRecipe SHAPELESS = new ShapelessAlchemyRecipe(
            List.of(SizedIngredient.of(Items.REDSTONE, 8), SizedIngredient.of(Items.GLOWSTONE_DUST, 2)), new ItemStack(Items.DIAMOND, 3));
    private static final AlchemyRecipe SHAPED = new ShapedAlchemyRecipe(
            Map.of(0, SizedIngredient.of(Items.REDSTONE, 8), 4, SizedIngredient.of(Items.GLOWSTONE_DUST, 2)), new ItemStack(Items.DIAMOND, 3));

    @Test void shapelessIngredientsMayLieOnAnyPointsAndEachSlotGivesUpItsOwnAmount() {
        assertArrayEquals(new int[] {8, 2, 0, 0, 0}, SHAPELESS.consumption(input(new ItemStack(Items.REDSTONE, 8), new ItemStack(Items.GLOWSTONE_DUST, 2))));
        assertArrayEquals(new int[] {0, 2, 0, 0, 8}, SHAPELESS.consumption(input(ItemStack.EMPTY, new ItemStack(Items.GLOWSTONE_DUST, 64),
                ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.REDSTONE, 30))), "Order must not matter, and a slot may hold more than is needed");
        assertEquals(3, SHAPELESS.assemble(input(), null).getCount());
    }

    @Test void shapelessRejectsTooFewItemsMissingIngredientsAndStrayItems() {
        assertNull(SHAPELESS.consumption(input(new ItemStack(Items.REDSTONE, 7), new ItemStack(Items.GLOWSTONE_DUST, 2))), "Quantity is part of the recipe");
        assertNull(SHAPELESS.consumption(input(new ItemStack(Items.REDSTONE, 8))));
        assertNull(SHAPELESS.consumption(input(new ItemStack(Items.REDSTONE, 8), new ItemStack(Items.GLOWSTONE_DUST, 2), new ItemStack(Items.DIRT))));
        assertNull(SHAPELESS.consumption(input()));
        // The amount must be in one slot: two slots of four are two stacks, and the recipe names one.
        assertNull(SHAPELESS.consumption(input(new ItemStack(Items.REDSTONE, 4), new ItemStack(Items.REDSTONE, 4), new ItemStack(Items.GLOWSTONE_DUST, 2))));
    }

    @Test void shapelessGivesTwoIngredientsOfOneItemToTheSlotsThatCanAffordThem() {
        AlchemyRecipe recipe = new ShapelessAlchemyRecipe(List.of(SizedIngredient.of(Items.REDSTONE, 2), SizedIngredient.of(Items.REDSTONE, 10)), new ItemStack(Items.DIAMOND));
        // Only one pairing works: the slot of three cannot pay ten.
        assertArrayEquals(new int[] {10, 2, 0, 0, 0}, recipe.consumption(input(new ItemStack(Items.REDSTONE, 12), new ItemStack(Items.REDSTONE, 3))));
        assertNull(recipe.consumption(input(new ItemStack(Items.REDSTONE, 9), new ItemStack(Items.REDSTONE, 3))));
    }

    @Test void shapedWantsEachIngredientInItsOwnSlotAndTheOtherSlotsEmpty() {
        ItemStack redstone = new ItemStack(Items.REDSTONE, 20), glowstone = new ItemStack(Items.GLOWSTONE_DUST, 2);
        assertArrayEquals(new int[] {8, 0, 0, 0, 2}, SHAPED.consumption(input(redstone, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, glowstone)));
        assertNull(SHAPED.consumption(input(glowstone, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, redstone)), "Swapped slots are a different arrangement");
        assertNull(SHAPED.consumption(input(redstone, glowstone)), "The right items in the wrong slot do not match");
        assertNull(SHAPED.consumption(input(redstone, new ItemStack(Items.DIRT), ItemStack.EMPTY, ItemStack.EMPTY, glowstone)), "Unnamed slots must be empty");
        assertNull(SHAPED.consumption(input(new ItemStack(Items.REDSTONE, 7), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, glowstone)));
        // The same items satisfy the shapeless recipe wherever they lie.
        assertNotNull(SHAPELESS.consumption(input(redstone, glowstone)));
        assertEquals(AlchemyRecipe.SLOTS, SHAPED.getIngredients().size());
        assertThrows(IllegalArgumentException.class, () -> new ShapedAlchemyRecipe(Map.of(5, SizedIngredient.of(Items.REDSTONE, 1)), new ItemStack(Items.DIAMOND)));
    }

    @Test void craftingTakesTheAmountsAndCanRepeatUntilTheBenchRunsShort() {
        for (AlchemyRecipe recipe : new AlchemyRecipe[] {SHAPELESS, SHAPED}) {
            SimpleContainer bench = bench(new ItemStack(Items.REDSTONE, 20), ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.GLOWSTONE_DUST, 5));
            assertTrue(recipe.craft(bench).isEmpty());
            assertEquals(12, bench.getItem(0).getCount());
            assertEquals(3, bench.getItem(4).getCount());
            assertNotNull(recipe.craft(bench));
            assertEquals(4, bench.getItem(0).getCount());
            assertEquals(1, bench.getItem(4).getCount());
            assertNull(recipe.craft(bench), "Four redstone and one glowstone are not enough for a third craft");
            assertEquals(4, bench.getItem(0).getCount(), "A failed craft must not take anything");
            assertEquals(1, bench.getItem(4).getCount());
        }
    }

    @Test void emptiedContainersStayOnTheBenchAndTheRestAreHandedBack() {
        AlchemyRecipe recipe = new ShapelessAlchemyRecipe(List.of(SizedIngredient.of(Items.WATER_BUCKET, 1), SizedIngredient.of(Items.HONEY_BOTTLE, 2)), new ItemStack(Items.DIAMOND));
        SimpleContainer bench = bench(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.HONEY_BOTTLE, 5));
        List<ItemStack> overflow = recipe.craft(bench);
        assertSame(Items.BUCKET, bench.getItem(0).getItem(), "The emptied slot keeps its bucket");
        assertEquals(3, bench.getItem(1).getCount());
        assertEquals(1, overflow.size(), "Bottles cannot go back into a slot that still holds honey");
        assertSame(Items.GLASS_BOTTLE, overflow.get(0).getItem());
        assertEquals(2, overflow.get(0).getCount());
    }

    @Test void ironPickaxeIsShapedTwoIngotsInSlotZeroAndThreeSticksInSlotOne() throws Exception {
        AlchemyRecipe recipe = load("iron_pickaxe");
        assertInstanceOf(ShapedAlchemyRecipe.class, recipe);
        assertSame(ModRecipes.ALCHEMY_SHAPED.get(), recipe.getSerializer());
        assertSame(Items.IRON_PICKAXE, recipe.getResultItem(null).getItem());
        assertArrayEquals(new int[] {2, 3, 0, 0, 0}, recipe.consumption(input(new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.STICK, 3))));
        assertArrayEquals(new int[] {2, 3, 0, 0, 0}, recipe.consumption(input(new ItemStack(Items.IRON_INGOT, 10), new ItemStack(Items.STICK, 64))));
        assertNull(recipe.consumption(input(new ItemStack(Items.STICK, 3), new ItemStack(Items.IRON_INGOT, 2))), "Sticks in slot 0 and ingots in slot 1 is the wrong way round");
        assertNull(recipe.consumption(input(new ItemStack(Items.IRON_INGOT, 1), new ItemStack(Items.STICK, 3))));
        assertNull(recipe.consumption(input(new ItemStack(Items.IRON_INGOT, 2), new ItemStack(Items.STICK, 2))));
        assertNull(recipe.consumption(input(new ItemStack(Items.IRON_INGOT, 2), ItemStack.EMPTY, new ItemStack(Items.STICK, 3))));
    }

    @Test void goldBlockIsShapelessNineIngotsOnAnyOnePoint() throws Exception {
        AlchemyRecipe recipe = load("gold_block");
        assertInstanceOf(ShapelessAlchemyRecipe.class, recipe);
        assertSame(ModRecipes.ALCHEMY_SHAPELESS.get(), recipe.getSerializer());
        assertSame(Items.GOLD_BLOCK, recipe.getResultItem(null).getItem());
        for (int slot = 0; slot < AlchemyRecipe.SLOTS; slot++) {
            ItemStack[] stacks = new ItemStack[AlchemyRecipe.SLOTS];
            java.util.Arrays.fill(stacks, ItemStack.EMPTY);
            stacks[slot] = new ItemStack(Items.GOLD_INGOT, 9);
            int[] taken = recipe.consumption(input(stacks));
            assertNotNull(taken, "slot " + slot);
            assertEquals(9, taken[slot]);
        }
        assertNull(recipe.consumption(input(new ItemStack(Items.GOLD_INGOT, 8))));
        SimpleContainer bench = bench(ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.GOLD_INGOT, 20));
        assertNotNull(recipe.craft(bench));
        assertNotNull(recipe.craft(bench));
        assertNull(recipe.craft(bench));
        assertEquals(2, bench.getItem(2).getCount());
    }

    @Test void theExampleBucketRecipeStillLoads() throws Exception {
        AlchemyRecipe recipe = load("flowing_corruption_bucket");
        assertSame(ModItems.FLOWING_CORRUPTION_BUCKET.get(), recipe.getResultItem(null).getItem());
        assertArrayEquals(new int[] {8, 0, 1, 2, 0}, recipe.consumption(input(new ItemStack(ModItems.CORRUPTION_DUST.get(), 8), ItemStack.EMPTY,
                new ItemStack(Items.BUCKET), new ItemStack(ModItems.CORRUPTION_CRYSTAL.get(), 2))));
    }

    @Test void recipeFilesAreRejectedWhenTheyDoNotFitTheBench() {
        var shapeless = ShapelessAlchemyRecipe.CODEC.codec();
        String six = "{\"ingredients\":[" + "{\"item\":\"minecraft:stone\"},".repeat(5) + "{\"item\":\"minecraft:stone\"}],\"result\":{\"id\":\"minecraft:diamond\"}}";
        assertTrue(shapeless.parse(JsonOps.INSTANCE, JsonParser.parseString(six)).isError());
        assertTrue(shapeless.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"ingredients\":[],\"result\":{\"id\":\"minecraft:diamond\"}}")).isError());
        String zero = "{\"ingredients\":[{\"item\":\"minecraft:stone\",\"count\":0}],\"result\":{\"id\":\"minecraft:diamond\"}}";
        assertTrue(shapeless.parse(JsonOps.INSTANCE, JsonParser.parseString(zero)).isError());
        var shaped = ShapedAlchemyRecipe.CODEC.codec();
        for (String slots : new String[] {"{}", "{\"5\":{\"item\":\"minecraft:stone\"}}", "{\"-1\":{\"item\":\"minecraft:stone\"}}", "{\"top\":{\"item\":\"minecraft:stone\"}}"})
            assertTrue(shaped.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"slots\":" + slots + ",\"result\":{\"id\":\"minecraft:diamond\"}}")).isError(), slots);
        assertTrue(shaped.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"slots\":{\"4\":{\"item\":\"minecraft:stone\",\"count\":3}},\"result\":{\"id\":\"minecraft:diamond\"}}")).isSuccess());
    }
}
