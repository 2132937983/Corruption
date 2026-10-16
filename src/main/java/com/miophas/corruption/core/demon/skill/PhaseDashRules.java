package com.miophas.corruption.core.demon.skill;

/**
 * A run of blinks, as rules. Through the wind-up the boss gathers itself; then it blinks {@code hops} times, one every
 * {@code interval} ticks, each time through its quarry to the far side of it, {@code pass} blocks beyond, turned
 * {@code swerve} degrees off the way it came, the other way each time: so it zigzags back and forth through the quarry.
 * Everything within {@code width} blocks of the line it blinked along is hurt for {@code damage} with the corruption's own
 * hurt and laid under its curse, {@code curseTicks} at {@code curseLevel}; but any one creature by {@code hitsEach} of
 * the blinks at the most, though more of them pass through it (as every blink passes through the quarry).
 *
 * <p>Mind the curse: it bites for a share of the creature's whole health (see CorruptionCurseRules) whenever the time
 * it has left comes to a whole number of seconds. So a curse of whole seconds, laid again by every blink, bites again
 * at every blink, and a long or strong one kills anything at all, however much health it has. A curse of a second and
 * a half, laid again by every blink, bites just once, half a second after the last. Its bite, and the blinks' own hurt,
 * are turned aside by armour as any hurt is.
 */
public record PhaseDashRules(SkillTiming timing, double minRange, double maxRange, int hops, int interval, double pass, double swerve, double width,
                             double damage, int curseTicks, int curseLevel, int hitsEach) {
    public PhaseDashRules {
        hops = Math.max(1, hops);
        hitsEach = Math.max(1, hitsEach);
        interval = Math.max(1, interval);
        pass = Math.max(0, pass);
        width = Math.max(0, width);
        maxRange = Math.max(minRange, maxRange);
        // It runs at least until its last blink.
        timing = new SkillTiming(timing.windup(), Math.max(timing.ticks(), timing.windup() + (hops - 1) * interval + 1), timing.cooldown());
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Which blink falls on this tick of the skill, counted from 0; -1 if none does. */
    public int hopping(int elapsed) {
        if (elapsed < timing.windup()) return -1;
        int since = elapsed - timing.windup();
        if (since % interval != 0) return -1;
        int hop = since / interval;
        return hop < hops ? hop : -1;
    }

    /** Which way the hop-th blink swerves: 1 one way, -1 the other, by turns. */
    public static int side(int hop) { return hop % 2 == 0 ? 1 : -1; }

    /**
     * Where the hop-th blink lands, as an offset {x, z} from the quarry along the ground, the boss having set off from
     * {@code fromX, fromZ} (an offset from the quarry too): on along the way from there to the quarry, turned by its
     * swerve, {@code pass} blocks beyond it. Setting off from the quarry itself, it goes along +X.
     */
    public double[] landing(int hop, double fromX, double fromZ) {
        double length = Math.hypot(fromX, fromZ), wayX = length < 1e-6 ? 1 : -fromX / length, wayZ = length < 1e-6 ? 0 : -fromZ / length;
        double turn = Math.toRadians(swerve * side(hop)), cos = Math.cos(turn), sin = Math.sin(turn);
        return new double[]{(wayX * cos - wayZ * sin) * pass, (wayX * sin + wayZ * cos) * pass};
    }

    /** How far a point is from the segment between two others, every way. */
    public static double offLine(double ax, double ay, double az, double bx, double by, double bz, double px, double py, double pz) {
        double lx = bx - ax, ly = by - ay, lz = bz - az, square = lx * lx + ly * ly + lz * lz;
        double along = square < 1e-12 ? 0 : Math.max(0, Math.min(1, ((px - ax) * lx + (py - ay) * ly + (pz - az) * lz) / square));
        double nx = ax + lx * along - px, ny = ay + ly * along - py, nz = az + lz * along - pz;
        return Math.sqrt(nx * nx + ny * ny + nz * nz);
    }
}
