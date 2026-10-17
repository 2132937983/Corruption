package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.api.plant.MaliceVineApi;
import com.miophas.corruption.common.infection.CorruptionConversions;
import com.miophas.corruption.common.item.CorruptedRobeArmorItem;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModEffects;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModItems;
import com.miophas.corruption.core.corruption.CorruptedRobeRules.Tier;
import com.miophas.corruption.core.elite.BlighthoundRules;
import com.miophas.corruption.core.elite.MindweaverRules;
import com.miophas.corruption.core.elite.SowerRules;
import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The mindweaver, the blighthound and the sower on a real server: each is of the corruption as every elite is, hatches
 * from an egg of its own and is brooded by the vine; the mindweaver's thread hurts the mind through any armour, again and
 * again, mending it; the blighthound's every bite lays the curse on deeper, its spring three bites deep, its frenzy five,
 * and it leaves the cursed gas behind it as it runs; the sower's feet take the ground under them (where the world gives
 * leave), it sows the ground about it, sends roots up under a quarry, and mends on the corruption's ground. What the
 * curse and the thread do to a player's stand-in, bare and in diamond, is measured and printed (MEASURE) for the record.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class EliteTrioServerTest {
    private static final int Z = 9000;

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
    /** A player's stand-in standing still, in this armour (head to feet), or none. */
    private static Villager standIn(ServerLevel level, double x, double z, Item... armour) {
        Villager villager = still(level, EntityType.VILLAGER, x, z);
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int index = 0; index < armour.length; index++) villager.setItemSlot(slots[index], new ItemStack(armour[index]));
        villager.tick();
        return villager;
    }
    private static final Item[] DIAMOND = {Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS};
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
    private static void dress(LivingEntity wearer, @Nullable Tier grade) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET})
            wearer.setItemSlot(slot, ItemStack.EMPTY);
        if (grade == null) return;
        for (DeferredItem<CorruptedRobeArmorItem> robe : ModItems.CORRUPTED_ROBES)
            if (robe.get().robeTier() == grade) wearer.setItemSlot(robe.get().getEquipmentSlot(), new ItemStack(robe.get()));
    }
    @Nullable private static MobEffectInstance curse(LivingEntity cursed) { return cursed.getEffect(ModEffects.CORRUPTION_CURSE); }
    /** Lives a creature through its own ticks (its effects among them), as many as these. */
    private static void live(LivingEntity creature, int ticks) { for (int tick = 0; tick < ticks && creature.isAlive(); tick++) creature.tick(); }

    @Test void eachIsOfTheCorruptionHatchesFromItsOwnEggAndTheVineBroodsIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            List<CorruptionElite<?>> elites = List.of(place(level, ModEntities.CORRUPTED_MINDWEAVER.get(), 400_000.5, -60, Z + .5),
                    place(level, ModEntities.CORRUPTED_BLIGHTHOUND.get(), 400_006.5, -60, Z + .5), place(level, ModEntities.CORRUPTED_SOWER.get(), 400_012.5, -60, Z + .5));
            double[] health = {MindweaverRules.MAX_HEALTH, BlighthoundRules.MAX_HEALTH, SowerRules.MAX_HEALTH};
            CorruptedRemnant remnant = ModEntities.CORRUPTED_REMNANT.get().create(level);
            Villager wearer = still(level, EntityType.VILLAGER, 400_000.5, Z + 10.5);
            try {
                for (int index = 0; index < elites.size(); index++) {
                    CorruptionElite<?> elite = elites.get(index);
                    EntityType<?> type = elite.getType();
                    assertTrue(CorruptionFactionApi.isRemnant(elite) && CorruptionFactionApi.isElite(elite) && !CorruptionFactionApi.isBoss(elite), type + " is an elite");
                    assertEquals(health[index], elite.getMaxHealth());
                    assertTrue(elite.isAlliedTo(remnant) && elite.canAttack(wearer));
                    assertTrue(elite.hurt(level.damageSources().generic(), 5), type + ": any blow hurts it");
                    elite.invulnerableTime = 0;
                    assertFalse(elite.hurt(ModDamageTypes.corruption(level), 5), type + ": the corruption's hurt does not");
                    assertFalse(elite.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, 200)), type + ": nor its curse");
                    dress(wearer, Tier.NOVICE);
                    assertFalse(CorruptionVeilApi.hiddenFrom(wearer, elite), "a novice's robes do not hide their wearer from " + type);
                    dress(wearer, null);
                    SpawnEggItem egg = SpawnEggItem.byId(type);
                    assertNotNull(egg, type + " has an egg");
                    assertTrue(ModItems.ELITE_EGGS.stream().anyMatch(held -> held.get() == egg), "among the elites' eggs");
                    assertTrue(type.is(MaliceVineApi.BROOD), "the vine broods " + type + " on land");
                    LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, elite)
                            .withParameter(LootContextParams.ORIGIN, elite.position()).withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                            .create(LootContextParamSets.ENTITY);
                    assertTrue(server.reloadableRegistries().getLootTable(elite.getLootTable()).getRandomItems(params).stream()
                            .allMatch(stack -> stack.is(ModItems.CORRUPTION_REMAIN.get())), type + " leaves what a remnant leaves");
                }
                assertEquals(17, ModItems.ELITE_EGGS.size(), "an egg for each of the seventeen");
            } finally {
                elites.forEach(Entity::discard);
                wearer.discard();
            }
        });
    }

    @Test void theMindweaversThreadHurtsTheMindThroughAnyArmourAgainAndAgainAndMendsIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedMindweaver weaver = place(level, ModEntities.CORRUPTED_MINDWEAVER.get(), 400_100.5, -60, Z + .5);
            Villager bare = standIn(level, 400_100.5, Z + 8.5), dressed = standIn(level, 400_096.5, Z + 8.5, DIAMOND);
            try {
                weaver.setHealth(200);
                run(weaver, MindweaverRules.Skill.THREAD.ordinal(), bare);
                float bareLost = bare.getMaxHealth() - bare.getHealth();
                assertEquals(MindweaverRules.THREAD.pulses(), weaver.thread().landed(), "every pull lands");
                assertEquals(MindweaverRules.THREAD.most(), bareLost, .01, "each 1, ten times over");
                assertEquals(200 + MindweaverRules.THREAD.pulses() * MindweaverRules.THREAD.heal(), weaver.getHealth(), .01, "each pull mends it");
                run(weaver, MindweaverRules.Skill.THREAD.ordinal(), dressed);
                float dressedLost = dressed.getMaxHealth() - dressed.getHealth();
                assertEquals(bareLost, dressedLost, .01, "diamond turns none of it aside");
                System.out.printf("MEASURE mindweaver thread, one run: bare stand-in lost %.2f, in diamond lost %.2f (of 20)%n", bareLost, dressedLost);
            } finally {
                weaver.discard();
                bare.discard();
                dressed.discard();
            }
        });
    }

    @Test void theMindweaversThreadBreaksWhenItsQuarryIsOutOfReachAndItsWailStrikesTheMind(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedMindweaver weaver = place(level, ModEntities.CORRUPTED_MINDWEAVER.get(), 400_200.5, -60, Z + .5);
            Villager quarry = standIn(level, 400_200.5, Z + 8.5), near = standIn(level, 400_203.5, Z + 4.5);
            try {
                weaver.skills().comeRound(MindweaverRules.Skill.THREAD.ordinal() - 1);
                assertTrue(weaver.begin(MindweaverRules.Skill.THREAD.ordinal(), quarry));
                int windup = MindweaverRules.THREAD.timing().windup();
                for (int tick = 0; tick <= windup; tick++) weaver.skills().tick(weaver, quarry, cooldown -> cooldown);
                assertEquals(1, weaver.thread().landed());
                quarry.moveTo(400_200.5, -60, Z + 40.5);
                int ticks = 0;
                for (; ticks < 100 && weaver.skills().busy(); ticks++) weaver.skills().tick(weaver, quarry, cooldown -> cooldown);
                assertTrue(weaver.thread().broken(), "out of its reach, the thread breaks");
                assertTrue(ticks <= MindweaverRules.THREAD.interval() * MindweaverRules.THREAD.breaksAfter() + 1, "after so many pulls find nothing: " + ticks);
                quarry.moveTo(400_200.5, -60, Z + 6.5);
                run(weaver, MindweaverRules.Skill.WAIL.ordinal(), quarry);
                for (Villager mind : List.of(quarry, near)) {
                    assertTrue(mind.hasEffect(MobEffects.CONFUSION) && mind.hasEffect(MobEffects.DARKNESS) && mind.hasEffect(MobEffects.MOVEMENT_SLOWDOWN),
                            "its wail strikes the minds about its quarry");
                    assertTrue(mind.getHealth() < mind.getMaxHealth());
                }
            } finally {
                weaver.discard();
                quarry.discard();
                near.discard();
            }
        });
    }

    @Test void theBlighthoundsEveryBiteLaysTheCurseOnDeeper(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedBlighthound hound = place(level, ModEntities.CORRUPTED_BLIGHTHOUND.get(), 400_300.5, -60, Z + .5);
            Villager bitten = standIn(level, 400_300.5, Z + 1.5);
            CorruptedRemnant ally = still(level, ModEntities.CORRUPTED_REMNANT.get(), 400_302.5, Z + .5);
            try {
                assertTrue(hound.doHurtTarget(bitten));
                assertEquals(BlighthoundRules.CURSE.first(), curse(bitten).getDuration(), "a first bite curses it a while");
                assertEquals(0, curse(bitten).getAmplifier());
                for (int bite = 0; bite < 3; bite++) {
                    bitten.invulnerableTime = 0;
                    assertTrue(hound.doHurtTarget(bitten));
                }
                assertEquals(BlighthoundRules.CURSE.first() + 3 * BlighthoundRules.CURSE.add(), curse(bitten).getDuration(), "every bite after, a second longer");
                assertEquals(1, curse(bitten).getAmplifier(), "and long enough, twice as hard");
                for (int bite = 0; bite < 5; bite++) {
                    bitten.invulnerableTime = 0;
                    hound.doHurtTarget(bitten);
                }
                assertEquals(BlighthoundRules.CURSE.most(), curse(bitten).getDuration(), "never longer than the most");
                assertEquals(1, curse(bitten).getAmplifier(), "nor stronger than Curse II");
                assertFalse(CurseStacks.deepen(ally, hound, BlighthoundRules.CURSE), "the corruption's own are never cursed");
            } finally {
                hound.discard();
                bitten.discard();
                ally.discard();
            }
        });
    }

    @Test void theBlighthoundSpringsThreeBitesDeepWorriesFiveAndTrailsTheCursedGas(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedBlighthound hound = place(level, ModEntities.CORRUPTED_BLIGHTHOUND.get(), 400_400.5, -60, Z + .5);
            IronGolem sprungAt = still(level, EntityType.IRON_GOLEM, 400_400.5, Z + 6.5);
            Villager worried = standIn(level, 400_406.5, Z + 1.5);
            try {
                hound.setYRot(0);
                hound.skills().comeRound(BlighthoundRules.Skill.REND.ordinal() - 1);
                assertTrue(hound.begin(BlighthoundRules.Skill.REND.ordinal(), sprungAt));
                for (int tick = 0; tick < 60 && hound.skills().busy(); tick++) {
                    hound.skills().tick(hound, sprungAt, cooldown -> cooldown);
                    hound.act(sprungAt, hound.skills().busy());
                    hound.travel(Vec3.ZERO);
                }
                assertSame(sprungAt, hound.rend().struck(), "it sprang on the golem");
                assertEquals(BlighthoundRules.CURSE.first() + 2 * BlighthoundRules.CURSE.add(), curse(sprungAt).getDuration(), "cursing it three bites deep");
                hound.moveTo(400_405.5, -60, Z + .5, 0, 0);
                run(hound, BlighthoundRules.Skill.FRENZY.ordinal(), worried);
                assertNotNull(curse(worried), "its frenzy curses what it worries");
                assertEquals(1, curse(worried).getAmplifier(), "five bites deep: Curse II");
                hound.setOnGround(true);
                hound.setDeltaMovement(.3, 0, 0);
                List<AreaEffectCloud> trail = of(catching(() -> {
                    for (int tick = 0; tick < BlighthoundRules.TRAIL_EVERY; tick++) hound.trail();
                }, false), AreaEffectCloud.class);
                assertEquals(1, trail.size(), "running, it leaves a cloud of the cursed gas behind it");
                assertSame(hound, trail.get(0).getOwner());
            } finally {
                hound.discard();
                sprungAt.discard();
                worried.discard();
            }
        });
    }

    @Test void theBlighthoundsCurseOutlivesItButArmourTempersIt(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedBlighthound hound = place(level, ModEntities.CORRUPTED_BLIGHTHOUND.get(), 400_500.5, -60, Z + .5);
            Villager bare = standIn(level, 400_500.5, Z + 4.5), dressed = standIn(level, 400_504.5, Z + 4.5, DIAMOND);
            try {
                // As deep as its bites lay it: Curse II, six seconds to run. Then the hound is gone, and the curse bites on.
                for (Villager bitten : List.of(bare, dressed)) for (int bite = 0; bite < 8; bite++) CurseStacks.deepen(bitten, hound, BlighthoundRules.CURSE);
                hound.discard();
                live(bare, BlighthoundRules.CURSE.most() + 10);
                live(dressed, BlighthoundRules.CURSE.most() + 10);
                System.out.printf("MEASURE blighthound curse at its deepest, after the hound is gone: bare stand-in %s, in diamond kept %.2f of 20%n",
                        bare.isAlive() ? String.format("kept %.2f", bare.getHealth()) : "died", dressed.getHealth());
                assertFalse(bare.isAlive(), "bare, its curse at its deepest kills a player's stand-in");
                assertTrue(dressed.isAlive() && dressed.getHealth() > 6, "in diamond, it survives it: " + dressed.getHealth());
            } finally {
                bare.discard();
                dressed.discard();
            }
        });
    }

    @Test void theSowersFeetTakeTheGroundAndOnlyWhereTheWorldGivesLeave(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedSower sower = place(level, ModEntities.CORRUPTED_SOWER.get(), 400_600.5, -60, Z + .5);
            GameRules.BooleanValue griefing = level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING);
            try {
                sower.setOnGround(true);
                assertTrue(sower.sow(level) > 0, "where it stands, the ground is taken");
                assertTrue(CorruptionConversions.isCorrupted(level.getBlockState(sower.getOnPos())), "the block under it is the corruption's now");
                assertEquals(0, sower.sow(level), "once, while it stands there");
                sower.moveTo(400_603.5, -60, Z + .5, 0, 0);
                sower.setOnGround(true);
                assertTrue(sower.sow(level) > 0 && sower.sow(level) == 0, "and again where it comes to");
                assertTrue(sower.sown() <= 2 * SowerRules.SOW_MOST);
                griefing.set(false, server);
                sower.moveTo(400_607.5, -60, Z + .5, 0, 0);
                sower.setOnGround(true);
                assertEquals(0, sower.sow(level), "where the world gives no leave, nothing");
                assertFalse(CorruptionConversions.isCorrupted(level.getBlockState(sower.getOnPos())));
                griefing.set(true, server);
                sower.moveTo(400_600.5, -60, Z + .5, 0, 0);
                // What it stands on is found as it comes to stand (as it is every tick it moves, in the game).
                sower.setOnGround(true);
                sower.setHealth(100);
                assertTrue(sower.mend(level), "on the corruption's ground it mends: on " + sower.getOnPos() + ", " + level.getBlockState(sower.getOnPos()));
                assertEquals(100 + SowerRules.REGEN, sower.getHealth(), 1e-4);
                sower.moveTo(400_607.5, -60, Z + .5, 0, 0);
                sower.setOnGround(true);
                assertFalse(sower.mend(level), "off it, it does not");
            } finally {
                griefing.set(true, server);
                sower.discard();
            }
        });
    }

    @Test void theSowerSowsTheGroundAboutItAndSendsRootsUpUnderAQuarry(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            CorruptedSower sower = place(level, ModEntities.CORRUPTED_SOWER.get(), 400_700.5, -60, Z + .5);
            Villager close = standIn(level, 400_702.5, Z + .5), further = standIn(level, 400_700.5, Z + 9.5);
            CorruptedRemnant ally = still(level, ModEntities.CORRUPTED_REMNANT.get(), 400_698.5, Z + .5);
            try {
                run(sower, SowerRules.Skill.SEED.ordinal(), close);
                assertTrue(sower.seed().turned() > 8, "it sows the ground about it: " + sower.seed().turned());
                assertTrue(sower.seed().struck().contains(close) && close.getHealth() < close.getMaxHealth(), "and throws back what is near");
                assertFalse(sower.seed().struck().contains(ally), "but not its own side");
                int corrupted = 0;
                for (int east = -3; east <= 3; east++)
                    for (int south = -3; south <= 3; south++)
                        if (CorruptionConversions.isCorrupted(level.getBlockState(new BlockPos(400_700 + east, -61, Z + south)))) corrupted++;
                assertTrue(corrupted > 8, "the ground about it is the corruption's: " + corrupted);
                run(sower, SowerRules.Skill.SNARE.ordinal(), further);
                assertTrue(sower.snare().caught().contains(further), "its roots catch the quarry where it stood");
                MobEffectInstance held = further.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                assertNotNull(held, "and hold it fast");
                assertEquals(SowerRules.SNARE.holdLevel(), held.getAmplifier());
                assertTrue(further.getHealth() < further.getMaxHealth());
            } finally {
                sower.discard();
                close.discard();
                further.discard();
                ally.discard();
            }
        });
    }
}
