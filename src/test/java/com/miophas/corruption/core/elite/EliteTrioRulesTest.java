package com.miophas.corruption.core.elite;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.corruption.RemnantRules;
import com.miophas.corruption.core.demon.AndromaliusRules;
import com.miophas.corruption.core.demon.BelialRules;
import com.miophas.corruption.core.demon.DantalionRules;
import com.miophas.corruption.core.demon.DecarabiaRules;
import com.miophas.corruption.core.demon.SeereRules;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleFunction;
import java.util.stream.DoubleStream;
import org.junit.jupiter.api.Test;

/**
 * The three elites that came after the fourteen, each made to a brief: the mindweaver, very hard to wear down, its blow
 * next to nothing, its harm a thread pulled at again and again in the mind; the blighthound, fast and weak-bitten, its
 * harm the curse every bite lays on deeper; the sower, the ground under its feet taken by the corruption. As rules, and
 * how each chooses what to do.
 */
class EliteTrioRulesTest {
    private static double leastLord() {
        return DoubleStream.of(AndromaliusRules.MAX_HEALTH, DantalionRules.MAX_HEALTH, SeereRules.MAX_HEALTH, DecarabiaRules.MAX_HEALTH,
                BelialRules.MAX_HEALTH).min().orElseThrow();
    }

    private static int outOfAHundred(DoubleFunction<Boolean> chosen) {
        int count = 0;
        for (int roll = 0; roll < 100; roll++) if (chosen.apply(roll / 100.0)) count++;
        return count;
    }

    @Test void theMindweaverIsTheHardestEliteToWearDownAndItsBlowIsNextToNothing() {
        List<Double> others = List.of(AberrationRules.MAX_HEALTH, ScreecherRules.MAX_HEALTH, LurkerRules.MAX_HEALTH, BruteRules.MAX_HEALTH,
                HowlerRules.MAX_HEALTH, StalkerRules.MAX_HEALTH, BloaterRules.MAX_HEALTH, MortarRules.MAX_HEALTH, CrawlerRules.MAX_HEALTH, GazerRules.MAX_HEALTH,
                HivemotherRules.MAX_HEALTH, DrifterRules.MAX_HEALTH, MawRules.MAX_HEALTH, NaiadRules.MAX_HEALTH, BlighthoundRules.MAX_HEALTH, SowerRules.MAX_HEALTH);
        for (double health : others) assertTrue(MindweaverRules.MAX_HEALTH >= health * 1.5, "half again any other elite's: " + health);
        assertTrue(MindweaverRules.MAX_HEALTH > leastLord(), "more even than the least of the lords' (who needs the arcane to be hurt)");
        assertTrue(MindweaverRules.MAX_HEALTH < leastLord() * 2);
        assertTrue(MindweaverRules.ATTACK_DAMAGE <= RemnantRules.DEFINITION.attackDamage(), "its blow no harder than a remnant's");
        assertTrue(MindweaverRules.THREAD.interval() <= 10, "its thread pulls at least twice a second");
        assertTrue(MindweaverRules.THREAD.most() >= 3 * MindweaverRules.ATTACK_DAMAGE, "and does far more than its blow");
        assertTrue(MindweaverRules.THREAD.heal() > 0, "each pull mends it");
    }

