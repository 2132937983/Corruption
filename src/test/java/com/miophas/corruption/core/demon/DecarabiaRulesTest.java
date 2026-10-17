package com.miophas.corruption.core.demon;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.DecarabiaRules.Form;
import com.miophas.corruption.core.demon.DecarabiaRules.Situation;
import com.miophas.corruption.core.demon.DecarabiaRules.Skill;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Decarabia as rules: his change, his likenesses, and what he chooses to do as the star and as the goshawk. */
class DecarabiaRulesTest {
    private static final Set<Skill> ALL = EnumSet.copyOf(DecarabiaRules.SKILLS);

    private static Situation star(double distance, float health) { return new Situation(Form.STAR, distance, 4, true, health, false, ALL); }
    private static Situation hawk(double distance, boolean stunned) { return new Situation(Form.HAWK, distance, 9, true, .8F, stunned, ALL); }

    /** How often each skill is chosen over a thousand even rolls. */
    private static Map<Skill, Integer> picks(Situation now) {
        Map<Skill, Integer> picked = new EnumMap<>(Skill.class);
        for (int roll = 0; roll < 1000; roll++) picked.merge(DecarabiaRules.choose(now, (roll + .5) / 1000), 1, Integer::sum);
        return picked;
    }

    @Test void heChangesAtHalfHisHealthAndIsMadeWholeByTheEnd() {
        assertFalse(DecarabiaRules.changes(.51F));
        assertTrue(DecarabiaRules.changes(.5F));
        assertEquals(180, DecarabiaRules.healthAt(180, 360, 0), 1e-4);
        assertEquals(360, DecarabiaRules.healthAt(180, 360, 1), 1e-4, "all of it by the end");
        assertTrue(DecarabiaRules.healthAt(180, 360, .5F) > 180 && DecarabiaRules.healthAt(180, 360, .5F) < 360);
    }

    @Test void theStarShootsSpinsAndOnceHurtSplitsButNeverDoesWhatTheGoshawkDoes() {
        Map<Skill, Integer> whole = picks(star(12, 1));
        assertEquals(Set.of(Skill.VOLLEY, Skill.SPIN), whole.keySet(), "unhurt, he does not split");
        Map<Skill, Integer> hurt = picks(star(12, .8F));
        assertEquals(Set.of(Skill.VOLLEY, Skill.SPIN, Skill.SPLIT), hurt.keySet());
        assertTrue(picks(star(4, .8F)).get(Skill.SPIN) > hurt.get(Skill.SPIN), "close, he spins the more");
        assertEquals(Set.of(Skill.SPLIT), picks(star(30, .8F)).keySet(), "too far to shoot or to spin at: only his split (he closes in meanwhile)");
    }

    @Test void theGoshawkStoopsTheMoreOnAQuarryHeHasStunned() {
        Map<Skill, Integer> free = picks(hawk(16, false)), stunned = picks(hawk(16, true));
        assertEquals(Set.of(Skill.SCREECH, Skill.DIVE, Skill.DIVES, Skill.STORM, Skill.BARRAGE), free.keySet());
        assertFalse(stunned.containsKey(Skill.SCREECH), "a stunned quarry is not screeched at again");
        assertTrue(stunned.get(Skill.DIVE) > free.get(Skill.DIVE) && stunned.get(Skill.DIVES) > free.get(Skill.DIVES));
        for (Skill skill : free.keySet()) assertTrue(skill.hawk(), skill + " is the goshawk's");
    }

    @Test void heDoesNothingWithHisQuarryOutOfSightNorWhatHasNotComeRound() {
        assertEquals(Skill.NONE, DecarabiaRules.choose(new Situation(Form.STAR, 10, 4, false, .5F, false, ALL), .5));
        assertEquals(Skill.NONE, DecarabiaRules.choose(new Situation(Form.HAWK, 10, 9, true, .5F, false, EnumSet.noneOf(Skill.class)), .5));
        assertEquals(Skill.VOLLEY, DecarabiaRules.choose(new Situation(Form.STAR, 10, 4, true, 1, false, EnumSet.of(Skill.VOLLEY)), .9));
    }

    @Test void aSmallStarShootsOrSpinsAsTheStarDoes() {
        assertEquals(Skill.VOLLEY, DecarabiaRules.chooseSmall(15, true, EnumSet.of(Skill.VOLLEY, Skill.SPIN), 0));
        assertEquals(Skill.NONE, DecarabiaRules.chooseSmall(15, false, ALL, .5));
        assertEquals(Skill.SPIN, DecarabiaRules.chooseSmall(4, true, EnumSet.of(Skill.SPIN), .5));
        assertEquals(Skill.NONE, DecarabiaRules.chooseSmall(40, true, ALL, .5), "too far for either");
    }

    @Test void hisFiveStarsScatterEvenlyRoundWhereHeSplit() {
        assertEquals(4, DecarabiaRules.LIKENESSES);
        for (int index = 0; index <= DecarabiaRules.LIKENESSES; index++) {
            double[] at = DecarabiaRules.scatter(index, .3);
            assertEquals(DecarabiaRules.SCATTER, Math.hypot(at[0], at[1]), 1e-9);
            double[] next = DecarabiaRules.scatter((index + 1) % 5, .3);
            assertEquals(2 * DecarabiaRules.SCATTER * Math.sin(Math.PI / 5), Math.hypot(at[0] - next[0], at[1] - next[1]), 1e-9, "the points of a pentagon");
        }
        assertTrue(DecarabiaRules.SMALL < 1 && DecarabiaRules.LIKENESS_HEALTH < DecarabiaRules.MAX_HEALTH / 10);
        assertTrue(DecarabiaRules.STUN_TICKS >= 100, "a long stun once he is found out");
        assertTrue(DecarabiaRules.SMALL_VOLLEY.bolts() < DecarabiaRules.VOLLEY.bolts() && DecarabiaRules.SMALL_VOLLEY.damage() < DecarabiaRules.VOLLEY.damage());
    }

    @Test void hisBarrageIsSixtyFourBolts() {
        assertEquals(64, DecarabiaRules.BARRAGE.count());
        assertEquals(3, DecarabiaRules.DIVES.dives());
    }
}
