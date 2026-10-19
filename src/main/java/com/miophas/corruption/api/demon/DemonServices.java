package com.miophas.corruption.api.demon;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import net.minecraft.world.entity.Entity;

/** Runtime policy supplied by common; the reusable demon base has no configuration dependency. */
public final class DemonServices {
    private static volatile BooleanSupplier arcaneOnly = () -> true;
    private static volatile BiPredicate<Entity, Entity> feuding = (first, second) -> false;
    public static void install(BooleanSupplier damagePolicy, BiPredicate<Entity, Entity> hostility) {
        arcaneOnly = Objects.requireNonNull(damagePolicy); feuding = Objects.requireNonNull(hostility);
    }
    public static boolean arcaneOnly() { return arcaneOnly.getAsBoolean(); }
    public static boolean feuding(Entity first, Entity second) { return feuding.test(first, second); }
    private DemonServices() {}
}
