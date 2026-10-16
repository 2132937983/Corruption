package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CorruptedRobeRules;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * What a corrupted robe ({@link CorruptedRobeItem}) does for its wearer, for whatever asks: the corruption's creatures
 * before they take something for prey, what hurts with the corruption's hurt, what raises or lowers a player's
 * corruption. The mod's own creatures and rules ask here; an add-on's corruption creature should ask
 * {@link #hiddenFrom} before it takes a target.
 */
public final class CorruptionVeilApi {
    /**
     * The corruption's own hurt, the damage type tag corruption:corruption_damage: what the robes turn aside. It holds
     * the corruption's hurt (corruption:corruption, which its ground, its waters, its curse and its spells deal) and
     * its taking a creature at once (corruption:corruption_conversion); an add-on's own may join by data.
     */
    public static final TagKey<DamageType> CORRUPTION_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("corruption", "corruption_damage"));

    /** The grade of corrupted robe this creature wears as a set, if it wears one. */
    public static Optional<CorruptedRobeRules.Tier> worn(LivingEntity wearer) { return CorruptedRobeItem.worn(wearer); }

    /**
     * Whether a creature is hidden from one of the corruption's, so that it may not take it for prey nor turn on it
     * when struck: from its creatures by any set of corrupted robes, from its lords only by the highest grade. What is
     * not of the corruption sees as it always did.
     */
    public static boolean hiddenFrom(LivingEntity target, @Nullable Entity seer) {
        if (seer == null || seer == target || !CorruptionFactionApi.isRemnant(seer)) return false;
        Optional<CorruptedRobeRules.Tier> worn = worn(target);
        if (worn.isEmpty()) return false;
        return CorruptionFactionApi.isBoss(seer) ? worn.get().hiddenFromBosses() : worn.get().hiddenFromRemnants();
    }

    /** Whether a hurt is the corruption's own. */
    public static boolean isCorruptionDamage(DamageSource source) { return source.is(CORRUPTION_DAMAGE); }

    /** How much of a hurt this much reaches a creature, its robe turning aside what its grade turns aside of the corruption's. */
    public static float damageTaken(LivingEntity target, DamageSource source, float amount) {
        if (!isCorruptionDamage(source)) return amount;
        return worn(target).map(tier -> tier.corruptionDamage(amount)).orElse(amount);
    }

    /**
     * Whether a set of corrupted robes holds this wearer's corruption fast (at its grade's threshold or above): then
     * nothing raises or lowers it but the robe itself.
     */
    public static boolean holdsCorruption(LivingEntity wearer) { return worn(wearer).isPresent(); }

    private CorruptionVeilApi() {}
}
