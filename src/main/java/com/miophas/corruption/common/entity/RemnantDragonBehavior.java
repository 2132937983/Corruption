package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.corruption.*;
import com.miophas.corruption.common.registry.ModEntities;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;

/** Local dragon phases: never ticks a vanilla dragon proxy or claims the world's End fight. */
final class RemnantDragonBehavior {
    private final CorruptedRemnant actor;
    private final ServerBossEvent boss = new ServerBossEvent(Component.translatable("entity.corruption.corrupted_dragon"),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
    private int phaseTicks, attackCycle, flameCount, fireCharge, chargeRecovery, deathTicks;
    private float sittingDamage;
    private Vec3 center, destination;
    private EndCrystal crystal;
    private UUID crystalId, flameId;
    private boolean blocked;
    RemnantDragonBehavior(CorruptedRemnant actor) { this.actor = actor; boss.setPlayBossMusic(true); }
    void addViewer(ServerPlayer player) { boss.addPlayer(player); }
    void removeViewer(ServerPlayer player) { boss.removePlayer(player); }
    void close(boolean destroyed) { boss.removeAllPlayers(); if (destroyed) clearFlame(); }
    private boolean valid(LivingEntity target) { return target != null && target.isAlive() && !target.isSpectator() && actor.canAttack(target); }
    private Vec3 forward() { return Vec3.directionFromRotation(0, actor.getYRot()); }
    Vec3 partPosition(int index) {
        Vec3 f = forward(), r = new Vec3(f.z, 0, -f.x);
        double along = switch (index) { case 0 -> 4.2; case 1 -> 2.7; case 2 -> -1; case 3 -> -4.5; case 4 -> -6.5; case 5 -> -8.5; default -> -.2; };
        double side = index == 6 ? 4.5 : index == 7 ? -4.5 : 0;
        double y = index == 2 ? 3.5 : index >= 6 ? 4.4 : 3.8;
        if (actor.dragonPhase().sitting() && index < 2) y -= .8;
        return actor.position().add(f.scale(along)).add(r.scale(side)).add(0, y, 0);
    }
    AABB partBox(int index) {
        Vec3 p = partPosition(index);
        double w = index == 0 ? 1.25 : index == 1 || index == 2 ? 3 : index >= 6 ? 4 : 2;
        double h = index >= 6 ? 2 : index == 2 ? 3 : index == 0 ? 1.25 : 2;
        return new AABB(p.x-w/2, p.y, p.z-w/2, p.x+w/2, p.y+h, p.z+w/2);
    }
    private Vec3 orbit() {
        double angle = actor.tickCount * .025 + actor.getUUID().hashCode() % 360;
        return center.add(Math.cos(angle)*24, 14 + Math.sin(angle*.7)*3, Math.sin(angle)*24);
    }
    private Vec3 safe(Vec3 point) {
        return new Vec3(point.x, Mth.clamp(point.y, actor.level().getMinBuildHeight()+8, actor.level().getMaxBuildHeight()-10), point.z);
    }
    private Vec3 ground(LivingEntity target) {
        Vec3 p = target.position().subtract(forward().scale(5));
        BlockPos column = BlockPos.containing(p);
        if (!actor.level().hasChunkAt(column)) return null;
        int y = actor.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column.getX(), column.getZ());
        if (y <= actor.level().getMinBuildHeight() || y >= actor.level().getMaxBuildHeight()-8) return null;
        var support = actor.level().getBlockState(new BlockPos(column.getX(),y-1,column.getZ()));
        return support.getFluidState().isEmpty() ? new Vec3(column.getX()+.5, y, column.getZ()+.5) : null;
    }
    void transition(RemnantDragonPhase next) {
        if (actor.dragonPhase() == RemnantDragonPhase.BREATH && next != RemnantDragonPhase.BREATH) clearFlame();
        actor.setDragonPhase(next); phaseTicks = 0; fireCharge = 0; chargeRecovery = 0;
        var target = actor.getTarget();
        if (next == RemnantDragonPhase.CHARGE && valid(target)) destination = target.position().add(0,-3,0);
        if (next == RemnantDragonPhase.LANDING && valid(target)) {
            destination = ground(target); if (destination == null) { actor.setDragonPhase(RemnantDragonPhase.HOLDING); return; }
            flameCount = 0; sittingDamage = 0;
        }
        if (next == RemnantDragonPhase.TAKEOFF) destination = actor.position().add(forward().scale(15)).add(0,18,0);
        if (next == RemnantDragonPhase.ROARING) actor.playSound(SoundEvents.ENDER_DRAGON_GROWL, 5, 1);
        if (next == RemnantDragonPhase.BREATH) flameCount++;
    }
    /** Called by a dedicated goal, excluding all generic flight and melee goals. */
    void fly() {
        if (!actor.isAlive()) return;
        actor.getNavigation().stop();
        var target = actor.getTarget();
        if (!valid(target)) {
            actor.setTarget(null);
            if (actor.tickCount % 20 == 0 && actor.level() instanceof ServerLevel server) {
                target = server.getEntitiesOfClass(LivingEntity.class, actor.getBoundingBox().inflate(RemnantDragonApi.followRange()), this::valid)
                        .stream().min(java.util.Comparator.comparingDouble(actor::distanceToSqr)).orElse(null);
                actor.setTarget(target);
            }
        }
        if (center == null) center = actor.position();
        if (valid(target)) center = center.lerp(target.position(), .015);
        var phase = actor.dragonPhase(); phaseTicks++;
        if (!valid(target) && phase != RemnantDragonPhase.HOLDING && phase != RemnantDragonPhase.HOVER && phase != RemnantDragonPhase.TAKEOFF) {
            transition(RemnantDragonPhase.TAKEOFF); phase = actor.dragonPhase();
        }
        Vec3 point = orbit();
        switch (phase) {
            case HOLDING -> {
                if (valid(target) && phaseTicks >= RemnantDragonApi.duration(phase)) {
                    transition(switch (attackCycle++ % 3) { case 0 -> RemnantDragonPhase.STRAFE; case 1 -> RemnantDragonPhase.CHARGE; default -> RemnantDragonPhase.LANDING; });
                }
            }
            case STRAFE -> {
                if (valid(target)) {
                    point = target.position().subtract(forward().scale(16)).add(0,8,0);
                    face(target.position());
                    Vec3 aim = target.getBoundingBox().getCenter().subtract(partPosition(0)).normalize();
                    if (actor.distanceToSqr(target) < RemnantDragonApi.fireballRange()*RemnantDragonApi.fireballRange() && actor.hasLineOfSight(target)) {
                        fireCharge++;
                        if (fireCharge >= RemnantDragonApi.fireballWarmup() && forward().dot(new Vec3(aim.x,0,aim.z).normalize()) > Math.cos(Math.toRadians(10))) {
                            shoot(target); transition(RemnantDragonPhase.HOLDING);
                        }
                    } else fireCharge = Math.max(0,fireCharge-1);
                }
                if (phaseTicks >= RemnantDragonApi.duration(phase)) transition(RemnantDragonPhase.HOLDING);
            }
            case CHARGE -> {
                point = destination == null ? point : destination;
                if (actor.position().distanceToSqr(point) < 36 || blocked || chargeRecovery > 0) chargeRecovery++;
                if (chargeRecovery >= 10 || phaseTicks >= RemnantDragonApi.duration(phase)) transition(RemnantDragonPhase.HOLDING);
            }
            case LANDING -> {
                point = destination == null ? point : destination;
                if (actor.position().distanceToSqr(point) < 1) { actor.setPos(point); actor.setDeltaMovement(Vec3.ZERO); transition(RemnantDragonPhase.SCANNING); }
                else if (phaseTicks >= RemnantDragonApi.duration(phase)) transition(RemnantDragonPhase.TAKEOFF);
            }
            case SCANNING, ROARING, BREATH -> {
                actor.setDeltaMovement(Vec3.ZERO);
                if (valid(target)) face(target.position());
                if (phase == RemnantDragonPhase.BREATH && phaseTicks == RemnantDragonApi.breathWarmup()) breathe();
                if (phaseTicks >= RemnantDragonApi.duration(phase)) transition(phase == RemnantDragonPhase.SCANNING ? RemnantDragonPhase.ROARING
                        : phase == RemnantDragonPhase.ROARING ? RemnantDragonPhase.BREATH
                        : flameCount >= RemnantDragonApi.maxBreaths() ? RemnantDragonPhase.TAKEOFF : RemnantDragonPhase.SCANNING);
                return;
            }
            case TAKEOFF -> { point = destination == null ? point : destination; if (phaseTicks >= 60 || actor.position().distanceToSqr(point)<9) transition(RemnantDragonPhase.HOLDING); }
            case HOVER -> { actor.setDeltaMovement(Vec3.ZERO); if (phaseTicks >= 40) transition(RemnantDragonPhase.HOLDING); return; }
            case DYING -> { return; }
        }
        point = safe(point);
        if (!actor.level().hasChunkAt(BlockPos.containing(point))) { actor.setDeltaMovement(actor.getDeltaMovement().scale(.5)); return; }
        Vec3 direction = point.subtract(actor.position()); double length = direction.length();
        if (length > .1) {
            Vec3 desired = direction.scale(Math.min(RemnantDragonApi.speed(actor.dragonPhase()), length*.25)/length);
            actor.setDeltaMovement(actor.getDeltaMovement().lerp(desired, blocked ? .04 : .12));
            if (phase != RemnantDragonPhase.STRAFE) face(point);
        }
    }
    private void face(Vec3 point) {
        Vec3 d=point.subtract(actor.position());
        float yaw=(float)(Mth.atan2(d.z,d.x)*180/Math.PI)-90;
        actor.setYRot(Mth.rotLerp(.12F,actor.getYRot(),yaw)); actor.yBodyRot=actor.getYRot(); actor.yHeadRot=actor.getYRot();
    }
    private void shoot(LivingEntity target) {
        Vec3 muzzle = partPosition(0).add(forward()).add(0,.5,0);
        var ball = ModEntities.CORRUPTED_DRAGON_FIREBALL.get().create(actor.level());
        if (ball == null) return;
        ball.setOwner(actor); ball.setPos(muzzle); ball.setDeltaMovement(target.getBoundingBox().getCenter().subtract(muzzle).normalize());
        actor.level().addFreshEntity(ball); actor.level().levelEvent(1017,actor.blockPosition(),0); actor.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
    }
    private void breathe() {
        if (!(actor.level() instanceof ServerLevel server)) return;
        Vec3 p = partPosition(0).add(forward().scale(2.5));
        BlockPos column=BlockPos.containing(p); if (!server.hasChunkAt(column)) return;
        int y=server.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,column.getX(),column.getZ());
        var cloud=CorruptedDragonBreath.create(server,actor,new Vec3(p.x,y,p.z),false);
        if (cloud != null) flameId=cloud.getUUID();
    }
    private void clearFlame() {
        if (flameId != null && actor.level() instanceof ServerLevel server && server.getEntity(flameId) instanceof CorruptedDragonBreath cloud) cloud.discard();
        flameId=null;
    }
    void onHurt(float damage) {
        if (actor.dragonPhase().sitting()) { sittingDamage+=damage; if (sittingDamage > actor.getMaxHealth()*.25) { sittingDamage=0; transition(RemnantDragonPhase.TAKEOFF); } }
    }
    void tick() {
        if (actor instanceof CorruptedDragon dragon) dragon.updateParts();
        if (actor.level().isClientSide) {
            if (actor.isAlive() && !actor.dragonPhase().sitting() && actor.tickCount % 35 == 0) actor.playSound(SoundEvents.ENDER_DRAGON_FLAP,5,.8F);
            if (actor.dragonPhase()==RemnantDragonPhase.BREATH && actor.tickCount%2==0) {
                Vec3 p=partPosition(0),f=forward();
                for(int i=0;i<8;i++)actor.level().addParticle(ParticleTypes.DRAGON_BREATH,p.x,p.y+.5,p.z,f.x*.4,-.15,f.z*.4);
            }
            return;
        }
        boss.setProgress(Mth.clamp(actor.getHealth()/actor.getMaxHealth(),0,1));
        boss.setName(actor.hasCustomName()?actor.getCustomName():Component.translatable("entity.corruption.corrupted_dragon"));
        if (!actor.isAlive() || actor.isNoAi()) return;
        crystals();
        contact();
        if (actor.tickCount%4==0) walls();
    }
    private void contact() {
        // Head/neck do physical damage; wing push is excluded for allies and creative/spectator players.
        for (int i : new int[]{0,1,6,7}) {
            for (LivingEntity target:actor.level().getEntitiesOfClass(LivingEntity.class,partBox(i).inflate(i<2?1:.5),this::valid)) {
                if (i>=6) {
                    double x=target.getX()-actor.getX(), z=target.getZ()-actor.getZ(),length=Math.max(x*x+z*z,.1);
                    target.push(x/length*4,.2,z/length*4);
                    if (!actor.dragonPhase().sitting()) target.hurt(actor.damageSources().mobAttack(actor),RemnantDragonApi.wingDamage());
                } else if (target.hurt(actor.damageSources().mobAttack(actor),RemnantDragonApi.headDamage())) actor.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            }
        }
    }
    private void crystals() {
        var server=(ServerLevel)actor.level();
        if (crystal==null && crystalId!=null) { if (server.getEntity(crystalId) instanceof EndCrystal c) crystal=c; crystalId=null; }
        if (crystal!=null && crystal.isRemoved()) {
            if (crystal.getRemovalReason()==Entity.RemovalReason.KILLED) { actor.hurtDragonPart(true,actor.damageSources().explosion(crystal,null),RemnantDragonApi.crystalDamage()); if(valid(actor.getTarget()))transition(RemnantDragonPhase.CHARGE); }
            crystal=null;
        }
        if (actor.tickCount%10==0) {
            crystal=server.getEntitiesOfClass(EndCrystal.class,actor.getBoundingBox().inflate(RemnantDragonApi.crystalRange()),c->c.isAlive())
                    .stream().min(java.util.Comparator.comparingDouble(actor::distanceToSqr)).orElse(null);
            actor.setDragonCrystal(crystal==null?null:crystal.blockPosition());
            if(crystal!=null && actor.getHealth()<actor.getMaxHealth())actor.heal(1);
        }
    }
    private void walls() {
        var server=(ServerLevel)actor.level(); blocked=false; boolean destroyed=false;
        boolean grief=net.neoforged.neoforge.event.EventHooks.canEntityGrief(server,actor);
        for(int part=0;part<3;part++) {
            AABB box=partBox(part);
            for(BlockPos pos:BlockPos.betweenClosed(BlockPos.containing(box.minX,box.minY,box.minZ),BlockPos.containing(box.maxX,box.maxY,box.maxZ))) {
                if(!server.hasChunkAt(pos))continue;
                var state=server.getBlockState(pos);
                if(state.isAir()||state.is(BlockTags.DRAGON_TRANSPARENT))continue;
                if(grief&&!state.is(BlockTags.DRAGON_IMMUNE)&&net.neoforged.neoforge.common.CommonHooks.canEntityDestroy(server,pos,actor))destroyed|=server.removeBlock(pos,false);
                else blocked=true;
            }
        }
        if(destroyed)server.levelEvent(2008,actor.blockPosition(),0);
        if(blocked)actor.setDeltaMovement(actor.getDeltaMovement().scale(.5));
    }
    void tickDeath() {
        deathTicks++; actor.deathTime=deathTicks;
        if (!actor.level().isClientSide && deathTicks==1) { actor.setDragonPhase(RemnantDragonPhase.DYING);clearFlame();actor.level().globalLevelEvent(1028,actor.blockPosition(),0); }
        actor.setDeltaMovement(Vec3.ZERO); actor.setPos(actor.position().add(0,.1,0));
        if(actor.level() instanceof ServerLevel server) {
            if(deathTicks>=180)server.sendParticles(ParticleTypes.EXPLOSION_EMITTER,actor.getX(),actor.getY()+2,actor.getZ(),1,3,2,3,0);
            if(server.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                int amount=deathTicks>150&&deathTicks%5==0?40:0;if(deathTicks==200)amount+=100;
                if(amount>0)ExperienceOrb.award(server,actor.position(),actor.dragonExperience(amount));
            }
            if(deathTicks>=200) { boss.removeAllPlayers();actor.remove(Entity.RemovalReason.KILLED);actor.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ENTITY_DIE); }
        }
    }
    void save(CompoundTag root) {
        CompoundTag tag=new CompoundTag();tag.putInt("Phase",actor.dragonPhase().ordinal());tag.putInt("Ticks",phaseTicks);tag.putInt("Cycle",attackCycle);
        tag.putInt("Breaths",flameCount);tag.putFloat("SittingDamage",sittingDamage);tag.putInt("DeathTicks",deathTicks);
        tag.putInt("FireCharge",fireCharge);tag.putInt("ChargeRecovery",chargeRecovery);
        if(center!=null)put(tag,"Center",center);if(destination!=null)put(tag,"Destination",destination);
        if(crystal!=null)tag.putUUID("Crystal",crystal.getUUID());if(flameId!=null)tag.putUUID("Flame",flameId);
        root.put("RemnantDragon",tag);
    }
    void load(CompoundTag root) {
        var tag=root.getCompound("RemnantDragon");actor.setDragonPhase(RemnantDragonPhase.fromId(tag.getInt("Phase")));
        phaseTicks=Math.clamp(tag.getInt("Ticks"),0,200);attackCycle=Math.max(0,tag.getInt("Cycle"));flameCount=Math.clamp(tag.getInt("Breaths"),0,4);
        sittingDamage=Math.max(0,tag.getFloat("SittingDamage"));deathTicks=Math.clamp(tag.getInt("DeathTicks"),0,200);
        fireCharge=Math.clamp(tag.getInt("FireCharge"),0,RemnantDragonApi.fireballWarmup());chargeRecovery=Math.clamp(tag.getInt("ChargeRecovery"),0,10);
        center=read(tag,"Center");destination=read(tag,"Destination");
        if(tag.hasUUID("Crystal"))crystalId=tag.getUUID("Crystal");if(tag.hasUUID("Flame"))flameId=tag.getUUID("Flame");
    }
    private static void put(CompoundTag tag,String key,Vec3 value) { CompoundTag v=new CompoundTag();v.putDouble("X",value.x);v.putDouble("Y",value.y);v.putDouble("Z",value.z);tag.put(key,v); }
    private static Vec3 read(CompoundTag tag,String key) { if(!tag.contains(key,10))return null;var v=tag.getCompound(key);Vec3 p=new Vec3(v.getDouble("X"),v.getDouble("Y"),v.getDouble("Z"));return Double.isFinite(p.lengthSqr())?p:null; }
}
