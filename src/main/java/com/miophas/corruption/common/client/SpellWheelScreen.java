package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneSpellApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.api.arcane.SpellText;
import com.miophas.corruption.common.network.SelectSpellPayload;
import com.miophas.corruption.core.arcane.Grimoire;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

/**
 * The spell wheel: one sector for each slot of the casting unit in hand, however many it has, the first
 * straight up and the rest clockwise, each bearing the spell written in it, or dim and empty. The sector the
 * pointer is in is chosen, if a spell is written there; the middle, or an empty sector, leaves the choice as it
 * was. The wand's present spell is marked in gold on the rim. It does not pause the game, and it writes nothing:
 * when the wheel key is let go it tells the server which spell was chosen (see {@link SelectSpellPayload}), and
 * the server writes the wand. Closed with Escape, it chooses nothing.
 *
 * <p>The sectors are drawn as the slots require; textures/gui/spell_wheel.png is only the ornament round them,
 * an outer ring and a medallion in the middle (gamemodels/spell_wheel/draw_wheel.py). Each spell reads as its
 * meaning, if the arcane data gives one, and as its id if not.
 */
public final class SpellWheelScreen extends Screen {
    private static final ResourceLocation WHEEL = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/spell_wheel.png");
    /** How much of the shorter side of the screen the wheel takes up. */
    private static final double SIZE = 0.6;
    /**
     * Shares of the wheel's half-width: where the sectors begin and end, between the medallion and the ring the
     * texture draws; and the medallion, which is the dead zone.
     */
    public static final double INNER = 0.31, OUTER = 0.86, MEDALLION = 0.30;
    private static final int WRITTEN = 0xB0201828, WRITTEN_ALTERNATE = 0xB0281E32, EMPTY = 0x70101014, CHOSEN_FILL = 0xD05E2F52,
            DIVIDER = 0xE0563A5E, RIM = 0xFFD08AC8, GOLD = 0xFFD8A936;
    private static final int NAME = 0xFFE3D3EA, CHOSEN_NAME = 0xFFFFE070, EMPTY_NAME = 0xFF6E6278, NUMBER = 0xFF9C8FA6, TITLE = 0xFFFFFFFF,
            EFFECT = 0xFFB9A8C8;
    /** How many lines of what the chosen spell does are shown under the wheel, so it cannot run off a short screen. */
    private static final int EFFECT_LINES = 4;

    private final Grimoire grimoire;
    private final WheelSectors sectors;
    private final List<Component> names;
    private final List<List<Component>> effects;
    private int chosen;

    /** @param capacity how many slots the casting unit in hand has: one sector each */
    public SpellWheelScreen(Grimoire grimoire, int capacity) {
        super(Component.translatable("gui.corruption.spell_wheel"));
        this.grimoire = grimoire;
        this.sectors = new WheelSectors(Math.max(capacity, grimoire.size()));
        this.names = grimoire.spells().stream().map(SpellWheelScreen::read).toList();
        this.effects = grimoire.spells().stream().map(SpellWheelScreen::effects).toList();
        this.chosen = grimoire.selected();
    }

