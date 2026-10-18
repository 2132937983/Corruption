package com.miophas.corruption.core.altar;

import com.miophas.corruption.core.cooking.ShapelessMatch;

/**
 * Whether what is at the altar makes a rite: what lies on its pillars, what stands on its offering, the ground
 * round it, and the players there. Shaped, each pillar holds what its slot asks for and the pillars asked for
 * nothing hold nothing; shapeless, what is asked for may lie on any pillars, one thing to a pillar, with
 * nothing left over. Whether a pillar's item meets an ingredient, or a block what is asked, is for the caller
 * to say.
 */
public final class RitualMatch {
    /**
     * Shaped: the pillars a recipe asks for hold what they are asked, and the rest hold nothing. Answers the
     * pillars used, in slot order, or null.
     *
     * @param asked which pillars the recipe asks for something on
     * @param fits whether the pillar in this slot holds what the recipe asks of that slot; asked the slot twice
     */
    public static int[] shaped(boolean[] asked, boolean[] filled, ShapelessMatch.Fits fits) {
        int count = 0;
        for (int slot = 0; slot < asked.length; slot++) {
            if (asked[slot] != filled[slot]) return null;
            if (asked[slot]) {
                if (!fits.fits(slot, slot)) return null;
                count++;
            }
        }
        int[] used = new int[count];
        for (int slot = 0, at = 0; slot < asked.length; slot++) if (asked[slot]) used[at++] = slot;
        return used;
    }

    /** Shapeless: for each ingredient, the pillar that holds it, or null if they cannot all be found with nothing left over. */
    public static int[] shapeless(int ingredients, boolean[] filled, ShapelessMatch.Fits fits) { return ShapelessMatch.match(ingredients, filled, fits); }

    /** A place on the altar's ground, in blocks east and north of the offering. */
    public interface Place { boolean test(int east, int north); }

    /**
     * Whether the altar's ground is as a rite asks: where it asks for a block, that block stands; everywhere
     * else on the ground, the offering and its pillars aside, nothing does.
     *
     * @param asks whether the rite asks for a block at a place
     * @param met whether the block asked for there stands there
     * @param empty whether nothing stands at a place
     */
    public static boolean ground(Place asks, Place met, Place empty) { return ground(AltarForm.HEXAGRAM, asks, met, empty); }

    /** As {@link #ground(Place, Place, Place)}, round an altar of this shape: its own offering and pillars aside. */
    public static boolean ground(AltarForm form, Place asks, Place met, Place empty) {
        for (int east = -AltarLayout.REACH; east <= AltarLayout.REACH; east++)
            for (int north = -AltarLayout.REACH; north <= AltarLayout.REACH; north++) {
                if (form.isAltar(east, north)) continue;
                if (asks.test(east, north) ? !met.test(east, north) : !empty.test(east, north)) return false;
            }
        return true;
    }

    /**
     * Whether the players at the altar meet a rite's corruption: any, or none, if it asks for none (0);
     * otherwise one of them corrupted at least that far.
     *
     * @param present each player's corruption
     */
    public static boolean corruption(int asked, int... present) {
        if (asked <= 0) return true;
        for (int value : present) if (value >= asked) return true;
        return false;
    }

    /** Whether what stands on the offering meets a recipe: anything, or nothing, if it asks for no creature; otherwise one of the kind asked, by id. */
    public static boolean offering(String asked, String present) { return asked == null || asked.equals(present); }

    private RitualMatch() {}
}
