package com.miophas.corruption.common.block;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.api.energy.EnergyCharger;
import com.miophas.corruption.common.energy.EnergyCharging;
import com.miophas.corruption.common.energy.EnergyPouring;
import com.miophas.corruption.common.registry.ModBlockEntities;
import com.miophas.corruption.common.registry.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
 * The Corrupted Arcane Charger: the block that pours Corruption Energy into an item left on it. It holds one
 * item and a store of its own; the store is filled by hand or by an add-on's cables (see
 * {@link com.miophas.corruption.common.blockentity.ArcaneChargerBlockEntity}), and a step of it goes into the
 * item each tick, until the store is spent or the item full — how a wand is charged without transmuting it.
 *
 * <p>There is no screen. Energy is poured in by right-clicking with something that has some to give, and no
 * sneak is asked for as it is elsewhere: the charger is made to be fed. A tool with nothing to give is set on
 * instead, and what lies there is taken back.
 *
 * <p>Everything this block asks of the machine is asked by interface: the item slot through {@link EnergyCharger},
 * and the store through {@link CorruptionEnergyApi#storage}, so an add-on's own machine that keeps an item to
 * charge answers to it just the same.
 *
 * <p>It shows its store: an arc of sparks stands over it, its highest point in the middle and its count rising
 * with what the store holds, turning and drifting slowly, with motes of corruption wandering about it. Each
 * step of energy that goes into the item sends a spark from the arc after it.
 */
public final class ArcaneChargerBlock extends BaseEntityBlock {
    public static final MapCodec<ArcaneChargerBlock> CODEC = simpleCodec(ArcaneChargerBlock::new);
    /** How many sparks stand over it when its store is full. */
    private static final int SPARKS = 5;
    /** The arc the sparks stand on: its reach, and how far round from the top its ends fall. */
    private static final double ARC_RADIUS = .42, ARC_SPREAD = 1.25;
    /** How high the middle of the arc stands over the block, and how far the whole of it drifts up and down. */
    private static final double ARC_HEIGHT = 1.30, ARC_DRIFT = .045;
    /** How far the arc turns a tick, in degrees, so the sparks are not quite still. */
    private static final double TURN_DEGREES = .4;
    /** Where the item being charged is held, which is where the sparks are sent (see ArcaneChargerRenderer). */
    private static final double ITEM_HEIGHT = 1.15;
    /** Which side its store is asked for by: the charger offers the same one on every side. */
    private static final Direction STORE_SIDE = Direction.UP;

    public ArcaneChargerBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<ArcaneChargerBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new com.miophas.corruption.common.blockentity.ArcaneChargerBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.ARCANE_CHARGER.get(),
                com.miophas.corruption.common.blockentity.ArcaneChargerBlockEntity::serverTick);
    }

    /**
     * Energy is poured in with something that has some to give; what lies there is taken back; a tool with
     * nothing to give is set on; anything else is passed on, so blocks may still be placed against it.
     */
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof EnergyCharger charger)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (givesEnergy(stack)) {
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

    /** Used empty-handed, it gives back what lies on it. */
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof EnergyCharger charger) || charger.isEmpty()) return InteractionResult.PASS;
        if (!level.isClientSide) give(player, charger.take());
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Broken, it gives back what lies on it; the energy in its store is lost. */
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof EnergyCharger charger && !charger.isEmpty())
            Block.popResource(level, pos, charger.take());
        super.onRemove(state, level, pos, newState, moving);
    }

    /**
     * What stands over the charger and what moves about it: an arc of sparks whose count rises with what the
     * store holds, its highest point in the middle, turning and drifting slowly; motes of corruption wandering
     * about the plate; and, for each step of energy the item has just been given, a spark leaving the arc after
     * it, one place round the arc at a time.
     */
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof EnergyCharger charger)) return;
        double middleX = pos.getX() + .5, middleZ = pos.getZ() + .5;
        double time = level.getGameTime();
        double turn = Math.toRadians(time * TURN_DEGREES);
        double arcY = pos.getY() + ARC_HEIGHT + Math.sin(time / 24.0) * ARC_DRIFT;

        int standing = standingIn(level, pos);
        for (int spark = 0; spark < standing; spark++) {
            double[] at = arcPoint(spark, standing, turn, middleX, arcY, middleZ);
            level.addParticle(ModParticles.CORRUPTION_SPARK_PARTICLE.get(), at[0], at[1], at[2], 0, 0, 0);
        }
        int owed = charger.sparksOwed();
        for (int spark = 0; spark < owed; spark++) {
            // One place round the arc at a time, so the sparks leave it one by one rather than all together.
            double[] from = arcPoint((int) Math.floorMod((long) time + spark, Math.max(1, standing)), Math.max(1, standing), turn, middleX, arcY, middleZ);
            double pull = .3;
            level.addParticle(ModParticles.CORRUPTION_SPARK_PARTICLE.get(), from[0], from[1], from[2],
                    (middleX - from[0]) * pull, (pos.getY() + ITEM_HEIGHT - from[1]) * pull, (middleZ - from[2]) * pull);
        }
        if (random.nextInt(4) == 0) {
            level.addParticle(ModParticles.CORRUPTION_PARTICLE.get(),
                    middleX + (random.nextDouble() - .5) * .8,
                    pos.getY() + .9 + random.nextDouble() * .6,
                    middleZ + (random.nextDouble() - .5) * .8,
                    (random.nextDouble() - .5) * .012, .004 + random.nextDouble() * .006, (random.nextDouble() - .5) * .012);
        }
    }

    /** How many sparks stand over it: the share of its store, of the whole number it can show. */
    private static int standingIn(Level level, BlockPos pos) {
        CorruptionEnergyStorage store = CorruptionEnergyApi.storage(level, pos, STORE_SIDE);
        if (store == null || store.getCapacity() <= 0 || store.getEnergy() <= 0) return 0;
        return Math.max(1, (int) Math.min(SPARKS, Math.ceil((double) store.getEnergy() / store.getCapacity() * SPARKS)));
    }

    /** Where one spark of the arc stands: the ends of the arc fall away, so the middle of it is its highest point. */
    private static double[] arcPoint(int spark, int standing, double turn, double middleX, double arcY, double middleZ) {
        double along = standing <= 1 ? 0 : (spark / (standing - 1.0)) * 2 - 1;
        double angle = along * ARC_SPREAD;
        double reach = Math.sin(angle) * ARC_RADIUS;
        return new double[] {
                middleX + Math.cos(turn) * reach,
                arcY + Math.cos(angle) * ARC_RADIUS,
                middleZ + Math.sin(turn) * reach };
    }

    /**
     * Whether the held item has Corruption Energy to give: what is poured in by hand, without sneaking. Asked
     * of one item at a time, as {@link CorruptionEnergyApi#carriesEnergy} does, since a stack of several moves
     * no energy unless it is one of a kind that is used up when it gives — a candle, not a wand. The same
     * question pouring elsewhere asks, so a stack of candles is poured in here as it is there.
     */
    private static boolean givesEnergy(ItemStack stack) { return CorruptionEnergyApi.carriesEnergy(stack); }

    private static void give(Player player, ItemStack lying) {
        if (lying.isEmpty() || player.getAbilities().instabuild) return;
        if (!player.getInventory().add(lying)) player.drop(lying, false);
    }
}
