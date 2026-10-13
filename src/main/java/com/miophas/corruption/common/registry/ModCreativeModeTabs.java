package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.item.InfectedBlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Corruption.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CORRUPTION =
            TABS.register("corruption", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.corruption"))
                    .withTabsBefore(CreativeModeTabs.NATURAL_BLOCKS)
                    .icon(() -> ModItems.CORRUPTION.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CORRUPTION_DUST.get());
                        output.accept(ModItems.CORRUPTION_CRYSTAL.get());
                        output.accept(ModItems.ANCIENT_CORRUPTION_CRYSTAL.get());
                        output.accept(ModItems.HARMONIZED_CORRUPTION_CRYSTAL.get());
                        output.accept(ModItems.CORRUPTION_REMAIN.get());
                        output.accept(ModItems.LEAF_WINNOWER.get());
                        output.accept(ModItems.PURE_LEAF.get());
                        output.accept(ModItems.CORRUPTION_BENCH.get());
                        output.accept(ModItems.CORRUPTION.get());
                        output.accept(ModItems.MALICE.get());
                        output.accept(ModItems.CORRUPTION_TEETH.get());
                        output.accept(ModItems.MALICE_GRASS.get());
                        output.accept(ModItems.WHISPER_BLOOM.get());
                        output.accept(ModItems.BLOOD_FILAMENTS.get());
                        output.accept(ModItems.ELDRITCH.get());
                        output.accept(ModItems.EERIE.get());
                        output.accept(ModItems.ARCUNUM.get());
                        output.accept(ModItems.ARCUNUM_END.get());
                        output.accept(ModItems.SPIRIT.get());
                        output.accept(ModItems.SPIRIT_DEEPSLATE.get());
                        output.accept(ModItems.CORRUPTED_WATER_BUCKET.get());
                        output.accept(ModItems.CORRUPTED_LAVA_BUCKET.get());
                        output.accept(ModItems.FLOWING_CORRUPTION_BUCKET.get());
                        for (var container : new net.minecraft.world.item.Item[] {net.minecraft.world.item.Items.POTION, net.minecraft.world.item.Items.SPLASH_POTION,
                                net.minecraft.world.item.Items.LINGERING_POTION, net.minecraft.world.item.Items.TIPPED_ARROW})
                            for (var potion : ModPotions.CORRUPTION_CURSE)
                                output.accept(net.minecraft.world.item.alchemy.PotionContents.createItemStack(container, potion));
                        output.accept(InfectedBlockItem.of(Blocks.TUFF.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.DEEPSLATE.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.STONE.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.DIORITE.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.OAK_LOG.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.GRASS_BLOCK.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.SOUL_SAND.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.DIRT_PATH.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.LADDER.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.OAK_STAIRS.defaultBlockState()));
                        output.accept(InfectedBlockItem.of(Blocks.STONE_STAIRS.defaultBlockState()));
                    })
                    .build());

    private ModCreativeModeTabs() {}
}
