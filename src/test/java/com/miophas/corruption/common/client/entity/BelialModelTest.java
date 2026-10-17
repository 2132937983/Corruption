package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.entity.Belial;
import com.miophas.corruption.core.demon.BelialRules;
import com.miophas.corruption.core.demon.BelialRules.Skill;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

/**
 * Belial's two baked models and his chariot's. Each poses through every skill, and every pose eases back to rest by
 * itself once its skill is over. His beams leave from where his models hold his hands (the fair angel) and the eyes of
 * his wings (the hollow): the places the server sends them from are those. The fair angel falls in the first half of his
 * change and the hollow rises in the second; the hollow's six wings fan out behind him, facing forward. The chariot's
 * wheels turn as far as it runs, and its horses gallop. And the pictures they need are there.
 */
class BelialModelTest {
    private final BelialFairModel fair = new BelialFairModel(BelialFairModel.createBodyLayer().bakeRoot());
    private final BelialHollowModel hollow = new BelialHollowModel(BelialHollowModel.createBodyLayer().bakeRoot());
    private final FieryChariotModel chariot = new FieryChariotModel(FieryChariotModel.createBodyLayer().bakeRoot());

    /** Where a point given in a bone's own pixels is, in the model's pixels, through the bones from the root down to it. */
    private static Vector3f at(ModelPart root, float x, float y, float z, ModelPart... chain) {
        PoseStack stack = new PoseStack();
        root.translateAndRotate(stack);
        for (ModelPart part : chain) part.translateAndRotate(stack);
        Vector4f point = stack.last().pose().transform(new Vector4f(x / 16, y / 16, z / 16, 1));
        return new Vector3f(point.x * 16, point.y * 16, point.z * 16);
    }

    /** A place in the model's pixels as the entity measures it: {right, up, ahead} in blocks from his feet. */
    private static double[] frame(Vector3f model) { return new double[]{-model.x / 16, (24 - model.y) / 16, -model.z / 16}; }

    private static float[] pose(ModelPart[] parts) {
        float[] all = new float[parts.length * 9];
        for (int index = 0; index < parts.length; index++) {
            ModelPart part = parts[index];
            System.arraycopy(new float[]{part.x, part.y, part.z, part.xRot, part.yRot, part.zRot, part.xScale, part.yScale, part.zScale}, 0, all, index * 9, 9);
        }
        return all;
    }

    /** Whether every place in one list is within so far of one in the other (in blocks), each matched once. */
    private static void sameplaces(double[][] expected, List<double[]> actual, double within, String what) {
        assertEquals(expected.length, actual.size(), what);
        List<double[]> left = new ArrayList<>(actual);
        for (double[] want : expected) {
            double[] best = null;
            double bestDistance = Double.MAX_VALUE;
            for (double[] have : left) {
                double distance = Math.sqrt(Math.pow(have[0] - want[0], 2) + Math.pow(have[1] - want[1], 2) + Math.pow(have[2] - want[2], 2));
                if (distance < bestDistance) { bestDistance = distance; best = have; }
            }
            assertTrue(bestDistance <= within, what + ": " + Arrays.toString(want) + " is nowhere near where the model has them: "
                    + actual.stream().map(place -> String.format("{%.2f, %.2f, %.2f}", place[0], place[1], place[2])).toList());
            left.remove(best);
        }
    }

    @Test void allThreeBakeAndPoseThroughEverySkill() {
        for (Skill skill : Skill.values()) for (float ticks = 0; ticks < 140; ticks += 7) {
            float at = ticks;
            assertDoesNotThrow(() -> fair.pose(at, at * .2F, skill, at, at % 100 / 100, 0, 3), skill.name());
            assertDoesNotThrow(() -> hollow.pose(at, at * .2F, skill, at, at % 2 == 0, at % 100 / 100, at % 60 / 60, 3), skill.name());
        }
        assertDoesNotThrow(() -> chariot.pose(12.5F, 20));
        fair.pose(0, 0, Skill.NONE, 0, 0, 0, 0);
        assertEquals(fair.wing[0].zRot, -fair.wing[1].zRot, 1e-6, "his wings mirror each other");
        for (int pair = 0; pair < 3; pair++) {
            hollow.pose(0, 0, Skill.NONE, 0, false, 0, 0, 0);
            assertEquals(hollow.wing[pair][0].zRot, -hollow.wing[pair][1].zRot, 1e-6, "so do the hollow angel's, pair by pair");
        }
    }

    @Test void theFairAngelsBeamsLeaveFromHisRaisedHands() {
        fair.pose(0, 0, Skill.BEAMS, BelialRules.BEAMS.beam().timing().windup() + 10, 0, 0, 0);
        List<double[]> hands = new ArrayList<>();
        for (int side = 0; side < 2; side++) hands.add(frame(at(fair.root, 0, 11, 0, fair.body, fair.arm[side], fair.forearm[side])));
        sameplaces(Belial.FAIR_HANDS, hands, .3, "his hands");
        for (double[] hand : hands) assertTrue(hand[2] > .5, "held out before him");
    }

    @Test void theHollowAngelsBeamsLeaveFromTheEyesOfHisSixWings() {
        hollow.pose(0, 0, Skill.BEAMS, BelialRules.BEAMS.beam().timing().windup() + 10, false, 0, 0, 0);
        // Each eye is painted on its wing's feathers, 86% of the way out along the arm of it, 40% of the way back across them.
        float[][] wings = {{10, 14}, {9, 12}, {8, 10}};
        List<double[]> eyes = new ArrayList<>();
        for (int pair = 0; pair < 3; pair++)
            for (int side = 0; side < 2; side++) {
                float out = wings[pair][0] * .86F * (side == 0 ? -1 : 1);
                eyes.add(frame(at(hollow.root, out, .2F, 1 + wings[pair][1] * .4F, hollow.body, hollow.wing[pair][side])));
            }
        sameplaces(Belial.HOLLOW_EYES, eyes, .3, "the eyes of his wings");
    }

