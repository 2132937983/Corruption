package com.miophas.corruption.core.demon;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.DantalionRules.Situation;
import com.miophas.corruption.core.demon.DantalionRules.Skill;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** How Dantalion chooses what to do: in danger he blinks; otherwise he picks among what has come round and would serve. */
class DantalionRulesTest {
    private static final Set<Skill> ALL = EnumSet.complementOf(EnumSet.of(Skill.NONE));

    private static Situation at(double distance, boolean mirror, Set<Skill> ready) {
        return new Situation(distance, true, mirror, false, 0, false, true, false, ready);
    }

    /** What he picks over many rolls, and how often. */
    private static Map<Skill, Integer> picks(Situation now) {
        Map<Skill, Integer> seen = new EnumMap<>(Skill.class);
        for (int roll = 0; roll < 1000; roll++) seen.merge(DantalionRules.choose(now, roll / 1000.0), 1, Integer::sum);
        return seen;
    }

    @Test void inDangerHeBlinksFirstOfAll() {
        Situation danger = new Situation(10, true, false, true, 0, false, true, false, ALL);
        assertEquals(Skill.BLINK, DantalionRules.choose(danger, .5));
        Situation unready = new Situation(10, true, false, true, 0, false, true, false, EnumSet.of(Skill.GAZE));
        assertEquals(Skill.GAZE, DantalionRules.choose(unready, .5), "his blink not come round, he fights on");
        Situation hidden = new Situation(10, false, false, true, 0, false, true, false, ALL);
        assertEquals(Skill.BLINK, DantalionRules.choose(hidden, .5), "even with his quarry out of sight");
    }

    @Test void outOfSightHeDoesNothingButBlink() {
        Situation hidden = new Situation(10, false, true, false, 0, false, true, false, ALL);
        assertEquals(Skill.NONE, DantalionRules.choose(hidden, .2));
    }

    @Test void asTheBookHeNeverUsesTheMirrorsSkills() {
        Map<Skill, Integer> book = picks(at(12, false, ALL));
        for (Skill skill : book.keySet()) assertFalse(skill.mirrorOnly(), skill + " is the mirror's");
        assertTrue(book.keySet().containsAll(EnumSet.of(Skill.GAZE, Skill.GREATER_GAZE, Skill.SUMMON, Skill.STRIKE, Skill.STORM)), "every skill of the book's in turn: " + book);
        Map<Skill, Integer> mirror = picks(at(12, true, ALL));
        assertTrue(mirror.keySet().containsAll(EnumSet.of(Skill.BEAM, Skill.MIND_CONTROL, Skill.GREATER_MIND_CONTROL)), "the mirror's besides: " + mirror);
        assertFalse(mirror.containsKey(Skill.BLINK), "he blinks only for danger");
    }

    @Test void heChoosesOnlyWhatWouldServe() {
        Situation impaired = new Situation(12, true, false, false, 0, true, true, false, ALL);
        Map<Skill, Integer> seen = picks(impaired);
        assertFalse(seen.containsKey(Skill.GAZE) || seen.containsKey(Skill.GREATER_GAZE), "no gaze on what is already blinded or slowed");
        Situation crowded = new Situation(12, true, false, false, DantalionRules.SUMMON.most(), false, true, false, ALL);
        assertFalse(picks(crowded).containsKey(Skill.SUMMON), "no more remnants than he may keep");
        Situation beast = new Situation(12, true, true, false, 0, false, false, false, ALL);
        Map<Skill, Integer> onBeast = picks(beast);
        assertFalse(onBeast.containsKey(Skill.MIND_CONTROL) || onBeast.containsKey(Skill.GREATER_MIND_CONTROL), "only a player's mind can be taken");
        Situation held = new Situation(12, true, true, false, 0, false, true, true, ALL);
        Map<Skill, Integer> whileHeld = picks(held), whileFree = picks(at(12, true, ALL));
        assertFalse(whileHeld.containsKey(Skill.MIND_CONTROL), "a held mind is not taken again");
        assertTrue(whileHeld.getOrDefault(Skill.BEAM, 0) > whileFree.getOrDefault(Skill.BEAM, 0), "held, it is what his beam is for");
        assertEquals(Skill.NONE, DantalionRules.choose(at(12, true, EnumSet.noneOf(Skill.class)), .5), "nothing come round, nothing done");
    }

    @Test void heChangesAtHalfHisHealthAndHisCooldownsShortenAsTheMirror() {
        assertFalse(DantalionRules.changes(.51F));
        assertTrue(DantalionRules.changes(.5F));
        assertEquals(80, DantalionRules.cooldown(100, true));
        assertEquals(100, DantalionRules.cooldown(100, false));
        assertEquals(1, DantalionRules.keep(DantalionRules.FAR + 1));
        assertEquals(-1, DantalionRules.keep(DantalionRules.NEAR - 1));
        assertEquals(0, DantalionRules.keep(DantalionRules.KEEP_AT));
        assertEquals(9, DantalionRules.SKILLS.size(), "every skill but NONE, in the entity's order");
    }

    /** Over his change his health fills back up, evenly at first and last, all of it by the end. */
    @Test void heIsMadeWholeAsHeChanges() {
        assertEquals(100, DantalionRules.healthAt(100, 280, 0), 1e-4, "as he begins, as he was");
        assertEquals(190, DantalionRules.healthAt(100, 280, .5F), 1e-4, "halfway, halfway");
        assertEquals(280, DantalionRules.healthAt(100, 280, 1), 1e-4, "at the end, all of it");
        assertEquals(280, DantalionRules.healthAt(100, 280, 2), 1e-4, "and no more");
        float last = 100;
        for (float through = .05F; through <= 1; through += .05F) {
            float now = DantalionRules.healthAt(100, 280, through);
            assertTrue(now >= last, "never falling back");
            last = now;
        }
    }

    /** His gaze is Andromalius's own; his greater gaze opens more eyes, slows and blinds for longer, and withers. */
    @Test void hisGazeIsAndromaliussAndHisGreaterGazeGoesFurther() {
        var gaze = DantalionRules.GAZE;
        var greater = DantalionRules.GREATER_GAZE;
        assertEquals(AndromaliusRules.GAZE.slownessTicks(), gaze.slownessTicks());
        assertEquals(AndromaliusRules.GAZE.slownessLevel(), gaze.slownessLevel());
        assertEquals(AndromaliusRules.GAZE.blindnessTicks(), gaze.blindnessTicks());
        assertFalse(gaze.withers());
        assertTrue(greater.eyes() > gaze.eyes(), "more Eyes of Providence");
        assertTrue(greater.slownessTicks() > gaze.slownessTicks() && greater.blindnessTicks() > gaze.blindnessTicks(), "slowed and blinded for longer");
        assertTrue(greater.withers(), "and withered");
    }
}
