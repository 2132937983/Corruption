package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.RemnantAbility;
import com.miophas.corruption.api.corruption.RemnantAbilityApi;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemnantAbilityRulesTest {
    @Test void netherAndDragonFlyWithoutPhasingThroughWalls() {
        for(String source:java.util.List.of("minecraft:ghast","minecraft:blaze","minecraft:wither","minecraft:ender_dragon","minecraft:allay","minecraft:bat","minecraft:bee","minecraft:parrot","minecraft:phantom")){
            int flags=RemnantAbilityApi.flags(source,0);
            assertTrue(RemnantAbility.FLIGHT.in(flags));assertFalse(RemnantAbility.PHASE_WALLS.in(flags));
            assertFalse(RemnantAbility.BASE_SWIMMING.in(flags));
        }
        assertEquals(0,RemnantAbilityApi.flags("minecraft:strider",0));
    }
    @Test void axolotlKeepsAmphibiousMovementWithoutFlightOrSpells() {
        int flags=RemnantAbilityApi.flags("minecraft:axolotl",0);
        assertTrue(RemnantAbility.AMPHIBIOUS.in(flags));assertFalse(RemnantAbility.FLIGHT.in(flags));
        assertFalse(RemnantAbility.SUMMON_VEX.in(flags));
        assertEquals(0,RemnantAbilityApi.flags("minecraft:armadillo",0));
        assertEquals(0,RemnantAbilityApi.flags("minecraft:bogged",0));
    }
    @Test void customSourceAbilitiesAndNativeSubclassesMergeWithoutGivingOrdinaryMobsSpells() {
        ResourceLocation source = ResourceLocation.parse("fixture:custom_caster");
        RemnantAbilityApi.register(source, Set.of(RemnantAbility.SUMMON_VEX));
        RemnantAbilityApi.register(source, Set.of(RemnantAbility.SONIC_BOOM));
        int flags = RemnantAbilityApi.flags(source.toString(), RemnantAbility.WALL_CLIMB.mask());
        assertTrue(RemnantAbility.SUMMON_VEX.in(flags));
        assertTrue(RemnantAbility.SONIC_BOOM.in(flags));
        assertTrue(RemnantAbility.WALL_CLIMB.in(flags));
        assertEquals(0, RemnantAbilityApi.flags("minecraft:cow", 0));
        assertEquals(0, RemnantAbilityApi.flags("minecraft:creeper", 0));
    }
    @Test void waterAnimalsStayAquaticAndCaveSpidersCanClimb() {
        for(String source:java.util.List.of("minecraft:cod","minecraft:dolphin","minecraft:salmon","minecraft:pufferfish","minecraft:squid","minecraft:tadpole","minecraft:tropical_fish")) {
            int flags=RemnantAbilityApi.flags(source,0);
            assertTrue(RemnantAbility.SWIMMING.in(flags));assertFalse(RemnantAbility.FLIGHT.in(flags));
            assertFalse(RemnantAbility.AMPHIBIOUS.in(flags));
        }
        assertTrue(RemnantAbility.WALL_CLIMB.in(RemnantAbilityApi.flags("minecraft:cave_spider",0)));
        assertEquals(0,RemnantAbilityApi.flags("minecraft:camel",0));
        assertEquals(0,RemnantAbilityApi.flags("minecraft:cat",0));
    }
    @Test void ordinarySourcesDoNotAcquireAquaticMovement() {
        for(String source:java.util.List.of("minecraft:player","minecraft:zombie","fixture:unknown_mob","minecraft:spider")) {
            int flags=RemnantAbilityApi.flags(source,0);
            assertFalse(RemnantAbility.BASE_SWIMMING.in(flags));
            assertFalse(RemnantAbility.FLIGHT.in(flags));
            assertFalse(RemnantAbility.SWIMMING.in(flags));
        }
        assertTrue(RemnantAbility.WALL_CLIMB.in(RemnantAbilityApi.flags("minecraft:spider",0)));
        for(RemnantAbility nativeMovement:java.util.List.of(RemnantAbility.FLIGHT,RemnantAbility.SHULKER_COMBAT,RemnantAbility.SWIMMING,RemnantAbility.AMPHIBIOUS)) {
            int flags=RemnantAbilityApi.flags("fixture:special_movement",nativeMovement.mask());
            assertTrue(nativeMovement.in(flags));
            assertFalse(RemnantAbility.BASE_SWIMMING.in(flags));
        }
    }
    @Test void sonicUsesIndependentHorizontalAndVerticalBoundsIncludingHangingTargets() {
        assertTrue(RemnantAbilityApi.inRange(RemnantAbility.SONIC_BOOM, 14 * 14, 19));
        assertTrue(RemnantAbilityApi.inRange(RemnantAbility.SONIC_BOOM, 14 * 14, -19));
        assertFalse(RemnantAbilityApi.inRange(RemnantAbility.SONIC_BOOM, 15 * 15, 0));
        assertFalse(RemnantAbilityApi.inRange(RemnantAbility.SONIC_BOOM, 0, 20));
    }
    @Test void summonsAreCappedEvenIfTheLastCastWouldOtherwiseOvershoot() {
        assertEquals(3, RemnantAbilityApi.summonCount(0));
        assertEquals(2, RemnantAbilityApi.summonCount(4));
        assertEquals(0, RemnantAbilityApi.summonCount(6));
        assertEquals(0, RemnantAbilityApi.summonCount(100));
    }
}
