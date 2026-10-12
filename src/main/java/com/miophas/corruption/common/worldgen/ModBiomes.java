package com.miophas.corruption.common.worldgen;

import com.miophas.corruption.Corruption;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomes {
    public static final ResourceKey<Biome> CORRUPTION = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corruption"));

    private ModBiomes() {}
}
