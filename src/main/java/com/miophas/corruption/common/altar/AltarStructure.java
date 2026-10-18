package com.miophas.corruption.common.altar;

import com.miophas.corruption.common.block.AltarOfferingBlock;
import com.miophas.corruption.common.block.StaticCorruptedBlock;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.core.altar.AltarFacing;
import com.miophas.corruption.core.altar.AltarForm;
import com.miophas.corruption.core.altar.AltarLayout;
import com.miophas.corruption.core.altar.AltarStructureMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.Tags;

/**
 * A structure built round an altar, which a rite may ask to stand there (AltarRecipe's "structure"). Data packs
 * add them to the registry corruption:altar_structure, at data/&lt;namespace&gt;/corruption/altar_structure/&lt;name&gt;.json:
 * the shape of the altar it is built round ("form": "hexagram", as when it is left out, or "octagram"); a key
 * naming a block for each symbol, by id or tag, in a given state if asked ("state", the altar facing north); and a
 * map of layers ({@link AltarStructureMap}):
 * <pre>{"form": "octagram",
 *  "key": {"Q": {"block": "minecraft:quartz_block"},
 *          "v": {"block": "corruption:corruption_arcane_brick_stairs", "state": {"facing": "south", "half": "bottom"}}},
 *  "layers": [{"y": -1, "rows": ["QvQ", "Q.Q", "QQQ"]}]}</pre>
 * A rite asking for it is a rite for an altar of that shape.
 * Facing another way, the whole structure turns with the altar, and so do the states asked for. A corrupted
 * block counts as the block it was, so corruption spreading over a structure leaves it standing.
 */
