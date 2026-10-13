package com.miophas.corruption.common.compat.jei;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.alchemy.AlchemyRecipe;
import com.miophas.corruption.common.alchemy.CorruptionBenchMenu;
import com.miophas.corruption.common.alchemy.ShapedAlchemyRecipe;
import com.miophas.corruption.common.alchemy.ShapelessAlchemyRecipe;
import com.miophas.corruption.common.registry.ModBlocks;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

/**
 * Shows an alchemy recipe on the bench's own pentagram: ingredients on the points with their amounts,
 * the result in the centre. Shaped recipes keep each ingredient on its slot; shapeless ones fill the
 * points in order and carry JEI's shapeless mark.
 */
public final class AlchemyCategory implements IRecipeCategory<RecipeHolder<AlchemyRecipe>> {
    public static final RecipeType<RecipeHolder<AlchemyRecipe>> TYPE =
            RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "alchemy"));
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/gui_corruption_bench.png");
    /** The part of gui_corruption_bench.png that holds the pentagram; slot positions are the menu's, shifted by its corner. */
    private static final int LEFT = 22, TOP = 27, SIZE = 135;

    private final IDrawable background, icon;

    public AlchemyCategory(IGuiHelper gui) {
        background = gui.createDrawable(TEXTURE, LEFT, TOP, SIZE, SIZE);
        icon = gui.createDrawableItemLike(ModBlocks.CORRUPTION_BENCH.get());
    }

    @Override public RecipeType<RecipeHolder<AlchemyRecipe>> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("corruption.jei.alchemy"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return SIZE; }
    @Override public int getHeight() { return SIZE; }

    private static IRecipeSlotBuilder input(IRecipeLayoutBuilder builder, int slot) {
        return builder.addInputSlot(CorruptionBenchMenu.INPUT_POSITIONS[slot][0] - LEFT, CorruptionBenchMenu.INPUT_POSITIONS[slot][1] - TOP);
    }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlchemyRecipe> holder, IFocusGroup focuses) {
        AlchemyRecipe recipe = holder.value();
        if (recipe instanceof ShapedAlchemyRecipe shaped) {
            // All five slots are added, empty or not: JEI fills the bench by slot order when it moves items.
            for (int slot = 0; slot < AlchemyRecipe.SLOTS; slot++) {
                IRecipeSlotBuilder point = input(builder, slot);
                SizedIngredient wanted = shaped.slots().get(slot);
                if (wanted != null) point.addItemStacks(List.of(wanted.getItems()));
            }
        } else if (recipe instanceof ShapelessAlchemyRecipe shapeless) {
            for (int index = 0; index < shapeless.ingredients().size(); index++) input(builder, index).addItemStacks(List.of(shapeless.ingredients().get(index).getItems()));
            builder.setShapeless(SIZE - 14, 2);
        }
        var level = Minecraft.getInstance().level;
        builder.addOutputSlot(CorruptionBenchMenu.RESULT_X - LEFT, CorruptionBenchMenu.RESULT_Y - TOP)
                .addItemStack(recipe.getResultItem(level == null ? null : level.registryAccess()));
    }

    @Override public void draw(RecipeHolder<AlchemyRecipe> recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }
}
