package com.miophas.corruption.core.corruption;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GreedRulesTest {
    @Test void fiveStagesKeepRequestedHeightAndIncreaseThreat() {
        for (int stage=0;stage<5;stage++) {
            assertEquals(2+stage*2,GreedRules.height(stage));
            assertEquals(1+stage,GreedRules.width(stage));
            assertEquals(6+stage*4,GreedRules.detectionRange(stage,6,4));
            if(stage>0) assertTrue(GreedRules.attackInterval(stage)<GreedRules.attackInterval(stage-1));
        }
        assertEquals(2,GreedRules.height(-10));assertEquals(10,GreedRules.height(10));
    }
    @Test void oneRollReservesTenPercentForGreedAndFiftyForVine() {
        int[] counts=new int[3];
        for(int i=0;i<10000;i++)counts[MaliceRules.growthKind((i+.5)/10000,.1,.5)]++;
        assertArrayEquals(new int[]{4000,5000,1000},counts);
        assertEquals(1,MaliceRules.growthKind(.1,.1,.5));
        assertEquals(0,MaliceRules.growthKind(.6,.1,.5));
    }
    @Test void OversizedChancesCannotCreateTwoGrowthsAtOneAnchor() {
        assertEquals(2,MaliceRules.growthKind(.3,.7,.8));
        assertEquals(1,MaliceRules.growthKind(.99,.7,.8));
        assertEquals(0,MaliceRules.growthKind(.5,-1,-1));
    }
}
