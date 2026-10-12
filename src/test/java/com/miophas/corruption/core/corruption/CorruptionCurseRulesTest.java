package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.CorruptionCurseApi;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CorruptionCurseRulesTest {
    @Test void potionsFollowTheVanillaBaseLongStrongPattern() {
        var potions = CorruptionCurseApi.potions();
        assertEquals(3, potions.size());
        var base = potions.get(0);
        var extended = potions.get(1);
        var strong = potions.get(2);
        assertEquals(CorruptionCurseApi.potionName(), base.id());
        assertEquals("minecraft:awkward", base.brewedFrom());
        assertEquals("corruption:" + CorruptionCurseApi.flowingCorruption().id() + "_bucket", base.reagent());
        assertEquals("long_" + base.id(), extended.id());
        assertEquals("minecraft:redstone", extended.reagent());
        assertTrue(extended.durationTicks() > base.durationTicks());
        assertEquals(base.amplifier(), extended.amplifier());
        assertEquals("strong_" + base.id(), strong.id());
        assertEquals("minecraft:glowstone_dust", strong.reagent());
        assertTrue(strong.amplifier() > base.amplifier() && strong.durationTicks() < base.durationTicks());
    }
    @Test void everyVariantIsBrewedFromAnEarlierPotionAndLastsWholeCursePulses() {
        var known = new HashSet<String>();
        known.add("minecraft:awkward");
        for (var potion : CorruptionCurseApi.potions()) {
            assertTrue(known.contains(potion.brewedFrom()), potion.id());
            assertTrue(known.add("corruption:" + potion.id()), potion.id());
            assertTrue(potion.durationTicks() >= 20 && potion.durationTicks() % 20 == 0, potion.id());
        }
    }
    @Test void strongerCursesDealProportionallyMoreDamage() {
        assertEquals(1F, CorruptionCurseApi.damageScale(0));
        assertEquals(2F, CorruptionCurseApi.damageScale(1));
        assertEquals(1F, CorruptionCurseApi.damageScale(-3));
    }
    @Test void fluidCursesCreaturesAndSurvivalPlayersButNeverRemnantsOrCreativePlayers() {
        assertTrue(CorruptionCurseApi.fluidCurses(true, false, false));
        assertFalse(CorruptionCurseApi.fluidCurses(true, true, false));
        assertFalse(CorruptionCurseApi.fluidCurses(false, false, true));
        assertFalse(CorruptionCurseApi.flowingCorruption().playersOnly());
        assertTrue(CorruptionCurseApi.fluidCurses(false, false, false), "mobs and animals are cursed too");
    }
    @Test void fluidCurseIsEndlessIncurableAndOnlyReappliedWhenTheCurrentCurseIsLesser() {
        var fluid = CorruptionCurseApi.flowingCorruption();
        assertEquals(CorruptionCurseApi.ENDLESS, fluid.curseTicks());
        assertFalse(fluid.curable());
        assertTrue(CorruptionCurseApi.fluidCurseHolds(CorruptionCurseApi.ENDLESS, fluid.curseAmplifier(), false));
        assertTrue(CorruptionCurseApi.fluidCurseHolds(CorruptionCurseApi.ENDLESS, fluid.curseAmplifier() + 1, false));
        assertFalse(CorruptionCurseApi.fluidCurseHolds(CorruptionCurseApi.ENDLESS, fluid.curseAmplifier(), true), "a curable curse must be replaced");
        assertFalse(CorruptionCurseApi.fluidCurseHolds(120, fluid.curseAmplifier(), false), "a timed curse must be replaced");
        assertFalse(CorruptionCurseApi.fluidCurseHolds(CorruptionCurseApi.ENDLESS, fluid.curseAmplifier() - 1, false));
    }
}
