package com.miophas.corruption.common.altar;

import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.common.registry.ModRecipes;
import com.miophas.corruption.core.altar.AltarForm;
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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
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
 * A shaped rite that remakes one thing into another may name that thing's pillar as "keep" ("1" to "6"), and the item
 * it brings forth keeps what had been set on it (its enchantments, its wear, its name: see KeptComponents).
 *
 * A rite may ask for a structure to stand round the altar, by its id in the registry corruption:altar_structure
 * ({@link AltarStructure}), turned the way the altar faces:
 * <pre> "structure": "corruption:echo_sanctum"</pre>
 *
 * A rite is made for an altar of one shape ({@link AltarForm}), "form": "hexagram", of six pillars, or
 * "octagram", of eight, numbered the same way. Left out, it is the shape of the structure the rite asks for, or
 * else the hexagram. A rite is performed only at an altar of its shape, and asks for nothing on pillars it has not.
 *
 * @param pillars shaped: one for each of its altar's pillars, slot 1 first, {@link Ingredient#EMPTY} where a pillar
 *                must hold nothing; shapeless: one up to as many as its altar has pillars
 * @param blocks what must stand on the ground, and where; everywhere else on it, nothing
 * @param corruption how corrupted a player at the altar must be, or 0 for no player at all
 * @param keep shaped only: the pillar (from 1) whose item the result keeps what had been set on, if any
 * @param structure what must stand round the altar, if anything
 * @param form the shape of the altar it is performed at
 */
