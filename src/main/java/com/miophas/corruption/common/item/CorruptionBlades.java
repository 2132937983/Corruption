package com.miophas.corruption.common.item;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.arcane.ArcaneWeapons;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.core.arcane.ArcaneBladeRules;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * What the corruption's blades do where they land, by their tags ({@link ArcaneBladeRules}, {@link ArcaneWeapons}).
 * The arcane's mark needs nothing here: the Goetic demons let a marked blow through (GoeticDemon.accepts), and the
 * corruption's other creatures take blows anyway. The curse: a cursed blade's blow does not land on a lord of the
 * corruption at all, and on anything else not of the corruption it lays the curse.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class CorruptionBlades {
    /** A cursed blow on one of the corruption's lords rings off it, and whoever struck is told why. */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void refuse(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (!ArcaneBladeRules.refused(ArcaneWeapons.isCursedBlow(event.getSource()), CorruptionFactionApi.isBoss(target))) return;
        event.setCanceled(true);
        target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, .4F, 1.6F);
        if (event.getSource().getEntity() instanceof ServerPlayer player)
            player.displayClientMessage(Component.translatable("message.corruption.curse_refused", target.getDisplayName()), true);
    }

    /** A cursed blow lays the curse on what it struck, once the hurt has landed, if what it struck is not of the corruption. */
    @SubscribeEvent
    public static void curse(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (!target.isAlive() || !ArcaneBladeRules.curses(ArcaneWeapons.isCursedBlow(event.getSource()), CorruptionFactionApi.isRemnant(target))) return;
        target.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, ArcaneBladeRules.CURSE_TICKS, ArcaneBladeRules.CURSE_LEVEL), event.getSource().getEntity());
    }

    private CorruptionBlades() {}
}
