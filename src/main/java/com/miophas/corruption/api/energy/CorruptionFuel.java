package com.miophas.corruption.api.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

/**
 * What one item burnt for Corruption Energy is worth, and how long it takes to burn. Which items are fuel
 * is data, not code: any data pack, an add-on's included, names them in
 * data/&lt;namespace&gt;/data_maps/item/corruption_fuel.json:
 * <pre>{"values": {"corruption:corruption_dust": {"energy": "0.1KCE", "ticks": 10}}}</pre>
 * The arcane transmuter burns these, and so can any add-on's generator: {@link #of} says what an item is worth.
 *
 * @param energy in CE
 */
public record CorruptionFuel(long energy, int ticks) {
    public static final Codec<CorruptionFuel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CorruptionEnergyCodec.AMOUNT.fieldOf("energy").forGetter(CorruptionFuel::energy),
            ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter(CorruptionFuel::ticks)).apply(instance, CorruptionFuel::new));

    /** The data map itself, corruption:corruption_fuel. The mod registers it; it is sent to clients, so screens and JEI can show fuel values too. */
    public static final DataMapType<Item, CorruptionFuel> DATA_MAP = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("corruption", "corruption_fuel"), Registries.ITEM, CODEC).synced(CODEC, false).build();

    /** What this item is worth as fuel, or null if it is none. */
    @SuppressWarnings("deprecation")
    public static CorruptionFuel of(ItemStack stack) {
        return stack.isEmpty() ? null : stack.getItem().builtInRegistryHolder().getData(DATA_MAP);
    }
}
