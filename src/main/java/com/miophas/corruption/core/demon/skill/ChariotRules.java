package com.miophas.corruption.core.demon.skill;

/**
 * A chariot of fire driven at the boss's quarry, as rules. As it begins, the line it will run is marked on the ground:
 * from {@code from} blocks short of where its quarry stands, on the boss's side of it (turned a little either way, as
 * {@link #path} is told), straight through that place and on {@code past} blocks beyond. When the wind-up is over the
 * chariot is there at the start of the line, and runs it at {@code speed} blocks a tick. Everything within
 * {@code reach} of its path is struck once (for {@code damage}, thrown {@code knockback} blocks a tick away from the
 * line and up, and left burning for {@code burnSeconds}); every block in its way softer than {@code hardest} is broken,
 * as a charge breaks them ({@link ChargeRules#breaks}), and one that will not give way wrecks it there.
 */
public record ChariotRules(SkillTiming timing, double minRange, double maxRange, double from, double past, double speed, double reach,
                           double damage, double knockback, int burnSeconds, float hardest) {
    public ChariotRules {
        maxRange = Math.max(minRange, maxRange);
        from = Math.max(1, from);
        past = Math.max(0, past);
        speed = Math.max(.05, speed);
        reach = Math.max(0, reach);
        burnSeconds = Math.max(0, burnSeconds);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether a block this hard gives way to the chariot: one that can be broken at all, softer than the hardest. */
    public boolean breaks(float hardness) { return hardness >= 0 && hardness < hardest; }

    /** How long the line it runs is, in blocks. */
    public double length() { return from + past; }

    /** How many ticks it takes to run the whole line. */
    public int runTicks() { return (int) Math.ceil(length() / speed); }

    /**
     * The line it runs, on the ground: {startX, startZ, endX, endZ}. From the quarry's place back towards the boss's
     * (or along +Z, if the two stand in one place), turned {@code turn} radians about the quarry, {@code from} blocks;
     * and on through the quarry's place {@code past} blocks beyond it.
     */
    public double[] path(double quarryX, double quarryZ, double bossX, double bossZ, double turn) {
        double dx = bossX - quarryX, dz = bossZ - quarryZ, length = Math.hypot(dx, dz);
        if (length < 1e-6) { dx = 0; dz = 1; } else { dx /= length; dz /= length; }
        double cos = Math.cos(turn), sin = Math.sin(turn), backX = dx * cos - dz * sin, backZ = dx * sin + dz * cos;
        return new double[]{quarryX + backX * from, quarryZ + backZ * from, quarryX - backX * past, quarryZ - backZ * past};
    }

    /** How far along its line (0 at its start, 1 at its end) the chariot is this many ticks into its run. */
    public double along(double ticks) { return Math.max(0, Math.min(1, ticks * speed / length())); }

    /** How far a point lies from the stretch of line between two others, on the ground: what the chariot's reach is measured by. */
    public static double offLine(double x, double z, double ax, double az, double bx, double bz) {
        double lx = bx - ax, lz = bz - az, square = lx * lx + lz * lz;
        double share = square < 1e-9 ? 0 : Math.max(0, Math.min(1, ((x - ax) * lx + (z - az) * lz) / square));
        return Math.hypot(x - (ax + lx * share), z - (az + lz * share));
    }
}
