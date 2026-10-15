package com.miophas.corruption.common.energy;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyBuffer;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import com.miophas.corruption.core.energy.EnergyTool;
import net.minecraft.world.item.ItemStack;

/**
 * A machine's giving out of Corruption Energy through one slot: the tool lying there is charged out of the
 * machine's own store, a step at a time — {@link EnergyTool#step} much each call, so whoever fills it has a
 * moment to show for every piece of energy that passes, and is done when the store is spent or the tool full.
 * Nothing is spent on an item that is no tool: a battery, which pours out what it carries rather than being
 * filled, is not charged here. The arcane charger keeps one; it is the mirror of {@link EnergyIntake}.
 */
public final class EnergyCharging {
    /** Whether it would take any charge at all: it is a tool, and is not already full. */
    public static boolean chargeable(ItemStack stack) {
        EnergyTool tool = CorruptionEnergyTool.of(stack);
        return tool.takes(CorruptionEnergyTool.held(stack), Long.MAX_VALUE);
    }

    /**
     * Puts one step of the store into the tool, as much as both of them agree to. Answers how much went in,
     * which is what the caller tells its clients to send a spark after.
     */
    public static long step(ItemStack item, CorruptionEnergyBuffer energy) {
        EnergyTool tool = CorruptionEnergyTool.of(item);
        long want = tool.taken(CorruptionEnergyTool.held(item), tool.step());
        return want <= 0 ? 0 : CorruptionEnergyApi.move(energy, CorruptionEnergyTool.storage(item), want);
    }

    private EnergyCharging() {}
}
