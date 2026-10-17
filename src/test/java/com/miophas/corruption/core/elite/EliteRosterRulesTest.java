package com.miophas.corruption.core.elite;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.corruption.RemnantRules;
import com.miophas.corruption.core.demon.AndromaliusRules;
import com.miophas.corruption.core.demon.BelialRules;
import com.miophas.corruption.core.demon.DantalionRules;
import com.miophas.corruption.core.demon.DecarabiaRules;
import com.miophas.corruption.core.demon.SeereRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SummonRules;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleFunction;
import java.util.stream.DoubleStream;
import org.junit.jupiter.api.Test;

/**
 * The eleven elites that joined the first three, as rules: each between the remnants and the lords as the first three
 * are, its skills in its set in their order; and how each chooses what to do (what serves it near, what far, what it
 * passes over).
 */
class EliteRosterRulesTest {
    /** One elite's numbers and its skills, for the checks every elite passes. */
    private record Elite(String name, double health, double damage, Enum<?>[] skills, List<? extends Enum<?>> set) {}

    private static final List<Elite> ELITES = List.of(
            new Elite("brute", BruteRules.MAX_HEALTH, BruteRules.ATTACK_DAMAGE, BruteRules.Skill.values(), BruteRules.SKILLS),
            new Elite("howler", HowlerRules.MAX_HEALTH, HowlerRules.ATTACK_DAMAGE, HowlerRules.Skill.values(), HowlerRules.SKILLS),
            new Elite("stalker", StalkerRules.MAX_HEALTH, StalkerRules.ATTACK_DAMAGE, StalkerRules.Skill.values(), StalkerRules.SKILLS),
            new Elite("bloater", BloaterRules.MAX_HEALTH, BloaterRules.ATTACK_DAMAGE, BloaterRules.Skill.values(), BloaterRules.SKILLS),
            new Elite("mortar", MortarRules.MAX_HEALTH, MortarRules.ATTACK_DAMAGE, MortarRules.Skill.values(), MortarRules.SKILLS),
            new Elite("crawler", CrawlerRules.MAX_HEALTH, CrawlerRules.ATTACK_DAMAGE, CrawlerRules.Skill.values(), CrawlerRules.SKILLS),
            new Elite("gazer", GazerRules.MAX_HEALTH, GazerRules.ATTACK_DAMAGE, GazerRules.Skill.values(), GazerRules.SKILLS),
            new Elite("hivemother", HivemotherRules.MAX_HEALTH, HivemotherRules.ATTACK_DAMAGE, HivemotherRules.Skill.values(), HivemotherRules.SKILLS),
            new Elite("drifter", DrifterRules.MAX_HEALTH, DrifterRules.ATTACK_DAMAGE, DrifterRules.Skill.values(), DrifterRules.SKILLS),
            new Elite("maw", MawRules.MAX_HEALTH, MawRules.ATTACK_DAMAGE, MawRules.Skill.values(), MawRules.SKILLS),
            new Elite("naiad", NaiadRules.MAX_HEALTH, NaiadRules.ATTACK_DAMAGE, NaiadRules.Skill.values(), NaiadRules.SKILLS));

    /** How many of a hundred rolls, evenly spread, a choice comes out as wanted. */
    private static int outOfAHundred(DoubleFunction<Boolean> chosen) {
        int count = 0;
        for (int roll = 0; roll < 100; roll++) if (chosen.apply(roll / 100.0)) count++;
        return count;
    }

