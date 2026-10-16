package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.SeereRules;
import com.miophas.corruption.core.demon.SeereRules.Skill;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

/**
 * Seere's baked model: it has every bone his rig poses. At rest his wings lie folded along his
 * flanks, their feathers hanging down and nowhere standing up over his back; in the air they spread wide and beat;
 * half open they are swept back whole, not broken. He gallops, rears to strike, dives with his wings swept back, lies on
 * his side in death, and every pose eases back to rest by itself once its skill is over (the client goes on posing a
 * skill until the next begins). And the pictures he needs are there: his texture, and his glow over the same places.
 */
class SeereModelTest {
    private final SeereModel model = new SeereModel(SeereModel.createBodyLayer().bakeRoot());

    private void rest() { model.pose(0, 0, 0, Skill.NONE, 0, 0, 0, 0, 0); }

    /** Where a point given in a bone's own pixels is, in the model's pixels, through the bones from the root down to it. */
    private Vector3f at(float x, float y, float z, ModelPart... chain) {
        PoseStack stack = new PoseStack();
        model.root.translateAndRotate(stack);
        for (ModelPart part : chain) part.translateAndRotate(stack);
        Vector4f point = stack.last().pose().transform(new Vector4f(x / 16, y / 16, z / 16, 1));
        return new Vector3f(point.x * 16, point.y * 16, point.z * 16);
    }
    /** The left wing's shoulder, the tip of its outermost feather, and the foot of its forearm's outermost feather. */
    private Vector3f shoulder() { return at(0, 0, 0, model.body, model.wing[0]); }
    private Vector3f tip() { return at(-24, 0, .8F, model.body, model.wing[0], model.wingFore[0], model.wingHand[0]); }
    private Vector3f feather() { return at(-13, 0, 17.9F, model.body, model.wing[0], model.wingFore[0]); }
    /** The top of his back and his belly, and his left flank, in the model's pixels. */
    private float back() { return model.body.y - 7; }
    private float belly() { return model.body.y + 7; }
    private static final float FLANK = -6;

    private float[] pose() {
        float[] all = new float[model.all.length * 6];
        for (int index = 0; index < model.all.length; index++) {
            ModelPart part = model.all[index];
            System.arraycopy(new float[]{part.x, part.y, part.z, part.xRot, part.yRot, part.zRot}, 0, all, index * 6, 6);
        }
        return all;
    }

    @Test void itBakesWithEveryBoneTheRigNeeds() {
        assertTrue(GeoModel.load(SeereModel.GEOMETRY).bones().stream().noneMatch(bone -> bone.name().endsWith("_glow")), "what glows is painted, not built");
        for (Skill skill : Skill.values()) for (float ticks = 0; ticks < 120; ticks += 7) {
            float at = ticks;
            assertDoesNotThrow(() -> model.pose(at, at, .5F, skill, at, at % 2 == 0 ? 0 : 1, at, 0, 3), skill.name());
        }
        rest();
        assertEquals(model.wing[0].x, -model.wing[1].x, 1e-6, "his wings mirror each other");
        assertEquals(model.wing[0].yRot, -model.wing[1].yRot, 1e-6);
        assertEquals(model.wing[0].xRot, model.wing[1].xRot, 1e-6);
    }

    @Test void atRestHisWingsLieFoldedAlongHisFlanksTheirFeathersHangingDown() {
        rest();
        Vector3f shoulder = shoulder(), tip = tip(), feather = feather();
        assertTrue(tip.z - shoulder.z > 12, "the hand laid back along him, behind the shoulder: " + tip);
        assertTrue(tip.x < FLANK && feather.x < FLANK, "outside his flank, not in it: " + tip + " " + feather);
        assertTrue(tip.x > FLANK - 9 && feather.x > FLANK - 9, "but close along it: " + tip + " " + feather);
        assertTrue(tip.y > back() && feather.y > back(), "nothing standing up over his back: " + tip + " " + feather);
        assertTrue(feather.y > shoulder.y + 10, "the feathers hanging down his side: " + feather);
        assertTrue(feather.y < belly() + 1, "no lower than his belly: " + feather + " against " + belly());
        assertEquals(-SeereModel.FOLD_SHUT, model.wingFore[0].zRot, .05F, "the forearm shut forward over the arm");
        assertEquals(SeereModel.FOLD_SHUT, model.wingHand[0].zRot, .05F, "and the hand back over that");
    }

