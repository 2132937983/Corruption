package com.miophas.corruption.common.altar;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The altar's ground as its rites see it: what stands at each place, in blocks east and north of the offering
 * on its level, as though the altar faced north; the way it really faces, it is turned with it.
 */
public interface AltarGround {
    BlockState at(int east, int north);

    /** Bare ground: nothing anywhere. */
    AltarGround CLEAR = (east, north) -> Blocks.AIR.defaultBlockState();
}
