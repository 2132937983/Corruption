package com.miophas.corruption.common.item;

import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import net.minecraft.world.item.Item;

/**
 * The corruption's wand: a casting tool of the mod's own. What it does is
 * {@link ArcaneCastingItem}'s and, under that, core.arcane; this only says how much of its energy it gives away
 * at a time when that energy is poured out.
 */
public final class CorruptionArcaneStickItem extends ArcaneCastingItem {
    public CorruptionArcaneStickItem(Item.Properties properties) { super(properties, CorruptionEnergyApi.kilo(1)); }
}
