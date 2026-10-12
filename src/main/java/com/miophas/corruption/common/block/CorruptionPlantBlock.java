package com.miophas.corruption.common.block;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.infection.CorruptionConversions;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CorruptionPlantBlock extends BushBlock {
    public static final MapCodec<CorruptionPlantBlock> CODEC = simpleCodec(CorruptionPlantBlock::new);
    public CorruptionPlantBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends BushBlock> codec() { return CODEC; }
    @Override protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return CorruptionConversions.isCorrupted(state) && state.isFaceSturdy(level, pos, net.minecraft.core.Direction.UP);
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(2, 0, 2, 14, 10, 14);
    }
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && this instanceof CorruptionTeethBlock && entity instanceof LivingEntity living
                && living.isAlive() && !living.isSpectator() && !CorruptionFactionApi.isRemnant(living)) {
            // A stronger single hit survives vanilla hurt cooldown instead of being swallowed by the floor's hit.
            living.hurt(ModDamageTypes.corruption(level), living.getMaxHealth() * 0.25F);
        }
    }
}
