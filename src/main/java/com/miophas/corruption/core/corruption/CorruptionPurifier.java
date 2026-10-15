package com.miophas.corruption.core.corruption;

/**
 * Something taken to lower a player's corruption, drunk or eaten and used up: purified water, or an add-on's elixir.
 * This is only how much it lowers it by, and what the corruption comes to after it; the item itself is
 * api.corruption.CorruptionPurifyingItem.
 *
 * @param amount how many points of corruption it takes away, none or more
 */
public record CorruptionPurifier(int amount) {
    public CorruptionPurifier {
        if (amount < 0) throw new IllegalArgumentException("A purifier cannot add corruption: " + amount);
    }

    /** What a player's corruption comes to once this has been taken: lower by its amount, never below none. */
    public int purify(int corruption) { return PlayerCorruptionRules.decrease(corruption, amount); }
}
