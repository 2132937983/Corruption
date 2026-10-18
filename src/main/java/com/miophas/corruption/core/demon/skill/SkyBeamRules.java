package com.miophas.corruption.core.demon.skill;

/**
 * Beams of light driven straight down out of magic circles in the sky, as rules: lightning and a beam in one. Every beam
 * has a circle of its own, hanging {@code altitude} blocks over the ground where the quarry stood. As it begins the circles
 * open there, and where each beam will first strike is marked on the ground under its circle: that is its wind-up, its
 * warning, a third of the whole ({@link #WARNING}). Then the beams pour down for the rest. Every {@code every} ticks they
 * hurt whatever is within {@code width} of a beam's middle (anywhere from the ground up to its circle) for {@code damage}.
 *
 * <p>A single beam's one circle hangs over the mark, its beam striking the mark itself. A star's circles hang at its points
 * ({@link #spread}) round the mark: five for a pentagram, six for a hexagram, eight for an octagram, every one pouring a beam
 * as wide as the single one would be. As they pour, the star turns slowly ({@link #SPIN}) and closes in, circles and beams
 * together, until they meet on the mark as they end. Whoever is inside must get out before they meet: through a gap between
 * two beams, or across where one will strike while it is still only marked.
 *
 * <p>Where the beams are at each moment ({@link #points}) is worked out here, for the server that hurts with them and the
 * client that draws them alike, so that what is seen is what strikes.
 *
 * @param timing   its wind-up (the warning) and its cooldown; the whole is made three times the wind-up
 * @param beams    how many beams: 1, or a star's points, 5, 6 or 8
 * @param altitude how high over the mark the circles hang, the beams pouring from them
 * @param width    how far from the middle of each beam it reaches
 * @param damage   the hurt of one pulse
 * @param every    ticks between pulses, the first as the beams strike
 */
public record SkyBeamRules(SkillTiming timing, double minRange, double maxRange, int beams, double altitude, double width, double damage, int every) {
    /** The share of the whole its warning takes, before the beams strike: a third. */
    public static final double WARNING = 1 / 3.0;
    /** How fast a star turns as it pours, in degrees a tick: slowly enough that a gap between two of its beams can be run through. */
    public static final double SPIN = .25;

    public SkyBeamRules {
        if (beams != 1 && beams != 5 && beams != 6 && beams != 8) throw new IllegalArgumentException("One beam, or a star of 5, 6 or 8: " + beams);
        int warning = Math.max(1, timing.windup());
        timing = new SkillTiming(warning, warning * 3, timing.cooldown());
        maxRange = Math.max(minRange, maxRange);
        width = Math.max(0, width);
        every = Math.max(1, every);
    }

    /** How far from the mark a star's points stand as it opens ({@link #spread(int, double)}); a single beam has none. */
    public double spread() { return spread(beams, width); }

    /** How far round a beam's magic circle reaches: a quarter as far again as its beam, and .8 more. */
    public static double circle(double width) { return width * 1.25 + .8; }

    /**
     * How far from the mark the points of a star of beams this wide stand as it opens: far enough out that between two
     * neighbouring beams there is as much again as one reaches, and 1.6 more, to run out through; their circles
     * ({@link #circle}) stand apart. A single beam has none.
     */
    public static double spread(int beams, double width) { return beams <= 1 ? 0 : (Math.max(0, width) * 1.5 + .8) / Math.sin(Math.PI / beams); }

    /** How far round each of a star's beams reaches, its points standing this far out as it opens: {@link #spread(int, double)} undone. A single beam's, its own. */
    public static double width(int beams, double spread) {
        return beams <= 1 ? spread : Math.max(0, (spread * Math.sin(Math.PI / beams) - .8) / 1.5);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** How many ticks the beams pour: the whole after the warning. */
    public int pouring() { return timing.ticks() - timing.windup(); }

    /** Whether the beams pour on this tick of it. */
    public boolean pours(int elapsed) { return elapsed >= timing.windup() && elapsed < timing.ticks(); }

    /** Whether the beams hurt on this tick of it. */
    public boolean pulses(int elapsed) { return pours(elapsed) && (elapsed - timing.windup()) % every == 0; }

    /** Where its beams (and their circles) are {@code since} ticks after they began to pour, the star first turned {@code turn} degrees: {x, z} from the mark, each. */
    public double[][] points(double turn, double since) { return points(beams, spread(), turn, since, pouring()); }

    /**
     * Whether something whose middle stands {@code across} blocks from a beam's middle, along the ground, is caught by it,
     * its own half-width allowed.
     */
    public boolean catches(double across, double halfWidth) { return across <= width + halfWidth; }

    /**
     * Where beams out of circles in the sky strike, and their circles hang, {x, z} from the mark each: a single beam on the
     * mark; a star's points round it, {@code spread} from it, the first turned {@code turn} degrees (0 towards +x, then
     * towards +z). Through the {@code pouring} ticks they pour, {@code since} ticks into them, the star is turned {@link #SPIN}
     * degrees a tick further and closed in evenly, to meet on the mark at the end. Before they pour (less than 0), where
     * they will first strike.
     */
    public static double[][] points(int beams, double spread, double turn, double since, double pouring) {
        double[][] at = new double[Math.max(1, beams)][2];
        if (beams <= 1 || spread <= 0) return at;
        double poured = Math.max(0, since), share = Math.min(1, poured / Math.max(1, pouring));
        double out = spread * (1 - share), angle = Math.toRadians(turn + SPIN * poured);
        for (int beam = 0; beam < beams; beam++) {
            double a = angle + 2 * Math.PI * beam / beams;
            at[beam][0] = Math.cos(a) * out;
            at[beam][1] = Math.sin(a) * out;
        }
        return at;
    }
}
