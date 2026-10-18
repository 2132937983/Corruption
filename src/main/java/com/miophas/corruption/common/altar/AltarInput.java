package com.miophas.corruption.common.altar;

import com.miophas.corruption.core.altar.AltarForm;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * What is at an altar, as its recipes see it: the item on each of its pillars, slot 1 first (six of them, or
 * eight, by the altar's shape); the kind of creature bound on its offering; its ground; the corruption of each
 * player at it; and what stands round it, for rites that ask for a structure there.
 *
 * @param creature null when nothing is bound there
 */
public record AltarInput(List<ItemStack> pillars, EntityType<?> creature, AltarGround ground, int[] corruption, AltarSpace space) implements RecipeInput {
    /** Bare ground and nobody there. */
    public AltarInput(List<ItemStack> pillars, EntityType<?> creature) { this(pillars, creature, AltarGround.CLEAR, new int[0]); }
    /** Nothing standing round it. */
    public AltarInput(List<ItemStack> pillars, EntityType<?> creature, AltarGround ground, int[] corruption) { this(pillars, creature, ground, corruption, AltarSpace.EMPTY); }

    /** The shape of the altar, by how many pillars it has; null for any other count. */
    public AltarForm form() { return AltarForm.withSlots(pillars.size()); }

    @Override public ItemStack getItem(int index) { return pillars.get(index); }
    @Override public int size() { return pillars.size(); }
    /** Nothing on the pillars and nothing bound: no rite to look for. */
    @Override public boolean isEmpty() { return creature == null && RecipeInput.super.isEmpty(); }
}
