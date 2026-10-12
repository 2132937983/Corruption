package com.miophas.corruption.common.block;

import com.miophas.corruption.common.infection.CorruptionConversions;
import com.miophas.corruption.common.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/** Animated surface growth; infection seeds bounded patches, without a scan or per-block ticker. */
public final class EerieBlock extends MultifaceBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<EerieBlock> CODEC = simpleCodec(EerieBlock::new);
    private final MultifaceSpreader spreader = new MultifaceSpreader(new MultifaceSpreader.DefaultSpreaderConfig(this) {
        @Override protected boolean stateCanBeReplaced(BlockGetter level, BlockPos from, BlockPos pos, Direction face, BlockState state) {
            if (level instanceof LevelReader reader && (!reader.hasChunkAt(pos) || !reader.hasChunkAt(pos.relative(face))
                    || reader.isOutsideBuildHeight(pos) || reader.isOutsideBuildHeight(pos.relative(face)))) return false;
            BlockState support = level.getBlockState(pos.relative(face));
            // Reserve malice's vertical space for its anchored vine entity.
            return CorruptionConversions.isCorrupted(support) && !(support.is(ModBlocks.MALICE.get()) && face.getAxis() == Direction.Axis.Y)
                    && super.stateCanBeReplaced(level, from, pos, face, state);
        }
    });
    public EerieBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }
    @Override protected MapCodec<EerieBlock> codec() { return CODEC; }
    @Override public MultifaceSpreader getSpreader() { return spreader; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); builder.add(BlockStateProperties.WATERLOGGED);
    }
    @Override protected FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        BlockState result = super.updateShape(state, direction, neighbor, level, pos, neighborPos);
        return result.isAir() && state.getValue(BlockStateProperties.WATERLOGGED) ? net.minecraft.world.level.block.Blocks.WATER.defaultBlockState() : result;
    }
    public boolean seedPatch(LevelAccessor level, BlockPos anchor, RandomSource random) {
        for (Direction outward : Direction.allShuffled(random)) {
            BlockPos pos = anchor.relative(outward);
            if (level.isOutsideBuildHeight(pos) || !level.hasChunkAt(pos)) continue;
            BlockState current = level.getBlockState(pos);
            if (!current.isAir() && !current.is(this)) continue;
            BlockState seed = getStateForPlacement(current, level, pos, outward.getOpposite());
            if (seed == null || !level.setBlock(pos, seed, UPDATE_ALL)) continue;
            var positions = new ArrayList<BlockPos>(); positions.add(pos);
            int additions = 2 + random.nextInt(4);
            for (int i = 0; i < additions; i++) {
                BlockPos from = positions.get(random.nextInt(positions.size()));
                spreader.spreadFromRandomFaceTowardRandomDirection(level.getBlockState(from), level, from, random)
                        .ifPresent(next -> positions.add(next.pos()));
            }
            return true;
        }
        return false;
    }
}
