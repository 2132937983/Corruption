package com.miophas.corruption.common.worldgen.feature;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.worldgen.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

/** Covers exposed cave floors during chunk generation; never runs as a block tick. */
public final class CorruptionSurfaceFeature extends Feature<BlockStateConfiguration> {
    private static final TagKey<Block> REPLACEABLE_CAVE_FLOOR = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "replaceable_cave_floor"));

    public CorruptionSurfaceFeature() {
        super(BlockStateConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        WorldGenLevel level = context.level();
        int minX = context.origin().getX() & ~15;
        int minZ = context.origin().getZ() & ~15;
        int maxY = Math.min(context.chunkGenerator().getSeaLevel(), level.getMaxBuildHeight() - 2);
        BlockPos.MutableBlockPos floor = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
        boolean placed = false;

        // Visit every underground floor in this chunk, including stacked caves.
        for (int x = minX; x < minX + 16; x++) {
            for (int z = minZ; z < minZ + 16; z++) {
                for (int y = level.getMinBuildHeight() + 1; y <= maxY; y++) {
                    floor.set(x, y, z);
                    above.set(x, y + 1, z);
                    if (level.getBlockState(floor).is(REPLACEABLE_CAVE_FLOOR)
                            && level.isEmptyBlock(above)
                            && level.getBiome(floor).is(ModBiomes.CORRUPTION)) {
                        setBlock(level, floor, context.config().state);
                        com.miophas.corruption.common.infection.CorruptionEcology.decorate(level, floor, context.random());
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }
}
