package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.RemnantCombatApi;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import java.util.List;

/** Capture appearance and source combat data for both death conversion and summoned remnants. */
final class RemnantAppearance {
    static CompoundTag capture(LivingEntity victim, boolean corruptionDeath) {
        CompoundTag appearance = new CompoundTag();
        appearance.putBoolean("CorruptionDeath", corruptionDeath);
        appearance.putString("Type", BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()).toString());
        appearance.putFloat("Width", victim.getBbWidth());
        appearance.putFloat("Height", victim.getBbHeight());
        var damage = victim.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        var profile = com.miophas.corruption.api.corruption.RemnantCombatApi.capture(victim.getMaxHealth(), damage == null ? Double.NaN : damage.getValue());
        appearance.putDouble("AttackDamage", profile.attackDamage());
        var knockback = victim.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_KNOCKBACK);
        if (knockback != null) appearance.putDouble("AttackKnockback", knockback.getValue());
        CompoundTag source = victim.saveWithoutId(new CompoundTag());
        // Keep type-specific appearance (variants, age, wool, etc.) without active state or inventories.
        for (String key : List.of("UUID", "Pos", "Motion", "Rotation", "Passengers", "Leash", "Brain", "Inventory",
                "Items", "LootTable", "Attributes", "ActiveEffects", "ForgeCaps", "CustomName", "CustomNameVisible")) source.remove(key);
        appearance.put("Entity", source);
        if (victim instanceof Player player) {
            appearance.putUUID("PlayerUUID", player.getUUID());
            appearance.putString("PlayerName", player.getGameProfile().getName());
            ListTag textures = new ListTag();
            for (var property : player.getGameProfile().getProperties().get("textures")) {
                CompoundTag entry = new CompoundTag();
                entry.putString("Value", property.value());
                if (property.signature() != null) entry.putString("Signature", property.signature());
                textures.add(entry);
            }
            appearance.put("PlayerTextures", textures);
        }
        return appearance;
    }
    private RemnantAppearance() {}
}
