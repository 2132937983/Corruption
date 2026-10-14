package com.miophas.corruption.api.arcane;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

/**
 * A string of the corruption's arcane. Strings are data, a registry of their own that any data pack adds to,
 * one file each, in data/&lt;namespace&gt;/corruption/arcane_string/&lt;name&gt;.json:
 * <pre>{"text": "bael rex primus orientis", "meaning": {"translate": "arcane.corruption.baal"}}</pre>
 * Its text is its arcane form, which anyone can see, drawn in the enchanting table's glyphs; its meaning is
 * shown only to whoever holds a key that reveals it (see {@link ArcaneKey}). Which strings a key reveals is a
 * tag of this registry, data/&lt;namespace&gt;/tags/corruption/arcane_string/&lt;tag&gt;.json.
 *
 * @param text its arcane form
 * @param meaning what it says, read through a key: plain text, or better a translation key
 */
public record ArcaneString(String text, Component meaning) {
    public static final Codec<ArcaneString> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_EMPTY_STRING.fieldOf("text").forGetter(ArcaneString::text),
            ComponentSerialization.CODEC.fieldOf("meaning").forGetter(ArcaneString::meaning)).apply(instance, ArcaneString::new));

    /** The registry, corruption:arcane_string. The mod creates it and sends it to clients. */
    public static final ResourceKey<Registry<ArcaneString>> REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("corruption", "arcane_string"));
}
