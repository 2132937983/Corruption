package com.miophas.corruption.common.altar;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonParser;
import com.miophas.corruption.common.block.AltarOfferingBlock;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.GeneratedCorruption;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.core.altar.AltarFacing;
import com.miophas.corruption.core.altar.AltarForm;
import com.miophas.corruption.core.altar.AltarLayout;
import com.miophas.corruption.core.altar.AltarState;
import com.miophas.corruption.core.altar.AltarStructureMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Structures built round altars: the Echo Sanctum as the data has it, round an octagram altar; built facing any way,
 * it stands, and turned it does not; the offering finds the shape of the altar round it, and changes with it; a block
 * missing or wrong is found, while corruption over it leaves it standing; a rite asking for it is performed within it
 * and not without; what the data may not say is refused; and the commands build and check it.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class AltarStructureServerTest {
    private static final ResourceKey<AltarStructure> SANCTUM = ResourceKey.create(AltarStructure.REGISTRY, ResourceLocation.parse("corruption:echo_sanctum"));
    /** The test world's surface is at y -60: the offering stands on it, the sanctum's floor sunk into it. */
    private static final int Y = -59;

    private static Holder<AltarStructure> sanctum(ServerLevel level) { return level.registryAccess().registryOrThrow(AltarStructure.REGISTRY).getHolderOrThrow(SANCTUM); }

    private static AltarOfferingBlockEntity raise(ServerLevel level, BlockPos at, AltarFacing facing) {
        TestWorld.load(level, at.getX() - 8, at.getX() + 8, at.getZ() - 8, at.getZ() + 8);
        sanctum(level).value().buildWithAltar(level, at, facing);
        return assertInstanceOf(AltarOfferingBlockEntity.class, level.getBlockEntity(at));
    }

    private static BlockPos at(AltarOfferingBlockEntity offering, AltarStructureMap.Cell cell) {
        return AltarStructure.place(offering.getBlockPos(), offering.facing(), cell.east(), cell.up(), cell.north());
    }

    @Test void theSanctumIsInTheDataAsDesigned(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarStructure structure = sanctum(level).value();
            Map<String, Integer> counts = new HashMap<>();
            int up = Integer.MAX_VALUE, top = Integer.MIN_VALUE;
            for (AltarStructureMap.Cell cell : structure.cells()) {
                BlockState placed = structure.part(cell).placed();
                counts.merge(BuiltInRegistries.BLOCK.getKey(placed.getBlock()).getPath(), 1, Integer::sum);
                if (placed.hasProperty(SlabBlock.TYPE)) assertEquals(SlabType.BOTTOM, placed.getValue(SlabBlock.TYPE), "slabs lie low");
                if (placed.hasProperty(StairBlock.HALF)) assertEquals(Half.BOTTOM, placed.getValue(StairBlock.HALF), "stairs stand upright");
                assertFalse(cell.up() == 0 && AltarLayout.onGround(cell.east(), cell.north()), "nothing on the altar's ground");
                up = Math.min(up, cell.up());
                top = Math.max(top, cell.up());
                assertTrue(Math.abs(cell.east()) <= 5 && Math.abs(cell.north()) <= 5, "eleven by eleven");
            }
            assertEquals(Map.of("quartz_block", 85, "corruption_arcane_brick", 14, "corruption_arcane_brick_slab", 33,
                    "corruption_arcane_brick_stairs", 5, "corruption_echo_alloy_block", 5), counts);
            assertEquals(AltarForm.OCTAGRAM, structure.form(), "built round an octagram altar");
            // Each pillar, and the offering, stands on arcane bricks.
            for (int slot = 0; slot < AltarForm.OCTAGRAM.slots(); slot++) {
                int east = AltarForm.OCTAGRAM.east(slot), north = AltarForm.OCTAGRAM.north(slot);
                assertEquals('B', structure.cells().stream().filter(c -> c.up() == -1 && c.east() == east && c.north() == north).findFirst().orElseThrow().symbol());
            }
            assertEquals(-1, up);
            assertEquals(3, top);
            assertEquals("altar_structure.corruption.echo_sanctum", ((net.minecraft.network.chat.contents.TranslatableContents) AltarStructure.name(SANCTUM).getContents()).getKey());
        });
    }

    @Test void builtFacingAnyWayItStandsAndTurnedItDoesNot(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            for (AltarFacing facing : AltarFacing.values()) {
                AltarOfferingBlockEntity offering = raise(level, new BlockPos(2000 + 40 * facing.ordinal(), Y, 2000), facing);
                assertEquals(facing, offering.facing());
                assertTrue(offering.formed(level), facing + ": the altar stands");
                assertEquals(AltarForm.OCTAGRAM, offering.standing(level), facing + ": eight pillars");
                assertEquals(AltarForm.OCTAGRAM, offering.form(), "built, the offering shows it, so the octagram's circle is drawn");
                AltarStructure structure = sanctum(level).value();
                assertEquals(List.of(), structure.wrong(offering.space(level)), facing + ": all of it");
                assertTrue(structure.standsIn(offering.space(level)));
                // The east stair faces the middle in the world, whichever way that lies.
                BlockState stair = level.getBlockState(AltarStructure.place(offering.getBlockPos(), facing, 5, -1, 0));
                assertEquals(Direction.valueOf(facing.name()).getCounterClockWise(), stair.getValue(StairBlock.FACING), facing + ": the east stair climbs inwards");
                // The way the offering faces is the way the structure must: turn it, and it no longer stands.
                BlockPos pos = offering.getBlockPos();
                level.setBlock(pos, level.getBlockState(pos).setValue(AltarOfferingBlock.FACING, Direction.valueOf(facing.name()).getClockWise()), 3);
                AltarOfferingBlockEntity turned = (AltarOfferingBlockEntity) level.getBlockEntity(pos);
                assertFalse(structure.standsIn(turned.space(level)), facing + " turned");
            }
        });
    }

    @Test void aBlockMissingOrWrongIsFoundButCorruptionLeavesItStanding(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, new BlockPos(2400, Y, 2000), AltarFacing.EAST);
            AltarStructure structure = sanctum(level).value();
            List<AltarStructureMap.Cell> cells = structure.cells();
            AltarStructureMap.Cell groove = cells.stream().filter(c -> c.symbol() == '_' && c.up() == -1).findFirst().orElseThrow();
            BlockPos slab = at(offering, groove);
            BlockState was = level.getBlockState(slab);
            level.setBlock(slab, Blocks.AIR.defaultBlockState(), 3);
            assertEquals(List.of(groove), structure.wrong(offering.space(level)), "a slab missing");
            level.setBlock(slab, was.setValue(SlabBlock.TYPE, SlabType.TOP), 3);
            assertEquals(List.of(groove), structure.wrong(offering.space(level)), "a slab laid high");
            level.setBlock(slab, was, 3);

            AltarStructureMap.Cell step = cells.stream().filter(c -> c.symbol() == '^').findFirst().orElseThrow();
            BlockPos stair = at(offering, step);
            BlockState climbing = level.getBlockState(stair);
            level.setBlock(stair, climbing.setValue(StairBlock.FACING, climbing.getValue(StairBlock.FACING).getOpposite()), 3);
            assertEquals(List.of(step), structure.wrong(offering.space(level)), "a stair the wrong way round");
            level.setBlock(stair, climbing, 3);

            AltarStructureMap.Cell head = cells.stream().filter(c -> c.symbol() == 'E').findFirst().orElseThrow();
            level.setBlock(at(offering, head), Blocks.IRON_BLOCK.defaultBlockState(), 3);
            assertEquals(List.of(head), structure.wrong(offering.space(level)), "iron for the alloy");
            level.setBlock(at(offering, head), ModBlocks.CORRUPTION_ECHO_ALLOY_BLOCK.get().defaultBlockState(), 3);

            // Corruption spreading over it: every block of it that has a corrupted twin becomes it.
            int corrupted = 0;
            for (AltarStructureMap.Cell cell : cells) {
                BlockState twin = GeneratedCorruption.convert(level.getBlockState(at(offering, cell)));
                if (twin == null) continue;
                level.setBlock(at(offering, cell), twin, 3);
                corrupted++;
            }
            assertTrue(corrupted > 100, "most of it can be corrupted: " + corrupted);
            assertEquals(List.of(), structure.wrong(offering.space(level)), "and still stands");
        });
    }

    private static final String RITE = """
            {"type": "corruption:altar_shaped", "pillars": {"1": {"item": "minecraft:stick"}}, "structure": "corruption:echo_sanctum",
             "time": 20, "result": {"id": "minecraft:diamond"}}""";

    private static AltarRecipe rite(ServerLevel level, String json) {
        return AltarRecipe.SHAPED_CODEC.codec().parse(level.registryAccess().createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(json)).getOrThrow();
    }

    @Test void aRiteAskingForItIsPerformedWithinItAndNotWithout(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarRecipe recipe = rite(level, RITE);
            assertEquals(SANCTUM, recipe.structure().orElseThrow().unwrapKey().orElseThrow());
            assertTrue(AltarRecipe.SHAPED_CODEC.codec().parse(level.registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString(RITE.replace("echo_sanctum", "no_such_place"))).isError(), "a structure the data does not have");
            assertEquals(AltarForm.OCTAGRAM, recipe.form(), "a rite asking for the sanctum is an octagram's");
            assertEquals(8, recipe.pillars().size());
            assertTrue(AltarRecipe.SHAPED_CODEC.codec().parse(level.registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString(RITE.replace("\"structure\"", "\"form\": \"hexagram\", \"structure\""))).isError(), "nor can it be a hexagram's");
            RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
            com.miophas.corruption.common.registry.ModRecipes.ALTAR_SHAPED.get().streamCodec().encode(buffer, recipe);
            AltarRecipe sent = com.miophas.corruption.common.registry.ModRecipes.ALTAR_SHAPED.get().streamCodec().decode(buffer);
            assertEquals(recipe.structure(), sent.structure(), "sent to clients with it");
            assertEquals(AltarForm.OCTAGRAM, sent.form(), "and its shape");
            assertEquals(8, sent.pillars().size());
            List<ItemStack> stick = new ArrayList<>(Collections.nCopies(8, ItemStack.EMPTY));
            stick.set(0, new ItemStack(Items.STICK));
            assertFalse(recipe.matches(new AltarInput(stick, null), level), "nothing standing round it");

            var manager = level.getServer().getRecipeManager();
            List<RecipeHolder<?>> before = List.copyOf(manager.getRecipes());
            List<RecipeHolder<?>> with = new ArrayList<>(before);
            with.add(new RecipeHolder<>(ResourceLocation.parse("corruption:test/sanctum_rite"), recipe));
            manager.replaceRecipes(with);
            try {
                assertEquals(1, perform(level, raise(level, new BlockPos(2600, Y, 2000), AltarFacing.SOUTH)), "within the sanctum, a diamond");
                AltarOfferingBlockEntity broken = raise(level, new BlockPos(2640, Y, 2000), AltarFacing.SOUTH);
                AltarStructureMap.Cell corner = sanctum(level).value().cells().stream().filter(c -> c.up() == 3).findFirst().orElseThrow();
                level.setBlock(at(broken, corner), Blocks.AIR.defaultBlockState(), 3);
                assertEquals(0, perform(level, broken), "an obelisk's cap gone, nothing");
            } finally { manager.replaceRecipes(before); }
        });
    }

    /** Sets a stick on the first pillar and runs the offering a while; answers how many diamonds it brought forth. */
    private static int perform(ServerLevel level, AltarOfferingBlockEntity offering) {
        BlockPos first = offering.place(AltarLayout.east(0), AltarLayout.north(0));
        ((AltarPillarBlockEntity) level.getBlockEntity(first)).set(new ItemStack(Items.STICK));
        for (int tick = 0; tick < 80; tick++) AltarOfferingBlockEntity.serverTick(level, offering.getBlockPos(), level.getBlockState(offering.getBlockPos()), offering);
        int diamonds = level.getEntitiesOfClass(ItemEntity.class, new AABB(offering.getBlockPos()).inflate(2), item -> item.getItem().is(Items.DIAMOND))
                .stream().mapToInt(item -> item.getItem().getCount()).sum();
        boolean stickGone = ((AltarPillarBlockEntity) level.getBlockEntity(first)).item().isEmpty();
        assertEquals(diamonds > 0, stickGone, "the stick is taken only when the rite is done");
        return diamonds;
    }

    @Test void theOfferingFindsTheShapeOfItsAltarAndChangesWithIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, new BlockPos(2700, Y, 2000), AltarFacing.NORTH);
            BlockPos pos = offering.getBlockPos();
            tick(level, offering, 10);
            assertEquals(AltarState.BINDING, offering.state());
            assertEquals(AltarForm.OCTAGRAM, offering.form());
            // Its octagram pillars off the diagonals and the sides, a hexagram's in their places: a hexagram stands.
            for (int slot = 0; slot < AltarForm.OCTAGRAM.slots(); slot++)
                if (AltarForm.HEXAGRAM.slotAt(AltarForm.OCTAGRAM.east(slot), AltarForm.OCTAGRAM.north(slot)) < 0)
                    level.setBlock(offering.place(AltarForm.OCTAGRAM.east(slot), AltarForm.OCTAGRAM.north(slot)), Blocks.AIR.defaultBlockState(), 3);
            for (int slot = 0; slot < AltarForm.HEXAGRAM.slots(); slot++)
                level.setBlock(offering.place(AltarForm.HEXAGRAM.east(slot), AltarForm.HEXAGRAM.north(slot)), ModBlocks.ALTAR_PILLAR.get().defaultBlockState(), 3);
            assertEquals(AltarForm.HEXAGRAM, offering.standing(level));
            tick(level, offering, 10);
            assertEquals(AltarForm.HEXAGRAM, offering.form(), "the hexagram's circle now");
            assertEquals(AltarStar.HEXAGRAM, level.getBlockState(pos).getValue(AltarOfferingBlock.STAR));
            assertTrue(offering.state().showsCircle());
            assertTrue(sanctum(level).value().standsIn(offering.space(level)), "the sanctum itself still stands round it");
            List<ItemStack> six = new ArrayList<>(Collections.nCopies(6, ItemStack.EMPTY));
            six.set(0, new ItemStack(Items.STICK));
            assertFalse(rite(level, RITE).matches(new AltarInput(six, null, AltarGround.CLEAR, new int[0], offering.space(level)), level),
                    "but a rite asking for it is an octagram's, not performed at a hexagram");
            // One more gone: nothing stands, and the offering keeps the shape it last found.
            level.setBlock(offering.place(AltarForm.HEXAGRAM.east(1), AltarForm.HEXAGRAM.north(1)), Blocks.AIR.defaultBlockState(), 3);
            tick(level, offering, 10);
            assertEquals(AltarState.DORMANT, offering.state());
            assertEquals(AltarForm.HEXAGRAM, offering.form());
        });
    }

    private static void tick(ServerLevel level, AltarOfferingBlockEntity offering, int ticks) {
        for (int tick = 0; tick < ticks; tick++) AltarOfferingBlockEntity.serverTick(level, offering.getBlockPos(), level.getBlockState(offering.getBlockPos()), offering);
    }

    @Test void theDataMayNotAskWhatCannotBe(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            String good = """
                    {"key": {"s": {"block": "corruption:corruption_arcane_brick_slab", "state": {"type": "bottom"}}, "q": {"block": "#c:storage_blocks/quartz"}},
                     "layers": [{"y": -1, "rows": ["sqs"]}]}""";
            assertTrue(parse(level, good).isSuccess(), "a slab and a tag");
            assertTrue(parse(level, good.replace("\"bottom\"", "\"sideways\"")).isError(), "a slab is never sideways");
            assertTrue(parse(level, good.replace("\"type\"", "\"facing\"")).isError(), "a slab has no facing");
            assertTrue(parse(level, good.replace("\"y\": -1", "\"y\": 0")).isError(), "beside the offering, on its ground");
            assertTrue(parse(level, good.replace("\"q\":", "\"qq\":")).isError(), "a symbol is one character");
            assertTrue(parse(level, good.replace("sqs", "sxs")).isError(), "x is in no key");
            String marked = "{\"form\": \"%s\", \"key\": {\"q\": {\"block\": \"minecraft:quartz_block\"}}, \"layers\": [{\"y\": -1, \"rows\": [\"q\"]},"
                    + " {\"y\": 0, \"rows\": [\".......\", \"..P....\", \".......\", \"...@...\", \".......\", \".......\", \".......\"]}]}";
            assertTrue(parse(level, marked.formatted("hexagram")).isError(), "a hexagram has no pillar at -1, 2");
            assertTrue(parse(level, marked.formatted("octagram")).isError(), "nor an octagram");
            String octagramMark = marked.replace("\"..P....\"", "\".......\"").replace("\"y\": 0, \"rows\": [\".......\"", "\"y\": 0, \"rows\": [\"P......\"");
            assertTrue(parse(level, octagramMark.formatted("octagram")).isSuccess(), "an octagram's at -3, 3");
            assertTrue(parse(level, octagramMark.formatted("hexagram")).isError(), "not a hexagram's");
            assertEquals(AltarForm.HEXAGRAM, parse(level, good).getOrThrow().form(), "left unsaid, a hexagram's");
        });
    }

    private static com.mojang.serialization.DataResult<AltarStructure> parse(ServerLevel level, String json) {
        return AltarStructure.CODEC.parse(level.registryAccess().createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(json));
    }

    /** Whoever runs a command, with the chat it is told kept. */
    private static final class Listener implements CommandSource {
        final List<String> heard = new ArrayList<>();
        @Override public void sendSystemMessage(Component message) { heard.add(message.getString()); }
        @Override public boolean acceptsSuccess() { return true; }
        @Override public boolean acceptsFailure() { return true; }
        @Override public boolean shouldInformAdmins() { return false; }
    }

    @Test void theCommandsBuildItAndFindWhatIsWrong(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Listener listener = new Listener();
            CommandSourceStack source = new CommandSourceStack(listener, Vec3.ZERO, Vec2.ZERO, level, 4, "test", Component.literal("test"), level.getServer(), null);
            var commands = level.getServer().getCommands().getDispatcher();
            BlockPos pos = new BlockPos(2800, Y, 2000);
            TestWorld.load(level, pos.getX() - 8, pos.getX() + 8, pos.getZ() - 8, pos.getZ() + 8);
            String where = pos.getX() + " " + pos.getY() + " " + pos.getZ();
            try {
                int asked = sanctum(level).value().cells().size();
                assertEquals(asked, commands.execute("corruption altar build corruption:echo_sanctum " + where + " west", source));
                assertEquals(Direction.WEST, level.getBlockState(pos).getValue(AltarOfferingBlock.FACING));
                assertEquals(AltarStar.OCTAGRAM, level.getBlockState(pos).getValue(AltarOfferingBlock.STAR));
                assertEquals(asked, commands.execute("corruption altar check corruption:echo_sanctum " + where, source), "it stands");
                BlockPos head = AltarStructure.place(pos, AltarFacing.WEST, 0, 1, 5);
                assertTrue(level.getBlockState(head).is(ModBlocks.CORRUPTION_ECHO_ALLOY_BLOCK.get()), "the alloy stands at the head, west of the offering");
                level.setBlock(head, Blocks.AIR.defaultBlockState(), 3);
                listener.heard.clear();
                assertEquals(0, commands.execute("corruption altar check corruption:echo_sanctum " + where, source));
                assertTrue(listener.heard.stream().anyMatch(line -> line.contains(head.getX() + ", " + head.getY() + ", " + head.getZ())), "names where: " + listener.heard);
                assertThrows(CommandSyntaxException.class, () -> commands.execute("corruption altar build corruption:no_such_place " + where, source));
                assertThrows(CommandSyntaxException.class, () -> commands.execute("corruption altar check corruption:echo_sanctum " + pos.getX() + " " + (Y + 5) + " " + pos.getZ(), source),
                        "no offering there");
            } catch (CommandSyntaxException failure) { fail(failure); }
        });
    }
}
