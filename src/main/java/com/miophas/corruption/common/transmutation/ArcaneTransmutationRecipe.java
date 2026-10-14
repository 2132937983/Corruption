package com.miophas.corruption.common.transmutation;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCodec;
import com.miophas.corruption.common.registry.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

/**
 * Corrupted Arcane Transmutation: in the arcane transmuter, an item (in the amount the recipe asks for) and
 * some Corruption Energy become something else. Every transmutation takes the same time,
 * {@link CorruptionEnergyApi#transmuteTicks()}; the recipe sets only what goes in, what it costs and what
 * comes out:
 * <pre>{"type": "corruption:arcane_transmutation", "ingredient": {"item": "minecraft:water_bucket"},
 *  "energy": "20KCE", "result": {"id": "corruption:flowing_corruption_bucket"}}</pre>
 *
 * @param energy in CE
 */
public record ArcaneTransmutationRecipe(SizedIngredient ingredient, long energy, ItemStack result) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<ArcaneTransmutationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(ArcaneTransmutationRecipe::ingredient),
            CorruptionEnergyCodec.AMOUNT.fieldOf("energy").forGetter(ArcaneTransmutationRecipe::energy),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ArcaneTransmutationRecipe::result)).apply(instance, ArcaneTransmutationRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArcaneTransmutationRecipe> STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC, ArcaneTransmutationRecipe::ingredient, ByteBufCodecs.VAR_LONG, ArcaneTransmutationRecipe::energy,
            ItemStack.STREAM_CODEC, ArcaneTransmutationRecipe::result, ArcaneTransmutationRecipe::new);

    /** The ingredient is there in at least the amount asked for. */
    @Override public boolean matches(SingleRecipeInput input, Level level) { return ingredient.test(input.item()); }
    @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY, ingredient.ingredient()); }
    /** Not shown in the recipe book, which has no page for it; JEI shows it instead. */
    @Override public boolean isSpecial() { return true; }
    @Override public RecipeSerializer<?> getSerializer() { return ModRecipes.ARCANE_TRANSMUTATION_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return ModRecipes.ARCANE_TRANSMUTATION.get(); }

    public static final class Serializer implements RecipeSerializer<ArcaneTransmutationRecipe> {
        @Override public MapCodec<ArcaneTransmutationRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, ArcaneTransmutationRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
