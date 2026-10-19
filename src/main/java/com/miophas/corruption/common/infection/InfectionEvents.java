package com.miophas.corruption.common.infection;

import com.miophas.corruption.api.infection.InfectionApi;
import com.miophas.corruption.api.infection.InfectionScheduler;
import com.miophas.corruption.api.infection.InfectionSettings;
import com.miophas.corruption.common.config.InfectionConfig;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class InfectionEvents {
    private static final Map<ServerLevel, InfectionScheduler> SCHEDULERS = new HashMap<>();

    public static void install() {
        com.miophas.corruption.api.infection.WorldInfection.install(new com.miophas.corruption.api.infection.WorldInfection.Provider() {
            public void activate(ServerLevel level, BlockPos pos) { InfectionEvents.activate(level, pos); }
            public void deactivate(ServerLevel level, BlockPos pos) { InfectionEvents.deactivate(level, pos); }
            public boolean infect(ServerLevel level, BlockPos pos) {
                if (!level.hasChunkAt(pos) || level.isOutsideBuildHeight(pos)) return false;
                return new ServerInfectionWorld(level).tryInfect(pos);
            }
            public boolean isCorrupted(net.minecraft.world.level.block.state.BlockState state) { return CorruptionConversions.isCorrupted(state); }
            public boolean isSource(net.minecraft.world.level.block.state.BlockState state) { return CorruptionConversions.isSource(state); }
            public boolean canInfect(ServerLevel level, BlockPos pos) {
                return level.hasChunkAt(pos) && CorruptionConversions.canConvert(level.getBlockState(pos), level, pos);
            }
        });
    }

    /** Includes arbitrary registered add-on sources placed through ordinary block updates. */
    @SubscribeEvent public static void onNeighbors(net.neoforged.neoforge.event.level.BlockEvent.NeighborNotifyEvent event) {
        if (!com.miophas.corruption.api.infection.InfectionExtensions.hasSources()
                || !(event.getLevel() instanceof ServerLevel level)) return;
        BlockPos changed = event.getPos();
        if (com.miophas.corruption.api.infection.InfectionExtensions.isSource(event.getState())) activate(level, changed);
        for (var direction : event.getNotifiedSides()) {
            BlockPos neighbor = changed.relative(direction);
            if (level.hasChunkAt(neighbor) && com.miophas.corruption.api.infection.InfectionExtensions.isSource(level.getBlockState(neighbor))) activate(level, neighbor);
        }
    }

    private static InfectionScheduler scheduler(ServerLevel level) {
        return SCHEDULERS.computeIfAbsent(level,
                key -> InfectionApi.createScheduler(new ServerInfectionWorld(key), InfectionConfig.settings()));
    }

    public static void updateSettings(MinecraftServer server, InfectionSettings settings) {
        for (ServerLevel level : server.getAllLevels()) {
            InfectionScheduler scheduler = SCHEDULERS.get(level);
            if (scheduler != null) {
                scheduler.updateSettings(settings, level.getGameTime());
            }
        }
    }

    public static void activate(ServerLevel level, BlockPos pos) {
        scheduler(level).activate(pos);
    }

    /** A consumed/removed neighbor cannot give a dormant source new work. */
    public static void neighborChanged(ServerLevel level, BlockPos source, BlockPos neighbor) {
        if (level.hasChunkAt(neighbor)
                && CorruptionConversions.canConvert(level.getBlockState(neighbor), level, neighbor)) {
            activate(level, source);
        }
    }

    public static void deactivate(ServerLevel level, BlockPos pos) {
        InfectionScheduler scheduler = SCHEDULERS.get(level);
        if (scheduler != null) {
            scheduler.deactivate(pos);
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ChunkPos pos = event.getChunk().getPos();
            // Only enqueue bookkeeping here; block reads wait for a later server tick.
            level.getServer().execute(() -> scheduler(level).chunkLoaded(pos));
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ChunkPos pos = event.getChunk().getPos();
            level.getServer().execute(() -> {
                InfectionScheduler scheduler = SCHEDULERS.get(level);
                if (scheduler != null) {
                    scheduler.chunkUnloaded(pos);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            InfectionScheduler scheduler = SCHEDULERS.get(level);
            if (scheduler != null) {
                scheduler.tick(level.getGameTime());
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            InfectionScheduler scheduler = SCHEDULERS.remove(level);
            if (scheduler != null) {
                scheduler.clear();
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        SCHEDULERS.values().forEach(InfectionScheduler::clear);
        SCHEDULERS.clear();
    }

    private InfectionEvents() {}
}
