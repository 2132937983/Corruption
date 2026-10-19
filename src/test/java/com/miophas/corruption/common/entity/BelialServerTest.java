package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.GoeticDemon;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneDamage;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.altar.AltarRecipe;
import com.miophas.corruption.common.altar.GroundBlock;
import com.miophas.corruption.common.config.DemonConfig;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.demon.BelialRules;
import com.miophas.corruption.core.demon.BelialRules.Skill;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
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
 * Belial on a real server: what he is; his fair angel brought down and the hollow risen whole; his skills as each (each
 * begun by hand and run through his own set of them): his volleys, his beams from his hands and from his wings' eyes, his
 * host round his quarry, his chariot through it and through what stands in its way, his likenesses and his stun, his
 * strikes at the mind; a server's leave for any hurt to reach the demons; and the rite that brings him forth, and what he
 * leaves. The test world never brings new entities into sight, so what his skills bring forth is caught as it joins.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class BelialServerTest {
    /** Belial at x, hovering a few blocks over the flat ground (its surface at y -60), facing south (+Z). */
    private static Belial belial(ServerLevel level, int x) {
        TestWorld.load(level, x - 48, x + 48, 3000 - 48, 3000 + 48);
        Belial angel = ModEntities.BELIAL.get().create(level);
        angel.moveTo(x + .5, -56, 3000.5, 0, 0);
        level.addFreshEntity(angel);
        return angel;
    }
    private static <T extends LivingEntity> T still(T creature, ServerLevel level, double x, double z) {
        creature.moveTo(x, -60, z, 0, 0);
        if (creature instanceof net.minecraft.world.entity.Mob mob) mob.setNoAi(true);
        level.addFreshEntity(creature);
        return creature;
    }
    private static IronGolem golem(ServerLevel level, double x, double z) { return still(EntityType.IRON_GOLEM.create(level), level, x, z); }
    private static FakePlayer player(ServerLevel level, String name) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }
    /** What joins the world while this runs; discarded afterwards unless kept. */
    private static List<Entity> catching(Runnable run, boolean keep) {
        List<Entity> joined = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> joined.add(event.getEntity());
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try { run.run(); }
        finally { NeoForge.EVENT_BUS.unregister(catcher); }
        if (!keep) joined.forEach(Entity::discard);
        return joined;
    }
    /** Runs one of his skills on this quarry from its beginning to its end, by hand; how many ticks it took. */
    private static int run(Belial angel, Skill skill, LivingEntity target) {
        angel.skills().comeRound(angel.slot(skill));
        assertTrue(angel.begin(skill, target), skill + " begins");
        int ticks = 0;
        for (; ticks < 400 && angel.skills().busy(); ticks++) {
            angel.skills().tick(angel, target, cooldown -> cooldown);
            angel.travel(Vec3.ZERO);
        }
        assertFalse(angel.skills().busy(), skill + " runs its course");
        return ticks;
    }
    /** Brings the fair angel down, and lives his change through: the hollow angel stands. */
    private static void hollow(ServerLevel level, Belial angel) {
        assertTrue(angel.hurt(ArcaneDamage.source(level, player(level, "belial_hollower")), 10_000), "the arcane reaches him");
        for (int tick = 0; tick < BelialRules.CHANGE_TICKS + 2 && angel.changing() > 0; tick++) angel.customServerAiStep();
        assertTrue(angel.hollow());
    }
    /** Where a place in his own frame ({right, up, ahead}, from his feet) is, as he faces now. */
    private static Vec3 frame(Belial angel, double[] at) {
        Vec3 forward = Vec3.directionFromRotation(0, angel.getYRot()), side = new Vec3(-forward.z, 0, forward.x);
        return angel.position().add(side.scale(at[0])).add(forward.scale(at[2])).add(0, at[1], 0);
    }

    @Test void heIsTheFairAngelFlyingAndOnlyTheArcaneHurtsHim(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 160_000);
            try {
                assertInstanceOf(GoeticDemon.class, angel);
                assertTrue(CorruptionFactionApi.isRemnant(angel) && CorruptionFactionApi.isBoss(angel), "of the corruption, one of its lords");
                assertTrue(angel.isNoGravity(), "he flies");
                assertFalse(angel.hollow(), "he comes forth as the fair angel");
                assertEquals(BelialRules.MAX_HEALTH, angel.getMaxHealth());
                assertEquals(6, angel.emblem());
                assertFalse(angel.hurt(level.damageSources().generic(), 10), "only the arcane hurts him");
                assertTrue(angel.hurt(ArcaneDamage.source(level, player(level, "belial_striker")), 10));
                assertTrue(angel.getHealth() < angel.getMaxHealth());
            } finally {
                angel.discard();
            }
        });
    }

    @Test void aServerMayLetAnyHurtReachTheDemons(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 160_200);
            try {
                DemonConfig.setArcaneOnly(false);
                assertTrue(angel.hurt(level.damageSources().generic(), 10), "any hurt reaches him, the server says");
                assertTrue(angel.getHealth() < angel.getMaxHealth());
                DemonConfig.setArcaneOnly(true);
                angel.invulnerableTime = 0;
                assertFalse(angel.hurt(level.damageSources().generic(), 10), "and only the arcane again once it says otherwise");
            } finally {
                DemonConfig.setArcaneOnly(true);
                angel.discard();
            }
        });
    }

    @Test void broughtToNothingTheFairAngelRisesAgainAsTheHollowWhole(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 160_400);
            try {
                assertTrue(angel.hurt(ArcaneDamage.source(level, player(level, "belial_feller")), 10_000));
                assertTrue(angel.isAlive(), "the fair angel brought to nothing does not die");
                assertEquals(BelialRules.CHANGE_TICKS, angel.changing(), "he falls, and changes");
                assertFalse(angel.hurt(ArcaneDamage.source(level, player(level, "belial_feller")), 50), "nothing reaches him as he changes");
                for (int tick = 0; tick < BelialRules.CHANGE_TICKS / 2; tick++) angel.customServerAiStep();
                assertTrue(angel.hollow(), "the hollow angel there by its middle");
                for (int tick = 0; tick < BelialRules.CHANGE_TICKS && angel.changing() > 0; tick++) angel.customServerAiStep();
                assertEquals(0, angel.changing());
                assertEquals(angel.getMaxHealth(), angel.getHealth(), 1e-3, "risen with all of his health");
                // (Ticked by hand, his moment of protection from the first blow never ran down; in the world it is long gone.)
                angel.invulnerableTime = 0;
                assertTrue(angel.hurt(ArcaneDamage.source(level, player(level, "belial_feller")), 10_000), "the hollow angel is hurt as any");
                assertTrue(angel.isDeadOrDying(), "the hollow angel dies when he is brought down");
            } finally {
                angel.discard();
            }
        });
    }

    @Test void whatPassesEveryDefenceBringsTheFairAngelDownForGood(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 160_600);
            try {
                angel.hurt(level.damageSources().genericKill(), Float.MAX_VALUE);
                assertTrue(angel.isDeadOrDying(), "/kill is the end of him");
                assertEquals(0, angel.changing());
            } finally {
                angel.discard();
            }
        });
    }

    @Test void theHollowAngelsVolleysAreThickerThanTheFairAngels(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 160_800);
            IronGolem quarry = golem(level, angel.getX(), angel.getZ() + 12);
            try {
                List<Entity> fair = catching(() -> run(angel, Skill.VOLLEY, quarry), false);
                assertEquals(BelialRules.VOLLEY.bolts(), fair.stream().filter(DemonBolt.class::isInstance).count(), "the fair angel's");
                hollow(level, angel);
                List<Entity> hollow = catching(() -> run(angel, Skill.VOLLEY, quarry), false);
                assertEquals(BelialRules.HOLLOW_VOLLEY.bolts(), hollow.stream().filter(DemonBolt.class::isInstance).count(), "the hollow angel's");
            } finally {
                quarry.discard();
                angel.discard();
            }
        });
    }

    @Test void hisBeamsAreTwoFromTheFairAngelsHandsAndSixFromTheEyesOfTheHollowsWings(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 161_000);
            IronGolem quarry = golem(level, angel.getX(), angel.getZ() + 14);
            try {
                angel.skills().comeRound(angel.slot(Skill.BEAMS));
                assertTrue(angel.begin(Skill.BEAMS, quarry));
                assertEquals(2, angel.beams().beams().size(), "two, from the fair angel");
                for (int index = 0; index < 2; index++)
                    assertTrue(angel.beams().beams().get(index).position().distanceTo(frame(angel, Belial.FAIR_HANDS[index])) < .5, "from his hand");
                angel.skills().interrupt(angel, cooldown -> cooldown);
                hollow(level, angel);
                angel.skills().comeRound(angel.slot(Skill.BEAMS));
                assertTrue(angel.begin(Skill.BEAMS, quarry));
                assertEquals(6, angel.beams().beams().size(), "six, from the hollow angel");
                for (int index = 0; index < 6; index++)
                    assertTrue(angel.beams().beams().get(index).position().distanceTo(frame(angel, Belial.HOLLOW_EYES[index])) < .5, "from the eye of a wing");
            } finally {
                angel.skills().interrupt(angel, cooldown -> cooldown);
                quarry.discard();
                angel.discard();
            }
        });
    }

    @Test void hisHostRisesRoundHisQuarry(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 161_200);
            IronGolem quarry = golem(level, angel.getX() + 4, angel.getZ() + 20);
            try {
                List<Entity> joined = catching(() -> run(angel, Skill.SUMMON, quarry), false);
                List<Entity> host = joined.stream().filter(CorruptedRemnant.class::isInstance).toList();
                assertEquals(BelialRules.SUMMON.count(), host.size(), "a host of them");
                for (Entity remnant : host)
                    assertTrue(Math.hypot(remnant.getX() - quarry.getX(), remnant.getZ() - quarry.getZ()) < BelialRules.SUMMON.radius() + 1.5,
                            "round his quarry, not round him: " + remnant.position());
            } finally {
                quarry.discard();
                angel.discard();
            }
        });
    }

    /** Begins his chariot on his quarry and stands a wall of this block across its line between its start and the quarry. */
    private static Vec3[] chariotWith(ServerLevel level, Belial angel, LivingEntity quarry, Block wall) {
        angel.skills().comeRound(angel.slot(Skill.CHARIOT));
        assertTrue(angel.begin(Skill.CHARIOT, quarry));
        Vec3[] line = angel.chariot().line();
        assertNotNull(line);
        Vec3 middle = line[0].lerp(quarry.position(), .5), across = new Vec3(line[1].z - line[0].z, 0, line[0].x - line[1].x).normalize();
        for (int off = -3; off <= 3; off++)
            for (int up = 0; up < 2; up++) level.setBlockAndUpdate(BlockPos.containing(middle.add(across.scale(off))).atY(-60 + up), wall.defaultBlockState());
        return new Vec3[]{line[0], line[1], middle};
    }

    @Test void hisChariotRunsThroughHisQuarryBreakingWhatStandsInItsWay(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 161_400);
            IronGolem quarry = golem(level, angel.getX(), angel.getZ() + 16);
            FieryChariot chariot = null;
            try {
                Vec3[] line = chariotWith(level, angel, quarry, Blocks.STONE);
                List<Entity> joined = catching(() -> {
                    for (int tick = 0; tick <= BelialRules.CHARIOT.timing().windup(); tick++) angel.skills().tick(angel, quarry, cooldown -> cooldown);
                }, true);
                chariot = (FieryChariot) joined.stream().filter(FieryChariot.class::isInstance).findFirst().orElseThrow(() -> new AssertionError("the chariot comes"));
                assertTrue(chariot.position().distanceTo(line[0]) < 1, "at the start of its line");
                for (int tick = 0; tick < BelialRules.CHARIOT.runTicks() + 5 && !chariot.isRemoved(); tick++) chariot.tick();
                assertTrue(chariot.isRemoved() && !chariot.wrecked(), "it ran its line, and is gone at its end");
                assertTrue(level.getBlockState(BlockPos.containing(line[2]).atY(-60)).isAir(), "the wall in its way broken");
                assertTrue(quarry.getHealth() < quarry.getMaxHealth(), "its quarry struck");
                assertTrue(quarry.getRemainingFireTicks() > 0, "and left burning");
                assertTrue(chariot.struck().contains(quarry.getUUID()));
            } finally {
                if (chariot != null) chariot.discard();
                quarry.discard();
                angel.discard();
            }
        });
    }

    @Test void hisChariotIsWreckedOnWhatWillNotGiveWay(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 161_600);
            IronGolem quarry = golem(level, angel.getX(), angel.getZ() + 16);
            FieryChariot chariot = null;
            try {
                Vec3[] line = chariotWith(level, angel, quarry, Blocks.OBSIDIAN);
                List<Entity> joined = catching(() -> {
                    for (int tick = 0; tick <= BelialRules.CHARIOT.timing().windup(); tick++) angel.skills().tick(angel, quarry, cooldown -> cooldown);
                }, true);
                chariot = (FieryChariot) joined.stream().filter(FieryChariot.class::isInstance).findFirst().orElseThrow();
                for (int tick = 0; tick < BelialRules.CHARIOT.runTicks() + 5 && !chariot.isRemoved(); tick++) chariot.tick();
                assertTrue(chariot.isRemoved() && chariot.wrecked(), "wrecked on the obsidian");
                assertTrue(level.getBlockState(BlockPos.containing(line[2]).atY(-60)).is(Blocks.OBSIDIAN), "which stands");
                assertEquals(quarry.getMaxHealth(), quarry.getHealth(), 1e-3, "and never reached its quarry behind it");
            } finally {
                if (chariot != null) chariot.discard();
                quarry.discard();
                angel.discard();
            }
        });
    }

    @Test void hisLikenessesAppearAndStruckHimselfTheyAreGoneAndHeHangsStunned(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 161_800);
            IronGolem quarry = golem(level, angel.getX(), angel.getZ() + 12);
            FakePlayer player = player(level, "belial_finder");
            List<Entity> joined = new ArrayList<>();
            try {
                hollow(level, angel);
                angel.setTarget(quarry);
                joined.addAll(catching(() -> run(angel, Skill.CLONES, quarry), true));
                List<Belial> likenesses = angel.likenesses();
                assertEquals(BelialRules.LIKENESSES, likenesses.size(), "four likenesses");
                assertTrue(angel.split());
                for (Belial likeness : likenesses) {
                    assertTrue(likeness.likeness() && likeness.hollow() && likeness.split(), "each the hollow angel, as he is");
                    assertEquals(BelialRules.LIKENESS_HEALTH, likeness.getMaxHealth(), 1e-4);
                    assertFalse(likeness.barShown() || likeness.shouldBeSaved() || likeness.shouldDropLoot() || likeness.shouldDropExperience(),
                            "no bar, not kept, and it leaves nothing");
                }
                for (int tick = 0; tick < 40; tick++) {
                    angel.customServerAiStep();
                    for (Belial likeness : likenesses) if (likeness.isAlive()) likeness.customServerAiStep();
                }
                assertTrue(likenesses.get(0).hurt(ArcaneDamage.source(level, player), 5), "a likeness is hurt");
                angel.customServerAiStep();
                assertTrue(angel.split(), "and he is not found out by it");
                angel.invulnerableTime = 0;
                assertTrue(angel.hurt(ArcaneDamage.source(level, player), 5), "struck himself");
                angel.customServerAiStep();
                assertFalse(angel.split(), "they are gone");
                for (Belial likeness : likenesses) assertTrue(likeness.isRemoved(), "every one");
                assertEquals(BelialRules.STUN_TICKS, angel.stunned(), "and he hangs stunned");
                assertTrue(angel.skills().cooling(angel.slot(Skill.CLONES)) >= BelialRules.CLONES_COOLDOWN - 1, "he does not call them again soon");
                for (int tick = 0; tick < 40; tick++) {
                    angel.customServerAiStep();
                    assertEquals(Skill.NONE, angel.skill(), "stunned, he does nothing");
                }
            } finally {
                joined.forEach(Entity::discard);
                quarry.discard();
                angel.discard();
            }
        });
    }

    @Test void hisStrikeAtTheMindHurtsAndCloudsHisQuarryAndThoseNearIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Belial angel = belial(level, 162_000);
            Villager quarry = still(EntityType.VILLAGER.create(level), level, angel.getX(), angel.getZ() + 10);
            Villager near = still(EntityType.VILLAGER.create(level), level, angel.getX() + 2, angel.getZ() + 10);
            Villager far = still(EntityType.VILLAGER.create(level), level, angel.getX() + 8, angel.getZ() + 10);
            try {
                hollow(level, angel);
                run(angel, Skill.PSYCHIC, quarry);
                assertTrue(angel.psychic().struck().contains(quarry) && angel.psychic().struck().contains(near), "his quarry and the one beside it");
                assertFalse(angel.psychic().struck().contains(far), "not the one further off");
                for (Villager mind : List.of(quarry, near)) {
                    assertTrue(mind.getHealth() < mind.getMaxHealth(), "hurt");
                    assertTrue(mind.hasEffect(MobEffects.CONFUSION) && mind.hasEffect(MobEffects.DARKNESS) && mind.hasEffect(MobEffects.MOVEMENT_SLOWDOWN),
                            "its sight swimming and darkened, its limbs heavy");
                }
                assertTrue(BelialRules.PSYCHIC.timing().cooldown() <= 60, "and he will strike again soon");
            } finally {
                quarry.discard();
                near.discard();
                far.discard();
                angel.discard();
            }
        });
    }

    @Test void theRiteAsksForOfferingsAndAVillagerAndHeLeavesHisHaloBehind(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var recipe = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", "altar/belial")).orElseThrow().value();
            AltarRecipe rite = assertInstanceOf(AltarRecipe.class, recipe);
            assertEquals(EntityType.VILLAGER, rite.offering().orElseThrow(), "a villager given up");
            Map<List<Integer>, Block> ground = Map.of(List.of(2, 2), Blocks.MAGMA_BLOCK, List.of(2, -2), Blocks.MAGMA_BLOCK, List.of(-2, -2), Blocks.MAGMA_BLOCK,
                    List.of(-2, 2), Blocks.MAGMA_BLOCK, List.of(0, 2), Blocks.GOLD_BLOCK, List.of(0, -2), Blocks.GOLD_BLOCK);
            assertEquals(ground.size(), rite.blocks().size());
            for (GroundBlock block : rite.blocks())
                assertTrue(block.test(ground.get(List.of(block.east(), block.north())).defaultBlockState()), "fire at the corners, gold before and behind");
            ItemStack parchment = new ItemStack(ModItems.PARCHMENT.get());
            assertTrue(rite.pillars().get(0).test(ArcaneApi.inscribed(parchment, ArcaneApi.key("corruption:belial"))), "the first pillar bears his own string");
            assertFalse(rite.pillars().get(0).test(parchment));
            for (var item : List.of(Items.BLAZE_ROD, Items.GOLD_BLOCK, Items.GHAST_TEAR, ModItems.CORRUPTED_HEARTSTONE.get(), ModItems.CHAOS_CATALYST.get()))
                assertTrue(rite.pillars().stream().anyMatch(pillar -> pillar.test(new ItemStack(item))), item + " on a pillar");
            assertEquals(12, rite.corruption());
            assertEquals(ModEntities.BELIAL.get(), rite.result().creature().orElseThrow(), "and Belial comes forth");
            ResourceKey<LootTable> his = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("corruption", "entities/belial"));
            assertEquals(his, ModEntities.BELIAL.get().getDefaultLootTable());
            assertNotSame(LootTable.EMPTY, server.reloadableRegistries().getLootTable(his), "he leaves something behind");
        });
    }
}
