package com.miophas.corruption.core.arcane;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** The arcane's rules without a game: keys, reading, and copying onto parchment at a desk that is only numbers. */
class ArcaneRulesTest {
    /** A desk with a key, some blank parchment and ink, and room for so many copies. */
    private static final class Desk implements Transcription.Desk {
        Codex key;
        int parchment, ink, room;
        final List<String> copies = new ArrayList<>();
        Desk(Codex key, int parchment, int ink, int room) { this.key = key; this.parchment = parchment; this.ink = ink; this.room = room; }
        @Override public Codex key() { return key; }
        @Override public int blankParchment() { return parchment; }
        @Override public int ink() { return ink; }
        @Override public boolean hasRoomFor(String string) { return copies.size() < room; }
        @Override public void write(String string, int parchment, int ink) { this.parchment -= parchment; this.ink -= ink; copies.add(string); }
    }

    private static final Codex CLAVICULA = new Codex(List.of("corruption:baal", "corruption:agares", "corruption:corruption"));

    @Test void aKeyRevealsItsStringsOnceEachInItsOrder() {
        Codex codex = new Codex(List.of("b", "a", "b", "c"));
        assertEquals(List.of("b", "a", "c"), codex.strings());
        assertEquals("a", codex.at(1));
        assertNull(codex.at(3)); assertNull(codex.at(-1));
        assertEquals(2, codex.indexOf("c"));
        assertTrue(codex.reveals("a")); assertFalse(codex.reveals("d"));
        assertTrue(Codex.EMPTY.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> codex.strings().add("e"), "a codex does not change");
    }

    @Test void onlyAReaderWithTheRightKeyUnderstandsAString() {
        assertEquals(ArcaneReading.Sight.MEANING, ArcaneReading.sight("corruption:baal", List.of(Codex.EMPTY, CLAVICULA)));
        assertEquals(ArcaneReading.Sight.GLYPHS, ArcaneReading.sight("corruption:baal", List.of()), "without a key, only glyphs");
        assertEquals(ArcaneReading.Sight.GLYPHS, ArcaneReading.sight("corruption:paimon", List.of(CLAVICULA)), "a key reveals only its own strings");
    }

    @Test void aCopyUsesOneParchmentAndOneInk() {
        Desk desk = new Desk(CLAVICULA, 3, 2, 64);
        assertEquals(Transcription.Check.READY, Transcription.transcribe(desk, "corruption:agares"));
        assertEquals(List.of("corruption:agares"), desk.copies);
        assertEquals(2, desk.parchment); assertEquals(1, desk.ink);
        assertEquals(Transcription.Check.READY, Transcription.transcribe(desk, "corruption:baal"));
        assertEquals(Transcription.Check.NO_INK, Transcription.transcribe(desk, "corruption:baal"), "out of ink");
        assertEquals(2, desk.copies.size(), "nothing more was copied");
        assertEquals(1, desk.parchment, "nor any parchment used");
    }

    @Test void theFirstThingMissingIsTheOneNamed() {
        assertEquals(Transcription.Check.NO_KEY, Transcription.check(new Desk(Codex.EMPTY, 1, 1, 1), "corruption:baal"));
        assertEquals(Transcription.Check.NOT_IN_KEY, Transcription.check(new Desk(CLAVICULA, 1, 1, 1), "corruption:paimon"));
        assertEquals(Transcription.Check.NOT_IN_KEY, Transcription.check(new Desk(CLAVICULA, 1, 1, 1), null), "nothing chosen");
        assertEquals(Transcription.Check.NO_PARCHMENT, Transcription.check(new Desk(CLAVICULA, 0, 0, 0), "corruption:baal"));
        assertEquals(Transcription.Check.NO_INK, Transcription.check(new Desk(CLAVICULA, 1, 0, 0), "corruption:baal"));
        assertEquals(Transcription.Check.NO_ROOM, Transcription.check(new Desk(CLAVICULA, 1, 1, 0), "corruption:baal"));
        assertEquals(Transcription.Check.READY, Transcription.check(new Desk(CLAVICULA, 1, 1, 1), "corruption:baal"));
        Desk full = new Desk(CLAVICULA, 1, 1, 0);
        Transcription.transcribe(full, "corruption:baal");
        assertEquals(1, full.parchment, "nothing is used up when no copy is made");
    }

    /** Walks up from wherever the tests run to the project folder. */
    private static Path source(String folder) {
        for (Path at = Path.of("").toAbsolutePath(); at != null; at = at.getParent())
            if (Files.isDirectory(at.resolve(folder))) return at.resolve(folder);
        throw new AssertionError("Cannot find " + folder);
    }
    private static List<String> imports(String folder) throws Exception {
        try (Stream<Path> files = Files.list(source(folder))) {
            return files.filter(file -> file.toString().endsWith(".java")).flatMap(file -> {
                try { return Files.readAllLines(file).stream().filter(line -> line.startsWith("import ")).map(line -> file.getFileName() + ": " + line); }
                catch (Exception failure) { throw new RuntimeException(failure); }
            }).toList();
        }
    }

    @Test void theRulesAreGameFreeAndTheApiNeedsNothingInternal() throws Exception {
        for (String line : imports("src/main/java/com/miophas/corruption/core/arcane"))
            assertTrue(line.contains("import java."), "core/arcane is plain Java: " + line);
        for (String line : imports("src/main/java/com/miophas/corruption/api/arcane"))
            assertFalse(line.contains("com.miophas.corruption.common."), "api/arcane reaches into common: " + line);
    }
}
