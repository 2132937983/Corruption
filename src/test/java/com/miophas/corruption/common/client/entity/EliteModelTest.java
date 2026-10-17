package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.entity.CorruptedAberration;
import com.miophas.corruption.common.entity.CorruptedLurker;
import com.miophas.corruption.common.entity.CorruptedScreecher;
import com.miophas.corruption.core.elite.AberrationRules;
import com.miophas.corruption.core.elite.LurkerRules;
import com.miophas.corruption.core.elite.ScreecherRules;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

/**
 * The elites' baked models: each poses through every skill and every pose eases back to rest by itself; its two sides
 * mirror each other at rest; the aberration stands on its feet; what each spits leaves it at its model's mouth; the
 * aberration's legs step three at a time, the screecher's wings beat and fold, the lurker's tentacles ripple in the water
 * and hang limp out of it; and the pictures they need are there.
 */
class EliteModelTest {
    private final CorruptedAberrationModel aberration = new CorruptedAberrationModel(CorruptedAberrationModel.createBodyLayer().bakeRoot());
    private final CorruptedScreecherModel screecher = new CorruptedScreecherModel(CorruptedScreecherModel.createBodyLayer().bakeRoot());
    private final CorruptedLurkerModel lurker = new CorruptedLurkerModel(CorruptedLurkerModel.createBodyLayer().bakeRoot());

    /** The corners of every cube of a model whose bone's path (as ModelPart.visit gives it) passes, in the model's pixels. */
    private static List<Vector3f> corners(ModelPart root, Predicate<String> path) {
        List<Vector3f> found = new ArrayList<>();
        root.visit(new PoseStack(), (pose, at, index, cube) -> {
            if (!path.test(at)) return;
            for (float x : new float[]{cube.minX, cube.maxX}) for (float y : new float[]{cube.minY, cube.maxY}) for (float z : new float[]{cube.minZ, cube.maxZ}) {
                Vector3f corner = pose.pose().transformPosition(x / 16, y / 16, z / 16, new Vector3f());
                found.add(corner.mul(16));
            }
        });
        return found;
    }

    /** A place in a model's pixels as the entity measures it: {right, up, ahead} in blocks from its feet. */
    private static double[] frame(Vector3f model) { return new double[]{-model.x / 16, (24 - model.y) / 16, -model.z / 16}; }

    /** The middle of the foremost of some corners (within a pixel of the foremost), in the entity's frame. */
    private static double[] foremost(List<Vector3f> corners) {
        float front = (float) corners.stream().mapToDouble(corner -> corner.z).min().orElseThrow();
        Vector3f sum = new Vector3f();
        int count = 0;
        for (Vector3f corner : corners) if (corner.z <= front + 1) { sum.add(corner); count++; }
        return frame(sum.div(count));
    }

    private static void near(double[] expected, double[] actual, double within, String what) {
        double distance = Math.sqrt(Math.pow(expected[0] - actual[0], 2) + Math.pow(expected[1] - actual[1], 2) + Math.pow(expected[2] - actual[2], 2));
        assertTrue(distance <= within, String.format("%s: the server has {%.2f, %.2f, %.2f}, the model {%.2f, %.2f, %.2f}", what, expected[0], expected[1],
                expected[2], actual[0], actual[1], actual[2]));
    }

    private static float[] pose(ModelPart[] parts) {
        float[] all = new float[parts.length * 9];
        for (int index = 0; index < parts.length; index++) {
            ModelPart part = parts[index];
            System.arraycopy(new float[]{part.x, part.y, part.z, part.xRot, part.yRot, part.zRot, part.xScale, part.yScale, part.zScale}, 0, all, index * 9, 9);
        }
        return all;
    }

