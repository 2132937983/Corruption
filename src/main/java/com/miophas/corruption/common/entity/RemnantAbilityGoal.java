package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.RemnantAbility;
import com.miophas.corruption.api.corruption.RemnantAbilityApi;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/** Casts block wandering and melee; lost or allied targets cancel before the ability fires. */
final class RemnantAbilityGoal extends Goal {
    private final CorruptedRemnant remnant;
    private RemnantAbility ability;
    private LivingEntity target;
    private int age;
    RemnantAbilityGoal(CorruptedRemnant remnant) { this.remnant = remnant; setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }
    @Override public boolean canUse() { ability = remnant.abilities().choose(); return ability != null; }
    @Override public boolean canContinueToUse() {
        return ability != null && age < RemnantAbilityApi.spec(ability).durationTicks()
                && remnant.getTarget() == target && remnant.abilities().valid(target);
    }
    @Override public boolean requiresUpdateEveryTick() { return true; }
    @Override public void start() {
        age = 0; target = remnant.getTarget(); remnant.getNavigation().stop(); remnant.abilities().begin(ability);
    }
    @Override public void tick() {
        if (target == null) return;
        remnant.getLookControl().setLookAt(target, 30, 30);
        if (++age == RemnantAbilityApi.spec(ability).windupTicks()) remnant.abilities().perform(ability, target);
    }
    @Override public void stop() { remnant.endAbility(); target = null; ability = null; }
}
