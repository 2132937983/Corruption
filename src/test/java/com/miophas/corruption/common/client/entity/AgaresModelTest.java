package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.AgaresApi;
import com.miophas.corruption.api.corruption.AgaresAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLeg;
import com.miophas.corruption.api.corruption.ColossusLegs;
import com.miophas.corruption.common.registry.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

/** Poses the real baked model and measures it, so what is drawn agrees with what the server hits. */
class AgaresModelTest {
    private static final String[] HEAD = {"agares", "hips", "torso", "chest", "neck", "neck_1", "neck_2", "neck_3", "neck_4", "head"};
    private final ModelPart root = AgaresModel.createBodyLayer().bakeRoot();
    private final AgaresModel model = new AgaresModel(root);
    private final ColossusLegs legs = AgaresApi.legs();

    /**
     * Where a point of a bone ends up, in model pixels as the rules count them: sideways positive to its
     * left, height above the ground, forward the way it faces. The point is given in the bone's own space.
     */
    private float[] locate(float x, float y, float z, String... path) {
        PoseStack stack = new PoseStack();
        ModelPart part = root;
        for (String name : path) { part = part.getChild(name); part.translateAndRotate(stack); }
        Vector3f at = stack.last().pose().transformPosition(new Vector3f(x / 16, y / 16, z / 16));
        return new float[]{at.x * 16, 24 - at.y * 16, -at.z * 16};
    }
    /** The middle of a sole: under its ankle by the height of the foot, and ahead of it. */
    private float[] sole(String side) {
        return locate(0, legs.foot(), -legs.footCentre(), "agares", "hips", side + "_leg", side + "_shin", side + "_tarsus", side + "_foot");
    }
    /** The front of the upper jaw, where it meets the ground in a bite. */
    private float[] snout() { return locate(0, 17, -332, HEAD); }
    private static AgaresPose attacking(AgaresAttack attack, float ticks) { return new AgaresPose(0, 0, 0, attack, ticks, 0, 0, 0); }

    @Test void theRulesMeasureTheModelThatIsShipped() {
        // The gait works from these numbers and the model is posed by its results, so they must be the model's own.
        Map<String, PartPose> bones = new java.util.HashMap<>();
        for (GeoModel.Bone bone : GeoModel.load("agares").bones()) bones.put(bone.name(), bone.pose());
        assertEquals(-AgaresApi.hipPivot(), bones.get("hips").y);
        assertEquals(legs.thigh(), bones.get("left_shin").y); assertEquals(legs.shin(), bones.get("left_tarsus").y); assertEquals(legs.cannon(), bones.get("left_foot").y);
        assertEquals(legs.side(), bones.get("left_leg").x); assertEquals(-legs.side(), bones.get("right_leg").x);
        assertEquals(-Math.toRadians(legs.restThigh()), bones.get("left_leg").xRot, 1e-3);
        assertEquals(-Math.toRadians(legs.restShin()), bones.get("left_shin").xRot, 1e-3);
        assertEquals(-Math.toRadians(legs.restCannon()), bones.get("left_tarsus").xRot, 1e-3);
        assertEquals(Math.toRadians(legs.splay()), -bones.get("left_leg").zRot, 1e-3);
        assertEquals("corruption:agares", ModEntities.AGARES.getId().toString());
    }

    @Test void itStandsOnItsFeetWithItsSkullOverEightHundredBlocksUp() {
        model.pose(AgaresPose.standing(0));
        ColossusGait gait = AgaresApi.gait(0, 0, AgaresAttack.NONE, 0, 0);
        for (String side : new String[]{"left", "right"}) {
            float[] sole = sole(side);
            ColossusLeg leg = gait.leg(side.equals("left"));
            assertEquals(0, sole[1], 1, side + " foot on the ground");
            assertEquals(leg.side(), sole[0], 4, side); assertEquals(leg.forward(), sole[2], 1.5, side);
        }
        assertTrue(sole("left")[0] > 0 && sole("right")[0] < 0, "the left foot is on its left");
        // The crown of the skull is a little above and behind the head's pivot.
        float skull = locate(0, -79, 0, HEAD)[1];
        assertTrue(AgaresApi.blocks(skull) > 800, "its skull is " + AgaresApi.blocks(skull) + " blocks up");
        assertEquals(AgaresApi.skullTopBlocks(), AgaresApi.blocks(skull), 20);
        assertEquals(AgaresApi.eyeHeightBlocks(), AgaresApi.blocks(locate(0, -40, -70, HEAD)[1]), 20, "its eyes are where the entity's are");
    }

