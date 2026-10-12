package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.RemnantDragonRules;

public final class RemnantDragonApi {
    public static double followRange() { return RemnantDragonRules.FOLLOW_RANGE; }
    public static double crystalRange() { return RemnantDragonRules.CRYSTAL_RANGE; }
    public static double fireballRange() { return RemnantDragonRules.FIREBALL_RANGE; }
    public static int fireballWarmup() { return RemnantDragonRules.FIREBALL_WARMUP; }
    public static int breathWarmup() { return RemnantDragonRules.BREATH_WARMUP; }
    public static int breathDuration() { return RemnantDragonRules.BREATH_DURATION; }
    public static int cloudDuration() { return RemnantDragonRules.FIREBALL_CLOUD_DURATION; }
    public static int reapplication() { return RemnantDragonRules.BREATH_REAPPLICATION; }
    public static int maxBreaths() { return RemnantDragonRules.MAX_BREATHS; }
    public static float headDamage() { return RemnantDragonRules.HEAD_DAMAGE; }
    public static float wingDamage() { return RemnantDragonRules.WING_DAMAGE; }
    public static float crystalDamage() { return RemnantDragonRules.CRYSTAL_DAMAGE; }
    public static float receivedDamage(boolean head, float damage) { return RemnantDragonRules.receivedDamage(head, damage); }
    public static double speed(RemnantDragonPhase phase) { return RemnantDragonRules.speed(phase); }
    public static int duration(RemnantDragonPhase phase) { return RemnantDragonRules.duration(phase); }
    private RemnantDragonApi() {}
}