    @Test void theHollowAngelsWingsFanOutBehindHimFacingForward() {
        hollow.pose(0, 0, Skill.NONE, 0, false, 0, 0, 0);
        for (int side = 0; side < 2; side++) {
            float out = side == 0 ? -10 : 10;
            double upper = frame(at(hollow.root, out, 0, 0, hollow.body, hollow.wing[0][side]))[1];
            double middle = frame(at(hollow.root, out * .9F, 0, 0, hollow.body, hollow.wing[1][side]))[1];
            double lower = frame(at(hollow.root, out * .8F, 0, 0, hollow.body, hollow.wing[2][side]))[1];
            assertTrue(upper > middle && middle > lower, "the highest pair raised, the lowest lowered: " + upper + " " + middle + " " + lower);
            // The way the face of a wing looks: its feathers' plane turned upright, the face with the eye before him.
            Vector3f root = at(hollow.root, 0, 0, 0, hollow.body, hollow.wing[1][side]), face = at(hollow.root, 0, 1, 0, hollow.body, hollow.wing[1][side]);
            assertTrue(face.z - root.z < -.8F, "the eye's face looks before him: " + face.sub(root));
        }
    }

    @Test void theFairAngelFallsInTheFirstHalfOfHisChangeAndTheHollowRisesInTheSecond() {
        fair.pose(0, 0, Skill.NONE, 0, .45F, 0, 0);
        assertTrue(fair.body.xScale < .2F, "the fair angel dwindles as he falls");
        assertTrue(fair.body.xRot < -.5F, "falling back");
        hollow.pose(0, 0, Skill.NONE, 0, false, .5F, 0, 0);
        assertTrue(hollow.body.xScale < .05F, "the hollow angel is nothing yet at its middle");
        hollow.pose(0, 0, Skill.NONE, 0, false, 1, 0, 0);
        assertEquals(1, hollow.body.xScale, 1e-6, "and whole by its end");
    }

    @Test void everyPoseEasesBackToRestOnceItsSkillIsOver() {
        fair.pose(0, 0, Skill.NONE, 0, 0, 0, 0);
        float[] fairRest = pose(fair.all);
        hollow.pose(0, 0, Skill.NONE, 0, false, 0, 0, 0);
        float[] hollowRest = pose(hollow.all);
        for (Skill skill : Skill.values()) {
            fair.pose(0, 0, skill, 400, 0, 0, 0);
            assertArrayEquals(fairRest, pose(fair.all), 1e-4F, skill + " settles the fair angel back to rest");
            hollow.pose(0, 0, skill, 400, false, 0, 0, 0);
            assertArrayEquals(hollowRest, pose(hollow.all), 1e-4F, skill + " settles the hollow angel back to rest");
        }
    }

    @Test void theChariotsWheelsTurnAsFarAsItRunsAndItsHorsesGallop() {
        chariot.pose(0, 0);
        float[] standing = pose(chariot.legs[0]);
        chariot.pose(FieryChariotModel.WHEEL * 2, 0);
        assertEquals(2, chariot.wheels[0].xRot, 1e-5, "a wheel turns a radian for every radius it runs");
        assertEquals(chariot.wheels[0].xRot, chariot.wheels[1].xRot, 1e-6, "both alike");
        chariot.pose(FieryChariotModel.STRIDE * .3F, 0);
        assertFalse(Arrays.equals(standing, pose(chariot.legs[0])), "its legs swing as it runs");
        assertNotEquals(chariot.legs[0][0].xRot, chariot.legs[0][2].xRot, "fore and hind legs out of step");
    }

    @Test void thePicturesTheyNeedAreThere() throws Exception {
        for (String name : List.of("belial", "fiery_chariot")) {
            BufferedImage texture = image("/assets/corruption/textures/entity/" + name + ".png"), glow = image("/assets/corruption/textures/entity/" + name + "_glow.png");
            assertEquals(texture.getWidth(), glow.getWidth());
            assertEquals(texture.getHeight(), glow.getHeight());
            int lit = 0;
            for (int y = 0; y < glow.getHeight(); y++) for (int x = 0; x < glow.getWidth(); x++) {
                if ((glow.getRGB(x, y) >>> 24) == 0) continue;
                lit++;
                assertTrue((texture.getRGB(x, y) >>> 24) != 0, name + " glows only where it is painted");
            }
            assertTrue(lit > 100, name + " has its fire and its light: " + lit);
        }
        assertEquals(256, image("/assets/corruption/textures/entity/belial.png").getHeight(), "the two angels share a texture as their models say");
        BufferedImage bar = image("/assets/corruption/textures/gui/colossus_boss_bar.png");
        int drawn = 0, fire = 0, fair = 0;
        for (int y = 72; y < 96; y++) for (int x = 192; x < 216; x++) {
            int pixel = bar.getRGB(x, y);
            if ((pixel >>> 24) == 0) continue;
            drawn++;
            int r = (pixel >> 16) & 255, g = (pixel >> 8) & 255, b = pixel & 255;
            if (r > 200 && g < 150 && b < 80) fire++;
            if (r > 200 && g > 180 && b > 160) fair++;
        }
        assertTrue(drawn > 200, "his emblem is on the boss bar, after Decarabia's: " + drawn);
        assertTrue(fire > 5 && fair > 5, "half fair, half fire: " + fair + " " + fire);
    }

    private static BufferedImage image(String path) throws Exception {
        try (InputStream in = BelialModelTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return ImageIO.read(in);
        }
    }
}
