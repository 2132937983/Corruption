package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.alchemy.CorruptionBenchMenu;
import com.miophas.corruption.common.registry.ModMenus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Draws gui_corruption_bench.png; the slot positions that match it are in {@link CorruptionBenchMenu}. */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class CorruptionBenchScreen extends AbstractContainerScreen<CorruptionBenchMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/gui_corruption_bench.png");

    public CorruptionBenchScreen(CorruptionBenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        // The panel fills 178 x 256 of the 256 x 256 texture: taller than any vanilla screen.
        imageWidth = 178;
        imageHeight = 256;
        titleLabelX = CorruptionBenchMenu.INVENTORY_X;
        inventoryLabelX = CorruptionBenchMenu.INVENTORY_X;
        inventoryLabelY = CorruptionBenchMenu.INVENTORY_Y - 11;
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @SubscribeEvent public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.CORRUPTION_BENCH.get(), CorruptionBenchScreen::new);
    }
}
