package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.cooking.BrassCauldronBlockEntity;
import com.miophas.corruption.common.cooking.BrassCauldronMenu;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.core.cooking.BrassCauldronRules;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * What can be seen inside a brass cauldron from above: the fluid in its tank, its still surface tinted as the
 * fluid is, rising from the floor to just under the rim as the tank fills (a fluid that glows, such as lava,
 * glows here too); and the seven things cooking in it, laid out as the screen lays them out, a hexagon round a
 * middle. With fluid in it they float on the surface, half under it, bobbing and slowly turning; dry, they lie
 * on the floor.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class BrassCauldronRenderer implements BlockEntityRenderer<BrassCauldronBlockEntity> {
    /** The inside of the cauldron, in pixels: between its walls, from its floor to just under its rim. */
    public static final float INNER_MIN = 2, INNER_MAX = 14, FLOOR = 4, BRIM = 15;
    /** The least the surface stands above the floor, so that a drop of fluid never lies in the floor's own plane and flickers with it. */
    public static final float LOWEST = FLOOR + .5F;
    /** How big the things in it are drawn, in blocks, and how far the hexagon's corners lie from its middle, in pixels. */
    public static final float ITEM_SIZE = .22F, ITEM_SPREAD = 3.6F;

    private final ItemRenderer items;

    public BrassCauldronRenderer(BlockEntityRendererProvider.Context context) { items = context.getItemRenderer(); }

    /** How high the surface stands, in blocks from the cauldron's foot, for a tank this full. */
    public static float surface(int amount) {
        return (LOWEST + (BRIM - LOWEST) * Math.min(1, amount / (float) BrassCauldronRules.TANK)) / 16F;
    }

    /** Where the thing in an input slot lies across the cauldron's inside, in pixels: x, then z. The screen's hexagon, shrunk to fit. */
    public static float[] spot(int slot) {
        int[] middle = BrassCauldronMenu.INPUT_SLOTS[0], at = BrassCauldronMenu.INPUT_SLOTS[slot];
        float scale = ITEM_SPREAD / 22F;
        return new float[]{8 + (at[0] - middle[0]) * scale, 8 + (at[1] - middle[1]) * scale};
    }

    @Override public void render(BrassCauldronBlockEntity cauldron, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        FluidStack fluid = cauldron.fluid();
        float time = cauldron.getLevel() == null ? 0 : cauldron.getLevel().getGameTime() + partialTick;
        if (!fluid.isEmpty()) renderFluid(fluid, pose, buffers, light, overlay);
        List<ItemStack> things = cauldron.shownItems();
        for (int slot = 0; slot < things.size(); slot++) {
            ItemStack thing = things.get(slot);
            if (thing.isEmpty()) continue;
            float[] at = spot(slot);
            float bob = fluid.isEmpty() ? 0 : (float) Math.sin(time / 12 + slot * 1.7) * .25F;
            float height = fluid.isEmpty() ? (FLOOR + .3F) / 16 : surface(fluid.getAmount()) + bob / 16;
            pose.pushPose();
            pose.translate(at[0] / 16, height, at[1] / 16);
            pose.mulPose(Axis.YP.rotationDegrees(slot * 51 + (fluid.isEmpty() ? 0 : time * .6F)));
            pose.mulPose(Axis.XP.rotationDegrees(90));
            pose.scale(ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
            items.renderStatic(thing, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buffers, cauldron.getLevel(), (int) cauldron.getBlockPos().asLong() + slot);
            pose.popPose();
        }
    }

    private static void renderFluid(FluidStack fluid, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        IClientFluidTypeExtensions look = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(look.getStillTexture(fluid));
        int tint = look.getTintColor(fluid), glow = fluid.getFluid().getFluidType().getLightLevel(fluid);
        if ((tint >>> 24) == 0) tint |= 0xFF000000;
        int lit = glow > 0 ? LightTexture.pack(Math.max(glow, LightTexture.block(light)), LightTexture.sky(light)) : light;
        float y = surface(fluid.getAmount()), min = INNER_MIN / 16F, max = INNER_MAX / 16F;
        float u0 = sprite.getU(INNER_MIN / 16F), u1 = sprite.getU(INNER_MAX / 16F), v0 = sprite.getV(INNER_MIN / 16F), v1 = sprite.getV(INNER_MAX / 16F);
        VertexConsumer buffer = buffers.getBuffer(Sheets.translucentCullBlockSheet());
        PoseStack.Pose at = pose.last();
        buffer.addVertex(at, min, y, min).setColor(tint).setUv(u0, v0).setOverlay(overlay).setLight(lit).setNormal(at, 0, 1, 0);
        buffer.addVertex(at, min, y, max).setColor(tint).setUv(u0, v1).setOverlay(overlay).setLight(lit).setNormal(at, 0, 1, 0);
        buffer.addVertex(at, max, y, max).setColor(tint).setUv(u1, v1).setOverlay(overlay).setLight(lit).setNormal(at, 0, 1, 0);
        buffer.addVertex(at, max, y, min).setColor(tint).setUv(u1, v0).setOverlay(overlay).setLight(lit).setNormal(at, 0, 1, 0);
    }

    @SubscribeEvent public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BRASS_CAULDRON.get(), BrassCauldronRenderer::new);
    }
}
