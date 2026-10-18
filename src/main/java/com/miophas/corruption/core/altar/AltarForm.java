package com.miophas.corruption.core.altar;

/**
 * The shapes an altar may take: where its pillars stand round its offering, on the offering's level, each given
 * in blocks east, then north, of it as though the altar faced north. Either way the first pillar stands four
 * blocks north, and the others follow it clockwise, seen from above; the altar's ground is the same square
 * ({@link AltarLayout#REACH}). Its rites are made for one shape or the other.
 *
 * <ul>
 * <li>{@link #HEXAGRAM}: six pillars at the corners of a six-pointed star, two triangles.</li>
 * <li>{@link #OCTAGRAM}: eight pillars at the corners of an eight-pointed star, each joined to the third after it.
 *     Four stand four blocks out along the ways of the compass, and four three blocks out both ways between them,
 *     as near a circle as whole blocks allow.</li>
 * </ul>
 */
public enum AltarForm {
    HEXAGRAM(2, new int[][]{{0, 4}, {4, 2}, {4, -2}, {0, -4}, {-4, -2}, {-4, 2}}),
    OCTAGRAM(3, new int[][]{{0, 4}, {3, 3}, {4, 0}, {3, -3}, {0, -4}, {-3, -3}, {-4, 0}, {-3, 3}});

    /** The most pillars any altar has. */
    public static final int MOST_SLOTS = 8;

    private final int step;
    private final int[][] pillars;

    AltarForm(int step, int[][] pillars) {
        this.step = step;
        this.pillars = pillars;
    }

    public int slots() { return pillars.length; }
    public int east(int slot) { return pillars[slot][0]; }
    public int north(int slot) { return pillars[slot][1]; }
    /** How its star is drawn: each pillar's corner joined to the one this many after it, clockwise. */
    public int step() { return step; }

    /** Whether all its pillars stand where they should. */
    public boolean formed(AltarLayout.Place place) {
        for (int slot = 0; slot < slots(); slot++) if (!place.pillarAt(east(slot), north(slot))) return false;
        return true;
    }

    /** The slot a pillar at this place fills, counting from 0, or -1 if none of its pillars belongs there. */
    public int slotAt(int east, int north) {
        for (int slot = 0; slot < slots(); slot++) if (east(slot) == east && north(slot) == north) return slot;
        return -1;
    }

    /** Whether the altar itself stands at a place: its offering, or one of its pillars. */
    public boolean isAltar(int east, int north) { return east == 0 && north == 0 || slotAt(east, north) >= 0; }

    /** The shape with this many pillars, or null if none has. */
    public static AltarForm withSlots(int slots) {
        for (AltarForm form : values()) if (form.slots() == slots) return form;
        return null;
    }

    /**
     * Which shape stands, given where pillars stand: the octagram if all eight of its pillars do, else the hexagram
     * if its six do, else null. They share the pillars north and south of the offering, so a hexagram's are never
     * all an octagram's.
     */
    public static AltarForm standing(AltarLayout.Place place) {
        if (OCTAGRAM.formed(place)) return OCTAGRAM;
        return HEXAGRAM.formed(place) ? HEXAGRAM : null;
    }
}