    @Test void theDrawnFeetAreWhereTheGaitSaysThroughTheWholeStride() {
        float planted = Float.NaN;
        for (float cycle = 0; cycle < 1; cycle += .02F) {
            model.pose(new AgaresPose(53, cycle, 1, AgaresAttack.NONE, 0, 0, 25, -10));
            ColossusGait gait = AgaresApi.gait(cycle, 1, AgaresAttack.NONE, 0, 0);
            for (String side : new String[]{"left", "right"}) {
                float[] sole = sole(side);
                ColossusLeg leg = gait.leg(side.equals("left"));
                assertEquals(leg.lift(), sole[1], 1.5, side + " height at " + cycle);
                assertEquals(leg.forward(), sole[2], 2, side + " forward at " + cycle);
                assertEquals(leg.side(), sole[0], 5, side + " sideways at " + cycle);
            }
            if (gait.left().lift() == 0 && cycle < .5F) {
                float ground = sole("left")[2] + cycle * legs.cycle();
                if (Float.isNaN(planted)) planted = ground;
                assertEquals(planted, ground, 2.5, "the planted foot slid at " + cycle);
            }
        }
    }

    @Test void aStompRaisesTheFootTheServerLifts() {
        AgaresAttack stomp = AgaresAttack.STOMP_RIGHT;
        float ticks = stomp.strikeFrom() - 7;
        model.pose(attacking(stomp, ticks));
        assertEquals(AgaresApi.gait(0, 0, stomp, ticks, 0).right().lift(), sole("right")[1], 1.5);
        assertTrue(sole("right")[1] > 120); assertEquals(0, sole("left")[1], 1);
        model.pose(attacking(stomp, stomp.strikeFrom()));
        assertEquals(0, sole("right")[1], 1, "down again when the damage lands");
    }

    @Test void theJawsAreOnTheGroundWhereTheBiteIsDealt() {
        AgaresAttack bite = AgaresAttack.BITE;
        model.pose(AgaresPose.standing(0));
        float rest = snout()[1];
        model.pose(attacking(bite, 45));
        assertTrue(snout()[1] > rest + 60, "it rears before it strikes: " + snout()[1] + " from " + rest);
        for (int tick = bite.strikeFrom(); tick <= bite.strikeTo(); tick += 2) {
            model.pose(attacking(bite, tick));
            float[] snout = snout();
            assertEquals(AgaresApi.biteForward(tick), snout[2], 35, "forward at tick " + tick);
            assertTrue(snout[1] > 0 && snout[1] < 70, "the snout skims the ground at tick " + tick + ": " + snout[1]);
            assertEquals(0, snout[0], 3, "straight ahead");
            assertEquals(0, sole("left")[1], 1, "it crouches without lifting its feet");
        }
        model.pose(attacking(bite, bite.ticks()));
        assertEquals(rest, snout()[1], 1, "and comes back up");
    }

