package com.miophas.corruption.common.cooking;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.api.energy.CorruptionEnergyCodec;
import com.miophas.corruption.common.registry.ModRecipes;
import com.miophas.corruption.core.cooking.BrassCauldronRules;
import com.miophas.corruption.core.cooking.ShapelessMatch;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * Brass cauldron cooking: up to seven ingredients, in any order and any of its slots, each perhaps several of
 * a thing in one slot; a fluid from its tank, if the recipe wants one; a parchment in its parchment slot
 * bearing a given string of the arcane, if the recipe wants one; and Corruption Energy, if it costs any.
 * <pre>{"type": "corruption:brass_cooking",
 *  "ingredients": [{"item": "minecraft:rotten_flesh"}],
 *  "fluid": {"fluid": "corruption:flowing_corruption", "amount": 10},
 *  "inscription": "corruption:corruption",
 *  "energy": "10CE", "time": 100,
 *  "result": {"id": "corruption:corruption_remain"}}</pre>
 * "fluid", "inscription" and "energy" may be left out, for none; "time" too, for
 * {@link BrassCauldronRules#DEFAULT_COOK_TICKS}. Every slot with something in it must be one of the
 * ingredients, so nothing is left over. The parchment is never used up (see {@link BrassCauldronRules#inscribed}).
 *
 * @param inscription the arcane string the parchment must bear, if any
 * @param energy in CE
 * @param time in ticks
 */
public record BrassCookingRecipe(List<SizedIngredient> ingredients, Optional<SizedFluidIngredient> fluid, Optional<ResourceKey<ArcaneString>> inscription,
                                 long energy, int time, ItemStack result) implements Recipe<BrassCookingInput> {
    public static final MapCodec<BrassCookingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedIngredient.FLAT_CODEC.listOf(1, BrassCauldronRules.INPUTS).fieldOf("ingredients").forGetter(BrassCookingRecipe::ingredients),
            SizedFluidIngredient.FLAT_CODEC.optionalFieldOf("fluid").forGetter(BrassCookingRecipe::fluid),
            ArcaneApi.INSCRIPTION_CODEC.optionalFieldOf("inscription").forGetter(BrassCookingRecipe::inscription),
            CorruptionEnergyCodec.AMOUNT.optionalFieldOf("energy", 0L).forGetter(BrassCookingRecipe::energy),
            Codec.intRange(1, BrassCauldronRules.MAX_COOK_TICKS).optionalFieldOf("time", BrassCauldronRules.DEFAULT_COOK_TICKS).forGetter(BrassCookingRecipe::time),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(BrassCookingRecipe::result)).apply(instance, BrassCookingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BrassCookingRecipe> STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list(BrassCauldronRules.INPUTS)), BrassCookingRecipe::ingredients,
            ByteBufCodecs.optional(SizedFluidIngredient.STREAM_CODEC), BrassCookingRecipe::fluid,
            ByteBufCodecs.optional(ArcaneApi.INSCRIPTION_STREAM_CODEC), BrassCookingRecipe::inscription,
            ByteBufCodecs.VAR_LONG, BrassCookingRecipe::energy,
            ByteBufCodecs.VAR_INT, BrassCookingRecipe::time,
            ItemStack.STREAM_CODEC, BrassCookingRecipe::result, BrassCookingRecipe::new);

    /** For each ingredient, the slot that meets it, or null if the cauldron's things do not make this recipe. */
    public int[] slots(BrassCookingInput input) {
        boolean[] filled = new boolean[input.size()];
        for (int slot = 0; slot < filled.length; slot++) filled[slot] = !input.getItem(slot).isEmpty();
        return ShapelessMatch.match(ingredients.size(), filled, (ingredient, slot) -> ingredients.get(ingredient).test(input.getItem(slot)));
    }

    /** The things are all there, nothing else is, the tank holds the fluid in the amount asked for, and the parchment bears the string asked for. */
    @Override public boolean matches(BrassCookingInput input, Level level) {
        return fluid.map(wanted -> wanted.test(input.fluid())).orElse(true)
                && BrassCauldronRules.inscribed(inscription.map(ArcaneApi::id).orElse(null), input.inscription() == null ? null : ArcaneApi.id(input.inscription()))
                && slots(input) != null;
    }

    @Override public ItemStack assemble(BrassCookingInput input, HolderLookup.Provider registries) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    @Override public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (SizedIngredient ingredient : ingredients) list.add(ingredient.ingredient());
        return list;
    }
    /** Not shown in the recipe book, which has no page for it; JEI shows it instead. */
    @Override public boolean isSpecial() { return true; }
    @Override public RecipeSerializer<?> getSerializer() { return ModRecipes.BRASS_COOKING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return ModRecipes.BRASS_COOKING.get(); }

    public static final class Serializer implements RecipeSerializer<BrassCookingRecipe> {
        @Override public MapCodec<BrassCookingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, BrassCookingRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
