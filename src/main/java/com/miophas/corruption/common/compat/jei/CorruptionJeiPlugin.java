package com.miophas.corruption.common.compat.jei;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.alchemy.AlchemyRecipe;
import com.miophas.corruption.common.alchemy.CorruptionBenchMenu;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.common.registry.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * Shows alchemy recipes in JEI. JEI finds this class by its annotation and nothing else refers to it, so
 * the mod still loads when JEI is not installed.
 */
@JeiPlugin
public final class CorruptionJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "jei");

    @Override public ResourceLocation getPluginUid() { return UID; }

    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AlchemyCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level != null) registration.addRecipes(AlchemyCategory.TYPE, level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMY.get()));
    }

    /** The bench appears beside the category, and clicking it in JEI lists every alchemy recipe. */
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(AlchemyCategory.TYPE, ModBlocks.CORRUPTION_BENCH.get());
    }

    /** JEI's "+" button: fills the five inputs from the player's inventory, honouring each slot's amount. */
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CorruptionBenchMenu.class, ModMenus.CORRUPTION_BENCH.get(), AlchemyCategory.TYPE,
                0, AlchemyRecipe.SLOTS, CorruptionBenchMenu.FIRST_PLAYER_SLOT, CorruptionBenchMenu.END_PLAYER_SLOT - CorruptionBenchMenu.FIRST_PLAYER_SLOT);
    }
}
