package com.miophas.corruption.api.corruption;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** Per-remnant custom abilities, in addition to the built-in ability flags. Server thread only. */
public final class RemnantExtensions {
    public record Context(Mob host, ResourceLocation source, CompoundTag appearance, CompoundTag data) {}
    public interface Ability {
        void tick();
        /** Release temporary entities and state when the host is removed. */
        default void removed() {}
    }
    @FunctionalInterface public interface Factory { Ability create(Context context); }
    private record Entry(Predicate<ResourceLocation> source, Factory factory) {}
    private static final ConcurrentSkipListMap<String, Entry> ENTRIES = new ConcurrentSkipListMap<>();
    public static void register(ResourceLocation id, Predicate<ResourceLocation> sources, Factory factory) {
        if (ENTRIES.putIfAbsent(Objects.requireNonNull(id).toString(), new Entry(Objects.requireNonNull(sources), Objects.requireNonNull(factory))) != null)
            throw new IllegalStateException("Remnant ability already registered: " + id);
    }
    /** Creates independent runtime objects; context.data is saved with the host's persistent entity data. */
    public static List<Ability> create(Mob host, ResourceLocation source, CompoundTag appearance) {
        if (ENTRIES.isEmpty()) return List.of();
        List<Ability> result = new ArrayList<>();
        CompoundTag persistent = host.getPersistentData();
        if (!persistent.contains("corruption:extension_abilities", net.minecraft.nbt.Tag.TAG_COMPOUND))
            persistent.put("corruption:extension_abilities", new CompoundTag());
        CompoundTag states = persistent.getCompound("corruption:extension_abilities");
        ENTRIES.forEach((id, entry) -> {
            if (!entry.source.test(source)) return;
            if (!states.contains(id, net.minecraft.nbt.Tag.TAG_COMPOUND)) states.put(id, new CompoundTag());
            result.add(Objects.requireNonNull(entry.factory.create(new Context(host, source, appearance.copy(), states.getCompound(id)))));
        });
        return List.copyOf(result);
    }
    public static void unregister(ResourceLocation id) { ENTRIES.remove(id.toString()); }
    private RemnantExtensions() {}
}
