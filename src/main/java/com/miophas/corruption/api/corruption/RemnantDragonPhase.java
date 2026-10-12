package com.miophas.corruption.api.corruption;

/** Ordinals are persisted and synchronized; append new phases rather than reordering. */
public enum RemnantDragonPhase {
    HOLDING, STRAFE, CHARGE, LANDING, SCANNING, ROARING, BREATH, TAKEOFF, HOVER, DYING;
    public boolean sitting() { return this == SCANNING || this == ROARING || this == BREATH; }
    public static RemnantDragonPhase fromId(int id) {
        return id >= 0 && id < values().length ? values()[id] : HOLDING;
    }
}
