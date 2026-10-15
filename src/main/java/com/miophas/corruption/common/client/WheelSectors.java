package com.miophas.corruption.common.client;

/**
 * The spell wheel's sectors, as plain geometry: one for each slot of the casting unit in hand, however many it
 * has, the first centred straight up and the rest following clockwise, as the wand's tooltip and the pedestal
 * number them. Angles are in radians, clockwise from straight up; offsets are in screen pixels from the wheel's
 * middle, across to the right and down.
 */
public final class WheelSectors {
    private final int count;

    /** Sectors for a unit with this many slots; one at least. */
    public WheelSectors(int count) { this.count = Math.max(1, count); }

    public int count() { return count; }
    /** How wide each sector is. */
    public double step() { return Math.PI * 2 / count; }
    /** Where a sector's middle points. */
    public double middle(int sector) { return sector * step(); }
    /** Where a sector begins, going clockwise; it ends where the next begins. */
    public double start(int sector) { return middle(sector) - step() / 2; }
    public double end(int sector) { return middle(sector) + step() / 2; }

    /** The sector a point lies in, by the way it lies from the middle; -1 within the dead zone, where nothing is pointed at. */
    public int at(double across, double down, double deadZone) {
        if (across * across + down * down < deadZone * deadZone) return -1;
        double angle = Math.atan2(across, -down);
        return Math.floorMod((int) Math.floor((angle + step() / 2) / step()), count);
    }

    /** What the wheel has chosen once the pointer is at this sector: it, if a spell is written there; otherwise what it had chosen. */
    public static int choose(int pointedAt, int chosen, int written) { return pointedAt >= 0 && pointedAt < written ? pointedAt : chosen; }

    /** Where a point at this angle and distance from the middle lies: {across, down}. */
    public static double[] point(double angle, double radius) { return new double[]{Math.sin(angle) * radius, -Math.cos(angle) * radius}; }
}
