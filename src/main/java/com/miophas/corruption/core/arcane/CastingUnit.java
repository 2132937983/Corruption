package com.miophas.corruption.core.arcane;

/**
 * A casting unit: what a wand is, apart from the game. It holds a {@link Grimoire} — the spells it has been
 * taught and which of them is chosen — with room for so many of them, and the Corruption Energy it has to
 * spend; it can be taught a spell, told which to use, and asked whether its spell can be cast now and what that
 * costs. Units differ in their room: the mod's wand has {@link Grimoire#CAPACITY}, another tool may have more.
 *
 * <p>The same unit backs the mod's wand and any add-on's own casting tool: the mechanism is here, and the game
 * side only reads it and writes it back (see {@link #caster} and SpellCasting.Caster).
 */
public final class CastingUnit {
    public static final CastingUnit EMPTY = new CastingUnit(Grimoire.EMPTY, 0);

    private final Grimoire grimoire;
    private final long energy;
    private final int capacity;

    /** A unit with room for this many spells; never less than one. */
    public CastingUnit(Grimoire grimoire, long energy, int capacity) {
        this.grimoire = grimoire == null ? Grimoire.EMPTY : grimoire;
        this.energy = Math.max(0, energy);
        this.capacity = Math.max(1, capacity);
    }

    /** A unit with the mod's own wand's room, {@link Grimoire#CAPACITY}. */
    public CastingUnit(Grimoire grimoire, long energy) { this(grimoire, energy, Grimoire.CAPACITY); }

    public Grimoire grimoire() { return grimoire; }
    public long energy() { return energy; }
    /** How many spells it has room for. */
    public int capacity() { return capacity; }
    /** The spell casting would use, or null while none is written in. */
    public String current() { return grimoire.current(); }
    public boolean isEmpty() { return grimoire.isEmpty(); }

    /** Whether a spell by this id can be added after its last, and if not, why not. */
    public Grimoire.Inscribe check(String spell) { return grimoire.check(spell, capacity); }
    /** Whether a spell by this id can be written into this slot: the next free one, or one written over. */
    public Grimoire.Inscribe checkAt(int slot, String spell) { return grimoire.checkAt(slot, spell, capacity); }

    /** The same unit with the spell added after its last, if it can be; teaching wears nothing out. */
    public CastingUnit inscribing(String spell) { return with(grimoire.inscribed(spell, capacity)); }
    /** The same unit with the spell written into this slot, if it can be. */
    public CastingUnit writingAt(int slot, String spell) { return with(grimoire.writtenAt(slot, spell, capacity)); }

    /** The same unit with another spell chosen, or the next one round; choosing one it lacks changes nothing. */
    public CastingUnit selecting(int index) { return with(grimoire.selecting(index)); }
    public CastingUnit cycling() { return with(grimoire.cycled()); }

    /** The same unit holding this much energy; it holds never less than none. */
    public CastingUnit withEnergy(long ce) { return new CastingUnit(grimoire, ce, capacity); }
    private CastingUnit with(Grimoire taught) { return new CastingUnit(taught, energy, capacity); }
    /** The same unit with this much spent out of it. */
    public CastingUnit spending(long ce) { return withEnergy(energy - ce); }

    /**
     * What is to hand when a cast is tried, apart from the unit itself: how corrupted the caster is and how far
     * they may go, how many parchments bearing the spell they carry, whether the spell is still cooling, and
     * whether they cast for nothing, as in creative play.
     */
    public record Situation(int corruption, int corruptionLimit, int parchments, boolean cooling, boolean free) {}

    /**
     * The caster side of this unit, for {@link SpellCasting}: what it holds, what is to hand, and where what a
     * cast costs goes. The game side reads the corruption and the parchment off the player, and writes the
     * payment back to the wand and the player.
     */
    public SpellCasting.Caster caster(Situation situation, Payment payment) {
        return new SpellCasting.Caster() {
            @Override public long energy() { return energy; }
            @Override public int corruption() { return situation.corruption(); }
            @Override public int corruptionLimit() { return situation.corruptionLimit(); }
            @Override public int parchment() { return situation.parchments(); }
            @Override public boolean cooling() { return situation.cooling(); }
            @Override public boolean free() { return situation.free(); }
            @Override public void pay(long spent, int usedUp, int corruptionGain) { payment.pay(spent, usedUp, corruptionGain); }
        };
    }

    /** The caster side of this unit for a caster with no limit to their corruption who pays as usual. */
    public SpellCasting.Caster caster(int corruption, int parchments, boolean cooling, Payment payment) {
        return caster(new Situation(corruption, Integer.MAX_VALUE, parchments, cooling, false), payment);
    }

    /** Where what a cast costs goes: the energy out of the wand, the parchment used up, the corruption gained. */
    @FunctionalInterface
    public interface Payment {
        void pay(long energy, int parchments, int corruptionGain);
    }
}
