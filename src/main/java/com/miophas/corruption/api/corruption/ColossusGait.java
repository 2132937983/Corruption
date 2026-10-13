package com.miophas.corruption.api.corruption;

/** Both legs and the height of the hips above the ground, in model pixels, at one moment. */
public record ColossusGait(ColossusLeg left, ColossusLeg right, float hip) {
    public ColossusLeg leg(boolean left) { return left ? this.left : right; }
}
