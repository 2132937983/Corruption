package com.miophas.corruption.api.plant;

/** Immutable species data. Registration, configuration and rendering remain in common. */
public record LivingPlantDefinition(int stages, int initialHeight, int heightPerStage,
        float initialWidth, float widthPerStage, double health, boolean causesBlindness,
        boolean attractsRemnants, boolean attacksEnemies) {
    public LivingPlantDefinition {
        if (stages < 1 || initialHeight < 1 || heightPerStage < 0 || initialWidth <= 0 || widthPerStage < 0
                || !Float.isFinite(initialWidth) || !Float.isFinite(widthPerStage) || !Double.isFinite(health) || health <= 0)
            throw new IllegalArgumentException("Invalid living plant dimensions or health");
    }
    public int clampStage(int stage) { return Math.clamp(stage, 0, stages - 1); }
    public int height(int stage) { return initialHeight + heightPerStage * clampStage(stage); }
    public float width(int stage) { return initialWidth + widthPerStage * clampStage(stage); }
}
