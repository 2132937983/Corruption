package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.blockentity.InfectedBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Corruption.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfectedBlockEntity>> INFECTED_BLOCK = TYPES.register(
            "infected_block", () -> BlockEntityType.Builder.of(InfectedBlockEntity::new, ModBlocks.INFECTED_BLOCK.get()).build(null));

    private ModBlockEntities() {}
}
