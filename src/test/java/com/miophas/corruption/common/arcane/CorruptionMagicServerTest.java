package com.miophas.corruption.common.arcane;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.api.arcane.ArcaneString;
import com.miophas.corruption.api.arcane.WandSpells;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.miophas.corruption.common.entity.ReanimationEvents;
import com.miophas.corruption.common.entity.SpellBeamEntity;
import com.miophas.corruption.common.entity.SpellProjectile;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.registry.ModCreativeModeTabs;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
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
 * The corruption's flashier magic on a real server: volleys of bolts fanned from the caster or dropped from the
 * sky, the corruption's own lightning; where each string is found; and what the creative tabs hold. The test world
 * never brings new entities into sight, so what a spell brings forth is caught as it joins the world.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class CorruptionMagicServerTest {
    private static FakePlayer caster(ServerLevel level, int x, String name) {
        TestWorld.load(level, x - 16, x + 16, 3000 - 16, 3000 + 16);
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.moveTo(x + .5, -60, 3000.5, 0, 0);
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }
    private static List<Entity> catching(Runnable cast) {
        List<Entity> joined = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> joined.add(event.getEntity());
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try { cast.run(); }
        finally { NeoForge.EVENT_BUS.unregister(catcher); }
        joined.forEach(Entity::discard);
        return joined;
    }
    private static double degrees(Vec3 way) { return Math.toDegrees(Math.atan2(way.x, way.z)); }

    @Test void aVolleyFansOutAcrossItsArcFromTheCaster(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_000, "volley_caster");
            ArcaneCasting.Origin origin = ArcaneCasting.Origin.of(player);
            SpellAction.LaunchBolt volley = new SpellAction.LaunchBolt("corruption:spell_bolt", 1.5, 0, List.of(), "", SpellAction.LaunchBolt.CORRUPTION_TRAIL, List.of(),
                    5, 40, SpellAction.Source.CASTER, SpellAction.LaunchBolt.SKY_HEIGHT);
            List<SpellProjectile> bolts = catching(() -> ArcaneCasting.world().perform(player, origin, volley)).stream()
                    .filter(SpellProjectile.class::isInstance).map(SpellProjectile.class::cast).toList();
            assertEquals(5, bolts.size(), "five bolts at once");
            double gaze = degrees(origin.direction());
            List<Double> turned = bolts.stream().map(bolt -> degrees(bolt.getDeltaMovement()) - gaze).sorted().toList();
            assertEquals(-20, turned.get(0), .5, "the first at one edge of the arc");
            assertEquals(20, turned.get(4), .5, "the last at the other");
            assertEquals(0, turned.get(2), .5, "the middle one along the gaze");
            for (SpellProjectile bolt : bolts) {
                assertEquals(origin.launch().y, bolt.getY(), 1e-6, "all set off from where the caster launches bolts");
                assertSame(player, bolt.getOwner());
            }
        });
    }

    @Test void boltsFromTheSkyFallOnTheOrigin(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_100, "sky_caster");
            Vec3 struck = new Vec3(48_106.5, -60, 3004.5);
            ArcaneCasting.Origin where = ArcaneCasting.Origin.impact(struck, new Vec3(1, 0, 0));
            SpellAction.LaunchBolt rain = new SpellAction.LaunchBolt("corruption:spell_bolt", 1.2, 0, List.of(), "", List.of(), List.of(),
                    8, 3, SpellAction.Source.SKY, 14);
            List<Entity> bolts = catching(() -> ArcaneCasting.world().perform(player, where, rain)).stream().filter(SpellProjectile.class::isInstance).toList();
            assertEquals(8, bolts.size());
            for (Entity bolt : bolts) {
                assertEquals(struck.y + 14, bolt.getY(), 1e-6, "fourteen blocks over where the bolt struck");
                assertTrue(Math.hypot(bolt.getX() - struck.x, bolt.getZ() - struck.z) <= 3 + 1e-6, "scattered no further than the spell says");
                assertTrue(bolt.getDeltaMovement().y < 0, "falling");
                assertEquals(0, bolt.getDeltaMovement().horizontalDistance(), 1e-6, "straight down");
            }
        });
    }

    @Test void corruptedLightningIsTheCorruptionsOwnBolt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_200, "tempest_caster");
            List<Entity> corrupted = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player),
                    new SpellAction.Lightning(6, 5, true, 4, "corruption:corruption")));
            assertEquals(5, corrupted.size(), "as many strikes as the spell says");
            for (Entity strike : corrupted) {
                assertEquals(ModEntities.CORRUPT_LIGHTNING.get(), strike.getType(), "the corruption's violet bolt, not the game's");
                assertTrue(strike.position().subtract(player.position()).horizontalDistance() <= 6 + 1, "within the spell's reach");
                assertEquals(-60, strike.getY(), 1e-6, "falling on the ground");
            }
            List<Entity> plain = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), new SpellAction.Lightning(6, 1)));
            assertEquals(List.of(EntityType.LIGHTNING_BOLT), plain.stream().map(Entity::getType).toList(), "lightning not said to be corrupt is the game's own");
        });
    }

    @Test void lightningFallsWhereTheBoltLandedEvenUnderARoof(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_300, "roof_caster");
            // A stone roof five blocks over the ground, as a cave's or a house's would be.
            for (int x = 48_310; x <= 48_316; x++) for (int z = 2997; z <= 3003; z++) level.setBlockAndUpdate(new BlockPos(x, -55, z), Blocks.STONE.defaultBlockState());
            Vec3 landed = new Vec3(48_313.5, -60, 3000.5);
            List<Entity> strikes = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.impact(landed, new Vec3(1, 0, 0)),
                    new SpellAction.Lightning(0, 2, true, 0, "")));
            assertEquals(2, strikes.size());
            for (Entity strike : strikes) assertEquals(-60, strike.getY(), 1e-6, "on the floor under the roof, not on top of it");
            assertEquals(new BlockPos(48_313, -60, 3000), SpellEffects.groundAt(level, new BlockPos(48_313, -57, 3000)), "a bolt struck in mid-air calls it down to the ground below");
            assertEquals(new BlockPos(48_313, -54, 3000), SpellEffects.groundAt(level, new BlockPos(48_313, -54, 3000)), "one that landed on the roof, onto the roof");
            BlockPos chasm = new BlockPos(48_313, 100, 3000);
            assertEquals(chasm, SpellEffects.groundAt(level, chasm), "with no ground in reach, where it struck");
        });
    }

    @Test void aSpellsEffectStandsOnTheGroundForItsTimeAndTellsTheClientsWhatItIs(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            FakePlayer player = caster(level, 48_400, "visual_caster");
            SpellAction.Visual circle = new SpellAction.Visual("corruption:magic_circle", 6, 2, 30, 0x8A0303, 0xFFB040, .5, "minecraft:entity.evoker.prepare_summon");
            List<SpellVisualEntity> shown = catching(() -> ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), circle)).stream()
                    .filter(SpellVisualEntity.class::isInstance).map(SpellVisualEntity.class::cast).toList();
            assertEquals(1, shown.size());
            SpellVisualEntity effect = shown.get(0);
            assertEquals(-60, effect.getY(), 1e-6, "on the ground under the caster, not at their middle");
            assertEquals(player.getX(), effect.getX(), 1e-6);
            assertEquals(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("corruption", "magic_circle"), effect.effect());
            assertEquals(30, effect.lifetime());
            assertEquals(0x8A0303, effect.colour());
            assertEquals(0xFFB040, effect.accent());
            assertEquals(.5F, effect.shake(), 1e-6);

            RegistryFriendlyByteBuf wire = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
            effect.writeSpawnData(wire);
            SpellVisualEntity seen = ModEntities.SPELL_VISUAL.get().create(level);
            seen.readSpawnData(wire);
            assertEquals(List.of(effect.effect(), effect.radius(), effect.height(), effect.lifetime(), effect.colour(), effect.accent(), effect.shake()),
                    List.of(seen.effect(), seen.radius(), seen.height(), seen.lifetime(), seen.colour(), seen.accent(), seen.shake()), "a client sees it as it is");
            assertTrue(seen.getBoundingBoxForCulling().getXsize() >= 12, "and draws it whenever any of it could be in sight");

            assertFalse(seen.isPickable(), "nothing can hit it");
            seen.tickCount = 29;
            seen.tick();
            assertFalse(seen.isRemoved(), "it stands for its time");
            seen.tickCount = 30;
            seen.tick();
            assertTrue(seen.isRemoved(), "and is gone after it");
        });
    }

    /**
     * A spell reaches as far as its radius says: a blast of sixteen blocks hurts and throws a creature fifteen
     * blocks off (once, everything was cut short at twelve), and leaves one at seventeen alone.
     */
    @Test void aSpellReachesAsFarAsItsRadiusSays(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            int x = 46_240;
            TestWorld.load(level, x - 24, x + 24, 3000 - 24, 3000 + 24);
            FakePlayer player = caster(level, x, "reach_caster");
            Pig near = pig(level, x + 15.5), far = pig(level, x + 17.5);
            try {
                ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), new SpellAction.Damage(5, 16));
                ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), new SpellAction.Push(16, 1.5, .2));
                assertEquals(near.getMaxHealth() - 5, near.getHealth(), 1e-3, "fifteen blocks off, within sixteen: hurt");
                assertTrue(near.getDeltaMovement().x > 1, "and thrown away from the caster");
                assertEquals(far.getMaxHealth(), far.getHealth(), 1e-3, "seventeen blocks off: untouched");
                assertEquals(0, far.getDeltaMovement().x, 1e-6, "and unmoved");
            } finally {
                near.discard();
                far.discard();
            }
        });
    }

    /**
     * A bolt's blast throws what it catches away from the caster: a creature standing between the caster and where
     * the bolt landed is driven on, away from them, not flung back onto them as a push from the blast would.
     */
    @Test void aBlastWhereABoltLandsThrowsAwayFromTheCaster(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            int x = 46_304;
            TestWorld.load(level, x - 24, x + 24, 3000 - 24, 3000 + 24);
            FakePlayer player = caster(level, x, "blast_caster");
            Pig between = pig(level, x + 4.5), beyond = pig(level, x + 10.5);
            try {
                Vec3 landed = new Vec3(x + 7.5, -59, 3000.5);
                ArcaneCasting.Origin struck = ArcaneCasting.Origin.impact(landed, new Vec3(1, 0, 0));
                ArcaneCasting.world().perform(player, struck, new SpellAction.Push(8, 1.4, .4, SpellAction.Push.From.CASTER));
                assertTrue(between.getDeltaMovement().x > 1, "between the caster and the blast: driven on, away from the caster");
                assertTrue(beyond.getDeltaMovement().x > 1, "beyond the blast: away from the caster too");
                between.setDeltaMovement(Vec3.ZERO);
                ArcaneCasting.world().perform(player, struck, new SpellAction.Push(8, 1.4, .4));
                assertTrue(between.getDeltaMovement().x < -1, "where a push from the blast itself would have flung it back at the caster");
            } finally {
                between.discard();
                beyond.discard();
            }
        });
    }

    /**
     * A beam fires from its caster's hand along their gaze until its time is up: every pulse it hurts what stands in
     * its line, each pulse landing however close together they come, and never what is behind its caster or off to
     * the side; it follows their gaze as they turn; a wall stops it; and when its time is up, it is gone.
     */
    @Test void aBeamFiresAlongTheCastersGazeUntilItEnds(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            int x = 46_368;
            TestWorld.load(level, x - 24, x + 24, 3000 - 24, 3000 + 24);
            FakePlayer player = caster(level, x, "beam_caster");
            player.moveTo(x + .5, -60, 3000.5, -90, 0);
            Pig ahead = pig(level, x + 10.5), behind = pig(level, x - 6.5), aside = pig(level, x + 10.5, 3004.5), round = pig(level, x + .5, 2991.5);
            BlockPos wall = new BlockPos(x, -59, 2995);
            try {
                SpellAction.Beam fired = new SpellAction.Beam(32, 1, 12, 2, List.of(new SpellAction.Damage(2, .5)), 0xFFD24A, 0xFFFFFF, 0, "");
                // Caught as it joins, and kept (the catching above discards what it catches).
                List<SpellBeamEntity> beams = new ArrayList<>();
                Consumer<EntityJoinLevelEvent> catcher = event -> { if (event.getEntity() instanceof SpellBeamEntity one) beams.add(one); };
                NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
                try { ArcaneCasting.world().perform(player, ArcaneCasting.Origin.of(player), fired); }
                finally { NeoForge.EVENT_BUS.unregister(catcher); }
                assertEquals(1, beams.size(), "one beam, fired");
                SpellBeamEntity beam = beams.get(0);
                tick(beam, 3);
                assertEquals(ahead.getMaxHealth() - 4, ahead.getHealth(), 1e-3, "two pulses two ticks apart, both landing on what stands in its line");
                assertEquals(behind.getMaxHealth(), behind.getHealth(), 1e-3, "nothing behind its caster");
                assertEquals(aside.getMaxHealth(), aside.getHealth(), 1e-3, "nor four blocks off to the side of it");
                player.setYRot(180);
                tick(beam, 4);
                assertEquals(round.getMaxHealth() - 4, round.getHealth(), 1e-3, "it follows its caster's gaze as they turn");
                assertEquals(ahead.getMaxHealth() - 4, ahead.getHealth(), 1e-3, "and leaves what it has turned from");
                level.setBlock(wall, Blocks.STONE.defaultBlockState(), 3);
                level.setBlock(wall.above(), Blocks.STONE.defaultBlockState(), 3);
                tick(beam, 2);
                assertEquals(round.getMaxHealth() - 4, round.getHealth(), 1e-3, "a wall stops it");
                tick(beam, 3);
                assertFalse(beam.isRemoved(), "twelve ticks it fires");
                tick(beam, 1);
                assertTrue(beam.isRemoved(), "and then it is gone");
                assertEquals(player.getMaxHealth(), player.getHealth(), 1e-3, "never hurting its caster");
            } finally {
                level.setBlock(wall, Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(wall.above(), Blocks.AIR.defaultBlockState(), 3);
                for (Pig pig : List.of(ahead, behind, aside, round)) pig.discard();
            }
        });
    }

    /**
     * What a spell's explosion breaks is as its data says: of the blocks it would break, none further than its radius,
     * none tougher than its limit, and only the nearest, as many as it may (air takes nothing from the count).
     */
    @Test void aSpellsExplosionBreaksOnlyWhatItsDataAllows(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 46_432;
            TestWorld.load(level, x - 24, x + 24, 3000 - 24, 3000 + 24);
            Vec3 at = new Vec3(x + .5, -50.5, 3000.5);
            BlockPos middle = BlockPos.containing(at);
            List<BlockPos> blocks = new ArrayList<>();
            try {
                for (int out = 1; out <= 6; out++) {
                    level.setBlock(middle.east(out), Blocks.STONE.defaultBlockState(), 3);
                    blocks.add(middle.east(out));
                }
                level.setBlock(middle.west(1), Blocks.OBSIDIAN.defaultBlockState(), 3);
                blocks.add(middle.west(1));
                blocks.add(middle.above(2));
                SpellExplosions.bound(level, null, blocks, at, new SpellAction.Explode(4, true, 4.5, 3, 6));
                assertEquals(List.of(middle.east(1), middle.east(2), middle.east(3), middle.above(2)), blocks,
                        "the three nearest stones within reach, and the air; not the obsidian, not what lies past the radius");

                // An explosion going off for real, in a solid block of stone: whatever its force would break, the bounds hold.
                for (BlockPos pos : BlockPos.betweenClosed(middle.offset(-6, -6, -6), middle.offset(6, 6, 6))) level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
                level.setBlock(middle.north(1), Blocks.OBSIDIAN.defaultBlockState(), 3);
                FakePlayer player = caster(level, x, "blast_maker");
                SpellExplosions.explode(player, at, new SpellAction.Explode(6, true, 3, 12, 6));
                int broken = 0;
                for (BlockPos pos : BlockPos.betweenClosed(middle.offset(-6, -6, -6), middle.offset(6, 6, 6))) {
                    if (!level.getBlockState(pos).isAir()) continue;
                    broken++;
                    assertTrue(Vec3.atCenterOf(pos).distanceTo(at) <= 3 + 1e-6, "nothing broken past its radius: " + pos);
                }
                assertTrue(broken > 0 && broken <= 12, "some broken, and twelve at most: " + broken);
                assertTrue(level.getBlockState(middle.north(1)).is(Blocks.OBSIDIAN), "nothing tougher than its limit");
                assertEquals(player.getMaxHealth(), player.getHealth(), 1e-3, "and its caster unhurt");
            } finally {
                for (BlockPos pos : BlockPos.betweenClosed(middle.offset(-7, -7, -7), middle.offset(7, 7, 7))) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        });
    }

    private static <T extends Entity> T place(ServerLevel level, EntityType<T> type, Vec3 at) {
        T entity = type.create(level);
        entity.moveTo(at.x, at.y, at.z, 0, 0);
        assertTrue(level.addFreshEntity(entity));
        return entity;
    }

    @Test void aSpellTurnsTheCreaturesItReachesIntoRemnantsWhereTheyStand(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SpellEffects.install();
            int x = 45_200;
            FakePlayer player = caster(level, x, "remnant_maker");
            Vec3 at = new Vec3(x + .5, -60, 3010.5);
            ArcaneCasting.Origin origin = new ArcaneCasting.Origin(at, at, new Vec3(0, 0, 1));
            Pig pig = place(level, EntityType.PIG, at.add(2, 0, 0));
            Zombie zombie = place(level, EntityType.ZOMBIE, at.add(-2, 0, 1));
            Wolf pet = place(level, EntityType.WOLF, at.add(0, 0, 2));
            pet.setTame(true, false);
            pet.setOwnerUUID(player.getUUID());
            Zombie summoned = place(level, EntityType.ZOMBIE, at.add(1, 0, -2));
            SpellSummons.bind(summoned, player);
            Pig kept = place(level, EntityType.PIG, at.add(-1, 0, -1));
            kept.setInvulnerable(true);
            WitherBoss boss = place(level, EntityType.WITHER, at.add(0, 0, -4));
            Pig far = place(level, EntityType.PIG, at.add(9, 0, 0));
            CorruptedRemnant already = place(level, ModEntities.CORRUPTED_REMNANT.get(), at.add(-3, 0, -1));
            List<LivingEntity> placed = List.of(pig, zombie, pet, summoned, kept, boss, far, already);
            try {
                ArcaneCasting.world().perform(player, origin, new SpellAction.CorruptCreatures(6, 0));
                assertTrue(pig.isAlive() && zombie.isAlive(), "a chance of none turns none");

                List<Entity> joined = catching(() -> {
                    ArcaneCasting.world().perform(player, origin, new SpellAction.CorruptCreatures(6));
                    ReanimationEvents.tick(null);
                });
                assertTrue(pig.isDeadOrDying() && zombie.isDeadOrDying(), "what it reaches falls to the corruption at once");
                for (LivingEntity spared : List.of(pet, summoned, kept, boss, far, already))
                    assertTrue(spared.isAlive() && spared.getHealth() == spared.getMaxHealth(), "but not " + spared
                            + ": the caster's own, what is kept from harm, a boss, what is out of reach, or what is of the corruption already");
                List<CorruptedRemnant> risen = joined.stream().filter(CorruptedRemnant.class::isInstance).map(CorruptedRemnant.class::cast)
                        .filter(remnant -> remnant.position().distanceTo(at) < 8).toList();
                assertEquals(2, risen.size(), "a remnant rises for each: " + risen);
                for (CorruptedRemnant remnant : risen) {
                    LivingEntity was = remnant.appearance().getString("Type").equals("minecraft:pig") ? pig : zombie;
                    assertEquals(BuiltInRegistries.ENTITY_TYPE.getKey(was.getType()).toString(), remnant.appearance().getString("Type"), "in its own likeness");
                    assertTrue(remnant.position().distanceTo(was.position()) < 1e-6, "where it stood");
                    assertEquals(was.getMaxHealth(), remnant.getHealth(), 1e-3, "whole");
                }
                assertNotEquals(risen.get(0).appearance().getString("Type"), risen.get(1).appearance().getString("Type"), "one for the pig, one for the zombie");
            } finally {
                placed.forEach(Entity::discard);
            }
        });
    }

    /** Ticks an entity as its level would, this many times. */
    private static void tick(Entity entity, int ticks) {
        for (int tick = 0; tick < ticks && !entity.isRemoved(); tick++) {
            entity.tickCount++;
            entity.tick();
        }
    }

    private static Pig pig(ServerLevel level, double x) { return pig(level, x, 3000.5); }

    private static Pig pig(ServerLevel level, double x, double z) {
        Pig pig = EntityType.PIG.create(level);
        pig.moveTo(x, -60, z, 0, 0);
        pig.setNoAi(true);
        level.addFreshEntity(pig);
        return pig;
    }

    @Test void aVolleyIsDrawnEvenlyAndShapesAreWhereTheySay() {
        Vec3 ahead = new Vec3(0, 0, 1);
        assertEquals(ahead, SpellEffects.fanned(ahead, 0, 1, 90), "one bolt flies along the gaze");
        assertEquals(ahead, SpellEffects.fanned(ahead, 2, 5, 0), "with no fan, all along it");
        assertEquals(-45, degrees(SpellEffects.fanned(ahead, 0, 3, 90)), 1e-4);
        assertEquals(45, degrees(SpellEffects.fanned(ahead, 2, 3, 90)), 1e-4);
        for (int point = 0; point < 12; point++) {
            Vec3 ring = SpellEffects.shapePoint(SpellAction.Shape.RING, 4, 2, point, 12);
            assertEquals(4, ring.horizontalDistance(), 1e-6, "a ring is as wide as its radius");
            assertEquals(0, ring.y, 1e-6, "and level with the origin");
            assertEquals(3, SpellEffects.shapePoint(SpellAction.Shape.SPHERE, 3, 2, point, 12).length(), 1e-6, "a sphere is round");
        }
        assertEquals(Vec3.ZERO, SpellEffects.shapePoint(SpellAction.Shape.COLUMN, 3, 5, 0, 6), "a column rises from the origin");
        assertEquals(new Vec3(0, 5, 0), SpellEffects.shapePoint(SpellAction.Shape.COLUMN, 3, 5, 5, 6), "as high as the spell says");
        assertEquals(6, SpellEffects.shapePoint(SpellAction.Shape.SPIRAL, 2, 6, 9, 10).y, 1e-6, "and so does a spiral");
    }

    @Test void eachStringIsFoundInItsBook(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var registries = level.registryAccess();
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:corruption_tempest"))));
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS_I.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:baal"))));
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS_IV.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:asmoday"))));
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS_VIII.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:andromalius"))));
            assertEquals(List.of(ModItems.CLAVICULA_SALOMONIS_ASCENSION.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:pentacle_sun"))));
            assertEquals(List.of(ModItems.LEMEGETON.get()), items(ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:ars_goetia"))));
            assertEquals(List.of(), ArcaneApi.keysTo(registries, ArcaneApi.key("corruption:no_such_string")));
            for (var key : ModItems.KEYS) assertFalse(ArcaneApi.strings(registries, new ItemStack(key.get())).isEmpty(), key.getId() + " reveals something");
        });
    }
    private static List<net.minecraft.world.item.Item> items(List<ItemStack> stacks) { return stacks.stream().map(ItemStack::getItem).toList(); }

    @Test void parchmentsHaveATabOfTheirOwnAndTheMainTabNoLongerTeachesWands(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var registries = level.registryAccess();
            List<ResourceKey<ArcaneString>> strings = ModCreativeModeTabs.inscribable(registries);
            int all = registries.registryOrThrow(ArcaneString.REGISTRY).size();
            assertEquals(all, strings.size(), "every string the data holds");
            assertEquals(all, new HashSet<>(strings).size(), "each once");
            assertEquals(ArcaneApi.key("corruption:corruption"), strings.get(0), "the Prologue's first");
            assertTrue(strings.indexOf(ArcaneApi.key("corruption:baal")) < strings.indexOf(ArcaneApi.key("corruption:andromalius")), "then the spirits in order");
            assertTrue(strings.indexOf(ArcaneApi.key("corruption:andromalius")) < strings.indexOf(ArcaneApi.key("corruption:pentacle_saturn")), "then the Ascension");
            assertEquals(ArcaneApi.key("corruption:ars_notoria"), strings.get(strings.size() - 1), "and the Lesser Key last");

            CreativeModeTab.ItemDisplayParameters shown = new CreativeModeTab.ItemDisplayParameters(level.enabledFeatures(), false, registries);
            CreativeModeTab parchments = ModCreativeModeTabs.PARCHMENTS.get();
            parchments.buildContents(shown);
            assertEquals(all, parchments.getDisplayItems().size());
            assertTrue(parchments.getDisplayItems().stream().allMatch(stack -> stack.is(ModItems.PARCHMENT.get()) && ArcaneApi.inscription(stack) != null));
            CreativeModeTab main = ModCreativeModeTabs.CORRUPTION.get();
            main.buildContents(shown);
            for (var wand : ModItems.WANDS) assertTrue(main.contains(new ItemStack(wand.get())), wand.getId() + " is in the main tab");
            for (var key : ModItems.KEYS) assertTrue(main.contains(new ItemStack(key.get())), key.getId() + " is in the main tab");
            assertTrue(main.getDisplayItems().stream().noneMatch(stack -> ArcaneApi.inscription(stack) != null), "no inscribed parchments there");
            assertTrue(main.getDisplayItems().stream().noneMatch(stack -> !WandSpells.of(stack).isEmpty()), "nor wands taught a spell");
        });
    }
}
