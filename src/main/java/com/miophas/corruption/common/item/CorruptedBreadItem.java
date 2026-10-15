package com.miophas.corruption.common.item;

import com.miophas.corruption.common.entity.PlayerCorruption;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Corrupted Bread: bread baked through with the corruption in the brass cauldron. It is eaten as bread is, but it
 * feeds the corruption, not the eater: it restores no hunger and no saturation, and leaves whoever eats it one
 * point more corrupted. Since it does nothing for hunger it can be eaten however full one is.
 */
public final class CorruptedBreadItem extends Item {
    /** How much more corrupted a loaf leaves its eater. */
    public static final int CORRUPTION = 1;
    /** Food that feeds nothing: no hunger, no saturation, and eaten whether hungry or not. */
    public static final FoodProperties FOOD = new FoodProperties.Builder().nutrition(0).saturationModifier(0).alwaysEdible().build();

    public CorruptedBreadItem(Item.Properties properties) { super(properties.food(FOOD)); }

    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity eater) {
        ItemStack left = super.finishUsingItem(stack, level, eater);
        if (!level.isClientSide && eater instanceof Player player) PlayerCorruption.feed(player, CORRUPTION);
        return left;
    }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("item.corruption.corrupted_bread.desc", CORRUPTION).withStyle(ChatFormatting.DARK_PURPLE));
    }
}
