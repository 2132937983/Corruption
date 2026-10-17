package com.miophas.corruption.common.config;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.core.plant.MaliceVineRules;
import net.minecraft.server.MinecraftServer;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * A settings file written by an older version of the mod is brought up to date as it loads: a setting still at an old
 * default that has since changed is moved to the new default (the vine's sprouting chance, its brood's interval), and a
 * server's own choice is left as it is; a file of this version is left alone.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class SettingsMigrationServerTest {
    @Test void anOldDefaultIsMovedToTheNewOneAndAServersOwnChoiceStays(MinecraftServer server) {
        assertEquals(.05, InfectionConfig.upgraded(0, .5, InfectionConfig.OLD_VINE_CHANCE, InfectionConfig.VINE_CHANCE_DEFAULT), "the old default goes");
        assertEquals(.3, InfectionConfig.upgraded(0, .3, InfectionConfig.OLD_VINE_CHANCE, InfectionConfig.VINE_CHANCE_DEFAULT), "a choice stays");
        assertEquals(.5, InfectionConfig.upgraded(InfectionConfig.SETTINGS_VERSION, .5, InfectionConfig.OLD_VINE_CHANCE, InfectionConfig.VINE_CHANCE_DEFAULT),
                "set on a file of this version, it is a choice too");
        assertEquals(600, InfectionConfig.upgraded(0, 3600, InfectionConfig.OLD_VINE_ELITE_INTERVAL, MaliceVineRules.BROOD_TICKS));
        assertEquals(1200, InfectionConfig.upgraded(0, 1200, InfectionConfig.OLD_VINE_ELITE_INTERVAL, MaliceVineRules.BROOD_TICKS));
        assertEquals(.05, InfectionConfig.VINE_CHANCE_DEFAULT, 1e-9, "a vine sprouts on one malice in twenty");
        assertEquals(600, MaliceVineRules.BROOD_TICKS, "and broods every half a minute");
    }

    @Test void theLoadedFileIsBroughtUpToDateOnceAndOnlyOnce(MinecraftServer server) {
        assertTrue(InfectionConfig.SPEC.isLoaded(), "the server loads its settings");
        assertEquals(InfectionConfig.SETTINGS_VERSION, InfectionConfig.CONFIG_VERSION.get(), "and brings them up to date as it does");
        double chance = InfectionConfig.VINE_CHANCE.get();
        int interval = InfectionConfig.VINE_ELITE_INTERVAL.get();
        try {
            // As a file from before versions were kept, still holding the old defaults.
            InfectionConfig.CONFIG_VERSION.set(0);
            InfectionConfig.VINE_CHANCE.set(InfectionConfig.OLD_VINE_CHANCE);
            InfectionConfig.VINE_ELITE_INTERVAL.set(InfectionConfig.OLD_VINE_ELITE_INTERVAL);
            InfectionConfig.migrate();
            assertEquals(InfectionConfig.VINE_CHANCE_DEFAULT, InfectionConfig.VINE_CHANCE.get(), 1e-9, "0.5 becomes 0.05");
            assertEquals(MaliceVineRules.BROOD_TICKS, InfectionConfig.VINE_ELITE_INTERVAL.get(), "3600 ticks become 600");
            assertEquals(InfectionConfig.SETTINGS_VERSION, InfectionConfig.CONFIG_VERSION.get(), "and the file is of this version now");
            // A server that sets 0.5 again afterwards keeps it.
            InfectionConfig.VINE_CHANCE.set(InfectionConfig.OLD_VINE_CHANCE);
            InfectionConfig.migrate();
            assertEquals(InfectionConfig.OLD_VINE_CHANCE, InfectionConfig.VINE_CHANCE.get(), 1e-9, "once up to date, it is left alone");
        } finally {
            InfectionConfig.VINE_CHANCE.set(chance);
            InfectionConfig.VINE_ELITE_INTERVAL.set(interval);
            InfectionConfig.CONFIG_VERSION.set(InfectionConfig.SETTINGS_VERSION);
            InfectionConfig.SPEC.save();
        }
    }
}
