package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.RemnantCombatApi;
import com.miophas.corruption.api.corruption.RemnantAbility;
import com.miophas.corruption.api.corruption.RemnantAbilityApi;
import com.miophas.corruption.api.corruption.RemnantAnimationApi;
import com.miophas.corruption.common.registry.ModDamageTypes;
import com.miophas.corruption.common.registry.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** A distinct hostile entity with source appearance, attributes and selected source abilities. */
public class CorruptedRemnant extends Monster implements net.minecraft.world.entity.monster.RangedAttackMob {
    private static final EntityDataAccessor<CompoundTag> APPEARANCE = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Long> BOW_DRAW_START = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> ABILITY_FLAGS = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CAST_KIND = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> CAST_START = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DRAGON_PHASE = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<java.util.Optional<net.minecraft.core.BlockPos>> DRAGON_CRYSTAL = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<net.minecraft.core.Direction> SHULKER_FACE = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Integer> SHULKER_PEEK = SynchedEntityData.defineId(CorruptedRemnant.class, EntityDataSerializers.INT);
    private RemnantShulkerBehavior shulker;
    private RemnantDragonBehavior dragon;
    private RemnantAbilities abilities;
    private int abilitiesVersion = -1;
    private int appearanceVersion;
    private RemnantCombat combat;
    private int combatVersion = -1;
    private net.minecraft.core.BlockPos attraction;
    private long attractionExpires;
    public void attractTo(net.minecraft.core.BlockPos anchor, long expires) {
        if (getTarget() != null) return;
        if (attraction == null || level().getGameTime() >= attractionExpires
                || anchor.distToCenterSqr(position()) <= attraction.distToCenterSqr(position())) {
            attraction = anchor.immutable(); attractionExpires = expires;
        }
    }

    public CorruptedRemnant(EntityType<? extends CorruptedRemnant> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder attributes() {
        var definition = RemnantCombatApi.definition();
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, definition.health())
                .add(Attributes.MOVEMENT_SPEED, definition.movementSpeed())
                .add(Attributes.ATTACK_DAMAGE, definition.attackDamage()).add(Attributes.FOLLOW_RANGE, definition.followRange());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(APPEARANCE, new CompoundTag());
        builder.define(BOW_DRAW_START, -1L);
        builder.define(ABILITY_FLAGS, 0); builder.define(CAST_KIND, 0);
        builder.define(CAST_START, -1L); builder.define(CLIMBING, false);
        builder.define(SHULKER_FACE, net.minecraft.core.Direction.DOWN); builder.define(SHULKER_PEEK, 0);
        builder.define(DRAGON_PHASE, 0); builder.define(DRAGON_CRYSTAL, java.util.Optional.empty());
    }

