package com.miophas.corruption.api.plant;

/** Coordinates relative to the anchor. Implementations must never load chunks. */
public interface LivingPlantSpace {
    boolean available(int x, int y, int z);
    boolean canOccupy(int x, int y, int z);
    void clearDecoration(int x, int y, int z);
}
