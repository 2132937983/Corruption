package com.miophas.corruption.core.demon;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.demon.SolomonRules.Edict;
import com.miophas.corruption.core.demon.SolomonRules.Wyrm;
import com.miophas.corruption.core.demon.skill.BurrowRules;
import com.miophas.corruption.core.demon.skill.DrakeRushRules;
import com.miophas.corruption.core.demon.skill.EliteCallRules;
import com.miophas.corruption.core.demon.skill.FireballRules;
import com.miophas.corruption.core.demon.skill.HandRules;
import com.miophas.corruption.core.demon.skill.RamRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.VenusRules;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Solomon without a game: his dragons' bodies, his numbers, what he and his dragons choose, and the rules of what is new in them. */
class SolomonRulesTest {
    private static double distance(WyrmChain chain, int a, int b) {
        return Math.sqrt(Math.pow(chain.x(a) - chain.x(b), 2) + Math.pow(chain.y(a) - chain.y(b), 2) + Math.pow(chain.z(a) - chain.z(b), 2));
    }

    @Test void aDragonIsEightOfThemFourChunksLongEachAndOnlyThreeAtWorkAtOnce() {
        assertEquals(8, SolomonRules.DRAGONS);
        assertEquals(64, SolomonRules.LENGTH, 1e-9, "four chunks from snout to tail");
        assertEquals(8, new HashSet<>(List.of(SolomonRules.ACCENTS[0], SolomonRules.ACCENTS[1], SolomonRules.ACCENTS[2], SolomonRules.ACCENTS[3],
                SolomonRules.ACCENTS[4], SolomonRules.ACCENTS[5], SolomonRules.ACCENTS[6], SolomonRules.ACCENTS[7])).size(), "a light of its own each");
        assertEquals(12, Set.copyOf(SolomonRules.WYRM_SKILLS).size());
        assertFalse(SolomonRules.WYRM_SKILLS.contains(Wyrm.NONE));
        assertEquals(8, Set.copyOf(SolomonRules.EDICTS).size());
        assertFalse(SolomonRules.EDICTS.contains(Edict.NONE));
        for (int casting = 0; casting < 3; casting++) assertTrue(SolomonRules.mayCast(casting), casting + " at work");
        assertFalse(SolomonRules.mayCast(3), "never a fourth");
        assertTrue(SolomonRules.BEAM.damage() > 5, "a beam harder than Dantalion's or Seere's");
        assertTrue(SolomonRules.DELUGE.count() > 64 * 2, "more bolts than Decarabia's barrage, by far");
    }

    @Test void itsBodyFollowsItsHeadAndKeepsItsLength() {
        WyrmChain chain = new WyrmChain(31, 2, 0, 64, 0);
        assertEquals(30, chain.links());
        assertEquals(60, chain.length(), 1e-9);
        assertEquals(0, distance(chain, 0, 30), 1e-9, "coiled up where its head is");
        chain.follow(0, 64, 1);
        assertEquals(1, chain.z(0), 1e-9);
        assertEquals(0, chain.z(1), 1e-9, "a link lying within reach is left where it is: it unwinds a link at a time");
        // Straight south 80 blocks: laid out straight behind, each joint the spacing from the next.
        for (int step = 2; step <= 80; step++) chain.follow(0, 64, step);
        for (int joint = 1; joint < chain.joints(); joint++) assertEquals(2, distance(chain, joint - 1, joint), 1e-9, "joint " + joint);
        assertEquals(60, distance(chain, 0, 30), 1e-6, "its full length behind its head");
        double[] facing = chain.facing(0);
        assertEquals(0, facing[0], 1e-9, "going south: yaw 0");
        assertEquals(0, facing[1], 1e-9);
        // Then west: its body turns the corner after its head, the joints behind the corner still on the old line.
        for (int step = 1; step <= 20; step++) chain.follow(-step, 64, 80);
        assertEquals(90, chain.facing(0)[0], .1, "going west: yaw 90 (a body drawn round a corner straightens as it goes)");
        assertEquals(0, chain.x(chain.joints() - 1), 1e-3, "its tail still on the way south");
        assertTrue(chain.z(chain.joints() - 1) < 80);
        // Then up: it looks up.
        for (int step = 1; step <= 10; step++) chain.follow(-20, 64 + step, 80);
        assertTrue(chain.facing(0)[1] < -80, "climbing: pitched up");
        assertEquals(1, chain.taper(0, .4), 1e-9);
        assertEquals(.4, chain.taper(29, .4), 1e-9);
        WyrmChain copy = new WyrmChain(31, 2, 0, 0, 0);
        copy.load(chain.save());
        for (int joint = 0; joint < chain.joints(); joint++) assertEquals(chain.y(joint), copy.y(joint), 1e-12);
        copy.load(new double[3]);
        assertEquals(chain.y(5), copy.y(5), 1e-12, "a body of another length is not taken");
    }

