package com.miophas.corruption.core.arcane;

/**
 * A casting unit: what a wand is, apart from the game. It holds a {@link Grimoire} — the spells it has been
 * taught and which of them is chosen — and the Corruption Energy it has to spend, and it can be taught a spell,
 * told which to use, and asked whether its spell can be cast now and what that costs.
 *
 * <p>The same unit backs the mod's wand and any add-on's own casting tool: the mechanism is here, and the game
 * side only reads it and writes it back (see {@link #caster} and SpellCasting.Caster).
 */
public final class CastingUnit {
    public static final CastingUnit EMPTY = new CastingUnit(Grimoire.EMPTY, 0);

    private final Grimoire grimoire;
    private final long energy;

    public CastingUnit(Grimoire grimoire, long energy) {
        this.grimoire = grimoire == null ? Grimoire.EMPTY : grimoire;
        this.energy = Math.max(0, energy);
    }

    public Grimoire grimoire() { return grimoire; }
    public long energy() { return energy; }
    /** The spell casting would use, or null while none is written in. */
    public String current() { return grimoire.current(); }
    public boolean isEmpty() { return grimoire.isEmpty(); }

    /** Whether a spell by this id can be written in, and if not, why not. */
    public Grimoire.Inscribe check(String spell) { return grimoire.check(spell); }

    /** The same unit with the spell written in, if it can be; teaching wears nothing out. */
    public CastingUnit inscribing(String spell) { return new CastingUnit(grimoire.inscribed(spell), energy); }

    /** The same unit with another spell chosen, or the next one round; choosing one it lacks changes nothing. */
    public CastingUnit selecting(int index) { return new CastingUnit(grimoire.selecting(index), energy); }
    public CastingUnit cycling() { return new CastingUnit(grimoire.cycled(), energy); }

    /** The same unit holding this much energy; it holds never less than none. */
    public CastingUnit withEnergy(long ce) { return new CastingUnit(grimoire, ce); }
    /** The same unit with this much spent out of it. */
    public CastingUnit spending(long ce) { return withEnergy(energy - ce); }

    /**
     * The caster side of this unit, for {@link SpellCasting}: what it holds, what is to hand, and where what a
     * cast costs goes. The game side reads the corruption and the parchment off the player, and writes the
     * payment back to the wand and the player.
     */
    public SpellCasting.Caster caster(int corruption, int parchments, boolean cooling, Payment payment) {
        return new SpellCasting.Caster() {
            @Override public long energy() { return energy; }
            @Override public int corruption() { return corruption; }
            @Override public int parchment() { return parchments; }
            @Override public boolean cooling() { return cooling; }
            @Override public void pay(long spent, int usedUp, int corruptionGain) { payment.pay(spent, usedUp, corruptionGain); }
        };
    }

    /** Where what a cast costs goes: the energy out of the wand, the parchment used up, the corruption gained. */
    @FunctionalInterface
    public interface Payment {
        void pay(long energy, int parchments, int corruptionGain);
    }
}
