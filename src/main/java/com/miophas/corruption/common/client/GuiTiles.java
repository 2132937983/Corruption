package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Named pieces of a GUI texture, as listed in an XML file under assets/&lt;namespace&gt;/xml/. Screens draw
 * them by name, so the picture can be rearranged (or replaced by a resource pack) by editing the list
 * rather than the code. {@link #GUI} is textures/gui/gui_tiles.png, listed in xml/gui_tiles.xml:
 * <pre>
 * &lt;tiles texture="corruption:textures/gui/gui_tiles.png" width="270" height="90"&gt;
 *     &lt;tile name="progress_bar" x="0" y="16" width="22" height="16"/&gt;
 * &lt;/tiles&gt;</pre>
 * The list is read again whenever resources are reloaded.
 */
public final class GuiTiles implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    /** The tiles of textures/gui/gui_tiles.png: the progress, loading and Corruption Energy bars. */
    public static final GuiTiles GUI = new GuiTiles(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "xml/gui_tiles.xml"));
    /** The prefix of a bar's empty look: "bg_loading_bar" is the empty "loading_bar". */
    public static final String EMPTY = "bg_";

    /** A piece of the texture, in its pixels. */
    public record Tile(String name, int x, int y, int width, int height) {}
    /** A texture and its tiles by name. */
    public record Sheet(ResourceLocation texture, int width, int height, Map<String, Tile> tiles) {
        static final Sheet NONE = new Sheet(ResourceLocation.withDefaultNamespace("missingno"), 1, 1, Map.of());
        public Tile tile(String name) {
            Tile tile = tiles.get(name);
            if (tile == null) throw new IllegalArgumentException("No tile " + name + " on " + texture);
            return tile;
        }
    }
    /** Which way a bar fills. */
    public enum Fill { LEFT_TO_RIGHT, BOTTOM_TO_TOP }

    private final ResourceLocation list;
    private volatile Sheet sheet = Sheet.NONE;

    public GuiTiles(ResourceLocation list) { this.list = list; }

    public Sheet sheet() { return sheet; }
    public Tile tile(String name) { return sheet.tile(name); }

    /**
     * Reads a tile list. External entities and DOCTYPEs are refused: the file comes from resource packs,
     * and a tile list has no need of them.
     *
     * @throws IllegalArgumentException if a tile is unnamed, named twice, or lies outside the texture
     */
    public static Sheet read(InputStream xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        Element root = factory.newDocumentBuilder().parse(xml).getDocumentElement();
        if (!root.getTagName().equals("tiles")) throw new IllegalArgumentException("A tile list's root is <tiles>, not <" + root.getTagName() + ">");
        ResourceLocation texture = ResourceLocation.parse(root.getAttribute("texture"));
        int width = number(root, "width"), height = number(root, "height");
        Map<String, Tile> tiles = new LinkedHashMap<>();
        NodeList entries = root.getElementsByTagName("tile");
        for (int index = 0; index < entries.getLength(); index++) {
            Element entry = (Element) entries.item(index);
            String name = entry.getAttribute("name");
            Tile tile = new Tile(name, number(entry, "x"), number(entry, "y"), number(entry, "width"), number(entry, "height"));
            if (name.isEmpty()) throw new IllegalArgumentException("A tile on " + texture + " has no name");
            if (tile.x + tile.width > width || tile.y + tile.height > height || tile.width == 0 || tile.height == 0)
                throw new IllegalArgumentException("Tile " + name + " does not lie inside the " + width + "x" + height + " texture");
            if (tiles.put(name, tile) != null) throw new IllegalArgumentException("Two tiles are named " + name);
        }
        return new Sheet(texture, width, height, Map.copyOf(tiles));
    }

    private static int number(Element element, String attribute) {
        String text = element.getAttribute(attribute);
        try {
            int value = Integer.parseInt(text.trim());
            if (value < 0) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException bad) {
            throw new IllegalArgumentException("<" + element.getTagName() + " " + attribute + "=\"" + text + "\"> is not a whole number of pixels");
        }
    }

    @Override public void onResourceManagerReload(ResourceManager resources) {
        try (InputStream in = resources.open(list)) {
            sheet = read(in);
        } catch (Exception failure) {
            LOGGER.error("Could not read the GUI tiles in {}", list, failure);
            sheet = Sheet.NONE;
        }
    }

    /** Draws a whole tile with its top left corner here. Nothing is drawn for a tile the list does not have. */
    public void draw(GuiGraphics gui, String name, int x, int y) { draw(gui, name, x, y, 1, Fill.LEFT_TO_RIGHT); }

    /** Draws the part of a tile a bar this full shows: from the left, or from the bottom up. */
    public void draw(GuiGraphics gui, String name, int x, int y, float fraction, Fill fill) {
        Sheet current = sheet;
        Tile tile = current.tiles.get(name);
        if (tile == null || fraction <= 0) return;
        fraction = Math.min(fraction, 1);
        if (fill == Fill.LEFT_TO_RIGHT) {
            int width = Mth.ceil(tile.width * fraction);
            gui.blit(current.texture, x, y, tile.x, tile.y, width, tile.height, current.width, current.height);
        } else {
            int height = Mth.ceil(tile.height * fraction);
            gui.blit(current.texture, x, y + tile.height - height, tile.x, tile.y + tile.height - height, tile.width, height, current.width, current.height);
        }
    }

    /** A bar: its empty look ({@link #EMPTY} + name), then as much of the full one over it as it is filled. */
    public void bar(GuiGraphics gui, String name, int x, int y, float fraction, Fill fill) {
        draw(gui, EMPTY + name, x, y);
        draw(gui, name, x, y, fraction, fill);
    }
}
