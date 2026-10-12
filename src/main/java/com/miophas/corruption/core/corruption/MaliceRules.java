package com.miophas.corruption.core.corruption;

/** Ecology rules shared by generation, infection and planted entities. */
public final class MaliceRules {
    public static boolean succeeds(double roll, double chance) { return chance > 0 && roll < chance; }
    public static int growthDirection(boolean aboveEmpty, boolean belowEmpty) {
        return aboveEmpty && !belowEmpty ? 1 : belowEmpty && !aboveEmpty ? -1 : 0;
    }
    public static int height(int stage) { return com.miophas.corruption.core.plant.LivingPlantRules.definition(com.miophas.corruption.api.plant.LivingPlantKind.MALICE_VINE).height(stage); }
    public static int growthKind(double roll, double greedChance, double vineChance) {
        double greed = Math.clamp(greedChance, 0, 1);
        return roll < greed ? 2 : roll < greed + Math.min(Math.clamp(vineChance, 0, 1), 1 - greed) ? 1 : 0;
    }
    private MaliceRules() {}
}
