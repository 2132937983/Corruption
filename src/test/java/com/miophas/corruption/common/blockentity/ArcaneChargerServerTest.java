package com.miophas.corruption.common.blockentity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import com.miophas.corruption.common.energy.EnergyIntake;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import com.mojang.authlib.GameProfile;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The arcane charger on a real server: a tool set on it is filled from its store, a step a tick; a tool's charge
 * never leaves it, so a wand part full is charged rather than emptied; batteries are poured in by hand.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class ArcaneChargerServerTest {
    private static ArcaneChargerBlockEntity place(ServerLevel level, int x) {
        BlockPos pos = new BlockPos(x, -50, 3000);
        TestWorld.load(level, x, x, 3000, 3000);
        level.setBlock(pos, ModBlocks.CORRUPTION_ARCANE_CHARGER.get().defaultBlockState(), 3);
        return (ArcaneChargerBlockEntity) level.getBlockEntity(pos);
    }
    private static CorruptionEnergyStorage store(ServerLevel level, ArcaneChargerBlockEntity charger) {
        return CorruptionEnergyApi.storage(level, charger.getBlockPos(), Direction.UP);
    }
    private static ItemStack wand(long energy) { return CorruptionEnergyTool.charged(new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get()), energy); }
    private static FakePlayer player(ServerLevel level, String name) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.getInventory().clearContent();
        return player;
    }
    private static BlockHitResult hit(ArcaneChargerBlockEntity charger) {
        BlockPos pos = charger.getBlockPos();
        return new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
    }
    private static void tick(ServerLevel level, ArcaneChargerBlockEntity charger, int ticks) {
        for (int tick = 0; tick < ticks; tick++) ArcaneChargerBlockEntity.serverTick(level, charger.getBlockPos(), charger.getBlockState(), charger);
    }

    @Test void aToolsChargeNeverLeavesIt() {
        ItemStack wand = wand(5_000);
        assertFalse(CorruptionEnergyApi.carriesEnergy(wand), "a wand is no battery: it has nothing to give a store");
        assertFalse(EnergyIntake.powers(wand), "nor is it fuel for a machine");
        CorruptionEnergyStorage store = CorruptionEnergyApi.storage(wand);
        assertFalse(store.canExtract());
        assertEquals(0, store.extract(1_000, false));
        assertEquals(5_000, CorruptionEnergyTool.held(wand), "nothing came out");
        assertEquals(1_000, store.insert(1_000, false), "but it takes energy in");
        assertEquals(6_000, CorruptionEnergyTool.held(wand));
        assertTrue(CorruptionEnergyApi.carriesEnergy(CorruptionEnergyCarrier.charged(new ItemStack(ModItems.CORRUPTION_CANDLE.get()), 10_000)), "a candle is a battery");
    }

    @Test void aWandPartFullIsSetOnAndFilledAStepATick(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneChargerBlockEntity charger = place(level, 48_000);
            FakePlayer player = player(level, "charger_user");
            try {
                assertEquals(30_000, store(level, charger).insert(30_000, false));
                player.setItemInHand(InteractionHand.MAIN_HAND, wand(5_000));
                BlockState state = charger.getBlockState();
                assertEquals(ItemInteractionResult.CONSUME, state.useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit(charger)));
                assertTrue(player.getMainHandItem().isEmpty(), "the wand is set on");
                assertEquals(5_000, CorruptionEnergyTool.held(charger.charge()), "not emptied into the charger");
                assertEquals(30_000, charger.energy(), "and the store took nothing from it");
                assertTrue(charger.charging());
                tick(level, charger, 5);
                assertEquals(10_000, CorruptionEnergyTool.held(charger.charge()), "a KCE a tick");
                tick(level, charger, 20);
                assertEquals(20_000, CorruptionEnergyTool.held(charger.charge()), "full, and no fuller");
                assertEquals(15_000, charger.energy(), "the store paid for it, and no more");
                assertFalse(charger.charging());
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
                assertEquals(ItemInteractionResult.CONSUME, state.useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit(charger)),
                        "anything else in hand takes the wand back");
                assertTrue(charger.isEmpty());
                assertTrue(player.getInventory().contains(wand(20_000)), "given back full");
            } finally {
                level.removeBlock(charger.getBlockPos(), false);
                player.getInventory().clearContent();
            }
        });
    }

    @Test void itsStoreIsFilledFromOutsideAndNeverDrained(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneChargerBlockEntity charger = place(level, 48_016);
            FakePlayer player = player(level, "charger_feeder");
            try {
                CorruptionEnergyStorage store = store(level, charger);
                assertFalse(store.canExtract());
                assertEquals(ArcaneChargerBlockEntity.CAPACITY, store.getCapacity());
                player.setItemInHand(InteractionHand.MAIN_HAND, CorruptionEnergyCarrier.charged(new ItemStack(ModItems.CORRUPTION_CANDLE.get(), 2), 10_000));
                BlockState state = charger.getBlockState();
                state.useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit(charger));
                assertEquals(10_000, charger.energy(), "a candle poured in by hand, without sneaking");
                assertEquals(1, player.getMainHandItem().getCount(), "one of the two");
                assertEquals(0, store.extract(5_000, false), "nothing is taken back out");
                assertEquals(10_000, charger.energy());
                assertFalse(charger.charging(), "with nothing on it, it charges nothing");

                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE));
                assertEquals(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION, state.useItemOn(player.getMainHandItem(), level, player, InteractionHand.MAIN_HAND, hit(charger)));
                assertEquals(InteractionResult.PASS, state.useWithoutItem(level, player, hit(charger)), "a block in hand is left to be set against it");
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                assertEquals(InteractionResult.CONSUME, state.useWithoutItem(level, player, hit(charger)), "empty-handed, it says how full its store is");
            } finally {
                level.removeBlock(charger.getBlockPos(), false);
                player.getInventory().clearContent();
            }
        });
    }

    @Test void itsRingOfSparksShowsHowFullItsStoreIs(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneChargerBlockEntity charger = place(level, 48_032);
            try {
                assertEquals(0, charger.sparks(), "an empty store shows nothing");
                store(level, charger).insert(1, false);
                assertEquals(1, charger.sparks(), "any energy at all shows a spark");
                store(level, charger).insert(ArcaneChargerBlockEntity.CAPACITY / 2 - 1, false);
                assertEquals(ArcaneChargerBlockEntity.SPARKS / 2, charger.sparks(), "half full, half of them");
                store(level, charger).insert(ArcaneChargerBlockEntity.CAPACITY, false);
                assertEquals(ArcaneChargerBlockEntity.SPARKS, charger.sparks());
            } finally {
                level.removeBlock(charger.getBlockPos(), false);
            }
        });
        for (int sparks = 1; sparks <= ArcaneChargerBlockEntity.SPARKS; sparks++)
            for (int spark = 0; spark < sparks; spark++) {
                double[] at = ArcaneChargerBlockEntity.ringPoint(spark, sparks, 37, 11);
                assertEquals(ArcaneChargerBlockEntity.RING_RADIUS, Math.hypot(at[0] - .5, at[2] - .5), 1e-9, "round the item");
                assertEquals(ArcaneChargerBlockEntity.ITEM_HEIGHT, at[1], .051, "at its height, rippling a little");
            }
    }

    @Test void aSparkIsLightNotATile() throws Exception {
        BufferedImage spark;
        try (InputStream in = ArcaneChargerServerTest.class.getResourceAsStream("/assets/corruption/textures/particle/corruption_spark_particle.png")) {
            assertNotNull(in);
            spark = ImageIO.read(in);
        }
        for (int[] corner : new int[][]{{0, 0}, {15, 0}, {0, 15}, {15, 15}}) assertEquals(0, spark.getRGB(corner[0], corner[1]) >>> 24, "clear at its corners");
        assertEquals(255, spark.getRGB(7, 7) >>> 24, "bright at its heart");
    }
}
