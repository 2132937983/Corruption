package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.Agares;
import com.miophas.corruption.common.entity.Colossus;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

/**
 * Draws a colossus's boss bar in the mod's own style instead of the game's purple strip: a thorned frame
 * with the boss's emblem in a medallion, health that drains a moment after it is lost, and a claw at the
 * end. The art is textures/gui/colossus_boss_bar.png, drawn by gamemodels/boss_bar/draw_boss_bar.py; the
 * places below are where that script puts each piece.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class ColossusBossBar {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/colossus_boss_bar.png");
    private static final int SHEET_WIDTH = 256, SHEET_HEIGHT = 128, FRAME_WIDTH = 256, FRAME_HEIGHT = 34;
    private static final int HEALTH_X = 36, HEALTH_Y = 12, HEALTH_WIDTH = 200, HEALTH_HEIGHT = 10, HEALTH_V = 40, LOST_V = 56, NOTCH_V = 100;
    private static final int EMBLEM_X = 4, EMBLEM_Y = 4, EMBLEM_SIZE = 24, EMBLEM_V = 72, BAAL_U = 0, AGARES_U = 32;
    /** Lost health stays on the bar, paler, and drains away at this share of the bar a tick. */
    private static final float DRAIN_PER_TICK = .006F;
    private static final int NAME_COLOUR = 0xF2B5C6;
    /** How much of each bar is drawn as present or just lost, so it can drain behind the real value. */
    private static final Map<UUID, Float> SHOWN = new HashMap<>();

    @SubscribeEvent
    public static void draw(CustomizeGuiOverlayEvent.BossEventProgress event) {
        Minecraft minecraft = Minecraft.getInstance();
        UUID id = event.getBossEvent().getId();
        Colossus owner = owner(minecraft, id);
        if (owner == null) return;
        event.setCanceled(true);

        GuiGraphics gui = event.getGuiGraphics();
        float health = Mth.clamp(event.getBossEvent().getProgress(), 0, 1);
        if (SHOWN.size() > 16) SHOWN.clear();
        float shown = Math.max(health, SHOWN.getOrDefault(id, health) - DRAIN_PER_TICK * event.getPartialTick().getRealtimeDeltaTicks());
        SHOWN.put(id, shown);

        int left = (gui.guiWidth() - FRAME_WIDTH) / 2, top = event.getY() + 1;
        RenderSystem.enableBlend();
        gui.blit(TEXTURE, left, top, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, SHEET_WIDTH, SHEET_HEIGHT);
        gui.blit(TEXTURE, left + HEALTH_X, top + HEALTH_Y, 0, LOST_V, Mth.ceil(HEALTH_WIDTH * shown), HEALTH_HEIGHT, SHEET_WIDTH, SHEET_HEIGHT);
        gui.blit(TEXTURE, left + HEALTH_X, top + HEALTH_Y, 0, HEALTH_V, Mth.ceil(HEALTH_WIDTH * health), HEALTH_HEIGHT, SHEET_WIDTH, SHEET_HEIGHT);
        gui.blit(TEXTURE, left + HEALTH_X, top + HEALTH_Y, 0, NOTCH_V, HEALTH_WIDTH, HEALTH_HEIGHT, SHEET_WIDTH, SHEET_HEIGHT);
        gui.blit(TEXTURE, left + EMBLEM_X, top + EMBLEM_Y, owner instanceof Agares ? AGARES_U : BAAL_U, EMBLEM_V, EMBLEM_SIZE, EMBLEM_SIZE, SHEET_WIDTH, SHEET_HEIGHT);
        RenderSystem.disableBlend();

        Component name = event.getBossEvent().getName();
        gui.drawString(minecraft.font, name, (gui.guiWidth() - minecraft.font.width(name)) / 2, event.getY() - 9, NAME_COLOUR);
        event.setIncrement(FRAME_HEIGHT + minecraft.font.lineHeight + 4);
    }

    /** The colossus this bar belongs to, if it is one of ours and in sight. */
    private static Colossus owner(Minecraft minecraft, UUID bar) {
        if (minecraft.level == null) return null;
        for (Entity entity : minecraft.level.entitiesForRendering())
            if (entity instanceof Colossus colossus && colossus.bossBar().filter(bar::equals).isPresent()) return colossus;
        return null;
    }

    private ColossusBossBar() {}
}
