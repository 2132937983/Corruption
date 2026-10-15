package com.miophas.corruption.core.arcane;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * What a wand has been taught and which of it is chosen: at most {@link #CAPACITY} spells, known by their ids
 * ("corruption:baal"), and the one that casting uses. A spell already known is not written again; a full
 * grimoire takes no more.
 *
 * <p>Like a {@link Codex}, this is plain data with no game in it. The arcane pedestal fills it from parchment
 * bearing a string; the wheel screen chooses among what it holds.
 */
public record Grimoire(List<String> spells, int selected) {
    /** How many spells one wand has room for. */
    public static final int CAPACITY = 3;
    public static final Grimoire EMPTY = new Grimoire(List.of(), 0);

    public Grimoire {
        spells = List.copyOf(new LinkedHashSet<>(spells));
        selected = spells.isEmpty() ? 0 : Math.floorMod(selected, spells.size());
    }

    /** Whether a spell can be written into the grimoire, and if not, why not. */
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

    /** Whether this spell, by its id, can be written in; a spell is never written twice. */
    public Inscribe check(String spell) {
        if (spells.contains(spell)) return Inscribe.ALREADY_KNOWN;
        return spells.size() < CAPACITY ? Inscribe.READY : Inscribe.FULL;
    }

    /** The same grimoire with the spell written in, if it can be; otherwise this one, unchanged. */
    public Grimoire inscribed(String spell) {
        if (check(spell) != Inscribe.READY) return this;
        List<String> grown = new ArrayList<>(spells);
        grown.add(spell);
        return new Grimoire(grown, spells.isEmpty() ? 0 : selected);
    }

    /** The same grimoire with another spell chosen; choosing one it does not hold changes nothing. */
    public Grimoire selecting(int index) { return index < 0 || index >= spells.size() ? this : new Grimoire(spells, index); }

    /** The same grimoire with the next spell chosen, wrapping round; the state a wand is in between casts. */
    public Grimoire cycled() { return spells.isEmpty() ? this : new Grimoire(spells, selected + 1); }
}