    @Test void allThreeBakeAndPoseThroughEverySkill() {
        for (AberrationRules.Skill skill : AberrationRules.Skill.values()) for (float ticks = 0; ticks < 60; ticks += 3) {
            float at = ticks, blend = ticks % 20 / 20;
            assertDoesNotThrow(() -> aberration.pose(at, .8F, at, 20, -10, skill.ordinal(), at, blend, blend, blend, blend, at % 12, at % 2 == 0), skill.name());
        }
        for (ScreecherRules.Skill skill : ScreecherRules.Skill.values()) for (float ticks = 0; ticks < 60; ticks += 3) {
            float at = ticks, blend = ticks % 20 / 20;
            assertDoesNotThrow(() -> screecher.pose(.6F, at, 15, 5, skill.ordinal(), at, blend, blend, blend, blend, blend), skill.name());
        }
        for (LurkerRules.Skill skill : LurkerRules.Skill.values()) for (float ticks = 0; ticks < 60; ticks += 3) {
            float at = ticks, blend = ticks % 20 / 20;
            assertDoesNotThrow(() -> lurker.pose(.5F, at, blend, skill.ordinal(), at, at % 12, blend, blend), skill.name());
        }
    }

    @Test void everyPoseEasesBackToRestOnceItsSkillIsOver() {
        aberration.pose(0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, false);
        float[] rest = pose(aberration.all);
        aberration.pose(0, 0, 0, 0, 0, AberrationRules.Skill.ACID.ordinal(), 200, 0, 0, 0, 0, 100, false);
        assertArrayEquals(rest, pose(aberration.all), 1e-5F, "long after its acid, with every blend gone, it is at rest");
        screecher.pose(0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0);
        float[] still = pose(screecher.all);
        screecher.pose(0, 0, 0, 0, ScreecherRules.Skill.FEATHERS.ordinal(), 200, 0, 0, 0, 0, 0);
        assertArrayEquals(still, pose(screecher.all), 1e-5F);
        lurker.pose(0, 0, 1, 0, 100, 100, 0, 0);
        float[] drifting = pose(lurker.all);
        lurker.pose(0, 0, 1, LurkerRules.Skill.BOLTS.ordinal(), 200, 100, 0, 0);
        assertArrayEquals(drifting, pose(lurker.all), 1e-5F);
    }

    @Test void theirSidesMirrorEachOtherAtRest() {
        aberration.pose(0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, false);
        for (int leg = 0; leg < 3; leg++) {
            assertEquals(aberration.legs[0][leg].zRot, -aberration.legs[1][leg].zRot, 1e-6);
            assertEquals(aberration.legs[0][leg].yRot, -aberration.legs[1][leg].yRot, 1e-6);
        }
        screecher.pose(0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0);
        for (int piece = 0; piece < 3; piece++) assertEquals(screecher.wings[0][piece].zRot, -screecher.wings[1][piece].zRot, 1e-6);
        lurker.pose(0, 0, 1, 0, 100, 100, 0, 0);
        assertEquals(lurker.fins[0].zRot, -lurker.fins[1].zRot, 1e-6);
    }

    @Test void theAberrationStandsOnItsFeetAndStepsThreeAtATime() {
        aberration.pose(0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, false);
        double lowest = corners(aberration.root, path -> true).stream().mapToDouble(corner -> (24 - corner.y) / 16).min().orElseThrow();
        assertEquals(0, lowest, .12, "its feet on the ground");
        aberration.pose(1.3F, 1, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, false);
        float first = aberration.legs[0][0].yRot, middle = aberration.legs[0][1].yRot, last = aberration.legs[0][2].yRot;
        aberration.pose(0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, false);
        float restFirst = aberration.legs[0][0].yRot, restMiddle = aberration.legs[0][1].yRot, restLast = aberration.legs[0][2].yRot;
        assertTrue((first - restFirst) * (middle - restMiddle) < 0, "the middle leg of a side swings against the first");
        assertTrue((first - restFirst) * (last - restLast) > 0, "and the last with it");
        aberration.pose(0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 5, false);
        assertTrue(aberration.opened > .9F, "its maw parts to bite");
        aberration.pose(0, 0, 0, 0, 0, AberrationRules.Skill.ROAR.ordinal(), 15, 0, 0, 0, 1, 100, true);
        assertEquals(1, aberration.opened, 1e-6, "and gapes as it roars");
        assertTrue(aberration.crystals.xScale > 1.05F, "berserk, its crystals swell");
    }

