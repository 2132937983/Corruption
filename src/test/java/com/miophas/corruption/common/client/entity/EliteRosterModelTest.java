package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.entity.CorruptedBloater;
import com.miophas.corruption.common.entity.CorruptedBrute;
import com.miophas.corruption.common.entity.CorruptedCrawler;
import com.miophas.corruption.common.entity.CorruptedDrifter;
import com.miophas.corruption.common.entity.CorruptedGazer;
import com.miophas.corruption.common.entity.CorruptedHivemother;
import com.miophas.corruption.common.entity.CorruptedHowler;
import com.miophas.corruption.common.entity.CorruptedMaw;
import com.miophas.corruption.common.entity.CorruptedMortar;
import com.miophas.corruption.common.entity.CorruptedNaiad;
import com.miophas.corruption.common.entity.CorruptedStalker;
import com.miophas.corruption.core.elite.BloaterRules;
import com.miophas.corruption.core.elite.BruteRules;
import com.miophas.corruption.core.elite.CrawlerRules;
import com.miophas.corruption.core.elite.DrifterRules;
import com.miophas.corruption.core.elite.GazerRules;
import com.miophas.corruption.core.elite.HivemotherRules;
import com.miophas.corruption.core.elite.HowlerRules;
import com.miophas.corruption.core.elite.MawRules;
import com.miophas.corruption.core.elite.MortarRules;
import com.miophas.corruption.core.elite.NaiadRules;
import com.miophas.corruption.core.elite.StalkerRules;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

/**
 * The eleven newer elites' baked models: each poses through every skill and eases back to rest by itself once its skill
 * is over; their left and right sides mirror each other as built; what each shoots leaves it where its model shoots it
 * from; each moves as it should (the brute's legs step by turns and its club comes up overhead to slam, the bloater
 * swells to vent, the crawler's legs ripple, the hivemother's wings beat, the drifter's tendrils gather in to lift, the
 * maw's lips gape, the naiad lets its spear go and lies forward to swim); and the pictures they need are there.
 */
class EliteRosterModelTest {
    private final CorruptedBruteModel brute = new CorruptedBruteModel(CorruptedBruteModel.createBodyLayer().bakeRoot());
    private final CorruptedHowlerModel howler = new CorruptedHowlerModel(CorruptedHowlerModel.createBodyLayer().bakeRoot());
    private final CorruptedStalkerModel stalker = new CorruptedStalkerModel(CorruptedStalkerModel.createBodyLayer().bakeRoot());
    private final CorruptedBloaterModel bloater = new CorruptedBloaterModel(CorruptedBloaterModel.createBodyLayer().bakeRoot());
    private final CorruptedMortarModel mortar = new CorruptedMortarModel(CorruptedMortarModel.createBodyLayer().bakeRoot());
    private final CorruptedCrawlerModel crawler = new CorruptedCrawlerModel(CorruptedCrawlerModel.createBodyLayer().bakeRoot());
    private final CorruptedGazerModel gazer = new CorruptedGazerModel(CorruptedGazerModel.createBodyLayer().bakeRoot());
    private final CorruptedHivemotherModel hive = new CorruptedHivemotherModel(CorruptedHivemotherModel.createBodyLayer().bakeRoot());
    private final CorruptedDrifterModel drifter = new CorruptedDrifterModel(CorruptedDrifterModel.createBodyLayer().bakeRoot());
    private final CorruptedMawModel maw = new CorruptedMawModel(CorruptedMawModel.createBodyLayer().bakeRoot());
    private final CorruptedNaiadModel naiad = new CorruptedNaiadModel(CorruptedNaiadModel.createBodyLayer().bakeRoot());

    /** Each model, with how many skills its elite has. */
    private Map<PosedEliteModel<?>, Integer> all() {
        return Map.ofEntries(Map.entry(brute, BruteRules.SKILLS.size()), Map.entry(howler, HowlerRules.SKILLS.size()), Map.entry(stalker, StalkerRules.SKILLS.size()),
                Map.entry(bloater, BloaterRules.SKILLS.size()), Map.entry(mortar, MortarRules.SKILLS.size()), Map.entry(crawler, CrawlerRules.SKILLS.size()),
                Map.entry(gazer, GazerRules.SKILLS.size()), Map.entry(hive, HivemotherRules.SKILLS.size()), Map.entry(drifter, DrifterRules.SKILLS.size()),
                Map.entry(maw, MawRules.SKILLS.size()), Map.entry(naiad, NaiadRules.SKILLS.size()));
    }

