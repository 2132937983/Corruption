package com.miophas.corruption.core.corruption;

import static com.miophas.corruption.core.corruption.CorruptedRobeRules.NOTHING_KEPT;
import static com.miophas.corruption.core.corruption.CorruptedRobeRules.step;
import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.corruption.CorruptedRobeRules.Hold;
import com.miophas.corruption.core.corruption.CorruptedRobeRules.Tier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The corrupted robes as rules: what each grade does, what makes a set, and the hold on a wearer's corruption. */
class CorruptedRobeRulesTest {
    @Test void eachGradeHoldsCorruptionHigherAndTurnsAsideMoreOfTheCorruptionsHurt() {
        assertEquals(5, Tier.NOVICE.threshold());
        assertEquals(12, Tier.INTERMEDIATE.threshold());
        assertEquals(19, Tier.ADVANCED.threshold(), "one short of being taken whole");
        assertTrue(Tier.ADVANCED.threshold() < PlayerCorruptionRules.MAX);
        assertEquals(10, Tier.NOVICE.corruptionDamage(10), 1e-6, "the novice's turns none of it aside");
        assertEquals(5, Tier.INTERMEDIATE.corruptionDamage(10), 1e-6, "the intermediate's half");
        assertEquals(0, Tier.ADVANCED.corruptionDamage(10), 1e-6, "the advanced's all");
        for (Tier tier : Tier.values()) assertTrue(tier.hiddenFromRemnants(), "every grade veils its wearer from the corruption's creatures");
        assertFalse(Tier.NOVICE.hiddenFromBosses());
        assertFalse(Tier.INTERMEDIATE.hiddenFromBosses());
        assertTrue(Tier.ADVANCED.hiddenFromBosses(), "only the highest from its lords");
        assertFalse(Tier.NOVICE.hiddenFromElites(), "the corruption's elites see through a novice's robes");
        assertTrue(Tier.INTERMEDIATE.hiddenFromElites(), "but not through better ones");
        assertTrue(Tier.ADVANCED.hiddenFromElites());
    }

    @Test void aSetIsFourPiecesAndOnlyAsGoodAsItsWorst() {
        assertEquals(Optional.of(Tier.ADVANCED), CorruptedRobeRules.worn(List.of(Tier.ADVANCED, Tier.ADVANCED, Tier.ADVANCED, Tier.ADVANCED)));
        assertEquals(Optional.of(Tier.INTERMEDIATE), CorruptedRobeRules.worn(List.of(Tier.ADVANCED, Tier.INTERMEDIATE, Tier.ADVANCED, Tier.ADVANCED)),
                "mixed grades are the lowest of them");
        assertEquals(Optional.of(Tier.NOVICE), CorruptedRobeRules.worn(List.of(Tier.NOVICE, Tier.ADVANCED, Tier.INTERMEDIATE, Tier.ADVANCED)));
        assertEquals(Optional.empty(), CorruptedRobeRules.worn(Arrays.asList(Tier.ADVANCED, null, Tier.ADVANCED, Tier.ADVANCED)), "three pieces are no set");
        assertEquals(Optional.empty(), CorruptedRobeRules.worn(List.of(Tier.ADVANCED, Tier.ADVANCED, Tier.ADVANCED)));
    }

    @Test void puttingASetOnRaisesALowCorruptionToTheThresholdAndLeavesAHighOneAsItIs() {
        Hold raised = step(3, NOTHING_KEPT, NOTHING_KEPT, Tier.NOVICE);
        assertEquals(new Hold(5, 3, 5), raised, "below the threshold: raised to it, and what it was kept");
        assertTrue(raised.holding());
        assertEquals(new Hold(5, 5, 5), step(5, NOTHING_KEPT, NOTHING_KEPT, Tier.NOVICE), "at it: left as it is");
        assertEquals(new Hold(8, 8, 8), step(8, NOTHING_KEPT, NOTHING_KEPT, Tier.NOVICE), "above it: never lowered");
        assertEquals(new Hold(19, 0, 19), step(0, NOTHING_KEPT, NOTHING_KEPT, Tier.ADVANCED));
    }

    @Test void heldItStaysWhereItIsHeldAndOnlyAHigherThresholdRaisesIt() {
        assertEquals(new Hold(8, 8, 8), step(10, 8, 8, Tier.NOVICE), "raised by something: put back");
        assertEquals(new Hold(8, 8, 8), step(6, 8, 8, Tier.NOVICE), "lowered by something: put back");
        assertEquals(new Hold(12, 8, 12), step(8, 8, 8, Tier.INTERMEDIATE), "a grade with a higher threshold raises it to that");
        assertEquals(new Hold(12, 8, 12), step(12, 8, 12, Tier.NOVICE), "a lower grade never lowers it");
        assertEquals(new Hold(19, 8, 19), step(12, 8, 12, Tier.ADVANCED));
        assertEquals(new Hold(19, 8, 19), step(19, 8, 19, Tier.INTERMEDIATE));
        assertEquals(new Hold(7, 3, 7), step(7, 3, NOTHING_KEPT, Tier.NOVICE), "kept but not yet held (a save from before it was): held where it is");
    }

    @Test void takenOffItGivesBackWhatItWas() {
        Hold off = step(12, 8, 12, null);
        assertEquals(new Hold(8, NOTHING_KEPT, NOTHING_KEPT), off, "taken off: what it was, given back");
        assertFalse(off.holding());
        assertEquals(new Hold(3, NOTHING_KEPT, NOTHING_KEPT), step(5, 3, 5, null), "a corruption raised by the robe too");
        assertEquals(new Hold(3, NOTHING_KEPT, NOTHING_KEPT), step(3, NOTHING_KEPT, NOTHING_KEPT, null), "nothing worn, nothing held");
    }
}
