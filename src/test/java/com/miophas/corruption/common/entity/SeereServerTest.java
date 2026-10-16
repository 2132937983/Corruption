package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneDamage;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.altar.AltarRecipe;
import com.miophas.corruption.common.entity.skill.PhaseDashSkill;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.demon.SeereRules;
import com.miophas.corruption.core.demon.SeereRules.Mode;
import com.miophas.corruption.core.demon.SeereRules.Skill;
import com.miophas.corruption.core.demon.skill.PhaseDashRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Seere on a real server: what he is, his going from one way to another (each shift begun by hand and lived through on
 * his own ticks), each of his blows begun by hand and run through his own set of them (so that each test sets up just
 * what it means to), the rite that brings him forth, and his death. The test world never brings new entities into
 * sight, so what his blows bring forth is caught as it joins.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class SeereServerTest {
    /** Seere at x, standing on the flat ground, facing south (+Z). */
    private static Seere seere(ServerLevel level, int x) {
        TestWorld.load(level, x - 40, x + 40, 3000 - 40, 3000 + 40);
        Seere horse = ModEntities.SEERE.get().create(level);
        horse.moveTo(x + .5, -60, 3000.5, 0, 0);
        horse.yBodyRot = 0;
        horse.yHeadRot = 0;
        level.addFreshEntity(horse);
        return horse;
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
    /** A quarry that outlasts a blow or two, and stands still for them. */
    private static IronGolem golem(ServerLevel level, double x, double z) {
        IronGolem golem = EntityType.IRON_GOLEM.create(level);
        golem.moveTo(x, -60, z, 0, 0);
        golem.setNoAi(true);
        level.addFreshEntity(golem);
        return golem;
    }
    /** A player's stand-in in armour, standing still: a villager has a player's health, and armour is reckoned alike for both. */
    private static Villager armoured(ServerLevel level, double x, double z, Item... armour) {
        Villager villager = EntityType.VILLAGER.create(level);
        villager.moveTo(x, -60, z, 0, 0);
        villager.setNoAi(true);
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int index = 0; index < armour.length; index++) villager.setItemSlot(slots[index], new ItemStack(armour[index]));
        level.addFreshEntity(villager);
        villager.tick();
        return villager;
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
    /**
     * Runs one of his blows on this quarry, from its beginning to its end, by hand: begun as his choosing would, run on
     * through his set of them, and him moved each tick as he moves himself.
     */
    private static void run(Seere horse, Skill blow, LivingEntity target) { run(horse, blow, target, () -> {}); }

    /** As {@link #run(Seere, Skill, LivingEntity)}, doing something more every tick (letting the quarry live its own ticks, say). */
    private static void run(Seere horse, Skill blow, LivingEntity target, Runnable everyTick) {
        horse.skills().comeRound(SeereRules.BLOWS.indexOf(blow));
        assertTrue(horse.begin(blow, target), blow + " begins");
        assertEquals(blow, horse.skill());
        for (int tick = 0; tick < 400 && horse.skills().busy(); tick++) {
            horse.skills().tick(horse, target, cooldown -> cooldown);
            horse.travel(Vec3.ZERO);
            everyTick.run();
        }
        assertFalse(horse.skills().busy(), blow + " runs its course");
    }
    /** Lives through ticks of his own until he goes this way, no more than so many; how many it took. */
    private static int until(Seere horse, Mode mode, int most) {
        int ticks = 0;
        while (horse.mode() != mode && ticks++ < most) horse.tick();
        assertEquals(mode, horse.mode(), "he goes " + mode + " within " + most + " ticks");
        return ticks;
    }
    private static long count(List<Entity> joined, String effect) {
        return joined.stream().filter(entity -> entity instanceof SpellVisualEntity shown && ResourceLocation.parse("corruption:" + effect).equals(shown.effect())).count();
    }
    private static boolean hurt(LivingEntity creature) { return !creature.isAlive() || creature.getHealth() < creature.getMaxHealth(); }

    @Test void heIsAWingedHorseOfTheCorruptionFasterThanAnyAndRidesNothing(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 120_000);
            Boat boat = EntityType.BOAT.create(level);
            try {
                assertInstanceOf(GoeticDemon.class, horse);
                assertTrue(CorruptionFactionApi.isRemnant(horse), "of the corruption's side");
                assertEquals(Mode.GROUND, horse.mode(), "he comes forth on the ground");
                assertFalse(horse.isNoGravity());
                assertEquals(SeereRules.MAX_HEALTH, horse.getMaxHealth());
                assertEquals(4, horse.emblem());
                assertTrue(horse.getAttributeValue(Attributes.MOVEMENT_SPEED) > .3375, "a horse's is .3375 at the most");
                assertEquals(Seere.BODY.size(), horse.getParts().length, "his body is his hitboxes");
                boat.moveTo(horse.position());
                level.addFreshEntity(boat);
                assertFalse(horse.startRiding(boat, true), "a boat would only hold him fast");
                assertFalse(horse.hurt(level.damageSources().generic(), 10), "only the arcane hurts him");
            } finally {
                boat.discard();
                horse.discard();
            }
        });
    }

    @Test void heTakesToTheSkyStaysThereAndComesDownAgain(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 120_400);
            try {
                double ground = horse.getY();
                horse.beginShift(Skill.TAKE_OFF, null);
                assertEquals(Skill.TAKE_OFF, horse.skill());
                until(horse, Mode.SKY, SeereRules.TAKE_OFF_TICKS + 2);
                assertTrue(horse.isNoGravity(), "held up by his wings");
                assertEquals(Skill.NONE, horse.skill(), "done taking off");
                assertTrue(horse.getY() > ground + 5, "well off the ground: " + (horse.getY() - ground));
                for (int tick = 0; tick < 40; tick++) horse.tick();
                assertTrue(horse.getY() > ground + 5, "and he stays up: " + (horse.getY() - ground));
                horse.beginShift(Skill.LAND, null);
                until(horse, Mode.GROUND, SeereRules.LAND_TICKS * 3 + 2);
                assertFalse(horse.isNoGravity(), "down again, and walking as anything does");
                for (int tick = 0; tick < 30; tick++) horse.tick();
                assertEquals(ground, horse.getY(), .01, "on the ground");
            } finally {
                horse.discard();
            }
        });
    }

    @Test void heGoesUnderTheEarthOutOfReachAndRisesBesideHisQuarryThrowingItUp(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 120_800);
            IronGolem golem = golem(level, horse.getX(), horse.getZ() + 20);
            FakePlayer player = player(level, horse.getX() - 30, horse.getZ(), "seere_digger");
            try {
                horse.setTarget(golem);
                horse.beginShift(Skill.BURROW, golem);
                until(horse, Mode.UNDERGROUND, SeereRules.BURROW_TICKS + 1);
                assertTrue(horse.getY() + horse.getBbHeight() < -60, "all of him under the ground");
                assertFalse(horse.hurt(ArcaneDamage.source(level, player), 20), "nothing reaches him there");
                for (PartEntity<?> part : horse.getParts()) assertFalse(part.isPickable(), "nor can be struck at");
                List<Entity> joined = catching(() -> {
                    for (int tick = 0; tick < 200 && horse.skill() != Skill.EMERGE; tick++) horse.tick();
                });
                assertEquals(Skill.EMERGE, horse.skill(), "he rises");
                assertTrue(Math.hypot(horse.getX() - golem.getX(), horse.getZ() - golem.getZ()) <= SeereRules.RISE_NEAR + .5,
                        "beside his quarry: " + horse.position() + " " + golem.position() + " " + horse.getTarget());
                assertTrue(hurt(golem), "hurting it as he comes");
                assertTrue(golem.getDeltaMovement().y > 0, "and throwing it up");
                assertTrue(joined.stream().anyMatch(entity -> entity instanceof FallingBlockEntity), "and the ground about him");
                assertTrue(count(joined, "fissure") >= 1, "the ground bursting");
                for (PartEntity<?> part : horse.getParts()) assertTrue(part.isPickable(), "rising, he can be struck at again");
                until(horse, Mode.GROUND, SeereRules.EMERGE_TICKS + 2);
                assertTrue(horse.getY() > -63, "up out of the earth: " + horse.getY());
                assertTrue(horse.hurt(ArcaneDamage.source(level, player), 5), "and hurt again");
            } finally {
                golem.discard();
                horse.discard();
            }
        });
    }

    @Test void hisHoovesStrikeWhatIsCloseBeforeHimAndNothingBehind(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 121_200);
            Pig before = pig(level, horse.getX(), horse.getZ() + 3), behind = pig(level, horse.getX(), horse.getZ() - 3.5);
            try {
                run(horse, Skill.HOOVES, before);
                assertTrue(hurt(before), "struck");
                assertEquals(behind.getMaxHealth(), behind.getHealth(), "nothing behind him");
            } finally {
                before.discard();
                behind.discard();
                horse.discard();
            }
        });
    }

    @Test void heBlinksBackAndForthThroughHisQuarryCursingEveryLineHeBlinksAlong(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 121_600);
            IronGolem quarry = golem(level, horse.getX(), horse.getZ() + 10), between = golem(level, horse.getX() + .5, horse.getZ() + 5),
                    aside = golem(level, horse.getX() + 14, horse.getZ() + 5);
            try {
                Vec3 from = horse.position();
                List<Entity> joined = catching(() -> run(horse, Skill.BLINKS, quarry));
                PhaseDashSkill blinks = (PhaseDashSkill) horse.skills().skill(SeereRules.BLOWS.indexOf(Skill.BLINKS));
                assertEquals(SeereRules.BLINKS.hops(), blinks.made(), "every blink made");
                assertEquals(SeereRules.BLINKS.hops(), count(joined, "rift"), "a rift where each came out");
                assertTrue(horse.position().distanceTo(from) > 3, "he is gone from where he stood");
                assertEquals(SeereRules.BLINKS.pass(), Math.hypot(horse.getX() - quarry.getX(), horse.getZ() - quarry.getZ()), .6, "ending a pass from his quarry");
                assertTrue(hurt(quarry) && quarry.hasEffect(ModEffects.CORRUPTION_CURSE), "his quarry hurt and cursed");
                assertEquals(quarry.getMaxHealth() - SeereRules.BLINKS.damage() * Math.min(SeereRules.BLINKS.hops(), SeereRules.BLINKS.hitsEach()), quarry.getHealth(), 1e-3,
                        "by every blink, as every blink passed through it");
                assertEquals(SeereRules.BLINKS.curseLevel(), quarry.getEffect(ModEffects.CORRUPTION_CURSE).getAmplifier());
                assertTrue(hurt(between) && between.hasEffect(ModEffects.CORRUPTION_CURSE), "and what stood on the line he blinked along");
                assertEquals(aside.getMaxHealth(), aside.getHealth(), "but nothing off it");
                assertFalse(aside.hasEffect(ModEffects.CORRUPTION_CURSE));
            } finally {
                quarry.discard();
                between.discard();
                aside.discard();
                horse.discard();
            }
        });
    }

    /**
     * A Warden blinked through, and left to live out the curse his blinks laid on it, still stands with two thirds of
     * its health and more: his blinks once laid a strong curse that bit again at every blink, and the curse takes a
     * share of a creature's whole health each time it bites, so it killed even a Warden outright.
     */
    @Test void aWardenOutlastsAWholeRunOfHisBlinks(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 123_600);
            Warden warden = EntityType.WARDEN.create(level);
            try {
                warden.moveTo(horse.getX(), -60, horse.getZ() + 10, 0, 0);
                warden.setNoAi(true);
                level.addFreshEntity(warden);
                catching(() -> run(horse, Skill.BLINKS, warden, () -> { if (warden.isAlive()) warden.tick(); }));
                for (int tick = 0; tick < 200 && warden.isAlive() && warden.hasEffect(ModEffects.CORRUPTION_CURSE); tick++) warden.tick();
                assertTrue(warden.isAlive(), "it lives");
                assertTrue(warden.getHealth() > warden.getMaxHealth() * 2 / 3, "with most of its health: " + warden.getHealth() + " of " + warden.getMaxHealth());
                assertFalse(warden.hasEffect(ModEffects.CORRUPTION_CURSE), "the curse has run its course");
            } finally {
                warden.discard();
                horse.discard();
            }
        });
    }

    /**
     * What a whole run of his blinks takes from a player in full diamond armour, curse and all: most of his health, but
     * not all of it. The corruption's hurt is turned aside by armour as any hurt is; his first blinks killed such a
     * player outright, and their first nerf left him a scratch (2 of 20).
     */
    @Test void aRunOfHisBlinksTakesMostOfAnArmouredPlayersHealthButLeavesHimStanding(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 124_000);
            Villager player = armoured(level, horse.getX(), horse.getZ() + 10, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);
            try {
                assertEquals(20, player.getMaxHealth(), "a player's health");
                assertEquals(20, player.getArmorValue(), "in full diamond");
                catching(() -> run(horse, Skill.BLINKS, player, () -> { if (player.isAlive()) player.tick(); }));
                for (int tick = 0; tick < 200 && player.isAlive() && player.hasEffect(ModEffects.CORRUPTION_CURSE); tick++) player.tick();
                assertTrue(player.isAlive(), "he lives");
                assertTrue(player.getHealth() < player.getMaxHealth() / 2, "with less than half his health: " + player.getHealth());
            } finally {
                player.discard();
                horse.discard();
            }
        });
    }

    /** A run of blinks held to hurting a creature once (as an add-on's boss may hold its own) hurts it once, though every blink passes through it. */
    @Test void aRunHeldToOneHurtACreatureHurtsItOnce(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 124_400);
            IronGolem quarry = golem(level, horse.getX(), horse.getZ() + 10);
            PhaseDashRules once = new PhaseDashRules(new SkillTiming(10, 0, 160), 3, 18, 4, 5, 5, 35, 1.6, 12, 30, 0, 1);
            PhaseDashSkill blinks = new PhaseDashSkill(once, 0, 0);
            try {
                catching(() -> {
                    blinks.begin(horse, quarry);
                    for (int elapsed = 0; elapsed < once.timing().ticks(); elapsed++) blinks.tick(horse, quarry, elapsed);
                });
                assertEquals(once.hops(), blinks.made(), "every blink made");
                assertEquals(quarry.getMaxHealth() - once.damage(), quarry.getHealth(), 1e-3, "but it hurt once");
            } finally {
                quarry.discard();
                horse.discard();
            }
        });
    }

    @Test void hisSpikesHurtAndSlowOnlyWhatStandsInTheFieldBeforeHim(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 122_000);
            IronGolem before = golem(level, horse.getX(), horse.getZ() + 8), behind = golem(level, horse.getX(), horse.getZ() - 6),
                    aside = golem(level, horse.getX() + 7, horse.getZ() + 2);
            try {
                List<Entity> joined = catching(() -> run(horse, Skill.SPIKES, before));
                int clusters = SeereRules.SPIKES.clusters().size();
                assertEquals(clusters, count(joined, "magic_circle"), "every place marked before");
                assertEquals(clusters, count(joined, "spikes"), "and the spikes come up at every one");
                assertTrue(hurt(before), "what stood in the field is hurt");
                assertEquals(before.getMaxHealth() - SeereRules.SPIKES.damage(), before.getHealth(), 1e-3, "for the whole of it, having no armour");
                assertEquals(SeereRules.SPIKES.slownessLevel(), before.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier(), "and slowed");
                for (IronGolem spared : List.of(behind, aside)) {
                    assertEquals(spared.getMaxHealth(), spared.getHealth(), "nothing outside it");
                    assertFalse(spared.hasEffect(MobEffects.MOVEMENT_SLOWDOWN));
                }
            } finally {
                before.discard();
                behind.discard();
                aside.discard();
                horse.discard();
            }
        });
    }

    @Test void hisDiveTearsUpTheGroundWhereHeStrikesButNotBedrockAndHeLandsThere(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 122_400);
            IronGolem quarry = golem(level, horse.getX() + 6, horse.getZ() + 10);
            BlockPos under = BlockPos.containing(quarry.getX(), quarry.getY() - 1, quarry.getZ());
            try {
                level.setBlockAndUpdate(under, Blocks.BEDROCK.defaultBlockState());
                level.setBlockAndUpdate(under.east(), Blocks.OBSIDIAN.defaultBlockState());
                horse.beginShift(Skill.TAKE_OFF, null);
                until(horse, Mode.SKY, SeereRules.TAKE_OFF_TICKS + 2);
                horse.moveTo(horse.getX(), -48, horse.getZ());
                List<Entity> joined = catching(() -> run(horse, Skill.DIVE, quarry));
                assertEquals(1, count(joined, "magic_circle"), "where he dives marked on the ground");
                assertTrue(horse.dive().struck(), "he struck");
                assertTrue(horse.position().distanceTo(quarry.position()) < 4, "where his quarry stood");
                assertTrue(hurt(quarry), "hurting it");
                assertTrue(horse.dive().torn() > 0, "tearing up the ground");
                assertTrue(level.getBlockState(under).is(Blocks.BEDROCK), "but never bedrock");
                assertTrue(level.getBlockState(under.east()).isAir(), "while obsidian goes with the rest");
                assertTrue(joined.stream().anyMatch(entity -> entity instanceof FallingBlockEntity falling && falling.getBlockState().is(Blocks.OBSIDIAN)),
                        "flung away as by a blast");
                assertTrue(joined.stream().filter(entity -> entity instanceof FallingBlockEntity).allMatch(entity -> entity.getDeltaMovement().y > 0), "every one of them up");
                horse.customServerAiStep();
                assertEquals(Mode.GROUND, horse.mode(), "his dive over, he is on the ground");
            } finally {
                quarry.discard();
                horse.discard();
            }
        });
    }

    @Test void heFiresMoreBeamsTheLowerHisHealthEachOutOfACircleOfItsOwn(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 122_800);
            IronGolem quarry = golem(level, horse.getX(), horse.getZ() + 14);
            try {
                List<Entity> joined = catching(() -> run(horse, Skill.BEAMS, quarry));
                assertEquals(1, joined.stream().filter(entity -> entity instanceof DemonBeamEntity).count(), "one beam at full health");
                assertEquals(1, count(joined, "sigil"));
                assertTrue(hurt(quarry), "burning what it catches");
                horse.setHealth(horse.getMaxHealth() * .3F);
                joined = catching(() -> run(horse, Skill.BEAMS, quarry));
                assertEquals(3, joined.stream().filter(entity -> entity instanceof DemonBeamEntity).count(), "three with most of his health gone");
                assertEquals(3, count(joined, "sigil"), "each out of a circle of its own");
            } finally {
                quarry.discard();
                horse.discard();
            }
        });
    }

    @Test void theRiteAsksForAHorseGivenUpAmongBalesOfHay(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            var recipe = level.getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("corruption", "altar/seere")).orElseThrow().value();
            AltarRecipe rite = assertInstanceOf(AltarRecipe.class, recipe);
            assertEquals(EntityType.HORSE, rite.offering().orElseThrow(), "a horse given up");
            assertEquals(4, rite.blocks().size(), "four bales of hay round it");
            for (var block : rite.blocks()) assertTrue(block.test(Blocks.HAY_BLOCK.defaultBlockState()));
            for (var item : List.of(Items.SADDLE, Items.FEATHER))
                assertTrue(rite.pillars().stream().anyMatch(pillar -> pillar.test(new ItemStack(item))), item + " on a pillar");
            assertEquals(ModEntities.SEERE.get(), rite.result().creature().orElseThrow(), "and Seere comes forth");
            assertTrue(rite.corruption() > 3, "asked of one far gone");
        });
    }

    @Test void underTheEarthHeComesUpToDieAndIsGoneAtTheEndOfHisDeath(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Seere horse = seere(level, 123_200);
            try {
                horse.beginShift(Skill.BURROW, null);
                until(horse, Mode.UNDERGROUND, SeereRules.BURROW_TICKS + 1);
                assertTrue(horse.hurt(level.damageSources().genericKill(), Float.MAX_VALUE), "what passes every defence reaches him even there");
                assertEquals(Mode.GROUND, horse.mode(), "he comes up to die");
                assertFalse(horse.isNoGravity());
                assertTrue(horse.getY() > -61, "on the ground: " + horse.getY());
                BossDeath death = horse.death();
                for (int tick = 0; tick < death.ticks(); tick++) horse.tick();
                assertFalse(horse.isRemoved(), "his body gone in his light, he lingers while his death plays out");
                for (int tick = 0; tick < death.linger(); tick++) horse.tick();
                assertTrue(horse.isRemoved(), "and then he is gone");
            } finally {
                horse.discard();
            }
        });
    }
}
