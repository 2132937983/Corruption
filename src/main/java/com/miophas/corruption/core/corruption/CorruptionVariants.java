package com.miophas.corruption.core.corruption;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Stable namespaced identifiers for generated static blocks and legacy source data. */
public final class CorruptionVariants {
    public static ResourceLocation variantId(BlockState source) {
        ResourceLocation original = BuiltInRegistries.BLOCK.getKey(source.getBlock());
        return ResourceLocation.fromNamespaceAndPath("corruption", "generated/" + original.getNamespace() + "/" + original.getPath());
    }

    public static BlockState readSource(CompoundTag tag) {
        ResourceLocation name = ResourceLocation.tryParse(tag.getString("Name"));
        if (name == null || !BuiltInRegistries.BLOCK.containsKey(name)) return Blocks.STONE.defaultBlockState();
        BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag);
        return state.isAir() || state.getBlock() instanceof net.minecraft.world.level.block.LiquidBlock
                || name.toString().equals("corruption:infected_block") ? Blocks.STONE.defaultBlockState() : state;
    }

    private CorruptionVariants() {}
}
