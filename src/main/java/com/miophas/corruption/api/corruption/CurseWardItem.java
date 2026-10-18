package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CurseWard;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Something worn that turns aside the hurt of the corruption's curse ({@link CurseWard}), as the echo alloy's armour
 * does: the curse's own pulses ({@link CorruptionCurseApi#isCurse}), and nothing else of the corruption's. The mod lets
 * everything a wearer has in their armour slots count together against every pulse; an add-on's armour need only
 * implement this.
 */
public interface CurseWardItem {
    /** How much of the curse's hurt this piece turns aside. */
    CurseWard curseWard();

    /** How much of it everything in a wearer's armour slots turns aside together. */
    static double worn(LivingEntity wearer) {
        List<CurseWard> wards = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            ItemStack piece = wearer.getItemBySlot(slot);
            if (piece.getItem() instanceof CurseWardItem item) wards.add(item.curseWard());
        }
        return CurseWard.total(wards);
    }

    /** How much of a pulse of the curse gets through to this wearer. */
    static float taken(LivingEntity wearer, float amount) { return CurseWard.taken(amount, worn(wearer)); }
}
