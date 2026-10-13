package com.miophas.corruption.api.winnow;

import com.miophas.corruption.core.winnow.LeafWinnowRules;
import java.util.List;
import java.util.Optional;

/** Bridge to the core leaf winnowing rules, so winnower tools and add-ons share one set of tables. */
public final class LeafWinnowApi {
    /** Called by the data pack loader before it registers a freshly parsed batch of tables. */
    public static void clear() { LeafWinnowRules.clear(); }

    public static void register(LeafWinnowTable table) { LeafWinnowRules.register(table); }

    public static Optional<LeafWinnowTable> table(String block) { return LeafWinnowRules.table(block); }

    public static Optional<LeafWinnowTable> tagTable(String tag) { return LeafWinnowRules.tagTable(tag); }

    /** Entries the given block hands out, or an empty list when no table covers it. */
    public static List<LeafWinnowDrop> drops(String block) { return LeafWinnowRules.drops(block); }

    /** Entries every block in the given block tag hands out; empty when no table covers the tag. */
    public static List<LeafWinnowDrop> tagDrops(String tag) { return LeafWinnowRules.tagDrops(tag); }

    /** Extra items a fortune level adds to one successful entry; fortune 0 adds nothing. Roll must be in [0, 1). */
    public static int fortuneBonus(int fortune, float roll) { return LeafWinnowRules.fortuneBonus(fortune, roll); }

    /** Amount handed out for one entry; 0 when its chance fails. All rolls must be in [0, 1). */
    public static int amount(LeafWinnowDrop drop, float chanceRoll, float countRoll, int bonus) {
        return LeafWinnowRules.amount(drop, chanceRoll, countRoll, bonus);
    }

    private LeafWinnowApi() {}
}
