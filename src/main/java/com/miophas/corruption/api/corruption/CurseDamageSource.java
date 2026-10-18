package com.miophas.corruption.api.corruption;

import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

/**
 * The hurt of the corruption's curse, each time it pulses: of the damage type corruption:corruption, as the corrupted
 * ground's hurt is, so every rule about the corruption's damage holds for it as before (the robes, the corruption's own
 * immunity, the tags it is in); but it is its own source, so what guards against the curse alone (CurseWardItem) can
 * tell it from the rest ({@link CorruptionCurseApi#isCurse}). As the ground's, it throws nothing back and sets nothing
 * panicking. Made by {@link CorruptionCurseApi#damage}.
 */
public final class CurseDamageSource extends DamageSource {
    CurseDamageSource(Holder<DamageType> type) { super(type); }

    @Override public boolean is(TagKey<DamageType> tag) {
        if (tag.equals(DamageTypeTags.NO_KNOCKBACK) || tag.equals(DamageTypeTags.NO_IMPACT)) return true;
        if (tag.equals(DamageTypeTags.PANIC_CAUSES) || tag.equals(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES)) return false;
        return super.is(tag);
    }
}
