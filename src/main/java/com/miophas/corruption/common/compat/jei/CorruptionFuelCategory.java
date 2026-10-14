package com.miophas.corruption.common.compat.jei;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.common.client.ArcaneTransmuterScreen;
import com.miophas.corruption.common.client.GuiTiles;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.api.energy.CorruptionFuel;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * What the arcane transmuter can take energy from in its fuel slot: each fuel with what it yields and how long
 * it takes, and each item that carries energy, full, with how long the transmuter takes to burn it down.
 */
public final class CorruptionFuelCategory implements IRecipeCategory<CorruptionFuelCategory.Entry> {
    public record Entry(ItemStack item, CorruptionFuel fuel) {}
    public static final RecipeType<Entry> TYPE = RecipeType.create(Corruption.MOD_ID, "corruption_fuel", Entry.class);
    private static final int WIDTH = 104, HEIGHT = 30;

    private final IDrawable icon, slot;

    public CorruptionFuelCategory(IGuiHelper gui) {
        icon = gui.createDrawableItemLike(ModBlocks.CORRUPTION_ARCANE_TRANSMUTER.get());
        slot = gui.getSlotDrawable();
    }

    /** Every item the corruption_fuel or corruption_energy_carrier data map, as the server sent them, gives a value. */
    public static List<Entry> entries() {
        List<Entry> entries = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(item);
            CorruptionFuel fuel = CorruptionFuel.of(stack);
            CorruptionEnergyCarrier carrier = CorruptionEnergyCarrier.of(stack);
            if (fuel != null) entries.add(new Entry(stack, fuel));
            else if (carrier != null && carrier.capacity() > 0)
                entries.add(new Entry(CorruptionEnergyCarrier.charged(stack, carrier.capacity()), new CorruptionFuel(carrier.capacity(), CorruptionEnergyApi.steps(carrier.capacity(), CorruptionEnergyApi.step(stack)))));
        }
        return entries;
    }

    @Override public RecipeType<Entry> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("corruption.jei.corruption_fuel"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, Entry entry, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 7).addItemStack(entry.item());
    }

    @Override public void draw(Entry entry, IRecipeSlotsView slots, GuiGraphics gui, double mouseX, double mouseY) {
        int ticks = entry.fuel().ticks();
        slot.draw(gui, 0, 6);
        GuiTiles.GUI.bar(gui, ArcaneTransmuterScreen.LOADING, 23, 8, (System.currentTimeMillis() / 50 % ticks) / (float) ticks, GuiTiles.Fill.BOTTOM_TO_TOP);
        var font = Minecraft.getInstance().font;
        gui.drawString(font, CorruptionEnergyApi.format(entry.fuel().energy()), 42, 6, 0xFF404040, false);
        gui.drawString(font, Component.translatable("corruption.jei.ticks", ticks), 42, 17, 0xFF808080, false);
    }
}
