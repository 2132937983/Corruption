package com.miophas.corruption.core.cooking;

import com.miophas.corruption.core.energy.CorruptionEnergy;

/**
 * How the brass cauldron cooks. Up to seven things go in, in any order (see {@link ShapelessMatch}), with a
 * fluid from its tank if the recipe wants one, and Corruption Energy if the recipe costs any. The energy is
 * drawn a share each tick as it cooks, as the arcane transmuter draws it; the ingredients and the fluid are
 * used up when it is done.
 */
public final class BrassCauldronRules {
    /** How many things it cooks together: the six corners of a hexagon and its middle. */
    public static final int INPUTS = 7;
    /** The most Corruption Energy it holds. */
    public static final long CAPACITY = CorruptionEnergy.kilo(50);
    /** How much fluid its tank holds, in millibuckets: four buckets. */
    public static final int TANK = 4000;
    /** How long a recipe cooks unless it says otherwise. */
    public static final int DEFAULT_COOK_TICKS = 100;
    /** The longest a recipe may cook; the screen is told the time in sixteen bits. */
    public static final int MAX_COOK_TICKS = Short.MAX_VALUE;

    /**
     * Whether the parchment in the cauldron meets a recipe: any parchment, or none, if the recipe asks for no
     * inscription; otherwise one bearing the very string asked for. A parchment is never used up: it is the
     * rune the cauldron cooks by, and stays for the next batch.
     *
     * @param asked the string the recipe asks for, by id, or null for none
     * @param borne the string the parchment there bears, or null if there is no inscribed parchment
     */
    public static boolean inscribed(String asked, String borne) { return asked == null || asked.equals(borne); }

    private BrassCauldronRules() {}
}
