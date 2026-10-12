package com.miophas.corruption.core.plant;

import com.miophas.corruption.api.plant.LivingPlantDefinition;
import com.miophas.corruption.api.plant.LivingPlantKind;
import com.miophas.corruption.api.plant.LivingPlantSpace;

/** Species definitions and growth policy, independent of entities and NeoForge events. */
public final class LivingPlantRules {
    private static final LivingPlantDefinition VINE = new LivingPlantDefinition(5, 2, 1, .8F, .2F, 20, true, true, false);
    private static final LivingPlantDefinition GREED = new LivingPlantDefinition(5, 2, 2, 1, 1, 40, true, true, true);

    public static LivingPlantDefinition definition(LivingPlantKind kind) {
        return switch (kind) { case MALICE_VINE -> VINE; case GREED -> GREED; };
    }
    public static int direction(LivingPlantSpace space) {
        boolean above = space.available(0, 1, 0) && space.canOccupy(0, 1, 0);
        boolean below = space.available(0, -1, 0) && space.canOccupy(0, -1, 0);
        return above && !below ? 1 : below && !above ? -1 : 0;
    }
    public static int direction(LivingPlantSpace space, int requested) {
        if (requested == 0) return direction(space);
        return (requested == 1 || requested == -1) && space.available(0, requested, 0)
                && space.canOccupy(0, requested, 0) ? requested : 0;
    }
    public static boolean hasRoom(LivingPlantKind kind, int stage, int direction, LivingPlantSpace space) {
        if (direction != 1 && direction != -1) return false;
        LivingPlantDefinition definition = definition(kind);
        // Vines retain their narrow stem; greed reserves its widening canopy.
        int radius = kind == LivingPlantKind.GREED ? (int) Math.ceil((definition.width(stage) - 1) / 2) : 0;
        for (int y = 1; y <= definition.height(stage); y++)
            for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++)
                if (!space.available(x, y * direction, z) || !space.canOccupy(x, y * direction, z)) return false;
        return true;
    }
    public static boolean canGrow(LivingPlantKind kind, int stage, boolean alive, boolean supported,
            int direction, LivingPlantSpace space) {
        return alive && supported && stage < definition(kind).stages() - 1 && hasRoom(kind, stage + 1, direction, space);
    }
    public static boolean prepareGrowth(LivingPlantKind kind, int stage, int direction, LivingPlantSpace space) {
        if (!hasRoom(kind, stage, direction, space)) return false;
        int radius = kind == LivingPlantKind.GREED ? (int) Math.ceil((definition(kind).width(stage) - 1) / 2) : 0;
        for (int y = 1; y <= definition(kind).height(stage); y++)
            for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++)
                space.clearDecoration(x, y * direction, z);
        return true;
    }
    public static int advanceGrowthTicks(int elapsed, int interval, int step) {
        return (int) Math.min(Math.max(1, interval), Math.max(0L, elapsed) + Math.max(0, step));
    }
    private LivingPlantRules() {}
}
