package com.miophas.corruption.core.energy;

/**
 * The rules of a Corruption Energy tool: something that holds a charge to work with, rather than energy to
 * burn. A battery — the Corruption Candle — gives out what it carries whole, and is gone once it has spent it;
 * a tool is drawn on by what it is used for, keeps whatever is left of its charge between uses, and is filled
 * again at a charger rather than replaced. Its charge never leaves it: nothing a tool does uses the tool up.
 *
 * <p>A tool is filled a step at a time, not all at once, so whoever fills it has a moment to show for each
 * piece of energy that passes: {@link #step} is how much goes in at once.
 *
 * @param capacity the most it holds, in CE
 * @param step how much goes into it at once, in CE
 */
public record EnergyTool(long capacity, long step) {
    /** For an item that is no tool at all. */
    public static final EnergyTool NONE = new EnergyTool(0, 0);

    public EnergyTool {
        capacity = Math.max(0, capacity);
        step = capacity <= 0 ? 0 : Math.clamp(step, 1, capacity);
    }

    /** Whether this names a tool at all. */
    public boolean isTool() { return capacity > 0; }

    /** What it holds of a charge this large, cut to what it can hold. */
    public long held(long energy) { return Math.clamp(energy, 0, capacity); }

    /** How much of this much energy it would take, with what it holds already. */
    public long taken(long energy, long amount) {
        return capacity <= 0 ? 0 : Math.max(0, Math.min(amount, capacity - held(energy)));
    }

    /** Whether it would take any of this much. */
    public boolean takes(long energy, long amount) { return taken(energy, amount) > 0; }
}
