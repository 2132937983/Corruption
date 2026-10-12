package com.miophas.corruption.common.block;

import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.entity.MaliceVine;
import com.miophas.corruption.common.entity.AnchoredLivingPlant;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class MaliceBlock extends CorruptionBlock implements BonemealableBlock {
    private static final ThreadLocal<Integer> BONEMEAL_FACE = new ThreadLocal<>();
    public static final MapCodec<MaliceBlock> CODEC = simpleCodec(MaliceBlock::new);
    public MaliceBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<MaliceBlock> codec() { return CODEC; }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moved) {
        super.onPlace(state, level, pos, previous, moved);
        if (!level.isClientSide && !previous.is(this)) level.scheduleTick(pos, this, 20);
    }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int growth = com.miophas.corruption.api.corruption.MaliceApi.growthKind(random.nextDouble(), InfectionConfig.greedChance(), InfectionConfig.vineChance());
        if (growth == 2) com.miophas.corruption.common.entity.Greed.sprout(level, pos);
        else if (growth == 1) MaliceVine.sprout(level, pos);
    }
    @Override public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        int direction = bonemealDirection(level, pos);
        if (level instanceof Level world) {
            AnchoredLivingPlant vine = world.getEntitiesOfClass(AnchoredLivingPlant.class,
                    new net.minecraft.world.phys.AABB(pos).inflate(3, 11, 3),
                    plant -> plant.isAlive() && plant.anchor().equals(pos)).stream().findFirst().orElse(null);
            return vine == null ? direction != 0 && MaliceVine.hasRoom(level, pos, direction, 2)
                    : vine.canGrow();
        }
        return direction != 0;
    }
    @Override public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) { return true; }
    @Override public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        AnchoredLivingPlant vine = AnchoredLivingPlant.find(level, pos);
        if (vine == null) MaliceVine.sprout(level, pos, bonemealDirection(level, pos)); else vine.grow();
    }
    private static int bonemealDirection(LevelReader level, BlockPos pos) {
        Integer clicked = BONEMEAL_FACE.get();
        if (clicked == null) return MaliceVine.direction(level, pos);
        return com.miophas.corruption.api.plant.LivingPlantApi.direction(new com.miophas.corruption.common.entity.LevelLivingPlantSpace(level, pos), clicked);
    }
    @Override protected net.minecraft.world.ItemInteractionResult useItemOn(net.minecraft.world.item.ItemStack stack,
            BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
        if (!stack.is(net.minecraft.world.item.Items.BONE_MEAL) || hit.getDirection().getAxis() != net.minecraft.core.Direction.Axis.Y)
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        BONEMEAL_FACE.set(hit.getDirection() == net.minecraft.core.Direction.DOWN ? -1 : 1);
        int count = stack.getCount();
        try {
            if (!net.minecraft.world.item.BoneMealItem.applyBonemeal(stack, level, pos, player))
                return net.minecraft.world.ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            if (!level.isClientSide) {
                if (player.getAbilities().instabuild) stack.setCount(count);
                level.levelEvent(1505, pos.relative(hit.getDirection()), 15);
            }
            return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
        } finally { BONEMEAL_FACE.remove(); }
    }
}
