package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.SpellAction;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * What casting a spell needs of the world, which the rules themselves cannot do: carrying out an action, and
 * reading and raising the caster's corruption. The mod's own side fits an implementation once at startup
 * (common.arcane.SpellEffects); add-ons register individual actions without replacing the world adapter.
 * An absent adapter is an initialization error, raised before casting pays.
 */
public final class ArcaneCasting {
    /**
     * Where an action is carried out: the point it spreads from, where a bolt it launches sets off, and which
     * way. Cast by hand, that is the caster; carried by a bolt, it is where the bolt struck.
     *
     * @param at the middle of what it reaches: creatures, blocks and summons round about
     * @param launch where a bolt it launches sets off from
     * @param direction which way such a bolt flies; a unit vector, or zero if there is none
     * @param struck the creature a bolt struck there, if it struck one; null cast by hand, or where a bolt struck a block
     */
    public record Origin(Vec3 at, Vec3 launch, Vec3 direction, @Nullable Entity struck) {
        /** Where no creature was struck. */
        public Origin(Vec3 at, Vec3 launch, Vec3 direction) { this(at, launch, direction, null); }

        /** At the caster: round the middle of them; a bolt sets off from just under their eyes, along their gaze. */
        public static Origin of(ServerPlayer caster) {
            return new Origin(caster.getBoundingBox().getCenter(), new Vec3(caster.getX(), caster.getEyeY() - .1, caster.getZ()), caster.getLookAngle());
        }

        /** Where a bolt struck, flying this way: a bolt launched there sets off a little short of it, the same way. */
        public static Origin impact(Vec3 at, Vec3 flying) { return impact(at, flying, null); }

        /** Where a bolt struck this creature (or none), flying this way. */
        public static Origin impact(Vec3 at, Vec3 flying, @Nullable Entity struck) {
            Vec3 way = flying.lengthSqr() < 1e-8 ? Vec3.ZERO : flying.normalize();
            return new Origin(at, at.subtract(way.scale(.25)), way, struck);
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

    @FunctionalInterface public interface ActionHandler<A extends SpellAction> {
        void perform(ServerPlayer caster, Origin origin, A action);
    }
    private static final java.util.Map<Class<?>, ActionHandler<?>> HANDLERS = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<net.minecraft.resources.ResourceLocation, ActionHandler<SpellAction.Custom>> CUSTOM = new java.util.concurrent.ConcurrentHashMap<>();
    private static volatile World installed, defaults;

    /** Adds or overrides one built-in action kind without taking over corruption bookkeeping. */
    public static <A extends SpellAction> void registerHandler(Class<A> type, ActionHandler<A> handler) {
        if (HANDLERS.putIfAbsent(java.util.Objects.requireNonNull(type), java.util.Objects.requireNonNull(handler)) != null)
            throw new IllegalStateException("Action handler already registered: " + type.getName());
    }
    public static <A extends SpellAction> void replaceHandler(Class<A> type, ActionHandler<A> handler) {
        HANDLERS.put(java.util.Objects.requireNonNull(type), java.util.Objects.requireNonNull(handler));
    }
    public static void unregisterHandler(Class<? extends SpellAction> type) { HANDLERS.remove(type); }
    /** Registers an add-on action for {type: custom, id: namespace:action, parameters: {...}}. */
    public static void registerAction(net.minecraft.resources.ResourceLocation id, ActionHandler<SpellAction.Custom> handler) {
        if (CUSTOM.putIfAbsent(java.util.Objects.requireNonNull(id), java.util.Objects.requireNonNull(handler)) != null)
            throw new IllegalStateException("Custom action already registered: " + id);
    }
    public static void replaceAction(net.minecraft.resources.ResourceLocation id, ActionHandler<SpellAction.Custom> handler) {
        CUSTOM.put(java.util.Objects.requireNonNull(id), java.util.Objects.requireNonNull(handler));
    }
    public static void unregisterAction(net.minecraft.resources.ResourceLocation id) { CUSTOM.remove(id); }
    public static void performCustom(ServerPlayer caster, Origin origin, SpellAction.Custom action) {
        var id = net.minecraft.resources.ResourceLocation.parse(action.id());
        var handler = CUSTOM.get(id);
        if (handler == null) throw new IllegalStateException("Custom spell action has not been registered: " + id);
        handler.perform(caster, origin, action);
    }
    private record Dispatch(World base) implements World {
        @Override public void perform(ServerPlayer caster, Origin origin, SpellAction action) {
            ActionHandler<?> handler = HANDLERS.get(action.getClass());
            if (handler != null) apply(handler, caster, origin, action);
            else if (action instanceof SpellAction.Custom custom) performCustom(caster, origin, custom);
            else base.perform(caster, origin, action);
        }
        @Override public int corruption(ServerPlayer caster) { return base.corruption(caster); }
        @Override public void corrupt(ServerPlayer caster, int gain) { base.corrupt(caster, gain); }
    }
    @SuppressWarnings("unchecked")
    private static <A extends SpellAction> void apply(ActionHandler<?> handler, ServerPlayer caster, Origin origin, A action) {
        ((ActionHandler<A>) handler).perform(caster, origin, action);
    }
    /** Legacy whole-world replacement. New extensions should register individual actions instead. Null restores the default. */
    @Deprecated public static void install(World implementation) { installed = implementation instanceof Dispatch dispatch ? dispatch.base() : implementation; }
    /** Common setup installs the default without overwriting an add-on's replacement. */
    public static void installDefault(World implementation) { defaults = java.util.Objects.requireNonNull(implementation); }
    /** Fails before a casting tool pays if common setup has not installed a world adapter. */
    public static World world() {
        World base = installed != null ? installed : defaults;
        if (base == null) throw new IllegalStateException("Spell world adapter must be installed before casting");
        return new Dispatch(base);
    }
    private ArcaneCasting() {}
}
