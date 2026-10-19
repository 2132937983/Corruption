package com.miophas.corruption.api.corruption;

import java.util.Objects;
import net.minecraft.world.entity.player.Player;

/** Full gameplay settlement, including worn wards and completed transformation. Server thread only. */
public final class PlayerCorruptionChanges {
    @FunctionalInterface public interface Provider { int raise(Player player, int amount); }
    private static volatile Provider provider;
    public static void install(Provider implementation) { provider = Objects.requireNonNull(implementation); }
    /** Returns the actual increase. Creative players can gain corruption but are never killed by this call. */
    public static int raise(Player player, int amount) {
        if (provider == null) throw new IllegalStateException("Player corruption is unavailable before mod construction");
        return provider.raise(player, amount);
    }
    private PlayerCorruptionChanges() {}
}
