package com.miophas.corruption.common.blockentity;

import com.miophas.corruption.api.corruption.CorruptionApi;
import com.miophas.corruption.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

/** Stores appearance only. It has no ticker, renderer, inventory, or copied source logic. */
public final class InfectedBlockEntity extends BlockEntity {
    public static final ModelProperty<BlockState> SOURCE = new ModelProperty<>();
    private BlockState source = Blocks.STONE.defaultBlockState();
    private CompoundTag sourceTag = NbtUtils.writeBlockState(source);
    private ModelData modelData = ModelData.builder().with(SOURCE, source).build();

    public InfectedBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFECTED_BLOCK.get(), pos, state);
    }

    public BlockState source() { return source; }

    @Override public void onLoad() {
        super.onLoad();
        com.miophas.corruption.common.infection.LegacyCorruptionUpgrade.enqueue(this);
    }

    public CompoundTag sourceTag() { return sourceTag.copy(); }

    public void setSource(BlockState original) {
        updateSource(NbtUtils.writeBlockState(original));
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
        com.miophas.corruption.common.infection.LegacyCorruptionUpgrade.enqueue(this);
    }

    private void updateSource(CompoundTag tag) {
        sourceTag = tag.copy();
        source = CorruptionApi.readSource(sourceTag);
        modelData = ModelData.builder().with(SOURCE, source).build();
        requestModelDataUpdate();
        if (level != null) level.getChunkSource().getLightEngine().checkBlock(worldPosition);
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        updateSource(tag.contains(CorruptionApi.SOURCE_STATE_KEY, 10) ? tag.getCompound(CorruptionApi.SOURCE_STATE_KEY)
                : NbtUtils.writeBlockState(Blocks.STONE.defaultBlockState()));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(CorruptionApi.SOURCE_STATE_KEY, sourceTag.copy());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveCustomOnly(registries); }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public ModelData getModelData() { return modelData; }
}
