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
     * The store this stack offers, or null for an item that is no tool. Energy moves in and out of a stack of
     * one; unlike a battery's, a tool's store never takes the item away once it is empty.
     */
    static CorruptionEnergyStorage storage(ItemStack stack) {
        EnergyTool tool = of(stack);
        return !tool.isTool() ? null : new CarriedEnergy(tool.capacity(), false, stack.getCount(),
                () -> stack.getOrDefault(CorruptionEnergyCarrier.ENERGY.get(), 0L),
                energy -> stack.set(CorruptionEnergyCarrier.ENERGY.get(), energy), () -> {});
    }

    /** A copy of the stack with each item holding this much. */
    static ItemStack charged(ItemStack stack, long energy) {
        ItemStack copy = stack.copy();
        copy.set(CorruptionEnergyCarrier.ENERGY.get(), Math.max(0, energy));
        return copy;
    }
}
