package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.entity.Amdusias;
import com.miophas.corruption.core.demon.AmdusiasRules;
import com.miophas.corruption.core.demon.AmdusiasRules.Skill;
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
 * Amdusias's baked model: every bone there, his fanfare leaving the tip of his horn where the model has it, each pose
 * (Seere's rearing, his horn lowered to the ground and lifted to the sky, a stamp for every ring) and its easing back to
 * rest, his death on his side; and the pictures he needs, his emblem on the boss bar among them.
 */
class AmdusiasModelTest {
    private final AmdusiasModel model = new AmdusiasModel(AmdusiasModel.createBodyLayer().bakeRoot());

    private void rest() { model.pose(0, 0, 0, Skill.NONE, 0, 0, 0); }

    private float[] pose() {
        float[] all = new float[model.all.length * 9];
        for (int index = 0; index < model.all.length; index++) {
            ModelPart part = model.all[index];
            System.arraycopy(new float[]{part.x, part.y, part.z, part.xRot, part.yRot, part.zRot, part.xScale, part.yScale, part.zScale}, 0, all, index * 9, 9);
        }
        return all;
    }

    /** The corners of every cube whose bone's path passes, in the model's pixels. */
    private List<Vector3f> corners(Predicate<String> path) {
        List<Vector3f> found = new ArrayList<>();
        model.root.visit(new PoseStack(), (pose, at, index, cube) -> {
            if (!path.test(at)) return;
            for (float x : new float[]{cube.minX, cube.maxX}) for (float y : new float[]{cube.minY, cube.maxY}) for (float z : new float[]{cube.minZ, cube.maxZ})
                found.add(pose.pose().transformPosition(x / 16, y / 16, z / 16, new Vector3f()).mul(16));
        });
        assertFalse(found.isEmpty());
        return found;
    }

    @Test void heBakesWithEveryBoneAndHisFanfareLeavesTheTipOfHisHorn() {
        assertNotNull(model.horn);
        rest();
        Vector3f tip = corners(path -> path.endsWith("/horn")).stream().min((a, b) -> Float.compare(a.z, b.z)).orElseThrow();
        double[] frame = {-tip.x / 16, (24 - tip.y) / 16, -tip.z / 16};
        double off = Math.sqrt(Math.pow(frame[0] - Amdusias.HORN[0], 2) + Math.pow(frame[1] - Amdusias.HORN[1], 2) + Math.pow(frame[2] - Amdusias.HORN[2], 2));
        assertTrue(off < .35, String.format("the server has his horn's tip at {%.2f, %.2f, %.2f}, the model {%.2f, %.2f, %.2f}", Amdusias.HORN[0], Amdusias.HORN[1],
                Amdusias.HORN[2], frame[0], frame[1], frame[2]));
    }

    @Test void heRearsLowersHisHornLiftsItAndStampsForEveryRing() {
        rest();
        float body = model.body.xRot, neck = model.neck.xRot;
        model.pose(0, 0, 0, Skill.HOOVES, AmdusiasModel.windup(Skill.HOOVES) - 2, 0, 0);
        assertTrue(model.body.xRot < body - .5F, "rearing for his hooves");
        model.pose(0, 0, 0, Skill.UPHEAVAL, 10, 0, 0);
        assertTrue(model.neck.xRot > neck + .3F, "his horn lowered to the ground to heave it");
        model.pose(0, 0, 0, Skill.FANFARE, 40, 0, 0);
        assertTrue(model.neck.xRot < neck - .4F, "his head thrown up for his fanfare");
        assertTrue(model.jaw.xRot > .3F, "his jaw open");
        // A stamp for every ring: reared just before each goes out, down as it does.
        float windup = AmdusiasModel.windup(Skill.CRESCENDO), every = AmdusiasRules.CRESCENDO.every();
        for (int ring = 0; ring < AmdusiasRules.CRESCENDO.rings(); ring++) {
            float goes = windup + ring * every;
            model.pose(0, 0, 0, Skill.CRESCENDO, goes - 1, 0, 0);
            float reared = model.body.xRot;
            model.pose(0, 0, 0, Skill.CRESCENDO, goes + 3, 0, 0);
            assertTrue(model.body.xRot > reared + .3F, "down as ring " + ring + " goes out");
        }
        model.pose(0, 1.3F, 1, Skill.NONE, 0, 0, 0);
        assertNotEquals(0, model.front[0].xRot, 1e-3, "he gallops");
        float y = model.body.y;
        model.pose(0, 0, 0, Skill.NONE, 0, 1, 0);
        assertTrue(model.body.zRot > 1, "dead, on his side");
        assertTrue(model.body.y > y + 8);
    }

    @Test void everyPoseEasesBackToRestOnceItsSkillIsOver() {
        rest();
        float[] rest = pose();
        for (Skill skill : Skill.values()) {
            for (float ticks = 0; ticks < AmdusiasModel.length(skill) + 10; ticks += 2.5F) {
                model.pose(ticks, ticks * .4F, .6F, skill, ticks, 0, 3);
                for (float value : pose()) assertTrue(Float.isFinite(value), skill + " at " + ticks);
            }
            model.pose(0, 0, 0, skill, AmdusiasModel.length(skill) + 40, 0, 0);
            assertArrayEquals(rest, pose(), 1e-4F, skill + " settles back to rest");
        }
    }

    @Test void thePicturesHeNeedsAreThere() throws Exception {
        BufferedImage texture = image("/assets/corruption/textures/entity/amdusias.png"), glow = image("/assets/corruption/textures/entity/amdusias_glow.png");
        assertEquals(256, texture.getWidth());
        assertEquals(128, texture.getHeight());
        assertEquals(texture.getWidth(), glow.getWidth());
        int lit = 0;
        for (int y = 0; y < glow.getHeight(); y++) for (int x = 0; x < glow.getWidth(); x++) {
            if ((glow.getRGB(x, y) >>> 24) == 0) continue;
            lit++;
            assertTrue((texture.getRGB(x, y) >>> 24) != 0, "glowing only where he is painted");
        }
        assertTrue(lit > 50, "his eyes, his sigils, his horn's spiral and the tips of his hair glow: " + lit);
        BufferedImage bar = image("/assets/corruption/textures/gui/colossus_boss_bar.png");
        int drawn = 0, green = 0;
        for (int y = 72; y < 96; y++) for (int x = 224; x < 248; x++) {
            int pixel = bar.getRGB(x, y);
            if ((pixel >>> 24) == 0) continue;
            drawn++;
            if (((pixel >> 8) & 255) > ((pixel >> 16) & 255) + 40) green++;
        }
        assertTrue(drawn > 150, "his emblem is on the boss bar, after Belial's");
        assertTrue(green > 5, "in his own pale green");
    }

    private static BufferedImage image(String path) throws Exception {
        try (InputStream in = AmdusiasModelTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return ImageIO.read(in);
        }
    }
}
