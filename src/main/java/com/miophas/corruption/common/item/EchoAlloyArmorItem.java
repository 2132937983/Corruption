package com.miophas.corruption.common.item;

import com.miophas.corruption.api.corruption.CurseWardItem;
import com.miophas.corruption.common.registry.ModArmorMaterials;
import com.miophas.corruption.core.alloy.EchoAlloyRules;
import com.miophas.corruption.core.corruption.CurseWard;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * A piece of the corruption echo alloy's armour (EchoAlloyRules): netherite's guard against blows, and its own share of
 * the corruption's curse turned aside ({@link CurseWardItem}; the whole set turns aside all of it). It does nothing else
 * against the corruption: the blows, spells and skills of its lords, elites and remnants it guards against only as
 * armour does. It does not burn.
 */
public final class EchoAlloyArmorItem extends ArmorItem implements CurseWardItem {
    private final CurseWard ward;

    public EchoAlloyArmorItem(Type type) {
        super(ModArmorMaterials.ECHO_ALLOY, type, new Item.Properties().fireResistant().durability(type.getDurability(EchoAlloyRules.ARMOUR_DURABILITY)));
        ward = new CurseWard(EchoAlloyRules.CURSE_SHARE[EchoAlloyRules.piece(type.getName())]);
    }

    @Override public CurseWard curseWard() { return ward; }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.corruption.echo_armor", Math.round(ward.share() * 100)).withStyle(ChatFormatting.DARK_AQUA));
        lines.add(Component.translatable("tooltip.corruption.echo_armor_set").withStyle(ChatFormatting.GRAY));
    }
}
