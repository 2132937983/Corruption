package com.miophas.corruption.core.energy;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.energy.CorruptionEnergyUnit;
import org.junit.jupiter.api.Test;

class CorruptionEnergyTest {
    @Test void aKceIsAThousandCe() {
        assertEquals(1000, CorruptionEnergy.KCE);
        assertEquals(100, CorruptionEnergy.kilo(.1));
        assertEquals(100_000, ArcaneTransmuterRules.CAPACITY, "the transmuter holds 100 KCE");
    }

    @Test void amountsAreReadWithOrWithoutTheirUnit() {
        assertEquals(100, CorruptionEnergy.parse("0.1KCE"));
        assertEquals(20_000, CorruptionEnergy.parse("20KCE"));
        assertEquals(20_000, CorruptionEnergy.parse(" 20 kce "));
        assertEquals(250, CorruptionEnergy.parse("250"));
        assertEquals(250, CorruptionEnergy.parse("250CE"));
        assertEquals(1, CorruptionEnergy.parse("0.001KCE"));
        for (String bad : new String[]{"", "KCE", "ten", "-5", "-0.1KCE", "0.0005KCE", "1.5CE", "12XCE", "MCE", "1.5 M CE"})
            assertThrows(IllegalArgumentException.class, () -> CorruptionEnergy.parse(bad), bad);
    }

    @Test void eachUnitIsAThousandOfTheOneBefore() {
        CorruptionEnergyUnit[] units = CorruptionEnergyUnit.values();
        assertEquals(1, units[0].ce());
        for (int unit = 1; unit < units.length; unit++) assertEquals(units[unit - 1].ce() * 1000, units[unit].ce(), units[unit].symbol());
        assertEquals("CE KCE MCE BCE TCE QaCE QiCE", String.join(" ", java.util.Arrays.stream(units).map(CorruptionEnergyUnit::symbol).toList()));
        assertEquals(1_000_000, CorruptionEnergy.MCE);
        assertEquals(1_000_000_000, CorruptionEnergy.BCE);
        assertTrue(Long.MAX_VALUE / CorruptionEnergy.QICE < 1000, "QiCE is the last unit a long can count a thousand of");
        assertEquals(1_500_000, CorruptionEnergy.of(1.5, CorruptionEnergyUnit.MCE));
        assertEquals(2_000_000_000_000L, CorruptionEnergy.of(2, CorruptionEnergyUnit.TCE));
        assertEquals(1, CorruptionEnergy.of(.0000006, CorruptionEnergyUnit.MCE), "to the nearest CE");
        assertThrows(IllegalArgumentException.class, () -> CorruptionEnergy.of(10, CorruptionEnergyUnit.QICE), "more than a long counts");
    }

    @Test void everyUnitIsReadInAnyCase() {
        assertEquals(12_000_000, CorruptionEnergy.parse("12MCE"));
        assertEquals(1_500_000, CorruptionEnergy.parse("1.5 mce"));
        assertEquals(1, CorruptionEnergy.parse("0.000001MCE"), "as many decimals as come to whole CE");
        assertEquals(2_500_000_000L, CorruptionEnergy.parse("2.5BCE"));
        assertEquals(7_000_000_000_000L, CorruptionEnergy.parse("7 TCE"));
        assertEquals(3_000_000_000_000_000L, CorruptionEnergy.parse("3QaCE"));
        assertEquals(3_000_000_000_000_000L, CorruptionEnergy.parse("3 QACE"));
        assertEquals(9_000_000_000_000_000_000L, CorruptionEnergy.parse("9qice"));
        assertEquals(Long.MAX_VALUE, CorruptionEnergy.parse("9.223372036854775807QiCE"));
        for (String bad : new String[]{"10QiCE", "9.3QiCE", "0.0000001MCE", "1.5QCE"})
            assertThrows(IllegalArgumentException.class, () -> CorruptionEnergy.parse(bad), bad);
    }

