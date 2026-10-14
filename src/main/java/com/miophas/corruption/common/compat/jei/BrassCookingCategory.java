package com.miophas.corruption.common.compat.jei;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.common.client.ArcaneTransmuterScreen;
import com.miophas.corruption.common.client.GuiTiles;
import com.miophas.corruption.common.cooking.BrassCauldronMenu;
import com.miophas.corruption.common.cooking.BrassCookingRecipe;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Shows a brass cauldron cooking as the cauldron does: the fluid it wants on the left, its ingredients on the
 * hexagon's slots (the middle first, then the corners), the inscribed parchment it asks for, if any, under the
 * hexagon (a catalyst, as it is never used up), the progress bar running for its time, the dish, and under
 * them the energy it costs and how long it cooks.
 */
public final class BrassCookingCategory implements IRecipeCategory<RecipeHolder<BrassCookingRecipe>> {
    public static final RecipeType<RecipeHolder<BrassCookingRecipe>> TYPE =
            RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "brass_cooking"));
    private static final int WIDTH = 150, HEIGHT = 92;
    /** The hexagon's middle; its slots lie as the cauldron's do around it. */
    private static final int MIDDLE_X = 48, MIDDLE_Y = 26, TANK_X = 4, TANK_Y = 4, TANK_HEIGHT = 64, ARROW_X = 90, ARROW_Y = 26, OUTPUT_X = 118, OUTPUT_Y = 21;

    private final IDrawable icon, slot, output;

    public BrassCookingCategory(IGuiHelper gui) {
        icon = gui.createDrawableItemLike(ModBlocks.BRASS_CAULDRON.get());
        slot = gui.getSlotDrawable();
        output = gui.getOutputSlot();
    }

    /** Where the ingredient on this slot of the hexagon goes, in the category's own pixels. */
    private static int[] slotAt(int index) {
        int[] middle = BrassCauldronMenu.INPUT_SLOTS[0], at = BrassCauldronMenu.INPUT_SLOTS[index];
        return new int[]{MIDDLE_X + at[0] - middle[0], MIDDLE_Y + at[1] - middle[1]};
    }

    @Override public RecipeType<RecipeHolder<BrassCookingRecipe>> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("corruption.jei.brass_cooking"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BrassCookingRecipe> holder, IFocusGroup focuses) {
        BrassCookingRecipe recipe = holder.value();
        for (int index = 0; index < recipe.ingredients().size(); index++) {
            int[] at = slotAt(index);
            builder.addInputSlot(at[0], at[1]).addItemStacks(List.of(recipe.ingredients().get(index).getItems()));
        }
        recipe.fluid().ifPresent(fluid -> {
            IRecipeSlotBuilder tank = builder.addSlot(RecipeIngredientRole.INPUT, TANK_X, TANK_Y).setFluidRenderer(fluid.amount(), false, 16, TANK_HEIGHT);
            for (FluidStack stack : fluid.getFluids()) tank.addFluidStack(stack.getFluid(), fluid.amount(), stack.getComponentsPatch());
        });
        recipe.inscription().ifPresent(string -> {
            int[] at = parchmentAt();
            builder.addSlot(RecipeIngredientRole.CATALYST, at[0], at[1]).addItemStack(ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), string))
                    .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("corruption.jei.parchment_kept").withStyle(ChatFormatting.GRAY)));
        });
        builder.addOutputSlot(OUTPUT_X + 5, OUTPUT_Y + 5).addItemStack(recipe.result());
    }

    /** Where the parchment goes: under the hexagon on the right, as in the cauldron. */
    private static int[] parchmentAt() {
        int[] middle = BrassCauldronMenu.INPUT_SLOTS[0];
        return new int[]{MIDDLE_X + BrassCauldronMenu.PARCHMENT_X - middle[0], MIDDLE_Y + BrassCauldronMenu.PARCHMENT_Y - middle[1]};
    }

    @Override public void draw(RecipeHolder<BrassCookingRecipe> holder, IRecipeSlotsView slots, GuiGraphics gui, double mouseX, double mouseY) {
        BrassCookingRecipe recipe = holder.value();
        for (int index = 0; index < BrassCauldronMenu.INPUT_SLOTS.length; index++) {
            int[] at = slotAt(index);
            slot.draw(gui, at[0] - 1, at[1] - 1);
        }
        if (recipe.inscription().isPresent()) slot.draw(gui, parchmentAt()[0] - 1, parchmentAt()[1] - 1);
        gui.fill(TANK_X - 1, TANK_Y - 1, TANK_X + 17, TANK_Y + TANK_HEIGHT + 1, 0xFF373737);
        gui.fill(TANK_X, TANK_Y, TANK_X + 16, TANK_Y + TANK_HEIGHT, 0xFF8B8B8B);
        output.draw(gui, OUTPUT_X, OUTPUT_Y);
        GuiTiles.GUI.bar(gui, ArcaneTransmuterScreen.PROGRESS, ARROW_X, ARROW_Y, (System.currentTimeMillis() / 50 % recipe.time()) / (float) recipe.time(), GuiTiles.Fill.LEFT_TO_RIGHT);
        Component line = recipe.energy() > 0
                ? Component.translatable("corruption.jei.cost_and_time", CorruptionEnergyApi.format(recipe.energy()), recipe.time() / 20F)
                : Component.translatable("corruption.jei.time", recipe.time() / 20F);
        var font = Minecraft.getInstance().font;
        gui.drawString(font, line, OUTPUT_X + 13 - font.width(line) / 2, OUTPUT_Y + 32, 0xFF808080, false);
    }
}
