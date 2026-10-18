package com.miophas.corruption.common.infection;

import com.miophas.corruption.common.registry.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The ground turned to the corruption from without its own spread: by a spell, by one of its creatures underfoot, by a
 * lord's blow. Either it is taken as the infection takes any block ({@link #infect}: now and then malice, now and then the
 * corruption itself, mostly the block's own corrupted form; it spreads on from there, unless taken dormant, which keeps
 * the variant but marks it still), or it is turned to the dormant corruption ({@link #dormant}), which spreads no further
 * but hurts whatever stands on it as any corruption does.
 */
public final class GroundBlight {
    /** Lets the infection take a block, as it takes any, if it can; whether it did. It spreads on from there. */
    public static boolean infect(ServerLevel level, BlockPos at) { return infect(level, at, false); }

    /**
     * Lets the infection take a block, as it takes any, if it can; whether it did. Dormant ({@code still}), the block
     * taken is an ordinary corrupted variant that never spreads on from there ({@link DormantGround}) - no second,
     * dormant twin of it is generated for the sake of it.
     */
    public static boolean infect(ServerLevel level, BlockPos at, boolean still) {
        if (!still) return new ServerInfectionWorld(level).tryInfect(at);
        if (!level.isLoaded(at)) return false;
        // Mark first: the taken block wakes the infection on placement, and a batch in the same tick may look at it.
        DormantGround.still(level, at);
        if (new ServerInfectionWorld(level).tryInfect(at)) return true;
        DormantGround.release(level, at);
        return false;
    }

    /** Whether the infection could take this block now. */
    public static boolean infectable(ServerLevel level, BlockPos at) { return new ServerInfectionWorld(level).canInfect(at); }

    /**
     * Whether a block can never be broken (bedrock, a barrier). The infection may take bedrock as it spreads, but what a
     * creature turns underfoot or a blow turns about it leaves such a block alone, lest it open the floor of the world.
     */
    public static boolean unbreakable(BlockGetter level, BlockPos at) { return level.getBlockState(at).getDestroySpeed(level, at) < 0; }

    /**
     * Turns a block to the dormant corruption, if the infection could take it and it is a whole, solid block of no ore and
     * no block entity: into that and nothing else (never malice, nor the corruption that spreads), with nothing growing on
     * it. Whether it did.
     */
    public static boolean dormant(ServerLevel level, BlockPos at) {
        if (!level.isLoaded(at)) return false;
        BlockState state = level.getBlockState(at);
        if (!dormantable(level, at, state)) return false;
        return level.setBlock(at, ModBlocks.DEACTIVATED_CORRUPTION.get().defaultBlockState(), Block.UPDATE_ALL);
    }

    /** Whether {@link #dormant} would turn this block. */
    public static boolean dormantable(BlockGetter level, BlockPos at, BlockState state) {
        return CorruptionConversions.canConvert(state, level, at) && !CorruptionOreConversions.isOre(state) && whole(level, at, state);
    }

    /** Whether this is a whole, solid block: no liquid, no block entity, a full cube that hides what is behind it. */
    public static boolean whole(BlockGetter level, BlockPos at, BlockState state) {
        return state.getFluidState().isEmpty() && !state.hasBlockEntity() && state.canOcclude() && state.isCollisionShapeFullBlock(level, at);
    }

    /**
     * The top block of the ground in this column near this height: the highest that something can stand on with room over
     * it, from {@code above} blocks over the height down to {@code below} under it; none if there is none in that span.
     */
    @Nullable public static BlockPos surface(BlockGetter level, int x, int y, int z, int above, int below) {
        for (int at = y + above; at >= y - below; at--) {
            BlockPos ground = new BlockPos(x, at, z);
            if (level.getBlockState(ground).blocksMotion() && !level.getBlockState(ground.above()).blocksMotion()) return ground;
        }
        return null;
    }

    private GroundBlight() {}
}
