package com.miophas.corruption.common.block;

import com.miophas.corruption.common.alchemy.CorruptionBenchMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** The alchemy work block. Like a crafting table it stores nothing: the menu holds the ingredients while it is open. */
public final class CorruptionBenchBlock extends Block {
    public static final MapCodec<CorruptionBenchBlock> CODEC = simpleCodec(CorruptionBenchBlock::new);
    private static final Component TITLE = Component.translatable("container.corruption.corruption_bench");

    public CorruptionBenchBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<CorruptionBenchBlock> codec() { return CODEC; }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        player.openMenu(state.getMenuProvider(level, pos));
        return InteractionResult.CONSUME;
    }

    @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) -> new CorruptionBenchMenu(id, inventory, ContainerLevelAccess.create(level, pos)), TITLE);
    }
}
