package com.miophas.corruption.common.arcane;

import com.miophas.corruption.Corruption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * What spells have set the corruption's creatures to: prey to bring down (SpellAction.MarkPrey), and a caster to let
 * be (SpellAction.Truce). Each lasts until its time is up (or, for a hunt, until the prey falls), and none is saved: a
 * world loaded again begins with none.
 *
 * <p>A creature sent after its prey takes no other for its quarry while the hunt lasts, whatever its own mind would
 * have it do: every change of quarry it makes is turned back onto the prey. Should the prey be of the corruption
 * itself, the hunter and its prey are no longer allies while the hunt lasts ({@link #feuding}), so that the one may
 * strike the other. A creature under a truce never takes the one it was sworn with for its quarry.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class PreyMarks {
    /** A hunt: whom, and until when (game time). */
    private record Hunt(UUID prey, long until) {}
    /** A truce: with whom, and until when (game time). */
    private record Truce(UUID spared, long until) {}

    /** By hunter. */
    private static final Map<UUID, Hunt> HUNTS = new HashMap<>();
    /** By the creature that swore it; one truce each, the latest. */
    private static final Map<UUID, Truce> TRUCES = new HashMap<>();

    /** Sends this creature after this prey until then: it takes it for its quarry now, and no other till then. */
    public static void hunt(Mob hunter, LivingEntity prey, long until) {
        HUNTS.put(hunter.getUUID(), new Hunt(prey.getUUID(), until));
        hunter.setTarget(prey);
    }

    /** Has this creature let this one be until then: it gives it up as its quarry now, and does not take it again till then. */
    public static void spare(Mob creature, LivingEntity spared, long until) {
        TRUCES.put(creature.getUUID(), new Truce(spared.getUUID(), until));
        if (creature.getTarget() == spared) creature.setTarget(null);
    }

    /** Whether this creature is hunting that one now. */
    public static boolean hunts(Entity hunter, Entity prey) {
        Hunt hunt = HUNTS.get(hunter.getUUID());
        return hunt != null && hunt.prey().equals(prey.getUUID()) && hunter.level().getGameTime() < hunt.until();
    }

    /** Whether either of these is hunting the other now: then they are no allies, of whatever side they are. None is feuding with nothing. */
    public static boolean feuding(@Nullable Entity one, @Nullable Entity other) {
        return one != null && other != null && (hunts(one, other) || hunts(other, one));
    }

    /** Whether this creature has sworn to let that one be, now. */
    public static boolean spares(Entity creature, Entity target) {
        Truce truce = TRUCES.get(creature.getUUID());
        return truce != null && truce.spared().equals(target.getUUID()) && creature.level().getGameTime() < truce.until();
    }

    /** The prey this creature is hunting now, if it still lives where the hunter is; a hunt that has run its course is let go. */
    @Nullable public static LivingEntity preyOf(Entity hunter) {
        Hunt hunt = HUNTS.get(hunter.getUUID());
        if (hunt == null) return null;
        Entity prey = hunter.level() instanceof ServerLevel level ? level.getEntity(hunt.prey()) : null;
        if (hunter.level().getGameTime() >= hunt.until() || !(prey instanceof LivingEntity living) || !living.isAlive()) {
            HUNTS.remove(hunter.getUUID());
            return null;
        }
        return living;
    }

    /** A hunter's every change of quarry is turned back onto its prey; a creature under a truce gives up the one it spares. */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void target(LivingChangeTargetEvent event) {
        LivingEntity creature = event.getEntity(), next = event.getNewAboutToBeSetTarget();
        if (creature.level().isClientSide) return;
        LivingEntity prey = preyOf(creature);
        if (prey != null) {
            if (next != prey) event.setNewAboutToBeSetTarget(prey);
            return;
        }
        if (next != null && spares(creature, next)) event.setNewAboutToBeSetTarget(null);
    }

    /** A prey fallen, whoever hunted it goes back to its own mind. */
    @SubscribeEvent public static void fallen(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        UUID fallen = event.getEntity().getUUID();
        HUNTS.values().removeIf(hunt -> hunt.prey().equals(fallen));
        HUNTS.remove(fallen);
        TRUCES.remove(fallen);
    }

    /** Once a second, hunts and truces that have run their course are let go. */
    @SubscribeEvent public static void sweep(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 != 0 || HUNTS.isEmpty() && TRUCES.isEmpty()) return;
        long now = event.getServer().overworld().getGameTime();
        HUNTS.values().removeIf(hunt -> now >= hunt.until());
        TRUCES.values().removeIf(truce -> now >= truce.until());
    }

    @SubscribeEvent public static void stopped(ServerStoppedEvent event) {
        HUNTS.clear();
        TRUCES.clear();
    }

    private PreyMarks() {}
}
