package com.miophas.corruption.core.plant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** A malice vine's brood and its defence, as rules. */
class MaliceVineRulesTest {
    @Test void itsShockwaveReachesFurtherTheBiggerItHasGrownAndGoesOffWellWithinIt() {
        assertEquals(3, MaliceVineRules.shockRadius(0));
        assertEquals(6, MaliceVineRules.shockRadius(4));
        assertEquals(6, MaliceVineRules.shockRadius(9), "no further than its last stage");
        for (int stage = 0; stage < 5; stage++) {
            assertTrue(MaliceVineRules.shockRadius(stage) > MaliceVineRules.shockRadius(stage - 1) || stage == 0);
            assertTrue(MaliceVineRules.shockTrigger(stage) < MaliceVineRules.shockRadius(stage), "what sets it off is caught in it");
        }
    }

    @Test void itBroodsWhenItHasWaitedLongEnoughAndThereIsRoom() {
        assertEquals(40, MaliceVineRules.waited(20, 20));
        assertEquals(Integer.MAX_VALUE, MaliceVineRules.waited(Integer.MAX_VALUE, 20), "waiting never wraps round");
        assertFalse(MaliceVineRules.due(3599, 3600));
        assertTrue(MaliceVineRules.due(3600, 3600));
        assertFalse(MaliceVineRules.due(1_000_000, 0), "an interval of 0 turns its brood off");
        assertTrue(MaliceVineRules.room(1, 2));
        assertFalse(MaliceVineRules.room(2, 2));
        assertFalse(MaliceVineRules.room(0, 0), "nor with no room at all");
    }

    @Test void itBringsForthOneOfTheSeasOnlyWhereThereIsWater() {
        for (double roll = 0; roll < 1; roll += .01) assertTrue(MaliceVineRules.pick(2, 1, false, roll) < 2, "away from water, only the land's and the air's");
        boolean sea = false;
        int[] seen = new int[3];
        for (double roll = 0; roll < 1; roll += .01) {
            int picked = MaliceVineRules.pick(2, 1, true, roll);
            seen[picked]++;
            sea |= picked == 2;
        }
        assertTrue(sea, "by water, the sea's too");
        for (int count : seen) assertTrue(count > 25, "each about as likely as the others");
        assertEquals(-1, MaliceVineRules.pick(0, 1, false, .5), "nothing it could bring forth here");
        assertEquals(0, MaliceVineRules.pick(0, 1, true, .99));
        assertEquals(2, MaliceVineRules.pick(2, 1, true, 1), "a roll of one is the last");
    }
}
