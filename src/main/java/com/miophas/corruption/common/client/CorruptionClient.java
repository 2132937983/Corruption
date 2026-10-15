package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.block.InfectedBlock;
import com.miophas.corruption.common.client.model.CorruptionModelLoader;
import com.miophas.corruption.common.client.model.SourceRenderView;
import com.miophas.corruption.common.client.particle.CorruptionScreamParticle;
import com.miophas.corruption.common.item.InfectedBlockItem;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.client.entity.CorruptedRemnantRenderer;
import com.miophas.corruption.common.client.entity.CrimsonTextures;
import com.miophas.corruption.common.client.entity.CorruptedZombieModel;
import com.miophas.corruption.common.client.entity.CorruptedZombieArmorModel;
import com.miophas.corruption.common.client.entity.CorruptedSkeletonModel;
import com.miophas.corruption.common.client.entity.CorruptedSkeletonArmorModel;
import com.miophas.corruption.common.client.entity.CorruptedSpiderModel;
import com.miophas.corruption.common.client.entity.CorruptedEndermanModel;
import com.miophas.corruption.common.client.entity.CorruptedCreeperModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class CorruptionClient {
    @SubscribeEvent public static void gui(net.neoforged.neoforge.client.event.RegisterGuiLayersEvent event){
        event.registerAbove(net.neoforged.neoforge.client.gui.VanillaGuiLayers.AIR_LEVEL,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"corruption_meter"),CorruptionHud::render);
    }
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        ModList.get().getModContainerById(Corruption.MOD_ID).orElseThrow()
                .registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        event.enqueueWork(ArcaneTooltip::registerItemProperties);
    }

    @SubscribeEvent
    public static void entityLayers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (var kind : com.miophas.corruption.common.client.entity.CorruptedWildlifeModel.Kind.values())
            event.registerLayerDefinition(kind.layer, kind::createLayer);
        for (var kind : com.miophas.corruption.common.client.entity.CorruptedNetherModel.Kind.values())
            event.registerLayerDefinition(kind.layer, kind::createLayer);
        for (var kind : com.miophas.corruption.common.client.entity.CorruptedVillageModel.Kind.values())
            event.registerLayerDefinition(kind.layer, kind::createLayer);
        for (var kind : com.miophas.corruption.common.client.entity.CorruptedLivestockModel.Kind.values())
            event.registerLayerDefinition(kind.layer, kind::createLayer);
        event.registerLayerDefinition(CorruptedZombieModel.LAYER_LOCATION, CorruptedZombieModel::createBodyLayer);
        event.registerLayerDefinition(CorruptedZombieArmorModel.INNER, () -> CorruptedZombieArmorModel.createLayer(0.5F));
        event.registerLayerDefinition(CorruptedZombieArmorModel.OUTER, () -> CorruptedZombieArmorModel.createLayer(1.0F));
        event.registerLayerDefinition(CorruptedSkeletonModel.LAYER_LOCATION, CorruptedSkeletonModel::createBodyLayer);
        event.registerLayerDefinition(CorruptedSkeletonArmorModel.INNER, () -> CorruptedSkeletonArmorModel.createLayer(0.5F));
        event.registerLayerDefinition(CorruptedSkeletonArmorModel.OUTER, () -> CorruptedSkeletonArmorModel.createLayer(1.0F));
        event.registerLayerDefinition(CorruptedSpiderModel.LAYER_LOCATION, CorruptedSpiderModel::createBodyLayer);
        event.registerLayerDefinition(CorruptedEndermanModel.LAYER_LOCATION, CorruptedEndermanModel::createBodyLayer);
        event.registerLayerDefinition(CorruptedCreeperModel.LAYER_LOCATION, CorruptedCreeperModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.MaliceVineModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.MaliceVineModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.GreedModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.GreedModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.BaalModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.BaalModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.AgaresModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.AgaresModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void entities(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CORRUPTED_REMNANT.get(), CorruptedRemnantRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_DRAGON.get(), CorruptedRemnantRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_DRAGON_FIREBALL.get(), net.minecraft.client.renderer.entity.DragonFireballRenderer::new);
        event.registerEntityRenderer(ModEntities.SPELL_BOLT.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ARCANE_PEDESTAL.get(), ArcanePedestalRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_DRAGON_BREATH.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.MALICE_VINE.get(), com.miophas.corruption.common.client.entity.MaliceVineRenderer::new);
        event.registerEntityRenderer(ModEntities.GREED.get(), com.miophas.corruption.common.client.entity.GreedRenderer::new);
        event.registerEntityRenderer(ModEntities.GREED_PROJECTILE.get(), com.miophas.corruption.common.client.entity.GreedProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.BAAL.get(), com.miophas.corruption.common.client.entity.BaalRenderer::new);
        event.registerEntityRenderer(ModEntities.AGARES.get(), com.miophas.corruption.common.client.entity.AgaresRenderer::new);
    }

    @SubscribeEvent
    public static void reload(net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(CrimsonTextures.INSTANCE);
        event.registerReloadListener(GuiTiles.GUI);
    }

    @SubscribeEvent
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.CORRUPTION_SCREAM.get(), CorruptionScreamParticle.Provider::new);
        event.registerSpriteSet(ModParticles.CORRUPTION_PARTICLE.get(), com.miophas.corruption.common.client.particle.CorruptionParticle.Provider::new);
    }

    @SubscribeEvent
    public static void loaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corrupted"), new CorruptionModelLoader());
    }

    @SubscribeEvent
    public static void blockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tint) -> event.getBlockColors().getColor(
                ((com.miophas.corruption.common.block.StaticCorruptedBlock) state.getBlock()).source(state), level, pos, tint),
                com.miophas.corruption.common.registry.GeneratedCorruption.blocks().toArray(net.minecraft.world.level.block.Block[]::new));
        event.register((state, level, pos, tint) -> {
            var source = level != null && pos != null ? InfectedBlock.source(level, pos) : Blocks.STONE.defaultBlockState();
            return event.getBlockColors().getColor(source, level == null ? null : new SourceRenderView(level), pos, tint);
        }, ModBlocks.INFECTED_BLOCK.get());
    }

    @SubscribeEvent
    public static void itemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> {
            var block = (com.miophas.corruption.common.block.StaticCorruptedBlock) ((net.minecraft.world.item.BlockItem) stack.getItem()).getBlock();
            return event.getItemColors().getColor(new ItemStack(block.original()), tint);
        }, com.miophas.corruption.common.registry.GeneratedCorruption.blocks().stream().map(net.minecraft.world.level.block.Block::asItem).toArray(net.minecraft.world.item.Item[]::new));
        event.register((stack, tint) -> event.getItemColors().getColor(new ItemStack(InfectedBlockItem.source(stack).getBlock()), tint),
                ModItems.INFECTED_BLOCK.get());
    }
}
