package com.miophas.corruption.core.energy;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.energy.CorruptionEnergyAcceptance;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

/** The rules for items carrying energy and for taking it in, without a game: the stores here are plain buffers. */
class EnergyAcceptanceTest {
    private static EnergyBuffer store(long capacity, long energy) {
        EnergyBuffer buffer = new EnergyBuffer(capacity, Long.MAX_VALUE, Long.MAX_VALUE, null);
        buffer.setEnergy(energy);
        return buffer;
    }
    /** One item of a kind that stays once empty, carrying what the holder says. */
    private static CarriedEnergy item(long capacity, AtomicLong carried) { return new CarriedEnergy(capacity, false, 1, carried::get, carried::set, () -> {}); }
    /** A stack of candles, used up once empty: each carries what the holder says, and the count goes down as one burns away. */
    private static CarriedEnergy candles(AtomicInteger count, AtomicLong each) {
        return new CarriedEnergy(10_000, true, count.get(), each::get, each::set, count::decrementAndGet);
    }

    @Test void partOfAChargeFitsWhereOnlyAPartWouldWholeOnesOnlyWhereAllWould() {
        assertEquals(3_000, EnergyAcceptance.accepted(10_000, 3_000, CorruptionEnergyAcceptance.PARTIAL));
        assertEquals(10_000, EnergyAcceptance.accepted(10_000, 50_000, CorruptionEnergyAcceptance.PARTIAL));
        assertEquals(0, EnergyAcceptance.accepted(10_000, 9_999, CorruptionEnergyAcceptance.WHOLE));
        assertEquals(10_000, EnergyAcceptance.accepted(10_000, 10_000, CorruptionEnergyAcceptance.WHOLE));
        for (CorruptionEnergyAcceptance acceptance : CorruptionEnergyAcceptance.values()) {
            assertEquals(0, EnergyAcceptance.accepted(0, 10_000, acceptance), "nothing offered");
            assertEquals(0, EnergyAcceptance.accepted(10_000, 0, acceptance), "no room");
            assertEquals(0, EnergyAcceptance.accepted(-5, 10_000, acceptance));
        }
    }

    @Test void aStoreTakesWhatFitsAndTheItemKeepsTheRest() {
        AtomicLong candle = new AtomicLong(10_000);
        EnergyBuffer machine = store(100_000, 96_000);
        assertEquals(4_000, EnergyAcceptance.accept(item(10_000, candle), machine, CorruptionEnergyAcceptance.PARTIAL));
        assertEquals(100_000, machine.getEnergy());
        assertEquals(6_000, candle.get(), "the item keeps what did not fit");
        assertFalse(EnergyAcceptance.spent(candle.get(), true));
    }

    @Test void aStoreThatTakesOnlyWholeChargesTakesNothingWhileTheWholeWouldNotFit() {
        AtomicLong carried = new AtomicLong(10_000);
        EnergyBuffer machine = store(100_000, 96_000);
        assertEquals(0, EnergyAcceptance.accept(item(10_000, carried), machine, CorruptionEnergyAcceptance.WHOLE));
        assertEquals(10_000, carried.get());
        assertEquals(96_000, machine.getEnergy());
        machine.setEnergy(90_000);
        assertEquals(10_000, EnergyAcceptance.accept(item(10_000, carried), machine, CorruptionEnergyAcceptance.WHOLE));
        assertEquals(0, carried.get());
        assertTrue(EnergyAcceptance.spent(carried.get(), true), "a candle with nothing left burns away");
        assertFalse(EnergyAcceptance.spent(carried.get(), false), "other carriers stay, empty");
    }

    @Test void theStoresOwnLimitsCountAsLackOfRoom() {
        AtomicLong candle = new AtomicLong(10_000);
        CorruptionEnergyStorage slow = store(100_000, 0).view(2_500, 0);
        assertEquals(0, EnergyAcceptance.accept(item(10_000, candle), slow, CorruptionEnergyAcceptance.WHOLE), "it cannot take the whole at once");
        assertEquals(2_500, EnergyAcceptance.accept(item(10_000, candle), slow, CorruptionEnergyAcceptance.PARTIAL));
        assertEquals(7_500, candle.get());
        assertEquals(0, EnergyAcceptance.accept(item(10_000, candle), store(100_000, 0).view(0, 0), CorruptionEnergyAcceptance.PARTIAL), "a store that takes nothing in");
    }

