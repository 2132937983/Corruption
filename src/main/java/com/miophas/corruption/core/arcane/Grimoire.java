package com.miophas.corruption.core.arcane;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * What a wand has been taught and which of it is chosen: the spells it holds, known by their ids
 * ("corruption:baal"), in the order of its slots, and the one that casting uses. A spell already known is not
 * written again; a grimoire with no room left takes no more. How much room there is belongs to the casting
 * unit that carries it (see {@link CastingUnit}); {@link #CAPACITY} is the mod's own wand's.
 *
 * <p>Like a {@link Codex}, this is plain data with no game in it. The arcane pedestal fills it from parchment
 * bearing a string; the wheel screen chooses among what it holds.
 */
public record Grimoire(List<String> spells, int selected) {
    /** How many spells the mod's own wand has room for. */
    public static final int CAPACITY = 3;
    public static final Grimoire EMPTY = new Grimoire(List.of(), 0);

    public Grimoire {
        spells = List.copyOf(new LinkedHashSet<>(spells));
        selected = spells.isEmpty() ? 0 : Math.floorMod(selected, spells.size());
    }

    /** Whether a spell can be written in, and if not, why not. */
    public enum Inscribe { READY, ALREADY_KNOWN, FULL }

    public boolean isEmpty() { return spells.isEmpty(); }
    public int size() { return spells.size(); }
    public boolean knows(String spell) { return spells.contains(spell); }
    /** The spell in this place, or null past either end. */
    public String at(int index) { return index >= 0 && index < spells.size() ? spells.get(index) : null; }
    /** The spell casting uses, or null while none is written. */
    public String current() { return at(selected); }
    /** Where a spell sits, or -1 if the grimoire does not hold it. */
    public int indexOf(String spell) { return spells.indexOf(spell); }

    /** Whether this spell, by its id, can be added after the last, with room for this many; a spell is never written twice. */
    public Inscribe check(String spell, int capacity) { return checkAt(spells.size(), spell, capacity); }
    /** {@link #check(String, int)} for the mod's own wand. */
    public Inscribe check(String spell) { return check(spell, CAPACITY); }

    /**
     * Whether this spell can be written into this place, with room for this many: the next free place adds it,
     * one already written is written over. A place past the next free one, or beyond the room, is full.
     */
    public Inscribe checkAt(int index, String spell, int capacity) {
        if (spells.contains(spell)) return Inscribe.ALREADY_KNOWN;
        return index >= 0 && index <= spells.size() && index < capacity ? Inscribe.READY : Inscribe.FULL;
    }

    /** The same grimoire with the spell added after the last, if it can be; otherwise this one, unchanged. */
    public Grimoire inscribed(String spell, int capacity) { return writtenAt(spells.size(), spell, capacity); }
    /** {@link #inscribed(String, int)} for the mod's own wand. */
    public Grimoire inscribed(String spell) { return inscribed(spell, CAPACITY); }

    /**
     * The same grimoire with the spell written into this place, if it can be (see {@link #checkAt}): added at the
     * next free place, or over what was written there. The chosen spell stays chosen by its place.
     */
    public Grimoire writtenAt(int index, String spell, int capacity) {
        if (checkAt(index, spell, capacity) != Inscribe.READY) return this;
        List<String> written = new ArrayList<>(spells);
        if (index == written.size()) written.add(spell); else written.set(index, spell);
        return new Grimoire(written, spells.isEmpty() ? 0 : selected);
    }

    /** The same grimoire with another spell chosen; choosing one it does not hold changes nothing. */
    public Grimoire selecting(int index) { return index < 0 || index >= spells.size() ? this : new Grimoire(spells, index); }

    /** The same grimoire with the next spell chosen, wrapping round; the state a wand is in between casts. */
    public Grimoire cycled() { return spells.isEmpty() ? this : new Grimoire(spells, selected + 1); }
}
