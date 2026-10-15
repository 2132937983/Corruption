package com.miophas.corruption.common.compat.jei;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneSpell;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.SpellRule;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

/**
 * What the arcane pedestal can be taught: every spell the data holds, shown as the parchment bearing its string
 * going in and a wand coming out, with what casting it costs. A spell with no parchment of its own cannot be
 * learned; how it is written is the arcane_spell registry, not a recipe.
 */
public final class ArcaneSpellsCategory implements IRecipeCategory<ArcaneSpellsCategory.Entry> {
    public record Entry(ResourceKey<ArcaneSpell> id, ArcaneSpell spell) {}

    public static final RecipeType<Entry> TYPE = RecipeType.create(Corruption.MOD_ID, "arcane_spell", Entry.class);
    private static final int WIDTH = 132, HEIGHT = 44;
    private static final int INPUT_X = 1, OUTPUT_X = 111, SLOT_Y = 12;
    private static final int TEXT_X = 22, LINE = 10;

    private final IDrawable icon;

    public ArcaneSpellsCategory(IGuiHelper gui) { icon = gui.createDrawableItemLike(ModItems.CORRUPTION_ARCANE_STICK.get()); }

    /** Every spell the data holds, as the server sent them. */
    public static List<Entry> entries() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return List.of();
        List<Entry> entries = new ArrayList<>();
        minecraft.level.registryAccess().lookup(ArcaneSpell.REGISTRY).ifPresent(spells ->
                spells.listElements().forEach(holder -> entries.add(new Entry(holder.key(), holder.value()))));
        return entries;
    }

    @Override public RecipeType<Entry> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("corruption.jei.arcane_spell"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }

    /** Parchment bearing the string going in; a wand comes out of the pedestal, taught. */
    @Override public void setRecipe(IRecipeLayoutBuilder builder, Entry entry, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                .addItemStack(ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), ArcaneApi.key(entry.id().location().toString())));
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y).addItemStack(new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get()));
    }

    /** What casting the spell costs, and what it asks of the caster, as far as the data says. */
    @Override public void draw(Entry entry, IRecipeSlotsView slots, GuiGraphics gui, double mouseX, double mouseY) {
        SpellRule rule = entry.spell().rule();
        var font = Minecraft.getInstance().font;
        int y = 3;
        gui.drawString(font, Component.translatable("corruption.jei.spell_energy", CorruptionEnergyApi.format(rule.energy())), TEXT_X, y, 0xFF404040, false);
        if (rule.cooldown() > 0) gui.drawString(font, Component.translatable("corruption.jei.spell_cooldown", rule.cooldown()), TEXT_X, y += LINE, 0xFF808080, false);
        if (rule.minCorruption() > 0) gui.drawString(font, Component.translatable("corruption.jei.spell_corruption", rule.minCorruption()), TEXT_X, y += LINE, 0xFF808080, false);
        if (rule.corrupts()) gui.drawString(font, Component.translatable("corruption.jei.spell_corruption_gain", rule.corruptionGain()), TEXT_X, y + LINE, 0xFF808080, false);
    }
}
