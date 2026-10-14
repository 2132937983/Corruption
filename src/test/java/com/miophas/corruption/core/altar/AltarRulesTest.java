package com.miophas.corruption.core.altar;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** The altar's rules without a game: where its pillars stand, how its offering's state changes, what makes a rite, and how one unfolds. */
class AltarRulesTest {
    @Test void sixPillarsStandAtTheCornersOfAHexagramSlotOneNorthTheRestClockwise() {
        int[][] expected = {{0, 4}, {4, 2}, {4, -2}, {0, -4}, {-4, -2}, {-4, 2}};
        assertEquals(6, AltarLayout.SLOTS);
        for (int slot = 0; slot < AltarLayout.SLOTS; slot++) {
            assertEquals(expected[slot][0], AltarLayout.east(slot));
            assertEquals(expected[slot][1], AltarLayout.north(slot));
            assertEquals(slot, AltarLayout.slotAt(expected[slot][0], expected[slot][1]));
            assertTrue(Math.abs(AltarLayout.east(slot)) <= AltarLayout.REACH && Math.abs(AltarLayout.north(slot)) <= AltarLayout.REACH);
        }
        assertEquals(-1, AltarLayout.slotAt(0, 0), "the offering is no pillar");
        // Clockwise seen from above: each next pillar turns right from the last, round the middle.
        for (int slot = 0; slot < AltarLayout.SLOTS; slot++) {
            int next = (slot + 1) % AltarLayout.SLOTS;
            assertTrue(AltarLayout.east(slot) * AltarLayout.north(next) - AltarLayout.north(slot) * AltarLayout.east(next) < 0, "clockwise from " + slot);
        }
        Set<Integer> corners = new HashSet<>();
        for (int[] triangle : AltarLayout.TRIANGLES) for (int slot : triangle) corners.add(slot);
        assertEquals(6, corners.size(), "the hexagram's two triangles use every pillar once");
    }

