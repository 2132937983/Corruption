package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.BaalApi;
import com.miophas.corruption.api.corruption.BaalAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLeg;
import com.miophas.corruption.common.registry.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

/** Poses the real baked model and measures it, so what is drawn agrees with what the server hits. */
class BaalModelTest {
    private final ModelPart root = BaalModel.createBodyLayer().bakeRoot();
    private final BaalModel model = new BaalModel(root);

    /**
     * Where a point of a bone ends up, in model pixels as the rules count them: sideways positive to Baal's
     * left, height above the ground, forward the way it faces. The point is given in the bone's own space.
     */
    private float[] locate(float x, float y, float z, String... path) {
        PoseStack stack = new PoseStack();
        ModelPart part = root;
        for (String name : path) { part = part.getChild(name); part.translateAndRotate(stack); }
        Vector3f at = stack.last().pose().transformPosition(new Vector3f(x / 16, y / 16, z / 16));
        return new float[]{at.x * 16, 24 - at.y * 16, -at.z * 16};
    }
    /** The middle of a hoof's sole: fourteen pixels under its ankle and six ahead of it. */
    private float[] sole(String side) {
        return locate(0, 14, -6, "baal", "hips", side + "_leg", side + "_shin", side + "_foot", side + "_hoof");
    }
    private float[] palm(String side) {
        return locate(0, 18, 0, "baal", "hips", "torso", "chest", side + "_arm", side + "_forearm", side + "_hand");
    }

    @Test void theModelBakesWithEveryBoneTheRigNeeds() {
        var layer = BaalModel.createBodyLayer();
        assertNotNull(layer.bakeRoot());
        // The constructor looks up every animated bone and throws on a missing one.
        assertEquals(root, model.root());
        assertEquals(170, root.getAllParts().count(), "bones, including the ones Blockbench adds for turned cubes");
        assertEquals("corruption:baal", ModEntities.BAAL.getId().toString());
    }

    @Test void standingPutsBothHoovesFlatOnTheGround() {
        model.pose(BaalPose.standing(0));
        ColossusGait gait = BaalApi.gait(0, 0, BaalAttack.NONE, 0, 0);
        for (String side : new String[]{"left", "right"}) {
            float[] sole = sole(side);
            ColossusLeg leg = gait.leg(side.equals("left"));
            assertEquals(0, sole[1], .3, side + " hoof on the ground");
            assertEquals(leg.side(), sole[0], 1.5, side); assertEquals(leg.forward(), sole[2], .5, side);
        }
        assertTrue(sole("left")[0] > 0 && sole("right")[0] < 0, "the left hoof is on Baal's left");
    }

    @Test void theDrawnHoovesAreWhereTheGaitSaysThroughTheWholeStride() {
        float planted = Float.NaN;
        for (float cycle = 0; cycle < 1; cycle += .02F) {
            model.pose(new BaalPose(37, cycle, 1, BaalAttack.NONE, 0, 0, 20, 10));
            ColossusGait gait = BaalApi.gait(cycle, 1, BaalAttack.NONE, 0, 0);
            for (String side : new String[]{"left", "right"}) {
                float[] sole = sole(side);
                ColossusLeg leg = gait.leg(side.equals("left"));
                assertEquals(leg.lift(), sole[1], .4, side + " height at " + cycle);
                assertEquals(leg.forward(), sole[2], .5, side + " forward at " + cycle);
                assertEquals(leg.side(), sole[0], 1.5, side + " sideways at " + cycle);
            }
            // While the left hoof is down it must not slide: the body moves over it at exactly the walking pace.
            if (gait.left().lift() == 0 && cycle < .5F) {
                float ground = sole("left")[2] + cycle * 160;
                if (Float.isNaN(planted)) planted = ground;
                assertEquals(planted, ground, .6, "the planted hoof slid at " + cycle);
            }
        }
    }

    @Test void aStompRaisesTheHoofTheServerLifts() {
        BaalAttack stomp = BaalAttack.STOMP_LEFT;
        float ticks = stomp.impactTick() - 6;
        model.pose(new BaalPose(0, 0, 0, stomp, ticks, 0, 0, 0));
        assertEquals(BaalApi.gait(0, 0, stomp, ticks, 0).left().lift(), sole("left")[1], .5);
        assertTrue(sole("left")[1] > 40);
        assertEquals(0, sole("right")[1], .3);
        model.pose(new BaalPose(0, 0, 0, stomp, stomp.impactTick(), 0, 0, 0));
        assertEquals(0, sole("left")[1], .3, "down again when the damage lands");
    }

    @Test void theSlammingHandLandsWhereTheDamageDoes() {
        for (BaalAttack slam : new BaalAttack[]{BaalAttack.SLAM_LEFT, BaalAttack.SLAM_RIGHT}) {
            String side = slam.left() ? "left" : "right";
            model.pose(BaalPose.standing(0));
            float[] rest = palm(side);
            model.pose(new BaalPose(0, 0, 0, slam, slam.impactTick() * .6F, 0, 0, 0));
            assertTrue(palm(side)[1] > rest[1] + 150, side + " hand is drawn back overhead before the blow: " + palm(side)[1]);
            model.pose(new BaalPose(0, 0, 0, slam, slam.impactTick(), 0, 0, 0));
            float[] palm = palm(side), impact = BaalApi.impact(slam, BaalApi.gait(0, 0, slam, slam.impactTick(), 0));
            assertEquals(impact[0], palm[0], 8, side + " sideways");
            assertEquals(impact[1], palm[2], 8, side + " forward");
            assertTrue(palm[1] > 0 && palm[1] < 25, side + " palm is on the ground: " + palm[1]);
            assertEquals(0, sole(side)[1], .3, "it crouches without lifting its hooves");
        }
    }

    @Test void dyingFoldsItOntoItsHaunchesWithItsHoovesStillDown() {
        model.pose(BaalPose.standing(0));
        float standing = locate(0, 0, 0, "baal", "hips", "torso", "chest", "neck", "head")[1];
        model.pose(new BaalPose(0, 0, 0, BaalAttack.NONE, 0, 1, 0, 0));
        float fallen = locate(0, 0, 0, "baal", "hips", "torso", "chest", "neck", "head")[1];
        assertTrue(fallen < standing - 80, "the head drops: " + standing + " to " + fallen);
        assertEquals(0, sole("left")[1], .3); assertEquals(0, sole("right")[1], .3);
        for (float collapse = .1F; collapse <= 1; collapse += .1F) {
            model.pose(new BaalPose(0, 0, 0, BaalAttack.NONE, 0, collapse, 0, 0));
            for (String side : new String[]{"left", "right"}) assertTrue(palm(side)[1] > 5, side + " hand goes through the ground at " + collapse);
        }
    }

    @Test void poseIsRebuiltFromRestEachTime() {
        model.pose(new BaalPose(500, 3.3F, 1, BaalAttack.ROAR, 40, 0, 30, -20));
        model.pose(BaalPose.standing(0));
        float[] first = palm("left");
        model.pose(new BaalPose(900, 7.1F, 1, BaalAttack.SLAM_RIGHT, 46, .5F, -40, 30));
        model.pose(BaalPose.standing(0));
        assertArrayEquals(first, palm("left"), 1e-4F);
    }
}