    @Test void theBlighthoundIsFastWeakBittenAndCursesDeep() {
        for (double speed : new double[]{BruteRules.MOVEMENT_SPEED, HowlerRules.MOVEMENT_SPEED, StalkerRules.MOVEMENT_SPEED, AberrationRules.MOVEMENT_SPEED})
            assertTrue(BlighthoundRules.MOVEMENT_SPEED > speed, "faster than any elite that walks: " + speed);
        assertTrue(BlighthoundRules.MOVEMENT_SPEED > .3375, "faster than the fastest horse");
        assertTrue(BlighthoundRules.ATTACK_DAMAGE <= RemnantRules.DEFINITION.attackDamage(), "its bite no harder than a remnant's blow");
        assertTrue(BlighthoundRules.MAX_HEALTH < StalkerRules.MAX_HEALTH, "and it is frail");
        assertEquals(1, BlighthoundRules.CURSE.strongest(), "its curse goes to Curse II, no further");
        assertTrue(BlighthoundRules.CURSE.most() <= 120, "and lasts six seconds at the most after its last bite");
        assertTrue(BlighthoundRules.frenzyBites(BlighthoundRules.FRENZY.windup()));
        int bites = 0;
        for (int tick = 0; tick < BlighthoundRules.FRENZY.ticks(); tick++) if (BlighthoundRules.frenzyBites(tick)) bites++;
        assertEquals(5, bites, "five bites in its frenzy");
    }

    @Test void theSowerTakesTheGroundAFewBlocksATick() {
        assertTrue(SowerRules.SOW_MOST > 0 && SowerRules.SOW_MOST <= 4);
        assertFalse(SowerRules.SEED.dormant(), "it is not turned to the one dormant corruption");
        assertTrue(SowerRules.SEED.still(), "what it sows keeps its own variant and spreads no further");
        assertTrue(SowerRules.SEED.most() > 0);
        assertTrue(SowerRules.SNARE.holds(true) > SowerRules.SNARE.holds(false), "its roots hold longer on the corruption's ground");
        assertTrue(SowerRules.REGEN > 0);
    }

    @Test void eachChoosesWhatServes() {
        assertEquals(MindweaverRules.Skill.NONE, MindweaverRules.choose(5, EnumSet.noneOf(MindweaverRules.Skill.class), .5));
        Set<MindweaverRules.Skill> minds = EnumSet.allOf(MindweaverRules.Skill.class);
        assertTrue(outOfAHundred(roll -> MindweaverRules.choose(4, minds, roll) == MindweaverRules.Skill.WAIL)
                > outOfAHundred(roll -> MindweaverRules.choose(9, minds, roll) == MindweaverRules.Skill.WAIL), "it wails the more at what is close");
        for (double roll = 0; roll <= 1; roll += .05)
            assertEquals(MindweaverRules.Skill.THREAD, MindweaverRules.choose(15, minds, roll), "beyond its wail, its thread");

        Set<BlighthoundRules.Skill> hounds = EnumSet.allOf(BlighthoundRules.Skill.class);
        for (double roll = 0; roll <= 1; roll += .05) {
            assertEquals(BlighthoundRules.Skill.FRENZY, BlighthoundRules.choose(1.5, hounds, roll), "close in, it worries");
            assertEquals(BlighthoundRules.Skill.REND, BlighthoundRules.choose(7, hounds, roll), "a little off, it springs");
        }
        assertEquals(BlighthoundRules.Skill.NONE, BlighthoundRules.choose(20, hounds, .5), "far off, it runs");

        Set<SowerRules.Skill> sowers = EnumSet.allOf(SowerRules.Skill.class);
        assertTrue(outOfAHundred(roll -> SowerRules.choose(3, sowers, roll) == SowerRules.Skill.SEED) > 50, "it sows about a quarry close by");
        for (double roll = 0; roll <= 1; roll += .05) assertEquals(SowerRules.Skill.SNARE, SowerRules.choose(12, sowers, roll), "and sends roots under one further off");
        for (MindweaverRules.Skill skill : MindweaverRules.SKILLS) assertEquals(skill.ordinal() - 1, MindweaverRules.SKILLS.indexOf(skill));
        for (BlighthoundRules.Skill skill : BlighthoundRules.SKILLS) assertEquals(skill.ordinal() - 1, BlighthoundRules.SKILLS.indexOf(skill));
        for (SowerRules.Skill skill : SowerRules.SKILLS) assertEquals(skill.ordinal() - 1, SowerRules.SKILLS.indexOf(skill));
    }
}
