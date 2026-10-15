package com.miophas.corruption.api.energy;

import net.minecraft.world.item.ItemStack;

/**
 * A block that keeps one item and charges it out of the Corruption Energy it holds: what the arcane charger is.
 * The store it fills that item from is the one it offers to everyone else through
 * {@link CorruptionEnergyCapabilities#BLOCK}, so it may be poured into by hand or filled by an add-on's cables,
 * and what it holds is given to the item a step at a time (see
 * {@link com.miophas.corruption.core.energy.EnergyTool}).
 *
 * <p>The block asks for this interface alone, never for the mod's own charger, so an add-on's machine that keeps
 * an item to charge works the same way.
 */
public interface EnergyCharger {
    /** What lies there to be charged, or nothing. */
    ItemStack charge();

    /** Sets this on it to be charged, one of it. */
    void put(ItemStack stack);

    /** Takes what lies there off, leaving nothing. */
    ItemStack take();

    /** Whether anything lies there. */
    boolean isEmpty();

    /**
     * How many sparks the item is owed for the energy it has been given since this was last asked — one for each
     * step of a charge, so that a charge is something to watch rather than something that has already happened.
     * A machine that shows its charging some other way need not answer, and owes nothing; this is asked while
     * drawing, so only ever on the client.
     */
    default int sparksOwed() { return 0; }
}