    @Override
    protected void registerGoals() {
        var definition = RemnantCombatApi.definition();
        goalSelector.addGoal(0, new FloatGoal(this) {
            @Override public boolean canUse() { return !hasAbility(RemnantAbility.DRAGON_COMBAT) && !hasAbility(RemnantAbility.SHULKER_COMBAT)
                    && !hasAbility(RemnantAbility.SWIMMING) && isInLava() && super.canUse(); }
        });
        goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.Goal() {
            { setFlags(java.util.EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }
            @Override public boolean canUse() { return hasAbility(RemnantAbility.DRAGON_COMBAT); }
            @Override public boolean requiresUpdateEveryTick() { return true; }
            @Override public void tick() { dragon().fly(); }
        });
        goalSelector.addGoal(0, new RemnantAbilityGoal(this));
        goalSelector.addGoal(1, new RemnantFlightGoal(this));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, definition.attackSpeed(), false) {
            private boolean climbing() { return hasAbility(RemnantAbility.WALL_CLIMB) && !isInWater() && getTarget()!=null && getTarget().isAlive(); }
            @Override public boolean canUse() { return !hasRangedAttack() && !hasAbility(RemnantAbility.FLIGHT) && !hasAbility(RemnantAbility.SHULKER_COMBAT) && (super.canUse() || climbing()); }
            @Override public boolean canContinueToUse() { return !hasRangedAttack() && !hasAbility(RemnantAbility.FLIGHT) && !hasAbility(RemnantAbility.SHULKER_COMBAT) && (super.canContinueToUse() || climbing()); }
        });
        goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.RangedAttackGoal(this, definition.attackSpeed(), definition.rangedInterval(), definition.rangedDistance()) {
            @Override public boolean canUse() { return hasRangedAttack() && !hasAnimatedWeapon() && super.canUse(); }
            @Override public boolean canContinueToUse() { return hasRangedAttack() && !hasAnimatedWeapon() && super.canContinueToUse(); }
            @Override public void start() { super.start(); setAggressive(true); }
            @Override public void stop() { super.stop(); setAggressive(false); }
        });
        goalSelector.addGoal(1, new RemnantBowAttackGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, definition.strollSpeed()) {
            @Override public boolean canUse() { return !hasAbility(RemnantAbility.FLIGHT) && !hasAbility(RemnantAbility.SHULKER_COMBAT)
                    && !hasAbility(RemnantAbility.SWIMMING) && !(canSwim() && isInWater()) && super.canUse(); }
            @Override public boolean canContinueToUse() { return !(canSwim() && isInWater()) && super.canContinueToUse(); }
        });
        goalSelector.addGoal(5, new net.minecraft.world.entity.ai.goal.RandomSwimmingGoal(this, definition.strollSpeed(), 40) {
            @Override public boolean canUse() { return canSwim() && isInWater() && super.canUse(); }
            @Override public boolean canContinueToUse() { return isInWater() && super.canContinueToUse(); }
        });
        goalSelector.addGoal(4, new net.minecraft.world.entity.ai.goal.Goal() {
            { setFlags(java.util.EnumSet.of(Flag.MOVE, Flag.LOOK)); }
            @Override public boolean canUse() { return !hasAbility(RemnantAbility.SHULKER_COMBAT) && getTarget() == null && attraction != null && level().getGameTime() < attractionExpires; }
            @Override public boolean canContinueToUse() { return canUse(); }
            @Override public void tick() {
                if (tickCount % 20 == 0 && attraction != null) {
                    getNavigation().moveTo(attraction.getX() + 0.5, getY(), attraction.getZ() + 0.5, definition.attractionSpeed());
                    getLookControl().setLookAt(attraction.getX() + 0.5, attraction.getY() + 0.5, attraction.getZ() + 0.5);
                }
            }
            @Override public void stop() { getNavigation().stop(); }
        });
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, CorruptedRemnant.class));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                target -> !com.miophas.corruption.api.corruption.CorruptionFactionApi.isRemnant(target)) {
            @Override protected net.minecraft.world.phys.AABB getTargetSearchArea(double range) {
                if (!hasAbility(RemnantAbility.SHULKER_COMBAT)) return super.getTargetSearchArea(range);
                return switch (shulkerAttachFace().getAxis()) {
                    case X -> getBoundingBox().inflate(4, range, range);
                    case Y -> getBoundingBox().inflate(range, 4, range);
                    case Z -> getBoundingBox().inflate(range, range, 4);
                };
            }
        });
    }

    public void initialize(CompoundTag appearance, float maxHealth) {
        entityData.set(APPEARANCE, appearance.copy());
        var profile = com.miophas.corruption.api.corruption.RemnantCombatApi.capture(maxHealth,
                appearance.contains("AttackDamage", 99) ? appearance.getDouble("AttackDamage") : Double.NaN);
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(profile.maxHealth());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(profile.attackDamage());
        setHealth(getMaxHealth());
        abilities();
        if (hasAbility(RemnantAbility.DRAGON_COMBAT)) {
            getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(com.miophas.corruption.api.corruption.RemnantDragonApi.followRange());
            getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
            setPersistenceRequired(); noCulling = true;
            xpReward = 0;
        }
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) {
            setShulkerAttachFace(net.minecraft.core.Direction.from3DDataValue(appearance.getCompound("Entity").getByte("AttachFace")));
            getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1); setPersistenceRequired(); shulker().armor();
        }
        refreshDimensions();
    }

    public CompoundTag appearance() { return entityData.get(APPEARANCE).copy(); }
    public int appearanceVersion() { return appearanceVersion; }
    RemnantShulkerBehavior shulker() { if (shulker == null) shulker = new RemnantShulkerBehavior(this); return shulker; }
    public net.minecraft.core.Direction shulkerAttachFace() { return entityData.get(SHULKER_FACE); }
    void setShulkerAttachFace(net.minecraft.core.Direction face) { entityData.set(SHULKER_FACE, face); }
    public int shulkerRawPeek() { return entityData.get(SHULKER_PEEK); }
    void setShulkerRawPeek(int value) { entityData.set(SHULKER_PEEK, value); }
    public float shulkerPeek(float partial) { return shulker().peek(partial); }
    @Override public boolean canBeCollidedWith() {
        return hasAbility(RemnantAbility.SHULKER_COMBAT) ? isAlive() : super.canBeCollidedWith();
    }
    @Override public void push(Entity other) {
        if (!hasAbility(RemnantAbility.SHULKER_COMBAT)) super.push(other);
    }
    @Override protected net.minecraft.world.entity.ai.control.BodyRotationControl createBodyControl() {
        return new net.minecraft.world.entity.ai.control.BodyRotationControl(this) {
            @Override public void clientTick() { if (!hasAbility(RemnantAbility.SHULKER_COMBAT)) super.clientTick(); }
        };
    }
    @Override public void travel(net.minecraft.world.phys.Vec3 input) {
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) { setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO); return; }
        if (canSwim() && isControlledByLocalInstance() && isInWater()) {
            moveRelative(getSpeed(), input);move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(.9));return;
        }
        super.travel(input);
        if (!canSwim() && !isNoGravity() && isInWater()) {
            double depth=getFluidHeight(net.minecraft.tags.FluidTags.WATER),rest=Math.min(.65,getBbHeight()*.45);
            var velocity=getDeltaMovement();
            setDeltaMovement(velocity.x,net.minecraft.util.Mth.clamp(velocity.y*.65+(depth-rest)*.035,-.045,.045),velocity.z);
        }
    }
    @Override public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type) {
        return !canSwim() && super.canDrownInFluidType(type);
    }
    @Override public void move(net.minecraft.world.entity.MoverType type, net.minecraft.world.phys.Vec3 delta) {
        if (hasAbility(RemnantAbility.SHULKER_COMBAT) && !isPassenger()) {
            if (type == net.minecraft.world.entity.MoverType.SHULKER_BOX || type == net.minecraft.world.entity.MoverType.PISTON) shulker().teleport();
            return;
        }
        super.move(type, delta);
    }
    @Override protected net.minecraft.world.phys.AABB makeBoundingBox() {
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) return net.minecraft.world.entity.monster.Shulker.getProgressAabb(1,
                shulkerAttachFace().getOpposite(), com.miophas.corruption.api.corruption.RemnantShulkerApi.physicalPeek(shulker == null ? 0 : shulker.peek(1)))
                .move(getX() - .5, getY(), getZ() - .5);
        return super.makeBoundingBox();
    }
    RemnantDragonBehavior dragon() { if (dragon == null) dragon = new RemnantDragonBehavior(this); return dragon; }
    public com.miophas.corruption.api.corruption.RemnantDragonPhase dragonPhase() {
        return com.miophas.corruption.api.corruption.RemnantDragonPhase.fromId(entityData.get(DRAGON_PHASE));
    }
    void setDragonPhase(com.miophas.corruption.api.corruption.RemnantDragonPhase phase) { entityData.set(DRAGON_PHASE, phase.ordinal()); }
    public java.util.Optional<net.minecraft.core.BlockPos> dragonCrystal() { return entityData.get(DRAGON_CRYSTAL); }
    void setDragonCrystal(net.minecraft.core.BlockPos pos) { entityData.set(DRAGON_CRYSTAL, java.util.Optional.ofNullable(pos)); }
    @Override protected boolean shouldDespawnInPeaceful() {
        return !entityData.get(APPEARANCE).getBoolean("CorruptionDeath");
    }

    private RemnantCombat combat() {
        if (combat == null || combatVersion != appearanceVersion) { combat = new RemnantCombat(this); combatVersion = appearanceVersion; }
        return combat;
    }
    RemnantAbilities abilities() {
        if (abilities == null || abilitiesVersion != appearanceVersion) {
            abilities = new RemnantAbilities(this); abilitiesVersion = appearanceVersion;
            if (!level().isClientSide) entityData.set(ABILITY_FLAGS,
                    RemnantAbilityApi.flags(appearance().getString("Type"), combat().nativeAbilities()));
            updateAbilityMovement();
        }
        return abilities;
    }
    public boolean hasAbility(RemnantAbility ability) {
        return entityData != null && ability.in(RemnantAbilityApi.flags(entityData.get(APPEARANCE).getString("Type"), entityData.get(ABILITY_FLAGS)));
    }
    public boolean canSwim() {
        return hasAbility(RemnantAbility.AMPHIBIOUS) || hasAbility(RemnantAbility.SWIMMING);
    }
    private void updateAbilityMovement() {
        getNavigation().stop();
        navigation = hasAbility(RemnantAbility.FLIGHT) ? new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(this, level())
                : hasAbility(RemnantAbility.AMPHIBIOUS) ? new net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation(this, level())
                : hasAbility(RemnantAbility.SWIMMING) ? new net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation(this, level())
                : new RemnantSurfaceNavigation(this, level());
        moveControl = hasAbility(RemnantAbility.FLIGHT) ? new RemnantFlightMoveControl(this)
                : hasAbility(RemnantAbility.AMPHIBIOUS) || hasAbility(RemnantAbility.SWIMMING) ? new net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl(this, 85, 10, 1, .25F, false)
                : new RemnantSurfaceMoveControl(this);
        if(navigation instanceof net.minecraft.world.entity.ai.navigation.GroundPathNavigation ground)ground.setCanFloat(true);
        lookControl = hasAbility(RemnantAbility.SHULKER_COMBAT) ? new RemnantShulkerLookControl(this) : new net.minecraft.world.entity.ai.control.LookControl(this);
        setNoGravity(hasAbility(RemnantAbility.FLIGHT) || hasAbility(RemnantAbility.SHULKER_COMBAT));
    }
    void beginAbility(RemnantAbility ability) {
        entityData.set(CAST_KIND, ability.ordinal() + 1); entityData.set(CAST_START, level().getGameTime());
    }
    void endAbility() { entityData.set(CAST_KIND, 0); entityData.set(CAST_START, -1L); }
    public RemnantAbility castingAbility() {
        int kind = entityData.get(CAST_KIND);
        return kind > 0 && kind <= RemnantAbility.values().length ? RemnantAbility.values()[kind - 1] : null;
    }
    public float abilityCastTicks(float partialTick) {
        long start = entityData.get(CAST_START); return start < 0 ? -1 : Math.max(0, level().getGameTime() - start + partialTick);
    }
    @Override public void tick() {
        if (!level().isClientSide) abilities();
        boolean phase = hasAbility(RemnantAbility.PHASE_WALLS) || hasAbility(RemnantAbility.DRAGON_COMBAT);
        if (phase) noPhysics = true;
        super.tick();
        if (phase) noPhysics = false;
        if (!level().isClientSide) {
            entityData.set(CLIMBING, hasAbility(RemnantAbility.WALL_CLIMB) && horizontalCollision);
            abilities().tick();
        }
        if (hasAbility(RemnantAbility.DRAGON_COMBAT)) dragon().tick();
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) shulker().tick();
    }
    @Override public boolean onClimbable() {
        return hasAbility(RemnantAbility.WALL_CLIMB) ? entityData.get(CLIMBING) : super.onClimbable();
    }
    @Override public void makeStuckInBlock(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.phys.Vec3 multiplier) {
        if (!hasAbility(RemnantAbility.WALL_CLIMB) || !state.is(net.minecraft.world.level.block.Blocks.COBWEB)) super.makeStuckInBlock(state, multiplier);
    }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return !hasAbility(RemnantAbility.FLIGHT) && !hasAbility(RemnantAbility.SHULKER_COMBAT) && super.causeFallDamage(distance, multiplier, source);
    }
    @Override public void remove(RemovalReason reason) {
        if (dragon != null) dragon.close(reason.shouldDestroy());
        if (reason.shouldDestroy() && abilities != null && abilities.summoner != null
                && level() instanceof net.minecraft.server.level.ServerLevel server
                && server.getEntity(abilities.summoner) instanceof CorruptedRemnant owner) owner.abilities().release(getUUID());
        super.remove(reason);
    }
    public boolean hasRangedAttack() { return !level().isClientSide && combat().ranged(); }
    public boolean hasSkeletonBow() {
        return appearance().getString("Type").equals("minecraft:skeleton")
                && hasRangedAttack() && combat().weapon().getItem() instanceof net.minecraft.world.item.BowItem;
    }
    public boolean hasAnimatedWeapon() {
        if (hasSkeletonBow()) return true;
        String source = appearance().getString("Type");
        if (!source.equals("minecraft:pillager") && !source.equals("minecraft:illusioner")) return false;
        if (!hasRangedAttack()) return false;
        var weapon = combat().weapon().getItem();
        return weapon instanceof net.minecraft.world.item.BowItem || weapon instanceof net.minecraft.world.item.CrossbowItem;
    }
    /** One timestamp per draw; clients interpolate locally without per-tick packets. */
    public void beginBowDraw() { entityData.set(BOW_DRAW_START, level().getGameTime()); }
    public void stopBowDraw() { entityData.set(BOW_DRAW_START, -1L); }
    public float bowDrawTicks(float partialTick) {
        long start = entityData.get(BOW_DRAW_START);
        return start < 0 ? -1 : net.minecraft.util.Mth.clamp(level().getGameTime() - start + partialTick, 0, 28);
    }
    @Override public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (level().isClientSide) return;
        level().broadcastEntityEvent(this, RemnantAnimationApi.ATTACK_EVENT);
        combat().shoot(target, distanceFactor);
    }
    /** Client only: attack events the renderer replays on the source proxy; bounded in case the remnant is never drawn. */
    private final java.util.ArrayDeque<Byte> relayedEvents = new java.util.ArrayDeque<>();
    public Byte pollRelayedEvent() { return relayedEvents.poll(); }
    @Override public void handleEntityEvent(byte id) {
        if (id == RemnantAnimationApi.ATTACK_EVENT && relayedEvents.size() < 8) relayedEvents.add(id);
        super.handleEntityEvent(id);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (APPEARANCE.equals(key)) { appearanceVersion++; refreshDimensions(); }
        if (ABILITY_FLAGS.equals(key) && level().isClientSide) updateAbilityMovement();
        if (SHULKER_FACE.equals(key) && hasAbility(RemnantAbility.SHULKER_COMBAT)) setBoundingBox(makeBoundingBox());
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        // Called during Entity's constructor, before synced data exists.
        if (entityData == null) return super.getDefaultDimensions(pose);
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) return EntityDimensions.scalable(1, 1);
        CompoundTag data = entityData.get(APPEARANCE);
        if (data.getString("Type").equals("minecraft:axolotl")) {
            float scale=data.getCompound("Entity").getInt("Age")<0?.5F:1;
            return EntityDimensions.scalable(1.35F*scale,1.5F*scale);
        }
        float width = data.getFloat("Width"), height = data.getFloat("Height");
        return width > 0 && height > 0 ? EntityDimensions.scalable(width, height) : super.getDefaultDimensions(pose);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damage) {
        return damage.is(ModDamageTypes.CORRUPTION)
                || hasAbility(RemnantAbility.DRAGON_COMBAT) && damage.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)
                || super.isInvulnerableTo(damage);
    }

    @Override public boolean hurt(DamageSource source, float amount) {
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) {
            if (source.getEntity() != null && isAlliedTo(source.getEntity()) || shulkerRawPeek() == 0 && source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.AbstractArrow) return false;
            boolean hit = super.hurt(source, amount);
            if (hit && !level().isClientSide && isAlive()) shulker().onHurt(source);
            return hit;
        }
        return hasAbility(RemnantAbility.DRAGON_COMBAT) ? hurtDragonPart(false, source, amount) : super.hurt(source, amount);
    }
    boolean hurtDragonPart(boolean head, DamageSource source, float amount) {
        if (isInvulnerableTo(source) || isDeadOrDying() || source.getEntity() != null && isAlliedTo(source.getEntity())) return false;
        if (dragonPhase().sitting() && (source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.AbstractArrow
                || source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.windcharge.WindCharge)) {
            source.getDirectEntity().igniteForSeconds(1); return false;
        }
        float before = getHealth();
        boolean hit = super.hurt(source, com.miophas.corruption.api.corruption.RemnantDragonApi.receivedDamage(head, amount));
        if (hit && !level().isClientSide) dragon().onHurt(before - getHealth());
        return hit;
    }
    @Override public boolean addEffect(net.minecraft.world.effect.MobEffectInstance effect, Entity source) {
        return !hasAbility(RemnantAbility.DRAGON_COMBAT) && super.addEffect(effect, source);
    }
    @Override public void startSeenByPlayer(net.minecraft.server.level.ServerPlayer player) {
        super.startSeenByPlayer(player); if (hasAbility(RemnantAbility.DRAGON_COMBAT)) dragon().addViewer(player);
    }
    @Override public void stopSeenByPlayer(net.minecraft.server.level.ServerPlayer player) {
        super.stopSeenByPlayer(player); if (dragon != null) dragon.removeViewer(player);
    }
    @Override protected void tickDeath() {
        if (hasAbility(RemnantAbility.DRAGON_COMBAT)) dragon().tickDeath(); else super.tickDeath();
    }
    int dragonExperience(int amount) { return net.neoforged.neoforge.event.EventHooks.getExperienceDrop(this, lastHurtByPlayer, amount); }
    @Override protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) return shulkerRawPeek() == 0 ? null : net.minecraft.sounds.SoundEvents.SHULKER_AMBIENT;
        return hasAbility(RemnantAbility.DRAGON_COMBAT) ? net.minecraft.sounds.SoundEvents.ENDER_DRAGON_AMBIENT : ModSounds.REMNANT_AMBIENT.get();
    }
    @Override protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source) {
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) return shulkerRawPeek() == 0 ? net.minecraft.sounds.SoundEvents.SHULKER_HURT_CLOSED : net.minecraft.sounds.SoundEvents.SHULKER_HURT;
        return hasAbility(RemnantAbility.DRAGON_COMBAT) ? net.minecraft.sounds.SoundEvents.ENDER_DRAGON_HURT : ModSounds.REMNANT_HURT.get();
    }
    @Override protected net.minecraft.sounds.SoundEvent getDeathSound() {
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) return net.minecraft.sounds.SoundEvents.SHULKER_DEATH;
        return hasAbility(RemnantAbility.DRAGON_COMBAT) ? super.getDeathSound() : ModSounds.REMNANT_DEATH.get();
    }
    /** Shulker and dragon remnants keep the voices of the mobs they were; every other remnant uses its own. */
    private boolean hasOwnVoice() { return !hasAbility(RemnantAbility.SHULKER_COMBAT) && !hasAbility(RemnantAbility.DRAGON_COMBAT); }
    /** One set of recordings serves every body: a remnant the size of a chicken sounds higher than one the size of a warden. */
    private float sizePitch() { return net.minecraft.util.Mth.clamp(1.45F - getBbHeight() * .25F, .7F, 1.4F); }
    @Override public float getVoicePitch() { return hasOwnVoice() ? super.getVoicePitch() * sizePitch() : super.getVoicePitch(); }
    @Override protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        if (hasOwnVoice()) playSound(ModSounds.REMNANT_STEP.get(), .2F, sizePitch()); else super.playStepSound(pos, state);
    }
    @Override
    public boolean doHurtTarget(Entity target) {
        if (isAlliedTo(target)) return false;
        if (!level().isClientSide) level().broadcastEntityEvent(this, RemnantAnimationApi.ATTACK_EVENT);
        if (hasOwnVoice()) playSound(ModSounds.REMNANT_ATTACK.get(), getSoundVolume(), getVoicePitch());
        // Source mobs supply attributes and appearance, not poison, wither or other hit effects.
        var source = damageSources().mobAttack(this);
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (level() instanceof net.minecraft.server.level.ServerLevel server) damage = net.minecraft.world.item.enchantment.EnchantmentHelper.modifyDamage(server, combat().weapon(), target, source, damage);
        boolean hit = target.hurt(source, damage);
        if (hit && target instanceof LivingEntity living && level() instanceof net.minecraft.server.level.ServerLevel server) {
            double knockback = appearance().getDouble("AttackKnockback");
            if (knockback > 0) living.knockback(knockback * 0.5, Math.sin(Math.toRadians(getYRot())), -Math.cos(Math.toRadians(getYRot())));
            setLastHurtMob(target);
        }
        return hit;
    }

    @Override public boolean isAlliedTo(Entity entity) { return com.miophas.corruption.api.corruption.CorruptionFactionApi.isRemnant(entity); }
    @Override public boolean canAttack(LivingEntity target) { return !isAlliedTo(target) && super.canAttack(target); }
    @Override public boolean canAttackType(EntityType<?> type) { return type != getType() && !type.is(com.miophas.corruption.api.corruption.CorruptionFactionApi.REMNANTS); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("SourceAppearance", appearance());
        abilities().save(tag);
        if (hasAbility(RemnantAbility.DRAGON_COMBAT)) dragon().save(tag);
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) shulker().save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(APPEARANCE, tag.getCompound("SourceAppearance").copy());
        abilities().load(tag);
        if (hasAbility(RemnantAbility.SHULKER_COMBAT)) { shulker = new RemnantShulkerBehavior(this); shulker.load(tag); getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1); setPersistenceRequired(); }
        if (hasAbility(RemnantAbility.DRAGON_COMBAT)) {
            dragon = new RemnantDragonBehavior(this); dragon.load(tag);
            getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(com.miophas.corruption.api.corruption.RemnantDragonApi.followRange());
            getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);setPersistenceRequired();noCulling=true;
        }
        refreshDimensions();
    }
}
