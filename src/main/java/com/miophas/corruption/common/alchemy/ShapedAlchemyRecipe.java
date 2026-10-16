package com.miophas.corruption.common.alchemy;

import com.miophas.corruption.common.registry.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

/**
 * Ingredients with amounts in named slots. A slot the recipe does not name must be empty.
 * There is no mirroring or shifting as in a crafting grid: the five points are distinct places.
 * A recipe that remakes one thing into another may name its slot as "keep", and the result keeps what had been set on
 * that thing (its enchantments, its wear, its name: see {@link KeptComponents}).
 */
public final class ShapedAlchemyRecipe implements AlchemyRecipe {
    /** JSON object keys are text, so slots are written "0" to "4". */
    private static final Codec<Integer> SLOT = Codec.STRING.comapFlatMap(text -> {
        try {
            int slot = Integer.parseInt(text);
            return slot >= 0 && slot < SLOTS ? DataResult.success(slot) : DataResult.error(() -> "Slot must be 0 to " + (SLOTS - 1) + ": " + text);
        } catch (NumberFormatException notANumber) {
            return DataResult.error(() -> "Not a slot number: " + text);
        }
    }, String::valueOf);
    private static final MapCodec<ShapedAlchemyRecipe> FIELDS = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(SLOT, SizedIngredient.FLAT_CODEC)
                    .validate(slots -> slots.isEmpty() ? DataResult.error(() -> "A shaped alchemy recipe needs at least one slot") : DataResult.success(slots))
                    .fieldOf("slots").forGetter(ShapedAlchemyRecipe::slots),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter((ShapedAlchemyRecipe recipe) -> recipe.result),
            SLOT.optionalFieldOf("keep").forGetter(ShapedAlchemyRecipe::keep))
            .apply(instance, (slots, result, keep) -> new ShapedAlchemyRecipe(slots, result, keep)));
    public static final MapCodec<ShapedAlchemyRecipe> CODEC = FIELDS.flatXmap(ShapedAlchemyRecipe::checked, ShapedAlchemyRecipe::checked);
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedAlchemyRecipe> STREAM_CODEC = StreamCodec.of((buffer, recipe) -> {
        buffer.writeVarInt(recipe.slots.size());
        recipe.slots.forEach((slot, ingredient) -> {
            buffer.writeVarInt(slot);
            SizedIngredient.STREAM_CODEC.encode(buffer, ingredient);
        });
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        buffer.writeVarInt(recipe.keep.orElse(-1));
    }, buffer -> {
        Map<Integer, SizedIngredient> slots = new TreeMap<>();
        for (int count = buffer.readVarInt(); count > 0; count--) slots.put(buffer.readVarInt(), SizedIngredient.STREAM_CODEC.decode(buffer));
        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
        int keep = buffer.readVarInt();
        return new ShapedAlchemyRecipe(slots, result, keep < 0 ? Optional.empty() : Optional.of(keep));
    });

    private final Map<Integer, SizedIngredient> slots;
    private final ItemStack result;
    private final Optional<Integer> keep;

    public ShapedAlchemyRecipe(Map<Integer, SizedIngredient> slots, ItemStack result) { this(slots, result, Optional.empty()); }

    public ShapedAlchemyRecipe(Map<Integer, SizedIngredient> slots, ItemStack result, Optional<Integer> keep) {
        this.slots = java.util.Collections.unmodifiableMap(new TreeMap<>(slots));
        for (int slot : this.slots.keySet()) if (slot < 0 || slot >= SLOTS) throw new IllegalArgumentException("No such bench slot: " + slot);
        this.result = result;
        this.keep = keep;
    }

    /** The slot whose thing the result keeps what had been set on, if any. */
    public Optional<Integer> keep() { return keep; }

    /** A recipe keeps what lies in a slot it asks for, or nothing. */
    private static DataResult<ShapedAlchemyRecipe> checked(ShapedAlchemyRecipe recipe) {
        return recipe.keep.isEmpty() || recipe.slots.containsKey(recipe.keep.get()) ? DataResult.success(recipe)
                : DataResult.error(() -> "A recipe keeps what lies in a slot it asks for, not slot " + recipe.keep.get());
    }

    /** In slot order. */
    public Map<Integer, SizedIngredient> slots() { return slots; }

    @Override public int[] consumption(AlchemyInput input) {
        int[] taken = new int[input.size()];
        for (int slot = 0; slot < input.size(); slot++) {
            SizedIngredient wanted = slots.get(slot);
            ItemStack placed = input.getItem(slot);
            if (wanted == null ? !placed.isEmpty() : !wanted.test(placed)) return null;
            if (wanted != null) taken[slot] = wanted.count();
        }
        return taken;
    }

    @Override public ItemStack assemble(AlchemyInput input, HolderLookup.Provider registries) {
        return keep.map(slot -> KeptComponents.onto(result.copy(), input.getItem(slot))).orElseGet(result::copy);
    }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }
    /** One entry per slot, empty where the recipe wants nothing. */
    @Override public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> bySlot = NonNullList.withSize(SLOTS, Ingredient.EMPTY);
        slots.forEach((slot, ingredient) -> bySlot.set(slot, ingredient.ingredient()));
        return bySlot;
    }
    @Override public RecipeSerializer<?> getSerializer() { return ModRecipes.ALCHEMY_SHAPED.get(); }

    public static final class Serializer implements RecipeSerializer<ShapedAlchemyRecipe> {
        @Override public MapCodec<ShapedAlchemyRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, ShapedAlchemyRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
