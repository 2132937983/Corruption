package com.miophas.corruption.common.arcane;

import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.api.corruption.PlayerCorruptionApi;
import com.miophas.corruption.common.entity.PlayerCorruption;
import com.miophas.corruption.common.entity.SpellProjectile;
import com.miophas.corruption.common.infection.ServerInfectionWorld;
import com.miophas.corruption.common.registry.ModAttachments;
import com.miophas.corruption.common.registry.ModTags;
import com.miophas.corruption.core.arcane.SpellAction;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;

/**
 * What a spell's actions are worth in the world: this is where a rule's plain kinds become effects. Which
 * effects a spell has is data ({@code corruption:arcane_spell}); this only says what each kind does.
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

    /** Fits this as the world spells are carried out through. */
    public static void install() { ArcaneCasting.install(new SpellEffects()); }

    @Override public void perform(ServerPlayer caster, SpellAction action) {
        switch (action) {
            case SpellAction.ApplyEffect effect -> applyEffect(caster, effect);
            case SpellAction.Damage damage -> damage(caster, damage);
            case SpellAction.Corrupt corrupt -> corrupt(caster, corrupt);
            case SpellAction.Freeze freeze -> freeze(caster, freeze);
            case SpellAction.Summon summon -> summon(caster, summon);
            case SpellAction.LaunchBolt bolt -> launch(caster, bolt);
        }
    }

    @Override public int corruption(ServerPlayer caster) { return PlayerCorruption.value(caster); }

    /** Casting leaves the caster more corrupted, as the corruption's own curse does, but never in creative or spectator play. */
    @Override public void corrupt(ServerPlayer caster, int gain) {
        if (gain <= 0 || caster.isCreative() || caster.isSpectator()) return;
        int raised = Math.min(PlayerCorruptionApi.max(), PlayerCorruption.value(caster) + gain);
        caster.setData(ModAttachments.PLAYER_CORRUPTION, raised);
    }

    /** Gives a mob effect to the caster alone, or to everything near them. */
    private void applyEffect(ServerPlayer caster, SpellAction.ApplyEffect action) {
        Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(action.effect())).orElse(null);
        if (effect == null) return;
        if (action.target() == SpellAction.Target.SELF) { caster.addEffect(new MobEffectInstance(effect, action.duration(), action.amplifier())); return; }
        for (LivingEntity target : nearby(caster, action.radius())) target.addEffect(new MobEffectInstance(effect, action.duration(), action.amplifier()));
    }

    /** Hurts everything near the caster. */
    private void damage(ServerPlayer caster, SpellAction.Damage action) {
        for (LivingEntity target : nearby(caster, action.radius()))
            target.hurt(caster.damageSources().indirectMagic(caster, caster), (float) action.amount());
    }

    /** Corrupts the blocks around the caster, as the infection does, each of them this much of the time. */
    private void corrupt(ServerPlayer caster, SpellAction.Corrupt action) {
        ServerLevel level = caster.serverLevel();
        ServerInfectionWorld world = new ServerInfectionWorld(level);
        int radius = (int) Math.max(0, Math.min(MAX_CORRUPTION_RADIUS, Math.round(action.radius())));
        double chance = Math.max(0, Math.min(1, action.chance()));
        BlockPos origin = caster.blockPosition();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int corrupted = 0;
        for (int across = -radius; across <= radius && corrupted < MAX_CORRUPTED_BLOCKS; across++)
            for (int up = -radius; up <= radius && corrupted < MAX_CORRUPTED_BLOCKS; up++)
                for (int along = -radius; along <= radius && corrupted < MAX_CORRUPTED_BLOCKS; along++) {
                    if (across * across + up * up + along * along > radius * radius) continue;
                    if (level.random.nextDouble() >= chance) continue;
                    if (world.tryInfect(at.set(origin.getX() + across, origin.getY() + up, origin.getZ() + along).immutable())) corrupted++;
                }
    }

    /** Chills everything near the caster, and stings it with the cold. */
    private void freeze(ServerPlayer caster, SpellAction.Freeze action) {
        for (LivingEntity target : nearby(caster, action.radius())) {
            target.setTicksFrozen(target.getTicksFrozen() + action.ticks());
            target.hurt(caster.damageSources().freeze(), 1.0F);
        }
    }

    /** Brings forth the creatures asked for, scattered around the caster. */
    private void summon(ServerPlayer caster, SpellAction.Summon action) {
        EntityType<?> type = EntityType.byString(action.entity()).orElse(null);
        if (type == null || !type.is(ModTags.SPELL_SUMMONS)) return;
        ServerLevel level = caster.serverLevel();
        for (int brought = 0; brought < action.count(); brought++) {
            Entity created = type.create(level);
            if (created == null) continue;
            double angle = level.random.nextDouble() * Math.PI * 2, distance = 1.5 + level.random.nextDouble() * 1.5;
            created.moveTo(caster.getX() + Math.cos(angle) * distance, caster.getY(), caster.getZ() + Math.sin(angle) * distance, caster.getYRot(), 0.0F);
            level.addFreshEntity(created);
        }
    }

    /** Flies the bolt asked for, from the caster's eyes along their gaze. */
    private void launch(ServerPlayer caster, SpellAction.LaunchBolt action) {
        EntityType<?> type = EntityType.byString(action.entity()).orElse(null);
        if (type == null || !type.is(ModTags.SPELL_BOLTS)) return;
        ServerLevel level = caster.serverLevel();
        if (!(type.create(level) instanceof Projectile bolt)) return;
        if (bolt instanceof SpellProjectile spell) spell.carrying(action.onHit());
        bolt.setOwner(caster);
        bolt.setPos(caster.getX(), caster.getEyeY() - 0.1, caster.getZ());
        bolt.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, (float) action.speed(), action.inaccuracy());
        level.addFreshEntity(bolt);
    }

    /** Everything living within this far of the caster, the caster left out. */
    private static List<LivingEntity> nearby(ServerPlayer caster, double radius) {
        AABB area = caster.getBoundingBox().inflate(Math.min(MAX_RADIUS, Math.max(0, radius)));
        return caster.level().getEntitiesOfClass(LivingEntity.class, area, target -> target != caster && target.isAlive());
    }

    private SpellEffects() {}
}
