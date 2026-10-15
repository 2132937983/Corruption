package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.SpellAction;
import net.minecraft.server.level.ServerPlayer;

/**
 * What casting a spell needs of the world, which the rules themselves cannot do: carrying out an action, and
 * reading and raising the caster's corruption. The mod's own side fits an implementation once at startup
 * (common.arcane.SpellEffects); an add-on with effects of its own may fit its own, or leave the default, under
 * which spells are paid for but do nothing.
 */
public final class ArcaneCasting {
    /** The world side of casting, as the casting rules use it. */
    public interface World {
        /** Carries out one action for this caster. */
        void perform(ServerPlayer caster, SpellAction action);
        /** How corrupted the caster is, on the corruption scale. */
        int corruption(ServerPlayer caster);
        /** Leaves the caster this much more corrupted, up to what the scale allows. */
        void corrupt(ServerPlayer caster, int gain);
    }

    /** Until the mod fits its own: nothing happens, and none is corrupted. */
    private static final World NONE = new World() {
        @Override public void perform(ServerPlayer caster, SpellAction action) {}
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
