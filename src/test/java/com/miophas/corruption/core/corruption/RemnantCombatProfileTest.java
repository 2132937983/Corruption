package com.miophas.corruption.core.corruption;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RemnantCombatProfileTest {
    @Test void retainsSmallMobAndBossHealthAndModifiedDamage() {
        assertEquals(new RemnantCombatProfile(4, 1), RemnantCombatProfile.capture(4, 1));
        assertEquals(new RemnantCombatProfile(500, 23.5), RemnantCombatProfile.capture(500, 23.5));
        assertEquals(0, RemnantCombatProfile.capture(20, 0).attackDamage());
    }
    @Test void missingAndInvalidAttributesHaveSafeDefaults() {
        assertEquals(new RemnantCombatProfile(20, 3), RemnantCombatProfile.capture(Double.NaN, Double.NaN));
        assertEquals(new RemnantCombatProfile(20, 3), RemnantCombatProfile.capture(0, -1));
        assertEquals(new RemnantCombatProfile(20, 3), RemnantCombatProfile.capture(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }
}
