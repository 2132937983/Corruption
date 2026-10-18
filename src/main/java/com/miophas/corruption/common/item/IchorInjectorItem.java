package com.miophas.corruption.common.item;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.core.energy.EnergyDeviceRules;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The ichor injector ({@link EnergyDeviceRules#INJECTOR}): a syringe of the corruption's own ichor, driven by a charge
 * of Corruption Energy. Used, it pays its cost, takes the corruption's curse off its user and heals them; then it must
 * rest. Without the charge for a dose it does nothing, and says how much it wants. In creative, it needs no charge.
 */
public final class IchorInjectorItem extends ChargedDeviceItem {
    private static final EnergyDeviceRules.Injector RULES = EnergyDeviceRules.INJECTOR;

    public IchorInjectorItem(Properties properties) { super(properties, RULES.tool()); }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        if (!player.getAbilities().instabuild && charge(stack) < RULES.cost()) {
            if (!level.isClientSide) player.displayClientMessage(Component.translatable("message.corruption.injector_uncharged",
                    CorruptionEnergyApi.format(RULES.cost())), true);
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) inject(player, stack, player.getAbilities().instabuild);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * A dose: paid for (unless {@code free}), the curse taken off whoever took it, and they healed; then the injector
     * rests, if they are a player. Whether it was given.
     */
    public boolean inject(LivingEntity user, ItemStack stack, boolean free) {
        if (!free && !pay(stack, RULES.cost())) return false;
        user.removeEffect(ModEffects.CORRUPTION_CURSE);
        user.heal(RULES.heal());
        if (user instanceof Player player) player.getCooldowns().addCooldown(this, RULES.cooldown());
        user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, .8F, .6F);
        if (user.level() instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.HEART, user.getX(), user.getY() + user.getBbHeight() * .6, user.getZ(), 3, .3, .3, .3, 0);
        return true;
    }
}
