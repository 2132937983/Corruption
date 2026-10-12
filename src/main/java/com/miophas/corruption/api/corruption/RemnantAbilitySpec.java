package com.miophas.corruption.api.corruption;

/** Reuse time is measured from the beginning of a cast. */
public record RemnantAbilitySpec(int windupTicks, int durationTicks, int reuseTicks,
        double horizontalRange, double verticalRange) {}
