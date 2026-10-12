package com.miophas.corruption.core.corruption;

public final class GreedRules {
    public static int height(int stage) { return com.miophas.corruption.core.plant.LivingPlantRules.definition(com.miophas.corruption.api.plant.LivingPlantKind.GREED).height(stage); }
    public static float width(int stage) { return com.miophas.corruption.core.plant.LivingPlantRules.definition(com.miophas.corruption.api.plant.LivingPlantKind.GREED).width(stage); }
    public static int neckSegments(int stage, int head) { return 2 + Math.clamp(stage, 0, 4) + (head == 2 ? 1 : 0); }
    public static double detectionRange(int stage, int base, int step) { return base + Math.clamp(stage, 0, 4) * (double) step; }
    public static int attackInterval(int stage) { return 40 - Math.clamp(stage, 0, 4) * 4; }
    public static final int WINDUP_TICKS = 8, CURSE_TICKS = 120, PROJECTILE_LIFETIME = 100;
    private GreedRules() {}
}
