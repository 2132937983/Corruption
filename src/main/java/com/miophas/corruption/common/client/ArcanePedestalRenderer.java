package com.miophas.corruption.common.client;

import com.miophas.corruption.common.arcane.ArcanePedestalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * The parchment lying on the arcane pedestal: the item model of whatever it bears, floating over the block and
 * turning slowly, so an inscribed parchment raises the string it carries the way the lectern's key does. What
 * lies there is what the block entity was last told (see {@link ArcanePedestalBlockEntity}).
 */
public final class ArcanePedestalRenderer implements BlockEntityRenderer<ArcanePedestalBlockEntity> {
    /** How high over the block it floats, and how far it rises and falls. */
    private static final double HEIGHT = 1.15, BOB = 0.06;
    private static final float TURN_DEGREES_PER_TICK = 1.5F;

    private final ItemRenderer items;

    public ArcanePedestalRenderer(BlockEntityRendererProvider.Context context) { this.items = context.getItemRenderer(); }

    @Override public void render(ArcanePedestalBlockEntity pedestal, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        ItemStack parchment = pedestal.parchment();
        if (parchment.isEmpty()) return;
        double time = pedestal.getLevel() == null ? 0 : pedestal.getLevel().getGameTime() + partialTick;
        pose.pushPose();
        pose.translate(0.5, HEIGHT + Math.sin(time / 20.0) * BOB, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees((float) (time * TURN_DEGREES_PER_TICK)));
        items.renderStatic(parchment, ItemDisplayContext.FIXED, light, overlay, pose, buffers, pedestal.getLevel(), 0);
        pose.popPose();
    }
}
