package com.miophas.corruption.common.arcane;

import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.common.registry.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import com.miophas.corruption.api.arcane.ArcaneDamage;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.entity.SpellBeamEntity;
import com.miophas.corruption.common.entity.SpellProjectile;
import com.miophas.corruption.common.entity.SpellVisualEntity;
import com.miophas.corruption.common.block.CorruptionBlock;
import com.miophas.corruption.common.block.StaticCorruptedBlock;
import com.miophas.corruption.common.entity.CorruptedRemnant;
import com.miophas.corruption.common.entity.DemonBoss;
import com.miophas.corruption.common.infection.CorruptionConversions;
import com.miophas.corruption.common.infection.CorruptionOreConversions;
import com.miophas.corruption.common.infection.ServerInfectionWorld;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModTags;
import com.miophas.corruption.core.arcane.SpellAction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;

/**
 * What a spell's actions are worth in the world: this is where a rule's plain kinds become effects. Which
 * effects a spell has is data ({@code corruption:arcane_spell}); this only says what each kind does, round the
 * origin it is given: the caster, cast by hand, or where a bolt struck. The caster is never caught in their own
 * spell's harm.
 *
 * <p>It also reads and raises the caster's corruption, and is fitted to {@link ArcaneCasting} at startup, so
 * that any casting tool, the mod's or an add-on's, casts through it.
 */
public final class SpellEffects implements ArcaneCasting.World {
    /** How many blocks one cast may corrupt at most, so a wide spell cannot cost the server dear. */
    private static final int MAX_CORRUPTED_BLOCKS = 64;
    /** How near where lightning falls a creature must be to be hurt by it, beyond the hurt lightning itself does. */
    private static final double STRIKE_REACH = 1.75;
    /** How loud a spell's effect sounds: as loud as thunder, heard a long way off. */
    private static final float VISUAL_VOLUME = 4.0F;
    /** How far above and below the origin a strike looks for ground to land on. */
    private static final int STRIKE_ABOVE = 2, STRIKE_BELOW = 12;
    /** How many places round the origin a summoned creature is tried at before it is set down at the origin itself. */
    private static final int SUMMON_TRIES = 8;
    /** How many blocks one cast may still at most. */
    private static final int MAX_QUELLED_BLOCKS = 128;

    /** Fits this as the world spells are carried out through. */
    public static void install() { ArcaneCasting.install(new SpellEffects()); }

    @Override public void perform(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction action) {
        switch (action) {
            case SpellAction.ApplyEffect effect -> applyEffect(caster, origin, effect);
            case SpellAction.Damage damage -> damage(caster, origin, damage);
            case SpellAction.Corrupt corrupt -> corrupt(caster, origin, corrupt);
            case SpellAction.CorruptCreatures turn -> corruptCreatures(caster, origin, turn);
            case SpellAction.Freeze freeze -> freeze(caster, origin, freeze);
            case SpellAction.Summon summon -> summon(caster, origin, summon);
            case SpellAction.Ignite ignite -> ignite(caster, origin, ignite);
            case SpellAction.Lightning lightning -> lightning(caster, origin, lightning);
            case SpellAction.Explode explode -> explode(caster, origin, explode);
            case SpellAction.Teleport teleport -> teleport(caster, origin, teleport);
            case SpellAction.Weather weather -> weather(caster, origin, weather);
            case SpellAction.Pacify pacify -> pacify(caster, origin, pacify);
            case SpellAction.GiveItem give -> give(caster, origin, give);
            case SpellAction.Push push -> push(caster, origin, push);
            case SpellAction.Spectacle spectacle -> show(caster, origin, spectacle);
            case SpellAction.Visual visual -> visual(caster, origin, visual);
            case SpellAction.LaunchBolt bolt -> launch(caster, origin, bolt);
            case SpellAction.Beam beam -> beam(caster, beam);
            case SpellAction.Quell quell -> quell(caster, origin, quell);
            case SpellAction.RaiseRemnants raise -> raiseRemnants(caster, origin, raise);
            case SpellAction.MarkPrey mark -> markPrey(caster, origin, mark);
            case SpellAction.Truce truce -> truce(caster, truce);
        }
    }

    @Override public int corruption(ServerPlayer caster) { return PlayerCorruption.value(caster); }

