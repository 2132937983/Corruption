package com.miophas.corruption.api.corruption;

/**
 * One leg's pose. The angles are degrees, positive swinging forward: the thigh from hanging straight down,
 * the shin, cannon bone and hoof (or foot) each from the bone above. The rest places the middle of the sole
 * in model pixels: sideways from the colossus's centre (positive to its left), ahead of its hips and above
 * the ground.
 */
public record ColossusLeg(float thigh, float shin, float cannon, float hoof, float side, float forward, float lift) {}
