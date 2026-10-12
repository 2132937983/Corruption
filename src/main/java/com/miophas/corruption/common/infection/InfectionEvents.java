package com.miophas.corruption.common.infection;

import com.miophas.corruption.api.infection.InfectionApi;
import com.miophas.corruption.api.infection.InfectionScheduler;
import com.miophas.corruption.api.infection.InfectionSettings;
import com.miophas.corruption.common.config.InfectionConfig;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class InfectionEvents {
    private static final Map<ServerLevel, InfectionScheduler> SCHEDULERS = new HashMap<>();

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
