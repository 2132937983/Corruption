package com.miophas.corruption.common.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModDamageTypes;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class ReanimationEvents {
    private record Pending(LivingDeathEvent event, ServerLevel level, CompoundTag appearance, float health, boolean corruptionDeath) {}
    private static final Map<UUID, Pending> PENDING = new LinkedHashMap<>();
    // Entity.equals compares numeric entity IDs, which players reuse after respawning.
    // Compare object identity so the next life can produce its own remnant.
    private static final Map<UUID, WeakReference<net.minecraft.world.entity.LivingEntity>> REANIMATED = new LinkedHashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void death(LivingDeathEvent event) {
        var victim = event.getEntity();
        boolean corruptionDeath = event.getSource().is(ModDamageTypes.CORRUPTION)||event.getSource().is(ModDamageTypes.CORRUPTION_CONVERSION);
        boolean playerDeath = victim instanceof Player;
        var previous = REANIMATED.get(victim.getUUID());
        if (!(victim.level() instanceof ServerLevel level) || com.miophas.corruption.api.corruption.CorruptionFactionApi.isRemnant(victim) || previous != null && previous.get() == victim
                || !corruptionDeath && !playerDeath && (!(victim instanceof Mob) || level.getDifficulty() == Difficulty.PEACEFUL)) return;
        if (!corruptionDeath && !playerDeath && !CorruptionExposure.isExposed(victim)) return;
        CompoundTag appearance = RemnantAppearance.capture(victim, corruptionDeath || playerDeath);
        // Player.die and a few modded die overrides can emit the same death event more than once.
        PENDING.putIfAbsent(victim.getUUID(), new Pending(event, level, appearance, victim.getMaxHealth(), corruptionDeath || playerDeath));
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        REANIMATED.values().removeIf(reference -> {
            var entity = reference.get();
            return entity == null || entity.isAlive();
        });
        var pending = List.copyOf(PENDING.values());
        PENDING.clear();
        for (Pending entry : pending) {
            var victim = entry.event.getEntity();
            if (entry.event.isCanceled() || !victim.isDeadOrDying() || !entry.corruptionDeath && entry.level.getDifficulty() == Difficulty.PEACEFUL) continue;
            var remnant = ModEntities.createRemnant(entry.level, entry.appearance);
            if (remnant == null) continue;
            remnant.initialize(entry.appearance, entry.health);
            remnant.moveTo(victim.getX(), victim.getY(), victim.getZ(), victim.getYRot(), 0);
            if (entry.level.addFreshEntity(remnant)) {
                REANIMATED.put(victim.getUUID(), new WeakReference<>(victim));
            }
        }
    }

    @SubscribeEvent
    public static void stopped(ServerStoppedEvent event) { PENDING.clear(); REANIMATED.clear(); }

    private ReanimationEvents() {}
}
