package com.miophas.corruption.core.demon.skill;

/** Velocity of torn ground, independent of any boss or attack that tears it. */
public record DebrisRules(double radius, double speed) {
    public DebrisRules { radius = Math.max(0, radius); speed = Math.max(0, speed); }
    public static boolean tears(double hardness) { return hardness >= 0; }
    public double[] fling(double dx, double dy, double dz) {
        double out = Math.hypot(dx, dz), distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double share = radius <= 0 ? 1 : Math.max(.35, 1 - distance / (radius + 1));
        double wayX = out < 1e-6 ? 0 : dx / out, wayZ = out < 1e-6 ? 0 : dz / out;
        double up = speed * share, along = speed * share * Math.min(1, .35 + out / Math.max(1, radius));
        return new double[]{wayX * along, up, wayZ * along};
    }
}
