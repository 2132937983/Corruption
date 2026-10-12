package com.miophas.corruption.common.infection;

import com.miophas.corruption.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

/** Ore outcomes depend on the world's dimension and the original ore's host material. */
public final class CorruptionOreConversions {
    public static boolean isOre(BlockState state) {
        return state.is(Tags.Blocks.ORES) || state.is(Tags.Blocks.ORES_IN_GROUND_STONE)
                || state.is(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                || state.is(Tags.Blocks.ORES_IN_GROUND_NETHERRACK);
    }

    /** Returns null for non-ores and dimensions without an ore conversion rule. */
    public static BlockState convert(BlockState source, Level level, BlockPos pos) {
        if (!isOre(source)) return null;
        if (level.dimension().equals(Level.NETHER)) return ModBlocks.ARCUNUM.get().defaultBlockState();
        if (level.dimension().equals(Level.END)) return ModBlocks.ARCUNUM_END.get().defaultBlockState();
        if (!level.dimension().equals(Level.OVERWORLD)) return null;
        boolean deepslate = source.is(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                || (!source.is(Tags.Blocks.ORES_IN_GROUND_STONE) && pos.getY() < 0);
        return (deepslate ? ModBlocks.SPIRIT_DEEPSLATE : ModBlocks.SPIRIT).get().defaultBlockState();
    }

    private CorruptionOreConversions() {}
}