    @Test void theTailIsDrawnWhereTheSweepStrikes() {
        String[] tip = {"agares", "hips", "tail", "tail_1", "tail_2", "tail_3", "tail_4", "tail_5", "tail_6", "tail_7", "tail_8", "tail_9", "tail_10", "tail_11", "tail_12", "tail_13"};
        float[] root = locate(0, 0, 0, "agares", "hips", "tail");
        for (AgaresAttack sweep : new AgaresAttack[]{AgaresAttack.TAIL_LEFT, AgaresAttack.TAIL_RIGHT})
            for (int tick = sweep.strikeFrom(); tick <= sweep.strikeTo(); tick += 3) {
                model.pose(attacking(sweep, tick));
                float[] end = locate(0, 0, 0, tip);
                double drawn = Math.toDegrees(Math.atan2(end[0] - root[0], -(end[2] - root[2])));
                assertEquals(AgaresApi.tailAngle(sweep, tick), drawn, 14, sweep + " at tick " + tick);
                assertTrue(Math.hypot(end[0] - root[0], end[2] - root[2]) > 600, "the tail stays stretched out");
            }
        model.pose(attacking(AgaresAttack.TAIL_LEFT, AgaresAttack.TAIL_LEFT.strikeTo()));
        assertTrue(locate(0, 0, 0, tip)[0] > 500, "a sweep to the left ends on its left");
    }

    @Test void theWingsOpenAndBeatForward() {
        // The far end of the bone that carries the long flight feathers.
        String[] hand = {"agares", "hips", "torso", "chest", "left_wing", "left_wing_forearm", "left_wing_hand"};
        model.pose(AgaresPose.standing(0));
        float[] rest = locate(224, 0, 0, hand);
        model.pose(attacking(AgaresAttack.GUST, 56));
        float[] open = locate(224, 0, 0, hand);
        assertTrue(open[1] > rest[1] + 300, "the wing unfolds upwards: its tip is " + open[1] + " high, from " + rest[1]);
        model.pose(attacking(AgaresAttack.GUST, AgaresAttack.GUST.strikeFrom()));
        assertTrue(locate(224, 0, 0, hand)[2] > open[2] + 250, "then it is thrown forward");
        assertTrue(rest[0] > 0, "the left wing is on its left");
        model.pose(attacking(AgaresAttack.GUST, AgaresAttack.GUST.ticks()));
        assertArrayEquals(rest, locate(224, 0, 0, hand), .5F, "and folds back");
    }

    @Test void dyingBringsItDownWithItsFeetStillOnTheGround() {
        model.pose(AgaresPose.standing(0));
        float standing = locate(0, 0, 0, HEAD)[1];
        model.pose(new AgaresPose(0, 0, 0, AgaresAttack.NONE, 0, 1, 0, 0));
        assertTrue(locate(0, 0, 0, HEAD)[1] < standing - 400, "the head comes down: " + standing + " to " + locate(0, 0, 0, HEAD)[1]);
        assertEquals(0, sole("left")[1], 1); assertEquals(0, sole("right")[1], 1);
        // All the way down, its jaws and chin come to the ground without going into it.
        for (float collapse = .05F; collapse <= 1.001F; collapse += .05F) {
            model.pose(new AgaresPose(0, 0, 0, AgaresAttack.NONE, 0, collapse, 0, 0));
            assertTrue(snout()[1] > 20 && locate(0, 60, -40, HEAD)[1] > 20, "its head goes through the ground at " + collapse + ": " + snout()[1]);
        }
        assertTrue(snout()[1] < 110 && snout()[2] > 900, "it ends laid out in front of itself: " + snout()[1] + " up, " + snout()[2] + " ahead");
    }

    @Test void onlyTheGlowingPiecesAreDrawnForTheGlow() {
        int all = GeoModelTest.faces(root).size();
        model.glowOnly(true);
        int glowing = GeoModelTest.faces(root).size();
        model.glowOnly(false);
        assertEquals(all, GeoModelTest.faces(root).size());
        assertTrue(glowing > 400 && glowing < all / 8, "eyes, veins and the fissure, not the body: " + glowing + " of " + all);
        model.pose(new AgaresPose(700, 4.4F, 1, AgaresAttack.SCREECH, 50, .3F, 40, 20));
        model.pose(AgaresPose.standing(0));
        float[] first = snout();
        model.pose(attacking(AgaresAttack.BITE, 70));
        model.pose(AgaresPose.standing(0));
        assertArrayEquals(first, snout(), 1e-3F, "a pose is rebuilt from rest each time");
    }
}
