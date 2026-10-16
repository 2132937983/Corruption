package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.model.geom.ModelPart;
import org.junit.jupiter.api.Test;

/**
 * The spell bolt's baked model: its shell, its light and its heart are drawn apart, its rings turn against each other,
 * its shell turns slowly and its tail sways; it is as big as the box of the bolt it draws; and its texture is there.
 */
class SpellBoltModelTest {
    private final SpellBoltModel model = new SpellBoltModel(SpellBoltModel.createBodyLayer().bakeRoot());

    @Test void itsShellItsLightAndItsHeartAreDrawnApart() {
        assertEquals(SpellBoltModel.FINS, model.fins.length);
        assertTrue(model.shell.length > 0 && model.light.length > 0 && model.heart.length > 0);
        model.only(SpellBoltModel.SHELL);
        for (ModelPart part : model.shell) assertFalse(part.skipDraw);
        for (ModelPart part : model.light) assertTrue(part.skipDraw);
        for (ModelPart part : model.heart) assertTrue(part.skipDraw);
        model.only(SpellBoltModel.LIGHT);
        for (ModelPart part : model.light) assertFalse(part.skipDraw);
        for (ModelPart part : model.shell) assertTrue(part.skipDraw);
        model.only(SpellBoltModel.HEART);
        for (ModelPart part : model.heart) assertFalse(part.skipDraw);
        for (ModelPart part : model.light) assertTrue(part.skipDraw);
        model.only(-1);
        for (ModelPart part : model.all) assertFalse(part.skipDraw, "and all of it again");
    }

    @Test void itsRingsTurnAgainstEachOtherItsShellSlowlyAndItsTailSways() {
        model.pose(0);
        float front = model.ringFront.zRot, back = model.ringBack.zRot, fin = model.fins[0].zRot, tail = model.tailEnd.xRot, throb = model.crystal.xScale;
        model.pose(10);
        assertTrue(model.ringFront.zRot > front + 2, "the ring about its point turns one way");
        assertTrue(model.ringBack.zRot < back - 1.5F, "the other the other way");
        float turned = model.fins[0].zRot - fin;
        assertTrue(turned > 0 && turned < model.ringFront.zRot - front, "the shell turns, the slower");
        assertNotEquals(tail, model.tailEnd.xRot, 1e-4, "the tail sways");
        assertNotEquals(throb, model.crystal.xScale, 1e-4, "the crystal throbs");
        model.pose(0);
        assertEquals(front, model.ringFront.zRot, 1e-6, "and it all comes back to where it was");
    }

    /** The ring about its point is its widest: eleven pixels, drawn at the renderer's fit, the half block of a bolt's box. */
    @Test void itIsAsBigAsTheBoxOfTheBoltItDraws() {
        float low = Float.MAX_VALUE, high = -Float.MAX_VALUE;
        for (GeoModel.Bone bone : GeoModel.load(SpellBoltModel.GEOMETRY).bones())
            if (bone.name().startsWith("ring_front"))
                for (float[] cube : bone.cubes()) { low = Math.min(low, cube[0]); high = Math.max(high, cube[0] + cube[3]); }
        assertEquals(11, high - low, 1e-4, "eleven pixels across");
        assertEquals(8, (high - low) * SpellBoltRenderer.FIT, 1e-4, "eight, drawn: half a block");
    }

    @Test void itsTextureIsThereAndItsHeartIsDrawnWhiteHot() throws Exception {
        try (InputStream in = SpellBoltModelTest.class.getResourceAsStream("/assets/corruption/textures/entity/spell_bolt.png")) {
            assertNotNull(in);
            BufferedImage texture = ImageIO.read(in);
            assertEquals(128, texture.getWidth());
            assertEquals(64, texture.getHeight());
        }
        assertEquals(0xFFFFFF, SpellBoltRenderer.whiter(0xFFFFFF));
        assertEquals(0xA6A6A6, SpellBoltRenderer.whiter(0x000000), "black drawn most of the way to white");
        int rose = SpellBoltRenderer.whiter(0xC6728C);
        assertTrue((rose >> 16 & 255) > 0xC6 && (rose >> 8 & 255) > 0x72 && (rose & 255) > 0x8C, "every channel the brighter");
    }
}
