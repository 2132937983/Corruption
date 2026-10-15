package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneSpell;
import com.miophas.corruption.api.arcane.ArcaneString;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

/** The registries the mod adds that data packs fill: data/&lt;namespace&gt;/corruption/&lt;registry&gt;/&lt;name&gt;.json. */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class ModRegistries {
    /** Arcane strings, and the spells made of them, sent to clients too, so screens and tooltips can show them. */
    @SubscribeEvent public static void register(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ArcaneString.REGISTRY, ArcaneString.CODEC, ArcaneString.CODEC);
        event.dataPackRegistry(ArcaneSpell.REGISTRY, ArcaneSpell.CODEC, ArcaneSpell.CODEC);
    }

    private ModRegistries() {}
}
