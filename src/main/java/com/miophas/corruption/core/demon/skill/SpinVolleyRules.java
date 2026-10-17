package com.miophas.corruption.core.demon.skill;

/**
 * A star's volley, as rules: a boss with points (a star's five) spins about the way it faces its quarry, and pours the
 * corruption's bolts out of every point at it. Through the wind-up it spins up; then, every {@code interval} ticks to
 * the end of the skill, each of its {@code points} sends a bolt at {@code speed} blocks a tick from its tip at the
 * quarry, aimed up to {@code spread} degrees off it, scattered so that no two come alike. The points turn {@code spin}
 * degrees a tick, so the bolts leave from a turning wheel: a quarry that stands still is struck, one that keeps moving
 * mostly is not. Each hurts what it strikes for {@code damage} with the corruption's own hurt, as any blow does (a
 * creature just struck is not struck again for a moment).
 */
public record SpinVolleyRules(SkillTiming timing, double minRange, double maxRange, int points, int interval, double spread, double speed,
                              double damage, double spin) {
    public SpinVolleyRules {
        points = Math.max(1, points);
        interval = Math.max(1, interval);
        spread = Math.max(0, Math.min(89, spread));
        speed = Math.max(.05, speed);
        maxRange = Math.max(minRange, maxRange);
    }

    public boolean inRange(double distance) { return distance >= minRange && distance <= maxRange; }

    /** Whether the points fire on this tick of the skill. */
    public boolean firing(int elapsed) { return elapsed >= timing.windup() && (elapsed - timing.windup()) % interval == 0; }

    /** How many bolts one use sends out in all. */
    public int bolts() { return points * ((timing.ticks() - 1 - timing.windup()) / interval + 1); }

    /**
     * How far round the points have turned by this tick of the skill, in degrees: winding up from still through the
     * wind-up, then at full spin. (A client asks between ticks, to draw the points where the bolts leave from.)
     */
    public double turned(double elapsed) {
        int windup = timing.windup();
        if (elapsed <= windup) return windup <= 0 ? 0 : spin * elapsed * elapsed / (2.0 * windup);
        return spin * windup / 2.0 + spin * (elapsed - windup);
    }

    /** Where the point-th point lies about the boss's face, in degrees from straight up, this far round: the points evenly spaced. */
    public double pointAt(int point, double turned) { return turned + point * 360.0 / points; }

    /** Where a point this many degrees round from straight up lies on the boss's face: {right, up}, a unit vector. */
    public static double[] tip(double degrees) {
        double round = Math.toRadians(degrees);
        return new double[]{Math.sin(round), Math.cos(round)};
    }

    /**
     * How far off its quarry the point-th bolt of the beat-th burst is aimed: {right, up} in degrees, no further than
     * {@code spread}, every bolt somewhere else (by the golden angle), so that they come as a scatter and not a line.
     */
    public double[] scatter(int beat, int point) {
        int index = beat * points + point;
        double angle = index * 2.3999632297, out = spread * Math.sqrt((index % 7 + .5) / 7);
        return new double[]{Math.cos(angle) * out, Math.sin(angle) * out};
    }
}
