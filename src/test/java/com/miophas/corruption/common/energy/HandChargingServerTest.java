package com.miophas.corruption.common.energy;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyTool;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModItems;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Charging a tool by hand: a wand in the main hand and Corruption Candles in the off hand, and using the wand
 * pours one candle into it, as much as it has room for; the candle keeps the rest, or burns away once spent. A full
 * wand is cast with as ever.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class HandChargingServerTest {
    private static final long KCE = CorruptionEnergyApi.kilo(1);

    private static FakePlayer holding(ServerLevel level, String name, ItemStack main, ItemStack off) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.setGameMode(GameType.SURVIVAL);
        player.getInventory().clearContent();
        player.setItemInHand(InteractionHand.MAIN_HAND, main);
        player.setItemInHand(InteractionHand.OFF_HAND, off);
        return player;
    }
    private static ItemStack candles(int count, long each) {
        return CorruptionEnergyCarrier.charged(new ItemStack(ModItems.CORRUPTION_CANDLE.get(), count), each);
    }
    private static PlayerInteractEvent.RightClickItem use(FakePlayer player, InteractionHand hand) {
        return NeoForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickItem(player, hand));
    }

    @Test void usingAWandWithCandlesInTheOtherHandChargesItACandleAtATime(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer player = holding(level, "hand_charger", new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get()), candles(2, 10 * KCE));
            assertTrue(EnergyPouring.chargeable(player.getMainHandItem(), player.getOffhandItem()));

            PlayerInteractEvent.RightClickItem first = use(player, InteractionHand.MAIN_HAND);
            assertTrue(first.isCanceled(), "charging comes before the wand's cast");
            assertTrue(first.getCancellationResult().consumesAction(), "and the use is taken up by it");
            assertEquals(10 * KCE, CorruptionEnergyTool.held(player.getMainHandItem()), "one candle's worth");
            assertEquals(1, player.getOffhandItem().getCount(), "and that candle burnt away");

            use(player, InteractionHand.MAIN_HAND);
            assertEquals(20 * KCE, CorruptionEnergyTool.held(player.getMainHandItem()), "the next fills it");
            assertTrue(player.getOffhandItem().isEmpty(), "and is gone too");

            player.setItemInHand(InteractionHand.OFF_HAND, candles(1, 10 * KCE));
            assertFalse(EnergyPouring.chargeable(player.getMainHandItem(), player.getOffhandItem()), "a full wand is not charged");
            assertFalse(use(player, InteractionHand.MAIN_HAND).isCanceled(), "it is cast with as ever");
            assertEquals(10 * KCE, CorruptionEnergyCarrier.carried(player.getOffhandItem()), "and the candle is left alone");
        });
    }

    @Test void aWandWithLittleRoomTakesWhatFitsAndTheCandleKeepsTheRest(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ItemStack wand = CorruptionEnergyTool.charged(new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get()), 15 * KCE);
            FakePlayer player = holding(level, "hand_partial", wand, candles(1, 10 * KCE));
            assertEquals(5 * KCE, EnergyPouring.chargeHeld(player));
            assertEquals(20 * KCE, CorruptionEnergyTool.held(player.getMainHandItem()));
            assertEquals(1, player.getOffhandItem().getCount(), "the candle is not spent");
            assertEquals(5 * KCE, CorruptionEnergyCarrier.carried(player.getOffhandItem()), "it keeps what did not fit");

            ItemStack advanced = new ItemStack(ModItems.ADVANCED_CORRUPTION_ARCANE_STICK.get());
            FakePlayer second = holding(level, "hand_advanced", advanced, candles(3, 10 * KCE));
            for (int use = 0; use < 3; use++) EnergyPouring.chargeHeld(second);
            assertEquals(30 * KCE, CorruptionEnergyTool.held(second.getMainHandItem()), "any tool, however large");
        });
    }

    @Test void onlyAToolInTheMainHandFromABatteryInTheOtherIsCharged(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer offhand = holding(level, "hand_offhand", new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get()), candles(1, 10 * KCE));
            assertFalse(use(offhand, InteractionHand.OFF_HAND).isCanceled(), "using the candle hand does nothing of the kind");
            assertEquals(0, CorruptionEnergyTool.held(offhand.getMainHandItem()));

            ItemStack full = CorruptionEnergyTool.charged(new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get()), 20 * KCE);
            FakePlayer twoWands = holding(level, "hand_two_wands", new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get()), full);
            assertFalse(EnergyPouring.chargeable(twoWands.getMainHandItem(), twoWands.getOffhandItem()), "a tool's charge never comes out of it");
            assertEquals(0, EnergyPouring.chargeHeld(twoWands));
            assertEquals(20 * KCE, CorruptionEnergyTool.held(twoWands.getOffhandItem()));

            FakePlayer notATool = holding(level, "hand_not_tool", new ItemStack(Items.STICK), candles(1, 10 * KCE));
            assertFalse(EnergyPouring.chargeable(notATool.getMainHandItem(), notATool.getOffhandItem()), "a stick is no tool");
            FakePlayer spent = holding(level, "hand_spent", new ItemStack(ModItems.CORRUPTION_ARCANE_STICK.get()), new ItemStack(ModItems.CORRUPTION_CANDLE.get()));
            assertFalse(EnergyPouring.chargeable(spent.getMainHandItem(), spent.getOffhandItem()), "nor is an empty candle a battery");
        });
    }
}
