package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;

/** Damage types are data-driven registry entries; resolve them from the active world's registry. */
public final class ModDamageTypes {
    public static final ResourceKey<DamageType> CORRUPTION = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corruption"));
    public static final ResourceKey<DamageType> CORRUPTION_CONVERSION = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID,"corruption_conversion"));
    public static DamageSource conversion(Level level){return new ContactDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(CORRUPTION_CONVERSION));}

    /** The corruption taking a creature at once by this one's doing, such as a spell's caster, who has the credit of it. */
    public static DamageSource conversion(Level level, net.minecraft.world.entity.LivingEntity attacker) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(CORRUPTION_CONVERSION), attacker);
    }

    public static DamageSource corruption(Level level) {
        return new ContactDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(CORRUPTION));
    }

    /**
     * The corruption's hurt by this one's doing; dealt by one that no robe deceives (CorruptionVeilApi.PIERCES_VEIL), it is
     * Solomon's hurt instead ({@link #SOLOMON}), which no robe turns aside.
     */
    public static DamageSource corruption(Level level, net.minecraft.world.entity.LivingEntity attacker) {
        if (com.miophas.corruption.api.corruption.CorruptionVeilApi.pierces(attacker)) return solomon(level, attacker);
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(CORRUPTION), attacker);
    }

    /**
     * Solomon's hurt (corruption:solomon): the corruption's, as the king who bound its lords deals it; not in the tag
     * corruption:corruption_damage, so no corrupted robe turns it aside, and not in corruption:spell.
     */
    public static final ResourceKey<DamageType> SOLOMON = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "solomon"));

    /** Solomon's hurt by this one's doing. */
    public static DamageSource solomon(Level level, @javax.annotation.Nullable net.minecraft.world.entity.Entity attacker) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SOLOMON), attacker);
    }

    /**
     * Hurt in the mind (corruption:psychic): armour does not turn it aside (the tag minecraft:bypasses_armor), though
     * Protection and Resistance do, and it throws nothing back (minecraft:no_knockback).
     */
    public static final ResourceKey<DamageType> PSYCHIC = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "psychic"));

    /** Hurt in the mind by this one's doing. */
    public static DamageSource psychic(Level level, net.minecraft.world.entity.LivingEntity attacker) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(PSYCHIC), attacker);
    }

    /** Environmental contact keeps damage events/cooldowns without knockback or panic AI. */
    private static final class ContactDamageSource extends DamageSource {
        private ContactDamageSource(Holder<DamageType> type) { super(type); }
        @Override public boolean is(TagKey<DamageType> tag) {
            if (tag.equals(DamageTypeTags.NO_KNOCKBACK) || tag.equals(DamageTypeTags.NO_IMPACT)) return true;
            if (tag.equals(DamageTypeTags.PANIC_CAUSES) || tag.equals(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES)) return false;
            return super.is(tag);
        }
    }

    private ModDamageTypes() {}
}
