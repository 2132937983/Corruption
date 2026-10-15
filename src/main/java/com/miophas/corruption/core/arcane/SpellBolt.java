package com.miophas.corruption.core.arcane;

/**
 * How a spell bolt flies, which is the same for every bolt: whether it is pulled down, how much of its speed it
 * keeps each tick, how many ticks it lasts before it is spent, and how near it must come to strike. A spell's
 * {@link SpellAction.LaunchBolt} says how fast it goes and what it does where it strikes; this says how it gets
 * there.
 *
 * @param gravity blocks a tick it falls by, 0 for a bolt that keeps to its line
 * @param drag the share of its speed it keeps each tick, 1 for none lost
 * @param lifetime how many ticks it flies before it is spent
 * @param hitRadius how near it must come to a block or creature to strike it
 */
public record SpellBolt(double gravity, double drag, int lifetime, double hitRadius) {
    public static final SpellBolt DEFAULT = new SpellBolt(0.0, 0.99, 100, 0.6);
}
