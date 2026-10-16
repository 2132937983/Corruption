package com.miophas.corruption.core.demon;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.SeereRules.Mode;
import com.miophas.corruption.core.demon.SeereRules.Situation;
import com.miophas.corruption.core.demon.SeereRules.Skill;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** How Seere chooses what to do: by the way he goes (on the ground, in the sky, under the earth), and what would serve. */
class SeereRulesTest {
    private static final Set<Skill> ALL = EnumSet.copyOf(SeereRules.BLOWS);

    private static Situation ground(double distance, double offDegrees, int modeTicks) {
        return new Situation(Mode.GROUND, distance, 0, true, offDegrees, modeTicks, 0, ALL);
    }

    private static Situation sky(double distance, double above, int modeTicks) {
        return new Situation(Mode.SKY, distance, above, true, 0, modeTicks, 0, ALL);
    }

    /** What he picks over many rolls, and how often. */
    private static Map<Skill, Integer> picks(Situation now) {
        Map<Skill, Integer> seen = new EnumMap<>(Skill.class);
        for (int roll = 0; roll < 1000; roll++) seen.merge(SeereRules.choose(now, roll / 1000.0), 1, Integer::sum);
        return seen;
    }

    @Test void onTheGroundHisHoovesComeFirstForWhatIsCloseBeforeHim() {
        assertEquals(Skill.HOOVES, SeereRules.choose(ground(3, 10, 0), .99));
        assertEquals(Skill.BLINKS, SeereRules.choose(ground(3, 120, 0), 0), "not for what is behind him");
        Situation unready = new Situation(Mode.GROUND, 3, 0, true, 0, 0, 0, EnumSet.of(Skill.BLINKS));
        assertEquals(Skill.BLINKS, SeereRules.choose(unready, .5), "his hooves not come round, he blinks");
    }

    @Test void onTheGroundHeBlinksThrowsUpSpikesOrTakesOff() {
        assertEquals(Set.of(Skill.BLINKS, Skill.SPIKES), picks(ground(10, 0, 0)).keySet(), "not up again so soon after coming down");
        assertEquals(Set.of(Skill.BLINKS, Skill.SPIKES, Skill.TAKE_OFF), picks(ground(10, 0, SeereRules.GROUND_LEAST)).keySet());
        assertEquals(Set.of(Skill.BLINKS), picks(ground(10, 60, 0)).keySet(), "his spikes only for a quarry before him");
        Situation high = new Situation(Mode.GROUND, 10, 6, true, 0, 0, 0, ALL);
        assertEquals(Skill.TAKE_OFF, SeereRules.choose(high, .1), "at once for a quarry high above him");
    }

    @Test void aQuarryFarOffOrLongUnseenHeGoesToUnderTheEarth() {
        assertEquals(Skill.BURROW, SeereRules.choose(ground(SeereRules.BURROW_BEYOND + 1, 0, 0), .5));
        assertEquals(Skill.BURROW, SeereRules.choose(new Situation(Mode.GROUND, 10, 0, false, 0, 0, SeereRules.UNSEEN_BURROW, ALL), .5));
        assertEquals(Skill.NONE, SeereRules.choose(new Situation(Mode.GROUND, 10, 0, false, 0, 0, 10, ALL), .5), "only just lost, he waits");
        assertEquals(Skill.NONE, SeereRules.choose(new Situation(Mode.UNDERGROUND, 10, 0, false, 0, 0, 0, ALL), .5), "under the earth he only goes");
    }

    @Test void inTheSkyHeDivesOrPoursBeamsAndComesDownInTime() {
        assertEquals(Set.of(Skill.DIVE, Skill.BEAMS), picks(sky(20, -9, 0)).keySet());
        assertEquals(Set.of(Skill.BEAMS), picks(sky(20, 0, 0)).keySet(), "no dive on what is not well below him");
        assertTrue(picks(sky(20, -9, SeereRules.SKY_LEAST)).containsKey(Skill.LAND));
        assertEquals(Skill.LAND, SeereRules.choose(new Situation(Mode.SKY, 20, -9, false, 0, SeereRules.SKY_LEAST / 2, 30, ALL), .5),
                "his quarry lost from the sky a while after he took to it, he comes down");
        assertEquals(Skill.NONE, SeereRules.choose(new Situation(Mode.SKY, 20, -9, false, 0, 10, 30, ALL), .5));
        assertEquals(Skill.NONE, SeereRules.choose(new Situation(Mode.SKY, 20, -9, true, 0, 0, 0, Set.of()), .5), "nothing come round");
    }

    @Test void moreBeamsAsHisHealthGoes() {
        assertEquals(1, SeereRules.beams(1));
        assertEquals(2, SeereRules.beams(.7F));
        assertEquals(3, SeereRules.beams(.45F));
        assertEquals(4, SeereRules.beams(.1F));
    }

    @Test void heRisesNearHisQuarryOrWhenHeHasBeenUnderLongEnough() {
        assertFalse(SeereRules.rises(10, 0, true));
        assertTrue(SeereRules.rises(SeereRules.RISE_NEAR, 0, true));
        assertTrue(SeereRules.rises(10, SeereRules.UNDERGROUND_MOST, true));
        assertTrue(SeereRules.rises(10, 0, false), "with none to go to");
    }

    @Test void everyShiftTakesItsTimeAndHeOutrunsAnyHorse() {
        for (Skill skill : Skill.values()) assertEquals(skill.shift(), SeereRules.shiftTicks(skill) > 0, skill.name());
        assertFalse(SeereRules.BLOWS.stream().anyMatch(Skill::shift));
        assertTrue(SeereRules.SPEED > .3375, "a horse's speed is .3375 at the most");
    }
}
