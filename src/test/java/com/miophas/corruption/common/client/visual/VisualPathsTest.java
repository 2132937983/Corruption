package com.miophas.corruption.common.client.visual;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** The shapes the effects are drawn along: the same seed always the same shape, and each where it should be. */
class VisualPathsTest {
    @Test void aBoltRunsJaggedFromEndToEndTheSameEveryTime() {
        float[] bolt = VisualPaths.bolt(new Random(5), 1, 12, -2, 3, 0, 4, 5, .22F);
        assertEquals((32 + 1) * 3, bolt.length, "halved five times: thirty-two pieces");
        assertArrayEquals(new float[]{1, 12, -2}, Arrays.copyOfRange(bolt, 0, 3), 1e-6F);
        assertArrayEquals(new float[]{3, 0, 4}, Arrays.copyOfRange(bolt, bolt.length - 3, bolt.length), 1e-6F);
        assertArrayEquals(bolt, VisualPaths.bolt(new Random(5), 1, 12, -2, 3, 0, 4, 5, .22F), "the same seed, the same bolt");
        assertFalse(Arrays.equals(bolt, VisualPaths.bolt(new Random(6), 1, 12, -2, 3, 0, 4, 5, .22F)), "another seed, another bolt");
        float straightest = 0;
        for (int at = 3; at + 3 < bolt.length; at += 3) {
            assertTrue(Float.isFinite(bolt[at]) && Float.isFinite(bolt[at + 1]) && Float.isFinite(bolt[at + 2]));
            // How far off the straight line from end to end.
            float t = (12 - bolt[at + 1]) / 12, x = 1 + 2 * t, z = -2 + 6 * t;
            straightest = Math.max(straightest, Math.abs(bolt[at] - x) + Math.abs(bolt[at + 2] - z));
        }
        assertTrue(straightest > .1F, "it is jagged");
        assertTrue(straightest < 8, "but it does not wander off");

        List<float[]> forks = VisualPaths.forks(new Random(9), bolt, 3, 4, 3, .25F);
        assertEquals(3, forks.size());
        for (float[] fork : forks) {
            assertEquals(9 * 3, fork.length);
            boolean fromTheBolt = false;
            for (int at = 0; at < bolt.length; at += 3)
                fromTheBolt |= bolt[at] == fork[0] && bolt[at + 1] == fork[1] && bolt[at + 2] == fork[2];
            assertTrue(fromTheBolt, "each fork sets out from a point of the bolt");
            float dx = fork[fork.length - 3] - fork[0], dy = fork[fork.length - 2] - fork[1], dz = fork[fork.length - 1] - fork[2];
            assertTrue(Math.sqrt(dx * dx + dy * dy + dz * dz) <= 4.01, "no longer than asked");
        }
    }

    @Test void cracksRunOutFromTheMiddleAndStayInside() {
        List<float[]> cracks = VisualPaths.cracks(new Random(3), 7, 6);
        assertFalse(cracks.isEmpty());
        int fromTheMiddle = 0;
        for (float[] piece : cracks) {
            assertEquals(6, piece.length);
            assertTrue(Math.hypot(piece[2], piece[3]) <= 6 + 1e-3, "inside the radius");
            assertTrue(Math.hypot(piece[0], piece[1]) <= 6 + 1e-3);
            assertTrue(piece[5] > piece[4], "each piece runs on from where the last left off");
            assertEquals(Math.hypot(piece[2] - piece[0], piece[3] - piece[1]), piece[5] - piece[4], 1e-3);
            if (piece[0] == 0 && piece[1] == 0 && piece[4] == 0) fromTheMiddle++;
        }
        assertEquals(7, fromTheMiddle, "seven arms set out from the middle");
        assertTrue(cracks.stream().anyMatch(piece -> Math.hypot(piece[2], piece[3]) > 5.9), "and they reach the edge");
        assertEquals(cracks.size(), VisualPaths.cracks(new Random(3), 7, 6).size(), "the same seed, the same cracks");
    }

    @Test void aSealStaysInItsCircleAndIsAlwaysTheSameForTheSameSeed() {
        for (long seed = 0; seed < 200; seed++) {
            VisualPaths.Seal seal = VisualPaths.seal(new Random(seed));
            assertTrue(seal.strokes().size() >= 3, "a stem, a bar and an arc at least");
            for (float[] stroke : seal.strokes()) {
                assertEquals(0, stroke.length % 2);
                assertTrue(stroke.length >= 4, "every stroke a line");
                for (int at = 0; at < stroke.length; at += 2) assertTrue(Math.hypot(stroke[at], stroke[at + 1]) <= .851, "inside its circle");
            }
            for (float[] ring : seal.rings()) assertTrue(Math.hypot(ring[0], ring[1]) + ring[2] <= .9001);
            VisualPaths.Seal again = VisualPaths.seal(new Random(seed));
            assertEquals(seal.strokes().size(), again.strokes().size());
            for (int stroke = 0; stroke < seal.strokes().size(); stroke++) assertArrayEquals(seal.strokes().get(stroke), again.strokes().get(stroke));
        }
    }

    @Test void aFigureJoinsEveryStarOnceGrowingFromTheFirst() {
        float[][] stars = new float[12][];
        Random random = new Random(4);
        for (int star = 0; star < stars.length; star++) stars[star] = new float[]{random.nextFloat() * 8, random.nextFloat() * 8, random.nextFloat() * 8};
        int[][] lines = VisualPaths.figure(stars);
        assertEquals(stars.length - 1, lines.length, "a star joined for every line, none twice");
        Set<Integer> joined = new HashSet<>(Set.of(0));
        for (int[] line : lines) {
            assertTrue(joined.contains(line[0]), "each line runs from a star already in the figure");
            assertTrue(joined.add(line[1]), "to one that is not yet");
        }
        assertEquals(stars.length, joined.size());
        assertEquals(0, VisualPaths.figure(new float[][]{{0, 0, 0}}).length, "one star is no figure");
    }
}
