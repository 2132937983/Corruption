package com.miophas.corruption.api.infection;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/** Extensions used by the mod's existing infection scheduler. Register during common setup. */
public final class InfectionExtensions {
    public interface Conversion {
        /** Read-only, inexpensive eligibility check. Never load chunks. */
        boolean matches(BlockGetter level, BlockPos pos, BlockState original);
        /** Replacement state only; the mod applies it and performs normal decoration and scheduling. */
        BlockState convert(BlockGetter level, BlockPos pos, BlockState original);
    }
    private record Source(Predicate<BlockState> corrupted, Predicate<BlockState> spreads) {}
    private record Rule(ResourceLocation id, int priority, Conversion conversion) {}
    private static final ConcurrentHashMap<ResourceLocation, Source> SOURCES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ResourceLocation, Rule> RULES = new ConcurrentHashMap<>();
    private static volatile List<Source> sources = List.of();
    private static volatile List<Rule> ordered = List.of();
    public static synchronized void registerSource(ResourceLocation id, Predicate<BlockState> corrupted, Predicate<BlockState> spreads) {
        if (SOURCES.putIfAbsent(Objects.requireNonNull(id), new Source(Objects.requireNonNull(corrupted), Objects.requireNonNull(spreads))) != null)
            throw new IllegalStateException("Infection source already registered: " + id);
        sources = List.copyOf(SOURCES.values());
    }
    public static synchronized void registerConversion(ResourceLocation id, int priority, Conversion conversion) {
        Objects.requireNonNull(id); Objects.requireNonNull(conversion);
        if (RULES.putIfAbsent(id, new Rule(id, priority, conversion)) != null) throw new IllegalStateException("Conversion already registered: " + id);
        reorder();
    }
    public static boolean hasSources() { return !sources.isEmpty(); }
    public static boolean isCorrupted(BlockState state) { for (Source source : sources) if (source.corrupted.test(state)) return true; return false; }
    public static boolean isSource(BlockState state) { for (Source source : sources) if (source.corrupted.test(state) && source.spreads.test(state)) return true; return false; }
    public static Optional<Conversion> conversion(BlockGetter level, BlockPos pos, BlockState state) {
        for (Rule rule : ordered) if (rule.conversion.matches(level, pos, state)) return Optional.of(rule.conversion);
        return Optional.empty();
    }
    public static synchronized void unregister(ResourceLocation id) { SOURCES.remove(id); sources = List.copyOf(SOURCES.values()); RULES.remove(id); reorder(); }
    private static void reorder() { ordered = RULES.values().stream().sorted(Comparator.comparingInt(Rule::priority).reversed().thenComparing(rule -> rule.id.toString())).toList(); }
    private InfectionExtensions() {}
}
