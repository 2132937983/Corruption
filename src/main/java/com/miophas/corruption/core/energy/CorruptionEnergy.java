package com.miophas.corruption.core.energy;

import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.api.energy.CorruptionEnergyUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

/**
 * Corruption Energy, CE: the mod's own unit of energy, stored and spent in whole CE. Larger amounts are counted
 * in thousands: a KCE is a thousand CE, an MCE a million, a BCE a billion, then TCE, QaCE and QiCE (see
 * {@link CorruptionEnergyUnit}). The KCE is the size people usually speak in: the arcane transmuter holds
 * 100 KCE. With {@link EnergyBuffer} (holding it), {@link CarriedEnergy} (items carrying it),
 * {@link EnergyAcceptance} (taking in what items carry) and {@link ArcaneTransmuterRules} (paying it in and out
 * over time), this is the whole of the energy system's rules; api.energy is how the rest of the mod, and
 * add-ons, use them.
 */
public final class CorruptionEnergy {
    public static final long CE = CorruptionEnergyUnit.CE.ce(), KCE = CorruptionEnergyUnit.KCE.ce(), MCE = CorruptionEnergyUnit.MCE.ce(),
            BCE = CorruptionEnergyUnit.BCE.ce(), TCE = CorruptionEnergyUnit.TCE.ce(), QACE = CorruptionEnergyUnit.QACE.ce(), QICE = CorruptionEnergyUnit.QICE.ce();
    /** The unit amounts are written in when nothing larger fits: the size people speak in. */
    public static final CorruptionEnergyUnit SMALLEST_SHOWN = CorruptionEnergyUnit.KCE;
    /** The units with the longest symbols first, so that reading "3QaCE" finds QaCE before it finds CE. */
    private static final CorruptionEnergyUnit[] LONGEST_FIRST = Arrays.stream(CorruptionEnergyUnit.values())
            .sorted(Comparator.comparingInt((CorruptionEnergyUnit unit) -> unit.symbol().length()).reversed()).toArray(CorruptionEnergyUnit[]::new);

    /** This many KCE, in CE. */
    public static long kilo(double kce) { return of(kce, CorruptionEnergyUnit.KCE); }

    /**
     * This much of a unit, in CE, to the nearest CE: 1.5 MCE is 1 500 000.
     *
     * @throws IllegalArgumentException for more than a long can count
     */
    public static long of(double amount, CorruptionEnergyUnit unit) {
        try { return BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(unit.ce())).setScale(0, RoundingMode.HALF_UP).longValueExact(); }
        catch (ArithmeticException tooMuch) { throw new IllegalArgumentException("More Corruption Energy than can be counted: " + amount + " " + unit.symbol(), tooMuch); }
    }

    /**
     * Reads an amount as data files write it: plain CE ("250"), or with any unit ("250CE", "0.1KCE", "20 KCE",
     * "1.5MCE", "2 bce", "3QaCE"), in any case. A unit may have as many decimals as come to whole CE: three
     * for KCE, six for MCE, and so on.
     *
     * @throws IllegalArgumentException for anything else, a negative amount, part of a CE, or more than a long can count
     */
    public static long parse(String text) {
        String amount = text.trim().toUpperCase(Locale.ROOT);
        CorruptionEnergyUnit unit = CorruptionEnergyUnit.CE;
        for (CorruptionEnergyUnit candidate : LONGEST_FIRST) {
            String symbol = candidate.symbol().toUpperCase(Locale.ROOT);
            if (amount.endsWith(symbol)) { unit = candidate; amount = amount.substring(0, amount.length() - symbol.length()); break; }
        }
        try {
            BigDecimal value = new BigDecimal(amount.trim()).multiply(BigDecimal.valueOf(unit.ce()));
            if (value.signum() < 0) throw new IllegalArgumentException("Corruption Energy cannot be negative: " + text);
            return value.longValueExact();
        } catch (NumberFormatException | ArithmeticException notAnAmount) {
            throw new IllegalArgumentException("Not an amount of Corruption Energy: " + text, notAnAmount);
        }
    }

    /**
     * The unit an amount reads best in: the largest it makes at least one of, but nothing smaller than a KCE.
     * 100 CE is 0.1 KCE, 45 300 CE is 45.3 KCE, 2 500 000 CE is 2.5 MCE.
     */
    public static CorruptionEnergyUnit unit(long ce) {
        CorruptionEnergyUnit best = SMALLEST_SHOWN;
        for (CorruptionEnergyUnit unit : CorruptionEnergyUnit.values()) if (unit.ce() > best.ce() && ce >= unit.ce()) best = unit;
        return best;
    }

    /** An amount in a unit, without the unit, exactly and with no more decimals than it needs: 1 500 000 CE is "1.5" MCE. */
    public static String text(long ce, CorruptionEnergyUnit unit) {
        return BigDecimal.valueOf(ce).divide(BigDecimal.valueOf(unit.ce())).stripTrailingZeros().toPlainString();
    }

    /** An amount in KCE without the unit: 100 CE is "0.1", 45300 CE "45.3". */
    public static String kiloText(long ce) { return text(ce, CorruptionEnergyUnit.KCE); }

    /** An amount with its unit, in the unit it reads best in: "0.1 KCE", "20 KCE", "2.5 MCE". Exact, so it reads back as the same amount. */
    public static String format(long ce) { return format(ce, unit(ce)); }

    /** An amount in a given unit: for amounts side by side, such as "50 / 100 KCE", written in the one unit. */
    public static String format(long ce, CorruptionEnergyUnit unit) { return text(ce, unit) + " " + unit.symbol(); }

    /**
     * Moves up to this much from one store to another: only what the one will give and the other will take,
     * both asked first without doing anything. Answers how much moved. Should the receiver take less than it
     * said, the rest is put back where it came from, if that store takes energy in.
     */
    public static long move(CorruptionEnergyStorage from, CorruptionEnergyStorage to, long most) {
        if (most <= 0 || !from.canExtract() || !to.canInsert()) return 0;
        long agreed = to.insert(from.extract(most, true), true);
        if (agreed <= 0) return 0;
        long taken = from.extract(agreed, false), given = to.insert(taken, false);
        if (given < taken) from.insert(taken - given, false);
        return given;
    }

    private CorruptionEnergy() {}
}
