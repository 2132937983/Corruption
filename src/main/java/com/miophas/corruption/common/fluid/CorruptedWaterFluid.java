package com.miophas.corruption.common.fluid;

import com.miophas.corruption.common.registry.ModFluids;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import net.neoforged.neoforge.fluids.FluidType;

public abstract class CorruptedWaterFluid extends WaterFluid {
    @Override public Fluid getSource() { return ModFluids.WATER.get(); }
    @Override public Fluid getFlowing() { return ModFluids.FLOWING_WATER.get(); }
    @Override public FluidType getFluidType() { return ModFluids.WATER_TYPE.get(); }
    @Override public Item getBucket() { return ModItems.CORRUPTED_WATER_BUCKET.get(); }
    @Override public boolean isSame(Fluid fluid) { return fluid == getSource() || fluid == getFlowing() || super.isSame(fluid); }
    @Override public BlockState createLegacyBlock(FluidState state) { return ModBlocks.CORRUPTED_WATER.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state)); }
    public static final class Source extends CorruptedWaterFluid {
        @Override public int getAmount(FluidState state) { return 8; }
        @Override public boolean isSource(FluidState state) { return true; }
    }
    public static final class Flowing extends CorruptedWaterFluid {
        @Override protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) { super.createFluidStateDefinition(builder); builder.add(LEVEL); }
        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
        @Override public boolean isSource(FluidState state) { return false; }
    }
}
