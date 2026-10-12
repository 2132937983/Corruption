package com.miophas.corruption.core.infection;

import com.miophas.corruption.api.infection.InfectionScheduler;
import com.miophas.corruption.api.infection.InfectionSettings;
import com.miophas.corruption.api.infection.InfectionWorld;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;

/** Bounded, round-robin infection work. No NeoForge events or concrete mod blocks live here. */
public final class InfectionEngine implements InfectionScheduler {
    private static final Direction[] NEIGHBORS = Direction.values();
    private static final int SCAN_CHUNKS_PER_BATCH = 32;
    private final InfectionWorld world;
    private InfectionSettings settings;
    private final Map<ChunkPos, ChunkState> chunks = new HashMap<>();
    private final LinkedHashSet<ChunkPos> pendingScans = new LinkedHashSet<>();
    private final LinkedHashSet<ChunkPos> activeChunks = new LinkedHashSet<>();
    private final LinkedHashSet<ChunkPos> discoveryChunks = new LinkedHashSet<>();
    private final LinkedHashSet<ChunkPos> pausedChunks = new LinkedHashSet<>();
    private long nextScanTick = -1;
    private long nextResumeTick = -1;
    private long nextSpreadTick = -1;
    private int idleScanVisits;
    private int discoveryPhase;
    private long conversionOrder;

    public InfectionEngine(InfectionWorld world, InfectionSettings settings) {
        this.world = Objects.requireNonNull(world);
        this.settings = Objects.requireNonNull(settings);
    }

    @Override
    public void chunkLoaded(ChunkPos chunk) {
        chunks.computeIfAbsent(chunk, ignored -> new ChunkState());
        requestScan(chunk);
        // Revisit loaded neighbors so previously dormant border sources can wake up.
        requestScan(new ChunkPos(chunk.x - 1, chunk.z));
        requestScan(new ChunkPos(chunk.x + 1, chunk.z));
        requestScan(new ChunkPos(chunk.x, chunk.z - 1));
        requestScan(new ChunkPos(chunk.x, chunk.z + 1));
    }

    private void requestScan(ChunkPos chunk) {
        ChunkState state = chunks.get(chunk);
        if (state != null && pendingScans.add(chunk)) {
            state.scanCursor = 0;
        }
    }

    @Override
    public void chunkUnloaded(ChunkPos chunk) {
        chunks.remove(chunk);
        pendingScans.remove(chunk);
        activeChunks.remove(chunk);
        discoveryChunks.remove(chunk);
        pausedChunks.remove(chunk);
    }

    @Override
    public void activate(BlockPos source) {
        ChunkPos chunk = new ChunkPos(source);
        ChunkState state = chunks.computeIfAbsent(chunk, ignored -> new ChunkState());
        state.discoveredSources.remove(source);
        if (state.discoveredSources.isEmpty()) discoveryChunks.remove(chunk);
        state.sources.add(source.immutable());
        if (!pausedChunks.contains(chunk)) activeChunks.add(chunk);
    }

    private void discover(BlockPos source) {
        ChunkPos chunk = new ChunkPos(source);
        ChunkState state = chunks.get(chunk);
        if (!state.sources.contains(source)) {
            state.discoveredSources.add(source.immutable());
            if (!pausedChunks.contains(chunk)) discoveryChunks.add(chunk);
        }
    }

    @Override
    public void deactivate(BlockPos source) {
        ChunkPos chunk = new ChunkPos(source);
        ChunkState state = chunks.get(chunk);
        if (state != null) {
            state.sources.remove(source);
            state.discoveredSources.remove(source);
            if (state.sources.isEmpty()) {
                activeChunks.remove(chunk);
            }
            if (state.discoveredSources.isEmpty()) discoveryChunks.remove(chunk);
            if (state.sources.isEmpty() && state.discoveredSources.isEmpty()) pausedChunks.remove(chunk);
        }
    }

    @Override
    public void tick(long gameTime) {
        // Keep load/source bookkeeping while paused, but perform no world scans or conversions.
        if (!settings.enabled()) return;
        if (nextScanTick < 0) {
            nextScanTick = nextResumeTick = gameTime + settings.scanIntervalTicks();
        }
        if (nextSpreadTick < 0) {
            nextSpreadTick = gameTime + settings.spreadIntervalTicks();
        }
        if (gameTime >= nextResumeTick) {
            nextResumeTick = gameTime + settings.scanIntervalTicks();
            resumeChunks();
        }
        if (gameTime >= nextScanTick) {
            // A reloaded world has every chunk waiting. Scanning one batch per interval left fronts idle for
            // minutes, so the backlog is worked every tick and only an idle queue waits a whole interval.
            nextScanTick = gameTime + (discoverSources() ? 1 : settings.scanIntervalTicks());
        }
        if (gameTime >= nextSpreadTick) {
            nextSpreadTick = gameTime + settings.spreadIntervalTicks();
            spread();
        }
    }

