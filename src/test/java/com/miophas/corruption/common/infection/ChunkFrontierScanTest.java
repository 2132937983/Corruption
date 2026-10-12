package com.miophas.corruption.common.infection;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.infection.InfectionWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.junit.jupiter.api.Test;

/** Scans real chunk sections, so it checks the code the server actually runs after a reload. */
class ChunkFrontierScanTest {
    private static final ChunkPos CHUNK = new ChunkPos(3, -2);
    private static final BlockState STONE = Blocks.STONE.defaultBlockState(), INERT = Blocks.BARRIER.defaultBlockState();
    private static final int MIN_SECTION = -4;

    private static BlockState corruption() { return ModBlocks.CORRUPTION.get().defaultBlockState(); }

    private static LevelChunkSection section(BlockState fill) {
        var section = new LevelChunkSection(new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(),
                PalettedContainer.Strategy.SECTION_STATES), null);
        if (!fill.isAir()) for (int i = 0; i < 4096; i++) section.setBlockState(i & 15, i >> 8, i >> 4 & 15, fill, false);
        return section;
    }

    private static List<BlockPos> scanAll(LevelChunkSection[] sections, int budget, ChunkFrontierScan.Outside outside, int[] spent) {
        List<BlockPos> found = new ArrayList<>();
        int cursor = 0;
        for (int calls = 0; calls < 100_000; calls++) {
            InfectionWorld.ScanResult result = ChunkFrontierScan.scan(sections, MIN_SECTION, CHUNK, cursor, budget, EmptyBlockGetter.INSTANCE, outside);
            assertTrue(result.inspectedBlocks() <= budget, "A call spent more than it was given");
            assertTrue(result.complete() || result.nextCursor() > cursor, "A call made no progress");
            found.addAll(result.sources());
            spent[0] += result.inspectedBlocks();
            cursor = result.nextCursor();
            if (result.complete()) return found;
        }
        return fail("The scan never completed");
    }

    @Test
    void stoneCanBeInfectedButBarriersAndCorruptionCannot() {
        assertTrue(CorruptionConversions.canConvert(STONE), "These tests rely on stone being infectable");
        assertFalse(CorruptionConversions.canConvert(INERT));
        assertFalse(CorruptionConversions.canConvert(corruption()));
    }

    @Test
    void aSolidInfectionReportsOnlyTheBlocksThatTouchSomethingInfectable() {
        // Air below, two solid corrupted sections, stone above; stone beyond the east edge, barriers elsewhere.
        LevelChunkSection[] sections = {section(Blocks.AIR.defaultBlockState()), section(corruption()), section(corruption()), section(STONE)};
        int[] spent = {0};
        List<BlockPos> found = scanAll(sections, 2048, pos -> pos.getX() > CHUNK.getMaxBlockX() ? STONE : INERT, spent);
        Set<BlockPos> expected = new HashSet<>();
        int low = (MIN_SECTION + 1) << 4, high = ((MIN_SECTION + 3) << 4) - 1;
        for (int y = low; y <= high; y++) for (int i = 0; i < 16; i++) expected.add(new BlockPos(CHUNK.getMaxBlockX(), y, CHUNK.getMinBlockZ() + i));
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) expected.add(new BlockPos(CHUNK.getMinBlockX() + x, high, CHUNK.getMinBlockZ() + z));
        assertEquals(expected, new HashSet<>(found));
        assertEquals(found.size(), new HashSet<>(found).size(), "A block was reported twice");
        assertEquals(752, found.size(), "8192 corrupted blocks, of which only the east face and the top layer can spread");
        assertEquals(8192, spent[0], "Each corrupted block costs one unit; the air and stone sections cost nothing");
    }

    @Test
    void aBlockBesideAnUnloadedChunkIsReportedBecauseNothingIsKnownAboutItsNeighbor() {
        LevelChunkSection[] sections = {section(corruption())};
        List<BlockPos> found = scanAll(sections, 4096, pos -> pos.getZ() < CHUNK.getMinBlockZ() ? null : INERT, new int[1]);
        assertEquals(256, found.size());
        for (BlockPos pos : found) assertEquals(CHUNK.getMinBlockZ(), pos.getZ());
    }

    @Test
    void worldHeightAirAndCorruptionAreNotTreatedAsPlacesToSpread() {
        LevelChunkSection[] sections = {section(corruption())};
        assertTrue(scanAll(sections, 4096, pos -> corruption(), new int[1]).isEmpty());
        assertTrue(scanAll(sections, 4096, pos -> Blocks.AIR.defaultBlockState(), new int[1]).isEmpty());
    }

    @Test
    void aLoneCorruptedBlockInsideStoneIsFoundAndUncorruptedBlocksCostASixteenth() {
        LevelChunkSection[] sections = {section(STONE), section(STONE)};
        sections[1].setBlockState(15, 15, 15, corruption(), false);
        int[] spent = {0};
        List<BlockPos> found = scanAll(sections, 2048, pos -> INERT, spent);
        assertEquals(List.of(new BlockPos(CHUNK.getMaxBlockX(), ((MIN_SECTION + 2) << 4) - 1, CHUNK.getMaxBlockZ())), found);
        // The first section holds no corruption and is skipped; the second costs 4095 reads and one corrupted block.
        assertEquals(Math.ceilDiv(4095 + 16, 16), spent[0]);
    }

    @Test
    void theScanResumesFromItsCursorAndFindsTheSameBlocksWithAnyBudget() {
        LevelChunkSection[] sections = {section(STONE), section(Blocks.AIR.defaultBlockState()), section(STONE)};
        for (int i = 0; i < 4096; i += 7) sections[0].setBlockState(i & 15, i >> 8, i >> 4 & 15, corruption(), false);
        for (int i = 0; i < 4096; i += 13) sections[2].setBlockState(i & 15, i >> 8, i >> 4 & 15, corruption(), false);
        ChunkFrontierScan.Outside outside = pos -> STONE;
        Set<BlockPos> whole = new HashSet<>(scanAll(sections, 1_000_000, outside, new int[1]));
        assertFalse(whole.isEmpty());
        for (int budget : new int[] {1, 3, 64, 257}) {
            List<BlockPos> pieces = scanAll(sections, budget, outside, new int[1]);
            assertEquals(pieces.size(), new HashSet<>(pieces).size(), "Budget " + budget + " reported a block twice");
            assertEquals(whole, new HashSet<>(pieces), "Budget " + budget + " changed what the scan finds");
        }
    }
}
