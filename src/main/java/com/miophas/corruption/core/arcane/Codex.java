package com.miophas.corruption.core.arcane;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * The arcane strings a key reveals, such as the Clavicula Salomonis, in the order it holds them. Strings are
 * known by their ids ("corruption:baal"); what they say is data, read elsewhere. A string appears once, where
 * it first appears.
 */
public record Codex(List<String> strings) {
    public static final Codex EMPTY = new Codex(List.of());

    public Codex { strings = List.copyOf(new LinkedHashSet<>(strings)); }

    public boolean isEmpty() { return strings.isEmpty(); }
    public int size() { return strings.size(); }
    public boolean reveals(String string) { return strings.contains(string); }
    /** The string at this place, or null past either end. */
    public String at(int index) { return index >= 0 && index < strings.size() ? strings.get(index) : null; }
    /** Where a string is, or -1 if this codex does not reveal it. */
    public int indexOf(String string) { return strings.indexOf(string); }
}
