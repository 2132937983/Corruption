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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.miophas.corruption.common.transmutation.ArcaneTransmuterBlockEntity>> ARCANE_TRANSMUTER = TYPES.register(
            "corruption_arcane_transmuter", () -> BlockEntityType.Builder.of(com.miophas.corruption.common.transmutation.ArcaneTransmuterBlockEntity::new, ModBlocks.CORRUPTION_ARCANE_TRANSMUTER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.miophas.corruption.common.arcane.ArcaneLecternBlockEntity>> ARCANE_LECTERN = TYPES.register(
            "corruption_arcane_lectern", () -> BlockEntityType.Builder.of(com.miophas.corruption.common.arcane.ArcaneLecternBlockEntity::new, ModBlocks.CORRUPTION_ARCANE_LECTERN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.miophas.corruption.common.arcane.ArcanePedestalBlockEntity>> ARCANE_PEDESTAL = TYPES.register(
            "corruption_arcane_pedestal", () -> BlockEntityType.Builder.of(com.miophas.corruption.common.arcane.ArcanePedestalBlockEntity::new, ModBlocks.CORRUPTION_ARCANE_PEDESTAL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.miophas.corruption.common.blockentity.ArcaneChargerBlockEntity>> ARCANE_CHARGER = TYPES.register(
            "corruption_arcane_charger", () -> BlockEntityType.Builder.of(com.miophas.corruption.common.blockentity.ArcaneChargerBlockEntity::new, ModBlocks.CORRUPTION_ARCANE_CHARGER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.miophas.corruption.common.cooking.BrassCauldronBlockEntity>> BRASS_CAULDRON = TYPES.register(
            "brass_cauldron", () -> BlockEntityType.Builder.of(com.miophas.corruption.common.cooking.BrassCauldronBlockEntity::new, ModBlocks.BRASS_CAULDRON.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.miophas.corruption.common.altar.AltarPillarBlockEntity>> ALTAR_PILLAR = TYPES.register(
            "altar_pillar", () -> BlockEntityType.Builder.of(com.miophas.corruption.common.altar.AltarPillarBlockEntity::new, ModBlocks.ALTAR_PILLAR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.miophas.corruption.common.altar.AltarOfferingBlockEntity>> ALTAR_OFFERING = TYPES.register(
            "altar_offering", () -> BlockEntityType.Builder.of(com.miophas.corruption.common.altar.AltarOfferingBlockEntity::new, ModBlocks.ALTAR_OFFERING.get()).build(null));

    private ModBlockEntities() {}
}
