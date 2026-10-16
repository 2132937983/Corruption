package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.DantalionRules;
import com.miophas.corruption.core.demon.DantalionRules.Skill;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.junit.jupiter.api.Test;

/**
 * Dantalion's two baked models: each has every bone its rig poses, and glowing pieces; and each of his poses moves what
 * it should. The book opens its eye to gaze, snaps shut to blink, turns its leaves over and back, rears up to call
 * lightning, and is slammed shut with its loose pages flung off as he changes; the mirror is made before the eyes at
 * the end of his change, shows its spiral only while it reaches for a mind or holds one, draws its shards in to gather
 * its beam, and loses its glass and masks in death. Every pose eases back to rest by itself once its skill is over (the
 * client goes on posing a skill until the next begins), and what turns faster or slower never jumps as it does. And the
 * pictures he needs are there.
 */
class DantalionModelTest {
    private final DantalionBookModel book = new DantalionBookModel(DantalionBookModel.createBodyLayer().bakeRoot());
    private final DantalionMirrorModel mirror = new DantalionMirrorModel(DantalionMirrorModel.createBodyLayer().bakeRoot());

    private void book(float age, Skill skill, float ticks) { book.pose(age, skill, ticks, 0, 0, 0, 0); }
    private void mirror(float age, Skill skill, float ticks) { mirror.pose(age, skill, ticks, 0, 0, 0, 0); }

    @Test void bothBakeWithEveryBoneTheirRigsNeedAndGlowingPieces() {
        assertEquals(DantalionBookModel.LEAVES, book.leaves.length);
        assertEquals(DantalionMirrorModel.ARCS, mirror.arcs.length);
        assertTrue(GeoModel.load(DantalionBookModel.GEOMETRY).bones().stream().filter(bone -> bone.name().endsWith("_glow")).count() >= 4,
                "the book's eye, its runes and its loose pages glow");
        assertTrue(GeoModel.load(DantalionMirrorModel.GEOMETRY).bones().stream().filter(bone -> bone.name().endsWith("_glow")).count() >= 4,
                "the mirror's rim, its spiral, the eyes of its masks glow");
        for (Skill skill : Skill.values()) for (float ticks = 0; ticks < 100; ticks += 7) {
            float at = ticks;
            assertDoesNotThrow(() -> book(at, skill, at), skill + " as the book");
            assertDoesNotThrow(() -> mirror(at, skill, at), skill + " as the mirror");
        }
    }

    @Test void theBookStandsOpenAtRestAndItsTwoCoversMirrorEachOther() {
        book(0, Skill.NONE, 0);
        assertEquals(book.leftCover.yRot, -book.rightCover.yRot, 1e-6);
        float swing = Mth.HALF_PI * (1 - DantalionBookModel.REST_OPEN);
        assertEquals(swing, book.leftCover.yRot, .1F, "open in a V, some 25 degrees short of flat each way");
        assertEquals(.12F, book.eye.yScale, 1e-6, "its eye shut");
    }

    @Test void itThrowsItselfOpenAndOpensItsEyeToGaze() {
        book(0, Skill.NONE, 0);
        float swing = book.leftCover.yRot;
        int windup = DantalionRules.GREATER_GAZE.timing().windup();
        book(0, Skill.GREATER_GAZE, windup);
        assertTrue(book.leftCover.yRot < swing - .3F && book.rightCover.yRot > -swing + .3F, "thrown open flat");
        assertEquals(1, book.eye.yScale, 1e-4, "its eye wide open");
        book(0, Skill.GREATER_GAZE, windup + 14);
        assertEquals(.12F, book.eye.yScale, 1e-4, "and shut again after");
    }

    @Test void itSnapsShutToBlinkAndOpensAgainAfter() {
        book(0, Skill.BLINK, DantalionRules.BLINK.timing().ticks() + 8);
        assertTrue(book.leftCover.yRot < .6F, "open again once he is there: " + book.leftCover.yRot);
        book(0, Skill.BLINK, 3);
        assertTrue(book.leftCover.yRot > 1.3F && book.rightCover.yRot < -1.3F, "shut, covers together: " + book.leftCover.yRot);
        assertTrue(book.spine.xScale > 2.3F && book.spine.z < -2.5F, "its spine widened and come forward to meet them");
        book(0, Skill.NONE, 0);
        assertEquals(1.06F, book.spine.xScale, .05F, "open, as narrow as it was made");
        assertEquals(0, book.spine.z, .4F, "and where it was made");
    }

