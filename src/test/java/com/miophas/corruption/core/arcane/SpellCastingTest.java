package com.miophas.corruption.core.arcane;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The rules of casting without a game: what a wand can be taught and which spell it uses, and what a cast asks and costs. */
class SpellCastingTest {
    @Test void aGrimoireHoldsThreeSpellsEachOnceAndKeepsItsChoice() {
        Grimoire grimoire = Grimoire.EMPTY.inscribed("corruption:baal");
        assertEquals("corruption:baal", grimoire.current(), "the first spell taught is the one used");
        grimoire = grimoire.inscribed("corruption:agares").selecting(1).inscribed("corruption:corruption");
        assertEquals(List.of("corruption:baal", "corruption:agares", "corruption:corruption"), grimoire.spells());
        assertEquals("corruption:agares", grimoire.current(), "teaching another keeps the choice");
        assertEquals(Grimoire.Inscribe.ALREADY_KNOWN, grimoire.check("corruption:baal"));
        assertEquals(Grimoire.Inscribe.FULL, grimoire.check("corruption:transmutation"));
        assertSame(grimoire, grimoire.inscribed("corruption:transmutation"), "full, it takes no more");
        assertSame(grimoire, grimoire.selecting(3), "a choice it does not hold changes nothing");
        assertEquals("corruption:corruption", grimoire.cycled().current());
        assertEquals("corruption:baal", grimoire.cycled().cycled().current(), "round to the first again");
        assertEquals(List.of("a", "b"), new Grimoire(List.of("a", "b", "a"), 0).spells(), "a spell is never held twice");
        assertEquals(1, new Grimoire(List.of("a", "b"), -1).selected(), "a choice past either end wraps round");
        assertNull(Grimoire.EMPTY.current());
    }

    @Test void aSpellIsWrittenIntoTheNextFreeSlotOrOverOneAlreadyWritten() {
        Grimoire grimoire = Grimoire.EMPTY.inscribed("a", 5).inscribed("b", 5).selecting(1);
        assertEquals(Grimoire.Inscribe.READY, grimoire.checkAt(2, "c", 5), "the next free slot");
        assertEquals(Grimoire.Inscribe.FULL, grimoire.checkAt(3, "c", 5), "no gaps: past the next free slot there is no room");
        assertEquals(Grimoire.Inscribe.FULL, grimoire.checkAt(-1, "c", 5));
        assertEquals(Grimoire.Inscribe.ALREADY_KNOWN, grimoire.checkAt(0, "b", 5), "a spell is never held twice, wherever it is written");
        Grimoire over = grimoire.writtenAt(0, "c", 5);
        assertEquals(List.of("c", "b"), over.spells(), "written over");
        assertEquals("b", over.current(), "the chosen spell stays chosen");
        assertEquals(List.of("a", "b", "c"), grimoire.writtenAt(2, "c", 5).spells());
        assertSame(grimoire, grimoire.writtenAt(4, "c", 5));
        assertEquals(Grimoire.Inscribe.FULL, grimoire.checkAt(2, "c", 2), "two slots, both written");
        assertEquals(Grimoire.Inscribe.READY, grimoire.checkAt(1, "c", 2), "but either may be written over");
    }

    @Test void castingUnitsDifferInTheirRoom() {
        CastingUnit wand = CastingUnit.EMPTY.withEnergy(100);
        assertEquals(Grimoire.CAPACITY, wand.capacity(), "the mod's wand: three");
        for (String spell : new String[]{"a", "b", "c"}) wand = wand.inscribing(spell);
        assertEquals(Grimoire.Inscribe.FULL, wand.check("d"));
        CastingUnit staff = new CastingUnit(wand.grimoire(), 100, 6);
        assertEquals(Grimoire.Inscribe.READY, staff.check("d"), "a tool with room for six");
        staff = staff.inscribing("d").inscribing("e").inscribing("f");
        assertEquals(6, staff.grimoire().size());
        assertEquals(6, staff.withEnergy(0).capacity(), "spending or charging keeps its room");
        assertEquals(List.of("a", "x", "c", "d", "e", "f"), staff.writingAt(1, "x").grimoire().spells());
        assertEquals(1, new CastingUnit(Grimoire.EMPTY, 0, 0).capacity(), "room for one at least");
    }

