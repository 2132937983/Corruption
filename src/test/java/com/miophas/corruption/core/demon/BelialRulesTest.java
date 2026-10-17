package com.miophas.corruption.core.demon;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.BelialRules.Form;
import com.miophas.corruption.core.demon.BelialRules.Situation;
import com.miophas.corruption.core.demon.BelialRules.Skill;
import com.miophas.corruption.core.demon.skill.ChargeRules;
import com.miophas.corruption.core.demon.skill.ChariotRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SummonRules;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Belial's rules: what each angel chooses, and what neither does; his blink when pressed; what the five of him do while
 * his likenesses are about; the hollow angel's volleys thicker and quicker and his beams six to the fair angel's two; his
 * host risen round his quarry; his chariot's line; his strikes at the mind quick to come round.
 */
class BelialRulesTest {
    private static final Set<Skill> ALL = EnumSet.copyOf(BelialRules.SKILLS);

    private static Situation at(Form form, double distance, float health, boolean unsafe, int called, boolean split, Set<Skill> ready) {
        return new Situation(form, distance, true, health, unsafe, called, split, ready);
    }

    /** Every skill he picks, rolling all the way through. */
    private static Set<Skill> chosen(Situation now) {
        Set<Skill> seen = new HashSet<>();
        for (double roll = 0; roll < 1; roll += .01) seen.add(BelialRules.choose(now, roll));
        return seen;
    }

    @Test void theFairAngelVolleysPoursBeamsCallsHisHostAndHisChariotButNoMore() {
        Set<Skill> fair = chosen(at(Form.FAIR, 14, .5F, false, 0, false, ALL));
        assertEquals(EnumSet.of(Skill.VOLLEY, Skill.BEAMS, Skill.SUMMON, Skill.CHARIOT), fair);
        Set<Skill> hollow = chosen(at(Form.HOLLOW, 14, .5F, false, 0, false, ALL));
        assertEquals(EnumSet.of(Skill.VOLLEY, Skill.BEAMS, Skill.SUMMON, Skill.CHARIOT, Skill.CLONES, Skill.PSYCHIC), hollow,
                "the hollow angel does all the fair one did, and his own");
    }

    @Test void hisVolleyIsHisCommonBlowAndTheHollowStrikesAtTheMindAsOften() {
        int volleys = 0, minds = 0, all = 0;
        for (double roll = 0; roll < 1; roll += .001, all++) {
            Skill next = BelialRules.choose(at(Form.HOLLOW, 14, .5F, false, 0, false, ALL), roll);
            if (next == Skill.VOLLEY) volleys++;
            if (next == Skill.PSYCHIC) minds++;
        }
        assertTrue(volleys > all / 5, "the volley a common choice: " + volleys + " of " + all);
        assertEquals(volleys, minds, all / 100.0, "and his strikes at the mind as common");
        assertTrue(BelialRules.PSYCHIC.timing().cooldown() + BelialRules.PSYCHIC.timing().ticks() <= 80, "coming round within four seconds");
    }

    @Test void heBlinksWhenPressedAndHisLikenessesComeOnlyOnceHeIsHurtAndAreFewWhileHeHasTheMany() {
        assertEquals(Skill.BLINK, BelialRules.choose(at(Form.FAIR, 3, 1, true, 0, false, ALL), .5));
        assertNotEquals(Skill.BLINK, BelialRules.choose(at(Form.FAIR, 3, 1, true, 0, false, EnumSet.complementOf(EnumSet.of(Skill.BLINK, Skill.NONE))), .5),
                "not before it has come round");
        assertFalse(chosen(at(Form.HOLLOW, 14, 1, false, 0, false, ALL)).contains(Skill.CLONES), "not at his full health");
        assertTrue(chosen(at(Form.HOLLOW, 14, BelialRules.CLONES_BELOW, false, 0, false, ALL)).contains(Skill.CLONES));
        assertFalse(chosen(at(Form.FAIR, 14, .5F, false, BelialRules.SUMMON.most(), false, ALL)).contains(Skill.SUMMON), "no more of his host while he has his most");
        assertEquals(Skill.NONE, BelialRules.choose(new Situation(Form.HOLLOW, 14, false, .5F, false, 0, false, ALL), .5), "nothing he cannot see");
    }

