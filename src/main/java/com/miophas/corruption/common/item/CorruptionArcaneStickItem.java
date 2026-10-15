package com.miophas.corruption.common.item;

import com.miophas.corruption.api.arcane.ArcaneCastingItem;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.core.arcane.Grimoire;
import com.miophas.corruption.core.energy.EnergyTool;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * The corruption's wands: casting tools of the mod's own, one to a {@link Tier}. What a wand does is
 * {@link ArcaneCastingItem}'s and, under that, core.arcane; this only says what each tier is, as an add-on's wand
 * would: a {@link com.miophas.corruption.api.energy.CorruptionEnergyTool} holding so much, filled so much at a
 * time, with room for so many spells — not a battery, so its charge is cast with and filled again, never poured out
 * and consumed. The higher the tier, the more it holds and the more spells it carries; a spell costing more than a
 * wand can hold is beyond it.
 */
public final class CorruptionArcaneStickItem extends ArcaneCastingItem {
    /** The wands, lowest first: what each holds, how much goes in at a time, and how many spells it has room for. */
    public enum Tier {
        NOVICE(new EnergyTool(CorruptionEnergyApi.kilo(10), CorruptionEnergyApi.kilo(.5)), 2, Rarity.COMMON),
        BASIC(ArcaneCastingItem.DEFAULT_TOOL, Grimoire.CAPACITY, Rarity.COMMON),
        INTERMEDIATE(new EnergyTool(CorruptionEnergyApi.kilo(50), CorruptionEnergyApi.kilo(2.5)), 5, Rarity.UNCOMMON),
        ADVANCED(new EnergyTool(CorruptionEnergyApi.kilo(100), CorruptionEnergyApi.kilo(5)), 8, Rarity.RARE);

        private final EnergyTool tool;
        private final int spells;
        private final Rarity rarity;

        Tier(EnergyTool tool, int spells, Rarity rarity) {
            this.tool = tool;
            this.spells = spells;
            this.rarity = rarity;
        }

        public EnergyTool tool() { return tool; }
        public int spells() { return spells; }
        public Rarity rarity() { return rarity; }
    }

    private final Tier tier;

    public CorruptionArcaneStickItem(Item.Properties properties, Tier tier) {
        super(properties.stacksTo(1).rarity(tier.rarity()), tier.tool(), tier.spells());
        this.tier = tier;
    }

    public Tier tier() { return tier; }
}
