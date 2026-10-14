package com.miophas.corruption.common.block;

import com.miophas.corruption.common.cooking.BrassCauldronBlockEntity;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidUtil;

/**
 * The Brass Cauldron: a pot of corrupted brass on four feet (gamemodels/brass_cauldron/brass_cauldron.bbmodel)
 * that cooks up to seven things together with a fluid and Corruption Energy, by the corruption:brass_cooking
 * recipes. Used, it opens, whatever is in hand; used sneaking with a bucket, or anything else that holds
 * fluid, it is filled from it or emptied into it. It bubbles while it cooks, and the fluid it holds and the
 * things cooking in it show inside it. See {@link BrassCauldronBlockEntity} for what it does.
 */
public final class BrassCauldronBlock extends BaseEntityBlock {
    public static final MapCodec<BrassCauldronBlock> CODEC = simpleCodec(BrassCauldronBlock::new);
    /** Whether it is cooking. */
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    /** As a cauldron is: walls around a hollow, standing on four feet. */
    private static final VoxelShape INSIDE = box(2, 4, 2, 14, 16, 14);
    private static final VoxelShape SHAPE = Shapes.join(Shapes.block(),
            Shapes.or(box(0, 0, 4, 16, 3, 12), box(4, 0, 0, 12, 3, 16), box(2, 0, 2, 14, 3, 14), INSIDE), BooleanOp.ONLY_FIRST);

    public BrassCauldronBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LIT, false));
    }

    @Override protected MapCodec<BrassCauldronBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(LIT); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) { return INSIDE; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new BrassCauldronBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.BRASS_CAULDRON.get(), BrassCauldronBlockEntity::serverTick);
    }

    /** Whether this is something a player fills it from or empties it into, sneaking: a bucket, or anything else that holds fluid. */
    public static boolean holdsFluid(ItemStack stack) { return !stack.isEmpty() && FluidUtil.getFluidHandler(stack.copyWithCount(1)).isPresent(); }

    /**
     * Sneaking with a bucket, it is filled or emptied (the game asks the block at all only because
     * {@link #onSneakUse} told it to); otherwise it opens, as though the hand were empty.
     */
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.isSecondaryUseActive() || !holdsFluid(stack)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        return FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection()) ? ItemInteractionResult.CONSUME : ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * A player sneaking would use the item and pass the block by, and a bucket would pour into the world. With
     * something that holds fluid in hand at a brass cauldron, the cauldron is asked instead, and the item not.
     */
    public static void onSneakUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntity().isSecondaryUseActive() || !holdsFluid(event.getItemStack())) return;
        if (!event.getLevel().getBlockState(event.getPos()).is(ModBlocks.BRASS_CAULDRON.get())) return;
        event.setUseBlock(TriState.TRUE);
        event.setUseItem(TriState.FALSE);
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof BrassCauldronBlockEntity cauldron) player.openMenu(cauldron);
        return InteractionResult.CONSUME;
    }

    /** Broken, it spills what it holds, an item half burnt down with what it has left; its fluid and energy are lost. */
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof BrassCauldronBlockEntity cauldron) {
            Containers.dropContents(level, pos, cauldron);
            if (!cauldron.burningItem().isEmpty()) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), cauldron.burningItem());
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    /** While it cooks, it bubbles, and motes of corruption rise from it. */
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) return;
        double x = pos.getX() + .25 + random.nextDouble() * .5, z = pos.getZ() + .25 + random.nextDouble() * .5, y = pos.getY() + .9;
        level.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0, .02, 0);
        if (random.nextInt(3) == 0) level.addParticle(ModParticles.CORRUPTION_PARTICLE.get(), x, y + .05, z, 0, .02, 0);
    }
}
