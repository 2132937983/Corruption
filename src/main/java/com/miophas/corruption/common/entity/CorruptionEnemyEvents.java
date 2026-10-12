package com.miophas.corruption.common.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class CorruptionEnemyEvents {
    @SubscribeEvent public static void join(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Mob mob)
                || CorruptionFactionApi.isRemnant(mob)) return;
        // Attack attributes alone are insufficient: passive animals can also have those attributes.
        if (!(mob instanceof Monster || mob instanceof AbstractGolem
                || mob.getType().getCategory() == MobCategory.MONSTER)) return;
        if (mob.targetSelector.getAvailableGoals().stream()
                .anyMatch(goal -> goal.getGoal() instanceof CorruptionEnemyTargetGoal)) return;
        mob.targetSelector.addGoal(0, new CorruptionEnemyTargetGoal(mob));
    }

    private CorruptionEnemyEvents() {}
}
