package com.miophas.corruption.common.config;

import com.miophas.corruption.api.infection.InfectionSettings;
import com.miophas.corruption.common.infection.InfectionEvents;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/** Gameplay defaults, file persistence and live configuration belong to common. */
public final class InfectionConfig {
    public static final InfectionSettings DEFAULT = new InfectionSettings(4, 64, 16, 1.0, 20, 2048, 0.15, 8);
    public static final int MIN_SPREAD_INTERVAL = 1;
    public static final int MAX_SPREAD_INTERVAL = 12000;
    public static final int MAX_SOURCES_PER_BATCH = 1024;
    public static final int MAX_CONVERSIONS_PER_BATCH = 128;
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue INFECTION_ENABLED;
    public static final ModConfigSpec.IntValue SPREAD_INTERVAL;
    public static final ModConfigSpec.IntValue SOURCES_PER_BATCH;
    public static final ModConfigSpec.IntValue CONVERSIONS_PER_BATCH;
    public static final ModConfigSpec.IntValue MAX_SPREAD_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue SPREAD_CHANCE;
    public static final ModConfigSpec.DoubleValue BASE_CORRUPTION_CHANCE;
    public static final ModConfigSpec.IntValue SCAN_INTERVAL;
    public static final ModConfigSpec.IntValue SCAN_BLOCKS_PER_BATCH;
    public static final ModConfigSpec.BooleanValue CORRUPTED_BLOCKS_FLAMMABLE;
    private static volatile boolean corruptedBlocksFlammable;
    private static volatile InfectionSettings current = DEFAULT;
    public static final ModConfigSpec.DoubleValue MALICE_CHANCE, VINE_CHANCE, DECOR_CHANCE, ELDRITCH_CHANCE, EERIE_CHANCE, ELDRITCH_GROWTH_CHANCE;
    public static final ModConfigSpec.IntValue VINE_GROWTH_TICKS, BLINDNESS_RADIUS, ATTRACTION_RADIUS;
    public static final ModConfigSpec.DoubleValue GREED_CHANCE;
    public static final ModConfigSpec.IntValue GREED_GROWTH_TICKS, GREED_RANGE, GREED_RANGE_STEP;
    private record Ecology(double malice, double vine, double decor, int growth, int blindness, int attraction,
            double eldritch, double eerie, double eldritchGrowth, double greed, int greedGrowth, int greedRange, int greedRangeStep) {}
    private static final Ecology DEFAULT_ECOLOGY = new Ecology(0.01, 0.5, 0.12, 1200, 4, 16, 0.08, 0.20, 0.10, 0.10, 1200, 6, 4);
    private static volatile Ecology ecology = DEFAULT_ECOLOGY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Corruption infection settings. Changes apply to all server dimensions without restarting.")
                .push("infection");
        INFECTION_ENABLED = builder
                .comment("Master switch for block infection. Disabled: no discovery scans or conversions. Existing blocks, plant growth and damage remain active; re-enabling resumes loaded fronts.")
                .translation("corruption.configuration.infection.enabled")
                .define("enabled", true);
        SPREAD_INTERVAL = builder
                .comment("Ticks between infection batches. 20 ticks = 1 second at normal TPS; lower is faster.")
                .translation("corruption.configuration.infection.spreadIntervalTicks")
                .defineInRange("spreadIntervalTicks", DEFAULT.spreadIntervalTicks(),
                        MIN_SPREAD_INTERVAL, MAX_SPREAD_INTERVAL);
        SPREAD_CHANCE = builder
                .comment("Probability that each eligible source converts one neighbor per batch. 0.0 disables spread.")
                .translation("corruption.configuration.infection.spreadChance")
                .defineInRange("spreadChance", DEFAULT.spreadChance(), 0.0, 1.0);
        BASE_CORRUPTION_CHANCE = builder
                .comment("Chance that an infected solid becomes the original corruption block instead of its corrupted variant. Fluids retain their fluid form.")
                .translation("corruption.configuration.infection.baseCorruptionChance")
                .defineInRange("baseCorruptionChance", DEFAULT.baseCorruptionChance(), 0.0, 1.0);
        SOURCES_PER_BATCH = builder
                .comment("Base source budget per dimension per batch; scales with active frontier work, up to maxSpreadMultiplier.")
                .translation("corruption.configuration.infection.sourcesPerBatch")
                .defineInRange("sourcesPerBatch", DEFAULT.sourcesPerBatch(), 1, MAX_SOURCES_PER_BATCH);
        CONVERSIONS_PER_BATCH = builder
                .comment("Base conversion budget per dimension per batch; scales with active frontier work, up to maxSpreadMultiplier.")
                .translation("corruption.configuration.infection.conversionsPerBatch")
                .defineInRange("conversionsPerBatch", DEFAULT.conversionsPerBatch(), 1, MAX_CONVERSIONS_PER_BATCH);
        MAX_SPREAD_MULTIPLIER = builder
                .comment("Maximum adaptive spread budget multiplier. 1 keeps a fixed global budget; higher values preserve frontier speed as infection grows. Default maximum: 512 source checks and 128 conversions per batch.")
                .translation("corruption.configuration.infection.maxSpreadMultiplier")
                .defineInRange("maxSpreadMultiplier", DEFAULT.maxSpreadMultiplier(), 1, 16);
        SCAN_INTERVAL = builder
                .comment("Ticks between rechecks of chunks that are loaded but not ticking, or paused; at least 20 ticks. Newly loaded ticking chunks are scanned every tick regardless, so spread resumes promptly after a reload.")
                .translation("corruption.configuration.infection.scanIntervalTicks")
                .defineInRange("scanIntervalTicks", DEFAULT.scanIntervalTicks(), 20, 1200);
        SCAN_BLOCKS_PER_BATCH = builder
                .comment("Maximum discovery work per dimension per tick while loaded chunks wait to be scanned. One unit is one corrupted block checked for somewhere to spread; any other block costs a sixteenth.")
                .translation("corruption.configuration.infection.scanBlocksPerBatch")
                .defineInRange("scanBlocksPerBatch", DEFAULT.scanBlocksPerBatch(), 128, 8192);
        builder.pop();
        builder.push("blocks");
        CORRUPTED_BLOCKS_FLAMMABLE = builder
                .comment("Whether generated corrupted block variants can burn and spread fire. Does not affect corruption, malice, ores, fluids or plants. Uses wood-like flammability when enabled.")
                .translation("corruption.configuration.blocks.corruptedBlocksFlammable")
                .define("corruptedBlocksFlammable", false);
        builder.pop();
        builder.push("ecology");
        MALICE_CHANCE = builder.comment("Chance an eligible infected solid becomes malice. Ore conversions and fluids retain their own forms.")
                .translation("corruption.configuration.ecology.maliceChance")
                .defineInRange("maliceChance", 0.01, 0.0, 1.0);
        VINE_CHANCE = builder.comment("One-time vine sprouting chance after malice is placed. Shares a mutually exclusive roll with greedChance; needs support and clear growing space.")
                .translation("corruption.configuration.ecology.vineChance")
                .defineInRange("vineChance", 0.5, 0.0, 1.0);
        GREED_CHANCE = builder.comment("One-time greed sprouting chance on malice. Uses a single mutually exclusive roll with vineChance; greed has priority if their sum exceeds 1.")
                .translation("corruption.configuration.ecology.greedChance").defineInRange("greedChance", 0.10, 0, 1);
        GREED_GROWTH_TICKS = builder.translation("corruption.configuration.ecology.greedGrowthTicks")
                .defineInRange("greedGrowthTicks", 1200, 20, 72000);
        GREED_RANGE = builder.translation("corruption.configuration.ecology.greedDetectionRange")
                .defineInRange("greedDetectionRange", 6, 1, 64);
        GREED_RANGE_STEP = builder.translation("corruption.configuration.ecology.greedRangePerStage")
                .defineInRange("greedRangePerStage", 4, 1, 16);
        DECOR_CHANCE = builder.comment("Chance to decorate an exposed corrupted floor after infection or cave generation.")
                .translation("corruption.configuration.ecology.decorationChance")
                .defineInRange("decorationChance", 0.12, 0.0, 1.0);
        ELDRITCH_CHANCE = builder.comment("Chance to sprout an upward or hanging eldritch vine at an exposed infected solid.")
                .translation("corruption.configuration.ecology.eldritchChance")
                .defineInRange("eldritchChance", 0.08, 0.0, 1.0);
        EERIE_CHANCE = builder.comment("Chance to seed an eerie surface patch after infection; each seed attempts at most five extra faces.")
                .translation("corruption.configuration.ecology.eerieChance")
                .defineInRange("eerieChance", 0.20, 0.0, 1.0);
        ELDRITCH_GROWTH_CHANCE = builder.comment("Chance for an eldritch terminal segment to extend at a vanilla random block tick.")
                .translation("corruption.configuration.ecology.eldritchGrowthChance")
                .defineInRange("eldritchGrowthChance", 0.10, 0.0, 1.0);
        VINE_GROWTH_TICKS = builder.translation("corruption.configuration.ecology.vineGrowthTicks")
                .defineInRange("vineGrowthTicks", 1200, 20, 72000);
        BLINDNESS_RADIUS = builder.translation("corruption.configuration.ecology.vineBlindnessRadius")
                .defineInRange("vineBlindnessRadius", 4, 1, 16);
        ATTRACTION_RADIUS = builder.translation("corruption.configuration.ecology.vineAttractionRadius")
                .defineInRange("vineAttractionRadius", 16, 1, 64);
        builder.pop();
        SPEC = builder.build();
    }

    public static InfectionSettings settings() {
        return current;
    }
    public static boolean corruptedBlocksFlammable() { return corruptedBlocksFlammable; }
    public static double maliceChance() { return ecology.malice; }
    public static double vineChance() { return ecology.vine; }
    public static double greedChance() { return ecology.greed; }
    public static int greedGrowthTicks() { return ecology.greedGrowth; }
    public static int greedRange() { return ecology.greedRange; }
    public static int greedRangeStep() { return ecology.greedRangeStep; }
    public static double decorChance() { return ecology.decor; }
    public static double eldritchChance() { return ecology.eldritch; }
    public static double eerieChance() { return ecology.eerie; }
    public static double eldritchGrowthChance() { return ecology.eldritchGrowth; }
    public static int vineGrowthTicks() { return ecology.growth; }
    public static int blindnessRadius() { return ecology.blindness; }
    public static int attractionRadius() { return ecology.attraction; }

    public static void onLoading(ModConfigEvent.Loading event) {
        loadSnapshot(event);
    }

    public static void onReloading(ModConfigEvent.Reloading event) {
        loadSnapshot(event);
    }

    private static void loadSnapshot(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        InfectionSettings updated = new InfectionSettings(SPREAD_INTERVAL.get(), SOURCES_PER_BATCH.get(),
                CONVERSIONS_PER_BATCH.get(), SPREAD_CHANCE.get(), SCAN_INTERVAL.get(), SCAN_BLOCKS_PER_BATCH.get(), BASE_CORRUPTION_CHANCE.get(), MAX_SPREAD_MULTIPLIER.get(), INFECTION_ENABLED.get());
        current = updated;
        corruptedBlocksFlammable = CORRUPTED_BLOCKS_FLAMMABLE.get();
        ecology = new Ecology(MALICE_CHANCE.get(), VINE_CHANCE.get(), DECOR_CHANCE.get(),
                VINE_GROWTH_TICKS.get(), BLINDNESS_RADIUS.get(), ATTRACTION_RADIUS.get(),
                ELDRITCH_CHANCE.get(), EERIE_CHANCE.get(), ELDRITCH_GROWTH_CHANCE.get(),
                GREED_CHANCE.get(), GREED_GROWTH_TICKS.get(), GREED_RANGE.get(), GREED_RANGE_STEP.get());
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            // File watchers can run on another thread; all engine updates stay on the server thread.
            server.execute(() -> InfectionEvents.updateSettings(server, current));
        }
    }

    public static void onUnloading(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            current = DEFAULT;
            corruptedBlocksFlammable = false;
            ecology = DEFAULT_ECOLOGY;
        }
    }

    /** Called by operator commands on the server thread. */
    public static void saveSettings(MinecraftServer server, InfectionSettings updated) {
        INFECTION_ENABLED.set(updated.enabled());
        SPREAD_INTERVAL.set(updated.spreadIntervalTicks());
        SPREAD_CHANCE.set(updated.spreadChance());
        SOURCES_PER_BATCH.set(updated.sourcesPerBatch());
        CONVERSIONS_PER_BATCH.set(updated.conversionsPerBatch());
        SCAN_INTERVAL.set(updated.scanIntervalTicks());
        SCAN_BLOCKS_PER_BATCH.set(updated.scanBlocksPerBatch());
        BASE_CORRUPTION_CHANCE.set(updated.baseCorruptionChance());
        MAX_SPREAD_MULTIPLIER.set(updated.maxSpreadMultiplier());
        SPEC.save();
        current = updated;
        InfectionEvents.updateSettings(server, updated);
    }

    private InfectionConfig() {}
}
