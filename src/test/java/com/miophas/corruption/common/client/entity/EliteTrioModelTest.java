package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.entity.CorruptedBlighthound;
import com.miophas.corruption.common.entity.CorruptedMindweaver;
import com.miophas.corruption.common.entity.CorruptedSower;
import com.miophas.corruption.core.elite.BlighthoundRules;
import com.miophas.corruption.core.elite.MindweaverRules;
import com.miophas.corruption.core.elite.SowerRules;
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
 * The mindweaver's, the blighthound's and the sower's baked models: each poses through every skill and eases back to rest;
 * their sides mirror each other as built; what each sends out leaves it where its model sends it from; the mindweaver's
 * brain swells as it pulls at a mind, the blighthound's split jaw parts and it stretches out to spring, the sower's petals
 * part and it swells to sow; and the pictures they need are there.
 */
class EliteTrioModelTest {
    private final CorruptedMindweaverModel weaver = new CorruptedMindweaverModel(CorruptedMindweaverModel.createBodyLayer().bakeRoot());
    private final CorruptedBlighthoundModel hound = new CorruptedBlighthoundModel(CorruptedBlighthoundModel.createBodyLayer().bakeRoot());
    private final CorruptedSowerModel sower = new CorruptedSowerModel(CorruptedSowerModel.createBodyLayer().bakeRoot());

    private Map<PosedEliteModel<?>, Integer> all() {
        return Map.of(weaver, MindweaverRules.SKILLS.size(), hound, BlighthoundRules.SKILLS.size(), sower, SowerRules.SKILLS.size());
    }
    private static float[] on(int skills, int skill, float how) {
        float[] on = new float[skills + 1];
        if (skill > 0) on[skill] = how;
        return on;
    }
    private static void rest(PosedEliteModel<?> model, int skills, int posed) { model.pose(0, 0, 0, 0, 0, posed, 200, new float[skills + 1], 100, 0); }
    private static void in(PosedEliteModel<?> model, int skills, int skill, float since) { model.pose(0, 0, 0, 0, 0, skill, since, on(skills, skill, 1), 100, 0); }
    private static float[] pose(ModelPart[] parts) {
        float[] all = new float[parts.length * 9];
        for (int index = 0; index < parts.length; index++) {
            ModelPart part = parts[index];
            System.arraycopy(new float[]{part.x, part.y, part.z, part.xRot, part.yRot, part.zRot, part.xScale, part.yScale, part.zScale}, 0, all, index * 9, 9);
        }
        return all;
    }
    private static List<Vector3f> corners(ModelPart root, Predicate<String> path) {
        List<Vector3f> found = new ArrayList<>();
        root.visit(new PoseStack(), (pose, at, index, cube) -> {
            if (!path.test(at)) return;
            for (float x : new float[]{cube.minX, cube.maxX}) for (float y : new float[]{cube.minY, cube.maxY}) for (float z : new float[]{cube.minZ, cube.maxZ})
                found.add(pose.pose().transformPosition(x / 16, y / 16, z / 16, new Vector3f()).mul(16));
        });
        assertFalse(found.isEmpty(), "some bone passes");
        return found;
    }
    /** The middle of those corners within a pixel of the foremost, in the entity's frame {right, up, ahead}. */
    private static double[] foremost(List<Vector3f> corners) {
        float front = (float) corners.stream().mapToDouble(corner -> corner.z).min().orElseThrow();
        Vector3f sum = new Vector3f();
        int count = 0;
        for (Vector3f corner : corners) if (corner.z <= front + 1) { sum.add(corner); count++; }
        sum.div(count);
        return new double[]{-sum.x / 16, (24 - sum.y) / 16, -sum.z / 16};
    }
    private static void near(double[] expected, double[] actual, double within, String what, List<String> off) {
        double distance = Math.sqrt(Math.pow(expected[0] - actual[0], 2) + Math.pow(expected[1] - actual[1], 2) + Math.pow(expected[2] - actual[2], 2));
        if (distance > within) off.add(String.format("%s: the server has {%.2f, %.2f, %.2f}, the model {%.2f, %.2f, %.2f}", what, expected[0], expected[1],
                expected[2], actual[0], actual[1], actual[2]));
    }

    @Test void eachBakesPosesThroughEverySkillAndEasesBackToRest() {
        all().forEach((model, skills) -> {
            for (int skill = 0; skill <= skills; skill++) for (float ticks = 0; ticks < 90; ticks += 2.5F) {
                int posed = skill;
                float at = ticks, blend = ticks % 20 / 20;
                model.pose(at * .3F, .8F, at, 20, -10, posed, at, on(skills, posed, blend), at % 12, blend);
                for (float value : pose(model.all)) assertTrue(Float.isFinite(value), model + " stays finite in skill " + posed + " at " + at);
            }
            rest(model, skills, 0);
            float[] still = pose(model.all);
            for (int skill = 1; skill <= skills; skill++) {
                rest(model, skills, skill);
                assertArrayEquals(still, pose(model.all), 1e-5F, model + ": long after skill " + skill + ", it is at rest");
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
                assertNotNull(right, bone.getKey() + " has a right to it");
                assertEquals(left.x, -right.x, 1e-4, bone.getKey());
                assertEquals(left.y, right.y, 1e-4, bone.getKey());
                assertEquals(left.yRot, -right.yRot, 1e-4, bone.getKey());
                assertEquals(left.zRot, -right.zRot, 1e-4, bone.getKey());
                pairs++;
            }
            assertTrue(pairs > 0, model + " has sides");
        });
    }

