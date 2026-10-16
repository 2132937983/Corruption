package com.miophas.corruption.common.item;

import com.miophas.corruption.api.arcane.ArcaneWeapons;
import com.miophas.corruption.core.arcane.ArcaneBladeRules;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

/**
 * One of the corruption's blades ({@link ArcaneBladeRules}): a sword, as heavy as its rules say, whose powers come of
 * the item tags it is in (the arcane's mark, the corruption's curse, see {@link ArcaneWeapons} and CorruptionBlades);
 * under its name it says which it has.
 */
public final class CorruptionBladeItem extends SwordItem {
    /** A bare hand strikes this hard already; a sword's own damage is counted on top of it. */
    private static final int HAND = 1;

    /** A sword of this tier, as long-lasting as given, mended with this, striking this hard all told at a sword's pace. */
    public CorruptionBladeItem(int durability, Supplier<Ingredient> repair, int damage) {
        this(new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, durability, 9, 0, ArcaneBladeRules.ENCHANTABILITY, repair), damage);
    }

    private CorruptionBladeItem(Tier tier, int damage) {
        super(tier, new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(tier, damage - HAND,
                (float) (ArcaneBladeRules.SWORD_SPEED - 4))));
    }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        if (stack.is(ArcaneWeapons.MARKED)) lines.add(Component.translatable("tooltip.corruption.blade_marked").withStyle(ChatFormatting.LIGHT_PURPLE));
        if (stack.is(ArcaneWeapons.CURSED)) lines.add(Component.translatable("tooltip.corruption.blade_cursed").withStyle(ChatFormatting.DARK_RED));
    }
}
