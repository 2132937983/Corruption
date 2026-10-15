package com.miophas.corruption.common.block;

import com.miophas.corruption.common.arcane.ArcanePedestalBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The arcane pedestal: the block a wand is taught at (gamemodels/arcane_pedestal/arcane_pedestal.bbmodel). Used,
 * whatever is in hand, it opens its screen: a parchment bearing a string of the arcane and a wand are laid on
 * it there, and the spell the string names is written into the wand's slot of the player's choosing. Teaching
 * costs nothing: neither wand nor parchment is used up, for a cast is what spends parchment. What lies on it
 * floats over it (see {@link ArcanePedestalBlockEntity}); broken, it drops it.
 */
public final class ArcanePedestalBlock extends BaseEntityBlock {
    public static final MapCodec<ArcanePedestalBlock> CODEC = simpleCodec(ArcanePedestalBlock::new);
    /** As the model stands, bottom to top: foot, plinth, band, shaft, neck and capital. */
    private static final VoxelShape SHAPE = Shapes.or(box(0, 0, 0, 16, 1, 16), box(1, 1, 1, 15, 3, 15), box(2, 3, 2, 14, 4, 14),
            box(3, 4, 3, 13, 11, 13), box(2, 11, 2, 14, 12, 14), box(1, 12, 1, 15, 14, 15));

    public ArcanePedestalBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<ArcanePedestalBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ArcanePedestalBlockEntity(pos, state); }

    /** Used, it opens its screen; with anything in hand too, as nothing in hand does anything to it. */
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof ArcanePedestalBlockEntity pedestal) player.openMenu(pedestal);
        return InteractionResult.CONSUME;
    }

    /** Broken, it spills what lies on it. */
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof ArcanePedestalBlockEntity pedestal)
            Containers.dropContents(level, pos, pedestal);
        super.onRemove(state, level, pos, newState, moving);
    }
}
