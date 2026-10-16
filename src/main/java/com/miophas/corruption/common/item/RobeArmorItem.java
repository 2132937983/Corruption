package com.miophas.corruption.common.item;

import com.miophas.corruption.api.corruption.CorruptionWardItem;
import com.miophas.corruption.common.registry.ModArmorMaterials;
import com.miophas.corruption.core.corruption.CorruptionWard;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * A piece of the robe: little armour against blows, but woven with pure leaves, so that it holds back the
 * corruption's growth in its wearer ({@link CorruptionWardItem}). Each piece holds back its own share of every point
 * the wearer would gain, the robe itself the most; worn whole, the four hold back four fifths of it. It does nothing
 * for corruption already there.
 *
 * <p>It is the attire of the Great Sage's path, which is yet to open, and its tooltip says so. The corrupted robes
 * (CorruptedRobeArmorItem) are another thing: the corruption's own, which veil their wearer from its creatures.
 */
public final class RobeArmorItem extends ArmorItem implements CorruptionWardItem {
    /** How much of the corruption's growth each piece holds back. */
    public static double share(ArmorItem.Type type) {
        return switch (type) {
            case HELMET, BOOTS -> .15;
            case CHESTPLATE -> .30;
            case LEGGINGS -> .20;
            case BODY -> 0;
        };
    }

    /** Durability as leather's is reckoned, a little longer. */
    private static final int DURABILITY = 8;

    private final CorruptionWard ward;

    public RobeArmorItem(ArmorItem.Type type) {
        super(ModArmorMaterials.ROBE, type, new Item.Properties().durability(type.getDurability(DURABILITY)));
        ward = new CorruptionWard(share(type));
    }

    @Override public CorruptionWard ward() { return ward; }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.corruption.ward", percent(ward.share())).withStyle(ChatFormatting.AQUA));
        double whole = 0;
        for (ArmorItem.Type piece : new ArmorItem.Type[]{ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS})
            whole += share(piece);
        lines.add(Component.translatable("tooltip.corruption.ward_whole", percent(whole)).withStyle(ChatFormatting.DARK_AQUA));
        lines.add(Component.translatable("tooltip.corruption.robe_sage").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
    }

    private static String percent(double share) { return Math.round(share * 100) + "%"; }
}
