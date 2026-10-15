package com.miophas.corruption.core.arcane;

/**
 * Casting a spell: what must be true before it is cast, and what it costs. A wand holds the Corruption Energy
 * to spend; one parchment bearing the spell must be at hand and is used up; the caster's corruption both gates
 * the spell and may be raised by it.
 *
 * <p>The rules are plain Java; who casts, with what, is a {@link Caster}, which the game side implements (see
 * {@link CastingUnit#caster}).
 */
public final class SpellCasting {
    /** How much parchment one cast uses up, as one transcription makes one copy. */
    public static final int PARCHMENT_PER_CAST = 1;

    /** Whether a spell can be cast, and if not, the first thing that stops it. */
    public enum Check { READY, COOLING, NOT_CORRUPTED_ENOUGH, NO_ENERGY, NO_PARCHMENT }

    /**
     * What casting has to hand: the wand's energy, the caster's corruption, the parchment bearing the spell,
     * and whether the spell is still cooling. The game side reads these off the player and the wand.
     */
    public interface Caster {
        /** The Corruption Energy the wand holds, in CE. */
        long energy();
        /** How corrupted the caster is, on the corruption scale. */
        int corruption();
        /** How many parchments bearing this spell are at hand. */
        int parchment();
        /** Whether the spell is still cooling. */
        boolean cooling();
        /** Spends what the spell costs: this much energy, this many parchments, and this much more corruption. */
        void pay(long energy, int parchments, int corruptionGain);
    }

    /** Whether the spell can be cast now. */
    public static Check check(SpellRule rule, Caster caster) {
        if (caster.cooling()) return Check.COOLING;
        if (caster.corruption() < rule.minCorruption()) return Check.NOT_CORRUPTED_ENOUGH;
        if (caster.energy() < rule.energy()) return Check.NO_ENERGY;
        if (caster.parchment() < PARCHMENT_PER_CAST) return Check.NO_PARCHMENT;
        return Check.READY;
    }

    /** Casts the spell if it can be, spending the energy, the parchment and the corruption. Answers the check. */
    public static Check cast(SpellRule rule, Caster caster) {
        Check check = check(rule, caster);
        if (check == Check.READY) caster.pay(rule.energy(), PARCHMENT_PER_CAST, rule.corruptionGain());
        return check;
    }

    private SpellCasting() {}
}
