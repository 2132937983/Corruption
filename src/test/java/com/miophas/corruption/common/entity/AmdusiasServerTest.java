package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.GoeticDemon;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneApi;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.altar.AltarOfferingBlockEntity;
import com.miophas.corruption.common.altar.AltarPillarBlockEntity;
import com.miophas.corruption.common.block.AltarOfferingBlock;
import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.altar.AltarLayout;
import com.miophas.corruption.core.altar.AltarRules;
import com.miophas.corruption.core.altar.RitualRules;
import com.miophas.corruption.core.demon.AmdusiasRules;
import com.miophas.corruption.core.demon.AmdusiasRules.Skill;
import com.miophas.corruption.core.demon.skill.UpheavalRules.Form;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
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
 * Amdusias on a real server: what he is; each of his blows begun by hand and run through his own set of them (Seere's
 * three among them, his own after); the ground he blights, heaves up, breaks open or spears, and only spears where the
 * world gives no leave; his fanfare's bolts, every one from his horn; his crescendo's rings; and the rite that brings him
 * forth at the altar for a horse, run whole. The test world never brings new entities into sight, so what his blows and
 * the rite bring forth is caught as it joins.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class AmdusiasServerTest {
    private static final int Z = 11000;

    /** Amdusias at x, standing on the flat ground, facing south (+Z). */
    private static Amdusias amdusias(ServerLevel level, int x) {
        TestWorld.load(level, x - 48, x + 48, Z - 48, Z + 48);
        Amdusias unicorn = ModEntities.AMDUSIAS.get().create(level);
        unicorn.moveTo(x + .5, -60, Z + .5, 0, 0);
        unicorn.yBodyRot = 0;
        unicorn.yHeadRot = 0;
        level.addFreshEntity(unicorn);
        return unicorn;
    }
    private static <T extends LivingEntity> T still(ServerLevel level, EntityType<T> type, double x, double z) {
        T creature = type.create(level);
        creature.moveTo(x, -60, z, 0, 0);
        if (creature instanceof net.minecraft.world.entity.Mob mob) mob.setNoAi(true);
        level.addFreshEntity(creature);
        return creature;
    }
    private static List<Entity> catching(Runnable run, boolean keep) {
        List<Entity> joined = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> catcher = event -> joined.add(event.getEntity());
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, catcher);
        try { run.run(); }
        finally { NeoForge.EVENT_BUS.unregister(catcher); }
        if (!keep) joined.forEach(Entity::discard);
        return joined;
    }
    /** Runs one of his blows on this quarry from its beginning to its end, by hand, as his own ticks run it. */
    private static void run(Amdusias unicorn, Skill blow, LivingEntity target) {
        unicorn.skills().comeRound(AmdusiasRules.BLOWS.indexOf(blow));
        assertTrue(unicorn.begin(blow, target), blow + " begins");
        assertEquals(blow, unicorn.skill());
        for (int tick = 0; tick < 400 && unicorn.skills().busy(); tick++) {
            unicorn.skills().tick(unicorn, target, cooldown -> cooldown);
            unicorn.travel(Vec3.ZERO);
        }
        assertFalse(unicorn.skills().busy(), blow + " runs its course");
    }
    /** Begins one of his blows and runs it this many ticks. */
    private static void runFor(Amdusias unicorn, Skill blow, LivingEntity target, int ticks) {
        unicorn.skills().comeRound(AmdusiasRules.BLOWS.indexOf(blow));
        assertTrue(unicorn.begin(blow, target), blow + " begins");
        for (int tick = 0; tick < ticks; tick++) unicorn.skills().tick(unicorn, target, cooldown -> cooldown);
    }
    private static boolean dormant(ServerLevel level, BlockPos at) { return level.getBlockState(at).is(ModBlocks.DEACTIVATED_CORRUPTION.get()); }

    @Test void heIsALordOfTheCorruptionThatOnlyTheArcaneHurts(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Amdusias unicorn = amdusias(level, 500_000);
            try {
                assertInstanceOf(GoeticDemon.class, unicorn);
                assertTrue(CorruptionFactionApi.isBoss(unicorn) && CorruptionFactionApi.isRemnant(unicorn), "a lord, of the corruption's side");
                assertEquals(AmdusiasRules.MAX_HEALTH, unicorn.getMaxHealth());
                assertEquals(7, unicorn.emblem(), "his emblem on the bar, the eighth");
                assertEquals(Amdusias.BODY.size(), unicorn.getParts().length, "struck at in his parts");
                assertFalse(unicorn.isNoGravity(), "he cannot fly");
                assertFalse(unicorn.hurt(level.damageSources().generic(), 10), "an ordinary blow passes through him");
                assertTrue(unicorn.hurt(ModDamageTypes.corruption(level), 10), "the corruption's own hurt (a spell's) does not");
            } finally {
                unicorn.discard();
            }
        });
    }

    @Test void hisHoovesBlinksAndSpikesAreSeeres(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Amdusias unicorn = amdusias(level, 500_100);
            IronGolem before = still(level, EntityType.IRON_GOLEM, 500_100.5, Z + 3.5);
            try {
                run(unicorn, Skill.HOOVES, before);
                assertEquals(before.getMaxHealth() - AmdusiasRules.HOOVES.damage(), before.getHealth(), .01, "his hooves come down on it");
                before.setHealth(before.getMaxHealth());
                run(unicorn, Skill.BLINKS, before);
                assertTrue(before.getHealth() < before.getMaxHealth(), "his blinks cut through it");
                before.setHealth(before.getMaxHealth());
                before.invulnerableTime = 0;
                unicorn.moveTo(500_100.5, -60, Z + .5, 0, 0);
                before.moveTo(500_100.5, -60, Z + 8.5);
                run(unicorn, Skill.SPIKES, before);
                assertTrue(before.getHealth() < before.getMaxHealth() && before.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "his spikes strike and slow it");
            } finally {
                unicorn.discard();
                before.discard();
            }
        });
    }

    @Test void hisBlightTurnsTheGroundAboutHimDormantButOnlyWhereTheWorldGivesLeave(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Amdusias unicorn = amdusias(level, 500_200);
            Villager near = still(level, EntityType.VILLAGER, 500_203.5, Z + .5);
            GameRules.BooleanValue griefing = level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING);
            try {
                run(unicorn, Skill.BLIGHT, near);
                assertTrue(unicorn.blight().struck().contains(near) && near.getHealth() < near.getMaxHealth(), "stamping, he hurts what is near");
                int turned = unicorn.blight().turned();
                assertTrue(turned > 40 && turned <= AmdusiasRules.BLIGHT.most(), "the ground about him dormant: " + turned);
                assertTrue(dormant(level, new BlockPos(500_200, -61, Z)), "under him");
                assertTrue(dormant(level, new BlockPos(500_206, -61, Z)), "six blocks off");
                assertFalse(dormant(level, new BlockPos(500_209, -61, Z)), "and no further than its reach");
                griefing.set(false, server);
                unicorn.moveTo(500_230.5, -60, Z + .5, 0, 0);
                near.moveTo(500_233.5, -60, Z + .5);
                run(unicorn, Skill.BLIGHT, near);
                assertEquals(0, unicorn.blight().turned(), "where the world gives no leave, the ground stays as it was");
            } finally {
                griefing.set(true, server);
                unicorn.discard();
                near.discard();
            }
        });
    }

    @Test void hisUpheavalHeavesTheGroundUpBreaksItOpenOrSpearsIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Amdusias unicorn = amdusias(level, 500_300);
            IronGolem quarry = still(level, EntityType.IRON_GOLEM, 500_300.5, Z + 10.5);
            GameRules.BooleanValue griefing = level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING);
            try {
                // Rising: a pillar of the dormant corruption under it, and it flung up off it.
                runFor(unicorn, Skill.UPHEAVAL, quarry, 1);
                unicorn.upheaval().force(Form.RISE);
                for (int tick = 0; tick < 60 && unicorn.skills().busy(); tick++) unicorn.skills().tick(unicorn, quarry, cooldown -> cooldown);
                assertEquals(9 * AmdusiasRules.UPHEAVAL.rise(), unicorn.upheaval().raised(), "every marked column risen three blocks");
                for (int up = 0; up < AmdusiasRules.UPHEAVAL.rise(); up++) assertTrue(dormant(level, new BlockPos(500_300, -60 + up, Z + 10)));
                assertTrue(quarry.getY() >= -60 + AmdusiasRules.UPHEAVAL.rise() - .01, "lifted with it, nothing shut inside it");
                assertTrue(quarry.getDeltaMovement().y > .8, "and flung up");
                assertTrue(quarry.getHealth() < quarry.getMaxHealth());
                // Sinking: a pit broken open under it, its floor dormant.
                quarry.moveTo(500_300.5, -60, Z + 20.5);
                quarry.setDeltaMovement(Vec3.ZERO);
                List<Entity> thrown = catching(() -> {
                    runFor(unicorn, Skill.UPHEAVAL, quarry, 1);
                    unicorn.upheaval().force(Form.SINK);
                    for (int tick = 0; tick < 60 && unicorn.skills().busy(); tick++) unicorn.skills().tick(unicorn, quarry, cooldown -> cooldown);
                }, false);
                assertTrue(unicorn.upheaval().sunk() >= 9, "the ground under it broken open: " + unicorn.upheaval().sunk());
                assertTrue(thrown.stream().anyMatch(net.minecraft.world.entity.item.FallingBlockEntity.class::isInstance), "its blocks thrown up and out");
                assertTrue(level.getBlockState(new BlockPos(500_300, -61, Z + 20)).isAir(), "a pit where it stood");
                assertTrue(level.getBlockState(new BlockPos(500_300, -64, Z + 20)).is(Blocks.BEDROCK)
                        || dormant(level, new BlockPos(500_300, -64, Z + 20)) || dormant(level, new BlockPos(500_300, -63, Z + 20)), "its floor dormant, or the bedrock");
                // Spikes, all that is left where the world gives no leave: the ground as it was, and it flung up high.
                griefing.set(false, server);
                quarry.moveTo(500_310.5, -60, Z + 10.5);
                quarry.setDeltaMovement(Vec3.ZERO);
                quarry.setHealth(quarry.getMaxHealth());
                unicorn.moveTo(500_310.5, -60, Z + .5, 0, 0);
                run(unicorn, Skill.UPHEAVAL, quarry);
                assertEquals(Form.SPIKES, unicorn.upheaval().form(), "without leave to change the ground, spikes");
                assertEquals(0, unicorn.upheaval().raised() + unicorn.upheaval().sunk());
                assertEquals(Blocks.GRASS_BLOCK, level.getBlockState(new BlockPos(500_310, -61, Z + 10)).getBlock(), "the ground as it was");
                assertTrue(quarry.getDeltaMovement().y > 1 && quarry.getHealth() < quarry.getMaxHealth(), "it flung up and hurt");
            } finally {
                griefing.set(true, server);
                unicorn.discard();
                quarry.discard();
            }
        });
    }

    @Test void hisStrikeAtTheMindAndHisFanfareFromHisHorn(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Amdusias unicorn = amdusias(level, 500_400);
            Villager mind = still(level, EntityType.VILLAGER, 500_400.5, Z + 12.5);
            IronGolem far = still(level, EntityType.IRON_GOLEM, 500_400.5, Z + 20.5);
            try {
                run(unicorn, Skill.PSYCHIC, mind);
                assertTrue(mind.hasEffect(MobEffects.CONFUSION) && mind.hasEffect(MobEffects.DARKNESS) && mind.getHealth() < mind.getMaxHealth(), "he strikes at its mind");
                Vec3 horn = unicorn.spitFrom(0);
                List<DemonBolt> bolts = catching(() -> run(unicorn, Skill.FANFARE, far), false).stream().filter(DemonBolt.class::isInstance)
                        .map(DemonBolt.class::cast).toList();
                assertEquals(AmdusiasRules.FANFARE.bolts(), bolts.size(), "his fanfare: " + AmdusiasRules.FANFARE.bolts() + " bolts");
                assertEquals(AmdusiasRules.FANFARE.bolts(), unicorn.fanfare().loosed());
                assertTrue(bolts.stream().allMatch(bolt -> bolt.position().distanceTo(horn) < 1), "every one from the tip of his horn");
            } finally {
                unicorn.discard();
                mind.discard();
                far.discard();
            }
        });
    }

    @Test void hisCrescendosRingsHurtAndThrowBackWhatTheyPassButNotHisOwn(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Amdusias unicorn = amdusias(level, 500_500);
            IronGolem near = still(level, EntityType.IRON_GOLEM, 500_505.5, Z + .5);
            Villager beyond = still(level, EntityType.VILLAGER, 500_520.5, Z + .5);
            CorruptedRemnant ally = still(level, ModEntities.CORRUPTED_REMNANT.get(), 500_500.5, Z + 6.5);
            try {
                List<Entity> shown = catching(() -> run(unicorn, Skill.CRESCENDO, near), false);
                assertEquals(AmdusiasRules.CRESCENDO.rings(), unicorn.crescendo().hits(), "every ring catches the golem once, and nothing else");
                assertEquals(near.getMaxHealth() - AmdusiasRules.CRESCENDO.most(), near.getHealth(), .01, "each hurting it");
                assertTrue(near.getDeltaMovement().x > .5, "and throwing it away from him");
                assertEquals(beyond.getMaxHealth(), beyond.getHealth(), "past the rings' reach, nothing");
                assertEquals(ally.getMaxHealth(), ally.getHealth(), "his own side, nothing");
                assertEquals(AmdusiasRules.CRESCENDO.rings(), shown.stream().filter(entity -> entity instanceof SpellVisualEntity visual
                        && net.minecraft.resources.ResourceLocation.parse("corruption:crescendo").equals(visual.effect())).count(), "each ring drawn as music");
            } finally {
                unicorn.discard();
                near.discard();
                beyond.discard();
                ally.discard();
            }
        });
    }

    @Test void theRiteBringsHimForthForAHorseAmongNoteBlocks(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            BlockPos at = new BlockPos(500_600, -50, Z);
            TestWorld.load(level, at.getX() - 8, at.getX() + 8, Z - 8, Z + 8);
            for (int east = -AltarLayout.REACH; east <= AltarLayout.REACH; east++)
                for (int north = -AltarLayout.REACH; north <= AltarLayout.REACH; north++) {
                    level.setBlock(at.offset(east, -1, -north), Blocks.STONE.defaultBlockState(), 3);
                    level.setBlock(at.offset(east, 0, -north), Blocks.AIR.defaultBlockState(), 3);
                }
            level.setBlock(at, ModBlocks.ALTAR_OFFERING.get().defaultBlockState().setValue(AltarOfferingBlock.FACING, Direction.NORTH), 3);
            AltarOfferingBlockEntity offering = (AltarOfferingBlockEntity) level.getBlockEntity(at);
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++)
                level.setBlock(offering.place(AltarLayout.east(slot), AltarLayout.north(slot)), ModBlocks.ALTAR_PILLAR.get().defaultBlockState(), 3);
            for (int tick = 0; tick < AltarRules.CHECK_TICKS; tick++) AltarOfferingBlockEntity.serverTick(level, at, level.getBlockState(at), offering);
            ItemStack[] pillars = {ArcaneApi.inscribed(new ItemStack(ModItems.PARCHMENT.get()), ArcaneApi.key("corruption:amdusias")), new ItemStack(Items.GOAT_HORN),
                    new ItemStack(Items.NOTE_BLOCK), new ItemStack(ModItems.CORRUPTED_HEARTSTONE.get()), new ItemStack(Items.AMETHYST_SHARD), new ItemStack(Items.BELL)};
            for (int slot = 0; slot < AltarLayout.SLOTS; slot++)
                ((AltarPillarBlockEntity) level.getBlockEntity(offering.place(AltarLayout.east(slot), AltarLayout.north(slot)))).set(pillars[slot]);
            for (int[] corner : new int[][]{{2, 2}, {2, -2}, {-2, -2}, {-2, 2}}) level.setBlock(offering.place(corner[0], corner[1]), Blocks.NOTE_BLOCK.defaultBlockState(), 3);
            Horse horse = EntityType.HORSE.create(level);
            horse.moveTo(offering.top().x, offering.top().y, offering.top().z);
            offering.bind(horse);
            FakePlayer witness = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes("amdusias_witness".getBytes()), "amdusias_witness"));
            witness.moveTo(offering.top().x + 2.5, at.getY(), offering.top().z + 1);
            witness.setData(ModAttachments.PLAYER_CORRUPTION, 14);
            if (!level.players().contains(witness)) level.players().add(witness);
            try {
                List<Entity> joined = catching(() -> {
                    for (int tick = 0; tick < 240 + RitualRules.AFTER_STRIKE + 1; tick++) AltarOfferingBlockEntity.serverTick(level, at, level.getBlockState(at), offering);
                }, false);
                assertTrue(horse.isRemoved(), "the horse is given up");
                assertTrue(joined.stream().anyMatch(entity -> entity.getType() == ModEntities.AMDUSIAS.get()), "and Amdusias comes forth: " + joined);
            } finally {
                level.players().remove(witness);
                witness.setData(ModAttachments.PLAYER_CORRUPTION, 0);
                for (int east = -AltarLayout.REACH; east <= AltarLayout.REACH; east++)
                    for (int north = -AltarLayout.REACH; north <= AltarLayout.REACH; north++) level.setBlock(at.offset(east, 0, -north), Blocks.AIR.defaultBlockState(), 3);
            }
        });
    }
}