    @Test void eachDragonCirclesOnARingOfItsOwn() {
        Set<Long> angles = new HashSet<>();
        for (int dragon = 0; dragon < SolomonRules.DRAGONS; dragon++) {
            double[] at = SolomonRules.orbit(dragon, 0);
            double out = Math.hypot(at[0], at[2]);
            assertTrue(out >= 16 && out <= 28, dragon + " out, near enough to strike from: " + out);
            assertTrue(at[1] >= 6 && at[1] <= 19, dragon + " up, low enough to strike from: " + at[1]);
            angles.add(Math.round(Math.toDegrees(Math.atan2(at[2], at[0]))));
            double[] later = SolomonRules.orbit(dragon, 150);
            assertTrue(Math.hypot(at[0] - later[0], at[2] - later[2]) > 10, "going round");
        }
        assertEquals(8, angles.size(), "spread round");
    }

    @Test void aDragonBlessesItsHurtKinFirstAndStrikesUnseen() {
        Set<Wyrm> all = EnumSet.copyOf(SolomonRules.WYRM_SKILLS);
        assertEquals(Wyrm.VENUS, SolomonRules.chooseWyrm(new SolomonRules.WyrmSituation(20, true, .5, 10, all), .9), "hurt kin: Venus first");
        Set<Wyrm> seen = EnumSet.noneOf(Wyrm.class), unseen = EnumSet.noneOf(Wyrm.class);
        for (int roll = 0; roll < 200; roll++) {
            seen.add(SolomonRules.chooseWyrm(new SolomonRules.WyrmSituation(20, true, 1, 10, all), roll / 200.0));
            unseen.add(SolomonRules.chooseWyrm(new SolomonRules.WyrmSituation(20, false, 1, 10, all), roll / 200.0));
        }
        assertEquals(EnumSet.of(Wyrm.BEAM, Wyrm.DELUGE, Wyrm.FIREBALLS, Wyrm.BURROW, Wyrm.RAM, Wyrm.STORM, Wyrm.HANDS, Wyrm.SKY_BEAM, Wyrm.PENTAGRAM,
                Wyrm.HEXAGRAM, Wyrm.OCTAGRAM), seen, "all but Venus, unhurt");
        assertEquals(EnumSet.of(Wyrm.BURROW, Wyrm.STORM, Wyrm.HANDS, Wyrm.SKY_BEAM, Wyrm.PENTAGRAM, Wyrm.HEXAGRAM, Wyrm.OCTAGRAM), unseen,
                "unseen, it burrows, or strikes from the sky or the ground");
        assertEquals(Wyrm.NONE, SolomonRules.chooseWyrm(new SolomonRules.WyrmSituation(20, true, 1, 10, EnumSet.noneOf(Wyrm.class)), .5));
        assertEquals(Wyrm.NONE, SolomonRules.chooseWyrm(new SolomonRules.WyrmSituation(200, true, 1, 10, all), .5), "nothing reaches so far");
    }

    @Test void hisSealDecreesWhatServes() {
        Set<Edict> all = EnumSet.copyOf(SolomonRules.EDICTS);
        Set<Edict> grounded = EnumSet.noneOf(Edict.class), flying = EnumSet.noneOf(Edict.class), full = EnumSet.noneOf(Edict.class);
        for (int roll = 0; roll < 300; roll++) {
            grounded.add(SolomonRules.chooseEdict(new SolomonRules.EdictSituation(40, true, 0, 0, true, all), roll / 300.0));
            flying.add(SolomonRules.chooseEdict(new SolomonRules.EdictSituation(40, false, 0, 0, false, all), roll / 300.0));
            full.add(SolomonRules.chooseEdict(new SolomonRules.EdictSituation(40, true, 6, 16, true, all), roll / 300.0));
        }
        assertEquals(all, grounded, "all eight serve against a quarry on the ground in sight");
        assertEquals(EnumSet.of(Edict.ELITES, Edict.REMNANTS, Edict.STUN, Edict.DRAKES), flying, "out of sight and off the ground");
        assertFalse(full.contains(Edict.ELITES) || full.contains(Edict.REMNANTS), "no room for more elites or remnants");
    }

    @Test void betweenItsThingsADragonSwoopsAndItsBodyHurts() {
        var swoop = SolomonRules.SWOOP;
        assertTrue(swoop.inRange(64) && !swoop.inRange(65));
        assertTrue(SolomonRules.maySwoop(0) && SolomonRules.maySwoop(1) && !SolomonRules.maySwoop(2), "no more than two at once");
        assertEquals(swoop.restLeast(), swoop.rest(0));
        assertEquals(swoop.restMost(), swoop.rest(.999999));
        assertTrue(swoop.done(1.9, 5), "its head at its quarry");
        assertTrue(swoop.done(5, -1), "gone by it");
        assertFalse(swoop.done(30, -1), "behind it, but far off: it turns back for it");
        assertFalse(swoop.done(10, 8), "still coming");
        assertTrue(swoop.speed() > SolomonRules.CRUISE, "faster than it cruises");
        assertTrue(SolomonRules.HEAD_CONTACT > SolomonRules.BODY_CONTACT && SolomonRules.BODY_CONTACT > 0);
    }