    @Override
    public void updateSettings(InfectionSettings updated, long gameTime) {
        Objects.requireNonNull(updated);
        if (settings.enabled() != updated.enabled()) {
            nextSpreadTick = gameTime + updated.spreadIntervalTicks();
            nextScanTick = nextResumeTick = gameTime + updated.scanIntervalTicks();
        }
        if (settings.spreadIntervalTicks() != updated.spreadIntervalTicks()) {
            nextSpreadTick = gameTime + updated.spreadIntervalTicks();
        }
        if (settings.scanIntervalTicks() != updated.scanIntervalTicks()) {
            nextScanTick = nextResumeTick = gameTime + updated.scanIntervalTicks();
        }
        settings = updated;
    }

    /** Returns whether a pass on the next tick could still find work. */
    private boolean discoverSources() {
        if (pendingScans.isEmpty()) return false;
        int remaining = settings.scanBlocksPerBatch();
        int visits = Math.min(SCAN_CHUNKS_PER_BATCH, pendingScans.size());
        int visited = visits, scanned = 0;
        Set<ChunkPos> ticking = new HashSet<>();
        var upcoming = pendingScans.iterator();
        for (int checked = 0; checked < visits; checked++) {
            ChunkPos chunk = upcoming.next();
            if (world.isTicking(new BlockPos(chunk.getMinBlockX(), 0, chunk.getMinBlockZ()))) ticking.add(chunk);
        }
        while (remaining > 0 && visits > 0 && !pendingScans.isEmpty()) {
            visits--;
            ChunkPos chunk = takeFirst(pendingScans);
            ChunkState state = chunks.get(chunk);
            // Loading/view distance can be larger than simulation distance. Discover ticking chunks first.
            if (!ticking.remove(chunk)) {
                pendingScans.add(chunk);
                continue;
            }
            int share = Math.ceilDiv(remaining, ticking.size() + 1);
            InfectionWorld.ScanResult result = world.scanSources(chunk, state.scanCursor, share);
            result.sources().forEach(this::discover);
            state.scanCursor = result.nextCursor();
            remaining -= result.inspectedBlocks();
            scanned++;
            if (!result.complete()) {
                pendingScans.add(chunk);
            }
        }
        if (scanned > 0) {
            idleScanVisits = 0;
            return true;
        }
        // Chunks loaded but not ticking stay queued. Keep rotating until every one has been looked at once,
        // then stop polling them each tick.
        idleScanVisits += visited;
        if (idleScanVisits < pendingScans.size()) return true;
        idleScanVisits = 0;
        return false;
    }

    private void resumeChunks() {
        int visits = Math.min(SCAN_CHUNKS_PER_BATCH, pausedChunks.size());
        while (visits-- > 0) {
            ChunkPos chunk = takeFirst(pausedChunks);
            ChunkState state = chunks.get(chunk);
            BlockPos source = !state.sources.isEmpty() ? state.sources.iterator().next()
                    : state.discoveredSources.iterator().next();
            if (world.isTicking(source)) {
                if (!state.sources.isEmpty()) activeChunks.add(chunk);
                if (!state.discoveredSources.isEmpty()) discoveryChunks.add(chunk);
            } else {
                pausedChunks.add(chunk);
            }
        }
    }

    private int spreadMultiplier() {
        // Count queued frontier work, not all corrupted blocks or all loaded chunks.
        // Stop at the configured ceiling: counting is bounded even with enormous frontiers.
        int baseCapacity = Math.min(settings.sourcesPerBatch(), settings.conversionsPerBatch());
        int ceiling = baseCapacity * settings.maxSpreadMultiplier();
        long queued = 0;
        for (ChunkPos chunk : activeChunks) {
            queued += chunks.get(chunk).sources.size();
            if (queued >= ceiling) return settings.maxSpreadMultiplier();
        }
        return Math.max(1, (int) Math.ceilDiv(queued, baseCapacity));
    }

