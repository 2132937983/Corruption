package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.elite.CorruptionElite;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.api.plant.MaliceVineApi;
import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.item.CorruptedRobeArmorItem;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.corruption.CorruptedRobeRules.Tier;
import com.miophas.corruption.core.demon.skill.SpitRules;
import com.miophas.corruption.core.elite.AberrationRules;
import com.miophas.corruption.core.elite.LurkerRules;
import com.miophas.corruption.core.elite.ScreecherRules;
import com.miophas.corruption.core.plant.MaliceVineRules;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The corruption's elites on a real server: what they are (of the corruption, no lords; any blow hurts them, the
 * corruption's curse and hurt do not; a novice's robes do not hide their wearer from them; they leave what a remnant
 * leaves); each one's skills, begun by hand and run through its own set of them; and the malice vine that broods them,
 * and defends itself. The test world never brings new entities into sight, so what is loosed or brought forth is caught
 * as it joins.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class CorruptionElitesServerTest {
    private static final int Z = 5000;

    private static <T extends Mob> T place(ServerLevel level, EntityType<T> type, double x, double y, double z) {
        TestWorld.load(level, (int) x - 48, (int) x + 48, Z - 48, Z + 48);
        T creature = type.create(level);
        creature.moveTo(x, y, z, 0, 0);
        level.addFreshEntity(creature);
        return creature;
    }
    private static <T extends Mob> T still(ServerLevel level, EntityType<T> type, double x, double z) {
        T creature = place(level, type, x, -60, z);
        creature.setNoAi(true);
        return creature;
    }
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
    /** Runs one of an elite's skills (by its place in its rules' Skill) on this quarry from beginning to end, by hand; how many ticks it took. */
    private static <T extends CorruptionElite<T>> int run(T elite, int skill, @Nullable LivingEntity target) {
        elite.skills().comeRound(skill - 1);
        assertTrue(elite.begin(skill, target), "skill " + skill + " begins");
        int ticks = 0;
        for (; ticks < 400 && elite.skills().busy(); ticks++) {
            elite.skills().tick(elite.host(), target, cooldown -> cooldown);
            elite.travel(Vec3.ZERO);
        }
        assertFalse(elite.skills().busy(), "skill " + skill + " runs its course");
        return ticks;
    }
    private static void dress(LivingEntity wearer, @Nullable Tier grade) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET})
            wearer.setItemSlot(slot, ItemStack.EMPTY);
        if (grade == null) return;
        for (DeferredItem<CorruptedRobeArmorItem> robe : ModItems.CORRUPTED_ROBES)
            if (robe.get().robeTier() == grade) wearer.setItemSlot(robe.get().getEquipmentSlot(), new ItemStack(robe.get()));
    }
    /** A pool of water, {@code half} blocks out each way from the middle and three deep, its top at the ground's. */
    private static void pool(ServerLevel level, int x, int z, int half) {
        for (BlockPos at : BlockPos.betweenClosed(x - half, -63, z - half, x + half, -61, z + half)) level.setBlockAndUpdate(at, Blocks.WATER.defaultBlockState());
    }
    private static void unpool(ServerLevel level, int x, int z, int half) {
        for (BlockPos at : BlockPos.betweenClosed(x - half, -63, z - half, x + half, -61, z + half))
            level.setBlockAndUpdate(at, at.getY() == -61 ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.DIRT.defaultBlockState());
    }
    private static List<CorruptionElite<?>> eachOf(ServerLevel level, double x) {
        return List.of(place(level, ModEntities.CORRUPTED_ABERRATION.get(), x, -60, Z + .5), place(level, ModEntities.CORRUPTED_SCREECHER.get(), x + 6, -56, Z + .5),
                place(level, ModEntities.CORRUPTED_LURKER.get(), x + 12, -60, Z + .5));
    }

    @Test void eachIsOfTheCorruptionButNoLordAndAnyBlowHurtsIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            List<CorruptionElite<?>> elites = eachOf(level, 200_000.5);
            CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            Villager villager = still(level, EntityType.VILLAGER, 200_020.5, Z + .5);
            try {
                double[] health = {AberrationRules.MAX_HEALTH, ScreecherRules.MAX_HEALTH, LurkerRules.MAX_HEALTH};
                for (int index = 0; index < 3; index++) {
                    CorruptionElite<?> elite = elites.get(index);
                    assertTrue(CorruptionFactionApi.isRemnant(elite) && CorruptionFactionApi.isElite(elite), elite + " is one of the corruption's elites");
                    assertFalse(CorruptionFactionApi.isBoss(elite), "but no lord");
                    assertEquals(health[index], elite.getMaxHealth());
                    assertTrue(elite.isAlliedTo(remnant) && !elite.canAttack(remnant), "the corruption's own are its allies");
                    assertTrue(elite.canAttack(villager), "everything else its prey");
                    assertTrue(elite.hurt(level.damageSources().generic(), 5), "any blow hurts it: it needs no arcane");
                    assertEquals(health[index] - 5, elite.getHealth(), 1e-3);
                    elite.invulnerableTime = 0;
                    assertFalse(elite.hurt(ModDamageTypes.corruption(level), 5), "the corruption's own hurt does not");
                    assertFalse(elite.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, 200)), "its curse takes no hold on it");
                    assertFalse(elite.hasEffect(ModEffects.CORRUPTION_CURSE));
                    assertTrue(elite.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20)), "though other effects do");
                }
                assertTrue(elites.get(1).isNoGravity(), "the screecher flies");
            } finally {
                elites.forEach(Entity::discard);
                villager.discard();
            }
        });
    }

    @Test void aNovicesRobesDoNotHideTheirWearerFromAnEliteThoughBetterOnesDo(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedAberration elite = place(level, ModEntities.CORRUPTED_ABERRATION.get(), 200_100.5, -60, Z + .5);
            CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            Villager wearer = still(level, EntityType.VILLAGER, 200_106.5, Z + .5);
            try {
                dress(wearer, Tier.NOVICE);
                assertTrue(CorruptionVeilApi.hiddenFrom(wearer, remnant), "a novice's robes hide it from the corruption's creatures");
                assertFalse(CorruptionVeilApi.hiddenFrom(wearer, elite), "but not from its elites");
                assertTrue(elite.canAttack(wearer), "which still take it for prey");
                for (Tier grade : List.of(Tier.INTERMEDIATE, Tier.ADVANCED)) {
                    dress(wearer, grade);
                    assertTrue(CorruptionVeilApi.hiddenFrom(wearer, elite), grade + " robes hide it from them");
                    assertFalse(elite.canAttack(wearer));
                }
            } finally {
                elite.discard();
                wearer.discard();
            }
        });
    }

    @Test void eachLeavesWhatARemnantLeaves(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            List<CorruptionElite<?>> elites = eachOf(level, 200_200.5);
            try {
                Item remain = ModItems.CORRUPTION_REMAIN.get();
                for (CorruptionElite<?> elite : elites)
                    for (int roll = 0; roll < 20; roll++) {
                        LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, elite)
                                .withParameter(LootContextParams.ORIGIN, elite.position()).withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                                .create(LootContextParamSets.ENTITY);
                        List<ItemStack> drops = server.reloadableRegistries().getLootTable(elite.getLootTable()).getRandomItems(params);
                        int count = drops.stream().mapToInt(ItemStack::getCount).sum();
                        assertTrue(drops.stream().allMatch(stack -> stack.is(remain)), elite + " leaves the corruption's remains, as a remnant does");
                        assertTrue(count >= 1 && count <= 4, "one to four of them: " + count);
                    }
            } finally {
                elites.forEach(Entity::discard);
            }
        });
    }

    @Test void theAberrationPouncesOnWhatStandsBeforeItAndSpitsAcidThatLeavesACursedPool(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedAberration elite = place(level, ModEntities.CORRUPTED_ABERRATION.get(), 200_300.5, -60, Z + .5);
            IronGolem golem = still(level, EntityType.IRON_GOLEM, 200_300.5, Z + 8.5);
            Villager villager = still(level, EntityType.VILLAGER, 200_300.5, Z + 14.5);
            try {
                elite.setYRot(0);
                run(elite, AberrationRules.Skill.POUNCE.ordinal(), golem);
                assertSame(golem, elite.pounce().struck(), "it leapt on the golem");
                assertTrue(golem.getHealth() < golem.getMaxHealth(), "and struck it");
                elite.moveTo(200_300.5, -60, Z + .5, 0, 0);
                List<Entity> loosed = catching(() -> run(elite, AberrationRules.Skill.ACID.ordinal(), villager), true);
                List<DemonBolt> globs = loosed.stream().filter(DemonBolt.class::isInstance).map(DemonBolt.class::cast).toList();
                assertEquals(AberrationRules.ACID.shots(), globs.size(), "three globs of acid");
                for (DemonBolt glob : globs) {
                    assertEquals(SpitRules.Look.GLOB, glob.look());
                    assertTrue(glob.pool().any(), "each leaves a pool where it bursts");
                }
                List<Entity> left = catching(() -> {
                    for (DemonBolt glob : globs) for (int tick = 0; tick < 80 && !glob.isRemoved(); tick++) glob.tick();
                }, false);
                List<AreaEffectCloud> pools = left.stream().filter(AreaEffectCloud.class::isInstance).map(AreaEffectCloud.class::cast).toList();
                assertFalse(pools.isEmpty(), "a pool where they burst");
                assertTrue(pools.stream().allMatch(pool -> pool.getOwner() == elite), "the aberration's");
            } finally {
                elite.discard();
                golem.discard();
                villager.discard();
            }
        });
    }

    @Test void broughtLowTheAberrationRoarsAndGoesBerserk(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedAberration elite = place(level, ModEntities.CORRUPTED_ABERRATION.get(), 200_400.5, -60, Z + .5);
            Villager villager = still(level, EntityType.VILLAGER, 200_402.5, Z + .5);
            try {
                double speed = elite.getAttributeValue(Attributes.MOVEMENT_SPEED), damage = elite.getAttributeValue(Attributes.ATTACK_DAMAGE);
                elite.customServerAiStep();
                assertFalse(elite.berserk() || elite.skill() == AberrationRules.Skill.ROAR.ordinal(), "whole, it is calm");
                elite.setHealth((float) (elite.getMaxHealth() * .3));
                elite.customServerAiStep();
                assertEquals(AberrationRules.Skill.ROAR.ordinal(), elite.skill(), "brought low, it roars");
                for (int tick = 0; tick < 60 && elite.skills().busy(); tick++) elite.skills().tick(elite, villager, cooldown -> cooldown);
                assertTrue(elite.berserk(), "and goes berserk");
                assertEquals(speed * (1 + AberrationRules.BERSERK_SPEED), elite.getAttributeValue(Attributes.MOVEMENT_SPEED), 1e-6, "faster");
                assertEquals(damage * (1 + AberrationRules.BERSERK_DAMAGE), elite.getAttributeValue(Attributes.ATTACK_DAMAGE), 1e-6, "harder-hitting");
                assertEquals(50, elite.cooldown(100), "quicker to its skills");
                assertTrue(villager.getDeltaMovement().horizontalDistance() > .3, "its roar threw back what was near");
            } finally {
                elite.discard();
                villager.discard();
            }
        });
    }

    @Test void theScreecherLoosesFeathersDivesAndScreechesToStunAndSlow(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedScreecher bird = place(level, ModEntities.CORRUPTED_SCREECHER.get(), 200_500.5, -54, Z + .5);
            IronGolem golem = still(level, EntityType.IRON_GOLEM, 200_500.5, Z + 10.5);
            Villager near = still(level, EntityType.VILLAGER, 200_503.5, Z + .5);
            CorruptedRemnant ally = still(level, ModEntities.CORRUPTED_REMNANT.get(), 200_497.5, Z + .5);
            try {
                List<Entity> loosed = catching(() -> run(bird, ScreecherRules.Skill.FEATHERS.ordinal(), golem), false);
                List<DemonBolt> feathers = loosed.stream().filter(DemonBolt.class::isInstance).map(DemonBolt.class::cast).toList();
                assertEquals(ScreecherRules.FEATHERS.shots(), feathers.size(), "a fan of feathers");
                assertTrue(feathers.stream().allMatch(feather -> feather.look() == SpitRules.Look.FEATHER));
                run(bird, ScreecherRules.Skill.SCREECH.ordinal(), near);
                assertTrue(near.hasEffect(ModEffects.STUNNED), "its screech stuns what is near");
                assertTrue(near.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "and slows it");
                assertFalse(ally.hasEffect(ModEffects.STUNNED), "but not its own side");
                bird.moveTo(200_500.5, -54, Z + .5, 0, 0);
                BlockPos ground = BlockPos.containing(golem.getX(), -61, golem.getZ());
                run(bird, ScreecherRules.Skill.DIVE.ordinal(), golem);
                assertTrue(bird.dive().struck(), "it dives on the golem");
                assertTrue(golem.getHealth() < golem.getMaxHealth(), "and strikes it");
                assertEquals(0, bird.dive().torn(), "tearing nothing up");
                assertFalse(level.getBlockState(ground).isAir(), "the ground as it was");
            } finally {
                bird.discard();
                golem.discard();
                near.discard();
                ally.discard();
            }
        });
    }

    @Test void inTheWaterTheLurkerDartsFarFasterThanItCrawlsOnLand(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 200_600;
            TestWorld.load(level, x - 48, x + 48, Z - 48, Z + 48);
            pool(level, x, Z, 12);
            CorruptedLurker lurker = place(level, ModEntities.CORRUPTED_LURKER.get(), x + .5, -62.5, Z + .5);
            Villager swimmer = still(level, EntityType.VILLAGER, x + .5, Z + 10.5);
            swimmer.moveTo(x + .5, -62.5, Z + 10.5);
            try {
                lurker.setTarget(swimmer);
                double fastest = 0;
                for (int tick = 0; tick < 12; tick++) {
                    lurker.tick();
                    fastest = Math.max(fastest, lurker.getDeltaMovement().length());
                }
                assertTrue(lurker.isInWater(), "it is in the water");
                assertTrue(fastest > .45, "it darts at its quarry: " + fastest + " blocks a tick");
                unpool(level, x, Z, 12);
                lurker.moveTo(x + .5, -60, Z + .5, 0, 0);
                swimmer.moveTo(x + .5, -60, Z + 10.5);
                lurker.setDeltaMovement(Vec3.ZERO);
                double crawling = 0;
                for (int tick = 0; tick < 12; tick++) {
                    lurker.tick();
                    crawling = Math.max(crawling, lurker.getDeltaMovement().horizontalDistance());
                }
                assertFalse(lurker.isInWater());
                assertTrue(crawling < fastest / 3, "on land it only crawls: " + crawling);
            } finally {
                lurker.discard();
                swimmer.discard();
                unpool(level, x, Z, 12);
            }
        });
    }

    @Test void theLurkerDragsWhatItGrabsInAndShootsBoltsThatThrowBackWhatTheyStrike(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 200_700;
            TestWorld.load(level, x - 48, x + 48, Z - 48, Z + 48);
            pool(level, x, Z, 5);
            CorruptedLurker lurker = place(level, ModEntities.CORRUPTED_LURKER.get(), x + .5, -62.5, Z + .5);
            Villager ashore = still(level, EntityType.VILLAGER, x + .5, Z + 9.5);
            try {
                lurker.setYRot(0);
                lurker.skills().comeRound(LurkerRules.Skill.GRAB.ordinal() - 1);
                assertTrue(lurker.begin(LurkerRules.Skill.GRAB.ordinal(), ashore));
                for (int tick = 0; tick <= LurkerRules.GRAB.timing().windup() + 2; tick++) lurker.skills().tick(lurker, ashore, cooldown -> cooldown);
                assertSame(ashore, lurker.grab().held(), "its tentacle has hold of the villager on the bank");
                assertTrue(ashore.getHealth() < ashore.getMaxHealth(), "hurt as it is taken");
                Vec3 toward = lurker.position().subtract(ashore.position()).multiply(1, 0, 1).normalize();
                assertTrue(ashore.getDeltaMovement().multiply(1, 0, 1).dot(toward) > .2, "and dragged towards the water");
                lurker.skills().interrupt(lurker, cooldown -> cooldown);
                ashore.setDeltaMovement(Vec3.ZERO);
                // As it would be by now: at the edge, up under the surface, the better to shoot over the bank.
                lurker.moveTo(x + .5, -61.2, Z + 4.6, 0, 0);
                // The test server ticks nothing by itself: it is told it is in the water.
                lurker.baseTick();
                assertTrue(lurker.isInWater());
                for (int tick = 0; tick < 30; tick++) {
                    lurker.act(ashore, false);
                    lurker.travel(Vec3.ZERO);
                }
                assertTrue(Math.abs(lurker.getZ() - (Z + 4.6)) < .1, "it comes no further than the water's edge for what is ashore");
                assertTrue(lurker.getY() + CorruptedLurker.MAW[1] > -60, "but surfaces there, its maw over the water");
                lurker.baseTick();
                assertTrue(lurker.isInWater(), "still in it");
                List<Entity> loosed = catching(() -> run(lurker, LurkerRules.Skill.BOLTS.ordinal(), ashore), true);
                List<DemonBolt> bolts = loosed.stream().filter(DemonBolt.class::isInstance).map(DemonBolt.class::cast).toList();
                assertEquals(LurkerRules.BOLTS.shots(), bolts.size(), "three bolts of water");
                assertTrue(bolts.stream().allMatch(bolt -> bolt.look() == SpitRules.Look.WATER && bolt.push() > 0));
                ashore.invulnerableTime = 0;
                float before = ashore.getHealth();
                DemonBolt first = bolts.get(0);
                for (int tick = 0; tick < 40 && !first.isRemoved(); tick++) first.tick();
                assertTrue(ashore.getHealth() < before, "a bolt strikes it");
                Vec3 flight = ashore.position().subtract(lurker.position()).multiply(1, 0, 1).normalize();
                assertTrue(ashore.getDeltaMovement().multiply(1, 0, 1).dot(flight) > .3, "and throws it back");
                bolts.forEach(Entity::discard);
            } finally {
                lurker.discard();
                ashore.discard();
                unpool(level, x, Z, 5);
            }
        });
    }

    /** A malice vine on malice at this place, grown to this stage. */
    private static MaliceVine vine(ServerLevel level, int x, int stage) {
        TestWorld.load(level, x - 48, x + 48, Z - 48, Z + 48);
        BlockPos anchor = new BlockPos(x, -61, Z);
        level.setBlockAndUpdate(anchor, ModBlocks.MALICE.get().defaultBlockState());
        MaliceVine vine = MaliceVine.sprout(level, anchor, 1);
        assertNotNull(vine, "a vine sprouts on the malice");
        for (int grown = 0; grown < stage; grown++) assertTrue(vine.grow());
        return vine;
    }

    @Test void aMaliceVineBroodsAnEliteWhenAPlayerIsNearAndThereIsRoom(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            assertEquals(.05, InfectionConfig.VINE_CHANCE_DEFAULT, 1e-9, "a vine sprouts on one malice in twenty");
            MaliceVine vine = vine(level, 200_800, 2);
            List<Entity> extra = new ArrayList<>();
            try {
                vine.waitedFor(InfectionConfig.vineEliteInterval());
                assertTrue(catching(() -> vine.brood(level), false).stream().noneMatch(CorruptionFactionApi::isElite), "with no player near, it waits");
                FakePlayer near = player(level, "vine_watcher");
                near.moveTo(200_810.5, -60, Z + .5);
                level.addFreshEntity(near);
                extra.add(near);
                List<Entity> brought = catching(() -> vine.brood(level), false);
                List<Entity> elites = brought.stream().filter(CorruptionFactionApi::isElite).toList();
                assertEquals(1, elites.size(), "with a player near, it brings forth one elite");
                Entity elite = elites.get(0);
                assertTrue(elite.getType().is(MaliceVineApi.BROOD), "one of the land's or the air's, with no water near");
                double out = Math.hypot(elite.getX() - (200_800 + .5), elite.getZ() - (Z + .5));
                assertTrue(out >= MaliceVineRules.BROOD_NEAR - .01 && out <= MaliceVineRules.BROOD_FAR + .01, "a few blocks from it: " + out);
                assertEquals(0, vine.broodWaited(), "and waits again");
                for (int index = 0; index < InfectionConfig.vineEliteCap(); index++)
                    extra.add(place(level, ModEntities.CORRUPTED_SCREECHER.get(), 200_790.5 + index, -55, Z + 8.5));
                vine.waitedFor(InfectionConfig.vineEliteInterval());
                assertTrue(catching(() -> vine.brood(level), false).stream().noneMatch(CorruptionFactionApi::isElite), "nor while there are as many as it may have about it");
            } finally {
                vine.discard();
                extra.forEach(Entity::discard);
            }
        });
    }

    @Test void tickByTickAMaliceVineBringsForthAnEliteWithinHalfAMinute(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            assertEquals(MaliceVineRules.BROOD_TICKS, InfectionConfig.vineEliteInterval(), "every half a minute, unless the server says otherwise");
            MaliceVine vine = vine(level, 201_200, 0);
            FakePlayer near = player(level, "vine_waiter");
            near.moveTo(201_210.5, -60, Z + .5);
            level.addFreshEntity(near);
            List<Entity> brought = new ArrayList<>();
            try {
                int ticks = 0;
                // The test server ticks nothing by itself: its clock is wound on by hand, as a real server's is.
                for (; ticks < InfectionConfig.vineEliteInterval() + 40 && brought.stream().noneMatch(CorruptionFactionApi::isElite); ticks++) {
                    vine.tickCount++;
                    brought.addAll(catching(vine::tick, true));
                }
                assertTrue(brought.stream().anyMatch(CorruptionFactionApi::isElite), "an elite within " + ticks + " ticks");
                assertTrue(ticks <= InfectionConfig.vineEliteInterval() + 20, "as soon as its time came: " + ticks);
            } finally {
                vine.discard();
                near.discard();
                brought.forEach(Entity::discard);
            }
        });
    }

    @Test void eachEliteHasASpawnEggOfItsOwn(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            List<EntityType<? extends Mob>> elites = List.of(ModEntities.CORRUPTED_ABERRATION.get(), ModEntities.CORRUPTED_SCREECHER.get(),
                    ModEntities.CORRUPTED_LURKER.get());
            TestWorld.load(level, 201_300 - 16, 201_300 + 48, Z - 16, Z + 16);
            for (int index = 0; index < elites.size(); index++) {
                EntityType<? extends Mob> type = elites.get(index);
                net.minecraft.world.item.SpawnEggItem egg = ModItems.ELITE_EGGS.get(index).get();
                ItemStack stack = new ItemStack(egg);
                assertSame(type, egg.getType(stack), type + "'s own egg");
                assertSame(egg, net.minecraft.world.item.SpawnEggItem.byId(type), "the game knows it as " + type + "'s");
                FakePlayer user = player(level, "egg_user_" + index);
                user.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stack);
                BlockPos ground = new BlockPos(201_300 + index * 8, -61, Z);
                List<Entity> hatched = catching(() -> egg.useOn(new net.minecraft.world.item.context.UseOnContext(user, net.minecraft.world.InteractionHand.MAIN_HAND,
                        new net.minecraft.world.phys.BlockHitResult(Vec3.atCenterOf(ground).add(0, .5, 0), net.minecraft.core.Direction.UP, ground, false))), false);
                assertTrue(hatched.stream().anyMatch(entity -> entity.getType() == type), "used on the ground, it brings forth " + type);
            }
        });
    }

    @Test void aMaliceVinePutsOneOfTheSeasInTheWaterAndOnlyWhereThereIsAny(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            MaliceVine vine = vine(level, 200_900, 1);
            try {
                assertNull(vine.inWater(level, ModEntities.CORRUPTED_LURKER.get()), "no water, no place for the lurker");
                Vec3 ground = vine.onGround(level, ModEntities.CORRUPTED_ABERRATION.get());
                assertNotNull(ground, "the aberration has a place on the ground");
                assertTrue(level.getBlockState(BlockPos.containing(ground).below()).isSolid(), "on something solid");
                pool(level, 200_906, Z, 2);
                Vec3 wet = vine.inWater(level, ModEntities.CORRUPTED_LURKER.get());
                assertNotNull(wet, "a place in the pool");
                assertTrue(level.getFluidState(BlockPos.containing(wet)).is(net.minecraft.tags.FluidTags.WATER));
                Mob lurker = catching(() -> vine.bringForth(level, ModEntities.CORRUPTED_LURKER.get(), wet), false).stream()
                        .filter(Mob.class::isInstance).map(Mob.class::cast).findFirst().orElseThrow();
                assertInstanceOf(CorruptedLurker.class, lurker, "the lurker, brought forth in the water");
            } finally {
                vine.discard();
                unpool(level, 200_906, Z, 2);
            }
        });
    }

    @Test void aMaliceVineThrowsBackWhatComesCloseWithAShockwave(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            MaliceVine vine = vine(level, 201_000, 2);
            Villager close = still(level, EntityType.VILLAGER, 201_002.5, Z + .5);
            CorruptedAberration ally = still(level, ModEntities.CORRUPTED_ABERRATION.get(), 200_998.5, Z + .5);
            try {
                assertTrue(vine.threatened(level), "something not of its side is close");
                dress(close, Tier.NOVICE);
                assertFalse(vine.threatened(level), "but not what a robe hides from it");
                dress(close, null);
                vine.gather();
                assertEquals(MaliceVineRules.SHOCK_WINDUP, vine.shocking(), "it gathers itself");
                for (int tick = 0; tick < MaliceVineRules.SHOCK_WINDUP; tick++) vine.tick();
                assertEquals(0, vine.shocking(), "and lets loose");
                assertTrue(close.getHealth() < close.getMaxHealth(), "what was close is hurt");
                assertTrue(close.getDeltaMovement().x > .5, "and thrown away from it");
                assertEquals(ally.getMaxHealth(), ally.getHealth(), "its own side is not");
                vine.gather();
                assertEquals(0, vine.shocking(), "it cannot again until its cooldown is over");
            } finally {
                vine.discard();
                close.discard();
                ally.discard();
            }
        });
    }

    @Test void struckAMaliceVineGathersItselfToStrikeBack(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            MaliceVine vine = vine(level, 201_100, 0);
            try {
                FakePlayer striker = player(level, "vine_striker");
                assertTrue(vine.hurt(level.damageSources().playerAttack(striker), 1));
                assertEquals(MaliceVineRules.SHOCK_WINDUP, vine.shocking(), "struck, it gathers itself");
            } finally {
                vine.discard();
            }
        });
    }
}
