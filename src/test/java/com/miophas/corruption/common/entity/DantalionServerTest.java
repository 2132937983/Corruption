package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneDamage;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.demon.skill.SkillSet;
import com.miophas.corruption.common.altar.AltarRecipe;
import com.miophas.corruption.common.entity.skill.BlinkSkill;
import com.miophas.corruption.common.entity.skill.MindControlSkill;
import com.miophas.corruption.common.entity.skill.SummonSkill;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.DantalionRules;
import com.miophas.corruption.core.demon.DantalionRules.Skill;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;
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
 * Dantalion on a real server: what he is, his change, each of his skills run through his own set of them by hand (so
 * that each test sets up just what it means to), the rite that brings him forth, and his death. The test world never
 * brings new entities into sight, so what his skills bring forth is caught as it joins.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class DantalionServerTest {
    /** Dantalion at x, a few blocks over the flat ground, facing south (+Z). */
    private static Dantalion dantalion(ServerLevel level, int x) {
        TestWorld.load(level, x - 40, x + 40, 3000 - 24, 3000 + 24);
        Dantalion book = ModEntities.DANTALION.get().create(level);
        book.moveTo(x + .5, -57, 3000.5, 0, 0);
        book.yBodyRot = 0;
        book.yHeadRot = 0;
        level.addFreshEntity(book);
        return book;
    }
    private static FakePlayer player(ServerLevel level, double x, double z, String name) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.moveTo(x, -60, z, 180, 0);
        player.setGameMode(GameType.SURVIVAL);
        player.removeAllEffects();
        player.setHealth(player.getMaxHealth());
        return player;
    }
    private static Pig pig(ServerLevel level, double x, double z) {
        Pig pig = EntityType.PIG.create(level);
        pig.moveTo(x, -60, z, 0, 0);
        pig.setNoAi(true);
        level.addFreshEntity(pig);
        return pig;
    }
    private static List<Entity> catching(Runnable run) {
        List<Entity> joined = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> joined.add(event.getEntity());
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try { run.run(); }
        finally { NeoForge.EVENT_BUS.unregister(catcher); }
        joined.forEach(Entity::discard);
        return joined;
    }
    /** Runs one of his skills on this quarry, from its beginning to its end, by hand. */
    private static void run(Dantalion boss, Skill skill, LivingEntity target, Runnable everyTick) {
        SkillSet<Dantalion> skills = boss.skills();
        int index = DantalionRules.SKILLS.indexOf(skill);
        skills.comeRound(index);
        assertTrue(skills.begin(index, boss, target), skill + " begins");
        for (int tick = 0; tick < 400 && skills.busy(); tick++) {
            skills.tick(boss, target, cooldown -> cooldown);
            everyTick.run();
        }
        assertFalse(skills.busy(), skill + " runs its course");
    }
    private static long count(List<Entity> joined, String effect) {
        return joined.stream().filter(entity -> entity instanceof SpellVisualEntity shown && ResourceLocation.parse("corruption:" + effect).equals(shown.effect())).count();
    }

    @Test void heIsAFlyingDemonOfTheCorruptionAndRidesNothing(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 60_000);
            Andromalius serpent = ModEntities.ANDROMALIUS.get().create(level);
            Baal baal = ModEntities.BAAL.get().create(level);
            Boat boat = EntityType.BOAT.create(level);
            try {
                assertInstanceOf(GoeticDemon.class, book);
                assertTrue(CorruptionFactionApi.isRemnant(book), "of the corruption's side");
                assertTrue(book.isNoGravity(), "he flies");
                assertEquals(DantalionRules.MAX_HEALTH, book.getMaxHealth());
                assertEquals(3, book.emblem());
                boat.moveTo(book.position());
                level.addFreshEntity(boat);
                assertFalse(book.startRiding(boat, true), "a boat would only hold him fast");
                assertFalse(serpent.startRiding(boat, true), "nor Andromalius");
                assertFalse(baal.startRiding(boat, true), "nor a colossus");
                assertFalse(book.hurt(level.damageSources().generic(), 10), "only the arcane hurts him");
            } finally {
                boat.discard();
                book.discard();
            }
        });
    }

    /** Brought to half his health he changes, untouchable, into the mirror, and rises from it whole. */
    @Test void atHalfHisHealthHeBecomesTheMirrorAndNothingHurtsHimWhileHeChanges(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 60_400);
            FakePlayer player = player(level, book.getX(), book.getZ() + 12, "dantalion_changer");
            try {
                assertFalse(book.mirror());
                assertEquals(DantalionRules.BOOK_HEIGHT, book.getBbHeight(), 1e-4);
                book.setHealth(book.getMaxHealth() * .49F);
                book.customServerAiStep();
                assertEquals(DantalionRules.CHANGE_TICKS, book.changing(), "he begins to change");
                float health = book.getHealth();
                assertFalse(book.hurt(ArcaneDamage.source(level, player), 20), "nothing hurts him while he changes");
                assertEquals(health, book.getHealth(), 1e-4);
                List<Entity> joined = catching(() -> {
                    for (int tick = 0; tick < DantalionRules.CHANGE_TICKS / 2; tick++) book.customServerAiStep();
                    assertTrue(book.getHealth() > health && book.getHealth() < book.getMaxHealth(), "his health filling back up as he changes: " + book.getHealth());
                    for (int tick = DantalionRules.CHANGE_TICKS / 2; tick < DantalionRules.CHANGE_TICKS; tick++) book.customServerAiStep();
                });
                assertEquals(book.getMaxHealth(), book.getHealth(), 1e-4, "he rises whole");
                assertFalse(book.changing() > 0 || DantalionRules.changes(book.getHealth() / book.getMaxHealth()) && !book.mirror(), "and does not change again");
                assertTrue(book.mirror(), "he is the mirror");
                assertEquals(0, book.changing(), "and done changing");
                assertEquals(DantalionRules.MIRROR_HEIGHT, book.getBbHeight(), 1e-4, "standing taller");
                assertTrue(count(joined, "nova") >= 1 && count(joined, "shockwave") >= 1, "in a burst of his light, and a ring of it");
                assertTrue(book.hurt(ArcaneDamage.source(level, player), 5), "and can be hurt again");
            } finally {
                book.discard();
            }
        });
    }

    @Test void hisGazeTurnsAMindAndHisGreaterGazeWithersIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 60_800);
            FakePlayer player = player(level, book.getX(), book.getZ() + 10, "dantalion_gazed");
            try {
                List<Entity> joined = catching(() -> run(book, Skill.GAZE, player, () -> {}));
                assertEquals(1, count(joined, "eye"), "an Eye of Providence over him");
                assertTrue(player.hasEffect(MobEffects.BLINDNESS) && player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "blinded and slowed");
                assertFalse(player.hasEffect(MobEffects.WITHER));
                player.removeAllEffects();
                joined = catching(() -> run(book, Skill.GREATER_GAZE, player, () -> {}));
                assertEquals(DantalionRules.GREATER_GAZE.eyes(), count(joined, "eye"), "more Eyes of Providence, round his quarry too");
                assertTrue(player.hasEffect(MobEffects.WITHER), "and withering");
                assertEquals(DantalionRules.GREATER_GAZE.blindnessTicks(), player.getEffect(MobEffects.BLINDNESS).getDuration(), 2, "for longer");
            } finally {
                book.discard();
            }
        });
    }

    @Test void hisLightningIsMarkedBeforeItFalls(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 61_200);
            Pig pig = pig(level, book.getX(), book.getZ() + 12);
            try {
                List<Entity> bolts = new ArrayList<>();
                List<Entity> joined = catching(() -> run(book, Skill.STRIKE, pig, () -> {}));
                joined.stream().filter(entity -> entity instanceof CorruptLightning).forEach(bolts::add);
                assertEquals(1, count(joined, "magic_circle"), "the place marked");
                assertEquals(1, bolts.size(), "one bolt");
                assertTrue(bolts.get(0).position().distanceTo(new Vec3(pig.getX(), bolts.get(0).getY(), pig.getZ())) < .01, "where its quarry stood");
                assertTrue(pig.getHealth() < pig.getMaxHealth(), "and it hurt");

                bolts.clear();
                joined = catching(() -> run(book, Skill.STORM, pig, () -> {}));
                joined.stream().filter(entity -> entity instanceof CorruptLightning).forEach(bolts::add);
                assertEquals(DantalionRules.STORM.bolts(), count(joined, "magic_circle"), "every place marked at once");
                assertEquals(DantalionRules.STORM.bolts(), bolts.size(), "a storm of bolts");
                for (Entity bolt : bolts) assertTrue(Math.hypot(bolt.getX() - pig.getX(), bolt.getZ() - pig.getZ()) <= DantalionRules.STORM.scatter() + .01, "round its quarry");
            } finally {
                pig.discard();
                book.discard();
            }
        });
    }

    @Test void heCallsUpRemnantsInTheLikenessesOfTheDeadNoMoreThanHeMayKeep(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 61_600);
            Pig pig = pig(level, book.getX(), book.getZ() + 12);
            List<CorruptedRemnant> raised = new ArrayList<>();
            Consumer<EntityJoinLevelEvent> keep = event -> { if (event.getEntity() instanceof CorruptedRemnant remnant) raised.add(remnant); };
            NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, keep);
            try {
                run(book, Skill.SUMMON, pig, () -> {});
                assertEquals(DantalionRules.SUMMON.count(), raised.size(), "a band of them");
                assertEquals(DantalionRules.SUMMON.count(), SummonSkill.called(book), "each marked as his");
                for (CorruptedRemnant remnant : raised) {
                    assertTrue(DantalionRules.SUMMON.looks().contains(remnant.appearance().getString("Type")), "in a likeness asked for: " + remnant.appearance());
                    assertSame(pig, remnant.getTarget(), "set on his quarry at once");
                    assertTrue(remnant.distanceTo(book) < DantalionRules.SUMMON.radius() + 6, "round him");
                }
                run(book, Skill.SUMMON, pig, () -> {});
                assertEquals(DantalionRules.SUMMON.most(), SummonSkill.called(book));
                assertFalse(book.skills().skill(DantalionRules.SKILLS.indexOf(Skill.SUMMON)).usable(book, pig), "and no more than he may keep");
            } finally {
                NeoForge.EVENT_BUS.unregister(keep);
                raised.forEach(Entity::discard);
                pig.discard();
                book.discard();
            }
        });
    }

    @Test void inDangerHeBlinksToWhereHeSeesHisQuarryFromAfar(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 62_000);
            Pig pig = pig(level, book.getX(), book.getZ() + 3);
            try {
                BlinkSkill blink = (BlinkSkill) book.skills().skill(DantalionRules.SKILLS.indexOf(Skill.BLINK));
                assertTrue(blink.weigh(book, pig), "his quarry nearer than he lets it come: not safe");
                Vec3 from = book.position();
                List<Entity> joined = catching(() -> run(book, Skill.BLINK, pig, () -> {}));
                assertTrue(book.position().distanceTo(from) > 5, "he is gone from there");
                double distance = Math.hypot(book.getX() - pig.getX(), book.getZ() - pig.getZ());
                assertTrue(distance >= DantalionRules.BLINK.nearest() - .01 && distance <= DantalionRules.BLINK.farthest() + .01, "to a middle distance from his quarry: " + distance);
                assertTrue(book.hasLineOfSight(pig), "in sight of it");
                assertTrue(count(joined, "rift") >= 2, "a rift where he was, and where he is");
            } finally {
                pig.discard();
                book.discard();
            }
        });
    }

    @Test void asTheMirrorHisBeamFollowsAndBurnsWhatItCatches(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 62_400);
            Pig pig = pig(level, book.getX(), book.getZ() + 10);
            pig.setHealth(pig.getMaxHealth());
            try {
                List<DemonBeamEntity> beams = new ArrayList<>();
                boolean[] fired = {false};
                List<Entity> joined = catching(() -> run(book, Skill.BEAM, pig, () -> {
                    if (book.skills().running() >= 0 && beams.isEmpty())
                        level.getEntitiesOfClass(DemonBeamEntity.class, book.getBoundingBox().inflate(4)).forEach(beams::add);
                    if (!beams.isEmpty() && beams.get(0).fired()) fired[0] = true;
                }));
                assertEquals(1, joined.stream().filter(entity -> entity instanceof DemonBeamEntity).count(), "one beam stood up");
                assertTrue(fired[0], "it fires once it has gathered");
                assertTrue(pig.getHealth() < pig.getMaxHealth() || !pig.isAlive(), "and burns what it catches");
                assertTrue(beams.get(0).isRemoved(), "and is gone when it is over");
            } finally {
                pig.discard();
                book.discard();
            }
        });
    }

    /**
     * His greater mind control: once he has taken the player's mind the skill is over, but the hold goes on for as long
     * as its rules say, and meanwhile he is free to turn his beam on the player he holds; then he lets go.
     */
    @Test void heTakesHoldOfAPlayersMindAndBurnsThemWhileHeHoldsIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 62_800);
            FakePlayer player = player(level, book.getX(), book.getZ() + 10, "dantalion_held");
            Pig pig = pig(level, book.getX() + 2, book.getZ() + 10);
            try {
                int index = DantalionRules.SKILLS.indexOf(Skill.GREATER_MIND_CONTROL), beam = DantalionRules.SKILLS.indexOf(Skill.BEAM);
                MindControlSkill taking = (MindControlSkill) book.skills().skill(index);
                assertFalse(taking.usable(book, pig), "only a player's mind can be taken");
                assertTrue(taking.usable(book, player));
                catching(() -> run(book, Skill.GREATER_MIND_CONTROL, player, () -> {}));
                assertTrue(book.held(player), "the skill over, the player is still held");
                assertEquals(DantalionRules.GREATER_MIND_CONTROL.timing().windup() + 1, DantalionRules.GREATER_MIND_CONTROL.timing().ticks(),
                        "the skill is only the reaching and the taking");
                book.skills().comeRound(beam);
                assertTrue(book.skills().begin(beam, book, player), "he may turn his beam on the one he holds");
                int ticks = 0;
                while (book.held(player) && ticks++ < 200) catching(() -> book.skills().tick(book, player, cooldown -> cooldown));
                assertEquals(DantalionRules.GREATER_MIND_CONTROL.holds(), ticks, 2, "held for as long as the rules say");
                assertFalse(book.held(player), "and let go");
                assertNull(taking.held());
                book.skills().interrupt(book, cooldown -> cooldown);
            } finally {
                pig.discard();
                book.discard();
            }
        });
    }

    @Test void dyingHeLetsGoOfTheMindHeHolds(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 63_600);
            FakePlayer player = player(level, book.getX(), book.getZ() + 10, "dantalion_dying_hold");
            try {
                catching(() -> run(book, Skill.MIND_CONTROL, player, () -> {}));
                assertTrue(book.held(player));
                assertTrue(book.hurt(level.damageSources().genericKill(), Float.MAX_VALUE));
                assertFalse(book.held(player), "his death lets go of it");
            } finally {
                book.discard();
            }
        });
    }

    @Test void theRiteAsksForEightEnchantingTablesAMendingBookAndAVillager(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var recipe = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", "altar/dantalion")).orElseThrow().value();
            assertInstanceOf(AltarRecipe.class, recipe);
            AltarRecipe rite = (AltarRecipe) recipe;
            assertEquals(EntityType.VILLAGER, rite.offering().orElseThrow(), "a villager given up");
            assertEquals(8, rite.blocks().size(), "eight enchanting tables round it");
            for (var block : rite.blocks()) assertTrue(block.test(Blocks.ENCHANTING_TABLE.defaultBlockState()), "each an enchanting table");
            ItemStack mending = new ItemStack(Items.ENCHANTED_BOOK), sharpness = new ItemStack(Items.ENCHANTED_BOOK);
            var enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            ItemEnchantments.Mutable one = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            one.set(enchantments.getOrThrow(Enchantments.MENDING), 1);
            mending.set(DataComponents.STORED_ENCHANTMENTS, one.toImmutable());
            ItemEnchantments.Mutable other = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            other.set(enchantments.getOrThrow(Enchantments.SHARPNESS), 1);
            sharpness.set(DataComponents.STORED_ENCHANTMENTS, other.toImmutable());
            assertTrue(rite.pillars().stream().anyMatch(pillar -> pillar.test(mending)), "a Mending book on a pillar");
            assertFalse(rite.pillars().stream().anyMatch(pillar -> pillar.test(sharpness)), "no other enchanted book will do");
            assertTrue(rite.corruption() > 3, "asked of one further gone than Andromalius's rite asks");
        });
    }

    @Test void heDiesFallingToTheGroundAndIsGoneAtTheEndOfHisDeath(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Dantalion book = dantalion(level, 63_200);
            try {
                double high = book.getY();
                assertTrue(book.hurt(level.damageSources().genericKill(), Float.MAX_VALUE));
                BossDeath death = book.death();
                for (int tick = 0; tick < death.ticks(); tick++) book.tick();
                assertTrue(book.getY() < high - 1, "he sinks to the ground as he dies");
                assertFalse(book.isRemoved(), "his body gone in his light, he lingers while his death plays out");
                for (int tick = 0; tick < death.linger(); tick++) book.tick();
                assertTrue(book.isRemoved(), "and then he is gone");
            } finally {
                book.discard();
            }
        });
    }
}
