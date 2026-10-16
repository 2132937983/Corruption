package com.miophas.corruption.core.demon.skill;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/** The rules every boss's skills share: plain arithmetic, checked as such. */
class SkillRulesTest {
    @Test void aSkillRunsItsWindupThenTakesEffectOnceAndEnds() {
        SkillTiming timing = new SkillTiming(5, 8, 40);
        assertTrue(timing.windingUp(4));
        assertFalse(timing.strikes(4));
        assertTrue(timing.strikes(5), "it takes effect on the first tick after its wind-up");
        assertFalse(timing.over(7));
        assertTrue(timing.over(8));
        assertEquals(.5F, timing.wound(2.5F), 1e-6);
        SkillTiming odd = new SkillTiming(10, 3, -5);
        assertEquals(11, odd.ticks(), "a skill lasts at least past its wind-up");
        assertEquals(0, odd.cooldown());
    }

    @Test void theBookRunsOneSkillAtATimeAndCountsCooldownsDown() {
        SkillBook book = new SkillBook(3);
        assertTrue(book.begin(1));
        assertFalse(book.begin(0), "only one at a time");
        assertEquals(0, book.advance());
        assertEquals(1, book.advance());
        assertEquals(2, book.elapsed());
        book.finish(3);
        assertFalse(book.busy());
        assertFalse(book.ready(1), "come round only after its cooldown");
        book.cool(); book.cool(); book.cool();
        assertTrue(book.ready(1));
        book.holdOff(2, 7);
        assertEquals(7, book.cooling(2));
        int[] saved = book.save();
        SkillBook loaded = new SkillBook(2);
        loaded.load(saved);
        assertEquals(0, loaded.cooling(0));
        assertEquals(0, loaded.cooling(1));
        assertEquals(-1, new SkillBook(1).advance(), "nothing running, nothing to carry out");
    }

    @Test void aChargeBreaksWhatIsSofterThanObsidianAndNothingElse() {
        ChargeRules charge = new ChargeRules(new SkillTiming(10, 30, 100), 5, 20, 1.5, 12, 1, ChargeRules.OBSIDIAN);
        assertTrue(charge.breaks(1.5F), "stone");
        assertTrue(charge.breaks(5), "iron");
        assertTrue(charge.breaks(0), "grass and flowers");
        assertFalse(charge.breaks(50), "obsidian holds");
        assertFalse(charge.breaks(-1), "nor what cannot be broken at all");
        assertEquals(30, charge.reach(), 1e-9, "twenty ticks at a block and a half");
        assertFalse(charge.rushing(9));
        assertTrue(charge.rushing(10));
    }

    @Test void remnantsAreCalledUpToTheMostAndRoundTheBoss() {
        SummonRules summon = new SummonRules(new SkillTiming(5, 10, 100), 3, 6, 4, List.of("a", "b"));
        assertEquals(3, summon.toCall(0));
        assertEquals(2, summon.toCall(4));
        assertEquals(0, summon.toCall(6));
        assertFalse(summon.wanted(9));
        for (int one = 0; one < 3; one++) {
            double[] place = summon.place(one, 3, .3);
            assertEquals(4, Math.hypot(place[0], place[1]), 1e-9, "on the ring");
        }
        assertEquals("b", summon.look(1, 0));
        assertEquals("a", summon.look(1, 1));
    }

