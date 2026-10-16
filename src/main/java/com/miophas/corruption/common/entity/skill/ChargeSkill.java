package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.core.demon.skill.ChargeRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

/**
 * The charge ({@link ChargeRules}) as the mod carries it out for any boss. Through the wind-up the boss stands, faces
 * its quarry and churns the ground; then it rushes along the line it last saw it on, every tick breaking whatever lies
 * in the way of its body that gives way (anything softer than obsidian, with its drops, as long as the game lets
 * creatures break blocks: the mobGriefing rule) and ramming whatever it meets, once each. What will not give way stops
 * it with a crash.
 */
public final class ChargeSkill implements BossSkill<Mob> {
    /** Its feet clear the ground by this much, so it breaks what stands in its way, not what it runs on. */
    private static final double FEET = .3;

    private final ChargeRules rules;
    private final Set<UUID> rammed = new HashSet<>();
    private Vec3 way = new Vec3(0, 0, 1);
    private boolean stopped;

    public ChargeSkill(ChargeRules rules) { this.rules = rules; }

    public ChargeRules rules() { return rules; }

    /** The way it rushes, along the ground, of length one. */
    public Vec3 way() { return way; }

    /** Whether something that would not give way has stopped it. */
    public boolean stopped() { return stopped; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)); }

    @Override public void begin(Mob boss, LivingEntity target) {
        rammed.clear();
        stopped = false;
        way = along(target.position().subtract(boss.position()), boss);
        boss.getNavigation().stop();
        SkillShows.sound(boss, SoundEvents.RAVAGER_ROAR, 2, .7F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (rules.timing().windingUp(elapsed)) {
            if (target != null) way = along(target.position().subtract(boss.position()), boss);
            face(boss, way);
            boss.getNavigation().stop();
            boss.setDeltaMovement(boss.getDeltaMovement().multiply(.3, 1, .3));
            if (elapsed % 3 == 0 && boss.level() instanceof ServerLevel level) {
                BlockState under = level.getBlockState(boss.blockPosition().below());
                if (!under.isAir()) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, under), boss.getX(), boss.getY() + .1, boss.getZ(), 12, .8, .1, .8, .1);
            }
            return;
        }
        if (rules.timing().strikes(elapsed)) SkillShows.sound(boss, SoundEvents.RAVAGER_ATTACK, 2, .6F);
        if (stopped || !rules.rushing(elapsed) || !(boss.level() instanceof ServerLevel level)) return;
        face(boss, way);
        Vec3 step = way.scale(rules.speed());
        AABB body = boss.getBoundingBox(), ahead = body.move(step).inflate(.15, 0, .15);
        if (!giveWay(level, boss, new AABB(ahead.minX, body.minY + FEET, ahead.minZ, ahead.maxX, ahead.maxY, ahead.maxZ))) {
            crash(level, boss);
            return;
        }
        boss.setDeltaMovement(step.x, boss.getDeltaMovement().y, step.z);
        boss.hurtMarked = true;
        level.sendParticles(ParticleTypes.CLOUD, boss.getX(), boss.getY() + .4, boss.getZ(), 4, .6, .2, .6, .02);
        for (LivingEntity met : level.getEntitiesOfClass(LivingEntity.class, body.expandTowards(step).inflate(.6),
                met -> met != boss && met.isAlive() && !boss.isAlliedTo(met) && !rammed.contains(met.getUUID())))
            ram(boss, met);
    }

    @Override public void end(Mob boss, boolean broken) {
        rammed.clear();
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(.3, 1, .3));
    }

    /**
     * Breaks every block in this box that gives way to the charge, and whether the box is clear now: nothing left in
     * it that would stop a body (water, grass and the like stop nothing).
     */
    private boolean giveWay(ServerLevel level, Mob boss, AABB box) {
        boolean griefing = EventHooks.canEntityGrief(level, boss), clear = true;
        for (BlockPos at : BlockPos.betweenClosed(Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ),
                Mth.floor(box.maxX - 1e-7), Mth.floor(box.maxY - 1e-7), Mth.floor(box.maxZ - 1e-7))) {
            BlockState state = level.getBlockState(at);
            if (state.isAir() || !state.getFluidState().isEmpty() && state.getCollisionShape(level, at).isEmpty()) continue;
            if (griefing && rules.breaks(state.getDestroySpeed(level, at))) level.destroyBlock(at, true, boss);
            else if (!state.getCollisionShape(level, at).isEmpty()) clear = false;
        }
        return clear;
    }

    /** Something would not give way: the charge stops dead, with a crash. */
    private void crash(ServerLevel level, Mob boss) {
        stopped = true;
        boss.setDeltaMovement(0, boss.getDeltaMovement().y, 0);
        SkillShows.sound(boss, SoundEvents.ANVIL_LAND, 1.5F, .5F);
        Vec3 front = boss.position().add(way.scale(boss.getBbWidth() * .6)).add(0, boss.getBbHeight() * .4, 0);
        level.sendParticles(ParticleTypes.EXPLOSION, front.x, front.y, front.z, 2, .3, .3, .3, 0);
    }

    /** Rams something: its blow, and it is thrown off along the charge and up. */
    private void ram(Mob boss, LivingEntity met) {
        rammed.add(met.getUUID());
        if (!met.hurt(boss.damageSources().mobAttack(boss), (float) rules.damage())) return;
        met.push(way.x * rules.knockback(), .5, way.z * rules.knockback());
        met.hurtMarked = true;
        SkillShows.sound(boss, SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.5F, .6F);
    }

    /** A way along the ground, of length one; the way the boss faces if this one has no length along the ground. */
    private static Vec3 along(Vec3 toward, Mob boss) {
        Vec3 level = new Vec3(toward.x, 0, toward.z);
        if (level.lengthSqr() < 1e-6) {
            float yaw = boss.getYRot() * Mth.DEG_TO_RAD;
            return new Vec3(-Mth.sin(yaw), 0, Mth.cos(yaw));
        }
        return level.normalize();
    }

    private static void face(Mob boss, Vec3 way) {
        float yaw = (float) (Mth.atan2(way.z, way.x) * Mth.RAD_TO_DEG) - 90;
        boss.setYRot(yaw);
        boss.yBodyRot = yaw;
        boss.yHeadRot = yaw;
    }
}
