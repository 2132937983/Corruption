package com.miophas.corruption.core.demon.skill;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.SolomonRules;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Beams out of magic circles in the sky, without a game: one beam or a star of five, six or eight; a warning a third of the
 * whole, then pulses; a single beam on its mark; a star's beams (and circles) at its points, room between them to slip
 * through, then turning and closing in to meet on the mark, catching whoever stayed on it; every one of Solomon's as wide as
 * a chunk, and every one of them left in time by whoever runs.
 */
class SkyBeamRulesTest {
    private static double degrees(double[] point) { return Math.toDegrees(Math.atan2(point[1], point[0])); }

    /** How far apart two angles are, in degrees, 0 to 180. */
    private static double apart(double a, double b) {
        double difference = Math.abs(((a - b) % 360 + 540) % 360 - 180);
        return difference;
    }

    /**
     * Whether someone who sets off from the mark as it begins, {@code late} ticks late, and runs straight out at
     * {@code speed} blocks a tick some way, is never caught by a pulse of this star.
     */
    private static boolean outrun(SkyBeamRules rules, double speed, int late) {
        int warning = rules.timing().windup();
        for (int degree = 0; degree < 360; degree++) {
            double way = Math.toRadians(degree);
            boolean caught = false;
            for (int since = 0; since < rules.pouring() && !caught; since += rules.every()) {
                double ran = Math.max(0, warning + since - late) * speed;
                for (double[] point : rules.points(0, since))
                    caught |= rules.catches(Math.hypot(Math.cos(way) * ran - point[0], Math.sin(way) * ran - point[1]), .3);
            }
            if (!caught) return true;
        }
        return false;
    }

    @Test void oneBeamOrAStarOfFiveSixOrEight() {
        for (int beams : new int[]{1, 5, 6, 8}) assertEquals(beams, new SkyBeamRules(new SkillTiming(10, 30, 100), 0, 80, beams, 20, 1.4, 5, 5).beams());
        for (int beams : new int[]{0, 2, 3, 4, 7, 9})
            assertThrows(IllegalArgumentException.class, () -> new SkyBeamRules(new SkillTiming(10, 30, 100), 0, 80, beams, 20, 1.4, 5, 5), beams + " beams");
        assertEquals(0, new SkyBeamRules(new SkillTiming(10, 30, 100), 0, 80, 1, 20, 2, 5, 5).spread(), "a single beam has no spread");
        for (int beams : new int[]{5, 6, 8})
            for (double width : new double[]{0, 1.4, 9}) {
                double spread = SkyBeamRules.spread(beams, width);
                assertEquals(width + 1.6, 2 * spread * Math.sin(Math.PI / beams) - 2 * width, 1e-9, beams + " points " + width + " round: between two beams, as much again as one reaches");
                assertTrue(2 * spread * Math.sin(Math.PI / beams) + 1e-9 >= 2 * SkyBeamRules.circle(width), "their circles do not overlap");
                assertEquals(width, SkyBeamRules.width(beams, spread), 1e-9, "its beams' reach told again from its spread, as its effect is");
            }
        assertEquals(7, SkyBeamRules.width(1, 7), "a single beam's, its own");
    }

    @Test void itWarnsForAThirdOfItsWholeThenPulses() {
        for (SkyBeamRules rules : List.of(SolomonRules.SKY_BEAM, SolomonRules.PENTAGRAM, SolomonRules.HEXAGRAM, SolomonRules.OCTAGRAM)) {
            int warning = rules.timing().windup(), whole = rules.timing().ticks();
            assertEquals(warning * 3, whole, "the whole three times the warning");
            assertEquals(warning, whole * SkyBeamRules.WARNING, 1e-9, "the share its effect draws the warning for");
            assertEquals(whole - warning, rules.pouring());
            assertFalse(rules.pours(warning - 1));
            assertTrue(rules.pulses(warning), "the first pulse as the beams strike");
            assertFalse(rules.pulses(warning + 1));
            assertTrue(rules.pulses(warning + rules.every()));
            assertFalse(rules.pours(whole), "done");
        }
    }

