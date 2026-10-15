package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.SpellAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * What casting a spell needs of the world, which the rules themselves cannot do: carrying out an action, and
 * reading and raising the caster's corruption. The mod's own side fits an implementation once at startup
 * (common.arcane.SpellEffects); an add-on with effects of its own may fit its own, or leave the default, under
 * which spells are paid for but do nothing.
 */
public final class ArcaneCasting {
    /**
     * Where an action is carried out: the point it spreads from, where a bolt it launches sets off, and which
     * way. Cast by hand, that is the caster; carried by a bolt, it is where the bolt struck.
     *
     * @param at the middle of what it reaches: creatures, blocks and summons round about
     * @param launch where a bolt it launches sets off from
     * @param direction which way such a bolt flies; a unit vector, or zero if there is none
     */
    public record Origin(Vec3 at, Vec3 launch, Vec3 direction) {
        /** At the caster: round the middle of them; a bolt sets off from just under their eyes, along their gaze. */
        public static Origin of(ServerPlayer caster) {
            return new Origin(caster.getBoundingBox().getCenter(), new Vec3(caster.getX(), caster.getEyeY() - .1, caster.getZ()), caster.getLookAngle());
        }

        /** Where a bolt struck, flying this way: a bolt launched there sets off a little short of it, the same way. */
        public static Origin impact(Vec3 at, Vec3 flying) {
            Vec3 way = flying.lengthSqr() < 1e-8 ? Vec3.ZERO : flying.normalize();
            return new Origin(at, at.subtract(way.scale(.25)), way);
        }
    }

    /** The world side of casting, as the casting rules use it. */
    public interface World {
        /** Carries out one action for this caster, at this origin. */
        void perform(ServerPlayer caster, Origin origin, SpellAction action);
        /** How corrupted the caster is, on the corruption scale. */
        int corruption(ServerPlayer caster);
        /** Leaves the caster this much more corrupted, up to what the scale allows. */
        void corrupt(ServerPlayer caster, int gain);
    }

    /** Until the mod fits its own: nothing happens, and none is corrupted. */
    private static final World NONE = new World() {
        @Override public void perform(ServerPlayer caster, Origin origin, SpellAction action) {}
        @Override public int corruption(ServerPlayer caster) { return 0; }
        @Override public void corrupt(ServerPlayer caster, int gain) {}
    };

    private static World world = NONE;

    /** Fits the implementation spells are carried out through. */
    public static void install(World implementation) { world = implementation == null ? NONE : implementation; }

    /** The world casting works through. */
    public static World world() { return world; }

    private ArcaneCasting() {}
}