    private void spread() {
        int multiplier = spreadMultiplier();
        int sourceBudget = settings.sourcesPerBatch() * multiplier;
        int conversionBudget = settings.conversionsPerBatch() * multiplier;
        // Capture the batch first: each source runs once, and new sources wait until the next batch.
        List<BlockPos> batch = new ArrayList<>(sourceBudget);
        // Changed and newly infected sources take precedence over a chunk's initial discovery backlog.
        // Stop checking after a bounded number of chunks, even if all of them are paused.
        long chunkVisits = (long) sourceBudget + SCAN_CHUNKS_PER_BATCH;
        discoveryPhase = (discoveryPhase + 1) & 3;
        int discoverySlots = Math.max(sourceBudget / 4, discoveryPhase == 0 ? 1 : 0);
        int frontierCount = 0;
        while (batch.size() < sourceBudget && chunkVisits-- > 0
                && (!activeChunks.isEmpty() || !discoveryChunks.isEmpty())) {
            // Reserve some work for discovering other fronts, even during continuous spread.
            boolean frontier = !activeChunks.isEmpty()
                    && (frontierCount < sourceBudget - discoverySlots || discoveryChunks.isEmpty());
            LinkedHashSet<ChunkPos> queue = frontier ? activeChunks : discoveryChunks;
            ChunkPos chunk = takeFirst(queue);
            ChunkState state = chunks.get(chunk);
            LinkedHashSet<BlockPos> sources = frontier ? state.sources : state.discoveredSources;
            if (!world.isTicking(sources.iterator().next())) {
                activeChunks.remove(chunk);
                discoveryChunks.remove(chunk);
                pausedChunks.add(chunk);
                continue;
            }
            batch.add(takeFirst(sources));
            if (frontier) frontierCount++;
            if (!sources.isEmpty()) {
                queue.add(chunk);
            }
        }

        Map<ChunkPos, ChunkCandidates> eligible = new HashMap<>();
        for (BlockPos source : batch) {
            if (!world.isLoaded(source) || !world.isSource(source)) {
                deactivate(source);
                continue;
            }

            List<BlockPos> targets = new ArrayList<>(6);
            boolean unloadedNeighbor = false;
            for (Direction direction : NEIGHBORS) {
                BlockPos target = source.relative(direction);
                if (!world.isInBounds(target)) continue;
                if (!world.isLoaded(target)) {
                    unloadedNeighbor = true;
                } else if (world.canInfect(target)) {
                    targets.add(target);
                }
            }
            if (!targets.isEmpty()) {
                activate(source);
                ChunkPos chunk = new ChunkPos(source);
                ChunkCandidates candidates = eligible.computeIfAbsent(chunk,
                        ignored -> new ChunkCandidates(chunks.get(chunk), eligible.size()));
                candidates.sources.addLast(new SpreadCandidate(source, targets, unloadedNeighbor));
            } else if (unloadedNeighbor) {
                deactivate(source);
                discover(source);
            } else {
                deactivate(source);
            }
            // Fully surrounded sources sleep until a neighbor change or a chunk reload wakes them.
        }

        if (settings.spreadChance() == 0) return;
        // Checking sources fairly is not enough: a truncated batch can repeatedly convert only
        // its first chunks. Persist the last serviced order separately from the source queues.
        PriorityQueue<ChunkCandidates> turns = new PriorityQueue<>(Comparator
                .comparingLong((ChunkCandidates candidates) -> candidates.state.lastConversionOrder)
                .thenComparingInt(candidates -> candidates.batchOrder));
        turns.addAll(eligible.values());
        int converted = 0;
        while (converted < conversionBudget && !turns.isEmpty()) {
            ChunkCandidates candidates = turns.remove();
            SpreadCandidate candidate = candidates.sources.removeFirst();
            candidates.state.lastConversionOrder = ++conversionOrder;
            boolean infected = false;
            if (world.randomDouble() < settings.spreadChance()) {
                int firstTarget = world.randomInt(candidate.targets().size());
                // Another source in this batch may have consumed the chosen neighbor. Retry the
                // remaining cached neighbors, still with at most one conversion for this source.
                for (int offset = 0; offset < candidate.targets().size(); offset++) {
                    BlockPos target = candidate.targets().get((firstTarget + offset) % candidate.targets().size());
                    if (world.tryInfect(target, settings.baseCorruptionChance())) {
                        converted++;
                        infected = true;
                        activate(target);
                        break;
                    }
                }
            }
            // Neighbor callbacks can reactivate a source. Retire it after consuming its last target.
            if (infected && candidate.targets().size() == 1 && !candidate.unloadedNeighbor()) {
                deactivate(candidate.source());
            }
            if (!candidates.sources.isEmpty()) turns.add(candidates);
        }
    }

    private static <T> T takeFirst(LinkedHashSet<T> queue) {
        var iterator = queue.iterator();
        T first = iterator.next();
        iterator.remove();
        return first;
    }

    @Override
    public void clear() {
        chunks.clear();
        pendingScans.clear();
        activeChunks.clear();
        discoveryChunks.clear();
        pausedChunks.clear();
        nextScanTick = -1;
        nextResumeTick = -1;
        nextSpreadTick = -1;
        idleScanVisits = 0;
        discoveryPhase = 0;
        conversionOrder = 0;
    }

    private static final class ChunkState {
        final LinkedHashSet<BlockPos> sources = new LinkedHashSet<>();
        final LinkedHashSet<BlockPos> discoveredSources = new LinkedHashSet<>();
        int scanCursor;
        long lastConversionOrder;
    }

    private record SpreadCandidate(BlockPos source, List<BlockPos> targets, boolean unloadedNeighbor) {}

    private static final class ChunkCandidates {
        final ChunkState state;
        final int batchOrder;
        final ArrayDeque<SpreadCandidate> sources = new ArrayDeque<>();

        ChunkCandidates(ChunkState state, int batchOrder) {
            this.state = state;
            this.batchOrder = batchOrder;
        }
    }
}
