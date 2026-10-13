package com.miophas.corruption.core.corruption;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.AgaresAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLeg;
import com.miophas.corruption.api.corruption.ColossusLegs;
import com.miophas.corruption.api.corruption.ColossusPart;
import org.junit.jupiter.api.Test;

class AgaresRulesTest {
    private static final ColossusLegs LEGS = AgaresRules.legs();
    private static ColossusGait walking(double cycle) { return AgaresRules.gait(cycle, 1, AgaresAttack.NONE, 0, 0); }
    private static ColossusGait standing() { return AgaresRules.gait(0, 0, AgaresAttack.NONE, 0, 0); }

    /** Where the leg's own angles put the middle of its sole: ahead of the hips, and above the ground. */
    private static double[] sole(ColossusLeg leg, double hip) {
        double thigh = Math.toRadians(leg.thigh()), shin = thigh + Math.toRadians(leg.shin()), cannon = shin + Math.toRadians(leg.cannon());
        double forward = LEGS.thigh() * Math.sin(thigh) + LEGS.shin() * Math.sin(shin) + LEGS.cannon() * Math.sin(cannon);
        double drop = LEGS.thigh() * Math.cos(thigh) + LEGS.shin() * Math.cos(shin) + LEGS.cannon() * Math.cos(cannon);
        return new double[]{forward + LEGS.footCentre(), hip - drop * Math.cos(Math.toRadians(LEGS.splay())) - LEGS.foot()};
    }

    @Test void itStandsMoreThanEightHundredBlocksTall() {
        assertTrue(AgaresRules.blocks(AgaresRules.SKULL_TOP) > 800, "to the top of its skull");
        assertEquals(915.625, AgaresRules.blocks(AgaresRules.TOP), 1e-9, "to the tips of its crest");
        assertTrue(AgaresRules.blocks(AgaresRules.TOP) > 3.5 * BaalRules.blocks(BaalRules.TOP), "several times Baal");
    }

    @Test void standingIsTheModelsOwnPoseAndTheSharedGaitHoldsForItsLegs() {
        ColossusGait gait = standing();
        for (ColossusLeg leg : new ColossusLeg[]{gait.left(), gait.right()}) {
            assertEquals(38, leg.thigh(), .01); assertEquals(-76, leg.shin(), .01); assertEquals(62, leg.cannon(), .01); assertEquals(-24, leg.hoof(), .01);
            assertEquals(0, leg.lift(), 1e-6);
        }
        assertEquals(AgaresRules.hipPivot(), gait.hip(), 4, "the model file's hips are near where the legs really reach");
        double planted = walking(0).left().forward();
        for (double cycle = 0; cycle < 1; cycle += .011) {
            ColossusGait walk = walking(cycle);
            assertTrue(walk.left().lift() == 0 || walk.right().lift() == 0, "both feet in the air at " + cycle);
            for (ColossusLeg leg : new ColossusLeg[]{walk.left(), walk.right()}) {
                double[] sole = sole(leg, walk.hip());
                assertEquals(leg.forward(), sole[0], .05, "forward at " + cycle);
                assertEquals(leg.lift(), sole[1], .05, "height at " + cycle);
            }
            if (cycle < LEGS.stance()) assertEquals(planted, walk.left().forward() + cycle * LEGS.cycle(), .05, "the planted foot slid at " + cycle);
        }
        assertEquals(337.5, AgaresRules.blocks(LEGS.cycle()), 1e-9, "one stride covers more than Baal is tall");
    }

    @Test void itsLegsCanFoldForAStompABiteAndItsDeath() {
        AgaresAttack stomp = AgaresAttack.STOMP_LEFT;
        ColossusGait raised = AgaresRules.gait(0, 0, stomp, stomp.strikeFrom() - 7, 0);
        assertTrue(raised.left().lift() > 120, "raised " + raised.left().lift());
        assertEquals(raised.left().lift(), sole(raised.left(), raised.hip())[1], .05, "the leg can fold that far");
        assertEquals(0, raised.right().lift(), 1e-6);
        assertEquals(0, AgaresRules.gait(0, 0, stomp, stomp.strikeFrom(), 0).left().lift(), 1e-6, "down on the tick it strikes");
        ColossusGait biting = AgaresRules.gait(0, 0, AgaresAttack.BITE, AgaresAttack.BITE.strikeFrom(), 0), dead = AgaresRules.gait(0, 0, AgaresAttack.NONE, 0, 1);
        assertTrue(biting.hip() < standing().hip() - 80 && dead.hip() < biting.hip());
        for (ColossusGait low : new ColossusGait[]{biting, dead}) assertEquals(0, sole(low.left(), low.hip())[1], .05, "its feet stay down as it crouches");
    }

