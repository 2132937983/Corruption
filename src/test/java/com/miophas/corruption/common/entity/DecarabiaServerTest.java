package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneDamage;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.altar.AltarRecipe;
import com.miophas.corruption.common.altar.GroundBlock;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.demon.DecarabiaRules.Skill;
import com.miophas.corruption.core.demon.DecarabiaRules;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Pig;
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
 * Decarabia on a real server: what he is, his skills as the star and as the goshawk (each begun by hand and run through
 * his own set of them), his split into five and his likenesses' coming back, his stun, and his change. The test world
 * never brings new entities into sight, so what his skills bring forth is caught as it joins.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class DecarabiaServerTest {
    /** Decarabia at x, hovering a few blocks over the flat ground, facing south (+Z). */
    private static Decarabia decarabia(ServerLevel level, int x) {
        TestWorld.load(level, x - 40, x + 40, 3000 - 40, 3000 + 40);
        Decarabia star = ModEntities.DECARABIA.get().create(level);
        star.moveTo(x + .5, -56, 3000.5, 0, 0);
        level.addFreshEntity(star);
        return star;
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
    /** Runs one of his skills on this quarry from its beginning to its end, by hand, ticking the quarry too; how many ticks it took. */
    private static int run(Decarabia star, Skill skill, LivingEntity target) {
        star.skills().comeRound(DecarabiaRules.SKILLS.indexOf(skill));
        assertTrue(star.begin(skill, target), skill + " begins");
        int ticks = 0;
        for (; ticks < 400 && star.skills().busy(); ticks++) {
            star.skills().tick(star, target, cooldown -> cooldown);
            star.travel(Vec3.ZERO);
        }
        assertFalse(star.skills().busy(), skill + " runs its course");
        return ticks;
    }
    /** Makes him the goshawk, as his change does, by living it through. */
    private static void goshawk(Decarabia star) {
        star.setHealth(star.getMaxHealth() * .4F);
        star.customServerAiStep();
        for (int tick = 0; tick < DecarabiaRules.CHANGE_TICKS + 2 && star.changing() > 0; tick++) star.customServerAiStep();
        assertTrue(star.hawk());
    }
    private static long bolts(List<Entity> joined) { return joined.stream().filter(DemonBolt.class::isInstance).count(); }
    private static boolean hurt(LivingEntity creature) { return !creature.isAlive() || creature.getHealth() < creature.getMaxHealth(); }

    @Test void heIsAFlyingStarOfTheCorruptionThatOnlyTheArcaneHurts(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia star = decarabia(level, 140_000);
            try {
                assertInstanceOf(GoeticDemon.class, star);
                assertTrue(CorruptionFactionApi.isRemnant(star), "of the corruption's side");
                assertTrue(CorruptionFactionApi.isBoss(star), "one of its lords");
                assertTrue(star.isNoGravity(), "he flies");
                assertFalse(star.hawk(), "he comes forth as the star");
                assertEquals(DecarabiaRules.MAX_HEALTH, star.getMaxHealth());
                assertEquals(DecarabiaRules.STAR_SIZE, star.getBbWidth(), 1e-4);
                assertEquals(5, star.emblem());
                assertFalse(star.hurt(level.damageSources().generic(), 10), "only the arcane hurts him");
            } finally {
                star.discard();
            }
        });
    }

    @Test void hisVolleyPoursBoltsFromHisFivePointsAtHisQuarry(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia star = decarabia(level, 140_400);
            IronGolem quarry = golem(level, star.getX(), star.getZ() + 12);
            try {
                List<Entity> joined = catching(() -> run(star, Skill.VOLLEY, quarry), true);
                List<DemonBolt> shot = joined.stream().filter(DemonBolt.class::isInstance).map(DemonBolt.class::cast).toList();
                assertEquals(DecarabiaRules.VOLLEY.bolts(), shot.size(), "five points, every beat");
                assertEquals(shot.size(), star.volley().loosed());
                for (DemonBolt bolt : shot) {
                    assertSame(star, bolt.demon(), "his own");
                    Vec3 toQuarry = quarry.getBoundingBox().getCenter().subtract(bolt.position()).normalize();
                    assertTrue(bolt.getDeltaMovement().normalize().dot(toQuarry) > .95, "each at his quarry");
                }
                for (int tick = 0; tick < 40; tick++) for (DemonBolt bolt : shot) if (!bolt.isRemoved()) bolt.tick();
                assertTrue(hurt(quarry), "and they strike it");
                joined.forEach(Entity::discard);
            } finally {
                quarry.discard();
                star.discard();
            }
        });
    }

    @Test void hisSpinFliesAtHisQuarryAndStrikesItAgainAndAgain(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia star = decarabia(level, 140_800);
            IronGolem quarry = golem(level, star.getX(), star.getZ() + 5);
            try {
                double before = star.distanceTo(quarry);
                catching(() -> run(star, Skill.SPIN, quarry), false);
                assertTrue(star.distanceTo(quarry) < before, "he flew at it");
                assertTrue(hurt(quarry), "struck");
                assertTrue(((com.miophas.corruption.common.entity.skill.SpinSkill) star.skills().skill(DecarabiaRules.SKILLS.indexOf(Skill.SPIN))).blows() > 1,
                        "again and again");
            } finally {
                quarry.discard();
                star.discard();
            }
        });
    }

    @Test void heSplitsIntoFiveSmallStarsAndStruckHimselfTheyComeBackAndHeHangsStunned(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia star = decarabia(level, 141_200);
            IronGolem quarry = golem(level, star.getX(), star.getZ() + 12);
            FakePlayer player = player(level, "decarabia_finder");
            List<Entity> joined = new ArrayList<>();
            try {
                Vec3 from = star.position();
                star.setTarget(quarry);
                joined.addAll(catching(() -> run(star, Skill.SPLIT, quarry), true));
                List<Decarabia> likenesses = star.likenesses();
                assertEquals(DecarabiaRules.LIKENESSES, likenesses.size(), "four likenesses");
                assertTrue(star.split() && star.small(), "and he small among them");
                for (Decarabia likeness : likenesses) {
                    assertTrue(likeness.likeness() && likeness.small() && likeness.split());
                    assertTrue(joined.contains(likeness));
                    assertEquals(DecarabiaRules.LIKENESS_HEALTH, likeness.getMaxHealth(), 1e-4);
                    assertFalse(likeness.barShown(), "no bar of its own");
                    assertFalse(likeness.shouldBeSaved(), "nor kept");
                    assertFalse(likeness.shouldDropLoot() || likeness.shouldDropExperience(), "and it leaves nothing");
                }
                assertEquals(DecarabiaRules.SCATTER, Math.hypot(star.getX() - from.x, star.getZ() - from.z), .01, "he too went to a point of the five");
                // His likenesses go about as small stars do.
                for (int tick = 0; tick < 60; tick++) {
                    star.customServerAiStep();
                    for (Decarabia likeness : likenesses) if (likeness.isAlive()) likeness.customServerAiStep();
                }
                Decarabia struck = likenesses.get(0);
                assertTrue(struck.hurt(ArcaneDamage.source(level, player), 5), "a likeness is hurt");
                star.customServerAiStep();
                assertTrue(star.split(), "and he is not found out by it");
                assertTrue(star.hurt(ArcaneDamage.source(level, player), 5), "struck himself");
                star.customServerAiStep();
                assertFalse(star.split() || star.small(), "they come back into him, and he is whole in size");
                for (Decarabia likeness : likenesses) assertTrue(likeness.isRemoved(), "every likeness gone");
                assertEquals(DecarabiaRules.STUN_TICKS, star.stunned(), "and he hangs stunned");
                assertTrue(star.skills().cooling(DecarabiaRules.SKILLS.indexOf(Skill.SPLIT)) >= DecarabiaRules.SPLIT_COOLDOWN - 1, "he does not split again soon");
                for (int tick = 0; tick < 40; tick++) {
                    star.customServerAiStep();
                    assertEquals(Skill.NONE, star.skill(), "stunned, he does nothing");
                }
            } finally {
                joined.forEach(Entity::discard);
                quarry.discard();
                star.discard();
            }
        });
    }

    @Test void leftUnfoundTheyComeBackAllTheSameButHeIsNotStunned(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia star = decarabia(level, 141_600);
            IronGolem quarry = golem(level, star.getX(), star.getZ() + 12);
            List<Entity> joined = new ArrayList<>();
            try {
                joined.addAll(catching(() -> run(star, Skill.SPLIT, quarry), true));
                assertTrue(star.split());
                for (int tick = 0; tick <= DecarabiaRules.SPLIT_MOST && star.split(); tick++) star.customServerAiStep();
                assertFalse(star.split(), "back whole in time");
                assertEquals(0, star.stunned(), "not stunned: he was not found out");
            } finally {
                joined.forEach(Entity::discard);
                quarry.discard();
                star.discard();
            }
        });
    }

    @Test void atHalfHisHealthHeBecomesAGoshawkWholeAgainAndUntouchableThroughIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia star = decarabia(level, 142_000);
            FakePlayer player = player(level, "decarabia_changer");
            try {
                star.setHealth(star.getMaxHealth() * .45F);
                star.customServerAiStep();
                assertEquals(DecarabiaRules.CHANGE_TICKS, star.changing(), "his change begins");
                assertFalse(star.hurt(ArcaneDamage.source(level, player), 20), "nothing hurts him through it");
                for (int tick = 0; tick < DecarabiaRules.CHANGE_TICKS && star.changing() > 0; tick++) star.customServerAiStep();
                assertTrue(star.hawk(), "the goshawk");
                assertEquals(star.getMaxHealth(), star.getHealth(), 1e-3, "made whole");
                assertEquals(DecarabiaRules.HAWK_HEIGHT, star.getBbHeight(), 1e-4, "his box a bird's");
                assertEquals(DecarabiaRules.HAWK_SPEED, star.getAttributeValue(Attributes.FLYING_SPEED), 1e-6, "and faster");
                assertTrue(star.hurt(ArcaneDamage.source(level, player), 5), "and hurt again after it");
            } finally {
                star.discard();
            }
        });
    }

    @Test void theGoshawksScreechStunsWhatIsNearAndAStunnedCreatureCannotStrike(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia hawk = decarabia(level, 142_400);
            Villager near = still(EntityType.VILLAGER.create(level), level, hawk.getX() + 4, hawk.getZ() + 4);
            IronGolem striker = golem(level, hawk.getX() - 3, hawk.getZ() + 3);
            Villager far = still(EntityType.VILLAGER.create(level), level, hawk.getX() + 30, hawk.getZ());
            Pig struck = still(EntityType.PIG.create(level), level, hawk.getX() - 4, hawk.getZ() + 3);
            try {
                goshawk(hawk);
                catching(() -> run(hawk, Skill.SCREECH, near), false);
                assertTrue(near.hasEffect(ModEffects.STUNNED) && striker.hasEffect(ModEffects.STUNNED), "what is near is stunned");
                assertFalse(far.hasEffect(ModEffects.STUNNED), "what is far is not");
                assertEquals(DecarabiaRules.SCREECH.stunTicks(), near.getEffect(ModEffects.STUNNED).getDuration());
                near.tick();
                assertEquals(0, near.getAttributeValue(Attributes.MOVEMENT_SPEED), 1e-9, "it cannot move");
                assertFalse(striker.doHurtTarget(struck) && hurt(struck), "nor strike");
                assertFalse(hurt(struck));
            } finally {
                near.discard();
                striker.discard();
                far.discard();
                struck.discard();
                hawk.discard();
            }
        });
    }

    @Test void hisStoopIsOverAsSoonAsItStrikes(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia hawk = decarabia(level, 142_800);
            IronGolem quarry = golem(level, hawk.getX() + 4, hawk.getZ() + 10);
            try {
                goshawk(hawk);
                hawk.moveTo(hawk.getX(), -48, hawk.getZ());
                int ticks = catching(() -> {}, false).size();
                ticks = run(hawk, Skill.DIVE, quarry);
                assertTrue(hawk.dive().struck(), "he struck");
                assertTrue(hurt(quarry), "hurting what stood there");
                assertTrue(ticks < DecarabiaRules.DIVE.timing().ticks() - 10, "and was done at once, not resting out the skill's time: " + ticks);
            } finally {
                quarry.discard();
                hawk.discard();
            }
        });
    }

    @Test void hisStoopsComeOneAfterAnother(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia hawk = decarabia(level, 143_200);
            IronGolem quarry = golem(level, hawk.getX() + 3, hawk.getZ() + 8);
            try {
                goshawk(hawk);
                hawk.moveTo(hawk.getX(), -50, hawk.getZ());
                List<Entity> joined = catching(() -> run(hawk, Skill.DIVES, quarry), false);
                assertEquals(DecarabiaRules.DIVES.dives(), hawk.dives().struck(), "every one of them struck");
                assertTrue(joined.stream().filter(entity -> entity instanceof SpellVisualEntity shown
                        && "corruption:magic_circle".equals(shown.effect().toString())).count() >= DecarabiaRules.DIVES.dives(), "each marked first");
                assertTrue(hurt(quarry));
            } finally {
                quarry.discard();
                hawk.discard();
            }
        });
    }

    @Test void hisBarrageRainsSixtyFourBoltsOnWhereHisQuarryStood(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Decarabia hawk = decarabia(level, 143_600);
            IronGolem quarry = golem(level, hawk.getX(), hawk.getZ() + 14);
            try {
                goshawk(hawk);
                hawk.moveTo(hawk.getX(), -48, hawk.getZ());
                List<Entity> joined = catching(() -> run(hawk, Skill.BARRAGE, quarry), true);
                assertEquals(64, bolts(joined), "sixty-four bolts");
                Vec3 mark = hawk.barrage().mark() == null ? quarry.position() : hawk.barrage().mark();
                List<DemonBolt> shot = joined.stream().filter(DemonBolt.class::isInstance).map(DemonBolt.class::cast).toList();
                for (int tick = 0; tick < DecarabiaRules.BARRAGE.flight() + 5; tick++) for (DemonBolt bolt : shot) if (!bolt.isRemoved()) bolt.tick();
                assertTrue(shot.stream().allMatch(Entity::isRemoved), "every one came down");
                assertTrue(hurt(quarry), "on where it stood");
                joined.forEach(Entity::discard);
                assertNotNull(mark);
            } finally {
                quarry.discard();
                hawk.discard();
            }
        });
    }

    @Test void theRiteAsksForAVillagerGivenUpInAHexagonOfPreciousBlocks(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var recipe = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", "altar/decarabia")).orElseThrow().value();
            AltarRecipe rite = assertInstanceOf(AltarRecipe.class, recipe);
            assertTrue(rite.shaped(), "each pillar its own");
            assertEquals(EntityType.VILLAGER, rite.offering().orElseThrow(), "a villager given up");
            // One block of each, at the corners of a hexagon round the offering, inside the pillars' own.
            Map<List<Integer>, Block> hexagon = Map.of(List.of(0, 2), Blocks.DIAMOND_BLOCK, List.of(2, 1), Blocks.GOLD_BLOCK, List.of(2, -1), Blocks.EMERALD_BLOCK,
                    List.of(0, -2), Blocks.LAPIS_BLOCK, List.of(-2, -1), Blocks.REDSTONE_BLOCK, List.of(-2, 1), Blocks.QUARTZ_BLOCK);
            assertEquals(6, rite.blocks().size(), "six blocks");
            for (GroundBlock ground : rite.blocks()) {
                Block wanted = hexagon.get(List.of(ground.east(), ground.north()));
                assertNotNull(wanted, "at a corner of the hexagon: " + ground.east() + ", " + ground.north());
                assertTrue(ground.test(wanted.defaultBlockState()), wanted + " where it should be");
                for (Block other : hexagon.values()) if (other != wanted) assertFalse(ground.test(other.defaultBlockState()), "and no other there");
            }
            ItemStack parchment = new ItemStack(ModItems.PARCHMENT.get());
            assertTrue(rite.pillars().get(0).test(ArcaneApi.inscribed(parchment, ArcaneApi.key("corruption:decarabia"))), "the first pillar bears his own string");
            assertFalse(rite.pillars().get(0).test(parchment), "not a blank parchment");
            for (var item : List.of(Items.FEATHER, Items.DIAMOND, Items.EMERALD, ModItems.CORRUPTED_HEARTSTONE.get(), ModItems.ABYSSAL_PEARL.get()))
                assertTrue(rite.pillars().stream().anyMatch(pillar -> pillar.test(new ItemStack(item))), item + " on a pillar");
            assertEquals(10, rite.corruption(), "asked of one far gone");
            assertEquals(ModEntities.DECARABIA.get(), rite.result().creature().orElseThrow(), "and Decarabia comes forth");
            ResourceKey<LootTable> his = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("corruption", "entities/decarabia"));
            assertEquals(his, ModEntities.DECARABIA.get().getDefaultLootTable());
            assertNotSame(LootTable.EMPTY, server.reloadableRegistries().getLootTable(his), "he leaves something behind");
        });
    }
}
