package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.GoeticDemon;

import com.miophas.corruption.api.demon.BossDeath;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.arcane.ArcaneDamage;
import com.miophas.corruption.api.arcane.client.SpellVisuals;
import com.miophas.corruption.common.altar.AltarRecipe;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.AndromaliusRules;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Andromalius on a real server: what he is, his gaze, his volleys, his ward, and the rite that brings him forth.
 * His mind is stepped by hand (customServerAiStep, without his goals moving him about), so each test sets up just
 * what it means to. The test world never brings new entities into sight, so his fireballs are caught as they join.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class AndromaliusServerTest {
    /** A serpent at x, facing south (+Z), as all the tests here place him. */
    private static Andromalius serpent(ServerLevel level, int x) {
        TestWorld.load(level, x - 40, x + 40, 3000 - 16, 3000 + 16);
        Andromalius serpent = ModEntities.ANDROMALIUS.get().create(level);
        serpent.moveTo(x + .5, -60, 3000.5, 0, 0);
        serpent.yBodyRot = 0;
        serpent.yHeadRot = 0;
        level.addFreshEntity(serpent);
        return serpent;
    }
    /** Steps his mind until a volley has begun and ended, or for at most so many ticks; what joined the world meanwhile. */
    private static List<Entity> oneVolley(Andromalius serpent, int most) {
        return catching(() -> {
            boolean begun = false;
            for (int tick = 0; tick < most; tick++) {
                serpent.customServerAiStep();
                if (serpent.attack() == AndromaliusRules.Attack.VOLLEY) begun = true;
                else if (begun) return;
            }
        });
    }
    private static FakePlayer player(ServerLevel level, double x, String name) {
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
        player.moveTo(x, -60, 3000.5, 90, 0);
        player.setGameMode(GameType.SURVIVAL);
        player.removeAllEffects();
        return player;
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

    @Test void heIsABossOfNoGreatStrength(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 52_000);
            assertEquals(AndromaliusRules.MAX_HEALTH, serpent.getAttributeValue(Attributes.MAX_HEALTH));
            assertEquals(AndromaliusRules.ARMOR, serpent.getAttributeValue(Attributes.ARMOR));
            assertTrue(serpent.fireImmune(), "a hell serpent does not burn");
            assertTrue(serpent.isPersistenceRequired(), "and does not wander off into nothing");
            assertFalse(serpent.removeWhenFarAway(1000));
            assertTrue(serpent.bossBar().isPresent(), "he has a boss bar, drawn in the mod's style");
            assertEquals(2, serpent.emblem());
            assertEquals(AndromaliusRules.Attack.NONE, serpent.attack());
            serpent.discard();
        });
    }

    @Test void hisGazeFlashesHisEyeAndLeavesItsMarkSlowedAndBlinded(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 52_100);
            FakePlayer player = player(level, 52_100.5 + 12, "gaze_target");
            serpent.setTarget(player);
            boolean[] reared = {false};
            catching(() -> {
                for (int ticks = 0; ticks < 200 && !player.hasEffect(MobEffects.BLINDNESS); ticks++) {
                    serpent.customServerAiStep();
                    reared[0] |= serpent.attack() == AndromaliusRules.Attack.GAZE;
                }
            });
            assertTrue(reared[0], "he rears before the eye opens");
            assertTrue(player.hasEffect(MobEffects.BLINDNESS), "his quarry is blinded");
            assertEquals(AndromaliusRules.SLOWNESS_LEVEL, player.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier(), "and slowed");
            assertEquals(AndromaliusRules.Attack.NONE, serpent.attack());
            catching(serpent::customServerAiStep);
            assertTrue(serpent.hunting(), "and what he has blinded he hunts at once");
            assertEquals(AndromaliusRules.Intent.HUNT, serpent.intent());

            FakePlayer builder = player(level, 52_100.5 + 12, "gaze_creative");
            builder.setGameMode(GameType.CREATIVE);
            serpent.gaze(builder);
            assertFalse(builder.hasEffect(MobEffects.BLINDNESS), "a player in creative is not his quarry");
            serpent.discard();
        });
    }

    @Test void hisVolleysAreTenWhileHeIsWholeAndTwentyTwoAtTheLast(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 52_200);
            // Twenty-six blocks off: past the gaze's reach, within the volley's.
            FakePlayer player = player(level, 52_200.5 + 26, "volley_target");
            serpent.setTarget(player);
            List<Entity> first = oneVolley(serpent, 200);
            List<Entity> fireballs = first.stream().filter(SmallFireball.class::isInstance).toList();
            assertEquals(AndromaliusRules.volleySize(1), fireballs.size(), "a volley of ten while he is whole");
            for (Entity ball : fireballs) {
                assertSame(serpent, ((SmallFireball) ball).getOwner());
                assertTrue(ball.getDeltaMovement().x > 0, "every one flying at his quarry");
                assertEquals(serpent.mouth().y, ball.getY(), 1e-6, "from his jaws");
            }
            serpent.setHealth(1);
            List<Entity> last = oneVolley(serpent, 200);
            assertEquals(22, last.stream().filter(SmallFireball.class::isInstance).count(), "twenty-two at the last");
            serpent.discard();
        });
    }

    @Test void heBitesWhatIsBeforeHimAndLashesWhatIsBehind(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            // A pig, not a player: a fake player is never ticked, so it never outgrows a new player's first three seconds of safety.
            Andromalius serpent = serpent(level, 52_500);
            net.minecraft.world.entity.animal.Pig prey = EntityType.PIG.create(level);
            prey.moveTo(52_500.5, -60, 3003.5, 180, 0);
            serpent.setTarget(prey);
            float whole = prey.getHealth();
            boolean[] bit = {false};
            catching(() -> {
                for (int tick = 0; tick < 30 && prey.getHealth() >= whole; tick++) {
                    serpent.customServerAiStep();
                    bit[0] |= serpent.attack() == AndromaliusRules.Attack.BITE;
                }
            });
            assertTrue(bit[0], "three blocks before him, he bites");
            assertTrue(prey.getHealth() < whole, "and it hurts");
            assertTrue(prey.hasEffect(MobEffects.POISON), "and poisons");
            assertTrue(serpent.getDeltaMovement().z > .3, "he lunged at it");

            Andromalius second = serpent(level, 52_540);
            net.minecraft.world.entity.animal.Pig behind = EntityType.PIG.create(level);
            behind.moveTo(52_540.5, -60, 2997.5, 0, 0);
            second.setTarget(behind);
            assertTrue(second.inTailArc(behind), "three blocks behind him is where his tail sweeps");
            FakePlayer before = player(level, 52_540.5, "tail_front");
            before.moveTo(52_540.5, -60, 3003.5, 0, 0);
            assertFalse(second.inTailArc(before), "and before him is not");
            boolean[] swept = {false};
            catching(() -> { for (int tick = 0; tick < 4; tick++) { second.customServerAiStep(); swept[0] |= second.attack() == AndromaliusRules.Attack.TAIL; } });
            assertTrue(swept[0], "for what is behind him he swings his tail");
            float health = behind.getHealth();
            second.lash(behind);
            assertTrue(behind.getHealth() < health, "a lash hurts");
            assertTrue(behind.getDeltaMovement().z < -.5 && behind.getDeltaMovement().y > .3, "and throws it off, away from him and up");
            serpent.discard(); second.discard();
        });
    }

    @Test void heDodgesWhatIsThrownAtHimAndIsEnragedBelowHalfHisHealth(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 52_600);
            net.minecraft.world.entity.projectile.Arrow arrow = EntityType.ARROW.create(level);
            arrow.setPos(52_590.5, -58, 3000.5);
            arrow.setDeltaMovement(2, 0, 0);
            serpent.evade(arrow);
            assertEquals(0, serpent.getDeltaMovement().x, 1e-6, "he slithers across its line, not along it");
            assertTrue(Math.abs(serpent.getDeltaMovement().z) > 1, "and hard");

            double speed = serpent.getAttributeValue(Attributes.MOVEMENT_SPEED);
            assertFalse(serpent.enraged());
            FakePlayer player = player(level, 52_600.5 + 12, "rage_target");
            serpent.setTarget(player);
            serpent.setHealth((float) AndromaliusRules.MAX_HEALTH / 2);
            catching(serpent::customServerAiStep);
            assertTrue(serpent.enraged(), "at half his health, he is enraged");
            assertEquals(speed * (1 + AndromaliusRules.ENRAGED_SPEED), serpent.getAttributeValue(Attributes.MOVEMENT_SPEED), 1e-9, "and faster");
            assertEquals(AndromaliusRules.Attack.GAZE, serpent.attack(), "his gaze ready at once");
            serpent.discard();
        });
    }

    /**
     * He is of the corruption's side, as its remnants are: the corruption's own are his allies and never his prey;
     * whatever else lives is his prey, a beast as much as a player, when no player is about.
     */
    @Test void heHuntsWhatIsNotOfTheCorruption(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 47_000);
            net.minecraft.world.entity.animal.Pig pig = EntityType.PIG.create(level);
            pig.moveTo(serpent.getX() + 8, serpent.getY(), serpent.getZ(), 0, 0);
            pig.setNoAi(true);
            level.addFreshEntity(pig);
            CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            remnant.moveTo(serpent.getX() + 4, serpent.getY(), serpent.getZ(), 0, 0);
            remnant.setNoAi(true);
            level.addFreshEntity(remnant);
            try {
                assertTrue(com.miophas.corruption.api.corruption.CorruptionFactionApi.isRemnant(serpent), "of the corruption's side");
                assertTrue(serpent.isAlliedTo(remnant), "its own are his allies");
                assertFalse(serpent.canAttack(remnant), "and never his prey");
                // His mind picks a target as it would, taking its chances as it does.
                for (int tries = 0; tries < 400 && serpent.getTarget() == null; tries++)
                    for (var goal : serpent.targetSelector.getAvailableGoals())
                        if (goal.getGoal() instanceof net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<?> hunting && hunting.canUse()) hunting.start();
                assertSame(pig, serpent.getTarget(), "a beast, with no player about; not the remnant nearer to him");
            } finally {
                pig.discard();
                remnant.discard();
                serpent.discard();
            }
        });
    }

    /** The corruption's ground hurts what treads on it, but not one of its own, as he now is. */
    @Test void theCorruptionsGroundSparesHim(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 54_400);
            net.minecraft.world.entity.animal.Pig pig = EntityType.PIG.create(level);
            pig.moveTo(serpent.getX() + 6, serpent.getY(), serpent.getZ(), 0, 0);
            level.addFreshEntity(pig);
            BlockPos under = serpent.blockPosition().below(), underPig = pig.blockPosition().below();
            var before = level.getBlockState(under);
            try {
                var ground = ModBlocks.CORRUPTION.get().defaultBlockState();
                level.setBlock(under, ground, 3);
                level.setBlock(underPig, ground, 3);
                serpent.setOnGround(true);
                pig.setOnGround(true);
                ground.getBlock().stepOn(level, under, ground, serpent);
                ground.getBlock().stepOn(level, underPig, ground, pig);
                assertEquals(serpent.getMaxHealth(), serpent.getHealth(), 1e-4, "of the corruption, he walks its ground unhurt");
                assertTrue(pig.getHealth() < pig.getMaxHealth(), "what is not of it is hurt");
            } finally {
                level.setBlock(under, before, 3);
                level.setBlock(underPig, before, 3);
                pig.discard();
                serpent.discard();
            }
        });
    }

    /**
     * His charge, run by hand: it bores a tunnel his own size (three blocks across, four high) through a wall of stone in
     * its way, leaving the rest of the wall standing, and stops dead at a wall of obsidian beyond it.
     */
    @Test void hisChargeSmashesThroughWhatIsSofterThanObsidianAndStopsAtObsidian(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 55_200);
            net.minecraft.world.entity.animal.Pig pig = EntityType.PIG.create(level);
            pig.moveTo(serpent.getX(), -60, serpent.getZ() + 20, 0, 0);
            pig.setNoAi(true);
            level.addFreshEntity(pig);
            int x = net.minecraft.util.Mth.floor(serpent.getX()), z = net.minecraft.util.Mth.floor(serpent.getZ());
            java.util.List<net.minecraft.core.BlockPos> stone = new ArrayList<>(), tunnel = new ArrayList<>(), obsidian = new ArrayList<>();
            for (int across = -2; across <= 2; across++) for (int up = 0; up < 5; up++) {
                stone.add(new net.minecraft.core.BlockPos(x + across, -60 + up, z + 5));
                if (Math.abs(across) <= 1 && up <= 3) tunnel.add(new net.minecraft.core.BlockPos(x + across, -60 + up, z + 5));
                obsidian.add(new net.minecraft.core.BlockPos(x + across, -60 + up, z + 11));
            }
            try {
                stone.forEach(at -> level.setBlock(at, Blocks.STONE.defaultBlockState(), 3));
                obsidian.forEach(at -> level.setBlock(at, Blocks.OBSIDIAN.defaultBlockState(), 3));
                var charging = serpent.charging();
                assertTrue(charging.usable(serpent, pig), "twenty blocks off: in its reach");
                charging.begin(serpent, pig);
                for (int tick = 0; tick < charging.timing().ticks(); tick++) {
                    charging.tick(serpent, pig, tick);
                    serpent.move(MoverType.SELF, serpent.getDeltaMovement());
                }
                charging.end(serpent, false);
                assertTrue(tunnel.stream().allMatch(at -> level.getBlockState(at).isAir()), "the stone in his way gave way");
                assertTrue(stone.stream().filter(at -> !tunnel.contains(at)).allMatch(at -> level.getBlockState(at).is(Blocks.STONE)), "the rest of the wall stands");
                assertTrue(obsidian.stream().allMatch(at -> level.getBlockState(at).is(Blocks.OBSIDIAN)), "the obsidian did not");
                assertTrue(charging.stopped(), "and stopped him dead");
                assertTrue(serpent.getZ() > z + 5 && serpent.getZ() < z + 11, "past the stone, short of the obsidian: " + serpent.getZ());
            } finally {
                stone.forEach(at -> level.setBlock(at, Blocks.AIR.defaultBlockState(), 3));
                obsidian.forEach(at -> level.setBlock(at, Blocks.AIR.defaultBlockState(), 3));
                pig.discard();
                serpent.discard();
            }
        });
    }

    @Test void hisChargeRamsWhatItMeets(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 55_600);
            net.minecraft.world.entity.animal.Pig pig = EntityType.PIG.create(level);
            pig.moveTo(serpent.getX(), -60, serpent.getZ() + 10, 0, 0);
            pig.setNoAi(true);
            level.addFreshEntity(pig);
            try {
                var charging = serpent.charging();
                charging.begin(serpent, pig);
                for (int tick = 0; tick < charging.timing().ticks(); tick++) {
                    charging.tick(serpent, pig, tick);
                    serpent.move(MoverType.SELF, serpent.getDeltaMovement());
                }
                assertTrue(pig.getHealth() < pig.getMaxHealth() || !pig.isAlive(), "rammed");
                assertTrue(pig.getDeltaMovement().y > 0 || pig.getZ() > serpent.getZ() - 1, "and thrown off along the charge");
            } finally {
                pig.discard();
                serpent.discard();
            }
        });
    }

    /** His body is made of hitboxes that follow him as he turns: blows land on those, and the arcane through them reaches him. */
    @Test void hisBodyIsMadeOfPartsThatFollowHim(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 56_000);
            FakePlayer player = player(level, 56_000.5 + 6, "andromalius_parts");
            try {
                serpent.tick();
                var parts = serpent.getParts();
                assertEquals(AndromaliusRules.BODY.size(), parts.length);
                assertTrue(serpent.isMultipartEntity());
                assertFalse(serpent.isPickable(), "his own box is not there to be struck at");
                GoeticDemon.Part head = null, tip = null;
                for (var part : parts) {
                    GoeticDemon.Part body = (GoeticDemon.Part) part;
                    assertTrue(body.isPickable());
                    if (body.definition().name().equals("head")) head = body;
                    if (body.definition().name().equals("tail_tip")) tip = body;
                }
                assertNotNull(head);
                assertTrue(head.getZ() - serpent.getZ() > 1 && head.getY() - serpent.getY() > 2.5, "his head reared up ahead of him (he faces south)");
                assertTrue(tip.getZ() - serpent.getZ() < -6, "the tip of his tail lies far behind him");
                serpent.setYRot(90);
                serpent.yBodyRot = 90;
                serpent.tick();
                assertTrue(head.getX() - serpent.getX() < -1, "turned west, his head follows");
                float health = serpent.getHealth();
                assertFalse(head.hurt(level.damageSources().playerAttack(player), 10), "a blade on his head passes through him as through the rest");
                assertTrue(head.hurt(ArcaneDamage.source(level, player), 10), "the arcane does not");
                assertTrue(serpent.getHealth() < health, "and it is his hurt");
                assertTrue(serpent.hurt(level.damageSources().genericKill(), Float.MAX_VALUE));
                assertFalse(head.isPickable(), "dying, he is nothing to strike at");
            } finally {
                serpent.discard();
            }
        });
    }

    @Test void heDiesInASpectacleAndIsGoneAtTheEndOfIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 53_600);
            try {
                assertTrue(serpent.hurt(level.damageSources().genericKill(), Float.MAX_VALUE), "commands still work on him");
                assertTrue(serpent.isDeadOrDying());
                BossDeath death = serpent.death();
                assertEquals(AndromaliusRules.DEATH_TICKS, death.ticks(), "his own death, as long as his rules say");
                assertEquals(serpent.getY() + serpent.getBbHeight() * .6, serpent.heart(1).y, 1e-6, "his light pours out of him a little over halfway up");
                for (int tick = 0; tick < death.ticks(); tick++) serpent.tick();
                assertFalse(serpent.isRemoved(), "his body is gone in his last burst, but he lingers while his death plays out");
                assertTrue(serpent.getBoundingBoxForCulling().getXsize() > 20, "and he is drawn wherever any of it could be seen");
                for (int tick = 0; tick < death.linger() - 1; tick++) serpent.tick();
                assertFalse(serpent.isRemoved());
                serpent.tick();
                assertTrue(serpent.isRemoved(), "and then he is gone");
            } finally {
                serpent.discard();
            }
        });
    }

    /**
     * Every boss's death, and the ordinary one a demon of an add-on's dies: each moment of it is an effect the mod draws,
     * sounds as the game can, and has played out by the time the boss is taken away; its rays come and its body goes.
     */
    @Test void everyDeathIsMadeOfWhatTheGameHas(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            for (BossDeath death : List.of(Baal.DEATH, Agares.DEATH, Andromalius.DEATH, BossDeath.ordinary(4, 0xC6728C, 0xFFD24A))) {
                assertTrue(death.rays().count() > 0 && death.rays().length() > 0, "light pours out of it");
                assertFalse(death.beats().isEmpty());
                for (BossDeath.Beat beat : death.beats()) {
                    String what = beat.visual().effect() + " at " + beat.tick();
                    assertTrue(SpellVisuals.BUILT_IN.contains(ResourceLocation.parse(beat.visual().effect())), what + " is one the mod draws");
                    assertTrue(beat.visual().sound().isEmpty() || BuiltInRegistries.SOUND_EVENT.containsKey(ResourceLocation.parse(beat.visual().sound())),
                            what + " sounds as the game can: " + beat.visual().sound());
                    assertTrue(beat.tick() >= 0 && beat.tick() + beat.visual().ticks() <= death.lasts(), what + " has played out before it is taken away");
                }
                assertTrue(death.beats().stream().anyMatch(beat -> beat.visual().effect().equals("corruption:nova") && Math.abs(beat.tick() - death.ticks()) <= 4),
                        "its body is gone in a burst of its light");
            }
        });
    }

    @Test void onlyTheArcaneHurtsADemon(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 52_260);
            FakePlayer player = player(level, 52_260.5 + 14, "demon_target");
            assertInstanceOf(GoeticDemon.class, serpent, "he is a demon of the Goetia");
            assertTrue(GoeticDemon.accepts(ArcaneDamage.source(level, player)), "a spell's own hurt reaches him");
            assertTrue(GoeticDemon.accepts(ArcaneDamage.source(level, player, "corruption:corruption")), "and a spell that hurts with the corruption");
            assertTrue(GoeticDemon.accepts(level.damageSources().genericKill()), "and /kill");
            assertTrue(GoeticDemon.accepts(level.damageSources().fellOutOfWorld()), "and the void");
            for (var blow : List.of(level.damageSources().playerAttack(player), level.damageSources().arrow(null, player), level.damageSources().magic(),
                    level.damageSources().indirectMagic(player, player), level.damageSources().onFire(), level.damageSources().wither(),
                    level.damageSources().lightningBolt(), level.damageSources().explosion(null, player), level.damageSources().mobAttack(player)))
                assertFalse(GoeticDemon.accepts(blow), blow.getMsgId() + " passes through him");

            float whole = serpent.getHealth();
            assertFalse(serpent.hurt(level.damageSources().playerAttack(player), 10), "a sword passes through");
            assertFalse(serpent.hurt(level.damageSources().magic(), 10), "and a potion's harm");
            assertEquals(whole, serpent.getHealth());
            assertTrue(serpent.hurt(ArcaneDamage.source(level, player), 10), "a spell does not");
            float unwarded = whole - serpent.getHealth();
            assertTrue(unwarded > 0);

            // Through his ward, the arcane hurts him by half (his armour taking its share either way).
            serpent.invulnerableTime = 0;
            assertTrue(serpent.raiseShield());
            float before = serpent.getHealth();
            assertTrue(serpent.hurt(ArcaneDamage.source(level, player), 10));
            float warded = before - serpent.getHealth();
            assertTrue(warded > 0 && warded < unwarded * .7F, "softened by his ward: " + warded + " of " + unwarded);
            serpent.discard();
        });
    }

    @Test void hisWardStandsItsTimeAndRisesWhenHeIsRushed(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Andromalius serpent = serpent(level, 52_300);
            assertTrue(serpent.raiseShield());
            assertTrue(serpent.shielded());
            catching(() -> { for (int tick = 0; tick < AndromaliusRules.SHIELD_TICKS; tick++) serpent.customServerAiStep(); });
            assertFalse(serpent.shielded(), "it stands four seconds");
            assertFalse(serpent.raiseShield(), "and needs time to come back");

            // Rushed: his quarry sprints at him from fourteen blocks off.
            Andromalius second = serpent(level, 52_340);
            FakePlayer rusher = player(level, 52_340.5 + 14, "ward_rusher");
            second.setTarget(rusher);
            for (int tick = 0; tick < 10 && !second.shielded(); tick++) {
                rusher.moveTo(rusher.getX() - .3, rusher.getY(), rusher.getZ());
                second.customServerAiStep();
            }
            assertTrue(second.shielded(), "a rush raises the ward");
            Andromalius third = serpent(level, 52_380);
            FakePlayer walker = player(level, 52_380.5 + 14, "ward_walker");
            third.setTarget(walker);
            catching(() -> {
                for (int tick = 0; tick < 20; tick++) {
                    walker.moveTo(walker.getX() - .2, walker.getY(), walker.getZ());
                    third.customServerAiStep();
                }
            });
            assertFalse(third.shielded(), "a walk does not");
            serpent.discard(); second.discard(); third.discard();
        });
    }

    @Test void heIsBroughtForthAtTheAltarForAVillagerAmongWitherSkulls(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var rite = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", "altar/andromalius")).orElse(null);
            assertNotNull(rite, "the rite loads");
            AltarRecipe recipe = (AltarRecipe) rite.value();
            assertEquals(EntityType.VILLAGER, recipe.offering().orElseThrow(), "a villager is offered");
            assertEquals(ModEntities.ANDROMALIUS.get(), recipe.result().creature().orElseThrow(), "and he comes");
            assertEquals(4, recipe.blocks().size());
            for (var block : recipe.blocks()) {
                assertTrue(block.test(net.minecraft.world.level.block.Blocks.WITHER_SKELETON_SKULL.defaultBlockState()), "wither skulls on the ground");
                assertTrue(block.test(net.minecraft.world.level.block.Blocks.WITHER_SKELETON_WALL_SKULL.defaultBlockState()), "on the floor or a wall alike");
                assertFalse(block.test(net.minecraft.world.level.block.Blocks.SKELETON_SKULL.defaultBlockState()), "a skeleton's will not do");
            }
            assertEquals(6, recipe.pillars().stream().filter(pillar -> !pillar.isEmpty()).count(), "and all six pillars laid");
            assertEquals(3, recipe.corruption(), "and a player at the altar corrupted at least so far");
            var parchment = new net.minecraft.world.item.ItemStack(com.miophas.corruption.common.registry.ModItems.PARCHMENT.get());
            var pillar = recipe.pillars().get(0);
            assertTrue(pillar.test(ArcaneApi.inscribed(parchment, ArcaneApi.key("corruption:andromalius"))), "the first pillar bears his own string");
            assertFalse(pillar.test(parchment), "not a blank parchment");
            assertFalse(pillar.test(ArcaneApi.inscribed(parchment, ArcaneApi.key("corruption:baal"))), "nor another spirit's");
            assertTrue(recipe.pillars().get(3).test(new net.minecraft.world.item.ItemStack(com.miophas.corruption.common.registry.ModItems.CORRUPTED_HEARTSTONE.get())));
            LootTable loot = server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("corruption", "entities/andromalius")));
            assertNotSame(LootTable.EMPTY, loot, "he leaves something behind");
            assertEquals(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("corruption", "entities/andromalius")),
                    ModEntities.ANDROMALIUS.get().getDefaultLootTable());
        });
    }
}
