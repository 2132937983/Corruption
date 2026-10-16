package com.miophas.corruption.common.item;

import com.miophas.corruption.api.corruption.CorruptedRobeItem;
import com.miophas.corruption.core.corruption.CorruptedRobeRules;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * A piece of a corrupted robe, of one of the three grades ({@link CorruptedRobeRules.Tier}). Worn as a set of four, the
 * robe does what its grade does (see CorruptedRobes and CorruptionVeilApi); under its name, each piece says what that is.
 */
public final class CorruptedRobeArmorItem extends ArmorItem implements CorruptedRobeItem {
    private final CorruptedRobeRules.Tier tier;

    public CorruptedRobeArmorItem(CorruptedRobeRules.Tier tier, Holder<ArmorMaterial> material, ArmorItem.Type type, int durability) {
        super(material, type, new Item.Properties().durability(type.getDurability(durability)));
        this.tier = tier;
    }

    @Override public CorruptedRobeRules.Tier robeTier() { return tier; }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.corruption.robe_set").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("tooltip.corruption.robe_veil").withStyle(ChatFormatting.DARK_PURPLE));
        lines.add(Component.translatable("tooltip.corruption.robe_hold", tier.threshold()).withStyle(ChatFormatting.DARK_PURPLE));
        if (tier.corruptionDamageTaken() < 1)
            lines.add(Component.translatable("tooltip.corruption.robe_damage", Math.round((1 - tier.corruptionDamageTaken()) * 100) + "%").withStyle(ChatFormatting.DARK_PURPLE));
        if (tier.hiddenFromBosses()) lines.add(Component.translatable("tooltip.corruption.robe_bosses").withStyle(ChatFormatting.LIGHT_PURPLE));
        lines.add(Component.translatable("tooltip.corruption.robe_lowest").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
