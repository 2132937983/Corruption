package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.ArcaneReading;
import com.miophas.corruption.core.arcane.Codex;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * The corruption's arcane for this mod and its add-ons: its strings ({@link ArcaneString}), the keys that
 * reveal them ({@link ArcaneKey}), and parchment that bears one. The rules (what a key reveals, what a
 * reader understands, how a copy is made) are plain Java in com.miophas.corruption.core.arcane; this is
 * where they meet the game's items and registries.
 */
public final class ArcaneApi {
    /** Items that take an inscription: the mod's parchment, and whatever data packs add to corruption:parchment. */
    public static final TagKey<Item> PARCHMENT = ItemTags.create(ResourceLocation.fromNamespaceAndPath("corruption", "parchment"));
    /** What transcription uses up as ink: corruption:arcane_ink, the mod's bottles of ink to begin with. */
    public static final TagKey<Item> INK = ItemTags.create(ResourceLocation.fromNamespaceAndPath("corruption", "arcane_ink"));
    /** The font arcane forms are drawn in: the enchanting table's glyphs. */
    public static final ResourceLocation GLYPH_FONT = ResourceLocation.withDefaultNamespace("alt");

    /** How the inscription is written: in data and saves as the string's id, "corruption:baal". */
    public static final Codec<ResourceKey<ArcaneString>> INSCRIPTION_CODEC = ResourceKey.codec(ArcaneString.REGISTRY);
    public static final StreamCodec<ByteBuf, ResourceKey<ArcaneString>> INSCRIPTION_STREAM_CODEC = ResourceKey.streamCodec(ArcaneString.REGISTRY);
    /** The data component corruption:inscription: the one string a parchment bears. The mod registers it. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<ArcaneString>>> INSCRIPTION =
            DeferredHolder.create(Registries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath("corruption", "inscription"));

    // Strings

    /** A string's id as the rules know it: "corruption:baal". */
    public static String id(ResourceKey<ArcaneString> string) { return string.location().toString(); }
    /** The string with this id, or null if the id is not one. */
    public static ResourceKey<ArcaneString> key(String id) {
        ResourceLocation location = id == null ? null : ResourceLocation.tryParse(id);
        return location == null ? null : ResourceKey.create(ArcaneString.REGISTRY, location);
    }
    /** What a string is, as the data loaded says, or null if no such string was loaded. */
    public static ArcaneString string(HolderLookup.Provider registries, ResourceKey<ArcaneString> string) {
        if (registries == null || string == null) return null;
        return registries.lookup(ArcaneString.REGISTRY).flatMap(strings -> strings.get(string)).map(Holder::value).orElse(null);
    }

    // Keys

    public static boolean isKey(ItemStack stack) { return ArcaneKey.of(stack) != null; }

    /** The strings an item reveals, in its order; none if it is no key, or its tag holds none. */
    public static List<Holder<ArcaneString>> strings(HolderLookup.Provider registries, ItemStack stack) {
        ArcaneKey key = ArcaneKey.of(stack);
        if (key == null || registries == null) return List.of();
        return registries.lookup(ArcaneString.REGISTRY).flatMap(strings -> strings.get(key.strings()))
                .map(set -> set.stream().<Holder<ArcaneString>>map(holder -> holder).toList()).orElse(List.of());
    }

    /** The same as the rules see it: the ids of the strings an item reveals. */
    public static Codex codex(HolderLookup.Provider registries, ItemStack stack) {
        List<String> ids = new ArrayList<>();
        for (Holder<ArcaneString> string : strings(registries, stack)) string.unwrapKey().ifPresent(key -> ids.add(id(key)));
        return ids.isEmpty() ? Codex.EMPTY : new Codex(ids);
    }

    /**
     * Every item that is a key revealing this string, in the order the items were registered: where a player finds
     * it. None if no key reveals it, as for a string an add-on writes without a book.
     */
    public static List<ItemStack> keysTo(HolderLookup.Provider registries, ResourceKey<ArcaneString> string) {
        if (registries == null || string == null) return List.of();
        HolderLookup.RegistryLookup<ArcaneString> strings = registries.lookup(ArcaneString.REGISTRY).orElse(null);
        if (strings == null) return List.of();
        List<Item> keys = new ArrayList<>();
        BuiltInRegistries.ITEM.getDataMap(ArcaneKey.DATA_MAP).forEach((item, key) -> {
            boolean reveals = strings.get(key.strings()).map(set -> set.stream().anyMatch(held -> held.is(string))).orElse(false);
            if (reveals) BuiltInRegistries.ITEM.getOptional(item).ifPresent(keys::add);
        });
        keys.sort(Comparator.comparingInt(BuiltInRegistries.ITEM::getId));
        return keys.stream().map(ItemStack::new).toList();
    }

    /** What a reader carrying these items makes of a string: its meaning if any of them is a key that reveals it, else only its glyphs. */
    public static ArcaneReading.Sight sight(HolderLookup.Provider registries, ResourceKey<ArcaneString> string, Iterable<ItemStack> carried) {
        List<Codex> keys = new ArrayList<>();
        for (ItemStack stack : carried) if (isKey(stack)) keys.add(codex(registries, stack));
        return ArcaneReading.sight(id(string), keys);
    }

    // Parchment

    /** The string an item bears, or null if it bears none. */
    public static ResourceKey<ArcaneString> inscription(ItemStack stack) { return stack.isEmpty() ? null : stack.get(INSCRIPTION.get()); }
    /** Whether an item is parchment with nothing written on it yet. */
    public static boolean isBlankParchment(ItemStack stack) { return !stack.isEmpty() && stack.is(PARCHMENT) && inscription(stack) == null; }
    /** One of this parchment, bearing the string. */
    public static ItemStack inscribed(ItemStack parchment, ResourceKey<ArcaneString> string) {
        ItemStack one = parchment.copyWithCount(1);
        one.set(INSCRIPTION.get(), string);
        return one;
    }
    public static boolean isInk(ItemStack stack) { return !stack.isEmpty() && stack.is(INK); }

    /**
     * How many copies' worth of ink a stack holds. Ink that wears, as a bottle of it does, holds what it has left
     * of its uses; any other ink is one copy an item.
     */
    public static int inkIn(ItemStack stack) {
        if (!isInk(stack)) return 0;
        return stack.isDamageableItem() ? (stack.getMaxDamage() - stack.getDamageValue()) * stack.getCount() : stack.getCount();
    }

    /**
     * Uses up this many copies' worth of ink, and answers what is left of the stack. Ink that wears wears a use
     * a copy, and once used up leaves what it is made to leave, a bottle of ink its glass bottle; other ink is
     * used up an item a copy.
     */
    public static ItemStack useInk(ItemStack stack, int copies) {
        ItemStack left = stack.copy();
        for (int copy = 0; copy < copies && isInk(left); copy++) {
            if (!left.isDamageableItem()) { left.shrink(1); continue; }
            left.setDamageValue(left.getDamageValue() + 1);
            if (left.getDamageValue() >= left.getMaxDamage()) left = left.getCraftingRemainingItem();
        }
        return left;
    }

    // Showing it

    /** An arcane form as it is seen: drawn in glyphs. */
    public static MutableComponent glyphs(String text) { return Component.literal(text).withStyle(style -> style.withFont(GLYPH_FONT)); }

    private ArcaneApi() {}
}
