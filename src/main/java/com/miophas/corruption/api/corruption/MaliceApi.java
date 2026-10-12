package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.MaliceRules;

public final class MaliceApi {
    public static boolean succeeds(double roll, double chance) { return MaliceRules.succeeds(roll, chance); }
    public static int growthDirection(boolean aboveEmpty, boolean belowEmpty) { return MaliceRules.growthDirection(aboveEmpty, belowEmpty); }
    public static int height(int stage) { return MaliceRules.height(stage); }
    public static int growthKind(double roll, double greedChance, double vineChance) { return MaliceRules.growthKind(roll, greedChance, vineChance); }
    private MaliceApi() {}
}
