package com.miophas.corruption.common.alchemy;

import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModRecipes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * A corruption bench recipe. Like crafting, it comes in a shaped kind ({@link ShapedAlchemyRecipe}: each
 * ingredient in a named slot) and a shapeless kind ({@link ShapelessAlchemyRecipe}: any arrangement).
 *
 * <p>Both differ from vanilla crafting in one way: an ingredient asks for a quantity, its slot must hold at
 * least that many, and crafting takes that many from it rather than one.
 */
public interface AlchemyRecipe extends Recipe<AlchemyInput> {
    /** Slots are numbered around the bench's pentagram: 0 top left, 1 top right, 2 left, 3 right, 4 bottom. */
    int SLOTS = 5;

    /** How many items this recipe takes from each slot, or null when the slots do not satisfy it. */
    int[] consumption(AlchemyInput input);

    /**
     * Crafts once: takes each ingredient's amount out of the bench. Containers left behind, such as an empty
     * bucket, go back into the slot they came from when it was emptied; the rest are returned for the crafter.
     * Returns null, changing nothing, when the bench does not satisfy the recipe.
     */
    default List<ItemStack> craft(Container bench) {
        int[] taken = consumption(AlchemyInput.of(bench));
        if (taken == null) return null;
        List<ItemStack> overflow = new ArrayList<>();
        for (int slot = 0; slot < taken.length; slot++) {
            if (taken[slot] == 0) continue;
            ItemStack left = bench.getItem(slot).getCraftingRemainingItem();
            bench.removeItem(slot, taken[slot]);
            for (int remaining = left.isEmpty() ? 0 : taken[slot]; remaining > 0; ) {
                ItemStack returned = left.copyWithCount(Math.min(remaining, left.getMaxStackSize()));
                remaining -= returned.getCount();
                if (bench.getItem(slot).isEmpty()) bench.setItem(slot, returned); else overflow.add(returned);
            }
        }
        return overflow;
    }

    @Override default boolean matches(AlchemyInput input, Level level) { return consumption(input) != null; }
    @Override default boolean canCraftInDimensions(int width, int height) { return true; }
    // The vanilla recipe book has no page for this type; "special" keeps it from logging an unknown category.
    @Override default boolean isSpecial() { return true; }
    @Override default ItemStack getToastSymbol() { return new ItemStack(ModBlocks.CORRUPTION_BENCH.get()); }
    @Override default RecipeType<?> getType() { return ModRecipes.ALCHEMY.get(); }
}
