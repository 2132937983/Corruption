package com.miophas.corruption.common.arcane;

import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.entity.SpellProjectile;
import com.miophas.corruption.common.infection.ServerInfectionWorld;
import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.common.registry.ModTags;
import com.miophas.corruption.core.arcane.SpellAction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
    /** How far a spell reaches at most, whatever its data asks for. */
    private static final double MAX_RADIUS = 12.0;
    /** How far a spell corrupts at most, in blocks a way. */
    private static final int MAX_CORRUPTION_RADIUS = 8;
    /** How many blocks one cast may corrupt at most, so a wide spell cannot cost the server dear. */
    private static final int MAX_CORRUPTED_BLOCKS = 64;
    /** How many places round the origin a summoned creature is tried at before it is set down at the origin itself. */
    private static final int SUMMON_TRIES = 8;

    /** Fits this as the world spells are carried out through. */
    public static void install() { ArcaneCasting.install(new SpellEffects()); }

    @Override public void perform(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction action) {
        switch (action) {
            case SpellAction.ApplyEffect effect -> applyEffect(caster, origin, effect);
            case SpellAction.Damage damage -> damage(caster, origin, damage);
            case SpellAction.Corrupt corrupt -> corrupt(caster, origin, corrupt);
            case SpellAction.Freeze freeze -> freeze(caster, origin, freeze);
            case SpellAction.Summon summon -> summon(caster, origin, summon);
            case SpellAction.LaunchBolt bolt -> launch(caster, origin, bolt);
        }
    }

    @Override public int corruption(ServerPlayer caster) { return PlayerCorruption.value(caster); }

    /** Casting leaves the caster more corrupted, as the corruption's own curse does, but never in creative or spectator play. */
    @Override public void corrupt(ServerPlayer caster, int gain) {
        if (gain <= 0 || caster.isCreative() || caster.isSpectator()) return;
        int before = PlayerCorruption.value(caster), after = PlayerCorruptionApi.increase(before, gain);
        if (after != before) caster.setData(ModAttachments.PLAYER_CORRUPTION, after);
    }

    /** Gives a mob effect to the caster alone, or to everything near the origin. */
    private void applyEffect(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.ApplyEffect action) {
        Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(action.effect())).orElse(null);
        if (effect == null) return;
        if (action.target() == SpellAction.Target.SELF) { caster.addEffect(new MobEffectInstance(effect, action.duration(), action.amplifier())); return; }
        for (LivingEntity target : nearby(caster, origin, action.radius())) target.addEffect(new MobEffectInstance(effect, action.duration(), action.amplifier()));
    }

    /** Hurts everything near the origin. */
    private void damage(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Damage action) {
        for (LivingEntity target : nearby(caster, origin, action.radius()))
            target.hurt(caster.damageSources().indirectMagic(caster, caster), (float) action.amount());
    }

    /**
     * Corrupts the blocks round the origin, as the infection does, each of them this much of the time: the
     * nearest first, so that a spell cut short by its cap spreads evenly from the middle.
     */
    private void corrupt(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction.Corrupt action) {
        ServerLevel level = caster.serverLevel();
        ServerInfectionWorld world = new ServerInfectionWorld(level);
        int radius = (int) Math.max(0, Math.min(MAX_CORRUPTION_RADIUS, Math.round(action.radius())));
        double chance = Math.max(0, Math.min(1, action.chance()));
        int corrupted = 0;
        for (BlockPos at : nearestFirst(BlockPos.containing(origin.at()), radius, level.random)) {
            if (corrupted >= MAX_CORRUPTED_BLOCKS) break;
            if (level.random.nextDouble() >= chance) continue;
            if (world.tryInfect(at)) corrupted++;
        }
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
        if (!(type.create(level) instanceof Projectile bolt)) return;
        if (bolt instanceof SpellProjectile spell) spell.carrying(action.onHit());
        bolt.setOwner(caster);
        bolt.setPos(origin.launch());
        Vec3 way = origin.direction();
        bolt.shoot(way.x, way.y, way.z, (float) action.speed(), action.inaccuracy());
        level.addFreshEntity(bolt);
    }

    /** Everything living within this far of the origin, the caster left out. */
    private static List<LivingEntity> nearby(ServerPlayer caster, ArcaneCasting.Origin origin, double radius) {
        double reach = Math.min(MAX_RADIUS, Math.max(0, radius));
        Vec3 at = origin.at();
        return caster.level().getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(reach),
                target -> target != caster && target.isAlive() && target.getBoundingBox().distanceToSqr(at) <= reach * reach);
    }

    private SpellEffects() {}
}
