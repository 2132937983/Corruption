package com.miophas.corruption.api.altar;

import com.miophas.corruption.core.altar.AltarFacing;
import com.miophas.corruption.core.altar.AltarForm;
import com.miophas.corruption.core.altar.AltarState;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

/** Read and operate native altars without referencing their block entity or recipe implementation. */
public final class AltarApi {
    public record View(AltarState state, AltarForm form, AltarFacing facing, int elapsed,
                       Optional<ResourceLocation> recipe, Optional<Mob> bound) {}
    public interface Provider {
        Optional<View> find(ServerLevel level, BlockPos position);
        List<ResourceLocation> structures(ServerLevel level);
        boolean build(ServerLevel level, BlockPos position, ResourceLocation structure, AltarFacing facing, boolean withAltar);
    }
    private static volatile Provider provider;
    public static void install(Provider implementation) { provider = Objects.requireNonNull(implementation); }
    private static Provider world() {
        if (provider == null) throw new IllegalStateException("Altars are unavailable before mod construction");
        return provider;
    }
    public static Optional<View> find(ServerLevel level, BlockPos position) { return world().find(level, position); }
    public static List<ResourceLocation> structures(ServerLevel level) { return world().structures(level); }
    /** Explicit world edit, intended for add-on commands or generation; never calls chunk loading itself. */
    public static boolean build(ServerLevel level, BlockPos position, ResourceLocation structure, AltarFacing facing, boolean withAltar) {
        return world().build(level, position, structure, facing, withAltar);
    }
    private AltarApi() {}
}
