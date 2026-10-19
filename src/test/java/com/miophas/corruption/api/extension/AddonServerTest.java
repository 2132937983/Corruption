package com.miophas.corruption.api.extension;

import com.miophas.corruption.api.corruption.RemnantExtensions;
import com.miophas.corruption.api.demon.DemonBoss;
import com.miophas.corruption.api.demon.skill.*;
import com.miophas.corruption.api.infection.*;
import com.miophas.corruption.api.plant.*;
import com.miophas.corruption.common.entity.TestWorld;
import com.miophas.corruption.common.infection.ServerInfectionWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import fixture.corruption.AddonContent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.phys.AABB;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

/** Test scaffolding may inspect native internals; AddonContent itself is a separate API-only JAR consumer. */
@ExtendWith(EphemeralTestServerProvider.class)
class AddonServerTest {
    @Test void previouslyMissingSkillsAndPlayerIndependentVisualsRunFromTheConsumer(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BlockPos at = new BlockPos(680_000, -60, 16_000);
            TestWorld.load(level, at.getX() - 24, at.getX() + 24, at.getZ() - 24, at.getZ() + 40);
            var host = EntityType.ZOMBIE.create(level); var target = EntityType.VILLAGER.create(level);
            host.setPos(at.getCenter()); target.setPos(at.getCenter().add(0, 0, 12));
            host.setNoAi(true); target.setNoAi(true); level.addFreshEntity(host); level.addFreshEntity(target);
            AABB area = new AABB(at).inflate(45);
            Set<Entity> before = new HashSet<>(level.getEntitiesOfClass(Entity.class, area));
            try {
                AddonContent.visual(level, host.position());
                var skills = AddonContent.skills();
                assertEquals(5, skills.size());
                for (var skill : skills) { skill.begin(host, target); skill.tick(host, target, skill.timing().windup()); }
                Set<String> kinds = new HashSet<>();
                for (var entity : level.getEntitiesOfClass(Entity.class, area))
                    if (!before.contains(entity)) kinds.add(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
                for (String kind : new String[]{"spell_visual", "solomon_fireball", "solomon_hand", "solomon_drake"})
                    assertTrue(kinds.contains("corruption:" + kind), kinds.toString());
                assertTrue(host.getDeltaMovement().lengthSqr() > 0, "Swoop moves the unrelated vanilla host");
            } finally {
                for (var entity : level.getEntitiesOfClass(Entity.class, area)) if (!before.contains(entity)) entity.discard();
                host.discard(); target.discard();
            }
        });
    }

    @Test void customSourcesAndConversionRulesParticipateInTheNativeWorldAdapter(MinecraftServer server) {
        AddonContent.registerInfection();
        try {
            TestWorld.onServer(server, level -> {
                BlockPos at = new BlockPos(680_080, -50, 16_000);
                TestWorld.load(level, at.getX() - 2, at.getX() + 2, at.getZ() - 2, at.getZ() + 2);
                level.setBlock(at, Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(at.east(), Blocks.STONE.defaultBlockState(), 3);
                assertTrue(WorldInfection.canInfect(level, at));
                assertTrue(WorldInfection.infect(level, at));
                assertTrue(level.getBlockState(at).is(Blocks.OBSIDIAN));
                assertTrue(WorldInfection.isCorrupted(level.getBlockState(at)));
                assertTrue(WorldInfection.isSource(level.getBlockState(at)));
                var scan = new ServerInfectionWorld(level).scanSources(new ChunkPos(at), 0, 100_000);
                assertTrue(scan.sources().contains(at), "Custom sources survive the native chunk frontier scan");
                WorldInfection.deactivate(level, at);
                level.removeBlock(at, false); level.removeBlock(at.east(), false);
            });
        } finally { InfectionExtensions.unregister(AddonContent.INFECTION); }
    }

    @Test @SuppressWarnings({"unchecked", "rawtypes"})
    void thirdPlantSpeciesGrowsFromEitherSideAndReceivesNativeBlockBonemeal(MinecraftServer server) {
        AddonContent.registerPlant();
        TestWorld.onServer(server, level -> {
            BlockPos start = new BlockPos(680_160, -40, 16_000);
            TestWorld.load(level, start.getX() - 8, start.getX() + 32, start.getZ() - 8, start.getZ() + 8);
            for (int direction : new int[]{1, -1}) {
                BlockPos at = start.offset(direction == 1 ? 0 : 20, 0, 0);
                level.setBlock(at, ModBlocks.MALICE.get().defaultBlockState(), 3);
                var plant = new AddonContent.Plant((EntityType) EntityType.ZOMBIE, level);
                plant.anchorAt(at, direction); level.addFreshEntity(plant);
                try {
                    assertTrue(plant.canGrow());
                    var state = level.getBlockState(at);
                    ((BonemealableBlock) state.getBlock()).performBonemeal(level, level.random, at, state);
                    assertEquals(1, plant.stage());
                    assertEquals(direction < 0, plant.hanging());
                    assertEquals(4, plant.getBbHeight(), .001);
                    assertSame(plant, AnchoredLivingPlant.find(level, at));
                    var saved = plant.saveWithoutId(new CompoundTag());
                    var restored = new AddonContent.Plant((EntityType) EntityType.ZOMBIE, level);
                    restored.load(saved);
                    assertEquals(1, restored.stage()); assertEquals(plant.anchor(), restored.anchor());
                    assertEquals(plant.hanging(), restored.hanging()); restored.discard();
                } finally { plant.discard(); level.removeBlock(at, false); }
            }
        });
    }

    @Test void customDoorConversionAppliesTheSameRuleToBothHalvesBeforeNotifyingNeighbors(MinecraftServer server) {
        var id = AddonContent.id("paired_conversion");
        InfectionExtensions.registerSource(id, state -> state.is(Blocks.IRON_DOOR), state -> true);
        InfectionExtensions.registerConversion(id, 100, new InfectionExtensions.Conversion() {
            public boolean matches(net.minecraft.world.level.BlockGetter level, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
                return state.is(Blocks.OAK_DOOR);
            }
            public net.minecraft.world.level.block.state.BlockState convert(net.minecraft.world.level.BlockGetter level, BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
                return Blocks.IRON_DOOR.defaultBlockState().setValue(net.minecraft.world.level.block.DoorBlock.HALF,
                        state.getValue(net.minecraft.world.level.block.DoorBlock.HALF));
            }
        });
        try {
            TestWorld.onServer(server, level -> {
                var at = new BlockPos(680_320, -50, 16_000);
                TestWorld.load(level, at.getX() - 4, at.getX() + 4, at.getZ() - 4, at.getZ() + 4);
                level.setBlock(at.below(), Blocks.STONE.defaultBlockState(), 3);
                var lower = Blocks.OAK_DOOR.defaultBlockState();
                int flags = net.minecraft.world.level.block.Block.UPDATE_CLIENTS | net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE;
                level.setBlock(at, lower, flags);
                level.setBlock(at.above(), lower.setValue(net.minecraft.world.level.block.DoorBlock.HALF,
                        net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER), flags);
                assertTrue(WorldInfection.infect(level, at));
                assertTrue(level.getBlockState(at).is(Blocks.IRON_DOOR));
                assertTrue(level.getBlockState(at.above()).is(Blocks.IRON_DOOR));
                assertTrue(WorldInfection.isSource(level.getBlockState(at.above())));
                level.removeBlock(at.above(), false); level.removeBlock(at, false); level.removeBlock(at.below(), false);
            });
        } finally { InfectionExtensions.unregister(id); }
    }

    @Test @SuppressWarnings({"unchecked", "rawtypes"})
    void addonBossUsesNativeBarAndDeathContractsWithoutANativeSuperclass(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var boss = new AddonContent.Demon((EntityType) EntityType.ZOMBIE, level);
            assertInstanceOf(DemonBoss.class, boss);
            assertTrue(boss.bossBar().isPresent());
            assertEquals(AddonContent.id("textures/gui/emblem.png"), boss.emblemTexture().orElseThrow());
            assertTrue(boss.death().lasts() > boss.death().ticks());
            assertFalse(boss.death().beats().isEmpty());
            assertFalse(boss.removeWhenFarAway(1_000_000));
            assertNotNull(boss.bossEvent()); boss.discard();
        });
    }

    @Test void customRemnantAbilityHasIndependentPersistentStateAndRemovalLifecycle(MinecraftServer server) {
        AtomicInteger removed = new AtomicInteger(); AddonContent.registerAbility(removed::incrementAndGet);
        try {
            TestWorld.onServer(server, level -> {
                BlockPos at = new BlockPos(680_240, -60, 16_000);
                TestWorld.load(level, at.getX() - 8, at.getX() + 8, at.getZ() - 8, at.getZ() + 8);
                var source = EntityType.COW.create(level); source.setPos(at.getCenter());
                Mob remnant = AddonContent.remnant(source).orElseThrow(); level.addFreshEntity(remnant);
                remnant.tick();
                assertEquals(1, abilityState(remnant).getInt("pulses"));
                CompoundTag saved = remnant.saveWithoutId(new CompoundTag());
                remnant.discard(); assertEquals(1, removed.get());
                Mob loaded = AddonContent.remnant(source).orElseThrow(); loaded.load(saved); level.addFreshEntity(loaded);
                loaded.tick(); assertEquals(2, abilityState(loaded).getInt("pulses"));
                loaded.discard(); assertEquals(2, removed.get()); source.discard();
            });
        } finally { RemnantExtensions.unregister(AddonContent.ABILITY); }
    }
    private static CompoundTag abilityState(Mob mob) {
        return mob.getPersistentData().getCompound("corruption:extension_abilities").getCompound(AddonContent.ABILITY.toString());
    }

    @Test void nativeBossSkillSetHonorsTheSameFactoryRegistrationAsAnAddon(MinecraftServer server) {
        AtomicInteger began = new AtomicInteger();
        BossSkillRegistry.register(BossSkillApi.GAZE, (rules, options) -> new BossSkill<Mob>() {
            public SkillTiming timing() { return new SkillTiming(0, 1, 0); }
            public boolean usable(Mob host, LivingEntity target) { return true; }
            public void begin(Mob host, LivingEntity target) { began.incrementAndGet(); }
            public void tick(Mob host, LivingEntity target, int elapsed) {}
        });
        try {
            TestWorld.onServer(server, level -> {
                var boss = ModEntities.DANTALION.get().create(level);
                var target = EntityType.VILLAGER.create(level);
                int gaze = boss.skills().index("gaze");
                assertTrue(boss.skills().begin(gaze, boss, target));
                assertEquals(1, began.get()); boss.discard(); target.discard();
            });
        } finally { BossSkillRegistry.unregister(BossSkillApi.GAZE.id()); }
    }
}
