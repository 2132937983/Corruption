package com.miophas.corruption.common.compat.jei;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.alchemy.AlchemyRecipe;
import com.miophas.corruption.common.alchemy.CorruptionBenchMenu;
import com.miophas.corruption.common.client.ArcaneTransmuterScreen;
import com.miophas.corruption.common.client.BrassCauldronScreen;
import com.miophas.corruption.common.cooking.BrassCauldronBlockEntity;
import com.miophas.corruption.common.cooking.BrassCauldronMenu;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.common.registry.ModRecipes;
import com.miophas.corruption.common.transmutation.ArcaneTransmuterBlockEntity;
import com.miophas.corruption.common.transmutation.ArcaneTransmuterMenu;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Shows alchemy, Corrupted Arcane Transmutation, brass cauldron cooking, altar rites, the corruption's spells and the fuels that power them in JEI. JEI finds this class by
 * its annotation and nothing else refers to it, so the mod still loads when JEI is not installed.
 */
@JeiPlugin
public final class CorruptionJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "jei");

    @Override public ResourceLocation getPluginUid() { return UID; }

    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AlchemyCategory(gui), new ArcaneTransmutationCategory(gui), new BrassCookingCategory(gui), new AltarRiteCategory(gui),
                new ArcaneSpellsCategory(gui), new CorruptionFuelCategory(gui));
    }

    @Override public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            registration.addRecipes(AlchemyCategory.TYPE, level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMY.get()));
            registration.addRecipes(ArcaneTransmutationCategory.TYPE, level.getRecipeManager().getAllRecipesFor(ModRecipes.ARCANE_TRANSMUTATION.get()));
            registration.addRecipes(BrassCookingCategory.TYPE, level.getRecipeManager().getAllRecipesFor(ModRecipes.BRASS_COOKING.get()));
            registration.addRecipes(AltarRiteCategory.TYPE, level.getRecipeManager().getAllRecipesFor(ModRecipes.ALTAR.get()));
        }
        registration.addRecipes(ArcaneSpellsCategory.TYPE, ArcaneSpellsCategory.entries());
        registration.addRecipes(CorruptionFuelCategory.TYPE, CorruptionFuelCategory.entries());
    }

    /** Each machine appears beside its categories, and clicking it in JEI lists what it does. */
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(AlchemyCategory.TYPE, ModBlocks.CORRUPTION_BENCH.get());
        registration.addRecipeCatalysts(ArcaneTransmutationCategory.TYPE, ModBlocks.CORRUPTION_ARCANE_TRANSMUTER.get());
        registration.addRecipeCatalysts(BrassCookingCategory.TYPE, ModBlocks.BRASS_CAULDRON.get());
        registration.addRecipeCatalysts(AltarRiteCategory.TYPE, ModBlocks.ALTAR_OFFERING.get(), ModBlocks.ALTAR_PILLAR.get());
        registration.addRecipeCatalysts(ArcaneSpellsCategory.TYPE, ModBlocks.CORRUPTION_ARCANE_PEDESTAL.get(), ModItems.CORRUPTION_ARCANE_STICK.get());
        registration.addRecipeCatalysts(CorruptionFuelCategory.TYPE, ModBlocks.CORRUPTION_ARCANE_TRANSMUTER.get(), ModBlocks.BRASS_CAULDRON.get());
    }

    /** JEI's "+" button: fills the bench's five inputs, honouring each slot's amount, the transmuter's input, or the cauldron's seven. */
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CorruptionBenchMenu.class, ModMenus.CORRUPTION_BENCH.get(), AlchemyCategory.TYPE,
                0, AlchemyRecipe.SLOTS, CorruptionBenchMenu.FIRST_PLAYER_SLOT, CorruptionBenchMenu.END_PLAYER_SLOT - CorruptionBenchMenu.FIRST_PLAYER_SLOT);
        registration.addRecipeTransferHandler(ArcaneTransmuterMenu.class, ModMenus.ARCANE_TRANSMUTER.get(), ArcaneTransmutationCategory.TYPE,
                ArcaneTransmuterBlockEntity.INPUT, 1, ArcaneTransmuterMenu.FIRST_PLAYER_SLOT, ArcaneTransmuterMenu.END_PLAYER_SLOT - ArcaneTransmuterMenu.FIRST_PLAYER_SLOT);
        registration.addRecipeTransferHandler(BrassCauldronMenu.class, ModMenus.BRASS_CAULDRON.get(), BrassCookingCategory.TYPE,
                0, BrassCauldronBlockEntity.INPUTS, BrassCauldronMenu.FIRST_PLAYER_SLOT, BrassCauldronMenu.END_PLAYER_SLOT - BrassCauldronMenu.FIRST_PLAYER_SLOT);
    }

    /**
     * Clicking the transmuter's or the cauldron's progress bar shows what it makes, and its loading bar its
     * fuels, as the furnace's arrow does. JEI draws a tooltip over a clickable area, so these areas carry the screen's own
     * lines for the bar as well, and the screen stops drawing its own there: one tooltip, not two on top of
     * each other.
     */
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ArcaneTransmuterScreen.class, new IGuiContainerHandler<ArcaneTransmuterScreen>() {
            @Override public Collection<IGuiClickableArea> getGuiClickableAreas(ArcaneTransmuterScreen screen, double mouseX, double mouseY) {
                return List.of(area(screen, ArcaneTransmuterScreen.PROGRESS, ArcaneTransmutationCategory.TYPE), area(screen, ArcaneTransmuterScreen.LOADING, CorruptionFuelCategory.TYPE));
            }
        });
        registration.addGuiContainerHandler(BrassCauldronScreen.class, new IGuiContainerHandler<BrassCauldronScreen>() {
            @Override public Collection<IGuiClickableArea> getGuiClickableAreas(BrassCauldronScreen screen, double mouseX, double mouseY) {
                return List.of(area(BrassCauldronScreen.area(BrassCauldronScreen.PROGRESS), screen.barTooltip(BrassCauldronScreen.PROGRESS), BrassCookingCategory.TYPE),
                        area(BrassCauldronScreen.area(BrassCauldronScreen.LOADING), screen.barTooltip(BrassCauldronScreen.LOADING), CorruptionFuelCategory.TYPE));
            }
        });
        ArcaneTransmuterScreen.explainBarsInRecipeViewer();
    }

    private static IGuiClickableArea area(ArcaneTransmuterScreen screen, String bar, RecipeType<?> type) {
        return area(ArcaneTransmuterScreen.area(bar), screen.barTooltip(bar), type);
    }

    /** A clickable area showing a category, its tooltip carrying the bar's own lines too. */
    private static IGuiClickableArea area(int[] area, List<Component> lines, RecipeType<?> type) {
        return new IGuiClickableArea() {
            @Override public Rect2i getArea() { return new Rect2i(area[0], area[1], area[2], area[3]); }
            @Override public void getTooltip(ITooltipBuilder tooltip) {
                tooltip.addAll(lines);
                tooltip.add(Component.translatable("jei.tooltip.show.recipes"));
            }
            @Override public void onClick(IFocusFactory focuses, IRecipesGui recipes) { recipes.showTypes(List.of(type)); }
        };
    }
}
