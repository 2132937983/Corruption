package com.miophas.corruption.common.arcane;

import com.miophas.corruption.Corruption;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

/**
 * Creatures a spell brought forth: each remembers who summoned it, and never turns on them, nor on anything else
 * they summoned. What it does otherwise is its own nature.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class SpellSummons {
    private static final String SUMMONER = "corruption:summoner";

    /** Marks a creature as summoned by this player. */
    public static void bind(Mob mob, Player summoner) { mob.getPersistentData().putUUID(SUMMONER, summoner.getUUID()); }

    /** Who summoned this creature, or null if no spell did. */
    public static UUID summoner(Entity entity) {
        return entity.getPersistentData().hasUUID(SUMMONER) ? entity.getPersistentData().getUUID(SUMMONER) : null;
    }

    /** A summoned creature never takes its summoner, or another of their summons, for its target. */
    @SubscribeEvent public static void target(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewAboutToBeSetTarget();
        UUID summoner = summoner(event.getEntity());
        if (target != null && summoner != null && (summoner.equals(target.getUUID()) || summoner.equals(summoner(target)))) event.setCanceled(true);
    }

    private SpellSummons() {}
}