    @Test void everyNewEliteStandsBetweenTheRemnantsAndTheLordsAndKeepsItsSkillsInOrder() {
        double remnant = RemnantRules.DEFINITION.health();
        double leastLord = DoubleStream.of(AndromaliusRules.MAX_HEALTH, DantalionRules.MAX_HEALTH, SeereRules.MAX_HEALTH, DecarabiaRules.MAX_HEALTH,
                BelialRules.MAX_HEALTH).min().orElseThrow();
        for (Elite elite : ELITES) {
            assertTrue(elite.health() > remnant * 3, elite.name() + ": far more than a remnant's " + remnant);
            assertTrue(elite.health() < leastLord, elite.name() + ": less than the least of the lords' " + leastLord);
            assertTrue(elite.damage() >= 2 * RemnantRules.DEFINITION.attackDamage(), elite.name() + ": its blow hits harder than a remnant's");
            assertEquals("NONE", elite.skills()[0].name(), elite.name() + ": NONE first");
            assertEquals(elite.skills().length - 1, elite.set().size(), elite.name() + ": every skill but NONE is in its set");
            for (Enum<?> skill : elite.set()) assertEquals(skill.ordinal() - 1, elite.set().indexOf(skill), elite.name() + ": in their order");
        }
    }

    @Test void nothingReadyMeansItStrikesWithWhatItHas() {
        assertEquals(BruteRules.Skill.NONE, BruteRules.choose(5, EnumSet.noneOf(BruteRules.Skill.class), .5));
        assertEquals(HowlerRules.Skill.NONE, HowlerRules.choose(5, EnumSet.noneOf(HowlerRules.Skill.class), .5));
        assertEquals(StalkerRules.Skill.NONE, StalkerRules.choose(5, EnumSet.noneOf(StalkerRules.Skill.class), .5));
        assertEquals(BloaterRules.Skill.NONE, BloaterRules.choose(5, EnumSet.noneOf(BloaterRules.Skill.class), .5));
        assertEquals(MortarRules.Skill.NONE, MortarRules.choose(5, EnumSet.noneOf(MortarRules.Skill.class), .5));
        assertEquals(CrawlerRules.Skill.NONE, CrawlerRules.choose(5, EnumSet.noneOf(CrawlerRules.Skill.class), .5));
        assertEquals(GazerRules.Skill.NONE, GazerRules.choose(5, false, EnumSet.noneOf(GazerRules.Skill.class), .5));
        assertEquals(HivemotherRules.Skill.NONE, HivemotherRules.choose(5, EnumSet.noneOf(HivemotherRules.Skill.class), .5));
        assertEquals(DrifterRules.Skill.NONE, DrifterRules.choose(5, EnumSet.noneOf(DrifterRules.Skill.class), .5));
        assertEquals(MawRules.Skill.NONE, MawRules.choose(5, true, EnumSet.noneOf(MawRules.Skill.class), .5));
        assertEquals(NaiadRules.Skill.NONE, NaiadRules.choose(5, false, EnumSet.noneOf(NaiadRules.Skill.class), .5));
    }

    @Test void theBruteSlamsCloseAndHurlsFar() {
        Set<BruteRules.Skill> all = EnumSet.allOf(BruteRules.Skill.class);
        int near = outOfAHundred(roll -> BruteRules.choose(4, all, roll) == BruteRules.Skill.HURL);
        int far = outOfAHundred(roll -> BruteRules.choose(16, all, roll) == BruteRules.Skill.HURL);
        assertTrue(far > near, "it hurls the more at what is far: " + far + " against " + near);
        assertTrue(BruteRules.SLAM.damage() > BruteRules.ATTACK_DAMAGE, "its slam hits harder than its blow");
        assertTrue(BruteRules.HURL.lob(), "what it hurls is lobbed");
        assertTrue(BruteRules.KNOCKBACK_RESISTANCE >= .9, "and it is hardly moved");
    }

    @Test void theHowlersCallComesToNothingInTime() {
        assertTrue(HowlerRules.HOWL.passes(), "what it calls up crumbles away");
        assertEquals(600, HowlerRules.HOWL.lasts());
        assertTrue(HowlerRules.HOWL.most() >= HowlerRules.HOWL.count(), "a call is never more than it may have about");
        Set<HowlerRules.Skill> all = EnumSet.allOf(HowlerRules.Skill.class);
        assertTrue(outOfAHundred(roll -> HowlerRules.choose(12, all, roll) == HowlerRules.Skill.BILE)
                > outOfAHundred(roll -> HowlerRules.choose(4, all, roll) == HowlerRules.Skill.BILE), "it spits its bile the more from afar");
    }

