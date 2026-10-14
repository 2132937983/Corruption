/**
 * The corruption's arcane for add-ons: strings that only a key can make sense of, and parchment to copy them
 * onto, made ready for recipes that will ask for a parchment bearing a given string.
 *
 * <ul>
 *   <li><b>Strings</b> are a data pack registry, {@link com.miophas.corruption.api.arcane.ArcaneString}: an
 *   arcane form, seen by all in glyphs, and a meaning.</li>
 *   <li><b>Keys</b> reveal the meanings of a tag of strings. Which items are keys is the
 *   {@link com.miophas.corruption.api.arcane.ArcaneKey#DATA_MAP arcane_key data map}; the Clavicula Salomonis is one.</li>
 *   <li><b>Parchment</b> bears one string, as the {@link com.miophas.corruption.api.arcane.ArcaneApi#INSCRIPTION
 *   corruption:inscription} component. Any item in the corruption:parchment tag is parchment, and anything in
 *   corruption:arcane_ink is ink.</li>
 *   <li><b>Copying</b> a string from a key onto parchment uses up one parchment and one ink; the corrupted arcane
 *   lectern does it. The rules, and the Desk a copier of one's own would implement, are in
 *   com.miophas.corruption.core.arcane.Transcription.</li>
 * </ul>
 *
 * The rules are plain Java in com.miophas.corruption.core.arcane, with no game code in them; its small value
 * types (Codex, ArcaneReading.Sight, Transcription.Check) appear here as they are.
 */
package com.miophas.corruption.api.arcane;
