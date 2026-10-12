package com.miophas.corruption.common.entity;

import java.util.EnumSet;
import com.miophas.corruption.api.corruption.RemnantCombatApi;
import com.miophas.corruption.api.corruption.RemnantDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;

/** Forty-tick bow/crossbow cadence, with eight ticks to raise the weapon and twenty to draw. */
final class RemnantBowAttackGoal extends Goal {
    private final CorruptedRemnant remnant;
    private static final RemnantDefinition DEFINITION = RemnantCombatApi.definition();
    private int remaining, visibleTicks;
    private net.minecraft.world.entity.LivingEntity previousTarget;

    RemnantBowAttackGoal(CorruptedRemnant remnant) {
        this.remnant = remnant;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean canUse() {
        return remnant.hasAnimatedWeapon() && remnant.getTarget() != null && remnant.getTarget().isAlive();
    }
    @Override public boolean canContinueToUse() { return canUse(); }
    @Override public boolean requiresUpdateEveryTick() { return true; }
    @Override public void start() {
        remaining = DEFINITION.rangedInterval();
        visibleTicks = 0;
        previousTarget = remnant.getTarget();
        remnant.setAggressive(true);
        remnant.stopBowDraw();
    }
    @Override public void stop() {
        remnant.stopBowDraw();
        remnant.setAggressive(false);
        previousTarget = null;
        remnant.getNavigation().stop();
    }
    @Override public void tick() {
        var target = remnant.getTarget();
        if (target == null || !target.isAlive()) return;
        if (target != previousTarget) {
            previousTarget = target;
            remaining = DEFINITION.rangedInterval();
            visibleTicks = 0;
            remnant.stopBowDraw();
        }
        double distance = remnant.distanceToSqr(target);
        boolean visible = remnant.getSensing().hasLineOfSight(target);
        visibleTicks = visible ? visibleTicks + 1 : 0;
        if (distance <= DEFINITION.rangedDistance() * DEFINITION.rangedDistance() && visibleTicks >= 5) remnant.getNavigation().stop();
        else remnant.getNavigation().moveTo(target, DEFINITION.attackSpeed());
        remnant.getLookControl().setLookAt(target, 30, 30);
        if (!visible) {
            remnant.stopBowDraw();
            remaining = DEFINITION.rangedInterval();
            return;
        }
        if (--remaining == DEFINITION.rangedInterval() - 12) remnant.beginBowDraw();
        if (remaining <= 0) {
            remnant.performRangedAttack(target, Mth.clamp((float) Math.sqrt(distance) / DEFINITION.rangedDistance(), 0.1F, 1));
            remnant.stopBowDraw();
            remaining = DEFINITION.rangedInterval();
        }
    }
}
