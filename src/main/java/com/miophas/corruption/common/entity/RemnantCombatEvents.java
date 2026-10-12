package com.miophas.corruption.common.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class RemnantCombatEvents {
    @SubscribeEvent public static void bottle(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        var player=event.getEntity();var level=event.getLevel();var stack=player.getItemInHand(event.getHand());
        if (!stack.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) return;
        var clouds=level.getEntitiesOfClass(CorruptedDragonBreath.class,player.getBoundingBox().inflate(2),cloud->cloud.isAlive()&&cloud.getRadius()>.5F);
        if(clouds.isEmpty())return;
        event.setCanceled(true);event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide));
        if(level.isClientSide)return;
        var cloud=clouds.getFirst();cloud.setRadius(cloud.getRadius()-.5F);
        level.playSound(null,player.blockPosition(),net.minecraft.sounds.SoundEvents.BOTTLE_FILL_DRAGONBREATH,net.minecraft.sounds.SoundSource.NEUTRAL,1,1);
        level.gameEvent(player,net.minecraft.world.level.gameevent.GameEvent.FLUID_PICKUP,player.position());
        if(player instanceof net.minecraft.server.level.ServerPlayer server)net.minecraft.advancements.CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(server,stack,cloud);
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(net.minecraft.world.item.Items.GLASS_BOTTLE));
        player.setItemInHand(event.getHand(),net.minecraft.world.item.ItemUtils.createFilledResult(stack,player,new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DRAGON_BREATH)));
    }
    /** Upgrade previously saved generic dragon remnants to the multipart entity before registration. */
    @SubscribeEvent(priority = EventPriority.HIGHEST) public static void dragon(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof CorruptedRemnant remnant)
                || remnant instanceof CorruptedDragon || !remnant.appearance().getString("Type").equals("minecraft:ender_dragon")) return;
        var dragon = com.miophas.corruption.common.registry.ModEntities.CORRUPTED_DRAGON.get().create(event.getLevel());
        if (dragon == null) return;
        dragon.load(remnant.saveWithoutId(new net.minecraft.nbt.CompoundTag()));
        event.setCanceled(true); event.getLevel().addFreshEntity(dragon);
    }
    @SubscribeEvent(priority = EventPriority.LOWEST) public static void projectile(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof Projectile projectile) {
            var owner = RemnantCombat.PROJECTILE_OWNER.get();
            if (owner != null) projectile.setOwner(owner);
        }
    }
    @SubscribeEvent public static void friendlyDamage(LivingIncomingDamageEvent event) {
        var attacker = event.getSource().getEntity();
        if (attacker instanceof CorruptedRemnant && CorruptionFactionApi.isRemnant(event.getEntity())) event.setCanceled(true);
    }
    @SubscribeEvent public static void friendlyImpact(net.neoforged.neoforge.event.entity.ProjectileImpactEvent event) {
        if (event.getProjectile().getOwner() instanceof CorruptedRemnant
                && event.getRayTraceResult() instanceof net.minecraft.world.phys.EntityHitResult hit
                && CorruptionFactionApi.isRemnant(hit.getEntity())) event.setCanceled(true);
    }
    private RemnantCombatEvents() {}
}
