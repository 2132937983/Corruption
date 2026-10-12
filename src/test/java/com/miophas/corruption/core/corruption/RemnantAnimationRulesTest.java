package com.miophas.corruption.core.corruption;

import com.miophas.corruption.api.corruption.RemnantAnimationApi;
import com.miophas.corruption.api.corruption.RemnantAnimationDriver;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemnantAnimationRulesTest {
    @Test void moddedSourcesReplayAttackEventsAndVanillaSourcesKeepTheirDedicatedModels() {
        assertNotNull(RemnantAnimationApi.driver("fixture:unregistered_boss"));
        assertNull(RemnantAnimationApi.driver("minecraft:zombie"));
    }
    @Test void registeredDriversOverrideDefaultsAndCanBeRemoved() {
        ResourceLocation boss = ResourceLocation.parse("fixture:animated_boss"), vanilla = ResourceLocation.parse("minecraft:ravager");
        RemnantAnimationDriver custom = (source, event) -> {};
        RemnantAnimationApi.register(boss, custom);
        RemnantAnimationApi.register(vanilla, custom);
        assertSame(custom, RemnantAnimationApi.driver(boss.toString()));
        assertSame(custom, RemnantAnimationApi.driver(vanilla.toString()));
        RemnantAnimationApi.unregister(boss);
        RemnantAnimationApi.unregister(vanilla);
        assertNotSame(custom, RemnantAnimationApi.driver(boss.toString()));
        assertNull(RemnantAnimationApi.driver(vanilla.toString()));
    }
    @Test void disabledSourcesDoNotReplayAnimations() {
        ResourceLocation boss = ResourceLocation.parse("fixture:silent_boss");
        RemnantAnimationApi.disable(boss);
        assertNull(RemnantAnimationApi.driver(boss.toString()));
        RemnantAnimationApi.unregister(boss);
        assertNotNull(RemnantAnimationApi.driver(boss.toString()));
    }
    @Test void driversRunForTheDefaultDurationUnlessOverridden() {
        assertEquals(RemnantAnimationRules.DEFAULT_DURATION, ((RemnantAnimationDriver) (source, event) -> {}).duration());
    }
}
