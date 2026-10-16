package com.miophas.corruption.api.corruption;

import com.miophas.corruption.core.corruption.CorruptedRobeRules;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * A piece of a corrupted robe, of a grade ({@link CorruptedRobeRules.Tier}). Worn as a set of four, the robe veils its
 * wearer from the corruption, holds its corruption fast and turns aside the corruption's hurt, as its grade says
 * ({@link CorruptionVeilApi}). An add-on's robe implements this and is a set with the mod's own pieces.
 */
public interface CorruptedRobeItem {
    /** This piece's grade. */
    CorruptedRobeRules.Tier robeTier();

    /** The grade the robes in a wearer's four armour slots make together: none unless all four are corrupted robes; the lowest if they are. */
    static Optional<CorruptedRobeRules.Tier> worn(LivingEntity wearer) {
        List<CorruptedRobeRules.Tier> pieces = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            ItemStack piece = wearer.getItemBySlot(slot);
            pieces.add(piece.getItem() instanceof CorruptedRobeItem robe ? robe.robeTier() : null);
        }
        return CorruptedRobeRules.worn(pieces);
    }
}
