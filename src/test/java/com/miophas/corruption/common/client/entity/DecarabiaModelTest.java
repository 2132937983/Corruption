package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.DecarabiaRules;
import com.miophas.corruption.core.demon.DecarabiaRules.Skill;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpinVolleyRules;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

/**
 * Decarabia's two baked models. The star turns its points on their wheel while his eye stays upright, and through a
 * volley they stand where the bolts leave from; it gathers itself to split, and folds away in his change. The goshawk's
 * primaries are each a bone of their own, splayed into fingers that close up in a stoop; he beats his wings, raises them
 * for his barrage, sweeps them back and reaches out his talons in a stoop, throws back his head to screech, and unfolds
 * out of nothing in his change. Every pose eases back to rest by itself once its skill is
 * over. And the pictures he needs are there: his texture, his glow over the same places, his emblem.
 */
class DecarabiaModelTest {
    private final DecarabiaStarModel star = new DecarabiaStarModel(DecarabiaStarModel.createBodyLayer().bakeRoot());
    private final DecarabiaHawkModel hawk = new DecarabiaHawkModel(DecarabiaHawkModel.createBodyLayer().bakeRoot());

    private void starAt(float spin, Skill skill, float ticks) { star.pose(0, spin, 0, skill, ticks, false, false, 0, 0, 0); }
    private void hawkAt(Skill skill, float ticks, float pitch) { hawk.pose(0, 0, skill, ticks, pitch, 0, 0, 0); }

    /** Where a point given in a bone's own pixels is, in the model's pixels, through the bones from the root down to it. */
    private static Vector3f at(ModelPart root, float x, float y, float z, ModelPart... chain) {
        PoseStack stack = new PoseStack();
        root.translateAndRotate(stack);
        for (ModelPart part : chain) part.translateAndRotate(stack);
        Vector4f point = stack.last().pose().transform(new Vector4f(x / 16, y / 16, z / 16, 1));
        return new Vector3f(point.x * 16, point.y * 16, point.z * 16);
    }
    /** The tip of one of the star's points, and its heart. */
    private Vector3f tip(int ray) { return at(star.root, 0, -20, 0, star.star, star.wheel, star.rays[ray]); }
    private Vector3f heart() { return at(star.root, 0, 0, 0, star.star); }
    /** The goshawk's left shoulder; the tip of one of that hand's primaries (the longest, unless said), in the model's pixels. */
    private Vector3f shoulder() { return at(hawk.root, 0, 0, 0, hawk.body, hawk.wing[0]); }
    private Vector3f wingTip() { return primaryTip(0, 2); }
    private Vector3f primaryTip(int side, int index) {
        // Each primary is built from its root out along the wing (-X for the first wing, +X for the other), 12 to 15 long.
        float length = new float[]{12, 14, 15, 15, 14, 12}[index];
        return at(hawk.root, side == 0 ? -length : length, 0, 0, hawk.body, hawk.wing[side], hawk.wingFore[side], hawk.wingHand[side], hawk.primaries[side][index]);
    }
    /** Which way one of the primaries points, from its own root to its tip. */
    private Vector3f primaryWay(int side, int index) {
        Vector3f root = at(hawk.root, 0, 0, 0, hawk.body, hawk.wing[side], hawk.wingFore[side], hawk.wingHand[side], hawk.primaries[side][index]);
        return primaryTip(side, index).sub(root).normalize();
    }
    /** How far apart the outermost and innermost primaries point, in degrees. */
    private double fingersSpread(int side) {
        return Math.toDegrees(primaryWay(side, 0).angle(primaryWay(side, DecarabiaHawkModel.PRIMARIES - 1)));
    }

    private static float[] pose(ModelPart[] parts) {
        float[] all = new float[parts.length * 9];
        for (int index = 0; index < parts.length; index++) {
            ModelPart part = parts[index];
            System.arraycopy(new float[]{part.x, part.y, part.z, part.xRot, part.yRot, part.zRot, part.xScale, part.yScale, part.zScale}, 0, all, index * 9, 9);
        }
        return all;
    }

    @Test void bothBakeWithEveryBoneTheirRigsNeed() {
        for (Skill skill : Skill.values()) for (float ticks = 0; ticks < 120; ticks += 7) {
            float at = ticks;
            assertDoesNotThrow(() -> star.pose(at, at * 13, at * 4, skill, at, at % 2 == 0, at % 3 == 0, at % 120 / 120, 0, 3), skill.name());
            assertDoesNotThrow(() -> hawk.pose(at, at * .3F, skill, at, at % 80 - 20, at % 120 / 120, 0, 3), skill.name());
        }
        hawkAt(Skill.NONE, 0, 0);
        assertEquals(hawk.wing[0].x, -hawk.wing[1].x, 1e-6, "his wings mirror each other");
        assertEquals(hawk.wing[0].zRot, -hawk.wing[1].zRot, 1e-6);
        assertEquals(hawk.wing[0].yRot, -hawk.wing[1].yRot, 1e-6);
        for (int index = 0; index < DecarabiaHawkModel.PRIMARIES; index++) {
            assertEquals(hawk.primaries[0][index].yRot, -hawk.primaries[1][index].yRot, 1e-6, "and so do their primaries");
            assertEquals(primaryTip(0, index).x, -primaryTip(1, index).x, 1e-3);
            assertEquals(primaryTip(0, index).z, primaryTip(1, index).z, 1e-3);
        }
        assertTrue(GeoModel.load(DecarabiaHawkModel.GEOMETRY).bones().size() > 12 + 2 * DecarabiaHawkModel.PRIMARIES - 1, "a bone for each primary");
    }

