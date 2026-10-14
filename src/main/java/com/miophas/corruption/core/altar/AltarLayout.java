package com.miophas.corruption.core.altar;

/**
 * The altar's shape. An Altar Offering stands at its middle, and six Altar Pillars around it, on its own level,
 * at the corners of a hexagram: each given in blocks east, then north, of the offering. The first, four blocks
 * north, is slot 1 of the altar's recipes; the others follow it clockwise, seen from above. This is the altar
 * facing north; it may face any of the four ways, and all of it then turns with it (see {@link AltarFacing}).
 */
public final class AltarLayout {
    public static final int SLOTS = 6;
    /** How far east, then north, each pillar stands from the offering, slot 1 first. */
    private static final int[][] PILLARS = {{0, 4}, {4, 2}, {4, -2}, {0, -4}, {-4, -2}, {-4, 2}};
    /** The hexagram: its two triangles, each three pillars by their place in the order above. */
    public static final int[][] TRIANGLES = {{0, 2, 4}, {1, 3, 5}};
    /** The farthest any pillar stands from the offering along either axis. */
    public static final int REACH = 4;

    /** Whether a pillar stands at a place, given in blocks east and north of the offering. */
    public interface Place { boolean pillarAt(int east, int north); }

    public static int east(int slot) { return PILLARS[slot][0]; }
    public static int north(int slot) { return PILLARS[slot][1]; }

    /** Whether all six pillars stand where they should. */
    public static boolean formed(Place place) {
        for (int slot = 0; slot < SLOTS; slot++) if (!place.pillarAt(east(slot), north(slot))) return false;
        return true;
    }

    /** Whether a place lies on the altar's ground: the square from -REACH to REACH both ways round the offering. */
    public static boolean onGround(int east, int north) { return Math.abs(east) <= REACH && Math.abs(north) <= REACH; }

    /** Whether the altar itself stands at a place: its offering, or one of its pillars. */
    public static boolean isAltar(int east, int north) { return east == 0 && north == 0 || slotAt(east, north) >= 0; }

    /** The slot a pillar at this place fills, counting from 0, or -1 if no pillar belongs there. */
    public static int slotAt(int east, int north) {
        for (int slot = 0; slot < SLOTS; slot++) if (east(slot) == east && north(slot) == north) return slot;
        return -1;
    }

    private AltarLayout() {}
}
