package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneDamage;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.common.altar.AltarOfferingBlockEntity;
import com.miophas.corruption.common.altar.AltarPillarBlockEntity;
import com.miophas.corruption.common.altar.AltarRecipe;
import com.miophas.corruption.common.altar.AltarStructure;
import com.miophas.corruption.common.item.CorruptedRobeArmorItem;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.altar.AltarFacing;
import com.miophas.corruption.core.altar.AltarForm;
import com.miophas.corruption.core.corruption.CorruptedRobeRules;
import com.miophas.corruption.core.demon.SolomonRules;
import com.miophas.corruption.core.demon.SolomonRules.Edict;
import com.miophas.corruption.core.demon.SolomonRules.Wyrm;
import com.miophas.corruption.common.entity.skill.BurrowSkill;
import com.miophas.corruption.common.entity.skill.RamSkill;
import com.miophas.corruption.common.entity.skill.SkyBeamSkill;
import com.miophas.corruption.core.demon.skill.SkyBeamRules;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Solomon in a world: his seal rises over where he was called up and his eight dragons burst out of it, each four chunks long,
 * its body following its head; only the arcane hurts them, nothing hurts his seal, and his bar is theirs; no robe hides
 * anyone from him or turns aside his hurt; no more than three of his dragons are at work at once; each of his dragons' eight
 * skills and his seal's eight edicts do what they say; between them his dragons swoop on their quarry, no more than two at
 * once; whatever a dragon's head and body pass through is hurt, and broken as the Ender Dragon breaks it, but for the altar,
 * and not where it burrows; its body writhes as it works, its links with it; his hurt reaches an Ender Dragon; when the last
 * dragon falls he falls, and leaves his ring; and the rite at the octagram altar of the Echo Sanctum, with the eight books
 * of his Key, calls him up once and takes the books. His dragons hunt what is not of the corruption far below them, and
 * are hunted back; his coming brings a thunderstorm, and his bar fogs the world and darkens the sky.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class SolomonServerTest {
    private static final int Z = 13000;
    private static final double GROUND = -60;

    /** His seal brings a thunderstorm on the whole test world, which does not tick its weather away: clear it after each test. */
    @AfterEach void clearTheSky(MinecraftServer server) {
        TestWorld.onServer(server, level -> level.setWeatherParameters(0, 0, false, false));
    }

    /** One tick of a creature, as the world gives it (the test world does not tick its creatures itself). */
    private static void step(Entity entity) {
        entity.tickCount++;
        entity.tick();
    }

    private static Solomon seal(ServerLevel level, int x) {
        TestWorld.load(level, x - 80, x + 80, Z - 80, Z + 80);
        Solomon seal = ModEntities.SOLOMON.get().create(level);
        seal.moveTo(x + .5, GROUND, Z + .5, 0, 0);
        level.addFreshEntity(seal);
        return seal;
    }

    /** His seal rises until it calls up his dragons; answers them. */
    private static List<SolomonWyrm> risen(Solomon seal) {
        for (int tick = 0; tick < 300 && seal.dragons().isEmpty(); tick++) step(seal);
        return seal.dragons();
    }

    private static Villager quarry(ServerLevel level, double x, double z) {
        Villager villager = EntityType.VILLAGER.create(level);
        villager.moveTo(x, GROUND, z, 0, 0);
        villager.setNoAi(true);
        level.addFreshEntity(villager);
        return villager;
    }

    /** A villager hanging where it is put, doing nothing, with this much health. */
    private static Villager standIn(ServerLevel level, double x, double y, double z, double health) {
        Villager villager = EntityType.VILLAGER.create(level);
        villager.moveTo(x, y, z, 0, 0);
        villager.setNoAi(true);
        villager.setNoGravity(true);
        villager.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        villager.setHealth((float) health);
        level.addFreshEntity(villager);
        return villager;
    }

    private static double apart(double[] a, double[] b) { return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2) + Math.pow(a[2] - b[2], 2)); }

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

    /** A dragon set alone at this place, its head turned south, serving no seal; its body laid out north of it. */
    private static SolomonWyrm dragon(ServerLevel level, double x, double y, double z) {
        TestWorld.load(level, (int) x - 80, (int) x + 80, (int) z - 80, (int) z + 80);
        SolomonWyrm dragon = ModEntities.SOLOMON_WYRM.get().create(level);
        dragon.moveTo(x, y, z, 0, 0);
        level.addFreshEntity(dragon);
        for (int tick = 0; tick < 40; tick++) {
            dragon.setDeltaMovement(0, 0, .8);
            dragon.setPos(dragon.getX(), y, dragon.getZ() + .8);
            step(dragon);
        }
        dragon.setPos(x, y, z);
        dragon.setDeltaMovement(Vec3.ZERO);
        return dragon;
    }

    /** Begins one of a dragon's skills on its quarry and runs it to its end, the dragon ticked as the world would tick it. */
    private static void run(SolomonWyrm dragon, Wyrm skill, LivingEntity target) {
        dragon.setTarget(target);
        dragon.skills().comeRound(SolomonRules.WYRM_SKILLS.indexOf(skill));
        assertTrue(dragon.begin(skill, target), skill + " begins");
        for (int tick = 0; tick < 400 && dragon.skills().busy(); tick++) step(dragon);
        assertFalse(dragon.skills().busy(), skill + " runs its course");
    }

    @Test void hisSealRisesAndHisEightDragonsBurstOutOfItFourChunksLongEach(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Solomon seal = seal(level, 600_000);
            try {
                List<SolomonWyrm> dragons = risen(seal);
                assertEquals(8, dragons.size(), "eight dragons");
                assertEquals(GROUND + SolomonRules.CORE_HEIGHT, seal.home().y, 1, "over where he was called up");
                Set<Integer> books = new HashSet<>();
                for (SolomonWyrm dragon : dragons) {
                    books.add(dragon.index());
                    assertSame(seal, dragon.seal());
                    assertTrue(dragon.isMultipartEntity());
                    assertEquals(30, dragon.getParts().length, "thirty links");
                    assertEquals(SolomonRules.JOINTS, dragon.chain().joints());
                }
                assertEquals(Set.of(0, 1, 2, 3, 4, 5, 6, 7), books, "one of each book");
                // They fly out, and their bodies unwind behind them.
                for (int tick = 0; tick < 160; tick++) {
                    step(seal);
                    for (SolomonWyrm dragon : dragons) step(dragon);
                }
                for (SolomonWyrm dragon : dragons) {
                    var chain = dragon.chain();
                    double drawn = 0;
                    for (int joint = 1; joint < chain.joints(); joint++)
                        drawn += Math.sqrt(Math.pow(chain.x(joint) - chain.x(joint - 1), 2) + Math.pow(chain.y(joint) - chain.y(joint - 1), 2) + Math.pow(chain.z(joint) - chain.z(joint - 1), 2));
                    assertTrue(drawn > chain.length() / 2, "dragon " + dragon.index() + " unwinding, no longer in a heap: " + drawn);
                    assertTrue(dragon.distanceTo(seal) < SolomonRules.LEASH, "never far from the seal");
                    assertTrue(dragon.getY() > GROUND, "in the air");
                }
            } finally {
                seal.dragons().forEach(Entity::discard);
                seal.discard();
            }
        });
    }

    @Test void onlyTheArcaneHurtsHisDragonsNothingHurtsHisSealAndHisBarIsTheirs(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Solomon seal = seal(level, 602_000);
            try {
                List<SolomonWyrm> dragons = risen(seal);
                SolomonWyrm dragon = dragons.get(0);
                FakePlayer player = player(level, "solomon_striker");
                assertFalse(dragon.hurt(level.damageSources().playerAttack(player), 50), "a blade passes through");
                assertEquals(SolomonRules.WYRM_HEALTH, dragon.getHealth(), 1e-3);
                assertTrue(dragon.hurt(ArcaneDamage.source(level, player), 100), "a spell reaches it");
                dragon.invulnerableTime = 0;
                SolomonWyrm.Link link = (SolomonWyrm.Link) dragon.getParts()[15];
                assertTrue(link.hurt(ArcaneDamage.source(level, player), 100), "so does one on any link of its body");
                float left = dragon.getHealth();
                assertTrue(left < SolomonRules.WYRM_HEALTH - 150 && left > SolomonRules.WYRM_HEALTH - 200, "both reach it, its armour taking a little: " + left);
                assertFalse(seal.hurt(ArcaneDamage.source(level, player), 1000), "nothing hurts his seal");
                assertFalse(seal.isPickable());
                step(seal);
                assertEquals(left / SolomonRules.WYRM_HEALTH, seal.share(dragon.index()), 1e-3, "its share of his bar");
                assertEquals(1F, seal.share((dragon.index() + 1) % 8), 1e-3);
            } finally {
                seal.dragons().forEach(Entity::discard);
                seal.discard();
            }
        });
    }

    @Test void noRobeHidesAnyoneFromHimNorTurnsAsideHisHurt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SolomonWyrm dragon = dragon(level, 604_000, GROUND + 20, Z);
            Amdusias unicorn = ModEntities.AMDUSIAS.get().create(level);
            unicorn.moveTo(604_010, GROUND, Z, 0, 0);
            level.addFreshEntity(unicorn);
            FakePlayer wearer = player(level, "solomon_robed");
            try {
                for (DeferredItem<CorruptedRobeArmorItem> robe : ModItems.CORRUPTED_ROBES)
                    if (robe.get().robeTier() == CorruptedRobeRules.Tier.ADVANCED) wearer.setItemSlot(robe.get().getEquipmentSlot(), new ItemStack(robe.get()));
                assertTrue(CorruptionVeilApi.hiddenFrom(wearer, unicorn), "an Advanced robe hides its wearer from the other lords");
                assertFalse(CorruptionVeilApi.hiddenFrom(wearer, dragon), "not from Solomon's dragons");
                assertTrue(dragon.canAttack(wearer));
                var his = ModDamageTypes.corruption(level, dragon);
                assertTrue(his.is(ModDamageTypes.SOLOMON), "his corruption's hurt is his own");
                assertEquals(10, CorruptionVeilApi.damageTaken(wearer, his, 10), 1e-6, "which no robe turns aside");
                assertEquals(0, CorruptionVeilApi.damageTaken(wearer, ModDamageTypes.corruption(level, unicorn), 10), 1e-6, "as it does another lord's");
                // What he calls up is not deceived either.
                CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
                assertTrue(CorruptionVeilApi.hiddenFrom(wearer, remnant));
                CorruptionVeilApi.unveil(remnant);
                assertFalse(CorruptionVeilApi.hiddenFrom(wearer, remnant));
                remnant.discard();
            } finally {
                dragon.discard();
                unicorn.discard();
            }
        });
    }

    @Test void noMoreThanThreeOfHisDragonsAreAtWorkAtOnce(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Solomon seal = seal(level, 606_000);
            try {
                List<SolomonWyrm> dragons = risen(seal);
                Villager quarry = quarry(level, 606_000, Z + 20);
                assertTrue(seal.mayCast());
                for (int dragon = 0; dragon < 3; dragon++) {
                    dragons.get(dragon).skills().comeRound(SolomonRules.WYRM_SKILLS.indexOf(Wyrm.STORM));
                    assertTrue(dragons.get(dragon).begin(Wyrm.STORM, quarry));
                }
                assertFalse(seal.mayCast(), "three at work: no fourth");
                quarry.discard();
            } finally {
                seal.dragons().forEach(Entity::discard);
                seal.discard();
            }
        });
    }

    @Test void itsBeamDelugeFireballsStormAndHandsStrikeItsQuarry(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 608_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 12, Z);
            List<Entity> joined = new ArrayList<>();
            try {
                Villager beamed = quarry(level, x, Z + 24);
                run(dragon, Wyrm.BEAM, beamed);
                assertTrue(beamed.getHealth() < beamed.getMaxHealth() || beamed.isDeadOrDying(), "the beam hurts");

                Villager deluged = quarry(level, x, Z + 24);
                joined.addAll(catching(() -> run(dragon, Wyrm.DELUGE, deluged), true));
                assertTrue(joined.stream().filter(DemonBolt.class::isInstance).count() >= SolomonRules.DELUGE.count() * 9 / 10, "a deluge of bolts");

                Villager burnt = quarry(level, x + 3, Z + 24);
                joined.addAll(catching(() -> run(dragon, Wyrm.FIREBALLS, burnt), true));
                List<SolomonFireball> balls = joined.stream().filter(SolomonFireball.class::isInstance).map(SolomonFireball.class::cast).toList();
                assertEquals(SolomonRules.FIREBALLS.count(), balls.size(), "five fireballs");
                balls.get(0).burst(level, burnt.position());
                assertTrue(burnt.getHealth() < burnt.getMaxHealth() || burnt.isDeadOrDying(), "Baal's fire hurts");
                assertTrue(burnt.isOnFire(), "and burns");

                Villager struck = quarry(level, x - 3, Z + 24);
                run(dragon, Wyrm.STORM, struck);
                assertTrue(struck.getHealth() < struck.getMaxHealth() || struck.isDeadOrDying(), "the lightning hurts");

                Villager smashed = quarry(level, x + 6, Z + 30);
                joined.addAll(catching(() -> run(dragon, Wyrm.HANDS, smashed), true));
                List<SolomonHand> hands = joined.stream().filter(SolomonHand.class::isInstance).map(SolomonHand.class::cast).toList();
                assertEquals(SolomonRules.HANDS.hands(), hands.size(), "three hands");
                assertEquals(0, hands.get(0).position().distanceTo(new Vec3(smashed.getX(), hands.get(0).getY(), smashed.getZ())), .01, "the first under its quarry");
                for (int tick = 0; tick < SolomonRules.HANDS.lasts() + 2; tick++) for (SolomonHand hand : hands) if (!hand.isRemoved()) step(hand);
                assertTrue(hands.get(0).struck().contains(smashed), "a hand falls on it");
                assertTrue(hands.stream().allMatch(Entity::isRemoved), "then sinks away");
            } finally {
                joined.forEach(Entity::discard);
                dragon.discard();
            }
        });
    }

    @Test void itBurrowsUpThroughItsQuarryAndRamsWhereItStood(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 610_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 14, Z);
            try {
                Villager under = quarry(level, x, Z + 30);
                double lowest = Double.MAX_VALUE;
                dragon.setTarget(under);
                dragon.skills().comeRound(SolomonRules.WYRM_SKILLS.indexOf(Wyrm.BURROW));
                assertTrue(dragon.begin(Wyrm.BURROW, under));
                for (int tick = 0; tick < 400 && dragon.skills().busy(); tick++) {
                    step(dragon);
                    lowest = Math.min(lowest, dragon.headCentre().y);
                }
                assertTrue(lowest < GROUND - 3, "under the ground: " + lowest);
                assertEquals(BurrowSkill.Phase.DONE, dragon.burrow().phase(), "up through it and on into the sky");
                assertTrue(dragon.burrow().struck().contains(under), "its quarry caught as it burst up");
                assertTrue(dragon.getY() > GROUND + 10, "high again");

                Villager rammed = quarry(level, dragon.getX(), dragon.getZ() + 26);
                dragon.setTarget(rammed);
                dragon.skills().comeRound(SolomonRules.WYRM_SKILLS.indexOf(Wyrm.RAM));
                assertTrue(dragon.begin(Wyrm.RAM, rammed));
                lowest = Double.MAX_VALUE;
                for (int tick = 0; tick < 400 && dragon.skills().busy(); tick++) {
                    step(dragon);
                    lowest = Math.min(lowest, dragon.headCentre().y);
                }
                assertEquals(RamSkill.Phase.DONE, dragon.ram().phase(), "rammed, under, and out again");
                assertTrue(dragon.ram().struck().contains(rammed), "its quarry caught where it stood");
                assertTrue(lowest < GROUND - 3, "through the ground");
            } finally {
                dragon.discard();
            }
        });
    }

    @Test void venusBlessesItAndItsKin(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 612_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 12, Z), kin = dragon(level, x + 20, GROUND + 12, Z);
            SolomonWyrm far = dragon(level, x + 200, GROUND + 12, Z);
            Villager quarry = quarry(level, x, Z + 20);
            try {
                for (SolomonWyrm hurt : List.of(dragon, kin, far)) hurt.setHealth(hurt.getMaxHealth() / 2);
                run(dragon, Wyrm.VENUS, quarry);
                double healed = SolomonRules.VENUS.healed(SolomonRules.WYRM_HEALTH);
                assertTrue(dragon.getHealth() >= SolomonRules.WYRM_HEALTH / 2 + healed - 1, "itself healed");
                assertTrue(kin.getHealth() >= SolomonRules.WYRM_HEALTH / 2 + healed - 1, "its kin about it too");
                assertEquals(SolomonRules.WYRM_HEALTH / 2, far.getHealth(), 1e-3, "not one far off");
                assertTrue(kin.hasEffect(MobEffects.ABSORPTION) && kin.hasEffect(MobEffects.REGENERATION), "shielded and mending");
            } finally {
                dragon.discard();
                kin.discard();
                far.discard();
                quarry.discard();
            }
        });
    }

    @Test void hisSealsEdicts(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 614_000;
            Solomon seal = seal(level, x);
            List<Entity> joined = new ArrayList<>();
            try {
                risen(seal);
                seal.dragons().forEach(Entity::discard);
                Villager quarry = quarry(level, x + 12, Z + 12);
                for (Edict edict : SolomonRules.EDICTS) seal.edicts().comeRound(SolomonRules.EDICTS.indexOf(edict));
                joined.addAll(catching(() -> {
                    for (Edict edict : List.of(Edict.GAZE, Edict.ELITES, Edict.REMNANTS, Edict.DORMANT, Edict.STUN, Edict.DRAKES)) {
                        seal.edicts().comeRound(SolomonRules.EDICTS.indexOf(edict));
                        assertTrue(seal.begin(edict, quarry), edict + " begins");
                        for (int tick = 0; tick < 200 && seal.edicts().busy(); tick++) seal.edicts().tick(seal, quarry, cooldown -> cooldown);
                    }
                }, true));
                assertTrue(quarry.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && quarry.hasEffect(MobEffects.BLINDNESS), "brainwashed");
                List<Mob> elites = seal.elites().called();
                assertTrue(elites.size() == 3 || elites.size() == 4, "three or four elites: " + elites.size());
                for (Mob elite : elites) {
                    assertTrue(CorruptionVeilApi.pierces(elite), "no robe deceives them");
                    assertTrue(elite.distanceTo(quarry) < SolomonRules.ELITES.radius() + 3, "about the quarry");
                }
                List<CorruptedRemnant> remnants = joined.stream().filter(CorruptedRemnant.class::isInstance).map(CorruptedRemnant.class::cast).toList();
                assertEquals(SolomonRules.REMNANTS.count(), remnants.size(), "a band of remnants");
                for (CorruptedRemnant remnant : remnants) {
                    assertTrue(CorruptionVeilApi.pierces(remnant));
                    assertTrue(remnant.distanceTo(quarry) < SolomonRules.REMNANTS.radius() + 2, "about the quarry");
                }
                assertTrue(seal.dormant().turned() > 50, "the ground about it gone dormant: " + seal.dormant().turned());
                assertTrue(level.getBlockState(BlockPos.containing(quarry.getX() + 3, GROUND - 1, quarry.getZ())).is(ModBlocks.DEACTIVATED_CORRUPTION.get()),
                        "about the quarry, not the seal");
                assertTrue(quarry.hasEffect(ModEffects.STUNNED), "stunned");
                List<SolomonDrake> drakes = seal.drakes().sent();
                assertEquals(8, drakes.size(), "eight drakes");
                Set<Long> ways = new HashSet<>();
                for (SolomonDrake drake : drakes) {
                    Vec3 off = drake.from().subtract(seal.drakes().mark());
                    assertEquals(SolomonRules.DRAKES.distance(), Math.hypot(off.x, off.z), 1e-6);
                    ways.add(Math.round(Math.toDegrees(Math.atan2(off.z, off.x))));
                }
                assertEquals(8, ways.size(), "from the eight ways");
                for (int tick = 0; tick < SolomonRules.DRAKES.lasts() + 2; tick++) for (SolomonDrake drake : drakes) if (!drake.isRemoved()) step(drake);
                assertTrue(drakes.stream().anyMatch(drake -> drake.struck().contains(quarry)), "they rush through it");
                assertTrue(drakes.stream().allMatch(Entity::isRemoved), "and are gone");
                // The upheaval heaves the ground under the quarry.
                Villager heaved = quarry(level, x - 12, Z - 12);
                seal.edicts().comeRound(SolomonRules.EDICTS.indexOf(Edict.UPHEAVAL));
                assertTrue(seal.begin(Edict.UPHEAVAL, heaved));
                for (int tick = 0; tick < 200 && seal.edicts().busy(); tick++) seal.edicts().tick(seal, heaved, cooldown -> cooldown);
                assertTrue(heaved.getHealth() < heaved.getMaxHealth() || heaved.isDeadOrDying() || heaved.getY() != GROUND, "heaved, broken under or speared");
                heaved.discard();
                quarry.discard();
            } finally {
                joined.forEach(Entity::discard);
                seal.discard();
            }
        });
    }

    @Test void whenTheLastDragonFallsHeFallsAndLeavesHisRing(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Solomon seal = seal(level, 616_000);
            List<Entity> joined = new ArrayList<>();
            try {
                List<SolomonWyrm> dragons = risen(seal);
                FakePlayer slayer = player(level, "solomon_slayer");
                for (int index = 0; index < 7; index++) dragons.get(index).hurt(ArcaneDamage.source(level, slayer), 10_000);
                step(seal);
                assertEquals(7, seal.fallen());
                assertFalse(seal.isDeadOrDying(), "while one lives, he lives");
                assertEquals(1F, seal.share(dragons.get(7).index()), 1e-3, "the last one's share still full");
                joined.addAll(catching(() -> {
                    dragons.get(7).hurt(ArcaneDamage.source(level, slayer), 10_000);
                    step(seal);
                }, true));
                assertTrue(seal.isDeadOrDying(), "the last fallen, he falls");
                assertTrue(joined.stream().anyMatch(entity -> entity instanceof ItemEntity item && item.getItem().is(ModItems.SOLOMON_RING.get())), "and leaves his ring");
                for (int tick = 0; tick < SolomonRules.DEATH_TICKS + 120 && !seal.isRemoved(); tick++) step(seal);
                assertTrue(seal.isRemoved(), "and is gone");
                assertTrue(SolomonLedger.of(level).fallen(seal.getUUID()).isEmpty(), "his page in the ledger done with");
            } finally {
                joined.forEach(Entity::discard);
                seal.dragons().forEach(Entity::discard);
                if (!seal.isRemoved()) seal.discard();
            }
        });
    }

    @Test void theLedgerTellsHisSealOfADragonThatFellOutOfItsSight(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Solomon seal = seal(level, 618_000);
            try {
                risen(seal);
                SolomonLedger.of(level).fell(seal.getUUID(), 3);
                step(seal);
                assertEquals(0, seal.share(3), 1e-6);
                assertEquals(1, seal.fallen());
            } finally {
                seal.dragons().forEach(Entity::discard);
                SolomonLedger.of(level).forget(seal.getUUID());
                seal.discard();
            }
        });
    }

    /**
     * What his hardest single blows do to a 20-health stand-in, bare and in full netherite (armour counts once it has lived a
     * tick): printed as MEASURE lines for the changelog. They are set by comparison with the other lords, not tuned to these.
     */
    @Test void measureHisBlowsAgainstArmour(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SolomonWyrm dragon = dragon(level, 622_000, GROUND + 12, Z);
            Villager bare = quarry(level, 622_000, Z + 10), armoured = quarry(level, 622_004, Z + 10);
            try {
                net.minecraft.world.entity.EquipmentSlot[] slots = {net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST,
                        net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.FEET};
                net.minecraft.world.item.Item[] netherite = {net.minecraft.world.item.Items.NETHERITE_HELMET, net.minecraft.world.item.Items.NETHERITE_CHESTPLATE,
                        net.minecraft.world.item.Items.NETHERITE_LEGGINGS, net.minecraft.world.item.Items.NETHERITE_BOOTS};
                for (int at = 0; at < 4; at++) armoured.setItemSlot(slots[at], new ItemStack(netherite[at]));
                armoured.tick();
                var hurt = ModDamageTypes.corruption(level, dragon);
                StringBuilder line = new StringBuilder("MEASURE Solomon's blows on a 20-health stand-in, bare / full netherite:");
                for (var blow : List.of(java.util.Map.entry("beam pulse", SolomonRules.BEAM.damage()), java.util.Map.entry("deluge bolt", SolomonRules.DELUGE.damage()),
                        java.util.Map.entry("fireball heart", SolomonRules.FIREBALLS.damage()), java.util.Map.entry("burrow", SolomonRules.BURROW.damage()),
                        java.util.Map.entry("ram", SolomonRules.RAM.damage()), java.util.Map.entry("lightning", SolomonRules.STORM.damage()),
                        java.util.Map.entry("hand", SolomonRules.HANDS.damage()), java.util.Map.entry("drake", SolomonRules.DRAKES.damage()),
                        java.util.Map.entry("head's touch", SolomonRules.HEAD_CONTACT), java.util.Map.entry("body's touch", SolomonRules.BODY_CONTACT),
                        java.util.Map.entry("sky beam pulse", SolomonRules.SKY_BEAM.damage()), java.util.Map.entry("star beam pulse", SolomonRules.PENTAGRAM.damage()))) {
                    float[] lost = new float[2];
                    int index = 0;
                    for (Villager wearer : List.of(bare, armoured)) {
                        wearer.setHealth(20);
                        wearer.invulnerableTime = 0;
                        wearer.hurt(hurt, (float) (double) blow.getValue());
                        lost[index++] = 20 - wearer.getHealth();
                    }
                    line.append(String.format(" %s %.1f / %.2f;", blow.getKey(), lost[0], lost[1]));
                    assertTrue(lost[1] < lost[0], blow.getKey() + ": armour takes some of it");
                }
                System.out.println(line);
            } finally {
                dragon.discard();
                bare.discard();
                armoured.discard();
            }
        });
    }

    @Test void itBreaksWhatItFliesThroughAsTheEnderDragonDoesButNotTheAltar(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 624_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 20, Z);
            List<BlockPos> filled = new ArrayList<>();
            try {
                // Stone all through its head and one link of its body; and in them obsidian, an altar pillar and an offering.
                AABB head = dragon.getBoundingBox(), part = dragon.getParts()[12].getBoundingBox();
                for (AABB box : List.of(head, part))
                    for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(box.minX, box.minY, box.minZ), BlockPos.containing(box.maxX, box.maxY, box.maxZ))) {
                        level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
                        filled.add(pos.immutable());
                    }
                BlockPos obsidian = BlockPos.containing(head.getCenter()), pillar = obsidian.above(), altar = BlockPos.containing(part.getCenter());
                level.setBlock(obsidian, Blocks.OBSIDIAN.defaultBlockState(), 3);
                level.setBlock(pillar, ModBlocks.ALTAR_PILLAR.get().defaultBlockState(), 3);
                level.setBlock(altar, ModBlocks.ALTAR_OFFERING.get().defaultBlockState(), 3);
                GameRules.BooleanValue griefing = level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING);
                griefing.set(false, server);
                try {
                    assertEquals(0, dragon.smash(level), "nothing where creatures may not break blocks");
                } finally {
                    griefing.set(true, server);
                }
                int broken = dragon.smash(level);
                assertEquals(filled.size() - 3, broken, "everything else it passes through");
                for (BlockPos pos : filled)
                    if (!pos.equals(obsidian) && !pos.equals(pillar) && !pos.equals(altar)) assertTrue(level.getBlockState(pos).isAir(), "broken at " + pos);
                assertTrue(level.getBlockState(obsidian).is(Blocks.OBSIDIAN), "not what the Ender Dragon cannot break");
                assertTrue(level.getBlockState(pillar).is(ModBlocks.ALTAR_PILLAR.get()) && level.getBlockState(altar).is(ModBlocks.ALTAR_OFFERING.get()), "nor the altar");
                assertTrue(level.getEntitiesOfClass(ItemEntity.class, head.minmax(part).inflate(4)).isEmpty(), "dropping nothing");
            } finally {
                for (BlockPos pos : filled) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                dragon.discard();
            }
        });
    }

    @Test void itSwimsThroughTheEarthWhenItBurrowsAndItsBodyAfterIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 626_000;
            // Ten blocks of stone over the flat world, so there is earth to burrow through.
            int top = (int) GROUND + 8;
            TestWorld.load(level, x - 80, x + 80, Z - 80, Z + 80);
            for (BlockPos pos : BlockPos.betweenClosed(x - 10, (int) GROUND - 1, Z - 6, x + 10, top - 1, Z + 44)) level.setBlock(pos, Blocks.STONE.defaultBlockState(), 2);
            SolomonWyrm dragon = dragon(level, x, top + 14, Z);
            Villager under = standIn(level, x, top, Z + 30, 1000);
            try {
                dragon.setTarget(under);
                dragon.skills().comeRound(SolomonRules.WYRM_SKILLS.indexOf(Wyrm.BURROW));
                assertTrue(dragon.begin(Wyrm.BURROW, under));
                int buried = 0;
                List<BlockPos> tunnel = new ArrayList<>();
                for (int tick = 0; tick < 400 && dragon.skills().busy(); tick++) {
                    step(dragon);
                    if (dragon.burrowed()) buried++;
                    if (dragon.burrow().phase() == BurrowSkill.Phase.TUNNEL) tunnel.add(BlockPos.containing(dragon.headCentre()));
                }
                assertEquals(BurrowSkill.Phase.DONE, dragon.burrow().phase());
                assertTrue(buried > 10, "it swam through the earth, breaking none of it: buried " + buried + " ticks");
                assertTrue(dragon.burrow().struck().contains(under), "and burst up through its quarry");
                // Its body swims after it, however long it takes to come out: the way under the ground is not carved out behind it.
                for (int tick = 0; tick < 120; tick++) step(dragon);
                long open = tunnel.stream().filter(pos -> level.getBlockState(pos).isAir()).count();
                assertFalse(tunnel.isEmpty());
                assertTrue(tunnel.stream().allMatch(pos -> pos.getY() < top - 2), "under the ground, from where it went in (found however high it hung)");
                assertTrue(open <= tunnel.size() / 10, "the way it went under the ground still stone: " + open + " of " + tunnel.size() + " open");
            } finally {
                dragon.discard();
                under.discard();
            }
        });
    }

    @Test void whatItsHeadAndBodyPassThroughIsHurtAgainAndAgain(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 628_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 30, Z);
            Vec3 head = dragon.headCentre();
            double[] middle = dragon.link(12), far = dragon.link(20);
            Villager bitten = standIn(level, head.x, head.y - 1, head.z, 100), crushed = standIn(level, middle[0], middle[1] - 1, middle[2], 100);
            Villager aside = standIn(level, x + 12, GROUND + 30, Z, 100);
            FakePlayer builder = player(level, "solomon_builder");
            builder.setGameMode(GameType.CREATIVE);
            builder.moveTo(far[0], far[1] - 1, far[2]);
            level.addFreshEntity(builder);
            try {
                List<LivingEntity> hurt = dragon.touch(level);
                assertTrue(hurt.contains(bitten), "its head bites");
                assertTrue(hurt.contains(crushed), "its body crushes");
                assertFalse(hurt.contains(aside), "not what it does not touch");
                assertFalse(hurt.contains(builder), "nor a player who only builds");
                assertEquals(100 - SolomonRules.HEAD_CONTACT, bitten.getHealth(), 1e-3);
                assertEquals(100 - SolomonRules.BODY_CONTACT, crushed.getHealth(), 1e-3);
                assertTrue(bitten.getDeltaMovement().lengthSqr() > .1 && crushed.getDeltaMovement().lengthSqr() > .1, "and throws");
                assertTrue(dragon.touch(level).isEmpty(), "not again at once");
                // Half a second on, still in it: hurt again.
                crushed.invulnerableTime = 10;
                assertTrue(dragon.touch(level).contains(crushed), "again");
                assertEquals(100 - 2 * SolomonRules.BODY_CONTACT, crushed.getHealth(), 1e-3);
            } finally {
                dragon.discard();
                bitten.discard();
                crushed.discard();
                aside.discard();
                builder.discard();
            }
        });
    }

    @Test void hisHurtReachesAnEnderDragon(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 630_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 30, Z);
            EnderDragon ender = EntityType.ENDER_DRAGON.create(level);
            ender.moveTo(x, GROUND + 60, Z + 40, 0, 0);
            level.addFreshEntity(ender);
            try {
                float full = ender.getHealth();
                assertTrue(ender.hurt(ModDamageTypes.corruption(level, dragon), 20));
                float lost = full - ender.getHealth();
                // Vanilla lets only a player's blow or a blast hurt it; his is let in too. A blow on its body, as every blow not
                // on its head, takes a quarter and one.
                assertEquals(20 / 4F + 1, lost, 1e-3, "his blow reaches it");
                ender.invulnerableTime = 0;
                ender.moveTo(dragon.getX(), dragon.getY() - 2, dragon.getZ(), 0, 0);
                assertTrue(dragon.touch(level).contains(ender), "and so does his dragon's touch");
                assertTrue(full - ender.getHealth() > lost, "it is hurt again");
            } finally {
                ender.discard();
                dragon.discard();
            }
        });
    }

    @Test void betweenItsThingsItSwoopsThroughItsQuarryNoMoreThanTwoAtOnce(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 632_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 16, Z);
            Villager quarry = standIn(level, x, GROUND, Z + 30, 1000);
            try {
                dragon.setTarget(quarry);
                dragon.beginSwoop();
                assertTrue(dragon.swooping());
                assertEquals(Wyrm.SWOOP, dragon.skill(), "its clients see it swoop, and pose it so");
                assertFalse(dragon.casting(), "none of the three at work");
                double nearest = Double.MAX_VALUE;
                for (int tick = 0; tick < 200 && dragon.swooping(); tick++) {
                    step(dragon);
                    nearest = Math.min(nearest, dragon.headCentre().distanceTo(quarry.getBoundingBox().getCenter()));
                }
                assertFalse(dragon.swooping(), "done");
                assertTrue(nearest < 3, "through its quarry: " + nearest);
                assertTrue(quarry.getHealth() < 1000, "hurt as it went through");
                assertTrue(dragon.getY() > GROUND + 5, "and away up again: " + dragon.getY());
                assertEquals(Wyrm.NONE, dragon.skill());
            } finally {
                dragon.discard();
                quarry.discard();
            }
            Solomon seal = seal(level, 634_000);
            try {
                List<SolomonWyrm> dragons = risen(seal);
                assertTrue(seal.maySwoop());
                dragons.get(0).beginSwoop();
                dragons.get(1).beginSwoop();
                assertFalse(seal.maySwoop(), "two swooping: no third");
                assertTrue(seal.mayCast(), "and a swoop is none of the three at work");
            } finally {
                seal.dragons().forEach(Entity::discard);
                seal.discard();
            }
        });
    }

    @Test void itsBeamsOutOfTheSkyStrikeWhereTheirStarsSay(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 646_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 12, Z);
            List<Entity> joined = new ArrayList<>();
            List<Entity> stood = new ArrayList<>();
            try {
                for (Wyrm kind : List.of(Wyrm.SKY_BEAM, Wyrm.PENTAGRAM, Wyrm.HEXAGRAM, Wyrm.OCTAGRAM)) {
                    int index = SolomonRules.WYRM_SKILLS.indexOf(kind);
                    SkyBeamSkill skill = (SkyBeamSkill) dragon.skills().skill(index);
                    SkyBeamRules rules = skill.rules();
                    Villager stayed = standIn(level, x, GROUND, Z + 24, 1000);
                    Villager clear = standIn(level, x + rules.spread() + rules.width() + 4, GROUND, Z + 24, 1000);
                    stood.addAll(List.of(stayed, clear));
                    dragon.setTarget(stayed);
                    dragon.skills().comeRound(index);
                    joined.addAll(catching(() -> assertTrue(dragon.begin(kind, stayed), kind + " begins"), true));
                    String effect = "corruption:" + SkyBeamSkill.effect(rules.beams());
                    assertTrue(joined.stream().anyMatch(entity -> entity instanceof SpellVisualEntity shown && shown.effect().toString().equals(effect)),
                            kind + ": its circle shown in the sky, " + effect);
                    // One standing where a star's first beam will sweep by, a little into its pouring.
                    Vec3 mark = skill.mark();
                    double[] point = rules.points(skill.turn(), rules.every() * 6)[0];
                    Villager swept = standIn(level, mark.x + point[0], mark.y, mark.z + point[1], 1000);
                    stood.add(swept);
                    for (int tick = 0; tick < 400 && dragon.skills().busy(); tick++) step(dragon);
                    assertFalse(dragon.skills().busy(), kind + " runs its course");
                    assertTrue(skill.struck().contains(stayed), kind + ": whoever stayed on the mark is caught");
                    if (rules.beams() > 1) assertTrue(skill.struck().contains(swept), kind + ": and one where its beams sweep by");
                    assertFalse(skill.struck().contains(clear), kind + ": not one clear of them");
                    assertTrue(stayed.getHealth() < 1000 && clear.getHealth() == 1000);
                }
            } finally {
                joined.forEach(Entity::discard);
                stood.forEach(Entity::discard);
                dragon.discard();
            }
        });
    }

    @Test void itsBodyWrithesAsItWorksAndItsLinksWithIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 636_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 24, Z);
            Villager quarry = standIn(level, x, GROUND, Z + 30, 1000);
            try {
                for (int link = 0; link < dragon.chain().links(); link++) assertArrayEquals(dragon.chain().link(link), dragon.link(link), 1e-9, "at rest, where its chain lies");
                dragon.setTarget(quarry);
                dragon.skills().comeRound(SolomonRules.WYRM_SKILLS.indexOf(Wyrm.STORM));
                assertTrue(dragon.begin(Wyrm.STORM, quarry));
                double moving = 0;
                double[] before = null;
                for (int tick = 0; tick < SolomonRules.STORM.timing().windup() + 20; tick++) {
                    step(dragon);
                    double[] now = dragon.link(20);
                    if (before != null) moving = Math.max(moving, apart(before, now));
                    before = now;
                }
                double most = 0;
                for (int link = 0; link < dragon.chain().links(); link++) most = Math.max(most, apart(dragon.link(link), dragon.chain().link(link)));
                assertTrue(most > 1, "its body writhes off the way it lies: " + most);
                assertTrue(moving > .1, "and keeps moving as it hangs at work: " + moving);
                SolomonWyrm.Link link = (SolomonWyrm.Link) dragon.getParts()[20];
                double[] at = dragon.link(20);
                assertEquals(at[0], link.getX(), 1e-6, "its links' hitboxes with it");
                assertEquals(at[1], link.getY() + link.getBbHeight() / 2, 1e-6);
                assertEquals(at[2], link.getZ(), 1e-6);
            } finally {
                dragon.discard();
                quarry.discard();
            }
        });
    }

    /**
     * What breaking and hurting with its whole body costs the server, a dragon a tick, flying through open air (where it
     * spends most of a fight): printed as a MEASURE line. Eight of them fly at once.
     */
    @Test void measureWhatItsBodyCostsATick(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            SolomonWyrm dragon = dragon(level, 638_000, GROUND + 30, Z);
            try {
                for (int warm = 0; warm < 2000; warm++) {
                    dragon.smash(level);
                    dragon.touch(level);
                }
                int ticks = 5000;
                long began = System.nanoTime();
                for (int tick = 0; tick < ticks; tick++) {
                    dragon.smash(level);
                    dragon.touch(level);
                }
                double each = (System.nanoTime() - began) / 1000.0 / ticks;
                System.out.println(String.format("MEASURE breaking and touching with its whole body, in open air: %.1f microseconds a dragon a tick (%.2f ms a tick for eight)", each, each * 8 / 1000));
                assertTrue(each < 2000, "well under a tick's 50 ms: " + each);
            } finally {
                dragon.discard();
            }
        });
    }

    /** The eight books of his Key, I to VIII. */
    private static final List<DeferredItem<net.minecraft.world.item.Item>> BOOKS = List.of(ModItems.CLAVICULA_SALOMONIS_I, ModItems.CLAVICULA_SALOMONIS_II,
            ModItems.CLAVICULA_SALOMONIS_III, ModItems.CLAVICULA_SALOMONIS_IV, ModItems.CLAVICULA_SALOMONIS_V, ModItems.CLAVICULA_SALOMONIS_VI,
            ModItems.CLAVICULA_SALOMONIS_VII, ModItems.CLAVICULA_SALOMONIS_VIII);

    private static AltarRecipe rite(ServerLevel level) {
        return (AltarRecipe) level.getServer().getRecipeManager().byKey(ResourceLocation.parse("corruption:altar/solomon")).orElseThrow().value();
    }

    /** The Echo Sanctum built round an octagram altar here, with no player anywhere near; its offering. */
    private static AltarOfferingBlockEntity sanctum(ServerLevel level, int x) {
        BlockPos at = new BlockPos(x, (int) GROUND + 1, Z);
        TestWorld.load(level, x - 16, x + 16, Z - 16, Z + 16);
        rite(level).structure().orElseThrow().value().buildWithAltar(level, at, AltarFacing.NORTH);
        assertTrue(level.players().stream().noneMatch(player -> player.distanceToSqr(at.getCenter()) < 64 * 64), "no player anywhere near the altar");
        return (AltarOfferingBlockEntity) level.getBlockEntity(at);
    }

    private static AltarPillarBlockEntity pillar(AltarOfferingBlockEntity offering, int slot) {
        return (AltarPillarBlockEntity) offering.getLevel().getBlockEntity(offering.place(AltarForm.OCTAGRAM.east(slot), AltarForm.OCTAGRAM.north(slot)));
    }

    /** Lays the eight books on the eight pillars, in order. */
    private static void lay(AltarOfferingBlockEntity offering) {
        for (int slot = 0; slot < 8; slot++) pillar(offering, slot).set(new ItemStack(BOOKS.get(slot).get()));
    }

    /** Runs the offering this many ticks, as the world would; what joined the world meanwhile. */
    private static List<Entity> perform(AltarOfferingBlockEntity offering, int ticks) {
        Level level = offering.getLevel();
        BlockPos at = offering.getBlockPos();
        return catching(() -> {
            for (int tick = 0; tick < ticks; tick++) AltarOfferingBlockEntity.serverTick(level, at, level.getBlockState(at), offering);
        }, true);
    }

    @Test void itHuntsCreaturesFarBelowItAndTheyHuntIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            int x = 642_000;
            SolomonWyrm dragon = dragon(level, x, GROUND + 12, Z);
            IronGolem golem = EntityType.IRON_GOLEM.create(level);
            golem.moveTo(dragon.getX(), GROUND, dragon.getZ(), 0, 0);
            level.addFreshEntity(golem);
            try {
                // A creature looks for another only four blocks up or down: the dragon is twelve up.
                for (int tick = 0; tick < 200 && golem.getTarget() != dragon; tick++) step(golem);
                assertSame(dragon, golem.getTarget(), "a golem on the ground turns on a dragon twelve blocks over it");
                for (int tick = 0; tick < 200 && dragon.getTarget() == null; tick++) step(dragon);
                assertSame(golem, dragon.getTarget(), "and the dragon turns on the golem far below it");
                // The corruption's lesser kind it still looks for only four blocks up or down, as any creature looks for another.
                CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
                remnant.moveTo(golem.getX(), golem.getY() + 12, golem.getZ());
                assertFalse(CorruptionEnemyTargetGoal.enemy(golem, remnant), "not a remnant twelve up");
                remnant.moveTo(golem.getX() + 5, golem.getY(), golem.getZ());
                assertTrue(CorruptionEnemyTargetGoal.enemy(golem, remnant), "a remnant level with it");
                remnant.discard();
                Solomon seal = ModEntities.SOLOMON.get().create(level);
                assertFalse(seal.canBeSeenAsEnemy(), "nothing hunts his seal, which nothing can hurt");
                seal.discard();
            } finally {
                golem.discard();
                dragon.discard();
            }
        });
    }

    @Test void hisComingBringsAThunderstormAndHisBarFogsTheWorldAndDarkensTheSky(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            level.setWeatherParameters(12_000, 0, false, false);
            Solomon seal = seal(level, 644_000);
            try {
                assertTrue(seal.bossEvent().shouldCreateWorldFog(), "his bar fogs the world, as the Ender Dragon's does");
                assertTrue(seal.bossEvent().shouldDarkenScreen(), "and darkens the sky");
                assertFalse(level.getLevelData().isThundering());
                step(seal);
                assertTrue(level.getLevelData().isRaining() && level.getLevelData().isThundering(), "a thunderstorm as he comes");
                assertEquals(SolomonRules.STORM_HOLD, level.getServer().getWorldData().overworldData().getThunderTime(), "held some while ahead");
                // Cleared by another hand while he is about, it breaks again.
                level.setWeatherParameters(12_000, 0, false, false);
                step(seal);
                assertTrue(level.getLevelData().isThundering(), "held while he is about");
            } finally {
                seal.dragons().forEach(Entity::discard);
                seal.discard();
            }
        });
    }

    @Test void theRiteOfTheEightBooksCallsHimUpAndTakesTheBooks(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            AltarRecipe rite = rite(level);
            assertEquals(AltarForm.OCTAGRAM, rite.form(), "an octagram's rite");
            assertEquals(ResourceKey.create(AltarStructure.REGISTRY, ResourceLocation.parse("corruption:echo_sanctum")), rite.structure().orElseThrow().unwrapKey().orElseThrow());
            assertTrue(rite.consumes(), "the books are taken");
            assertEquals(0, rite.corruption(), "no one need stand at the altar, corrupted or not");
            assertEquals(ModEntities.SOLOMON.get(), rite.result().creature().orElseThrow());
            for (int slot = 0; slot < 8; slot++) assertTrue(rite.pillars().get(slot).test(new ItemStack(BOOKS.get(slot).get())), "book " + (slot + 1) + " on pillar " + (slot + 1));

            AltarOfferingBlockEntity offering = sanctum(level, 620_000);
            lay(offering);
            List<Entity> joined = new ArrayList<>();
            try {
                // Twice as long as the rite takes, and more: he is called up once.
                joined.addAll(perform(offering, rite.time() * 2 + 80));
                assertEquals(1, joined.stream().filter(Solomon.class::isInstance).count(), "he is called up, once");
                for (int slot = 0; slot < 8; slot++) assertTrue(pillar(offering, slot).item().isEmpty(), "book " + (slot + 1) + " taken");
                // Eight books more, and he is called again.
                lay(offering);
                joined.addAll(perform(offering, rite.time() + 60));
                assertEquals(2, joined.stream().filter(Solomon.class::isInstance).count(), "eight books more call him again");
                for (int slot = 0; slot < 8; slot++) assertTrue(pillar(offering, slot).item().isEmpty(), "and are taken");
            } finally {
                joined.forEach(Entity::discard);
            }
        });
    }

    /**
     * A rite that only reads what lies on its pillars ("consumes": false, as a data pack may write one) calls up what it calls
     * once, not again and again while those things lie there; lift one and lay it back, and it may be done again. His own
     * rite, made to read the books rather than take them for the length of this test, stands in for one.
     */
    @Test void aRiteThatOnlyReadsIsNotDoneAgainUntilWhatLiesThereChanges(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var recipes = level.getServer().getRecipeManager();
            List<RecipeHolder<?>> all = new ArrayList<>(recipes.getRecipes());
            ResourceLocation id = ResourceLocation.parse("corruption:altar/solomon");
            AltarRecipe taking = rite(level);
            AltarRecipe reading = new AltarRecipe(taking.shaped(), taking.pillars(), taking.offering(), taking.blocks(), taking.corruption(), taking.result(),
                    taking.time(), taking.keep(), taking.structure(), taking.form(), false);
            List<RecipeHolder<?>> swapped = new ArrayList<>();
            for (RecipeHolder<?> holder : all) swapped.add(holder.id().equals(id) ? new RecipeHolder<>(id, reading) : holder);
            recipes.replaceRecipes(swapped);
            List<Entity> joined = new ArrayList<>();
            try {
                assertFalse(rite(level).consumes(), "read, for now");
                AltarOfferingBlockEntity offering = sanctum(level, 640_000);
                lay(offering);
                joined.addAll(perform(offering, reading.time() * 2 + 80));
                assertEquals(1, joined.stream().filter(Solomon.class::isInstance).count(), "called up once, not again while the books only lie there");
                for (int slot = 0; slot < 8; slot++) assertTrue(pillar(offering, slot).item().is(BOOKS.get(slot).get()), "book " + (slot + 1) + " left where it lay");
                // A book lifted and laid back: the rite may be done again.
                pillar(offering, 0).set(ItemStack.EMPTY);
                joined.addAll(perform(offering, 1));
                pillar(offering, 0).set(new ItemStack(BOOKS.get(0).get()));
                joined.addAll(perform(offering, reading.time() + 60));
                assertEquals(2, joined.stream().filter(Solomon.class::isInstance).count(), "laid anew, the books call him again");
            } finally {
                joined.forEach(Entity::discard);
                recipes.replaceRecipes(all);
            }
            assertTrue(rite(level).consumes(), "his own rite back as it was");
        });
    }
}