    @Test void hisPrimariesSplayIntoFingersAndCloseUpInAStoop() {
        hawkAt(Skill.NONE, 0, 0);
        Vector3f root = at(hawk.root, 0, 0, 0, hawk.body, hawk.wing[0], hawk.wingFore[0], hawk.wingHand[0]);
        for (int index = 1; index < DecarabiaHawkModel.PRIMARIES; index++)
            assertTrue(primaryTip(0, index).z > primaryTip(0, index - 1).z, "each further in is swept further back: " + index);
        assertTrue(primaryTip(0, 0).x < root.x - 10, "the outermost reaches out along the wing");
        double open = fingersSpread(0);
        assertTrue(open > 35, "splayed into fingers: " + open);
        assertTrue(hawk.primaries[0][0].zRot > hawk.primaries[0][DecarabiaHawkModel.PRIMARIES - 1].zRot && hawk.primaries[0][0].zRot > 0,
                "their tips bent up off the hand as they bear him, the outermost most");
        hawkAt(Skill.DIVE, DecarabiaRules.DIVE.timing().windup() + 5, 70);
        double closed = fingersSpread(0);
        assertTrue(closed < open / 3, "drawn together in a stoop: " + closed + " against " + open);
        assertEquals(closed, fingersSpread(1), 1e-3, "the same on both hands");
    }

    @Test void theStarsPointsTurnOnTheirWheelWhileHisEyeStaysUpright() {
        starAt(0, Skill.NONE, 0);
        Vector3f up = tip(0), heart = heart();
        assertTrue(up.y < heart.y - 15 && Math.abs(up.x - heart.x) < .5F, "at rest a point stands straight up: " + up);
        starAt(90, Skill.NONE, 0);
        Vector3f turned = tip(0);
        assertTrue(turned.x < heart.x - 15 && Math.abs(turned.y - heart.y) < 1, "a quarter turn takes it to his right, which is -X: " + turned);
        assertEquals(0, star.star.zRot, 1e-6, "his eye does not turn with them");
    }

    @Test void throughAVolleyHisPointsStandWhereItsBoltsLeaveFrom() {
        SpinVolleyRules volley = DecarabiaRules.VOLLEY;
        for (int elapsed = volley.timing().windup(); elapsed < volley.timing().ticks(); elapsed += volley.interval()) {
            float turned = (float) volley.turned(elapsed);
            starAt(turned, Skill.VOLLEY, elapsed);
            Vector3f heart = heart();
            for (int point = 0; point < volley.points(); point++) {
                // Where the bolt leaves from about his face: {right, up}; his right is -X and up -Y in the model.
                double[] from = SpinVolleyRules.tip(volley.pointAt(point, turned));
                double best = Double.MAX_VALUE;
                for (int ray = 0; ray < 5; ray++) {
                    Vector3f tip = tip(ray);
                    double right = -(tip.x - heart.x), up = -(tip.y - heart.y), length = Math.hypot(right, up);
                    best = Math.min(best, Math.hypot(right / length - from[0], up / length - from[1]));
                }
                assertTrue(best < .02, "a point stands where bolt " + point + " leaves at tick " + elapsed + ": " + best);
            }
        }
    }

    @Test void gatheringHimselfToSplitHisPointsStretchAndHeLeansIntoHisSpin() {
        starAt(0, Skill.NONE, 0);
        float reach = tip(0).distance(heart());
        starAt(0, Skill.SPLIT, DecarabiaRules.SPLIT.windup() - 1);
        assertTrue(tip(0).distance(heart()) > reach * 1.15F, "his points reach further as he gathers himself");
        starAt(0, Skill.SPIN, DecarabiaRules.SPIN.timing().windup() + 4);
        assertTrue(star.star.xRot > .3F, "leaning in as he spins at his quarry");
    }

    @Test void inHisChangeTheStarFoldsAwayAndTheGoshawkUnfoldsOutOfNothing() {
        star.pose(0, 0, 0, Skill.NONE, 0, false, false, .5F, 0, 0);
        for (ModelPart ray : star.rays) assertFalse(ray.visible, "by its middle his points are folded away");
        assertFalse(star.ring.visible, "and his ring closed up");
        assertTrue(star.star.xScale < .5F, "the core dwindling");
        hawk.pose(0, 0, Skill.NONE, 0, 0, .5F, 0, 0);
        assertTrue(hawk.body.xScale < .05F, "the goshawk is nothing yet at its middle");
        hawk.pose(0, 0, Skill.NONE, 0, 0, .85F, 0, 0);
        assertTrue(hawk.body.xScale > .7F && hawk.wing[0].xScale < hawk.body.xScale, "his body grows, his wings after it");
        hawk.pose(0, 0, Skill.NONE, 0, 0, 1, 0, 0);
        assertEquals(1, hawk.body.xScale, 1e-6);
        assertEquals(1, hawk.wing[0].xScale, 1e-6, "and he is whole by its end");
    }

