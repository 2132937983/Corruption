package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.AgaresAttack;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLegs;
import com.miophas.corruption.api.corruption.ColossusPart;
import java.util.ArrayList;
import java.util.List;

/**
 * Agares's size, walk and attacks. Lengths are model pixels unless a name says blocks: the model is built at
 * the usual sixteen pixels to a block and shown SCALE times larger. Sideways is positive to its left and
 * forward is the way it faces. The measurements repeat the model exported from gamemodels/agares and must
 * change with it; AgaresModelTest checks that they still agree.
 */
public final class AgaresRules {
    /** How many times larger than its model Agares stands. Hitboxes, reach and stride all follow this. */
    public static final float SCALE = 10;
    public static final float BLOCKS_PER_PIXEL = SCALE / 16;
    /** Model measurements: the crown of the skull, the tips of the crest above it, and its eyes. */
    public static final int SKULL_TOP = 1338, TOP = 1465, EYE = 1270;

    /** The box that stands for Agares itself, in blocks. It is small on purpose: the parts are what gets hit. */
    public static final float BODY_WIDTH_BLOCKS = 10, BODY_HEIGHT_BLOCKS = 16;
    /** Vanilla caps the health attribute at 1024, so the rest of its toughness is in DAMAGE_TAKEN. */
    public static final double MAX_HEALTH = 1024, ARMOR = 24, ARMOR_TOUGHNESS = 16;
    public static final float DAMAGE_TAKEN = .1F, WEAK_PART_DAMAGE = 2;
    public static final double SIGHT_BLOCKS = 512, WALK_BLOCKS_PER_TICK = .6;
    public static final float TURN_DEGREES_PER_TICK = .35F;
    /** It only walks, bites or beats its wings once it faces its prey this closely. */
    public static final float WALK_FACING_DEGREES = 30, BITE_FACING_DEGREES = 6, GUST_FACING_DEGREES = 20;
    public static final int ATTACK_COOLDOWN_TICKS = 60, DEATH_TICKS = 260;
    /** It closes to this distance, then stands and fights: everything in front of it is within reach of its jaws. */
    public static final double STAND_OFF_BLOCKS = 380, BITE_FACING_MARGIN_BLOCKS = 20;

    static final int HIP_PIVOT = 517;
    static final ColossusLegs LEGS = new ColossusLegs(191, 224, 166, 38, 106, 40, 38, -76, 62, 6, 1.9, 540, .56, 90, 14, 140, 30, 330);
    /** How far a bite sinks it towards its haunches. */
    static final double BITE_CROUCH = .55;

    /** The jaws come down this far ahead of the hips, pass this far at the middle of the rake, and end out here. Measured from AgaresModel's bite poses. */
    public static final int BITE_NEAR = 392, BITE_MID = 642, BITE_FAR = 964;
    public static final double STOMP_RANGE_BLOCKS = 180, FOOTFALL_RADIUS_BLOCKS = 70, STOMP_RADIUS_BLOCKS = 190, BITE_RADIUS_BLOCKS = 70;
    public static final float FOOTFALL_DAMAGE = 40, STOMP_DAMAGE = 70, BITE_DAMAGE = 90, TAIL_DAMAGE = 60, GUST_DAMAGE = 25, EDGE_DAMAGE = .3F;
    /** The tail leaves the body this far behind the hips, and swings this many degrees either side of straight back. */
    static final int TAIL_ROOT = 93;
    public static final double TAIL_SWING_DEGREES = 95, TAIL_NEAR_BLOCKS = 40, TAIL_FAR_BLOCKS = 640, TAIL_HALF_WIDTH_BLOCKS = 55;
    /** Prey this high above its feet is out of reach of everything but the wings. */
    public static final double GUST_HEIGHT_BLOCKS = 60, GUST_RANGE_BLOCKS = 520, GUST_HALF_ANGLE_DEGREES = 55, GUST_PUSH = 3.2;
    public static final double SCREECH_RADIUS_BLOCKS = 420;
    public static final int SCREECH_CURSE_TICKS = 400, SCREECH_CURSE_AMPLIFIER = 2, SCREECH_INTERVAL_TICKS = 1500;

