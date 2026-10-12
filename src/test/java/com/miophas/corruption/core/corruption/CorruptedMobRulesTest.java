package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.CorruptedMobApi;
import com.miophas.corruption.api.corruption.CorruptedMobDefinition;
import com.miophas.corruption.api.corruption.CorruptedMobFamily;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CorruptedMobRulesTest {
    @Test void everyFamilyKeepsItsSpeciesWithUniqueIdsAndSources() {
        assertEquals(5, CorruptedMobApi.family(CorruptedMobFamily.DEDICATED).size());
        assertEquals(6, CorruptedMobApi.family(CorruptedMobFamily.LIVESTOCK).size());
        assertEquals(7, CorruptedMobApi.family(CorruptedMobFamily.VILLAGE).size());
        assertEquals(14, CorruptedMobApi.family(CorruptedMobFamily.NETHER).size());
        assertEquals(49, CorruptedMobApi.family(CorruptedMobFamily.WILDLIFE).size());
        for (var family : CorruptedMobFamily.values()) {
            var ids = new HashSet<String>(); var sources = new HashSet<String>();
            for (CorruptedMobDefinition definition : CorruptedMobApi.family(family)) {
                assertTrue(ids.add(definition.id()), definition.id());
                assertTrue(sources.add(definition.source()), definition.source());
                assertEquals("minecraft:" + definition.id(), definition.source());
                assertSame(definition, CorruptedMobApi.of(family, definition.source()));
            }
        }
    }
    @Test void flagsAndLayersMatchTheModelsTheyDescribe() {
        assertTrue(CorruptedMobApi.require(CorruptedMobFamily.NETHER, "ghast").flying());
        assertFalse(CorruptedMobApi.require(CorruptedMobFamily.NETHER, "ghast").humanoid());
        assertTrue(CorruptedMobApi.require(CorruptedMobFamily.NETHER, "piglin").humanoid());
        assertTrue(CorruptedMobApi.require(CorruptedMobFamily.WILDLIFE, "warden").humanoid());
        assertFalse(CorruptedMobApi.require(CorruptedMobFamily.WILDLIFE, "wolf").humanoid());
        assertEquals("native_wildlife", CorruptedMobApi.require(CorruptedMobFamily.WILDLIFE, "vex").layerVariant());
        assertEquals("main", CorruptedMobApi.require(CorruptedMobFamily.VILLAGE, "vex").layerVariant());
    }
    @Test void aSourceMayAppearInSeveralFamiliesButOnlyKnownOnesHaveDedicatedModels() {
        assertNotNull(CorruptedMobApi.of(CorruptedMobFamily.VILLAGE, "minecraft:vex"));
        assertNotNull(CorruptedMobApi.of(CorruptedMobFamily.WILDLIFE, "minecraft:vex"));
        assertNull(CorruptedMobApi.of(CorruptedMobFamily.LIVESTOCK, "minecraft:vex"));
        assertTrue(CorruptedMobApi.hasDedicatedModel("minecraft:zombie"));
        assertTrue(CorruptedMobApi.hasDedicatedModel("minecraft:wolf"));
        assertFalse(CorruptedMobApi.hasDedicatedModel("minecraft:turtle_helmet"));
        assertFalse(CorruptedMobApi.hasDedicatedModel("othermod:boss"));
        assertThrows(IllegalStateException.class, () -> CorruptedMobApi.require(CorruptedMobFamily.LIVESTOCK, "wolf"));
    }
}
