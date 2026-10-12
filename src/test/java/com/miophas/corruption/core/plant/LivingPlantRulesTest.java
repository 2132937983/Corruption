package com.miophas.corruption.core.plant;

import com.miophas.corruption.api.plant.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LivingPlantRulesTest {
    @Test void explicitBonemealFaceSelectsEitherFreeSideAndNeverFallsBackThroughAnObstruction() {
        Space space=new Space();
        assertEquals(0,LivingPlantApi.direction(space));
        assertEquals(-1,LivingPlantApi.direction(space,-1));
        assertEquals(1,LivingPlantApi.direction(space,1));
        space.blocked.add("0:-1:0");
        assertEquals(0,LivingPlantApi.direction(space,-1));
        assertEquals(1,LivingPlantApi.direction(space,1));
        space.unloaded.add("0:1:0");
        assertEquals(0,LivingPlantApi.direction(space,1));
    }
    private static final class Space implements LivingPlantSpace {
        final Set<String> blocked = new HashSet<>(), unloaded = new HashSet<>(), decorations = new HashSet<>();
        int cleared, reads;
        String key(int x, int y, int z) { return x + ":" + y + ":" + z; }
        public boolean available(int x, int y, int z) { return !unloaded.contains(key(x, y, z)); }
        public boolean canOccupy(int x, int y, int z) {
            assertTrue(available(x, y, z), "Read an unloaded cell");
            reads++;
            return !blocked.contains(key(x, y, z));
        }
        public void clearDecoration(int x, int y, int z) { if (decorations.remove(key(x, y, z))) cleared++; }
    }
    @Test void growingCanopyReplacesDecorationsOnlyAfterTheWholeVolumePasses() {
        Space space = new Space();
        space.decorations.add("1:1:0");
        space.blocked.add("0:4:0");
        assertFalse(LivingPlantApi.prepareGrowth(LivingPlantKind.GREED, 1, 1, space));
        assertEquals(0, space.cleared);
        assertFalse(LivingPlantApi.canGrow(LivingPlantKind.GREED, 0, true, true, 1, space));
        space.blocked.clear();
        assertTrue(LivingPlantApi.canGrow(LivingPlantKind.GREED, 0, true, true, 1, space));
        assertTrue(LivingPlantApi.prepareGrowth(LivingPlantKind.GREED, 1, 1, space));
        assertEquals(1, space.cleared);
    }
    @Test void hangingGrowthChecksDownwardAndNeverReadsUnloadedCells() {
        Space space = new Space();
        space.unloaded.add("0:-4:0");
        assertFalse(LivingPlantApi.hasRoom(LivingPlantKind.GREED, 1, -1, space));
        space.unloaded.clear();
        space.blocked.add("0:4:0");
        assertTrue(LivingPlantApi.hasRoom(LivingPlantKind.GREED, 1, -1, space));
        assertFalse(LivingPlantApi.hasRoom(LivingPlantKind.GREED, 1, 1, space));
    }
    @Test void growthRejectsMaturityDeathOrLostSupportBeforeAnySpaceReads() {
        Space space = new Space();
        assertFalse(LivingPlantApi.canGrow(LivingPlantKind.GREED, 4, true, true, 1, space));
        assertFalse(LivingPlantApi.canGrow(LivingPlantKind.GREED, 0, false, true, 1, space));
        assertFalse(LivingPlantApi.canGrow(LivingPlantKind.GREED, 0, true, false, 1, space));
        assertEquals(0, space.reads);
    }
    @Test void independentSpeciesReserveDifferentCanopiesAndRejectAmbiguousAttachment() {
        Space space = new Space();
        space.blocked.add("1:1:0");
        assertTrue(LivingPlantApi.hasRoom(LivingPlantKind.MALICE_VINE, 1, 1, space));
        assertFalse(LivingPlantApi.hasRoom(LivingPlantKind.GREED, 1, 1, space));
        assertEquals(0, LivingPlantApi.direction(space));
        space.blocked.add("0:-1:0");
        assertEquals(1, LivingPlantApi.direction(space));
    }
}
