package com.miophas.corruption.core.demon;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.SolomonRules.Wyrm;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * A flood dragon's body without a game: at work it moves, each of its things its own way, its neck where its head is and
 * every link its length; at nothing it lies where its chain does; its motion takes hold and dies away; a body hanging
 * straight down or all in a heap is posed without fail; and its body swims through the earth where its head swam.
 */
class WyrmPoseTest {
    /** A body laid out straight and level, north of its head, high up. */
    private static WyrmChain straight() {
        WyrmChain chain = new WyrmChain(SolomonRules.JOINTS, SolomonRules.SPACING, 0, 100, 0);
        for (int joint = 0; joint < chain.joints(); joint++) chain.set(joint, 0, 100, -joint * SolomonRules.SPACING);
        return chain;
    }

    private static double[] pose(WyrmChain chain, Wyrm skill, double since, double ended) {
        return WyrmPose.pose(chain, skill, since, ended, new double[chain.joints() * 3]);
    }

    /** How far a posed joint lies from where the chain has it. */
    private static double off(WyrmChain chain, double[] posed, int joint) {
        return Math.sqrt(Math.pow(posed[joint * 3] - chain.x(joint), 2) + Math.pow(posed[joint * 3 + 1] - chain.y(joint), 2) + Math.pow(posed[joint * 3 + 2] - chain.z(joint), 2));
    }

    /** The most any joint moved from one pose to another. */
    private static double moved(double[] from, double[] to) {
        double most = 0;
        for (int joint = 0; joint < from.length / 3; joint++)
            most = Math.max(most, Math.sqrt(Math.pow(to[joint * 3] - from[joint * 3], 2) + Math.pow(to[joint * 3 + 1] - from[joint * 3 + 1], 2) + Math.pow(to[joint * 3 + 2] - from[joint * 3 + 2], 2)));
        return most;
    }

    private static List<Wyrm> moving() {
        List<Wyrm> all = new ArrayList<>(SolomonRules.WYRM_SKILLS);
        all.add(Wyrm.SWOOP);
        return all;
    }

    @Test void atNothingItsBodyLiesWhereItsChainDoes() {
        WyrmChain chain = straight();
        double[] saved = chain.save();
        assertArrayEquals(saved, pose(chain, Wyrm.NONE, 50, -1), "at nothing");
        assertArrayEquals(saved, pose(chain, Wyrm.BEAM, 0, -1), "the moment it begins");
        assertArrayEquals(saved, pose(chain, Wyrm.BEAM, 200, WyrmPose.EASE_OUT), "once its motion has died away");
    }

    @Test void atWorkEachThingMovesItsBodyKeepingItsNeckAndItsLength() {
        WyrmChain chain = straight();
        StringBuilder line = new StringBuilder("POSE the most a joint lies from its chain, winding up / at work:");
        for (Wyrm skill : moving()) {
            WyrmPose.Motion motion = WyrmPose.motion(skill);
            double[] most = new double[2];
            int stage = motion.windup() == WyrmPose.Wave.STILL ? 1 : 0;
            // Late in its wind-up (if it has one), and well into its work.
            double[] when = {Math.max(WyrmPose.EASE_IN, motion.windupTicks() - 2), motion.windupTicks() + 30};
            for (double since : stage == 1 ? new double[]{when[1]} : when) {
                double[] posed = pose(chain, skill, since, -1);
                assertEquals(0, off(chain, posed, 0), 1e-12, skill + ": its neck where its head is");
                for (int joint = 1; joint < chain.joints(); joint++) {
                    double length = Math.sqrt(Math.pow(posed[joint * 3] - posed[joint * 3 - 3], 2) + Math.pow(posed[joint * 3 + 1] - posed[joint * 3 - 2], 2)
                            + Math.pow(posed[joint * 3 + 2] - posed[joint * 3 - 1], 2));
                    assertEquals(SolomonRules.SPACING, length, 1e-9, skill + ": link " + joint + " its length");
                    most[stage] = Math.max(most[stage], off(chain, posed, joint));
                    assertTrue(Double.isFinite(posed[joint * 3]) && Double.isFinite(posed[joint * 3 + 1]) && Double.isFinite(posed[joint * 3 + 2]));
                }
                assertTrue(most[stage] > 1, skill + " moves its body at " + since + ": " + most[stage]);
                assertTrue(most[stage] < 12, skill + " no wilder than that: " + most[stage]);
                stage++;
            }
            // At work it never stops moving: not even a body braced to pour out its beam.
            double[] now = pose(chain, skill, motion.windupTicks() + 30, -1), soon = pose(chain, skill, motion.windupTicks() + 33, -1);
            assertTrue(moved(now, soon) > .05, skill + " still moving at work: " + moved(now, soon));
            line.append(String.format(" %s %.1f / %.1f;", skill.name().toLowerCase(), most[0], most[1]));
        }
        System.out.println(line);
        // Each its own way.
        assertTrue(moved(pose(chain, Wyrm.BEAM, 60, -1), pose(chain, Wyrm.HANDS, 60, -1)) > 1, "a beam and the hands differ");
    }

