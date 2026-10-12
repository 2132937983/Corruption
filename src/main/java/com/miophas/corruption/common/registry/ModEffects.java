package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.block.CorruptionBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.*;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Corruption.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> CORRUPTION_CURSE = EFFECTS.register("corruption_curse",
            () -> new MobEffect(MobEffectCategory.HARMFUL, 0x83384D) {
                @Override public boolean shouldApplyEffectTickThisTick(int remaining, int amplifier) { return remaining % 20 == 0; }
                @Override public boolean applyEffectTick(LivingEntity target, int amplifier) {
                    if (!target.level().isClientSide && !CorruptionFactionApi.isRemnant(target)
                            && (!(target instanceof Player player) || !player.isCreative() && !player.isSpectator())) {
                        if(target instanceof Player player)com.miophas.corruption.common.entity.PlayerCorruption.cursePulse(player);
                        if(target.isAlive())
                        target.hurt(ModDamageTypes.corruption(target.level()), target.getMaxHealth() * CorruptionBlock.CONTACT_DAMAGE_FRACTION
                                * com.miophas.corruption.api.corruption.CorruptionCurseApi.damageScale(amplifier));
                    }
                    return true;
                }
            });
    private ModEffects() {}
}