    @Test void theBiteRearsThenRakesItsJawsOutAlongTheGround() {
        AgaresAttack bite = AgaresAttack.BITE;
        assertEquals(1, AgaresRules.biteRear(bite, 45), 1e-9);
        assertEquals(0, AgaresRules.biteDown(bite, 50), 1e-9);
        assertEquals(1, AgaresRules.biteDown(bite, bite.strikeFrom()), 1e-9);
        assertEquals(0, AgaresRules.biteRear(bite, bite.strikeFrom()), 1e-9, "the rearing is spent by the time the head is down");
        assertEquals(AgaresRules.BITE_NEAR, AgaresRules.biteForward(bite.strikeFrom()), 1e-9);
        assertEquals(AgaresRules.BITE_MID, AgaresRules.biteForward((bite.strikeFrom() + bite.strikeTo()) / 2F), 1e-9);
        assertEquals(AgaresRules.BITE_FAR, AgaresRules.biteForward(bite.strikeTo()), 1e-9);
        double last = 0;
        for (int tick = bite.strikeFrom(); tick <= bite.strikeTo(); tick++) {
            double at = AgaresRules.biteForward(tick);
            assertTrue(at > last, "the jaws only ever move away from it");
            // Between one tick and the next the jaws move less than their own reach, so nothing in their path is skipped.
            if (tick > bite.strikeFrom()) assertTrue(AgaresRules.blocks(at - last) < AgaresRules.BITE_RADIUS_BLOCKS);
            last = at;
        }
        assertEquals(0, AgaresRules.biteDown(bite, bite.ticks()), 1e-9);
        assertEquals(0, AgaresRules.biteDown(AgaresAttack.GUST, 70), 1e-9);
    }

    @Test void theTailSweepsFromOneSideOfItsBackToTheOther() {
        AgaresAttack left = AgaresAttack.TAIL_LEFT, right = AgaresAttack.TAIL_RIGHT;
        assertEquals(-AgaresRules.TAIL_SWING_DEGREES, AgaresRules.tailAngle(left, left.strikeFrom()), 1e-6, "drawn back to the right");
        assertEquals(AgaresRules.TAIL_SWING_DEGREES, AgaresRules.tailAngle(left, left.strikeTo()), 1e-6, "and ends on the left");
        assertEquals(-AgaresRules.tailAngle(left, 55), AgaresRules.tailAngle(right, 55), 1e-9);
        assertEquals(0, AgaresRules.tailAngle(left, 0), 1e-9); assertEquals(0, AgaresRules.tailAngle(left, left.ticks()), 1e-9);
        assertEquals(0, AgaresRules.tailAngle(AgaresAttack.BITE, 55), 1e-9);
        // Something straight behind is struck as the tail passes through the middle, and only then.
        int hits = 0;
        for (int tick = 0; tick <= left.ticks(); tick++) if (AgaresRules.tailHits(left, tick, -300, 0)) { hits++; assertTrue(Math.abs(AgaresRules.tailAngle(left, tick)) < 20); }
        assertTrue(hits >= 1 && hits <= 6, "ticks it was under the tail: " + hits);
        // Every place in the arc behind it is passed over at some tick; nothing in front of it ever is.
        for (double bearing = 95; bearing <= 265; bearing += 5) for (double distance : new double[]{200, 300, 560}) {
            double forward = distance * Math.cos(Math.toRadians(bearing)), side = distance * Math.sin(Math.toRadians(bearing));
            boolean struck = false;
            for (int tick = left.strikeFrom(); tick <= left.strikeTo(); tick++) struck |= AgaresRules.tailHits(left, tick, forward, side);
            assertTrue(struck, "missed bearing " + bearing + " at " + distance);
        }
        for (int tick = 0; tick <= left.ticks(); tick++) assertFalse(AgaresRules.tailHits(left, tick, 250, 40));
        assertFalse(AgaresRules.tailHits(left, 55, -900, 0), "beyond its tip");
    }

    @Test void theWingsReachWhatIsInFrontAtAnyHeight() {
        assertTrue(AgaresRules.gustHits(300, 0)); assertTrue(AgaresRules.gustHits(200, 200));
        assertFalse(AgaresRules.gustHits(-50, 0)); assertFalse(AgaresRules.gustHits(100, 300)); assertFalse(AgaresRules.gustHits(600, 0));
        AgaresAttack gust = AgaresAttack.GUST;
        assertEquals(1, AgaresRules.gustOpen(gust, 56), 1e-9); assertEquals(0, AgaresRules.gustBeat(gust, 58), 1e-9);
        assertEquals(1, AgaresRules.gustBeat(gust, gust.strikeFrom()), 1e-9); assertEquals(0, AgaresRules.gustOpen(gust, gust.ticks()), 1e-9);
    }

