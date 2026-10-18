package com.miophas.corruption.core.altar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The map of a structure built round an altar, as data writes it: layers, each at a height above the offering
 * (0 its own level, -1 the level under it), drawn as rows from north to south, each row a line of symbols from
 * west to east, as seen from above with the altar facing north. The offering stands at the middle of every
 * layer, so each has an odd number of rows, all as long and of an odd length. A symbol names a block in the
 * structure's key; {@link #ANY} (or a space) leaves a place to anything. A structure is built round an altar of
 * one shape ({@link AltarForm}): in the layer at the offering's level, {@link #OFFERING} and {@link #PILLAR} may
 * mark where that altar itself stands, and nothing else may be asked of its ground ({@link AltarLayout#onGround}):
 * that belongs to the rites.
 */
public final class AltarStructureMap {
    public static final char ANY = '.';
    public static final char OFFERING = '@';
    public static final char PILLAR = 'P';
    /** How far from the offering, every way across, and down or up, a structure may reach. */
    public static final int REACH = 16;

    /** A layer as data writes it: its height above the offering, and its rows from north to south. */
    public record Layer(int up, List<String> rows) {
        public Layer { rows = List.copyOf(rows); }
    }

    /** A place the structure asks for a block at, in blocks east, up and north of the offering as though the altar faced north. */
    public record Cell(int east, int up, int north, char symbol) {}

    /** Whether a symbol may name a block in the key: any but those that mark nothing to ask for. */
    public static boolean keySymbol(char symbol) { return symbol != ANY && symbol != ' ' && symbol != OFFERING && symbol != PILLAR; }

    /** What is wrong with a map round a hexagram altar, given the symbols its key names; null if nothing. */
    public static String problem(List<Layer> layers, Set<Character> key) { return problem(layers, key, AltarForm.HEXAGRAM); }

    /** What is wrong with a map round an altar of this shape, given the symbols its key names; null if nothing. */
    public static String problem(List<Layer> layers, Set<Character> key, AltarForm form) {
        if (layers.isEmpty()) return "A structure has at least one layer";
        for (char symbol : key) if (!keySymbol(symbol)) return "'" + symbol + "' cannot name a block: it marks nothing, the offering or a pillar";
        Set<Integer> heights = new HashSet<>();
        Set<Character> used = new HashSet<>();
        for (Layer layer : layers) {
            if (!heights.add(layer.up())) return "Two layers at " + layer.up();
            if (Math.abs(layer.up()) > REACH) return "A layer at " + layer.up() + " is more than " + REACH + " from the offering";
            int depth = layer.rows().size();
            if (depth % 2 == 0) return "The layer at " + layer.up() + " has " + depth + " rows, not an odd number with the offering in the middle";
            int width = layer.rows().get(0).length();
            for (String row : layer.rows())
                if (row.length() != width) return "The rows of the layer at " + layer.up() + " are not all as long";
            if (width % 2 == 0) return "The rows of the layer at " + layer.up() + " are " + width + " long, not an odd length with the offering in the middle";
            if (width / 2 > REACH || depth / 2 > REACH) return "The layer at " + layer.up() + " reaches more than " + REACH + " from the offering";
            for (int row = 0; row < depth; row++)
                for (int column = 0; column < width; column++) {
                    char symbol = layer.rows().get(row).charAt(column);
                    int east = column - width / 2, north = depth / 2 - row;
                    String where = " at " + east + ", " + layer.up() + ", " + north;
                    if (symbol == OFFERING) {
                        if (layer.up() != 0 || east != 0 || north != 0) return "The offering stands at 0, 0, 0, not" + where;
                    } else if (symbol == PILLAR) {
                        if (layer.up() != 0 || form.slotAt(east, north) < 0) return "No pillar of the " + form.name().toLowerCase(java.util.Locale.ROOT) + " stands" + where;
                    } else if (keySymbol(symbol)) {
                        if (!key.contains(symbol)) return "'" + symbol + "'" + where + " is not in the key";
                        if (layer.up() == 0 && AltarLayout.onGround(east, north))
                            return "A block asked for" + where + " lies on the altar's ground, which belongs to its rites";
                        used.add(symbol);
                    }
                }
        }
        for (char symbol : key) if (!used.contains(symbol)) return "'" + symbol + "' is in the key but nowhere in the map";
        if (used.isEmpty()) return "A structure asks for at least one block";
        return null;
    }

    /** The places a map asks for blocks at, layer by layer as written, each row west to east. */
    public static List<Cell> cells(List<Layer> layers) {
        List<Cell> cells = new ArrayList<>();
        for (Layer layer : layers) {
            int depth = layer.rows().size();
            for (int row = 0; row < depth; row++) {
                String line = layer.rows().get(row);
                for (int column = 0; column < line.length(); column++)
                    if (keySymbol(line.charAt(column))) cells.add(new Cell(column - line.length() / 2, layer.up(), depth / 2 - row, line.charAt(column)));
            }
        }
        return List.copyOf(cells);
    }

    private AltarStructureMap() {}
}
