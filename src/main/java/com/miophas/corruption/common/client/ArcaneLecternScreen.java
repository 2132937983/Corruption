package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.common.arcane.ArcaneLecternMenu;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.core.arcane.Transcription;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * The arcane lectern's screen, gui_corruption_arcane_lectern.png. The list in the middle shows the strings of
 * the key on the left, each as its arcane form, in glyphs, over its meaning, read through the key. Choose
 * one, and Transcribe copies it onto a blank parchment with one ink; while it cannot, hovering over the
 * button says what is missing.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class ArcaneLecternScreen extends AbstractContainerScreen<ArcaneLecternMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/gui_corruption_arcane_lectern.png");
    /** The list: where its rows start, how wide their text runs, how tall each is, and how many show at once. */
    public static final int LIST_X = 31, LIST_Y = 18, LIST_WIDTH = 108, ROW_HEIGHT = 24, ROWS = 3;
    /** The scroll bar, along the list's right edge. */
    public static final int SCROLL_X = 142, SCROLL_WIDTH = 4;
    /** The Transcribe button, under the list; its three looks are drawn below the panel in the texture. */
    public static final int BUTTON_X = 29, BUTTON_Y = 96, BUTTON_WIDTH = 120, BUTTON_HEIGHT = 14, BUTTON_U = 0, BUTTON_V = 212;
    private static final int LABEL_COLOUR = 0xE3D3EA, GLYPH_COLOUR = 0xC6728C, MEANING_COLOUR = 0xE3D3EA, QUIET_COLOUR = 0x9C8FA6;

    private int first, selected = -1;
    private ItemStack shownKey = ItemStack.EMPTY;

    public ArcaneLecternScreen(ArcaneLecternMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 210;
        inventoryLabelY = imageHeight - 94;
    }

    /** Another key, another list: start again from its top, with nothing chosen. */
    private List<Holder<ArcaneString>> strings() {
        if (!ItemStack.matches(shownKey, menu.key())) { shownKey = menu.key().copy(); first = 0; selected = -1; }
        return menu.strings();
    }

    @Override protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        List<Holder<ArcaneString>> strings = strings();
        boolean ready = selected >= 0 && menu.check(selected) == Transcription.Check.READY;
        int look = !ready ? 2 : overButton(mouseX, mouseY) ? 1 : 0;
        gui.blit(TEXTURE, leftPos + BUTTON_X, topPos + BUTTON_Y, BUTTON_U, BUTTON_V + look * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
        Component label = Component.translatable("gui.corruption.transcribe");
        gui.drawString(font, label, leftPos + BUTTON_X + (BUTTON_WIDTH - font.width(label)) / 2, topPos + BUTTON_Y + 3, ready ? LABEL_COLOUR : QUIET_COLOUR, false);

        for (int row = 0; row < ROWS && first + row < strings.size(); row++) {
            int index = first + row, x = leftPos + LIST_X, y = topPos + LIST_Y + row * ROW_HEIGHT;
            if (index == selected) gui.fill(x - 1, y, x + LIST_WIDTH + 1, y + ROW_HEIGHT - 1, 0x90B05A77);
            else if (row == rowAt(mouseX, mouseY)) gui.fill(x - 1, y, x + LIST_WIDTH + 1, y + ROW_HEIGHT - 1, 0x30FFFFFF);
            ArcaneString string = strings.get(index).value();
            gui.drawString(font, fit(ArcaneApi.glyphs(string.text())), x + 2, y + 3, GLYPH_COLOUR, false);
            gui.drawString(font, fit(string.meaning()), x + 2, y + 13, MEANING_COLOUR, false);
        }
        if (strings.size() > ROWS) {
            int track = ROWS * ROW_HEIGHT, thumb = Math.max(8, track * ROWS / strings.size());
            int top = topPos + LIST_Y + (track - thumb) * first / (strings.size() - ROWS);
            gui.fill(leftPos + SCROLL_X, top, leftPos + SCROLL_X + SCROLL_WIDTH, top + thumb, 0xFFB05A77);
        }
    }

    @Override protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, title, titleLabelX, titleLabelY, LABEL_COLOUR, false);
        gui.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOUR, false);
        if (menu.key().isEmpty()) {
            for (FormattedCharSequence line : font.split(Component.translatable("gui.corruption.lectern_empty"), LIST_WIDTH))
                gui.drawString(font, line, LIST_X + (LIST_WIDTH - font.width(line)) / 2, LIST_Y + 28, QUIET_COLOUR, false);
        }
    }

    /** One tooltip at a time: an item's, else the string's under the mouse in full, else why Transcribe cannot be pressed. */
    @Override public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);
        if (!menu.getCarried().isEmpty() || hoveredSlot != null && hoveredSlot.hasItem()) return;
        List<Holder<ArcaneString>> strings = strings();
        int row = rowAt(mouseX, mouseY);
        if (row >= 0 && first + row < strings.size()) {
            ArcaneString string = strings.get(first + row).value();
            gui.renderComponentTooltip(font, List.of(ArcaneApi.glyphs(string.text()).withColor(GLYPH_COLOUR), string.meaning()), mouseX, mouseY);
        } else if (overButton(mouseX, mouseY)) {
            Transcription.Check check = selected < 0 ? Transcription.Check.NOT_IN_KEY : menu.check(selected);
            if (check != Transcription.Check.READY)
                gui.renderTooltip(font, Component.translatable("gui.corruption.transcription." + check.name().toLowerCase(Locale.ROOT)), mouseX, mouseY);
        }
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int row = rowAt(mouseX, mouseY);
        if (button == 0 && row >= 0 && first + row < strings().size()) {
            selected = first + row;
            click();
            return true;
        }
        if (button == 0 && overButton(mouseX, mouseY) && selected >= 0 && menu.check(selected) == Transcription.Check.READY && minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, selected);
            click();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int size = strings().size();
        if (size > ROWS && isHovering(LIST_X - 2, LIST_Y, SCROLL_X + SCROLL_WIDTH - LIST_X + 2, ROWS * ROW_HEIGHT, mouseX, mouseY)) {
            first = Mth.clamp(first - (int) Math.signum(scrollY), 0, size - ROWS);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    /** The row of the list under the mouse, or -1. */
    private int rowAt(double mouseX, double mouseY) {
        double x = mouseX - leftPos - LIST_X, y = mouseY - topPos - LIST_Y;
        if (x < -1 || x >= LIST_WIDTH + 1 || y < 0 || y >= ROWS * ROW_HEIGHT) return -1;
        return (int) (y / ROW_HEIGHT);
    }

    private boolean overButton(double mouseX, double mouseY) { return isHovering(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY); }

    /** Text cut to the list's width, ending in dots where it was cut. */
    private FormattedCharSequence fit(Component text) {
        if (font.width(text) <= LIST_WIDTH - 4) return text.getVisualOrderText();
        FormattedText cut = font.substrByWidth(text, LIST_WIDTH - 4 - font.width("..."));
        return Language.getInstance().getVisualOrder(FormattedText.composite(cut, FormattedText.of("...")));
    }

    private void click() {
        if (minecraft != null) minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
    }

    @SubscribeEvent public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ARCANE_LECTERN.get(), ArcaneLecternScreen::new);
    }
}
