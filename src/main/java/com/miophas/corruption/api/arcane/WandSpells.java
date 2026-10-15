package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.Grimoire;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * The spells a wand has been taught, and which of them is chosen: the {@link Grimoire}, carried as the data
 * component corruption:wand_spells. The arcane pedestal writes spells in; casting and the wheel screen read
 * them out. What the rules are is core.arcane.Grimoire's.
 */
public final class WandSpells {
    public static final Codec<Grimoire> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().optionalFieldOf("spells", List.of()).forGetter(Grimoire::spells),
            Codec.INT.optionalFieldOf("selected", 0).forGetter(Grimoire::selected)).apply(instance, Grimoire::new));
    public static final StreamCodec<ByteBuf, Grimoire> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), Grimoire::spells,
            ByteBufCodecs.VAR_INT, Grimoire::selected, Grimoire::new);

    /** The data component corruption:wand_spells: what a wand has been taught. The mod registers it. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Grimoire>> TYPE =
            DeferredHolder.create(Registries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath("corruption", "wand_spells"));

    /** What a wand has been taught; empty for anything that carries nothing. */
    public static Grimoire of(ItemStack stack) {
        Grimoire grimoire = stack.isEmpty() ? null : stack.get(TYPE.get());
        return grimoire == null ? Grimoire.EMPTY : grimoire;
    }

    /** Writes this grimoire onto the stack itself; one with nothing in it carries the component no longer. */
    public static void set(ItemStack stack, Grimoire grimoire) {
        if (grimoire.isEmpty()) stack.remove(TYPE.get());
        else stack.set(TYPE.get(), grimoire);
    }

    private WandSpells() {}
}
