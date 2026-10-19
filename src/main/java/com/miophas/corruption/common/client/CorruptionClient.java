package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.block.InfectedBlock;
import com.miophas.corruption.common.client.entity.CorruptedCreeperModel;
import com.miophas.corruption.common.client.entity.CorruptedEndermanModel;
import com.miophas.corruption.common.client.entity.CorruptedRemnantRenderer;
import com.miophas.corruption.common.client.entity.CorruptedSkeletonArmorModel;
import com.miophas.corruption.common.client.entity.CorruptedSkeletonModel;
import com.miophas.corruption.common.client.entity.CorruptedSpiderModel;
import com.miophas.corruption.common.client.entity.CorruptedZombieArmorModel;
import com.miophas.corruption.common.client.entity.CorruptedZombieModel;
import com.miophas.corruption.common.client.entity.CrimsonTextures;
import com.miophas.corruption.common.client.model.CorruptionModelLoader;
import com.miophas.corruption.common.client.model.SourceRenderView;
import com.miophas.corruption.common.client.particle.CorruptionScreamParticle;
import com.miophas.corruption.common.item.InfectedBlockItem;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

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
        com.miophas.corruption.common.client.visual.SpellVisualRenderer.registerBuiltIn();
        com.miophas.corruption.api.corruption.client.CorruptedTextures.install(new com.miophas.corruption.api.corruption.client.CorruptedTextures.Provider() {
            public net.minecraft.resources.ResourceLocation texture(net.minecraft.resources.ResourceLocation source) { return CrimsonTextures.INSTANCE.texture(source); }
            public net.minecraft.resources.ResourceLocation playerTexture(net.minecraft.resources.ResourceLocation source, int progress) { return CrimsonTextures.INSTANCE.texture(source, progress); }
            public net.minecraft.client.renderer.RenderType layer(net.minecraft.client.renderer.RenderType source, net.minecraft.resources.ResourceLocation playerSkin, int progress) { return CrimsonTextures.INSTANCE.layer(source, playerSkin, progress); }
        });
        com.miophas.corruption.api.demon.client.BossDeathVisuals.install((boss, partial, camera, pose, buffers, far) -> {
            if (boss instanceof com.miophas.corruption.api.demon.DemonBoss descriptor)
                com.miophas.corruption.common.client.visual.DeathVisual.draw(
                        com.miophas.corruption.common.client.visual.DeathVisual.Dying.of(boss, descriptor, partial, camera), pose, buffers, far);
        });
        com.miophas.corruption.common.entity.SpellBeamEntity.firing = com.miophas.corruption.common.client.visual.CameraShake::rumble;
        MindControlClient.install();
        com.miophas.corruption.api.corruption.RemnantAnimationApi.installPlayback(new com.miophas.corruption.common.client.entity.SourceAnimationPlayback());
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
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.AndromaliusModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.AndromaliusModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.SpellBoltModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.SpellBoltModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.DantalionBookModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.DantalionBookModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.SeereModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.SeereModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.AmdusiasModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.AmdusiasModel::createBodyLayer);
        com.miophas.corruption.common.client.entity.SolomonModels.register(event);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.DantalionMirrorModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.DantalionMirrorModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.DecarabiaStarModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.DecarabiaStarModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.DecarabiaHawkModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.DecarabiaHawkModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.BelialFairModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.BelialFairModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.BelialHollowModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.BelialHollowModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.FieryChariotModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.FieryChariotModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedAberrationModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedAberrationModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedScreecherModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedScreecherModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedLurkerModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedLurkerModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedBruteModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedBruteModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedHowlerModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedHowlerModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedStalkerModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedStalkerModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedBloaterModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedBloaterModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedMortarModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedMortarModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedCrawlerModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedCrawlerModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedGazerModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedGazerModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedHivemotherModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedHivemotherModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedDrifterModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedDrifterModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedMawModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedMawModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedNaiadModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedNaiadModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedMindweaverModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedMindweaverModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedBlighthoundModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedBlighthoundModel::createBodyLayer);
        event.registerLayerDefinition(com.miophas.corruption.common.client.entity.CorruptedSowerModel.LAYER_LOCATION,
                com.miophas.corruption.common.client.entity.CorruptedSowerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void entities(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CORRUPTED_REMNANT.get(), CorruptedRemnantRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_DRAGON.get(), CorruptedRemnantRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_DRAGON_FIREBALL.get(), net.minecraft.client.renderer.entity.DragonFireballRenderer::new);
        // A spell bolt glows by its own light, as a fireball does.
        event.registerEntityRenderer(ModEntities.SPELL_BOLT.get(), com.miophas.corruption.common.client.entity.SpellBoltRenderer::new);
        // A spell's effect, drawn by whatever is registered for it (see SpellVisualRenderer.registerBuiltIn).
        event.registerEntityRenderer(ModEntities.SPELL_VISUAL.get(), com.miophas.corruption.common.client.visual.SpellVisualRenderer::new);
        // A spell's beam, drawn from its caster's hand along their gaze as this client sees them.
        event.registerEntityRenderer(ModEntities.SPELL_BEAM.get(), com.miophas.corruption.common.client.visual.SpellBeamRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ARCANE_PEDESTAL.get(), ArcanePedestalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ARCANE_CHARGER.get(), ArcaneChargerRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_DRAGON_BREATH.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.MALICE_VINE.get(), com.miophas.corruption.common.client.entity.MaliceVineRenderer::new);
        event.registerEntityRenderer(ModEntities.GREED.get(), com.miophas.corruption.common.client.entity.GreedRenderer::new);
        event.registerEntityRenderer(ModEntities.GREED_PROJECTILE.get(), com.miophas.corruption.common.client.entity.GreedProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.BAAL.get(), com.miophas.corruption.common.client.entity.BaalRenderer::new);
        event.registerEntityRenderer(ModEntities.AGARES.get(), com.miophas.corruption.common.client.entity.AgaresRenderer::new);
        event.registerEntityRenderer(ModEntities.ANDROMALIUS.get(), com.miophas.corruption.common.client.entity.AndromaliusRenderer::new);
        event.registerEntityRenderer(ModEntities.DANTALION.get(), com.miophas.corruption.common.client.entity.DantalionRenderer::new);
        event.registerEntityRenderer(ModEntities.SEERE.get(), com.miophas.corruption.common.client.entity.SeereRenderer::new);
        event.registerEntityRenderer(ModEntities.AMDUSIAS.get(), com.miophas.corruption.common.client.entity.AmdusiasRenderer::new);
        event.registerEntityRenderer(ModEntities.SOLOMON.get(), com.miophas.corruption.common.client.entity.SolomonRenderer::new);
        event.registerEntityRenderer(ModEntities.SOLOMON_WYRM.get(), com.miophas.corruption.common.client.entity.SolomonWyrmRenderer::new);
        event.registerEntityRenderer(ModEntities.SOLOMON_HAND.get(), com.miophas.corruption.common.client.entity.SolomonHandRenderer::new);
        event.registerEntityRenderer(ModEntities.SOLOMON_DRAKE.get(), com.miophas.corruption.common.client.entity.SolomonDrakeRenderer::new);
        event.registerEntityRenderer(ModEntities.SOLOMON_FIREBALL.get(), context -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(context, 3, true));
        event.registerEntityRenderer(ModEntities.DECARABIA.get(), com.miophas.corruption.common.client.entity.DecarabiaRenderer::new);
        event.registerEntityRenderer(ModEntities.BELIAL.get(), com.miophas.corruption.common.client.entity.BelialRenderer::new);
        event.registerEntityRenderer(ModEntities.FIERY_CHARIOT.get(), com.miophas.corruption.common.client.entity.FieryChariotRenderer::new);
        event.registerEntityRenderer(ModEntities.DEMON_BEAM.get(), com.miophas.corruption.common.client.visual.DemonBeamRenderer::new);
        event.registerEntityRenderer(ModEntities.DEMON_BOLT.get(), com.miophas.corruption.common.client.entity.DemonBoltRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_ABERRATION.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::aberration);
        event.registerEntityRenderer(ModEntities.CORRUPTED_SCREECHER.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::screecher);
        event.registerEntityRenderer(ModEntities.CORRUPTED_LURKER.get(), com.miophas.corruption.common.client.entity.CorruptedLurkerRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_BRUTE.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::brute);
        event.registerEntityRenderer(ModEntities.CORRUPTED_HOWLER.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::howler);
        event.registerEntityRenderer(ModEntities.CORRUPTED_STALKER.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::stalker);
        event.registerEntityRenderer(ModEntities.CORRUPTED_BLOATER.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::bloater);
        event.registerEntityRenderer(ModEntities.CORRUPTED_MORTAR.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::mortar);
        event.registerEntityRenderer(ModEntities.CORRUPTED_CRAWLER.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::crawler);
        event.registerEntityRenderer(ModEntities.CORRUPTED_GAZER.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::gazer);
        event.registerEntityRenderer(ModEntities.CORRUPTED_HIVEMOTHER.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::hivemother);
        event.registerEntityRenderer(ModEntities.CORRUPTED_DRIFTER.get(), com.miophas.corruption.common.client.entity.CorruptedDrifterRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_MAW.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::maw);
        event.registerEntityRenderer(ModEntities.CORRUPTED_NAIAD.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::naiad);
        event.registerEntityRenderer(ModEntities.CORRUPTED_MINDWEAVER.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::mindweaver);
        event.registerEntityRenderer(ModEntities.CORRUPTED_BLIGHTHOUND.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::blighthound);
        event.registerEntityRenderer(ModEntities.CORRUPTED_SOWER.get(), com.miophas.corruption.common.client.entity.CorruptionEliteRenderer::sower);
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
        event.registerSpriteSet(ModParticles.CORRUPTION_SPARK_PARTICLE.get(), com.miophas.corruption.common.client.particle.CorruptionSparkParticle.Provider::new);
        // Andromalius's eye is a picture over the view, not a sprite: it needs no particles/*.json.
        event.registerSpecial(ModParticles.ANDROMALIUS_GAZE.get(), new com.miophas.corruption.common.client.particle.GazeParticle.Provider());
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
