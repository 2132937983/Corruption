package com.miophas.corruption.common.client;

import com.miophas.corruption.common.blockentity.ArcaneChargerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * What lies on the arcane charger to be charged: its item model, held over the plate and turning slowly, so
 * whoever set it there sees it working. What lies there is what the block entity was last told (see
 * {@link ArcaneChargerBlockEntity}); its energy bar, drawn under the item, shows how far the charging has come.
 */
public final class ArcaneChargerRenderer implements BlockEntityRenderer<ArcaneChargerBlockEntity> {
    /** How high over the block it is held, and how far it rises and falls. */
    private static final double HEIGHT = 1.15, BOB = 0.05;
    private static final float TURN_DEGREES_PER_TICK = 1.2F;

    private final ItemRenderer items;

    public ArcaneChargerRenderer(BlockEntityRendererProvider.Context context) { this.items = context.getItemRenderer(); }

    @Override public void render(ArcaneChargerBlockEntity charger, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        ItemStack charge = charger.charge();
        if (charge.isEmpty()) return;
        double time = charger.getLevel() == null ? 0 : charger.getLevel().getGameTime() + partialTick;
        pose.pushPose();
        pose.translate(0.5, HEIGHT + Math.sin(time / 20.0) * BOB, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees((float) (time * TURN_DEGREES_PER_TICK)));
        items.renderStatic(charge, ItemDisplayContext.GROUND, light, overlay, pose, buffers, charger.getLevel(), 0);
        pose.popPose();
    }
}
