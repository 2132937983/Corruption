package com.miophas.corruption.api.altar;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

/** Lifecycle extensions keyed to data-pack recipe IDs. All callbacks run on the server thread. */
public final class AltarRituals {
    public record Context(ServerLevel level, BlockPos position, ResourceLocation recipe, int elapsed, int duration,
                          List<ItemStack> offerings, Optional<Mob> bound) {
        public Context { position = position.immutable(); offerings = offerings.stream().map(ItemStack::copy).toList(); }
    }
    public enum End { COMPLETED, INTERRUPTED, REJECTED }
    public interface Handler {
        default boolean canStart(Context context) { return true; }
        default void started(Context context) {}
        default void tick(Context context) {}
        /** False aborts before offerings are consumed. */
        default boolean canComplete(Context context) { return true; }
        /** Suppress the standard output while retaining normal offering consumption. Emit custom outputs in completed(). */
        default boolean replacesResult(Context context) { return false; }
        default void completed(Context context) {}
        default void ended(Context context, End reason) {}
    }
    private record Entry(ResourceLocation recipe, Handler handler) {}
    private static final ConcurrentSkipListMap<String, Entry> HANDLERS = new ConcurrentSkipListMap<>();
    public static void register(ResourceLocation id, ResourceLocation recipe, Handler handler) {
        if (HANDLERS.putIfAbsent(Objects.requireNonNull(id).toString(), new Entry(Objects.requireNonNull(recipe), Objects.requireNonNull(handler))) != null)
            throw new IllegalStateException("Altar handler already registered: " + id);
    }
    public static List<Handler> handlers(ResourceLocation recipe) {
        if (HANDLERS.isEmpty()) return List.of();
        return HANDLERS.values().stream().filter(entry -> entry.recipe.equals(recipe)).map(Entry::handler).toList();
    }
    public static void unregister(ResourceLocation id) { HANDLERS.remove(id.toString()); }
    private AltarRituals() {}
}
