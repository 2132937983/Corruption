package com.miophas.corruption.common.infection;

import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.miophas.corruption.common.block.EldritchVineBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

/** Runs once at a successful conversion or cave-floor placement, never as a world scan. */
public final class CorruptionEcology {
    public static void decorate(LevelAccessor level, BlockPos floor, RandomSource random) {
        var support = level.getBlockState(floor);
        if (!CorruptionConversions.isCorrupted(support) || !support.getFluidState().isEmpty() || support.is(ModBlocks.MALICE.get())) return;
        if (random.nextDouble() < InfectionConfig.eldritchChance()) {
            Direction direction = level.getBlockState(floor.below()).isAir() ? Direction.DOWN : Direction.UP;
            BlockPos pos = floor.relative(direction);
            if (!level.isOutsideBuildHeight(pos) && level.hasChunkAt(pos) && level.getBlockState(pos).isAir()) {
                var vine = ModBlocks.ELDRITCH.get();
                var state = vine.defaultBlockState().setValue(EldritchVineBlock.HANGING, direction == Direction.DOWN)
                        .setValue(EldritchVineBlock.AGE, random.nextInt(5));
                if (state.canSurvive(level, pos) && level.setBlock(pos, state, 3)) {
                    for (int i = random.nextInt(3); i > 0 && vine.grow(level, pos, state); i--) {
                        pos = pos.relative(direction); state = level.getBlockState(pos);
                    }
                }
            }
        }
        decorateFloor(level, floor, random);
        if (random.nextDouble() < InfectionConfig.eerieChance()) ModBlocks.EERIE.get().seedPatch(level, floor, random);
    }
    private static void decorateFloor(LevelAccessor level, BlockPos floor, RandomSource random) {
        if (!level.getBlockState(floor.above()).isAir() || random.nextDouble() >= InfectionConfig.decorChance()) return;
        var block = switch (random.nextInt(5)) {
            case 0, 1 -> ModBlocks.CORRUPTION_TEETH.get();
            case 2 -> ModBlocks.MALICE_GRASS.get();
            case 3 -> ModBlocks.WHISPER_BLOOM.get();
            default -> ModBlocks.BLOOD_FILAMENTS.get();
        };
        var state = block.defaultBlockState();
        if (state.canSurvive(level, floor.above())) level.setBlock(floor.above(), state, 3);
    }
    private CorruptionEcology() {}
}
