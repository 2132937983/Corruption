package com.miophas.corruption.common.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * What one of the corruption's lords leaves when it falls, its own and no other's (the item tag corruption:boss_relics,
 * CorruptionFactionApi.RELICS): kept for what may one day be called up with them all. It does nothing yet; it glints, it
 * does not burn, and under its name it says what it is, in the line its language gives at
 * "item.corruption.&lt;id&gt;.desc", and whose it is.
 */
public final class BossRelicItem extends Item {
    public BossRelicItem(Item.Properties properties) { super(properties); }

    @Override public boolean isFoil(ItemStack stack) { return true; }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        lines.add(Component.translatable("tooltip.corruption.boss_relic").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("tooltip.corruption.boss_relic_gathered").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
    }
}
