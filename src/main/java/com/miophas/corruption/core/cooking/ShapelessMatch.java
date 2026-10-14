package com.miophas.corruption.core.cooking;

/**
 * Shapeless matching, as a pot cooks: what goes in may lie in any slot, in any order. Each of a recipe's
 * ingredients must be met by a slot of its own, and every slot with something in it must meet one, so nothing
 * is left over and nothing counts twice. Whether a slot's stack meets an ingredient (its amount included) is
 * for the caller to say.
 */
public final class ShapelessMatch {
    /** Whether the stack in a slot meets an ingredient. */
    public interface Fits { boolean fits(int ingredient, int slot); }

    /**
     * For each ingredient, the slot that meets it; null if they cannot all be met with nothing left over.
     *
     * @param filled which slots hold something
     */
    public static int[] match(int ingredients, boolean[] filled, Fits fits) {
        int held = 0;
        for (boolean slot : filled) if (slot) held++;
        if (ingredients < 0 || held != ingredients) return null;
        int[] slots = new int[ingredients];
        return place(0, filled, new boolean[filled.length], slots, fits) ? slots : null;
    }

    /** Finds a slot for this ingredient and every one after it, trying again with another slot when a later one cannot be met. */
    private static boolean place(int ingredient, boolean[] filled, boolean[] taken, int[] slots, Fits fits) {
        if (ingredient == slots.length) return true;
        for (int slot = 0; slot < filled.length; slot++) {
            if (!filled[slot] || taken[slot] || !fits.fits(ingredient, slot)) continue;
            taken[slot] = true;
            slots[ingredient] = slot;
            if (place(ingredient + 1, filled, taken, slots, fits)) return true;
            taken[slot] = false;
        }
        return false;
    }

    private ShapelessMatch() {}
}
