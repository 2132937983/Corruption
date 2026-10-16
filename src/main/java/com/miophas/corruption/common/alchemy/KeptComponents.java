package com.miophas.corruption.common.alchemy;

import net.minecraft.world.item.ItemStack;

/**
 * What a recipe that remakes one thing into another keeps of it: its own data, as set on it since it was made (its
 * enchantments, its wear, its name and the like), carried over onto what it becomes. So a sword reforged at the bench,
 * or a robe dyed a grade deeper, keeps what had been done to it. A recipe asks for this with "keep", naming the slot (or
 * pillar) that holds the thing remade.
 */
public final class KeptComponents {
    /** The result, with what had been set on the thing remade carried onto it. */
    public static ItemStack onto(ItemStack result, ItemStack from) {
        if (!from.isEmpty()) result.applyComponents(from.getComponentsPatch());
        return result;
    }

    private KeptComponents() {}
}
