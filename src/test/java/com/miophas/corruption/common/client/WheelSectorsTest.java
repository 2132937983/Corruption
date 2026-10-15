package com.miophas.corruption.common.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** The spell wheel's geometry: one sector a slot, however many, the first straight up, the rest clockwise. */
class WheelSectorsTest {
    private static int at(WheelSectors wheel, double angle) {
        double[] point = WheelSectors.point(angle, 100);
        return wheel.at(point[0], point[1], 30);
    }

    @Test void theFirstSectorIsCentredStraightUpAndTheRestFollowClockwise() {
        for (int count : new int[]{1, 3, 6, 8, 12}) {
            WheelSectors wheel = new WheelSectors(count);
            assertEquals(count, wheel.count());
            assertEquals(0, wheel.at(0, -100, 30), count + ": straight up is the first");
            for (int sector = 0; sector < count; sector++) {
                assertEquals(sector, at(wheel, wheel.middle(sector)), count + ": the middle of " + sector);
                assertEquals(sector, at(wheel, wheel.start(sector) + 1e-6), count + ": just inside the start of " + sector);
                assertEquals(sector, at(wheel, wheel.end(sector) - 1e-6), count + ": just inside the end of " + sector);
                assertEquals(wheel.end(sector), wheel.start(sector + 1), 1e-9, "each begins where the last ended");
            }
        }
        WheelSectors three = new WheelSectors(3);
        assertEquals(1, three.at(100, 30, 30), "right and a little down: the second of three, clockwise");
        assertEquals(2, three.at(-100, 30, 30), "left: the third");
        assertEquals(0, new WheelSectors(1).at(-50, 80, 30), "one sector is the whole wheel");
        assertEquals(1, new WheelSectors(0).count(), "never fewer than one");
    }

    @Test void theMiddleChoosesNothingAndEmptySlotsCannotBeChosen() {
        WheelSectors eight = new WheelSectors(8);
        assertEquals(-1, eight.at(10, -10, 30), "within the dead zone");
        assertEquals(0, eight.at(0, -30, 30), "on its edge, the sector");
        assertEquals(2, WheelSectors.choose(2, 0, 5), "a written slot is chosen");
        assertEquals(1, WheelSectors.choose(6, 1, 5), "an empty one keeps the choice");
        assertEquals(1, WheelSectors.choose(-1, 1, 5), "and so does the middle");
    }

    @Test void pointsLieWhereTheirAnglesSay() {
        double[] up = WheelSectors.point(0, 10), right = WheelSectors.point(Math.PI / 2, 10), down = WheelSectors.point(Math.PI, 10);
        assertArrayEquals(new double[]{0, -10}, up, 1e-9);
        assertArrayEquals(new double[]{10, 0}, right, 1e-9, "clockwise from up is right, as the screen sees it");
        assertArrayEquals(new double[]{0, 10}, down, 1e-9);
    }
}
