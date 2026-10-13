package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.BaalAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLeg;
import com.miophas.corruption.api.corruption.ColossusLegs;
import com.miophas.corruption.api.corruption.ColossusPart;
import java.util.ArrayList;
import java.util.List;

/**
 * Baal's size, walk and attacks. Lengths are model pixels unless a name says blocks: the model is built at
 * the usual sixteen pixels to a block and shown SCALE times larger. Sideways is positive to Baal's left and
 * forward is the way it faces. The leg numbers repeat gamemodels/baal/build_model.js and must change with it.
 */
public final class BaalRules {
    /** How many times larger than its model Baal stands. Hitboxes, reach and stride all follow this. */
    public static final float SCALE = 10;
    public static final float BLOCKS_PER_PIXEL = SCALE / 16;
    /** Model measurements: the crown of the skull, the horn tips and the tips of the claws on its back. */
    public static final int SKULL_TOP = 296, HORN_TOP = 349, TOP = 374, EYE = 270;

    /** The box that stands for Baal itself, in blocks. It is small on purpose: the parts are what gets hit. */
    public static final float BODY_WIDTH_BLOCKS = 6, BODY_HEIGHT_BLOCKS = 10;
    /** Vanilla caps the health attribute at 1024, so the rest of Baal's toughness is in DAMAGE_TAKEN. */
    public static final double MAX_HEALTH = 1024, ARMOR = 20, ARMOR_TOUGHNESS = 12;
    public static final float DAMAGE_TAKEN = .25F, WEAK_PART_DAMAGE = 2;
    public static final double SIGHT_BLOCKS = 512, WALK_BLOCKS_PER_TICK = .3;
    public static final float TURN_DEGREES_PER_TICK = .6F;
    /** Baal only walks or strikes once it faces its target this closely. */
    public static final float WALK_FACING_DEGREES = 30, STRIKE_FACING_DEGREES = 8;
    public static final int ATTACK_COOLDOWN_TICKS = 50, DEATH_TICKS = 160;

    static final int HIP_PIVOT = 149, THIGH = 56, SHIN = 54, CANNON = 36, HOOF = 14, LEG_SIDE = 30, HOOF_CENTRE = 6;
    static final double REST_THIGH = 22, REST_SHIN = -48, REST_CANNON = 44, SPLAY = Math.toRadians(5), MAX_BEND = 2.6;
    /** One full cycle, left step and right step, covers this much ground. */
    public static final int CYCLE = 160;
    /** The share of a cycle each hoof spends on the ground; above a half, so both are down between steps. */
    static final double STANCE = .56, STEP = STANCE * CYCLE / 2, SWING_LIFT = 30, BOB = 5;
    static final double STOMP_LIFT = 46, STOMP_REACH = 12, COLLAPSED_HIP = 112, SLAM_CROUCH = .6;
    static final ColossusLegs LEGS = new ColossusLegs(THIGH, SHIN, CANNON, HOOF, LEG_SIDE, HOOF_CENTRE, REST_THIGH, REST_SHIN, REST_CANNON,
            Math.toDegrees(SPLAY), MAX_BEND, CYCLE, STANCE, SWING_LIFT, BOB, STOMP_LIFT, STOMP_REACH, COLLAPSED_HIP);

    /** Where a slamming hand lands: this far to its own side and this far ahead of the hips. BaalModel's slam pose is tuned to this. */
    public static final int SLAM_SIDE = 17, SLAM_FORWARD = 154;
    public static final double STOMP_RANGE_BLOCKS = 70, SLAM_RANGE_BLOCKS = 125;
    public static final double FOOTFALL_RADIUS_BLOCKS = 26, STOMP_RADIUS_BLOCKS = 60, SLAM_RADIUS_BLOCKS = 34;
    public static final float FOOTFALL_DAMAGE = 30, STOMP_DAMAGE = 50, SLAM_DAMAGE = 70, EDGE_DAMAGE = .3F;
    public static final double ROAR_RADIUS_BLOCKS = 192;
    public static final int ROAR_CURSE_TICKS = 300, ROAR_CURSE_AMPLIFIER = 1, ROAR_INTERVAL_TICKS = 1200;

    private static final List<ColossusPart> PARTS = buildParts();

    public static double blocks(double pixels) { return pixels * BLOCKS_PER_PIXEL; }
    public static ColossusLegs legs() { return LEGS; }
    public static float restHip() { return (float) ColossusGaitRules.restHip(LEGS); }
    /** The hips' height in the model file, which the animated height replaces. */
    public static int hipPivot() { return HIP_PIVOT; }
    public static List<ColossusPart> parts() { return PARTS; }

    /** How far through its walk cycle this many blocks carry Baal. */
    public static double cycles(double blocks) { return ColossusGaitRules.cycles(LEGS, blocks / BLOCKS_PER_PIXEL); }
    /** Turning on the spot makes it step too: its hooves travel an arc around its centre. */
    public static double cyclesForTurn(double degrees) { return ColossusGaitRules.cyclesForTurn(LEGS, degrees); }
    /** Which hooves came down while the walk moved between these two points of the cycle: 1 left, 2 right. */
    public static int footfalls(double before, double after) { return ColossusGaitRules.footfalls(before, after); }

    /** How high a stomping hoof is held, 0 to 1: a slow raise, then down in the last few ticks before it lands. */
    public static double stompRaise(BaalAttack attack, float ticks) {
        double fall = 5, top = attack.impactTick() - fall;
        if (!attack.stomp() || ticks <= 0 || ticks >= attack.impactTick()) return 0;
        if (ticks < top) { double rise = ticks / top; return rise * rise * (3 - 2 * rise); }
        double left = (attack.impactTick() - ticks) / fall;
        return left * left;
    }

