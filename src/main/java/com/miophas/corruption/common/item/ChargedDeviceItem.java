package com.miophas.corruption.common.item;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import com.miophas.corruption.core.energy.EnergyDeviceRules;
import com.miophas.corruption.core.energy.EnergyTool;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * A device of the corruption's that runs on a charge of Corruption Energy ({@link CorruptionEnergyTool}): filled on the
 * arcane charger or poured into by hand, and drawn on only by its own use (EnergyDeviceRules). A bar under it shows its
 * charge while it is not full; changing charge in the hand does not make the hand swing it up again. Under its name it
 * says what it does, in the line its language gives at "tooltip.corruption.&lt;id&gt;".
 */
public abstract class ChargedDeviceItem extends Item implements CorruptionEnergyTool {
    private static final int BAR_COLOUR = 0xC6728C;
    private final EnergyTool tool;

    protected ChargedDeviceItem(Properties properties, EnergyTool tool) {
        super(properties.stacksTo(1));
        this.tool = tool;
    }

    @Override public EnergyTool tool() { return tool; }

    /** What it holds now, in CE. */
    public long charge(ItemStack stack) { return CorruptionEnergyTool.held(stack); }

    /** Pays this much of its charge, if it holds it: whether it did. */
    public boolean pay(ItemStack stack, long cost) {
        long held = charge(stack);
        if (!EnergyDeviceRules.affords(held, cost)) return false;
        stack.set(CorruptionEnergyCarrier.ENERGY.get(), EnergyDeviceRules.paid(held, cost));
        return true;
    }

    @Override public boolean isBarVisible(ItemStack stack) { return charge(stack) < tool.capacity(); }
    @Override public int getBarWidth(ItemStack stack) { return Math.round(13 * CorruptionEnergyApi.fraction(charge(stack), tool.capacity())); }
    @Override public int getBarColor(ItemStack stack) { return BAR_COLOUR; }

    /** Its charge changing in the hand is not a new thing put there. */
    @Override public boolean shouldCauseReequipAnimation(ItemStack before, ItemStack after, boolean slotChanged) {
        return slotChanged || !ItemStack.isSameItem(before, after);
    }

    @Override public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.corruption." + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this).getPath())
                .withStyle(ChatFormatting.GRAY));
    }
}
