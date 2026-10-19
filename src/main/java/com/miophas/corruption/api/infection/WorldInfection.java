package com.miophas.corruption.api.infection;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/** Access to the mod's actual per-world scheduler, not a second independent infection engine. Server thread only. */
public final class WorldInfection {
    public interface Provider {
        void activate(ServerLevel level, BlockPos pos);
        void deactivate(ServerLevel level, BlockPos pos);
        boolean infect(ServerLevel level, BlockPos pos);
        boolean isCorrupted(BlockState state);
        boolean isSource(BlockState state);
        boolean canInfect(ServerLevel level, BlockPos pos);
    }
    private static volatile Provider provider;
    public static void install(Provider implementation) { provider = Objects.requireNonNull(implementation); }
    private static Provider world() {
        if (provider == null) throw new IllegalStateException("World infection is unavailable before mod construction");
        return provider;
    }
    public static void activate(ServerLevel level, BlockPos pos) { world().activate(level, pos); }
    public static void deactivate(ServerLevel level, BlockPos pos) { world().deactivate(level, pos); }
    public static boolean infect(ServerLevel level, BlockPos pos) { return world().infect(level, pos); }
    public static boolean isCorrupted(BlockState state) { return world().isCorrupted(state); }
    public static boolean isSource(BlockState state) { return world().isSource(state); }
    public static boolean canInfect(ServerLevel level, BlockPos pos) { return world().canInfect(level, pos); }
    private WorldInfection() {}
}
