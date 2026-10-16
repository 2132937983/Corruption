package com.miophas.corruption.common.infection;

import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.block.CorruptionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Eligible states map to pre-registered static variants; no block entities are created. */
public final class CorruptionConversions {
    public static final TagKey<Block> INFECTABLE = TagKey.create(Registries.BLOCK, ResourceLocation.parse("corruption:infectable"));
    public static final TagKey<Block> IMMUNE = TagKey.create(Registries.BLOCK, ResourceLocation.parse("corruption:infection_immune"));

    public static boolean canConvert(BlockState state) {
        return canConvert(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    public static boolean canConvert(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.is(IMMUNE) || isCorrupted(state) || state.getBlock() instanceof com.miophas.corruption.common.block.CorruptionPlantBlock
                || state.getBlock() instanceof LeavesBlock || state.is(BlockTags.LEAVES)) return false;
        if (state.is(net.minecraft.world.level.block.Blocks.WATER) || state.is(net.minecraft.world.level.block.Blocks.LAVA)) return true;
        if (state.isAir() || state.getRenderShape() == RenderShape.INVISIBLE && !state.hasBlockEntity()
                || (state.getDestroySpeed(level, pos) < 0 && !state.is(net.minecraft.world.level.block.Blocks.BEDROCK))) return false;
        if (CorruptionOreConversions.isOre(state)) {
            return level instanceof Level world && CorruptionOreConversions.convert(state, world, pos) != null;
        }
        return com.miophas.corruption.common.registry.GeneratedCorruption.convert(state) != null;
    }

    public static BlockState convert(BlockState original) {
        if (original.is(net.minecraft.world.level.block.Blocks.WATER) || original.is(net.minecraft.world.level.block.Blocks.LAVA))
            return (original.is(net.minecraft.world.level.block.Blocks.WATER) ? ModBlocks.CORRUPTED_WATER : ModBlocks.CORRUPTED_LAVA).get()
                    .defaultBlockState().setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, original.getValue(net.minecraft.world.level.block.LiquidBlock.LEVEL));
        return com.miophas.corruption.common.registry.GeneratedCorruption.convert(original);
    }

    public static boolean isCorrupted(BlockState state) {
        return state.getBlock() instanceof CorruptionBlock || state.getBlock() instanceof com.miophas.corruption.common.block.CorruptedLiquidBlock;
    }

    /** Whether this is a source of the infection: corrupted, and of a corruption that spreads (not the dormant corruption). */
    public static boolean isSource(BlockState state) {
        return isCorrupted(state) && !(state.getBlock() instanceof CorruptionBlock corruption && !corruption.spreads());
    }

    private CorruptionConversions() {}
}
