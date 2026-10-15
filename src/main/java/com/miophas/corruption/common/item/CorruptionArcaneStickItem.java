package com.miophas.corruption.common.item;

import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import net.minecraft.world.item.Item;

/**
 * The corruption's wand: a casting tool of the mod's own. What it does is
 * {@link ArcaneCastingItem}'s and, under that, core.arcane; this only says what it is as a
 * {@link com.miophas.corruption.api.energy.CorruptionEnergyTool}: a tool of twenty KCE, filled a KCE at a time,
 * with room for three spells — not a battery, so its charge is cast with and filled again, never poured out and
 * consumed.
 */
public final class CorruptionArcaneStickItem extends ArcaneCastingItem {
    public CorruptionArcaneStickItem(Item.Properties properties) { super(properties); }
}
