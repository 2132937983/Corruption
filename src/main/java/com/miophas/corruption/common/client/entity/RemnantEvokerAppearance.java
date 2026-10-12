package com.miophas.corruption.common.client.entity;

import com.miophas.corruption.api.corruption.RemnantAbility;
import com.miophas.corruption.api.corruption.RemnantAbilityApi;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Evoker;

/** Client-only appearance proxy; its spell pose follows the actual remnant's cast. */
final class RemnantEvokerAppearance extends Evoker {
    private final CorruptedRemnant remnant;
    RemnantEvokerAppearance(CorruptedRemnant remnant) { super(EntityType.EVOKER, remnant.level()); this.remnant = remnant; }
    void updateSpell() {
        RemnantAbility ability = remnant.castingAbility();
        boolean casting = ability == RemnantAbility.SUMMON_VEX || ability == RemnantAbility.EVOKER_FANGS;
        spellCastingTickCount = casting ? Math.max(1, RemnantAbilityApi.spec(ability).durationTicks() - (int) remnant.abilityCastTicks(0)) : 0;
        setIsCastingSpell(ability == RemnantAbility.SUMMON_VEX ? IllagerSpell.SUMMON_VEX
                : ability == RemnantAbility.EVOKER_FANGS ? IllagerSpell.FANGS : IllagerSpell.NONE);
    }
}