    @Test void whatEachSpitsLeavesItAtItsModelsMouth() {
        aberration.pose(0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, false);
        near(CorruptedAberration.MAW, foremost(corners(aberration.root, path -> path.contains("maw_"))), .25, "the aberration's maw");
        screecher.pose(0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0);
        near(CorruptedScreecher.BEAK, foremost(corners(screecher.root, path -> path.endsWith("/head"))), .3, "the screecher's beak");
        lurker.pose(0, 0, 1, 0, 100, 100, 0, 0);
        double[] lip = foremost(corners(lurker.root, path -> path.equals("/body"))), jaw = foremost(corners(lurker.root, path -> path.endsWith("/jaw")));
        double[] mouth = {0, (lip[1] + jaw[1]) / 2 - .1, (lip[2] + jaw[2]) / 2};
        near(CorruptedLurker.MAW, mouth, .3, "the lurker's maw");
    }

    @Test void theScreechersWingsBeatAndFoldAndItsBeakOpensToScream() {
        screecher.pose(0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0);
        float rest = screecher.wings[0][0].zRot;
        screecher.pose(0, 5, 0, 0, 0, 100, 0, 0, 0, 0, 0);
        assertNotEquals(rest, screecher.wings[0][0].zRot, 1e-3F, "its wings beat");
        screecher.pose(0, 0, 0, 0, 0, 100, 0, 1, 0, 0, 0);
        assertTrue(Math.abs(screecher.wings[0][0].yRot) > 1, "folded back to dive");
        screecher.pose(0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0);
        float shut = screecher.jaw.xRot;
        screecher.pose(0, 0, 0, 0, ScreecherRules.Skill.SCREECH.ordinal(), 20, 0, 0, 0, 0, 1);
        assertTrue(screecher.jaw.xRot > shut + .5F, "its beak wide as it screams");
    }

    @Test void theLurkersTentaclesRippleInTheWaterAndHangLimpOutOfIt() {
        lurker.pose(.5F, 10, 1, 0, 100, 100, 0, 0);
        float wet = lurker.tentacles[0][0][0].xRot, tip = lurker.tentacles[0][0][2].yRot;
        lurker.pose(.5F, 16, 1, 0, 100, 100, 0, 0);
        assertNotEquals(tip, lurker.tentacles[0][0][2].yRot, 1e-3F, "they ripple");
        lurker.pose(.5F, 10, 1, 0, 100, 100, 0, 0);
        lurker.pose(0, 10, 0, 0, 100, 100, 0, 0);
        assertTrue(lurker.tentacles[0][0][0].xRot < wet - .1F, "out of the water they droop");
        lurker.pose(0, 10, 1, LurkerRules.Skill.GRAB.ordinal(), 5, 100, 0, 1);
        assertTrue(lurker.arms[0][0].xRot < -2, "its arms reach forward for what it grabs");
        assertFalse(lurker.lash.visible || lurker.lashClub.visible, "its lash is drawn only along the lash");
    }

    @Test void thePicturesTheyNeedAreThere() throws Exception {
        for (String name : List.of("corrupted_aberration", "corrupted_screecher", "corrupted_lurker")) {
            for (String picture : List.of(name, name + "_glow")) {
                try (InputStream in = getClass().getResourceAsStream("/assets/corruption/textures/entity/" + picture + ".png")) {
                    assertNotNull(in, picture);
                    BufferedImage image = ImageIO.read(in);
                    assertEquals(128, image.getWidth(), picture);
                }
            }
        }
        try (InputStream in = getClass().getResourceAsStream("/assets/corruption/textures/entity/corrupted_feather.png")) {
            assertNotNull(in, "the screecher's feather");
        }
    }
}