    private static final List<ColossusPart> PARTS = buildParts();

    public static double blocks(double pixels) { return pixels * BLOCKS_PER_PIXEL; }
    public static ColossusLegs legs() { return LEGS; }
    public static float restHip() { return (float) ColossusGaitRules.restHip(LEGS); }
    /** The hips' height in the model file, which the animated height replaces. */
    public static int hipPivot() { return HIP_PIVOT; }
    public static List<ColossusPart> parts() { return PARTS; }

    private static double ease(double amount) { amount = Math.clamp(amount, 0, 1); return amount * amount * (3 - 2 * amount); }
    /** 0 before from, 1 after to, eased between. */
    private static double rise(double ticks, double from, double to) { return ease((ticks - from) / (to - from)); }

    /** How high a stomping foot is held, 0 to 1: a slow raise, then down in the last few ticks before it lands. */
    public static double stompRaise(AgaresAttack attack, float ticks) {
        double fall = 6, top = attack.strikeFrom() - fall;
        if (!attack.stomp() || ticks <= 0 || ticks >= attack.strikeFrom()) return 0;
        if (ticks < top) return ease(ticks / top);
        double left = (attack.strikeFrom() - ticks) / fall;
        return left * left;
    }

    /** How far it has reared back before a bite, 0 to 1: up through the wind-up, spent as the head comes down. */
    public static double biteRear(AgaresAttack attack, float ticks) {
        if (attack != AgaresAttack.BITE) return 0;
        return rise(ticks, 0, 40) * (1 - rise(ticks, 50, attack.strikeFrom()));
    }
    /** How far its head is down at the ground, 0 to 1: it falls faster and faster, stays down through the rake, then lifts. */
    public static double biteDown(AgaresAttack attack, float ticks) {
        if (attack != AgaresAttack.BITE || ticks <= 50) return 0;
        if (ticks < attack.strikeFrom()) { double fall = (ticks - 50) / (attack.strikeFrom() - 50); return fall * fall; }
        return 1 - rise(ticks, attack.strikeTo() + 8, attack.ticks());
    }
    /** How far the jaws have been raked out along the ground, 0 at its feet to 1 at full stretch. */
    public static double biteRake(AgaresAttack attack, float ticks) {
        if (attack != AgaresAttack.BITE) return 0;
        return Math.clamp((ticks - attack.strikeFrom()) / (double) (attack.strikeTo() - attack.strikeFrom()), 0, 1);
    }
    /** Where the jaws are on the ground at this tick of a bite, ahead of the hips: the curve through the three measured places. */
    public static double biteForward(float ticks) {
        double rake = biteRake(AgaresAttack.BITE, ticks);
        return (1 - rake) * (1 - 2 * rake) * BITE_NEAR + 4 * rake * (1 - rake) * BITE_MID + rake * (2 * rake - 1) * BITE_FAR;
    }

    /**
     * Which way the tail points at this tick of a sweep, in degrees from straight back, positive towards
     * its left. It is drawn aside, whipped across to the other side, and let fall back.
     */
    public static double tailAngle(AgaresAttack attack, float ticks) {
        if (!attack.tail()) return 0;
        double across = 2 * rise(ticks, attack.strikeFrom(), attack.strikeTo()) - 1;
        return (attack.left() ? 1 : -1) * TAIL_SWING_DEGREES * across * rise(ticks, 0, attack.strikeFrom() - 4) * (1 - rise(ticks, attack.strikeTo() + 8, attack.ticks()));
    }
    /** Whether the sweeping tail passes over a place this far ahead and to the left, in blocks, at this tick. */
    public static boolean tailHits(AgaresAttack attack, int ticks, double forward, double side) {
        if (!attack.tail() || !attack.strikes(ticks)) return false;
        double angle = Math.toRadians(tailAngle(attack, ticks)), alongSide = Math.sin(angle), alongForward = -Math.cos(angle);
        double behind = forward + blocks(TAIL_ROOT), along = side * alongSide + behind * alongForward, across = Math.abs(side * alongForward - behind * alongSide);
        return along >= TAIL_NEAR_BLOCKS && along <= TAIL_FAR_BLOCKS && across <= TAIL_HALF_WIDTH_BLOCKS;
    }

