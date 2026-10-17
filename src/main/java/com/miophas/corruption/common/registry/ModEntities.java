package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.miophas.corruption.api.corruption.AgaresApi;
import com.miophas.corruption.api.corruption.BaalApi;
import com.miophas.corruption.api.corruption.RemnantCombatApi;
import com.miophas.corruption.api.plant.LivingPlantApi;
import com.miophas.corruption.api.plant.LivingPlantKind;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Corruption.MOD_ID)
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Corruption.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedDragon>> CORRUPTED_DRAGON = TYPES.register("corrupted_dragon",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedDragon::new, MobCategory.MONSTER)
                    .sized(16,8).fireImmune().clientTrackingRange(16).updateInterval(3).build("corruption:corrupted_dragon"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedDragonFireball>> CORRUPTED_DRAGON_FIREBALL = TYPES.register("corrupted_dragon_fireball",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedDragonFireball::new, MobCategory.MISC)
                    .sized(1,1).clientTrackingRange(10).updateInterval(1).build("corruption:corrupted_dragon_fireball"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedDragonBreath>> CORRUPTED_DRAGON_BREATH = TYPES.register("corrupted_dragon_breath",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedDragonBreath::new, MobCategory.MISC)
                    .sized(6,.5F).fireImmune().clientTrackingRange(10).updateInterval(10).build("corruption:corrupted_dragon_breath"));
    /** The bolt a spell's LaunchBolt flies, carrying the effects it does where it strikes. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.SpellProjectile>> SPELL_BOLT = TYPES.register("spell_bolt",
            () -> EntityType.Builder.<com.miophas.corruption.common.entity.SpellProjectile>of(com.miophas.corruption.common.entity.SpellProjectile::new, MobCategory.MISC)
                    .sized(.5F, .5F).clientTrackingRange(10).updateInterval(1).build("corruption:spell_bolt"));
    public static CorruptedRemnant createRemnant(net.minecraft.world.level.Level level, net.minecraft.nbt.CompoundTag appearance) {
        return appearance.getString("Type").equals("minecraft:ender_dragon") ? CORRUPTED_DRAGON.get().create(level) : CORRUPTED_REMNANT.get().create(level);
    }
    public static final DeferredHolder<EntityType<?>, EntityType<CorruptedRemnant>> CORRUPTED_REMNANT = TYPES.register("corrupted_remnant",
            () -> EntityType.Builder.of(CorruptedRemnant::new, MobCategory.MONSTER)
                    .sized(RemnantCombatApi.definition().width(), RemnantCombatApi.definition().height())
                    .clientTrackingRange(10).updateInterval(3).build("corruption:corrupted_remnant"));
    /** The purple bolt that strikes an altar as a rite completes: lightning that burns and hurts nothing. */
    /** A spell's effect, drawn on the client where a spell's "visual" action sets it down; never saved. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.SpellVisualEntity>> SPELL_VISUAL = TYPES.register("spell_visual",
            () -> EntityType.Builder.<com.miophas.corruption.common.entity.SpellVisualEntity>of(com.miophas.corruption.common.entity.SpellVisualEntity::new, MobCategory.MISC)
                    .noSave().fireImmune().sized(.1F, .1F).clientTrackingRange(12).updateInterval(Integer.MAX_VALUE).build("corruption:spell_visual"));
    /** A spell's beam as it fires: kept at its caster's hand and turned as they look; drawn from them on the client; never saved. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.SpellBeamEntity>> SPELL_BEAM = TYPES.register("spell_beam",
            () -> EntityType.Builder.<com.miophas.corruption.common.entity.SpellBeamEntity>of(com.miophas.corruption.common.entity.SpellBeamEntity::new, MobCategory.MISC)
                    .noSave().fireImmune().sized(.1F, .1F).clientTrackingRange(16).updateInterval(1).build("corruption:spell_beam"));
    /** A boss's beam (the beam skill): stood where it leaves the boss and turned every tick by the skill; never saved. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.DemonBeamEntity>> DEMON_BEAM = TYPES.register("demon_beam",
            () -> EntityType.Builder.<com.miophas.corruption.common.entity.DemonBeamEntity>of(com.miophas.corruption.common.entity.DemonBeamEntity::new, MobCategory.MISC)
                    .noSave().fireImmune().sized(.1F, .1F).clientTrackingRange(16).updateInterval(1).build("corruption:demon_beam"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptLightning>> CORRUPT_LIGHTNING = TYPES.register("corrupt_lightning",
            () -> EntityType.Builder.<com.miophas.corruption.common.entity.CorruptLightning>of(com.miophas.corruption.common.entity.CorruptLightning::new, MobCategory.MISC)
                    .noSave().sized(0, 0).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE).build("corruption:corrupt_lightning"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.MaliceVine>> MALICE_VINE = TYPES.register("malice_vine",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.MaliceVine::new, MobCategory.MISC)
                    .sized(LivingPlantApi.definition(LivingPlantKind.MALICE_VINE).width(0), LivingPlantApi.definition(LivingPlantKind.MALICE_VINE).height(0))
                    .clientTrackingRange(10).updateInterval(20).build("corruption:malice_vine"));

    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Greed>> GREED = TYPES.register("greed",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Greed::new, MobCategory.MONSTER)
                    .sized(LivingPlantApi.definition(LivingPlantKind.GREED).width(0), LivingPlantApi.definition(LivingPlantKind.GREED).height(0))
                    .clientTrackingRange(12).updateInterval(10).build("corruption:greed"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.GreedProjectile>> GREED_PROJECTILE = TYPES.register("greed_projectile",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.GreedProjectile::new, MobCategory.MISC)
                    .sized(.7F, .7F).clientTrackingRange(8).updateInterval(2).build("corruption:greed_projectile"));
    /** Its own box is a small stand-in; the hitboxes are its parts. Sent from far off and every tick, so its stride stays in step. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Baal>> BAAL = TYPES.register("baal",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Baal::new, MobCategory.MONSTER)
                    .sized(BaalApi.bodyWidthBlocks(), BaalApi.bodyHeightBlocks()).eyeHeight(BaalApi.eyeHeightBlocks())
                    .fireImmune().clientTrackingRange(32).updateInterval(1).build("corruption:baal"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Agares>> AGARES = TYPES.register("agares",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Agares::new, MobCategory.MONSTER)
                    .sized(AgaresApi.bodyWidthBlocks(), AgaresApi.bodyHeightBlocks()).eyeHeight(AgaresApi.eyeHeightBlocks())
                    .fireImmune().clientTrackingRange(32).updateInterval(1).build("corruption:agares"));
    /** Andromalius, the seventy-second spirit: a hell serpent with its forebody raised, two blocks across and four high. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Andromalius>> ANDROMALIUS = TYPES.register("andromalius",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Andromalius::new, MobCategory.MONSTER)
                    .sized(2F, 4F).eyeHeight(3.7F).fireImmune().clientTrackingRange(10).build("corruption:andromalius"));
    /** Dantalion, the seventy-first spirit: a great flying book, then a mirror standing taller (his own box changes as he does). */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Dantalion>> DANTALION = TYPES.register("dantalion",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Dantalion::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.demon.DantalionRules.BOOK_WIDTH, com.miophas.corruption.core.demon.DantalionRules.BOOK_HEIGHT)
                    .fireImmune().clientTrackingRange(10).build("corruption:dantalion"));
    /** Seere, the seventieth spirit: a winged horse, fast on the ground and in the sky (sent to clients the oftener for it). */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Seere>> SEERE = TYPES.register("seere",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Seere::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.demon.SeereRules.WIDTH, com.miophas.corruption.core.demon.SeereRules.HEIGHT).eyeHeight(2.6F)
                    .fireImmune().clientTrackingRange(12).updateInterval(2).build("corruption:seere"));
    /** Decarabia, the sixty-ninth spirit: a star, and then a goshawk; his likenesses are of his own kind (sent to clients often, as he flies fast). */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Decarabia>> DECARABIA = TYPES.register("decarabia",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Decarabia::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.demon.DecarabiaRules.STAR_SIZE, com.miophas.corruption.core.demon.DecarabiaRules.STAR_SIZE)
                    .fireImmune().clientTrackingRange(12).updateInterval(2).build("corruption:decarabia"));
    /** Belial, the sixty-eighth spirit: the fair angel, then the hollow; his likenesses are of his own kind (sent to clients often, as he flies). */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Belial>> BELIAL = TYPES.register("belial",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Belial::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.demon.BelialRules.WIDTH, com.miophas.corruption.core.demon.BelialRules.HEIGHT).eyeHeight(3.1F)
                    .fireImmune().clientTrackingRange(12).updateInterval(2).build("corruption:belial"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.Amdusias>> AMDUSIAS = TYPES.register("amdusias",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.Amdusias::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.demon.AmdusiasRules.WIDTH, com.miophas.corruption.core.demon.AmdusiasRules.HEIGHT).eyeHeight(2.9F)
                    .fireImmune().clientTrackingRange(12).updateInterval(2).build("corruption:amdusias"));
    /** His chariot of fire: no creature, it runs its line and is gone (its clients run the line themselves, from what it sends as it appears). */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.FieryChariot>> FIERY_CHARIOT = TYPES.register("fiery_chariot",
            () -> EntityType.Builder.<com.miophas.corruption.common.entity.FieryChariot>of(com.miophas.corruption.common.entity.FieryChariot::new, MobCategory.MISC)
                    .sized(com.miophas.corruption.common.entity.FieryChariot.WIDTH, com.miophas.corruption.common.entity.FieryChariot.HEIGHT).fireImmune()
                    .clientTrackingRange(10).updateInterval(20).build("corruption:fiery_chariot"));
    /** The corrupted aberration, the land's elite: a low mass of flesh on six legs (sent to clients often, for its pounce). */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedAberration>> CORRUPTED_ABERRATION = TYPES.register("corrupted_aberration",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedAberration::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.AberrationRules.WIDTH, com.miophas.corruption.core.elite.AberrationRules.HEIGHT).eyeHeight(.9F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_aberration"));
    /** The corrupted screecher, the air's elite: a flayed bird (sent to clients often, as it flies). */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedScreecher>> CORRUPTED_SCREECHER = TYPES.register("corrupted_screecher",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedScreecher::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.ScreecherRules.WIDTH, com.miophas.corruption.core.elite.ScreecherRules.HEIGHT).eyeHeight(.65F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_screecher"));
    /** The corrupted lurker, the sea's elite: a tentacled horror (sent to clients often, as it darts). */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedLurker>> CORRUPTED_LURKER = TYPES.register("corrupted_lurker",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedLurker::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.LurkerRules.WIDTH, com.miophas.corruption.core.elite.LurkerRules.HEIGHT).eyeHeight(.55F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_lurker"));
    /** The corrupted brute, an elite: a hunched mountain of flesh with a club arm. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedBrute>> CORRUPTED_BRUTE = TYPES.register("corrupted_brute",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedBrute::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.BruteRules.WIDTH, com.miophas.corruption.core.elite.BruteRules.HEIGHT).eyeHeight(2.0F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_brute"));
    /** The corrupted howler, an elite: a gaunt giant with a split throat. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedHowler>> CORRUPTED_HOWLER = TYPES.register("corrupted_howler",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedHowler::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.HowlerRules.WIDTH, com.miophas.corruption.core.elite.HowlerRules.HEIGHT).eyeHeight(2.3F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_howler"));
    /** The corrupted stalker, an elite: a bladed hunter that fades from sight. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedStalker>> CORRUPTED_STALKER = TYPES.register("corrupted_stalker",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedStalker::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.StalkerRules.WIDTH, com.miophas.corruption.core.elite.StalkerRules.HEIGHT).eyeHeight(1.9F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_stalker"));
    /** The corrupted bloater, an elite: a walking sac of gas. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedBloater>> CORRUPTED_BLOATER = TYPES.register("corrupted_bloater",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedBloater::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.BloaterRules.WIDTH, com.miophas.corruption.core.elite.BloaterRules.HEIGHT).eyeHeight(1.0F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_bloater"));
    /** The corrupted mortar, an elite: a plated beast with a tube of flesh on its back. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedMortar>> CORRUPTED_MORTAR = TYPES.register("corrupted_mortar",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedMortar::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.MortarRules.WIDTH, com.miophas.corruption.core.elite.MortarRules.HEIGHT).eyeHeight(1.0F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_mortar"));
    /** The corrupted crawler, an elite: a segmented thing that climbs walls. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedCrawler>> CORRUPTED_CRAWLER = TYPES.register("corrupted_crawler",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedCrawler::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.CrawlerRules.WIDTH, com.miophas.corruption.core.elite.CrawlerRules.HEIGHT).eyeHeight(0.5F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_crawler"));
    /** The corrupted gazer, an elite: a great eye ringed with lesser eyes. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedGazer>> CORRUPTED_GAZER = TYPES.register("corrupted_gazer",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedGazer::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.GazerRules.WIDTH, com.miophas.corruption.core.elite.GazerRules.HEIGHT).eyeHeight(0.6F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_gazer"));
    /** The corrupted hivemother, an elite: a flying hive of flesh. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedHivemother>> CORRUPTED_HIVEMOTHER = TYPES.register("corrupted_hivemother",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedHivemother::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.HivemotherRules.WIDTH, com.miophas.corruption.core.elite.HivemotherRules.HEIGHT).eyeHeight(0.6F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_hivemother"));
    /** The corrupted drifter, an elite: a drifting bell of membrane. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedDrifter>> CORRUPTED_DRIFTER = TYPES.register("corrupted_drifter",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedDrifter::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.DrifterRules.WIDTH, com.miophas.corruption.core.elite.DrifterRules.HEIGHT).eyeHeight(1.0F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_drifter"));
    /** The corrupted maw, an elite: an eel that is all mouth. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedMaw>> CORRUPTED_MAW = TYPES.register("corrupted_maw",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedMaw::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.MawRules.WIDTH, com.miophas.corruption.core.elite.MawRules.HEIGHT).eyeHeight(0.8F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_maw"));
    /** The corrupted naiad, an elite: a drowned spear-bearer with a finned tail. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedNaiad>> CORRUPTED_NAIAD = TYPES.register("corrupted_naiad",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedNaiad::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.NaiadRules.WIDTH, com.miophas.corruption.core.elite.NaiadRules.HEIGHT).eyeHeight(1.7F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_naiad"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedMindweaver>> CORRUPTED_MINDWEAVER = TYPES.register("corrupted_mindweaver",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedMindweaver::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.MindweaverRules.WIDTH, com.miophas.corruption.core.elite.MindweaverRules.HEIGHT).eyeHeight(1.9F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_mindweaver"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedBlighthound>> CORRUPTED_BLIGHTHOUND = TYPES.register("corrupted_blighthound",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedBlighthound::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.BlighthoundRules.WIDTH, com.miophas.corruption.core.elite.BlighthoundRules.HEIGHT).eyeHeight(0.8F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_blighthound"));
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.CorruptedSower>> CORRUPTED_SOWER = TYPES.register("corrupted_sower",
            () -> EntityType.Builder.of(com.miophas.corruption.common.entity.CorruptedSower::new, MobCategory.MONSTER)
                    .sized(com.miophas.corruption.core.elite.SowerRules.WIDTH, com.miophas.corruption.core.elite.SowerRules.HEIGHT).eyeHeight(1.1F)
                    .clientTrackingRange(10).updateInterval(2).build("corruption:corrupted_sower"));
    /** A demon's bolt of the corruption (Decarabia's volley and barrage), sent every tick so that a stream of them stays smooth. */
    public static final DeferredHolder<EntityType<?>, EntityType<com.miophas.corruption.common.entity.DemonBolt>> DEMON_BOLT = TYPES.register("demon_bolt",
            () -> EntityType.Builder.<com.miophas.corruption.common.entity.DemonBolt>of(com.miophas.corruption.common.entity.DemonBolt::new, MobCategory.MISC)
                    .sized((float) com.miophas.corruption.common.entity.DemonBolt.SIZE, (float) com.miophas.corruption.common.entity.DemonBolt.SIZE)
                    .clientTrackingRange(8).updateInterval(1).build("corruption:demon_bolt"));

    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(CORRUPTED_REMNANT.get(), CorruptedRemnant.attributes().build());
        event.put(CORRUPTED_DRAGON.get(), CorruptedRemnant.attributes().build());
        event.put(MALICE_VINE.get(), com.miophas.corruption.common.entity.MaliceVine.attributes().build());
        event.put(GREED.get(), com.miophas.corruption.common.entity.Greed.attributes().build());
        event.put(BAAL.get(), com.miophas.corruption.common.entity.Baal.attributes().build());
        event.put(AGARES.get(), com.miophas.corruption.common.entity.Agares.attributes().build());
        event.put(ANDROMALIUS.get(), com.miophas.corruption.common.entity.Andromalius.attributes().build());
        event.put(DANTALION.get(), com.miophas.corruption.common.entity.Dantalion.attributes().build());
        event.put(SEERE.get(), com.miophas.corruption.common.entity.Seere.attributes().build());
        event.put(DECARABIA.get(), com.miophas.corruption.common.entity.Decarabia.attributes().build());
        event.put(BELIAL.get(), com.miophas.corruption.common.entity.Belial.attributes().build());
        event.put(AMDUSIAS.get(), com.miophas.corruption.common.entity.Amdusias.attributes().build());
        event.put(CORRUPTED_ABERRATION.get(), com.miophas.corruption.common.entity.CorruptedAberration.attributes().build());
        event.put(CORRUPTED_SCREECHER.get(), com.miophas.corruption.common.entity.CorruptedScreecher.attributes().build());
        event.put(CORRUPTED_LURKER.get(), com.miophas.corruption.common.entity.CorruptedLurker.attributes().build());
        event.put(CORRUPTED_BRUTE.get(), com.miophas.corruption.common.entity.CorruptedBrute.attributes().build());
        event.put(CORRUPTED_HOWLER.get(), com.miophas.corruption.common.entity.CorruptedHowler.attributes().build());
        event.put(CORRUPTED_STALKER.get(), com.miophas.corruption.common.entity.CorruptedStalker.attributes().build());
        event.put(CORRUPTED_BLOATER.get(), com.miophas.corruption.common.entity.CorruptedBloater.attributes().build());
        event.put(CORRUPTED_MORTAR.get(), com.miophas.corruption.common.entity.CorruptedMortar.attributes().build());
        event.put(CORRUPTED_CRAWLER.get(), com.miophas.corruption.common.entity.CorruptedCrawler.attributes().build());
        event.put(CORRUPTED_GAZER.get(), com.miophas.corruption.common.entity.CorruptedGazer.attributes().build());
        event.put(CORRUPTED_HIVEMOTHER.get(), com.miophas.corruption.common.entity.CorruptedHivemother.attributes().build());
        event.put(CORRUPTED_DRIFTER.get(), com.miophas.corruption.common.entity.CorruptedDrifter.attributes().build());
        event.put(CORRUPTED_MAW.get(), com.miophas.corruption.common.entity.CorruptedMaw.attributes().build());
        event.put(CORRUPTED_NAIAD.get(), com.miophas.corruption.common.entity.CorruptedNaiad.attributes().build());
        event.put(CORRUPTED_MINDWEAVER.get(), com.miophas.corruption.common.entity.CorruptedMindweaver.attributes().build());
        event.put(CORRUPTED_BLIGHTHOUND.get(), com.miophas.corruption.common.entity.CorruptedBlighthound.attributes().build());
        event.put(CORRUPTED_SOWER.get(), com.miophas.corruption.common.entity.CorruptedSower.attributes().build());
    }

    private ModEntities() {}
}
