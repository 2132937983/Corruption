package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.common.block.CorruptionBlock;
import com.miophas.corruption.common.entity.Andromalius;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.miophas.corruption.common.entity.SpellProjectile;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.infection.CorruptionConversions;
import com.miophas.corruption.common.infection.ServerInfectionWorld;
import com.miophas.corruption.common.registry.GeneratedCorruption;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The Arcana of Corruption's new arts on a real server: the dormant corruption, which is the corruption in every way
 * but that it never spreads; a blight that leaves nothing else; quelling the corruption already there; remnants raised to
 * serve the caster for a while; a prey marked for the corruption's creatures, its bosses among them, to hunt; a truce
 * that has them spare the caster; and a bolt as big as its spell says, striking the further off. The test world is flat,
 * its surface at y -60; what each test sets down it takes away again.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class CorruptionArcanaServerTest {
    private static FakePlayer caster(ServerLevel level, int x, String name) {
        TestWorld.load(level, x - 48, x + 64, 3000 - 32, 3000 + 32);
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.moveTo(x + .5, -60, 3000.5, 0, 0);
        player.setGameMode(GameType.SURVIVAL);
        player.removeAllEffects();
        return player;
    }
    private static Pig pig(ServerLevel level, double x, double z) {
        Pig pig = EntityType.PIG.create(level);
        pig.moveTo(x, -60, z, 0, 0);
        pig.setNoAi(true);
        level.addFreshEntity(pig);
        return pig;
    }
    private static CorruptedRemnant remnant(ServerLevel level, double x, double z) {
        CorruptedRemnant remnant = CorruptedRemnant.raised(level, EntityType.ZOMBIE, new Vec3(x, -60, z), 0);
        level.addFreshEntity(remnant);
        return remnant;
    }
    private static List<Entity> catching(Runnable cast) {
        List<Entity> joined = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> joined.add(event.getEntity());
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try { cast.run(); }
        finally { NeoForge.EVENT_BUS.unregister(catcher); }
        return joined;
    }
    private static void clear(ServerLevel level, BlockPos from, BlockPos to) {
        for (BlockPos at : BlockPos.betweenClosed(from, to)) level.setBlock(at, Blocks.AIR.defaultBlockState(), 3);
    }
    private static BlockState dormant() { return ModBlocks.DEACTIVATED_CORRUPTION.get().defaultBlockState(); }

    @Test void theDormantCorruptionIsTheCorruptionButNeverSpreads(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            FakePlayer player = caster(level, 70_000, "dormant_caster");
            BlockPos still = new BlockPos(70_004, -60, 3004), live = new BlockPos(70_004, -60, 2996);
            Pig pig = pig(level, still.getX() + .5, still.getZ() + .5);
            try {
                assertTrue(CorruptionConversions.isCorrupted(dormant()), "corrupted ground, wherever that matters");
                assertFalse(CorruptionConversions.isSource(dormant()), "but no source of the infection");
                assertTrue(CorruptionConversions.isSource(ModBlocks.CORRUPTION.get().defaultBlockState()), "as the corruption is");
                assertFalse(CorruptionConversions.canConvert(dormant()), "and nothing turns it into anything else");
                assertEquals(ModBlocks.CORRUPTION.get().defaultDestroyTime(), ModBlocks.DEACTIVATED_CORRUPTION.get().defaultDestroyTime(), "made the same");
                assertEquals(ModBlocks.CORRUPTION.get().defaultBlockState().getSoundType(), dormant().getSoundType(), "and sounds the same");
                level.setBlock(still, dormant(), 3);
                level.setBlock(still.above(), Blocks.STONE.defaultBlockState(), 3);
                level.setBlock(live, ModBlocks.CORRUPTION.get().defaultBlockState(), 3);
                level.setBlock(live.above(), Blocks.STONE.defaultBlockState(), 3);
                var frontier = new ArrayList<BlockPos>();
                ServerInfectionWorld world = new ServerInfectionWorld(level);
                for (ChunkPos chunk : List.of(new ChunkPos(still), new ChunkPos(live))) {
                    int cursor = 0;
                    for (int pass = 0; pass < 64; pass++) {
                        var scan = world.scanSources(chunk, cursor, 1 << 20);
                        frontier.addAll(scan.sources());
                        cursor = scan.nextCursor();
                        if (scan.complete()) break;
                    }
                }
                assertFalse(frontier.contains(still), "the dormant corruption gives the infection no work, stone beside it or not");
                assertTrue(frontier.contains(live), "where the corruption beside the same stone does");
                pig.setOnGround(true);
                float health = pig.getHealth();
                ModBlocks.DEACTIVATED_CORRUPTION.get().stepOn(level, still, dormant(), pig);
                assertTrue(pig.getHealth() < health, "it hurts what stands on it, as the corruption does");
                assertEquals(health - pig.getMaxHealth() * CorruptionBlock.CONTACT_DAMAGE_FRACTION, pig.getHealth(), 1e-3, "as much");
            } finally {
                clear(level, still, still.above());
                clear(level, live, live.above());
                pig.discard();
            }
        });
    }

    @Test void aDormantBlightLeavesOnlyCorruptionThatNeverSpreads(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 70_400, "blight_caster");
            BlockPos low = new BlockPos(70_406, -60, 2998), high = new BlockPos(70_410, -58, 3002), middle = new BlockPos(70_408, -59, 3000);
            try {
                for (BlockPos at : BlockPos.betweenClosed(low, high)) level.setBlock(at, Blocks.STONE.defaultBlockState(), 3);
                BlockPos slab = middle.offset(1, 0, 0), ore = middle.offset(-1, 0, 0);
                level.setBlock(slab, Blocks.STONE_SLAB.defaultBlockState(), 3);
                level.setBlock(ore, Blocks.IRON_ORE.defaultBlockState(), 3);
                ArcaneCasting.world().perform(player, new ArcaneCasting.Origin(Vec3.atCenterOf(middle), Vec3.atCenterOf(middle), Vec3.ZERO),
                        new SpellAction.Corrupt(2, 1, true));
                int blighted = 0;
                for (BlockPos at : BlockPos.betweenClosed(low, high)) {
                    BlockState state = level.getBlockState(at);
                    assertFalse(state.is(ModBlocks.MALICE.get()) || state.is(ModBlocks.CORRUPTION.get()), "never malice nor the corruption that spreads, at " + at);
                    assertFalse(CorruptionConversions.isSource(state), "nothing that spreads at all, at " + at);
                    if (state.is(ModBlocks.DEACTIVATED_CORRUPTION.get())) blighted++;
                }
                assertTrue(blighted >= 20, "the stone round it blighted: " + blighted);
                assertTrue(level.getBlockState(middle).is(ModBlocks.DEACTIVATED_CORRUPTION.get()));
                assertTrue(level.getBlockState(slab).is(Blocks.STONE_SLAB), "what is not a whole block is left");
                assertTrue(level.getBlockState(ore).is(Blocks.IRON_ORE), "and ores");
                assertTrue(level.getBlockState(high.above()).isAir(), "and nothing grows on it");
            } finally {
                clear(level, low, high.above());
            }
        });
    }

    @Test void quellingStillsTheCorruptionThatSpreads(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 70_800, "quell_caster");
            BlockPos at = new BlockPos(70_804, -60, 3000);
            BlockState taken = GeneratedCorruption.convert(Blocks.STONE.defaultBlockState()), slab = GeneratedCorruption.convert(Blocks.STONE_SLAB.defaultBlockState());
            assertNotNull(taken);
            List<BlockPos> row = List.of(at, at.east(), at.east(2), at.east(3), at.east(4));
            try {
                level.setBlock(row.get(0), ModBlocks.CORRUPTION.get().defaultBlockState(), 3);
                level.setBlock(row.get(1), ModBlocks.MALICE.get().defaultBlockState(), 3);
                level.setBlock(row.get(2), taken, 3);
                if (slab != null) level.setBlock(row.get(3), slab, 3);
                level.setBlock(row.get(4), ModBlocks.ARCUNUM.get().defaultBlockState(), 3);
                ArcaneCasting.world().perform(player, new ArcaneCasting.Origin(Vec3.atCenterOf(at.east(2)), Vec3.atCenterOf(at), Vec3.ZERO), new SpellAction.Quell(4));
                for (int index = 0; index < 3; index++)
                    assertTrue(level.getBlockState(row.get(index)).is(ModBlocks.DEACTIVATED_CORRUPTION.get()), "stilled: " + index);
                if (slab != null) assertEquals(slab, level.getBlockState(row.get(3)), "a corrupted slab is no whole block, and is left");
                assertTrue(level.getBlockState(row.get(4)).is(ModBlocks.ARCUNUM.get()), "the corruption's ores are left");
            } finally {
                row.forEach(place -> level.setBlock(place, Blocks.AIR.defaultBlockState(), 3));
            }
        });
    }

    @Test void raisedRemnantsServeTheCasterAndCrumbleInTime(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 71_200, "host_caster");
            List<Entity> joined = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player),
                    new SpellAction.RaiseRemnants(4, 3, List.of("minecraft:zombie", "minecraft:wither"), 100)));
            List<CorruptedRemnant> raised = joined.stream().filter(CorruptedRemnant.class::isInstance).map(CorruptedRemnant.class::cast).toList();
            try {
                assertEquals(2, raised.size(), "a boss's likeness is passed over: two of the four");
                for (CorruptedRemnant remnant : raised) {
                    assertEquals(player.getUUID(), SpellSummons.summoner(remnant), "the caster's");
                    assertEquals(level.getGameTime() + 100, remnant.crumblesAt(), "for five seconds");
                    assertTrue(remnant.distanceTo(player) < 5, "round the caster");
                    remnant.setTarget(player);
                    assertNull(remnant.getTarget(), "never turning on the caster");
                }
                CorruptedRemnant first = raised.get(0);
                // The test world's clock stands still: it is moved on past their time by hand.
                var clock = (net.minecraft.world.level.storage.ServerLevelData) level.getLevelData();
                clock.setGameTime(level.getGameTime() + 101);
                first.tick();
                assertTrue(first.isRemoved(), "their time up, they crumble away");
            } finally {
                joined.forEach(Entity::discard);
            }
        });
    }

    @Test void theMarkSendsTheCorruptionItsBossesAmongItAfterItsPrey(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 71_600, "mark_caster");
            double x = player.getX(), z = player.getZ();
            Pig prey = pig(level, x + 20, z);
            CorruptedRemnant byPrey = remnant(level, x + 23, z), byCaster = remnant(level, x - 5, z), far = remnant(level, x + 60, z);
            Andromalius serpent = ModEntities.ANDROMALIUS.get().create(level);
            serpent.moveTo(x + 26, -60, z, 0, 0);
            level.addFreshEntity(serpent);
            try {
                Vec3 struck = prey.getBoundingBox().getCenter();
                ArcaneCasting.world().perform(player, new ArcaneCasting.Origin(struck, struck, Vec3.ZERO, prey), new SpellAction.MarkPrey(24, 200));
                assertTrue(prey.hasEffect(MobEffects.GLOWING), "the prey glows");
                for (var hunter : List.of(byPrey, byCaster, serpent)) {
                    assertSame(prey, hunter.getTarget(), hunter + " hunts it");
                    assertTrue(PreyMarks.hunts(hunter, prey));
                }
                assertFalse(PreyMarks.hunts(far, prey), "what is out of reach of both goes its own way");
                byPrey.setTarget(player);
                assertSame(prey, byPrey.getTarget(), "nothing turns a hunter from its prey");
                serpent.setTarget(null);
                assertSame(prey, serpent.getTarget(), "not even a boss");
                assertTrue(prey.hurt(level.damageSources().genericKill(), Float.MAX_VALUE));
                assertFalse(PreyMarks.hunts(byPrey, prey), "the prey fallen, the hunt is over");
            } finally {
                for (Entity entity : List.of(prey, byPrey, byCaster, far, serpent)) entity.discard();
            }
        });
    }

    @Test void aMarkedCreatureOfTheCorruptionIsNoLongerItsHuntersAlly(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 72_000, "feud_caster");
            double x = player.getX(), z = player.getZ();
            CorruptedRemnant prey = remnant(level, x + 8, z), hunter = remnant(level, x + 10, z), other = remnant(level, x + 40, z);
            try {
                assertTrue(hunter.isAlliedTo(prey), "allies before");
                Vec3 struck = prey.getBoundingBox().getCenter();
                ArcaneCasting.world().perform(player, new ArcaneCasting.Origin(struck, struck, Vec3.ZERO, prey), new SpellAction.MarkPrey(12, 200));
                assertFalse(hunter.isAlliedTo(prey), "no ally of its prey");
                assertFalse(prey.isAlliedTo(hunter), "nor its prey of it");
                assertTrue(other.isAlliedTo(prey), "but the rest of the corruption are allies still");
                float health = prey.getHealth();
                assertTrue(prey.hurt(level.damageSources().mobAttack(hunter), 4), "and the hunter's blows land");
                assertTrue(prey.getHealth() < health);
            } finally {
                for (Entity entity : List.of(prey, hunter, other)) entity.discard();
            }
        });
    }

    @Test void theMarkFallsOnWhatItsBoltStrikesAndABiggerBoltStrikesFurtherOff(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 72_400, "bolt_caster");
            double x = player.getX(), z = player.getZ();
            // A pig a block and a half to the side of the bolt's line: its box is a block and five hundredths off the line.
            Pig small = pig(level, x + 1.5, z + 8), big = pig(level, x + 21.5, z + 8);
            try {
                assertFalse(fly(level, player, new Vec3(x, -59.5, z), SpellAction.LaunchBolt.DEFAULT_SIZE), "a bolt of the usual size passes it by");
                assertFalse(small.hasEffect(MobEffects.GLOWING));
                assertTrue(fly(level, player, new Vec3(x + 20, -59.5, z), 1.5), "three times the size, it strikes it");
                assertTrue(big.hasEffect(MobEffects.GLOWING), "and the mark it carried fell on what it struck");
            } finally {
                small.discard();
                big.discard();
            }
        });
    }

    /** Flies a bolt of this size carrying a mark from here along +Z, a tick at a time, until it is spent; whether it struck a creature. */
    private static boolean fly(ServerLevel level, FakePlayer caster, Vec3 from, double size) {
        SpellProjectile bolt = ModEntities.SPELL_BOLT.get().create(level);
        bolt.flying(new SpellAction.LaunchBolt("corruption:spell_bolt", 1, 0, List.of(new SpellAction.MarkPrey(4, 100)), "", List.of(), List.of(), 1, 0,
                SpellAction.Source.CASTER, 12, -1, size));
        assertEquals(size, bolt.getBbWidth(), 1e-6, "its box as big as it is");
        bolt.setOwner(caster);
        bolt.setPos(from);
        bolt.shoot(0, 0, 1, 1, 0);
        level.addFreshEntity(bolt);
        boolean[] struck = {false};
        for (int tick = 0; tick < 30 && !bolt.isRemoved(); tick++) bolt.tick();
        struck[0] = bolt.isRemoved() && bolt.getZ() < from.z + 20;
        bolt.discard();
        return struck[0];
    }

    @Test void aTruceSparesTheCasterButNoBossSwearsIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 72_800, "truce_caster");
            double x = player.getX(), z = player.getZ();
            CorruptedRemnant remnant = remnant(level, x + 4, z);
            Andromalius serpent = ModEntities.ANDROMALIUS.get().create(level);
            serpent.moveTo(x - 6, -60, z, 0, 0);
            level.addFreshEntity(serpent);
            try {
                remnant.setTarget(player);
                assertSame(player, remnant.getTarget());
                ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), new SpellAction.Truce(16, 200));
                assertNull(remnant.getTarget(), "it gives the caster up");
                remnant.setTarget(player);
                assertNull(remnant.getTarget(), "and will not take them again");
                assertTrue(PreyMarks.spares(remnant, player));
                serpent.setTarget(player);
                assertSame(player, serpent.getTarget(), "a boss swears to no one");
            } finally {
                remnant.discard();
                serpent.discard();
            }
        });
    }
}
