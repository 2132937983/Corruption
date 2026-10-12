package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.fluid.CorruptedLavaFluid;
import com.miophas.corruption.common.fluid.CorruptedWaterFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModFluids {
    public static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Corruption.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Corruption.MOD_ID);
    public static final DeferredHolder<FluidType, FluidType> WATER_TYPE = TYPES.register("corrupted_water", () -> new FluidType(FluidType.Properties.create()
            .descriptionId("block.corruption.corrupted_water").fallDistanceModifier(0).canExtinguish(true).canHydrate(true).supportsBoating(true)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)) {
        @Override public boolean canConvertToSource(FluidState state, LevelReader level, BlockPos pos) { return level instanceof Level world && world.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_WATER_SOURCE_CONVERSION); }
    });
    public static final DeferredHolder<FluidType, FluidType> LAVA_TYPE = TYPES.register("corrupted_lava", () -> new FluidType(FluidType.Properties.create()
            .descriptionId("block.corruption.corrupted_lava").canSwim(false).canDrown(false).pathType(PathType.LAVA).adjacentPathType(null)
            .density(3000).viscosity(6000).temperature(1300).lightLevel(15)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)) {
        @Override public boolean canConvertToSource(FluidState state, LevelReader level, BlockPos pos) { return level instanceof Level world && world.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_LAVA_SOURCE_CONVERSION); }
        @Override public boolean move(FluidState state, LivingEntity entity, Vec3 input, double gravity) {
            double previousY = entity.getY();
            entity.moveRelative(0.02F, input);
            entity.move(MoverType.SELF, entity.getDeltaMovement());
            if (entity.getFluidTypeHeight(this) <= entity.getFluidJumpThreshold()) {
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.5, 0.8, 0.5));
                entity.setDeltaMovement(entity.getFluidFallingAdjustedMovement(gravity, entity.getDeltaMovement().y <= 0, entity.getDeltaMovement()));
            } else entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5));
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, -gravity / 4, 0));
            Vec3 delta = entity.getDeltaMovement();
            if (entity.horizontalCollision && entity.isFree(delta.x, delta.y + 0.6 - entity.getY() + previousY, delta.z)) entity.setDeltaMovement(delta.x, 0.3, delta.z);
            return true;
        }
    });
    public static final DeferredHolder<Fluid, CorruptedWaterFluid.Source> WATER = FLUIDS.register("corrupted_water", CorruptedWaterFluid.Source::new);
    public static final DeferredHolder<Fluid, CorruptedWaterFluid.Flowing> FLOWING_WATER = FLUIDS.register("flowing_corrupted_water", CorruptedWaterFluid.Flowing::new);
    public static final DeferredHolder<Fluid, CorruptedLavaFluid.Source> LAVA = FLUIDS.register("corrupted_lava", CorruptedLavaFluid.Source::new);
    public static final DeferredHolder<Fluid, CorruptedLavaFluid.Flowing> FLOWING_LAVA = FLUIDS.register("flowing_corrupted_lava", CorruptedLavaFluid.Flowing::new);

    private static final com.miophas.corruption.api.corruption.FlowingCorruptionDefinition FLOWING_CORRUPTION_RULES = com.miophas.corruption.api.corruption.CorruptionCurseApi.flowingCorruption();
    /** Not tagged as water: it cannot be boated on, farmed with or breathed in, and its sources do not multiply. */
    public static final DeferredHolder<FluidType, FluidType> FLOWING_CORRUPTION_TYPE = TYPES.register(FLOWING_CORRUPTION_RULES.id(), () -> new FluidType(FluidType.Properties.create()
            .descriptionId("block.corruption.flowing_corruption").fallDistanceModifier(0)
            .density(FLOWING_CORRUPTION_RULES.density()).viscosity(FLOWING_CORRUPTION_RULES.viscosity())
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> FLOWING_CORRUPTION = FLUIDS.register(FLOWING_CORRUPTION_RULES.id(),
            () -> new BaseFlowingFluid.Source(ModFluids.FLOWING_CORRUPTION_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_CORRUPTION_FLOWING = FLUIDS.register(FLOWING_CORRUPTION_RULES.id() + "_flowing",
            () -> new BaseFlowingFluid.Flowing(ModFluids.FLOWING_CORRUPTION_PROPERTIES));
    private static final BaseFlowingFluid.Properties FLOWING_CORRUPTION_PROPERTIES = new BaseFlowingFluid.Properties(FLOWING_CORRUPTION_TYPE, FLOWING_CORRUPTION, FLOWING_CORRUPTION_FLOWING)
            .bucket(() -> ModItems.FLOWING_CORRUPTION_BUCKET.get()).block(() -> ModBlocks.FLOWING_CORRUPTION.get())
            .slopeFindDistance(FLOWING_CORRUPTION_RULES.slopeFindDistance()).levelDecreasePerBlock(FLOWING_CORRUPTION_RULES.levelDecreasePerBlock())
            .tickRate(FLOWING_CORRUPTION_RULES.tickDelay()).explosionResistance(100);

    public static void interactions() {
        for (FluidType lava : new FluidType[] {LAVA_TYPE.get(), NeoForgeMod.LAVA_TYPE.value()}) {
            FluidInteractionRegistry.addInteraction(lava, new FluidInteractionRegistry.InteractionInformation(
                    (level, pos, adjacent, state) -> level.getFluidState(adjacent).is(FluidTags.WATER),
                    state -> state.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState()));
        }
        FluidInteractionRegistry.addInteraction(LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                (level, pos, adjacent, state) -> level.getBlockState(pos.below()).is(Blocks.SOUL_SOIL) && level.getBlockState(adjacent).is(Blocks.BLUE_ICE), Blocks.BASALT.defaultBlockState()));
    }
    private ModFluids() {}
}
