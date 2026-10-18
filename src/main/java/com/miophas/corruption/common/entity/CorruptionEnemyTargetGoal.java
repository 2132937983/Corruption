package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.AABB;

/**
 * Supplies a faction target; movement and attacks remain the original mob's responsibility. Like any creature's, its look
 * for other creatures reaches only four blocks up or down; but a lord of the corruption ({@code corruption:bosses}) it sees
 * at any height within its reach, so that Solomon's dragons, hanging high over the ground, are hunted too.
 */
public final class CorruptionEnemyTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    /** How far up or down a creature looks for another, as vanilla has it. */
    static final double REACH_UP = 4;
    private int unseenTicks;

    public CorruptionEnemyTargetGoal(Mob mob) {
        super(mob, LivingEntity.class, 10, true, false, CorruptionEnemyTargetGoal::enemy);
        // Corruption is a separate faction even when entities share a scoreboard team.
        targetConditions = TargetingConditions.forNonCombat().range(getFollowDistance())
                .selector(entity -> enemy(mob, entity));
    }

    private static boolean enemy(LivingEntity entity) {
        return entity.isAlive() && entity.canBeSeenAsEnemy() && CorruptionFactionApi.isRemnant(entity);
    }

    /** Whether a creature hunts this one: of the corruption, and within four blocks up or down of it unless a lord of it. */
    static boolean enemy(Mob seeker, LivingEntity entity) {
        return enemy(entity) && (CorruptionFactionApi.isBoss(entity)
                || entity.getBoundingBox().intersects(seeker.getBoundingBox().inflate(Double.MAX_VALUE / 4, REACH_UP, Double.MAX_VALUE / 4)));
    }

    @Override protected AABB getTargetSearchArea(double range) { return mob.getBoundingBox().inflate(range); }

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