    @Test void anAltarMayFaceAnyOfTheFourWaysTurningWhollyWithIt() {
        int[][] first = {{0, 4}, {4, 0}, {0, -4}, {-4, 0}};
        Set<List<Integer>> northSouth = new HashSet<>(), eastWest = new HashSet<>();
        for (AltarFacing facing : AltarFacing.values()) {
            assertArrayEquals(first[facing.quarterTurns()], facing.inWorld(AltarLayout.east(0), AltarLayout.north(0)), "pillar 1 stands the way it faces: " + facing);
            assertArrayEquals(facing.inWorld(0, 0), new int[]{0, 0}, "the offering stays put");
            Set<List<Integer>> pillars = new HashSet<>();
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++) {
                int[] at = facing.inWorld(AltarLayout.east(slot), AltarLayout.north(slot)), next = facing.inWorld(AltarLayout.east((slot + 1) % 6), AltarLayout.north((slot + 1) % 6));
                assertTrue(at[0] * next[1] - at[1] * next[0] < 0, "still clockwise from " + slot + ", facing " + facing);
                pillars.add(List.of(at[0], at[1]));
            }
            if (facing.quarterTurns() % 2 == 0) northSouth.addAll(pillars); else eastWest.addAll(pillars);
        }
        assertEquals(6, northSouth.size(), "facing north or south, the pillars stand in the same places, numbered from opposite ends");
        assertEquals(6, eastWest.size());
        assertTrue(northSouth.stream().noneMatch(eastWest::contains), "facing east or west they stand elsewhere");
        assertArrayEquals(new int[]{0, -1}, AltarFacing.EAST.inWorld(1, 0), "one to the right, facing east, is south");
        assertArrayEquals(new int[]{-1, 0}, AltarFacing.SOUTH.inWorld(1, 0));
        assertArrayEquals(new int[]{0, 1}, AltarFacing.WEST.inWorld(1, 0));
        assertArrayEquals(new int[]{-3, -2}, AltarFacing.WEST.inWorld(-2, 3));
    }

    @Test void theAltarStandsOnlyWithAllSixPillars() {
        assertTrue(AltarLayout.formed((east, north) -> AltarLayout.slotAt(east, north) >= 0));
        assertFalse(AltarLayout.formed((east, north) -> AltarLayout.slotAt(east, north) >= 0 && !(east == 4 && north == -2)), "one missing");
        assertFalse(AltarLayout.formed((east, north) -> false));
    }

    @Test void standingItBindsBrokenItSleepsAndARiteLetsGo() {
        assertEquals(AltarState.BINDING, AltarRules.checked(AltarState.DORMANT, true), "newly stood, it binds");
        assertEquals(AltarState.FORMED, AltarRules.checked(AltarState.FORMED, true), "after a rite it stays unbinding until used");
        assertEquals(AltarState.BINDING, AltarRules.checked(AltarState.BINDING, true));
        for (AltarState state : AltarState.values()) assertEquals(AltarState.DORMANT, AltarRules.checked(state, false), "broken from " + state);
        assertEquals(AltarState.BINDING, AltarRules.used(AltarState.FORMED, true), "used by hand, it binds again");
        assertEquals(AltarState.FORMED, AltarRules.used(AltarState.BINDING, true), "used while binding, it lets go");
        assertEquals(AltarState.BINDING, AltarRules.used(AltarState.DORMANT, true), "its altar newly stood");
        for (AltarState state : AltarState.values()) assertEquals(AltarState.DORMANT, AltarRules.used(state, false), "no altar, from " + state);
        assertEquals(AltarState.FORMED, AltarRules.afterRite(AltarState.BINDING), "what a rite brings forth walks free");
        assertEquals(AltarState.DORMANT, AltarRules.afterRite(AltarState.DORMANT));
        assertFalse(AltarState.DORMANT.showsCircle()); assertTrue(AltarState.FORMED.showsCircle()); assertTrue(AltarState.BINDING.showsCircle());
        assertTrue(AltarState.BINDING.binds()); assertFalse(AltarState.FORMED.binds());
        assertFalse(AltarState.DORMANT.performs()); assertTrue(AltarState.FORMED.performs());
    }

    /** Pillars holding letters; an ingredient asks for a letter. */
    private static int[] shaped(String asked, String held) {
        boolean[] wants = new boolean[6], filled = new boolean[6];
        for (int slot = 0; slot < 6; slot++) { wants[slot] = asked.charAt(slot) != '.'; filled[slot] = held.charAt(slot) != '.'; }
        return RitualMatch.shaped(wants, filled, (ingredient, slot) -> asked.charAt(ingredient) == held.charAt(slot));
    }

    @Test void shapedEachPillarHoldsWhatItsSlotAsksAndTheRestNothing() {
        assertArrayEquals(new int[]{0, 2, 4}, shaped("a.b.c.", "a.b.c."));
        assertNull(shaped("a.b.c.", "b.a.c."), "turned round, it is not the same rite");
        assertNull(shaped("a.b.c.", "a.b.cd"), "something on a pillar asked for nothing");
        assertNull(shaped("a.b.c.", "a.b..."), "something missing");
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5}, shaped("abcdef", "abcdef"));
    }

    @Test void shapelessTheIngredientsMayLieOnAnyPillars() {
        String held = ".b..ca";
        boolean[] filled = new boolean[6];
        for (int slot = 0; slot < 6; slot++) filled[slot] = held.charAt(slot) != '.';
        String asked = "abc";
        assertArrayEquals(new int[]{5, 1, 4}, RitualMatch.shapeless(3, filled, (ingredient, slot) -> asked.charAt(ingredient) == held.charAt(slot)));
        assertNull(RitualMatch.shapeless(2, filled, (ingredient, slot) -> "ab".charAt(ingredient) == held.charAt(slot)), "the c is left over");
    }

    @Test void aRiteAskingForACreatureNeedsThatKindBound() {
        assertTrue(RitualMatch.offering(null, null));
        assertTrue(RitualMatch.offering(null, "minecraft:pig"), "a creature bound does not stop a rite that asks for none");
        assertTrue(RitualMatch.offering("minecraft:pig", "minecraft:pig"));
        assertFalse(RitualMatch.offering("minecraft:pig", "minecraft:cow"));
        assertFalse(RitualMatch.offering("minecraft:pig", null));
    }

    @Test void theGroundRunsFourBlocksEveryWayWithTheAltarStandingOnIt() {
        assertTrue(AltarLayout.onGround(0, 0) && AltarLayout.onGround(4, -4) && AltarLayout.onGround(-4, 4));
        assertFalse(AltarLayout.onGround(5, 0));
        assertFalse(AltarLayout.onGround(0, -5));
        assertTrue(AltarLayout.isAltar(0, 0), "the offering");
        for (int slot = 0; slot < AltarLayout.SLOTS; slot++) assertTrue(AltarLayout.isAltar(AltarLayout.east(slot), AltarLayout.north(slot)), "pillar " + slot);
        assertFalse(AltarLayout.isAltar(1, 1));
        int free = 0;
        for (int east = -5; east <= 5; east++)
            for (int north = -5; north <= 5; north++) if (AltarLayout.onGround(east, north) && !AltarLayout.isAltar(east, north)) free++;
        assertEquals(9 * 9 - 7, free, "nine by nine, less the offering and its six pillars");
    }

    @Test void theGroundIsClearButForTheBlocksARiteAsksFor() {
        Set<List<Integer>> ring = Set.of(List.of(0, 1), List.of(1, 1), List.of(1, 0), List.of(1, -1), List.of(0, -1), List.of(-1, -1), List.of(-1, 0), List.of(-1, 1));
        RitualMatch.Place candles = (east, north) -> ring.contains(List.of(east, north));
        RitualMatch.Place nothingAsked = (east, north) -> false;
        assertTrue(RitualMatch.ground(nothingAsked, nothingAsked, (east, north) -> true), "nothing asked, bare ground");
        assertFalse(RitualMatch.ground(nothingAsked, nothingAsked, (east, north) -> !(east == 3 && north == -3)), "a stray block spoils it");
        assertTrue(RitualMatch.ground(nothingAsked, nothingAsked, (east, north) -> !AltarLayout.isAltar(east, north)), "the altar itself is not looked at");
        assertTrue(RitualMatch.ground(candles, candles, (east, north) -> !candles.test(east, north)), "eight candles round the offering");
        assertFalse(RitualMatch.ground(candles, (east, north) -> candles.test(east, north) && !(east == 1 && north == 1), (east, north) -> !candles.test(east, north)), "one short");
        assertFalse(RitualMatch.ground(candles, candles, (east, north) -> !candles.test(east, north) && !(east == -4 && north == 0)), "the candles, and something else");
    }

    @Test void aRiteAskingForCorruptionNeedsAPlayerCorruptedThatFar() {
        assertTrue(RitualMatch.corruption(0), "asking none, it needs nobody");
        assertTrue(RitualMatch.corruption(0, 20));
        assertFalse(RitualMatch.corruption(10), "nobody there");
        assertFalse(RitualMatch.corruption(10, 3, 9));
        assertTrue(RitualMatch.corruption(10, 3, 10), "one is enough");
    }

    @Test void aRiteGathersThenTheLightningStrikesThenWhatItBringsAppears() {
        int ticks = 60;
        assertEquals(RitualRules.Phase.GATHERING, RitualRules.phase(0, ticks));
        assertEquals(RitualRules.Phase.GATHERING, RitualRules.phase(ticks - 1, ticks));
        assertEquals(RitualRules.Phase.STRIKE, RitualRules.phase(ticks, ticks));
        assertEquals(RitualRules.Phase.AFTERMATH, RitualRules.phase(ticks + 1, ticks));
        assertEquals(RitualRules.Phase.COMPLETE, RitualRules.phase(ticks + RitualRules.AFTER_STRIKE, ticks), "the lightning first, then what it brings");
        assertTrue(RitualRules.AFTER_STRIKE > 0);
    }

    /** Walks up from wherever the tests run to the project folder. */
    private static Path source(String folder) {
        for (Path at = Path.of("").toAbsolutePath(); at != null; at = at.getParent())
            if (Files.isDirectory(at.resolve(folder))) return at.resolve(folder);
        throw new AssertionError("Cannot find " + folder);
    }

    @Test void theRulesAreGameFree() throws Exception {
        try (Stream<Path> files = Files.list(source("src/main/java/com/miophas/corruption/core/altar"))) {
            List<String> imports = files.filter(file -> file.toString().endsWith(".java")).flatMap(file -> {
                try { return Files.readAllLines(file).stream().filter(line -> line.startsWith("import ")); }
                catch (Exception failure) { throw new RuntimeException(failure); }
            }).toList();
            for (String line : imports)
                assertTrue(line.startsWith("import java.") || line.startsWith("import com.miophas.corruption.core."), "core/altar is plain Java: " + line);
        }
    }
}
