package com.miophas.corruption.core.demon;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Andromalius's rules: how he chooses what to do and how to move, his volleys and where he aims them, his gaze's
 * reach, how he feels a rush and what he dodges, his ward's clock, and what enrages him.
 */
class AndromaliusRulesTest {
    private static AndromaliusRules.Situation at(double distance, boolean behind) {
        return new AndromaliusRules.Situation(distance, true, behind, false, false, false, true, true, true, true);
    }

    @Test void heLashesWhatIsBehindHimBitesWhatIsBeforeHimAndWorksOnWhatIsFurther() {
        assertEquals(AndromaliusRules.Attack.TAIL, AndromaliusRules.choose(at(3, true)), "close behind him: the tail");
        assertEquals(AndromaliusRules.Attack.BITE, AndromaliusRules.choose(at(3, false)), "close before him: the fangs");
        assertEquals(AndromaliusRules.Attack.GAZE, AndromaliusRules.choose(at(12, false)), "further: the gaze first");
        assertEquals(AndromaliusRules.Attack.VOLLEY, AndromaliusRules.choose(at(26, false)), "past the gaze's reach: fireballs");
        assertEquals(AndromaliusRules.Attack.NONE, AndromaliusRules.choose(at(31, false)), "past their range: nothing");
        assertEquals(AndromaliusRules.Attack.VOLLEY, AndromaliusRules.choose(new AndromaliusRules.Situation(12, true, false, true, false, false, true, true, true, true)),
                "something already blinded is not stared at again, but spat at");
        assertEquals(AndromaliusRules.Attack.NONE, AndromaliusRules.choose(new AndromaliusRules.Situation(12, false, false, false, false, false, true, true, true, true)),
                "nothing he cannot see is stared or spat at");
        assertEquals(AndromaliusRules.Attack.NONE, AndromaliusRules.choose(new AndromaliusRules.Situation(3, true, false, false, false, false, true, true, false, true)),
                "with his bite cooling, nothing for what is close before him: fireballs are not for what is on top of him");
        assertEquals(AndromaliusRules.Attack.NONE, AndromaliusRules.choose(new AndromaliusRules.Situation(3, true, true, false, false, false, true, true, true, false)),
                "nor his fangs for what is behind him");
    }

    @Test void heCirclesHuntsWhatIsImpairedAndDrawsBack() {
        assertEquals(AndromaliusRules.Intent.CIRCLE, AndromaliusRules.intent(at(11, false)));
        assertEquals(AndromaliusRules.Intent.BACK_OFF, AndromaliusRules.intent(at(4, false)), "too near");
        assertEquals(AndromaliusRules.Intent.CLOSE_IN, AndromaliusRules.intent(at(20, false)), "too far");
        assertEquals(AndromaliusRules.Intent.CLOSE_IN, AndromaliusRules.intent(new AndromaliusRules.Situation(11, false, false, false, false, false, true, true, true, true)), "out of sight");
        assertEquals(AndromaliusRules.Intent.HUNT, AndromaliusRules.intent(new AndromaliusRules.Situation(11, true, false, true, false, false, true, true, true, true)), "blinded or slowed: hunted");
        assertEquals(AndromaliusRules.Intent.HUNT, AndromaliusRules.intent(new AndromaliusRules.Situation(4, true, false, false, true, false, true, true, true, true)), "hunting, he does not draw back");
        assertEquals(AndromaliusRules.Intent.BACK_OFF, AndromaliusRules.intent(new AndromaliusRules.Situation(4, true, false, true, true, true, true, true, true, true)), "after a hunt he draws back, whatever else");
        assertTrue(AndromaliusRules.huntOver(AndromaliusRules.HUNT_TICKS, 0));
        assertTrue(AndromaliusRules.huntOver(10, AndromaliusRules.HUNT_BITES));
        assertFalse(AndromaliusRules.huntOver(10, AndromaliusRules.HUNT_BITES - 1));
    }

    @Test void behindHimIsAHundredDegreesRoundOrMore() {
        assertFalse(AndromaliusRules.behind(0));
        assertFalse(AndromaliusRules.behind(99));
        assertTrue(AndromaliusRules.behind(100));
        assertTrue(AndromaliusRules.behind(-150));
        assertTrue(AndromaliusRules.behind(180));
        assertTrue(AndromaliusRules.behind(260), "260 round is 100 the other way");
        assertFalse(AndromaliusRules.behind(350), "and 350 is just off his nose");
        assertEquals(-100, AndromaliusRules.wrap(260), 1e-9);
        assertEquals(180, AndromaliusRules.wrap(-180), 1e-9);
    }

