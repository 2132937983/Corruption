package com.miophas.corruption.common.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/** Finds the structure a place stands in, among some, by its pieces: as vanilla finds a fortress for its spawns. */
final class StructureLookup {
    /** The structure among these with a piece here, or {@link StructureStart#INVALID_START}. */
    static StructureStart at(ServerLevel level, BlockPos pos, HolderSet<Structure> structures) {
        return level.structureManager().getStructureWithPieceAt(pos, structure -> among(structures, structure));
    }

    /** Whether a structure is one of these. A listed one is matched by its name, whichever lookup the list was read with. */
    static boolean among(HolderSet<Structure> structures, Holder<Structure> structure) {
        return structures.unwrap().map(tag -> structure.is(tag),
                listed -> listed.stream().anyMatch(wanted -> wanted.unwrapKey().isPresent() && wanted.unwrapKey().equals(structure.unwrapKey())));
    }

    private StructureLookup() {}
}
