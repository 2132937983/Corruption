package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyCapabilities;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/** What the mod's blocks and items offer other mods through capabilities. */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class ModCapabilities {
    /**
     * The arcane transmuter and the brass cauldron take Corruption Energy in on every side, from anything that
     * offers it, and give none out; the cauldron's tank fills and empties through NeoForge's fluid capability. Items carry it either as a tool says for itself (see {@link com.miophas.corruption.api.energy.CorruptionEnergyTool}),
     * or as the corruption_energy_carrier data map says; the data map is data, read long after capabilities are settled and changed by any data pack, so every item is given the provider, and it answers only for those the data map names at the time.
     */
    @SubscribeEvent public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(CorruptionEnergyCapabilities.BLOCK, ModBlockEntities.ARCANE_TRANSMUTER.get(), (machine, side) -> machine.energyStorage());
        event.registerBlockEntity(CorruptionEnergyCapabilities.BLOCK, ModBlockEntities.BRASS_CAULDRON.get(), (cauldron, side) -> cauldron.energyStorage());
        event.registerBlockEntity(CorruptionEnergyCapabilities.BLOCK, ModBlockEntities.ARCANE_CHARGER.get(), (charger, side) -> charger.energyStorage());
        event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, ModBlockEntities.BRASS_CAULDRON.get(), (cauldron, side) -> cauldron.fluidHandler());
        event.registerItem(CorruptionEnergyCapabilities.ITEM, (stack, nothing) ->
                        CorruptionEnergyTool.of(stack).isTool() ? CorruptionEnergyTool.storage(stack) : CorruptionEnergyCarrier.storage(stack),
                BuiltInRegistries.ITEM.stream().toArray(ItemLike[]::new));
    }

    private ModCapabilities() {}
}
