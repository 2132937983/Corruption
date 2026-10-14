package com.miophas.corruption.common.transmutation;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.energy.CorruptionEnergyApi;
import com.miophas.corruption.api.energy.CorruptionEnergyStorage;
import com.miophas.corruption.api.energy.CorruptionFuel;
import com.miophas.corruption.common.block.ArcaneTransmuterBlock;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The transmuter on a real server, where the corruption_fuel data map and the recipes are loaded from the
 * mod's data. Each test places its own and ticks it by hand.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class ArcaneTransmuterServerTest {
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
    private static boolean lit(ServerLevel level, ArcaneTransmuterBlockEntity machine) { return level.getBlockState(machine.getBlockPos()).getValue(ArcaneTransmuterBlock.LIT); }

    @Test void corruptionPowderBurnsIntoATenthOfAKceInTenTicks(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_000, 0);
            machine.setItem(ArcaneTransmuterBlockEntity.FUEL, new ItemStack(ModItems.CORRUPTION_DUST.get(), 10));
            tick(level, machine, 1);
            assertEquals(10, machine.energy(), "paid out as it burns");
            assertEquals(9, machine.getItem(ArcaneTransmuterBlockEntity.FUEL).getCount());
            assertTrue(lit(level, machine));
            tick(level, machine, 9);
            assertEquals(100, machine.energy(), "one powder, 0.1 KCE, ten ticks");
            tick(level, machine, 90);
            assertEquals(1000, machine.energy());
            assertTrue(machine.getItem(ArcaneTransmuterBlockEntity.FUEL).isEmpty());
            tick(level, machine, 1);
            assertFalse(lit(level, machine), "out once there is nothing to do");
            level.removeBlock(machine.getBlockPos(), false);
        });
    }

    @Test void fuelWaitsUntilAllItsEnergyFits(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_016, 99_950);
            machine.setItem(ArcaneTransmuterBlockEntity.FUEL, new ItemStack(ModItems.CORRUPTION_DUST.get(), 3));
            tick(level, machine, 30);
            assertEquals(99_950, machine.energy());
            assertEquals(3, machine.getItem(ArcaneTransmuterBlockEntity.FUEL).getCount(), "nothing is burnt into a store that cannot hold it");
            assertFalse(machine.burning());
            assertFalse(machine.canPlaceItem(ArcaneTransmuterBlockEntity.FUEL, new ItemStack(Items.COAL)), "only what the data map names is fuel");
            level.removeBlock(machine.getBlockPos(), false);
        });
    }

    @Test void aWaterBucketAnd20KceBecomeFlowingCorruptionIn200Ticks(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_032, 25_000);
            machine.setItem(ArcaneTransmuterBlockEntity.INPUT, new ItemStack(Items.WATER_BUCKET));
            tick(level, machine, 100);
            assertEquals(100, machine.progress());
            assertEquals(15_000, machine.energy(), "half way, half the cost has been drawn");
            assertTrue(lit(level, machine));
            tick(level, machine, 100);
            assertEquals(5_000, machine.energy());
            assertTrue(machine.getItem(ArcaneTransmuterBlockEntity.INPUT).isEmpty(), "the water bucket is used up");
            assertTrue(machine.getItem(ArcaneTransmuterBlockEntity.OUTPUT).is(ModItems.FLOWING_CORRUPTION_BUCKET.get()));
            assertEquals(0, machine.progress());
            level.removeBlock(machine.getBlockPos(), false);
        });
    }

    @Test void itWaitsForEnergyAndGivesBackWhatAnAbandonedTransmutationDrew(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_048, 5_000);
            machine.setItem(ArcaneTransmuterBlockEntity.INPUT, new ItemStack(Items.WATER_BUCKET));
            tick(level, machine, 120);
            assertEquals(50, machine.progress(), "5 KCE buys a quarter of the way");
            assertEquals(0, machine.energy());
            assertFalse(lit(level, machine), "waiting is not working");
            machine.setItem(ArcaneTransmuterBlockEntity.FUEL, new ItemStack(ModItems.CORRUPTION_DUST.get(), 2));
            tick(level, machine, 20);
            assertTrue(machine.progress() > 50, "fuel lets it go on");
            machine.setItem(ArcaneTransmuterBlockEntity.INPUT, ItemStack.EMPTY);
            tick(level, machine, 1);
            assertEquals(0, machine.progress());
            assertEquals(5_200, machine.energy(), "taking the bucket out gives back everything it had drawn");
            level.removeBlock(machine.getBlockPos(), false);
        });
    }

    @Test void otherModsCanFillItsStoreThroughTheCapabilityButNotDrainIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_080, 0);
            BlockPos pos = machine.getBlockPos();
            for (Direction side : Direction.values()) assertNotNull(CorruptionEnergyApi.storage(level, pos, side), "offered on the " + side + " side");
            CorruptionEnergyStorage store = CorruptionEnergyApi.storage(level, pos, null);
            assertTrue(store.canInsert()); assertFalse(store.canExtract());
            assertEquals(30_000, store.insert(30_000, false));
            assertEquals(30_000, machine.energy(), "an add-on's generator fills the transmuter's own store");
            assertEquals(0, store.extract(1_000, false));
            assertEquals(70_000, store.insert(1_000_000, true), "never past its capacity");
            // A burning fuel's unpaid energy has its room kept for it.
            machine.setItem(ArcaneTransmuterBlockEntity.FUEL, new ItemStack(ModItems.CORRUPTION_DUST.get()));
            store.insert(69_900, false);
            tick(level, machine, 1);
            assertEquals(99_910, machine.energy());
            assertEquals(0, store.insert(1_000, false), "the fuel's other 90 CE are still owed");
            tick(level, machine, 9);
            assertEquals(100_000, machine.energy(), "and arrive");
            // What it holds is saved and loaded with the block.
            CompoundTag saved = machine.saveWithoutMetadata(level.registryAccess());
            ArcaneTransmuterBlockEntity copy = new ArcaneTransmuterBlockEntity(pos, level.getBlockState(pos));
            copy.loadWithComponents(saved, level.registryAccess());
            assertEquals(100_000, copy.energy());
            assertNull(CorruptionEnergyApi.storage(level, pos.above(), null), "a block with no store offers none");
            assertEquals(new CorruptionFuel(100, 10), CorruptionEnergyApi.fuel(new ItemStack(ModItems.CORRUPTION_DUST.get())), "the fuel data map, as loaded from the mod's data");
            assertNull(CorruptionEnergyApi.fuel(new ItemStack(Items.COAL)));
            level.removeBlock(pos, false);
        });
    }

    @Test void itOpensToHoppersAsAFurnaceDoes(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            ArcaneTransmuterBlockEntity machine = place(level, 40_064, 0);
            assertArrayEquals(new int[]{ArcaneTransmuterBlockEntity.INPUT}, machine.getSlotsForFace(Direction.UP));
            assertArrayEquals(new int[]{ArcaneTransmuterBlockEntity.FUEL}, machine.getSlotsForFace(Direction.EAST));
            assertTrue(machine.canPlaceItemThroughFace(ArcaneTransmuterBlockEntity.FUEL, new ItemStack(ModItems.CORRUPTION_DUST.get()), Direction.EAST));
            assertTrue(machine.canTakeItemThroughFace(ArcaneTransmuterBlockEntity.OUTPUT, new ItemStack(ModItems.FLOWING_CORRUPTION_BUCKET.get()), Direction.DOWN));
            assertFalse(machine.canTakeItemThroughFace(ArcaneTransmuterBlockEntity.FUEL, new ItemStack(ModItems.CORRUPTION_DUST.get()), Direction.DOWN), "fuel stays in");
            machine.setItem(ArcaneTransmuterBlockEntity.INPUT, new ItemStack(Items.WATER_BUCKET));
            level.removeBlock(machine.getBlockPos(), false);
            assertFalse(level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new net.minecraft.world.phys.AABB(machine.getBlockPos()).inflate(2)).isEmpty(), "broken, it spills what it held");
        });
    }
}
