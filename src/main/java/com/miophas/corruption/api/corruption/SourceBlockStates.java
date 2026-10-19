package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CorruptionVariants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Game serialization adapter, kept out of the variant rules. */
final class SourceBlockStates {
    static BlockState read(CompoundTag tag) {
        ResourceLocation name = ResourceLocation.tryParse(tag.getString("Name"));
        if (name == null || !BuiltInRegistries.BLOCK.containsKey(name)) return Blocks.STONE.defaultBlockState();
        BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag);
        return CorruptionVariants.validSource(name.getNamespace(), name.getPath(), state.isAir(), state.getBlock() instanceof LiquidBlock)
                ? state : Blocks.STONE.defaultBlockState();
    }
    private SourceBlockStates() {}
}
