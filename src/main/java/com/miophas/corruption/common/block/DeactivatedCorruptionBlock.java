package com.miophas.corruption.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

/**
 * The corruption fallen still: the corruption block in every way (it is made with the same properties, looks the same,
 * hurts and screams as it does, drops what it drops, and counts as corrupted ground wherever that matters), except that
 * it never spreads. It is no source of the infection, so it neither wakes the infection when it is set down nor gives
 * it work; and, being corrupted already, nothing turns it into anything else. Dormant corruption spells leave it, and
 * quelling stills the spreading corruption into it.
 */
public final class DeactivatedCorruptionBlock extends CorruptionBlock {
    public static final MapCodec<DeactivatedCorruptionBlock> CODEC = simpleCodec(DeactivatedCorruptionBlock::new);

    public DeactivatedCorruptionBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<? extends Block> codec() { return CODEC; }

    /** It never spreads: that is all it is for. */
    @Override public boolean spreads() { return false; }
}
