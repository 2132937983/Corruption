package com.miophas.corruption.core.cooking;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Shapeless matching without a game: slots hold letters, and an ingredient is met by a slot holding its letter. */
class ShapelessMatchTest {
    private static int[] match(String ingredients, String... slots) {
        boolean[] filled = new boolean[slots.length];
        for (int slot = 0; slot < slots.length; slot++) filled[slot] = !slots[slot].isEmpty();
        return ShapelessMatch.match(ingredients.length(), filled, (ingredient, slot) -> slots[slot].indexOf(ingredients.charAt(ingredient)) >= 0);
    }

    @Test void whatGoesInMayLieInAnySlotInAnyOrder() {
        assertArrayEquals(new int[]{4, 1}, match("ab", "", "b", "", "", "a", "", ""));
        assertArrayEquals(new int[]{0}, match("a", "a", "", "", "", "", "", ""));
    }

    @Test void nothingIsLeftOverAndNothingCountsTwice() {
        assertNull(match("a", "a", "c", "", "", "", "", ""), "the c is left over");
        assertNull(match("aa", "a", "", "", "", "", "", ""), "one a cannot meet two");
        assertNull(match("ab", "a", "", "", "", "", "", ""), "the b is missing");
        assertNull(match("", "a", "", "", "", "", "", ""));
        assertArrayEquals(new int[0], match("", "", "", "", "", "", "", ""), "nothing makes nothing");
    }

    @Test void aSlotThatFitsSeveralIngredientsIsGivenToTheOneThatNeedsIt() {
        // The first slot would do for either; only the second leaves a home for both.
        assertArrayEquals(new int[]{1, 0}, match("ab", "ab", "a", "", "", "", "", ""));
        assertArrayEquals(new int[]{2, 0, 1}, match("abc", "bc", "c", "a", "", "", "", ""));
    }

    @Test void aRecipeAskingForARuneTakesOnlyThatRuneAndOneAskingNoneTakesAny() {
        assertTrue(BrassCauldronRules.inscribed(null, null));
        assertTrue(BrassCauldronRules.inscribed(null, "corruption:baal"), "a parchment there does not stop a recipe that asks for none");
        assertTrue(BrassCauldronRules.inscribed("corruption:corruption", "corruption:corruption"));
        assertFalse(BrassCauldronRules.inscribed("corruption:corruption", "corruption:baal"));
        assertFalse(BrassCauldronRules.inscribed("corruption:corruption", null));
    }

    @Test void theCauldronHoldsSevenThings() {
        assertEquals(7, BrassCauldronRules.INPUTS);
        assertEquals(4000, BrassCauldronRules.TANK, "four buckets");
        assertEquals(50_000, BrassCauldronRules.CAPACITY);
        assertTrue(BrassCauldronRules.DEFAULT_COOK_TICKS > 0 && BrassCauldronRules.DEFAULT_COOK_TICKS <= BrassCauldronRules.MAX_COOK_TICKS);
    }

    /** Walks up from wherever the tests run to the project folder. */
    private static Path source(String folder) {
        for (Path at = Path.of("").toAbsolutePath(); at != null; at = at.getParent())
            if (Files.isDirectory(at.resolve(folder))) return at.resolve(folder);
        throw new AssertionError("Cannot find " + folder);
    }

    @Test void theRulesAreGameFree() throws Exception {
        try (Stream<Path> files = Files.list(source("src/main/java/com/miophas/corruption/core/cooking"))) {
            List<String> imports = files.filter(file -> file.toString().endsWith(".java")).flatMap(file -> {
                try { return Files.readAllLines(file).stream().filter(line -> line.startsWith("import ")); }
                catch (Exception failure) { throw new RuntimeException(failure); }
            }).toList();
            for (String line : imports)
                assertTrue(line.startsWith("import java.") || line.startsWith("import com.miophas.corruption.core."), "core/cooking is plain Java: " + line);
        }
    }
}
