package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.BaalRules;
import java.util.List;

/** Baal's numbers for the entity, its model and its renderer. Lengths are model pixels unless a name says blocks. */
public final class BaalApi {
    public static float scale() { return BaalRules.SCALE; }
    public static double blocks(double pixels) { return BaalRules.blocks(pixels); }
    /** From the ground to the tips of the claws on its back. */
    public static double heightBlocks() { return BaalRules.blocks(BaalRules.TOP); }
    public static double skullTopBlocks() { return BaalRules.blocks(BaalRules.SKULL_TOP); }
    public static float eyeHeightBlocks() { return (float) BaalRules.blocks(BaalRules.EYE); }
    public static float bodyWidthBlocks() { return BaalRules.BODY_WIDTH_BLOCKS; }
    public static float bodyHeightBlocks() { return BaalRules.BODY_HEIGHT_BLOCKS; }

    public static double maxHealth() { return BaalRules.MAX_HEALTH; }
    public static double armor() { return BaalRules.ARMOR; }
    public static double armorToughness() { return BaalRules.ARMOR_TOUGHNESS; }
    /** The share of a hit that gets through, and how much more a hit on a weak part does. */
    public static float damageTaken(boolean weakPart) { return BaalRules.DAMAGE_TAKEN * (weakPart ? BaalRules.WEAK_PART_DAMAGE : 1); }
    public static double sightBlocks() { return BaalRules.SIGHT_BLOCKS; }
    public static double walkBlocksPerTick() { return BaalRules.WALK_BLOCKS_PER_TICK; }
    public static float turnDegreesPerTick() { return BaalRules.TURN_DEGREES_PER_TICK; }
    public static float walkFacingDegrees() { return BaalRules.WALK_FACING_DEGREES; }
    public static float strikeFacingDegrees() { return BaalRules.STRIKE_FACING_DEGREES; }
    public static int attackCooldownTicks() { return BaalRules.ATTACK_COOLDOWN_TICKS; }
    public static int deathTicks() { return BaalRules.DEATH_TICKS; }

    public static double cycles(double blocks) { return BaalRules.cycles(blocks); }
    public static double cyclesForTurn(double degrees) { return BaalRules.cyclesForTurn(degrees); }
    /** Which hooves came down between two points of the walk cycle: 1 left, 2 right. */
    public static int footfalls(double before, double after) { return BaalRules.footfalls(before, after); }
    public static ColossusGait gait(double cycle, float walk, BaalAttack attack, float attackTicks, float collapse) {
        return BaalRules.gait(cycle, walk, attack, attackTicks, collapse);
    }
    public static ColossusLegs legs() { return BaalRules.legs(); }
    public static float restHip() { return BaalRules.restHip(); }
    /** The three below run 0 to 1 through a move, for the model: a stomping hoof's height, a slamming arm drawn back, and its fall. */
    public static float stompRaise(BaalAttack attack, float ticks) { return (float) BaalRules.stompRaise(attack, ticks); }
    public static float slamRaise(BaalAttack attack, float ticks) { return (float) BaalRules.slamRaise(attack, ticks); }
    public static float slamStrike(BaalAttack attack, float ticks) { return (float) BaalRules.slamStrike(attack, ticks); }
    public static int hipPivot() { return BaalRules.hipPivot(); }

    public static List<ColossusPart> parts() { return BaalRules.parts(); }
    /** Where a part is for these legs: the middle of its base, sideways, height and forward. */
    public static float[] place(ColossusPart part, ColossusGait gait) { return BaalRules.place(part, gait); }

    public static BaalAttack attackFor(double forwardBlocks, double sideBlocks) { return BaalRules.attackFor(forwardBlocks, sideBlocks); }
    /** Where this attack lands, sideways and forward from Baal's centre. */
    public static float[] impact(BaalAttack attack, ColossusGait gait) { return BaalRules.impact(attack, gait); }
    public static double radiusBlocks(BaalAttack attack) { return BaalRules.radiusBlocks(attack); }
    public static float damage(BaalAttack attack, double distanceBlocks) {
        return BaalRules.damage(BaalRules.damage(attack), distanceBlocks, BaalRules.radiusBlocks(attack));
    }
    public static double roarRadiusBlocks() { return BaalRules.ROAR_RADIUS_BLOCKS; }
    public static int roarCurseTicks() { return BaalRules.ROAR_CURSE_TICKS; }
    public static int roarCurseAmplifier() { return BaalRules.ROAR_CURSE_AMPLIFIER; }
    public static int roarIntervalTicks() { return BaalRules.ROAR_INTERVAL_TICKS; }

    private BaalApi() {}
}
