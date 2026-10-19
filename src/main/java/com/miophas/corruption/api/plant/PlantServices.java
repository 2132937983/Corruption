package com.miophas.corruption.api.plant;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Runtime effects and configuration installed by common, shared by native and add-on plants. */
public final class PlantServices {
    @FunctionalInterface public interface Attraction { void attract(ServerLevel level, AABB area, BlockPos anchor, long expires); }
    private static DoubleSupplier blindness = () -> 6, attractionRadius = () -> 16;
    private static Predicate<BlockState> decoration = state -> false;
    private static Attraction attraction = (level, area, anchor, expires) -> {};
    public static void install(DoubleSupplier blindnessRadius, DoubleSupplier remnantsRadius,
            Predicate<BlockState> replaceable, Attraction attract) {
        blindness = Objects.requireNonNull(blindnessRadius); attractionRadius = Objects.requireNonNull(remnantsRadius);
        decoration = Objects.requireNonNull(replaceable); attraction = Objects.requireNonNull(attract);
    }
    public static double blindnessRadius() { return blindness.getAsDouble(); }
    public static double attractionRadius() { return attractionRadius.getAsDouble(); }
    public static boolean decoration(BlockState state) { return decoration.test(state); }
    public static void attract(ServerLevel level, AABB area, BlockPos anchor, long expires) { attraction.attract(level, area, anchor, expires); }
    private PlantServices() {}
}
