package com.miophas.corruption.common.cooking;

import com.miophas.corruption.api.arcane.ArcaneString;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * What the brass cauldron holds, as its recipes see it: the seven things in it, the fluid in its tank, and the
 * string the parchment in its parchment slot bears.
 *
 * @param inscription null without an inscribed parchment
 */
public record BrassCookingInput(List<ItemStack> items, FluidStack fluid, ResourceKey<ArcaneString> inscription) implements RecipeInput {
    @Override public ItemStack getItem(int index) { return items.get(index); }
    @Override public int size() { return items.size(); }
}