    @Test void theNewSkillsRules() {
        BurrowRules burrow = SolomonRules.BURROW;
        assertArrayEquals(new double[]{10, -5}, burrow.entry(30, -15), 1e-9, "a third of the way");
        assertTrue(burrow.under(1) && !burrow.under(5));
        assertTrue(burrow.catches(4, 1) && !burrow.catches(5, 1) && !burrow.catches(1, -3));

        RamRules ram = SolomonRules.RAM;
        double[] past = ram.past(10, -5, 0);
        assertEquals(10 * ram.through() / Math.sqrt(125), past[0], 1e-9, "on along the line it came");
        assertEquals(0, past[2], 1e-9);
        assertEquals(-ram.depth(), past[1], 1e-9, "and down into the ground at the least");
        assertTrue(ram.past(10, 20, 0)[1] <= -ram.depth(), "even rising, it goes down past its mark");
        double[] outlet = ram.outlet(-3, 4);
        assertEquals(ram.away(), Math.hypot(outlet[0], outlet[1]), 1e-9);
        assertTrue(outlet[0] < 0 && outlet[1] > 0, "on the way it came, beyond the mark");
        assertEquals(ram.damage() / 2, ram.burstDamage(), 1e-9);

        VenusRules venus = SolomonRules.VENUS;
        assertTrue(venus.wanted(.7) && !venus.wanted(.8));
        assertEquals(80, venus.healed(400), 1e-9, "a fifth of its health");

        FireballRules fireballs = SolomonRules.FIREBALLS;
        int[] shots = new int[fireballs.count()];
        int loosed = 0;
        for (int tick = 0; tick < fireballs.timing().ticks(); tick++) if (fireballs.shot(tick) >= 0) shots[loosed++] = tick;
        assertEquals(fireballs.count(), loosed);
        assertEquals(fireballs.timing().windup(), shots[0]);
        assertEquals(fireballs.every(), shots[1] - shots[0]);
        assertEquals(fireballs.damage(), fireballs.at(0), 1e-9);
        assertEquals(fireballs.damage() / 2, fireballs.at(fireballs.radius()), 1e-9, "half at its edge");
        assertEquals(0, fireballs.at(fireballs.radius() + .1), 1e-9);
        assertEquals(fireballs.lead(), fireballs.leading(1000));

        HandRules hands = SolomonRules.HANDS;
        assertArrayEquals(new double[]{0, 0}, hands.place(0, 1), 1e-9, "the first under its quarry");
        for (int index = 1; index < hands.hands(); index++) assertEquals(hands.spread(), Math.hypot(hands.place(index, .3)[0], hands.place(index, .3)[1]), 1e-9);
        assertEquals(0, hands.grown(0), 1e-9);
        assertEquals(1, hands.grown(hands.rise()), 1e-9);
        assertEquals(1, hands.reared(hands.rise() + hands.hold()), 1e-9, "fully reared");
        assertEquals(0, hands.reared(hands.impact()), 1e-9, "flat as it falls");
        assertEquals(0, hands.grown(hands.lasts()), 1e-9, "sunk away at the end");

        DrakeRushRules drakes = SolomonRules.DRAKES;
        double[] north = drakes.start(0), east = drakes.start(2);
        assertEquals(-drakes.distance(), north[2], 1e-9, "the first from the north");
        assertEquals(drakes.distance(), east[0], 1e-9, "clockwise: the third from the east");
        assertEquals(drakes.height(), north[1], 1e-9);
        assertTrue(drakes.flight() * drakes.speed() >= 2 * drakes.distance(), "through the mark and the same beyond");

        EliteCallRules elites = SolomonRules.ELITES;
        assertEquals(4, elites.count(0, .2));
        assertEquals(3, elites.count(0, .8));
        assertEquals(1, elites.count(5, .2), "never more than six about");
        assertFalse(elites.wanted(6));
        assertEquals(elites.radius(), Math.hypot(elites.place(2, 4, .5)[0], elites.place(2, 4, .5)[1]), 1e-9);
    }

    private static Path source(String folder) {
        for (Path at = Path.of("").toAbsolutePath(); at != null; at = at.getParent())
            if (Files.isDirectory(at.resolve(folder))) return at.resolve(folder);
        throw new AssertionError("Cannot find " + folder);
    }

    @Test void theRulesAreGameFree() throws Exception {
        for (String folder : List.of("core/demon", "core/demon/skill")) {
            Path dir = source("src/main/java/com/miophas/corruption/" + folder);
            try (Stream<Path> files = Files.list(dir)) {
                for (Path file : files.filter(f -> f.toString().endsWith(".java")).toList())
                    for (String line : Files.readAllLines(file))
                        if (line.startsWith("import "))
                            assertTrue(line.startsWith("import java.") || line.startsWith("import com.miophas.corruption.core."), file + ": " + line);
            }
        }
        assertNotNull(new SkillTiming(1, 2, 3));
    }
}
