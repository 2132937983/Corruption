package com.miophas.corruption.api.arcane;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

/**
 * The mark every spell's hurt carries. A spell does not hurt with the ordinary magics of the game: it hurts with
 * {@link #SPELL}, a damage type of the corruption's own, so a creature can tell a spell from an ordinary blow
 * and answer each as it will — something meant to fall to the arcane alone asks {@link #isSpell(DamageSource)}
 * and turns every other hurt away.
 *
 * <p>The type itself is data, data/corruption/damage_type/arcane_spell.json, and it carries its own death
 * message. {@link #SPELL_DAMAGE} names the tag it begins in, so an add-on's own spell damage may join by data
 * alone and answer to the same question.
 *
 * <p>Only the hurts a spell deals as a {@link com.miophas.corruption.core.arcane.SpellAction.Damage} carry this
 * mark. What a spell does through the world instead — burning, lightning, an explosion, the cold of a freezing
 * spell — is the world's own doing, with the sources the game gives those, and is not marked here.
 */
public final class ArcaneDamage {
    /** The damage type a spell's hurt is dealt with, corruption:arcane_spell. */
    public static final ResourceKey<DamageType> SPELL = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath("corruption", "arcane_spell"));
    /** What counts as a spell's hurt: the tag corruption:spell, which {@link #SPELL} begins. */
    public static final TagKey<DamageType> SPELL_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath("corruption", "spell"));

    /** The source a spell hurts with: dealt by this caster, and marked as a spell's. */
    public static DamageSource source(Level level, ServerPlayer caster) { return source(level, caster, ""); }

    /**
     * The source a spell hurts with, of the kind the spell names — or of {@link #SPELL} for "" or for a kind
     * the game does not know. So a spell that names none hurts with the arcane mark, and one that names the
     * corruption's own hurt, say, hurts with that instead, and the corruption's children are proof against it
     * as they are against the rest of the corruption.
     */
    public static DamageSource source(Level level, ServerPlayer caster, String type) {
        Registry<DamageType> kinds = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        Holder<DamageType> kind = type == null || type.isEmpty() ? kinds.getHolderOrThrow(SPELL)
                : kinds.getHolder(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(type))).orElse(kinds.getHolderOrThrow(SPELL));
        return new DamageSource(kind, caster, caster);
    }

    /** Whether this hurt came from a spell, so that what falls to the arcane alone may tell one from a blow. */
    public static boolean isSpell(DamageSource source) {
        return source.is(SPELL) || source.is(SPELL_DAMAGE);
    }

    private ArcaneDamage() {}
}
