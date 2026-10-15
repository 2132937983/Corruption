package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.blockentity.ArcaneChargerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

/**
 * What the arcane charger shows of its work, drawn every frame so it holds still and moves smoothly: the item
 * lying on it, held over the block and turning slowly; and about the item a ring of sparks, as many as the
 * charger's store is full (see {@link ArcaneChargerBlockEntity#sparks()}), glowing by their own light, rippling
 * up and down and twinkling, the ring turning faster while the item is charged. The sparks that fly from the
 * ring into the item are particles the block entity sends.
 */
public final class ArcaneChargerRenderer implements BlockEntityRenderer<ArcaneChargerBlockEntity> {
    private static final ResourceLocation SPARK = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/particle/corruption_spark_particle.png");
    /** How far the item rises and falls, and how fast it turns. */
    private static final double BOB = 0.05;
    private static final float TURN_DEGREES_PER_TICK = 1.2F;
    /** How wide a spark of the ring is drawn, and how much it swells and shrinks as it twinkles. */
    private static final float SPARK_SIZE = .085F, TWINKLE = .25F;

    private final ItemRenderer items;

    public ArcaneChargerRenderer(BlockEntityRendererProvider.Context context) { this.items = context.getItemRenderer(); }

    @Override public void render(ArcaneChargerBlockEntity charger, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        double time = charger.getLevel() == null ? 0 : charger.getLevel().getGameTime() + partialTick;
        ItemStack charge = charger.charge();
        if (!charge.isEmpty()) {
            pose.pushPose();
            pose.translate(0.5, ArcaneChargerBlockEntity.ITEM_HEIGHT + Math.sin(time / 20.0) * BOB, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees((float) (time * TURN_DEGREES_PER_TICK)));
            items.renderStatic(charge, ItemDisplayContext.GROUND, light, overlay, pose, buffers, charger.getLevel(), 0);
            pose.popPose();
        }
        int sparks = charger.sparks();
        if (sparks == 0) return;
        VertexConsumer glow = buffers.getBuffer(RenderType.eyes(SPARK));
        double turned = charger.turned(partialTick);
        for (int spark = 0; spark < sparks; spark++) {
            double[] at = ArcaneChargerBlockEntity.ringPoint(spark, sparks, turned, time);
            float size = SPARK_SIZE * (1 + TWINKLE * (float) Math.sin(time / 3.0 + spark * 2.3));
            pose.pushPose();
            pose.translate(at[0], at[1], at[2]);
            pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            pose.scale(size, size, size);
            facing(glow, pose.last());
            pose.popPose();
        }
    }

    /** A square of the spark's picture facing whoever looks; drawn both ways round, so it shows whichever way the quad is culled. */
    private static void facing(VertexConsumer buffer, PoseStack.Pose at) {
        float[][] corners = {{-1, -1, 0, 1}, {-1, 1, 0, 0}, {1, 1, 1, 0}, {1, -1, 1, 1}};
        for (int corner = 0; corner < 4; corner++) vertex(buffer, at, corners[corner]);
        for (int corner = 3; corner >= 0; corner--) vertex(buffer, at, corners[corner]);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose at, float[] corner) {
        buffer.addVertex(at, corner[0], corner[1], 0).setColor(0xFFFFFFFF).setUv(corner[2], corner[3])
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, 0, 1);
    }

    /** The ring and the item stand over the block; they are drawn whenever any of that could be seen. */
    @Override public AABB getRenderBoundingBox(ArcaneChargerBlockEntity charger) {
        return new AABB(charger.getBlockPos()).expandTowards(0, 1, 0).inflate(.1);
    }
}