    @Test void theStalkerFadesOnlyFromAfar() {
        Set<StalkerRules.Skill> all = EnumSet.allOf(StalkerRules.Skill.class);
        for (double roll = 0; roll <= 1; roll += .02)
            assertNotEquals(StalkerRules.Skill.FADE, StalkerRules.choose(StalkerRules.FADE_FROM - .5, all, roll), "too close to slip away");
        assertTrue(outOfAHundred(roll -> StalkerRules.choose(StalkerRules.FADE_FROM + 2, all, roll) == StalkerRules.Skill.FADE) > 0, "but does from afar");
        assertTrue(StalkerRules.FADE_TICKS > StalkerRules.FADE.ticks(), "faded for longer than it takes to fade");
    }

    @Test void theBloaterVentsOnlyAtWhatIsCloseAndBurstsTheWorseForDying() {
        Set<BloaterRules.Skill> all = EnumSet.allOf(BloaterRules.Skill.class);
        for (double roll = 0; roll <= 1; roll += .02)
            assertNotEquals(BloaterRules.Skill.VENT, BloaterRules.choose(BloaterRules.VENT_RANGE + 1, all, roll));
        assertTrue(outOfAHundred(roll -> BloaterRules.choose(3, all, roll) == BloaterRules.Skill.VENT) > 50, "close by, it vents the more");
        assertTrue(BloaterRules.BURST.radius() > BloaterRules.VENT_CLOUD.radius(), "its last burst spreads wider than its vent");
        assertTrue(BloaterRules.BURST.poisonTicks() >= BloaterRules.VENT_CLOUD.poisonTicks());
        assertTrue(BloaterRules.LOB.lob() && BloaterRules.LOB.pool().any(), "what it lobs leaves a pool");
    }

    @Test void theMortarKeepsItsDistanceAndReachesFurtherThanItKeeps() {
        assertEquals(-1, MortarRules.keep(MortarRules.NEAR - 1), "it backs away from what is close");
        assertEquals(0, MortarRules.keep((MortarRules.NEAR + MortarRules.FAR) / 2), "it holds where it is between");
        assertEquals(1, MortarRules.keep(MortarRules.FAR + 1), "it closes on what is far");
        assertTrue(MortarRules.BARRAGE.maxRange() > MortarRules.FAR, "its barrage reaches further than it keeps");
        assertTrue(MortarRules.SPIKES.maxRange() < MortarRules.NEAR, "its spikes are for what comes too close");
    }

    @Test void theCrawlerSpitsWhatItCannotLeapTo() {
        Set<CrawlerRules.Skill> all = EnumSet.allOf(CrawlerRules.Skill.class);
        assertTrue(outOfAHundred(roll -> CrawlerRules.choose(CrawlerRules.POUNCE.maxRange() + 4, all, roll) == CrawlerRules.Skill.VENOM)
                > outOfAHundred(roll -> CrawlerRules.choose(5, all, roll) == CrawlerRules.Skill.VENOM));
        assertTrue(CrawlerRules.HEIGHT < 1, "it is low enough to go where a player crawls");
    }

    @Test void theGazerDoesNotStareAtWhatIsSlowedAlready() {
        Set<GazerRules.Skill> all = EnumSet.allOf(GazerRules.Skill.class);
        for (double roll = 0; roll <= 1; roll += .02) assertNotEquals(GazerRules.Skill.GAZE, GazerRules.choose(8, true, all, roll));
        assertTrue(outOfAHundred(roll -> GazerRules.choose(8, false, all, roll) == GazerRules.Skill.GAZE) > 0);
        assertTrue(GazerRules.BEAM.maxRange() > GazerRules.KEEP_AT, "its beam reaches past where it keeps");
    }

