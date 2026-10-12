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

    public static DamageSource corruption(Level level) {
        return new ContactDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(CORRUPTION));
    }

    public static DamageSource corruption(Level level, net.minecraft.world.entity.LivingEntity attacker) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(CORRUPTION), attacker);
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
