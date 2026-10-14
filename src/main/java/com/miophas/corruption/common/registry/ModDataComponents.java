package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The data the mod keeps on item stacks. */
public final class ModDataComponents {
    public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Corruption.MOD_ID);
    /** The Corruption Energy one item carries; the API's {@link CorruptionEnergyCarrier#ENERGY} names this same entry. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> CORRUPTION_ENERGY = TYPES.registerComponentType(
            CorruptionEnergyCarrier.ENERGY.getId().getPath(),
            builder -> builder.persistent(CorruptionEnergyCarrier.ENERGY_CODEC).networkSynchronized(CorruptionEnergyCarrier.ENERGY_STREAM_CODEC));
    /** The one arcane string a parchment bears; the API's {@link ArcaneApi#INSCRIPTION} names this same entry. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<ArcaneString>>> INSCRIPTION = TYPES.registerComponentType(
            ArcaneApi.INSCRIPTION.getId().getPath(),
            builder -> builder.persistent(ArcaneApi.INSCRIPTION_CODEC).networkSynchronized(ArcaneApi.INSCRIPTION_STREAM_CODEC));

    private ModDataComponents() {}
}
