package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.RemnantAbility;
import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

final class RemnantFlightGoal extends Goal {
    private final CorruptedRemnant remnant;
    private int nextHit;
    RemnantFlightGoal(CorruptedRemnant remnant) { this.remnant = remnant; setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
    @Override public boolean canUse() {
        return remnant.hasAbility(RemnantAbility.FLIGHT) && !remnant.hasAbility(RemnantAbility.DRAGON_COMBAT)
                && !remnant.hasRangedAttack() && remnant.abilities().valid(remnant.getTarget());
    }
    @Override public boolean canContinueToUse() { return canUse(); }
    @Override public boolean requiresUpdateEveryTick() { return true; }
    @Override public void tick() {
        var target = remnant.getTarget(); if (target == null) return;
        Vec3 pos = target.getEyePosition().add(0, -remnant.getBbHeight() * .5, 0);
        remnant.getMoveControl().setWantedPosition(pos.x, pos.y, pos.z, 1);
        remnant.getLookControl().setLookAt(target, 30, 30);
        if (remnant.tickCount >= nextHit && remnant.getBoundingBox().inflate(.25).intersects(target.getBoundingBox())) {
            nextHit = remnant.tickCount + 20; remnant.swing(InteractionHand.MAIN_HAND); remnant.doHurtTarget(target);
        }
    }
    @Override public void stop() { remnant.getNavigation().stop(); }
}
