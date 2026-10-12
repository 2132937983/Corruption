package com.miophas.corruption.common.block;

import com.miophas.corruption.common.config.InfectionConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** One block ID, with upward/downward growth and a separate texture for the terminal segment. */
public final class EldritchVineBlock extends Block implements BonemealableBlock {
    public static final MapCodec<EldritchVineBlock> CODEC = simpleCodec(EldritchVineBlock::new);
    public static final BooleanProperty HANGING = BooleanProperty.create("hanging");
    public static final BooleanProperty TIP = BooleanProperty.create("tip");
    public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
    private static final VoxelShape SHAPE = box(4, 0, 4, 12, 16, 12);

    public EldritchVineBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HANGING, false).setValue(TIP, true).setValue(AGE, 0));
    }
    @Override protected MapCodec<EldritchVineBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING, TIP, AGE);
    }
    public static Direction growthDirection(BlockState state) { return state.getValue(HANGING) ? Direction.DOWN : Direction.UP; }
    private boolean continues(BlockState state, boolean hanging) { return state.is(this) && state.getValue(HANGING) == hanging; }
    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction growth = growthDirection(state);
        BlockPos supportPos = pos.relative(growth.getOpposite());
        BlockState support = level.getBlockState(supportPos);
        return continues(support, state.getValue(HANGING)) || support.isFaceSturdy(level, supportPos, growth);
    }
    @Override @Nullable public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction growth : new Direction[]{context.getClickedFace() == Direction.DOWN ? Direction.DOWN : Direction.UP,
                context.getClickedFace() == Direction.DOWN ? Direction.UP : Direction.DOWN}) {
            BlockState state = defaultBlockState().setValue(HANGING, growth == Direction.DOWN);
            if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                return state.setValue(TIP, !continues(context.getLevel().getBlockState(context.getClickedPos().relative(growth)), growth == Direction.DOWN));
            }
        }
        return null;
    }
    @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
        return direction == growthDirection(state) ? state.setValue(TIP, !continues(neighbor, state.getValue(HANGING))) : state;
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override protected boolean isRandomlyTicking(BlockState state) { return state.getValue(TIP) && state.getValue(AGE) < 25; }
    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextDouble() < InfectionConfig.eldritchGrowthChance()) grow(level, pos, state);
    }
    private boolean hasRoom(LevelReader level, BlockPos pos, BlockState state) {
        BlockPos next = pos.relative(growthDirection(state));
        return state.is(this) && state.getValue(TIP) && !level.isOutsideBuildHeight(next)
                && level.hasChunkAt(next) && level.getBlockState(next).isAir();
    }
    public boolean grow(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!state.canSurvive(level, pos) || !hasRoom(level, pos, state)) return false;
        return level.setBlock(pos.relative(growthDirection(state)), defaultBlockState()
                .setValue(HANGING, state.getValue(HANGING)).setValue(AGE, Math.min(25, state.getValue(AGE) + 1)), UPDATE_ALL);
    }
    private BlockPos terminal(LevelReader level, BlockPos pos, BlockState state) {
        Direction direction = growthDirection(state);
        for (int i = 0; i < level.getHeight() && !state.getValue(TIP); i++) {
            BlockPos next = pos.relative(direction);
            if (level.isOutsideBuildHeight(next) || !level.hasChunkAt(next)) break;
            BlockState neighbor = level.getBlockState(next);
            if (!continues(neighbor, state.getValue(HANGING))) break;
            pos = next; state = neighbor;
        }
        return pos;
    }
    @Override public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        BlockPos tip = terminal(level, pos, state); return hasRoom(level, tip, level.getBlockState(tip));
    }
    @Override public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) { return true; }
    @Override public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        pos = terminal(level, pos, state); state = level.getBlockState(pos);
        for (int remaining = 1 + random.nextInt(3); remaining > 0 && grow(level, pos, state); remaining--) {
            pos = pos.relative(growthDirection(state)); state = level.getBlockState(pos);
        }
    }
}
