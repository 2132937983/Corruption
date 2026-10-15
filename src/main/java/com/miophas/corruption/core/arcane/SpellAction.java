package com.miophas.corruption.core.arcane;

import java.util.List;

/**
 * One thing a spell does, when it is cast by hand or when a spell bolt strikes. A spell is an ordered list of
 * these (see {@link SpellRule}), carried out in order; the same handful of kinds serves a cast from the wand and
 * the payload of a bolt, so a spell may launch a bolt that carries more of the same: which effects a spell has,
 * and what a bolt does where it lands, is entirely data.
 *
 * <p>Nothing here touches the game: what an effect or a creature is called is a plain id, and the numbers are
 * what the effect is worth. The game side turns each kind into its effect (see the mod's common.arcane).
 */
public sealed interface SpellAction {
    /** Who an effect falls on: the caster alone, or everything near the caster. */
    enum Target { SELF, NEARBY }

    /** Gives a mob effect to the caster alone ({@link Target#SELF}) or to everything within {@code radius} blocks. */
    record ApplyEffect(String effect, Target target, double radius, int duration, int amplifier) implements SpellAction {}

    /** Hurts everything within {@code radius} blocks of the caster for this much. */
    record Damage(double amount, double radius) implements SpellAction {}

    /** Corrupts the blocks around the caster within {@code radius}, each of them this much of the time. */
    record Corrupt(double radius, double chance) implements SpellAction {}

    /** Chills everything within {@code radius} blocks of the caster, for this long. */
    record Freeze(double radius, int ticks) implements SpellAction {}

    /** Brings forth this many of a creature, scattered around the caster. */
    record Summon(String entity, int count) implements SpellAction {}

    /**
     * Flies a spell bolt from the caster's eyes along their gaze: an entity, "corruption:spell_bolt" unless
     * another is named, that carries {@code onHit} and does it where it strikes. {@code speed} is in blocks a
     * tick, {@code inaccuracy} the spread in blocks, 0 for a true shot.
     */
    record LaunchBolt(String entity, double speed, float inaccuracy, List<SpellAction> onHit) implements SpellAction {}
}
