package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyUnit;
import com.miophas.corruption.common.cooking.BrassCauldronBlockEntity;
import com.miophas.corruption.common.cooking.BrassCauldronMenu;
import com.miophas.corruption.common.registry.ModMenus;
import com.miophas.corruption.core.cooking.BrassCauldronRules;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * The brass cauldron's screen: gui_brass_cauldron.png, its panel tiled with corrupted brass. The tank on the
 * left shows its fluid rising; beside the energy slot the loading bar fills as fuel turns into energy; before
 * the dish the progress bar fills as it cooks; under the dish the energy bar shows the store. Hovering over
 * the tank or a bar says what it means in numbers.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class BrassCauldronScreen extends AbstractContainerScreen<BrassCauldronMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/gui/gui_brass_cauldron.png");
    public static final String LOADING = ArcaneTransmuterScreen.LOADING, PROGRESS = ArcaneTransmuterScreen.PROGRESS, ENERGY = ArcaneTransmuterScreen.ENERGY, TANK = "tank";
    /** Where the bars go: beside the energy slot, half way from the hexagon to the dish, and under the dish. */
    public static final int LOADING_X = 55, LOADING_Y = 85, PROGRESS_X = 97, PROGRESS_Y = 40, ENERGY_X = 100, ENERGY_Y = 66;
    /** The tank's scale, drawn over the fluid: a strip beside the panel in the texture. */
    public static final int SCALE_U = 176, SCALE_V = 0;
    private static final int LABEL_COLOUR = 0xF4E3B8;

    public BrassCauldronScreen(BrassCauldronMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 200;
        inventoryLabelY = imageHeight - 94;
    }

    @Override protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        drawFluid(gui, menu.fluid(), leftPos + BrassCauldronMenu.TANK_X, topPos + BrassCauldronMenu.TANK_Y, BrassCauldronMenu.TANK_WIDTH, BrassCauldronMenu.TANK_HEIGHT, menu.fluidFraction());
        gui.blit(TEXTURE, leftPos + BrassCauldronMenu.TANK_X, topPos + BrassCauldronMenu.TANK_Y, SCALE_U, SCALE_V, BrassCauldronMenu.TANK_WIDTH, BrassCauldronMenu.TANK_HEIGHT);
        GuiTiles.GUI.bar(gui, LOADING, leftPos + LOADING_X, topPos + LOADING_Y, menu.burnFraction(), GuiTiles.Fill.BOTTOM_TO_TOP);
        GuiTiles.GUI.bar(gui, PROGRESS, leftPos + PROGRESS_X, topPos + PROGRESS_Y, menu.progressFraction(), GuiTiles.Fill.LEFT_TO_RIGHT);
        GuiTiles.GUI.bar(gui, ENERGY, leftPos + ENERGY_X, topPos + ENERGY_Y, menu.energyFraction(), GuiTiles.Fill.LEFT_TO_RIGHT);
    }

    /** A fluid's still texture, tinted as the fluid is, tiled into a window and filled from its bottom to this share of its height. */
    static void drawFluid(GuiGraphics gui, FluidStack fluid, int x, int y, int width, int height, float fraction) {
        int filled = Math.round(height * fraction);
        if (fluid.isEmpty() || filled <= 0) return;
        IClientFluidTypeExtensions look = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(look.getStillTexture(fluid));
        int tint = look.getTintColor(fluid);
        gui.setColor((tint >> 16 & 255) / 255F, (tint >> 8 & 255) / 255F, (tint & 255) / 255F, (tint >>> 24) / 255F == 0 ? 1 : (tint >>> 24) / 255F);
        gui.enableScissor(x, y + height - filled, x + width, y + height);
        for (int top = y + height - 16; top > y + height - filled - 16; top -= 16)
            for (int left = x; left < x + width; left += 16) gui.blit(left, top, 0, 16, 16, sprite);
        gui.disableScissor();
        gui.setColor(1, 1, 1, 1);
    }

    @Override protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, title, titleLabelX, titleLabelY, LABEL_COLOUR, false);
        gui.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOUR, false);
    }

    /**
     * One tooltip at a time: an item's when the mouse is on one, otherwise that of the tank or bar under it, and
     * none while an item is carried. JEI makes the progress and loading bars clickable and shows its own tooltip
     * there, so when it does, the screen leaves those two to JEI, which shows these same lines.
     */
    @Override public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        renderTooltip(gui, mouseX, mouseY);
        if (!menu.getCarried().isEmpty() || hoveredSlot != null && hoveredSlot.hasItem()) return;
        if (hoveredSlot != null && hoveredSlot.getContainerSlot() == BrassCauldronBlockEntity.PARCHMENT && hoveredSlot.container != minecraft.player.getInventory()) {
            gui.renderComponentTooltip(font, List.of(Component.translatable("gui.corruption.parchment_slot"),
                    Component.translatable("gui.corruption.parchment_slot_hint").withStyle(net.minecraft.ChatFormatting.GRAY)), mouseX, mouseY);
            return;
        }
        for (String bar : new String[]{TANK, ENERGY, PROGRESS, LOADING}) {
            int[] area = area(bar);
            if (!isHovering(area[0], area[1], area[2], area[3], mouseX, mouseY)) continue;
            if (bar.equals(TANK) || bar.equals(ENERGY) || !ArcaneTransmuterScreen.barsExplainedByRecipeViewer()) {
                List<Component> lines = barTooltip(bar);
                if (!lines.isEmpty()) gui.renderComponentTooltip(font, lines, mouseX, mouseY);
            }
            return;
        }
    }

    /** What the tank or a bar means in numbers; nothing for a bar with nothing going on. */
    public List<Component> barTooltip(String bar) {
        List<Component> lines = new ArrayList<>();
        switch (bar) {
            case TANK -> {
                FluidStack fluid = menu.fluid();
                lines.add(fluid.isEmpty() ? Component.translatable("gui.corruption.tank_empty") : fluid.getHoverName());
                lines.add(Component.translatable("gui.corruption.fluid_amount", fluid.getAmount(), BrassCauldronRules.TANK));
            }
            case ENERGY -> {
                CorruptionEnergyUnit unit = CorruptionEnergyApi.unit(BrassCauldronRules.CAPACITY);
                lines.add(Component.translatable("gui.corruption.corruption_energy"));
                lines.add(Component.translatable("gui.corruption.energy_amount", CorruptionEnergyApi.text(menu.energy(), unit), CorruptionEnergyApi.text(BrassCauldronRules.CAPACITY, unit), unit.symbol()));
            }
            case PROGRESS -> {
                if (menu.cooking()) {
                    lines.add(Component.translatable("gui.corruption.cooking", Math.round(menu.progressFraction() * 100)));
                    if (menu.cost() > 0) lines.add(Component.translatable("gui.corruption.transmutation_cost", CorruptionEnergyApi.format(menu.cost())));
                }
            }
            case LOADING -> { if (menu.burnFraction() > 0) lines.add(Component.translatable("gui.corruption.converting", Math.round(menu.burnFraction() * 100))); }
            default -> {}
        }
        return lines;
    }

    /** Where the tank or a bar sits on the panel, and its size: x, y, width, height. */
    public static int[] area(String bar) {
        if (bar.equals(TANK)) return new int[]{BrassCauldronMenu.TANK_X, BrassCauldronMenu.TANK_Y, BrassCauldronMenu.TANK_WIDTH, BrassCauldronMenu.TANK_HEIGHT};
        GuiTiles.Tile tile = GuiTiles.GUI.sheet().tiles().get(bar);
        int width = tile == null ? 0 : tile.width(), height = tile == null ? 0 : tile.height();
        return switch (bar) {
            case ENERGY -> new int[]{ENERGY_X, ENERGY_Y, width, height};
            case PROGRESS -> new int[]{PROGRESS_X, PROGRESS_Y, width, height};
            case LOADING -> new int[]{LOADING_X, LOADING_Y, width, height};
            default -> new int[]{0, 0, 0, 0};
        };
    }

    @SubscribeEvent public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.BRASS_CAULDRON.get(), BrassCauldronScreen::new);
    }
}
