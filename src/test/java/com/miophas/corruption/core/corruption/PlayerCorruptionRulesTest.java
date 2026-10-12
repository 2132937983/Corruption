package com.miophas.corruption.core.corruption;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerCorruptionRulesTest {
    @Test void curseCannotRecoverOrOverflowAndOnlyCompletionCrossesTheLimit(){
        assertEquals(0,PlayerCorruptionRules.increase(0,-5));assertEquals(11,PlayerCorruptionRules.increase(10,1));
        assertEquals(20,PlayerCorruptionRules.increase(19,Integer.MAX_VALUE));
        assertTrue(PlayerCorruptionRules.completed(19,20));assertFalse(PlayerCorruptionRules.completed(20,20));
    }
    @Test void halfHeartsAccountForEveryUnitIncludingOddValues(){
        for(int value=0;value<=20;value++){
            int total=0;for(int heart=0;heart<10;heart++)total+=PlayerCorruptionRules.heartFill(value,heart);
            assertEquals(value,total);
        }
        assertEquals(2,PlayerCorruptionRules.heartFill(3,0));assertEquals(1,PlayerCorruptionRules.heartFill(3,1));assertEquals(0,PlayerCorruptionRules.heartFill(3,2));
    }
}