    @Test void theBossBlinksWhenItIsNotSafeAndPrefersPlacesAwayFromThreats() {
        BlinkRules blink = new BlinkRules(new SkillTiming(5, 8, 100), 10, 16, 3, 6, 12, 4, 3, 40, .1F, 4);
        assertFalse(blink.unsafe(new BlinkRules.Danger(false, 0, 0, false, 12)));
        assertTrue(blink.unsafe(new BlinkRules.Danger(true, 0, 0, false, 12)), "in water, fire or a wall");
        assertTrue(blink.unsafe(new BlinkRules.Danger(false, .1F, 0, false, 12)), "hurt hard of late");
        assertTrue(blink.unsafe(new BlinkRules.Danger(false, 0, 3, false, 12)), "crowded");
        assertTrue(blink.unsafe(new BlinkRules.Danger(false, 0, 0, true, 12)), "a player close");
        assertTrue(blink.unsafe(new BlinkRules.Danger(false, 0, 0, false, 3)), "its quarry too near");
        assertTrue(blink.score(13, 20) > blink.score(13, 2), "further from what crowded it is better");
        assertTrue(blink.score(13, 10) > blink.score(16, 10), "nearer the middle of its distances is better");
        for (int tried = 0; tried < blink.tries(); tried++) {
            double[] trial = blink.trial(tried, 0, .4);
            assertTrue(trial[1] >= 10 && trial[1] <= 16 && trial[2] >= 3 && trial[2] <= 6, "within its distances and heights");
        }
        BlinkRules.Wounds wounds = new BlinkRules.Wounds(4);
        assertEquals(0, wounds.lost());
        wounds.see(1); wounds.see(.95F); wounds.see(.8F);
        assertEquals(.2F, wounds.lost(), 1e-6);
        wounds.see(.8F); wounds.see(.8F);
        assertEquals(.15F, wounds.lost(), 1e-6, "only so far back");
        wounds.see(.9F);
        assertTrue(wounds.lost() < .1F, "health regained counts against what was lost");
    }

    @Test void boltsFallOneAfterAnotherOnTheirMarks() {
        StrikeRules storm = new StrikeRules(new SkillTiming(20, 20, 100), 2, 30, 5, 6, 3, 2, 10);
        assertEquals(20 + 4 * 3 + 1, storm.timing().ticks(), "the skill lasts until its last bolt has fallen");
        assertEquals(-1, storm.falling(19));
        assertEquals(0, storm.falling(20));
        assertEquals(-1, storm.falling(21));
        assertEquals(1, storm.falling(23));
        assertEquals(4, storm.falling(32));
        assertEquals(-1, storm.falling(35));
        assertArrayEquals(new double[]{0, 0}, storm.mark(0, 1), "the first right where its quarry stood");
        for (int bolt = 1; bolt < 5; bolt++) assertTrue(Math.hypot(storm.mark(bolt, 1)[0], storm.mark(bolt, 1)[1]) <= 6 + 1e-9, "the rest within the scatter");
        StrikeRules one = new StrikeRules(new SkillTiming(10, 12, 50), 2, 30, 1, 0, 0, 2, 10);
        assertEquals(0, one.falling(10));
    }

    @Test void aBeamFollowsNoFasterThanItTurnsAndPulsesInTime() {
        BeamRules beam = new BeamRules(new SkillTiming(10, 30, 100), 3, 30, 40, 1.5, 4, 5, 2);
        assertEquals(12, beam.follow(10, 90), 1e-6, "no faster than it may turn");
        assertEquals(-178, BeamRules.wrap(beam.follow(180, -170)), 1e-6, "the short way round");
        assertEquals(10.5F, beam.follow(10, 10.5F), 1e-6);
        assertFalse(beam.pulses(9));
        assertTrue(beam.pulses(10));
        assertFalse(beam.pulses(11));
        assertTrue(beam.pulses(14));
        assertFalse(beam.firing(30));
    }

    @Test void aHeldMindIsDrawnRoundAndItsWalkWeaves() {
        MindControlRules hold = new MindControlRules(new SkillTiming(10, 10, 100), 20, 50, true, .4F);
        assertEquals(11, hold.timing().ticks(), "the skill runs to the taking, and the hold outlasts it");
        assertEquals(50, hold.holds());
        assertEquals(40, MindControlRules.pull(0, 100, .4F), 1e-4);
        assertEquals(178, MindControlRules.pull(170, -170, .4F), 1e-3, "the short way round");
        for (int tick = 0; tick < 100; tick++) assertTrue(Math.abs(MindControlRules.weave(tick)) <= .6F + 1e-6);
    }

