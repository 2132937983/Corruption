package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.RemnantShulkerRules;

public final class RemnantShulkerApi {
    public static int closedArmor() { return RemnantShulkerRules.CLOSED_ARMOR; }
    public static int teleportAttempts() { return RemnantShulkerRules.TELEPORT_ATTEMPTS; }
    public static int teleportRadius() { return RemnantShulkerRules.TELEPORT_RADIUS; }
    public static int firstShotTicks() { return RemnantShulkerRules.FIRST_SHOT_TICKS; }
    public static double attackRange() { return RemnantShulkerRules.ATTACK_RANGE; }
    public static float physicalPeek(float peek) { return RemnantShulkerRules.physicalPeek(peek); }
    public static int shotDelay(int randomDigit) { return RemnantShulkerRules.shotDelay(randomDigit); }
    public static float reproductionChance(int nearby) { return RemnantShulkerRules.reproductionChance(nearby); }
    private RemnantShulkerApi() {}
}
