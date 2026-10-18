package com.miophas.corruption.common.infection;

import com.miophas.corruption.common.registry.ModAttachments;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * The corrupted ground that stays still: positions the infection took without leaving a source behind, so the block
 * there is an ordinary corrupted variant that hurts and looks as it always does but never spreads on from that spot.
 *
 * <p>It is kept as so many longs per chunk (a data attachment on the chunk; {@link Marks}), not as a second dormant twin
 * registered for every dynamic variant - a twin of each generated corrupted block would double the blocks generated and
 * the work of generating them - and not as one list for the whole level, so a save writes only the chunks whose marks
 * changed. The infection asks {@link #isDormant} wherever it asks whether a block is a source, so these positions are
 * never queued as fronts; a chunk that has none costs a null read.
 *
 * <p>Server thread only: the infection, the sower and block removal all run there, and the marks are set unsaved there.
 */
public final class DormantGround {
    /** Whether the corrupted ground here is dormant: it stays, and does not spread. */
    public static boolean isDormant(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = chunkAt(level, pos);
        return chunk != null && isDormant(chunk, pos);
    }

    /** The same, for a chunk already in hand. */
    public static boolean isDormant(LevelChunk chunk, BlockPos pos) {
        Marks marks = chunk.getExistingDataOrNull(ModAttachments.DORMANT_GROUND);
        return marks != null && marks.contains(pos.asLong());
    }

    /** Whether this chunk holds any dormant ground at all: the cheap question asked before filtering a scan. */
    public static boolean hasMarks(LevelChunk chunk) {
        return chunk.hasData(ModAttachments.DORMANT_GROUND);
    }

    /** Makes the ground here dormant, if it is not marked already. */
    public static void still(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = chunkAt(level, pos);
        if (chunk != null) still(chunk, pos);
    }

    public static void still(LevelChunk chunk, BlockPos pos) {
        if (chunk.getData(ModAttachments.DORMANT_GROUND).add(pos.asLong())) chunk.setUnsaved(true);
    }

    /** Forgets the ground here: it is gone, so whatever is put there later spreads as any corruption does. */
    public static void release(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = chunkAt(level, pos);
        if (chunk == null) return;
        Marks marks = chunk.getExistingDataOrNull(ModAttachments.DORMANT_GROUND);
        if (marks != null && marks.remove(pos.asLong())) chunk.setUnsaved(true);
    }

    /** Loaded chunks only: a block being placed, taken or read is always in one. */
    @Nullable private static LevelChunk chunkAt(ServerLevel level, BlockPos pos) {
        return level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * One chunk's dormant positions, as absolute longs. Saved with the chunk and read back when it loads; written only
     * while it holds something, so an emptied chunk carries nothing.
     */
    public static final class Marks {
        public static final Codec<Marks> CODEC = Codec.LONG_STREAM.xmap(Marks::load, Marks::write);
        private final LongSet positions = new LongOpenHashSet();

        public Marks() {}

        private static Marks load(LongStream positions) {
            Marks marks = new Marks();
            positions.forEach(marks.positions::add);
            return marks;
        }

        private LongStream write() { return positions.longStream(); }

        public boolean isEmpty() { return positions.isEmpty(); }
        boolean contains(long pos) { return !positions.isEmpty() && positions.contains(pos); }
        boolean add(long pos) { return positions.add(pos); }
        boolean remove(long pos) { return positions.remove(pos); }
    }

    private DormantGround() {}
}
