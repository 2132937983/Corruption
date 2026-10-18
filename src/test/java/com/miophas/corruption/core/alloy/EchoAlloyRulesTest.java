package com.miophas.corruption.core.alloy;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.corruption.CurseWard;
import com.miophas.corruption.core.energy.EnergyDeviceRules;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * The corruption echo alloy's gear and the devices added with it, as rules: its tools past netherite's, its armour
 * netherite's equal with the curse turned aside, all of it by the whole set; the lantern's and the injector's charge.
 */
class EchoAlloyRulesTest {
    @Test void itsToolsStrikeHarderLastLongerAndDigFasterThanNetherites() {
        assertEquals(9, EchoAlloyRules.damage(EchoAlloyRules.SWORD_DAMAGE), 1e-6, "the sword: netherite's strikes 8");
        assertEquals(7, EchoAlloyRules.damage(EchoAlloyRules.PICKAXE_DAMAGE), 1e-6, "the pickaxe: netherite's 6");
        assertEquals(11, EchoAlloyRules.damage(EchoAlloyRules.AXE_DAMAGE), 1e-6, "the axe: netherite's 10");
        assertEquals(1.6, EchoAlloyRules.swings(EchoAlloyRules.SWORD_SWING), 1e-6, "at a sword's pace");
        assertTrue(EchoAlloyRules.TOOL_DURABILITY > 2031, "longer-lasting than netherite");
        assertTrue(EchoAlloyRules.TOOL_SPEED > 9, "and quicker to dig");
    }

    @Test void itsArmourGuardsAsNetheritesAndTheWholeSetTurnsAsideAllOfTheCurse() {
        assertArrayEquals(new int[]{3, 8, 6, 3}, EchoAlloyRules.DEFENCE, "netherite's defence");
        assertEquals(3, EchoAlloyRules.TOUGHNESS, 1e-6);
        double sum = 0;
        for (double share : EchoAlloyRules.CURSE_SHARE) {
            assertTrue(share > 0 && share < 1, "each piece some, none all");
            sum += share;
        }
        assertEquals(1, sum, 1e-9, "the set, all of it");
        assertEquals(EchoAlloyRules.CURSE_SHARE[1], EchoAlloyRules.CURSE_SHARE[EchoAlloyRules.piece("chestplate")]);
        assertThrows(IllegalArgumentException.class, () -> EchoAlloyRules.piece("body"));
    }

    @Test void aCurseWardTurnsAsideItsShareAndTogetherNeverMoreThanAll() {
        List<CurseWard> set = Stream.of(.15, .4, .3, .15).map(CurseWard::new).toList();
        assertEquals(1, CurseWard.total(set), 1e-9);
        assertEquals(0, CurseWard.taken(3, CurseWard.total(set)), 1e-6, "the whole set: none of a pulse gets through");
        assertEquals(1.8F, CurseWard.taken(3, .4), 1e-6, "a chestplate alone: three fifths");
        assertEquals(3, CurseWard.taken(3, 0), 1e-6, "nothing worn: all");
        assertEquals(1, CurseWard.total(List.of(new CurseWard(.8), new CurseWard(.8))), 1e-9, "never past all");
        assertEquals(0, new CurseWard(-1).share(), "never less than none");
        assertEquals(0, CurseWard.taken(-2, 0), 1e-6);
    }

    @Test void theLanternBurnsForMinutesAndTheInjectorDosesFiveTimesAFullCharge() {
        EnergyDeviceRules.Lantern lantern = EnergyDeviceRules.LANTERN;
        assertEquals(800, lantern.tool().capacity() / lantern.cost() * lantern.every() / 20, "800 seconds to a full lantern");
        assertTrue(lantern.sight() > 220, "its sight outlasts the flicker");
        assertFalse(EnergyDeviceRules.renewSight(lantern.sight(), lantern), "not renewed while fresh");
        assertTrue(EnergyDeviceRules.renewSight(219, lantern), "renewed before it flickers");
        assertEquals(5, EnergyDeviceRules.uses(EnergyDeviceRules.INJECTOR.tool(), EnergyDeviceRules.INJECTOR.cost()));
        assertTrue(EnergyDeviceRules.affords(6000, 6000));
        assertFalse(EnergyDeviceRules.affords(5999, 6000));
        assertEquals(5999, EnergyDeviceRules.paid(5999, 6000), "what cannot pay keeps what it has");
        assertEquals(1000, EnergyDeviceRules.paid(7000, 6000));
    }

    @Test void theRulesAreGameFree() throws Exception {
        for (String folder : List.of("core/alloy")) {
            Path at = null;
            for (Path up = Path.of("").toAbsolutePath(); up != null && at == null; up = up.getParent())
                if (Files.isDirectory(up.resolve("src/main/java/com/miophas/corruption/" + folder))) at = up.resolve("src/main/java/com/miophas/corruption/" + folder);
            assertNotNull(at, folder + " is there");
            try (Stream<Path> files = Files.list(at)) {
                for (Path file : files.toList())
                    for (String line : Files.readAllLines(file))
                        if (line.startsWith("import ")) assertTrue(line.startsWith("import java."), folder + " is plain Java: " + line);
            }
        }
    }
}
