package com.miophas.corruption.common.block;

import com.miophas.corruption.common.altar.AltarPillarBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
 * The Altar Pillar: a column of corruption arcane bricks, one block tall (gamemodels/altar/altar_pillar.bbmodel),
 * that holds one item, floating over it. Used with an item, it takes one; used again, with or without anything
 * in hand and without sneaking, it gives it back. Six of
 * them around an Altar Offering make an altar, and what they hold is what its rites use; see AltarOfferingBlock.
 */
public final class AltarPillarBlock extends BaseEntityBlock {
    public static final MapCodec<AltarPillarBlock> CODEC = simpleCodec(AltarPillarBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(box(1, 0, 1, 15, 3, 15), box(4, 3, 4, 12, 13, 12), box(2, 13, 2, 14, 16, 14));

    public AltarPillarBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<AltarPillarBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new AltarPillarBlockEntity(pos, state); }

    /**
     * Used, no sneaking needed: a pillar holding something gives it back, whatever is in hand; an empty one takes
     * one of what is in hand.
     */
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof AltarPillarBlockEntity pillar)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!pillar.isEmpty()) {
            giveBack(level, pos, player, pillar);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide) {
            pillar.set(stack);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1, .8F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Used empty-handed, it gives back what it holds. */
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof AltarPillarBlockEntity pillar) || pillar.isEmpty()) return InteractionResult.PASS;
        giveBack(level, pos, player, pillar);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void giveBack(Level level, BlockPos pos, Player player, AltarPillarBlockEntity pillar) {
        if (level.isClientSide) return;
        ItemStack taken = pillar.take();
        if (!player.getInventory().add(taken)) player.drop(taken, false);
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1, .8F);
    }

    /** Broken, it drops what it holds. */
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof AltarPillarBlockEntity pillar && !pillar.isEmpty())
            Containers.dropItemStack(level, pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, pillar.item());
        super.onRemove(state, level, pos, newState, moving);
    }
}
