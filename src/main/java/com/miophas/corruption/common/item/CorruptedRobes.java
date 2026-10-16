package com.miophas.corruption.common.item;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.core.corruption.CorruptedRobeRules;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * What a set of corrupted robes does for its wearer, beyond veiling it from the corruption's creatures (which they
 * ask for themselves, see CorruptionVeilApi.hiddenFrom): it holds a player's corruption fast while worn, raised to its
 * grade's threshold if below it and never lowered, and gives back what it was when taken off
 * ({@link CorruptedRobeRules#step}); and it turns aside the corruption's hurt as its grade does. Raising and purifying
 * corruption ask CorruptionVeilApi.holdsCorruption and leave it be while it is held.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class CorruptedRobes {
    @SubscribeEvent
    public static void tick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide) hold(event.getEntity());
    }

    /** A player's corruption, one moment on: taken hold of, held where their robe holds it, or let go and given back. */
    public static void hold(Player player) {
        int kept = player.getData(ModAttachments.CORRUPTION_KEPT), held = player.getData(ModAttachments.CORRUPTION_HELD);
        CorruptedRobeRules.Tier worn = CorruptionVeilApi.worn(player).orElse(null);
        if (worn == null && kept == CorruptedRobeRules.NOTHING_KEPT && held == CorruptedRobeRules.NOTHING_KEPT) return;
        int corruption = PlayerCorruption.value(player);
        CorruptedRobeRules.Hold now = CorruptedRobeRules.step(corruption, kept, held, worn);
        if (now.corruption() != corruption) player.setData(ModAttachments.PLAYER_CORRUPTION, now.corruption());
        if (now.kept() != kept) player.setData(ModAttachments.CORRUPTION_KEPT, now.kept());
        if (now.held() != held) player.setData(ModAttachments.CORRUPTION_HELD, now.held());
    }

    /** The corruption's hurt, as much of it as a robe lets through: half of it through the middle grade, none through the highest. */
    @SubscribeEvent
    public static void hurt(LivingIncomingDamageEvent event) {
        float through = CorruptionVeilApi.damageTaken(event.getEntity(), event.getSource(), event.getAmount());
        if (through <= 0) event.setCanceled(true);
        else if (through != event.getAmount()) event.setAmount(through);
    }

    private CorruptedRobes() {}
}
