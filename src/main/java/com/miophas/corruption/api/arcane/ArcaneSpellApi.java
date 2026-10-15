package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.SpellRule;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

/**
 * The corruption's arcane spells for this mod and its add-ons: which string casts what, and the grimoire a wand
 * carries. The rules (what a spell costs, who may cast it, what it does) are plain Java in core.arcane; this is
 * where they meet the game's data.
 */
public final class ArcaneSpellApi {
    /** The spell of this string, or null if the string has none: ids match, so the string is looked up as a spell. */
    public static ArcaneSpell spell(HolderLookup.Provider registries, ResourceKey<ArcaneString> string) {
        if (registries == null || string == null) return null;
        ResourceKey<ArcaneSpell> spell = ResourceKey.create(ArcaneSpell.REGISTRY, string.location());
        return registries.lookup(ArcaneSpell.REGISTRY).flatMap(loaded -> loaded.get(spell)).map(Holder::value).orElse(null);
    }

    /** The rule of this string's spell, or {@link SpellRule#NONE} if the string has none. */
    public static SpellRule rule(HolderLookup.Provider registries, ResourceKey<ArcaneString> string) {
        ArcaneSpell spell = spell(registries, string);
        return spell == null ? SpellRule.NONE : spell.rule();
    }

    /** Whether this string can be cast at all. */
    public static boolean castable(HolderLookup.Provider registries, ResourceKey<ArcaneString> string) {
        return spell(registries, string) != null;
    }

    private ArcaneSpellApi() {}
}
