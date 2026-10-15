package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.common.network.SelectSpellPayload;
import com.miophas.corruption.core.arcane.Grimoire;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * The spell wheel: while it is open, the spells the wand has been taught stand round a circle, and the one the
 * pointer is nearest is the one chosen. It does not pause the game, and it writes nothing: on release it tells
 * the server which spell was chosen (see {@link SelectSpellPayload}), and the server writes the wand.
 *
 * <p>The ring itself is textures/gui/spell_wheel.png, drawn the whole width of the wheel; the names sit on it.
 * Each spell reads as its meaning, if the arcane data gives one, and as its id if not.
 */
public final class SpellWheelScreen extends Screen {
    private static final ResourceLocation WHEEL = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/spell_wheel.png");
    /** How much of the shorter side of the screen the wheel takes up. */
    private static final double SIZE = 0.6;
    /** How far from the middle the spells stand, as a share of the wheel's size. */
    private static final double RING = 0.34;
    /** Within this share of the middle, the choice is left as it was, so a flick back out does not change it. */
    private static final double DEAD_ZONE = 0.1;
    private static final int NAME = 0xFFCCCCCC, CHOSEN = 0xFFFFE070, TITLE = 0xFFFFFFFF;

    private final List<String> spells;
    private final List<Component> names;
    private int hovered;

    public SpellWheelScreen(Grimoire grimoire) {
        super(Component.translatable("gui.corruption.spell_wheel"));
        this.spells = List.copyOf(grimoire.spells());
        this.names = spells.stream().map(SpellWheelScreen::read).toList();
        this.hovered = grimoire.selected();
    }

    /** What a spell reads as: its meaning, if the arcane data gives one, else its id. */
    private static Component read(String spell) {
        Minecraft minecraft = Minecraft.getInstance();
        ArcaneString string = minecraft.level == null ? null : ArcaneApi.string(minecraft.level.registryAccess(), ArcaneApi.key(spell));
        return string == null ? Component.literal(spell) : string.meaning();
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hovered = nearest(mouseX, mouseY);
        int middleX = width / 2, middleY = height / 2;
        int size = size();
        graphics.blit(WHEEL, middleX - size / 2, middleY - size / 2, 0, 0, size, size, size, size);
        graphics.drawCenteredString(font, title, middleX, middleY - 4, TITLE);
        for (int index = 0; index < spells.size(); index++) {
            double angle = -Math.PI / 2 + index * (Math.PI * 2 / spells.size());
            int x = (int) Math.round(middleX + Math.cos(angle) * size * RING);
            int y = (int) Math.round(middleY + Math.sin(angle) * size * RING);
            graphics.drawCenteredString(font, names.get(index), x, y - 4, index == hovered ? CHOSEN : NAME);
        }
    }

    /** How wide the wheel is drawn, in pixels. */
    private int size() { return (int) (Math.min(width, height) * SIZE); }

    /** The spell the pointer is nearest, by the way it lies from the middle; the middle leaves it as it was. */
    private int nearest(int mouseX, int mouseY) {
        if (spells.isEmpty()) return -1;
        double across = mouseX - width / 2.0, down = mouseY - height / 2.0;
        double dead = size() * DEAD_ZONE;
        if (across * across + down * down < dead * dead) return hovered;
        double step = Math.PI * 2 / spells.size();
        int index = (int) Math.round((Math.atan2(down, across) + Math.PI / 2) / step);
        return Math.floorMod(index, spells.size());
    }

    /** Tells the server what was chosen; answers whether anything was. */
    public boolean chosen() {
        if (hovered < 0 || hovered >= spells.size()) return false;
        SelectSpellPayload.send(hovered);
        return true;
    }
}