    @Test void whatEachSendsOutLeavesItWhereItsModelSendsItFrom() {
        List<String> off = new ArrayList<>();
        rest(weaver, MindweaverRules.SKILLS.size(), 0);
        near(CorruptedMindweaver.MOUTH, foremost(corners(weaver.root, path -> path.endsWith("/brain"))), .35, "the front of the mindweaver's brain", off);
        rest(hound, BlighthoundRules.SKILLS.size(), 0);
        near(CorruptedBlighthound.MOUTH, foremost(corners(hound.root, path -> path.endsWith("/head"))), .35, "the blighthound's jaws", off);
        rest(sower, SowerRules.SKILLS.size(), 0);
        near(CorruptedSower.MOUTH, foremost(corners(sower.root, path -> path.contains("/maw_"))), .35, "the sower's flower of a mouth", off);
        assertTrue(off.isEmpty(), String.join("\n", off));
    }

    @Test void eachMovesAsItShould() {
        int minds = MindweaverRules.SKILLS.size();
        int pull = MindweaverRules.THREAD.timing().windup() + MindweaverRules.THREAD.interval() * 2;
        in(weaver, minds, MindweaverRules.Skill.THREAD.ordinal(), pull + 2.5F);
        float pulling = weaver.swollen;
        in(weaver, minds, MindweaverRules.Skill.THREAD.ordinal(), pull + 6);
        assertTrue(pulling > weaver.swollen + .05F, "its brain swells at every pull: " + pulling + " against " + weaver.swollen);
        rest(weaver, minds, 0);
        float arm = weaver.armLeft.zRot;
        in(weaver, minds, MindweaverRules.Skill.WAIL.ordinal(), MindweaverRules.WAIL.timing().windup() + 4);
        assertTrue(weaver.armLeft.zRot > arm + .5F, "its arms flung wide to wail");

        int hounds = BlighthoundRules.SKILLS.size();
        hound.pose(0, 0, 0, 0, 0, 0, 200, new float[hounds + 1], 5, 0);
        assertTrue(hound.open > .9F, "its split jaw parts to bite");
        assertEquals(hound.jawLeft.yRot, -hound.jawRight.yRot, 1e-5, "both halves alike");
        rest(hound, hounds, 0);
        float foreleg = hound.foreLeft.xRot;
        in(hound, hounds, BlighthoundRules.Skill.REND.ordinal(), BlighthoundRules.REND.timing().windup() + 4);
        assertTrue(hound.foreLeft.xRot < foreleg - .8F, "stretched out long in its spring");

        int sowers = SowerRules.SKILLS.size();
        rest(sower, sowers, 0);
        float shut = sower.open;
        in(sower, sowers, SowerRules.Skill.SEED.ordinal(), SowerRules.SEED.timing().windup() - .01F);
        assertTrue(sower.swollen > 1.15F, "swollen as it gathers to sow");
        in(sower, sowers, SowerRules.Skill.SNARE.ordinal(), 10);
        assertTrue(sower.open > shut + .5F, "its petals part as it sends its roots up");
        sower.pose(1.3F, 1, 0, 0, 0, 0, 200, new float[sowers + 1], 100, 0);
        float first = sower.roots[0][0].xRot, second = sower.roots[0][1].xRot;
        assertNotEquals(first, second, 1e-3F, "its roots step in a ripple, not all at once");
    }

    @Test void thePicturesTheyNeedAreThere() throws Exception {
        for (String name : List.of("corrupted_mindweaver", "corrupted_blighthound", "corrupted_sower")) {
            for (String picture : List.of(name, name + "_glow"))
                try (InputStream in = getClass().getResourceAsStream("/assets/corruption/textures/entity/" + picture + ".png")) {
                    assertNotNull(in, picture);
                    BufferedImage image = ImageIO.read(in);
                    assertEquals(128, image.getWidth(), picture);
                    assertEquals(128, image.getHeight(), picture);
                }
            try (InputStream egg = getClass().getResourceAsStream("/assets/corruption/textures/item/" + name + "_spawn_egg.png");
                 InputStream item = getClass().getResourceAsStream("/assets/corruption/models/item/" + name + "_spawn_egg.json")) {
                assertNotNull(egg, name + "'s egg");
                assertNotNull(item, name + "'s egg's item model");
            }
        }
    }
}
