package com.miophas.corruption.common.cooking;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.common.block.BrassCauldronBlock;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModFluids;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.common.transmutation.ArcaneTransmuterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The brass cauldron on a real server, where its recipes, the fuel and carrier data maps and the tags are
 * loaded from the mod's data. Each test places its own, fills it through the capabilities, and ticks it by hand.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class BrassCauldronServerTest {
    private static BrassCauldronBlockEntity place(ServerLevel level, int x) {
        BlockPos pos = new BlockPos(x, -50, 3000);
        TestWorld.load(level, x, x, 3000, 3000);
        level.setBlock(pos, ModBlocks.BRASS_CAULDRON.get().defaultBlockState(), 3);
        BrassCauldronBlockEntity cauldron = (BrassCauldronBlockEntity) level.getBlockEntity(pos);
        assertNotNull(cauldron);
        return cauldron;
    }
    private static IFluidHandler tank(ServerLevel level, BrassCauldronBlockEntity cauldron) {
        IFluidHandler tank = level.getCapability(Capabilities.FluidHandler.BLOCK, cauldron.getBlockPos(), Direction.UP);
        assertNotNull(tank, "its tank is offered on every side");
        return tank;
    }
    private static CorruptionEnergyStorage energy(ServerLevel level, BrassCauldronBlockEntity cauldron) { return CorruptionEnergyApi.storage(level, cauldron.getBlockPos(), Direction.NORTH); }
    private static void tick(ServerLevel level, BrassCauldronBlockEntity cauldron, int ticks) {
        for (int tick = 0; tick < ticks; tick++) BrassCauldronBlockEntity.serverTick(level, cauldron.getBlockPos(), level.getBlockState(cauldron.getBlockPos()), cauldron);
    }

    @Test void aRedMushroomTenMbOfWaterAndTenCeCookMushroomStew(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BrassCauldronBlockEntity cauldron = place(level, 42_000);
            assertEquals(1000, tank(level, cauldron).fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE));
            assertEquals(10, energy(level, cauldron).insert(10, false));
            cauldron.setItem(3, new ItemStack(Items.RED_MUSHROOM));
            tick(level, cauldron, 50);
            assertTrue(level.getBlockState(cauldron.getBlockPos()).getValue(BrassCauldronBlock.LIT), "it bubbles while it cooks");
            assertEquals(5, cauldron.energy(), "half way, half the cost has been drawn");
            tick(level, cauldron, 50);
            assertTrue(cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).is(Items.MUSHROOM_STEW));
            assertTrue(cauldron.getItem(3).isEmpty(), "the mushroom is cooked away");
            assertEquals(990, cauldron.fluid().getAmount(), "and ten millibuckets of water with it");
            assertEquals(0, cauldron.energy());
            tick(level, cauldron, 1);
            assertFalse(level.getBlockState(cauldron.getBlockPos()).getValue(BrassCauldronBlock.LIT), "still, once there is nothing to cook");
            level.removeBlock(cauldron.getBlockPos(), false);
        });
    }

    @Test void twoRedstoneInOneSlotAndFlowingCorruptionMakeCorruptionPowderWithoutEnergy(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BrassCauldronBlockEntity cauldron = place(level, 42_016);
            tank(level, cauldron).fill(new FluidStack(ModFluids.FLOWING_CORRUPTION.get(), 100), IFluidHandler.FluidAction.EXECUTE);
            cauldron.setItem(0, new ItemStack(Items.REDSTONE, 1));
            cauldron.setItem(5, new ItemStack(Items.REDSTONE, 1));
            tick(level, cauldron, 120);
            assertTrue(cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).isEmpty(), "two redstone must lie in one slot");
            cauldron.setItem(5, ItemStack.EMPTY);
            cauldron.setItem(0, new ItemStack(Items.REDSTONE, 3));
            tick(level, cauldron, 100);
            assertTrue(cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).is(ModItems.CORRUPTION_DUST.get()), "no energy needed");
            assertEquals(1, cauldron.getItem(0).getCount(), "two of the three are used");
            assertEquals(90, cauldron.fluid().getAmount());
            assertEquals(0, cauldron.energy());
            level.removeBlock(cauldron.getBlockPos(), false);
        });
    }

    @Test void itWaitsForEnergyAndWhatIsLeftOverOrMissingStopsIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BrassCauldronBlockEntity cauldron = place(level, 42_032);
            cauldron.setItem(0, new ItemStack(Items.RED_MUSHROOM));
            tick(level, cauldron, 100);
            assertEquals(0, cauldron.progress(), "without water nothing cooks");
            tank(level, cauldron).fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
            tick(level, cauldron, 100);
            assertTrue(cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).isEmpty(), "nor without the energy it costs");
            assertTrue(cauldron.progress() > 0 && cauldron.progress() < 100, "it gets as far as the energy it has takes it");
            energy(level, cauldron).insert(10, false);
            tick(level, cauldron, 40);
            assertTrue(cauldron.energy() < 10, "with energy it goes on, drawing it");
            cauldron.setItem(1, new ItemStack(Items.STICK));
            tick(level, cauldron, 100);
            assertTrue(cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).isEmpty(), "a stick left over spoils the dish");
            assertEquals(10, cauldron.energy(), "and what had been drawn goes back");
            assertEquals(0, cauldron.progress());
            cauldron.setItem(1, ItemStack.EMPTY);
            tick(level, cauldron, 100);
            assertTrue(cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).is(Items.MUSHROOM_STEW));
            level.removeBlock(cauldron.getBlockPos(), false);
        });
    }

    @Test void bucketsFillItAndCandlesPowerIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BrassCauldronBlockEntity cauldron = place(level, 42_048);
            Player player = FakePlayerFactory.getMinecraft(level);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET));
            assertTrue(FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, level, cauldron.getBlockPos(), Direction.UP));
            assertEquals(1000, cauldron.fluid().getAmount());
            assertTrue(player.getMainHandItem().is(Items.BUCKET), "the bucket comes back empty");
            cauldron.setItem(BrassCauldronBlockEntity.ENERGY, CorruptionEnergyCarrier.charged(new ItemStack(ModItems.CORRUPTION_CANDLE.get()), 10_000));
            tick(level, cauldron, 10);
            assertEquals(10_000, cauldron.energy(), "a candle burns down into it as into the transmuter");
            assertTrue(cauldron.canPlaceItem(BrassCauldronBlockEntity.ENERGY, new ItemStack(ModItems.CORRUPTION_DUST.get())), "fuel too");
            assertFalse(cauldron.canPlaceItem(BrassCauldronBlockEntity.ENERGY, new ItemStack(Items.STICK)));
            assertFalse(energy(level, cauldron).canExtract(), "other mods may fill its store but not drain it");
            // What it holds is saved with it.
            CompoundTag saved = cauldron.saveWithoutMetadata(level.registryAccess());
            BrassCauldronBlockEntity copy = new BrassCauldronBlockEntity(cauldron.getBlockPos(), level.getBlockState(cauldron.getBlockPos()));
            copy.loadWithComponents(saved, level.registryAccess());
            assertEquals(1000, copy.fluid().getAmount());
            assertTrue(copy.fluid().is(Fluids.WATER));
            assertEquals(10_000, copy.energy());
            assertEquals(1000, cauldron.getUpdateTag(level.registryAccess()).getCompound("Tank").getCompound("Fluid").getInt("amount"), "players near it are told what its tank holds");
            level.removeBlock(cauldron.getBlockPos(), false);
        });
    }

    @Test void rottenFleshBecomesCorruptionRemainOnlyByTheRuneThatCorruptsAll(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BrassCauldronBlockEntity cauldron = place(level, 42_080);
            tank(level, cauldron).fill(new FluidStack(ModFluids.FLOWING_CORRUPTION.get(), 100), IFluidHandler.FluidAction.EXECUTE);
            energy(level, cauldron).insert(100, false);
            cauldron.setItem(2, new ItemStack(Items.ROTTEN_FLESH, 2));
            tick(level, cauldron, 100);
            assertTrue(cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).isEmpty(), "without the rune, nothing");
            ItemStack blank = new ItemStack(ModItems.PARCHMENT.get());
            assertFalse(cauldron.canPlaceItem(BrassCauldronBlockEntity.PARCHMENT, blank), "a blank parchment is no rune");
            assertFalse(cauldron.canPlaceItem(BrassCauldronBlockEntity.PARCHMENT, new ItemStack(Items.PAPER)));
            cauldron.setItem(BrassCauldronBlockEntity.PARCHMENT, ArcaneApi.inscribed(blank, ArcaneApi.key("corruption:baal")));
            tick(level, cauldron, 100);
            assertTrue(cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).isEmpty(), "nor with another");
            ItemStack rune = ArcaneApi.inscribed(blank, ArcaneApi.key("corruption:corruption"));
            assertTrue(cauldron.canPlaceItem(BrassCauldronBlockEntity.PARCHMENT, rune));
            cauldron.setItem(BrassCauldronBlockEntity.PARCHMENT, rune);
            tick(level, cauldron, 100);
            assertTrue(cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).is(ModItems.CORRUPTION_REMAIN.get()));
            assertEquals(1, cauldron.getItem(2).getCount(), "one flesh is used");
            assertEquals(90, cauldron.fluid().getAmount());
            assertEquals(90, cauldron.energy());
            assertEquals(ArcaneApi.key("corruption:corruption"), ArcaneApi.inscription(cauldron.getItem(BrassCauldronBlockEntity.PARCHMENT)), "and the rune is kept");
            tick(level, cauldron, 100);
            assertEquals(2, cauldron.getItem(BrassCauldronBlockEntity.OUTPUT).getCount(), "for the next batch too");
            level.removeBlock(cauldron.getBlockPos(), false);
        });
    }

    @Test void onlySneakingDoesABucketFillItAndPlayersNearItSeeWhatIsInIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BrassCauldronBlockEntity cauldron = place(level, 42_096);
            BlockPos pos = cauldron.getBlockPos();
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
            Player player = FakePlayerFactory.getMinecraft(level);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.FLOWING_CORRUPTION_BUCKET.get()));
            player.setShiftKeyDown(false);
            var state = level.getBlockState(pos);
            assertEquals(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION, state.useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit),
                    "used plainly, it opens, bucket or no");
            assertTrue(cauldron.fluid().isEmpty());
            PlayerInteractEvent.RightClickBlock plain = new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, pos, hit);
            BrassCauldronBlock.onSneakUse(plain);
            assertTrue(plain.getUseBlock().isDefault(), "and the game is left to do as it does");
            player.setShiftKeyDown(true);
            PlayerInteractEvent.RightClickBlock sneaking = new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, pos, hit);
            BrassCauldronBlock.onSneakUse(sneaking);
            assertTrue(sneaking.getUseBlock().isTrue(), "sneaking, the cauldron is asked");
            assertTrue(sneaking.getUseItem().isFalse(), "and the bucket does not pour into the world");
            assertEquals(ItemInteractionResult.CONSUME, state.useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit));
            assertTrue(cauldron.fluid().is(ModFluids.FLOWING_CORRUPTION.get()));
            assertEquals(1000, cauldron.fluid().getAmount());
            assertTrue(player.getMainHandItem().is(Items.BUCKET));
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
            PlayerInteractEvent.RightClickBlock stick = new PlayerInteractEvent.RightClickBlock(player, InteractionHand.MAIN_HAND, pos, hit);
            BrassCauldronBlock.onSneakUse(stick);
            assertTrue(stick.getUseBlock().isDefault(), "sneaking with anything else is as it always is");
            player.setShiftKeyDown(false);
            cauldron.setItem(4, new ItemStack(Items.RED_MUSHROOM, 3));
            var told = cauldron.getUpdateTag(level.registryAccess());
            assertEquals(1000, told.getCompound("Tank").getCompound("Fluid").getInt("amount"));
            assertEquals(1, told.getList("Items", 10).size(), "players near it are told of the mushroom floating in it");
            BrassCauldronBlockEntity seen = new BrassCauldronBlockEntity(pos, state);
            seen.loadWithComponents(told, level.registryAccess());
            assertTrue(seen.shownItems().get(4).is(Items.RED_MUSHROOM));
            assertEquals(1000, seen.fluid().getAmount());
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            level.removeBlock(pos, false);
        });
    }

    @Test void copperIsTransmutedIntoCorruptedBrassWhichTheCommonTagsName(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BlockPos pos = new BlockPos(42_064, -50, 3000);
            TestWorld.load(level, 42_064, 42_064, 3000, 3000);
            level.setBlock(pos, ModBlocks.CORRUPTION_ARCANE_TRANSMUTER.get().defaultBlockState(), 3);
            ArcaneTransmuterBlockEntity transmuter = (ArcaneTransmuterBlockEntity) level.getBlockEntity(pos);
            CorruptionEnergyApi.storage(level, pos, null).insert(5_000, false);
            transmuter.setItem(ArcaneTransmuterBlockEntity.INPUT, new ItemStack(Items.COPPER_INGOT));
            for (int tick = 0; tick < CorruptionEnergyApi.transmuteTicks(); tick++) ArcaneTransmuterBlockEntity.serverTick(level, pos, level.getBlockState(pos), transmuter);
            assertTrue(transmuter.getItem(ArcaneTransmuterBlockEntity.OUTPUT).is(ModItems.CORRUPTED_BRASS.get()));
            assertEquals(0, transmuter.energy(), "for 5 KCE");
            level.removeBlock(pos, false);

            ItemStack brass = new ItemStack(ModItems.CORRUPTED_BRASS.get());
            assertTrue(brass.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/corrupted_brass"))));
            assertTrue(brass.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots"))));
            assertTrue(new ItemStack(ModItems.CORRUPTED_BRASS_BLOCK.get()).is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/corrupted_brass"))));
            assertTrue(ModBlocks.CORRUPTED_BRASS_BLOCK.get().defaultBlockState().is(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks"))));
            assertTrue(ModBlocks.BRASS_CAULDRON.get().defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE));
            var recipes = level.getRecipeManager();
            assertTrue(recipes.byKey(ResourceLocation.fromNamespaceAndPath("corruption", "corrupted_brass_block")).isPresent(), "nine make a block");
            assertTrue(recipes.byKey(ResourceLocation.fromNamespaceAndPath("corruption", "corrupted_brass_from_block")).isPresent(), "and a block nine");
        });
    }
}
