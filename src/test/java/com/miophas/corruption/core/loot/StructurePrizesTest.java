package com.miophas.corruption.core.loot;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Each prize comes out of each structure once; structures of one type are told apart by where they started; it saves. */
class StructurePrizesTest {
    @Test void aPrizeComesOutOfAStructureOnce() {
        StructurePrizes prizes = new StructurePrizes();
        assertTrue(prizes.isEmpty());
        assertFalse(prizes.taken("pack:book", "minecraft:stronghold", 3, -7));
        assertTrue(prizes.take("pack:book", "minecraft:stronghold", 3, -7), "the first time, it is there");
        assertFalse(prizes.take("pack:book", "minecraft:stronghold", 3, -7), "and never again");
        assertTrue(prizes.taken("pack:book", "minecraft:stronghold", 3, -7));
    }

    @Test void otherStructuresAndOtherPrizesAreCountedApart() {
        StructurePrizes prizes = new StructurePrizes();
        prizes.take("pack:book", "minecraft:stronghold", 3, -7);
        assertTrue(prizes.take("pack:book", "minecraft:stronghold", 3, -6), "another stronghold, started in another chunk");
        assertTrue(prizes.take("pack:book", "minecraft:fortress", 3, -7), "another kind of structure, started in the same chunk");
        assertTrue(prizes.take("pack:relic", "minecraft:stronghold", 3, -7), "another prize from the same stronghold");
        assertEquals(4, prizes.keys().size());
    }

    @Test void whatWasTakenSurvivesBeingSavedAndLoaded() {
        StructurePrizes prizes = new StructurePrizes();
        prizes.take("pack:book", "minecraft:bastion_remnant", -40, 12);
        prizes.take("pack:book", "minecraft:end_city", 100, 100);
        StructurePrizes loaded = new StructurePrizes(List.copyOf(prizes.keys()));
        assertEquals(prizes.keys(), loaded.keys());
        assertFalse(loaded.take("pack:book", "minecraft:end_city", 100, 100));
    }

    @Test void theRulesAreGameFree() throws Exception {
        Path folder = null;
        for (Path at = Path.of("").toAbsolutePath(); at != null && folder == null; at = at.getParent())
            if (Files.isDirectory(at.resolve("src/main/java/com/miophas/corruption/core/loot"))) folder = at.resolve("src/main/java/com/miophas/corruption/core/loot");
        assertNotNull(folder, "core/loot is there");
        try (Stream<Path> files = Files.list(folder)) {
            for (Path file : files.toList())
                for (String line : Files.readAllLines(file))
                    if (line.startsWith("import ")) assertTrue(line.startsWith("import java."), "core/loot is plain Java: " + line);
        }
    }
}
