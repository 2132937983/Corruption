package com.miophas.corruption.common.altar;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What stands round an altar, as a structure built round it sees it: at each place, in blocks east, up and north
 * of the offering as though the altar faced north, the block there turned back with it (a stair the altar's
 * turn has turned faces as it would facing north), a corrupted block seen as the block it was.
 */
public interface AltarSpace {
    BlockState at(int east, int up, int north);

    /** Nothing anywhere. */
    AltarSpace EMPTY = (east, up, north) -> Blocks.AIR.defaultBlockState();
}
