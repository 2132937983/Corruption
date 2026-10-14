package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.alchemy.AlchemyRecipe;
import com.miophas.corruption.common.alchemy.ShapedAlchemyRecipe;
import com.miophas.corruption.common.alchemy.ShapelessAlchemyRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Recipe kinds. Alchemy recipes are data files whose "type" is "corruption:alchemy_shaped" or
 * "corruption:alchemy_shapeless"; like the two kinds of crafting, both belong to one recipe type.
 */
public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Corruption.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Corruption.MOD_ID);
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemyRecipe>> ALCHEMY = TYPES.register("alchemy",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "alchemy")));
    public static final DeferredHolder<RecipeSerializer<?>, ShapedAlchemyRecipe.Serializer> ALCHEMY_SHAPED = SERIALIZERS.register("alchemy_shaped", ShapedAlchemyRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, ShapelessAlchemyRecipe.Serializer> ALCHEMY_SHAPELESS = SERIALIZERS.register("alchemy_shapeless", ShapelessAlchemyRecipe.Serializer::new);
    /** Corrupted Arcane Transmutation, done in the arcane transmuter: data files whose "type" is "corruption:arcane_transmutation". */
    public static final DeferredHolder<RecipeType<?>, RecipeType<com.miophas.corruption.common.transmutation.ArcaneTransmutationRecipe>> ARCANE_TRANSMUTATION = TYPES.register("arcane_transmutation",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "arcane_transmutation")));
    public static final DeferredHolder<RecipeSerializer<?>, com.miophas.corruption.common.transmutation.ArcaneTransmutationRecipe.Serializer> ARCANE_TRANSMUTATION_SERIALIZER =
            SERIALIZERS.register("arcane_transmutation", com.miophas.corruption.common.transmutation.ArcaneTransmutationRecipe.Serializer::new);
    /** Brass cauldron cooking: data files whose "type" is "corruption:brass_cooking". */
    public static final DeferredHolder<RecipeType<?>, RecipeType<com.miophas.corruption.common.cooking.BrassCookingRecipe>> BRASS_COOKING = TYPES.register("brass_cooking",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "brass_cooking")));
    public static final DeferredHolder<RecipeSerializer<?>, com.miophas.corruption.common.cooking.BrassCookingRecipe.Serializer> BRASS_COOKING_SERIALIZER =
            SERIALIZERS.register("brass_cooking", com.miophas.corruption.common.cooking.BrassCookingRecipe.Serializer::new);
    /** Altar rites: data files whose "type" is "corruption:altar_shaped" or "corruption:altar_shapeless"; both belong to the one recipe type. */
    public static final DeferredHolder<RecipeType<?>, RecipeType<com.miophas.corruption.common.altar.AltarRecipe>> ALTAR = TYPES.register("altar",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "altar")));
    public static final DeferredHolder<RecipeSerializer<?>, com.miophas.corruption.common.altar.AltarRecipe.Serializer> ALTAR_SHAPED =
            SERIALIZERS.register("altar_shaped", () -> new com.miophas.corruption.common.altar.AltarRecipe.Serializer(true));
    public static final DeferredHolder<RecipeSerializer<?>, com.miophas.corruption.common.altar.AltarRecipe.Serializer> ALTAR_SHAPELESS =
            SERIALIZERS.register("altar_shapeless", () -> new com.miophas.corruption.common.altar.AltarRecipe.Serializer(false));

    private ModRecipes() {}
}
