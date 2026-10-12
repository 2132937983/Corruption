package com.miophas.corruption.core.infection;

import com.miophas.corruption.api.infection.InfectionApi;
import com.miophas.corruption.api.infection.InfectionScheduler;
import com.miophas.corruption.api.infection.InfectionSettings;
import com.miophas.corruption.api.infection.InfectionWorld;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InfectionEngineTest {
    private static final BlockPos SOURCE = new BlockPos(8, 0, 8);
    private static final InfectionSettings SETTINGS = new InfectionSettings(100, 64, 8, 1, 20, 2048);

    @Test
    void disabledInfectionDoesNoWorldWorkAndResumesTrackedFrontsWithoutCatchup() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS.withEnabled(false));
        engine.chunkLoaded(new ChunkPos(SOURCE));
        engine.activate(SOURCE);
        for (int tick = 0; tick <= 1000; tick++) engine.tick(tick);
        assertEquals(0, world.sourceReads);
        assertEquals(0, world.scannedBlocks);
        assertEquals(0, world.conversions);
        engine.updateSettings(SETTINGS, 1000);
        engine.tick(1000);
        assertEquals(0, world.conversions);
        engine.tick(1020);
        assertTrue(world.scannedBlocks > 0);
        engine.tick(1100);
        assertEquals(1, world.conversions);
        engine.updateSettings(SETTINGS.withEnabled(false), 1101);
        world.put(SOURCE.west(), "deepslate");
        engine.activate(SOURCE);
        engine.tick(5000);
        assertEquals(1, world.conversions);
        engine.updateSettings(SETTINGS, 5000);
        engine.tick(5100);
        assertEquals(2, world.conversions);
    }

    @Test
    void unloadingAndRemovingSourcesWhileDisabledDoesNotReviveStaleWork() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS.withEnabled(false));
        engine.chunkLoaded(new ChunkPos(SOURCE));
        engine.activate(SOURCE);
        engine.deactivate(SOURCE);
        engine.chunkUnloaded(new ChunkPos(SOURCE));
        engine.updateSettings(SETTINGS, 0);
        engine.tick(100);
        assertEquals(0, world.sourceReads);
        assertEquals(0, world.scannedBlocks);
        assertEquals(0, world.conversions);
    }

    private static void addParallelFronts(FakeWorld world, InfectionScheduler engine, int chunk) {
        for (int lane = 0; lane < 16; lane++) {
            BlockPos source = new BlockPos(chunk * 16 + 2 + lane % 4 * 3, 0, 2 + lane / 4 * 3);
            world.put(source, "source");
            for (int y = 1; y <= 80; y++) world.put(source.above(y), "tuff");
            engine.activate(source);
        }
    }

    @Test
    void growingFromOneToEightFrontsKeepsTheOriginalFrontsCadence() {
        FakeWorld world = new FakeWorld();
        var engine = InfectionApi.createScheduler(world, new InfectionSettings(4, 64, 16, 1, 20, 2048, 0, 8));
        addParallelFronts(world, engine, 0);
        engine.tick(0);
        for (int tick = 4; tick <= 20; tick += 4) engine.tick(tick);
        assertEquals(80, world.conversions);
        for (int chunk = 1; chunk < 8; chunk++) addParallelFronts(world, engine, chunk);
        for (int tick = 24; tick <= 100; tick += 4) {
            int converted = world.conversions, reads = world.sourceReads;
            engine.tick(tick);
            assertEquals(128, world.conversions - converted, "Growing fronts diluted the propagation cadence");
            assertTrue(world.sourceReads - reads <= 512);
        }
        assertEquals(400, world.conversionsByChunk.get(new ChunkPos(0, 0)));
        for (int chunk = 1; chunk < 8; chunk++) assertEquals(320, world.conversionsByChunk.get(new ChunkPos(chunk, 0)));
    }

    @Test
    void adaptiveCapacityRemainsBoundedAndSharesItsCeilingFairly() {
        FakeWorld world = new FakeWorld();
        var engine = InfectionApi.createScheduler(world, new InfectionSettings(4, 64, 16, 1, 20, 2048, 0, 8));
        for (int chunk = 0; chunk < 16; chunk++) addParallelFronts(world, engine, chunk);
        engine.tick(0);
        for (int tick = 4; tick <= 80; tick += 4) {
            int converted = world.conversions, reads = world.sourceReads;
            engine.tick(tick);
            assertEquals(128, world.conversions - converted);
            assertTrue(world.sourceReads - reads <= 512);
        }
        for (int chunk = 0; chunk < 16; chunk++) assertEquals(160, world.conversionsByChunk.get(new ChunkPos(chunk, 0)));
    }

    @Test
    void adaptiveCapacityCanChangeLiveWithoutPostponingWorkOrLosingSources() {
        FakeWorld world = new FakeWorld();
        var engine = InfectionApi.createScheduler(world, new InfectionSettings(4, 64, 16, 1, 20, 2048, 0, 1));
        for (int chunk = 0; chunk < 8; chunk++) addParallelFronts(world, engine, chunk);
        engine.tick(0);
        engine.tick(4);
        assertEquals(16, world.conversions);
        engine.updateSettings(new InfectionSettings(4, 64, 16, 1, 20, 2048, 0, 8), 6);
        engine.tick(8);
        assertEquals(144, world.conversions);
        engine.updateSettings(new InfectionSettings(4, 64, 16, 1, 20, 2048, 0, 1), 9);
        engine.tick(12);
        assertEquals(160, world.conversions);
    }

    @Test
    void adaptiveCapacityDoesNotMultiplyBudgetsForADiscoveryBacklog() {
        FakeWorld world = new FakeWorld();
        var engine = InfectionApi.createScheduler(world, new InfectionSettings(1, 64, 16, 1, 20, 2048, 0, 8));
        for (int y = 1; y <= 256; y++) world.put(new BlockPos(8, y, 8), "source");
        engine.chunkLoaded(new ChunkPos(SOURCE));
        engine.tick(0);
        engine.tick(20);
        assertEquals(64, world.sourceReads);
        world.put(SOURCE, "source"); world.put(SOURCE.east(), "tuff"); engine.activate(SOURCE);
        engine.tick(21);
        assertEquals(128, world.sourceReads);
        assertEquals(1, world.conversions);
    }

    @Test
    void adaptiveCapacityAlsoCompensatesWhenSourceChecksAreTheSmallerBudget() {
        FakeWorld world = new FakeWorld();
        var engine = InfectionApi.createScheduler(world, new InfectionSettings(4, 4, 16, 1, 20, 2048, 0, 8));
        addParallelFronts(world, engine, 0);
        engine.tick(0);
        for (int tick = 4; tick <= 20; tick += 4) {
            int converted = world.conversions, reads = world.sourceReads;
            engine.tick(tick);
            assertEquals(16, world.conversions - converted);
            assertTrue(world.sourceReads - reads <= 32);
        }
    }

    @Test
    void rejectsUnsafeAdaptiveCapacities() {
        for (int multiplier : new int[] {0, 17, Integer.MAX_VALUE})
            assertThrows(IllegalArgumentException.class, () -> new InfectionSettings(4, 64, 16, 1, 20, 2048, 0, multiplier));
        assertThrows(IllegalArgumentException.class, () -> new InfectionSettings(4, Integer.MAX_VALUE, 16, 1, 20, 2048, 0, 8));
    }

    @Test
    void passesBaseConversionChanceToWorldAndAppliesLiveUpdatesWithoutLosingFrontier() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.put(SOURCE.east(2), "deepslate");
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(100, 64, 8, 1, 20, 2048, 0.37));
        engine.activate(SOURCE);
        engine.tick(0);
        engine.tick(100);
        assertEquals(0.37, world.lastBaseChance);
        assertEquals(1, world.conversions);
        engine.updateSettings(new InfectionSettings(10, 64, 8, 1, 20, 2048, 1), 100);
        engine.tick(110);
        assertEquals(1, world.lastBaseChance);
        assertEquals(2, world.conversions);
    }

    @Test
    void rejectsInvalidBaseConversionProbabilities() {
        for (double chance : new double[] {-0.01, 1.01, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new InfectionSettings(100, 64, 8, 1, 20, 2048, chance));
        }
    }

    @Test
    void waitsForIntervalAndNewlyInfectedBlocksWaitForTheNextBatch() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.put(SOURCE.east(2), "deepslate");
        world.put(SOURCE.east(3), "stone");
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS);
        engine.activate(SOURCE);
        engine.tick(0);
        engine.tick(99);
        assertEquals(0, world.conversions);
        engine.tick(100);
        assertEquals("infected:tuff", world.blocks.get(SOURCE.east()));
        assertEquals("deepslate", world.blocks.get(SOURCE.east(2)));
        engine.tick(200);
        assertEquals("infected:deepslate", world.blocks.get(SOURCE.east(2)));
        engine.tick(300);
        assertEquals("stone", world.blocks.get(SOURCE.east(3)));
        assertEquals(2, world.conversions);
    }

    @Test
    void deduplicatesSourcesAndEnforcesBothBatchLimits() {
        FakeWorld world = new FakeWorld();
        InfectionSettings limits = new InfectionSettings(100, 4, 2, 1, 20, 2048);
        InfectionScheduler engine = InfectionApi.createScheduler(world, limits);
        for (int i = 0; i < 20; i++) {
            BlockPos pos = new BlockPos(i * 32 + 8, 0, 8);
            world.put(pos, "source");
            world.put(pos.east(), "tuff");
            for (int duplicate = 0; duplicate < 10; duplicate++) {
                engine.activate(pos);
            }
        }
        engine.tick(0);
        engine.tick(100);
        assertEquals(2, world.conversions);
        assertEquals(4, world.sourceReads);
        engine.tick(200);
        assertEquals(4, world.conversions);
        assertEquals(8, world.sourceReads);
    }

    @Test
    void aSingleSourceGetsOnlyOneAttemptPerBatch() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.put(SOURCE.west(), "tuff");
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS);
        for (int i = 0; i < 100; i++) engine.activate(SOURCE);
        engine.tick(0);
        engine.tick(100);
        assertEquals(1, world.sourceReads);
        assertEquals(1, world.conversions);
    }

    @Test
    void removedSourcesDoNotSpreadAndDormantSourcesCanWake() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS);
        engine.activate(SOURCE);
        engine.tick(0);
        engine.tick(100);
        int reads = world.sourceReads;
        engine.tick(200);
        assertEquals(reads, world.sourceReads);
        world.put(SOURCE.east(), "tuff");
        engine.activate(SOURCE);
        engine.tick(300);
        assertEquals(1, world.conversions);
        world.put(SOURCE.west(), "deepslate");
        world.put(SOURCE, "air");
        engine.tick(400);
        assertEquals("deepslate", world.blocks.get(SOURCE.west()));
    }

    @Test
    void unloadedChunksAreNeverReadAndReloadRebuildsSources() {
        FakeWorld world = new FakeWorld();
        BlockPos source = new BlockPos(15, 0, 8);
        world.put(source, "source");
        world.blocks.put(source.east(), "tuff");
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS);
        engine.activate(source);
        engine.tick(0);
        engine.tick(100);
        assertEquals(0, world.conversions);
        engine.chunkUnloaded(new ChunkPos(source));
        int reads = world.sourceReads;
        engine.tick(200);
        assertEquals(reads, world.sourceReads);
        world.loaded.add(new ChunkPos(source.east()));
        engine.chunkLoaded(new ChunkPos(source));
        engine.tick(220);
        engine.tick(300);
        assertEquals("infected:tuff", world.blocks.get(source.east()));
    }

    @Test
    void discoveryUsesAGlobalBudgetAndContinuesFromItsCursor() {
        FakeWorld world = new FakeWorld();
        world.scanLength = 10;
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        BlockPos other = SOURCE.offset(32, 0, 0);
        world.put(other, "source");
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(100, 64, 8, 1, 20, 3));
        engine.chunkLoaded(new ChunkPos(SOURCE));
        engine.chunkLoaded(new ChunkPos(other));
        engine.tick(0);
        engine.tick(20);
        assertEquals(3, world.scannedBlocks);
        engine.tick(40);
        assertEquals(6, world.scannedBlocks);
        engine.tick(60);
        assertEquals(9, world.scannedBlocks);
        for (int time = 80; time <= 200; time += 20) engine.tick(time);
        assertEquals("infected:tuff", world.blocks.get(SOURCE.east()));
        assertTrue(world.scannedBlocks <= 30);
    }

    @Test
    void nonTickingChunksPauseAndClearRemovesAllWork() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.ticking = false;
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS);
        engine.activate(SOURCE);
        engine.tick(0);
        engine.tick(100);
        assertEquals(0, world.conversions);
        world.ticking = true;
        engine.tick(200);
        assertEquals(1, world.conversions);
        world.put(SOURCE.west(), "deepslate");
        engine.clear();
        engine.tick(300);
        engine.tick(400);
        assertEquals("deepslate", world.blocks.get(SOURCE.west()));
    }

    @Test
    void zeroChanceDoesNotConvertAndInvalidSettingsAreRejected() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(100, 64, 8, 0, 20, 2048));
        engine.activate(SOURCE);
        engine.tick(0);
        engine.tick(100);
        assertEquals(0, world.conversions);
        assertThrows(IllegalArgumentException.class,
                () -> new InfectionSettings(0, 64, 8, 0.25, 20, 2048));
        assertThrows(IllegalArgumentException.class,
                () -> new InfectionSettings(100, 64, 8, Double.NaN, 20, 2048));
    }

    @Test
    void liveIntervalChangesKeepQueuedSourcesAndRescheduleTheNextBatch() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.put(SOURCE.east(2), "deepslate");
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS);
        engine.activate(SOURCE);
        engine.tick(0);
        engine.tick(40);
        engine.updateSettings(new InfectionSettings(10, 64, 8, 1, 20, 2048), 40);
        engine.tick(49);
        assertEquals(0, world.conversions);
        engine.tick(50);
        assertEquals(1, world.conversions);
        assertEquals("deepslate", world.blocks.get(SOURCE.east(2)));
        engine.tick(60);
        assertEquals(2, world.conversions);
    }

    @Test
    void liveProbabilityAndBudgetChangesDoNotPostponeAnUnchangedInterval() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.put(SOURCE.west(), "deepslate");
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(100, 64, 8, 0, 20, 2048));
        engine.activate(SOURCE);
        engine.tick(0);
        engine.updateSettings(new InfectionSettings(100, 64, 1, 1, 20, 2048), 50);
        engine.tick(100);
        assertEquals(1, world.conversions);
        // Reapplying the same settings, as a file watcher would, must not delay the next batch.
        engine.updateSettings(new InfectionSettings(100, 64, 1, 1, 20, 2048), 150);
        engine.tick(200);
        assertEquals(2, world.conversions);
    }

    @Test
    void liveSlowerIntervalsCancelThePreviousDeadline() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS);
        engine.activate(SOURCE);
        engine.tick(0);
        engine.updateSettings(new InfectionSettings(200, 64, 8, 1, 20, 2048), 70);
        engine.tick(100);
        engine.tick(269);
        assertEquals(0, world.conversions);
        engine.tick(270);
        assertEquals(1, world.conversions);
    }

    @Test
    void liveScanBudgetChangesPreserveThePartialChunkCursor() {
        FakeWorld world = new FakeWorld();
        world.scanLength = 10;
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(100, 64, 8, 1, 20, 3));
        engine.chunkLoaded(new ChunkPos(SOURCE));
        engine.tick(0);
        engine.tick(20);
        engine.updateSettings(new InfectionSettings(100, 64, 8, 1, 20, 4), 22);
        engine.tick(40);
        assertEquals(7, world.scannedBlocks);
        engine.tick(60);
        assertEquals(10, world.scannedBlocks);
        engine.tick(100);
        assertEquals(1, world.conversions);
    }

    @Test
    void discoveredSourcesDoNotDelayANewlyActivatedFrontier() {
        FakeWorld world = new FakeWorld();
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(1, 4, 2, 1, 20, 2048));
        for (int y = 1; y <= 128; y++) world.put(new BlockPos(8, y, 8), "source");
        engine.chunkLoaded(new ChunkPos(SOURCE));
        engine.tick(0);
        engine.tick(20);
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.put(SOURCE.east(2), "deepslate");
        engine.activate(SOURCE);
        engine.tick(21);
        assertEquals("infected:tuff", world.blocks.get(SOURCE.east()), "Discovery backlog delayed a changed source");
        engine.tick(22);
        assertEquals("infected:deepslate", world.blocks.get(SOURCE.east(2)), "New infection was buried in the discovery backlog");
    }

    @Test
    void nonTickingChunksDoNotConsumeActiveSourceBudget() {
        FakeWorld world = new FakeWorld();
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(1, 4, 2, 1, 20, 2048));
        for (int i = 1; i <= 80; i++) {
            BlockPos paused = SOURCE.offset(i * 32, 0, 0);
            world.put(paused, "source");
            world.put(paused.east(), "tuff");
            world.nonTicking.add(new ChunkPos(paused));
            engine.activate(paused);
        }
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        engine.activate(SOURCE);
        engine.tick(0);
        // Paused chunks are removed with bounded chunk checks, without spending the four source slots.
        for (int time = 1; time <= 3; time++) engine.tick(time);
        assertEquals("infected:tuff", world.blocks.get(SOURCE.east()));
        assertTrue(world.sourceReads <= 4, "Paused chunks consumed block-read work");
        world.nonTicking.clear();
        engine.tick(20);
        assertTrue(world.conversions > 1, "Paused sources did not resume after ticking restarted");
    }

    @Test
    void exhaustedConversionBudgetStillRetiresDormantSources() {
        FakeWorld world = new FakeWorld();
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(1, 4, 1, 1, 20, 2048));
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.put(SOURCE.east(2), "tuff");
        engine.activate(SOURCE);
        for (int i = 1; i <= 3; i++) {
            BlockPos dormant = SOURCE.offset(i * 32, 0, 0);
            world.put(dormant, "source");
            engine.activate(dormant);
        }
        engine.tick(0);
        engine.tick(1);
        int reads = world.sourceReads;
        engine.tick(2);
        assertEquals(1, world.sourceReads - reads, "Spent budgets kept dormant or exhausted sources in the queue");
        assertEquals(2, world.conversions);
    }

    @Test
    void worldHeightBoundaryDoesNotKeepDormantSourcesAwake() {
        FakeWorld world = new FakeWorld();
        world.minBuildHeight = 0;
        world.put(SOURCE, "source");
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(1, 4, 2, 1, 20, 2048));
        engine.activate(SOURCE);
        engine.tick(0);
        engine.tick(1);
        int reads = world.sourceReads;
        engine.tick(2);
        assertEquals(reads, world.sourceReads, "World height was treated as an unloaded neighbor");
    }

    @Test
    void neighborCallbacksCannotKeepAnExhaustedSourceAwakeOrSpreadInTheSameBatch() {
        FakeWorld world = new FakeWorld();
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.put(SOURCE.east(2), "deepslate");
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(1, 4, 2, 1, 20, 2048));
        world.onInfect = () -> {
            engine.activate(SOURCE);
            engine.activate(SOURCE.east());
        };
        engine.activate(SOURCE);
        engine.tick(0);
        engine.tick(1);
        assertEquals(1, world.conversions);
        int reads = world.sourceReads;
        engine.tick(2);
        assertEquals(1, world.sourceReads - reads);
        assertEquals(2, world.conversions);
    }

    @Test
    void continuousFrontierWorkDoesNotStarveDiscoveryEvenWithASingleSourceBudget() {
        for (int budget : new int[] {1, 4}) {
            FakeWorld world = new FakeWorld();
            InfectionScheduler engine = InfectionApi.createScheduler(world,
                    new InfectionSettings(1, budget, 1, 0, 20, 2048));
            for (int y = 1; y <= 128; y++) {
                BlockPos active = new BlockPos(8, y, 8);
                world.put(active, "source");
                world.put(active.east(), "tuff");
                engine.activate(active);
            }
            BlockPos discovered = SOURCE.offset(32, 0, 0);
            world.put(discovered, "source");
            engine.chunkLoaded(new ChunkPos(discovered));
            engine.tick(0);
            for (int time = 1; time <= 24; time++) engine.tick(time);
            assertTrue(world.observedSources.contains(discovered), "Continuous fronts starved discovery with budget " + budget);
        }
    }

    @Test
    void conversionLimitsDoNotFavorTheSameChunksWhenTheSourceBudgetCompletesWholeRounds() {
        FakeWorld world = new FakeWorld();
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(1, 64, 16, 1, 20, 2048));
        for (int chunk = 0; chunk < 32; chunk++) {
            for (int row = 0; row < 16; row++) {
                BlockPos source = new BlockPos(chunk * 16 + 8, row * 3, 8);
                world.put(source, "source");
                world.put(source.east(), "tuff");
                engine.activate(source);
            }
        }
        engine.tick(0);
        for (int tick = 1; tick <= 8; tick++) engine.tick(tick);
        assertEquals(128, world.conversions);
        assertTrue(world.sourceReads <= 8 * 64);
        for (int chunk = 0; chunk < 32; chunk++) {
            assertEquals(4, world.conversionsByChunk.getOrDefault(new ChunkPos(chunk, 0), 0),
                    "Chunk " + chunk + " did not receive its fair conversion share");
        }
    }

    @Test
    void aNewlyDiscoveredChunkReceivesConversionsDuringContinuousFrontierWork() {
        FakeWorld world = new FakeWorld();
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(1, 64, 1, 1, 20, 2048));
        for (int row = 0; row < 128; row++) {
            BlockPos source = new BlockPos(8, row * 3, 8);
            world.put(source, "source");
            world.put(source.east(), "tuff");
            engine.activate(source);
        }
        BlockPos discovered = SOURCE.offset(32, 0, 0);
        world.put(discovered, "source");
        world.put(discovered.east(), "tuff");
        engine.chunkLoaded(new ChunkPos(discovered));
        engine.tick(0);
        for (int tick = 1; tick <= 21; tick++) engine.tick(tick);
        assertEquals("infected:tuff", world.blocks.get(discovered.east()),
                "Discovery checks ran, but the frontier monopolized the conversion limit");
    }

    @Test
    void discoverySharesItsReadBudgetAcrossChunksInTheSameBatch() {
        FakeWorld world = new FakeWorld();
        world.scanLength = 4096;
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(100, 64, 16, 1, 20, 2048));
        for (int chunk = 0; chunk < 8; chunk++) {
            BlockPos source = new BlockPos(chunk * 32 + 8, 0, 8);
            world.put(source, "source");
            engine.chunkLoaded(new ChunkPos(source));
        }
        engine.tick(0);
        engine.tick(20);
        assertEquals(2048, world.scannedBlocks);
        for (int chunk = 0; chunk < 8; chunk++) {
            assertEquals(256, world.scannedByChunk.getOrDefault(new ChunkPos(chunk * 2, 0), 0),
                    "A chunk consumed the scan budget before the others were visited");
        }
    }

    @Test
    void sharedTargetsDoNotWasteASecondSourcesConversionOpportunity() {
        FakeWorld world = new FakeWorld();
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(1, 64, 16, 1, 20, 2048));
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        world.put(SOURCE.east(2), "source");
        world.put(SOURCE.east(3), "deepslate");
        engine.activate(SOURCE);
        engine.activate(SOURCE.east(2));
        engine.tick(0);
        engine.tick(1);
        assertEquals(2, world.conversions, "A cached neighbor was consumed by another source in the batch");
        assertEquals("infected:tuff", world.blocks.get(SOURCE.east()));
        assertEquals("infected:deepslate", world.blocks.get(SOURCE.east(3)));
        assertEquals(2, world.sourceReads, "Newly infected blocks spread in the same batch");
    }

    @Test
    void pausedScansDoNotReserveUnusedSharesOfTheReadBudget() {
        FakeWorld world = new FakeWorld();
        world.scanLength = 4096;
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(100, 64, 16, 1, 20, 2048));
        world.put(SOURCE, "source");
        engine.chunkLoaded(new ChunkPos(SOURCE));
        BlockPos paused = SOURCE.offset(32, 0, 0);
        world.put(paused, "source");
        world.nonTicking.add(new ChunkPos(paused));
        engine.chunkLoaded(new ChunkPos(paused));
        engine.tick(0);
        engine.tick(20);
        assertEquals(2048, world.scannedBlocks, "The paused chunk reserved an unusable scan share");
        assertEquals(0, world.scannedByChunk.getOrDefault(new ChunkPos(paused), 0));
    }

    @Test
    void discoveryDoesNotSpendBlockReadsOnNonTickingChunks() {
        FakeWorld world = new FakeWorld();
        world.scanLength = 4096;
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(100, 64, 16, 1, 20, 2048));
        BlockPos paused = SOURCE.offset(32, 0, 0);
        world.put(paused, "source");
        world.nonTicking.add(new ChunkPos(paused));
        engine.chunkLoaded(new ChunkPos(paused));
        world.put(SOURCE, "source");
        engine.chunkLoaded(new ChunkPos(SOURCE));
        engine.tick(0);
        engine.tick(20);
        assertEquals(0, world.scannedByChunk.getOrDefault(new ChunkPos(paused), 0));
        assertEquals(2048, world.scannedByChunk.getOrDefault(new ChunkPos(SOURCE), 0));
        world.nonTicking.clear();
        engine.tick(40);
        assertTrue(world.scannedByChunk.getOrDefault(new ChunkPos(paused), 0) > 0);
    }

    @Test
    void reloadedChunksAreScannedEveryTickUntilTheBacklogIsClear() {
        FakeWorld world = new FakeWorld();
        world.scanLength = 10;
        world.put(SOURCE, "source");
        world.put(SOURCE.east(), "tuff");
        InfectionScheduler engine = InfectionApi.createScheduler(world,
                new InfectionSettings(1, 64, 8, 1, 20, 3));
        engine.chunkLoaded(new ChunkPos(SOURCE));
        engine.tick(0);
        for (int time = 1; time < 20; time++) engine.tick(time);
        assertEquals(0, world.scannedBlocks, "The first scan still waits one interval after the engine starts");
        int[] expected = {3, 6, 9, 10};
        for (int step = 0; step < expected.length; step++) {
            engine.tick(20 + step);
            assertEquals(expected[step], world.scannedBlocks, "Discovery waited for the scan interval instead of the next tick");
        }
        engine.tick(24);
        assertEquals("infected:tuff", world.blocks.get(SOURCE.east()), "A reloaded front did not resume as soon as its chunk was scanned");
        int scanned = world.scannedBlocks;
        for (int time = 25; time <= 200; time++) engine.tick(time);
        assertEquals(scanned, world.scannedBlocks, "A finished chunk was scanned again");
    }

    @Test
    void discoveryStopsPollingChunksThatAreLoadedButNotTicking() {
        FakeWorld world = new FakeWorld();
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS);
        for (int chunk = 0; chunk < 3; chunk++) {
            BlockPos waiting = SOURCE.offset(chunk * 32, 0, 0);
            world.put(waiting, "source");
            world.nonTicking.add(new ChunkPos(waiting));
            engine.chunkLoaded(new ChunkPos(waiting));
        }
        engine.tick(0);
        engine.tick(20);
        int checks = world.tickingChecks;
        assertTrue(checks > 0);
        for (int time = 21; time < 40; time++) engine.tick(time);
        assertEquals(checks, world.tickingChecks, "Chunks that cannot be scanned yet were polled every tick");
        world.nonTicking.clear();
        engine.tick(40);
        assertEquals(3, world.scannedBlocks);
    }

    @Test
    void aTickingChunkBehindManyWaitingOnesIsReachedWithinTicksNotIntervals() {
        FakeWorld world = new FakeWorld();
        InfectionScheduler engine = InfectionApi.createScheduler(world, SETTINGS);
        for (int chunk = 1; chunk <= 100; chunk++) {
            BlockPos waiting = SOURCE.offset(chunk * 32, 0, 0);
            world.put(waiting, "source");
            world.nonTicking.add(new ChunkPos(waiting));
            engine.chunkLoaded(new ChunkPos(waiting));
        }
        world.put(SOURCE, "source");
        engine.chunkLoaded(new ChunkPos(SOURCE));
        engine.tick(0);
        for (int time = 20; time <= 24; time++) engine.tick(time);
        assertEquals(1, world.scannedByChunk.getOrDefault(new ChunkPos(SOURCE), 0),
                "The only scannable chunk waited behind chunks that are not ticking");
    }

    private static final class FakeWorld implements InfectionWorld {
        int tickingChecks;
        final Map<BlockPos, String> blocks = new HashMap<>();
        final Set<ChunkPos> loaded = new HashSet<>();
        final Set<ChunkPos> nonTicking = new HashSet<>();
        final Set<BlockPos> observedSources = new HashSet<>();
        final Map<ChunkPos, Integer> conversionsByChunk = new HashMap<>();
        final Map<ChunkPos, Integer> scannedByChunk = new HashMap<>();
        int conversions;
        double lastBaseChance;
        int sourceReads;
        int scannedBlocks;
        int scanLength = 1;
        boolean ticking = true;
        int minBuildHeight = Integer.MIN_VALUE;
        Runnable onInfect = () -> {};

        void put(BlockPos pos, String block) {
            blocks.put(pos, block);
            loaded.add(new ChunkPos(pos));
        }

        private String read(BlockPos pos) {
            assertTrue(isLoaded(pos), "A block was read in an unloaded chunk");
            return blocks.getOrDefault(pos, "air");
        }

        public boolean isInBounds(BlockPos pos) { return pos.getY() >= minBuildHeight; }
        public boolean isLoaded(BlockPos pos) { return isInBounds(pos) && loaded.contains(new ChunkPos(pos)); }
        public boolean isTicking(BlockPos pos) { tickingChecks++; return ticking && !nonTicking.contains(new ChunkPos(pos)); }
        public boolean isSource(BlockPos pos) {
            sourceReads++;
            observedSources.add(pos);
            String block = read(pos);
            return block.equals("source") || block.startsWith("infected:");
        }
        public boolean canInfect(BlockPos pos) {
            String block = read(pos);
            return block.equals("tuff") || block.equals("deepslate");
        }
        public boolean tryInfect(BlockPos pos, double baseCorruptionChance) {
            lastBaseChance = baseCorruptionChance;
            return tryInfect(pos);
        }
        public boolean tryInfect(BlockPos pos) {
            if (!canInfect(pos)) return false;
            blocks.put(pos, "infected:" + read(pos));
            conversions++;
            conversionsByChunk.merge(new ChunkPos(pos), 1, Integer::sum);
            onInfect.run();
            return true;
        }
        public double randomDouble() { return 0; }
        public int randomInt(int bound) { return 0; }
        public ScanResult scanSources(ChunkPos chunk, int cursor, int maxBlocks) {
            int inspected = Math.min(maxBlocks, scanLength - cursor);
            scannedBlocks += inspected;
            scannedByChunk.merge(chunk, inspected, Integer::sum);
            int next = cursor + inspected;
            List<BlockPos> found = new ArrayList<>();
            if (next == scanLength) {
                blocks.forEach((pos, block) -> {
                    if (new ChunkPos(pos).equals(chunk) && (block.equals("source") || block.startsWith("infected:"))) {
                        found.add(pos);
                    }
                });
            }
            return new ScanResult(found, next, inspected, next == scanLength);
        }
    }
}