    @Test void amountsAreWrittenInTheLargestUnitTheyFill() {
        assertEquals(CorruptionEnergyUnit.KCE, CorruptionEnergy.unit(0), "nothing smaller than a KCE");
        assertEquals(CorruptionEnergyUnit.KCE, CorruptionEnergy.unit(999_999));
        assertEquals(CorruptionEnergyUnit.MCE, CorruptionEnergy.unit(1_000_000));
        assertEquals("999.999 KCE", CorruptionEnergy.format(999_999));
        assertEquals("1 MCE", CorruptionEnergy.format(1_000_000));
        assertEquals("1.234567 MCE", CorruptionEnergy.format(1_234_567), "exact, never rounded");
        assertEquals("2.5 BCE", CorruptionEnergy.format(2_500_000_000L));
        assertEquals("40 TCE", CorruptionEnergy.format(40_000_000_000_000L));
        assertEquals("1 QaCE", CorruptionEnergy.format(CorruptionEnergy.QACE));
        assertEquals("9.223372036854775807 QiCE", CorruptionEnergy.format(Long.MAX_VALUE));
        assertEquals("0.05 MCE", CorruptionEnergy.format(50_000, CorruptionEnergyUnit.MCE), "or in a unit of one's choosing");
        assertEquals("50", CorruptionEnergy.text(50_000, CorruptionEnergyUnit.KCE));
        for (long ce : new long[]{0, 1, 999_999, 1_000_000, 1_234_567, 2_500_000_001L, 40_000_000_000_000L, CorruptionEnergy.QACE + 1, Long.MAX_VALUE})
            assertEquals(ce, CorruptionEnergy.parse(CorruptionEnergy.format(ce)), "reads back what it writes: " + ce);
    }

    @Test void amountsUnderAMillionAreWrittenInKce() {
        assertEquals("0.1 KCE", CorruptionEnergy.format(100));
        assertEquals("20 KCE", CorruptionEnergy.format(20_000));
        assertEquals("100 KCE", CorruptionEnergy.format(100_000));
        assertEquals("45.3 KCE", CorruptionEnergy.format(45_300));
        assertEquals("0.001 KCE", CorruptionEnergy.format(1));
        assertEquals("0", CorruptionEnergy.kiloText(0));
        for (long ce : new long[]{0, 1, 99, 100, 1234, 20_000, 99_999}) assertEquals(ce, CorruptionEnergy.parse(CorruptionEnergy.format(ce)), "reads back what it writes");
    }

    @Test void fuelOnlyBurnsWhenAllOfItFits() {
        assertTrue(ArcaneTransmuterRules.fits(0, 100));
        assertTrue(ArcaneTransmuterRules.fits(99_900, 100));
        assertFalse(ArcaneTransmuterRules.fits(99_901, 100));
        assertFalse(ArcaneTransmuterRules.fits(0, -1));
    }

    @Test void workIsPaidForInSharesThatAddUpExactly() {
        for (long total : new long[]{0, 1, 7, 100, 20_000, 99_999})
            for (int ticks : new int[]{1, 3, 10, 200}) {
                long sum = 0;
                for (int tick = 0; tick < ticks; tick++) {
                    long share = ArcaneTransmuterRules.share(total, tick, ticks);
                    assertTrue(share >= 0);
                    sum += share;
                    assertEquals(sum, ArcaneTransmuterRules.paid(total, ticks, tick + 1), "paid so far");
                }
                assertEquals(total, sum, total + " over " + ticks);
                assertEquals(0, ArcaneTransmuterRules.share(total, ticks, ticks), "nothing after the end");
            }
        assertEquals(100, ArcaneTransmuterRules.share(20_000, 0, 200), "a transmutation draws 20 KCE at 0.1 KCE a tick");
        assertEquals(10, ArcaneTransmuterRules.share(100, 3, 10), "Corruption Powder pays 0.1 KCE over 10 ticks");
        assertEquals(.5F, ArcaneTransmuterRules.fraction(50_000, 100_000));
        assertEquals(0, ArcaneTransmuterRules.fraction(5, 0));
    }
}
