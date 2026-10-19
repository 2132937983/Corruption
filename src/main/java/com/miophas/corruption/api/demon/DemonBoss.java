package com.miophas.corruption.api.demon;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * A demon with a boss bar drawn in the mod's own style (ColossusBossBar): which bar on the client is its own,
 * and which emblem the bar's medallion shows; and how it dies, as a spectacle (see {@link BossDeath}).
 */
public interface DemonBoss {
    /** Which boss bar on the client is this creature's; empty until the server has said. */
    Optional<UUID> bossBar();

    /** Its emblem on textures/gui/colossus_boss_bar.png: 0 Baal's, 1 Agares's, 2 Andromalius's, 3 Dantalion's, 4 Seere's, 5 Decarabia's, 6 Belial's, 7 Amdusias's. */
    int emblem();

    /** Optional standalone 24 x 24 emblem, supplied by an add-on resource pack. */
    default Optional<net.minecraft.resources.ResourceLocation> emblemTexture() { return Optional.empty(); }

    /** How it dies: drawn by every client about its body as it goes, and sounded by the server. */
    BossDeath death();

    /** Where its light pours out of it as it dies: its heart, in the world, this far through the tick. */
    Vec3 heart(float partial);

    /**
     * Writes to the server's log what killed a boss, as the game does only for creatures with names of their own: a
     * boss that is gone should never leave anyone wondering why.
     */
    static void logDeath(LivingEntity boss, DamageSource source) {
        LogUtils.getLogger().info("{} died at {}: {} (damage type {}{})", boss.getName().getString(), boss.blockPosition().toShortString(),
                boss.getCombatTracker().getDeathMessage().getString(), source.typeHolder().getRegisteredName(),
                source.getEntity() == null ? "" : ", dealt by " + source.getEntity().getName().getString());
    }
}
