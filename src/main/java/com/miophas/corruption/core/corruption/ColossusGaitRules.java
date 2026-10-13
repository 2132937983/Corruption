package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLeg;
import com.miophas.corruption.api.corruption.ColossusLegs;
import com.miophas.corruption.api.corruption.ColossusPart;

/**
 * How a colossus walks, whatever its legs measure. The same result places its leg hitboxes on the server
 * and poses its legs on the client, so a foot is drawn where it does its damage. Lengths are model pixels;
 * sideways is positive to its left and forward is the way it faces.
 */
public final class ColossusGaitRules {
    /** The ankle's place below the hip joint, forward then down, with the resting bend scaled by this much. */
    static double[] ankle(ColossusLegs legs, double bend) {
        double thigh = Math.toRadians(legs.restThigh() * bend), shin = thigh + Math.toRadians(legs.restShin() * bend), cannon = shin + Math.toRadians(legs.restCannon() * bend);
        return new double[]{legs.thigh() * Math.sin(thigh) + legs.shin() * Math.sin(shin) + legs.cannon() * Math.sin(cannon),
                legs.thigh() * Math.cos(thigh) + legs.shin() * Math.cos(shin) + legs.cannon() * Math.cos(cannon)};
    }

    /** How far ahead of the hip joint the ankle rests. */
    public static double restForward(ColossusLegs legs) { return ankle(legs, 1)[0]; }
    /** The height of the hips when it stands at rest with its soles on the ground. */
    public static double restHip(ColossusLegs legs) { return ankle(legs, 1)[1] * Math.cos(Math.toRadians(legs.splay())) + legs.foot(); }

    /** How far through its walk cycle this many pixels carry it. */
    public static double cycles(ColossusLegs legs, double pixels) { return pixels / legs.cycle(); }
    /** Turning on the spot makes it step too: its feet travel an arc around its centre. */
    public static double cyclesForTurn(ColossusLegs legs, double degrees) { return cycles(legs, Math.abs(Math.toRadians(degrees)) * legs.side()); }

    /** Which feet came down while the walk moved between these two points of the cycle: 1 left, 2 right. */
    public static int footfalls(double before, double after) {
        return (Math.floor(after) > Math.floor(before) ? 1 : 0) | (Math.floor(after + .5) > Math.floor(before + .5) ? 2 : 0);
    }

    /**
     * Bends one leg so its foot stands flat with the ankle this far ahead of the hip joint and the sole this
     * far above the ground. The leg folds or straightens its resting zigzag to change length, then swings
     * from the hip. A place it cannot reach leaves it stretched towards it.
     */
    public static ColossusLeg reach(ColossusLegs legs, boolean left, double forward, double hip, double lift) {
        double splay = Math.toRadians(legs.splay());
        double drop = (hip - lift - legs.foot()) / Math.cos(splay), wanted = Math.hypot(forward, drop), straight = 0, folded = legs.maxBend();
        for (int step = 0; step < 28; step++) {
            double bend = (straight + folded) / 2;
            double[] at = ankle(legs, bend);
            if (Math.hypot(at[0], at[1]) > wanted) straight = bend; else folded = bend;
        }
        double bend = (straight + folded) / 2;
        double[] at = ankle(legs, bend);
        double thigh = legs.restThigh() * bend + Math.toDegrees(Math.atan2(forward, drop) - Math.atan2(at[0], at[1]));
        double shin = legs.restShin() * bend, cannon = legs.restCannon() * bend;
        return new ColossusLeg((float) thigh, (float) shin, (float) cannon, (float) -(thigh + shin + cannon),
                (float) ((left ? 1 : -1) * (legs.side() + drop * Math.sin(splay))), (float) (forward + legs.footCentre()), (float) lift);
    }

    /**
     * The legs at one moment. A foot on the ground moves back exactly as fast as the body moves forward, so it
     * stays where it was planted; the other swings ahead in an arc.
     *
     * @param cycle how far it has walked, in cycles
     * @param walk 0 standing to 1 walking, so it can ease in and out of its stride
     * @param raiseLeft how high the left foot is held for a stomp, 0 to 1, and raiseRight the right
     * @param crouch 0 standing to 1 sunk onto its haunches
     */
    public static ColossusGait gait(ColossusLegs legs, double cycle, float walk, double raiseLeft, double raiseRight, double crouch) {
        double phase = cycle - Math.floor(cycle), rest = restForward(legs);
        double hip = restHip(legs) - walk * legs.bob() * (.5 + .5 * Math.cos(4 * Math.PI * (phase - .03)));
        hip += (legs.collapsedHip() - hip) * Math.clamp(crouch, 0, 1);
        ColossusLeg[] pair = new ColossusLeg[2];
        for (int index = 0; index < 2; index++) {
            boolean left = index == 0;
            double own = left ? phase : phase + .5, forward, lift = 0, raise = left ? raiseLeft : raiseRight;
            own -= Math.floor(own);
            if (own < legs.stance()) forward = legs.step() * (1 - 2 * own / legs.stance());
            else {
                double swing = (own - legs.stance()) / (1 - legs.stance());
                forward = legs.step() * (2 * swing * swing * (3 - 2 * swing) - 1);
                lift = legs.swingLift() * Math.sin(Math.PI * swing);
            }
            forward *= walk; lift *= walk;
            if (raise > 0) { forward += legs.stompReach() * raise; lift = Math.max(lift, legs.stompLift() * raise); }
            pair[index] = reach(legs, left, rest + forward, hip, lift);
        }
        return new ColossusGait(pair[0], pair[1], (float) hip);
    }

    /** A joint of this leg, 1 knee to 3 ankle: sideways, height and forward. */
    private static double[] joint(ColossusLegs legs, ColossusLeg leg, boolean left, double hip, int joint) {
        double angle = 0, forward = 0, drop = 0, splay = Math.toRadians(legs.splay());
        double[] bends = {leg.thigh(), leg.shin(), leg.cannon()};
        int[] lengths = {legs.thigh(), legs.shin(), legs.cannon()};
        for (int bone = 0; bone < joint; bone++) {
            angle += Math.toRadians(bends[bone]);
            forward += lengths[bone] * Math.sin(angle); drop += lengths[bone] * Math.cos(angle);
        }
        return new double[]{(left ? 1 : -1) * (legs.side() + drop * Math.sin(splay)), hip - drop * Math.cos(splay), forward};
    }

    /** Where a part is for these legs: the middle of its base, sideways, height and forward. */
    public static float[] place(ColossusLegs legs, ColossusPart part, ColossusGait gait) {
        String name = part.name();
        boolean left = name.startsWith("left_");
        if (left || name.startsWith("right_")) {
            ColossusLeg leg = gait.leg(left);
            if (name.endsWith("_hoof") || name.endsWith("_foot")) return new float[]{leg.side(), leg.lift(), leg.forward()};
            boolean thigh = name.endsWith("_thigh");
            if (thigh || name.endsWith("_shin")) {
                double[] upper = thigh ? new double[]{(left ? 1 : -1) * legs.side(), gait.hip(), 0} : joint(legs, leg, left, gait.hip(), 1);
                double[] lower = joint(legs, leg, left, gait.hip(), thigh ? 1 : 3);
                return new float[]{(float) ((upper[0] + lower[0]) / 2), (float) ((upper[1] + lower[1]) / 2 - part.height() / 2), (float) ((upper[2] + lower[2]) / 2)};
            }
        }
        return new float[]{part.side(), part.bottom() + gait.hip() - (float) restHip(legs), part.forward()};
    }

    private ColossusGaitRules() {}
}
