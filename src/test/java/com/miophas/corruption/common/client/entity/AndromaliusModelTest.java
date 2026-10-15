package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.AndromaliusRules;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.model.geom.ModelPart;
import org.junit.jupiter.api.Test;

/**
 * Andromalius's baked model: it has every bone the rig poses, glowing pieces of its own, and each of his poses
 * moves what it should — the tail slithers, the hood spreads as he stares, the jaws open to spit, and he falls
 * forward in death. And the pictures he needs are there: his texture, his gaze, his emblem on the boss bar.
 */
class AndromaliusModelTest {
    private final ModelPart root = AndromaliusModel.createBodyLayer().bakeRoot();
    private final AndromaliusModel model = new AndromaliusModel(root);

    private void rest(float age) { model.pose(age, 0, 0, AndromaliusRules.Attack.NONE, 0, 0, 0, 0); }

    @Test void itBakesWithEveryBoneTheRigNeedsAndGlowingPieces() {
        assertEquals(AndromaliusModel.TAIL, model.tail.length);
        assertEquals(AndromaliusModel.NECK, model.neck.length);
        assertTrue(GeoModel.load(AndromaliusModel.GEOMETRY).bones().stream().filter(bone -> bone.name().endsWith("_glow")).count() >= 6,
                "his eyes, the third eye, his hood's marks and his embers glow");
        assertDoesNotThrow(() -> rest(0));
    }

    @Test void hisTailSlithersAndFasterAsHeMoves() {
        rest(0);
        float still = model.tail[4].yRot;
        rest(17);
        assertNotEquals(still, model.tail[4].yRot, 1e-4, "it never quite stops");
        // How far the tail swings either side of where it lies at rest, standing still and slithering.
        float idleLow = 9, idleHigh = -9, movingLow = 9, movingHigh = -9;
        for (int step = 0; step < 40; step++) {
            model.pose(step, step, 0, AndromaliusRules.Attack.NONE, 0, 0, 0, 0);
            idleLow = Math.min(idleLow, model.tail[6].yRot); idleHigh = Math.max(idleHigh, model.tail[6].yRot);
            model.pose(step, step, 1, AndromaliusRules.Attack.NONE, 0, 0, 0, 0);
            movingLow = Math.min(movingLow, model.tail[6].yRot); movingHigh = Math.max(movingHigh, model.tail[6].yRot);
        }
        assertTrue(movingHigh - movingLow > 2 * (idleHigh - idleLow), "slithering swings it wider: " + (idleHigh - idleLow) + " " + (movingHigh - movingLow));
    }

    @Test void heSpreadsHisHoodToStareAndOpensHisJawsToSpit() {
        rest(0);
        float left = model.leftHood.yRot, right = model.rightHood.yRot, jaw = model.jaw.xRot;
        assertEquals(left, -right, 1e-6, "the two halves of the hood mirror each other");
        model.pose(0, 0, 0, AndromaliusRules.Attack.GAZE, AndromaliusRules.GAZE_WINDUP - 4, 0, 0, 0);
        assertTrue(model.leftHood.yRot > left + .2F, "the left hood drawn forward round his head");
        assertTrue(model.rightHood.yRot < right - .2F, "and the right");
        assertTrue(model.leftHood.xScale > 1.1F, "and spread wide");
        model.pose(0, 0, 0, AndromaliusRules.Attack.VOLLEY, AndromaliusRules.VOLLEY_WINDUP, 0, 0, 0);
        assertTrue(model.jaw.xRot > jaw + .6F, "his jaws gape");
        rest(0);
        assertEquals(1, model.leftHood.xScale, 1e-6, "and all of it settles again");
    }

    @Test void heDrawsBackToBiteAndStrikesAndWhipsHisTailAcross() {
        rest(0);
        float neck = model.neck[1].xRot, head = model.head.xRot, jaw = model.jaw.xRot, tail = model.tail[2].yRot;
        model.pose(0, 0, 0, AndromaliusRules.Attack.BITE, AndromaliusRules.BITE_WINDUP - 1, 0, 0, 0);
        assertTrue(model.neck[1].xRot < neck - .15F && model.head.xRot < head - .2F, "drawn back to strike");
        model.pose(0, 0, 0, AndromaliusRules.Attack.BITE, AndromaliusRules.BITE_WINDUP + 1, 0, 0, 0);
        assertTrue(model.neck[1].xRot > neck + .25F, "then struck forward");
        assertTrue(model.jaw.xRot > jaw + .5F, "jaws wide");
        model.pose(0, 0, 0, AndromaliusRules.Attack.TAIL, AndromaliusRules.TAIL_WINDUP - 1, 0, 0, 0);
        assertTrue(model.tail[2].yRot > tail + .15F, "the tail drawn round one way");
        model.pose(0, 0, 0, AndromaliusRules.Attack.TAIL, AndromaliusRules.TAIL_WINDUP + 3, 0, 0, 0);
        assertTrue(model.tail[2].yRot < tail - .2F, "then whipped across the other");
    }

    @Test void heFallsForwardInDeath() {
        rest(0);
        float neck = model.neck[1].xRot, head = model.head.xRot;
        model.pose(0, 0, 0, AndromaliusRules.Attack.NONE, 0, 1, 0, 0);
        assertTrue(model.neck[1].xRot > neck + .4F && model.head.xRot > head + .3F, "his forebody lies forward");
    }

    @Test void thePicturesHeNeedsAreThere() throws Exception {
        assertSize("/assets/corruption/textures/entity/andromalius.png", 512, 256);
        assertSize("/assets/corruption/textures/misc/andromalius_gaze.png", 256, 256);
        BufferedImage bar = image("/assets/corruption/textures/gui/colossus_boss_bar.png");
        int drawn = 0;
        for (int y = 72; y < 96; y++) for (int x = 64; x < 88; x++) if ((bar.getRGB(x, y) >>> 24) != 0) drawn++;
        assertTrue(drawn > 150, "his emblem is on the boss bar, beside Baal's and Agares's");
        BufferedImage gaze = image("/assets/corruption/textures/misc/andromalius_gaze.png");
        int middle = gaze.getRGB(128, 141);
        assertTrue(((middle >> 16) & 255) < 40, "the pupil, at the eye's heart, is black");
        int iris = gaze.getRGB(112, 141);
        assertTrue(((iris >> 16) & 255) > 180 && ((iris >> 8) & 255) < 120, "the iris round it red");
    }

    private static BufferedImage image(String path) throws Exception {
        try (InputStream in = AndromaliusModelTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return ImageIO.read(in);
        }
    }
    private static void assertSize(String path, int width, int height) throws Exception {
        BufferedImage image = image(path);
        assertEquals(width, image.getWidth(), path);
        assertEquals(height, image.getHeight(), path);
    }
}
