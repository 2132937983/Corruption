package com.miophas.corruption.api.winnow;

import java.util.List;

/**
 * One winnowing table: what it covers and what it hands out, in the order the data pack lists it.
 *
 * @param target registry name of the block this table winnows, or of a block tag when
 *               {@link #tagged()} is true, written with a leading {@code #}
 * @param drops  the entries rolled whenever a winnower is used on a matching block
 */
public record LeafWinnowTable(String target, List<LeafWinnowDrop> drops) {
    public LeafWinnowTable {
        drops = List.copyOf(drops);
    }

    /** True when the target names a block tag, so every block in it shares this table. */
    public boolean tagged() { return target.startsWith("#"); }

    /** Name the table is filed under, with the tag marker removed. */
    public String key() { return tagged() ? target.substring(1) : target; }
}
