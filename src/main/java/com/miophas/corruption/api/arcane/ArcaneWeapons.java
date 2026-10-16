package com.miophas.corruption.api.arcane;

import com.miophas.corruption.core.arcane.ArcaneBladeRules;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The blades of the arcane, by the item tags that make them so ({@link ArcaneBladeRules}).
 * <ul>
 *   <li>{@link #MARKED}, corruption:arcane_weapons, the attack tag: a blow struck with the blade by hand carries the
 *       arcane's mark, so it reaches what only the arcane reaches (the Goetic demons ask {@link ArcaneDamage#isSpell}
 *       or {@link #isArcaneBlow}), and the corruption's creatures.</li>
 *   <li>{@link #CURSED}, corruption:cursed_weapons: its blows lay the corruption's curse on what they strike, and the
 *       corruption's lords turn them aside.</li>
 * </ul>
 * An add-on's weapon joins either by data alone.
 */
public final class ArcaneWeapons {
    public static final TagKey<Item> MARKED = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("corruption", "arcane_weapons"));
    public static final TagKey<Item> CURSED = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("corruption", "cursed_weapons"));

    /** The weapon a hurt was struck with by hand, or null for one that was not (a shot, a spell, a fall). */
    @Nullable public static ItemStack weapon(DamageSource source) {
        if (source.getEntity() == null || source.getDirectEntity() != source.getEntity()) return null;
        ItemStack weapon = source.getWeaponItem();
        return weapon == null || weapon.isEmpty() ? null : weapon;
    }

    /** Whether a hurt is a blow struck by hand with a blade that bears the arcane's mark. */
    public static boolean isArcaneBlow(DamageSource source) {
        ItemStack weapon = weapon(source);
        return ArcaneBladeRules.arcane(weapon != null && weapon.is(MARKED), weapon != null);
    }

    /** Whether a hurt is a blow struck by hand with a blade that carries the corruption's curse. */
    public static boolean isCursedBlow(DamageSource source) {
        ItemStack weapon = weapon(source);
        return weapon != null && weapon.is(CURSED);
    }

    private ArcaneWeapons() {}
}
