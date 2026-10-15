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
 * What lies on the arcane pedestal, floating over its plate: the parchment upright, turning slowly and rising
 * and falling, so that an inscribed one shows the string it bears; and under it the wand, lying flat across the
 * plate and turning the other way. What lies there is what the block entity was last told (see
 * {@link ArcanePedestalBlockEntity}).
 */
public final class ArcanePedestalRenderer implements BlockEntityRenderer<ArcanePedestalBlockEntity> {
    /** The plate's height, as the model stands: its top is 14 pixels up. */
    private static final double PLATE = 14 / 16.0;
    /** How high the parchment's middle floats, and how far it rises and falls; how high over the plate the wand lies. */
    private static final double PARCHMENT_HEIGHT = 1.3, BOB = 0.06, WAND_HEIGHT = PLATE + 0.12;
    /** How large each is drawn: the parchment clear of the wand under it, the wand within the plate. */
    private static final float PARCHMENT_SCALE = 0.6F, WAND_SCALE = 0.75F, TURN_DEGREES_PER_TICK = 1.5F;

    private final ItemRenderer items;

    public ArcanePedestalRenderer(BlockEntityRendererProvider.Context context) { this.items = context.getItemRenderer(); }

    @Override public void render(ArcanePedestalBlockEntity pedestal, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        double time = pedestal.getLevel() == null ? 0 : pedestal.getLevel().getGameTime() + partialTick;
        ItemStack parchment = pedestal.parchment(), wand = pedestal.wand();
        if (!parchment.isEmpty()) {
            pose.pushPose();
            pose.translate(0.5, PARCHMENT_HEIGHT + Math.sin(time / 20.0) * BOB, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees((float) (time * TURN_DEGREES_PER_TICK)));
            pose.scale(PARCHMENT_SCALE, PARCHMENT_SCALE, PARCHMENT_SCALE);
            items.renderStatic(parchment, ItemDisplayContext.FIXED, light, overlay, pose, buffers, pedestal.getLevel(), 0);
            pose.popPose();
        }
        if (!wand.isEmpty()) {
            pose.pushPose();
            pose.translate(0.5, WAND_HEIGHT, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees((float) (-time * TURN_DEGREES_PER_TICK * 0.5)));
            pose.mulPose(Axis.XP.rotationDegrees(90));
            pose.scale(WAND_SCALE, WAND_SCALE, WAND_SCALE);
            items.renderStatic(wand, ItemDisplayContext.FIXED, light, overlay, pose, buffers, pedestal.getLevel(), 1);
            pose.popPose();
        }
    }
}
