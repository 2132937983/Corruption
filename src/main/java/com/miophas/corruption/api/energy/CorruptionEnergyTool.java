package com.miophas.corruption.api.energy;

import com.miophas.corruption.core.energy.CarriedEnergy;
import com.miophas.corruption.core.energy.EnergyTool;
import net.minecraft.world.item.ItemStack;

/**
 * An item that is a Corruption Energy tool: a wand, a lamp, anything that holds a charge to work with rather
 * than energy to burn. It is <b>not</b> an item the corruption_energy_carrier data map names — that data map
 * describes batteries, such as the Corruption Candle, which pour out what they carry and are gone once spent
 * (see {@link CorruptionEnergyCarrier}). A tool says for itself, in code, how much it holds and how much of it
 * goes in at a time, and it is charged and drawn on instead of being used up.
 *
 * <p>What it holds now is written in the same corruption:corruption_energy component a battery's is, so a
 * recipe may hand out a charged tool the same way it hands out a charged candle.
 */
public interface CorruptionEnergyTool {
    /** The rules of this tool: how much it holds, and how much goes in at a time (see {@link EnergyTool}). */
    EnergyTool tool();

    /** The tool this stack is, or {@link EnergyTool#NONE} for an item that is none. */
    static EnergyTool of(ItemStack stack) {
        return stack.getItem() instanceof CorruptionEnergyTool tool ? tool.tool() : EnergyTool.NONE;
    }

    /** What one of these holds now, in CE; nothing for an item that is no tool. */
    static long held(ItemStack stack) {
        EnergyTool tool = of(stack);
        return tool.isTool() ? tool.held(stack.getOrDefault(CorruptionEnergyCarrier.ENERGY.get(), 0L)) : 0;
    }

    /**
     * The store this stack offers, or null for an item that is no tool. Energy goes into a stack of one, and never
     * comes out through it: a tool's charge is drawn on only by what the tool is used for (a wand's cast spends
     * it directly), so it is not poured into a machine, burnt as fuel, or emptied onto a charger it was set on.
     */
    static CorruptionEnergyStorage storage(ItemStack stack) {
        EnergyTool tool = of(stack);
        if (!tool.isTool()) return null;
        CarriedEnergy carried = new CarriedEnergy(tool.capacity(), false, stack.getCount(),
                () -> stack.getOrDefault(CorruptionEnergyCarrier.ENERGY.get(), 0L),
                energy -> stack.set(CorruptionEnergyCarrier.ENERGY.get(), energy), () -> {});
        return new CorruptionEnergyStorage() {
            @Override public long getEnergy() { return carried.getEnergy(); }
            @Override public long getCapacity() { return carried.getCapacity(); }
            @Override public long insert(long amount, boolean simulate) { return carried.insert(amount, simulate); }
            @Override public long extract(long amount, boolean simulate) { return 0; }
            @Override public boolean canInsert() { return carried.canInsert(); }
            @Override public boolean canExtract() { return false; }
        };
    }

    /** A copy of the stack with each item holding this much. */
    static ItemStack charged(ItemStack stack, long energy) {
        ItemStack copy = stack.copy();
        copy.set(CorruptionEnergyCarrier.ENERGY.get(), Math.max(0, energy));
        return copy;
    }
}
