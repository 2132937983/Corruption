package com.miophas.corruption.core.corruption;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.BaalAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLeg;
import com.miophas.corruption.api.corruption.ColossusPart;
import org.junit.jupiter.api.Test;

class BaalRulesTest {
    private static ColossusGait walking(double cycle) { return BaalRules.gait(cycle, 1, BaalAttack.NONE, 0, 0); }
    private static ColossusGait standing() { return BaalRules.gait(0, 0, BaalAttack.NONE, 0, 0); }

    /** Where the leg's own angles put the middle of its sole: ahead of the hips, and above the ground. */
    private static double[] sole(ColossusLeg leg, double hip) {
        double thigh = Math.toRadians(leg.thigh()), shin = thigh + Math.toRadians(leg.shin()), cannon = shin + Math.toRadians(leg.cannon());
        double forward = BaalRules.THIGH * Math.sin(thigh) + BaalRules.SHIN * Math.sin(shin) + BaalRules.CANNON * Math.sin(cannon);
        double drop = BaalRules.THIGH * Math.cos(thigh) + BaalRules.SHIN * Math.cos(shin) + BaalRules.CANNON * Math.cos(cannon);
        return new double[]{forward + BaalRules.HOOF_CENTRE, hip - drop * Math.cos(BaalRules.SPLAY) - BaalRules.HOOF};
    }

    @Test void standingIsTheModelsOwnPose() {
        ColossusGait gait = standing();
        for (ColossusLeg leg : new ColossusLeg[]{gait.left(), gait.right()}) {
            assertEquals(22, leg.thigh(), .01); assertEquals(-48, leg.shin(), .01); assertEquals(44, leg.cannon(), .01); assertEquals(-18, leg.hoof(), .01);
            assertEquals(0, leg.lift(), 1e-6);
        }
        assertEquals(-gait.right().side(), gait.left().side(), 1e-4);
        assertTrue(gait.left().side() > BaalRules.LEG_SIDE, "the legs splay outwards");
        // The model file's hips sit a little above where the splayed legs really reach.
        assertEquals(BaalRules.hipPivot(), gait.hip(), 1);
    }

    @Test void theLegReachesExactlyWhereItIsTold() {
        for (double cycle = 0; cycle < 1; cycle += .013) {
            ColossusGait gait = walking(cycle);
            for (ColossusLeg leg : new ColossusLeg[]{gait.left(), gait.right()}) {
                double[] sole = sole(leg, gait.hip());
                assertEquals(leg.forward(), sole[0], .02, "forward at " + cycle);
                assertEquals(leg.lift(), sole[1], .02, "height at " + cycle);
                assertEquals(0, leg.thigh() + leg.shin() + leg.cannon() + leg.hoof(), 1e-3, "the hoof stays level");
            }
        }
    }

    @Test void aPlantedHoofStaysWhereItWasPutDown() {
        // The left hoof lands at the start of a cycle. Until it lifts, the ground it stands on must not move under it.
        double planted = walking(0).left().forward();
        for (double cycle = 0; cycle < BaalRules.STANCE; cycle += .01) {
            ColossusLeg left = walking(cycle).left();
            assertEquals(0, left.lift(), 1e-6);
            assertEquals(planted, left.forward() + cycle * BaalRules.CYCLE, .02, "slid at " + cycle);
        }
    }

    @Test void oneHoofIsAlwaysOnTheGroundAndTheOtherClearsIt() {
        double highest = 0;
        for (double cycle = 0; cycle < 2; cycle += .007) {
            ColossusGait gait = walking(cycle);
            assertTrue(gait.left().lift() == 0 || gait.right().lift() == 0, "both in the air at " + cycle);
            assertTrue(gait.left().lift() >= 0 && gait.right().lift() >= 0);
            highest = Math.max(highest, Math.max(gait.left().lift(), gait.right().lift()));
        }
        assertEquals(BaalRules.SWING_LIFT, highest, .5);
        // Half a cycle on, the legs have swapped places.
        assertEquals(walking(.2).left().forward(), walking(.7).right().forward(), 1e-3);
    }

    @Test void hoovesComeDownOncePerCycleEach() {
        assertEquals(1, BaalRules.footfalls(.9, 1.1));
        assertEquals(2, BaalRules.footfalls(.4, .6));
        assertEquals(0, BaalRules.footfalls(.1, .4));
        assertEquals(0, BaalRules.footfalls(.6, .9));
        assertEquals(1, BaalRules.cycles(BaalRules.blocks(BaalRules.CYCLE)), 1e-9);
        assertTrue(BaalRules.cyclesForTurn(-90) > 0, "turning either way steps it forward through its cycle");
    }

    @Test void aStompLiftsOneHoofAndPutsItBackDownOnTheBeat() {
        BaalAttack stomp = BaalAttack.STOMP_RIGHT;
        ColossusGait raised = BaalRules.gait(0, 0, stomp, stomp.impactTick() - 6, 0);
        assertTrue(raised.right().lift() > 40, "raised " + raised.right().lift());
        assertEquals(0, raised.left().lift(), 1e-6);
        assertEquals(0, BaalRules.gait(0, 0, stomp, stomp.impactTick(), 0).right().lift(), 1e-6);
        assertEquals(0, BaalRules.gait(0, 0, stomp, 0, 0).right().lift(), 1e-6);
        double[] sole = sole(raised.right(), raised.hip());
        assertEquals(raised.right().lift(), sole[1], .05, "the leg can fold that far");
    }

