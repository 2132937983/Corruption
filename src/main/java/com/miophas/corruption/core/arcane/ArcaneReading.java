package com.miophas.corruption.core.arcane;

/**
 * What a reader makes of an arcane string. Anyone sees its arcane form; only a reader holding a key that
 * reveals the string, a book such as the Clavicula Salomonis, understands what it means.
 */
public final class ArcaneReading {
    /** How much of a string a reader takes in. */
    public enum Sight {
        /** Only its arcane form, unread. */
        GLYPHS,
        /** Its arcane form and its meaning. */
        MEANING
    }

    /** What a reader holding these keys makes of a string. */
    public static Sight sight(String string, Iterable<Codex> keys) {
        for (Codex key : keys) if (key.reveals(string)) return Sight.MEANING;
        return Sight.GLYPHS;
    }

    private ArcaneReading() {}
}