    @Test void whileHisLikenessesAreAboutTheFiveOfThemOnlyVolleyAndStrikeAtTheMind() {
        Set<Skill> split = chosen(at(Form.HOLLOW, 14, .5F, true, 0, true, ALL));
        assertEquals(EnumSet.of(Skill.VOLLEY, Skill.PSYCHIC), split, "not even a blink, which would give him away");
        Set<Skill> likeness = new HashSet<>();
        for (double roll = 0; roll < 1; roll += .01) likeness.add(BelialRules.chooseLikeness(14, true, ALL, roll));
        assertEquals(EnumSet.of(Skill.VOLLEY, Skill.PSYCHIC), likeness);
        Set<double[]> places = new HashSet<>();
        for (int index = 0; index <= BelialRules.LIKENESSES; index++) {
            double[] place = BelialRules.scatter(index, .3);
            assertEquals(BelialRules.SCATTER, Math.hypot(place[0], place[1]), 1e-9, "the five round where he was");
            places.add(place);
        }
        assertEquals(BelialRules.LIKENESSES + 1, places.size());
    }

    @Test void theHollowAngelsVolleysAreThickerAndQuickerAndHisBeamsSixToTwo() {
        assertTrue(BelialRules.HOLLOW_VOLLEY.bolts() > 3 * BelialRules.VOLLEY.bolts(), BelialRules.HOLLOW_VOLLEY.bolts() + " to " + BelialRules.VOLLEY.bolts());
        assertTrue(BelialRules.HOLLOW_VOLLEY.interval() < BelialRules.VOLLEY.interval(), "they come quicker");
        assertTrue(BelialRules.HOLLOW_VOLLEY.timing().cooldown() < BelialRules.VOLLEY.timing().cooldown(), "and come round sooner");
        assertEquals(2, BelialRules.beams(Form.FAIR));
        assertEquals(6, BelialRules.beams(Form.HOLLOW));
        assertTrue(BelialRules.BEAMS.most() >= 6);
        assertEquals(0, BelialRules.healthAt(400, 0), 1e-4, "risen from nothing");
        assertEquals(400, BelialRules.healthAt(400, 1), 1e-4, "to all of his health");
    }

    @Test void hisHostRisesRoundHisQuarryManyAtATime() {
        assertTrue(BelialRules.SUMMON.atQuarry(), "round where his quarry stood");
        assertTrue(BelialRules.SUMMON.count() >= 8, "a host: " + BelialRules.SUMMON.count());
        assertFalse(new SummonRules(new SkillTiming(5, 10, 100), 3, 6, 4, List.of("a")).atQuarry(), "a summon round the boss itself, as before");
    }

    @Test void hisChariotRunsThroughWhereHisQuarryStandsFromHisSide() {
        ChariotRules chariot = BelialRules.CHARIOT;
        double[] line = chariot.path(0, 0, 0, 30, 0);
        assertEquals(0, line[0], 1e-9);
        assertEquals(chariot.from(), line[1], 1e-9, "starting on his side of it");
        assertEquals(-chariot.past(), line[3], 1e-9, "and running on beyond it");
        assertEquals(0, ChariotRules.offLine(0, 0, line[0], line[1], line[2], line[3]), 1e-9, "through it");
        double[] turned = chariot.path(0, 0, 0, 30, Math.PI / 2);
        assertEquals(0, ChariotRules.offLine(0, 0, turned[0], turned[1], turned[2], turned[3]), 1e-9, "however it is turned");
        assertEquals(chariot.from(), Math.hypot(turned[0], turned[1]), 1e-9);
        assertEquals(0, chariot.along(0), 1e-9);
        assertEquals(1, chariot.along(chariot.runTicks()), 1e-9, "at its end by the time it has run");
        assertTrue(chariot.breaks(1.5F) && chariot.breaks(5) && !chariot.breaks(ChargeRules.OBSIDIAN) && !chariot.breaks(-1),
                "stone and iron give way; obsidian and bedrock do not");
        assertEquals(2, ChariotRules.offLine(2, 5, 0, 0, 0, 10), 1e-9, "the reach measured square to its line");
        assertEquals(5, ChariotRules.offLine(0, 15, 0, 0, 0, 10), 1e-9, "and from its end beyond it");
    }

    @Test void hisStrikeAtTheMindReachesThoseNearHisQuarry() {
        assertTrue(BelialRules.PSYCHIC.reaches(0) && BelialRules.PSYCHIC.reaches(BelialRules.PSYCHIC.radius()));
        assertFalse(BelialRules.PSYCHIC.reaches(BelialRules.PSYCHIC.radius() + .01));
        assertTrue(BelialRules.PSYCHIC.inRange(20) && !BelialRules.PSYCHIC.inRange(BelialRules.PSYCHIC.maxRange() + 1));
        assertTrue(BelialRules.PSYCHIC.nauseaTicks() > 0 && BelialRules.PSYCHIC.darknessTicks() > 0 && BelialRules.PSYCHIC.damage() > 0);
    }
}
