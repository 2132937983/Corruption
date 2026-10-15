package com.miophas.corruption.common.block;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.api.energy.EnergyCharger;
import com.miophas.corruption.common.blockentity.ArcaneChargerBlockEntity;
import com.miophas.corruption.common.energy.EnergyCharging;
import com.miophas.corruption.common.energy.EnergyPouring;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Corrupted Arcane Charger: the block that pours Corruption Energy into a tool left on it. It holds one item
 * and a store of its own; the store is filled by hand or by an add-on's cables (see
 * {@link ArcaneChargerBlockEntity}), and a step of it goes into the item each tick, until the store is spent or
 * the item full — how a wand is charged without transmuting it.
 *
 * <p>There is no screen. Energy is poured in by right-clicking with something that has some to give — a
 * battery, such as a Corruption Candle; never a tool, whose charge does not leave it — and no sneak is asked for
 * as it is elsewhere: the charger is made to be fed. A tool that is not full is set on instead, and what lies
 * there is taken back. Used empty-handed with nothing on it, it says how full its store is.
 *
 * <p>Everything this block asks of the machine is asked by interface: the item slot through {@link EnergyCharger},
 * and the store through {@link CorruptionEnergyApi#storage}, so an add-on's own machine that keeps an item to
 * charge answers to it just the same. What it shows of its work, the ring of sparks about the item and the
 * sparks flying into it, is drawn by the mod's own charger (see ArcaneChargerRenderer).
 */
public final class ArcaneChargerBlock extends BaseEntityBlock {
    public static final MapCodec<ArcaneChargerBlock> CODEC = simpleCodec(ArcaneChargerBlock::new);

    public ArcaneChargerBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<ArcaneChargerBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ArcaneChargerBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.ARCANE_CHARGER.get(),
                level.isClientSide ? ArcaneChargerBlockEntity::clientTick : ArcaneChargerBlockEntity::serverTick);
    }

    /**
     * Energy is poured in with something that has some to give; what lies there is taken back; a tool that is not
     * full is set on; anything else is passed on, so blocks may still be placed against it.
     */
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof EnergyCharger charger)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (CorruptionEnergyApi.carriesEnergy(stack)) {
            CorruptionEnergyStorage store = CorruptionEnergyApi.storage(level, pos, hit.getDirection());
            if (!level.isClientSide && store != null) EnergyPouring.pour(player, hand, store, pos.getCenter());
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!charger.isEmpty()) {
            if (!level.isClientSide) give(player, charger.take());
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!EnergyCharging.chargeable(stack)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!level.isClientSide) {
            charger.put(stack);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1, .8F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * Used empty-handed, it gives back what lies on it; with nothing on it, it says how full its store is. Used
     * with something it has no use for and nothing on it, it lets that thing be, so a block can be set against it.
     */
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof EnergyCharger charger)) return InteractionResult.PASS;
        if (charger.isEmpty() && !player.getMainHandItem().isEmpty()) return InteractionResult.PASS;
        if (!level.isClientSide) {
            if (!charger.isEmpty()) give(player, charger.take());
            else {
                CorruptionEnergyStorage store = CorruptionEnergyApi.storage(level, pos, hit.getDirection());
                if (store != null) player.displayClientMessage(Component.translatable("message.corruption.charger_store",
                        CorruptionEnergyApi.format(store.getEnergy()), CorruptionEnergyApi.format(store.getCapacity())), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Broken, it gives back what lies on it; the energy in its store is lost. */
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof EnergyCharger charger && !charger.isEmpty())
            Block.popResource(level, pos, charger.take());
        super.onRemove(state, level, pos, newState, moving);
    }

    /** Now and then a mote of corruption wanders up about the item, while it holds any energy at all. */
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof ArcaneChargerBlockEntity charger) || charger.energy() <= 0) return;
        level.addParticle(ModParticles.CORRUPTION_PARTICLE.get(),
                pos.getX() + .5 + (random.nextDouble() - .5) * .8,
                pos.getY() + 1 + random.nextDouble() * .5,
                pos.getZ() + .5 + (random.nextDouble() - .5) * .8,
                (random.nextDouble() - .5) * .012, .004 + random.nextDouble() * .006, (random.nextDouble() - .5) * .012);
    }

    private static void give(Player player, ItemStack lying) {
        if (lying.isEmpty() || player.getAbilities().instabuild) return;
        if (!player.getInventory().add(lying)) player.drop(lying, false);
    }
}
