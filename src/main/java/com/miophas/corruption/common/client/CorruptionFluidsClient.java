package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.client.model.CorruptedSpriteSource;
import com.miophas.corruption.common.registry.ModFluids;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class CorruptionFluidsClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(extension(false), ModFluids.WATER_TYPE.get());
        event.registerFluidType(extension(true), ModFluids.LAVA_TYPE.get());
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override public ResourceLocation getStillTexture() { return FLOWING_CORRUPTION_STILL; }
            @Override public ResourceLocation getFlowingTexture() { return FLOWING_CORRUPTION_FLOW; }
            @Override public ResourceLocation getRenderOverlayTexture(net.minecraft.client.Minecraft minecraft) { return SUBMERGED_OVERLAY; }
            // Submerged, the world is a few blocks of dark violet murk.
            @Override public org.joml.Vector3f modifyFogColor(net.minecraft.client.Camera camera, float partialTick, net.minecraft.client.multiplayer.ClientLevel level,
                    int renderDistance, float darkenWorldAmount, org.joml.Vector3f fogColor) { return new org.joml.Vector3f(.14F, .12F, .23F); }
            @Override public void modifyFogRender(net.minecraft.client.Camera camera, net.minecraft.client.renderer.FogRenderer.FogMode mode, float renderDistance,
                    float partialTick, float nearDistance, float farDistance, com.mojang.blaze3d.shaders.FogShape shape) {
                com.mojang.blaze3d.systems.RenderSystem.setShaderFogStart(.25F);
                com.mojang.blaze3d.systems.RenderSystem.setShaderFogEnd(4F);
            }
        }, ModFluids.FLOWING_CORRUPTION_TYPE.get());
    }
    private static final ResourceLocation FLOWING_CORRUPTION_STILL = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "block/fluid/flowing_corruption_still");
    private static final ResourceLocation FLOWING_CORRUPTION_FLOW = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "block/fluid/flowing_corruption_flow");
    private static final ResourceLocation SUBMERGED_OVERLAY = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
    private static IClientFluidTypeExtensions extension(boolean lava) {
        return new IClientFluidTypeExtensions() {
            @Override public ResourceLocation getStillTexture() { return CorruptedSpriteSource.fluidSpriteId(lava ? "lava_still" : "water_still"); }
            @Override public ResourceLocation getFlowingTexture() { return CorruptedSpriteSource.fluidSpriteId(lava ? "lava_flow" : "water_flow"); }
            @Override public ResourceLocation getOverlayTexture() { return lava ? null : CorruptedSpriteSource.fluidSpriteId("water_overlay"); }
            // White tint prevents the biome water color from adding saturation back to the baked greyscale texture.
            @Override public int getTintColor() { return 0xFFFFFFFF; }
        };
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.WATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_WATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.LAVA.get(), RenderType.solid());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_LAVA.get(), RenderType.solid());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_CORRUPTION.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_CORRUPTION_FLOWING.get(), RenderType.translucent());
        });
    }
}
