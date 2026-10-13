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

    private ModRecipes() {}
}
