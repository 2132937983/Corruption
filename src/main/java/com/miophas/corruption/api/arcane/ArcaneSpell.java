package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.SpellRule;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * A string of the arcane made castable. Spells are data, a registry of their own that any data pack adds to,
 * one file each, in data/&lt;namespace&gt;/corruption/arcane_spell/&lt;name&gt;.json. A spell's id is the id of
 * the {@link ArcaneString} it belongs to, so a parchment bearing a string names its spell by the same name:
 * <pre>{@code {"energy": "2KCE", "actions": [{"type": "corrupt_area", "radius": 3, "chance": 0.25}]}}</pre>
 * A string with no spell is read, and copied onto parchment, but cannot be cast. What the rule means, and what
 * it costs to cast, is {@link SpellRule}; how it is written is {@link SpellCodecs}.
 */
public record ArcaneSpell(SpellRule rule) {
    public static final Codec<ArcaneSpell> CODEC = SpellCodecs.RULE.xmap(ArcaneSpell::new, ArcaneSpell::rule);

    /** The registry, corruption:arcane_spell. The mod creates it and sends it to clients. */
    public static final ResourceKey<Registry<ArcaneSpell>> REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("corruption", "arcane_spell"));
}
