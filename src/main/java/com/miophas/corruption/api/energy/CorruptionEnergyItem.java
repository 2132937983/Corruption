package com.miophas.corruption.api.energy;

import com.miophas.corruption.core.energy.CarriedEnergy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The base for items that carry Corruption Energy in the corruption:corruption_energy data component, as the
 * Corruption Candle does. How much one can carry, and whether it is used up once empty, is data: the
 * corruption_energy_carrier data map must name the item (see {@link CorruptionEnergyCarrier}). This class
 * gives the energy out, in either of two ways:
 * <ul>
 *   <li>{@link #extractAll all at once}: everything one item carries, in a single call;</li>
 *   <li>{@link #extractStep bit by bit}: {@link #getStep a step} per call, for whoever takes it to call again,
 *   each tick say, until the item is empty. The arcane transmuter burns items down this way.</li>
 * </ul>
 * Both take from the stack they are given. Bit by bit, that must be a single item, since the rest of a stack
 * could not keep their own charge; all at once, a stack of items that are used up works too, the one that
 * gives leaving the stack. An item that is used up is gone the moment it gives the last of its energy.
 *
 * A bar under the item shows how full it is, as a tool's shows its wear.
 */
public class CorruptionEnergyItem extends Item {
    private static final int BAR_COLOUR = 0xC6728C;
    private final long step;

    /** @param step how much it gives at a time, bit by bit, in CE */
    public CorruptionEnergyItem(Properties properties, long step) {
        super(properties);
        if (step <= 0) throw new IllegalArgumentException("An item gives at least one CE a step");
        this.step = step;
    }

    /** Gives a KCE a step. */
    public CorruptionEnergyItem(Properties properties) { this(properties, CarriedEnergy.DEFAULT_STEP); }

    /** What one of these items carries, in CE. */
    public long getEnergy(ItemStack stack) { return CorruptionEnergyCarrier.carried(stack); }
    /** The most one can carry, as the carrier data map says; nothing if the data map does not name it. */
    public long getCapacity(ItemStack stack) {
        CorruptionEnergyCarrier carrier = CorruptionEnergyCarrier.of(stack);
        return carrier == null ? 0 : carrier.capacity();
    }
    /** Whether one is used up once it has given all it carried. */
    public boolean isConsumed(ItemStack stack) {
        CorruptionEnergyCarrier carrier = CorruptionEnergyCarrier.of(stack);
        return carrier != null && carrier.consumed();
    }
    /** How much it gives at a time, bit by bit. */
    public long getStep(ItemStack stack) { return step; }

    /** A new one carrying this much. */
    public ItemStack charged(long energy) { return CorruptionEnergyCarrier.charged(new ItemStack(this), energy); }

    /**
     * All at once: takes everything one item carries, and answers how much that is. Simulating, nothing
     * changes. Taken from a stack of several items that are used up, one leaves the stack; from a stack of
     * several that are not, nothing comes.
     */
    public long extractAll(ItemStack stack, boolean simulate) {
        CarriedEnergy carried = CorruptionEnergyCarrier.carriedEnergy(stack);
        return carried == null ? 0 : carried.extractAll(simulate);
    }

    /**
     * Bit by bit: takes one step of what the item carries, or what is left if that is less, and answers how
     * much. Simulating, nothing changes. Only a single item gives this way.
     */
    public long extractStep(ItemStack stack, boolean simulate) {
        CarriedEnergy carried = CorruptionEnergyCarrier.carriedEnergy(stack);
        return carried == null ? 0 : carried.extractStep(getStep(stack), simulate);
    }

    @Override public boolean isBarVisible(ItemStack stack) {
        long capacity = getCapacity(stack);
        return capacity > 0 && getEnergy(stack) < capacity;
    }
    @Override public int getBarWidth(ItemStack stack) {
        return Math.round(13 * CorruptionEnergyApi.fraction(getEnergy(stack), getCapacity(stack)));
    }
    @Override public int getBarColor(ItemStack stack) { return BAR_COLOUR; }
}
