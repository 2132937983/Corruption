package com.miophas.corruption.api.infection;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

/** World access supplied by common. Implementations must never force-load chunks. */
public interface InfectionWorld {
    /** Distinguish positions outside the world's build height from temporarily unloaded neighbors. */
    default boolean isInBounds(BlockPos pos) { return true; }

    boolean isLoaded(BlockPos pos);

    boolean isTicking(BlockPos pos);

    boolean isSource(BlockPos pos);

    boolean canInfect(BlockPos pos);

    /** Recheck the whitelist before replacing a block; return whether it changed. */
    boolean tryInfect(BlockPos pos);

    /** Chance that a solid becomes the base corruption block rather than a preserved variant. */
    default boolean tryInfect(BlockPos pos, double baseCorruptionChance) { return tryInfect(pos); }

    double randomDouble();

    int randomInt(int bound);

    /**
     * Continue scanning a chunk from {@code cursor}, spending at most {@code maxBlocks} units of work.
     *
     * <p>Report only sources that may have somewhere to spread: an infectable neighbour, or a neighbour whose
     * chunk is not loaded. Sources enclosed by blocks that cannot be infected should be left out. They are the
     * bulk of a large infection and would only fill the scheduler; a neighbour change wakes them later.
     */
    ScanResult scanSources(ChunkPos chunk, int cursor, int maxBlocks);

    /** @param inspectedBlocks work spent, in the same unit as the {@code maxBlocks} that was asked for */
    record ScanResult(List<BlockPos> sources, int nextCursor, int inspectedBlocks, boolean complete) {
        public ScanResult {
            sources = List.copyOf(sources);
        }
    }
}
