package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.arcane.ArcaneDamage;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * A spirit of the Goetia called up at the altar: a demon god, and a boss. What makes one is here, so that every
 * one of them is alike in it:
 *
 * <ul>
 *   <li><b>Only the arcane can hurt it.</b> Blades, arrows, fire, poison, potions and the rest pass through it;
 *       only a hurt with the arcane's mark on it ({@link ArcaneDamage#isSpell}: a spell's own damage, or any
 *       damage type in the tag corruption:spell, which data packs may add to) counts. What passes every defence
 *       (/kill, the void) still does. A player whose blow passes through is told so.</li>
 *   <li>It shows a boss bar in the mod's own style ({@link DemonBoss}), kept up to date with its health.</li>
 *   <li>It never despawns, takes no fall damage and cannot be leashed.</li>
 * </ul>
 *
 * A demon of an add-on's is made by extending this class; its own rules decide everything else.
 */
public abstract class GoeticDemon extends Monster implements DemonBoss {
    private static final EntityDataAccessor<Optional<UUID>> BOSS_BAR = SynchedEntityData.defineId(GoeticDemon.class, EntityDataSerializers.OPTIONAL_UUID);
    /** How often a player is reminded that their blows pass through, in ticks, at the most. */
    private static final int REMINDER_TICKS = 60;

    private final ServerBossEvent boss;
    private int remindedAt = -REMINDER_TICKS;

    protected GoeticDemon(EntityType<? extends GoeticDemon> type, Level level, BossEvent.BossBarColor colour) {
        super(type, level);
        boss = new ServerBossEvent(getDisplayName(), colour, BossEvent.BossBarOverlay.NOTCHED_10);
        setPersistenceRequired();
        if (!level.isClientSide) entityData.set(BOSS_BAR, Optional.of(boss.getId()));
    }

    /** Whether a hurt reaches a demon: one with the arcane's mark on it, or one that passes every defence. */
    public static boolean accepts(DamageSource source) {
        return ArcaneDamage.isSpell(source) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override public boolean isInvulnerableTo(DamageSource source) { return super.isInvulnerableTo(source) || !accepts(source); }

    @Override public boolean hurt(DamageSource source, float amount) {
        if (!accepts(source)) {
            passedThrough(source);
            return false;
        }
        return super.hurt(source, amount);
    }

    /** A blow that did not reach it: it rings off, and a player who struck it is told that only the arcane will do. */
    protected void passedThrough(DamageSource source) {
        if (!(level() instanceof ServerLevel server) || !(source.getEntity() instanceof ServerPlayer player)) return;
        server.playSound(null, getX(), getY(), getZ(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, .4F, 1.8F);
        server.sendParticles(ParticleTypes.WITCH, getX(), getY() + getBbHeight() * .6, getZ(), 8, .6, .6, .6, .05);
        if (tickCount - remindedAt >= REMINDER_TICKS) {
            remindedAt = tickCount;
            player.displayClientMessage(Component.translatable("message.corruption.demon_spell_only", getDisplayName()), true);
        }
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOSS_BAR, Optional.empty());
    }

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        boss.setProgress(getHealth() / getMaxHealth());
    }

    @Override public Optional<UUID> bossBar() { return entityData.get(BOSS_BAR); }

    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override public boolean canBeLeashed() { return false; }

    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); boss.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); boss.removePlayer(player); }
    @Override public void setCustomName(Component name) { super.setCustomName(name); boss.setName(getDisplayName()); }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (hasCustomName()) boss.setName(getDisplayName());
    }
}
