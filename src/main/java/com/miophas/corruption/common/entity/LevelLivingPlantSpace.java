package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.plant.LivingPlantSpace;
import com.miophas.corruption.common.block.CorruptionPlantBlock;
import com.miophas.corruption.common.block.EerieBlock;
import com.miophas.corruption.common.block.EldritchVineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** World adapter: solid terrain and fluids obstruct growth, replaceable decorations do not. */
public final class LevelLivingPlantSpace implements LivingPlantSpace {
    private final LevelReader level;
    private final BlockPos anchor;
    public LevelLivingPlantSpace(LevelReader level, BlockPos anchor) { this.level = level; this.anchor = anchor; }
    private BlockPos pos(int x, int y, int z) { return anchor.offset(x, y, z); }
    @Override public boolean available(int x, int y, int z) {
        BlockPos pos = pos(x, y, z);
        return !level.isOutsideBuildHeight(pos) && level.hasChunkAt(pos);
    }
    @Override public boolean canOccupy(int x, int y, int z) {
        if (!available(x, y, z)) return false;
        BlockPos pos = pos(x, y, z);
        var state = level.getBlockState(pos);
        return state.isAir() || state.getFluidState().isEmpty() &&
                (state.getBlock() instanceof CorruptionPlantBlock || state.getBlock() instanceof EerieBlock
                || state.getBlock() instanceof EldritchVineBlock
                || state.canBeReplaced() && state.getCollisionShape(level, pos).isEmpty());
    }
    @Override public void clearDecoration(int x, int y, int z) {
        if (level instanceof Level writable && !writable.isClientSide && available(x, y, z) && canOccupy(x, y, z)) {
            BlockPos pos = pos(x, y, z);
            if (!level.getBlockState(pos).isAir()) writable.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }
}
