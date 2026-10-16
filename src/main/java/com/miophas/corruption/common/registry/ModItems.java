package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.item.BasicCorruptionEnergyItem;
import com.miophas.corruption.common.item.CorruptionArcaneStickItem;
import com.miophas.corruption.common.item.CorruptionMaterialItem;
import com.miophas.corruption.common.item.InfectedBlockItem;
import com.miophas.corruption.common.item.ParchmentItem;
import com.miophas.corruption.common.item.LeafWinnowerItem;
import net.minecraft.util.RandomSource;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Corruption.MOD_ID);
    public static final DeferredItem<Item> CORRUPTION_DUST =
            ITEMS.register("corruption_dust", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORRUPTION_STICK =
            ITEMS.register("corruption_stick", () -> new Item(new Item.Properties()));
    /** The wands: casting tools, carrying Corruption Energy and the spells they have been taught, one to a tier. */
    public static final DeferredItem<CorruptionArcaneStickItem> NOVICE_CORRUPTION_ARCANE_STICK = wand("novice_corruption_arcane_stick", CorruptionArcaneStickItem.Tier.NOVICE);
    /** The Basic wand, the first the mod had, which keeps its id. */
    public static final DeferredItem<CorruptionArcaneStickItem> CORRUPTION_ARCANE_STICK = wand("corruption_arcane_stick", CorruptionArcaneStickItem.Tier.BASIC);
    public static final DeferredItem<CorruptionArcaneStickItem> INTERMEDIATE_CORRUPTION_ARCANE_STICK =
            wand("intermediate_corruption_arcane_stick", CorruptionArcaneStickItem.Tier.INTERMEDIATE);
    public static final DeferredItem<CorruptionArcaneStickItem> ADVANCED_CORRUPTION_ARCANE_STICK = wand("advanced_corruption_arcane_stick", CorruptionArcaneStickItem.Tier.ADVANCED);
    public static final DeferredItem<Item> CORRUPTION_MIXTURE_LEV0 =
            ITEMS.register("corruption_mixture_lev0", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORRUPTION_CRYSTAL =
            ITEMS.register("corruption_crystal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ANCIENT_CORRUPTION_CRYSTAL =
            ITEMS.register("ancient_corruption_crystal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HARMONIZED_CORRUPTION_CRYSTAL =
            ITEMS.register("harmonized_corruption_crystal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORRUPTION_REMAIN =
            ITEMS.register("corruption_remain", () -> new Item(new Item.Properties()));
    /** Carries the 10 KCE its making cost (data_maps/item/corruption_energy_carrier.json) and burns down a KCE at a time. */
    public static final DeferredItem<BasicCorruptionEnergyItem> CORRUPTION_CANDLE = ITEMS.register("corruption_candle",
            () -> new BasicCorruptionEnergyItem(new Item.Properties(), com.miophas.corruption.api.energy.CorruptionEnergyApi.kilo(1)));
    /**
     * The keys to the corruption's arcane: on the arcane lectern each reveals the strings data_maps/item/arcane_key.json
     * gives it. The Prologue (the first key the mod had, which keeps its id) holds the corruption's own arts; the
     * eight books of the Key, the seventy-two spirits of the Ars Goetia, nine to a book; the Ascension, the planetary
     * pentacles; the Lesser Key, the ancients' five arts; the Arcana of Corruption, the corruption's higher arts.
     */
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS = key("clavicula_salomonis", Rarity.EPIC);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_CORRUPTION = key("clavicula_salomonis_corruption", Rarity.EPIC);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_I = key("clavicula_salomonis_i", Rarity.UNCOMMON);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_II = key("clavicula_salomonis_ii", Rarity.UNCOMMON);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_III = key("clavicula_salomonis_iii", Rarity.UNCOMMON);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_IV = key("clavicula_salomonis_iv", Rarity.UNCOMMON);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_V = key("clavicula_salomonis_v", Rarity.RARE);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_VI = key("clavicula_salomonis_vi", Rarity.RARE);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_VII = key("clavicula_salomonis_vii", Rarity.RARE);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_VIII = key("clavicula_salomonis_viii", Rarity.RARE);
    public static final DeferredItem<Item> CLAVICULA_SALOMONIS_ASCENSION = key("clavicula_salomonis_ascension", Rarity.EPIC);
    public static final DeferredItem<Item> LEMEGETON = key("lemegeton", Rarity.EPIC);
    /** The keys, in the order they are read: the Prologue and the Arcana of Corruption, the eight books, the Ascension, the Lesser Key. */
    public static final List<DeferredItem<Item>> KEYS = List.of(CLAVICULA_SALOMONIS, CLAVICULA_SALOMONIS_CORRUPTION, CLAVICULA_SALOMONIS_I, CLAVICULA_SALOMONIS_II,
            CLAVICULA_SALOMONIS_III, CLAVICULA_SALOMONIS_IV, CLAVICULA_SALOMONIS_V, CLAVICULA_SALOMONIS_VI, CLAVICULA_SALOMONIS_VII,
            CLAVICULA_SALOMONIS_VIII, CLAVICULA_SALOMONIS_ASCENSION, LEMEGETON);
    /** The wands, lowest tier first. */
    public static final List<DeferredItem<CorruptionArcaneStickItem>> WANDS = List.of(NOVICE_CORRUPTION_ARCANE_STICK, CORRUPTION_ARCANE_STICK,
            INTERMEDIATE_CORRUPTION_ARCANE_STICK, ADVANCED_CORRUPTION_ARCANE_STICK);
    /** Made from leather; bears one arcane string once copied onto at the arcane lectern. */
    public static final DeferredItem<ParchmentItem> PARCHMENT = ITEMS.register("parchment", () -> new ParchmentItem(new Item.Properties()));
    /** How many copies a bottle of ink is good for. */
    public static final int INK_USES = 8;
    /**
     * A bottle of ink, made from an ink sac, water and a glass bottle: what the arcane lectern copies with, one
     * use a copy. It wears as a tool does, and once used up leaves its glass bottle.
     */
    public static final DeferredItem<Item> INK = ITEMS.register("ink",
            () -> new Item(new Item.Properties().durability(INK_USES).craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE)));
    /**
     * The corruption's raw materials, made and used only in the mod's own machines and rites: the transmuter, the
     * cauldron, the bench and the altar (gamemodels/corruption_materials/write_materials.py writes their recipes).
     */
    public static final DeferredItem<CorruptionMaterialItem> VOID_ASH = material("void_ash");
    public static final DeferredItem<CorruptionMaterialItem> CURSED_IRON_INGOT = material("cursed_iron_ingot");
    public static final DeferredItem<CorruptionMaterialItem> BLIGHTED_BONE = material("blighted_bone");
    public static final DeferredItem<CorruptionMaterialItem> TAINTED_FLESH = material("tainted_flesh");
    /** Bottled: it stacks to sixteen, and leaves its bottle when a machine uses it. */
    public static final DeferredItem<CorruptionMaterialItem> CORRUPTED_ICHOR = ITEMS.register("corrupted_ichor",
            () -> new CorruptionMaterialItem(new Item.Properties().stacksTo(16).craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE)));
    public static final DeferredItem<CorruptionMaterialItem> MALICE_FIBER = material("malice_fiber");
    public static final DeferredItem<CorruptionMaterialItem> SOUL_RESIN = material("soul_resin");
    public static final DeferredItem<CorruptionMaterialItem> SHADOW_SILK = material("shadow_silk");
    public static final DeferredItem<CorruptionMaterialItem> BLOODSTONE = material("bloodstone");
    public static final DeferredItem<CorruptionMaterialItem> RUNE_SLATE = material("rune_slate");
    public static final DeferredItem<CorruptionMaterialItem> ELDRITCH_MEMBRANE = material("eldritch_membrane");
    public static final DeferredItem<CorruptionMaterialItem> ABYSSAL_PEARL = ITEMS.register("abyssal_pearl",
            () -> new CorruptionMaterialItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<CorruptionMaterialItem> CORRUPTED_HEARTSTONE = ITEMS.register("corrupted_heartstone",
            () -> new CorruptionMaterialItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<CorruptionMaterialItem> CHAOS_CATALYST = ITEMS.register("chaos_catalyst",
            () -> new CorruptionMaterialItem(new Item.Properties().rarity(Rarity.RARE)));
    /** What the malice vine leaves when it is cut down, and what greed leaves: no machine makes either. */
    public static final DeferredItem<CorruptionMaterialItem> MALICE_TENDRIL = ITEMS.register("malice_tendril",
            () -> new CorruptionMaterialItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<CorruptionMaterialItem> GREED_HEARTWOOD = ITEMS.register("greed_heartwood",
            () -> new CorruptionMaterialItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    /** The corruption's plants brewed down to their spite. */
    public static final DeferredItem<CorruptionMaterialItem> RESENTMENT = ITEMS.register("resentment",
            () -> new CorruptionMaterialItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    /** The memories the corruption could not digest, and what is made of them: the advanced materials. */
    public static final DeferredItem<CorruptionMaterialItem> MEMORY_FRAGMENT = ITEMS.register("memory_fragment",
            () -> new CorruptionMaterialItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<CorruptionMaterialItem> CORRUPTED_MEMORY_GEM = ITEMS.register("corrupted_memory_gem",
            () -> new CorruptionMaterialItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<CorruptionMaterialItem> CORRUPTION_ENERGY_BRICK = ITEMS.register("corruption_energy_brick",
            () -> new CorruptionMaterialItem(new Item.Properties().rarity(Rarity.EPIC)));
    /** The materials, rawest first. */
    public static final List<DeferredItem<CorruptionMaterialItem>> MATERIALS = List.of(VOID_ASH, CURSED_IRON_INGOT, BLIGHTED_BONE, TAINTED_FLESH,
            CORRUPTED_ICHOR, MALICE_FIBER, SOUL_RESIN, SHADOW_SILK, BLOODSTONE, RUNE_SLATE, ELDRITCH_MEMBRANE, ABYSSAL_PEARL, CORRUPTED_HEARTSTONE, CHAOS_CATALYST,
            MALICE_TENDRIL, GREED_HEARTWOOD, RESENTMENT, MEMORY_FRAGMENT, CORRUPTED_MEMORY_GEM, CORRUPTION_ENERGY_BRICK);
    /**
     * The corruption's blades (ArcaneBladeRules): the energy sword, which bears the arcane's mark and reaches the
     * corruption's lords; and the arcane sword, twice as heavy and cursed besides, which they turn aside.
     */
    public static final DeferredItem<com.miophas.corruption.common.item.CorruptionBladeItem> CORRUPTION_ENERGY_SWORD = ITEMS.register("corruption_energy_sword",
            () -> new com.miophas.corruption.common.item.CorruptionBladeItem(com.miophas.corruption.core.arcane.ArcaneBladeRules.ENERGY_SWORD_DURABILITY,
                    () -> net.minecraft.world.item.crafting.Ingredient.of(CORRUPTION_ENERGY_BRICK.get()), com.miophas.corruption.core.arcane.ArcaneBladeRules.ENERGY_SWORD_DAMAGE));
    public static final DeferredItem<com.miophas.corruption.common.item.CorruptionBladeItem> CORRUPTED_ARCANE_SWORD = ITEMS.register("corrupted_arcane_sword",
            () -> new com.miophas.corruption.common.item.CorruptionBladeItem(com.miophas.corruption.core.arcane.ArcaneBladeRules.ARCANE_SWORD_DURABILITY,
                    () -> net.minecraft.world.item.crafting.Ingredient.of(RESENTMENT.get()), com.miophas.corruption.core.arcane.ArcaneBladeRules.ARCANE_SWORD_DAMAGE));
    /** Bread baked through with the corruption: eaten for no hunger at all, it leaves its eater one point more corrupted. */
    public static final DeferredItem<com.miophas.corruption.common.item.CorruptedBreadItem> CORRUPTED_BREAD = ITEMS.register("corrupted_bread",
            () -> new com.miophas.corruption.common.item.CorruptedBreadItem(new Item.Properties()));
    /** Water steeped with pure leaves: drunk, it takes a point of corruption away and leaves its bottle. */
    public static final DeferredItem<com.miophas.corruption.api.corruption.CorruptionPurifyingItem> PURIFIED_WATER = ITEMS.register("purified_water",
            () -> new com.miophas.corruption.api.corruption.CorruptionPurifyingItem(new Item.Properties().stacksTo(16),
                    new com.miophas.corruption.core.corruption.CorruptionPurifier(1), net.minecraft.world.item.UseAnim.DRINK, net.minecraft.world.item.Items.GLASS_BOTTLE));
    /** The robe, four pieces woven with pure leaves, which hold back the corruption's growth in their wearer. */
    public static final DeferredItem<com.miophas.corruption.common.item.RobeArmorItem> ROBE_HOOD = robe("robe_hood", net.minecraft.world.item.ArmorItem.Type.HELMET);
    public static final DeferredItem<com.miophas.corruption.common.item.RobeArmorItem> ROBE = robe("robe", net.minecraft.world.item.ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<com.miophas.corruption.common.item.RobeArmorItem> ROBE_LEGGINGS = robe("robe_leggings", net.minecraft.world.item.ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<com.miophas.corruption.common.item.RobeArmorItem> ROBE_BOOTS = robe("robe_boots", net.minecraft.world.item.ArmorItem.Type.BOOTS);
    /** The corrupted robes, a set of four in each grade (CorruptedRobeRules): hood, robe, leggings and boots, novice to advanced. */
    public static final List<DeferredItem<com.miophas.corruption.common.item.CorruptedRobeArmorItem>> CORRUPTED_ROBES = corruptedRobes();
    /** The robe's pieces, head to foot. */
    public static final List<DeferredItem<com.miophas.corruption.common.item.RobeArmorItem>> ROBE_SET = List.of(ROBE_HOOD, ROBE, ROBE_LEGGINGS, ROBE_BOOTS);
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
    /** The dormant corruption: it looks just like the corruption, so its item says what sets it apart. */
    public static final DeferredItem<BlockItem> DEACTIVATED_CORRUPTION = ITEMS.register("deactivated_corruption",
            () -> new com.miophas.corruption.common.item.DescribedBlockItem(ModBlocks.DEACTIVATED_CORRUPTION.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> CORRUPTION_BENCH = ITEMS.registerSimpleBlockItem("corruption_bench", ModBlocks.CORRUPTION_BENCH);
    public static final DeferredItem<BlockItem> ARCUNUM = ITEMS.registerSimpleBlockItem("arcunum", ModBlocks.ARCUNUM);
    public static final DeferredItem<BlockItem> MALICE = ITEMS.registerSimpleBlockItem("malice", ModBlocks.MALICE);
    public static final DeferredItem<BlockItem> CORRUPTION_ARCANE_BRICK =
            ITEMS.registerSimpleBlockItem("corruption_arcane_brick", ModBlocks.CORRUPTION_ARCANE_BRICK);
    /** Fired from clay bricks in the arcane transmuter, 20 KCE apiece; four build a corruption arcane brick. */
    public static final DeferredItem<Item> CURSED_BRICK = ITEMS.register("cursed_brick", () -> new Item(new Item.Properties()));
    /** Copper, transmuted in the arcane transmuter: c:ingots/corrupted_brass. */
    public static final DeferredItem<Item> CORRUPTED_BRASS = ITEMS.register("corrupted_brass", () -> new Item(new Item.Properties()));
    public static final DeferredItem<BlockItem> CORRUPTED_BRASS_BLOCK = ITEMS.registerSimpleBlockItem("corrupted_brass_block", ModBlocks.CORRUPTED_BRASS_BLOCK);
    public static final DeferredItem<BlockItem> BRASS_CAULDRON = ITEMS.registerSimpleBlockItem("brass_cauldron", ModBlocks.BRASS_CAULDRON);
    public static final DeferredItem<BlockItem> ALTAR_PILLAR = ITEMS.registerSimpleBlockItem("altar_pillar", ModBlocks.ALTAR_PILLAR);
    public static final DeferredItem<BlockItem> ALTAR_OFFERING = ITEMS.registerSimpleBlockItem("altar_offering", ModBlocks.ALTAR_OFFERING);
    public static final DeferredItem<BlockItem> CORRUPTION_ARCANE_LECTERN =
            ITEMS.registerSimpleBlockItem("corruption_arcane_lectern", ModBlocks.CORRUPTION_ARCANE_LECTERN);
    public static final DeferredItem<BlockItem> CORRUPTION_ARCANE_PEDESTAL =
            ITEMS.registerSimpleBlockItem("corruption_arcane_pedestal", ModBlocks.CORRUPTION_ARCANE_PEDESTAL);
    public static final DeferredItem<BlockItem> CORRUPTION_ARCANE_CHARGER =
            ITEMS.registerSimpleBlockItem("corruption_arcane_charger", ModBlocks.CORRUPTION_ARCANE_CHARGER);
    public static final DeferredItem<BlockItem> CORRUPTION_ARCANE_TRANSMUTER =
            ITEMS.registerSimpleBlockItem("corruption_arcane_transmuter", ModBlocks.CORRUPTION_ARCANE_TRANSMUTER);
    public static final DeferredItem<BlockItem> CORRUPTION_TEETH = ITEMS.registerSimpleBlockItem("corruption_teeth", ModBlocks.CORRUPTION_TEETH);
    public static final DeferredItem<BlockItem> MALICE_GRASS = ITEMS.registerSimpleBlockItem("malice_grass", ModBlocks.MALICE_GRASS);
    public static final DeferredItem<BlockItem> WHISPER_BLOOM = ITEMS.registerSimpleBlockItem("whisper_bloom", ModBlocks.WHISPER_BLOOM);
    public static final DeferredItem<BlockItem> BLOOD_FILAMENTS = ITEMS.registerSimpleBlockItem("blood_filaments", ModBlocks.BLOOD_FILAMENTS);
    public static final DeferredItem<BlockItem> ELDRITCH = ITEMS.registerSimpleBlockItem("eldritch", ModBlocks.ELDRITCH);
    public static final DeferredItem<BlockItem> EERIE = ITEMS.registerSimpleBlockItem("eerie", ModBlocks.EERIE);
    public static final DeferredItem<BlockItem> ARCUNUM_END = ITEMS.registerSimpleBlockItem("arcunum_end", ModBlocks.ARCUNUM_END);
    public static final DeferredItem<BlockItem> SPIRIT = ITEMS.registerSimpleBlockItem("spirit", ModBlocks.SPIRIT);
    public static final DeferredItem<BlockItem> SPIRIT_DEEPSLATE = ITEMS.registerSimpleBlockItem("spirit_deepslate", ModBlocks.SPIRIT_DEEPSLATE);

    private static DeferredItem<CorruptionArcaneStickItem> wand(String id, CorruptionArcaneStickItem.Tier tier) {
        return ITEMS.register(id, () -> new CorruptionArcaneStickItem(new Item.Properties(), tier));
    }

    private static DeferredItem<com.miophas.corruption.common.item.RobeArmorItem> robe(String id, net.minecraft.world.item.ArmorItem.Type type) {
        return ITEMS.register(id, () -> new com.miophas.corruption.common.item.RobeArmorItem(type));
    }

    private static List<DeferredItem<com.miophas.corruption.common.item.CorruptedRobeArmorItem>> corruptedRobes() {
        List<DeferredItem<com.miophas.corruption.common.item.CorruptedRobeArmorItem>> robes = new java.util.ArrayList<>();
        var tiers = com.miophas.corruption.core.corruption.CorruptedRobeRules.Tier.values();
        List<net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.item.ArmorMaterial, net.minecraft.world.item.ArmorMaterial>> materials = List.of(
                ModArmorMaterials.NOVICE_CORRUPTED_ROBE, ModArmorMaterials.INTERMEDIATE_CORRUPTED_ROBE, ModArmorMaterials.ADVANCED_CORRUPTED_ROBE);
        int[] durability = {15, 25, 37};
        String[] pieces = {"_hood", "", "_leggings", "_boots"};
        net.minecraft.world.item.ArmorItem.Type[] types = {net.minecraft.world.item.ArmorItem.Type.HELMET, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE,
                net.minecraft.world.item.ArmorItem.Type.LEGGINGS, net.minecraft.world.item.ArmorItem.Type.BOOTS};
        for (int grade = 0; grade < tiers.length; grade++) {
            var tier = tiers[grade];
            var material = materials.get(grade);
            int lasts = durability[grade];
            for (int piece = 0; piece < pieces.length; piece++) {
                var type = types[piece];
                robes.add(ITEMS.register(tier.name().toLowerCase(java.util.Locale.ROOT) + "_corrupted_robe" + pieces[piece],
                        () -> new com.miophas.corruption.common.item.CorruptedRobeArmorItem(tier, material, type, lasts)));
            }
        }
        return List.copyOf(robes);
    }

    private static DeferredItem<CorruptionMaterialItem> material(String id) {
        return ITEMS.register(id, () -> new CorruptionMaterialItem(new Item.Properties()));
    }

    private static DeferredItem<Item> key(String id, Rarity rarity) {
        return ITEMS.register(id, () -> new Item(new Item.Properties().stacksTo(1).rarity(rarity)));
    }

    /** Roll of 1-2 corruption dust, granted as an extra drop by corrupted blocks. */
    public static ItemStack corruptionDustDrop(RandomSource random) {
        return new ItemStack(CORRUPTION_DUST.get(), 1 + random.nextInt(2));
    }

    private ModItems() {}
}
