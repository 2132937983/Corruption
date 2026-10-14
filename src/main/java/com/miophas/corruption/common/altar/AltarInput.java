package com.miophas.corruption.common.altar;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * What is at an altar, as its recipes see it: the item on each of its six pillars, slot 1 first; the kind of
 * creature bound on its offering; its ground; and the corruption of each player at it.
 *
 * @param creature null when nothing is bound there
 */
public record AltarInput(List<ItemStack> pillars, EntityType<?> creature, AltarGround ground, int[] corruption) implements RecipeInput {
    /** Bare ground and nobody there. */
    public AltarInput(List<ItemStack> pillars, EntityType<?> creature) { this(pillars, creature, AltarGround.CLEAR, new int[0]); }

    @Override public ItemStack getItem(int index) { return pillars.get(index); }
    @Override public int size() { return pillars.size(); }
    /** Nothing on the pillars and nothing bound: no rite to look for. */
    @Override public boolean isEmpty() { return creature == null && RecipeInput.super.isEmpty(); }
}