    /** What a spell does, read out: one line an action, shown under the wheel while it is the chosen one. */
    private static List<Component> effects(String spell) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? List.of()
                : SpellText.lines(ArcaneSpellApi.rule(minecraft.level.registryAccess(), ArcaneApi.key(spell)));
    }

    /** What a spell reads as: its meaning, if the arcane data gives one, else its id. */
    private static Component read(String spell) {
        Minecraft minecraft = Minecraft.getInstance();
        ArcaneString string = minecraft.level == null ? null : ArcaneApi.string(minecraft.level.registryAccess(), ArcaneApi.key(spell));
        return string == null ? Component.literal(spell) : string.meaning();
    }

    @Override public boolean isPauseScreen() { return false; }

    /** Let go of the wheel key, it chooses and closes. */
    @Override public void tick() {
        if (!SpellWheel.held()) choose();
    }

    @Override public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (SpellWheel.OPEN.matches(keyCode, scanCode)) { choose(); return true; }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (SpellWheel.OPEN.matchesMouse(button)) { choose(); return true; }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void choose() {
        chosen();
        onClose();
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int middleX = width / 2, middleY = height / 2, size = size();
        double half = size / 2.0, inner = half * INNER, outer = half * OUTER;
        chosen = WheelSectors.choose(sectors.at(mouseX - middleX, mouseY - middleY, half * MEDALLION), chosen, grimoire.size());

        graphics.blit(WHEEL, middleX - size / 2, middleY - size / 2, 0, 0, size, size, size, size);
        VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f pose = graphics.pose().last().pose();
        for (int sector = 0; sector < sectors.count(); sector++) {
            boolean written = sector < grimoire.size();
            int fill = !written ? EMPTY : sector == chosen ? CHOSEN_FILL : sector % 2 == 0 ? WRITTEN : WRITTEN_ALTERNATE;
            arc(buffer, pose, middleX, middleY, inner, outer, sectors.start(sector), sectors.end(sector), fill);
            if (sector == chosen && written) arc(buffer, pose, middleX, middleY, outer - 2, outer, sectors.start(sector), sectors.end(sector), RIM);
            if (written && sector == grimoire.selected()) {
                double mark = sectors.step() * .18;
                arc(buffer, pose, middleX, middleY, outer + 1, outer + 4, sectors.middle(sector) - mark, sectors.middle(sector) + mark, GOLD);
            }
            if (sectors.count() > 1) divider(buffer, pose, middleX, middleY, inner, outer, sectors.start(sector), DIVIDER);
        }
        graphics.flush();

        graphics.drawCenteredString(font, title, middleX, middleY - size / 2 - 12, TITLE);
        double label = (inner + outer) / 2, chord = sectors.count() == 1 ? outer : 2 * label * Math.sin(sectors.step() / 2);
        float scale = sectors.count() <= 6 ? 1 : sectors.count() <= 9 ? .85F : .75F;
        int lineWidth = (int) Math.max(24, Math.min(outer - inner + 20, chord * .9) / scale);
        for (int sector = 0; sector < sectors.count(); sector++) {
            double[] at = WheelSectors.point(sectors.middle(sector), sectors.count() == 1 ? 0 : label);
            boolean written = sector < grimoire.size();
            Component text = written ? names.get(sector) : Component.translatable("gui.corruption.spell_wheel.empty");
            int colour = !written ? EMPTY_NAME : sector == chosen ? CHOSEN_NAME : NAME;
            List<FormattedCharSequence> lines = lines(text, lineWidth);
            graphics.pose().pushPose();
            graphics.pose().translate(middleX + at[0], middleY + at[1] + (sectors.count() == 1 ? -outer / 2 : 0), 0);
            graphics.pose().scale(scale, scale, 1);
            String number = String.valueOf(sector + 1);
            int top = -(lines.size() * 10 + 10) / 2;
            graphics.drawString(font, number, -font.width(number) / 2, top, written && sector == grimoire.selected() ? GOLD : NUMBER, false);
            for (int line = 0; line < lines.size(); line++)
                graphics.drawString(font, lines.get(line), -font.width(lines.get(line)) / 2, top + 10 + line * 10, colour, true);
            graphics.pose().popPose();
        }
        if (chosen < 0 || chosen >= names.size()) return;
        int under = middleY + size / 2 + 4;
        graphics.drawCenteredString(font, names.get(chosen), middleX, under, CHOSEN_NAME);
        List<Component> readout = effects.get(chosen);
        for (int line = 0; line < readout.size() && line < EFFECT_LINES; line++)
            graphics.drawCenteredString(font, readout.get(line), middleX, under + 11 + line * 10, EFFECT);
    }

    /** A name in at most two lines of this width, cut short with dots if it is longer still. */
    private List<FormattedCharSequence> lines(Component text, int width) {
        List<FormattedCharSequence> lines = font.split(text, width);
        if (lines.size() <= 2) return lines;
        FormattedText first = font.substrByWidth(text, width);
        FormattedText rest = font.substrByWidth(FormattedText.of(text.getString().substring(first.getString().length()).stripLeading()), width - font.width("..."));
        return List.of(Language.getInstance().getVisualOrder(first), Language.getInstance().getVisualOrder(FormattedText.composite(rest, FormattedText.of("..."))));
    }

    /** A band of the wheel between two distances from the middle and two angles, in small quads. */
    private static void arc(VertexConsumer buffer, Matrix4f pose, int middleX, int middleY, double from, double to, double start, double end, int colour) {
        int steps = Math.max(2, (int) Math.ceil((end - start) / (Math.PI / 48)));
        for (int step = 0; step < steps; step++) {
            double a = start + (end - start) * step / steps, b = start + (end - start) * (step + 1) / steps;
            double[] innerA = WheelSectors.point(a, from), innerB = WheelSectors.point(b, from), outerB = WheelSectors.point(b, to), outerA = WheelSectors.point(a, to);
            vertex(buffer, pose, middleX, middleY, innerA, colour);
            vertex(buffer, pose, middleX, middleY, innerB, colour);
            vertex(buffer, pose, middleX, middleY, outerB, colour);
            vertex(buffer, pose, middleX, middleY, outerA, colour);
        }
    }

    /** A thin line from the inner edge of the sectors to the outer, at this angle. */
    private static void divider(VertexConsumer buffer, Matrix4f pose, int middleX, int middleY, double from, double to, double angle, int colour) {
        double across = Math.cos(angle) * .75, down = Math.sin(angle) * .75;
        double[] inner = WheelSectors.point(angle, from), outer = WheelSectors.point(angle, to);
        vertex(buffer, pose, middleX, middleY, new double[]{inner[0] - across, inner[1] - down}, colour);
        vertex(buffer, pose, middleX, middleY, new double[]{inner[0] + across, inner[1] + down}, colour);
        vertex(buffer, pose, middleX, middleY, new double[]{outer[0] + across, outer[1] + down}, colour);
        vertex(buffer, pose, middleX, middleY, new double[]{outer[0] - across, outer[1] - down}, colour);
    }

    private static void vertex(VertexConsumer buffer, Matrix4f pose, int middleX, int middleY, double[] at, int colour) {
        buffer.addVertex(pose, (float) (middleX + at[0]), (float) (middleY + at[1]), 0).setColor(colour);
    }

    /** How wide the wheel is drawn, in pixels. */
    private int size() { return (int) (Math.min(width, height) * SIZE); }

    /** Tells the server what was chosen; answers whether anything was. */
    public boolean chosen() {
        if (chosen < 0 || chosen >= grimoire.size()) return false;
        SelectSpellPayload.send(chosen);
        return true;
    }
}
