package com.miophas.corruption.api.corruption;

/** Groups corrupted mob definitions by the dedicated model set that renders them. */
public enum CorruptedMobFamily {
    /** Hand-built models with their own renderer class each. */
    DEDICATED,
    LIVESTOCK, VILLAGE, NETHER, WILDLIFE
}
