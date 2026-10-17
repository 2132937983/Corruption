package com.miophas.corruption.core.demon;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.AmdusiasRules.Situation;
import com.miophas.corruption.core.demon.AmdusiasRules.Skill;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Amdusias as rules: Seere's blows on the ground, his own besides, one form only and no flight; and how he chooses what to
 * do (his hooves first for what is close before him; the rest as what would serve).
 */
class AmdusiasRulesTest {
    private static final Set<Skill> ALL = EnumSet.copyOf(AmdusiasRules.BLOWS);

    private static Situation at(double distance, double above, double offDegrees, int near) {
        return new Situation(distance, above, true, offDegrees, near, ALL);
    }

    private static Map<Skill, Integer> picks(Situation now) {
        Map<Skill, Integer> seen = new EnumMap<>(Skill.class);
        for (int roll = 0; roll < 1000; roll++) seen.merge(AmdusiasRules.choose(now, roll / 1000.0), 1, Integer::sum);
        return seen;
    }

    @Test void onTheGroundHeIsSeeresMatch() {
        assertSame(SeereRules.HOOVES, AmdusiasRules.HOOVES);
        assertSame(SeereRules.BLINKS, AmdusiasRules.BLINKS);
        assertSame(SeereRules.SPIKES, AmdusiasRules.SPIKES);
        assertEquals(AmdusiasRules.BLOWS.size() + 1, Skill.values().length, "every blow but NONE is in his set, and nothing that flies");
        for (Skill blow : AmdusiasRules.BLOWS) assertNotEquals(Skill.NONE, blow);
        assertTrue(AmdusiasRules.MAX_HEALTH > SeereRules.MAX_HEALTH && AmdusiasRules.MAX_HEALTH > BelialRules.MAX_HEALTH, "the strongest lord yet");
    }

    @Test void hisHoovesComeFirstForWhatIsCloseBeforeHim() {
        assertEquals(Skill.HOOVES, AmdusiasRules.choose(at(3, 0, 10, 1), .99));
        assertNotEquals(Skill.HOOVES, AmdusiasRules.choose(at(3, 0, 150, 1), .5), "not for what is behind him");
        assertEquals(Skill.NONE, AmdusiasRules.choose(new Situation(10, 0, false, 0, 1, ALL), .5), "a quarry he cannot see, he gallops after");
        assertEquals(Skill.NONE, AmdusiasRules.choose(new Situation(10, 0, true, 0, 1, EnumSet.noneOf(Skill.class)), .5), "nothing come round, nothing");
    }

    @Test void heChoosesWhatWouldServe() {
        Map<Skill, Integer> far = picks(at(25, 0, 0, 1));
        assertTrue(far.getOrDefault(Skill.FANFARE, 0) > 0 && far.getOrDefault(Skill.UPHEAVAL, 0) > 0 && far.getOrDefault(Skill.PSYCHIC, 0) > 0);
        assertEquals(0, far.getOrDefault(Skill.BLIGHT, 0), "no blight for a quarry far off");
        assertEquals(0, far.getOrDefault(Skill.CRESCENDO, 0), "nor a crescendo");
        assertEquals(0, far.getOrDefault(Skill.BLINKS, 0), "and too far to blink");
        Map<Skill, Integer> close = picks(at(6, 0, 0, 1)), crowded = picks(at(6, 0, 0, 3));
        assertTrue(crowded.get(Skill.CRESCENDO) > close.get(Skill.CRESCENDO), "the more about him, the likelier his crescendo");
        Map<Skill, Integer> level = picks(at(12, 0, 0, 1)), above = picks(at(12, 6, 0, 1));
        assertTrue(above.get(Skill.UPHEAVAL) > level.get(Skill.UPHEAVAL), "a quarry high above him, the likelier he heaves the ground under it");
        Map<Skill, Integer> near = picks(at(10, 0, 0, 1)), further = picks(at(20, 0, 0, 1));
        assertTrue(further.get(Skill.FANFARE) * 1.0 / 1000 > near.get(Skill.FANFARE) * 1.0 / 1000, "the further off, the likelier his fanfare");
        Map<Skill, Integer> aside = picks(at(10, 0, 90, 1));
        assertEquals(0, aside.getOrDefault(Skill.SPIKES, 0), "his spikes only for a quarry before him");
    }

    @Test void hisFanfareIsAGreatManyBoltsEachHurtingHard() {
        assertTrue(AmdusiasRules.FANFARE.bolts() >= 100, "a great many: " + AmdusiasRules.FANFARE.bolts());
        assertTrue(AmdusiasRules.FANFARE.damage() >= 6, "each hurting hard");
        assertTrue(AmdusiasRules.BLIGHT.dormant(), "his blight leaves the dormant corruption");
        assertTrue(AmdusiasRules.CRESCENDO.rings() >= 2 && AmdusiasRules.CRESCENDO.knockback() > 1, "his crescendo rings out more than once, throwing hard");
        assertTrue(AmdusiasRules.UPHEAVAL.spikeLaunch() > 1, "his spikes fling a quarry high");
    }
}