    /** A caster with this much to hand, writing down what it pays. */
    private static final class Hand implements SpellCasting.Caster {
        long energy = 10_000;
        int corruption, parchment = 1;
        boolean cooling, free;
        final List<String> paid = new ArrayList<>();
        @Override public long energy() { return energy; }
        @Override public int corruption() { return corruption; }
        @Override public int corruptionLimit() { return 20; }
        @Override public int parchment() { return parchment; }
        @Override public boolean cooling() { return cooling; }
        @Override public boolean free() { return free; }
        @Override public void pay(long spent, int parchments, int gain) { paid.add(spent + " " + parchments + " " + gain); }
    }

    @Test void aCastIsCheckedInOrderAndPaidOnlyWhenItHappens() {
        SpellRule rule = new SpellRule(2_000, 40, 5, 3, List.of(new SpellAction.Damage(4, 3)));
        Hand hand = new Hand();
        hand.cooling = true;
        assertEquals(SpellCasting.Check.COOLING, SpellCasting.cast(rule, hand), "cooling comes first");
        hand.cooling = false;
        assertEquals(SpellCasting.Check.NOT_CORRUPTED_ENOUGH, SpellCasting.cast(rule, hand));
        hand.corruption = 17;
        assertEquals(SpellCasting.Check.TOO_CORRUPTED, SpellCasting.cast(rule, hand), "17 and 3 more is all 20: the corruption would take them");
        hand.corruption = 16;
        hand.energy = 1_999;
        assertEquals(SpellCasting.Check.NO_ENERGY, SpellCasting.cast(rule, hand));
        hand.energy = 2_000;
        hand.parchment = 0;
        assertEquals(SpellCasting.Check.NO_PARCHMENT, SpellCasting.cast(rule, hand));
        assertEquals(List.of(), hand.paid, "nothing is paid for a spell not cast");
        hand.parchment = 1;
        assertEquals(SpellCasting.Check.READY, SpellCasting.cast(rule, hand));
        assertEquals(List.of("2000 1 3"), hand.paid, "its energy, one parchment and its corruption");
        Hand nearlyLost = new Hand();
        nearlyLost.corruption = 19;
        assertEquals(SpellCasting.Check.READY, SpellCasting.check(new SpellRule(0, 0, 0, 0, List.of()), nearlyLost),
                "a spell that corrupts no further never stops a caster for being corrupted");
    }

    @Test void inCreativePlayACastCostsNothingButStillCools() {
        SpellRule rule = new SpellRule(2_000, 40, 10, 3, List.of());
        Hand hand = new Hand();
        hand.free = true;
        hand.energy = 0;
        hand.parchment = 0;
        hand.corruption = 19;
        assertEquals(SpellCasting.Check.READY, SpellCasting.cast(rule, hand));
        assertEquals(List.of(), hand.paid, "free, it pays nothing");
        hand.cooling = true;
        assertEquals(SpellCasting.Check.COOLING, SpellCasting.cast(rule, hand));
    }

    @Test void aCastingUnitIsTaughtSpendsAndStandsForItsCaster() {
        CastingUnit unit = CastingUnit.EMPTY.withEnergy(5_000).inscribing("corruption:agares").inscribing("corruption:baal");
        assertEquals("corruption:agares", unit.current());
        assertEquals("corruption:baal", unit.selecting(1).current());
        assertEquals(0, unit.spending(9_000).energy(), "it never holds less than none");
        List<Long> spent = new ArrayList<>();
        SpellCasting.Caster caster = unit.caster(new CastingUnit.Situation(4, 20, 2, false, false), (energy, parchments, gain) -> spent.add(energy));
        assertEquals(5_000, caster.energy());
        assertEquals(20, caster.corruptionLimit());
        assertEquals(SpellCasting.Check.READY, SpellCasting.cast(new SpellRule(1_000, 0, 0, 1, List.of()), caster));
        assertEquals(List.of(1_000L), spent);
        assertEquals(Integer.MAX_VALUE, unit.caster(0, 1, false, (energy, parchments, gain) -> {}).corruptionLimit(), "no limit unless one is given");
    }
}
