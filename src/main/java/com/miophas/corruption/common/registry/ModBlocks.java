package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.block.CorruptionBlock;
import com.miophas.corruption.common.block.InfectedBlock;
import com.miophas.corruption.common.block.MaliceBlock;
import com.miophas.corruption.common.block.CorruptionPlantBlock;
import com.miophas.corruption.common.block.CorruptionTeethBlock;
import com.miophas.corruption.common.block.EldritchVineBlock;
import com.miophas.corruption.common.block.EerieBlock;
import com.miophas.corruption.common.block.LostSoulBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final SoundType INFECTED_SOUND = new SoundType(SoundType.WOOD.getVolume(), SoundType.WOOD.getPitch(),
            SoundType.GRAVEL.getBreakSound(), SoundType.SOUL_SAND.getStepSound(), SoundType.SOUL_SAND.getPlaceSound(),
            SoundType.WOOD.getHitSound(), SoundType.WOOD.getFallSound());
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Corruption.MOD_ID);
    public static final DeferredBlock<com.miophas.corruption.common.block.CorruptedLiquidBlock> CORRUPTED_WATER = BLOCKS.register("corrupted_water",
            () -> new com.miophas.corruption.common.block.CorruptedLiquidBlock(ModFluids.WATER.get(), false, BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WATER)));
    public static final DeferredBlock<com.miophas.corruption.common.block.CorruptedLiquidBlock> CORRUPTED_LAVA = BLOCKS.register("corrupted_lava",
            () -> new com.miophas.corruption.common.block.CorruptedLiquidBlock(ModFluids.LAVA.get(), true, BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.LAVA)));
    public static final DeferredBlock<com.miophas.corruption.common.block.FlowingCorruptionBlock> FLOWING_CORRUPTION = BLOCKS.register(com.miophas.corruption.api.corruption.CorruptionCurseApi.flowingCorruption().id(),
            () -> new com.miophas.corruption.common.block.FlowingCorruptionBlock(ModFluids.FLOWING_CORRUPTION.get(), BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WATER).mapColor(MapColor.COLOR_PURPLE)));
    public static final DeferredBlock<com.miophas.corruption.common.block.CorruptionBenchBlock> CORRUPTION_BENCH = BLOCKS.registerBlock("corruption_bench",
            com.miophas.corruption.common.block.CorruptionBenchBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
                    .instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD));
    public static final DeferredBlock<InfectedBlock> INFECTED_BLOCK = BLOCKS.registerBlock("infected_block",
            InfectedBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(INFECTED_SOUND).dynamicShape());
    public static final DeferredBlock<CorruptionBlock> CORRUPTION = BLOCKS.registerBlock("corruption", CorruptionBlock::new, corruption());
    /** The corruption fallen still: the corruption block, made with the same properties, that never spreads. */
    public static final DeferredBlock<com.miophas.corruption.common.block.DeactivatedCorruptionBlock> DEACTIVATED_CORRUPTION = BLOCKS.registerBlock(
            "deactivated_corruption", com.miophas.corruption.common.block.DeactivatedCorruptionBlock::new, corruption());

    /** What the corruption block is made of, and the dormant corruption with it. */
    private static BlockBehaviour.Properties corruption() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(1.5F, 6.0F)
                .sound(SoundType.SCULK)
                .requiresCorrectToolForDrops();
    }

    public static final DeferredBlock<CorruptionBlock> ARCUNUM = ore("arcunum", 3.0F, SoundType.STONE);
    public static final DeferredBlock<MaliceBlock> MALICE = BLOCKS.registerBlock("malice", MaliceBlock::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 6.0F)
                    .sound(SoundType.SCULK).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> CORRUPTION_ARCANE_BRICK = BLOCKS.registerBlock("corruption_arcane_brick",
            Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 6.0F)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops());
    /** Nine corrupted brass, stored. */
    public static final DeferredBlock<Block> CORRUPTED_BRASS_BLOCK = BLOCKS.registerBlock("corrupted_brass_block",
            Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER).requiresCorrectToolForDrops());
    /** Cooks up to seven things together with a fluid and Corruption Energy; see BrassCauldronBlockEntity. */
    public static final DeferredBlock<com.miophas.corruption.common.block.BrassCauldronBlock> BRASS_CAULDRON = BLOCKS.registerBlock("brass_cauldron",
            com.miophas.corruption.common.block.BrassCauldronBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(2.0F)
                    .sound(SoundType.COPPER).requiresCorrectToolForDrops().noOcclusion());
    /** Holds one item for an altar's rites; see AltarPillarBlock. */
    public static final DeferredBlock<com.miophas.corruption.common.block.AltarPillarBlock> ALTAR_PILLAR = BLOCKS.registerBlock("altar_pillar",
            com.miophas.corruption.common.block.AltarPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 6.0F)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());
    /** The middle of an altar: binds creatures and performs rites; see AltarOfferingBlock. */
    public static final DeferredBlock<com.miophas.corruption.common.block.AltarOfferingBlock> ALTAR_OFFERING = BLOCKS.registerBlock("altar_offering",
            com.miophas.corruption.common.block.AltarOfferingBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(1.5F, 6.0F)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion()
                    .lightLevel(state -> state.getValue(com.miophas.corruption.common.block.AltarOfferingBlock.ALTAR) == com.miophas.corruption.common.altar.AltarMode.BINDING ? 7 : 0));
    /** Pours its store into the item left on it, until that is full; see ArcaneChargerBlockEntity. */
    public static final DeferredBlock<com.miophas.corruption.common.block.ArcaneChargerBlock> CORRUPTION_ARCANE_CHARGER = BLOCKS.registerBlock("corruption_arcane_charger",
            com.miophas.corruption.common.block.ArcaneChargerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.0F)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state -> 4));
    /** Teaches a wand the spells of the parchment lying on it; see ArcanePedestalBlockEntity. */
    public static final DeferredBlock<com.miophas.corruption.common.block.ArcanePedestalBlock> CORRUPTION_ARCANE_PEDESTAL = BLOCKS.registerBlock("corruption_arcane_pedestal",
            com.miophas.corruption.common.block.ArcanePedestalBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.5F)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state -> 5).noOcclusion());
    /** Shows a key to the arcane, and copies its strings onto parchment; see ArcaneLecternBlockEntity. */
    public static final DeferredBlock<com.miophas.corruption.common.block.ArcaneLecternBlock> CORRUPTION_ARCANE_LECTERN = BLOCKS.registerBlock("corruption_arcane_lectern",
            com.miophas.corruption.common.block.ArcaneLecternBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.5F)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state -> 7).noOcclusion());
    /** Lit while it works; see ArcaneTransmuterBlockEntity. */
    public static final DeferredBlock<com.miophas.corruption.common.block.ArcaneTransmuterBlock> CORRUPTION_ARCANE_TRANSMUTER = BLOCKS.registerBlock("corruption_arcane_transmuter",
            com.miophas.corruption.common.block.ArcaneTransmuterBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.5F)
                    .sound(SoundType.STONE).requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) ? 9 : 0));
    public static final DeferredBlock<CorruptionTeethBlock> CORRUPTION_TEETH = BLOCKS.registerBlock("corruption_teeth",
            CorruptionTeethBlock::new, plantProperties());
    public static final DeferredBlock<CorruptionPlantBlock> MALICE_GRASS = plant("malice_grass");
    public static final DeferredBlock<CorruptionPlantBlock> WHISPER_BLOOM = plant("whisper_bloom");
    public static final DeferredBlock<LostSoulBlock> BLOOD_FILAMENTS = BLOCKS.registerBlock("blood_filaments",
            LostSoulBlock::new, plantProperties());
    public static final DeferredBlock<EldritchVineBlock> ELDRITCH = BLOCKS.registerBlock("eldritch", EldritchVineBlock::new,
            plantProperties().randomTicks().sound(SoundType.WEEPING_VINES));
    public static final DeferredBlock<EerieBlock> EERIE = BLOCKS.registerBlock("eerie", EerieBlock::new,
            plantProperties().sound(SoundType.SCULK_VEIN));
    public static final DeferredBlock<CorruptionBlock> ARCUNUM_END = ore("arcunum_end", 3.0F, SoundType.STONE);
    public static final DeferredBlock<CorruptionBlock> SPIRIT = ore("spirit", 3.0F, SoundType.STONE);
    public static final DeferredBlock<CorruptionBlock> SPIRIT_DEEPSLATE = ore("spirit_deepslate", 4.5F, SoundType.DEEPSLATE);

    private static DeferredBlock<CorruptionBlock> ore(String name, float hardness, SoundType sound) {
        return BLOCKS.registerBlock(name, CorruptionBlock::new, BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED).strength(hardness, 6.0F).sound(sound)
                .requiresCorrectToolForDrops());
    }
    private static BlockBehaviour.Properties plantProperties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).noCollission().noOcclusion()
                .instabreak().sound(SoundType.ROOTS).pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY);
    }
    private static DeferredBlock<CorruptionPlantBlock> plant(String name) {
        return BLOCKS.registerBlock(name, CorruptionPlantBlock::new, plantProperties());
    }

    private ModBlocks() {}
}
