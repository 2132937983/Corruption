package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class CorruptedDragonFireball extends DragonFireball {
    public CorruptedDragonFireball(EntityType<? extends CorruptedDragonFireball> type,Level level) { super(type,level); }
    @Override protected void onHit(HitResult hit) {
        if(hit instanceof EntityHitResult entity && (ownedBy(entity.getEntity())||CorruptionFactionApi.isRemnant(entity.getEntity())
                &&!com.miophas.corruption.common.arcane.PreyMarks.feuding(getOwner(),entity.getEntity())))return;
        if(level() instanceof ServerLevel server) {
            Vec3 pos=position();
            for(var victim:server.getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(4,2,4),e->e.isAlive()&&!e.isSpectator()
                    &&(!CorruptionFactionApi.isRemnant(e)||com.miophas.corruption.common.arcane.PreyMarks.feuding(getOwner(),e))
                    &&!(e instanceof net.minecraft.world.entity.player.Player player&&player.getAbilities().invulnerable))) {
                if(distanceToSqr(victim)<16) {pos=victim.position();break;}
            }
            CorruptedDragonBreath.create(server,getOwner() instanceof LivingEntity owner?owner:null,pos,true);
            level().levelEvent(2006,blockPosition(),isSilent()?-1:1);discard();
        }
    }
}
