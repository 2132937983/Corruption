package com.miophas.corruption.api.corruption;

/**
 * The flowing corruption fluid and the curse it lays on whoever enters it.
 *
 * @param id                    registry path shared by the source fluid, its block and its fluid type
 * @param curseTicks            curse duration, or {@link CorruptionCurseApi#ENDLESS}
 * @param curable               whether milk and other cures can lift the fluid's curse
 * @param playersOnly           when false, every living entity except remnants is cursed
 * @param tickDelay             ticks between flow updates; water is 5, lava 30
 * @param slopeFindDistance     how far it looks for a drop to flow towards; water is 4
 * @param levelDecreasePerBlock how quickly it thins out as it spreads; water is 1
 */
public record FlowingCorruptionDefinition(String id, int curseTicks, int curseAmplifier, boolean curable, boolean playersOnly,
        int tickDelay, int slopeFindDistance, int levelDecreasePerBlock, int density, int viscosity) {}