    @Test void theGreaterGazeOpensMoreEyesAndWithers() {
        GazeRules plain = new GazeRules(new SkillTiming(20, 21, 100), 3, 20, 100, 0, 60, 0, 0, 1);
        GazeRules greater = new GazeRules(new SkillTiming(30, 31, 200), 3, 20, 200, 1, 120, 100, 1, 5);
        assertEquals(0, plain.ringed(), "the one eye opens over the boss");
        assertEquals(4, greater.ringed(), "the rest round its quarry");
        assertFalse(plain.withers());
        assertTrue(greater.withers());
        assertEquals(Math.PI / 2, greater.ringAngle(1, 0), 1e-9);
        assertFalse(plain.inRange(2.9));
        assertTrue(plain.inRange(3));
    }

    @Test void aBlowCatchesWhatIsCloseBeforeTheBossAndNothingBehind() {
        MeleeRules hooves = new MeleeRules(new SkillTiming(12, 20, 30), 4.6, 140, 16, 1.4, .5);
        assertTrue(hooves.inRange(4.6));
        assertFalse(hooves.inRange(4.7));
        assertTrue(hooves.catches(3, 0));
        assertTrue(hooves.catches(3, 69), "within half its arc either way");
        assertTrue(hooves.catches(3, -69));
        assertFalse(hooves.catches(3, 71));
        assertFalse(hooves.catches(3, 180), "nothing behind");
        assertTrue(hooves.catches(3, 360), "however far round it is counted");
        assertFalse(hooves.catches(5, 0), "nor beyond its reach");
        assertEquals(360, new MeleeRules(new SkillTiming(1, 2, 3), 1, 720, 1, 0, 0).arc(), "no wider than all the way round");
    }

    @Test void blinksZigzagThroughTheQuarryOneAfterAnother() {
        PhaseDashRules blinks = new PhaseDashRules(new SkillTiming(10, 0, 160), 3, 18, 4, 5, 5, 35, 1.6, 12, 120, 1, 2);
        assertEquals(2, blinks.hitsEach());
        assertEquals(1, new PhaseDashRules(new SkillTiming(10, 0, 160), 3, 18, 4, 5, 5, 35, 1.6, 12, 120, 1, 0).hitsEach(), "a blink that hurts at all hurts once at the least");
        assertEquals(10 + 3 * 5 + 1, blinks.timing().ticks(), "it runs until its last blink");
        assertEquals(-1, blinks.hopping(9));
        assertEquals(0, blinks.hopping(10));
        assertEquals(-1, blinks.hopping(11));
        assertEquals(1, blinks.hopping(15));
        assertEquals(3, blinks.hopping(25));
        assertEquals(-1, blinks.hopping(30), "no more than its blinks");
        assertTrue(blinks.inRange(3) && blinks.inRange(18));
        assertFalse(blinks.inRange(2.9) || blinks.inRange(18.1));
        // From eight blocks west of its quarry, the first blink goes through it eastward, turned one way...
        double[] first = blinks.landing(0, -8, 0);
        assertEquals(5, Math.hypot(first[0], first[1]), 1e-9, "its pass beyond the quarry");
        assertEquals(35, Math.toDegrees(Math.atan2(first[1], first[0])), 1e-9);
        // ...and the next back through it from there, turned the other: so it lands due west.
        double[] second = blinks.landing(1, first[0], first[1]);
        assertEquals(-5, second[0], 1e-9);
        assertEquals(0, second[1], 1e-9);
        assertEquals(5, Math.hypot(blinks.landing(0, 0, 0)[0], blinks.landing(0, 0, 0)[1]), 1e-9, "setting off from the quarry itself, it still goes");
        assertEquals(0, PhaseDashRules.offLine(0, 0, 0, 10, 0, 0, 5, 0, 0), 1e-9);
        assertEquals(2, PhaseDashRules.offLine(0, 0, 0, 10, 0, 0, 5, 2, 0), 1e-9, "square to the line");
        assertEquals(3, PhaseDashRules.offLine(0, 0, 0, 10, 0, 0, 13, 0, 0), 1e-9, "past its end, from the end");
        assertEquals(5, PhaseDashRules.offLine(1, 1, 1, 1, 1, 1, 4, 5, 1), 1e-9, "a line of no length is a point");
    }