    @Test void itPicksTheWeaponThatCanReach() {
        assertEquals(AgaresAttack.SCREECH, AgaresRules.attackFor(300, 0, 0, 0, true));
        assertEquals(AgaresAttack.STOMP_LEFT, AgaresRules.attackFor(40, 100, 0, 90, false), "what is under it is stomped whichever way it faces");
        assertEquals(AgaresAttack.STOMP_RIGHT, AgaresRules.attackFor(-60, -30, 0, 0, false));
        assertEquals(AgaresAttack.BITE, AgaresRules.attackFor(300, 10, 0, 2, false));
        assertEquals(AgaresAttack.NONE, AgaresRules.attackFor(300, 60, 0, 11, false), "it turns to face before it bites");
        assertEquals(AgaresAttack.NONE, AgaresRules.attackFor(480, 0, 0, 0, false), "and comes closer first");
        assertEquals(AgaresAttack.TAIL_LEFT, AgaresRules.attackFor(-300, 40, 0, 170, false));
        assertEquals(AgaresAttack.TAIL_RIGHT, AgaresRules.attackFor(-200, -200, 0, 135, false));
        assertEquals(AgaresAttack.GUST, AgaresRules.attackFor(250, 30, 200, 7, false), "what flies gets the wings");
        assertEquals(AgaresAttack.NONE, AgaresRules.attackFor(-250, 0, 200, 180, false), "but not behind it");
        // Standing at its stand-off distance, nothing in front of it on the ground is out of reach: the stomp's edge meets the bite's.
        assertTrue(AgaresRules.blocks(AgaresRules.BITE_NEAR) - AgaresRules.BITE_RADIUS_BLOCKS < AgaresRules.STOMP_RANGE_BLOCKS);
        assertTrue(AgaresRules.blocks(AgaresRules.BITE_FAR) + AgaresRules.BITE_RADIUS_BLOCKS > AgaresRules.STAND_OFF_BLOCKS + AgaresRules.BITE_FACING_MARGIN_BLOCKS);
        assertTrue(AgaresRules.STAND_OFF_BLOCKS < AgaresRules.SIGHT_BLOCKS);
    }

    @Test void blowsFadeTowardsTheirEdge() {
        assertEquals(90, AgaresRules.damage(AgaresRules.damage(AgaresAttack.BITE), 0, 70), 1e-4);
        assertEquals(90 * AgaresRules.EDGE_DAMAGE, AgaresRules.damage(90, 70, 70), 1e-4);
        assertEquals(0, AgaresRules.damage(90, 70.1, 70));
        assertTrue(AgaresRules.radiusBlocks(AgaresAttack.STOMP_LEFT) > AgaresRules.radiusBlocks(AgaresAttack.NONE));
        assertTrue(AgaresRules.damage(AgaresAttack.NONE) > BaalRules.damage(com.miophas.corruption.api.corruption.BaalAttack.NONE), "even its footfall outdoes Baal's");
    }

    @Test void itsHitboxesCoverItFromFootToCrestAndFollowItsLegs() {
        assertEquals(37, AgaresRules.parts().size());
        assertEquals(AgaresRules.parts().size(), AgaresRules.parts().stream().map(ColossusPart::name).distinct().count());
        ColossusGait gait = walking(.75);
        ColossusPart foot = AgaresRules.parts().stream().filter(part -> part.name().equals("left_foot")).findFirst().orElseThrow();
        assertArrayEquals(new float[]{gait.left().side(), gait.left().lift(), gait.left().forward()}, AgaresRules.place(foot, gait), 1e-4F);
        assertTrue(gait.left().lift() > 40, "the left foot is mid-swing here");
        double top = AgaresRules.parts().stream().mapToDouble(part -> AgaresRules.place(part, standing())[1] + part.height()).max().orElseThrow();
        assertTrue(top > AgaresRules.SKULL_TOP - 60 && top < AgaresRules.TOP, "the highest hitbox ends about the skull: " + top);
        assertEquals(2, AgaresRules.parts().stream().filter(ColossusPart::weak).count(), "its head and its snout");
        assertEquals(12, AgaresRules.parts().stream().filter(part -> part.name().contains("_wing_")).count());
    }
}
