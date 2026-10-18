package com.miophas.corruption.common.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Which of Solomon's dragons have fallen, kept with the world: a dragon that falls tells its seal here as well as to its face,
 * so that its seal learns of it even if it lay out of reach when it fell (unloaded with the ground it hung over). The seal reads
 * it whenever it looks over its dragons, and forgets its own page once it has fallen itself.
 */
public final class SolomonLedger extends SavedData {
    private static final String NAME = "corruption_solomon";
    private static final Factory<SolomonLedger> FACTORY = new Factory<>(SolomonLedger::new, SolomonLedger::read);

    private final Map<UUID, TreeSet<Integer>> fallen = new HashMap<>();

    public static SolomonLedger of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(FACTORY, NAME); }

    /** The dragons of this seal's known to have fallen, by their place among its eight. */
    public List<Integer> fallen(UUID seal) {
        TreeSet<Integer> page = fallen.get(seal);
        return page == null ? List.of() : List.copyOf(page);
    }

    /** One of this seal's dragons has fallen. */
    public void fell(UUID seal, int dragon) {
        if (fallen.computeIfAbsent(seal, ignored -> new TreeSet<>()).add(dragon)) setDirty();
    }

    /** This seal has fallen itself: its page is done with. */
    public void forget(UUID seal) {
        if (fallen.remove(seal) != null) setDirty();
    }

    private static SolomonLedger read(CompoundTag tag, HolderLookup.Provider registries) {
        SolomonLedger ledger = new SolomonLedger();
        for (String key : tag.getAllKeys()) {
            try {
                TreeSet<Integer> page = new TreeSet<>();
                for (int dragon : tag.getIntArray(key)) page.add(dragon);
                ledger.fallen.put(UUID.fromString(key), page);
            } catch (IllegalArgumentException notAKey) { /* a page with no seal to it is passed over */ }
        }
        return ledger;
    }

    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        fallen.forEach((seal, page) -> tag.put(seal.toString(), new IntArrayTag(page.stream().mapToInt(Integer::intValue).toArray())));
        return tag;
    }
}
