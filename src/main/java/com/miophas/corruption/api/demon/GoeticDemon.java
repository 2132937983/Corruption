package com.miophas.corruption.api.demon;

import com.miophas.corruption.api.arcane.ArcaneDamage;
import com.miophas.corruption.api.arcane.ArcaneWeapons;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.CorruptionVeilApi;
import com.miophas.corruption.core.arcane.SpellAction;
import com.miophas.corruption.core.demon.BodyPart;
import java.util.List;
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
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

/**
 * A spirit of the Goetia called up at the altar: a demon god, and a boss. What makes one is here, so that every
 * one of them is alike in it:
 *
 * <ul>
 *   <li><b>Only the arcane can hurt it.</b> Blades, arrows, fire, poison, potions and the rest pass through it;
 *       only a hurt with the arcane's mark on it ({@link ArcaneDamage#isSpell}: a spell's own damage, or any
 *       damage type in the tag corruption:spell, which data packs may add to) counts. What passes every defence
 *       (/kill, the void) still does. A player whose blow passes through is told so. A server may turn this off
 *       ({@link DemonServices#arcaneOnly}, [demons] arcaneOnly in its gameplay settings): then any hurt reaches it.</li>
 *   <li>It shows a boss bar in the mod's own style ({@link DemonBoss}), kept up to date with its health.</li>
 *   <li>It never despawns, takes no fall damage, cannot be leashed, and rides nothing (a boat or a minecart would
 *       only hold it fast).</li>
 *   <li>It is of the corruption's side (give its type the tag corruption:corrupted_remnants): the corruption's
 *       own are its allies, and everything else is its prey, players first ({@link #huntTheUncorrupted}).</li>
 *   <li>Its body may be made of hitboxes ({@link BodyPart}s) that follow it as it turns, as a long body's should: then
 *       blows land on those, each a blow on it, and its own box only stands for it in the world.</li>
 *   <li>It dies as a spectacle ({@link #death()}, see {@link BossDeath}): an ordinary one in the corruption's colours,
 *       unless it says otherwise. Its body is gone at the end of its death, its bar put away; it lingers unseen while
 *       what its death set going plays out.</li>
 * </ul>
 *
 * A demon of an add-on's is made by extending this class; its own rules decide everything else.
 */
public abstract class GoeticDemon extends Monster implements DemonBoss {
    private static final EntityDataAccessor<Optional<UUID>> BOSS_BAR = SynchedEntityData.defineId(GoeticDemon.class, EntityDataSerializers.OPTIONAL_UUID);
    /** How often a player is reminded that their blows pass through, in ticks, at the most. */
    private static final int REMINDER_TICKS = 60;

    private final ServerBossEvent boss;
    private final Part[] parts;
    private int remindedAt = -REMINDER_TICKS;

    protected GoeticDemon(EntityType<? extends GoeticDemon> type, Level level, BossEvent.BossBarColor colour) { this(type, level, colour, List.of()); }

    /** A demon whose body is made of these hitboxes. */
    protected GoeticDemon(EntityType<? extends GoeticDemon> type, Level level, BossEvent.BossBarColor colour, List<BodyPart> body) {
        super(type, level);
        boss = new ServerBossEvent(getDisplayName(), colour, BossEvent.BossBarOverlay.NOTCHED_10);
        parts = body.stream().map(part -> new Part(this, part)).toArray(Part[]::new);
        // Its parts take the ids after its own, on server and client alike.
        if (parts.length > 0) setId(ENTITY_COUNTER.getAndAdd(parts.length + 1) + 1);
        setPersistenceRequired();
        if (!level.isClientSide) entityData.set(BOSS_BAR, Optional.of(boss.getId()));
    }

