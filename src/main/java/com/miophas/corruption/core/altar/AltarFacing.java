package com.miophas.corruption.core.altar;

/**
 * Which way an altar faces: the way its first pillar stands from its offering. Places on an altar
 * ({@link AltarLayout}, and the ground its rites ask for blocks on) are given as though it faced north, in
 * blocks east and north of the offering; facing another way, the whole altar turns with it, clockwise seen from
 * above, so that its pillars still follow one another clockwise from the first.
 */
public enum AltarFacing {
    NORTH, EAST, SOUTH, WEST;

    /** How many quarter turns, clockwise seen from above, it is turned from facing north. */
    public int quarterTurns() { return ordinal(); }

    /**
     * Where a place on the altar lies in the world: given in blocks east and north of the offering as though
     * the altar faced north, answers how far east, then north, of it the place lies with the altar facing this way.
     */
    public int[] inWorld(int east, int north) {
        return switch (this) {
            case NORTH -> new int[]{east, north};
            case EAST -> new int[]{north, -east};
            case SOUTH -> new int[]{-east, -north};
            case WEST -> new int[]{-north, east};
        };
    }
}
