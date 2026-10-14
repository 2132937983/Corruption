package com.miophas.corruption.common.client;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class GuiTilesTest {
    private static InputStream resource(String path) {
        InputStream in = GuiTilesTest.class.getResourceAsStream(path);
        assertNotNull(in, "Missing resource " + path);
        return in;
    }
    private static GuiTiles.Sheet shipped() throws Exception {
        try (InputStream in = resource("/assets/corruption/xml/gui_tiles.xml")) { return GuiTiles.read(in); }
    }
    private static GuiTiles.Sheet read(String xml) throws Exception { return GuiTiles.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))); }

    @Test void theListHasTheSixTilesOfGuiTiles() throws Exception {
        GuiTiles.Sheet sheet = shipped();
        assertEquals("corruption:textures/gui/gui_tiles.png", sheet.texture().toString());
        assertEquals(Map.of(
                "bg_progress_bar", new GuiTiles.Tile("bg_progress_bar", 0, 0, 22, 16), "progress_bar", new GuiTiles.Tile("progress_bar", 0, 16, 22, 16),
                "bg_loading_bar", new GuiTiles.Tile("bg_loading_bar", 22, 0, 14, 14), "loading_bar", new GuiTiles.Tile("loading_bar", 22, 14, 14, 14),
                "bg_corruption_energy_bar", new GuiTiles.Tile("bg_corruption_energy_bar", 36, 0, 68, 6),
                "corruption_energy_bar", new GuiTiles.Tile("corruption_energy_bar", 36, 6, 68, 6)), sheet.tiles());
    }

    @Test void theListAgreesWithThePicture() throws Exception {
        GuiTiles.Sheet sheet = shipped();
        BufferedImage picture;
        try (InputStream in = resource("/assets/corruption/textures/gui/gui_tiles.png")) { picture = ImageIO.read(in); }
        assertEquals(picture.getWidth(), sheet.width(), "the size the list gives is the picture's own");
        assertEquals(picture.getHeight(), sheet.height());
        for (GuiTiles.Tile tile : sheet.tiles().values()) {
            int drawn = 0;
            for (int y = tile.y(); y < tile.y() + tile.height(); y++)
                for (int x = tile.x(); x < tile.x() + tile.width(); x++) if ((picture.getRGB(x, y) >>> 24) != 0) drawn++;
            assertTrue(drawn > tile.width() * tile.height() / 5, tile.name() + " has something drawn in it: " + drawn);
            // Every filled bar has its empty look, the same size, to be drawn under it.
            if (!tile.name().startsWith(GuiTiles.EMPTY)) {
                GuiTiles.Tile empty = sheet.tile(GuiTiles.EMPTY + tile.name());
                assertEquals(tile.width(), empty.width(), tile.name()); assertEquals(tile.height(), empty.height(), tile.name());
            }
        }
    }

    @Test void aBrokenListIsRefusedWithItsReason() {
        String head = "<tiles texture=\"corruption:textures/gui/gui_tiles.png\" width=\"270\" height=\"90\">";
        assertThrows(IllegalArgumentException.class, () -> read(head + "<tile name=\"a\" x=\"260\" y=\"0\" width=\"22\" height=\"16\"/></tiles>"), "outside the texture");
        assertThrows(IllegalArgumentException.class, () -> read(head + "<tile name=\"a\" x=\"0\" y=\"0\" width=\"2\" height=\"2\"/><tile name=\"a\" x=\"2\" y=\"0\" width=\"2\" height=\"2\"/></tiles>"), "a name twice");
        assertThrows(IllegalArgumentException.class, () -> read(head + "<tile name=\"a\" x=\"zero\" y=\"0\" width=\"2\" height=\"2\"/></tiles>"), "not a number");
        assertThrows(IllegalArgumentException.class, () -> read("<sprites/>"), "not a tile list");
        // No DOCTYPE, so no entity can pull in a file from the player's disk.
        assertThrows(Exception.class, () -> read("<!DOCTYPE tiles [<!ENTITY secret SYSTEM \"file:///C:/Windows/win.ini\">]>" + head + "<tile name=\"&secret;\" x=\"0\" y=\"0\" width=\"2\" height=\"2\"/></tiles>"));
        assertThrows(IllegalArgumentException.class, () -> GuiTiles.GUI.tile("missing"));
    }
}