    /** How far the wings are opened for a gust, 0 to 1, and how far through the beat they are. */
    public static double gustOpen(AgaresAttack attack, float ticks) { return attack != AgaresAttack.GUST ? 0 : rise(ticks, 0, 54) * (1 - rise(ticks, 100, attack.ticks())); }
    public static double gustBeat(AgaresAttack attack, float ticks) {
        if (attack != AgaresAttack.GUST || ticks <= 58) return 0;
        if (ticks < attack.strikeFrom()) { double fall = (ticks - 58) / (attack.strikeFrom() - 58); return fall * fall; }
        return 1 - rise(ticks, 84, 130);
    }
    /** Whether a gust reaches a place this far ahead and to the left, in blocks. Height does not save anything. */
    public static boolean gustHits(double forward, double side) {
        return forward > 0 && Math.hypot(forward, side) <= GUST_RANGE_BLOCKS && Math.abs(Math.toDegrees(Math.atan2(side, forward))) <= GUST_HALF_ANGLE_DEGREES;
    }

    /**
     * The legs at one moment, on the gait every colossus shares.
     *
     * @param collapse 0 standing to 1 sunk onto its haunches, for its death; a bite also crouches it part of the way
     */
    public static ColossusGait gait(double cycle, float walk, AgaresAttack attack, float attackTicks, float collapse) {
        double raise = stompRaise(attack, attackTicks);
        return ColossusGaitRules.gait(LEGS, cycle, walk, attack.left() ? raise : 0, attack.left() ? 0 : raise,
                Math.max(Math.clamp(collapse, 0, 1), BITE_CROUCH * biteDown(attack, attackTicks)));
    }

    /** Where a part is for these legs: the middle of its base, sideways, height and forward. */
    public static float[] place(ColossusPart part, ColossusGait gait) { return ColossusGaitRules.place(LEGS, part, gait); }

    /**
     * What Agares does about prey this far ahead and to its left and this high above its feet, in blocks,
     * when it is this many degrees from facing it. NONE means there is nothing to do yet but turn or walk.
     */
    public static AgaresAttack attackFor(double forward, double side, double height, float off, boolean screechDue) {
        double distance = Math.hypot(forward, side), bearing = Math.abs(Math.toDegrees(Math.atan2(side, forward)));
        if (screechDue && distance <= SCREECH_RADIUS_BLOCKS) return AgaresAttack.SCREECH;
        if (height > GUST_HEIGHT_BLOCKS) return gustHits(forward, side) && off <= GUST_FACING_DEGREES ? AgaresAttack.GUST : AgaresAttack.NONE;
        if (distance <= STOMP_RANGE_BLOCKS) return side >= 0 ? AgaresAttack.STOMP_LEFT : AgaresAttack.STOMP_RIGHT;
        if (bearing >= 180 - TAIL_SWING_DEGREES + 5 && distance >= TAIL_NEAR_BLOCKS && distance <= TAIL_FAR_BLOCKS - blocks(TAIL_ROOT))
            return side >= 0 ? AgaresAttack.TAIL_LEFT : AgaresAttack.TAIL_RIGHT;
        // Its jaws reach farther than this, but it comes on until its prey is well inside them.
        if (forward > 0 && off <= BITE_FACING_DEGREES && distance >= blocks(BITE_NEAR) - BITE_RADIUS_BLOCKS && distance <= STAND_OFF_BLOCKS + BITE_FACING_MARGIN_BLOCKS) return AgaresAttack.BITE;
        return AgaresAttack.NONE;
    }