    @Test void heLeadsHisAimButNotTooFarAndFansItEnraged() {
        double[] still = AndromaliusRules.lead(new double[]{0, 0, 0}, new double[]{12, 0, 0}, new double[]{0, 0, 0});
        assertArrayEquals(new double[]{12, 0, 0}, still, 1e-9, "something still is aimed at where it is");
        double[] running = AndromaliusRules.lead(new double[]{0, 0, 0}, new double[]{12, 0, 0}, new double[]{0, 0, .3});
        assertArrayEquals(new double[]{12, 0, 3}, running, 1e-9, "something running, where it will be when the fireball gets there");
        double[] far = AndromaliusRules.lead(new double[]{0, 0, 0}, new double[]{60, 0, 0}, new double[]{0, 0, 1});
        assertEquals(AndromaliusRules.MAX_LEAD, far[2], 1e-9, "but never more than a second ahead");
        assertTrue(AndromaliusRules.leads(0));
        assertFalse(AndromaliusRules.leads(1));
        assertEquals(-AndromaliusRules.ENRAGED_FAN / 2, AndromaliusRules.fanned(0, 10), 1e-9);
        assertEquals(AndromaliusRules.ENRAGED_FAN / 2, AndromaliusRules.fanned(9, 10), 1e-9);
        assertEquals(0, AndromaliusRules.fanned(0, 1), 1e-9);
    }

    @Test void heDodgesOnlyWhatWillStrikeHim() {
        double[] middle = {0, 1.5, 0};
        assertTrue(AndromaliusRules.threatens(new double[]{-10, 1.5, 0}, new double[]{2, 0, 0}, middle), "an arrow at him");
        assertFalse(AndromaliusRules.threatens(new double[]{-10, 1.5, 0}, new double[]{-2, 0, 0}, middle), "not one going away");
        assertFalse(AndromaliusRules.threatens(new double[]{-10, 1.5, 3}, new double[]{2, 0, 0}, middle), "nor one that will miss him");
        assertFalse(AndromaliusRules.threatens(new double[]{-60, 1.5, 0}, new double[]{2, 0, 0}, middle), "nor one too far off to matter yet");
        assertFalse(AndromaliusRules.threatens(new double[]{-1, 1.5, 0}, new double[]{0, 0, 0}, middle), "nor one lying still");
    }

    @Test void belowHalfHisHealthHeIsEnraged() {
        assertFalse(AndromaliusRules.enraged(.51F));
        assertTrue(AndromaliusRules.enraged(.5F));
        assertEquals(96, AndromaliusRules.cooldown(160, true), "his attacks come round in three fifths of the time");
        assertEquals(160, AndromaliusRules.cooldown(160, false));
    }
    @Test void aVolleyIsTenWhileHeIsWholeAndTwentyTwoAtTheLast() {
        assertEquals(10, AndromaliusRules.volleySize(1));
        assertEquals(16, AndromaliusRules.volleySize(.5F));
        assertEquals(22, AndromaliusRules.volleySize(0));
        assertEquals(22, AndromaliusRules.volleySize(1 / 160F), "at his last hit point");
        for (float share = 0; share <= 1; share += .01F) {
            int size = AndromaliusRules.volleySize(share);
            assertTrue(size >= 10 && size <= 22, "never more than twenty-two: " + size);
        }
        assertEquals(22, AndromaliusRules.volleySize(-3), "whatever he is told");
        assertEquals(10, AndromaliusRules.volleySize(7));
    }

    @Test void aVolleyLeavesTwoATickUntilAllHaveGone() {
        for (int total : new int[]{10, 15, 22}) {
            int fired = 0;
            for (int tick = 0; tick < AndromaliusRules.firingTicks(total); tick++) {
                int now = AndromaliusRules.firedOn(tick, total);
                assertTrue(now >= 1 && now <= 2, "one or two a tick");
                fired += now;
            }
            assertEquals(total, fired, "every one of " + total);
            assertEquals(0, AndromaliusRules.firedOn(AndromaliusRules.firingTicks(total), total), "and none after");
        }
        assertEquals(0, AndromaliusRules.firedOn(-1, 10), "none while his jaws are still opening");
        assertEquals(11, AndromaliusRules.firingTicks(22));
    }

