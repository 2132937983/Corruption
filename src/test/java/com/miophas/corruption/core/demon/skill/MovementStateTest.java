package com.miophas.corruption.core.demon.skill;

import com.miophas.corruption.core.demon.SolomonRules;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MovementStateTest {
    @Test void burrowingWaitsForEveryBoundaryAndStrikesTheSurfaceOnlyOnce() {
        var rules = SolomonRules.BURROW; var state = new BurrowState();
        state.begin();state.rear(rules, rules.timing().windup() - 1);assertEquals(BurrowState.Phase.REAR, state.phase());
        state.rear(rules, rules.timing().windup());assertTrue(state.tunnelling());
        state.dive(rules, 100, 100);assertEquals(BurrowState.Phase.DIVE, state.phase());
        state.dive(rules, 100 - rules.depth(), 100);assertEquals(BurrowState.Phase.TUNNEL, state.phase());
        assertFalse(state.tunnel(rules, 100));assertTrue(state.tunnel(rules, 0));
        for(int i=0;i<rules.warn()-1;i++)state.warn(rules);
        assertEquals(BurrowState.Phase.WARN,state.phase());state.warn(rules);
        assertEquals(BurrowState.Phase.RISE,state.phase());
        assertFalse(state.rise(rules,99,100));assertTrue(state.rise(rules,100,100));assertFalse(state.tunnelling());
        assertFalse(state.rise(rules,100,100));assertFalse(state.rise(rules,100+rules.climb(),100));
        assertEquals(BurrowState.Phase.DONE,state.phase());state.begin();assertEquals(0,state.warned());
        state.end();assertFalse(state.tunnelling());
    }
    @Test void ramTracksItsEntireRouteAndCanBurstAndCompleteInTheSameTick() {
        var rules=SolomonRules.RAM;var state=new RamState();state.begin();
        assertFalse(state.aim(rules,0));assertTrue(state.aim(rules,rules.timing().windup()));
        state.charge(rules,100);assertEquals(RamState.Phase.CHARGE,state.phase());
        state.charge(rules,0);assertTrue(state.tunnelling());state.plunge(rules,0);state.under(rules,0);
        assertEquals(RamState.Phase.OUT,state.phase());assertFalse(state.out(rules,99,100));
        assertTrue(state.out(rules,100+rules.climb(),100));assertEquals(RamState.Phase.DONE,state.phase());
        assertFalse(state.out(rules,200,100));state.begin();state.end();assertFalse(state.tunnelling());
    }
    @Test void debrisPoliciesAreIndependentAndCustomPoliciesDoNotMutatePresets() {
        assertEquals(new DebrisRules(3.5,.9),SolomonRules.BURROW.debris());
        assertEquals(new DebrisRules(3.5,.9),SolomonRules.RAM.debris());
        var own=new DebrisRules(8,2);assertTrue(own.fling(0,0,0)[1]>SolomonRules.RAM.debris().fling(0,0,0)[1]);
        assertEquals(.9,SolomonRules.RAM.debris().speed());assertFalse(DebrisRules.tears(-1));
    }
}
