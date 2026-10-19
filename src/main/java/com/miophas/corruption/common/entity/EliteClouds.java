package com.miophas.corruption.common.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.elite.CorruptionElite;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.elite.BloaterRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

/**
 * A cloud of the corruption's gas let out by an elite (a bloater's vent, its burst as it dies): the game's lingering cloud,
 * owned by the elite, shrinking as it lies, cursing and poisoning whatever stands in it; and whatever is within it as it is
 * let out, not of the elite's side, thrown away from it. Its gas spares the elite and its own side: the game's cloud
 * would poison everything standing in it, its owner too.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class EliteClouds {
    public static AreaEffectCloud release(ServerLevel level, LivingEntity owner, Vec3 at, BloaterRules.Cloud cloud) {
        AreaEffectCloud gas = new AreaEffectCloud(level, at.x, at.y, at.z);
        gas.setOwner(owner);
        gas.setParticle(ModParticles.CORRUPTION_PARTICLE.get());
        gas.setRadius(cloud.radius());
        gas.setDuration(cloud.ticks());
        gas.setWaitTime(0);
        gas.setRadiusPerTick(-cloud.radius() / (cloud.ticks() * 2F));
        if (cloud.curseTicks() > 0) gas.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, cloud.curseTicks()));
        if (cloud.poisonTicks() > 0) gas.addEffect(new MobEffectInstance(MobEffects.POISON, cloud.poisonTicks()));
        level.addFreshEntity(gas);
        if (cloud.push() > 0) for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(cloud.radius()), near -> near != owner
                && near.isAlive() && !owner.isAlliedTo(near) && near.distanceToSqr(at) <= cloud.radius() * cloud.radius()
                && !(near instanceof Player player && (player.isCreative() || player.isSpectator())))) {
            Vec3 away = near.position().subtract(at).multiply(1, 0, 1);
            away = away.lengthSqr() < 1e-4 ? new Vec3(1, 0, 0) : away.normalize();
            near.push(away.x * cloud.push(), .25, away.z * cloud.push());
            near.hurtMarked = true;
        }
        level.sendParticles(ModParticles.CORRUPTION_PARTICLE.get(), at.x, at.y + .5, at.z, 40, cloud.radius() / 2, .5, cloud.radius() / 2, .05);
        return gas;
    }

    /** What an elite's cloud would lay on the elite itself, or on its own side, it does not. */
    @SubscribeEvent
    static void spareItsOwn(MobEffectEvent.Applicable event) {
        if (event.getEffectSource() instanceof AreaEffectCloud cloud && cloud.getOwner() instanceof CorruptionElite<?> elite
                && (event.getEntity() == elite || elite.isAlliedTo(event.getEntity())))
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }

    private EliteClouds() {}
}
