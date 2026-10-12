package com.miophas.corruption.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;

public final class CorruptionTeethBlock extends CorruptionPlantBlock {
    public static final MapCodec<CorruptionTeethBlock> CODEC = simpleCodec(CorruptionTeethBlock::new);
    public CorruptionTeethBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends BushBlock> codec() { return CODEC; }
}
