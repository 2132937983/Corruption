package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.GreedRules;

public final class GreedApi {
    public static int height(int stage) { return GreedRules.height(stage); }
    public static float width(int stage) { return GreedRules.width(stage); }
    public static int neckSegments(int stage, int head) { return GreedRules.neckSegments(stage, head); }
    public static double detectionRange(int stage, int base, int step) { return GreedRules.detectionRange(stage, base, step); }
    public static int attackInterval(int stage) { return GreedRules.attackInterval(stage); }
    public static int windupTicks() { return GreedRules.WINDUP_TICKS; }
    public static int curseTicks() { return GreedRules.CURSE_TICKS; }
    public static int projectileLifetime() { return GreedRules.PROJECTILE_LIFETIME; }
    private GreedApi() {}
}