public final class AltarStructure {
    public static final ResourceKey<Registry<AltarStructure>> REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("corruption", "altar_structure"));

    /** A block the structure asks for: a block or any of a tag, and, if asked, in a state, as though the altar faced north. */
    public record Part(Either<TagKey<Block>, Block> block, Map<String, String> state) {
        private static final Codec<Either<TagKey<Block>, Block>> BLOCK = Codec.either(TagKey.hashedCodec(Registries.BLOCK), BuiltInRegistries.BLOCK.byNameCodec());
        public static final Codec<Part> CODEC = RecordCodecBuilder.<Part>create(instance -> instance.group(
                BLOCK.fieldOf("block").forGetter(Part::block),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("state", Map.of()).forGetter(Part::state))
                .apply(instance, Part::new)).validate(Part::checked);

        public Part { state = Map.copyOf(state); }

        /** A single block has every property asked of it, and each may take the value asked; a tag's blocks are only known later. */
        private static DataResult<Part> checked(Part part) {
            if (part.block().left().isPresent()) return DataResult.success(part);
            Block block = part.block().right().orElseThrow();
            for (Map.Entry<String, String> asked : part.state().entrySet()) {
                Property<?> property = block.getStateDefinition().getProperty(asked.getKey());
                if (property == null) return DataResult.error(() -> BuiltInRegistries.BLOCK.getKey(block) + " has no property " + asked.getKey());
                if (property.getValue(asked.getValue()).isEmpty())
                    return DataResult.error(() -> BuiltInRegistries.BLOCK.getKey(block) + "'s " + asked.getKey() + " is never " + asked.getValue());
            }
            return DataResult.success(part);
        }

        public boolean test(BlockState seen) {
            if (!block.map(seen::is, seen::is)) return false;
            for (Map.Entry<String, String> asked : state.entrySet()) {
                Property<?> property = seen.getBlock().getStateDefinition().getProperty(asked.getKey());
                if (property == null || !property.getValue(asked.getValue()).map(seen.getValue(property)::equals).orElse(false)) return false;
            }
            return true;
        }

        /** What to place to build it, as though the altar faced north: the block, or the first of the tag, in the state asked; null if the tag is empty. */
        public BlockState placed() {
            Block one = block.map(tag -> BuiltInRegistries.BLOCK.getTag(tag).flatMap(set -> set.stream().findFirst()).map(Holder::value).orElse(null), b -> b);
            if (one == null) return null;
            BlockState state = one.defaultBlockState();
            for (Map.Entry<String, String> asked : this.state.entrySet()) {
                Property<?> property = one.getStateDefinition().getProperty(asked.getKey());
                if (property != null) state = with(state, property, asked.getValue());
            }
            return state;
        }

        private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String value) {
            return property.getValue(value).map(chosen -> state.setValue(property, chosen)).orElse(state);
        }

        /** What it asks for, by name: the block's, or the tag's, and the state asked, if any. */
        public Component name() {
            Component name = block.map(tag -> Component.translatableWithFallback(Tags.getTagTranslationKey(tag), "#" + tag.location()), Block::getName);
            if (state.isEmpty()) return name;
            return Component.translatable("corruption.altar_structure.part_state", name,
                    new TreeMap<>(state).entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(", ")));
        }
    }

    private static final Codec<Character> SYMBOL = Codec.STRING.comapFlatMap(text -> text.length() == 1 ? DataResult.success(text.charAt(0))
            : DataResult.error(() -> "A symbol is one character, not \"" + text + "\""), String::valueOf);
    private static final Codec<AltarStructureMap.Layer> LAYER = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("y").forGetter(AltarStructureMap.Layer::up),
            Codec.STRING.listOf(1, 2 * AltarStructureMap.REACH + 1).fieldOf("rows").forGetter(AltarStructureMap.Layer::rows))
            .apply(instance, AltarStructureMap.Layer::new));
    public static final Codec<AltarStructure> CODEC = RecordCodecBuilder.<AltarStructure>create(instance -> instance.group(
            AltarStar.FORM_CODEC.optionalFieldOf("form", AltarForm.HEXAGRAM).forGetter(AltarStructure::form),
            Codec.unboundedMap(SYMBOL, Part.CODEC).fieldOf("key").forGetter(AltarStructure::key),
            LAYER.listOf().fieldOf("layers").forGetter(AltarStructure::layers))
            .apply(instance, AltarStructure::new)).validate(structure -> {
                String problem = AltarStructureMap.problem(structure.layers(), structure.key().keySet(), structure.form());
                return problem == null ? DataResult.success(structure) : DataResult.error(() -> problem);
            });

    private final AltarForm form;
    private final Map<Character, Part> key;
    private final List<AltarStructureMap.Layer> layers;
    private final List<AltarStructureMap.Cell> cells;

    public AltarStructure(AltarForm form, Map<Character, Part> key, List<AltarStructureMap.Layer> layers) {
        this.form = form;
        this.key = Map.copyOf(key);
        this.layers = List.copyOf(layers);
        this.cells = AltarStructureMap.problem(this.layers, this.key.keySet(), form) == null ? AltarStructureMap.cells(this.layers) : List.of();
    }

    /** The shape of the altar it is built round. */
    public AltarForm form() { return form; }
    public Map<Character, Part> key() { return key; }
    public List<AltarStructureMap.Layer> layers() { return layers; }
    /** The places it asks for blocks at, as though the altar faced north. */
    public List<AltarStructureMap.Cell> cells() { return cells; }
    public Part part(AltarStructureMap.Cell cell) { return key.get(cell.symbol()); }

    /** Whether it all stands; never, for a structure made in code that asks for nothing (the data cannot). */
    public boolean standsIn(AltarSpace space) {
        for (AltarStructureMap.Cell cell : cells) if (!part(cell).test(space.at(cell.east(), cell.up(), cell.north()))) return false;
        return !cells.isEmpty();
    }

    /** The places where what it asks for does not stand, in the order of its map. */
    public List<AltarStructureMap.Cell> wrong(AltarSpace space) {
        List<AltarStructureMap.Cell> wrong = new ArrayList<>();
        for (AltarStructureMap.Cell cell : cells) if (!part(cell).test(space.at(cell.east(), cell.up(), cell.north()))) wrong.add(cell);
        return wrong;
    }

    /** How many of each block it asks for, by symbol in the order the map first uses them. */
    public Map<Character, Integer> counts() {
        Map<Character, Integer> counts = new java.util.LinkedHashMap<>();
        for (AltarStructureMap.Cell cell : cells) counts.merge(cell.symbol(), 1, Integer::sum);
        return counts;
    }

    /** Its name: altar_structure.&lt;namespace&gt;.&lt;path&gt; in the language files. */
    public static Component name(ResourceKey<AltarStructure> id) { return Component.translatable(Util.makeDescriptionId("altar_structure", id.location())); }
    public static Component name(Holder<AltarStructure> structure) {
        return structure.unwrapKey().map(AltarStructure::name).orElseGet(() -> Component.translatable("corruption.altar_structure.unnamed"));
    }

    // In the world

    /** The quarter turn that takes a north-facing altar to one facing this way. */
    public static Rotation turn(AltarFacing facing) { return Rotation.values()[facing.quarterTurns()]; }
    /** The quarter turn back. */
    public static Rotation back(AltarFacing facing) { return Rotation.values()[(4 - facing.quarterTurns()) % 4]; }

    /** Where a place round the altar lies, given in blocks east, up and north of the offering as though it faced north. */
    public static BlockPos place(BlockPos offering, AltarFacing facing, int east, int up, int north) {
        int[] at = facing.inWorld(east, north);
        return offering.offset(at[0], up, -at[1]);
    }

    /** What stands round an offering, as a structure sees it (see {@link AltarSpace}). */
    public static AltarSpace around(Level level, BlockPos offering, AltarFacing facing) {
        Rotation back = back(facing);
        return (east, up, north) -> {
            BlockPos pos = place(offering, facing, east, up, north);
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof StaticCorruptedBlock corrupted) state = corrupted.source(state);
            return state.rotate(level, pos, back);
        };
    }

    /**
     * Raises a whole altar of its shape facing this way: builds this round it, clears its ground, and sets its
     * offering and pillars in their places.
     */
    public void buildWithAltar(Level level, BlockPos offering, AltarFacing facing) {
        build(level, offering, facing);
        for (int east = -AltarLayout.REACH; east <= AltarLayout.REACH; east++)
            for (int north = -AltarLayout.REACH; north <= AltarLayout.REACH; north++)
                level.setBlock(place(offering, facing, east, 0, north), form.slotAt(east, north) >= 0 ? ModBlocks.ALTAR_PILLAR.get().defaultBlockState()
                        : Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(offering, ModBlocks.ALTAR_OFFERING.get().defaultBlockState().setValue(AltarOfferingBlock.FACING, Direction.valueOf(facing.name()))
                .setValue(AltarOfferingBlock.STAR, AltarStar.of(form)), Block.UPDATE_ALL);
    }

    /** Builds it round an offering facing this way, block by block as it asks, the places it leaves to anything untouched. */
    public void build(Level level, BlockPos offering, AltarFacing facing) {
        Rotation turn = turn(facing);
        for (AltarStructureMap.Cell cell : cells) {
            BlockState state = part(cell).placed();
            if (state == null) continue;
            BlockPos pos = place(offering, facing, cell.east(), cell.up(), cell.north());
            level.setBlock(pos, state.rotate(level, pos, turn), Block.UPDATE_ALL);
        }
    }
}
