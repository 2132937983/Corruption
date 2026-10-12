package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.plant.*;
import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** An anchored living growth. No wandering AI, chunk loads, or per-tick proximity scans. */
public abstract class AnchoredLivingPlant extends PathfinderMob {
    private static final EntityDataAccessor<BlockPos> ANCHOR = SynchedEntityData.defineId(AnchoredLivingPlant.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> HANGING = SynchedEntityData.defineId(AnchoredLivingPlant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STAGE = SynchedEntityData.defineId(AnchoredLivingPlant.class, EntityDataSerializers.INT);
    private int growthTicks;

    protected AnchoredLivingPlant(EntityType<? extends AnchoredLivingPlant> type, Level level) {
        super(type, level); setNoGravity(true); setNoAi(true); setPersistenceRequired(); noPhysics = true;
    }
    protected abstract LivingPlantKind plantKind();
    public static AttributeSupplier.Builder attributes(LivingPlantKind kind) {
        return PathfinderMob.createMobAttributes().add(Attributes.MAX_HEALTH, LivingPlantApi.definition(kind).health())
                .add(Attributes.MOVEMENT_SPEED, 0).add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }
    @Override protected void registerGoals() {}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder); builder.define(ANCHOR, BlockPos.ZERO); builder.define(HANGING, false); builder.define(STAGE, 0);
    }
    public int stage() { return entityData.get(STAGE); }
    public boolean hanging() { return entityData.get(HANGING); }
    public BlockPos anchor() { return entityData.get(ANCHOR); }
    public static int direction(LevelReader level, BlockPos pos) {
        return LivingPlantApi.direction(new LevelLivingPlantSpace(level, pos));
    }
    public static AnchoredLivingPlant find(ServerLevel level, BlockPos anchor) {
        return level.getEntitiesOfClass(AnchoredLivingPlant.class, new AABB(anchor).inflate(3, 11, 3),
                plant -> plant.isAlive() && plant.anchor().equals(anchor)).stream().findFirst().orElse(null);
    }
    public boolean canGrow() {
        boolean supported = level().hasChunkAt(anchor()) && level().getBlockState(anchor()).is(ModBlocks.MALICE.get());
        return LivingPlantApi.canGrow(plantKind(), stage(), isAlive() && !isRemoved(), supported,
                hanging() ? -1 : 1, new LevelLivingPlantSpace(level(), anchor()));
    }
    protected int growthHeight(int stage) { return LivingPlantApi.definition(plantKind()).height(stage); }
    protected float growthWidth(int stage) { return LivingPlantApi.definition(plantKind()).width(stage); }
    protected abstract int growthInterval();
    protected boolean hasGrowingRoom(int stage) { return LivingPlantApi.hasRoom(plantKind(), stage, hanging() ? -1 : 1, new LevelLivingPlantSpace(level(), anchor())); }
    protected final void initializeAnchor(BlockPos anchor, int direction) {
        entityData.set(ANCHOR, anchor.immutable()); entityData.set(HANGING, direction < 0); snapToAnchor();
    }
    public boolean grow() {
        if (level().isClientSide || !canGrow() || !LivingPlantApi.prepareGrowth(plantKind(), stage() + 1,
                hanging() ? -1 : 1, new LevelLivingPlantSpace(level(), anchor()))) return false;
        entityData.set(STAGE, stage() + 1); growthTicks = 0; snapToAnchor(); return true;
    }
    protected final void snapToAnchor() {
        BlockPos pos = anchor(); setPos(pos.getX() + 0.5, pos.getY() + (hanging() ? 0 : 1), pos.getZ() + 0.5);
        setDeltaMovement(Vec3.ZERO);
    }
    @Override public EntityDimensions getDefaultDimensions(Pose pose) {
        int stage = entityData == null ? 0 : stage();
        return EntityDimensions.scalable(growthWidth(stage), growthHeight(stage));
    }
    @Override protected AABB makeBoundingBox() {
        AABB normal = super.makeBoundingBox();
        return entityData != null && hanging() ? normal.move(0, -getBbHeight(), 0) : normal;
    }
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (STAGE.equals(key) || HANGING.equals(key)) refreshDimensions();
    }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        snapToAnchor();
        if ((tickCount + getId()) % 20 != 0) return;
        if (!server.getBlockState(anchor()).is(ModBlocks.MALICE.get())
                || !hasGrowingRoom(stage())) { discard(); return; }
        if (stage() < LivingPlantApi.definition(plantKind()).stages() - 1) {
            growthTicks = LivingPlantApi.advanceGrowthTicks(growthTicks, growthInterval(), 20);
            if (growthTicks >= growthInterval()) grow();
        }
        if (LivingPlantApi.definition(plantKind()).causesBlindness()) for (Player player : server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(InfectionConfig.blindnessRadius()),
                p -> p.isAlive() && !p.isSpectator() && !p.isCreative())) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false, true));
        }
        if (LivingPlantApi.definition(plantKind()).attractsRemnants() && (tickCount + getId()) % 40 == 0) {
            for (CorruptedRemnant remnant : server.getEntitiesOfClass(CorruptedRemnant.class,
                    getBoundingBox().inflate(InfectionConfig.attractionRadius()), Entity::isAlive)) {
                remnant.attractTo(anchor(), server.getGameTime() + 100);
            }
        }
    }
    @Override protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).is(Items.BONE_MEAL) && canGrow()) {
            if (level().isClientSide) return InteractionResult.SUCCESS;
            if (grow()) {
                if (!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);
                level().levelEvent(1505, anchor().offset(0, hanging() ? -1 : 1, 0), 15);
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(player, hand);
    }
    @Override public boolean isPushable() { return false; }
    @Override public void push(double x, double y, double z) {}
    @Override public void travel(Vec3 input) { setDeltaMovement(Vec3.ZERO); }
    @Override public boolean isAlliedTo(Entity entity) { return CorruptionFactionApi.isRemnant(entity); }
    @Override public boolean isInvulnerableTo(DamageSource source) { return source.is(ModDamageTypes.CORRUPTION) || super.isInvulnerableTo(source); }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag); tag.putLong("Anchor", anchor().asLong()); tag.putBoolean("Hanging", hanging());
        tag.putInt("GrowthStage", stage()); tag.putInt("GrowthTicks", growthTicks);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); entityData.set(ANCHOR, BlockPos.of(tag.getLong("Anchor")));
        entityData.set(HANGING, tag.getBoolean("Hanging")); entityData.set(STAGE, LivingPlantApi.definition(plantKind()).clampStage(tag.getInt("GrowthStage")));
        growthTicks = Math.max(0, tag.getInt("GrowthTicks")); setNoAi(true); setNoGravity(true); snapToAnchor();
    }
}
