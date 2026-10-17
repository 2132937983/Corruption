package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.api.plant.MaliceVineApi;
import com.miophas.corruption.common.item.CorruptedRobeArmorItem;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.corruption.CorruptedRobeRules.Tier;
import com.miophas.corruption.core.demon.skill.SpitRules;
import com.miophas.corruption.core.elite.BloaterRules;
import com.miophas.corruption.core.elite.BruteRules;
import com.miophas.corruption.core.elite.CrawlerRules;
import com.miophas.corruption.core.elite.DrifterRules;
import com.miophas.corruption.core.elite.GazerRules;
import com.miophas.corruption.core.elite.HivemotherRules;
import com.miophas.corruption.core.elite.HowlerRules;
import com.miophas.corruption.core.elite.MawRules;
import com.miophas.corruption.core.elite.MortarRules;
import com.miophas.corruption.core.elite.NaiadRules;
import com.miophas.corruption.core.elite.StalkerRules;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
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
 * The eleven elites that joined the first three, on a real server: each is of the corruption as the first three are (no
 * lord; any blow hurts it, the corruption's curse and hurt do not; a novice's robes do not hide their wearer from it; it
 * leaves what a remnant leaves; it hatches from an egg of its own; the vine broods it, the land's and the air's on land and
 * the seas' in the water); and each one's skills, begun by hand and run through its own set of them. The test world never
 * brings new entities into sight, so what is loosed or brought forth is caught as it joins.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class EliteRosterServerTest {
    private static final int Z = 7000;

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
    private static <T extends Entity> List<T> of(List<Entity> entities, Class<T> kind) { return entities.stream().filter(kind::isInstance).map(kind::cast).toList(); }
    /** Runs one of an elite's skills (by its place in its rules' Skill) on this quarry from beginning to end, by hand; how many ticks it took. */
    private static <T extends CorruptionElite<T>> int run(T elite, int skill, @Nullable LivingEntity target) {
        elite.skills().comeRound(skill - 1);
        assertTrue(elite.begin(skill, target), "skill " + skill + " begins");
        int ticks = 0;
        for (; ticks < 400 && elite.skills().busy(); ticks++) {
            elite.skills().tick(elite.elite(), target, cooldown -> cooldown);
            elite.travel(Vec3.ZERO);
        }
        assertFalse(elite.skills().busy(), "skill " + skill + " runs its course");
        return ticks;
    }
    /** Begins one of an elite's skills and runs it this many ticks, by hand. */
    private static <T extends CorruptionElite<T>> void runFor(T elite, int skill, LivingEntity target, int ticks) {
        elite.skills().comeRound(skill - 1);
        assertTrue(elite.begin(skill, target), "skill " + skill + " begins");
        for (int tick = 0; tick < ticks; tick++) elite.skills().tick(elite.elite(), target, cooldown -> cooldown);
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

    /** The eleven, by type: the land's, the air's, the seas'. */
    private static List<EntityType<? extends CorruptionElite<?>>> types() {
        return List.of(ModEntities.CORRUPTED_BRUTE.get(), ModEntities.CORRUPTED_HOWLER.get(), ModEntities.CORRUPTED_STALKER.get(),
                ModEntities.CORRUPTED_BLOATER.get(), ModEntities.CORRUPTED_MORTAR.get(), ModEntities.CORRUPTED_CRAWLER.get(), ModEntities.CORRUPTED_GAZER.get(),
                ModEntities.CORRUPTED_HIVEMOTHER.get(), ModEntities.CORRUPTED_DRIFTER.get(), ModEntities.CORRUPTED_MAW.get(), ModEntities.CORRUPTED_NAIAD.get());
    }
    private static final double[] HEALTH = {BruteRules.MAX_HEALTH, HowlerRules.MAX_HEALTH, StalkerRules.MAX_HEALTH, BloaterRules.MAX_HEALTH,
            MortarRules.MAX_HEALTH, CrawlerRules.MAX_HEALTH, GazerRules.MAX_HEALTH, HivemotherRules.MAX_HEALTH, DrifterRules.MAX_HEALTH, MawRules.MAX_HEALTH,
            NaiadRules.MAX_HEALTH};
    private static final int FLIERS = 6, SWIMMERS = 9;

    private static List<CorruptionElite<?>> eachOf(ServerLevel level, double x) {
        List<CorruptionElite<?>> all = new ArrayList<>();
        List<EntityType<? extends CorruptionElite<?>>> types = types();
        for (int index = 0; index < types.size(); index++)
            all.add(place(level, types.get(index), x + index * 6, index >= FLIERS && index < SWIMMERS ? -56 : -60, Z + .5));
        return all;
    }

    @Test void eachIsOfTheCorruptionButNoLordAndAnyBlowHurtsIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            List<CorruptionElite<?>> elites = eachOf(level, 300_000.5);
            CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            Villager villager = still(level, EntityType.VILLAGER, 300_000.5, Z + 10.5);
            try {
                for (int index = 0; index < elites.size(); index++) {
                    CorruptionElite<?> elite = elites.get(index);
                    assertTrue(CorruptionFactionApi.isRemnant(elite) && CorruptionFactionApi.isElite(elite), elite + " is one of the corruption's elites");
                    assertFalse(CorruptionFactionApi.isBoss(elite), "but no lord");
                    assertEquals(HEALTH[index], elite.getMaxHealth(), elite.toString());
                    assertTrue(elite.isAlliedTo(remnant) && !elite.canAttack(remnant), "the corruption's own are its allies");
                    assertTrue(elite.canAttack(villager), "everything else its prey");
                    assertTrue(elite.hurt(level.damageSources().generic(), 5), elite + ": any blow hurts it");
                    assertEquals(HEALTH[index] - 5, elite.getHealth(), 1e-3);
                    elite.invulnerableTime = 0;
                    assertFalse(elite.hurt(ModDamageTypes.corruption(level), 5), elite + ": the corruption's own hurt does not");
                    assertFalse(elite.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, 200)), elite + ": its curse takes no hold on it");
                    assertTrue(elite.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20)), "though other effects do");
                    assertEquals(index >= FLIERS && index < SWIMMERS, elite.isNoGravity(), elite + ": only the air's fly");
                }
            } finally {
                elites.forEach(Entity::discard);
                villager.discard();
            }
        });
    }

    @Test void aNovicesRobesDoNotHideTheirWearerFromAnyOfThemAndEachLeavesWhatARemnantLeaves(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            List<CorruptionElite<?>> elites = eachOf(level, 300_200.5);
            CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            Villager wearer = still(level, EntityType.VILLAGER, 300_200.5, Z + 10.5);
            try {
                Item remain = ModItems.CORRUPTION_REMAIN.get();
                for (CorruptionElite<?> elite : elites) {
                    dress(wearer, Tier.NOVICE);
                    assertTrue(CorruptionVeilApi.hiddenFrom(wearer, remnant));
                    assertFalse(CorruptionVeilApi.hiddenFrom(wearer, elite), "a novice's robes do not hide it from " + elite);
                    dress(wearer, Tier.INTERMEDIATE);
                    assertTrue(CorruptionVeilApi.hiddenFrom(wearer, elite), "better ones do");
                    for (int roll = 0; roll < 10; roll++) {
                        LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, elite)
                                .withParameter(LootContextParams.ORIGIN, elite.position()).withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                                .create(LootContextParamSets.ENTITY);
                        List<ItemStack> drops = server.reloadableRegistries().getLootTable(elite.getLootTable()).getRandomItems(params);
                        int count = drops.stream().mapToInt(ItemStack::getCount).sum();
                        assertTrue(drops.stream().allMatch(stack -> stack.is(remain)), elite + " leaves the corruption's remains");
                        assertTrue(count >= 1 && count <= 4, elite + ": one to four of them: " + count);
                    }
                }
            } finally {
                elites.forEach(Entity::discard);
                wearer.discard();
            }
        });
    }

    @Test void eachHatchesFromAnEggOfItsOwnAndTheVineBroodsItWhereItLives(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            List<EntityType<? extends CorruptionElite<?>>> types = types();
            assertTrue(ModItems.ELITE_EGGS.size() >= 3 + types.size(), "an egg for every elite");
            TestWorld.load(level, 300_400 - 16, 300_400 + types.size() * 8 + 16, Z - 16, Z + 16);
            for (int index = 0; index < types.size(); index++) {
                EntityType<? extends CorruptionElite<?>> type = types.get(index);
                SpawnEggItem egg = ModItems.ELITE_EGGS.get(3 + index).get();
                ItemStack stack = new ItemStack(egg);
                assertSame(type, egg.getType(stack), type + "'s own egg");
                assertSame(egg, SpawnEggItem.byId(type), "the game knows it as " + type + "'s");
                FakePlayer user = player(level, "roster_egg_" + index);
                user.setItemInHand(InteractionHand.MAIN_HAND, stack);
                BlockPos ground = new BlockPos(300_400 + index * 8, -61, Z);
                List<Entity> hatched = catching(() -> egg.useOn(new UseOnContext(user, InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3.atCenterOf(ground).add(0, .5, 0), Direction.UP, ground, false))), false);
                assertTrue(hatched.stream().anyMatch(entity -> entity.getType() == type), "used on the ground, it brings forth " + type);
                boolean sea = index >= SWIMMERS;
                assertEquals(!sea, type.is(MaliceVineApi.BROOD), type + (sea ? " is no brood of the land's" : " is brooded on land or in the air"));
                assertEquals(sea, type.is(MaliceVineApi.AQUATIC_BROOD), type + (sea ? " is brooded in the water" : " is not brooded in the water"));
            }
        });
    }

    @Test void theBruteSlamsWhatStandsBeforeItAndHurlsALobbedGlob(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedBrute brute = place(level, ModEntities.CORRUPTED_BRUTE.get(), 300_600.5, -60, Z + .5);
            IronGolem golem = still(level, EntityType.IRON_GOLEM, 300_600.5, Z + 3.5);
            Villager far = still(level, EntityType.VILLAGER, 300_600.5, Z + 16.5);
            try {
                brute.setYRot(0);
                brute.yBodyRot = 0;
                run(brute, BruteRules.Skill.SLAM.ordinal(), golem);
                assertTrue(golem.getHealth() < golem.getMaxHealth(), "its slam strikes the golem before it");
                List<DemonBolt> hurled = of(catching(() -> run(brute, BruteRules.Skill.HURL.ordinal(), far), false), DemonBolt.class);
                assertEquals(BruteRules.HURL.shots(), hurled.size(), "one glob, hurled");
                assertTrue(hurled.stream().allMatch(glob -> glob.look() == SpitRules.Look.GLOB));
            } finally {
                brute.discard();
                golem.discard();
                far.discard();
            }
        });
    }

    @Test void theHowlersHowlCallsUpRemnantsThatCrumbleAwayAndItSpitsBile(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedHowler howler = place(level, ModEntities.CORRUPTED_HOWLER.get(), 300_700.5, -60, Z + .5);
            Villager villager = still(level, EntityType.VILLAGER, 300_700.5, Z + 10.5);
            try {
                List<CorruptedRemnant> called = of(catching(() -> run(howler, HowlerRules.Skill.HOWL.ordinal(), villager), false), CorruptedRemnant.class);
                assertEquals(HowlerRules.HOWL.count(), called.size(), "it calls up remnants");
                long now = level.getGameTime();
                assertTrue(called.stream().allMatch(remnant -> remnant.crumblesAt() == now + HowlerRules.HOWL.lasts()), "each crumbles away in half a minute");
                List<DemonBolt> bile = of(catching(() -> run(howler, HowlerRules.Skill.BILE.ordinal(), villager), false), DemonBolt.class);
                assertEquals(HowlerRules.BILE.shots(), bile.size());
                assertTrue(bile.stream().allMatch(glob -> glob.look() == SpitRules.Look.GLOB && glob.pool().any()), "globs that leave a pool");
            } finally {
                howler.discard();
                villager.discard();
            }
        });
    }

    @Test void theStalkerFadesFromSightUntilItStrikesAndFlingsSpines(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedStalker stalker = place(level, ModEntities.CORRUPTED_STALKER.get(), 300_800.5, -60, Z + .5);
            Villager villager = still(level, EntityType.VILLAGER, 300_800.5, Z + 10.5);
            try {
                assertFalse(stalker.faded());
                run(stalker, StalkerRules.Skill.FADE.ordinal(), villager);
                assertTrue(stalker.faded() && stalker.hasEffect(MobEffects.INVISIBILITY), "it fades from sight");
                assertTrue(stalker.hasEffect(MobEffects.MOVEMENT_SPEED), "and quickens");
                assertFalse(stalker.skills().usable(StalkerRules.Skill.FADE.ordinal() - 1, stalker, villager), "it cannot fade while faded");
                stalker.doHurtTarget(villager);
                assertFalse(stalker.faded(), "striking, it comes out of its fade");
                List<DemonBolt> spines = of(catching(() -> run(stalker, StalkerRules.Skill.SPINES.ordinal(), villager), false), DemonBolt.class);
                assertEquals(StalkerRules.SPINES.shots(), spines.size(), "a fan of spines");
            } finally {
                stalker.discard();
                villager.discard();
            }
        });
    }

    @Test void theBloaterVentsACursedCloudAndBurstsWhenItDies(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedBloater bloater = place(level, ModEntities.CORRUPTED_BLOATER.get(), 300_900.5, -60, Z + .5);
            Villager villager = still(level, EntityType.VILLAGER, 300_900.5, Z + 4.5);
            try {
                List<AreaEffectCloud> vented = of(catching(() -> run(bloater, BloaterRules.Skill.VENT.ordinal(), villager), false), AreaEffectCloud.class);
                assertEquals(1, vented.size(), "a cloud of its gas");
                assertEquals(bloater, vented.get(0).getOwner());
                assertEquals(BloaterRules.VENT_CLOUD.radius(), vented.get(0).getRadius(), 1e-3);
                assertTrue(villager.getDeltaMovement().horizontalDistance() > .3, "what was near is pushed off");
                CorruptedRemnant ally = still(level, ModEntities.CORRUPTED_REMNANT.get(), 300_902.5, Z + .5);
                AreaEffectCloud gas = EliteClouds.release(level, bloater, bloater.position(), BloaterRules.VENT_CLOUD);
                assertTrue(villager.addEffect(new MobEffectInstance(MobEffects.POISON, 60), gas), "its gas poisons what is not of its side");
                assertFalse(bloater.addEffect(new MobEffectInstance(MobEffects.POISON, 60), gas), "but not the bloater itself");
                assertFalse(ally.addEffect(new MobEffectInstance(MobEffects.POISON, 60), gas), "nor its own side");
                assertTrue(ally.addEffect(new MobEffectInstance(MobEffects.POISON, 60)), "which is poisoned by any other");
                gas.discard();
                ally.discard();
                List<DemonBolt> lobbed = of(catching(() -> run(bloater, BloaterRules.Skill.LOB.ordinal(), villager), false), DemonBolt.class);
                assertEquals(BloaterRules.LOB.shots(), lobbed.size());
                List<AreaEffectCloud> burst = of(catching(() -> bloater.die(level.damageSources().generic()), false), AreaEffectCloud.class);
                assertEquals(1, burst.size(), "dying, it bursts");
                assertEquals(BloaterRules.BURST.radius(), burst.get(0).getRadius(), 1e-3, "wider than it vents");
            } finally {
                bloater.discard();
                villager.discard();
            }
        });
    }

    @Test void theMortarRainsABarrageFromAfarAndDrivesSpikesUpUnderWhatIsClose(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedMortar mortar = place(level, ModEntities.CORRUPTED_MORTAR.get(), 301_000.5, -60, Z + .5);
            Villager far = still(level, EntityType.VILLAGER, 301_000.5, Z + 14.5);
            Villager close = still(level, EntityType.VILLAGER, 301_000.5, Z + 4.5);
            try {
                List<DemonBolt> barrage = of(catching(() -> run(mortar, MortarRules.Skill.BARRAGE.ordinal(), far), false), DemonBolt.class);
                assertEquals(MortarRules.BARRAGE.count(), barrage.size(), "its barrage");
                assertEquals(MortarRules.BARRAGE.count(), mortar.barrage().loosed());
                catching(() -> run(mortar, MortarRules.Skill.SPIKES.ordinal(), close), false);
                assertTrue(close.getHealth() < close.getMaxHealth(), "the spikes strike what is close");
                assertTrue(close.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "and slow it");
            } finally {
                mortar.discard();
                far.discard();
                close.discard();
            }
        });
    }

    @Test void theCrawlerLeapsOnWhatStandsBeforeItAndSpitsVenom(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedCrawler crawler = place(level, ModEntities.CORRUPTED_CRAWLER.get(), 301_100.5, -60, Z + .5);
            IronGolem golem = still(level, EntityType.IRON_GOLEM, 301_100.5, Z + 6.5);
            Villager villager = still(level, EntityType.VILLAGER, 301_100.5, Z + 14.5);
            try {
                crawler.setYRot(0);
                run(crawler, CrawlerRules.Skill.POUNCE.ordinal(), golem);
                assertSame(golem, crawler.pounce().struck(), "it leapt on the golem");
                assertTrue(golem.getHealth() < golem.getMaxHealth());
                crawler.moveTo(301_100.5, -60, Z + .5, 0, 0);
                List<DemonBolt> venom = of(catching(() -> run(crawler, CrawlerRules.Skill.VENOM.ordinal(), villager), false), DemonBolt.class);
                assertEquals(CrawlerRules.VENOM.shots(), venom.size());
                assertTrue(venom.get(0).pool().any(), "its venom leaves a pool");
            } finally {
                crawler.discard();
                golem.discard();
                villager.discard();
            }
        });
    }

    @Test void theGazerBurnsWithItsBeamSlowsAndBlindsWithItsGazeAndLoosesBolts(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedGazer gazer = place(level, ModEntities.CORRUPTED_GAZER.get(), 301_200.5, -58, Z + .5);
            Villager villager = still(level, EntityType.VILLAGER, 301_200.5, Z + 8.5);
            try {
                catching(() -> run(gazer, GazerRules.Skill.BEAM.ordinal(), villager), false);
                assertTrue(villager.getHealth() < villager.getMaxHealth(), "its beam burns the villager");
                run(gazer, GazerRules.Skill.GAZE.ordinal(), villager);
                assertTrue(villager.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && villager.hasEffect(MobEffects.BLINDNESS), "its gaze slows and blinds");
                List<DemonBolt> bolts = of(catching(() -> run(gazer, GazerRules.Skill.BOLTS.ordinal(), villager), false), DemonBolt.class);
                assertEquals(GazerRules.BOLTS.shots(), bolts.size());
            } finally {
                gazer.discard();
                villager.discard();
            }
        });
    }

    @Test void theHivemotherCallsUpASwarmThatCrumblesAwayAndLoosesStingers(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedHivemother mother = place(level, ModEntities.CORRUPTED_HIVEMOTHER.get(), 301_300.5, -56, Z + .5);
            Villager villager = still(level, EntityType.VILLAGER, 301_300.5, Z + 8.5);
            try {
                List<CorruptedRemnant> swarm = of(catching(() -> run(mother, HivemotherRules.Skill.SWARM.ordinal(), villager), false), CorruptedRemnant.class);
                assertEquals(HivemotherRules.SWARM.count(), swarm.size(), "her swarm");
                assertTrue(swarm.stream().allMatch(remnant -> remnant.crumblesAt() > 0), "which crumbles away in time");
                List<DemonBolt> stingers = of(catching(() -> run(mother, HivemotherRules.Skill.STINGERS.ordinal(), villager), false), DemonBolt.class);
                assertEquals(HivemotherRules.STINGERS.shots(), stingers.size(), "a fan of stingers");
            } finally {
                mother.discard();
                villager.discard();
            }
        });
    }

    @Test void theDrifterLiftsWhatItCatchesAndRainsAcidOnWhatIsBelow(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedDrifter drifter = place(level, ModEntities.CORRUPTED_DRIFTER.get(), 301_400.5, -54, Z + .5);
            Villager villager = still(level, EntityType.VILLAGER, 301_400.5, Z + 2.5);
            try {
                runFor(drifter, DrifterRules.Skill.LIFT.ordinal(), villager, DrifterRules.LIFT.timing().windup() + 3);
                assertSame(villager, drifter.lift().held(), "its tendril has hold of the villager");
                assertTrue(villager.getHealth() < villager.getMaxHealth(), "stung as it is taken");
                assertTrue(villager.getDeltaMovement().y > .1, "and lifted");
                drifter.act(villager, true);
                assertSame(villager, drifter.reaching(), "every client sees what it holds");
                drifter.skills().interrupt(drifter, cooldown -> cooldown);
                List<DemonBolt> rain = of(catching(() -> run(drifter, DrifterRules.Skill.RAIN.ordinal(), villager), false), DemonBolt.class);
                assertEquals(DrifterRules.RAIN.count(), rain.size(), "its acid rains down");
            } finally {
                drifter.discard();
                villager.discard();
            }
        });
    }

    @Test void inTheWaterTheMawDrawsEverythingNearTowardsItsMouth(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 301_500;
            TestWorld.load(level, x - 48, x + 48, Z - 48, Z + 48);
            pool(level, x, Z, 10);
            CorruptedMaw maw = place(level, ModEntities.CORRUPTED_MAW.get(), x + .5, -62.5, Z + .5);
            Villager swimmer = still(level, EntityType.VILLAGER, x + .5, Z + 6.5);
            swimmer.moveTo(x + .5, -62.5, Z + 6.5);
            try {
                maw.baseTick();
                assertTrue(maw.isInWater(), "it is in the water");
                assertTrue(maw.skills().usable(MawRules.Skill.WHIRLPOOL.ordinal() - 1, maw, swimmer), "where it may whirl");
                runFor(maw, MawRules.Skill.WHIRLPOOL.ordinal(), swimmer, MawRules.WHIRLPOOL.windup() + 2);
                Vec3 toward = maw.inFrame(CorruptedMaw.MOUTH).subtract(swimmer.position()).multiply(1, 0, 1).normalize();
                assertTrue(swimmer.getDeltaMovement().multiply(1, 0, 1).dot(toward) > .1, "the swimmer is drawn towards its mouth");
                maw.skills().interrupt(maw, cooldown -> cooldown);
                swimmer.setDeltaMovement(Vec3.ZERO);
                List<DemonBolt> spout = of(catching(() -> run(maw, MawRules.Skill.SPOUT.ordinal(), swimmer), false), DemonBolt.class);
                assertEquals(1, spout.size(), "it spouts");
                assertTrue(spout.get(0).look() == SpitRules.Look.WATER && spout.get(0).push() > 0, "water that throws back what it strikes");
                unpool(level, x, Z, 10);
                maw.moveTo(x + .5, -60, Z + .5, 0, 0);
                maw.baseTick();
                assertFalse(maw.isInWater());
                assertFalse(maw.skills().usable(MawRules.Skill.WHIRLPOOL.ordinal() - 1, maw, swimmer), "out of the water, it cannot whirl");
            } finally {
                maw.discard();
                swimmer.discard();
                unpool(level, x, Z, 10);
            }
        });
    }

    @Test void theNaiadThrowsItsSpearAndSingsToStunWhatIsNear(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedNaiad naiad = place(level, ModEntities.CORRUPTED_NAIAD.get(), 301_600.5, -60, Z + .5);
            Villager far = still(level, EntityType.VILLAGER, 301_600.5, Z + 12.5);
            Villager near = still(level, EntityType.VILLAGER, 301_603.5, Z + .5);
            CorruptedRemnant ally = still(level, ModEntities.CORRUPTED_REMNANT.get(), 301_597.5, Z + .5);
            try {
                List<DemonBolt> spear = of(catching(() -> run(naiad, NaiadRules.Skill.SPEAR.ordinal(), far), false), DemonBolt.class);
                assertEquals(1, spear.size(), "its spear, thrown");
                assertTrue(spear.get(0).push() > 0, "that throws back what it strikes");
                run(naiad, NaiadRules.Skill.SONG.ordinal(), near);
                assertTrue(near.hasEffect(ModEffects.STUNNED) && near.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "its song stuns and slows what is near");
                assertFalse(ally.hasEffect(ModEffects.STUNNED), "but not its own side");
            } finally {
                naiad.discard();
                far.discard();
                near.discard();
                ally.discard();
            }
        });
    }
}
