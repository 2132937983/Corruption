package com.miophas.corruption.common.block;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Berry-bush movement and slowing, with corruption damage rather than thorns. */
public final class LostSoulBlock extends CorruptionPlantBlock {
    public static final MapCodec<LostSoulBlock> CODEC = simpleCodec(LostSoulBlock::new);
    private static final Vec3 SLOWDOWN = new Vec3(0.8F, 0.75, 0.8F);
    private static final float HURT_SPEED_THRESHOLD = 0.003F;

    public LostSoulBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<? extends BushBlock> codec() { return CODEC; }

    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity living) || !living.isAlive() || living.isSpectator()
                || CorruptionFactionApi.isRemnant(living)) return;
        living.makeStuckInBlock(state, SLOWDOWN);
        if (!level.isClientSide && (Math.abs(living.getX() - living.xOld) >= HURT_SPEED_THRESHOLD
                || Math.abs(living.getZ() - living.zOld) >= HURT_SPEED_THRESHOLD)) {
            living.hurt(ModDamageTypes.corruption(level), living.getMaxHealth() * CorruptionBlock.CONTACT_DAMAGE_FRACTION);
        }
    }
}
