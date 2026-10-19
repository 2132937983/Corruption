package com.miophas.corruption.api.demon.skill;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** Independent skill factories. Each creation returns fresh per-mob state. */
public final class BossSkillRegistry {
    public record Type<R>(ResourceLocation id, Class<R> rules) {
        public Type { Objects.requireNonNull(id); Objects.requireNonNull(rules); }
    }
    public record Options(int colour, int accent, ToIntFunction<Mob> count, boolean upAtOnce) {
        public Options { Objects.requireNonNull(count); }
        public Options(int colour, int accent) { this(colour, accent, mob -> Integer.MAX_VALUE, false); }
    }
    @FunctionalInterface public interface Factory<R> { BossSkill<Mob> create(R rules, Options options); }
    private record Entry<R>(Class<R> rules, Factory<R> factory) {
        BossSkill<Mob> create(Object value, Options options) { return Objects.requireNonNull(factory.create(rules.cast(value), options)); }
    }
    private static final Map<BossSkill<?>, Boolean> RESOLVED = java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());
    private static final Map<ResourceLocation, Entry<?>> DEFAULTS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Entry<?>> CUSTOM = new ConcurrentHashMap<>();
    /** Registers an additional skill, or overrides a built-in default. Duplicate custom registrations are rejected. */
    public static <R> void register(Type<R> type, Factory<R> factory) { put(CUSTOM, type, factory, false); }
    /** Explicitly replaces a custom factory; other skill registrations remain untouched. */
    public static <R> void replace(Type<R> type, Factory<R> factory) { put(CUSTOM, type, factory, true); }
    /** Installs the mod's default without replacing registrations made by add-ons, regardless of setup order. */
    public static <R> void registerDefault(Type<R> type, Factory<R> factory) { put(DEFAULTS, type, factory, false); }
    private static <R> void put(Map<ResourceLocation, Entry<?>> map, Type<R> type, Factory<R> factory, boolean replace) {
        Objects.requireNonNull(type); Objects.requireNonNull(factory);
        synchronized (BossSkillRegistry.class) {
            for (var registered : new Entry<?>[]{DEFAULTS.get(type.id()), CUSTOM.get(type.id())})
                if (registered != null && registered.rules() != type.rules()) throw new IllegalArgumentException("Different rule types for " + type.id());
            if (!replace && map.containsKey(type.id())) throw new IllegalStateException("Skill already registered: " + type.id());
            map.put(type.id(), new Entry<>(type.rules(), factory));
        }
    }
    public static void unregister(ResourceLocation id) { CUSTOM.remove(id); }
    public static <R> BossSkill<Mob> create(Type<R> type, R rules, Options options) {
        Objects.requireNonNull(type); Objects.requireNonNull(rules); Objects.requireNonNull(options);
        Entry<?> factory = CUSTOM.get(type.id());
        if (factory == null) factory = DEFAULTS.get(type.id());
        if (factory == null) throw new IllegalStateException("Skill has not been registered: " + type.id());
        if (factory.rules() != type.rules()) throw new IllegalArgumentException("Different rule types for " + type.id());
        BossSkill<Mob> created = factory.create(rules, options);
        RESOLVED.put(created, Boolean.TRUE);
        return created;
    }
    /** Resolve only a registered override; retain the exact native instance and state by default. */
    public static <R> BossSkill<Mob> overrideOr(Type<R> type, R rules, Options options, BossSkill<Mob> fallback) {
        if (RESOLVED.containsKey(fallback) || !CUSTOM.containsKey(type.id())) return fallback;
        return create(type, rules, options);
    }
    private BossSkillRegistry() {}
}
