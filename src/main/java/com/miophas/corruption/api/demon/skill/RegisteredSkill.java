package com.miophas.corruption.api.demon.skill;

import net.minecraft.world.entity.Mob;

/** Metadata allowing native SkillSets to honor add-on factory overrides without concrete implementation casts. */
public interface RegisteredSkill<R> extends BossSkill<Mob> {
    BossSkillRegistry.Type<R> type();
    R configuration();
    BossSkillRegistry.Options options();
    @Override default BossSkill<Mob> configured() { return BossSkillRegistry.overrideOr(type(), configuration(), options(), this); }
}
