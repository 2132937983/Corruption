package com.miophas.corruption.core.demon.skill;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** The skills the elites brought: the pounce, the spit, the grab; and a screech that slows as well as stuns. */
class EliteSkillRulesTest {
    @Test void aPounceArcsFromWhereItSprangToWhereItAimedAndLastsUntilItCouldHaveLanded() {
        PounceRules pounce = new PounceRules(new SkillTiming(14, 15, 100), 4, 12, 2.2, 14, .6, 14, 1.1);
        assertEquals(14 + 14 + 1, pounce.timing().ticks(), "it lasts at least until it could have landed");
        assertTrue(pounce.inRange(4) && pounce.inRange(12) && !pounce.inRange(3.9) && !pounce.inRange(12.1));
        assertFalse(pounce.leaping(13));
        assertTrue(pounce.leaping(14) && pounce.leaping(27));
        assertFalse(pounce.leaping(28));
        assertEquals(0, pounce.along(0));
        assertEquals(1, pounce.along(14));
        assertEquals(1, pounce.along(30), "never past where it aimed");
        assertEquals(0, pounce.over(0), 1e-9);
        assertEquals(0, pounce.over(14), 1e-9, "down at the end");
        assertEquals(2.2, pounce.over(7), 1e-9, "at its height halfway");
        assertTrue(pounce.over(3) < pounce.over(6), "rising to its height");
    }

    @Test void aSpitLoosesItsShotsOneAfterAnotherSweptAcrossItsQuarry() {
        SpitRules spit = new SpitRules(new SkillTiming(12, 13, 70), 4, 24, 5, 2, 36, 1.3, 4, 0, false, 1);
        assertEquals(12 + 4 * 2 + 1, spit.timing().ticks(), "it lasts until its last shot is loosed");
        int[] shots = new int[spit.timing().ticks()];
        for (int elapsed = 0; elapsed < shots.length; elapsed++) shots[elapsed] = spit.shot(elapsed);
        assertArrayEquals(new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, 1, -1, 2, -1, 3, -1, 4}, shots);
        assertEquals(-18, spit.across(0), 1e-9);
        assertEquals(0, spit.across(2), 1e-9, "the middle one straight at it");
        assertEquals(18, spit.across(4), 1e-9);
        assertEquals(0, new SpitRules(new SkillTiming(1, 2, 3), 0, 1, 1, 1, 40, 1, 1, 0, false, 1).across(0), "a single shot goes straight");
        assertEquals(SpitRules.Look.BOLT, spit.look(), "bolts of the corruption, unless it says otherwise");
        assertFalse(spit.pool().any());
        assertEquals(0, spit.push());
    }

    @Test void aLobbedShotComesDownWhereItWasAimedAfterItsFlight() {
        SpitRules spit = new SpitRules(new SkillTiming(16, 17, 90), 5, 20, 3, 6, 30, .9, 5, 2.5, true, 22);
        double[] thrown = spit.lobbed(9, -1.5, -4);
        double x = 0, y = 0, z = 0, vy = thrown[1];
        // As a bolt flies: it moves as fast as it goes, then falls faster.
        for (int tick = 0; tick < spit.flight(); tick++) {
            x += thrown[0];
            y += vy;
            z += thrown[2];
            vy -= SpitRules.GRAVITY;
        }
        assertEquals(9, x, 1e-9);
        assertEquals(-1.5, y, 1e-9);
        assertEquals(-4, z, 1e-9);
        assertTrue(thrown[1] > 0, "thrown up and over");
    }

    @Test void aSpitMayLookOtherwisePushAndLeaveAPoolAsTheRulesSay() {
        SpitRules spit = new SpitRules(new SkillTiming(10, 11, 50), 3, 24, 3, 4, 8, 1.7, 6, 0, false, 1);
        SpitRules water = spit.looking(SpitRules.Look.WATER).pushing(.9);
        assertEquals(SpitRules.Look.WATER, water.look());
        assertEquals(.9, water.push());
        assertEquals(spit.timing(), water.timing(), "all else as it was");
        SpitRules acid = spit.looking(SpitRules.Look.GLOB).pooling(new SpitRules.Pool(1.8F, 70, 40));
        assertTrue(acid.pool().any());
        assertEquals(40, acid.pool().curseTicks());
        assertFalse(new SpitRules.Pool(1, 0, 40).any(), "a pool that lies no time is none");
        assertEquals(0, spit.pushing(-1).push(), "nothing pushes backwards");
    }

    @Test void aGrabReachesOutThroughItsWindUpThenDrawsItsQuarryIn() {
        GrabRules grab = new GrabRules(new SkillTiming(10, 60, 180), 12, 2, .4, .1, 6);
        assertTrue(grab.inRange(12) && !grab.inRange(12.1));
        assertEquals(0, grab.reaching(0));
        assertEquals(.5, grab.reaching(5), 1e-9);
        assertEquals(1, grab.reaching(15), "out to its quarry by the end of its wind-up");
        assertFalse(grab.holding(9));
        assertTrue(grab.holding(10) && grab.holding(59));
        assertFalse(grab.holding(60));
        assertTrue(grab.drawnIn(2) && !grab.drawnIn(2.1));
        assertEquals(3, new GrabRules(new SkillTiming(1, 2, 3), 3, 9, 1, 1, 1).near(), "it draws in no nearer than it reaches");
    }

    @Test void aScreechMayLeaveItsHearersSlowedAsWellAsStunned() {
        ScreechRules stuns = new ScreechRules(new SkillTiming(14, 26, 260), 0, 22, 18, 50);
        assertFalse(stuns.slows(), "a screech as it was: only stuns");
        assertEquals(0, stuns.slowTicks());
        ScreechRules slows = new ScreechRules(new SkillTiming(14, 26, 260), 0, 12, 8, 30, 100, 2);
        assertTrue(slows.slows());
        assertEquals(2, slows.slowLevel());
        assertEquals(1, new ScreechRules(new SkillTiming(1, 2, 3), 0, 1, 1, 1, 10, 0).slowLevel(), "Slowness I at the least");
    }
}
