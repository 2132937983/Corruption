package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.*;
import com.miophas.corruption.common.registry.ModEntities;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** A vanilla particle cloud with faction-safe damage, including undead enemies. */
public final class CorruptedDragonBreath extends AreaEffectCloud {
    private final Map<UUID,Integer> victims=new HashMap<>();
    private float damage=3;
    public CorruptedDragonBreath(EntityType<? extends CorruptedDragonBreath> type,Level level) { super(type,level); }
    static CorruptedDragonBreath create(ServerLevel level,LivingEntity owner,Vec3 pos,boolean fireball) {
        var cloud=ModEntities.CORRUPTED_DRAGON_BREATH.get().create(level);if(cloud==null)return null;
        cloud.setPos(pos);cloud.setOwner(owner);cloud.setParticle(ParticleTypes.DRAGON_BREATH);
        cloud.setRadius(fireball?3:5);cloud.setDuration(fireball?RemnantDragonApi.cloudDuration():RemnantDragonApi.breathDuration());
        cloud.setWaitTime(20);cloud.setRadiusPerTick(fireball?4F/RemnantDragonApi.cloudDuration():0);
        cloud.damage=fireball?6:3;level.addFreshEntity(cloud);return cloud;
    }
    @Override public void tick() {
        super.tick();
        if(level().isClientSide||isRemoved()||tickCount<getWaitTime()||tickCount%5!=0)return;
        victims.entrySet().removeIf(e->e.getValue()<=tickCount);
        for(var victim:level().getEntitiesOfClass(LivingEntity.class,getBoundingBox(),e->e.isAlive()&&!e.isSpectator()
                &&(!CorruptionFactionApi.isRemnant(e)||com.miophas.corruption.common.arcane.PreyMarks.feuding(getOwner(),e))
                &&!(e instanceof net.minecraft.world.entity.player.Player p&&p.getAbilities().invulnerable))) {
            double x=victim.getX()-getX(),z=victim.getZ()-getZ();
            if(x*x+z*z>getRadius()*getRadius()||victims.containsKey(victim.getUUID()))continue;
            victims.put(victim.getUUID(),tickCount+RemnantDragonApi.reapplication());
            victim.hurt(damageSources().indirectMagic(this,getOwner()),damage);
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag);tag.putFloat("DragonBreathDamage",damage); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag);damage=tag.contains("DragonBreathDamage",99)?net.minecraft.util.Mth.clamp(tag.getFloat("DragonBreathDamage"),0,6):3; }
}
