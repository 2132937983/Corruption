package com.miophas.corruption;

import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.infection.InfectionEvents;
import com.miophas.corruption.common.command.CorruptionCommands;
import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.registry.ModCreativeModeTabs;
import com.miophas.corruption.common.registry.ModFeatures;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.worldgen.CorruptionRegion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import terrablender.api.Regions;

@Mod(Corruption.MOD_ID)
public final class Corruption {
    public static final String MOD_ID = "corruption";

    public Corruption(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, InfectionConfig.SPEC);
        modEventBus.addListener(InfectionConfig::onLoading);
        modEventBus.addListener(InfectionConfig::onReloading);
        modEventBus.addListener(InfectionConfig::onUnloading);
        com.miophas.corruption.common.registry.ModFluids.TYPES.register(modEventBus);
        com.miophas.corruption.common.registry.ModFluids.FLUIDS.register(modEventBus);
        com.miophas.corruption.common.registry.ModDataComponents.TYPES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModParticles.TYPES.register(modEventBus);
        ModEntities.TYPES.register(modEventBus);
        com.miophas.corruption.common.registry.ModAttachments.TYPES.register(modEventBus);
        com.miophas.corruption.common.registry.ModEffects.EFFECTS.register(modEventBus);
        com.miophas.corruption.common.registry.ModPotions.POTIONS.register(modEventBus);
        com.miophas.corruption.common.registry.ModSounds.SOUNDS.register(modEventBus);
        com.miophas.corruption.common.registry.ModMenus.MENUS.register(modEventBus);
        com.miophas.corruption.common.registry.ModRecipes.TYPES.register(modEventBus);
        com.miophas.corruption.common.registry.ModRecipes.SERIALIZERS.register(modEventBus);
        ModCreativeModeTabs.TABS.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        NeoForge.EVENT_BUS.register(InfectionEvents.class);
        NeoForge.EVENT_BUS.register(CorruptionCommands.class);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> Regions.register(new CorruptionRegion()));
        event.enqueueWork(com.miophas.corruption.common.registry.ModFluids::interactions);
    }
}
