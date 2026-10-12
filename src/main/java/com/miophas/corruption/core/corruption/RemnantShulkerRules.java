package com.miophas.corruption.core.corruption;

/** Bounded native shell, attack and teleport rules; independent of world access. */
public final class RemnantShulkerRules {
    public static final int CLOSED_ARMOR = 20, TELEPORT_ATTEMPTS = 5, TELEPORT_RADIUS = 8;
    public static final int FIRST_SHOT_TICKS = 20;
    public static final double ATTACK_RANGE = 20;
    public static float physicalPeek(float peek) {
        return (float) (0.5 - Math.sin((0.5 + Math.clamp(peek, 0, 1)) * Math.PI) * 0.5);
    }
    public static int shotDelay(int randomDigit) { return 20 + Math.clamp(randomDigit, 0, 9) * 10; }
    public static float reproductionChance(int nearby) { return Math.clamp(1 - (nearby - 1) / 5F, 0, 1); }
    private RemnantShulkerRules() {}
}