    @Test void theGazeReachesNeitherTooNearNorTooFar() {
        assertFalse(AndromaliusRules.inGaze(7.9));
        assertTrue(AndromaliusRules.inGaze(8));
        assertTrue(AndromaliusRules.inGaze(16));
        assertTrue(AndromaliusRules.inGaze(24));
        assertFalse(AndromaliusRules.inGaze(24.1));
    }

    private static double closing(double perTick, double from) {
        AndromaliusRules.Approach approach = new AndromaliusRules.Approach();
        for (int tick = 0; tick < AndromaliusRules.APPROACH_SAMPLES; tick++) approach.see(from - tick * perTick);
        return approach.closing();
    }

    @Test void heFeelsARushButNotAWalk() {
        assertEquals(.28, closing(.28, 14), 1e-9);
        assertTrue(AndromaliusRules.rushed(closing(.28, 14), 12.6), "a sprint is a rush");
        assertFalse(AndromaliusRules.rushed(closing(.216, 14), 12.9), "a walk is not");
        assertFalse(AndromaliusRules.rushed(closing(.5, 40), 37.5), "nor is anything still far off, however fast");
        assertTrue(closing(-.3, 10) < 0, "drawing away is closing in by less than nothing");
        AndromaliusRules.Approach approach = new AndromaliusRules.Approach();
        for (int tick = 0; tick < AndromaliusRules.APPROACH_SAMPLES - 1; tick++) approach.see(20 - tick);
        assertEquals(0, approach.closing(), "nothing is known until a full run of ticks is seen");
        approach.see(15);
        assertEquals(1, approach.closing(), 1e-9);
        approach.forget();
        assertEquals(0, approach.closing(), "and nothing once it is forgotten");
        // Only the last run of ticks counts: a sprint long ago is forgotten as a walk goes on.
        for (int tick = 0; tick < 20; tick++) approach.see(30 - (tick < 10 ? tick * .5 : 5 + (tick - 10) * .1));
        assertEquals(.1, approach.closing(), 1e-9);
    }

    @Test void theWardStandsFourSecondsThenTakesFourToComeBack() {
        AndromaliusRules.Shield shield = new AndromaliusRules.Shield();
        assertTrue(shield.ready());
        assertTrue(shield.raise());
        assertFalse(shield.raise(), "not while it stands");
        int up = 0;
        while (shield.raised()) {
            boolean fell = shield.tick();
            up++;
            assertEquals(!shield.raised(), fell, "it says when it falls");
        }
        assertEquals(AndromaliusRules.SHIELD_TICKS, up);
        assertFalse(shield.ready());
        assertFalse(shield.raise(), "nor while it comes back");
        int cooling = 0;
        while (!shield.ready()) { shield.tick(); cooling++; }
        assertEquals(AndromaliusRules.SHIELD_COOLDOWN, cooling);
        assertTrue(shield.raise());
        while (shield.raised()) shield.tick(AndromaliusRules.cooldown(AndromaliusRules.SHIELD_COOLDOWN, true));
        assertEquals(AndromaliusRules.cooldown(AndromaliusRules.SHIELD_COOLDOWN, true), shield.cooling(), "enraged, it comes back sooner");
        shield.set(9999, -5);
        assertEquals(AndromaliusRules.SHIELD_TICKS, shield.up(), "loaded within its bounds");
        assertEquals(0, shield.cooling());
    }

    @Test void theRulesArePlainJava() throws Exception {
        Path folder = null;
        for (Path at = Path.of("").toAbsolutePath(); at != null && folder == null; at = at.getParent())
            if (Files.isDirectory(at.resolve("src/main/java/com/miophas/corruption/core/demon"))) folder = at.resolve("src/main/java/com/miophas/corruption/core/demon");
        assertNotNull(folder);
        try (Stream<Path> files = Files.list(folder)) {
            List<String> imports = files.flatMap(file -> {
                try { return Files.readAllLines(file).stream().filter(line -> line.startsWith("import ") && !line.startsWith("import java.")); }
                catch (Exception failure) { throw new RuntimeException(failure); }
            }).toList();
            assertEquals(List.of(), imports, "core/demon imports nothing but java.*");
        }
    }
}
