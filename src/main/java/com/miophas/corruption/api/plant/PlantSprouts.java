package com.miophas.corruption.api.plant;

import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.DoubleSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

/** Additional growth on malice during infection. Native growth is attempted if no extension succeeds. */
public final class PlantSprouts {
    @FunctionalInterface public interface Factory { boolean sprout(ServerLevel level, BlockPos anchor, int direction); }
    private record Entry(DoubleSupplier chance, Factory factory) {}
    private static final ConcurrentSkipListMap<String, Entry> ENTRIES = new ConcurrentSkipListMap<>();
    public static void register(ResourceLocation id, DoubleSupplier chance, Factory factory) {
        if (ENTRIES.putIfAbsent(Objects.requireNonNull(id).toString(), new Entry(Objects.requireNonNull(chance), Objects.requireNonNull(factory))) != null)
            throw new IllegalStateException("Plant sprout already registered: " + id);
    }
    public static boolean sprout(ServerLevel level, BlockPos anchor) {
        if (ENTRIES.isEmpty() || !level.hasChunkAt(anchor) || AnchoredLivingPlant.find(level, anchor) != null) return false;
        int direction = AnchoredLivingPlant.direction(level, anchor);
        if (direction == 0) return false;
        for (Entry entry : ENTRIES.values()) {
            double chance = entry.chance.getAsDouble();
            if (!Double.isFinite(chance) || chance < 0 || chance > 1) throw new IllegalArgumentException("Plant sprout probability outside [0, 1]");
            if (chance > 0 && level.random.nextDouble() < chance && entry.factory.sprout(level, anchor, direction)) return true;
        }
        return false;
    }
    public static void unregister(ResourceLocation id) { ENTRIES.remove(id.toString()); }
    private PlantSprouts() {}
}
