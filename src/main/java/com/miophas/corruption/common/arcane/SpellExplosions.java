package com.miophas.corruption.common.arcane;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.core.arcane.SpellAction;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

/**
 * A spell's explosion: the game's own (its hurt, its knockback, its sound and smoke, what it tells the clients), set
 * off by the caster, so that it never hurts them; but what it breaks is bounded as the spell says
 * ({@link SpellAction.Explode}): within its break radius, no harder than its resistance limit, at most so many blocks,
 * the nearest first. The bounding is done as the explosion goes off, after anything guarding the land has had its say
 * (this runs last), so it only ever takes blocks away from what would break.
 *
 * <p>But for one thing: a blast that crushes adds every block near it no harder than it crushes, which its force alone
 * would have spared (obsidian, say), and never a block that cannot be broken at all (bedrock). So that it adds nothing
 * whatever guards the land would keep, each such block must first be one its caster might break: the game's break
 * event is fired for it, as for a block the caster broke by hand, and a cancelled one is left.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class SpellExplosions {
    /** The bounds of the spell's explosion going off right now, on this thread; null while none is. */
    private static SpellAction.Explode going;
    private static Vec3 goingAt;
    private static ServerPlayer goingBy;

    /** Sets a spell's explosion off here, for this caster. */
    public static void explode(ServerPlayer caster, Vec3 at, SpellAction.Explode action) {
        ServerLevel level = caster.serverLevel();
        going = action.breaksBlocks() && (action.limited() || action.crushes()) ? action : null;
        goingAt = at;
        goingBy = caster;
        try {
            level.explode(caster, at.x, at.y, at.z, (float) Math.max(0, action.power()),
                    action.breaksBlocks() ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
        } finally {
            going = null;
            goingAt = null;
            goingBy = null;
        }
    }

    /** Bounds what a spell's explosion breaks, as it goes off. */
    @SubscribeEvent(priority = EventPriority.LOWEST) public static void bound(ExplosionEvent.Detonate event) {
        SpellAction.Explode action = going;
        if (action == null || !event.getExplosion().center().equals(goingAt)) return;
        bound(event.getLevel(), event.getExplosion(), event.getAffectedBlocks(), goingAt, action, goingBy);
    }

    /**
     * What of these blocks the spell may break: none further than its radius, none harder than its limit, and of the
     * rest only the nearest, as many as it may. Air takes nothing from the count.
     */
    static void bound(Level level, Explosion explosion, List<BlockPos> blocks, Vec3 at, SpellAction.Explode action) {
        bound(level, explosion, blocks, at, action, null);
    }

    /** As {@link #bound(Level, Explosion, List, Vec3, SpellAction.Explode)}, crushing what it crushes as this caster might break it (with none, as anyone might). */
    static void bound(Level level, Explosion explosion, List<BlockPos> blocks, Vec3 at, SpellAction.Explode action, @Nullable Player caster) {
        double reach = action.breakRadius() * action.breakRadius();
        blocks.removeIf(pos -> {
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) return false;
            return Vec3.atCenterOf(pos).distanceToSqr(at) > reach || state.getExplosionResistance(level, pos, explosion) > action.maxResistance();
        });
        if (action.crushes()) crush(level, blocks, at, action, caster);
        List<BlockPos> solid = blocks.stream().filter(pos -> !level.getBlockState(pos).isAir())
                .sorted(Comparator.comparingDouble(pos -> Vec3.atCenterOf(pos).distanceToSqr(at))).toList();
        if (solid.size() <= action.maxBlocks()) return;
        Set<BlockPos> spared = new HashSet<>(solid.subList(action.maxBlocks(), solid.size()));
        blocks.removeIf(spared::contains);
    }

    /**
     * Adds to what breaks every block within the blast's crushing reach no harder than it crushes that is not breaking
     * already: no air, no liquid, nothing that cannot be broken at all, and nothing its caster might not break.
     */
    private static void crush(Level level, List<BlockPos> blocks, Vec3 at, SpellAction.Explode action, @Nullable Player caster) {
        Set<BlockPos> breaking = new HashSet<>(blocks);
        double most = action.crushReach() * action.crushReach();
        int reach = (int) Math.ceil(action.crushReach());
        BlockPos middle = BlockPos.containing(at);
        for (BlockPos pos : BlockPos.betweenClosed(middle.offset(-reach, -reach, -reach), middle.offset(reach, reach, reach))) {
            if (Vec3.atCenterOf(pos).distanceToSqr(at) > most || breaking.contains(pos) || !level.isLoaded(pos)) continue;
            BlockState state = level.getBlockState(pos);
            if (state.isAir() || !state.getFluidState().isEmpty() && state.getCollisionShape(level, pos).isEmpty()) continue;
            if (!action.crushes(state.getDestroySpeed(level, pos))) continue;
            if (caster != null && (!level.mayInteract(caster, pos) || NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, pos, state, caster)).isCanceled())) continue;
            BlockPos crushed = pos.immutable();
            blocks.add(crushed);
            breaking.add(crushed);
        }
    }

    private SpellExplosions() {}
}
