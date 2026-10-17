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
    /**
     * Stunned (a goshawk's screech, say): it can neither move nor jump (its speed and its jump taken to nothing), nor strike,
     * nor use anything (see Stuns, and on a player's own client StunClient); stars wheel about its head until it wears off.
     */
    public static final DeferredHolder<MobEffect, MobEffect> STUNNED = EFFECTS.register("stunned",
            () -> new MobEffect(MobEffectCategory.HARMFUL, 0xF2E6A0) {
                @Override public boolean shouldApplyEffectTickThisTick(int remaining, int amplifier) { return remaining % 3 == 0; }
                @Override public boolean applyEffectTick(LivingEntity target, int amplifier) {
                    if (target.level() instanceof net.minecraft.server.level.ServerLevel level)
                        for (int star = 0; star < 3; star++) {
                            double round = target.tickCount * .35 + star * Math.PI * 2 / 3, out = Math.max(.35, target.getBbWidth() * .6);
                            level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, target.getX() + Math.cos(round) * out,
                                    target.getY() + target.getBbHeight() + .25, target.getZ() + Math.sin(round) * out, 1, 0, 0, 0, 0);
                        }
                    return true;
                }
            }.addAttributeModifier(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "stunned"), -1,
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(net.minecraft.world.entity.ai.attributes.Attributes.JUMP_STRENGTH,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "stunned"), -1,
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "stunned"), -1,
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    private ModEffects() {}
}
