package com.miophas.corruption.common.alchemy;

import com.miophas.corruption.common.registry.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.RecipeMatcher;

/** One to five ingredients with amounts, on any of the bench's points. */
public final class ShapelessAlchemyRecipe implements AlchemyRecipe {
    public static final MapCodec<ShapelessAlchemyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedIngredient.FLAT_CODEC.listOf(1, SLOTS).fieldOf("ingredients").forGetter(ShapelessAlchemyRecipe::ingredients),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)).apply(instance, ShapelessAlchemyRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessAlchemyRecipe> STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list(SLOTS)), ShapelessAlchemyRecipe::ingredients,
            ItemStack.STREAM_CODEC, recipe -> recipe.result, ShapelessAlchemyRecipe::new);

    private final List<SizedIngredient> ingredients;
    private final ItemStack result;

    public ShapelessAlchemyRecipe(List<SizedIngredient> ingredients, ItemStack result) {
        this.ingredients = List.copyOf(ingredients);
        this.result = result;
    }

    public List<SizedIngredient> ingredients() { return ingredients; }

    /** Every filled slot must be claimed by a different ingredient; a slot may hold more than is needed. */
    @Override public int[] consumption(AlchemyInput input) {
        List<ItemStack> filled = new ArrayList<>(SLOTS);
        List<Integer> slots = new ArrayList<>(SLOTS);
        for (int slot = 0; slot < input.size(); slot++) {
            if (!input.getItem(slot).isEmpty()) {
                filled.add(input.getItem(slot));
                slots.add(slot);
            }
        }
        // The matcher pairs stacks with ingredients even when one stack could satisfy several of them.
        int[] matches = RecipeMatcher.findMatches(filled, ingredients.stream().map(ingredient -> (Predicate<ItemStack>) ingredient::test).toList());
        if (matches == null) return null;
        int[] taken = new int[input.size()];
        for (int index = 0; index < matches.length; index++) taken[slots.get(index)] = ingredients.get(matches[index]).count();
        return taken;
    }

    @Override public ItemStack assemble(AlchemyInput input, HolderLookup.Provider registries) { return result.copy(); }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> plain = NonNullList.create();
        ingredients.forEach(ingredient -> plain.add(ingredient.ingredient()));
        return plain;
    }
    @Override public RecipeSerializer<?> getSerializer() { return ModRecipes.ALCHEMY_SHAPELESS.get(); }

    public static final class Serializer implements RecipeSerializer<ShapelessAlchemyRecipe> {
        @Override public MapCodec<ShapelessAlchemyRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, ShapelessAlchemyRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
