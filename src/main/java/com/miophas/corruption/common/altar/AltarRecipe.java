package com.miophas.corruption.common.altar;

import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.common.registry.ModRecipes;
import com.miophas.corruption.core.altar.AltarLayout;
import com.miophas.corruption.core.altar.RitualMatch;
import com.miophas.corruption.core.altar.RitualRules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * A rite at the altar. Its ingredients lie one to a pillar; a creature may be asked for, bound on the
 * offering; and it brings forth an item or a creature on the offering. Shaped, each pillar by its slot (slot 1
 * is the pillar four blocks the way the altar faces, the rest following clockwise) holds what is asked of it, and
 * the others hold nothing:
 * <pre>{"type": "corruption:altar_shaped",
 *  "pillars": {"1": {"item": "corruption:corruption_crystal"}, "4": {"item": "corruption:corrupted_brass"}},
 *  "result": {"id": "corruption:ancient_corruption_crystal"}}</pre>
 * Shapeless, the ingredients may lie on any pillars, with nothing left over:
 * <pre>{"type": "corruption:altar_shapeless",
 *  "pillars": [{"item": "corruption:corruption_dust"}, {"item": "corruption:corruption_dust"}],
 *  "offering": "minecraft:pig", "time": 80,
 *  "result": {"entity": "minecraft:zombified_piglin"}}</pre>
 * "offering" may be left out, for no creature; "time", the ticks the rite gathers for, too.
 *
 * The altar's ground, the square four blocks every way round the offering on its level, must be clear but for
 * the altar itself, unless a rite asks for blocks on it ({@link GroundBlock}: places given as though the altar
 * faced north, so that they turn with it); and a rite may ask for a player
 * at the altar corrupted at least so far (0 to 20; 0, as when it is left out, asks for nobody):
 * <pre> "blocks": [{"at": [1, 0], "block": "#minecraft:candles"}, {"at": [-1, 0], "block": "#minecraft:candles"}],
 *  "corruption": 10</pre>
 *
 * @param pillars shaped: six, slot 1 first, {@link Ingredient#EMPTY} where a pillar must hold nothing; shapeless: one to six
 * @param blocks what must stand on the ground, and where; everywhere else on it, nothing
 * @param corruption how corrupted a player at the altar must be, or 0 for no player at all
 */
public record AltarRecipe(boolean shaped, List<Ingredient> pillars, Optional<EntityType<?>> offering, List<GroundBlock> blocks, int corruption,
                          AltarResult result, int time) implements Recipe<AltarInput> {
    /** A slot, as recipes write it: "1" to "6". */
    private static final Codec<Integer> SLOT = Codec.STRING.comapFlatMap(text -> {
        try {
            int slot = Integer.parseInt(text);
            return slot >= 1 && slot <= AltarLayout.SLOTS ? DataResult.success(slot) : DataResult.error(() -> "An altar has pillars 1 to " + AltarLayout.SLOTS + ", not " + text);
        } catch (NumberFormatException notANumber) { return DataResult.error(() -> "Not a pillar: " + text); }
    }, String::valueOf);
    private static final Codec<List<Ingredient>> SHAPED_PILLARS = Codec.unboundedMap(SLOT, Ingredient.CODEC_NONEMPTY)
            .validate(map -> map.isEmpty() ? DataResult.error(() -> "A rite asks for something on at least one pillar") : DataResult.success(map))
            .xmap(map -> {
                List<Ingredient> six = new ArrayList<>();
                for (int slot = 1; slot <= AltarLayout.SLOTS; slot++) six.add(map.getOrDefault(slot, Ingredient.EMPTY));
                return List.copyOf(six);
            }, six -> {
                Map<Integer, Ingredient> map = new LinkedHashMap<>();
                for (int slot = 0; slot < six.size(); slot++) if (!six.get(slot).isEmpty()) map.put(slot + 1, six.get(slot));
                return map;
            });
    private static final Codec<Integer> TIME = Codec.intRange(1, Short.MAX_VALUE);
    private static final Codec<List<GroundBlock>> BLOCKS = GroundBlock.CODEC.listOf().validate(blocks -> {
        Set<List<Integer>> places = new HashSet<>();
        for (GroundBlock block : blocks)
            if (!places.add(List.of(block.east(), block.north()))) return DataResult.error(() -> "Two blocks asked for at " + block.east() + ", " + block.north());
        return DataResult.success(blocks);
    });
    private static final Codec<Integer> CORRUPTION = Codec.intRange(0, PlayerCorruptionApi.max());

    public static final MapCodec<AltarRecipe> SHAPED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SHAPED_PILLARS.fieldOf("pillars").forGetter(AltarRecipe::pillars),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("offering").forGetter(AltarRecipe::offering),
            BLOCKS.optionalFieldOf("blocks", List.of()).forGetter(AltarRecipe::blocks),
            CORRUPTION.optionalFieldOf("corruption", 0).forGetter(AltarRecipe::corruption),
            AltarResult.CODEC.fieldOf("result").forGetter(AltarRecipe::result),
            TIME.optionalFieldOf("time", RitualRules.DEFAULT_TICKS).forGetter(AltarRecipe::time))
            .apply(instance, (pillars, offering, blocks, corruption, result, time) -> new AltarRecipe(true, pillars, offering, blocks, corruption, result, time)));
    public static final MapCodec<AltarRecipe> SHAPELESS_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf(1, AltarLayout.SLOTS).fieldOf("pillars").forGetter(AltarRecipe::pillars),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("offering").forGetter(AltarRecipe::offering),
            BLOCKS.optionalFieldOf("blocks", List.of()).forGetter(AltarRecipe::blocks),
            CORRUPTION.optionalFieldOf("corruption", 0).forGetter(AltarRecipe::corruption),
            AltarResult.CODEC.fieldOf("result").forGetter(AltarRecipe::result),
            TIME.optionalFieldOf("time", RitualRules.DEFAULT_TICKS).forGetter(AltarRecipe::time))
            .apply(instance, (pillars, offering, blocks, corruption, result, time) -> new AltarRecipe(false, pillars, offering, blocks, corruption, result, time)));

    private static StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> streamCodecFor(boolean shaped) {
        return StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(AltarLayout.SLOTS)), AltarRecipe::pillars,
                ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ENTITY_TYPE)), AltarRecipe::offering,
                GroundBlock.STREAM_CODEC.apply(ByteBufCodecs.list()), AltarRecipe::blocks,
                ByteBufCodecs.VAR_INT, AltarRecipe::corruption,
                AltarResult.STREAM_CODEC, AltarRecipe::result,
                ByteBufCodecs.VAR_INT, AltarRecipe::time,
                (pillars, offering, blocks, corruption, result, time) -> new AltarRecipe(shaped, pillars, offering, blocks, corruption, result, time));
    }

    /** The pillars whose items the rite uses, or null if what lies on the altar does not make it. */
    public int[] pillarsUsed(AltarInput input) {
        boolean[] filled = new boolean[AltarLayout.SLOTS];
        for (int slot = 0; slot < filled.length; slot++) filled[slot] = !input.getItem(slot).isEmpty();
        if (shaped) {
            boolean[] asked = new boolean[AltarLayout.SLOTS];
            for (int slot = 0; slot < asked.length; slot++) asked[slot] = !pillars.get(slot).isEmpty();
            return RitualMatch.shaped(asked, filled, (ingredient, slot) -> pillars.get(ingredient).test(input.getItem(slot)));
        }
        return RitualMatch.shapeless(pillars.size(), filled, (ingredient, slot) -> pillars.get(ingredient).test(input.getItem(slot)));
    }

    /** The block asked for at a place on the ground, or null. */
    public GroundBlock blockAt(int east, int north) {
        for (GroundBlock block : blocks) if (block.east() == east && block.north() == north) return block;
        return null;
    }

    /** Whether the ground is as the rite asks: its blocks where it asks for them, and nothing anywhere else. */
    public boolean groundMet(AltarGround ground) {
        return RitualMatch.ground((east, north) -> blockAt(east, north) != null, (east, north) -> blockAt(east, north).test(ground.at(east, north)),
                (east, north) -> ground.at(east, north).isAir());
    }

    /**
     * The creature asked for is bound on the offering, if one is asked for; a player at the altar is corrupted
     * far enough, if that is asked; the ground is as asked; and the pillars hold what is asked.
     */
    @Override public boolean matches(AltarInput input, Level level) {
        String asked = offering.map(type -> EntityType.getKey(type).toString()).orElse(null);
        String present = input.creature() == null ? null : EntityType.getKey(input.creature()).toString();
        return RitualMatch.offering(asked, present) && RitualMatch.corruption(corruption, input.corruption()) && pillarsUsed(input) != null && groundMet(input.ground());
    }

    @Override public ItemStack assemble(AltarInput input, HolderLookup.Provider registries) { return result.item().map(ItemStack::copy).orElse(ItemStack.EMPTY); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result.item().orElse(ItemStack.EMPTY); }
    @Override public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (Ingredient ingredient : pillars) if (!ingredient.isEmpty()) list.add(ingredient);
        return list;
    }
    /** Not shown in the recipe book; JEI shows it instead. */
    @Override public boolean isSpecial() { return true; }
    @Override public RecipeSerializer<?> getSerializer() { return shaped ? ModRecipes.ALTAR_SHAPED.get() : ModRecipes.ALTAR_SHAPELESS.get(); }
    @Override public RecipeType<?> getType() { return ModRecipes.ALTAR.get(); }

    public static final class Serializer implements RecipeSerializer<AltarRecipe> {
        private final boolean shaped;
        private final StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> streamCodec;
        public Serializer(boolean shaped) { this.shaped = shaped; this.streamCodec = streamCodecFor(shaped); }
        @Override public MapCodec<AltarRecipe> codec() { return shaped ? SHAPED_CODEC : SHAPELESS_CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> streamCodec() { return streamCodec; }
    }
}