    /**
     * Turns it on whatever is not of the corruption, as the corruption's remnants are: players first (at this
     * priority), and failing them anything living in sight that is not of the corruption, villagers, beasts and
     * other monsters alike. What is of the corruption is its ally, and never its prey.
     */
    protected void huntTheUncorrupted(int priority) {
        targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(priority + 1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, prey -> !CorruptionFactionApi.isRemnant(prey)));
    }

    /**
     * As {@link #huntTheUncorrupted}, for one that hangs high over its prey: it looks for prey as far below and above it as
     * about it. (A creature looks for other creatures only four blocks up or down; for players, as far as it can see.)
     */
    protected void huntTheUncorruptedFromOnHigh(int priority) {
        targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(priority + 1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, prey -> !CorruptionFactionApi.isRemnant(prey)) {
            @Override protected net.minecraft.world.phys.AABB getTargetSearchArea(double range) { return mob.getBoundingBox().inflate(range); }
        });
    }

    /** Its boss bar, for a demon to set how it changes the world about those who see it (its fog, its darkened sky). */
    public ServerBossEvent bossEvent() { return boss; }

    /** Its own side is its allies, but for one marked as its prey, or hunting it (see PreyMarks). */
    @Override public boolean isAlliedTo(Entity entity) {
        return CorruptionFactionApi.isRemnant(entity) && !DemonServices.feuding(this, entity) || super.isAlliedTo(entity);
    }
    /** Nothing of its own side, nor anything veiled from it by a corrupted robe (CorruptionVeilApi): it does not even turn on what it cannot see for what it is. */
    @Override public boolean canAttack(LivingEntity target) {
        return !isAlliedTo(target) && !CorruptionVeilApi.hiddenFrom(target, this) && super.canAttack(target);
    }

    /**
     * Whether a hurt reaches a demon: one with the arcane's mark on it (a spell's, or a blow struck by hand with a blade
     * that bears the mark, see ArcaneWeapons), or one that passes every defence; or any at all, where the server has
     * said that the demons need not be hurt by the arcane ({@link DemonServices#arcaneOnly}).
     */
    public static boolean accepts(DamageSource source) {
        return !DemonServices.arcaneOnly() || ArcaneDamage.isSpell(source) || ArcaneWeapons.isArcaneBlow(source) || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
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
        boss.setProgress(barProgress());
    }

    /** How full its bar is: its own health, as a share of its greatest; a demon whose life is elsewhere says otherwise. */
    protected float barProgress() { return getHealth() / getMaxHealth(); }

    @Override public Optional<UUID> bossBar() { return entityData.get(BOSS_BAR); }

    // ---- its body's hitboxes ----

    @Override public void setId(int id) {
        super.setId(id);
        if (parts != null) for (int index = 0; index < parts.length; index++) parts[index].setId(id + index + 1);
    }
    @Override public boolean isMultipartEntity() { return parts.length > 0; }
    @Override public PartEntity<?>[] getParts() { return parts; }
    /** With a body of parts, blows land on those; its own box is not there to be struck at. */
    @Override public boolean isPickable() { return parts.length == 0 && exposed() && super.isPickable(); }

    /** Whether its body is there to be struck at at all; a demon of its own may hide it away (under the earth, say). */
    protected boolean exposed() { return true; }

    /** Its parts follow it, on server and client alike, every tick. */
    @Override public void tick() {
        super.tick();
        float yaw = yBodyRot * Mth.DEG_TO_RAD, sin = Mth.sin(yaw), cos = Mth.cos(yaw);
        for (Part part : parts) {
            BodyPart at = part.definition;
            double x = part.getX(), y = part.getY(), z = part.getZ();
            part.setPos(getX() + at.left() * cos - at.forward() * sin, getY() + at.up(), getZ() + at.left() * sin + at.forward() * cos);
            part.xo = x; part.yo = y; part.zo = z; part.xOld = x; part.yOld = y; part.zOld = z;
        }
    }

    /** An ordinary death, as big as it is tall, in the corruption's colours; a demon of its own says otherwise. */
    @Override public BossDeath death() { return BossDeath.ordinary(getBbHeight(), SpellAction.Visual.ROSE, SpellAction.Visual.GOLD); }

    /** A little over halfway up its body. */
    @Override public Vec3 heart(float partial) { return getPosition(partial).add(0, getBbHeight() * .6, 0); }

    @Override public void die(DamageSource source) {
        if (!level().isClientSide && !isRemoved() && !dead && barShown()) DemonBoss.logDeath(this, source);
        super.die(source);
    }

    /** It dies as {@link #death()} says: sounded as it goes, its body gone at the end, then taken away once the rest has played out. */
    @Override protected void tickDeath() {
        BossDeath death = death();
        deathTime++;
        if (!(level() instanceof ServerLevel level) || isRemoved()) return;
        death.sound(level, this, heart(0), deathTime);
        if (deathTime == death.ticks()) {
            boss.setVisible(false);
            gone(level);
        }
        if (deathTime >= death.lasts()) remove(RemovalReason.KILLED);
    }

    /** Its body is gone, in the last burst of its light: a puff of smoke where it was, as any creature leaves. */
    protected void gone(ServerLevel level) { level.broadcastEntityEvent(this, (byte) 60); }

    /** Dying, it is drawn wherever any of its death could be seen, not only where its body is. */
    @Override public AABB getBoundingBoxForCulling() {
        if (deathTime <= 0) return super.getBoundingBoxForCulling();
        double reach = death().reach();
        return new AABB(getX() - reach, getY() - 2, getZ() - reach, getX() + reach, getY() + reach, getZ() + reach);
    }

    @Override public boolean removeWhenFarAway(double distance) { return false; }
    /** It rides nothing, not even when told to: a boat, a minecart or a creature would only hold it fast. */
    @Override public boolean startRiding(Entity vehicle, boolean force) { return false; }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override public boolean canBeLeashed() { return false; }

    /** Whether it shows its boss bar to those who see it: every demon does, but one that is only a likeness of another. */
    protected boolean barShown() { return true; }

    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); if (barShown()) boss.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); boss.removePlayer(player); }
    @Override public void setCustomName(Component name) { super.setCustomName(name); boss.setName(getDisplayName()); }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (hasCustomName()) boss.setName(getDisplayName());
    }

    /** One of its body's hitboxes. It has no life of its own: a blow on it is a blow on the demon, as the demon takes it. */
    public static final class Part extends PartEntity<GoeticDemon> {
        private final BodyPart definition;
        private final EntityDimensions size;

        Part(GoeticDemon demon, BodyPart definition) {
            super(demon);
            this.definition = definition;
            size = EntityDimensions.scalable((float) definition.width(), (float) definition.height());
            refreshDimensions();
        }

        public BodyPart definition() { return definition; }
        @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
        @Override protected void readAdditionalSaveData(CompoundTag tag) {}
        @Override protected void addAdditionalSaveData(CompoundTag tag) {}
        @Override public boolean shouldBeSaved() { return false; }
        /** Only while it lives and shows: a dying demon, or one hidden away, is nothing to strike at. */
        @Override public boolean isPickable() { return getParent().isAlive() && getParent().exposed(); }
        @Override public boolean is(Entity entity) { return this == entity || getParent() == entity; }
        @Override public EntityDimensions getDimensions(Pose pose) { return size == null ? super.getDimensions(pose) : size; }
        @Override public boolean hurt(DamageSource source, float amount) { return !isInvulnerableTo(source) && getParent().hurt(source, amount); }
    }
}