    @Test void itCrouchesIntoASlamAndSinksWhenItDies() {
        BaalAttack slam = BaalAttack.SLAM_LEFT;
        assertEquals(0, BaalRules.slamStrike(slam, 20), 1e-9);
        assertEquals(1, BaalRules.slamStrike(slam, slam.impactTick()), 1e-9);
        assertEquals(1, BaalRules.slamRaise(slam, slam.impactTick() * .6F), 1e-6);
        assertEquals(0, BaalRules.slamRaise(slam, slam.impactTick()), 1e-9);
        assertEquals(0, BaalRules.slamStrike(slam, slam.ticks()), 1e-9);
        assertEquals(0, BaalRules.slamStrike(BaalAttack.ROAR, 46), 1e-9);
        float standing = standing().hip();
        assertTrue(BaalRules.gait(0, 0, slam, slam.impactTick(), 0).hip() < standing - 15);
        ColossusGait dead = BaalRules.gait(0, 0, BaalAttack.NONE, 0, 1);
        assertEquals(BaalRules.COLLAPSED_HIP, dead.hip(), 1e-3);
        assertEquals(0, sole(dead.left(), dead.hip())[1], .05, "its hooves stay on the ground as it sinks");
    }

    @Test void itPicksTheBlowThatCanReach() {
        assertEquals(BaalAttack.STOMP_LEFT, BaalRules.attackFor(10, 20));
        assertEquals(BaalAttack.STOMP_RIGHT, BaalRules.attackFor(-30, -5));
        assertEquals(BaalAttack.SLAM_LEFT, BaalRules.attackFor(100, 5));
        assertEquals(BaalAttack.SLAM_RIGHT, BaalRules.attackFor(100, -5));
        assertEquals(BaalAttack.NONE, BaalRules.attackFor(200, 0));
        assertEquals(BaalAttack.NONE, BaalRules.attackFor(-100, 0), "it cannot slam behind itself");
        // Whatever stands where a slam lands is inside its radius, and so is everything out to the edge of its reach.
        double side = BaalRules.blocks(BaalRules.SLAM_SIDE), forward = BaalRules.blocks(BaalRules.SLAM_FORWARD);
        assertTrue(Math.hypot(side, BaalRules.SLAM_RANGE_BLOCKS - forward) < BaalRules.SLAM_RADIUS_BLOCKS);
        assertTrue(forward - BaalRules.SLAM_RADIUS_BLOCKS < BaalRules.STOMP_RANGE_BLOCKS, "no gap between the stomp and the slam");
    }

    @Test void blowsFadeTowardsTheirEdge() {
        assertEquals(70, BaalRules.damage(70, 0, 34), 1e-4);
        assertEquals(70 * BaalRules.EDGE_DAMAGE, BaalRules.damage(70, 34, 34), 1e-4);
        assertEquals(0, BaalRules.damage(70, 34.1, 34));
        assertTrue(BaalRules.damage(BaalAttack.SLAM_LEFT) > BaalRules.damage(BaalAttack.STOMP_LEFT));
        assertTrue(BaalRules.damage(BaalAttack.STOMP_LEFT) > BaalRules.damage(BaalAttack.NONE));
        assertTrue(BaalRules.radiusBlocks(BaalAttack.STOMP_LEFT) > BaalRules.radiusBlocks(BaalAttack.NONE));
    }

    @Test void theHitboxesFollowTheLegs() {
        ColossusGait gait = walking(.75);
        ColossusPart leftHoof = BaalRules.parts().stream().filter(part -> part.name().equals("left_hoof")).findFirst().orElseThrow();
        ColossusPart leftShin = BaalRules.parts().stream().filter(part -> part.name().equals("left_shin")).findFirst().orElseThrow();
        ColossusPart head = BaalRules.parts().stream().filter(part -> part.name().equals("head")).findFirst().orElseThrow();
        assertArrayEquals(new float[]{gait.left().side(), gait.left().lift(), gait.left().forward()}, BaalRules.place(leftHoof, gait), 1e-4F);
        assertTrue(gait.left().lift() > 10, "the left hoof is mid-swing here");
        float[] shin = BaalRules.place(leftShin, gait), standingShin = BaalRules.place(leftShin, standing());
        assertTrue(shin[1] > standingShin[1], "the shin rises with the lifted hoof");
        assertTrue(shin[1] + leftShin.height() < gait.hip(), "and stays below the hips");
        assertTrue(head.weak());
        assertEquals(head.bottom() + gait.hip() - BaalRules.restHip(), BaalRules.place(head, gait)[1], 1e-4, "the body bobs with the hips");
        assertEquals(23, BaalRules.parts().size());
        assertEquals(BaalRules.parts().size(), BaalRules.parts().stream().map(ColossusPart::name).distinct().count());
    }

    @Test void itIsAColossus() {
        assertEquals(233.75, BaalRules.blocks(BaalRules.TOP), 1e-9);
        assertEquals(185, BaalRules.blocks(BaalRules.SKULL_TOP), 1e-9);
        // One stride covers more ground than most mobs are long.
        assertEquals(100, BaalRules.blocks(BaalRules.CYCLE), 1e-9);
    }
}
