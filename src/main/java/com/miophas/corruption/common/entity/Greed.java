package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.CorruptionFactionApi;
import com.miophas.corruption.api.corruption.GreedApi;
import com.miophas.corruption.api.plant.*;
import com.miophas.corruption.api.plant.AnchoredLivingPlant;
import com.miophas.corruption.api.plant.LevelLivingPlantSpace;
import com.miophas.corruption.common.config.InfectionConfig;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.common.registry.ModEntities;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;

/** A rooted chimera tree; its only combat movement is firing from its three heads. */
public final class Greed extends AnchoredLivingPlant {
    private static final EntityDataAccessor<Long> ATTACK_START = SynchedEntityData.defineId(Greed.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> ATTACK_HEAD = SynchedEntityData.defineId(Greed.class, EntityDataSerializers.INT);
    private UUID pendingTarget;
    private long nextAttack;
    private int nextHead;

    public Greed(EntityType<? extends Greed> type, Level level) { super(type, level); }
    public static AttributeSupplier.Builder attributes() { return attributes(LivingPlantKind.GREED); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder); builder.define(ATTACK_START, -1L); builder.define(ATTACK_HEAD, 0);
    }
    @Override protected LivingPlantKind plantKind() { return LivingPlantKind.GREED; }
    @Override protected int growthInterval() { return InfectionConfig.greedGrowthTicks(); }
    public double detectionRange() { return GreedApi.detectionRange(stage(), InfectionConfig.greedRange(), InfectionConfig.greedRangeStep()); }
    public int attackingHead() { return entityData.get(ATTACK_HEAD); }
    public float shotAge(float partial) { long start = entityData.get(ATTACK_START); return start < 0 ? -1 : level().getGameTime() - start + partial; }
    public static boolean hasRoom(LevelReader level, BlockPos anchor, int direction, int stage) {
        return LivingPlantApi.hasRoom(LivingPlantKind.GREED, stage, direction, new LevelLivingPlantSpace(level, anchor));
    }
    public static Greed sprout(ServerLevel level, BlockPos anchor) {
        if (!level.hasChunkAt(anchor) || !level.getBlockState(anchor).is(ModBlocks.MALICE.get())) return null;
        AnchoredLivingPlant existing = AnchoredLivingPlant.find(level, anchor);
        if (existing != null) return existing instanceof Greed greed ? greed : null;
        int direction = direction(level, anchor);
        if (!hasRoom(level, anchor, direction, 0)) return null;
        Greed greed = ModEntities.GREED.get().create(level);
        if (greed == null) return null;
        LivingPlantApi.prepareGrowth(LivingPlantKind.GREED, 0, direction, new LevelLivingPlantSpace(level, anchor));
        greed.initializeAnchor(anchor, direction); greed.setYRot(level.random.nextFloat() * 360);
        greed.yBodyRot = greed.yHeadRot = greed.getYRot();
        return level.addFreshEntity(greed) ? greed : null;
    }
    public Vec3 mouth(int head) {
        double width = GreedApi.width(stage()), height = GreedApi.height(stage());
        // Java Blockbench export mirrors its X axis; match the exported head sockets.
        double x = (head == 1 ? .28 : head == 2 ? -.27 : 0) * width;
        double y = height * (head == 1 ? .635 : head == 2 ? .878 : .691);
        if (hanging()) { x = -x; y = -y; }
        return position().add(new Vec3(x, y, width * (head == 2 ? .11 : head == 1 ? .24 : .285) + .025).yRot(-(float) Math.toRadians(getYRot())));
    }
    private boolean eligible(LivingEntity target) {
        return target != null && target != this && target.isAlive() && !target.isSpectator()
                && !CorruptionFactionApi.isRemnant(target)
                && (!(target instanceof Player player) || !player.isCreative())
                && !com.miophas.corruption.api.corruption.CorruptionVeilApi.hiddenFrom(target, this)
                && getBoundingBox().distanceToSqr(target.getBoundingBox().getCenter()) <= detectionRange() * detectionRange();
    }
    private boolean visible(LivingEntity target, int head) {
        return level().clip(new ClipContext(mouth(head), target.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server) || !isAlive() || isRemoved()
                || !LivingPlantApi.definition(plantKind()).attacksEnemies()) return;
        long now = server.getGameTime();
        if (pendingTarget != null) {
            var candidate = server.getEntity(pendingTarget);
            LivingEntity target = candidate instanceof LivingEntity living ? living : null;
            if (!eligible(target) || !visible(target, attackingHead())) {
                pendingTarget = null; entityData.set(ATTACK_START, -1L); nextAttack = now + 10;
            } else if (shotAge(0) >= GreedApi.windupTicks()) {
                GreedProjectile projectile = ModEntities.GREED_PROJECTILE.get().create(server);
                if (projectile != null) {
                    projectile.setOwner(this); projectile.setPos(mouth(attackingHead()));
                    Vec3 aim = target.getEyePosition().subtract(projectile.position());
                    projectile.shoot(aim.x, aim.y, aim.z, .60F, 0);
                    server.addFreshEntity(projectile);
                }
                pendingTarget = null; nextAttack = now + GreedApi.attackInterval(stage());
            }
        } else if (now >= nextAttack && (tickCount + getId()) % 20 == 0) {
            int head = nextHead % 3;
            LivingEntity target = server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(detectionRange()), this::eligible).stream()
                    .filter(p -> visible(p, head)).min(java.util.Comparator.comparingDouble(p -> p.distanceToSqr(this))).orElse(null);
            if (target != null) {
                pendingTarget = target.getUUID(); entityData.set(ATTACK_HEAD, head); entityData.set(ATTACK_START, now); nextHead++;
            }
        }
    }
}
