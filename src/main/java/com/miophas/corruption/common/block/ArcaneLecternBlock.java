package com.miophas.corruption.common.block;

import com.miophas.corruption.common.arcane.ArcaneLecternBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import com.miophas.corruption.common.registry.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Corrupted Arcane Lectern: the block that receives a key to the arcane and shows its strings, and where
 * they are copied onto parchment with ink. Shaped as an enchanting table is, a slab twelve pixels high, it is
 * an altar of trimmed arcane bricks with a candle burning on each corner (gamemodels/arcane_lectern/
 * arcane_lectern.bbmodel); while it holds a key, the key floats open above it. It faces whoever placed it.
 * See {@link ArcaneLecternBlockEntity} for what it holds.
 */
public final class ArcaneLecternBlock extends BaseEntityBlock {
    public static final MapCodec<ArcaneLecternBlock> CODEC = simpleCodec(ArcaneLecternBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty HAS_KEY = BooleanProperty.create("has_key");
    /** What it occupies: the altar, as high as an enchanting table; the candles and the book above stand clear. */
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);
    /** The tips of the candles' wicks, in blocks from its corner: the same on every side, whichever way it faces. */
    private static final double[][] WICKS = {{2 / 16D, 2 / 16D}, {14 / 16D, 2 / 16D}, {2 / 16D, 14 / 16D}, {14 / 16D, 14 / 16D}};
    private static final double WICK_TOP = 18 / 16D;

    public ArcaneLecternBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HAS_KEY, false));
    }

    @Override protected MapCodec<ArcaneLecternBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, HAS_KEY); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override protected boolean useShapeForLightOcclusion(BlockState state) { return true; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ArcaneLecternBlockEntity(pos, state); }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof ArcaneLecternBlockEntity lectern) player.openMenu(lectern);
        return InteractionResult.CONSUME;
    }

    /** Broken, it spills what it holds, the key with the rest. */
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof ArcaneLecternBlockEntity lectern)
            Containers.dropContents(level, pos, lectern);
        super.onRemove(state, level, pos, newState, moving);
    }

    /** Its candles burn as candles do, a small flame on each wick and now and then smoke; over an open key, motes of corruption rise. */
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (double[] wick : WICKS) {
            double x = pos.getX() + wick[0], y = pos.getY() + WICK_TOP, z = pos.getZ() + wick[1];
            if (random.nextFloat() < .3F) level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
            level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0, 0, 0);
        }
        if (state.getValue(HAS_KEY) && random.nextInt(3) == 0)
            level.addParticle(ModParticles.CORRUPTION_PARTICLE.get(), pos.getX() + .5 + (random.nextDouble() - .5) * .5, pos.getY() + 1.05,
                    pos.getZ() + .5 + (random.nextDouble() - .5) * .4, 0, .015 + random.nextDouble() * .01, 0);
    }
}
