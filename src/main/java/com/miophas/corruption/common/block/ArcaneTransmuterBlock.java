package com.miophas.corruption.common.block;

import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.transmutation.ArcaneTransmuterBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.miophas.corruption.common.registry.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Corrupted Arcane Transmuter: a furnace for the corruption. It faces whoever placed it and is lit
 * while it burns fuel or transmutes; see {@link ArcaneTransmuterBlockEntity} for what it does.
 */
public final class ArcaneTransmuterBlock extends BaseEntityBlock {
    public static final MapCodec<ArcaneTransmuterBlock> CODEC = simpleCodec(ArcaneTransmuterBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public ArcaneTransmuterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override protected MapCodec<ArcaneTransmuterBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, LIT); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ArcaneTransmuterBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.ARCANE_TRANSMUTER.get(), ArcaneTransmuterBlockEntity::serverTick);
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof ArcaneTransmuterBlockEntity machine) player.openMenu(machine);
        return InteractionResult.CONSUME;
    }

    /** Broken, it spills what it holds, an item half burnt down with what it has left; the energy in the store is lost. */
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof ArcaneTransmuterBlockEntity machine) {
            Containers.dropContents(level, pos, machine);
            if (!machine.burningItem().isEmpty()) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), machine.burningItem());
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    /**
     * At work, motes of corruption drift out of the glowing mouth on its front. The mouth is the middle
     * six pixels across and runs from two to ten pixels up, so that is where they start.
     */
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) return;
        Direction facing = state.getValue(FACING);
        for (int mote = 0; mote < 2; mote++) {
            double across = (random.nextDouble() - .5) * .34, out = .02 + random.nextDouble() * .02;
            double x = pos.getX() + .5 + facing.getStepX() * .52 + (facing.getAxis() == Direction.Axis.Z ? across : 0);
            double z = pos.getZ() + .5 + facing.getStepZ() * .52 + (facing.getAxis() == Direction.Axis.X ? across : 0);
            level.addParticle(ModParticles.CORRUPTION_PARTICLE.get(), x, pos.getY() + .15 + random.nextDouble() * .45, z,
                    facing.getStepX() * out, .005 + random.nextDouble() * .015, facing.getStepZ() * out);
        }
    }
}
