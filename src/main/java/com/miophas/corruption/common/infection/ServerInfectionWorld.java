package com.miophas.corruption.common.infection;

import com.miophas.corruption.api.infection.InfectionWorld;
import com.miophas.corruption.common.block.CorruptionBlock;
import com.miophas.corruption.common.blockentity.InfectedBlockEntity;
import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public final class ServerInfectionWorld implements InfectionWorld {
    private final ServerLevel level;

    public ServerInfectionWorld(ServerLevel level) {
        this.level = level;
    }

    private LevelChunk loadedChunk(BlockPos pos) {
        return level.isOutsideBuildHeight(pos) ? null
                : level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public boolean isInBounds(BlockPos pos) {
        return !level.isOutsideBuildHeight(pos);
    }

    @Override
    public boolean isLoaded(BlockPos pos) {
        return loadedChunk(pos) != null;
    }

    @Override
    public boolean isTicking(BlockPos pos) {
        return level.shouldTickBlocksAt(new ChunkPos(pos).toLong());
    }

    private static boolean isSourceState(BlockState state) {
        return CorruptionConversions.isSource(state);
    }

    @Override
    public boolean isSource(BlockPos pos) {
        LevelChunk chunk = loadedChunk(pos);
        return chunk != null && isSourceState(chunk.getBlockState(pos)) && !DormantGround.isDormant(chunk, pos);
    }

    @Override
    public boolean canInfect(BlockPos pos) {
        LevelChunk chunk = loadedChunk(pos);
        return chunk != null && CorruptionConversions.canConvert(chunk.getBlockState(pos), level, pos);
    }

    @Override
    public boolean tryInfect(BlockPos pos) {
        return tryInfect(pos, InfectionConfig.settings().baseCorruptionChance());
    }

    @Override
    public boolean tryInfect(BlockPos pos, double baseCorruptionChance) {
        LevelChunk chunk = loadedChunk(pos);
        if (chunk == null) {
            return false;
        }
        BlockState previous = chunk.getBlockState(pos);
        if (!CorruptionConversions.canConvert(previous, level, pos)) {
            return false;
        }
        var custom = com.miophas.corruption.api.infection.InfectionExtensions.conversion(level, pos, previous);
        BlockState ore = custom.isPresent() ? java.util.Objects.requireNonNull(custom.get().convert(level, pos, previous), "Conversion returned null")
                : CorruptionOreConversions.convert(previous, level, pos);
        boolean fullSolid = previous.getFluidState().isEmpty() && !previous.hasBlockEntity()
                && !(previous.getBlock() instanceof net.minecraft.world.level.block.SlabBlock)
                && previous.canOcclude() && previous.isCollisionShapeFullBlock(level, pos);
        boolean malice = ore == null && fullSolid
                && com.miophas.corruption.api.corruption.MaliceApi.succeeds(level.random.nextDouble(), InfectionConfig.maliceChance());
        boolean useBase = ore == null && fullSolid && baseCorruptionChance > 0
                && (baseCorruptionChance >= 1 || level.random.nextDouble() < baseCorruptionChance);
        BlockState replacement = ore != null ? ore : malice ? ModBlocks.MALICE.get().defaultBlockState() : useBase ? ModBlocks.CORRUPTION.get().defaultBlockState()
                : CorruptionConversions.convert(previous);
        // Install both parts before shape notifications: vanilla neighbors otherwise remove
        // the remaining half as soon as its partner changes block type.
        BlockPos partnerPos = pairedPosition(previous, pos);
        if (partnerPos != null && loadedChunk(partnerPos) == null) return false;
        BlockState partner = partnerPos != null && loadedChunk(partnerPos) != null ? level.getBlockState(partnerPos) : null;
        BlockState paired = null;
        if (partner != null && partner.getBlock() == previous.getBlock()) {
            if (custom.isPresent()) {
                if (custom.get().matches(level, partnerPos, partner))
                    paired = java.util.Objects.requireNonNull(custom.get().convert(level, partnerPos, partner), "Conversion returned null");
            } else paired = com.miophas.corruption.common.registry.GeneratedCorruption.convert(partner);
        }
        if (paired != null) {
            if (!level.setBlock(pos, replacement, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE)) return false;
            level.setBlock(partnerPos, paired, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            replacement.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
            paired.updateNeighbourShapes(level, partnerPos, Block.UPDATE_ALL);
            level.updateNeighborsAt(pos, replacement.getBlock());
            level.updateNeighborsAt(partnerPos, paired.getBlock());
        } else if (!level.setBlock(pos, replacement, Block.UPDATE_ALL)) return false;
        if (custom.isPresent()) {
            com.miophas.corruption.api.infection.WorldInfection.activate(level, pos);
            if (paired != null) com.miophas.corruption.api.infection.WorldInfection.activate(level, partnerPos);
        }
        CorruptionEcology.decorate(level, pos, level.random);
        return true;
    }

    private static BlockPos pairedPosition(BlockState state, BlockPos pos) {
        if (state.getBlock() instanceof net.minecraft.world.level.block.DoorBlock
                || state.getBlock() instanceof net.minecraft.world.level.block.DoublePlantBlock)
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF)
                    == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        if (state.getBlock() instanceof net.minecraft.world.level.block.BedBlock)
            return pos.relative(state.getValue(net.minecraft.world.level.block.BedBlock.PART)
                    == net.minecraft.world.level.block.state.properties.BedPart.FOOT
                    ? state.getValue(net.minecraft.world.level.block.BedBlock.FACING) : state.getValue(net.minecraft.world.level.block.BedBlock.FACING).getOpposite());
        return null;
    }

    @Override
    public double randomDouble() {
        return level.random.nextDouble();
    }

    @Override
    public int randomInt(int bound) {
        return level.random.nextInt(bound);
    }

    @Override
    public ScanResult scanSources(ChunkPos pos, int cursor, int maxBlocks) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x, pos.z);
        if (chunk == null) {
            // The load event can precede FULL promotion. Retry later without loading the chunk.
            return new ScanResult(List.of(), cursor, 0, false);
        }
        ScanResult result = ChunkFrontierScan.scan(chunk.getSections(), chunk.getMinSection(), pos, cursor, maxBlocks, level, outside -> {
            LevelChunk other = loadedChunk(outside);
            return other == null ? null : other.getBlockState(outside);
        });
        // The scan reads states alone; dormant ground looks like any corruption in a section, so leave it out here.
        if (!DormantGround.hasMarks(chunk)) return result;
        List<BlockPos> sources = new ArrayList<>(result.sources().size());
        for (BlockPos source : result.sources()) if (!DormantGround.isDormant(chunk, source)) sources.add(source);
        return sources.size() == result.sources().size() ? result
                : new ScanResult(sources, result.nextCursor(), result.inspectedBlocks(), result.complete());
    }
}
