package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.worldgen.feature.CorruptionSurfaceFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Corruption.MOD_ID);
    public static final DeferredHolder<Feature<?>, CorruptionSurfaceFeature> CORRUPTION_SURFACE =
            FEATURES.register("corruption_surface", CorruptionSurfaceFeature::new);

    private ModFeatures() {}
}
