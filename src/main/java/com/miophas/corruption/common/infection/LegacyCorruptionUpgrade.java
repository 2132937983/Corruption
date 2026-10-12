package com.miophas.corruption.common.infection;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.blockentity.InfectedBlockEntity;
import com.miophas.corruption.common.registry.GeneratedCorruption;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Loaded old facades upgrade in bounded batches; no world scan, forced chunks or block entity ticker. */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class LegacyCorruptionUpgrade {
    private static final Map<ServerLevel, LinkedHashSet<BlockPos>> PENDING = new HashMap<>();
    public static void enqueue(InfectedBlockEntity entity) {
        if (entity.getLevel() instanceof ServerLevel level) level.getServer().execute(() ->
                PENDING.computeIfAbsent(level, ignored -> new LinkedHashSet<>()).add(entity.getBlockPos()));
    }
    @SubscribeEvent public static void tick(ServerTickEvent.Post event) {
        for (var entry : PENDING.entrySet()) {
            var level = entry.getKey();
            var iterator = entry.getValue().iterator();
            for (int count = 0; count < 256 && iterator.hasNext(); count++) {
                BlockPos pos = iterator.next(); iterator.remove();
                var chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
                if (chunk == null || !(chunk.getBlockEntity(pos) instanceof InfectedBlockEntity old)) continue;
                ResourceLocation original = ResourceLocation.tryParse(old.sourceTag().getString("Name"));
                // Leave missing-mod appearance data intact instead of losing its saved identifier.
                if (original == null || !BuiltInRegistries.BLOCK.containsKey(original)) continue;
                var replacement = GeneratedCorruption.convert(old.source());
                if (replacement != null) level.setBlock(pos, replacement, 3);
            }
        }
        PENDING.values().removeIf(java.util.Collection::isEmpty);
    }
    @SubscribeEvent public static void unload(LevelEvent.Unload event) { if (event.getLevel() instanceof ServerLevel level) PENDING.remove(level); }
    @SubscribeEvent public static void stopped(ServerStoppedEvent event) { PENDING.clear(); }
    private LegacyCorruptionUpgrade() {}
}