    @Test void inTheAirHisWingsSpreadWideAndBeat() {
        model.pose(0, 0, 0, Skill.NONE, 0, 1, Mth.HALF_PI, 0, 0);
        Vector3f up = tip();
        model.pose(0, 0, 0, Skill.NONE, 0, 1, -Mth.HALF_PI, 0, 0);
        Vector3f down = tip();
        for (Vector3f tip : new Vector3f[]{up, down}) assertTrue(tip.x < FLANK - 30, "spread far out to the side: " + tip);
        assertTrue(up.y < down.y - 20, "and beating, up and down: " + up + " " + down);
        assertTrue(up.y < back(), "high over his back at the top of the beat");
        assertEquals(0, SeereModel.flapRate(Skill.NONE, 0, 0, true), 1e-6, "on the ground they do not beat");
        assertTrue(SeereModel.flapRate(Skill.NONE, 0, 1, false) > 0);
        assertEquals(0, SeereModel.flapRate(Skill.DIVE, SeereRules.DIVE.timing().windup() + 2, 1, true), 1e-6, "nor falling in his dive");
        assertTrue(SeereModel.flapRate(Skill.BEAMS, 40, 1, false) < SeereModel.flapRate(Skill.NONE, 0, 1, false), "slow and mighty hanging to fire his beams");
    }

    @Test void halfOpenHisWingsAreSweptBackWholeNotBroken() {
        // At the moment of the beat where the forearm is not bent by it.
        model.pose(0, 0, 0, Skill.NONE, 0, .5F, .7F, 0, 0);
        assertEquals(0, model.wingFore[0].zRot, .1F, "the forearm not yet shut");
        assertEquals(0, model.wingHand[0].zRot, .1F, "nor the hand");
        Vector3f shoulder = shoulder(), tip = tip();
        assertTrue(tip.z > shoulder.z + 10 && tip.x < FLANK - 15, "the whole wing swept back and out: " + tip);
    }

    @Test void heGallopsAndRearsToStrike() {
        model.pose(0, 2, 1, Skill.NONE, 0, 0, 0, 0, 0);
        float frontA = model.front[0].xRot, hindA = model.hind[0].xRot;
        model.pose(0, 9.5F, 1, Skill.NONE, 0, 0, 0, 0, 0);
        assertTrue(Math.abs(model.front[0].xRot - frontA) > .5F && Math.abs(model.hind[0].xRot - hindA) > .5F, "his legs reach and gather as he runs");
        rest();
        float body = model.body.xRot, front = model.front[0].xRot;
        model.pose(0, 0, 0, Skill.HOOVES, SeereRules.HOOVES.timing().windup() - 2, 0, 0, 0, 0);
        assertTrue(model.body.xRot < body - .6F, "reared up: " + model.body.xRot);
        assertTrue(model.front[0].xRot < front - .6F, "his forehooves up");
        model.pose(0, 0, 0, Skill.SPIKES, SeereRules.SPIKES.timing().windup() - 4, 0, 0, 0, 0);
        Vector3f tip = tip();
        assertTrue(tip.y < back() - 10, "rearing for his spikes, his wings flung up: " + tip);
        model.pose(0, 0, 0, Skill.DIVE, SeereRules.DIVE.timing().windup() + 6, 1, 0, 0, 0);
        Vector3f shoulder = shoulder();
        tip = tip();
        assertTrue(tip.z > shoulder.z + 15, "diving, his wings swept back: " + tip);
    }

    @Test void dyingHeGoesDownOnHisSide() {
        rest();
        float y = model.body.y;
        model.pose(0, 0, 0, Skill.NONE, 0, 0, 0, 1, 0);
        assertTrue(model.body.zRot > 1, "on his side");
        assertTrue(model.body.y > y + 8, "down on the ground");
    }

    @Test void everyPoseEasesBackToRestOnceItsSkillIsOver() {
        rest();
        float[] rest = pose();
        for (Skill skill : Skill.values()) {
            model.pose(0, 0, 0, skill, SeereModel.length(skill) + 40, 0, 0, 0, 0);
            assertArrayEquals(rest, pose(), 1e-4F, skill + " settles back to rest");
        }
    }

    @Test void thePicturesHeNeedsAreThere() throws Exception {
        BufferedImage texture = image("/assets/corruption/textures/entity/seere.png"), glow = image("/assets/corruption/textures/entity/seere_glow.png");
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
        assertTrue(lit > 50, "his eyes, his sigils and the tips of his feathers glow: " + lit);
        BufferedImage bar = image("/assets/corruption/textures/gui/colossus_boss_bar.png");
        int drawn = 0, indigo = 0;
        for (int y = 72; y < 96; y++) for (int x = 128; x < 152; x++) {
            int pixel = bar.getRGB(x, y);
            if ((pixel >>> 24) == 0) continue;
            drawn++;
            if ((pixel & 255) > ((pixel >> 16) & 255) + 60) indigo++;
        }
        assertTrue(drawn > 150, "his emblem is on the boss bar, after Dantalion's");
        assertTrue(indigo > 5, "in his own indigo");
    }

    private static BufferedImage image(String path) throws Exception {
        try (InputStream in = SeereModelTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "Missing " + path);
            return ImageIO.read(in);
        }
    }
}
