package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import com.miophas.corruption.api.energy.CorruptionEnergyUnit;
import com.miophas.corruption.core.energy.EnergyTool;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Under the name of every item that carries Corruption Energy: how much it carries of what it can, and, while
 * it has some, how to pour it in. Any item the corruption_energy_carrier data map names gets these lines.
 */
/**
 * Under the name of every item that carries Corruption Energy: how much it carries of what it can, and, while
 * it has some, how to pour it in. Any item the corruption_energy_carrier data map names gets these lines, and
 * so does every casting tool, which says for itself how much it holds rather than being named there.
 */
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class CarriedEnergyTooltip {
    @SubscribeEvent public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        long capacity = capacityOf(stack);
        if (capacity <= 0) return;
        long carried = CorruptionEnergyTool.of(stack).isTool() ? CorruptionEnergyTool.held(stack) : CorruptionEnergyCarrier.carried(stack);
        List<Component> lines = event.getToolTip();
        int under = Math.min(1, lines.size());
        CorruptionEnergyUnit unit = CorruptionEnergyApi.unit(capacity);
        lines.add(under, Component.translatable("tooltip.corruption.carried_energy", CorruptionEnergyApi.text(carried, unit), CorruptionEnergyApi.text(capacity, unit), unit.symbol())
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        if (carried > 0) lines.add(under + 1, Component.translatable("tooltip.corruption.pour_energy").withStyle(ChatFormatting.GRAY));
    }

    /** What the item holds at most: a tool says so for itself, a battery as the data map says. */
    private static long capacityOf(ItemStack stack) {
        EnergyTool tool = CorruptionEnergyTool.of(stack);
        if (tool.isTool()) return tool.capacity();
        CorruptionEnergyCarrier carrier = CorruptionEnergyCarrier.of(stack);
        return carrier == null ? 0 : carrier.capacity();
    }

    private CarriedEnergyTooltip() {}
}
