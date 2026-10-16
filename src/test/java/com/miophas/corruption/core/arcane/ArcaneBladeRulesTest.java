package com.miophas.corruption.core.arcane;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** The corruption's blades as rules: how heavy each is, what the arcane's mark and the curse do, and the curse's measure. */
class ArcaneBladeRulesTest {
    @Test void theEnergySwordStrikesSixteenAndTheArcaneSwordTwiceThat() {
        assertEquals(16, ArcaneBladeRules.ENERGY_SWORD_DAMAGE);
        assertEquals(32, ArcaneBladeRules.ARCANE_SWORD_DAMAGE);
        assertTrue(ArcaneBladeRules.ARCANE_SWORD_DURABILITY > ArcaneBladeRules.ENERGY_SWORD_DURABILITY);
    }

    @Test void theMarkGoesWithABlowStruckByHand() {
        assertTrue(ArcaneBladeRules.arcane(true, true));
        assertFalse(ArcaneBladeRules.arcane(true, false), "not a shot, nor anything else not struck with it");
        assertFalse(ArcaneBladeRules.arcane(false, true), "nor a blow with an unmarked blade");
    }

    @Test void theCursedBladeIsTurnedAsideByTheCorruptionsLordsAndCursesWhatIsNotOfIt() {
        assertTrue(ArcaneBladeRules.refused(true, true), "its own lords turn the curse aside");
        assertFalse(ArcaneBladeRules.refused(true, false));
        assertFalse(ArcaneBladeRules.refused(false, true), "a clean blade reaches them");
        assertTrue(ArcaneBladeRules.curses(true, false));
        assertFalse(ArcaneBladeRules.curses(true, true), "the curse does not touch the corruption's own");
        assertFalse(ArcaneBladeRules.curses(false, false), "and a clean blade lays none");
    }

    @Test void itsCurseBitesOnceABlow() {
        assertEquals(0, ArcaneBladeRules.CURSE_LEVEL, "the curse at its weakest");
        assertTrue(ArcaneBladeRules.CURSE_TICKS > 20 && ArcaneBladeRules.CURSE_TICKS < 40, "long enough to bite once, a second after its start; not twice");
    }
}
