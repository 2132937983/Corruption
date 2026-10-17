package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.common.item.InfectedBlockItem;
import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneString;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeModeTabs {
    /** The elites' spawn eggs go in the game's own Spawn Eggs tab as well as the mod's. */
    public static void addToGameTabs(net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) for (var egg : ModItems.ELITE_EGGS) event.accept(egg.get());
    }

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Corruption.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CORRUPTION =
            TABS.register("corruption", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.corruption"))
                    .withTabsBefore(CreativeModeTabs.NATURAL_BLOCKS)
                    .icon(() -> ModItems.CORRUPTION.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CORRUPTION_DUST.get());
                        output.accept(ModItems.CORRUPTION_STICK.get());
                        for (var wand : ModItems.WANDS) output.accept(wand.get());
                        output.accept(ModItems.CORRUPTION_MIXTURE_LEV0.get());
                        output.accept(ModItems.CORRUPTION_CRYSTAL.get());
                        output.accept(ModItems.ANCIENT_CORRUPTION_CRYSTAL.get());
                        output.accept(ModItems.HARMONIZED_CORRUPTION_CRYSTAL.get());
                        output.accept(ModItems.CORRUPTION_REMAIN.get());
                        output.accept(ModItems.LEAF_WINNOWER.get());
                        output.accept(ModItems.PURE_LEAF.get());
                        output.accept(ModItems.CORRUPTION_BENCH.get());
                        output.accept(ModItems.CORRUPTION.get());
                        output.accept(ModItems.DEACTIVATED_CORRUPTION.get());
                        output.accept(ModItems.MALICE.get());
                        output.accept(ModItems.CORRUPTION_ARCANE_BRICK.get());
                        output.accept(ModItems.CURSED_BRICK.get());
                        output.accept(ModItems.CORRUPTION_ARCANE_TRANSMUTER.get());
                        // A candle as the transmuter makes it, carrying all it can.
                        net.minecraft.world.item.ItemStack candle = new net.minecraft.world.item.ItemStack(ModItems.CORRUPTION_CANDLE.get());
                        CorruptionEnergyCarrier carrier = CorruptionEnergyCarrier.of(candle);
                        output.accept(CorruptionEnergyCarrier.charged(candle, carrier == null ? CorruptionEnergyApi.kilo(10) : carrier.capacity()));
                        output.accept(ModItems.CORRUPTION_ARCANE_LECTERN.get());
                        output.accept(ModItems.CORRUPTION_ARCANE_PEDESTAL.get());
                        output.accept(ModItems.CORRUPTION_ARCANE_CHARGER.get());
                        output.accept(ModItems.ALTAR_OFFERING.get());
                        output.accept(ModItems.ALTAR_PILLAR.get());
                        output.accept(ModItems.CORRUPTED_BRASS.get());
                        output.accept(ModItems.CORRUPTED_BRASS_BLOCK.get());
                        output.accept(ModItems.BRASS_CAULDRON.get());
                        for (var key : ModItems.KEYS) output.accept(key.get());
                        output.accept(ModItems.PARCHMENT.get());
                        output.accept(ModItems.INK.get());
                        for (var material : ModItems.MATERIALS) output.accept(material.get());
                        for (var relic : ModItems.RELICS) output.accept(relic.get());
                        for (var egg : ModItems.ELITE_EGGS) output.accept(egg.get());
                        output.accept(ModItems.CORRUPTED_BREAD.get());
                        output.accept(ModItems.PURIFIED_WATER.get());
                        for (var piece : ModItems.ROBE_SET) output.accept(piece.get());
                        for (var piece : ModItems.CORRUPTED_ROBES) output.accept(piece.get());
                        output.accept(ModItems.CORRUPTION_ENERGY_SWORD.get());
                        output.accept(ModItems.CORRUPTED_ARCANE_SWORD.get());
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

    /**
     * A parchment bearing each string the data holds, for trying recipes and the pedestal without the lectern: so
     * many that they have a tab of their own. They come book by book, in each book's order, and any string no
     * book reveals (an add-on's, say) after them.
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PARCHMENTS =
            TABS.register("corruption_parchments", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.corruption_parchments"))
                    .withTabsBefore(CORRUPTION.getKey())
                    .icon(() -> ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), ArcaneApi.key("corruption:corruption")))
                    .displayItems((parameters, output) -> {
                        ItemStack blank = new ItemStack(ModItems.PARCHMENT.get());
                        for (ResourceKey<ArcaneString> string : inscribable(parameters.holders()))
                            output.accept(ArcaneApi.inscribed(blank, string));
                    })
                    .build());

    /** Every string the data holds: those the keys reveal first, key by key, then the rest. */
    public static List<ResourceKey<ArcaneString>> inscribable(HolderLookup.Provider registries) {
        Set<ResourceKey<ArcaneString>> strings = new LinkedHashSet<>();
        for (var key : ModItems.KEYS)
            for (Holder<ArcaneString> string : ArcaneApi.strings(registries, new ItemStack(key.get()))) string.unwrapKey().ifPresent(strings::add);
        registries.lookup(ArcaneString.REGISTRY).ifPresent(all -> all.listElementIds().forEach(strings::add));
        return List.copyOf(strings);
    }

    private ModCreativeModeTabs() {}
}
