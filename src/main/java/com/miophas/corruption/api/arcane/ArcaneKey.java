package com.miophas.corruption.api.arcane;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

/**
 * What makes an item a key to the arcane, as the Clavicula Salomonis is: the strings it reveals, a tag of the
 * arcane string registry. Which items are keys is data, the corruption:arcane_key data map, in
 * data/&lt;namespace&gt;/data_maps/item/arcane_key.json:
 * <pre>{"values": {"corruption:clavicula_salomonis": {"strings": "#corruption:clavicula_salomonis"}}}</pre>
 * so any item, an add-on's book or a vanilla one, can be made a key, and a key's strings grow as data packs
 * add to its tag.
 */
public record ArcaneKey(TagKey<ArcaneString> strings) {
    public static final Codec<ArcaneKey> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.hashedCodec(ArcaneString.REGISTRY).fieldOf("strings").forGetter(ArcaneKey::strings)).apply(instance, ArcaneKey::new));

    /** The data map itself. The mod registers it, and sends it to clients so screens and tooltips can read keys. */
    public static final DataMapType<Item, ArcaneKey> DATA_MAP = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("corruption", "arcane_key"), Registries.ITEM, CODEC).synced(CODEC, false).build();

    /** What this item is a key to, or null if it is none. */
    @SuppressWarnings("deprecation")
    public static ArcaneKey of(ItemStack stack) {
        return stack.isEmpty() ? null : stack.getItem().builtInRegistryHolder().getData(DATA_MAP);
    }
}
