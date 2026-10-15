package com.miophas.corruption.common.block;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import com.miophas.corruption.api.arcane.ArcaneSpellApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.common.arcane.ArcanePedestalBlockEntity;
import com.miophas.corruption.core.arcane.CastingUnit;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The arcane pedestal: the block a wand is taught at. A parchment bearing a string of the arcane lies open on
 * it; a wand used on it has the spell that string names written into its grimoire, one of at most
 * {@link CastingUnit} allows, and the parchment stays where it lies. The wand is not used up, and neither is
 * the parchment: teaching costs nothing, for a cast is what spends parchment.
 *
 * <p>With inscribed parchment in hand, one is laid on; with a hand empty, what lies there is taken back (see
 * {@link ArcanePedestalBlockEntity}).
 */
public final class ArcanePedestalBlock extends BaseEntityBlock {
    public static final MapCodec<ArcanePedestalBlock> CODEC = simpleCodec(ArcanePedestalBlock::new);
    /** A column, an enchanting table's height but narrower. */
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 14, 13);

    public ArcanePedestalBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<ArcanePedestalBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ArcanePedestalBlockEntity(pos, state); }

    /** A wand is taught what lies on it; inscribed parchment is laid on; anything else it gives back. */
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof ArcanePedestalBlockEntity pedestal)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (stack.getItem() instanceof ArcaneCastingItem wand) {
            teach(level, player, pedestal, wand, stack);
            return ItemInteractionResult.CONSUME;
        }
        if (!pedestal.isEmpty()) {
            give(player, pedestal.take());
            return ItemInteractionResult.CONSUME;
        }
        if (ArcaneApi.inscription(stack) != null) {
            pedestal.put(stack);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1, .8F);
        }
        return ItemInteractionResult.CONSUME;
    }

    /** Used empty-handed, it gives back the parchment lying on it. */
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof ArcanePedestalBlockEntity pedestal) || pedestal.isEmpty()) return InteractionResult.PASS;
        if (!level.isClientSide) give(player, pedestal.take());
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Broken, it spills the parchment lying on it. */
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof ArcanePedestalBlockEntity pedestal && !pedestal.isEmpty())
            Block.popResource(level, pos, pedestal.parchment());
        super.onRemove(state, level, pos, newState, moving);
    }

    /** Writes the string the parchment bears into the wand, if the wand has room and the string has a spell. */
    private static void teach(Level level, Player player, ArcanePedestalBlockEntity pedestal, ArcaneCastingItem wand, ItemStack wandStack) {
        if (pedestal.isEmpty()) { notice(player, "message.corruption.pedestal_empty"); return; }
        ResourceKey<ArcaneString> inscription = ArcaneApi.inscription(pedestal.parchment());
        if (inscription == null || !ArcaneSpellApi.castable(level.registryAccess(), inscription)) {
            notice(player, "message.corruption.pedestal_no_spell");
            return;
        }
        String spell = ArcaneApi.id(inscription);
        CastingUnit unit = wand.unit(wandStack);
        switch (unit.check(spell)) {
            case READY -> {
                wand.store(wandStack, unit.inscribing(spell));
                level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1, 1.2F);
                notice(player, "message.corruption.pedestal_inscribed");
            }
            case ALREADY_KNOWN -> notice(player, "message.corruption.pedestal_already_known");
            case FULL -> notice(player, "message.corruption.pedestal_full");
        }
    }

    private static void give(Player player, ItemStack lying) {
        if (lying.isEmpty() || player.getAbilities().instabuild) return;
        if (!player.getInventory().add(lying)) player.drop(lying, false);
    }

    private static void notice(Player player, String key) { player.displayClientMessage(Component.translatable(key), true); }
}
