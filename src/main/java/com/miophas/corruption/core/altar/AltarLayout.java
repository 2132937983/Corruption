package com.miophas.corruption.core.altar;

/**
 * The altar's shape. An Altar Offering stands at its middle, and Altar Pillars around it, on its own level: six
 * at the corners of a hexagram, or eight at the corners of an octagram ({@link AltarForm}). Each is given in blocks
 * east, then north, of the offering. The first, four blocks north, is slot 1 of the altar's recipes; the others
 * follow it clockwise, seen from above. This is the altar facing north; it may face any of the four ways, and all
 * of it then turns with it (see {@link AltarFacing}).
 *
 * The slots and places here are the hexagram's, as they were before there was an octagram.
 */
public final class AltarLayout {
    public static final int SLOTS = AltarForm.HEXAGRAM.slots();
    /** The hexagram: its two triangles, each three pillars by their place in the order above. */
    public static final int[][] TRIANGLES = {{0, 2, 4}, {1, 3, 5}};
    /** The farthest any pillar stands from the offering along either axis, either shape. */
    public static final int REACH = 4;

    /** Whether a pillar stands at a place, given in blocks east and north of the offering. */
    public interface Place { boolean pillarAt(int east, int north); }

    public static int east(int slot) { return AltarForm.HEXAGRAM.east(slot); }
    public static int north(int slot) { return AltarForm.HEXAGRAM.north(slot); }

    /** Whether all six of the hexagram's pillars stand where they should. */
    public static boolean formed(Place place) { return AltarForm.HEXAGRAM.formed(place); }

    /** Whether a place lies on the altar's ground: the square from -REACH to REACH both ways round the offering. */
    public static boolean onGround(int east, int north) { return Math.abs(east) <= REACH && Math.abs(north) <= REACH; }

    /** Whether the hexagram altar itself stands at a place: its offering, or one of its pillars. */
    public static boolean isAltar(int east, int north) { return AltarForm.HEXAGRAM.isAltar(east, north); }

    /** The slot a hexagram's pillar at this place fills, counting from 0, or -1 if none belongs there. */
    public static int slotAt(int east, int north) { return AltarForm.HEXAGRAM.slotAt(east, north); }

    private AltarLayout() {}
}
