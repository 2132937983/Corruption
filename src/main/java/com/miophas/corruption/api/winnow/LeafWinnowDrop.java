package com.miophas.corruption.api.winnow;

/**
 * One outcome a winnowing table can hand out.
 *
 * @param item   registry name of the item that drops
 * @param chance probability in [0, 1] that this entry drops at all; 1 always drops
 * @param min    smallest amount handed out when the entry succeeds
 * @param max    largest amount handed out when the entry succeeds
 */
public record LeafWinnowDrop(String item, float chance, int min, int max) {
    public LeafWinnowDrop {
        chance = Math.clamp(chance, 0F, 1F);
        min = Math.max(1, min);
        max = Math.max(min, max);
    }

    /** True when this entry can never fail, so no chance roll is needed. */
    public boolean certain() { return chance >= 1F; }

    /** True when this entry can never drop, so it can be skipped outright. */
    public boolean impossible() { return chance <= 0F; }
}
