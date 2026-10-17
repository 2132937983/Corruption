package com.miophas.corruption.core.demon.skill;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** The skill modules Decarabia brought: the star's volley and spin, the screech, the barrage, and dives one after another. */
class StarSkillRulesTest {
    private static final SpinVolleyRules VOLLEY = new SpinVolleyRules(new SkillTiming(16, 76, 140), 3, 26, 5, 4, 3, .9, 6, 13);

    @Test void aVolleyFiresFromEveryPointOnItsBeatAfterTheWindUp() {
        assertFalse(VOLLEY.firing(15), "not while it spins up");
        assertTrue(VOLLEY.firing(16));
        assertFalse(VOLLEY.firing(17));
        assertTrue(VOLLEY.firing(20));
        assertTrue(VOLLEY.firing(72));
        assertEquals(5 * 15, VOLLEY.bolts(), "five points, fifteen times");
    }

    @Test void itsPointsAreEvenlySpacedAndTurnInASpiral() {
        assertEquals(72, VOLLEY.pointAt(1, 0) - VOLLEY.pointAt(0, 0), 1e-9);
        assertEquals(VOLLEY.turned(16), VOLLEY.spin() * 16 / 2, 1e-9, "spinning up evenly from still");
        assertEquals(VOLLEY.turned(17) - VOLLEY.turned(16), VOLLEY.spin(), 1e-9, "then at full spin");
        double between = VOLLEY.turned(20) - VOLLEY.turned(16);
        assertNotEquals(0, between % 72, 1e-6, "from one beat to the next its points never come round onto where the last were: a spiral");
    }

    @Test void eachBoltLeavesFromItsPointsTipAndIsAimedNearItsQuarryNoTwoAlike() {
        assertArrayEquals(new double[]{0, 1}, SpinVolleyRules.tip(0), 1e-9, "the top point, straight up");
        assertArrayEquals(new double[]{1, 0}, SpinVolleyRules.tip(90), 1e-9);
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (int beat = 0; beat < 15; beat++)
            for (int point = 0; point < 5; point++) {
                double[] off = VOLLEY.scatter(beat, point);
                assertTrue(Math.hypot(off[0], off[1]) <= VOLLEY.spread() + 1e-9, "within its spread");
                assertTrue(seen.add(Math.round(off[0] * 1e6) + "," + Math.round(off[1] * 1e6)), "every bolt aimed somewhere of its own");
            }
        double[] none = new SpinVolleyRules(new SkillTiming(1, 2, 3), 0, 1, 5, 1, 0, 1, 1, 0).scatter(3, 2);
        assertArrayEquals(new double[]{0, 0}, none, 1e-9, "with no spread, straight at it");
    }

    @Test void aSpinStrikesOnItsBeatOnceTheWindUpIsOver() {
        SpinRules spin = new SpinRules(new SkillTiming(12, 52, 120), 0, 16, .55, 3, 5, 5, 1.1);
        assertFalse(spin.strikes(11));
        assertTrue(spin.strikes(12));
        assertFalse(spin.strikes(13));
        assertTrue(spin.strikes(17));
        assertEquals(8, spin.blows());
        assertTrue(spin.inRange(0) && spin.inRange(16) && !spin.inRange(17));
    }

    @Test void aScreechReachesAsFarAsItsRadius() {
        ScreechRules screech = new ScreechRules(new SkillTiming(14, 26, 260), 0, 22, 18, 50);
        assertTrue(screech.reaches(18));
        assertFalse(screech.reaches(18.01));
        assertEquals(1, new ScreechRules(new SkillTiming(1, 2, 3), 0, 1, 1, 0).stunTicks(), "a stun lasts a tick at the least");
    }

    @Test void aBarrageLoosesEveryBoltOnceAndLastsUntilTheLastIsGone() {
        BarrageRules barrage = new BarrageRules(new SkillTiming(24, 30, 420), 4, 40, 64, 4, 6, 20, 4, 1.2);
        assertTrue(barrage.timing().ticks() >= 24 + 16, "long enough to loose all 64, four a tick");
        int loosed = 0, next = 0;
        for (int tick = 0; tick < barrage.timing().ticks(); tick++) {
            int[] now = barrage.loosed(tick);
            if (now[1] > 0) assertEquals(next, now[0], "each bolt once, in order");
            next = now[0] + now[1];
            loosed += now[1];
            if (tick < 24) assertEquals(0, now[1], "none while it gathers itself");
        }
        assertEquals(64, loosed);
    }

    @Test void itsBoltsComeDownSpreadOverTheMarkAndExactlyWhereTheyWereThrown() {
        BarrageRules barrage = new BarrageRules(new SkillTiming(24, 40, 420), 4, 40, 64, 4, 6, 20, 4, 1.2);
        double nearest = Double.MAX_VALUE;
        for (int index = 0; index < 64; index++) {
            double[] at = barrage.landing(index, .7);
            assertTrue(Math.hypot(at[0], at[1]) <= 6 + 1e-9, "within the mark");
            for (int other = 0; other < index; other++) {
                double[] there = barrage.landing(other, .7);
                nearest = Math.min(nearest, Math.hypot(at[0] - there[0], at[1] - there[1]));
            }
        }
        assertTrue(nearest > .5, "spread out, not heaped: " + nearest);
        double[] velocity = barrage.launch(7, -9, -3);
        double x = 0, y = 0, z = 0, vy = velocity[1];
        for (int tick = 0; tick < barrage.flight(); tick++) {
            x += velocity[0]; y += vy; z += velocity[2];
            vy -= BarrageRules.GRAVITY;
        }
        assertArrayEquals(new double[]{7, -9, -3}, new double[]{x, y, z}, 1e-9, "flown tick by tick, it lands where it was aimed");
    }

    @Test void divesOneAfterAnotherFillTheirOwnTime() {
        DiveRules dive = new DiveRules(new SkillTiming(10, 40, 0), 4, 46, 3, 2, 3, 12, 1.2, 1.5, 6, .6);
        DiveChainRules dives = new DiveChainRules(dive, 3, 10, 30, 320);
        assertEquals(10, dives.timing().windup(), "the first climb is its wind-up");
        assertEquals(3 * (10 + 30), dives.timing().ticks());
        assertEquals(320, dives.timing().cooldown());
        assertTrue(dives.serves(20, 5));
        assertFalse(dives.serves(20, 1), "not on what is not well below it");
        assertEquals(1, new DiveChainRules(dive, 0, 0, 0, -1).dives(), "one dive at the least");
    }
}