    @Test void itsMotionTakesHoldAndDiesAway() {
        assertEquals(0, WyrmPose.strength(0, -1), 1e-12);
        double half = WyrmPose.strength(WyrmPose.EASE_IN / 2.0, -1);
        assertTrue(half > 0 && half < 1, "taking hold: " + half);
        assertEquals(1, WyrmPose.strength(WyrmPose.EASE_IN, -1), 1e-12, "taken hold");
        double dying = WyrmPose.strength(100, WyrmPose.EASE_OUT / 2.0);
        assertTrue(dying > 0 && dying < 1, "dying away: " + dying);
        assertEquals(0, WyrmPose.strength(100, WyrmPose.EASE_OUT), 1e-12, "gone");
        // Softly: no joint jumps as it begins, nor as it comes to rest.
        WyrmChain chain = straight();
        for (Wyrm skill : moving()) {
            double first = moved(pose(chain, skill, 0, -1), pose(chain, skill, 1, -1));
            double last = moved(pose(chain, skill, 100 + WyrmPose.EASE_OUT - 1, WyrmPose.EASE_OUT - 1), pose(chain, skill, 100 + WyrmPose.EASE_OUT, WyrmPose.EASE_OUT));
            assertTrue(first < .5 && last < .5, skill + " begins and ends softly: " + first + ", " + last);
        }
    }

    @Test void aBodyHangingStraightDownOrAllInAHeapIsPosedWithoutFail() {
        WyrmChain hanging = new WyrmChain(SolomonRules.JOINTS, SolomonRules.SPACING, 0, 100, 0);
        for (int joint = 0; joint < hanging.joints(); joint++) hanging.set(joint, 0, 100 - joint * SolomonRules.SPACING, 0);
        for (double value : pose(hanging, Wyrm.STORM, 60, -1)) assertTrue(Double.isFinite(value));
        WyrmChain heap = new WyrmChain(SolomonRules.JOINTS, SolomonRules.SPACING, 5, 70, 5);
        double[] posed = pose(heap, Wyrm.STORM, 60, -1);
        for (int joint = 0; joint < heap.joints(); joint++) assertEquals(0, off(heap, posed, joint), 1e-9, "a heap stays a heap");
    }

    @Test void itsBodySwimsThroughTheEarthWhereItsHeadSwam() {
        WyrmWake wake = new WyrmWake(SolomonRules.LENGTH * WyrmWake.CURL + 4);
        // Ten blocks in the air, twenty under the ground, ten in the air again, a block a tick.
        for (int tick = 0; tick < 40; tick++) wake.go(1, tick >= 10 && tick < 30);
        assertEquals(40, wake.travelled(), 1e-9);
        assertFalse(wake.swims(0, SolomonRules.SPACING), "just behind its head: in the air again");
        assertTrue(wake.swims(7, SolomonRules.SPACING), "14 to 16 behind it: where it swam");
        assertFalse(wake.swims(18, SolomonRules.SPACING), "36 behind it: before it went down");
        double[] saved = wake.save();
        WyrmWake again = new WyrmWake(SolomonRules.LENGTH * WyrmWake.CURL + 4);
        again.load(saved);
        assertTrue(again.swims(7, SolomonRules.SPACING), "kept");
        // Once the whole body has come by, the stretch is forgotten.
        for (int tick = 0; tick < 200; tick++) wake.go(1, false);
        for (int link = 0; link < SolomonRules.JOINTS - 1; link++) assertFalse(wake.swims(link, SolomonRules.SPACING), "link " + link);
        assertEquals(2, wake.save().length, "nothing kept of it");
    }
}
