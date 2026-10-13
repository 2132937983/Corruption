package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.item.InfectedBlockItem;
import com.miophas.corruption.common.item.LeafWinnowerItem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Corruption.MOD_ID);
    public static final DeferredItem<Item> CORRUPTION_DUST =
            ITEMS.register("corruption_dust", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORRUPTION_CRYSTAL =
            ITEMS.register("corruption_crystal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ANCIENT_CORRUPTION_CRYSTAL =
            ITEMS.register("ancient_corruption_crystal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HARMONIZED_CORRUPTION_CRYSTAL =
            ITEMS.register("harmonized_corruption_crystal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORRUPTION_REMAIN =
            ITEMS.register("corruption_remain", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURE_LEAF =
            ITEMS.register("pure_leaf", () -> new Item(new Item.Properties()));
    // 59 uses, the same as a wooden hoe.
    public static final DeferredItem<LeafWinnowerItem> LEAF_WINNOWER =
            ITEMS.register("leaf_winnower", () -> new LeafWinnowerItem(new Item.Properties().durability(59)));
    public static final DeferredItem<net.minecraft.world.item.BucketItem> CORRUPTED_WATER_BUCKET = ITEMS.register("corrupted_water_bucket",
            () -> new net.minecraft.world.item.BucketItem(ModFluids.WATER.get(), new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<net.minecraft.world.item.BucketItem> CORRUPTED_LAVA_BUCKET = ITEMS.register("corrupted_lava_bucket",
            () -> new net.minecraft.world.item.BucketItem(ModFluids.LAVA.get(), new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<net.minecraft.world.item.BucketItem> FLOWING_CORRUPTION_BUCKET = ITEMS.register(com.miophas.corruption.api.corruption.CorruptionCurseApi.flowingCorruption().id() + "_bucket",
            () -> new net.minecraft.world.item.BucketItem(ModFluids.FLOWING_CORRUPTION.get(), new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<InfectedBlockItem> INFECTED_BLOCK = ITEMS.register("infected_block",
            () -> new InfectedBlockItem(ModBlocks.INFECTED_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> CORRUPTION =
            ITEMS.registerSimpleBlockItem("corruption", ModBlocks.CORRUPTION);

    public static final DeferredItem<BlockItem> ARCUNUM = ITEMS.registerSimpleBlockItem("arcunum", ModBlocks.ARCUNUM);
    public static final DeferredItem<BlockItem> MALICE = ITEMS.registerSimpleBlockItem("malice", ModBlocks.MALICE);
    public static final DeferredItem<BlockItem> CORRUPTION_TEETH = ITEMS.registerSimpleBlockItem("corruption_teeth", ModBlocks.CORRUPTION_TEETH);
    public static final DeferredItem<BlockItem> MALICE_GRASS = ITEMS.registerSimpleBlockItem("malice_grass", ModBlocks.MALICE_GRASS);
    public static final DeferredItem<BlockItem> WHISPER_BLOOM = ITEMS.registerSimpleBlockItem("whisper_bloom", ModBlocks.WHISPER_BLOOM);
    public static final DeferredItem<BlockItem> BLOOD_FILAMENTS = ITEMS.registerSimpleBlockItem("blood_filaments", ModBlocks.BLOOD_FILAMENTS);
    public static final DeferredItem<BlockItem> ELDRITCH = ITEMS.registerSimpleBlockItem("eldritch", ModBlocks.ELDRITCH);
    public static final DeferredItem<BlockItem> EERIE = ITEMS.registerSimpleBlockItem("eerie", ModBlocks.EERIE);
    public static final DeferredItem<BlockItem> ARCUNUM_END = ITEMS.registerSimpleBlockItem("arcunum_end", ModBlocks.ARCUNUM_END);
    public static final DeferredItem<BlockItem> SPIRIT = ITEMS.registerSimpleBlockItem("spirit", ModBlocks.SPIRIT);
    public static final DeferredItem<BlockItem> SPIRIT_DEEPSLATE = ITEMS.registerSimpleBlockItem("spirit_deepslate", ModBlocks.SPIRIT_DEEPSLATE);

    /** Roll of 1-2 corruption dust, granted as an extra drop by corrupted blocks. */
    public static ItemStack corruptionDustDrop(RandomSource random) {
        return new ItemStack(CORRUPTION_DUST.get(), 1 + random.nextInt(2));
    }

    private ModItems() {}
}
