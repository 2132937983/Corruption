package com.miophas.corruption.api.corruption;

/**
 * The legs of a colossus and how it walks on them, in model pixels and degrees. A leg is three bones in a
 * zigzag (thigh, shin, cannon) and a foot that stays level; it changes length by folding or straightening
 * that zigzag.
 *
 * @param foot the height of the ankle above the sole
 * @param side how far each hip joint is from the middle
 * @param footCentre how far the middle of the sole is ahead of the ankle
 * @param restThigh the thigh's forward swing at rest, and likewise the shin's and cannon's bend from the bone above
 * @param splay how far each leg leans outward, in degrees
 * @param maxBend how many times its resting bend the zigzag may fold to
 * @param cycle the ground one full cycle, a left step and a right step, covers
 * @param stance the share of a cycle each foot spends on the ground; above a half, so both are down between steps
 * @param swingLift how high a walking foot is carried
 * @param bob how far the hips dip as the weight changes feet
 * @param stompLift how high a stomping foot is raised, and stompReach how far forward it is carried
 * @param collapsedHip the height of the hips when it has sunk onto its haunches
 */
public record ColossusLegs(int thigh, int shin, int cannon, int foot, int side, int footCentre, double restThigh, double restShin, double restCannon,
                           double splay, double maxBend, int cycle, double stance, double swingLift, double bob, double stompLift, double stompReach, double collapsedHip) {
    /** How far ahead of and behind its resting place a walking foot reaches. */
    public double step() { return stance * cycle / 2; }
}
