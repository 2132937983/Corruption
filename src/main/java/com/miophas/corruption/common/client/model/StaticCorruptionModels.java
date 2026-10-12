package com.miophas.corruption.common.client.model;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.registry.GeneratedCorruption;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;

@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class StaticCorruptionModels {
    private static final Map<StaticCorruptedModel, net.minecraft.world.level.block.state.BlockState> MODELS = new HashMap<>();
    private static final Map<StaticCorruptedModel, net.minecraft.world.level.block.state.BlockState> SPECIAL = new HashMap<>();
    @SubscribeEvent public static void sprites(RegisterSpriteSourceTypesEvent event) { event.register(net.minecraft.resources.ResourceLocation.parse("corruption:composed"), CorruptedSpriteSource.TYPE); }
    @SubscribeEvent(priority = EventPriority.LOWEST) public static void bake(ModelEvent.ModifyBakingResult event) {
        MODELS.clear();
        SPECIAL.clear();
        Map<net.minecraft.client.renderer.texture.TextureAtlasSprite, net.minecraft.client.renderer.texture.TextureAtlasSprite> sprites = new java.util.IdentityHashMap<>();
        for (var block : GeneratedCorruption.blocks()) {
            StaticCorruptedModel item = null;
            for (var state : block.getStateDefinition().getPossibleStates()) {
                var source = block.source(state);
                var base = event.getModels().get(BlockModelShaper.stateToModelLocation(source));
                if (base == null) continue;
                var model = new StaticCorruptedModel(base, source, sprite -> sprites.computeIfAbsent(sprite, original -> {
                    var result = event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS, CorruptedSpriteSource.spriteId(original.contents().name())));
                    return result.contents().name().equals(MissingTextureAtlasSprite.getLocation()) ? original : result;
                }));
                event.getModels().put(BlockModelShaper.stateToModelLocation(state), model);
                MODELS.put(model, source);
                if (source.hasBlockEntity()) SPECIAL.put(model, source);
                if (state == block.defaultBlockState()) item = model;
            }
            if (item != null) event.getModels().put(ModelResourceLocation.inventory(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block)), item);
        }
    }
    @SubscribeEvent public static void complete(ModelEvent.BakingCompleted event) {
        MODELS.forEach((model, source) -> {
            var base = event.getModels().get(BlockModelShaper.stateToModelLocation(source));
            if (base != null) model.refresh(base);
        });
    }
    @SubscribeEvent public static void reload(net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new net.minecraft.server.packs.resources.SimplePreparableReloadListener<Void>() {
            @Override protected Void prepare(net.minecraft.server.packs.resources.ResourceManager resources, net.minecraft.util.profiling.ProfilerFiller profiler) { return null; }
            @Override protected void apply(Void ignored, net.minecraft.server.packs.resources.ResourceManager resources, net.minecraft.util.profiling.ProfilerFiller profiler) {
                // Native block-entity renderers reload after ModelManager. Freeze them only when both are ready.
                FrozenBlockEntityModels.clear();
                SPECIAL.forEach((model, source) -> model.refresh(FrozenBlockEntityModels.bake(source)));
            }
        });
    }
}
