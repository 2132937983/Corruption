package com.miophas.corruption.common.item;

import com.miophas.corruption.api.energy.CorruptionEnergyItem;

/**
 * The mod's plain item that carries Corruption Energy: nothing beyond what every {@link CorruptionEnergyItem}
 * does, giving its energy out all at once or bit by bit, a given step at a time. How much one carries, and
 * whether it is used up once empty, is data (data_maps/item/corruption_energy_carrier.json). The Corruption
 * Candle is one, burning down a KCE a step.
 */
public class BasicCorruptionEnergyItem extends CorruptionEnergyItem {
    /** @param step how much it gives at a time, bit by bit, in CE */
    public BasicCorruptionEnergyItem(Properties properties, long step) { super(properties, step); }
}
