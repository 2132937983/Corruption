package com.miophas.corruption.common.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.registry.ModEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * What a creature stunned (ModEffects.STUNNED) cannot do, on the server and on its own client alike: strike anything
 * with its own hands, nor use an item, nor a block, nor another creature. Its feet are held by the effect itself (its
 * speed and its jump taken to nothing), and a player's own client stops its keys (StunClient).
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class Stuns {
    public static boolean stunned(LivingEntity creature) { return creature.hasEffect(ModEffects.STUNNED); }

    /** A blow struck by a stunned creature's own hand does not land; one by what it loosed before it was stunned does. */
    @SubscribeEvent public static void blow(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity striker && event.getSource().getDirectEntity() == striker && stunned(striker))
            event.setCanceled(true);
    }

    @SubscribeEvent public static void attack(AttackEntityEvent event) { stop(event.getEntity(), event); }
    @SubscribeEvent public static void use(LivingEntityUseItemEvent.Start event) { stop(event.getEntity(), event); }
    @SubscribeEvent public static void item(PlayerInteractEvent.RightClickItem event) { stop(event.getEntity(), event); }
    @SubscribeEvent public static void block(PlayerInteractEvent.RightClickBlock event) { stop(event.getEntity(), event); }
    @SubscribeEvent public static void dig(PlayerInteractEvent.LeftClickBlock event) { stop(event.getEntity(), event); }
    @SubscribeEvent public static void creature(PlayerInteractEvent.EntityInteract event) { stop(event.getEntity(), event); }

    private static void stop(LivingEntity creature, ICancellableEvent event) { if (stunned(creature)) event.setCanceled(true); }

    private Stuns() {}
}
