package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemnantDragonRulesTest {
    @Test void nativeCombatIsExclusiveToDragonSources() {
        assertTrue(RemnantAbility.DRAGON_COMBAT.in(RemnantAbilityApi.flags("minecraft:ender_dragon",0)));
        for(String source:java.util.List.of("minecraft:ghast","minecraft:blaze","minecraft:wither","minecraft:vex","minecraft:cow"))
            assertFalse(RemnantAbility.DRAGON_COMBAT.in(RemnantAbilityApi.flags(source,0)));
    }
    @Test void headAndArmoredPartsFollowNativeDamageFormula() {
        assertEquals(20,RemnantDragonApi.receivedDamage(true,20));
        assertEquals(6,RemnantDragonApi.receivedDamage(false,20));
        assertEquals(.625F,RemnantDragonApi.receivedDamage(false,.5F));
        assertEquals(0,RemnantDragonApi.receivedDamage(false,0));
    }
    @Test void groundedPhasesStopAndChargeIsFasterThanCruising() {
        for(var phase:RemnantDragonPhase.values())if(phase.sitting())assertEquals(0,RemnantDragonApi.speed(phase));
        assertTrue(RemnantDragonApi.speed(RemnantDragonPhase.CHARGE)>RemnantDragonApi.speed(RemnantDragonPhase.HOLDING));
        assertEquals(200,RemnantDragonApi.breathDuration());assertEquals(600,RemnantDragonApi.cloudDuration());
        assertEquals(RemnantDragonPhase.HOLDING,RemnantDragonPhase.fromId(-1));
        assertEquals(RemnantDragonPhase.HOLDING,RemnantDragonPhase.fromId(Integer.MAX_VALUE));
    }
}
