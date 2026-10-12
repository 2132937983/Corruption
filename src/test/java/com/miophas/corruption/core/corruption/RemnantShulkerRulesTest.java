package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemnantShulkerRulesTest {
    @Test void shellAndProjectileAbilityIsSeparateFromFlight() {
        int flags = RemnantAbilityApi.flags("minecraft:shulker", 0);
        assertTrue(RemnantAbility.SHULKER_COMBAT.in(flags));
        assertFalse(RemnantAbility.FLIGHT.in(flags));
        assertFalse(RemnantAbility.SHULKER_COMBAT.in(RemnantAbilityApi.flags("minecraft:ghast", 0)));
    }
    @Test void shellOpensWithinItsNativeBoundingBox() {
        assertEquals(0, RemnantShulkerApi.physicalPeek(0), 1.0E-6);
        assertEquals(.5F, RemnantShulkerApi.physicalPeek(.5F), 1.0E-6);
        assertEquals(1, RemnantShulkerApi.physicalPeek(1), 1.0E-6);
        assertEquals(0, RemnantShulkerApi.physicalPeek(-1), 1.0E-6);
        assertEquals(1, RemnantShulkerApi.physicalPeek(2), 1.0E-6);
    }
    @Test void ReproductionCrowdingAndCooldownHaveNativeLimits() {
        assertEquals(1, RemnantShulkerApi.reproductionChance(1));
        assertEquals(.4F, RemnantShulkerApi.reproductionChance(4), 1.0E-6);
        assertEquals(0, RemnantShulkerApi.reproductionChance(6));
        assertEquals(0, RemnantShulkerApi.reproductionChance(20));
        assertEquals(20, RemnantShulkerApi.shotDelay(0));
        assertEquals(110, RemnantShulkerApi.shotDelay(9));
    }
}
