package com.miophas.corruption.core.arcane;

import java.util.List;

/**
 * What a spell costs and what it does, as its data file says: the Corruption Energy it spends, the ticks before
 * it can be cast again, how corrupted the caster must be, how much more corrupted casting leaves them, and the
 * actions it carries out, in order.
 *
 * @param energy the Corruption Energy it spends, in CE; never negative
 * @param cooldown the ticks before it can be cast again; never negative
 * @param minCorruption how corrupted the caster must be, 0 for no such gate
 * @param corruptionGain how much more corrupted casting leaves the caster, 0 for none
 * @param actions what it does, in order
 */
public record SpellRule(long energy, int cooldown, int minCorruption, int corruptionGain, List<SpellAction> actions) {
    public SpellRule {
        energy = Math.max(0, energy);
        cooldown = Math.max(0, cooldown);
        minCorruption = Math.max(0, minCorruption);
        corruptionGain = Math.max(0, corruptionGain);
        actions = List.copyOf(actions);
    }

    /** A spell that does nothing and costs nothing, for an id that names no rule. */
    public static final SpellRule NONE = new SpellRule(0, 0, 0, 0, List.of());

    /** Whether casting raises the caster's corruption at all. */
    public boolean corrupts() { return corruptionGain > 0; }
}
