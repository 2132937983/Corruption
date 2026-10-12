package com.miophas.corruption.api.infection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

/** All calls belong on the logical server thread. */
public interface InfectionScheduler {
    void chunkLoaded(ChunkPos chunk);

    void chunkUnloaded(ChunkPos chunk);

    void activate(BlockPos source);

    void deactivate(BlockPos source);

    void tick(long gameTime);

    /** Replace settings without discarding queued sources or in-progress chunk scans. */
    void updateSettings(InfectionSettings settings, long gameTime);

    void clear();
}
