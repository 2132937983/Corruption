package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Solomon;
import com.miophas.corruption.common.entity.SolomonWyrm;
import com.miophas.corruption.core.demon.SolomonRules;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

/**
 * Draws Solomon's boss bar in a style of its own, after the boss bars of L_Ender's Cataclysm: a channel of gold and black
 * split into eight, one place for each of his dragons, each filled in the light of its book as far as that dragon's health
 * goes (what it just lost staying a moment, paler, before it drains), a fallen dragon's place dark and cracked with a broken
 * seal in it; over the middle a medallion of his seal under his crown; his name above in gold. The art is
 * textures/gui/solomon_boss_bar.png, drawn by gamemodels/solomon/draw_boss_bar.py; the places below are where that script
 * puts each piece. It is drawn before the mod's other boss bar (ColossusBossBar) looks at the bar, and in its place.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class SolomonBossBar {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/solomon_boss_bar.png");
    private static final int SHEET_WIDTH = 256, SHEET_HEIGHT = 128, FRAME_WIDTH = 256, FRAME_HEIGHT = 40;
    private static final int FILL_X = 17, FILL_Y = 23, FILL_WIDTH = 26, FILL_HEIGHT = 8, SEGMENT = 28, HEALTH_V = 48, LOST_V = 60, FALLEN_V = 72;
    /** Lost health stays on a dragon's place, paler, and drains away at this share of the place a tick. */
    private static final float DRAIN_PER_TICK = .02F;
    private static final int NAME_COLOUR = 0xF2C46A;
    /** How much of each dragon's place is drawn as present or just lost, so it can drain behind its real health. */
    private static final Map<UUID, float[]> SHOWN = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void draw(CustomizeGuiOverlayEvent.BossEventProgress event) {
        Minecraft minecraft = Minecraft.getInstance();
        UUID id = event.getBossEvent().getId();
        Solomon owner = owner(minecraft, id);
        if (owner == null) return;
        event.setCanceled(true);

        GuiGraphics gui = event.getGuiGraphics();
        if (SHOWN.size() > 8) SHOWN.clear();
        float[] shown = SHOWN.computeIfAbsent(id, ignored -> new float[SolomonRules.DRAGONS]);
        float drain = DRAIN_PER_TICK * event.getPartialTick().getRealtimeDeltaTicks();
        int left = (gui.guiWidth() - FRAME_WIDTH) / 2, top = event.getY() + 1;
        RenderSystem.enableBlend();
        gui.blit(TEXTURE, left, top, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, SHEET_WIDTH, SHEET_HEIGHT);
        for (int dragon = 0; dragon < SolomonRules.DRAGONS; dragon++) {
            float share = Mth.clamp(owner.share(dragon), 0, 1);
            shown[dragon] = Math.max(share, shown[dragon] - drain);
            int x = left + FILL_X + dragon * SEGMENT, y = top + FILL_Y;
            if (share <= 0 && shown[dragon] <= 0) {
                gui.blit(TEXTURE, x, y, 0, FALLEN_V, FILL_WIDTH, FILL_HEIGHT, SHEET_WIDTH, SHEET_HEIGHT);
                continue;
            }
            int accent = SolomonWyrm.accent(dragon);
            gui.setColor((accent >> 16 & 255) / 255F, (accent >> 8 & 255) / 255F, (accent & 255) / 255F, 1);
            gui.blit(TEXTURE, x, y, 0, LOST_V, Mth.ceil(FILL_WIDTH * shown[dragon]), FILL_HEIGHT, SHEET_WIDTH, SHEET_HEIGHT);
            gui.blit(TEXTURE, x, y, 0, HEALTH_V, Mth.ceil(FILL_WIDTH * share), FILL_HEIGHT, SHEET_WIDTH, SHEET_HEIGHT);
            gui.setColor(1, 1, 1, 1);
        }
        RenderSystem.disableBlend();

        Component name = event.getBossEvent().getName();
        gui.drawString(minecraft.font, name, (gui.guiWidth() - minecraft.font.width(name)) / 2, event.getY() - 9, NAME_COLOUR);
        event.setIncrement(FRAME_HEIGHT + minecraft.font.lineHeight + 4);
    }

    /** Solomon, if this bar is his and he is in sight. */
    private static Solomon owner(Minecraft minecraft, UUID bar) {
        if (minecraft.level == null) return null;
        for (Entity entity : minecraft.level.entitiesForRendering())
            if (entity instanceof Solomon solomon && solomon.bossBar().filter(bar::equals).isPresent()) return solomon;
        return null;
    }

    private SolomonBossBar() {}
}
