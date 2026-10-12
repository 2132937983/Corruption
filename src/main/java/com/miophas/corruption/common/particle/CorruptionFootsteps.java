package com.miophas.corruption.common.particle;

import com.miophas.corruption.common.registry.ModParticles;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/** One animation per visit to a block, using movement callbacks instead of entity scans. */
public final class CorruptionFootsteps {
    // Accessed only on the server thread; weak keys avoid retaining dead or unloaded entities.
    private static final Map<LivingEntity, Contact> LAST_CONTACT = new WeakHashMap<>();

    public static void step(Level level, BlockPos pos, Entity entity) {
        if (!(level instanceof ServerLevel server) || !(entity instanceof LivingEntity living)
                || !living.isAlive() || living.isSpectator() || !living.onGround()) return;
        Contact previous = LAST_CONTACT.get(living);
        boolean sameVisit = previous != null && previous.dimension().equals(level.dimension())
                && previous.pos().equals(pos) && living.tickCount - previous.tick() >= 0
                && living.tickCount - previous.tick() <= 1;
        boolean alreadyPlayed = sameVisit && previous.played();
        boolean play = !alreadyPlayed && isMoving(living);
        // Update stationary contacts too: stopping and starting on the same block is one visit.
        LAST_CONTACT.put(living, new Contact(level.dimension(), pos.immutable(), living.tickCount, alreadyPlayed || play));
        if (!play) return;
        double x = living.getX() + (server.random.nextDouble() - 0.5) * 0.35;
        double z = living.getZ() + (server.random.nextDouble() - 0.5) * 0.35;
        // A zero count sends one particle with an exact velocity, shared by nearby clients.
        server.sendParticles(ModParticles.CORRUPTION_SCREAM.get(), x, living.getY() + 0.05, z,
                0, (server.random.nextDouble() - 0.5) * 0.008, 0.035,
                (server.random.nextDouble() - 0.5) * 0.008, 1.0);
    }

    public static boolean isMoving(LivingEntity living) {
        double dx = living.getX() - living.xo;
        double dz = living.getZ() - living.zo;
        // Network-controlled players may have positional movement without horizontal velocity.
        return dx * dx + dz * dz >= 0.000001 || living.getDeltaMovement().horizontalDistanceSqr() >= 0.000001;
    }

    private record Contact(ResourceKey<Level> dimension, BlockPos pos, int tick, boolean played) {}

    private CorruptionFootsteps() {}
}
