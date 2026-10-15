package com.miophas.corruption.common.compat.jei;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneSpell;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.api.arcane.SpellText;
import com.miophas.corruption.api.arcane.WandSpells;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.Grimoire;
import com.miophas.corruption.core.arcane.SpellRule;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

/**
 * What the arcane pedestal can be taught: every spell the data holds, as the parchment bearing its string going
 * in and a wand coming out, taught it (each wand that holds enough to cast it, in turn); before the parchment, the book whose key reveals the string, where it is
 * copied out at the lectern (so looking up a book's uses lists its spells); between them, the spell's name; under them, what casting it costs and asks
 * of the caster; and under that, what it does, line by line, read out as the wand's tooltip reads it (see
 * {@link SpellText}). Hovering over the parchment or the wand tells what it does too, and so does hovering over
 * the lines, in full, when there are more of them than fit or they are too long. A spell with no parchment of its
 * own cannot be learned; how it is written is the arcane_spell registry, not a recipe.
 */
public final class ArcaneSpellsCategory implements IRecipeCategory<ArcaneSpellsCategory.Entry> {
    public record Entry(ResourceKey<ArcaneSpell> id, ArcaneSpell spell) {}

    public static final RecipeType<Entry> TYPE = RecipeType.create(Corruption.MOD_ID, "arcane_spell", Entry.class);
    private static final int WIDTH = 168, HEIGHT = 120;
    private static final int KEY_X = 1, INPUT_X = 19, OUTPUT_X = WIDTH - 17, SLOT_Y = 1;
    /** Where the name stands, the costs under it, the line under those, and the spell's lines under that. */
    private static final int NAME_Y = 6, COSTS_Y = 23, RULE_Y = 44, EFFECTS_Y = 48, LINE = 10, EFFECT_LINES = 6;
    private static final int NAME = 0xFF5E2F52, TEXT = 0xFF404040, QUIET = 0xFF808080, RULE = 0xFFA898B0;

    private final IDrawable icon;

    public ArcaneSpellsCategory(IGuiHelper gui) { icon = gui.createDrawableItemLike(ModItems.CORRUPTION_ARCANE_STICK.get()); }

