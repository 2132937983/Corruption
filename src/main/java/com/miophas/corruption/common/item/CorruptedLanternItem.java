package com.miophas.corruption.common.item;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.core.energy.EnergyDeviceRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The corrupted lantern ({@link EnergyDeviceRules#LANTERN}): a soul lantern whose flame is the corruption's own, burning
 * on a charge of Corruption Energy. Held in either hand it burns, a little of its charge every second: its holder sees
 * in the dark, and the corruption's creatures about them glow through walls, so nothing of the corruption's comes on
 * them unseen. Out of charge, it gutters and does nothing.
 */
public final class CorruptedLanternItem extends ChargedDeviceItem {
    private static final EnergyDeviceRules.Lantern RULES = EnergyDeviceRules.LANTERN;

    public CorruptedLanternItem(Properties properties) { super(properties, RULES.tool()); }

    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(level instanceof ServerLevel server) || !(entity instanceof LivingEntity holder)) return;
        if (holder.getMainHandItem() != stack && holder.getOffhandItem() != stack) return;
        if ((server.getGameTime() + holder.getId()) % RULES.every() == 0) burn(server, holder, stack);
    }

    /**
     * One beat of the lantern in someone's hand: it pays its cost and, if it could, renews its holder's sight in the dark
     * and makes the corruption's creatures within its reach glow. Whether it burned.
     */
    public boolean burn(ServerLevel level, LivingEntity holder, ItemStack stack) {
        if (!pay(stack, RULES.cost())) return false;
        MobEffectInstance sight = holder.getEffect(MobEffects.NIGHT_VISION);
        if (sight == null || EnergyDeviceRules.renewSight(sight.getDuration(), RULES))
            holder.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, RULES.sight(), 0, true, false, true));
        for (LivingEntity creature : level.getEntitiesOfClass(LivingEntity.class, holder.getBoundingBox().inflate(RULES.radius()),
                creature -> creature != holder && creature.isAlive() && CorruptionFactionApi.isRemnant(creature) && creature.distanceTo(holder) <= RULES.radius()))
            creature.addEffect(new MobEffectInstance(MobEffects.GLOWING, RULES.glow(), 0, true, false));
        return true;
    }
}
