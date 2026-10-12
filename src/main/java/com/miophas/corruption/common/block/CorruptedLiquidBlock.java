package com.miophas.corruption.common.block;

import com.miophas.corruption.common.infection.InfectionEvents;
import com.miophas.corruption.common.entity.CorruptionExposure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

/** Native liquid geometry and flow scheduling, with the same bounded infection scheduler as solids. */
public final class CorruptedLiquidBlock extends LiquidBlock {
    private final boolean lava;
    public CorruptedLiquidBlock(FlowingFluid fluid, boolean lava, Properties properties) {
        super(fluid, properties);
        this.lava = lava;
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moved) {
        super.onPlace(state, level, pos, old, moved);
        if (level instanceof ServerLevel server && !old.is(this) && level.getBlockState(pos).is(this)) InfectionEvents.activate(server, pos);
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moved) {
        super.onRemove(state, level, pos, next, moved);
        if (level instanceof ServerLevel server && !next.is(this)) InfectionEvents.deactivate(server, pos);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos from, boolean moved) {
        super.neighborChanged(state, level, pos, neighbor, from, moved);
        if (level instanceof ServerLevel server && level.getBlockState(pos).is(this)) InfectionEvents.neighborChanged(server, pos, from);
    }
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!(level instanceof ServerLevel) || entity.isSpectator()) return;
        if (!CorruptionExposure.intersectsFluid(state, level, pos, entity)) return;
        // Use the same hurt cooldown as ground contact, even when touching multiple fluid cells.
        CorruptionBlock.applyContactDamage(level, entity);
        // Custom fluid types do not enter Entity's vanilla LAVA_TYPE branch.
        if (lava) entity.lavaHurt();
    }
}
