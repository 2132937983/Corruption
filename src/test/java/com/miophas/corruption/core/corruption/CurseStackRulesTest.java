package com.miophas.corruption.core.corruption;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.elite.BlighthoundRules;
import org.junit.jupiter.api.Test;

/** The curse laid on deeper and deeper (the blighthound's bites): longer each time, then harder, never weaker, and in step with its bites. */
class CurseStackRulesTest {
    private static final CurseStackRules BITES = BlighthoundRules.CURSE;

    @Test void aFirstBiteCursesItForAWhileAtItsWeakest() {
        assertArrayEquals(new int[]{50, 0}, BITES.next(false, 0, 0));
        assertNotEquals(0, BITES.first() % 20, "a first curse of whole seconds would bite at once");
    }

    @Test void everyBiteAfterRunsItLongerAndAtLastHarder() {
        int ticks = 0, amplifier = 0;
        boolean cursed = false;
        int bites = 0;
        // A bite every 12 ticks, the curse running down between them.
        while (amplifier == 0 && bites < 20) {
            int[] next = BITES.next(cursed, ticks, amplifier);
            ticks = next[0] - BlighthoundRules.BITE_EVERY;
            amplifier = next[1];
            cursed = true;
            bites++;
        }
        assertEquals(1, amplifier, "Curse II at last");
        assertTrue(bites >= 4 && bites <= 9, "after some seconds of biting, not at once: " + bites + " bites");
        assertArrayEquals(new int[]{120, 1}, BITES.next(true, 115, 1), "never past the most");
        assertArrayEquals(new int[]{200, 2}, BITES.next(true, 200, 2), "a longer, stronger curse from elsewhere is left as it is");
        assertArrayEquals(new int[]{CorruptionCurseRules.ENDLESS, 0}, BITES.next(true, CorruptionCurseRules.ENDLESS, 0), "and one that never ends");
    }

    @Test void layingItOnAgainKeepsItsBitesInStep() {
        // It bites when what it has left is a whole number of seconds; what is added is a whole number of seconds.
        for (int remaining = 1; remaining < 120; remaining++) {
            int[] next = BITES.next(true, remaining, 0);
            if (next[0] < BITES.most()) assertEquals(remaining % 20, next[0] % 20, "in step from " + remaining);
        }
        assertEquals(40, new CurseStackRules(50, 30, 120, 50, 1).add(), "a whole number of seconds, the nearest");
        assertEquals(20, new CurseStackRules(50, 1, 120, 50, 1).add(), "a second at the least");
    }
}
