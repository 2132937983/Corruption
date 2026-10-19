package com.miophas.corruption.api.elite;

import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/** Native death particles used by the reusable elite base. */
public final class EliteServices {
    private static BiConsumer<ServerLevel, LivingEntity> death = (level, entity) -> {};
    public static void install(BiConsumer<ServerLevel, LivingEntity> particles) { death = Objects.requireNonNull(particles); }
    public static void death(ServerLevel level, LivingEntity entity) { death.accept(level, entity); }
    private EliteServices() {}
}
