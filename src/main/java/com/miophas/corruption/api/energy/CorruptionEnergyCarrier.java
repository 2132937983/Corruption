package com.miophas.corruption.api.energy;

import com.miophas.corruption.core.energy.CarriedEnergy;
import com.miophas.corruption.core.energy.EnergyAcceptance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

/**
 * An item that carries Corruption Energy about, to be given to blocks and entities that hold it, as the
 * Corruption Candle does. Two pieces of data make one, both open to data packs.
 * <ul>
 *   <li>The corruption_energy_carrier data map says which items carry energy, and how, in
 *   data/&lt;namespace&gt;/data_maps/item/corruption_energy_carrier.json:
 *   <pre>{"values": {"corruption:corruption_candle": {"capacity": "10KCE", "consumed": true}}}</pre></li>
 *   <li>The data component corruption:corruption_energy says how much one item carries now, so whatever
 *   makes an item, a recipe say, can give it a charge:
 *   <pre>"result": {"id": "corruption:corruption_candle", "components": {"corruption:corruption_energy": "10KCE"}}</pre>
 *   An item with none carries nothing.</li>
 * </ul>
 * Every item the data map names offers its energy through {@link CorruptionEnergyCapabilities#ITEM}, and
 * {@link CorruptionEnergyApi#importFrom} gives it to any block's or entity's store. An item of one's own is
 * best made a {@link CorruptionEnergyItem}, which gives its energy out all at once or bit by bit.
 *
 * @param capacity the most one item carries, in CE
 * @param consumed whether an item is used up once it has given all it carried; otherwise it stays, empty
 */
public record CorruptionEnergyCarrier(long capacity, boolean consumed) {
    public static final Codec<CorruptionEnergyCarrier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CorruptionEnergyCodec.AMOUNT.fieldOf("capacity").forGetter(CorruptionEnergyCarrier::capacity),
            Codec.BOOL.optionalFieldOf("consumed", false).forGetter(CorruptionEnergyCarrier::consumed)).apply(instance, CorruptionEnergyCarrier::new));

    /** The data map, corruption:corruption_energy_carrier. The mod registers it, and sends it to clients so tooltips can show it. */
    public static final DataMapType<Item, CorruptionEnergyCarrier> DATA_MAP = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath("corruption", "corruption_energy_carrier"), Registries.ITEM, CODEC).synced(CODEC, false).build();

    /** How the component is written: in data and saves as an amount, "10KCE" or 10000; sent to clients as a number of CE. */
    public static final Codec<Long> ENERGY_CODEC = CorruptionEnergyCodec.AMOUNT;
    public static final StreamCodec<ByteBuf, Long> ENERGY_STREAM_CODEC = ByteBufCodecs.VAR_LONG;
    /** The data component corruption:corruption_energy: the CE one item carries. The mod registers it. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> ENERGY =
            DeferredHolder.create(Registries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath("corruption", "corruption_energy"));

    /** How this kind of item carries energy, or null if it does not. */
    @SuppressWarnings("deprecation")
    public static CorruptionEnergyCarrier of(ItemStack stack) {
        return stack.isEmpty() ? null : stack.getItem().builtInRegistryHolder().getData(DATA_MAP);
    }

    /** What one of these items carries, in CE; nothing if it carries no energy. */
    public static long carried(ItemStack stack) {
        CorruptionEnergyCarrier carrier = of(stack);
        return carrier == null ? 0 : EnergyAcceptance.carried(stack.getOrDefault(ENERGY.get(), 0L), carrier.capacity());
    }

    /** A copy of the stack with each item carrying this much. */
    public static ItemStack charged(ItemStack stack, long energy) {
        ItemStack copy = stack.copy();
        copy.set(ENERGY.get(), Math.max(0, energy));
        return copy;
    }

    /**
     * The store this stack's data makes, or null if it carries no energy. Energy moves in and out of a stack
     * of one; a stack of several says what it carries in all but moves nothing (see core.energy.CarriedEnergy).
     * An item that is used up once empty leaves the stack as it gives the last of its energy.
     */
    public static CorruptionEnergyStorage storage(ItemStack stack) { return carriedEnergy(stack); }

    /** The same store, with the rules' own ways of giving energy all at once or bit by bit. */
    static CarriedEnergy carriedEnergy(ItemStack stack) {
        CorruptionEnergyCarrier carrier = of(stack);
        return carrier == null ? null : new CarriedEnergy(carrier.capacity(), carrier.consumed(), stack.getCount(),
                () -> stack.getOrDefault(ENERGY.get(), 0L), energy -> stack.set(ENERGY.get(), energy), () -> stack.shrink(1));
    }
}