    /**
     * Casting leaves the caster more corrupted, as the corruption's own curse does (and what they wear may hold some
     * of it back: see PlayerCorruption.raise), but never in creative or spectator play.
     */
    @Override public void corrupt(ServerPlayer caster, int gain) {
        if (gain <= 0 || caster.isCreative() || caster.isSpectator()) return;
        PlayerCorruption.raise(caster, gain);
    }

    /** Gives a mob effect to the caster alone, or to everything near the origin. */
    private void applyEffect(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.ApplyEffect action) {
        Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(action.effect())).orElse(null);
        if (effect == null) return;
        if (action.target() == SpellAction.Target.SELF) { caster.addEffect(new MobEffectInstance(effect, action.duration(), action.amplifier())); return; }
        for (LivingEntity target : nearby(caster, origin, action.radius())) target.addEffect(new MobEffectInstance(effect, action.duration(), action.amplifier()));
    }

    /**
     * Hurts everything near the origin, with the mark of a spell on it: what falls to the arcane alone can tell
     * this hurt from any other (see {@link ArcaneDamage}).
     */
    private void damage(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Damage action) {
        DamageSource hurt = ArcaneDamage.source(caster.serverLevel(), caster, action.type());
        for (LivingEntity target : nearby(caster, origin, action.radius()))
            target.hurt(hurt, (float) action.amount());
    }

    /**
     * Corrupts the blocks round the origin, as the infection does, each of them this much of the time: the
     * nearest first, so that a spell cut short by its cap spreads evenly from the middle. Dormant, it leaves only the
     * dormant corruption ({@link #blight}).
     */
    private void corrupt(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Corrupt action) {
        ServerLevel level = caster.serverLevel();
        ServerInfectionWorld world = new ServerInfectionWorld(level);
        int radius = (int) Math.max(0, Math.min(SpellAction.MAX_CORRUPTION_RADIUS, Math.round(action.radius())));
        double chance = Math.max(0, Math.min(1, action.chance()));
        int corrupted = 0;
        for (BlockPos at : nearestFirst(BlockPos.containing(origin.at()), radius, level.random)) {
            if (corrupted >= MAX_CORRUPTED_BLOCKS) break;
            if (level.random.nextDouble() >= chance) continue;
            if (action.inert() ? blight(level, at) : world.tryInfect(at)) corrupted++;
        }
    }

    /**
     * Turns a block to the dormant corruption, if the infection could take it and it is a whole, solid block of no
     * ore and no block entity: into that and nothing else (never malice, nor the corruption that spreads), with
     * nothing growing on it. Whether it did.
     */
    static boolean blight(ServerLevel level, BlockPos at) { return com.miophas.corruption.common.infection.GroundBlight.dormant(level, at); }

    /** Whether this is a whole, solid block: no liquid, no block entity, a full cube that hides what is behind it. */
    private static boolean whole(BlockGetter level, BlockPos at, BlockState state) { return com.miophas.corruption.common.infection.GroundBlight.whole(level, at, state); }

    /**
     * Stills the corruption round the origin, the nearest first, as much as one cast may: every whole block of it that
     * spreads (the corruption itself, malice, and the solid blocks it has taken; not its ores) becomes the dormant
     * corruption, which spreads no further.
     */
    private void quell(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Quell action) {
        ServerLevel level = caster.serverLevel();
        int radius = (int) Math.max(0, Math.min(SpellAction.MAX_CORRUPTION_RADIUS, Math.round(action.radius())));
        int stilled = 0;
        for (BlockPos at : nearestFirst(BlockPos.containing(origin.at()), radius, level.random)) {
            if (stilled >= MAX_QUELLED_BLOCKS) break;
            if (level.isLoaded(at) && stillable(level, at, level.getBlockState(at))
                    && level.setBlock(at, ModBlocks.DEACTIVATED_CORRUPTION.get().defaultBlockState(), Block.UPDATE_ALL)) stilled++;
        }
    }

    /** Whether quelling stills this block: a whole block of the corruption that spreads, the corruption, malice or a block it took. */
    static boolean stillable(BlockGetter level, BlockPos at, BlockState state) {
        return state.getBlock() instanceof CorruptionBlock corruption && corruption.spreads() && whole(level, at, state)
                && (state.is(ModBlocks.CORRUPTION.get()) || state.is(ModBlocks.MALICE.get()) || corruption instanceof StaticCorruptedBlock);
    }

    /** Every place within a sphere of this radius, the nearest first; those as near as one another, in no set order. */
    static List<BlockPos> nearestFirst(BlockPos middle, int radius, RandomSource random) {
        List<BlockPos> places = new ArrayList<>();
        for (int across = -radius; across <= radius; across++)
            for (int up = -radius; up <= radius; up++)
                for (int along = -radius; along <= radius; along++)
                    if (across * across + up * up + along * along <= radius * radius) places.add(middle.offset(across, up, along));
        Util.shuffle(places, random);
        places.sort(Comparator.comparingDouble(at -> at.distSqr(middle)));
        return places;
    }

    /** Chills everything near the origin, and stings it with the cold. */
    private void freeze(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Freeze action) {
        for (LivingEntity target : nearby(caster, origin, action.radius())) {
            target.setTicksFrozen(target.getTicksFrozen() + action.ticks());
            target.hurt(caster.damageSources().freeze(), 1.0F);
        }
    }

    /**
     * Brings forth the creatures asked for, scattered round the origin where there is room for them, as the game
     * brings forth any creature, armed and dressed. They are the caster's: they never turn on them (see
     * {@link SpellSummons}).
     */
    private void summon(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Summon action) {
        EntityType<?> type = EntityType.byString(action.entity()).orElse(null);
        if (type == null || !type.is(ModTags.SPELL_SUMMONS)) return;
        ServerLevel level = caster.serverLevel();
        for (int brought = 0; brought < action.count(); brought++) {
            Entity created = type.create(level);
            if (created == null) continue;
            setDown(level, created, origin, caster.getYRot());
            if (created instanceof Mob mob) {
                EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                SpellSummons.bind(mob, caster);
            }
            level.addFreshEntity(created);
        }
    }

    /**
     * Raises a band of remnants round the origin, set evenly round it, each in the next likeness the spell names (armed
     * and dressed as such creatures come): the caster's, never turning on them (see {@link SpellSummons}), and
     * crumbling away when their time is up. A likeness a spell may not turn a creature into (a boss, by default) is
     * passed over.
     */
    private void raiseRemnants(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.RaiseRemnants action) {
        ServerLevel level = caster.serverLevel();
        int start = level.random.nextInt(action.likenesses().size());
        double turn = level.random.nextDouble() * Math.PI * 2;
        long crumbles = action.lifetime() > 0 ? level.getGameTime() + action.lifetime() : 0;
        for (int one = 0; one < action.count(); one++) {
            EntityType<?> likeness = EntityType.byString(action.likeness(start + one)).orElse(null);
            if (likeness == null || likeness.is(ModTags.SPELL_CONVERSION_IMMUNE)) continue;
            double angle = turn + one * Math.PI * 2 / action.count();
            Vec3 ring = origin.at().add(Math.cos(angle) * action.radius(), 0, Math.sin(angle) * action.radius());
            Vec3 at = Vec3.atBottomCenterOf(groundAt(level, BlockPos.containing(ring.x, origin.at().y, ring.z)));
            CorruptedRemnant remnant = CorruptedRemnant.raised(level, likeness, at, caster.getYRot());
            if (remnant == null) continue;
            if (!level.noCollision(remnant)) setDown(level, remnant, origin, caster.getYRot());
            SpellSummons.bind(remnant, caster);
            if (crumbles > 0) remnant.crumbleAt(crumbles);
            if (!level.addFreshEntity(remnant)) continue;
            level.sendParticles(ParticleTypes.SOUL, remnant.getX(), remnant.getY() + .2, remnant.getZ(), 10, .4, .2, .4, .03);
            level.sendParticles(ModParticles.CORRUPTION_PARTICLE.get(), remnant.getX(), remnant.getY() + remnant.getBbHeight() / 2, remnant.getZ(), 16,
                    .4, remnant.getBbHeight() / 3, .4, .03);
        }
        level.playSound(null, origin.at().x, origin.at().y, origin.at().z, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.5F, .7F);
    }

    /**
     * Marks the creature a bolt struck (or the nearest to where it struck, within reach) as the corruption's prey: it
     * glows while the mark lasts, and every creature of the corruption, its bosses among them, within the spell's reach
     * of the prey or of the caster is sent after it (see {@link PreyMarks}), but for those the prey itself called up.
     * The caster hears how many answered. The mark does no harm.
     */
    private void markPrey(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.MarkPrey action) {
        LivingEntity prey = origin.struck() instanceof LivingEntity struck && struck != caster && struck.isAlive() ? struck : null;
        if (prey == null) {
            double nearest = Double.MAX_VALUE;
            for (LivingEntity near : nearby(caster, origin, SpellAction.MarkPrey.REACH)) {
                double distance = near.getBoundingBox().distanceToSqr(origin.at());
                if (distance < nearest) { nearest = distance; prey = near; }
            }
        }
        if (prey == null) return;
        ServerLevel level = caster.serverLevel();
        long until = level.getGameTime() + action.ticks();
        prey.addEffect(new MobEffectInstance(MobEffects.GLOWING, action.ticks(), 0, false, false), caster);
        UUID preyId = prey.getUUID();
        Set<Mob> hunters = new LinkedHashSet<>();
        double reach = action.radius();
        for (Vec3 middle : List.of(prey.position(), caster.position()))
            hunters.addAll(level.getEntitiesOfClass(Mob.class, new AABB(middle, middle).inflate(reach), mob -> mob.isAlive() && CorruptionFactionApi.isRemnant(mob)
                    && mob.getBoundingBox().distanceToSqr(middle) <= reach * reach && !preyId.equals(SpellSummons.summoner(mob))));
        LivingEntity marked = prey;
        hunters.removeIf(hunter -> hunter == marked);
        for (Mob hunter : hunters) PreyMarks.hunt(hunter, prey, until);
        caster.displayClientMessage(hunters.isEmpty() ? Component.translatable("message.corruption.prey_unheeded", prey.getDisplayName())
                : Component.translatable("message.corruption.prey_marked", prey.getDisplayName(), hunters.size()), true);
    }

    /**
     * Swears a truce with the corruption round the caster: its creatures within reach let the caster be for the
     * spell's time (see {@link PreyMarks}). Not its bosses, which swear to no one. The caster hears how many swore.
     */
    private void truce(ServerPlayer caster, SpellAction.Truce action) {
        ServerLevel level = caster.serverLevel();
        long until = level.getGameTime() + action.ticks();
        double reach = action.radius();
        Vec3 at = caster.position();
        List<Mob> sworn = level.getEntitiesOfClass(Mob.class, caster.getBoundingBox().inflate(reach), mob -> mob.isAlive() && CorruptionFactionApi.isRemnant(mob)
                && !(mob instanceof DemonBoss) && !mob.getType().is(Tags.EntityTypes.BOSSES) && mob.getBoundingBox().distanceToSqr(at) <= reach * reach);
        for (Mob mob : sworn) PreyMarks.spare(mob, caster, until);
        caster.displayClientMessage(Component.translatable("message.corruption.truce_sworn", sworn.size()), true);
    }

    /** Sets a summoned creature down near the origin, somewhere it fits; failing that, at the origin itself. */
    private static void setDown(ServerLevel level, Entity created, ArcaneCasting.Origin origin, float facing) {
        Vec3 at = origin.at();
        for (int tried = 0; tried < SUMMON_TRIES; tried++) {
            double angle = level.random.nextDouble() * Math.PI * 2, distance = 1.5 + level.random.nextDouble() * 1.5;
            created.moveTo(at.x + Math.cos(angle) * distance, at.y, at.z + Math.sin(angle) * distance, facing, 0.0F);
            if (level.noCollision(created)) return;
        }
        created.moveTo(at.x, at.y, at.z, facing, 0.0F);
        if (!level.noCollision(created)) created.moveTo(origin.launch().x, origin.launch().y, origin.launch().z, facing, 0.0F);
    }

    /** Flies the bolt asked for from where the origin launches bolts, the way it faces. */
    private void launch(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.LaunchBolt action) {
        EntityType<?> type = EntityType.byString(action.entity()).orElse(null);
        if (type == null || !type.is(ModTags.SPELL_BOLTS)) return;
        ServerLevel level = caster.serverLevel();
        for (int one = 0; one < action.count(); one++) {
            if (!(type.create(level) instanceof Projectile bolt)) return;
            if (bolt instanceof SpellProjectile spell) spell.flying(action);
            bolt.setOwner(caster);
            if (action.from() == SpellAction.Source.SKY) {
                Vec3 at = origin.at();
                double angle = level.random.nextDouble() * Math.PI * 2, out = Math.sqrt(level.random.nextDouble()) * action.fan();
                bolt.setPos(at.x + Math.cos(angle) * out, at.y + action.height(), at.z + Math.sin(angle) * out);
                bolt.shoot(0, -1, 0, (float) action.speed(), action.inaccuracy());
            } else {
                Vec3 way = fanned(origin.direction(), one, action.count(), action.fan());
                bolt.setPos(origin.launch());
                bolt.shoot(way.x, way.y, way.z, (float) action.speed(), action.inaccuracy());
            }
            level.addFreshEntity(bolt);
        }
    }

    /** The way one bolt of a volley flies: the gaze turned about the upright by its share of the fan, the first at one edge of it and the last at the other. */
    static Vec3 fanned(Vec3 way, int one, int count, double fan) {
        if (count <= 1 || fan <= 0) return way;
        double degrees = -fan / 2 + fan * one / (count - 1);
        return way.yRot((float) Math.toRadians(degrees));
    }

    /** Everything living within this far of the origin, the caster left out. */
    private static List<LivingEntity> nearby(ServerPlayer caster, ArcaneCasting.Origin origin, double radius) {
        double reach = Math.min(SpellAction.MAX_RADIUS, Math.max(0, radius));
        Vec3 at = origin.at();
        return caster.level().getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(reach),
                target -> target != caster && target.isAlive() && target.getBoundingBox().distanceToSqr(at) <= reach * reach);
    }

    /** Sets everything near the origin alight. */
    private void ignite(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Ignite action) {
        for (LivingEntity target : nearby(caster, origin, action.radius())) target.setRemainingFireTicks(action.seconds() * 20);
    }

    /**
     * Strikes lightning about the origin, each bolt falling on the ground there: where a spell's bolt struck, in a
     * cave or under a roof as much as in the open, not on the surface high above it.
     */
    private void lightning(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Lightning action) {
        ServerLevel level = caster.serverLevel();
        Vec3 at = origin.at();
        double reach = Math.max(0, action.radius());
        for (int strike = 0; strike < action.count(); strike++) {
            double angle = level.random.nextDouble() * Math.PI * 2, out = level.random.nextDouble() * reach;
            BlockPos column = BlockPos.containing(at.x + Math.cos(angle) * out, at.y, at.z + Math.sin(angle) * out);
            Entity bolt = (action.corrupt() ? ModEntities.CORRUPT_LIGHTNING.get() : EntityType.LIGHTNING_BOLT).create(level);
            if (bolt == null) continue;
            Vec3 falls = Vec3.atBottomCenterOf(groundAt(level, column));
            bolt.moveTo(falls);
            level.addFreshEntity(bolt);
            if (action.damage() > 0) {
                DamageSource hurt = ArcaneDamage.source(level, caster, action.damageType());
                Vec3 middle = falls.add(0, 1, 0);
                for (LivingEntity target : nearby(caster, new ArcaneCasting.Origin(middle, middle, Vec3.ZERO), STRIKE_REACH))
                    target.hurt(hurt, (float) action.damage());
            }
        }
    }

    /**
     * Where a strike in this column lands: on the highest ground a little above the origin's height or under it,
     * so that lightning called where a bolt struck a wall, a floor or a creature falls right there. With no ground
     * within reach (over a chasm, say), at the origin's height.
     */
    public static BlockPos groundAt(BlockGetter level, BlockPos column) {
        for (int up = STRIKE_ABOVE; up >= -STRIKE_BELOW; up--) {
            BlockPos at = column.above(up);
            if (!level.getBlockState(at).blocksMotion() && level.getBlockState(at.below()).blocksMotion()) return at;
        }
        return column;
    }

    /**
     * Thrusts everything near the origin away from it, or from the caster if the push says so, or draws it in,
     * lifting it as it goes. What stands right where it is thrust from (the creature a bolt struck) is thrust the
     * way the origin faces, as the bolt flew: on, away from the caster.
     */
    private void push(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Push action) {
        Vec3 from = action.from() == SpellAction.Push.From.CASTER ? caster.position() : origin.at();
        for (LivingEntity target : nearby(caster, origin, action.radius())) {
            Vec3 away = target.position().subtract(from).multiply(1, 0, 1);
            if (away.lengthSqr() < .25) away = origin.direction().multiply(1, 0, 1);
            if (away.lengthSqr() < 1e-4) away = new Vec3(caster.getRandom().nextDouble() - .5, 0, caster.getRandom().nextDouble() - .5);
            away = away.normalize().scale(action.strength());
            target.push(away.x, action.lift(), away.z);
            target.hurtMarked = true;
        }
    }

    /**
     * Sets a spell's effect down on the ground under the origin, for the clients to draw (see
     * {@link SpellVisualEntity}), and sounds it there.
     */
    private void visual(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Visual action) {
        ServerLevel level = caster.serverLevel();
        Vec3 at = origin.at();
        BlockPos ground = groundAt(level, BlockPos.containing(at));
        SpellVisualEntity shown = ModEntities.SPELL_VISUAL.get().create(level);
        if (shown == null) return;
        shown.showing(action, caster.getYRot());
        shown.moveTo(at.x, ground.getY(), at.z, caster.getYRot(), 0);
        level.addFreshEntity(shown);
        sound(level, at, action.sound());
    }

    /**
     * Fires a beam from the caster's hand along their gaze, for the server to pulse and the clients to draw (see
     * {@link SpellBeamEntity}), and sounds it there. A beam fires from the caster whatever the origin, even when a
     * bolt carries it.
     */
    private void beam(ServerPlayer caster, SpellAction.Beam action) {
        ServerLevel level = caster.serverLevel();
        SpellBeamEntity beam = ModEntities.SPELL_BEAM.get().create(level);
        if (beam == null) return;
        beam.firing(caster, action);
        level.addFreshEntity(beam);
        sound(level, beam.position(), action.sound());
    }

    /** Sounds a spell's sight where it shows, loud enough to be heard from afar; "" sounds nothing. */
    private static void sound(ServerLevel level, Vec3 at, String named) {
        if (named.isEmpty()) return;
        ResourceLocation id = ResourceLocation.tryParse(named);
        SoundEvent sound = id == null ? null : BuiltInRegistries.SOUND_EVENT.get(id);
        if (sound != null) level.playSound(null, at.x, at.y, at.z, sound, SoundSource.PLAYERS, VISUAL_VOLUME, 1.0F);
    }

    /** Draws a spell's particles in their shape about the origin: a sight, nothing more. */
    private void show(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Spectacle action) {
        ServerLevel level = caster.serverLevel();
        Vec3 at = origin.at();
        for (SpellAction.Particles particles : action.particles()) {
            ParticleOptions options = SpellProjectile.options(particles);
            if (options == null || particles.count() <= 0) continue;
            if (action.shape() == SpellAction.Shape.BURST) {
                level.sendParticles(options, at.x, at.y, at.z, particles.count(), particles.spread() + action.radius() / 2, particles.spread() + action.radius() / 2,
                        particles.spread() + action.radius() / 2, particles.speed());
                continue;
            }
            for (int point = 0; point < particles.count(); point++) {
                Vec3 offset = shapePoint(action.shape(), action.radius(), action.height(), point, particles.count());
                level.sendParticles(options, at.x + offset.x, at.y + offset.y, at.z + offset.z, 1, particles.spread(), particles.spread(), particles.spread(), particles.speed());
            }
        }
    }

    /**
     * Where one of so many points of a shape lies, from its middle: round a ring at the middle's height; over a
     * sphere, spread evenly; up a column from the middle; or twice round a spiral rising from it.
     */
    static Vec3 shapePoint(SpellAction.Shape shape, double radius, double height, int point, int points) {
        double share = points <= 1 ? 0 : (double) point / (points - 1);
        return switch (shape) {
            case BURST -> Vec3.ZERO;
            case RING -> {
                double angle = Math.PI * 2 * point / Math.max(1, points);
                yield new Vec3(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
            }
            case SPHERE -> {
                double up = 1 - 2 * (point + .5) / Math.max(1, points), across = Math.sqrt(Math.max(0, 1 - up * up));
                double angle = point * Math.PI * (3 - Math.sqrt(5));
                yield new Vec3(Math.cos(angle) * across * radius, up * radius, Math.sin(angle) * across * radius);
            }
            case COLUMN -> new Vec3(0, height * share, 0);
            case SPIRAL -> {
                double angle = Math.PI * 4 * share;
                yield new Vec3(Math.cos(angle) * radius, height * share, Math.sin(angle) * radius);
            }
        };
    }

    /** Bursts about the origin, breaking the ground only if the spell says so, and only as much as it says (see SpellExplosions). */
    private void explode(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Explode action) {
        SpellExplosions.explode(caster, origin.at(), action);
    }

    /** Carries the caster where they are looking, stopping short of whatever is in the way. */
    private void teleport(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Teleport action) {
        ServerLevel level = caster.serverLevel();
        Vec3 way = origin.direction(), from = caster.getEyePosition();
        Vec3 reach = from.add(way.scale(Math.max(0, action.distance())));
        BlockHitResult hit = level.clip(new ClipContext(from, reach, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        Vec3 to = hit.getType() == HitResult.Type.MISS ? reach : hit.getLocation().subtract(way.scale(.5));
        if (!level.noCollision(caster, caster.getBoundingBox().move(to.subtract(caster.position())))) return;
        caster.teleportTo(to.x, to.y, to.z);
        level.playSound(null, to.x, to.y, to.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.2F);
    }

    /** Tells the sky over the caster what to do, for as long as the spell asks. */
    private void weather(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Weather action) {
        ServerLevel level = caster.serverLevel();
        int ticks = Math.max(1, action.ticks());
        switch (action.sky()) {
            case CLEAR -> level.setWeatherParameters(ticks, 0, false, false);
            case RAIN -> level.setWeatherParameters(0, ticks, true, false);
            case THUNDER -> level.setWeatherParameters(0, ticks, true, true);
        }
    }

    /** Quiets everything near the origin, so that it stops fighting and forgets its grudges. */
    private void pacify(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Pacify action) {
        for (LivingEntity target : nearby(caster, origin, action.radius())) {
            target.setLastHurtByMob(null);
            target.setLastHurtMob(null);
            if (target instanceof Mob mob) { mob.setTarget(null); mob.getNavigation().stop(); }
        }
    }

    /**
     * Turns the creatures near the origin into the corruption's remnants: each falls to the corruption at once, by the
     * caster's doing, and rises where it stood in its own likeness, as one the corruption's curse has taken does (see
     * ReanimationEvents). What may not be turned so ({@link #turnable}) is left to the spell's other actions.
     */
    private void corruptCreatures(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.CorruptCreatures action) {
        DamageSource taken = ModDamageTypes.conversion(caster.serverLevel(), caster);
        for (LivingEntity target : nearby(caster, origin, action.radius()))
            if (target instanceof Mob mob && turnable(caster, mob) && caster.getRandom().nextDouble() < action.chance()) mob.hurt(taken, Float.MAX_VALUE);
    }

    /**
     * Whether a spell may turn this creature into a remnant: not if it is of the corruption already, or kept from harm
     * (a map's shopkeeper, say), or the caster's own (tamed by them, or called up by their spells), or of a kind the
     * tag corruption:spell_conversion_immune names. Players are never turned so, being no mobs.
     */
    static boolean turnable(ServerPlayer caster, Mob mob) {
        return !CorruptionFactionApi.isRemnant(mob) && !mob.isInvulnerable() && !mob.getType().is(ModTags.SPELL_CONVERSION_IMMUNE)
                && !(mob instanceof OwnableEntity owned && caster.getUUID().equals(owned.getOwnerUUID()))
                && !caster.getUUID().equals(SpellSummons.summoner(mob));
    }

    /** Puts what the spell gives into the caster's hands, or at their feet if they have no room. */
    private void give(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.GiveItem action) {
        Item item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(action.item())).orElse(null);
        if (item == null) return;
        ItemStack given = new ItemStack(item, Math.max(1, action.count()));
        if (!caster.getInventory().add(given)) caster.drop(given, false);
    }

    private SpellEffects() {}
}
