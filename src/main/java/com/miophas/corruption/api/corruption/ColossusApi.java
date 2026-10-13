package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.ColossusGaitRules;

/** The walk every colossus shares, for whatever legs it is given. Lengths are model pixels. */
public final class ColossusApi {
    public static double cycles(ColossusLegs legs, double pixels) { return ColossusGaitRules.cycles(legs, pixels); }
    public static double cyclesForTurn(ColossusLegs legs, double degrees) { return ColossusGaitRules.cyclesForTurn(legs, degrees); }
    /** Which feet came down between two points of the walk cycle: 1 left, 2 right. */
    public static int footfalls(double before, double after) { return ColossusGaitRules.footfalls(before, after); }
    public static float restHip(ColossusLegs legs) { return (float) ColossusGaitRules.restHip(legs); }
    /** Where a part is for these legs: the middle of its base, sideways, height and forward. */
    public static float[] place(ColossusLegs legs, ColossusPart part, ColossusGait gait) { return ColossusGaitRules.place(legs, part, gait); }

    private ColossusApi() {}
}
