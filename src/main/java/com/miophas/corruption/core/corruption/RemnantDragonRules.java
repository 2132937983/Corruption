package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.RemnantDragonPhase;

/** Vanilla dragon combat values, independent of entities, navigation and the End fight. */
public final class RemnantDragonRules {
    public static final double FOLLOW_RANGE = 96, CRYSTAL_RANGE = 32, FIREBALL_RANGE = 64;
    public static final int FIREBALL_WARMUP = 5, BREATH_WARMUP = 10, BREATH_DURATION = 200;
    public static final int FIREBALL_CLOUD_DURATION = 600, BREATH_REAPPLICATION = 20, MAX_BREATHS = 4;
    public static final float HEAD_DAMAGE = 10, WING_DAMAGE = 5, CRYSTAL_DAMAGE = 10;
    public static float receivedDamage(boolean head, float damage) {
        return damage <= 0 ? 0 : head ? damage : damage / 4 + Math.min(damage, 1);
    }
    public static double speed(RemnantDragonPhase phase) {
        return switch (phase) {
            case CHARGE -> 1.8;
            case TAKEOFF -> 1.1;
            case LANDING -> .55;
            case SCANNING, ROARING, BREATH, DYING -> 0;
            default -> .85;
        };
    }
    public static int duration(RemnantDragonPhase phase) {
        return switch (phase) {
            case HOLDING -> 100;
            case STRAFE, LANDING -> 160;
            case CHARGE -> 80;
            case SCANNING -> 25;
            case ROARING -> 40;
            case BREATH -> BREATH_DURATION;
            case TAKEOFF -> 60;
            case HOVER -> 40;
            case DYING -> 200;
        };
    }
    private RemnantDragonRules() {}
}
