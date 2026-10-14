package com.miophas.corruption.core.arcane;

/**
 * Transcription: copying one string of an arcane key onto parchment. It needs the key, open at that string; a
 * blank parchment; and ink. Each copy uses up one parchment and one ink and makes one parchment bearing the
 * string, which must have room where copies go. A parchment bears one string, for good: one already written
 * on is not blank, and is not written over.
 *
 * Where copies are made is a {@link Desk}; the arcane lectern is one, and anything else that keeps a key,
 * parchment and ink can be another.
 */
public final class Transcription {
    public static final int PARCHMENT_PER_COPY = 1, INK_PER_COPY = 1;

    /** Whether a copy can be made, and if not, the first thing that stops it. */
    public enum Check { READY, NO_KEY, NOT_IN_KEY, NO_PARCHMENT, NO_INK, NO_ROOM }

    /** Where copies are made, as the rules see it. */
    public interface Desk {
        /** The strings of the key on it; empty without one. */
        Codex key();
        /** How many blank parchments wait to be written on. */
        int blankParchment();
        /** How much ink there is. */
        int ink();
        /** Whether one more parchment bearing this string fits where the copies go. */
        boolean hasRoomFor(String string);
        /** Uses up this much parchment and ink, and puts out one parchment bearing the string. */
        void write(String string, int parchment, int ink);
    }

    /** Whether a copy of this string can be made at the desk. */
    public static Check check(Desk desk, String string) {
        Codex key = desk.key();
        if (key.isEmpty()) return Check.NO_KEY;
        if (string == null || !key.reveals(string)) return Check.NOT_IN_KEY;
        if (desk.blankParchment() < PARCHMENT_PER_COPY) return Check.NO_PARCHMENT;
        if (desk.ink() < INK_PER_COPY) return Check.NO_INK;
        if (!desk.hasRoomFor(string)) return Check.NO_ROOM;
        return Check.READY;
    }

    /** Makes one copy if it can. Answers what the check found: only when READY was a copy made. */
    public static Check transcribe(Desk desk, String string) {
        Check check = check(desk, string);
        if (check == Check.READY) desk.write(string, PARCHMENT_PER_COPY, INK_PER_COPY);
        return check;
    }

    private Transcription() {}
}
