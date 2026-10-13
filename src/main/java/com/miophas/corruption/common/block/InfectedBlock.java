package com.miophas.corruption.common.block;

import com.miophas.corruption.common.blockentity.InfectedBlockEntity;
import com.miophas.corruption.common.item.InfectedBlockItem;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.config.InfectionConfig;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class InfectedBlock extends CorruptionBlock implements EntityBlock {
    public static final MapCodec<InfectedBlock> CODEC = simpleCodec(InfectedBlock::new);

    public InfectedBlock(Properties properties) { super(properties); }

    @Override
    protected MapCodec<? extends InfectedBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new InfectedBlockEntity(pos, state); }

    public static BlockState source(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof InfectedBlockEntity entity ? entity.source() : Blocks.STONE.defaultBlockState();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return source(level, pos).getShape(level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return source(level, pos).getCollisionShape(level, pos, context);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return source(level, pos).getOcclusionShape(level, pos);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return InfectionConfig.corruptedBlocksFlammable() ? 20 : 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return InfectionConfig.corruptedBlocksFlammable() ? 5 : 0;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return source(level, pos).propagatesSkylightDown(level, pos);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return source(level, pos).getShadeBrightness(level, pos);
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return source(level, pos).getLightBlock(level, pos);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return source(level, pos).getLightEmission(level, pos);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof InfectedBlockEntity entity
                ? InfectedBlockItem.of(entity.sourceTag()) : InfectedBlockItem.of(Blocks.STONE.defaultBlockState()),
                ModItems.corruptionDustDrop(params.getLevel().getRandom()));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockEntity(pos) instanceof InfectedBlockEntity entity ? InfectedBlockItem.of(entity.sourceTag())
                : InfectedBlockItem.of(Blocks.STONE.defaultBlockState());
    }
}
