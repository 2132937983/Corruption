package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CorruptionWard;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Something worn that bears on how its wearer's corruption grows ({@link CorruptionWard}): the mod's robe holds it
 * back, an add-on's cursed armour might make it worse. The mod lets everything a player wears in their armour slots
 * bear on every point of corruption they gain, whatever gives it: the curse, a spell's cost, the corruption's bread.
 */
public interface CorruptionWardItem {
    /** How this piece bears on the corruption's growth. */
    CorruptionWard ward();

    /** How everything in a wearer's armour slots bears on it together. */
    static double worn(LivingEntity wearer) {
        List<CorruptionWard> wards = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            ItemStack piece = wearer.getItemBySlot(slot);
            if (piece.getItem() instanceof CorruptionWardItem item) wards.add(item.ward());
        }
        return CorruptionWard.total(wards);
    }
}