    public static double radiusBlocks(AgaresAttack attack) { return attack.stomp() ? STOMP_RADIUS_BLOCKS : attack == AgaresAttack.BITE ? BITE_RADIUS_BLOCKS : FOOTFALL_RADIUS_BLOCKS; }
    public static float damage(AgaresAttack attack) {
        return switch (attack) {
            case BITE -> BITE_DAMAGE;
            case STOMP_LEFT, STOMP_RIGHT -> STOMP_DAMAGE;
            case TAIL_LEFT, TAIL_RIGHT -> TAIL_DAMAGE;
            case GUST -> GUST_DAMAGE;
            default -> FOOTFALL_DAMAGE;
        };
    }
    /** Full damage at the middle of a blow, fading to EDGE_DAMAGE of it at the rim and nothing beyond. */
    public static float damage(float full, double distanceBlocks, double radiusBlocks) {
        return distanceBlocks > radiusBlocks ? 0 : (float) (full * (1 - (1 - EDGE_DAMAGE) * distanceBlocks / radiusBlocks));
    }

    private static List<ColossusPart> buildParts() {
        List<ColossusPart> parts = new ArrayList<>();
        parts.add(new ColossusPart("hips", 0, 406, -11, 260, 194, false));
        parts.add(new ColossusPart("belly", 0, 600, 40, 290, 130, false));
        parts.add(new ColossusPart("chest", 0, 730, 150, 380, 150, false));
        parts.add(new ColossusPart("shoulders", 0, 880, 250, 440, 150, false));
        parts.add(new ColossusPart("neck_low", 0, 1000, 290, 200, 110, false));
        parts.add(new ColossusPart("neck_high", 0, 1110, 310, 190, 150, false));
        parts.add(new ColossusPart("head", 0, 1150, 400, 220, 190, true));
        parts.add(new ColossusPart("snout", 0, 1080, 610, 200, 180, true));
        int[][] tail = {{372, 148, 220, 250}, {273, 232, 200, 208}, {169, 311, 180, 195}, {108, 423, 160, 166}, {84, 555, 150, 141}, {84, 695, 140, 101}, {104, 915, 300, 200}};
        for (int index = 0; index < tail.length; index++) parts.add(new ColossusPart("tail_" + index, 0, tail[index][0], -tail[index][1], tail[index][2], tail[index][3], false));
        // A wing is far wider than it is deep, and a hitbox is square, so each is a row of them along its span.
        int[][] wing = {{130, 735, 480}, {280, 800, 450}, {430, 899, 400}, {580, 899, 400}, {730, 843, 450}, {880, 843, 360}};
        for (int index = 0; index < 2; index++) {
            String name = index == 0 ? "left_" : "right_";
            int side = index == 0 ? 1 : -1;
            parts.add(new ColossusPart(name + "arm", side * 150, 669, 293, 120, 186, false));
            parts.add(new ColossusPart(name + "claw", side * 141, 587, 410, 200, 123, false));
            for (int span = 0; span < wing.length; span++) parts.add(new ColossusPart(name + "wing_" + span, side * wing[span][0], wing[span][1], 205 + span * 9, 160, wing[span][2], false));
            // These three follow the leg; ColossusGaitRules.place works out where they are.
            parts.add(new ColossusPart(name + "thigh", side * LEGS.side(), 300, 0, 200, 300, false));
            parts.add(new ColossusPart(name + "shin", side * LEGS.side(), 40, 0, 150, 380, false));
            parts.add(new ColossusPart(name + "foot", side * LEGS.side(), 0, 0, 290, 76, false));
        }
        return List.copyOf(parts);
    }

    private AgaresRules() {}
}
