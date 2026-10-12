package com.miophas.corruption.core.corruption;

/** Validates captured attributes without flattening small animals or large bosses to fixed health. */
public record RemnantCombatProfile(double maxHealth, double attackDamage) {
    public static RemnantCombatProfile capture(double health, double damage) {
        return new RemnantCombatProfile(Double.isFinite(health) && health > 0 ? health : RemnantRules.DEFINITION.health(),
                Double.isFinite(damage) && damage >= 0 ? damage : RemnantRules.DEFINITION.attackDamage());
    }
}
