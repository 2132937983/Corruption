package com.miophas.corruption.common.altar;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.block.AltarOfferingBlock;
import com.miophas.corruption.common.entity.CorruptLightning;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.registry.ModRecipes;
import com.miophas.corruption.core.altar.AltarFacing;
import com.miophas.corruption.core.altar.AltarLayout;
import com.miophas.corruption.core.altar.AltarRules;
import com.miophas.corruption.core.altar.AltarState;
import com.miophas.corruption.core.altar.RitualRules;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The altar on a real server, where its rites are loaded from the mod's data. Each test raises an altar of its
 * own and ticks its offering by hand. The test world never brings new creatures into sight, so a creature is
 * bound by hand rather than found walking onto the offering, and what the altar brings forth is caught as it
 * joins the world.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class AltarServerTest {
    /** An offering at x, facing north, its six pillars round it, on a floor of stone, its ground otherwise bare. */
    private static AltarOfferingBlockEntity raise(ServerLevel level, int x) { return raise(level, x, Direction.NORTH); }
    /** An offering at x facing this way, its six pillars round it as it faces, on a floor of stone, its ground otherwise bare. */
    private static AltarOfferingBlockEntity raise(ServerLevel level, int x, Direction facing) {
        BlockPos at = new BlockPos(x, -50, 3000);
        TestWorld.load(level, x - AltarLayout.REACH, x + AltarLayout.REACH, 3000 - AltarLayout.REACH, 3000 + AltarLayout.REACH);
        for (int east = -AltarLayout.REACH; east <= AltarLayout.REACH; east++)
            for (int north = -AltarLayout.REACH; north <= AltarLayout.REACH; north++) {
                level.setBlock(groundAt(at, east, north).below(), Blocks.STONE.defaultBlockState(), 3);
                level.setBlock(groundAt(at, east, north), Blocks.AIR.defaultBlockState(), 3);
            }
        level.setBlock(at, ModBlocks.ALTAR_OFFERING.get().defaultBlockState().setValue(AltarOfferingBlock.FACING, facing), 3);
        AltarOfferingBlockEntity offering = (AltarOfferingBlockEntity) level.getBlockEntity(at);
        for (int slot = 0; slot < AltarLayout.SLOTS; slot++) level.setBlock(pillarAt(offering, slot), ModBlocks.ALTAR_PILLAR.get().defaultBlockState(), 3);
        return offering;
    }
    /** Where a pillar stands, the way the offering faces. */
    private static BlockPos pillarAt(AltarOfferingBlockEntity offering, int slot) { return offering.place(AltarLayout.east(slot), AltarLayout.north(slot)); }
    /** A place in the world, east and north of the offering whichever way it faces. */
    private static BlockPos groundAt(BlockPos offering, int east, int north) { return offering.offset(east, 0, -north); }
    /** Sets a block on the ground, at a place given as the altar's recipes give it: turned the way the offering faces. */
    private static void put(ServerLevel level, AltarOfferingBlockEntity offering, BlockState block, int east, int north) { level.setBlock(offering.place(east, north), block, 3); }
    private static AltarPillarBlockEntity pillar(ServerLevel level, AltarOfferingBlockEntity offering, int slot) {
        return (AltarPillarBlockEntity) level.getBlockEntity(pillarAt(offering, slot));
    }
    private static void tick(ServerLevel level, AltarOfferingBlockEntity offering, int ticks) {
        for (int tick = 0; tick < ticks; tick++) AltarOfferingBlockEntity.serverTick(level, offering.getBlockPos(), level.getBlockState(offering.getBlockPos()), offering);
    }
    /** Ticks it, catching whatever joins the world meanwhile, in order. */
    private static List<Entity> tickCatching(ServerLevel level, AltarOfferingBlockEntity offering, int ticks) {
        List<Entity> joined = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> joined.add(event.getEntity());
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try { tick(level, offering, ticks); }
        finally { NeoForge.EVENT_BUS.unregister(catcher); }
        joined.forEach(Entity::discard);
        return joined;
    }
    private static void clear(ServerLevel level, AltarOfferingBlockEntity offering) {
        for (int east = -AltarLayout.REACH; east <= AltarLayout.REACH; east++)
            for (int north = -AltarLayout.REACH; north <= AltarLayout.REACH; north++) level.setBlock(groundAt(offering.getBlockPos(), east, north), Blocks.AIR.defaultBlockState(), 3);
    }
    /** A player, not truly in the world but counted among its players, standing east and north of the offering. */
    private static FakePlayer witness(ServerLevel level, AltarOfferingBlockEntity offering, double east, double north, int corruption) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.fromString("5f1c5d8e-6a2b-4f39-9d1e-a17a2e000001"), "altar_witness"));
        player.moveTo(offering.top().x + east, offering.getBlockPos().getY(), offering.top().z - north);
        player.setData(ModAttachments.PLAYER_CORRUPTION, corruption);
        if (!level.players().contains(player)) level.players().add(player);
        return player;
    }
    private static final int[][] RING = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};

    @Test void raisedItBindsBrokenItSleepsAndUsedItBindsOrLetsGo(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_000);
            assertEquals(AltarState.DORMANT, offering.state(), "until it looks");
            tick(level, offering, AltarRules.CHECK_TICKS);
            assertEquals(AltarState.BINDING, offering.state(), "its six pillars stand: its circle shows, and it binds");
            assertEquals(AltarMode.BINDING, level.getBlockState(offering.getBlockPos()).getValue(AltarOfferingBlock.ALTAR));
            level.setBlock(pillarAt(offering, 2), Blocks.AIR.defaultBlockState(), 3);
            tick(level, offering, AltarRules.CHECK_TICKS);
            assertEquals(AltarState.DORMANT, offering.state(), "a pillar gone, it sleeps");
            level.setBlock(pillarAt(offering, 2), ModBlocks.ALTAR_PILLAR.get().defaultBlockState(), 3);
            assertEquals(AltarState.BINDING, offering.use(level), "used, it looks at once");
            assertEquals(AltarState.FORMED, offering.use(level), "used again, it lets go");
            tick(level, offering, AltarRules.CHECK_TICKS * 3);
            assertEquals(AltarState.FORMED, offering.state(), "and stays so");
            assertEquals(AltarState.BINDING, offering.use(level), "and again, it binds");
            assertEquals(AltarMode.BINDING, level.getBlockState(offering.getBlockPos()).getValue(AltarOfferingBlock.ALTAR));
            level.setBlock(pillarAt(offering, 5), Blocks.STONE.defaultBlockState(), 3);
            assertEquals(AltarState.DORMANT, offering.use(level));
            assertEquals(AltarState.DORMANT, offering.use(level), "without its altar, it cannot bind");
            clear(level, offering);
        });
    }

    @Test void aShapedRiteNeedsEachPillarByItsNumberAndStrikesBeforeItBrings(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_016);
            tick(level, offering, AltarRules.CHECK_TICKS);
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++)
                pillar(level, offering, slot).set(new ItemStack(slot % 2 == 0 ? ModItems.CORRUPTED_BRASS.get() : ModItems.CORRUPTION_CRYSTAL.get()));
            tick(level, offering, 20);
            assertEquals(0, offering.elapsed(), "turned round, it is no rite");
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++)
                pillar(level, offering, slot).set(new ItemStack(slot % 2 == 0 ? ModItems.CORRUPTION_CRYSTAL.get() : ModItems.CORRUPTED_BRASS.get()));
            List<Entity> joined = tickCatching(level, offering, RitualRules.DEFAULT_TICKS + RitualRules.AFTER_STRIKE + 1);
            assertEquals(2, joined.size(), joined.toString());
            assertInstanceOf(CorruptLightning.class, joined.get(0), "purple lightning strikes first");
            assertTrue(joined.get(1) instanceof ItemEntity drop && drop.getItem().is(ModItems.ANCIENT_CORRUPTION_CRYSTAL.get()), "then the crystal appears");
            assertEquals(offering.top().x, joined.get(1).getX(), 1e-6, "on the offering");
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++) assertTrue(pillar(level, offering, slot).isEmpty(), "what lay on the pillars is used");
            assertEquals(AltarState.FORMED, offering.state(), "and it binds no more");
            clear(level, offering);
        });
    }

    @Test void aShapelessRiteTakesItsIngredientsFromAnyPillars(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_032);
            tick(level, offering, AltarRules.CHECK_TICKS);
            pillar(level, offering, 5).set(new ItemStack(Items.ROTTEN_FLESH));
            pillar(level, offering, 1).set(new ItemStack(Items.SPIDER_EYE));
            pillar(level, offering, 3).set(new ItemStack(ModItems.CORRUPTION_DUST.get()));
            tick(level, offering, 20);
            assertEquals(0, offering.elapsed(), "the bone is missing");
            pillar(level, offering, 2).set(new ItemStack(Items.BONE));
            List<Entity> joined = tickCatching(level, offering, RitualRules.DEFAULT_TICKS + RitualRules.AFTER_STRIKE + 1);
            assertTrue(joined.stream().anyMatch(entity -> entity instanceof ItemEntity drop && drop.getItem().is(ModItems.CORRUPTION_REMAIN.get()) && drop.getItem().getCount() == 4));
            assertTrue(pillar(level, offering, 2).isEmpty() && pillar(level, offering, 5).isEmpty());
            clear(level, offering);
        });
    }

    @Test void aBoundPigIsHeldThenGivenUpForAZombifiedPiglinWhichWalksFree(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_048);
            tick(level, offering, AltarRules.CHECK_TICKS);
            for (int slot : new int[]{0, 2, 4}) pillar(level, offering, slot).set(new ItemStack(ModItems.CORRUPTION_DUST.get()));
            tick(level, offering, 20);
            assertEquals(0, offering.elapsed(), "without the pig, nothing");
            Pig pig = EntityType.PIG.create(level);
            pig.moveTo(offering.top().x + .3, offering.top().y, offering.top().z - .2);
            offering.bind(pig);
            assertEquals(offering.top().x, pig.getX(), 1e-6, "bound, it is drawn to the middle and held there");
            assertSame(pig, offering.bound());
            List<Entity> joined = tickCatching(level, offering, 80 + RitualRules.AFTER_STRIKE + 1);
            assertInstanceOf(CorruptLightning.class, joined.get(0));
            assertTrue(pig.isRemoved(), "the pig is given up");
            Entity piglin = joined.stream().filter(entity -> entity.getType() == EntityType.ZOMBIFIED_PIGLIN).findFirst().orElseThrow();
            assertEquals(offering.top().x, piglin.getX(), 1e-6, "summoned on the offering");
            assertEquals(AltarState.FORMED, offering.state(), "and the offering binds no more, so it walks free");
            assertNull(offering.bound());
            clear(level, offering);
        });
    }

    @Test void eightCandlesRoundTheOfferingAndNothingElseOnTheGround(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_064);
            tick(level, offering, AltarRules.CHECK_TICKS);
            pillar(level, offering, 0).set(new ItemStack(ModItems.CORRUPTION_CANDLE.get()));
            pillar(level, offering, 2).set(new ItemStack(ModItems.CORRUPTION_CRYSTAL.get()));
            pillar(level, offering, 4).set(new ItemStack(ModItems.CORRUPTION_CRYSTAL.get()));
            for (int at = 0; at < RING.length - 1; at++) put(level, offering, (at % 2 == 0 ? Blocks.CANDLE : Blocks.PURPLE_CANDLE).defaultBlockState(), RING[at][0], RING[at][1]);
            tick(level, offering, 20);
            assertEquals(0, offering.elapsed(), "a candle short");
            put(level, offering, Blocks.RED_CANDLE.defaultBlockState(), RING[7][0], RING[7][1]);
            put(level, offering, Blocks.TORCH.defaultBlockState(), -3, 2);
            tick(level, offering, 20);
            assertEquals(0, offering.elapsed(), "the candles, and a torch as well");
            put(level, offering, Blocks.AIR.defaultBlockState(), -3, 2);
            List<Entity> joined = tickCatching(level, offering, RitualRules.DEFAULT_TICKS + RitualRules.AFTER_STRIKE + 1);
            assertTrue(joined.stream().anyMatch(entity -> entity instanceof ItemEntity drop && drop.getItem().is(ModItems.HARMONIZED_CORRUPTION_CRYSTAL.get())), joined.toString());
            for (int[] at : RING) assertTrue(level.getBlockState(offering.place(at[0], at[1])).is(BlockTags.CANDLES), "the candles stay");
            clear(level, offering);
        });
    }

    @Test void maliceWaitsForACorruptedPlayerWithinTheAltar(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_080);
            tick(level, offering, AltarRules.CHECK_TICKS);
            pillar(level, offering, 1).set(new ItemStack(ModItems.CORRUPTION_REMAIN.get()));
            pillar(level, offering, 3).set(new ItemStack(ModItems.CORRUPTION_DUST.get()));
            pillar(level, offering, 5).set(new ItemStack(ModItems.CORRUPTION_DUST.get()));
            tick(level, offering, 20);
            assertEquals(0, offering.elapsed(), "nobody there");
            FakePlayer player = witness(level, offering, 6, -1, 20);
            try {
                tick(level, offering, 20);
                assertEquals(0, offering.elapsed(), "corrupted enough, but outside the altar");
                witness(level, offering, 2.5, -1, 9);
                tick(level, offering, 20);
                assertEquals(0, offering.elapsed(), "within, but not corrupted enough");
                player.setData(ModAttachments.PLAYER_CORRUPTION, 10);
                List<Entity> joined = tickCatching(level, offering, RitualRules.DEFAULT_TICKS + RitualRules.AFTER_STRIKE + 1);
                assertTrue(joined.stream().anyMatch(entity -> entity instanceof ItemEntity drop && drop.getItem().is(ModItems.MALICE.get()) && drop.getItem().getCount() == 4), joined.toString());
            } finally {
                level.players().remove(player);
                player.setData(ModAttachments.PLAYER_CORRUPTION, 0);
            }
            clear(level, offering);
        });
    }

    @Test void aVillagerBoundAmidSoulSandBecomesAVex(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_096);
            tick(level, offering, AltarRules.CHECK_TICKS);
            pillar(level, offering, 0).set(new ItemStack(Items.PHANTOM_MEMBRANE));
            pillar(level, offering, 3).set(new ItemStack(ModItems.CORRUPTION_CRYSTAL.get()));
            Villager villager = EntityType.VILLAGER.create(level);
            villager.moveTo(offering.top());
            offering.bind(villager);
            tick(level, offering, 20);
            assertEquals(0, offering.elapsed(), "no soul sand");
            put(level, offering, Blocks.SOUL_SAND.defaultBlockState(), 2, 2);
            put(level, offering, Blocks.SOUL_SOIL.defaultBlockState(), 2, -2);
            put(level, offering, Blocks.SOUL_SAND.defaultBlockState(), -2, -2);
            put(level, offering, Blocks.SOUL_SOIL.defaultBlockState(), -2, 2);
            List<Entity> joined = tickCatching(level, offering, 80 + RitualRules.AFTER_STRIKE + 1);
            assertInstanceOf(CorruptLightning.class, joined.get(0));
            assertTrue(villager.isRemoved(), "the villager is given up");
            assertTrue(joined.stream().anyMatch(entity -> entity.getType() == EntityType.VEX), joined.toString());
            clear(level, offering);
        });
    }

    @Test void aPillarIsFilledAndEmptiedByHandWithoutSneakingAndPlayersNearItSeeItEmptied(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_112);
            AltarPillarBlockEntity pillar = pillar(level, offering, 0);
            BlockPos pos = pillar.getBlockPos();
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
            FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.fromString("5f1c5d8e-6a2b-4f39-9d1e-a17a2e000002"), "pillar_user"));
            player.getInventory().clearContent();
            player.setShiftKeyDown(false);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND, 3));
            assertTrue(level.getBlockState(pos).useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit).consumesAction());
            assertTrue(pillar.item().is(Items.DIAMOND));
            assertEquals(2, player.getMainHandItem().getCount(), "one is laid on it");
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
            assertEquals(ItemInteractionResult.CONSUME, level.getBlockState(pos).useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit),
                    "used with something else in hand, it gives back what it holds");
            assertTrue(pillar.isEmpty());
            assertTrue(player.getInventory().contains(new ItemStack(Items.DIAMOND)));
            assertTrue(player.getMainHandItem().is(Items.STICK), "and the hand is as it was");

            pillar.set(new ItemStack(Items.EMERALD));
            AltarPillarBlockEntity seen = new AltarPillarBlockEntity(pos, pillar.getBlockState());
            seen.handleUpdateTag(pillar.getUpdateTag(level.registryAccess()), level.registryAccess());
            assertTrue(seen.item().is(Items.EMERALD));
            pillar.take();
            CompoundTag emptied = pillar.getUpdateTag(level.registryAccess());
            assertFalse(emptied.isEmpty(), "an update with nothing in it is dropped, and the emerald would go on floating there");
            seen.handleUpdateTag(emptied, level.registryAccess());
            assertTrue(seen.isEmpty());
            clear(level, offering);
        });
    }

    @Test void theAltarsRitesAreLoadedAndSentToPlayersWhole(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var loaded = level.getRecipeManager().getAllRecipesFor(ModRecipes.ALTAR.get()).stream().map(holder -> holder.id().getPath()).toList();
            for (String name : new String[]{"ancient_corruption_crystal", "corruption_remain", "zombified_piglin", "harmonized_corruption_crystal", "malice", "vex"})
                assertTrue(loaded.contains("altar/" + name), name + " is loaded, among " + loaded);
            for (String name : new String[]{"harmonized_corruption_crystal", "malice", "vex"}) {
                RecipeHolder<?> holder = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", "altar/" + name)).orElseThrow();
                AltarRecipe rite = (AltarRecipe) holder.value();
                var codec = ((AltarRecipe.Serializer) rite.getSerializer()).streamCodec();
                RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
                codec.encode(buffer, rite);
                AltarRecipe sent = codec.decode(buffer);
                assertEquals(rite.shaped(), sent.shaped(), name);
                assertEquals(rite.blocks(), sent.blocks(), name);
                assertEquals(rite.corruption(), sent.corruption(), name);
                assertEquals(rite.offering(), sent.offering(), name);
                assertEquals(rite.time(), sent.time(), name);
                assertEquals(rite.result().creature(), sent.result().creature(), name);
                assertEquals(rite.pillars().size(), sent.pillars().size(), name);
            }
        });
    }

    @Test void placedItFacesTheWayThePlayerLooksAndTurnsWithTheWorld(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.fromString("5f1c5d8e-6a2b-4f39-9d1e-a17a2e000003"), "altar_builder"));
            BlockPos pos = new BlockPos(44_128, -50, 3000);
            TestWorld.load(level, pos.getX(), pos.getX(), pos.getZ(), pos.getZ());
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos.below()), Direction.UP, pos.below(), false);
            float[] yaws = {180, 270, 0, 90};
            Direction[] looking = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            for (int way = 0; way < 4; way++) {
                player.setYRot(yaws[way]);
                BlockState placed = ModBlocks.ALTAR_OFFERING.get().getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, new ItemStack(ModItems.ALTAR_OFFERING.get()), hit));
                assertEquals(looking[way], placed.getValue(AltarOfferingBlock.FACING), "looking " + looking[way]);
                assertEquals(AltarFacing.values()[way], AltarOfferingBlock.facing(placed));
            }
            BlockState north = ModBlocks.ALTAR_OFFERING.get().defaultBlockState();
            assertEquals(Direction.NORTH, north.getValue(AltarOfferingBlock.FACING), "offerings placed before it could face elsewhere face north, as they always did");
            assertEquals(Direction.EAST, north.rotate(Rotation.CLOCKWISE_90).getValue(AltarOfferingBlock.FACING));
            assertEquals(Direction.SOUTH, north.mirror(Mirror.LEFT_RIGHT).getValue(AltarOfferingBlock.FACING));
        });
    }

    @Test void facingEastItsPillarsAndGroundTurnWithIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_144, Direction.EAST);
            BlockPos at = offering.getBlockPos();
            int[][] pillars = {{4, 0}, {2, -4}, {-2, -4}, {-4, 0}, {-2, 4}, {2, 4}};
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++)
                assertEquals(groundAt(at, pillars[slot][0], pillars[slot][1]), pillarAt(offering, slot), "pillar " + (slot + 1) + " first to the east, then clockwise");
            assertEquals(groundAt(at, 0, -1), offering.place(1, 0), "what is to the right of it, facing north, is south of it facing east");
            tick(level, offering, AltarRules.CHECK_TICKS);
            assertEquals(AltarState.BINDING, offering.state(), "built facing east, it stands");
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++)
                pillar(level, offering, slot).set(new ItemStack(slot % 2 == 0 ? ModItems.CORRUPTION_CRYSTAL.get() : ModItems.CORRUPTED_BRASS.get()));
            List<Entity> joined = tickCatching(level, offering, RitualRules.DEFAULT_TICKS + RitualRules.AFTER_STRIKE + 1);
            assertTrue(joined.stream().anyMatch(entity -> entity instanceof ItemEntity drop && drop.getItem().is(ModItems.ANCIENT_CORRUPTION_CRYSTAL.get())), "its shaped rite, numbered from the east");
            clear(level, offering);

            AltarOfferingBlockEntity wrong = raise(level, 44_144, Direction.NORTH);
            level.setBlock(wrong.getBlockPos(), level.getBlockState(wrong.getBlockPos()).setValue(AltarOfferingBlock.FACING, Direction.EAST), 3);
            tick(level, wrong, AltarRules.CHECK_TICKS);
            assertEquals(AltarState.DORMANT, wrong.state(), "pillars set for an altar facing north do not stand for one facing east");
            clear(level, wrong);
        });
    }

    @Test void facingSouthTheSamePillarsAreNumberedFromTheSouth(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_160, Direction.SOUTH);
            assertEquals(groundAt(offering.getBlockPos(), 0, -4), pillarAt(offering, 0));
            tick(level, offering, AltarRules.CHECK_TICKS);
            assertEquals(AltarState.BINDING, offering.state());
            BlockPos at = offering.getBlockPos();
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++) {
                AltarPillarBlockEntity asNorth = (AltarPillarBlockEntity) level.getBlockEntity(groundAt(at, AltarLayout.east(slot), AltarLayout.north(slot)));
                asNorth.set(new ItemStack(slot % 2 == 0 ? ModItems.CORRUPTION_CRYSTAL.get() : ModItems.CORRUPTED_BRASS.get()));
            }
            tick(level, offering, 20);
            assertEquals(0, offering.elapsed(), "laid out as for an altar facing north, it is turned round: no rite");
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++)
                pillar(level, offering, slot).set(new ItemStack(slot % 2 == 0 ? ModItems.CORRUPTION_CRYSTAL.get() : ModItems.CORRUPTED_BRASS.get()));
            List<Entity> joined = tickCatching(level, offering, RitualRules.DEFAULT_TICKS + RitualRules.AFTER_STRIKE + 1);
            assertTrue(joined.stream().anyMatch(entity -> entity instanceof ItemEntity drop && drop.getItem().is(ModItems.ANCIENT_CORRUPTION_CRYSTAL.get())));
            clear(level, offering);
        });
    }

    @Test void itHoldsOneCreatureAndPushesAnyOtherOff(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarOfferingBlockEntity offering = raise(level, 44_176);
            tick(level, offering, AltarRules.CHECK_TICKS);
            Pig first = EntityType.PIG.create(level), second = EntityType.PIG.create(level);
            first.moveTo(offering.top().x + .2, offering.top().y, offering.top().z);
            second.moveTo(offering.top().x - .3, offering.top().y, offering.top().z + .1);
            offering.keep(List.of(first, second));
            assertSame(first, offering.bound());
            assertEquals(offering.top().x, first.getX(), 1e-6, "the first is held at the middle");
            assertNotEquals(offering.top().x, second.getX(), 1e-6, "the second is not");
            assertTrue(second.getDeltaMovement().x < 0, "it is pushed off, away from the middle");
            second.moveTo(offering.top());
            second.setDeltaMovement(Vec3.ZERO);
            offering.keep(List.of(first, second));
            assertSame(first, offering.bound(), "it still holds the first");
            assertTrue(second.getDeltaMovement().horizontalDistanceSqr() > 0, "and pushes the second off, even from the very middle");
            assertEquals(AltarState.FORMED, offering.use(level));
            assertNull(offering.bound(), "let go, it holds none");
            clear(level, offering);
        });
    }
}
