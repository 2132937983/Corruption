package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.common.arcane.ArcanePedestalMenu;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.core.arcane.CastingUnit;
import com.miophas.corruption.core.arcane.Grimoire;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * The arcane pedestal's screen, gui_corruption_arcane_pedestal.png. The frame in the middle shows the slots of
 * the wand on the left, as many as it has room for: a written one as the parchment bearing its spell, the one
 * the wand casts underlined in gold, an empty one by its number. Click a slot to write into it (the first free
 * one is chosen to begin with; a written one is written over), and Inscribe writes the parchment's spell there.
 * The line over the button says what will happen, or why nothing can.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class ArcanePedestalScreen extends AbstractContainerScreen<ArcanePedestalMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/gui_corruption_arcane_pedestal.png");
    /** The Inscribe button, under the frame; its three looks are drawn below the panel in the texture. */
    public static final int BUTTON_X = 29, BUTTON_Y = 96, BUTTON_WIDTH = 120, BUTTON_HEIGHT = 14, BUTTON_U = 0, BUTTON_V = 212;
    /** The line saying what Inscribe will do, between the frame and the button. */
    public static final int STATUS_X = 31, STATUS_Y = 85, STATUS_WIDTH = 118;
    private static final int LABEL_COLOUR = 0xE3D3EA, QUIET_COLOUR = 0x9C8FA6, NUMBER_COLOUR = 0xFF6E6278, FAR_NUMBER_COLOUR = 0xFF3E3646;
    private static final int PLACE_DARK = 0xFF0B080D, PLACE_LIGHT = 0xFF4A3F52, PLACE_FILL = 0xFF221A28, TARGET = 0xFFB05A77, CHOSEN = 0xFFD8A936, HOVER = 0x30FFFFFF;

    /** The slot chosen to write into, or -1 for the wand's first free one. */
    private int target = -1;
    private ItemStack shownWand = ItemStack.EMPTY;

    public ArcanePedestalScreen(ArcanePedestalMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 210;
        inventoryLabelY = imageHeight - 94;
    }

    /** The slot Inscribe writes into: the one chosen, or the wand's first free one. Another wand, another choice. */
    private int target() {
        if (!ItemStack.matches(shownWand, menu.wand())) { shownWand = menu.wand().copy(); target = -1; }
        return target >= 0 && target < menu.capacity() ? target : menu.firstFree();
    }

    @Override protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int target = target();
        boolean ready = menu.check(target) == ArcanePedestalMenu.Status.READY;
        int look = !ready ? 2 : overButton(mouseX, mouseY) ? 1 : 0;
        gui.blit(TEXTURE, leftPos + BUTTON_X, topPos + BUTTON_Y, BUTTON_U, BUTTON_V + look * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
        Grimoire grimoire = grimoire();
        Component label = Component.translatable(target >= 0 && grimoire.at(target) != null ? "gui.corruption.pedestal.inscribe_over" : "gui.corruption.pedestal.inscribe");
        gui.drawString(font, label, leftPos + BUTTON_X + (BUTTON_WIDTH - font.width(label)) / 2, topPos + BUTTON_Y + 3, ready ? LABEL_COLOUR : QUIET_COLOUR, false);

        CastingUnit unit = menu.unit();
        if (unit == null) return;
        int hovered = placeAt(mouseX, mouseY), free = menu.firstFree();
        for (int slot = 0; slot < unit.capacity(); slot++) {
            int[] at = ArcanePedestalMenu.placeAt(slot, unit.capacity());
            int x = leftPos + at[0], y = topPos + at[1], size = at[2];
            if (slot == target) gui.fill(x - 1, y - 1, x + size + 1, y + size + 1, TARGET);
            gui.fill(x, y, x + size, y + size, PLACE_DARK);
            gui.fill(x + 1, y + 1, x + size, y + size, PLACE_LIGHT);
            gui.fill(x + 1, y + 1, x + size - 1, y + size - 1, PLACE_FILL);
            String spell = grimoire.at(slot);
            if (spell != null) {
                ItemStack shown = ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), ArcaneApi.key(spell));
                if (size >= 18) gui.renderItem(shown, x + (size - 16) / 2, y + (size - 16) / 2);
                else {
                    gui.pose().pushPose();
                    gui.pose().translate(x + 1, y + 1, 0);
                    gui.pose().scale((size - 2) / 16F, (size - 2) / 16F, 1);
                    gui.renderItem(shown, 0, 0);
                    gui.pose().popPose();
                }
                if (slot == grimoire.selected()) gui.fill(x + 2, y + size - 3, x + size - 2, y + size - 2, CHOSEN);
            } else {
                String number = String.valueOf(slot + 1);
                gui.drawString(font, number, x + (size - font.width(number) + 1) / 2, y + (size - 7) / 2, slot == free ? NUMBER_COLOUR : FAR_NUMBER_COLOUR, false);
            }
            if (slot == hovered) gui.fill(x + 1, y + 1, x + size - 1, y + size - 1, HOVER);
        }
    }

    @Override protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, title, titleLabelX, titleLabelY, LABEL_COLOUR, false);
        gui.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOUR, false);
        if (menu.unit() == null) {
            int middle = (ArcanePedestalMenu.GRIMOIRE_LEFT + ArcanePedestalMenu.GRIMOIRE_RIGHT) / 2;
            List<FormattedCharSequence> lines = font.split(Component.translatable("gui.corruption.pedestal.wand_slots"), STATUS_WIDTH - 8);
            int top = (ArcanePedestalMenu.GRIMOIRE_TOP + ArcanePedestalMenu.GRIMOIRE_BOTTOM) / 2 - lines.size() * 5;
            for (int line = 0; line < lines.size(); line++)
                gui.drawString(font, lines.get(line), middle - font.width(lines.get(line)) / 2, top + line * 10, QUIET_COLOUR, false);
        }
        ArcanePedestalMenu.Status status = menu.check(target());
        gui.drawString(font, fit(status(status)), STATUS_X, STATUS_Y, status == ArcanePedestalMenu.Status.READY ? LABEL_COLOUR : QUIET_COLOUR, false);
    }

    /** What Inscribe will do, or why it cannot. */
    private Component status(ArcanePedestalMenu.Status status) {
        if (status != ArcanePedestalMenu.Status.READY) return Component.translatable("gui.corruption.pedestal.status." + status.name().toLowerCase(Locale.ROOT));
        int target = target();
        Component spell = name(ArcaneApi.id(menu.string()));
        String written = grimoire().at(target);
        return written == null ? Component.translatable("gui.corruption.pedestal.status.ready", spell, target + 1)
                : Component.translatable("gui.corruption.pedestal.status.ready_over", spell, name(written));
    }

    /** One tooltip at a time: an item's, else the wand's slot under the mouse. */
    @Override public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);
        if (!menu.getCarried().isEmpty() || hoveredSlot != null && hoveredSlot.hasItem()) return;
        int slot = placeAt(mouseX, mouseY);
        if (slot < 0) return;
        Grimoire grimoire = grimoire();
        String spell = grimoire.at(slot);
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.corruption.pedestal.slot", slot + 1));
        lines.add(spell == null ? Component.translatable("gui.corruption.pedestal.empty").withStyle(ChatFormatting.DARK_GRAY) : name(spell).copy().withStyle(ChatFormatting.LIGHT_PURPLE));
        if (spell != null && slot == grimoire.selected()) lines.add(Component.translatable("gui.corruption.pedestal.chosen").withStyle(ChatFormatting.GOLD));
        if (slot == target()) lines.add(Component.translatable("gui.corruption.pedestal.target").withStyle(ChatFormatting.GRAY));
        gui.renderComponentTooltip(font, lines, mouseX, mouseY);
    }

    /** A written slot, or the next free one (whichever empty slot is clicked), is chosen; the button writes there. */
    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int slot = placeAt(mouseX, mouseY);
        if (button == 0 && slot >= 0) {
            int written = grimoire().size();
            target();
            target = slot < written ? slot : written < menu.capacity() ? written : target;
            click();
            return true;
        }
        int target = target();
        if (button == 0 && overButton(mouseX, mouseY) && menu.check(target) == ArcanePedestalMenu.Status.READY && minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, target);
            click();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Grimoire grimoire() { CastingUnit unit = menu.unit(); return unit == null ? Grimoire.EMPTY : unit.grimoire(); }

    /** The wand's slot under the mouse, or -1. */
    private int placeAt(double mouseX, double mouseY) {
        int capacity = menu.capacity();
        for (int slot = 0; slot < capacity; slot++) {
            int[] at = ArcanePedestalMenu.placeAt(slot, capacity);
            if (isHovering(at[0], at[1], at[2], at[2], mouseX, mouseY)) return slot;
        }
        return -1;
    }

    private boolean overButton(double mouseX, double mouseY) { return isHovering(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY); }

    /** What a spell reads as: its meaning, if the arcane data gives one, else its id. */
    private Component name(String spell) {
        ResourceKey<ArcaneString> key = ArcaneApi.key(spell);
        ArcaneString string = minecraft == null || minecraft.level == null ? null : ArcaneApi.string(minecraft.level.registryAccess(), key);
        return string == null ? Component.literal(spell) : string.meaning();
    }

    /** Text cut to the status line's width, ending in dots where it was cut. */
    private FormattedCharSequence fit(Component text) {
        if (font.width(text) <= STATUS_WIDTH) return text.getVisualOrderText();
        FormattedText cut = font.substrByWidth(text, STATUS_WIDTH - font.width("..."));
        return Language.getInstance().getVisualOrder(FormattedText.composite(cut, FormattedText.of("...")));
    }

    private void click() {
        if (minecraft != null) minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
    }

    @SubscribeEvent public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ARCANE_PEDESTAL.get(), ArcanePedestalScreen::new);
    }
}
