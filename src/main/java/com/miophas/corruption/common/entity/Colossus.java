package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.ColossusApi;
import com.miophas.corruption.api.corruption.ColossusGait;
import com.miophas.corruption.api.corruption.ColossusLegs;
import com.miophas.corruption.api.corruption.ColossusPart;
import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.common.registry.ModEffects;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoubleUnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

/**
 * What every colossus has in common. They are far too large for the game's own movement and collision, so
 * this does both itself: a colossus follows the ground under its centre instead of colliding with it, its
 * own box is a small stand-in nothing can hit, and its hitboxes are parts that follow its body and its
 * walking legs. What it looks like, how it is built and what it does to its prey is each creature's own.
 */
public abstract class Colossus extends Mob implements Enemy {
    /** How far it has walked, in walk cycles; the legs on both sides are worked out from this. */
    private static final EntityDataAccessor<Float> CYCLE = SynchedEntityData.defineId(Colossus.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> ATTACK = SynchedEntityData.defineId(Colossus.class, EntityDataSerializers.BYTE);
    /** Which boss bar on the client is this creature's, so it can be drawn in the mod's own style. */
    private static final EntityDataAccessor<Optional<UUID>> BOSS_BAR = SynchedEntityData.defineId(Colossus.class, EntityDataSerializers.OPTIONAL_UUID);
    /** What {@link #chooseAttack} answers when there is nothing to start: walk closer, or stand and keep turning. */
    protected static final int WALK = 0, HOLD = -1;
    /** Stepping onto ground this much higher or lower takes it a tick per block, so it rises and sinks instead of jumping. */
    private static final double CLIMB_BLOCKS_PER_TICK = 1;

    private final Part[] parts;
    private final double pixel;
    private final ServerBossEvent boss = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
    private double walked;
    private float cycle, cycleO, walk, walkO;
    private int attackTicks, attackCooldown;

    /** @param blocksPerPixel how large one pixel of its model and of its hitbox list is in the world */
    protected Colossus(EntityType<? extends Colossus> type, Level level, List<ColossusPart> hitboxes, double blocksPerPixel) {
        super(type, level);
        pixel = blocksPerPixel;
        parts = hitboxes.stream().map(part -> new Part(this, part, blocksPerPixel)).toArray(Part[]::new);
        setId(ENTITY_COUNTER.getAndAdd(parts.length + 1) + 1);
        noCulling = true; noPhysics = true; xpReward = 500;
        setNoGravity(true); setPersistenceRequired();
        // It aims its own head; the usual control would keep pulling it back to face forward.
        lookControl = new LookControl(this) { @Override public void tick() {} };
        boss.setDarkenScreen(true);
        if (!level.isClientSide) entityData.set(BOSS_BAR, Optional.of(boss.getId()));
    }

    protected abstract ColossusLegs legs();
    /** Its legs for this state. Which attacks lift a foot or crouch it is the creature's own business. */
    protected abstract ColossusGait gait(float cycle, float walk, int attack, float attackTicks, float collapse);
    protected abstract int deathTicks();
    protected abstract double sightBlocks();
    protected abstract double walkBlocksPerTick();
    protected abstract float turnDegreesPerTick();
    /** It only walks once it faces its target this closely. */
    protected abstract float walkFacingDegrees();
    protected abstract int attackCooldownTicks();
    /** The share of a hit that gets through. */
    protected abstract float damageTaken(boolean weakPart);
    protected abstract int attackDuration(int attack);
    /**
     * What to do about a target this far ahead and to its left, in blocks, when it is this many degrees from
     * facing it: an attack to start, or {@link #WALK} or {@link #HOLD}. An attack is only started once the
     * last one's cooldown has run out; until then it holds.
     */
    protected abstract int chooseAttack(ServerLevel level, LivingEntity target, double forward, double side, float off);
    protected void attackStarted(ServerLevel level, int attack) {}
    /** Called every tick of an attack, with how long it has run. */
    protected abstract void attackTick(ServerLevel level, int attack, int ticks);
    /** A walking foot comes down. */
    protected abstract void footfall(ServerLevel level, boolean left);

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CYCLE, 0F); builder.define(ATTACK, (byte) 0); builder.define(BOSS_BAR, Optional.empty());
    }

    public int attackId() { return entityData.get(ATTACK); }
    public float attackTicks(float partial) { return attackId() == 0 ? 0 : attackTicks + partial; }
    public float cycle(float partial) { return Mth.lerp(partial, cycleO, cycle); }
    public float walk(float partial) { return Mth.lerp(partial, walkO, walk); }
    /** 0 alive to 1 fully sunk, through its death. */
    public float collapse(float partial) { return deathTime <= 0 ? 0 : Math.min(1, (deathTime + partial) / deathTicks()); }
    public Optional<UUID> bossBar() { return entityData.get(BOSS_BAR); }
    protected double blocks(double pixels) { return pixels * pixel; }
    protected ColossusGait gait() { return gait(cycle, walk, attackId(), attackTicks, collapse(0)); }
    protected float walking() { return walk; }

    protected void start(int attack) { entityData.set(ATTACK, (byte) attack); attackTicks = 0; }
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ATTACK.equals(key) && level().isClientSide) attackTicks = 0;
    }

    /** A place given sideways (positive to its left), up and forward of where it stands, in blocks. */
    protected Vec3 local(double side, double up, double forward) {
        float yaw = yBodyRot * Mth.DEG_TO_RAD;
        double sin = Mth.sin(yaw), cos = Mth.cos(yaw);
        return new Vec3(getX() + side * cos - forward * sin, getY() + up, getZ() + side * sin + forward * cos);
    }
    /** The same, given in model pixels. */
    protected Vec3 localPixels(double side, double up, double forward) { return local(blocks(side), blocks(up), blocks(forward)); }

    @Override public void tick() {
        super.tick();
        cycleO = cycle; walkO = walk;
        cycle = entityData.get(CYCLE);
        walk = Mth.approach(walk, cycle != cycleO ? 1 : 0, .05F);
        if (attackId() != 0) attackTicks++;
        // No body control turns it, and the client is never told the body's angle, so both sides take it from the entity's.
        yBodyRot = getYRot();
        ColossusGait gait = gait();
        for (Part part : parts) {
            float[] at = ColossusApi.place(legs(), part.definition, gait);
            double x = part.getX(), y = part.getY(), z = part.getZ();
            part.setPos(localPixels(at[0], at[1], at[2]));
            part.xo = x; part.yo = y; part.zo = z; part.xOld = x; part.yOld = y; part.zOld = z;
        }
    }

    /** It never moves by velocity, so nothing can shove, sink, float or drop it. */
    @Override public void travel(Vec3 input) {}

    protected boolean prey(Entity entity) {
        return entity instanceof LivingEntity living && living != this && living.isAlive() && !living.isSpectator()
                && !CorruptionFactionApi.isRemnant(living) && !(living instanceof Player player && player.getAbilities().invulnerable);
    }

    @Override protected void customServerAiStep() {
        ServerLevel level = (ServerLevel) level();
        boss.setProgress(getHealth() / getMaxHealth());
        if (attackCooldown > 0) attackCooldown--;
        // Players come first; otherwise it keeps after whatever last drew its attention, such as something that hurt it.
        if (tickCount % 20 == 0) {
            Player player = level.getNearestPlayer(getX(), getY(), getZ(), sightBlocks(), this::prey);
            if (player != null && player != getTarget()) setTarget(player);
        }
        if (getTarget() != null && (!prey(getTarget()) || distanceToSqr(getTarget()) > sightBlocks() * sightBlocks())) setTarget(null);
        LivingEntity target = getTarget();
        int attack = attackId();
        if (attack != 0) {
            attackTick(level, attack, attackTicks);
            if (attackTicks >= attackDuration(attack)) { start(0); attackCooldown = attackCooldownTicks(); }
        }
        if (target == null) { lookAhead(); return; }
        lookAt(target);
        if (attack != 0) return;

        Vec3 to = target.position().subtract(position());
        float wanted = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90, before = getYRot();
        float yaw = Mth.approachDegrees(before, wanted, turnDegreesPerTick());
        float off = Math.abs(Mth.wrapDegrees(wanted - yaw));
        double stepped = ColossusApi.cyclesForTurn(legs(), Mth.wrapDegrees(yaw - before));
        setYRot(yaw); yBodyRot = yaw;
        double sin = Mth.sin(yaw * Mth.DEG_TO_RAD), cos = Mth.cos(yaw * Mth.DEG_TO_RAD);

        int choice = chooseAttack(level, target, -to.x * sin + to.z * cos, to.x * cos + to.z * sin, off);
        if (choice > 0) {
            if (attackCooldown == 0) { start(choice); attackStarted(level, choice); }
        } else if (choice == WALK && off <= walkFacingDegrees()) {
            double speed = walkBlocksPerTick(), x = getX() - sin * speed, z = getZ() + cos * speed;
            Double ground = ground(level, x, z);
            if (ground != null) {
                setPos(x, getY() + Mth.clamp(ground - getY(), -CLIMB_BLOCKS_PER_TICK, CLIMB_BLOCKS_PER_TICK), z);
                stepped += ColossusApi.cycles(legs(), speed / pixel);
            }
        }
        if (stepped > 0) {
            int feet = walk > .5F ? ColossusApi.footfalls(walked, walked + stepped) : 0;
            walked += stepped; entityData.set(CYCLE, (float) walked);
            if ((feet & 1) != 0) footfall(level, true);
            if ((feet & 2) != 0) footfall(level, false);
        }
    }

    private void lookAt(LivingEntity target) {
        Vec3 eyes = getEyePosition(), to = target.getEyePosition().subtract(eyes);
        float yaw = Mth.clamp(Mth.wrapDegrees((float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90 - yBodyRot), -50, 50);
        float pitch = Mth.clamp((float) -(Mth.atan2(to.y, to.horizontalDistance()) * Mth.RAD_TO_DEG), -30, 60);
        yHeadRot = Mth.approachDegrees(yHeadRot, yBodyRot + yaw, 1.5F); setXRot(Mth.approachDegrees(getXRot(), pitch, 1));
    }
    private void lookAhead() { yHeadRot = Mth.approachDegrees(yHeadRot, yBodyRot, 1.5F); setXRot(Mth.approachDegrees(getXRot(), 0, 1)); }

    /**
     * The ground to stand on around this spot: the mean of a few samples about as far apart as its feet, so
     * one tree or pit does not jolt it, and under water rather than on it, so it wades. Null where the world
     * is not loaded.
     */
    private Double ground(ServerLevel level, double x, double z) {
        double total = 0, spread = blocks(legs().side());
        int samples = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int[] offset : new int[][]{{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int sampleX = Mth.floor(x + offset[0] * spread), sampleZ = Mth.floor(z + offset[1] * spread);
            if (!level.hasChunk(sampleX >> 4, sampleZ >> 4)) continue;
            pos.set(sampleX, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sampleX, sampleZ) - 1, sampleZ);
            while (pos.getY() > level.getMinBuildHeight() && !level.getFluidState(pos).isEmpty()) pos.move(0, -1, 0);
            total += pos.getY() + 1; samples++;
        }
        return samples == 0 ? null : total / samples;
    }

    /**
     * Hurts and throws everything standing around a place on the ground.
     *
     * @param damage what a blow does this many blocks from its middle; nothing where it answers 0 or less
     * @param struck if given, those already in it are spared and those hit are added, so a blow that sweeps hits each victim once
     */
    protected void strike(ServerLevel level, Vec3 centre, double radius, DoubleUnaryOperator damage, Set<UUID> struck) {
        AABB reach = new AABB(centre.x - radius, centre.y - 16, centre.z - radius, centre.x + radius, centre.y + radius, centre.z + radius);
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, reach, this::prey)) {
            if (struck != null && struck.contains(victim.getUUID())) continue;
            Vec3 away = victim.position().subtract(centre);
            double distance = away.horizontalDistance();
            float harm = (float) damage.applyAsDouble(distance);
            if (harm <= 0 || !victim.hurt(damageSources().mobAttack(this), harm)) continue;
            if (struck != null) struck.add(victim.getUUID());
            double push = distance < .01 ? 0 : 1.4 / distance;
            victim.push(away.x * push, 1, away.z * push); victim.hurtMarked = true;
        }
    }

    /** The crash and dust of something that size hitting the ground. */
    protected void crash(ServerLevel level, Vec3 centre, double radius) {
        level.playSound(null, centre.x, centre.y, centre.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 24, .5F);
        particles(level, ParticleTypes.EXPLOSION_EMITTER, centre.add(0, 2, 0), 6, radius / 4, 0);
        particles(level, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, centre.add(0, 2, 0), 60, radius / 3, .02);
    }

    /** Its voice, heard from far off, and the curse on all that hear it within this many blocks. */
    protected void roar(ServerLevel level, double radius, int curseTicks, int curseAmplifier) {
        Vec3 mouth = getEyePosition();
        level.playSound(null, mouth.x, mouth.y, mouth.z, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 30, .5F);
        level.playSound(null, mouth.x, mouth.y, mouth.z, SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 30, .5F);
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(radius, getEyeHeight() * 1.5 + radius, radius), this::prey))
            if (victim.position().subtract(position()).horizontalDistance() <= radius)
                victim.addEffect(new MobEffectInstance(ModEffects.CORRUPTION_CURSE, curseTicks, curseAmplifier), this);
        particles(level, ParticleTypes.SONIC_BOOM, mouth, 12, 6, 0);
    }

    /** The usual particle call only reaches players 32 blocks away; nobody stands that close to this. */
    protected static void particles(ServerLevel level, ParticleOptions type, Vec3 at, int count, double spread, double speed) {
        for (ServerPlayer player : level.players()) level.sendParticles(player, type, true, at.x, at.y, at.z, count, spread, 1, spread, speed);
    }

    @Override public boolean hurt(DamageSource source, float amount) { return hurtPart(null, source, amount); }
    /** Only something attacking it can hurt it: not fire, water, walls or its own side. Commands still can. */
    private boolean hurtPart(ColossusPart part, DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return super.hurt(source, amount);
        if (source.getEntity() == null || CorruptionFactionApi.isRemnant(source.getEntity())) return false;
        if (!super.hurt(source, amount * damageTaken(part != null && part.weak()))) return false;
        if (getTarget() == null && prey(source.getEntity())) setTarget((LivingEntity) source.getEntity());
        return true;
    }

    /** It sinks over several seconds instead of tipping over and vanishing in one. */
    @Override protected void tickDeath() {
        deathTime++;
        if (!(level() instanceof ServerLevel level) || isRemoved()) return;
        if (attackId() != 0) start(0);
        if (deathTime % 12 == 1) {
            Part part = parts[random.nextInt(parts.length)];
            Vec3 at = part.getBoundingBox().getCenter();
            level.playSound(null, at.x, at.y, at.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 24, .5F + random.nextFloat() * .2F);
            particles(level, ParticleTypes.EXPLOSION_EMITTER, at, 3, blocks(part.definition.width()) / 4, 0);
        }
        if (deathTime >= deathTicks()) { level.broadcastEntityEvent(this, (byte) 60); remove(RemovalReason.KILLED); }
    }

    @Override public void setId(int id) { super.setId(id); if (parts != null) for (int index = 0; index < parts.length; index++) parts[index].setId(id + index + 1); }
    @Override public boolean isMultipartEntity() { return true; }
    @Override public PartEntity<?>[] getParts() { return parts; }
    @Override public boolean isPickable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public void push(Entity entity) {}
    @Override protected void pushEntities() {}
    @Override public boolean isPushedByFluid() { return false; }
    @Override public boolean canUsePortal(boolean allowPassengers) { return false; }
    @Override public boolean canBeAffected(MobEffectInstance effect) { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public boolean isAlliedTo(Entity entity) { return CorruptionFactionApi.isRemnant(entity); }
    /** Players can turn their entity distance down; this is meant to be seen from as far as it is sent. */
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return true; }
    @Override protected BodyRotationControl createBodyControl() { return new BodyRotationControl(this) { @Override public void clientTick() {} }; }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.WARDEN_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.WARDEN_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.WARDEN_DEATH; }
    @Override protected float getSoundVolume() { return 16; }
    @Override public float getVoicePitch() { return .5F; }
    @Override public int getAmbientSoundInterval() { return 300; }

    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); boss.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); boss.removePlayer(player); }
    @Override public void setCustomName(Component name) { super.setCustomName(name); boss.setName(getDisplayName()); }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (hasCustomName()) boss.setName(getDisplayName());
    }

    /** One hitbox. It has no life of its own: a hit on it is a hit on the colossus. */
    public static final class Part extends PartEntity<Colossus> {
        private final ColossusPart definition;
        private final EntityDimensions size;

        Part(Colossus parent, ColossusPart definition, double blocksPerPixel) {
            super(parent);
            this.definition = definition;
            size = EntityDimensions.scalable((float) (definition.width() * blocksPerPixel), (float) (definition.height() * blocksPerPixel));
            refreshDimensions();
        }

        public ColossusPart definition() { return definition; }
        @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
        @Override protected void readAdditionalSaveData(CompoundTag tag) {}
        @Override protected void addAdditionalSaveData(CompoundTag tag) {}
        @Override public boolean shouldBeSaved() { return false; }
        @Override public boolean isPickable() { return true; }
        @Override public boolean is(Entity entity) { return this == entity || getParent() == entity; }
        @Override public EntityDimensions getDimensions(Pose pose) { return size == null ? super.getDimensions(pose) : size; }
        @Override public boolean hurt(DamageSource source, float amount) { return getParent().hurtPart(definition, source, amount); }
    }
}
