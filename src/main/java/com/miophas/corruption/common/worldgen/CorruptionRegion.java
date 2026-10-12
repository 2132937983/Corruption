package com.miophas.corruption.common.worldgen;

import com.miophas.corruption.Corruption;
import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

public final class CorruptionRegion extends Region {
    public CorruptionRegion() {
        super(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "overworld"), RegionType.OVERWORLD, 3);
    }

    @Override
    public void addBiomes(Registry<Biome> registry,
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // Vanilla regions retain the deep dark; only this region's matching points change.
        addModifiedVanillaOverworldBiomes(mapper,
                builder -> builder.replaceBiome(Biomes.DEEP_DARK, ModBiomes.CORRUPTION));
    }
}
