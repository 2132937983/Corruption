package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneKey;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionFuel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

/** Registers the data maps the mod defines: values data packs attach to registry entries, read from data/&lt;namespace&gt;/data_maps/&lt;registry&gt;/&lt;name&gt;.json. */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class ModDataMaps {
    @SubscribeEvent public static void register(RegisterDataMapTypesEvent event) {
        event.register(CorruptionFuel.DATA_MAP);
        event.register(CorruptionEnergyCarrier.DATA_MAP);
        event.register(ArcaneKey.DATA_MAP);
    }

    private ModDataMaps() {}
}
