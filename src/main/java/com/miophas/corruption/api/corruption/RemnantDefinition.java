package com.miophas.corruption.api.corruption;

/** Shared fallback attributes and AI parameters for the dynamic remnant species. */
public record RemnantDefinition(double health, double attackDamage, double movementSpeed, double followRange,
        float width, float height, double attackSpeed, double strollSpeed, double attractionSpeed,
        int rangedInterval, float rangedDistance) {}