public record AltarRecipe(boolean shaped, List<Ingredient> pillars, Optional<EntityType<?>> offering, List<GroundBlock> blocks, int corruption,
                          AltarResult result, int time, Optional<Integer> keep, Optional<Holder<AltarStructure>> structure, AltarForm form) implements Recipe<AltarInput> {
    /** A rite that keeps nothing of what it remakes. */
    public AltarRecipe(boolean shaped, List<Ingredient> pillars, Optional<EntityType<?>> offering, List<GroundBlock> blocks, int corruption,
                       AltarResult result, int time) {
        this(shaped, pillars, offering, blocks, corruption, result, time, Optional.empty());
    }
    /** A rite at a hexagram altar that asks for no structure round it. */
    public AltarRecipe(boolean shaped, List<Ingredient> pillars, Optional<EntityType<?>> offering, List<GroundBlock> blocks, int corruption,
                       AltarResult result, int time, Optional<Integer> keep) {
        this(shaped, pillars, offering, blocks, corruption, result, time, keep, Optional.empty());
    }
    /** A rite at an altar of the shape of the structure it asks for, or a hexagram if it asks for none. */
    public AltarRecipe(boolean shaped, List<Ingredient> pillars, Optional<EntityType<?>> offering, List<GroundBlock> blocks, int corruption,
                       AltarResult result, int time, Optional<Integer> keep, Optional<Holder<AltarStructure>> structure) {
        this(shaped, pillars, offering, blocks, corruption, result, time, keep, structure, shapeOf(Optional.empty(), structure));
    }

    /** The shape a rite is for: as it says, or as the structure it asks for is built, or else a hexagram. */
    private static AltarForm shapeOf(Optional<AltarForm> said, Optional<Holder<AltarStructure>> structure) {
        return said.orElseGet(() -> structure.map(asked -> asked.value().form()).orElse(AltarForm.HEXAGRAM));
    }

    /** A slot, as recipes write it: "1" to "8"; how many the rite's altar has is checked once its shape is known. */
    private static final Codec<Integer> SLOT = Codec.STRING.comapFlatMap(text -> {
        try {
            int slot = Integer.parseInt(text);
            return slot >= 1 && slot <= AltarForm.MOST_SLOTS ? DataResult.success(slot)
                    : DataResult.error(() -> "An altar has pillars 1 to " + AltarForm.MOST_SLOTS + " at most, not " + text);
        } catch (NumberFormatException notANumber) { return DataResult.error(() -> "Not a pillar: " + text); }
    }, String::valueOf);
    /** Shaped pillars, as far as the last one asked for; filled out to the altar's own once its shape is known. */
    private static final Codec<List<Ingredient>> SHAPED_PILLARS = Codec.unboundedMap(SLOT, Ingredient.CODEC_NONEMPTY)
            .validate(map -> map.isEmpty() ? DataResult.error(() -> "A rite asks for something on at least one pillar") : DataResult.success(map))
            .xmap(map -> {
                List<Ingredient> asked = new ArrayList<>();
                int last = map.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
                for (int slot = 1; slot <= last; slot++) asked.add(map.getOrDefault(slot, Ingredient.EMPTY));
                return List.copyOf(asked);
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
    private static final Codec<Holder<AltarStructure>> STRUCTURE = RegistryFixedCodec.create(AltarStructure.REGISTRY);

    /** Shaped pillars filled out with nothing to as many as an altar of this shape has; more, if more were asked for, for the check to refuse. */
    private static List<Ingredient> filledOut(List<Ingredient> asked, AltarForm form) {
        if (asked.size() >= form.slots()) return asked;
        List<Ingredient> all = new ArrayList<>(asked);
        while (all.size() < form.slots()) all.add(Ingredient.EMPTY);
        return List.copyOf(all);
    }

    private static final MapCodec<AltarRecipe> SHAPED_FIELDS = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SHAPED_PILLARS.fieldOf("pillars").forGetter(AltarRecipe::pillars),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("offering").forGetter(AltarRecipe::offering),
            BLOCKS.optionalFieldOf("blocks", List.of()).forGetter(AltarRecipe::blocks),
            CORRUPTION.optionalFieldOf("corruption", 0).forGetter(AltarRecipe::corruption),
            AltarResult.CODEC.fieldOf("result").forGetter(AltarRecipe::result),
            TIME.optionalFieldOf("time", RitualRules.DEFAULT_TICKS).forGetter(AltarRecipe::time),
            SLOT.optionalFieldOf("keep").forGetter(AltarRecipe::keep),
            STRUCTURE.optionalFieldOf("structure").forGetter(AltarRecipe::structure),
            AltarStar.FORM_CODEC.optionalFieldOf("form").forGetter(recipe -> Optional.of(recipe.form())))
            .apply(instance, (pillars, offering, blocks, corruption, result, time, keep, structure, form) -> {
                AltarForm shape = shapeOf(form, structure);
                return new AltarRecipe(true, filledOut(pillars, shape), offering, blocks, corruption, result, time, keep, structure, shape);
            }));
    public static final MapCodec<AltarRecipe> SHAPED_CODEC = SHAPED_FIELDS.flatXmap(AltarRecipe::checked, AltarRecipe::checked);

    /**
     * A rite asks for pillars its altar has, and for blocks on its ground, not where its own offering or pillars
     * stand; a structure it asks for is built round an altar of its shape; and it keeps what lies on a pillar it
     * asks for, and only for an item it brings forth.
     */
    private static DataResult<AltarRecipe> checked(AltarRecipe recipe) {
        AltarForm form = recipe.form();
        String shape = form.name().toLowerCase(java.util.Locale.ROOT);
        if (recipe.shaped() ? recipe.pillars().size() != form.slots() : recipe.pillars().size() > form.slots())
            return DataResult.error(() -> "A " + shape + " altar has " + form.slots() + " pillars; this rite asks for " + recipe.pillars().size());
        for (GroundBlock block : recipe.blocks())
            if (form.isAltar(block.east(), block.north()))
                return DataResult.error(() -> "The " + shape + " altar itself stands at " + block.east() + ", " + block.north());
        if (recipe.structure().isPresent() && recipe.structure().get().value().form() != form)
            return DataResult.error(() -> "The structure asked for is built round an altar of another shape than this rite's " + shape);
        if (recipe.keep().isEmpty()) return DataResult.success(recipe);
        int keep = recipe.keep().get();
        return keep <= recipe.pillars().size() && !recipe.pillars().get(keep - 1).isEmpty() && recipe.result().item().isPresent()
                ? DataResult.success(recipe) : DataResult.error(() -> "A rite keeps what lies on a pillar it asks for, and only for an item it brings forth");
    }
    private static final MapCodec<AltarRecipe> SHAPELESS_FIELDS = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf(1, AltarForm.MOST_SLOTS).fieldOf("pillars").forGetter(AltarRecipe::pillars),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().optionalFieldOf("offering").forGetter(AltarRecipe::offering),
            BLOCKS.optionalFieldOf("blocks", List.of()).forGetter(AltarRecipe::blocks),
            CORRUPTION.optionalFieldOf("corruption", 0).forGetter(AltarRecipe::corruption),
            AltarResult.CODEC.fieldOf("result").forGetter(AltarRecipe::result),
            TIME.optionalFieldOf("time", RitualRules.DEFAULT_TICKS).forGetter(AltarRecipe::time),
            STRUCTURE.optionalFieldOf("structure").forGetter(AltarRecipe::structure),
            AltarStar.FORM_CODEC.optionalFieldOf("form").forGetter(recipe -> Optional.of(recipe.form())))
            .apply(instance, (pillars, offering, blocks, corruption, result, time, structure, form) ->
                    new AltarRecipe(false, pillars, offering, blocks, corruption, result, time, Optional.empty(), structure, shapeOf(form, structure))));
    public static final MapCodec<AltarRecipe> SHAPELESS_CODEC = SHAPELESS_FIELDS.flatXmap(AltarRecipe::checked, AltarRecipe::checked);

    private static StreamCodec<RegistryFriendlyByteBuf, AltarRecipe> streamCodecFor(boolean shaped) {
        StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> pillars = Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(AltarForm.MOST_SLOTS));
        StreamCodec<RegistryFriendlyByteBuf, Optional<EntityType<?>>> offering = ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ENTITY_TYPE));
        StreamCodec<RegistryFriendlyByteBuf, List<GroundBlock>> blocks = GroundBlock.STREAM_CODEC.apply(ByteBufCodecs.list());
        StreamCodec<RegistryFriendlyByteBuf, Optional<Holder<AltarStructure>>> structure = ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(AltarStructure.REGISTRY));
        return StreamCodec.of((buffer, recipe) -> {
            pillars.encode(buffer, recipe.pillars());
            offering.encode(buffer, recipe.offering());
            blocks.encode(buffer, recipe.blocks());
            buffer.writeVarInt(recipe.corruption());
            AltarResult.STREAM_CODEC.encode(buffer, recipe.result());
            buffer.writeVarInt(recipe.time());
            buffer.writeVarInt(recipe.keep().orElse(0));
            structure.encode(buffer, recipe.structure());
            AltarStar.FORM_STREAM_CODEC.encode(buffer, recipe.form());
        }, buffer -> {
            List<Ingredient> read = pillars.decode(buffer);
            Optional<EntityType<?>> creature = offering.decode(buffer);
            List<GroundBlock> ground = blocks.decode(buffer);
            int corruption = buffer.readVarInt();
            AltarResult result = AltarResult.STREAM_CODEC.decode(buffer);
            int time = buffer.readVarInt(), keep = buffer.readVarInt();
            Optional<Holder<AltarStructure>> standing = structure.decode(buffer);
            AltarForm form = AltarStar.FORM_STREAM_CODEC.decode(buffer);
            return new AltarRecipe(shaped, read, creature, ground, corruption, result, time, keep == 0 ? Optional.empty() : Optional.of(keep), standing, form);
        });
    }

    /** The pillars whose items the rite uses, or null if what lies on the altar does not make it, or the altar is of another shape. */
    public int[] pillarsUsed(AltarInput input) {
        if (input.form() != form) return null;
        boolean[] filled = new boolean[form.slots()];
        for (int slot = 0; slot < filled.length; slot++) filled[slot] = !input.getItem(slot).isEmpty();
        if (shaped) {
            boolean[] asked = new boolean[form.slots()];
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
        return RitualMatch.ground(form, (east, north) -> blockAt(east, north) != null, (east, north) -> blockAt(east, north).test(ground.at(east, north)),
                (east, north) -> ground.at(east, north).isAir());
    }

    /** Whether the structure asked for, if any, stands round the altar. */
    public boolean structureMet(AltarSpace space) { return structure.map(asked -> asked.value().standsIn(space)).orElse(true); }

    /**
     * The altar is of the rite's shape; the creature asked for is bound on the offering, if one is asked for; a
     * player at the altar is corrupted far enough, if that is asked; the ground is as asked; the pillars hold what
     * is asked; and the structure asked for, if any, stands round it.
     */
    @Override public boolean matches(AltarInput input, Level level) {
        String asked = offering.map(type -> EntityType.getKey(type).toString()).orElse(null);
        String present = input.creature() == null ? null : EntityType.getKey(input.creature()).toString();
        return RitualMatch.offering(asked, present) && RitualMatch.corruption(corruption, input.corruption()) && pillarsUsed(input) != null && groundMet(input.ground())
                && structureMet(input.space());
    }

    /** The item the rite brings forth, keeping what had been set on the thing it remakes, if it keeps it. */
    @Override public ItemStack assemble(AltarInput input, HolderLookup.Provider registries) {
        return result.item().map(item -> keep.map(pillar -> com.miophas.corruption.common.alchemy.KeptComponents.onto(item.copy(), input.getItem(pillar - 1)))
                .orElseGet(item::copy)).orElse(ItemStack.EMPTY);
    }
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
