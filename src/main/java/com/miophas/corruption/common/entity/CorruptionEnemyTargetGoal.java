package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.warden.Warden;

/** Supplies a faction target; movement and attacks remain the original mob's responsibility. */
public final class CorruptionEnemyTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private int unseenTicks;

    public CorruptionEnemyTargetGoal(Mob mob) {
        super(mob, LivingEntity.class, 10, true, false, CorruptionEnemyTargetGoal::enemy);
        // Corruption is a separate faction even when entities share a scoreboard team.
        targetConditions = TargetingConditions.forNonCombat().range(getFollowDistance())
                .selector(CorruptionEnemyTargetGoal::enemy);
    }

    private static boolean enemy(LivingEntity entity) {
        return entity.isAlive() && entity.canBeSeenAsEnemy() && CorruptionFactionApi.isRemnant(entity);
    }

    @Override public boolean canUse() {
        return !CorruptionFactionApi.isRemnant(mob) && !mob.isNoAi() && super.canUse();
    }

    @Override public void start() {
        unseenTicks = 0;
        super.start();
        if (mob instanceof Warden warden) warden.setAttackTarget(target);
        syncBrain();
    }

    private void syncBrain() {
        if(mob instanceof net.minecraft.world.entity.monster.piglin.AbstractPiglin
                && mob.getBrain().checkMemory(MemoryModuleType.ANGRY_AT,MemoryStatus.REGISTERED)){
            // Piglin brains otherwise reject the supplied target as soon as their FIGHT activity runs.
            mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT,target.getUUID(),600L);
        }
        if (mob.getBrain().checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED)) {
            mob.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        }
    }

    @Override public boolean canContinueToUse() {
        if (CorruptionFactionApi.isRemnant(mob) || mob.isNoAi() || target == null || !enemy(target)
                || mob.distanceToSqr(target) > getFollowDistance() * getFollowDistance()) return false;
        if (mob.getSensing().hasLineOfSight(target)) unseenTicks = 0;
        else if (++unseenTicks > reducedTickDelay(unseenMemoryTicks)) return false;
        mob.setTarget(target);
        syncBrain();
        return true;
    }

    @Override public void stop() {
        if(target!=null&&mob instanceof net.minecraft.world.entity.monster.piglin.AbstractPiglin
                && mob.getBrain().checkMemory(MemoryModuleType.ANGRY_AT,MemoryStatus.VALUE_PRESENT)
                && mob.getBrain().getMemory(MemoryModuleType.ANGRY_AT).orElse(null).equals(target.getUUID()))mob.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        if (mob.getBrain().checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
                && mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null) == target) {
            mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
        super.stop();
        target = null;
    }
}
