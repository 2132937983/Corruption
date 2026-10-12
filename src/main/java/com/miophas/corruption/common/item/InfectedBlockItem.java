package com.miophas.corruption.common.item;

import com.miophas.corruption.api.corruption.CorruptionApi;
import com.miophas.corruption.common.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class InfectedBlockItem extends BlockItem {
    public InfectedBlockItem(Block block, Properties properties) { super(block, properties); }

    public static ItemStack of(BlockState source) { return of(NbtUtils.writeBlockState(source)); }

    public static ItemStack of(CompoundTag sourceTag) {
        var generated = com.miophas.corruption.common.registry.GeneratedCorruption.convert(CorruptionApi.readSource(sourceTag));
        if (generated != null) return com.miophas.corruption.common.registry.GeneratedCorruption.stack(generated);
        ItemStack stack = new ItemStack(ModItems.INFECTED_BLOCK.get());
        CompoundTag data = new CompoundTag();
        data.putString("id", "corruption:infected_block");
        data.put(CorruptionApi.SOURCE_STATE_KEY, sourceTag.copy());
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(data));
        return stack;
    }

    public static BlockState source(ItemStack stack) {
        CompoundTag data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
        return CorruptionApi.readSource(data.getCompound(CorruptionApi.SOURCE_STATE_KEY));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("block.corruption.infected_block.variant", source(stack).getBlock().getName());
    }
}
