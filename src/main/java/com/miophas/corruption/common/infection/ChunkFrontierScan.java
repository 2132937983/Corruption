package com.miophas.corruption.common.infection;

import com.miophas.corruption.api.infection.InfectionWorld;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

/**
 * Finds the corrupted blocks of a chunk that still have somewhere to spread, reading sections directly.
 *
 * <p>A large infection is almost entirely interior: blocks whose six neighbours are corrupted too. Reporting
 * those would fill the scheduler with millions of sources that can never do anything, so they are rejected
 * here with six array reads each. They wake through neighbour callbacks if their surroundings change.
 */
final class ChunkFrontierScan {
    /** The state at a position outside the scanned chunk, or null when that chunk is not loaded. */
    @FunctionalInterface
    interface Outside { BlockState state(BlockPos pos); }

    private static final Direction[] NEIGHBORS = Direction.values();
    private static final int BLOCKS_PER_SECTION = 4096;
    /** A corrupted block costs one unit of the scan budget because its neighbours are checked; any other block is a single read. */
    private static final int SOURCE_COST = 16, READ_COST = 1;

    /** The cursor counts blocks from the lowest section upward; sections without corruption are skipped for free. */
    static InfectionWorld.ScanResult scan(LevelChunkSection[] sections, int minSectionY, ChunkPos chunk, int cursor, int maxBlocks,
            BlockGetter level, Outside outside) {
        List<BlockPos> frontier = new ArrayList<>();
        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();
        int total = sections.length * BLOCKS_PER_SECTION;
        long budget = (long) maxBlocks * SOURCE_COST, spent = 0;
        while (cursor < total && spent < budget) {
            int index = cursor / BLOCKS_PER_SECTION;
            LevelChunkSection section = sections[index];
            if (section.hasOnlyAir() || !section.maybeHas(CorruptionConversions::isSource)) {
                cursor = (index + 1) * BLOCKS_PER_SECTION;
                continue;
            }
            int baseY = (minSectionY + index) << 4;
            int local = cursor % BLOCKS_PER_SECTION;
            for (; local < BLOCKS_PER_SECTION && spent < budget; local++) {
                int x = local & 15, z = local >> 4 & 15, y = local >> 8;
                if (!CorruptionConversions.isSource(section.getBlockState(x, y, z))) {
                    spent += READ_COST;
                    continue;
                }
                spent += SOURCE_COST;
                int worldX = chunk.getMinBlockX() + x, worldY = baseY + y, worldZ = chunk.getMinBlockZ() + z;
                if (canSpread(sections, index, x, y, z, worldX, worldY, worldZ, level, outside, neighbor)) {
                    frontier.add(new BlockPos(worldX, worldY, worldZ));
                }
            }
            cursor = index * BLOCKS_PER_SECTION + local;
        }
        return new InfectionWorld.ScanResult(frontier, cursor, (int) Math.min(maxBlocks, Math.ceilDiv(spent, SOURCE_COST)), cursor >= total);
    }

    private static boolean canSpread(LevelChunkSection[] sections, int index, int x, int y, int z, int worldX, int worldY, int worldZ,
            BlockGetter level, Outside outside, BlockPos.MutableBlockPos neighbor) {
        for (Direction direction : NEIGHBORS) {
            int nx = x + direction.getStepX(), ny = y + direction.getStepY(), nz = z + direction.getStepZ();
            neighbor.set(worldX + direction.getStepX(), worldY + direction.getStepY(), worldZ + direction.getStepZ());
            BlockState state;
            if ((nx | nz) >> 4 != 0) {
                state = outside.state(neighbor);
                // Nothing is known about an unloaded neighbour, so the scheduler must look again once it loads.
                if (state == null) return true;
            } else if (ny >> 4 == 0) {
                state = sections[index].getBlockState(nx, ny, nz);
            } else {
                int other = index + direction.getStepY();
                if (other < 0 || other >= sections.length) continue;
                state = sections[other].getBlockState(nx, ny & 15, nz);
            }
            if (!state.isAir() && !CorruptionConversions.isCorrupted(state) && CorruptionConversions.canConvert(state, level, neighbor)) return true;
        }
        return false;
    }

    private ChunkFrontierScan() {}
}
