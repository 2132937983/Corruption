package com.miophas.corruption.common.transmutation;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.energy.CorruptionEnergyAcceptance;
import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyCarrier;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.common.energy.EnergyPouring;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.item.BasicCorruptionEnergyItem;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The Corruption Candle on a real server, where the carrier data map, the c:candles tag and the recipes are
 * loaded from the mod's data: made in the transmuter, giving its energy all at once or bit by bit, burnt down
 * in the transmuter's fuel slot, poured in by hand, and offered to other mods as an item store.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class CorruptionCandleServerTest {
    private static ItemStack candles(int count, long each) { return CorruptionEnergyCarrier.charged(new ItemStack(ModItems.CORRUPTION_CANDLE.get(), count), each); }

    private static ArcaneTransmuterBlockEntity place(ServerLevel level, int x, long energy) {
        BlockPos pos = new BlockPos(x, -50, 3000);
        TestWorld.load(level, x, x, 3000, 3000);
        level.setBlock(pos, ModBlocks.CORRUPTION_ARCANE_TRANSMUTER.get().defaultBlockState(), 3);
        ArcaneTransmuterBlockEntity machine = (ArcaneTransmuterBlockEntity) level.getBlockEntity(pos);
        assertNotNull(machine);
        if (energy > 0) {
            CompoundTag tag = machine.saveWithoutMetadata(level.registryAccess());
            tag.putLong("Energy", energy);
            machine.loadWithComponents(tag, level.registryAccess());
        }
        return machine;
    }
    private static void tick(ServerLevel level, ArcaneTransmuterBlockEntity machine, int ticks) {
        for (int tick = 0; tick < ticks; tick++) ArcaneTransmuterBlockEntity.serverTick(level, machine.getBlockPos(), level.getBlockState(machine.getBlockPos()), machine);
    }

    @Test void aCandleAnd10KceBecomeACorruptionCandleCarrying10Kce(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_096, 25_000);
            machine.setItem(ArcaneTransmuterBlockEntity.INPUT, new ItemStack(Items.RED_CANDLE, 2));
            tick(level, machine, 200);
            ItemStack made = machine.getItem(ArcaneTransmuterBlockEntity.OUTPUT);
            assertTrue(made.is(ModItems.CORRUPTION_CANDLE.get()), "any candle in the tag will do");
            assertEquals(10_000, CorruptionEnergyCarrier.carried(made));
            assertEquals(15_000, machine.energy(), "the 10 KCE it cost went into the candle");
            tick(level, machine, 200);
            assertEquals(2, machine.getItem(ArcaneTransmuterBlockEntity.OUTPUT).getCount(), "candles made alike stack");
            assertTrue(machine.getItem(ArcaneTransmuterBlockEntity.INPUT).isEmpty());
            machine.setItem(ArcaneTransmuterBlockEntity.INPUT, candles(1, 10_000));
            tick(level, machine, 5);
            assertEquals(0, machine.progress(), "a corruption candle is not a candle to transmute again");
            level.removeBlock(machine.getBlockPos(), false);
        });
    }

    @Test void theFuelSlotLoadsACandlesWholeChargeAKceATickAndItBurnsAway(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_112, 0);
            assertTrue(machine.canPlaceItem(ArcaneTransmuterBlockEntity.FUEL, candles(1, 10_000)));
            assertFalse(machine.canPlaceItem(ArcaneTransmuterBlockEntity.FUEL, candles(1, 0)), "an empty candle has nothing to load");
            machine.setItem(ArcaneTransmuterBlockEntity.FUEL, candles(2, 10_000));
            tick(level, machine, 1);
            assertEquals(1_000, machine.energy());
            assertEquals(1, machine.getItem(ArcaneTransmuterBlockEntity.FUEL).getCount(), "one candle at a time");
            assertTrue(machine.burning());
            tick(level, machine, 9);
            assertEquals(10_000, machine.energy());
            tick(level, machine, 10);
            assertEquals(20_000, machine.energy());
            assertTrue(machine.getItem(ArcaneTransmuterBlockEntity.FUEL).isEmpty(), "spent candles burn away");
            level.removeBlock(machine.getBlockPos(), false);
        });
    }

    @Test void theCandleGivesItsEnergyAllAtOnceOrBitByBit(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BasicCorruptionEnergyItem item = ModItems.CORRUPTION_CANDLE.get();
            assertEquals(10_000, item.getCapacity(new ItemStack(item)));
            assertTrue(item.isConsumed(new ItemStack(item)));
            assertEquals(1_000, item.getStep(new ItemStack(item)), "a candle burns down a KCE at a time");
            ItemStack candle = item.charged(10_000);
            assertFalse(item.isBarVisible(candle), "full");
            assertEquals(1_000, item.extractStep(candle, true));
            assertEquals(10_000, item.getEnergy(candle), "simulating changes nothing");
            assertEquals(1_000, item.extractStep(candle, false));
            assertEquals(9_000, item.getEnergy(candle));
            assertTrue(item.isBarVisible(candle));
            assertEquals(Math.round(13 * .9F), item.getBarWidth(candle));
            assertEquals(9_000, item.extractAll(candle, false), "the rest, all at once");
            assertTrue(candle.isEmpty(), "and it burns away");
            ItemStack three = candles(3, 10_000);
            assertEquals(0, item.extractStep(three, false), "bit by bit, a single candle only");
            assertEquals(10_000, item.extractAll(three, false));
            assertEquals(2, three.getCount(), "all at once, one candle leaves the stack");
            assertEquals(10_000, item.getEnergy(three));
            assertEquals(1_000, CorruptionEnergyApi.extractStep(candles(1, 10_000), false), "the API asks the item itself");
            assertEquals(0, CorruptionEnergyApi.extractAll(new ItemStack(Items.STICK), false));
        });
    }

    @Test void aCandleHalfBurntDownKeepsWhatItHasLeft(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_160, 85_000);
            BlockPos pos = machine.getBlockPos();
            machine.setItem(ArcaneTransmuterBlockEntity.FUEL, candles(1, 10_000));
            tick(level, machine, 4);
            assertEquals(89_000, machine.energy());
            assertTrue(machine.getItem(ArcaneTransmuterBlockEntity.FUEL).isEmpty(), "the candle is inside, burning");
            assertEquals(6_000, CorruptionEnergyCarrier.carried(machine.burningItem()));
            assertEquals(5_000, CorruptionEnergyApi.storage(level, pos, null).insert(1_000_000, true), "the rest of the candle has its room kept for it");
            CompoundTag saved = machine.saveWithoutMetadata(level.registryAccess());
            ArcaneTransmuterBlockEntity copy = new ArcaneTransmuterBlockEntity(pos, level.getBlockState(pos));
            copy.loadWithComponents(saved, level.registryAccess());
            assertEquals(6_000, CorruptionEnergyCarrier.carried(copy.burningItem()), "saved with the block");
            level.removeBlock(pos, false);
            var dropped = level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new net.minecraft.world.phys.AABB(pos).inflate(2));
            assertTrue(dropped.stream().anyMatch(drop -> drop.getItem().is(ModItems.CORRUPTION_CANDLE.get()) && CorruptionEnergyCarrier.carried(drop.getItem()) == 6_000),
                    "broken, it drops the candle with what it has left");
            dropped.forEach(net.minecraft.world.entity.Entity::discard);
        });
    }

    @Test void aCandleWaitsUntilAllItCarriesFits(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_128, 95_000);
            machine.setItem(ArcaneTransmuterBlockEntity.FUEL, candles(1, 10_000));
            tick(level, machine, 20);
            assertEquals(95_000, machine.energy());
            assertEquals(10_000, CorruptionEnergyCarrier.carried(machine.getItem(ArcaneTransmuterBlockEntity.FUEL)), "nothing is taken from it");
            assertFalse(machine.canTakeItemThroughFace(ArcaneTransmuterBlockEntity.FUEL, machine.getItem(ArcaneTransmuterBlockEntity.FUEL), Direction.DOWN), "a hopper leaves it to load");
            // A half spent candle fits.
            machine.setItem(ArcaneTransmuterBlockEntity.FUEL, candles(1, 4_000));
            tick(level, machine, 4);
            assertEquals(99_000, machine.energy());
            level.removeBlock(machine.getBlockPos(), false);
        });
    }

    @Test void pouringByHandGivesWhatFitsAndTheCandleKeepsTheRest(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_144, 95_000);
            BlockPos pos = machine.getBlockPos();
            CorruptionEnergyStorage store = CorruptionEnergyApi.storage(level, pos, Direction.UP);
            Player player = FakePlayerFactory.getMinecraft(level);
            player.getInventory().clearContent();
            player.setItemInHand(InteractionHand.MAIN_HAND, candles(3, 10_000));
            assertEquals(5_000, EnergyPouring.pour(player, InteractionHand.MAIN_HAND, store, pos.getCenter()));
            assertEquals(100_000, machine.energy());
            assertEquals(2, player.getMainHandItem().getCount(), "one candle came off the stack to pour");
            assertEquals(10_000, CorruptionEnergyCarrier.carried(player.getMainHandItem()));
            int kept = player.getInventory().findSlotMatchingItem(candles(1, 5_000));
            assertTrue(kept >= 0, "the poured candle, with what did not fit, goes back to the player");
            assertEquals(0, EnergyPouring.pour(player, InteractionHand.MAIN_HAND, store, pos.getCenter()), "nothing goes into a full store");
            assertEquals(2, player.getMainHandItem().getCount());
            // Poured into a store with room, a single candle gives all it has and burns away.
            machine.setItem(ArcaneTransmuterBlockEntity.INPUT, new ItemStack(Items.WATER_BUCKET));
            tick(level, machine, 200);
            assertEquals(80_000, machine.energy());
            player.setItemInHand(InteractionHand.MAIN_HAND, candles(1, 10_000));
            assertEquals(10_000, EnergyPouring.pour(player, InteractionHand.MAIN_HAND, store, pos.getCenter()));
            assertTrue(player.getMainHandItem().isEmpty());
            assertEquals(90_000, machine.energy());
            player.getInventory().clearContent();
            level.removeBlock(pos, false);
        });
    }

    @Test void candlesOfferTheirEnergyToOtherModsAsAnItemStore(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            assertEquals(new CorruptionEnergyCarrier(10_000, true), CorruptionEnergyApi.carrier(new ItemStack(ModItems.CORRUPTION_CANDLE.get())), "the data map, as loaded from the mod's data");
            assertNull(CorruptionEnergyApi.storage(new ItemStack(Items.STICK)), "an item the data map does not name carries nothing");
            ItemStack candle = candles(1, 10_000);
            CorruptionEnergyStorage store = CorruptionEnergyApi.storage(candle);
            assertNotNull(store);
            assertEquals(10_000, store.getEnergy()); assertEquals(10_000, store.getCapacity());
            assertEquals(4_000, store.extract(4_000, false));
            assertEquals(6_000, CorruptionEnergyCarrier.carried(candle), "the store is the stack's own data");
            ItemStack drained = candles(1, 10_000);
            assertEquals(10_000, CorruptionEnergyApi.storage(drained).extract(1_000_000, false));
            assertTrue(drained.isEmpty(), "drained by another mod, a candle burns away too");
            CorruptionEnergyStorage several = CorruptionEnergyApi.storage(candles(3, 10_000));
            assertEquals(30_000, several.getEnergy());
            assertEquals(0, several.extract(1_000, false), "a stack of several moves nothing");
            ItemStack empty = new ItemStack(ModItems.CORRUPTION_CANDLE.get());
            assertFalse(CorruptionEnergyApi.carriesEnergy(empty));
            assertEquals(3_000, CorruptionEnergyApi.storage(empty).insert(3_000, false), "an add-on's charger could fill one");
            assertEquals(3_000, CorruptionEnergyCarrier.carried(empty));
            // What a candle carries is saved with it.
            ItemStack loaded = ItemStack.parseOptional(level.registryAccess(), (CompoundTag) candle.save(level.registryAccess()));
            assertEquals(6_000, CorruptionEnergyCarrier.carried(loaded));
            // Offered whole to a store too small for it, nothing moves and the stack is left alone.
            var small = CorruptionEnergyApi.buffer(5_000, Long.MAX_VALUE, Long.MAX_VALUE, () -> {});
            CorruptionEnergyApi.Imported refused = CorruptionEnergyApi.importFrom(candle, small, CorruptionEnergyAcceptance.WHOLE);
            assertEquals(0, refused.energy());
            assertEquals(6_000, CorruptionEnergyCarrier.carried(candle));
            assertEquals(0, small.getEnergy());
        });
    }
}
