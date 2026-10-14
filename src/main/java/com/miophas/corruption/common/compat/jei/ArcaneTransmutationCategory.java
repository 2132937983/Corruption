package com.miophas.corruption.common.compat.jei;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.common.client.ArcaneTransmuterScreen;
import com.miophas.corruption.common.client.GuiTiles;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.transmutation.ArcaneTransmutationRecipe;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
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

/**
 * Shows a Corrupted Arcane Transmutation as the transmuter does: the input, the progress bar running for the
 * transmutation's fixed time, the result, and under them the share of a full store of Corruption Energy it
 * costs, with the cost and time written out.
 */
public final class ArcaneTransmutationCategory implements IRecipeCategory<RecipeHolder<ArcaneTransmutationRecipe>> {
    public static final RecipeType<RecipeHolder<ArcaneTransmutationRecipe>> TYPE =
            RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "arcane_transmutation"));
    private static final int WIDTH = 104, HEIGHT = 52, INPUT_X = 9, OUTPUT_X = 66, BAR_X = 18, BAR_Y = 32;

    private final IDrawable icon, slot, output;

    public ArcaneTransmutationCategory(IGuiHelper gui) {
        icon = gui.createDrawableItemLike(ModBlocks.CORRUPTION_ARCANE_TRANSMUTER.get());
        slot = gui.getSlotDrawable();
        output = gui.getOutputSlot();
    }

    @Override public RecipeType<RecipeHolder<ArcaneTransmutationRecipe>> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("corruption.jei.arcane_transmutation"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ArcaneTransmutationRecipe> holder, IFocusGroup focuses) {
        ArcaneTransmutationRecipe recipe = holder.value();
        builder.addInputSlot(INPUT_X + 1, 5).addItemStacks(List.of(recipe.ingredient().getItems()));
        builder.addOutputSlot(OUTPUT_X + 5, 5).addItemStack(recipe.result());
    }

    @Override public void draw(RecipeHolder<ArcaneTransmutationRecipe> holder, IRecipeSlotsView slots, GuiGraphics gui, double mouseX, double mouseY) {
        long cost = holder.value().energy();
        int ticks = CorruptionEnergyApi.transmuteTicks();
        slot.draw(gui, INPUT_X, 4);
        output.draw(gui, OUTPUT_X, 0);
        GuiTiles.GUI.bar(gui, ArcaneTransmuterScreen.PROGRESS, 36, 5, (System.currentTimeMillis() / 50 % ticks) / (float) ticks, GuiTiles.Fill.LEFT_TO_RIGHT);
        GuiTiles.GUI.bar(gui, ArcaneTransmuterScreen.ENERGY, BAR_X, BAR_Y, CorruptionEnergyApi.fraction(cost, CorruptionEnergyApi.transmuterCapacity()), GuiTiles.Fill.LEFT_TO_RIGHT);
        Component line = Component.translatable("corruption.jei.cost_and_time", CorruptionEnergyApi.format(cost), ticks / 20F);
        var font = Minecraft.getInstance().font;
        gui.drawString(font, line, (WIDTH - font.width(line)) / 2, BAR_Y + 10, 0xFF808080, false);
    }
}
