package com.miophas.corruption.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

/**
 * Corrupted lightning: a purple bolt that strikes an altar's offering as a rite completes. It looks and sounds
 * like lightning, but sets nothing alight and hurts nothing. Drawn by CorruptLightningRenderer.
 */
public final class CorruptLightning extends LightningBolt {
    public CorruptLightning(EntityType<? extends CorruptLightning> type, Level level) {
        super(type, level);
        setVisualOnly(true);
    }
}