    @Test void spikesComeUpOnlyInTheStretchBeforeTheBoss() {
        SpikeFieldRules spikes = new SpikeFieldRules(new SkillTiming(20, 26, 200), 2, 16, 1.5, 16, 3, 11, 2.5, 14, 120, 2);
        assertEquals(0, spikes.widthAt(1), "nothing short of its near end");
        assertEquals(3, spikes.widthAt(1.5), 1e-9);
        assertEquals(7, spikes.widthAt(8.75), 1e-9, "widening evenly");
        assertEquals(11, spikes.widthAt(16), 1e-9);
        assertTrue(spikes.covers(8, 3));
        assertFalse(spikes.covers(2, 3), "narrow near the boss");
        assertFalse(spikes.covers(-3, 0), "nothing behind it");
        assertFalse(spikes.covers(17, 0), "nor past its far end");
        List<double[]> clusters = spikes.clusters();
        assertTrue(clusters.size() > 10, "a field of them");
        for (double[] cluster : clusters) assertTrue(spikes.covers(cluster[0], cluster[1]), "every cluster in the field");
        assertTrue(clusters.stream().anyMatch(cluster -> cluster[0] > 14), "out to its far end");
    }

    @Test void aDiveTearsUpWhatCanBeBrokenAndFlingsItOutAndUp() {
        DiveRules dive = new DiveRules(new SkillTiming(24, 64, 220), 6, 48, 5, 1.6, 4, 24, 1.6, 3.5, 48, .9);
        assertTrue(dive.serves(20, 8));
        assertFalse(dive.serves(20, 3), "not on what is too little below it");
        assertFalse(dive.serves(4, 8), "nor too near");
        assertFalse(dive.serves(60, 8), "nor too far");
        assertFalse(dive.diving(23));
        assertTrue(dive.diving(24));
        assertFalse(dive.diving(64));
        assertTrue(DiveRules.tears(50), "obsidian is torn up");
        assertTrue(DiveRules.tears(0));
        assertFalse(DiveRules.tears(-1), "bedrock is not");
        double[] near = dive.fling(1, 0, 0), far = dive.fling(3, 0, 0), under = dive.fling(0, -1, 0);
        assertTrue(near[1] > 0 && far[1] > 0, "always up");
        assertTrue(near[0] > 0 && far[0] > 0, "out from where it struck");
        assertTrue(near[1] > far[1], "the nearer the harder");
        assertEquals(0, under[0], 1e-9);
        assertEquals(0, under[2], 1e-9);
        assertTrue(under[1] > 0, "what is right under it goes straight up");
        for (double[] fling : new double[][]{near, far, under}) assertTrue(Math.hypot(fling[0], fling[2]) <= .9 + 1e-9 && fling[1] <= .9 + 1e-9, "no faster than its most");
    }

    @Test void moreBeamsTheLowerTheBossIsEachOutOfACircleOfItsOwn() {
        BeamVolleyRules volley = new BeamVolleyRules(new BeamRules(new SkillTiming(30, 90, 320), 4, 40, 44, 1.4, 4, 5, 2.4F), 4, 2.6);
        assertEquals(1, volley.byHealth(1), "one at full health");
        assertEquals(1, volley.byHealth(.8F));
        assertEquals(2, volley.byHealth(.75F));
        assertEquals(2, volley.byHealth(.6F));
        assertEquals(3, volley.byHealth(.5F));
        assertEquals(4, volley.byHealth(.25F));
        assertEquals(4, volley.byHealth(0), "never more than its most");
        assertEquals(1, volley.byHealth(2), "nor fewer than one");
        assertArrayEquals(new double[]{0, 0}, volley.circle(0, 1), "one leaves from before the boss itself");
        assertEquals(0, volley.circle(0, 3)[0], 1e-9);
        assertEquals(2.6, volley.circle(0, 3)[1], 1e-9, "the first of more straight above");
        for (int count = 2; count <= 4; count++)
            for (int index = 0; index < count; index++) assertEquals(2.6, Math.hypot(volley.circle(index, count)[0], volley.circle(index, count)[1]), 1e-9, "on the ring");
        double[] one = volley.circle(0, 4), next = volley.circle(1, 4);
        assertEquals(Math.sqrt(2) * 2.6, Math.hypot(one[0] - next[0], one[1] - next[1]), 1e-9, "set evenly round it");
    }
}
