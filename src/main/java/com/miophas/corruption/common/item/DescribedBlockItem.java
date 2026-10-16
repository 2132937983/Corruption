package com.miophas.corruption.common.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/**
 * A block's item that says under its name what sets the block apart, in the line its language gives at
 * "block.corruption.&lt;id&gt;.desc": the dormant corruption, which looks just like the corruption that spreads.
 */
public final class DescribedBlockItem extends BlockItem {
    public DescribedBlockItem(Block block, Item.Properties properties) { super(block, properties); }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
