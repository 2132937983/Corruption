package com.miophas.corruption.common.client;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyUnit;
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
@EventBusSubscriber(modid = Corruption.MOD_ID, value = Dist.CLIENT)
public final class CarriedEnergyTooltip {
    @SubscribeEvent public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        CorruptionEnergyCarrier carrier = CorruptionEnergyCarrier.of(stack);
        if (carrier == null) return;
        long carried = CorruptionEnergyCarrier.carried(stack);
        List<Component> lines = event.getToolTip();
        int under = Math.min(1, lines.size());
        CorruptionEnergyUnit unit = CorruptionEnergyApi.unit(carrier.capacity());
        lines.add(under, Component.translatable("tooltip.corruption.carried_energy", CorruptionEnergyApi.text(carried, unit), CorruptionEnergyApi.text(carrier.capacity(), unit), unit.symbol())
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        if (carried > 0) lines.add(under + 1, Component.translatable("tooltip.corruption.pour_energy").withStyle(ChatFormatting.GRAY));
    }

    private CarriedEnergyTooltip() {}
}