    @Test void inAStoopHisWingsSweepBackAndHisTalonsReachOut() {
        hawkAt(Skill.NONE, 0, 0);
        Vector3f spread = wingTip();
        float tucked = hawk.leg[0].xRot;
        hawkAt(Skill.DIVE, DecarabiaRules.DIVE.timing().windup() + 5, 70);
        Vector3f swept = wingTip(), shoulder = shoulder();
        assertTrue(swept.z - shoulder.z > 25, "his wing laid back along him: " + swept);
        assertTrue(Math.abs(swept.x) < Math.abs(spread.x) - 15, "not spread wide: " + swept + " against " + spread);
        assertTrue(hawk.leg[0].xRot < 0 && tucked > 0, "his talons out before him, not tucked back");
    }

    @Test void atHisBarrageHeRaisesHisWingsHighAndToScreechHeThrowsBackHisHead() {
        hawkAt(Skill.NONE, 0, 0);
        Vector3f gliding = wingTip();
        SkillTiming barrage = DecarabiaRules.BARRAGE.timing();
        hawkAt(Skill.BARRAGE, barrage.windup() - 1, 0);
        assertTrue(wingTip().y < gliding.y - 12, "his wings raised high: " + wingTip() + " against " + gliding);
        SkillTiming screech = DecarabiaRules.SCREECH.timing();
        hawkAt(Skill.SCREECH, screech.windup() - 1, 0);
        assertTrue(hawk.head.xRot < -.4F, "his head thrown back to draw breath");
        hawkAt(Skill.SCREECH, screech.windup() + 3, 0);
        assertTrue(hawk.jaw.xRot > .7F, "and his beak wide as he cries out");
    }

    @Test void everyPoseEasesBackToRestOnceItsSkillIsOver() {
        starAt(0, Skill.NONE, 0);
        float[] starRest = pose(star.all);
        hawkAt(Skill.NONE, 0, 0);
        float[] hawkRest = pose(hawk.all);
        for (Skill skill : Skill.values()) {
            starAt(0, skill, 200);
            assertArrayEquals(starRest, pose(star.all), 1e-4F, skill + " settles the star back to rest");
            hawkAt(skill, 400, 0);
            assertArrayEquals(hawkRest, pose(hawk.all), 1e-4F, skill + " settles the goshawk back to rest");
        }
    }

    @Test void hisWingsBeatHardestAtHisBarrageAndNotAtAllOnceHeIsDead() {
        SkillTiming barrage = DecarabiaRules.BARRAGE.timing();
        assertTrue(DecarabiaHawkModel.flapRate(Skill.BARRAGE, barrage.windup() + 2, false) > 2 * DecarabiaHawkModel.GLIDE);
        assertTrue(DecarabiaHawkModel.flapRate(Skill.DIVE, 2, false) > DecarabiaHawkModel.GLIDE, "climbing for a stoop");
        assertEquals(DecarabiaHawkModel.GLIDE, DecarabiaHawkModel.flapRate(Skill.SCREECH, 400, false), "back to gliding after");
        assertTrue(DecarabiaHawkModel.flapRate(Skill.NONE, 0, true) < .1F);
    }

    @Test void thePicturesHeNeedsAreThere() throws Exception {
        BufferedImage texture = image("/assets/corruption/textures/entity/decarabia.png"), glow = image("/assets/corruption/textures/entity/decarabia_glow.png");
        assertEquals(256, texture.getWidth());
        assertEquals(128, texture.getHeight());
        assertEquals(texture.getWidth(), glow.getWidth());
        assertEquals(texture.getHeight(), glow.getHeight());
        int lit = 0;
        for (int y = 0; y < glow.getHeight(); y++) for (int x = 0; x < glow.getWidth(); x++) {
            if ((glow.getRGB(x, y) >>> 24) == 0) continue;
            lit++;
            assertTrue((texture.getRGB(x, y) >>> 24) != 0, "glowing only where he is painted");
        }
        assertTrue(lit > 50, "his eye, the star's rim and runes and the goshawk's eyes glow: " + lit);
        BufferedImage bar = image("/assets/corruption/textures/gui/colossus_boss_bar.png");
        int drawn = 0, gold = 0;
        for (int y = 72; y < 96; y++) for (int x = 160; x < 184; x++) {
            int pixel = bar.getRGB(x, y);
            if ((pixel >>> 24) == 0) continue;
            drawn++;
            if (((pixel >> 16) & 255) > (pixel & 255) + 60) gold++;
        }
        assertTrue(drawn > 150, "his emblem is on the boss bar, after Seere's");
        assertTrue(gold > 20, "in his own gold");
    }

    private static BufferedImage image(String path) throws Exception {
        try (InputStream in = DecarabiaModelTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return ImageIO.read(in);
        }
    }
}
