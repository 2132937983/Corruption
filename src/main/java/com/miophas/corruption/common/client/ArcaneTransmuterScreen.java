package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyUnit;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.common.transmutation.ArcaneTransmuterMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * The arcane transmuter's screen: gui_corruption_arcane_transmuter.png, with three bars from {@link GuiTiles}
 * drawn into its free space. Between the two inputs the loading bar fills from the bottom as fuel, or what a
 * candle carries, turns into energy; before the result the progress bar fills as the transmutation goes on; under the result the
 * energy bar shows the store. Hovering over a bar says what it means in numbers.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class ArcaneTransmuterScreen extends AbstractContainerScreen<ArcaneTransmuterMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/gui_corruption_arcane_transmuter.png");
    public static final String LOADING = "loading_bar", PROGRESS = "progress_bar", ENERGY = "corruption_energy_bar";
    /** Where the bars go: centred between the two input slots, half way to the result, and under the result. */
    public static final int LOADING_X = 57, LOADING_Y = 36, PROGRESS_X = 81, PROGRESS_Y = 35, ENERGY_X = 90, ENERGY_Y = 61;
    /** The panel is dark stone, so the labels are drawn light. */
    private static final int LABEL_COLOUR = 0xE3D3EA;

    public ArcaneTransmuterScreen(ArcaneTransmuterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
        inventoryLabelY = imageHeight - 94;
    }

    @Override protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        GuiTiles.GUI.bar(gui, LOADING, leftPos + LOADING_X, topPos + LOADING_Y, menu.burnFraction(), GuiTiles.Fill.BOTTOM_TO_TOP);
        GuiTiles.GUI.bar(gui, PROGRESS, leftPos + PROGRESS_X, topPos + PROGRESS_Y, menu.progressFraction(), GuiTiles.Fill.LEFT_TO_RIGHT);
        GuiTiles.GUI.bar(gui, ENERGY, leftPos + ENERGY_X, topPos + ENERGY_Y, menu.energyFraction(), GuiTiles.Fill.LEFT_TO_RIGHT);
    }

    @Override protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, title, titleLabelX, titleLabelY, LABEL_COLOUR, false);
        gui.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOUR, false);
    }

    /**
     * One tooltip at a time: an item's when the mouse is on one, otherwise the bar's under it, and none
     * while an item is carried. JEI makes the progress and loading bars clickable and shows its own tooltip
     * there, so when it does, the screen leaves those two to JEI, which shows these same lines.
     */
    @Override public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);
        if (!menu.getCarried().isEmpty() || hoveredSlot != null && hoveredSlot.hasItem()) return;
        for (String bar : new String[]{ENERGY, PROGRESS, LOADING}) {
            if (!over(bar, mouseX, mouseY)) continue;
            if (bar.equals(ENERGY) || !barsExplainedByRecipeViewer) {
                List<Component> lines = barTooltip(bar);
                if (!lines.isEmpty()) gui.renderComponentTooltip(font, lines, mouseX, mouseY);
            }
            return;
        }
    }

    /** Told by the JEI plugin that JEI will show the progress and loading bars' tooltips, on this screen and the brass cauldron's. */
    public static void explainBarsInRecipeViewer() { barsExplainedByRecipeViewer = true; }
    public static boolean barsExplainedByRecipeViewer() { return barsExplainedByRecipeViewer; }
    private static boolean barsExplainedByRecipeViewer;

    /** What a bar means in numbers; nothing for a bar with nothing going on. */
    public List<Component> barTooltip(String bar) {
        List<Component> lines = new ArrayList<>();
        switch (bar) {
            case ENERGY -> {
                lines.add(Component.translatable("gui.corruption.corruption_energy"));
                long capacity = CorruptionEnergyApi.transmuterCapacity();
                CorruptionEnergyUnit unit = CorruptionEnergyApi.unit(capacity);
                lines.add(Component.translatable("gui.corruption.energy_amount", CorruptionEnergyApi.text(menu.energy(), unit), CorruptionEnergyApi.text(capacity, unit), unit.symbol()));
            }
            case PROGRESS -> {
                if (menu.cost() > 0) {
                    lines.add(Component.translatable("gui.corruption.transmuting", Math.round(menu.progressFraction() * 100)));
                    lines.add(Component.translatable("gui.corruption.transmutation_cost", CorruptionEnergyApi.format(menu.cost())));
                }
            }
            case LOADING -> { if (menu.burnFraction() > 0) lines.add(Component.translatable("gui.corruption.converting", Math.round(menu.burnFraction() * 100))); }
            default -> {}
        }
        return lines;
    }

    /** Where a bar sits on the panel, and its size: x, y, width, height. */
    public static int[] area(String bar) {
        GuiTiles.Tile tile = GuiTiles.GUI.sheet().tiles().get(bar);
        int width = tile == null ? 0 : tile.width(), height = tile == null ? 0 : tile.height();
        return switch (bar) {
            case ENERGY -> new int[]{ENERGY_X, ENERGY_Y, width, height};
            case PROGRESS -> new int[]{PROGRESS_X, PROGRESS_Y, width, height};
            case LOADING -> new int[]{LOADING_X, LOADING_Y, width, height};
            default -> new int[]{0, 0, 0, 0};
        };
    }

    private boolean over(String bar, int mouseX, int mouseY) {
        int[] area = area(bar);
        return area[2] > 0 && isHovering(area[0], area[1], area[2], area[3], mouseX, mouseY);
    }

    @SubscribeEvent public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ARCANE_TRANSMUTER.get(), ArcaneTransmuterScreen::new);
    }
}