    @Test void oneItemCarriesUpToItsCapacityAndMovesItsOwnEnergy() {
        AtomicLong carried = new AtomicLong();
        CarriedEnergy battery = item(10_000, carried);
        assertTrue(battery.canInsert()); assertTrue(battery.canExtract());
        assertEquals(10_000, battery.insert(25_000, false));
        assertEquals(10_000, carried.get());
        assertEquals(0, battery.insert(1, false), "full");
        assertEquals(3_000, battery.extract(3_000, true));
        assertEquals(10_000, carried.get(), "simulating changes nothing");
        assertEquals(3_000, battery.extract(3_000, false));
        assertEquals(7_000, battery.getEnergy());
        // Data that says more than it can hold, or less than nothing, is read as what it could be.
        carried.set(50_000); assertEquals(10_000, battery.getEnergy());
        carried.set(-50); assertEquals(0, battery.getEnergy());
        assertEquals(0, battery.extract(10, false));
    }

    @Test void allAtOnceGivesEverythingAndACandleBurnsAwayWithTheLastOfIt() {
        AtomicInteger count = new AtomicInteger(1);
        AtomicLong each = new AtomicLong(10_000);
        assertEquals(10_000, candles(count, each).extractAll(true));
        assertEquals(10_000, each.get()); assertEquals(1, count.get(), "simulating changes nothing");
        assertEquals(10_000, candles(count, each).extractAll(false));
        assertEquals(0, each.get());
        assertEquals(0, count.get(), "burnt away");
        AtomicLong battery = new AtomicLong(4_000);
        assertEquals(4_000, item(10_000, battery).extractAll(false));
        assertEquals(0, battery.get(), "one that stays is left empty");
    }

    @Test void bitByBitGivesAStepAtATimeUntilTheItemIsEmpty() {
        AtomicInteger count = new AtomicInteger(1);
        AtomicLong each = new AtomicLong(2_500);
        assertEquals(1_000, candles(count, each).extractStep(1_000, true));
        assertEquals(2_500, each.get());
        assertEquals(1_000, candles(count, each).extractStep(1_000, false));
        assertEquals(1_000, candles(count, each).extractStep(1_000, false));
        assertEquals(1, count.get(), "still burning");
        assertEquals(500, candles(count, each).extractStep(1_000, false), "the last step is what is left");
        assertEquals(0, count.get(), "burnt away with the last of it");
        assertEquals(3, CarriedEnergy.steps(2_500, 1_000));
    }

    @Test void fromAStackOfSeveralOnlyAllOfAUsedUpItemComes() {
        AtomicInteger count = new AtomicInteger(3);
        AtomicLong each = new AtomicLong(10_000);
        assertEquals(0, candles(count, each).extractStep(1_000, false), "the rest of the stack could not keep their own charge");
        assertEquals(10_000, candles(count, each).extractAll(false));
        assertEquals(2, count.get(), "one candle burnt away");
        assertEquals(10_000, each.get(), "the others keep theirs");
        CarriedEnergy batteries = new CarriedEnergy(10_000, false, 3, each::get, each::set, () -> fail("batteries stay"));
        assertEquals(0, batteries.extractAll(false), "an emptied battery would have nowhere to go");
    }

    @Test void aStackOfSeveralSaysWhatTheyCarryButMovesNothing() {
        AtomicLong each = new AtomicLong(10_000);
        CarriedEnergy stack = new CarriedEnergy(10_000, true, 16, each::get, each::set, () -> {});
        assertEquals(160_000, stack.getEnergy());
        assertEquals(160_000, stack.getCapacity());
        assertFalse(stack.canInsert()); assertFalse(stack.canExtract());
        assertEquals(0, stack.extract(1_000, false));
        assertEquals(0, stack.insert(1_000, false));
        assertEquals(10_000, each.get());
        assertEquals(0, EnergyAcceptance.accept(stack, store(1_000_000, 0), CorruptionEnergyAcceptance.PARTIAL), "one is taken off the stack first");
        assertEquals(Long.MAX_VALUE, new CarriedEnergy(Long.MAX_VALUE, false, 2, () -> Long.MAX_VALUE, energy -> {}, () -> {}).getEnergy(), "too much to count is the most there is");
    }

    @Test void stepsCountAPartAsAWholeOne() {
        assertEquals(10, CarriedEnergy.steps(CorruptionEnergy.kilo(10), CarriedEnergy.DEFAULT_STEP), "a candle in half a second");
        assertEquals(1, CarriedEnergy.steps(1, 1_000));
        assertEquals(1, CarriedEnergy.steps(0, 1_000));
        assertEquals(2, CarriedEnergy.steps(1_001, 1_000));
        assertEquals(1, CarriedEnergy.steps(5_000, 0));
        assertEquals(Integer.MAX_VALUE, CarriedEnergy.steps(Long.MAX_VALUE, 1));
    }
}
