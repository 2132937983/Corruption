package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.altar.AltarOfferingBlockEntity;
import com.miophas.corruption.common.altar.AltarPillarBlockEntity;
import com.miophas.corruption.common.entity.CorruptLightning;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.altar.AltarLayout;
import com.miophas.corruption.core.altar.AltarState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.joml.Matrix4f;

/** How the altar is drawn beyond its block models: what its pillars hold, its magic circle, and its lightning. */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class AltarRenderers {
    /** The hexagram circle drawn beneath a formed altar: textures/misc/altar_circle.png, CIRCLE_BLOCKS blocks across, centred on the offering. */
    public static final ResourceLocation CIRCLE = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "textures/misc/altar_circle.png");
    public static final float CIRCLE_BLOCKS = 10;

    /**
     * Where a pillar's corner of the hexagram lies in the circle's picture, as a share of its width: across, then
     * down. Up is the way the altar faces; the circle is turned with it.
     */
    public static float[] circlePoint(int slot) {
        return new float[]{.5F + AltarLayout.east(slot) / CIRCLE_BLOCKS, .5F - AltarLayout.north(slot) / CIRCLE_BLOCKS};
    }

    /** What a pillar holds, floating over it and slowly turning. */
    public static final class Pillar implements BlockEntityRenderer<AltarPillarBlockEntity> {
        private final ItemRenderer items;
        public Pillar(BlockEntityRendererProvider.Context context) { items = context.getItemRenderer(); }

        @Override public void render(AltarPillarBlockEntity pillar, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            if (pillar.isEmpty() || pillar.getLevel() == null) return;
            float time = pillar.getLevel().getGameTime() + partialTick;
            pose.pushPose();
            pose.translate(.5, 1.2 + Math.sin(time / 10) * .06, .5);
            pose.mulPose(Axis.YP.rotationDegrees(time * 2));
            items.renderStatic(pillar.item(), ItemDisplayContext.GROUND, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pose, buffers, pillar.getLevel(), (int) pillar.getBlockPos().asLong());
            pose.popPose();
        }
    }

    /**
     * The magic circle beneath a formed altar, glowing; brighter, and pulsing, while it binds. It is turned the way
     * the altar faces, so that its six points lie under the six pillars, the first marked by a second ring.
     */
    public static final class Offering implements BlockEntityRenderer<AltarOfferingBlockEntity> {
        public Offering(BlockEntityRendererProvider.Context context) {}

        @Override public void render(AltarOfferingBlockEntity offering, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            AltarState state = offering.state();
            if (!state.showsCircle() || offering.getLevel() == null) return;
            float time = offering.getLevel().getGameTime() + partialTick;
            float glow = state.binds() ? .8F + .2F * (float) Math.sin(time / 8) : .55F;
            int colour = 0xFF000000 | Math.round(255 * glow) << 16 | Math.round(255 * glow) << 8 | Math.round(255 * glow);
            float half = CIRCLE_BLOCKS / 2, min = .5F - half, max = .5F + half, y = .02F;
            VertexConsumer buffer = buffers.getBuffer(RenderType.eyes(CIRCLE));
            pose.pushPose();
            pose.translate(.5, 0, .5);
            pose.mulPose(Axis.YP.rotationDegrees(-90F * offering.facing().quarterTurns()));
            pose.translate(-.5, 0, -.5);
            PoseStack.Pose at = pose.last();
            buffer.addVertex(at, min, y, min).setColor(colour).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, 1, 0);
            buffer.addVertex(at, min, y, max).setColor(colour).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, 1, 0);
            buffer.addVertex(at, max, y, max).setColor(colour).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, 1, 0);
            buffer.addVertex(at, max, y, min).setColor(colour).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(at, 0, 1, 0);
            pose.popPose();
        }

        /** The circle reaches well beyond the block; it is drawn whenever any of it could be seen. */
        @Override public AABB getRenderBoundingBox(AltarOfferingBlockEntity offering) {
            return new AABB(offering.getBlockPos()).inflate(CIRCLE_BLOCKS / 2, 1, CIRCLE_BLOCKS / 2);
        }
        @Override public boolean shouldRenderOffScreen(AltarOfferingBlockEntity offering) { return true; }
    }

    /**
     * Corrupted lightning: drawn as the game draws lightning, a branching bolt of glowing quads reaching up from
     * where it strikes, but violet.
     */
    public static final class Lightning extends EntityRenderer<CorruptLightning> {
        private static final float RED = .62F, GREEN = .26F, BLUE = .82F;

        public Lightning(EntityRendererProvider.Context context) { super(context); }

        @Override public void render(CorruptLightning bolt, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
            float[] xs = new float[8], zs = new float[8];
            float x = 0, z = 0;
            RandomSource random = RandomSource.create(bolt.seed);
            for (int step = 7; step >= 0; step--) {
                xs[step] = x; zs[step] = z;
                x += random.nextInt(11) - 5;
                z += random.nextInt(11) - 5;
            }
            VertexConsumer buffer = buffers.getBuffer(RenderType.lightning());
            Matrix4f matrix = pose.last().pose();
            for (int layer = 0; layer < 4; layer++) {
                RandomSource branchRandom = RandomSource.create(bolt.seed);
                for (int branch = 0; branch < 3; branch++) {
                    int top = branch > 0 ? 7 - branch : 7, bottom = branch > 0 ? top - 2 : 0;
                    float bx = xs[top] - x, bz = zs[top] - z;
                    for (int step = top; step >= bottom; step--) {
                        float fromX = bx, fromZ = bz;
                        int spread = branch == 0 ? 11 : 31;
                        bx += branchRandom.nextInt(spread) - spread / 2;
                        bz += branchRandom.nextInt(spread) - spread / 2;
                        float lower = .1F + layer * .2F, upper = .1F + layer * .2F;
                        if (branch == 0) { lower *= step * .1F + 1; upper *= (step - 1) * .1F + 1; }
                        quad(matrix, buffer, bx, bz, step, fromX, fromZ, lower, upper, false, false, true, false);
                        quad(matrix, buffer, bx, bz, step, fromX, fromZ, lower, upper, true, false, true, true);
                        quad(matrix, buffer, bx, bz, step, fromX, fromZ, lower, upper, true, true, false, true);
                        quad(matrix, buffer, bx, bz, step, fromX, fromZ, lower, upper, false, true, false, false);
                    }
                }
            }
        }

        /** One face of one segment of the bolt, from step to step + 1, sixteen blocks a step. */
        private static void quad(Matrix4f matrix, VertexConsumer buffer, float x1, float z1, int step, float x2, float z2, float lower, float upper,
                                 boolean a, boolean b, boolean c, boolean d) {
            buffer.addVertex(matrix, x1 + (a ? upper : -upper), step * 16, z1 + (b ? upper : -upper)).setColor(RED, GREEN, BLUE, .3F);
            buffer.addVertex(matrix, x2 + (a ? lower : -lower), (step + 1) * 16, z2 + (b ? lower : -lower)).setColor(RED, GREEN, BLUE, .3F);
            buffer.addVertex(matrix, x2 + (c ? lower : -lower), (step + 1) * 16, z2 + (d ? lower : -lower)).setColor(RED, GREEN, BLUE, .3F);
            buffer.addVertex(matrix, x1 + (c ? upper : -upper), step * 16, z1 + (d ? upper : -upper)).setColor(RED, GREEN, BLUE, .3F);
        }

        @Override public ResourceLocation getTextureLocation(CorruptLightning bolt) { return TextureAtlas.LOCATION_BLOCKS; }
    }

    @SubscribeEvent public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ALTAR_PILLAR.get(), Pillar::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ALTAR_OFFERING.get(), Offering::new);
        event.registerEntityRenderer(ModEntities.CORRUPT_LIGHTNING.get(), Lightning::new);
    }

    private AltarRenderers() {}
}
