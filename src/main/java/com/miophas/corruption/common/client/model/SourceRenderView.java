package com.miophas.corruption.common.client.model;

import com.miophas.corruption.common.blockentity.InfectedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

/** Connected models see the original materials at neighboring corrupted positions. */
public record SourceRenderView(BlockAndTintGetter delegate) implements BlockAndTintGetter {
    @Override
    public BlockState getBlockState(BlockPos pos) {
        return delegate.getBlockEntity(pos) instanceof InfectedBlockEntity entity ? entity.source() : delegate.getBlockState(pos);
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        BlockEntity entity = delegate.getBlockEntity(pos);
        return entity instanceof InfectedBlockEntity ? null : entity;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) { return getBlockState(pos).getFluidState(); }

    @Override
    public int getHeight() { return delegate.getHeight(); }

    @Override
    public int getMinBuildHeight() { return delegate.getMinBuildHeight(); }

    @Override
    public float getShade(Direction direction, boolean shade) { return delegate.getShade(direction, shade); }

    @Override
    public LevelLightEngine getLightEngine() { return delegate.getLightEngine(); }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver resolver) { return delegate.getBlockTint(pos, resolver); }
}
