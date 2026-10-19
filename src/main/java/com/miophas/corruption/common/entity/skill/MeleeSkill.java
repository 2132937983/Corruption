package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.core.demon.skill.MeleeRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
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
import net.minecraft.world.phys.Vec3;

/**
 * A blow struck close in ({@link MeleeRules}) as the mod carries it out for any boss: through the wind-up it faces its
 * quarry and holds still, drawn back or reared up as its model shows it; at the end of it, it strikes everything within
 * its reach before it, in its arc, and throws it off; and the ground where the blow comes down is shaken.
 */
public final class MeleeSkill implements RegisteredSkill<MeleeRules> {
    private final MeleeRules rules;

    public MeleeSkill(MeleeRules rules) { this.rules = rules; }

    public MeleeRules rules() { return rules; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        boss.getNavigation().stop();
        SkillShows.sound(boss, SoundEvents.HORSE_ANGRY, 2, .6F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.getNavigation().stop();
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(.3, 1, .3));
        if (rules.timing().windingUp(elapsed)) {
            if (target != null) face(boss, target.position());
            return;
        }
        if (rules.timing().strikes(elapsed) && boss.level() instanceof ServerLevel level) strike(level, boss);
    }

    /** The blow comes down: everything within its reach and arc is struck and thrown off, and the ground before it shaken. */
    private void strike(ServerLevel level, Mob boss) {
        for (LivingEntity met : level.getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(rules.reach()), met -> met != boss && met.isAlive()
                && !boss.isAlliedTo(met) && rules.catches(boss.distanceTo(met), offDegrees(boss, met.position())))) {
            if (!met.hurt(boss.damageSources().mobAttack(boss), (float) rules.damage())) continue;
            Vec3 away = met.position().subtract(boss.position()).multiply(1, 0, 1);
            away = away.lengthSqr() < 1e-4 ? boss.getLookAngle().multiply(1, 0, 1) : away.normalize();
            met.push(away.x * rules.knockback(), rules.lift(), away.z * rules.knockback());
            met.hurtMarked = true;
        }
        Vec3 down = boss.position().add(Vec3.directionFromRotation(0, boss.getYRot()).scale(boss.getBbWidth() * .7));
        BlockState ground = level.getBlockState(BlockPos.containing(down).below());
        if (!ground.isAir()) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), down.x, down.y + .1, down.z, 30, .8, .1, .8, .15);
        SkillShows.sound(boss, SoundEvents.ANVIL_LAND, 1.2F, .5F);
        SkillShows.sound(boss, SoundEvents.HORSE_LAND, 2, .6F);
    }

    /** How far round from the way the boss faces a place is, in degrees. */
    static double offDegrees(Mob boss, Vec3 at) {
        Vec3 to = at.subtract(boss.position());
        return Mth.wrapDegrees(Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG - 90 - boss.getYRot());
    }

    /** Turns the boss, body and head, to face a place. */
    static void face(Mob boss, Vec3 at) {
        Vec3 to = at.subtract(boss.position());
        if (to.horizontalDistanceSqr() < 1e-6) return;
        float yaw = (float) (Mth.atan2(to.z, to.x) * Mth.RAD_TO_DEG) - 90;
        boss.setYRot(yaw);
        boss.yBodyRot = yaw;
        boss.yHeadRot = yaw;
    }
    @Override public BossSkillRegistry.Type<MeleeRules> type() { return BossSkillApi.MELEE; }
    @Override public MeleeRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(0, 0, mob -> Integer.MAX_VALUE, false); }
}
