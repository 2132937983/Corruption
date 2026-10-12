package com.miophas.corruption.common.entity;

import com.miophas.corruption.common.block.CorruptionBlock;
import com.miophas.corruption.common.block.CorruptedLiquidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Shared contact geometry for fluid damage and death-triggered reanimation. */
public final class CorruptionExposure {
    public static boolean intersectsFluid(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        if (!(state.getBlock() instanceof CorruptedLiquidBlock)) return false;
        var fluidBounds = new AABB(pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + state.getFluidState().getHeight(level, pos), pos.getZ() + 1);
        return entity.getBoundingBox().intersects(fluidBounds);
    }

    public static boolean isExposed(LivingEntity entity) {
        var level = entity.level();
        BlockPos floor = entity.getOnPosLegacy();
        if (entity.onGround() && level.hasChunkAt(floor) && level.getBlockState(floor).getBlock() instanceof CorruptionBlock) return true;
        var bounds = entity.getBoundingBox();
        // Only inspect the victim's occupied cells at death; never load chunks or scan nearby entities.
        for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(bounds.minX + 1.0E-7), Mth.floor(bounds.minY + 1.0E-7), Mth.floor(bounds.minZ + 1.0E-7),
                Mth.floor(bounds.maxX - 1.0E-7), Mth.floor(bounds.maxY - 1.0E-7), Mth.floor(bounds.maxZ - 1.0E-7))) {
            if (!level.isOutsideBuildHeight(pos) && level.hasChunkAt(pos)
                    && (intersectsFluid(level.getBlockState(pos), level, pos, entity)
                    || level.getBlockState(pos).getBlock() instanceof com.miophas.corruption.common.block.CorruptionTeethBlock
                    || level.getBlockState(pos).getBlock() instanceof com.miophas.corruption.common.block.LostSoulBlock)) return true;
        }
        return false;
    }

    private CorruptionExposure() {}
}
