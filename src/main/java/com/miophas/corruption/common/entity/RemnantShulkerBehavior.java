package com.miophas.corruption.common.entity;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.api.corruption.RemnantAbility;
import com.miophas.corruption.api.corruption.RemnantShulkerApi;
import com.miophas.corruption.common.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Real anchored shell and native homing projectiles, with remnant ownership and faction rules. */
final class RemnantShulkerBehavior {
    private static final ResourceLocation ARMOR = ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "closed_shell");
    private final CorruptedRemnant actor;
    private int shotTicks = RemnantShulkerApi.firstShotTicks(), peekTicks;
    private float peek, oldPeek;
    RemnantShulkerBehavior(CorruptedRemnant actor) { this.actor = actor; }
    float peek(float partial) { return net.minecraft.util.Mth.lerp(Math.clamp(partial, 0, 1), oldPeek, peek); }
    AABB bounds() { return Shulker.getProgressAabb(1, actor.shulkerAttachFace().getOpposite(), RemnantShulkerApi.physicalPeek(peek)).move(actor.getX() - .5, actor.getY(), actor.getZ() - .5); }
    private AABB bounds(BlockPos pos, Direction face, float progress) {
        return Shulker.getProgressAabb(1, face.getOpposite(), progress).move(pos.getX(), pos.getY(), pos.getZ());
    }
    void tick() {
        if (!actor.level().isClientSide) {
            actor.getNavigation().stop(); actor.setDeltaMovement(Vec3.ZERO);
            if (!actor.isPassenger()) {
                var pos = actor.blockPosition();
                actor.setPos(pos.getX() + .5, pos.getY(), pos.getZ() + .5);
                if (!canStay(pos, actor.shulkerAttachFace())) {
                    Direction face = surface(pos);
                    if (face != null) actor.setShulkerAttachFace(face); else teleport();
                }
            }
            var target = actor.getTarget();
            boolean attacking = !actor.isNoAi() && actor.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL && target != null && target.isAlive() && actor.canAttack(target)
                    && actor.distanceToSqr(target) < Math.pow(RemnantShulkerApi.attackRange(), 2);
            actor.setAggressive(attacking);
            if (attacking) {
                setPeek(100); peekTicks = 0;
                actor.getLookControl().setLookAt(target, 180, 180);
                if (--shotTicks <= 0) {
                    actor.level().addFreshEntity(new ShulkerBullet(actor.level(), actor, target, actor.shulkerAttachFace().getAxis()));
                    actor.playSound(SoundEvents.SHULKER_SHOOT, 2, (actor.getRandom().nextFloat() - actor.getRandom().nextFloat()) * .2F + 1);
                    actor.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                    shotTicks = RemnantShulkerApi.shotDelay(actor.getRandom().nextInt(10));
                }
            } else {
                shotTicks = RemnantShulkerApi.firstShotTicks();
                if (peekTicks > 0) { if (--peekTicks == 0) setPeek(0); }
                else if (!actor.isNoAi() && actor.getRandom().nextInt(40) == 0) {
                    peekTicks = 20 * (1 + actor.getRandom().nextInt(3)); setPeek(30);
                } else setPeek(0);
            }
            armor();
        }
        oldPeek = peek;
        float target = actor.shulkerRawPeek() / 100F;
        peek += Math.clamp(target - peek, -.05F, .05F);
        actor.setBoundingBox(bounds());
        float before = RemnantShulkerApi.physicalPeek(oldPeek), after = RemnantShulkerApi.physicalPeek(peek);
        if (!actor.level().isClientSide && after > before) {
            Direction d = actor.shulkerAttachFace().getOpposite();
            var slice = Shulker.getProgressDeltaAabb(1, d, before, after).move(actor.getX() - .5, actor.getY(), actor.getZ() - .5);
            for (Entity entity : actor.level().getEntities(actor, slice, e -> !e.isSpectator() && !e.isPassengerOfSameVehicle(actor)))
                if (!(entity instanceof Shulker) && !(entity instanceof CorruptedRemnant r && r.hasAbility(RemnantAbility.SHULKER_COMBAT)) && !entity.noPhysics)
                    entity.move(MoverType.SHULKER, new Vec3(d.getStepX(), d.getStepY(), d.getStepZ()).scale(after - before));
        }
    }
    private void setPeek(int amount) {
        if (actor.shulkerRawPeek() == amount) return;
        actor.setShulkerRawPeek(amount);
        actor.playSound(amount == 0 ? SoundEvents.SHULKER_CLOSE : SoundEvents.SHULKER_OPEN, 1, 1);
        actor.gameEvent(amount == 0 ? GameEvent.CONTAINER_CLOSE : GameEvent.CONTAINER_OPEN);
        armor();
    }
    void armor() {
        var attr = actor.getAttribute(Attributes.ARMOR);
        if (actor.shulkerRawPeek() == 0) {
            if (!attr.hasModifier(ARMOR)) attr.addTransientModifier(new AttributeModifier(ARMOR, RemnantShulkerApi.closedArmor(), AttributeModifier.Operation.ADD_VALUE));
        } else attr.removeModifier(ARMOR);
    }
    boolean canStay(BlockPos pos, Direction face) {
        var level = actor.level();
        return level.hasChunkAt(pos) && level.getWorldBorder().isWithinBounds(pos) && level.isEmptyBlock(pos)
                && level.loadedAndEntityCanStandOnFace(pos.relative(face), actor, face.getOpposite())
                && level.noCollision(actor, bounds(pos, face, 1).deflate(1.0E-6));
    }
    private Direction surface(BlockPos pos) {
        for (Direction d : Direction.values()) if (canStay(pos, d)) return d;
        return null;
    }
    boolean teleport() {
        if (actor.isNoAi() || !actor.isAlive() || actor.level().isClientSide) return false;
        var old = actor.blockPosition(); int radius = RemnantShulkerApi.teleportRadius();
        for (int i = 0; i < RemnantShulkerApi.teleportAttempts(); i++) {
            var pos = old.offset(actor.getRandom().nextInt(radius * 2 + 1) - radius,
                    actor.getRandom().nextInt(radius * 2 + 1) - radius, actor.getRandom().nextInt(radius * 2 + 1) - radius);
            if (pos.getY() <= actor.level().getMinBuildHeight() || pos.getY() >= actor.level().getMaxBuildHeight()) continue;
            Direction face = surface(pos); if (face == null) continue;
            var event = net.neoforged.neoforge.event.EventHooks.onEnderTeleport(actor, pos.getX() + .5, pos.getY(), pos.getZ() + .5);
            if (event.isCanceled()) continue;
            pos = BlockPos.containing(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            face = surface(pos); if (face == null) continue;
            actor.unRide(); actor.setShulkerAttachFace(face); actor.setShulkerRawPeek(0); armor();
            actor.setPos(pos.getX() + .5, pos.getY(), pos.getZ() + .5); actor.setBoundingBox(bounds(pos, face, 0));
            actor.setTarget(null); shotTicks = RemnantShulkerApi.firstShotTicks(); peekTicks = 0;
            actor.playSound(SoundEvents.SHULKER_TELEPORT, 1, 1); actor.gameEvent(GameEvent.TELEPORT);
            return true;
        }
        return false;
    }
    void onHurt(net.minecraft.world.damagesource.DamageSource source) {
        if (actor.getHealth() < actor.getMaxHealth() / 2 && actor.getRandom().nextInt(4) == 0) { teleport(); return; }
        if (!(source.getDirectEntity() instanceof ShulkerBullet) || actor.shulkerRawPeek() == 0) return;
        var pos = actor.position(); var face = actor.shulkerAttachFace(); var box = actor.getBoundingBox();
        if (!teleport()) return;
        int nearby = actor.level().getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, box.inflate(8),
                e -> e.isAlive() && (e instanceof Shulker || e instanceof CorruptedRemnant r && r.hasAbility(RemnantAbility.SHULKER_COMBAT))).size();
        if (actor.getRandom().nextFloat() >= RemnantShulkerApi.reproductionChance(nearby)) return;
        var child = ModEntities.CORRUPTED_REMNANT.get().create(actor.level());
        child.initialize(actor.appearance(), actor.getMaxHealth()); child.setShulkerAttachFace(face); child.setPos(pos);
        if (child.shulker().canStay(child.blockPosition(), face)) actor.level().addFreshEntity(child);
    }
    void save(CompoundTag tag) {
        var data = new CompoundTag(); data.putInt("Face", actor.shulkerAttachFace().get3DDataValue());
        data.putInt("Peek", actor.shulkerRawPeek()); data.putFloat("Progress", peek);
        data.putInt("ShotTicks", shotTicks); data.putInt("PeekTicks", peekTicks); tag.put("ShulkerBehavior", data);
    }
    void load(CompoundTag tag) {
        var data = tag.getCompound("ShulkerBehavior");
        actor.setShulkerAttachFace(Direction.from3DDataValue(data.getInt("Face")));
        actor.setShulkerRawPeek(Math.clamp(data.getInt("Peek"), 0, 100));
        peek = oldPeek = Math.clamp(data.getFloat("Progress"), 0, 1);
        shotTicks = Math.clamp(data.getInt("ShotTicks"), 1, 110); peekTicks = Math.clamp(data.getInt("PeekTicks"), 0, 60);
        armor(); actor.setBoundingBox(bounds());
    }
}
