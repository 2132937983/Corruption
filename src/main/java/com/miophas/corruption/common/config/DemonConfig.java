package com.miophas.corruption.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The Goetic demons' settings: a section, [demons], of the gameplay settings file (InfectionConfig.SPEC builds it,
 * corruption-server.toml). Whether the demons can be hurt only by the arcane (see GoeticDemon.accepts). Read as the
 * file is loaded or reloaded; until then, the default.
 */
public final class DemonConfig {
    /** Whether only the arcane hurts a demon, unless the file says otherwise. */
    public static final boolean ARCANE_ONLY_DEFAULT = true;
    public static ModConfigSpec.BooleanValue ARCANE_ONLY;
    private static volatile boolean arcaneOnly = ARCANE_ONLY_DEFAULT;

    /** Its section, pushed onto the gameplay settings' builder as they are built. */
    static void define(ModConfigSpec.Builder builder) {
        builder.push("demons");
        ARCANE_ONLY = builder
                .comment("Whether the Goetic demons (Andromalius, Dantalion, Seere, Decarabia, Belial, Amdusias, and any an add-on adds) can be hurt only by the "
                        + "arcane: a spell's own damage, a damage type in the tag corruption:spell, or a blow struck by hand with a blade that bears the "
                        + "arcane's mark (item tag corruption:arcane_weapons). What passes every defence (/kill, the void) always reaches them. "
                        + "Disabled, any damage hurts them, as it would any creature.")
                .translation("corruption.configuration.demons.arcaneOnly")
                .define("arcaneOnly", ARCANE_ONLY_DEFAULT);
        builder.pop();
    }

    static void load() { arcaneOnly = ARCANE_ONLY.get(); }

    static void reset() { arcaneOnly = ARCANE_ONLY_DEFAULT; }

    /** Whether the Goetic demons can be hurt only by the arcane. */
    public static boolean arcaneOnly() { return arcaneOnly; }

    /** Sets it: at once, and in the file too while the file is loaded (for an operator, or a test). */
    public static void setArcaneOnly(boolean only) {
        arcaneOnly = only;
        if (InfectionConfig.SPEC.isLoaded()) {
            ARCANE_ONLY.set(only);
            InfectionConfig.SPEC.save();
        }
    }

    private DemonConfig() {}
}
