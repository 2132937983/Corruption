package com.miophas.corruption.common.alchemy;

import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/** What lies on the five points of the corruption bench, in slot order. */
public record AlchemyInput(List<ItemStack> items) implements RecipeInput {
    public AlchemyInput {
        items = List.copyOf(items);
    }

    public static AlchemyInput of(Container bench) {
        return new AlchemyInput(java.util.stream.IntStream.range(0, AlchemyRecipe.SLOTS).mapToObj(bench::getItem).toList());
    }

    @Override public ItemStack getItem(int index) { return items.get(index); }
    @Override public int size() { return items.size(); }
}
