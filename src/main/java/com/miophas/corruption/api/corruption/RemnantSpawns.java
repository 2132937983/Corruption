package com.miophas.corruption.api.corruption;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/** Create native remnants with their source appearance and combat attributes. Returned entities are not added yet. */
public final class RemnantSpawns {
    public interface Provider {
        Optional<Mob> raise(ServerLevel level, EntityType<?> source, Vec3 position, float yaw);
        /** Capture a live or dead source. Does not kill, discard or change that source. */
        Optional<Mob> copy(LivingEntity source);
    }
    private static volatile Provider provider;
    public static void install(Provider implementation) { provider = Objects.requireNonNull(implementation); }
    private static Provider provider() {
        if (provider == null) throw new IllegalStateException("Remnant spawning is unavailable before mod construction");
        return provider;
    }
    public static Optional<Mob> raise(ServerLevel level, EntityType<?> source, Vec3 position, float yaw) { return provider().raise(level, source, position, yaw); }
    public static Optional<Mob> copy(LivingEntity source) { return provider().copy(source); }
    private RemnantSpawns() {}
}
