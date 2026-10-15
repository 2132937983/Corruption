package com.miophas.corruption.common.infection;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.block.CorruptionBlock;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The infection turns ordinary blocks into static corrupted copies, which have no block entity: a machine of the
 * mod's caught by it would lose whatever it held and stop working. Every block of the mod's that keeps a block
 * entity (the corruption's own blocks aside, which the infection never converts) is in corruption:infection_immune,
 * so a machine added later and left out of the tag fails here.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class MachinesStayUncorruptedServerTest {
    @Test void noMachineOfTheModsIsEverCorrupted(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            assertTrue(CorruptionConversions.canConvert(Blocks.STONE.defaultBlockState()), "the infection does convert ordinary blocks");
            List<String> corruptible = new ArrayList<>();
            for (var holder : ModBlocks.BLOCKS.getEntries()) {
                Block block = holder.get();
                if (!(block instanceof EntityBlock) || block instanceof CorruptionBlock) continue;
                if (!block.defaultBlockState().is(CorruptionConversions.IMMUNE) || CorruptionConversions.canConvert(block.defaultBlockState()))
                    corruptible.add(holder.getId().toString());
            }
            assertEquals(List.of(), corruptible, "machines the infection would corrupt");
            for (Block machine : new Block[]{ModBlocks.CORRUPTION_ARCANE_PEDESTAL.get(), ModBlocks.CORRUPTION_ARCANE_CHARGER.get(), ModBlocks.CORRUPTION_BENCH.get()})
                assertFalse(CorruptionConversions.canConvert(machine.defaultBlockState()), machine + " stays as it is");
        });
    }
}
