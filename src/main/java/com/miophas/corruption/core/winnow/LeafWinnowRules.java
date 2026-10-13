package com.miophas.corruption.core.winnow;

import com.miophas.corruption.api.winnow.LeafWinnowDrop;
import com.miophas.corruption.api.winnow.LeafWinnowTable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Leaf winnowing rules: the per-block tables a data pack defines and the rolls that pick from them.
 *
 * <p>The tables live here rather than in common so the rules stay independent of NeoForge events;
 * common's data pack listener rebuilds them on every reload.
 */
public final class LeafWinnowRules {
    /** Replaced wholesale on reload; blocks and tags are filed separately. */
    private static final Map<String, LeafWinnowTable> BLOCKS = new HashMap<>();
    private static final Map<String, LeafWinnowTable> TAGS = new HashMap<>();

    public static void clear() { BLOCKS.clear(); TAGS.clear(); }

    public static void register(LeafWinnowTable table) {
        (table.tagged() ? TAGS : BLOCKS).put(table.key(), table);
    }

    public static Optional<LeafWinnowTable> table(String block) { return Optional.ofNullable(BLOCKS.get(block)); }

    public static Optional<LeafWinnowTable> tagTable(String tag) { return Optional.ofNullable(TAGS.get(tag)); }

    public static List<LeafWinnowDrop> drops(String block) {
        LeafWinnowTable table = BLOCKS.get(block);
        return table == null ? List.of() : table.drops();
    }

    public static List<LeafWinnowDrop> tagDrops(String tag) {
        LeafWinnowTable table = TAGS.get(tag);
        return table == null ? List.of() : table.drops();
    }

    /** Extra items a fortune level adds to one successful entry; fortune 0 adds nothing. Roll must be in [0, 1). */
    public static int fortuneBonus(int fortune, float roll) {
        if (fortune <= 0) return 0;
        return Math.min(fortune, (int) (Math.clamp(roll, 0F, 1F) * (fortune + 1)));
    }

    /** Amount handed out for one entry; 0 when its chance fails. All rolls must be in [0, 1). */
    public static int amount(LeafWinnowDrop drop, float chanceRoll, float countRoll, int bonus) {
        if (drop.impossible() || chanceRoll >= drop.chance()) return 0;
        int span = drop.max() - drop.min() + 1;
        int base = drop.min() + Math.min(span - 1, (int) (Math.clamp(countRoll, 0F, 1F) * span));
        return base + Math.max(0, bonus);
    }

    private LeafWinnowRules() {}
}
