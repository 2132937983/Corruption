package com.miophas.corruption.core.energy;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class EnergyBufferTest {
    @Test void itHoldsBetweenNothingAndItsCapacity() {
        AtomicInteger told = new AtomicInteger();
        EnergyBuffer buffer = new EnergyBuffer(1000, Long.MAX_VALUE, Long.MAX_VALUE, told::incrementAndGet);
        assertEquals(600, buffer.insert(600, false));
        assertEquals(400, buffer.insert(900, false), "only what fits");
        assertEquals(0, buffer.insert(1, false), "full");
        assertEquals(1000, buffer.getEnergy()); assertEquals(0, buffer.getSpace());
        assertEquals(1000, buffer.extract(5000, false), "only what it has");
        assertEquals(0, buffer.extract(1, false));
        assertEquals(3, told.get(), "told of every real change, not of moves that changed nothing");
        assertEquals(0, buffer.insert(-50, false)); assertEquals(0, buffer.extract(-50, false), "nothing negative moves");
    }

    @Test void simulatingChangesNothing() {
        AtomicInteger told = new AtomicInteger();
        EnergyBuffer buffer = new EnergyBuffer(1000, Long.MAX_VALUE, Long.MAX_VALUE, told::incrementAndGet);
        buffer.setEnergy(300);
        assertEquals(700, buffer.insert(900, true)); assertEquals(300, buffer.extract(900, true));
        assertEquals(300, buffer.getEnergy());
        assertEquals(0, told.get(), "nor tells anyone; loading the amount does not either");
    }

    @Test void itsLimitsHoldPerMoveAndItsViewsShareTheStore() {
        EnergyBuffer buffer = new EnergyBuffer(10_000, 500, 200, null);
        assertEquals(500, buffer.insert(2000, false));
        assertEquals(200, buffer.extract(2000, false));
        CorruptionEnergyStorage fillOnly = buffer.view(Long.MAX_VALUE, 0);
        assertTrue(fillOnly.canInsert()); assertFalse(fillOnly.canExtract());
        assertEquals(500, fillOnly.insert(2000, false), "a view cannot loosen the buffer's own limits");
        assertEquals(0, fillOnly.extract(100, false));
        assertEquals(800, buffer.getEnergy()); assertEquals(800, fillOnly.getEnergy(), "it is the same energy");
        buffer.setEnergy(-5); assertEquals(0, buffer.getEnergy());
        buffer.setEnergy(1_000_000); assertEquals(10_000, buffer.getEnergy());
        assertThrows(IllegalArgumentException.class, () -> new EnergyBuffer(-1, 0, 0, null));
    }

    @Test void energyMovesOnlyAsFarAsBothSidesAgree() {
        EnergyBuffer from = new EnergyBuffer(1000, Long.MAX_VALUE, 300, null), to = new EnergyBuffer(250, Long.MAX_VALUE, Long.MAX_VALUE, null);
        from.setEnergy(1000);
        assertEquals(250, CorruptionEnergy.move(from, to, 10_000), "the receiver's room and the giver's limit, whichever is less");
        assertEquals(750, from.getEnergy()); assertEquals(250, to.getEnergy());
        assertEquals(0, CorruptionEnergy.move(from, to, 100), "nowhere to put it");
        assertEquals(0, CorruptionEnergy.move(to, from.view(Long.MAX_VALUE, 0), 0));
        assertEquals(0, CorruptionEnergy.move(from.view(Long.MAX_VALUE, 0), to, 100), "nothing is taken from a store that gives nothing");
        // A receiver that promises more than it takes: the difference goes back, so nothing is lost.
        CorruptionEnergyStorage fickle = new CorruptionEnergyStorage() {
            long held;
            @Override public long getEnergy() { return held; }
            @Override public long getCapacity() { return 1000; }
            @Override public long insert(long amount, boolean simulate) { long taken = simulate ? amount : amount / 2; if (!simulate) held += taken; return taken; }
            @Override public long extract(long amount, boolean simulate) { return 0; }
        };
        from.setEnergy(1000);
        assertEquals(150, CorruptionEnergy.move(from, fickle, 300));
        assertEquals(850, from.getEnergy(), "only what really arrived left the giver");
    }

    /** Walks up from wherever the tests run to the project folder. */
    private static Path source(String folder) {
        for (Path at = Path.of("").toAbsolutePath(); at != null; at = at.getParent())
            if (Files.isDirectory(at.resolve(folder))) return at.resolve(folder);
        throw new AssertionError("Cannot find " + folder);
    }
    private static List<String> imports(String folder) throws Exception {
        try (Stream<Path> files = Files.list(source(folder))) {
            return files.filter(file -> file.toString().endsWith(".java")).flatMap(file -> {
                try { return Files.readAllLines(file).stream().filter(line -> line.startsWith("import ")).map(line -> file.getFileName() + ": " + line); }
                catch (Exception failure) { throw new RuntimeException(failure); }
            }).toList();
        }
    }

    @Test void theRulesAreGameFreeAndTheApiNeedsNothingInternal() throws Exception {
        // The energy system's rules are plain Java, so they can be tested and reasoned about without a game.
        for (String line : imports("src/main/java/com/miophas/corruption/core/energy"))
            assertTrue(line.contains("import java.") || line.contains("import com.miophas.corruption.api.energy."), "core/energy is plain Java: " + line);
        // What add-ons build against must not lead them into the mod's own internals, which may change freely.
        for (String line : imports("src/main/java/com/miophas/corruption/api/energy"))
            assertFalse(line.contains("com.miophas.corruption.common."), "api/energy reaches into common: " + line);
    }
}