    /** How far into each skill's pose: this skill all the way, the rest not at all. */
    private static float[] on(int skills, int skill, float how) {
        float[] on = new float[skills + 1];
        if (skill > 0) on[skill] = how;
        return on;
    }
    /** At rest: standing, looking ahead, long after any skill or blow. */
    private static void rest(PosedEliteModel<?> model, int skills, int posed, float state) {
        model.pose(0, 0, 0, 0, 0, posed, 200, new float[skills + 1], 100, state);
    }
    /** In the midst of one skill, this many ticks into it, all the way into its pose. */
    private static void in(PosedEliteModel<?> model, int skills, int skill, float since) {
        model.pose(0, 0, 0, 0, 0, skill, since, on(skills, skill, 1), 100, 0);
    }

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
        assertFalse(found.isEmpty(), "some bone passes");
        return found;
    }
    /** A place in a model's pixels as the entity measures it: {right, up, ahead} in blocks from its feet. */
    private static double[] frame(Vector3f model) { return new double[]{-model.x / 16, (24 - model.y) / 16, -model.z / 16}; }
    /** The middle of those corners within a pixel of the furthest along some way, in the entity's frame. */
    private static double[] furthest(List<Vector3f> corners, java.util.function.ToDoubleFunction<Vector3f> along) {
        double most = corners.stream().mapToDouble(along).max().orElseThrow();
        Vector3f sum = new Vector3f();
        int count = 0;
        for (Vector3f corner : corners) if (along.applyAsDouble(corner) >= most - 1) { sum.add(corner); count++; }
        return frame(sum.div(count));
    }
    private static double[] foremost(List<Vector3f> corners) { return furthest(corners, corner -> -corner.z); }
    private static double[] lowest(List<Vector3f> corners) { return furthest(corners, corner -> corner.y); }
    private static double[] highest(List<Vector3f> corners) { return furthest(corners, corner -> -corner.y); }
    private static double[] middle(List<Vector3f> corners) {
        Vector3f sum = new Vector3f();
        corners.forEach(sum::add);
        return frame(sum.div(corners.size()));
    }
    /** Where the server has something nearly where the model has it; if not, why not, added to what is off. */
    private static void near(double[] expected, double[] actual, double within, String what, List<String> off) {
        double distance = Math.sqrt(Math.pow(expected[0] - actual[0], 2) + Math.pow(expected[1] - actual[1], 2) + Math.pow(expected[2] - actual[2], 2));
        if (distance > within) off.add(String.format("%s: the server has {%.2f, %.2f, %.2f}, the model {%.2f, %.2f, %.2f}", what, expected[0], expected[1],
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

    @Test void eachBakesAndPosesThroughEverySkillAndEveryTick() {
        all().forEach((model, skills) -> {
            for (int skill = 0; skill <= skills; skill++) for (float ticks = 0; ticks < 90; ticks += 2.5F) {
                int posed = skill;
                float at = ticks, blend = ticks % 20 / 20;
                assertDoesNotThrow(() -> model.pose(at * .3F, .8F, at, 20, -10, posed, at, on(skills, posed, blend), at % 12, blend), model + " " + posed);
                for (float value : pose(model.all)) assertTrue(Float.isFinite(value), model + " stays finite in skill " + posed + " at " + at);
            }
        });
    }

    @Test void everyPoseEasesBackToRestOnceItsSkillIsOver() {
        all().forEach((model, skills) -> {
            for (float state : new float[]{0, 1}) {
                rest(model, skills, 0, state);
                float[] still = pose(model.all);
                for (int skill = 1; skill <= skills; skill++) {
                    rest(model, skills, skill, state);
                    assertArrayEquals(still, pose(model.all), 1e-5F, model + ": long after skill " + skill + ", with every blend gone, it is at rest");
                }
            }
        });
    }

    @Test void theirLeftAndRightSidesMirrorEachOtherAsBuilt() {
        all().forEach((model, skills) -> {
            model.reset();
            int pairs = 0;
            for (Map.Entry<String, ModelPart> bone : model.bones.entrySet()) {
                if (!bone.getKey().contains("left")) continue;
                ModelPart left = bone.getValue(), right = model.bones.get(bone.getKey().replace("left", "right"));
                assertNotNull(right, model + ": " + bone.getKey() + " has a right to it");
                String what = model + " " + bone.getKey();
                assertEquals(left.x, -right.x, 1e-4, what);
                assertEquals(left.y, right.y, 1e-4, what);
                assertEquals(left.z, right.z, 1e-4, what);
                assertEquals(left.xRot, right.xRot, 1e-4, what);
                assertEquals(left.yRot, -right.yRot, 1e-4, what);
                assertEquals(left.zRot, -right.zRot, 1e-4, what);
                pairs++;
            }
            assertTrue(pairs > 0, model + " has sides");
        });
    }

    @Test void whatEachShootsLeavesItWhereItsModelShootsFrom() {
        List<String> off = new ArrayList<>();
        int skills = BruteRules.SKILLS.size();
        in(brute, skills, BruteRules.Skill.HURL.ordinal(), BruteRules.HURL.timing().windup());
        near(CorruptedBrute.MOUTH, foremost(corners(brute.root, path -> path.contains("club_fore"))), .35, "the brute's club as it lets fly", off);
        in(howler, HowlerRules.SKILLS.size(), HowlerRules.Skill.BILE.ordinal(), HowlerRules.BILE.timing().windup());
        near(CorruptedHowler.MOUTH, foremost(corners(howler.root, path -> path.contains("/head"))), .3, "the howler's jaws", off);
        in(stalker, StalkerRules.SKILLS.size(), StalkerRules.Skill.SPINES.ordinal(), StalkerRules.SPINES.timing().windup());
        near(CorruptedStalker.MOUTH, foremost(corners(stalker.root, path -> path.endsWith("/head"))), .3, "the stalker's head", off);
        in(bloater, BloaterRules.SKILLS.size(), BloaterRules.Skill.LOB.ordinal(), BloaterRules.LOB.timing().windup());
        near(CorruptedBloater.MOUTH, foremost(corners(bloater.root, path -> path.endsWith("/head"))), .3, "the bloater's mouth", off);
        rest(mortar, MortarRules.SKILLS.size(), 0, 0);
        near(CorruptedMortar.MOUTH, highest(corners(mortar.root, path -> path.endsWith("/cannon"))), .3, "the mortar's tube", off);
        rest(crawler, CrawlerRules.SKILLS.size(), 0, 0);
        near(CorruptedCrawler.MOUTH, foremost(corners(crawler.root, path -> path.startsWith("/head"))), .3, "the crawler's mandibles", off);
        rest(gazer, GazerRules.SKILLS.size(), 0, 0);
        near(CorruptedGazer.EYE, foremost(corners(gazer.root, path -> path.equals("/body"))), .3, "the gazer's great eye", off);
        in(hive, HivemotherRules.SKILLS.size(), HivemotherRules.Skill.STINGERS.ordinal(), HivemotherRules.STINGERS.timing().windup());
        near(CorruptedHivemother.STINGER, lowest(corners(hive.root, path -> path.endsWith("/tail"))), .3, "the hivemother's sting", off);
        rest(drifter, DrifterRules.SKILLS.size(), 0, 0);
        near(CorruptedDrifter.UNDER, lowest(corners(drifter.root, path -> path.equals("/bell"))), .3, "under the drifter's bell", off);
        rest(maw, MawRules.SKILLS.size(), 0, 1);
        near(CorruptedMaw.MOUTH, foremost(corners(maw.root, path -> path.contains("/lip_"))), .3, "the maw's lips", off);
        in(naiad, NaiadRules.SKILLS.size(), NaiadRules.Skill.SPEAR.ordinal(), NaiadRules.SPEAR.timing().windup() - .01F);
        near(CorruptedNaiad.MOUTH, middle(corners(naiad.root, path -> path.endsWith("/spear"))), .35, "the naiad's spear as it lets fly", off);
        assertTrue(off.isEmpty(), String.join("\n", off));
    }

    @Test void theBrutesLegsStepByTurnsAndItsClubComesUpOverheadToSlam() {
        int skills = BruteRules.SKILLS.size();
        rest(brute, skills, 0, 0);
        float left = brute.legLeft.xRot, right = brute.legRight.xRot;
        brute.pose(1, 1, 0, 0, 0, 0, 200, new float[skills + 1], 100, 0);
        assertTrue((brute.legLeft.xRot - left) * (brute.legRight.xRot - right) < 0, "one leg forward as the other goes back");
        int up = BruteRules.SLAM.timing().windup();
        in(brute, skills, BruteRules.Skill.SLAM.ordinal(), up - .01F);
        assertTrue(brute.club.xRot < -2.5F, "its club raised overhead");
        in(brute, skills, BruteRules.Skill.SLAM.ordinal(), up + 4);
        assertTrue(brute.club.xRot > -1.2F && brute.club.xRot < -.4F, "and brought down before it");
    }

    @Test void theHowlerThrowsItsHeadBackAndTheStalkerGoesLowFaded() {
        int skills = HowlerRules.SKILLS.size();
        rest(howler, skills, 0, 0);
        float neck = howler.neck.xRot, jaw = howler.jaw.xRot;
        in(howler, skills, HowlerRules.Skill.HOWL.ordinal(), 20);
        assertTrue(howler.neck.xRot < neck - .4F, "its head thrown back");
        assertTrue(howler.jaw.xRot > jaw + .7F, "its jaw hanging open");
        rest(stalker, StalkerRules.SKILLS.size(), 0, 0);
        float standing = stalker.body.y;
        rest(stalker, StalkerRules.SKILLS.size(), 0, 1);
        assertTrue(stalker.body.y > standing + 1, "faded, it goes lower");
    }

    @Test void theBloaterSwellsToVentAndTheMortarsTubeToFire() {
        int skills = BloaterRules.SKILLS.size(), up = BloaterRules.VENT.windup();
        in(bloater, skills, BloaterRules.Skill.VENT.ordinal(), up - .01F);
        assertTrue(bloater.swollen > 1.2F, "swollen as it gathers: " + bloater.swollen);
        in(bloater, skills, BloaterRules.Skill.VENT.ordinal(), up + 1);
        assertTrue(bloater.swollen < 1, "sagging as the gas goes: " + bloater.swollen);
        in(mortar, MortarRules.SKILLS.size(), MortarRules.Skill.BARRAGE.ordinal(), MortarRules.BARRAGE.timing().windup() - .01F);
        assertTrue(mortar.cannon.xScale > 1.1F, "its tube swells before its barrage");
    }

    @Test void theCrawlersLegsRippleAndItsMandiblesOpen() {
        int skills = CrawlerRules.SKILLS.size();
        rest(crawler, skills, 0, 0);
        float[] still = new float[CorruptedCrawlerModel.SEGMENTS];
        for (int leg = 0; leg < still.length; leg++) still[leg] = crawler.legs[0][leg].yRot;
        float shut = crawler.mandibleLeft.yRot;
        crawler.pose(1, 1, 0, 0, 0, 0, 200, new float[skills + 1], 100, 0);
        float first = crawler.legs[0][0].yRot - still[0];
        boolean differs = false;
        for (int leg = 1; leg < still.length; leg++) differs |= Math.abs(crawler.legs[0][leg].yRot - still[leg] - first) > .1F;
        assertTrue(differs, "a wave runs down its legs, not all of them at once");
        crawler.pose(0, 0, 0, 0, 0, 0, 200, new float[skills + 1], 5, 0);
        assertTrue(crawler.mandibleLeft.yRot > shut + .4F, "its mandibles part to bite");
        assertEquals(crawler.mandibleLeft.yRot, -crawler.mandibleRight.yRot, 1e-5, "both alike");
    }

    @Test void theGazerStaresWithEveryStalkAndTheHivemothersWingsBeat() {
        int skills = GazerRules.SKILLS.size();
        rest(gazer, skills, 0, 0);
        float[] still = new float[gazer.stalks.length];
        for (int stalk = 0; stalk < still.length; stalk++) still[stalk] = gazer.stalks[stalk].xRot;
        in(gazer, skills, GazerRules.Skill.GAZE.ordinal(), 10);
        for (int stalk = 0; stalk < still.length; stalk++) assertTrue(gazer.stalks[stalk].xRot > still[stalk] + .5F, "every stalk turned forward");
        int wings = HivemotherRules.SKILLS.size();
        hive.pose(0, 0, 0, 0, 0, 0, 200, new float[wings + 1], 100, 0);
        float down = hive.foreLeft.zRot;
        hive.pose(0, 0, 1, 0, 0, 0, 200, new float[wings + 1], 100, 0);
        assertNotEquals(down, hive.foreLeft.zRot, 1e-3F, "its wings beat");
        assertEquals(hive.foreLeft.zRot, -hive.foreRight.zRot, 1e-5, "together");
        in(hive, wings, HivemotherRules.Skill.SWOOP.ordinal(), 10);
        assertTrue(hive.foreLeft.yRot > .6F, "swept back to swoop");
    }

    @Test void theDriftersTendrilsGatherInToLiftAndItsLashIsDrawnOnlyAlongTheLash() {
        int skills = DrifterRules.SKILLS.size();
        rest(drifter, skills, 0, 0);
        float out = drifter.tendrils[0][0][0].zRot;
        in(drifter, skills, DrifterRules.Skill.LIFT.ordinal(), 20);
        assertTrue(drifter.tendrils[0][0][0].zRot < out - .3F, "its tendrils gathered in under it");
        assertFalse(drifter.lash.visible || drifter.lashClub.visible, "its lash is drawn only along the lash");
        int up = DrifterRules.RAIN.timing().windup();
        in(drifter, skills, DrifterRules.Skill.RAIN.ordinal(), up - .01F);
        assertTrue(drifter.squeezed < -.9F, "swollen before its rain");
        in(drifter, skills, DrifterRules.Skill.RAIN.ordinal(), up);
        assertTrue(drifter.squeezed > .9F, "squeezed hard as it rains");
    }

    @Test void theMawsLipsGapeAndItsFinsGoLimpOutOfTheWater() {
        int skills = MawRules.SKILLS.size();
        rest(maw, skills, 0, 1);
        assertTrue(maw.open < .2F, "its lips near shut at rest");
        float swimming = maw.finLeft.zRot;
        in(maw, skills, MawRules.Skill.WHIRLPOOL.ordinal(), 40);
        assertTrue(maw.open > .8F, "gaping as it whirls");
        maw.pose(0, 0, 0, 0, 0, 0, 200, new float[skills + 1], 5, 1);
        assertTrue(maw.open > .9F, "and as it bites");
        rest(maw, skills, 0, 0);
        assertTrue(maw.finLeft.zRot < swimming - .2F, "its fins limp out of the water");
    }

    @Test void theNaiadLetsItsSpearGoAndLiesForwardToSwim() {
        int skills = NaiadRules.SKILLS.size(), up = NaiadRules.SPEAR.timing().windup();
        in(naiad, skills, NaiadRules.Skill.SPEAR.ordinal(), up - 1);
        assertTrue(naiad.spear.visible, "its spear in its hand as it draws back");
        in(naiad, skills, NaiadRules.Skill.SPEAR.ordinal(), up + 1);
        assertFalse(naiad.spear.visible, "gone once thrown");
        in(naiad, skills, NaiadRules.Skill.SONG.ordinal(), 5);
        assertTrue(naiad.spear.visible, "and back for what it does next");
        naiad.pose(0, 1, 0, 0, 0, 0, 200, new float[skills + 1], 100, 1);
        assertTrue(naiad.tail0.xRot > 1, "swimming, it lies forward");
        rest(naiad, skills, 0, 0);
        assertEquals(0, naiad.tail0.xRot, .1, "and stands on its tail at rest out of the water");
    }

    @Test void thePicturesTheyNeedAreThere() throws Exception {
        for (String name : List.of("corrupted_brute", "corrupted_howler", "corrupted_stalker", "corrupted_bloater", "corrupted_mortar", "corrupted_crawler",
                "corrupted_gazer", "corrupted_hivemother", "corrupted_drifter", "corrupted_maw", "corrupted_naiad")) {
            for (String picture : List.of(name, name + "_glow")) {
                try (InputStream in = getClass().getResourceAsStream("/assets/corruption/textures/entity/" + picture + ".png")) {
                    assertNotNull(in, picture);
                    BufferedImage image = ImageIO.read(in);
                    assertEquals(128, image.getWidth(), picture);
                    assertEquals(128, image.getHeight(), picture);
                }
            }
            try (InputStream egg = getClass().getResourceAsStream("/assets/corruption/textures/item/" + name + "_spawn_egg.png");
                 InputStream item = getClass().getResourceAsStream("/assets/corruption/models/item/" + name + "_spawn_egg.json")) {
                assertNotNull(egg, name + "'s egg");
                assertNotNull(item, name + "'s egg's item model");
            }
        }
    }
}
