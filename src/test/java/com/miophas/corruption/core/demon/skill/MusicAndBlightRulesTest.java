package com.miophas.corruption.core.demon.skill;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.skill.UpheavalRules.Form;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * The skill rules that came with the mindweaver, the sower and Amdusias: the thread drawn out of a mind, the blight run
 * out over the ground, the roots sent up under a quarry, the ground heaved up or broken open, the fanfare of bolts, and
 * the rings of a crescendo. Plain arithmetic, checked as such.
 */
class MusicAndBlightRulesTest {
    @Test void aThreadPullsAtTheMindAgainAndAgainAndBreaksWhenItLosesItsQuarry() {
        PsychicStreamRules thread = new PsychicStreamRules(new SkillTiming(12, 92, 40), 0, 18, 22, 8, 1, 1, 2);
        assertFalse(thread.pulses(11), "nothing through the wind-up");
        assertTrue(thread.pulses(12), "the first pull as it is over");
        assertFalse(thread.pulses(13));
        assertTrue(thread.pulses(20), "then every interval");
        assertFalse(thread.pulses(92), "and none once the skill is over");
        assertEquals(10, thread.pulses(), "ten pulls in all");
        assertEquals(10, thread.most(), 1e-9);
        assertTrue(thread.holds(22) && !thread.holds(22.1), "it holds its quarry further off than it takes one");
        PsychicStreamRules odd = new PsychicStreamRules(new SkillTiming(2, 4, 0), 5, 1, 0, 0, -3, -1, 0);
        assertEquals(5, odd.maxRange(), 1e-9, "never a reach less than its least");
        assertEquals(5, odd.holdRange(), 1e-9, "nor a hold less than its reach");
        assertEquals(1, odd.interval());
        assertEquals(0, odd.damage(), 1e-9);
        assertEquals(1, odd.breaksAfter(), "a thread breaks at the first pull that finds nothing, at the soonest");
    }

    @Test void theBlightRunsOutFromTheCasterOverItsSpread() {
        BlightRules blight = new BlightRules(new SkillTiming(16, 20, 240), 10, 7, 80, 14, true, 8, .9, .35);
        assertEquals(31, blight.timing().ticks(), "it lasts until the blight has run its whole way");
        assertEquals(16, blight.reaching(0), "the ground at its feet turns as it strikes");
        assertEquals(23, blight.reaching(3.5), "half way out, half the spread later");
        assertEquals(30, blight.reaching(7));
        assertEquals(30, blight.reaching(12), "never later than the edge");
        assertTrue(blight.reaches(7) && !blight.reaches(7.01));
        assertTrue(blight.inRange(10) && !blight.inRange(10.5));
        int last = -1;
        for (double out = 0; out <= 7; out += .25) {
            assertTrue(blight.reaching(out) >= last, "the further, the later");
            last = blight.reaching(out);
        }
    }

    @Test void rootsHoldTwiceAsLongOnTheCorruptionsGround() {
        SnareRules snare = new SnareRules(new SkillTiming(14, 20, 120), 3, 16, 1.4, 6, 40, 3, .2);
        assertEquals(40, snare.holds(false));
        assertEquals(80, snare.holds(true));
        assertTrue(snare.catches(1.4) && !snare.catches(1.5));
        assertTrue(snare.inRange(3) && snare.inRange(16) && !snare.inRange(2.9));
    }

    @Test void theGroundRisesSinksOrIsSpearedAndOnlySpearedWithoutTheWorldsLeave() {
        UpheavalRules upheaval = new UpheavalRules(new SkillTiming(16, 22, 180), 3, 28, 1.5, 3, 3, 8, .95, 12, 1.15);
        Set<Form> seen = new HashSet<>();
        for (double roll = 0; roll < 1; roll += .05) {
            assertEquals(Form.SPIKES, upheaval.form(false, roll), "without leave to change the ground, only spikes");
            seen.add(upheaval.form(true, roll));
        }
        assertEquals(Set.of(Form.RISE, Form.SINK, Form.SPIKES), seen, "with it, all three");
        List<int[]> columns = upheaval.columns();
        assertEquals(9, columns.size(), "a circle a block and a half across from its middle takes the nine columns about it");
        assertTrue(columns.stream().anyMatch(column -> column[0] == 0 && column[1] == 0));
        assertTrue(upheaval.catches(1.9, 0) && !upheaval.catches(2.1, 0));
    }

    @Test void aFanfareLoosesAGreatManyBoltsInTwoSweepingArmsAndAFanStraightAtItsQuarry() {
        FanfareRules fanfare = new FanfareRules(new SkillTiming(30, 90, 340), 6, 40, 2, 45, 30, 10, 3, 16, 1.1, 7, 0);
        assertEquals(138, fanfare.bolts(), "two a tick for three seconds, and three every half second");
        assertFalse(fanfare.playing(29));
        assertTrue(fanfare.playing(30) && fanfare.playing(89) && !fanfare.playing(90));
        assertTrue(fanfare.volleys(30) && fanfare.volleys(40) && !fanfare.volleys(41));
        assertEquals(-8, fanfare.volleyAcross(0), 1e-9);
        assertEquals(8, fanfare.volleyAcross(2), 1e-9);
        double widest = 0;
        for (int tick = 30; tick < 90; tick++)
            for (int bolt = 0; bolt < 2; bolt++) {
                double[] way = fanfare.spray(tick, bolt);
                widest = Math.max(widest, Math.abs(way[0]));
                assertTrue(Math.abs(way[0]) <= 45 + 1e-9, "never wider than its sweep");
                assertTrue(Math.abs(way[1]) <= FanfareRules.RISE + 1e-9, "nor higher or lower than its sheet");
                if (bolt == 1) assertEquals(-fanfare.spray(tick, 0)[0], way[0], 1e-9, "the two arms sweep against each other");
            }
        assertTrue(widest > 44, "and it sweeps out as wide as it may: " + widest);
    }

    @Test void aCrescendosRingsGoOutOneAfterAnotherAndCatchWhatTheyPass() {
        CrescendoRules crescendo = new CrescendoRules(new SkillTiming(24, 0, 260), 0, 12, 3, 10, .9, 16, .8, 3, 9, 1.3, .45);
        assertEquals(18, crescendo.travel());
        assertEquals(63, crescendo.timing().ticks(), "it lasts until its last ring is gone");
        assertEquals(24, crescendo.starts(0));
        assertEquals(44, crescendo.starts(2));
        assertTrue(crescendo.front(0, 23) < 0, "no ring before the wind-up is over");
        assertEquals(0, crescendo.front(0, 24), 1e-9);
        assertEquals(9, crescendo.front(0, 34), 1e-9);
        assertTrue(crescendo.front(0, 24 + 18) < 0, "gone once past its radius");
        // Something 5 blocks off is caught by every ring, each on its own ticks.
        for (int ring = 0; ring < 3; ring++) {
            int caught = 0;
            for (int tick = 0; tick < crescendo.timing().ticks(); tick++) if (crescendo.catches(ring, tick, 5)) caught++;
            assertTrue(caught >= 1 && caught <= 2, "ring " + ring + " passes it in a tick or two: " + caught);
        }
        assertEquals(27, crescendo.most(), 1e-9);
    }
}
