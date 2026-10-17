package com.miophas.corruption.core.elite;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.corruption.RemnantRules;
import com.miophas.corruption.core.demon.AndromaliusRules;
import com.miophas.corruption.core.demon.BelialRules;
import com.miophas.corruption.core.demon.DantalionRules;
import com.miophas.corruption.core.demon.DecarabiaRules;
import com.miophas.corruption.core.demon.SeereRules;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * The corruption's elites as rules: between its remnants and its lords in health, harder-hitting than a remnant, each with
 * a blow from afar; and how each chooses what to do.
 */
class EliteRulesTest {
    @Test void anEliteStandsBetweenTheRemnantsAndTheLords() {
        double remnant = RemnantRules.DEFINITION.health();
        double leastLord = java.util.stream.DoubleStream.of(AndromaliusRules.MAX_HEALTH, DantalionRules.MAX_HEALTH, SeereRules.MAX_HEALTH,
                DecarabiaRules.MAX_HEALTH, BelialRules.MAX_HEALTH).min().orElseThrow();
        for (double health : new double[]{AberrationRules.MAX_HEALTH, ScreecherRules.MAX_HEALTH, LurkerRules.MAX_HEALTH}) {
            assertTrue(health > remnant * 3, "far more than a remnant's " + remnant + ": " + health);
            assertTrue(health < leastLord, "less than the least of the lords' " + leastLord + ": " + health);
        }
        for (double damage : new double[]{AberrationRules.ATTACK_DAMAGE, ScreecherRules.ATTACK_DAMAGE, LurkerRules.ATTACK_DAMAGE})
            assertTrue(damage >= 2 * RemnantRules.DEFINITION.attackDamage(), "its basic blow hits harder than a remnant's");
        assertTrue(AberrationRules.ACID.maxRange() > 12 && ScreecherRules.FEATHERS.maxRange() > 12 && LurkerRules.BOLTS.maxRange() > 12,
                "each strikes from afar too");
    }

    @Test void pickWeighsWhatWouldServeAndPassesOverWhatWouldNot() {
        double[] weights = {0, 3, 0, 1};
        assertEquals(1, EliteRules.pick(weights, 0));
        assertEquals(1, EliteRules.pick(weights, .74));
        assertEquals(3, EliteRules.pick(weights, .76));
        assertEquals(3, EliteRules.pick(weights, 1), "a roll of one is the last that serves");
        assertEquals(-1, EliteRules.pick(new double[]{0, 0}, .5), "none, when none would serve");
        assertEquals(-1, EliteRules.pick(new double[]{-2}, .5), "a weight below none is none");
    }

    @Test void theAberrationPouncesAndSpitsAndGoesBerserkWhenBroughtLow() {
        assertEquals(AberrationRules.Skill.NONE, AberrationRules.choose(8, EnumSet.noneOf(AberrationRules.Skill.class), .5), "it bites, with nothing ready");
        for (double roll = 0; roll <= 1; roll += .05) {
            assertEquals(AberrationRules.Skill.POUNCE, AberrationRules.choose(8, EnumSet.of(AberrationRules.Skill.POUNCE), roll));
            assertNotEquals(AberrationRules.Skill.ROAR, AberrationRules.choose(8, EnumSet.allOf(AberrationRules.Skill.class), roll), "it never chooses to roar");
        }
        int spat = 0;
        for (double roll = 0; roll < 1; roll += .01)
            if (AberrationRules.choose(16, EnumSet.of(AberrationRules.Skill.POUNCE, AberrationRules.Skill.ACID), roll) == AberrationRules.Skill.ACID) spat++;
        assertTrue(spat > 50, "further off than it can leap, it spits the more: " + spat);
        assertTrue(AberrationRules.berserk(.35) && !AberrationRules.berserk(.36));
        assertEquals(50, AberrationRules.cooldown(100, true), "berserk, its skills come round twice as fast");
        assertEquals(100, AberrationRules.cooldown(100, false));
        assertEquals(AberrationRules.SKILLS.size() + 1, AberrationRules.Skill.values().length, "every skill but NONE is in its set");
        for (AberrationRules.Skill skill : AberrationRules.SKILLS) assertEquals(skill.ordinal() - 1, AberrationRules.SKILLS.indexOf(skill), "in their order");
    }

    @Test void theScreecherDoesNotScreechAtWhatIsStunnedAlready() {
        Set<ScreecherRules.Skill> all = EnumSet.of(ScreecherRules.Skill.SWOOP, ScreecherRules.Skill.DIVE, ScreecherRules.Skill.FEATHERS, ScreecherRules.Skill.SCREECH);
        for (double roll = 0; roll <= 1; roll += .02)
            assertNotEquals(ScreecherRules.Skill.SCREECH, ScreecherRules.choose(6, true, all, roll));
        int screeched = 0;
        for (double roll = 0; roll < 1; roll += .01) if (ScreecherRules.choose(6, false, all, roll) == ScreecherRules.Skill.SCREECH) screeched++;
        assertTrue(screeched > 0, "but does at what is not");
        assertTrue(ScreecherRules.SCREECH.slows(), "its screech slows as well as stuns");
        assertEquals(0, ScreecherRules.DIVE.debrisMost(), "its dive tears nothing up");
        for (ScreecherRules.Skill skill : ScreecherRules.SKILLS) assertEquals(skill.ordinal() - 1, ScreecherRules.SKILLS.indexOf(skill));
    }

    @Test void theLurkerDragsInAQuarryOutOfTheWaterMoreReadilyThanOneInIt() {
        Set<LurkerRules.Skill> both = EnumSet.of(LurkerRules.Skill.GRAB, LurkerRules.Skill.BOLTS);
        int ashore = 0, swimming = 0;
        for (double roll = 0; roll < 1; roll += .01) {
            if (LurkerRules.choose(6, false, both, roll) == LurkerRules.Skill.GRAB) ashore++;
            if (LurkerRules.choose(6, true, both, roll) == LurkerRules.Skill.GRAB) swimming++;
        }
        assertTrue(ashore > swimming, ashore + " against " + swimming);
        assertTrue(LurkerRules.DART > 3 * .1, "it darts far faster than a player swims");
        assertTrue(LurkerRules.CRAWL < LurkerRules.WANDER * 3, "and crawls slowly on land");
        assertTrue(LurkerRules.BOLTS.push() > 0, "its bolts throw back what they strike");
        for (LurkerRules.Skill skill : LurkerRules.SKILLS) assertEquals(skill.ordinal() - 1, LurkerRules.SKILLS.indexOf(skill));
    }
}