    /** The keys that reveal a spell's string: the books it is copied out of. */
    public static List<ItemStack> keys(Entry entry) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? List.of() : ArcaneApi.keysTo(minecraft.level.registryAccess(), ArcaneApi.key(entry.id().location().toString()));
    }

    /** Every spell the data holds, as the server sent them. */
    public static List<Entry> entries() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return List.of();
        List<Entry> entries = new ArrayList<>();
        minecraft.level.registryAccess().lookup(ArcaneSpell.REGISTRY).ifPresent(spells ->
                spells.listElements().forEach(holder -> entries.add(new Entry(holder.key(), holder.value()))));
        return entries;
    }

    /** The parchment bearing a spell's string, which is how a spell is carried and taught. */
    public static ItemStack parchment(Entry entry) {
        return ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), ArcaneApi.key(entry.id().location().toString()));
    }

    /** A spell's name: what its string means, if the arcane data says, else its id. */
    public static Component name(Entry entry) {
        Minecraft minecraft = Minecraft.getInstance();
        ArcaneString string = minecraft.level == null ? null : ArcaneApi.string(minecraft.level.registryAccess(), ArcaneApi.key(entry.id().location().toString()));
        return string == null ? Component.literal(entry.id().location().toString()) : string.meaning();
    }

    @Override public RecipeType<Entry> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("corruption.jei.arcane_spell"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }

    /**
     * The book the string is found in; parchment bearing the string going in; a wand comes out of the pedestal,
     * taught the spell. The parchment or the wand, hovered, tells what it does.
     */
    @Override public void setRecipe(IRecipeLayoutBuilder builder, Entry entry, IFocusGroup focuses) {
        List<Component> effects = effects(entry);
        List<ItemStack> keys = keys(entry);
        if (!keys.isEmpty()) builder.addSlot(RecipeIngredientRole.CATALYST, KEY_X, SLOT_Y).setStandardSlotBackground().addItemStacks(keys)
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("corruption.jei.spell_key").withStyle(ChatFormatting.GRAY)));
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y).setStandardSlotBackground().addItemStack(parchment(entry))
                .addRichTooltipCallback((view, tooltip) -> tooltip.addAll(effects));
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y).setStandardSlotBackground().addItemStacks(taught(entry))
                .addRichTooltipCallback((view, tooltip) -> tooltip.addAll(effects));
    }

    /**
     * Each wand that can cast the spell, taught it: those that hold as much as a cast costs, the lowest tier first.
     * A spell no wand of the mod's can hold is shown on them all.
     */
    private static List<ItemStack> taught(Entry entry) {
        long cost = entry.spell().rule().energy();
        List<ItemStack> wands = new ArrayList<>();
        for (var wand : ModItems.WANDS) if (wand.get().tool().capacity() >= cost) wands.add(new ItemStack(wand.get()));
        if (wands.isEmpty()) for (var wand : ModItems.WANDS) wands.add(new ItemStack(wand.get()));
        for (ItemStack wand : wands) WandSpells.set(wand, Grimoire.EMPTY.inscribed(entry.id().location().toString()));
        return wands;
    }

    /** What the spell does, as a tooltip tells it: a heading, then its lines. */
    private static List<Component> effects(Entry entry) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("tooltip.corruption.wand_effects").withStyle(ChatFormatting.DARK_PURPLE));
        lines.addAll(SpellText.lines(entry.spell().rule()));
        return lines;
    }

    /** The name, what casting costs and asks, and what the spell does, as far as there is room. */
    @Override public void draw(Entry entry, IRecipeSlotsView slots, GuiGraphics gui, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        SpellRule rule = entry.spell().rule();
        int nameLeft = INPUT_X + 22, nameWidth = OUTPUT_X - 4 - nameLeft;
        FormattedCharSequence name = fit(font, name(entry), nameWidth);
        gui.drawString(font, name, nameLeft + (nameWidth - font.width(name)) / 2, NAME_Y, NAME, false);

        int half = WIDTH / 2;
        gui.drawString(font, Component.translatable("corruption.jei.spell_energy", CorruptionEnergyApi.format(rule.energy())), 2, COSTS_Y, TEXT, false);
        if (rule.cooldown() > 0) gui.drawString(font, Component.translatable("corruption.jei.spell_cooldown", rule.cooldown()), half, COSTS_Y, QUIET, false);
        if (rule.minCorruption() > 0) gui.drawString(font, Component.translatable("corruption.jei.spell_corruption", rule.minCorruption()), 2, COSTS_Y + LINE, QUIET, false);
        if (rule.corrupts()) gui.drawString(font, Component.translatable("corruption.jei.spell_corruption_gain", rule.corruptionGain()), half, COSTS_Y + LINE, QUIET, false);
        gui.fill(0, RULE_Y, WIDTH, RULE_Y + 1, RULE);

        List<Component> lines = SpellText.lines(rule);
        boolean more = lines.size() > EFFECT_LINES;
        int shown = more ? EFFECT_LINES - 1 : lines.size();
        for (int line = 0; line < shown; line++)
            gui.drawString(font, fit(font, Component.literal(lines.get(line).getString()), WIDTH - 4), 2, EFFECTS_Y + 2 + line * LINE, TEXT, false);
        if (more) gui.drawString(font, Component.translatable("corruption.jei.spell_more", lines.size() - shown), 2, EFFECTS_Y + 2 + shown * LINE, QUIET, false);
    }

    /** Over the spell's lines, all of them in full, when any is cut short or some are left out. */
    @Override public void getTooltip(ITooltipBuilder tooltip, Entry entry, IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (mouseY < EFFECTS_Y || mouseY >= HEIGHT || mouseX < 0 || mouseX >= WIDTH) return;
        Font font = Minecraft.getInstance().font;
        List<Component> lines = SpellText.lines(entry.spell().rule());
        boolean cut = lines.size() > EFFECT_LINES || lines.stream().anyMatch(line -> font.width(line) > WIDTH - 4);
        if (cut) tooltip.addAll(effects(entry));
    }

    /** Text cut to a width, ending in dots where it was cut. */
    private static FormattedCharSequence fit(Font font, Component text, int width) {
        if (font.width(text) <= width) return text.getVisualOrderText();
        FormattedText cut = font.substrByWidth(text, width - font.width("..."));
        return Language.getInstance().getVisualOrder(FormattedText.composite(cut, FormattedText.of("...")));
    }
}