    @Test void aSingleBeamStrikesItsMarkAndAStarsBeamsItsPointsTurningAndClosingIn() {
        assertArrayEquals(new double[][]{{0, 0}}, SolomonRules.SKY_BEAM.points(77, 20), "a single beam on the mark");
        SkyBeamRules single = SolomonRules.SKY_BEAM;
        assertEquals(16 * 16, Math.PI * single.width() * single.width(), 1e-6, "as much ground as a chunk");
        // Out of it from the mark within its warning: a player's sprint is 5.6 blocks a second, a walk 4.3.
        double out = single.width() + .3;
        assertTrue(out / 5.612 * 20 < single.timing().windup() - 10, "a sprint out of it with half a second to spare: " + out / 5.612 * 20 + " ticks");
        assertTrue(out / 4.317 * 20 < single.timing().windup(), "even a walk, just: " + out / 4.317 * 20 + " ticks");
        for (SkyBeamRules rules : List.of(SolomonRules.PENTAGRAM, SolomonRules.HEXAGRAM, SolomonRules.OCTAGRAM)) {
            String star = rules.beams() + " points";
            double[][] first = rules.points(30, 0);
            assertEquals(rules.beams(), first.length);
            assertArrayEquals(first, rules.points(30, -10), star + ": before they pour, where they will first strike");
            for (int beam = 0; beam < first.length; beam++) {
                assertEquals(rules.spread(), Math.hypot(first[beam][0], first[beam][1]), 1e-9, star + ": at the star's points at first, under their circles");
                assertEquals(0, apart(degrees(first[beam]), 30 + 360.0 * beam / rules.beams()), 1e-6, star + ": evenly round, the first turned as asked");
            }
            double[][] half = rules.points(30, rules.pouring() / 2.0), almost = rules.points(30, rules.pouring() - 1);
            assertEquals(rules.spread() / 2, Math.hypot(half[0][0], half[0][1]), 1e-9, star + ": closing in");
            assertEquals(0, apart(degrees(almost[0]), 30 + SkyBeamRules.SPIN * (rules.pouring() - 1)), 1e-6, star + ": turned as it poured");
            for (double[] point : rules.points(30, rules.pouring())) assertEquals(0, Math.hypot(point[0], point[1]), 1e-9, star + ": met on the mark at the end");
            // At first there is room to slip out between two beams; whoever stayed on the mark is caught as they meet.
            double gap = 2 * rules.spread() * Math.sin(Math.PI / rules.beams()) - 2 * rules.width();
            assertTrue(gap > rules.width(), star + ": room between two beams at first: " + gap);
            int last = (rules.pouring() - 1) / rules.every() * rules.every();
            double[] closing = rules.points(30, last)[0];
            assertTrue(rules.catches(Math.hypot(closing[0], closing[1]), .3), star + ": the last pulse catches whoever stayed on the mark");
        }
    }

    @Test void everyOneOfSolomonsBeamsIsAsWideAsAChunkAndEveryStarCanBeOutrun() {
        for (SkyBeamRules rules : List.of(SolomonRules.SKY_BEAM, SolomonRules.PENTAGRAM, SolomonRules.HEXAGRAM, SolomonRules.OCTAGRAM))
            assertEquals(16 * 16, Math.PI * rules.width() * rules.width(), 1e-6, rules.beams() + ": every beam as much ground as a chunk");
        for (SkyBeamRules rules : List.of(SolomonRules.PENTAGRAM, SolomonRules.HEXAGRAM, SolomonRules.OCTAGRAM)) {
            String star = rules.beams() + " points";
            assertTrue(rules.spread() + rules.width() > 33, star + ": far wider than a chunk in all: " + 2 * (rules.spread() + rules.width()) + " across");
            // A player sprints 5.6 blocks a second, walks 4.3; set off from the mark as the circles open, they get out in time.
            assertTrue(outrun(rules, 5.612 / 20, 10), star + ": a sprint out, set off half a second late");
            assertTrue(outrun(rules, 4.317 / 20, 0), star + ": even a walk, set off at once");
            assertFalse(outrun(rules, 1 / 20.0, 0), star + ": not a crawl");
        }
    }
}
