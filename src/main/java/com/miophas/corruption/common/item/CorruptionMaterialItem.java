package com.miophas.corruption.common.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * One of the corruption's raw materials: an ingredient and nothing else, made and used only in the mod's own
 * machines and rites (see gamemodels/corruption_materials/write_materials.py for which). Under its name it says
 * what it is, in the line its language gives at "item.corruption.&lt;id&gt;.desc".
 */
public final class CorruptionMaterialItem extends Item {
    public CorruptionMaterialItem(Item.Properties properties) { super(properties); }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
