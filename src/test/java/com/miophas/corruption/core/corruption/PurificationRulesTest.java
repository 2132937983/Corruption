package com.miophas.corruption.core.corruption;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

/** What lowers a player's corruption, and what worn things do to its growth. */
class PurificationRulesTest {
    @Test void corruptionIsLoweredButNeverBelowNone() {
        assertEquals(4, PlayerCorruptionRules.decrease(5, 1));
        assertEquals(0, PlayerCorruptionRules.decrease(0, 1));
        assertEquals(0, PlayerCorruptionRules.decrease(3, 99));
        assertEquals(PlayerCorruptionRules.MAX - 1, PlayerCorruptionRules.decrease(99, 1), "from within the scale");
        assertEquals(5, PlayerCorruptionRules.decrease(5, -3), "lowering by less than nothing does nothing");
    }

    @Test void aPurifierTakesItsAmountAway() {
        CorruptionPurifier water = new CorruptionPurifier(1);
        assertEquals(4, water.purify(5));
        assertEquals(0, water.purify(0));
        assertEquals(15, new CorruptionPurifier(5).purify(20));
        assertThrows(IllegalArgumentException.class, () -> new CorruptionPurifier(-1), "a purifier cannot corrupt");
    }

    private static java.util.function.DoubleSupplier rolls(double... values) {
        Iterator<Double> next = java.util.Arrays.stream(values).boxed().iterator();
        return next::next;
    }

    @Test void wornThingsHoldGrowthBackOrMakeItWorse() {
        assertEquals(.8, CorruptionWard.total(List.of(new CorruptionWard(.15), new CorruptionWard(.3), new CorruptionWard(.2), new CorruptionWard(.15))), 1e-9);
        assertEquals(1, CorruptionWard.total(List.of(new CorruptionWard(.9), new CorruptionWard(.9))), 1e-9, "never past holding back all of it");
        assertEquals(-1, CorruptionWard.total(List.of(new CorruptionWard(-.6), new CorruptionWard(-.6))), 1e-9, "nor past doubling all of it");
        assertEquals(1, new CorruptionWard(3).share(), 1e-9);
        assertEquals(0, CorruptionWard.total(List.of()), 1e-9);

        assertEquals(3, CorruptionWard.gain(3, 0, rolls()), "nothing worn: all of it");
        assertEquals(0, CorruptionWard.gain(5, 1, rolls(0, .5, .9, .99, .999)), "a share of one stops all of it");
        assertEquals(2, CorruptionWard.gain(4, .8, rolls(.1, .85, .5, .99)), "each point held back when its roll falls under the share");
        assertEquals(3, CorruptionWard.gain(2, -.5, rolls(.2, .7)), "below none, a point doubled when its roll falls under the share's size");
        assertEquals(0, CorruptionWard.gain(0, .5, rolls()), "and nothing of nothing");
    }

    @Test void theNewRulesArePlainJava() throws Exception {
        Path folder = null;
        for (Path at = Path.of("").toAbsolutePath(); at != null && folder == null; at = at.getParent())
            if (Files.isDirectory(at.resolve("src/main/java/com/miophas/corruption/core/corruption"))) folder = at.resolve("src/main/java/com/miophas/corruption/core/corruption");
        assertNotNull(folder);
        for (String name : new String[]{"CorruptionPurifier.java", "CorruptionWard.java", "PlayerCorruptionRules.java"})
            for (String line : Files.readAllLines(folder.resolve(name)))
                if (line.startsWith("import ")) assertTrue(line.startsWith("import java."), name + " imports only java.*: " + line);
    }
}