    @Test void itsLeavesTurnOverOneAfterAnotherAndBack() {
        assertEquals(0, DantalionBookModel.flipped(0, 0), 1e-6, "at first none is over");
        assertEquals(1, DantalionBookModel.flipped(0, .5F), 1e-6, "the first goes over");
        assertEquals(0, DantalionBookModel.flipped(1, .5F), 1e-6, "before the second");
        for (int leaf = 0; leaf < DantalionBookModel.LEAVES; leaf++)
            assertEquals(1, DantalionBookModel.flipped(leaf, DantalionBookModel.LEAVES - .5F), 1e-6, "by halfway all are over");
        assertEquals(0, DantalionBookModel.flipped(DantalionBookModel.LEAVES - 1, DantalionBookModel.LEAVES + .5F), 1e-6, "the last over is the first back");
        assertEquals(1, DantalionBookModel.flipped(0, DantalionBookModel.LEAVES + .5F), 1e-6, "while the first still lies over");
        for (int leaf = 0; leaf < DantalionBookModel.LEAVES; leaf++)
            assertEquals(0, DantalionBookModel.flipped(leaf, 2 * DantalionBookModel.LEAVES - .1F), 1e-6, "and at the end all are back");
        // Posed, the first leaf lies on the right block, then on the left, as the book rests.
        float lowest = 9, highest = -9;
        for (int age = 0; age < 420; age += 3) {
            book(age, Skill.NONE, 0);
            lowest = Math.min(lowest, book.leaves[0].yRot);
            highest = Math.max(highest, book.leaves[0].yRot);
        }
        assertTrue(highest > -.8F && lowest < -2.2F, "it lies on either block in turn: " + lowest + " to " + highest);
    }

    @Test void itRearsUpToCallLightning() {
        book(0, Skill.NONE, 0);
        float lean = book.book.xRot, height = book.book.y;
        book(0, Skill.STORM, DantalionRules.STORM.timing().windup());
        assertTrue(book.book.xRot < lean - .4F, "reared back, its pages to the sky");
        assertTrue(book.book.y < height - 2, "and risen");
    }

    @Test void asHeChangesTheBookIsSlammedShutAndItsLoosePagesFlungOff() {
        book(0, Skill.NONE, 0);
        float out = Mth.sqrt(book.loose[0].x * book.loose[0].x + book.loose[0].z * book.loose[0].z);
        book.pose(0, Skill.NONE, 0, .45F, 0, 0, 0);
        assertTrue(book.leftCover.yRot > 1.3F, "slammed shut");
        float flung = Mth.sqrt(book.loose[0].x * book.loose[0].x + book.loose[0].z * book.loose[0].z);
        assertTrue(flung > out * 1.8F, "its loose pages flung off: " + out + " to " + flung);
    }

    @Test void theMirrorIsMadeBeforeTheEyesAtTheEndOfHisChange() {
        mirror(0, Skill.NONE, 0);
        float frame = spread(mirror.arcs);
        assertEquals(1, mirror.glass.xScale, 1e-6, "made, its glass fills it");
        assertEquals(1, mirror.crest.xScale, 1e-6, "and its crest is set on");
        mirror.pose(0, Skill.NONE, 0, .52F, 0, 0, 0);
        assertTrue(spread(mirror.arcs) > frame * 2.5F, "at the start, its arcs scattered in the air");
        assertEquals(0, mirror.glass.xScale, 1e-6, "no glass yet");
        assertEquals(0, mirror.crest.xScale, 1e-6, "nor crest");
        mirror.pose(0, Skill.NONE, 0, .999F, 0, 0, 0);
        assertEquals(frame, spread(mirror.arcs), frame * .02F, "at the end, locked together");
        assertTrue(mirror.glass.xScale > .95F, "and filled");
    }

    @Test void itsSpiralShowsWhileItReachesForAMindAndWhileItHoldsOne() {
        mirror(0, Skill.NONE, 0);
        assertEquals(0, mirror.spiral.xScale, 1e-6, "no spiral at rest");
        int windup = DantalionRules.MIND_CONTROL.timing().windup();
        mirror(0, Skill.MIND_CONTROL, windup);
        assertTrue(mirror.spiral.xScale > .9F, "it swells out of the glass as he reaches for a mind");
        mirror(0, Skill.MIND_CONTROL, windup + 10);
        assertEquals(0, mirror.spiral.xScale, 1e-6, "and is gone again if he took none");
        mirror.pose(0, Skill.BEAM, DantalionRules.BEAM.timing().windup() + 10, 0, 0, 0, 0, 1, 0, 0);
        assertTrue(mirror.spiral.xScale > .9F, "but while he holds one it turns on, whatever else he does");
        assertTrue(DantalionMirrorModel.spiralRate(true) > DantalionMirrorModel.spiralRate(false), "the faster the harder he holds");
        assertTrue(DantalionMirrorModel.shardRate(Skill.NONE, 0, 1, true) > DantalionMirrorModel.shardRate(Skill.NONE, 0, 0, false), "and his shards whirl faster");
    }

    @Test void itDrawsItsShardsInToGatherItsBeamAndLetsThemOutAfter() {
        mirror(0, Skill.NONE, 0);
        float wide = spread(mirror.shards);
        mirror(0, Skill.BEAM, DantalionRules.BEAM.timing().windup());
        assertTrue(spread(mirror.shards) < wide * .7F, "its shards drawn in close");
        mirror(0, Skill.BEAM, DantalionRules.BEAM.timing().ticks() + 1);
        assertEquals(wide, spread(mirror.shards), wide * .01F, "and out again once it is over");
    }

