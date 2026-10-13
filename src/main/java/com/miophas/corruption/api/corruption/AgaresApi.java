package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.AgaresRules;
import java.util.List;

/** Agares's numbers for the entity, its model and its renderer. Lengths are model pixels unless a name says blocks. */
public final class AgaresApi {
    public static float scale() { return AgaresRules.SCALE; }
    public static double blocks(double pixels) { return AgaresRules.blocks(pixels); }
    /** From the ground to the tips of its crest. */
    public static double heightBlocks() { return AgaresRules.blocks(AgaresRules.TOP); }
    public static double skullTopBlocks() { return AgaresRules.blocks(AgaresRules.SKULL_TOP); }
    public static float eyeHeightBlocks() { return (float) AgaresRules.blocks(AgaresRules.EYE); }
    public static float bodyWidthBlocks() { return AgaresRules.BODY_WIDTH_BLOCKS; }
    public static float bodyHeightBlocks() { return AgaresRules.BODY_HEIGHT_BLOCKS; }

    public static double maxHealth() { return AgaresRules.MAX_HEALTH; }
    public static double armor() { return AgaresRules.ARMOR; }
    public static double armorToughness() { return AgaresRules.ARMOR_TOUGHNESS; }
    /** The share of a hit that gets through, and how much more a hit on a weak part does. */
    public static float damageTaken(boolean weakPart) { return AgaresRules.DAMAGE_TAKEN * (weakPart ? AgaresRules.WEAK_PART_DAMAGE : 1); }
    public static double sightBlocks() { return AgaresRules.SIGHT_BLOCKS; }
    public static double walkBlocksPerTick() { return AgaresRules.WALK_BLOCKS_PER_TICK; }
    public static float turnDegreesPerTick() { return AgaresRules.TURN_DEGREES_PER_TICK; }
    public static float walkFacingDegrees() { return AgaresRules.WALK_FACING_DEGREES; }
    public static int attackCooldownTicks() { return AgaresRules.ATTACK_COOLDOWN_TICKS; }
    public static int deathTicks() { return AgaresRules.DEATH_TICKS; }
    /** It walks towards prey farther off than this, and otherwise stands and fights. */
    public static double standOffBlocks() { return AgaresRules.STAND_OFF_BLOCKS; }

    public static ColossusLegs legs() { return AgaresRules.legs(); }
    public static ColossusGait gait(double cycle, float walk, AgaresAttack attack, float attackTicks, float collapse) {
        return AgaresRules.gait(cycle, walk, attack, attackTicks, collapse);
    }
    public static float restHip() { return AgaresRules.restHip(); }
    public static int hipPivot() { return AgaresRules.hipPivot(); }
    public static List<ColossusPart> parts() { return AgaresRules.parts(); }

    /** The ones below run 0 to 1 through a move, for the model. */
    public static float stompRaise(AgaresAttack attack, float ticks) { return (float) AgaresRules.stompRaise(attack, ticks); }
    public static float biteRear(AgaresAttack attack, float ticks) { return (float) AgaresRules.biteRear(attack, ticks); }
    public static float biteDown(AgaresAttack attack, float ticks) { return (float) AgaresRules.biteDown(attack, ticks); }
    public static float biteRake(AgaresAttack attack, float ticks) { return (float) AgaresRules.biteRake(attack, ticks); }
    public static float gustOpen(AgaresAttack attack, float ticks) { return (float) AgaresRules.gustOpen(attack, ticks); }
    public static float gustBeat(AgaresAttack attack, float ticks) { return (float) AgaresRules.gustBeat(attack, ticks); }
    /** Which way the tail points in a sweep, in degrees from straight back, positive towards its left. */
    public static float tailAngle(AgaresAttack attack, float ticks) { return (float) AgaresRules.tailAngle(attack, ticks); }

    public static AgaresAttack attackFor(double forwardBlocks, double sideBlocks, double heightBlocks, float off, boolean screechDue) {
        return AgaresRules.attackFor(forwardBlocks, sideBlocks, heightBlocks, off, screechDue);
    }
    /** Where the jaws are on the ground at this tick of a bite, in pixels ahead of the hips. */
    public static double biteForward(float ticks) { return AgaresRules.biteForward(ticks); }
    public static boolean tailHits(AgaresAttack attack, int ticks, double forwardBlocks, double sideBlocks) { return AgaresRules.tailHits(attack, ticks, forwardBlocks, sideBlocks); }
    public static boolean gustHits(double forwardBlocks, double sideBlocks) { return AgaresRules.gustHits(forwardBlocks, sideBlocks); }
    public static double radiusBlocks(AgaresAttack attack) { return AgaresRules.radiusBlocks(attack); }
    /** What a blow does this far from its middle. */
    public static float damage(AgaresAttack attack, double distanceBlocks) {
        return AgaresRules.damage(AgaresRules.damage(attack), distanceBlocks, AgaresRules.radiusBlocks(attack));
    }
    /** What the tail and the wings do to whatever they catch. */
    public static float flatDamage(AgaresAttack attack) { return AgaresRules.damage(attack); }
    public static double tailReachBlocks() { return AgaresRules.TAIL_FAR_BLOCKS; }
    public static double gustRangeBlocks() { return AgaresRules.GUST_RANGE_BLOCKS; }
    public static double gustPush() { return AgaresRules.GUST_PUSH; }
    public static double screechRadiusBlocks() { return AgaresRules.SCREECH_RADIUS_BLOCKS; }
    public static int screechCurseTicks() { return AgaresRules.SCREECH_CURSE_TICKS; }
    public static int screechCurseAmplifier() { return AgaresRules.SCREECH_CURSE_AMPLIFIER; }
    public static int screechIntervalTicks() { return AgaresRules.SCREECH_INTERVAL_TICKS; }

    private AgaresApi() {}
}
