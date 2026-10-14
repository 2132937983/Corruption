package com.miophas.corruption.common.item;

import com.miophas.corruption.api.arcane.ArcaneApi;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Parchment, made from leather. Blank, it can have one string of the arcane copied onto it at the arcane
 * lectern; it then bears that string for good, as its corruption:inscription component, and is called
 * Inscribed Parchment. What it bears, and whether its reader understands it, is shown under its name.
 */
public final class ParchmentItem extends Item {
    public ParchmentItem(Properties properties) { super(properties); }

    @Override public Component getName(ItemStack stack) {
        return ArcaneApi.inscription(stack) == null ? super.getName(stack) : Component.translatable(getDescriptionId() + ".inscribed");
    }
}