    /** The client goes on posing a skill after it is over, until he begins another: every pose has to settle by itself. */
    @Test void everyPoseEasesBackToRestOnceItsSkillIsOver() {
        for (Skill skill : Skill.values()) {
            book(50, Skill.NONE, 0);
            float[][] rest = snapshot(book.all);
            book(50, skill, 200);
            assertSame(rest, snapshot(book.all), skill + " as the book");
            mirror(50, Skill.NONE, 0);
            rest = snapshot(mirror.all);
            mirror(50, skill, 200);
            assertSame(rest, snapshot(mirror.all), skill + " as the mirror");
        }
    }

    /** What turns faster or slower (the leaves, the shards) changes its speed smoothly; and how far it has turned is wound on, never jumped. */
    @Test void theLeavesAndShardsTurnFasterTheMoreHeIsAboutWithoutEverJumping() {
        assertEquals(DantalionBookModel.LEAF_RATE, DantalionBookModel.leafRate(Skill.NONE, 0, 0), 1e-6);
        assertEquals(5 * DantalionBookModel.LEAF_RATE, DantalionBookModel.leafRate(Skill.GAZE, DantalionRules.GAZE.timing().windup(), 0), 1e-4, "thrown open to gaze");
        assertEquals(0, DantalionBookModel.leafRate(Skill.GAZE, 10, .5F), 1e-6, "still as he dies");
        assertTrue(DantalionMirrorModel.shardRate(Skill.BEAM, DantalionRules.BEAM.timing().windup() + 5, 0, false) > 3 * DantalionMirrorModel.ORBIT, "whirling as the beam fires");
        for (Skill skill : Skill.values()) {
            float leaves = DantalionBookModel.leafRate(skill, 0, 0), shards = DantalionMirrorModel.shardRate(skill, 0, 0, false);
            for (float ticks = .25F; ticks < 150; ticks += .25F) {
                float nowLeaves = DantalionBookModel.leafRate(skill, ticks, 0), nowShards = DantalionMirrorModel.shardRate(skill, ticks, 0, false);
                assertEquals(leaves, nowLeaves, .02F, skill + "'s leaves at " + ticks);
                assertEquals(shards, nowShards, .02F, skill + "'s shards at " + ticks);
                leaves = nowLeaves;
                shards = nowShards;
            }
            assertEquals(DantalionBookModel.LEAF_RATE, leaves, 1e-6, skill + ": the leaves back to their resting rate");
            assertEquals(DantalionMirrorModel.ORBIT, shards, 1e-6, skill + ": the shards too");
        }
    }

    @Test void bothFallInDeathTheMirrorLosingItsGlassAndMasks() {
        book(0, Skill.NONE, 0);
        float lean = book.book.xRot;
        book.pose(0, Skill.NONE, 0, 0, 1, 0, 0);
        assertTrue(book.book.xRot > lean + 1, "the book tips over");
        mirror(0, Skill.NONE, 0);
        float tilt = mirror.mirror.xRot, mask = mirror.masks[0].y;
        mirror.pose(0, Skill.NONE, 0, 0, 1, 0, 0);
        assertTrue(mirror.mirror.xRot > tilt + 1, "the mirror tips over");
        assertEquals(0, mirror.glass.xScale, 1e-6, "its glass gone");
        assertTrue(mirror.masks[0].y > mask + 10, "its masks fallen away");
    }

    @Test void thePicturesHeNeedsAreThere() throws Exception {
        BufferedImage texture = image("/assets/corruption/textures/entity/dantalion.png");
        assertEquals(512, texture.getWidth());
        assertEquals(256, texture.getHeight());
        BufferedImage bar = image("/assets/corruption/textures/gui/colossus_boss_bar.png");
        int drawn = 0;
        for (int y = 72; y < 96; y++) for (int x = 96; x < 120; x++) if ((bar.getRGB(x, y) >>> 24) != 0) drawn++;
        assertTrue(drawn > 150, "his emblem is on the boss bar, beside Andromalius's");
    }

    /** Where every bone lies and how it is turned and scaled. */
    private static float[][] snapshot(ModelPart[] parts) {
        float[][] all = new float[parts.length][];
        for (int index = 0; index < parts.length; index++) {
            ModelPart part = parts[index];
            all[index] = new float[]{part.x, part.y, part.z, part.xRot, part.yRot, part.zRot, part.xScale, part.yScale, part.zScale};
        }
        return all;
    }

    private static void assertSame(float[][] expected, float[][] actual, String what) {
        for (int index = 0; index < expected.length; index++)
            for (int value = 0; value < expected[index].length; value++)
                assertEquals(expected[index][value], actual[index][value], 1e-4, what + ", bone " + index + ", value " + value);
    }

    /** How far these bones lie from their parent's pivot, all told. */
    private static float spread(ModelPart[] parts) {
        float all = 0;
        for (ModelPart part : parts) all += Mth.sqrt(part.x * part.x + part.y * part.y + part.z * part.z);
        return all;
    }

    private static BufferedImage image(String path) throws Exception {
        try (InputStream in = DantalionModelTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return ImageIO.read(in);
        }
    }
}
