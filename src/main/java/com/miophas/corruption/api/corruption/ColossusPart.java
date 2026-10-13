package com.miophas.corruption.api.corruption;

/**
 * One of a colossus's hitboxes, in model pixels: the middle of its base (sideways positive to its left,
 * height above the ground, forward the way it faces) and its size. Hits on a weak part hurt more.
 * Parts named left_ or right_ and then thigh, shin, hoof or foot follow that leg as it walks.
 */
public record ColossusPart(String name, float side, float bottom, float forward, float width, float height, boolean weak) {}
