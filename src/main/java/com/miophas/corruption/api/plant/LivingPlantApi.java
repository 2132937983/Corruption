package com.miophas.corruption.api.plant;

import com.miophas.corruption.core.plant.LivingPlantRules;

/** Bridge to the core living-plant definitions and growth policy. */
public final class LivingPlantApi {
    public static LivingPlantDefinition definition(LivingPlantKind kind) { return LivingPlantRules.definition(kind); }
    public static int direction(LivingPlantSpace space) { return LivingPlantRules.direction(space); }
    public static int direction(LivingPlantSpace space, int requested) { return LivingPlantRules.direction(space, requested); }
    public static boolean hasRoom(LivingPlantKind kind, int stage, int direction, LivingPlantSpace space) {
        return LivingPlantRules.hasRoom(kind, stage, direction, space);
    }
    public static boolean canGrow(LivingPlantKind kind, int stage, boolean alive, boolean supported, int direction, LivingPlantSpace space) {
        return LivingPlantRules.canGrow(kind, stage, alive, supported, direction, space);
    }
    public static boolean prepareGrowth(LivingPlantKind kind, int stage, int direction, LivingPlantSpace space) {
        return LivingPlantRules.prepareGrowth(kind, stage, direction, space);
    }
    public static int advanceGrowthTicks(int elapsed, int interval, int step) {
        return LivingPlantRules.advanceGrowthTicks(elapsed, interval, step);
    }
    public static LivingPlantDefinition definition(net.minecraft.resources.ResourceLocation id) { return LivingPlantRegistry.species(id).definition(); }
    public static boolean hasRoom(net.minecraft.resources.ResourceLocation id, int stage, int direction, LivingPlantSpace space) {
        var species = LivingPlantRegistry.species(id);
        return LivingPlantRules.hasRoom(species.definition(), species.reserveCanopy(), stage, direction, space);
    }
    public static boolean canGrow(net.minecraft.resources.ResourceLocation id, int stage, boolean alive, boolean supported, int direction, LivingPlantSpace space) {
        var species = LivingPlantRegistry.species(id);
        return LivingPlantRules.canGrow(species.definition(), species.reserveCanopy(), stage, alive, supported, direction, space);
    }
    public static boolean prepareGrowth(net.minecraft.resources.ResourceLocation id, int stage, int direction, LivingPlantSpace space) {
        var species = LivingPlantRegistry.species(id);
        return LivingPlantRules.prepareGrowth(species.definition(), species.reserveCanopy(), stage, direction, space);
    }
    private LivingPlantApi() {}
}
