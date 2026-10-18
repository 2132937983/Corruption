package com.miophas.corruption.core.loot;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Which prizes have been taken from which structures, as rules: each prize can be taken from each structure once. A
 * structure is named by its type and the chunk it started in, which no two structures of one type share in a level.
 * Each level keeps its own (common: ModAttachments.STRUCTURE_PRIZES), and the loot condition
 * corruption:once_per_structure takes from them (common.loot.OncePerStructure).
 */
public final class StructurePrizes {
    private final Set<String> taken = new LinkedHashSet<>();

    public StructurePrizes() {}

    /** As saved: the keys of the prizes taken (see {@link #key}). */
    public StructurePrizes(Collection<String> keys) { taken.addAll(keys); }

    /** Takes a prize from a structure if it is still there: whether it was. */
    public boolean take(String prize, String structure, int chunkX, int chunkZ) { return taken.add(key(prize, structure, chunkX, chunkZ)); }

    /** Whether a prize has been taken from a structure. */
    public boolean taken(String prize, String structure, int chunkX, int chunkZ) { return taken.contains(key(prize, structure, chunkX, chunkZ)); }

    /** What names one prize of one structure; the names of prizes and structures hold no spaces, so no two read alike. */
    public static String key(String prize, String structure, int chunkX, int chunkZ) { return prize + " " + structure + " " + chunkX + " " + chunkZ; }

    /** Every prize taken, as keys, in the order they were taken. */
    public Set<String> keys() { return Collections.unmodifiableSet(taken); }

    public boolean isEmpty() { return taken.isEmpty(); }
}
