package com.miophas.corruption.core.altar;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** The maps of structures built round altars, without a game: how they are read, and what they may not ask. */
class AltarStructureMapTest {
    private static AltarStructureMap.Layer layer(int up, String... rows) { return new AltarStructureMap.Layer(up, List.of(rows)); }
    private static String problem(Set<Character> key, AltarStructureMap.Layer... layers) { return AltarStructureMap.problem(List.of(layers), key); }
    private static String problem(AltarForm form, Set<Character> key, AltarStructureMap.Layer... layers) { return AltarStructureMap.problem(List.of(layers), key, form); }

    @Test void rowsRunNorthToSouthAndEachRowWestToEastRoundTheOffering() {
        List<AltarStructureMap.Cell> cells = AltarStructureMap.cells(List.of(layer(-1, "a.b", ".c.", "d e")));
        assertEquals(List.of(new AltarStructureMap.Cell(-1, -1, 1, 'a'), new AltarStructureMap.Cell(1, -1, 1, 'b'), new AltarStructureMap.Cell(0, -1, 0, 'c'),
                new AltarStructureMap.Cell(-1, -1, -1, 'd'), new AltarStructureMap.Cell(1, -1, -1, 'e')), cells, "the space and the dot ask for nothing");
        assertNull(problem(Set.of('a', 'b', 'c', 'd', 'e'), layer(-1, "a.b", ".c.", "d e")));
    }

    @Test void layersAreOddEvenRowsAndCentredEachOnItsOwn() {
        assertNull(problem(Set.of('x'), layer(-1, "xxxxx", "xxxxx", "xxxxx"), layer(2, "x")), "a small layer is centred too");
        assertEquals(new AltarStructureMap.Cell(0, 2, 0, 'x'), AltarStructureMap.cells(List.of(layer(2, "x"))).get(0));
        assertNotNull(problem(Set.of('x'), layer(-1, "xx", "xx")), "even rows");
        assertNotNull(problem(Set.of('x'), layer(-1, "xx", "xx", "xx")), "even length");
        assertNotNull(problem(Set.of('x'), layer(-1, "xxx", "x", "xxx")), "ragged");
        assertNotNull(problem(Set.of('x'), layer(-1, "x"), layer(-1, "x")), "two at one height");
        assertNotNull(problem(Set.of('x')), "no layers");
        assertNotNull(problem(Set.of(), layer(-1, "...")), "nothing asked for");
        assertNotNull(problem(Set.of('x'), layer(AltarStructureMap.REACH + 1, "x")), "too high");
        String wide = "x".repeat(2 * AltarStructureMap.REACH + 3);
        assertNotNull(problem(Set.of('x'), layer(-1, wide)), "too wide");
    }

    @Test void everySymbolIsKeyedAndEveryKeyUsed() {
        assertNotNull(problem(Set.of('x'), layer(-1, "xyx")), "y has no block");
        assertNotNull(problem(Set.of('x', 'y'), layer(-1, "xxx")), "y is never used");
        assertNotNull(problem(Set.of('x', '.'), layer(-1, "x")), "the dot cannot name a block");
        assertNotNull(problem(Set.of('x', 'P'), layer(-1, "x")), "nor P");
        assertNotNull(problem(Set.of('x', '@'), layer(-1, "x")), "nor @");
    }

    @Test void theAltarsOwnGroundIsLeftToItsRitesButBeyondItAndAboveOrBelowAnythingGoes() {
        String edge = "x" + ".".repeat(9) + "x", empty = ".".repeat(11);
        String[] ring = new String[11];
        for (int row = 0; row < 11; row++) ring[row] = row == 0 || row == 10 ? edge : empty;
        assertNull(problem(Set.of('x'), layer(0, ring)), "five out is beyond the ground");
        assertNotNull(problem(Set.of('x'), layer(0, ".x.", "...", "...")), "one north of the offering is on its ground");
        assertNull(problem(Set.of('x'), layer(1, ".x.", "...", "...")), "but not a block above it");
        assertNull(problem(Set.of('x'), layer(-1, "x"), layer(0, "....P....", ".........", "P.......P", ".........", "....@....", ".........", "P.......P", ".........", "....P....")),
                "the altar marked where it stands");
        assertNotNull(problem(Set.of('x'), layer(-1, "x"), layer(0, "..P", "...", "...")), "no pillar stands one east and north");
        assertNotNull(problem(Set.of('x'), layer(-1, "x@x")), "the offering stands on its own level");
        assertNotNull(problem(Set.of('x'), layer(-1, "x"), layer(0, "@..", "...", "...")), "and in the middle");
    }

    @Test void pillarsAreMarkedWhereTheAltarsOwnShapeHasThem() {
        AltarStructureMap.Layer octagram = layer(0, "....P....", ".P.....P.", ".........", ".........", "P...@...P", ".........", ".........", ".P.....P.", "....P....");
        assertNull(problem(AltarForm.OCTAGRAM, Set.of('x'), layer(-1, "x"), octagram));
        assertNotNull(problem(AltarForm.HEXAGRAM, Set.of('x'), layer(-1, "x"), octagram), "no hexagram pillar at 3, 3");
        assertNotNull(problem(Set.of('x'), layer(-1, "x"), octagram), "left unsaid, a hexagram's");
        assertNotNull(problem(AltarForm.OCTAGRAM, Set.of('x'), layer(-1, "x"), layer(0, "....P....", ".........", "P.......P", ".........", "....@....",
                ".........", "P.......P", ".........", "....P....")), "no octagram pillar at 4, 2");
        assertNotNull(problem(AltarForm.OCTAGRAM, Set.of('x'), layer(0, ".........", ".x.......", ".........", ".........", "....@....", ".........",
                ".........", ".........", ".........")), "nor may a block be asked where its pillar stands, on the ground");
    }
}