    @Test void theHivemotherSwoopsAtWhatIsNearAndHerSwarmPasses() {
        Set<HivemotherRules.Skill> all = EnumSet.allOf(HivemotherRules.Skill.class);
        assertTrue(outOfAHundred(roll -> HivemotherRules.choose(2, all, roll) == HivemotherRules.Skill.SWOOP)
                > outOfAHundred(roll -> HivemotherRules.choose(12, all, roll) == HivemotherRules.Skill.SWOOP));
        assertTrue(HivemotherRules.SWARM.passes(), "her swarm crumbles away");
        assertTrue(HivemotherRules.STINGERS.shots() >= 5);
    }

    @Test void theDrifterLiftsAndRains() {
        for (double roll = 0; roll <= 1; roll += .02) {
            assertEquals(DrifterRules.Skill.LIFT, DrifterRules.choose(5, EnumSet.of(DrifterRules.Skill.LIFT), roll));
            assertEquals(DrifterRules.Skill.RAIN, DrifterRules.choose(5, EnumSet.of(DrifterRules.Skill.RAIN), roll));
        }
        assertTrue(DrifterRules.LIFT.reach() > DrifterRules.HOVER_HIGH, "its tendrils reach down further than it drifts high");
        assertEquals(0, DrifterRules.RAIN.minRange(), "it rains on what is right below it");
    }

    @Test void theMawWhirlsOnlyInTheWaterAndPullsTheHarderTheNearer() {
        Set<MawRules.Skill> all = EnumSet.allOf(MawRules.Skill.class);
        for (double roll = 0; roll <= 1; roll += .02) {
            assertNotEquals(MawRules.Skill.WHIRLPOOL, MawRules.choose(5, false, all, roll), "no whirlpool out of the water");
            assertNotEquals(MawRules.Skill.WHIRLPOOL, MawRules.choose(MawRules.WHIRL_RADIUS + 1, true, all, roll), "nor beyond its reach");
        }
        assertTrue(outOfAHundred(roll -> MawRules.choose(5, true, all, roll) == MawRules.Skill.WHIRLPOOL) > 0);
        assertEquals(0, MawRules.pull(MawRules.WHIRL_RADIUS + .1), 1e-9);
        assertTrue(MawRules.pull(1) > MawRules.pull(8), "the nearer the harder");
        assertTrue(MawRules.pull(MawRules.WHIRL_RADIUS) > 0, "and something even at its edge");
        assertTrue(MawRules.DART > .3, "it darts far faster than a player swims");
    }

    @Test void theNaiadDoesNotSingToWhatIsSlowedAlready() {
        Set<NaiadRules.Skill> all = EnumSet.allOf(NaiadRules.Skill.class);
        for (double roll = 0; roll <= 1; roll += .02) assertNotEquals(NaiadRules.Skill.SONG, NaiadRules.choose(6, true, all, roll));
        assertTrue(outOfAHundred(roll -> NaiadRules.choose(6, false, all, roll) == NaiadRules.Skill.SONG) > 0);
        assertTrue(outOfAHundred(roll -> NaiadRules.choose(16, false, all, roll) == NaiadRules.Skill.SPEAR)
                > outOfAHundred(roll -> NaiadRules.choose(4, false, all, roll) == NaiadRules.Skill.SPEAR), "it throws its spear the more from afar");
        assertTrue(NaiadRules.SPEAR.push() > 0, "its spear throws back what it strikes");
    }

    @Test void summonsStayUnlessTheyAreToldHowLongTheyLast() {
        SkillTiming timing = new SkillTiming(10, 20, 100);
        assertFalse(new SummonRules(timing, 2, 4, 3, List.of("minecraft:zombie")).passes(), "a lord's call stays");
        assertFalse(new SummonRules(timing, 2, 4, 3, List.of("minecraft:zombie"), true).passes());
        assertEquals(0, new SummonRules(timing, 2, 4, 3, List.of("minecraft:zombie"), false, -5).lasts(), "never less than none");
        assertTrue(new SummonRules(timing, 2, 4, 3, List.of("minecraft:zombie"), false, 200).passes());
    }
}