    private static double ease(double amount) { amount = Math.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }

    /** How far a slamming arm is drawn back over the head, 0 to 1: up through the wind-up, spent as it falls. */
    public static double slamRaise(BaalAttack attack, float ticks) {
        double top = attack.impactTick() * .6;
        if (!attack.slam() || ticks <= 0 || ticks >= attack.impactTick()) return 0;
        return ticks < top ? ease(ticks / top) : 1 - ease((ticks - top) / (attack.impactTick() - top));
    }

    /** How far the blow itself has come, 0 to 1: the arm falls faster and faster, rests on the ground, then lifts. */
    public static double slamStrike(BaalAttack attack, float ticks) {
        double start = attack.impactTick() * .6, rest = attack.impactTick() + 12;
        if (!attack.slam() || ticks <= start) return 0;
        if (ticks < attack.impactTick()) { double fall = (ticks - start) / (attack.impactTick() - start); return fall * fall; }
        return ticks <= rest ? 1 : 1 - ease((ticks - rest) / (attack.ticks() - rest));
    }

    /**
     * The legs at one moment, on the gait every colossus shares.
     *
     * @param cycle how far Baal has walked, in cycles
     * @param walk 0 standing to 1 walking, so it can ease in and out of its stride
     * @param collapse 0 standing to 1 sunk onto its haunches, for its death; a slam also crouches it part of the way
     */
    public static ColossusGait gait(double cycle, float walk, BaalAttack attack, float attackTicks, float collapse) {
        double raise = stompRaise(attack, attackTicks);
        return ColossusGaitRules.gait(LEGS, cycle, walk, attack.left() ? raise : 0, attack.left() ? 0 : raise,
                Math.max(Math.clamp(collapse, 0, 1), SLAM_CROUCH * slamStrike(attack, attackTicks)));
    }

    /** Where a part is for these legs: the middle of its base, sideways, height and forward. */
    public static float[] place(ColossusPart part, ColossusGait gait) { return ColossusGaitRules.place(LEGS, part, gait); }

    /** What Baal does about a target this far ahead and to its left, in blocks, once it faces it. NONE means walk closer. */
    public static BaalAttack attackFor(double forward, double side) {
        double distance = Math.hypot(forward, side);
        if (distance <= STOMP_RANGE_BLOCKS) return side >= 0 ? BaalAttack.STOMP_LEFT : BaalAttack.STOMP_RIGHT;
        if (forward > 0 && distance <= SLAM_RANGE_BLOCKS) return side >= 0 ? BaalAttack.SLAM_LEFT : BaalAttack.SLAM_RIGHT;
        return BaalAttack.NONE;
    }

    /** Where this attack lands, sideways and forward from Baal's centre. */
    public static float[] impact(BaalAttack attack, ColossusGait gait) {
        if (attack.slam()) return new float[]{attack.left() ? SLAM_SIDE : -SLAM_SIDE, SLAM_FORWARD};
        ColossusLeg leg = gait.leg(attack.left());
        return new float[]{leg.side(), leg.forward()};
    }

    public static double radiusBlocks(BaalAttack attack) { return attack.slam() ? SLAM_RADIUS_BLOCKS : attack.stomp() ? STOMP_RADIUS_BLOCKS : FOOTFALL_RADIUS_BLOCKS; }
    public static float damage(BaalAttack attack) { return attack.slam() ? SLAM_DAMAGE : attack.stomp() ? STOMP_DAMAGE : FOOTFALL_DAMAGE; }

    /** Full damage at the middle of a blow, fading to EDGE_DAMAGE of it at the rim and nothing beyond. */
    public static float damage(float full, double distanceBlocks, double radiusBlocks) {
        return distanceBlocks > radiusBlocks ? 0 : (float) (full * (1 - (1 - EDGE_DAMAGE) * distanceBlocks / radiusBlocks));
    }

    private static List<ColossusPart> buildParts() {
        List<ColossusPart> parts = new ArrayList<>();
        parts.add(new ColossusPart("hips", 0, 135, 0, 84, 36, false));
        parts.add(new ColossusPart("abdomen", 0, 165, 2, 60, 34, false));
        parts.add(new ColossusPart("chest", 0, 180, 15, 100, 76, false));
        parts.add(new ColossusPart("head", 0, 236, 82, 62, 62, true));
        for (int tail = 0; tail < 5; tail++)
            parts.add(new ColossusPart("tail_" + tail, 0, new int[]{118, 108, 105, 108, 125}[tail], -40 - tail * 45.5F, new int[]{44, 40, 36, 34, 36}[tail], new int[]{44, 34, 26, 26, 90}[tail], false));
        for (int index = 0; index < 2; index++) {
            String name = index == 0 ? "left_" : "right_";
            int side = index == 0 ? 1 : -1;
            parts.add(new ColossusPart(name + "arm", side * 68, 160, 20, 46, 100, false));
            parts.add(new ColossusPart(name + "forearm", side * 78, 82, 22, 46, 92, false));
            parts.add(new ColossusPart(name + "hand", side * 79, 24, 70, 60, 76, false));
            parts.add(new ColossusPart(name + "wing", side * 66, 262, 6, 92, 112, false));
            // These three follow the leg; place() works out where they are.
            parts.add(new ColossusPart(name + "thigh", side * LEG_SIDE, 90, 0, 46, 62, false));
            parts.add(new ColossusPart(name + "shin", side * LEG_SIDE, 14, 0, 38, 88, false));
            parts.add(new ColossusPart(name + "hoof", side * LEG_SIDE, 0, 0, 48, 24, false));
        }
        return List.copyOf(parts);
    }

    private BaalRules() {}
}
