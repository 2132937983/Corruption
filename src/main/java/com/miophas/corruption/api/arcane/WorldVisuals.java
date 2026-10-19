package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.SpellAction;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/** Server-side effects for mobs, rituals and blocks, without a player or fake player. */
public final class WorldVisuals {
    @FunctionalInterface public interface Provider {
        void show(ServerLevel level, Vec3 position, SpellAction.Visual visual, float yaw, boolean grounded);
    }
    private static volatile Provider provider;
    /** Installed by the mod during common setup. */
    public static void install(Provider implementation) { provider = Objects.requireNonNull(implementation); }
    public static void show(ServerLevel level, Vec3 position, SpellAction.Visual visual, float yaw, boolean grounded) {
        Provider active = provider;
        if (active == null) throw new IllegalStateException("World visuals are unavailable before common setup");
        active.show(Objects.requireNonNull(level), Objects.requireNonNull(position), Objects.requireNonNull(visual), yaw, grounded);
    }
    private WorldVisuals() {}
}
