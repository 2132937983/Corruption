package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.GreedApi;
import com.miophas.corruption.common.registry.ModEffects;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class GreedProjectile extends ThrowableProjectile {
    public GreedProjectile(EntityType<? extends GreedProjectile> type, Level level) { super(type, level); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected double getDefaultGravity() { return 0; }
    @Override protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !CorruptionFactionApi.isRemnant(entity)
                && (!(entity instanceof Player player) || !player.isCreative() && !player.isSpectator());
    }
    @Override protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if (!level().isClientSide && hit.getEntity() instanceof LivingEntity living)
            living.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, GreedApi.curseTicks(), 0), getOwner());
    }
    @Override protected void onHit(HitResult hit) { super.onHit(hit); if (!level().isClientSide) discard(); }
    @Override public void tick() { super.tick(); if (!level().isClientSide && tickCount >= GreedApi.projectileLifetime()) discard(); }
}
