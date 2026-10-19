package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.api.demon.skill.SpitCaster;
import com.miophas.corruption.common.entity.DemonBolt;
import com.miophas.corruption.core.demon.skill.FanfareRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * A fanfare of the corruption's bolts out of a horn ({@link FanfareRules}) as the mod carries it out for any creature.
 * Through the wind-up it faces its quarry and draws breath: a horn sounds, and music rings about where its bolts will
 * leave it (where a {@link SpitCaster} says, else from before its eyes). Then, for as long as it plays, its bolts
 * ({@link DemonBolt}) pour out of there, two arms of them sweeping back and forth across where the quarry is as each is
 * loosed, a fan straight at it now and then, notes flying off the horn and its call sounding again and again.
 */
public final class FanfareSkill implements RegisteredSkill<FanfareRules> {
    private final FanfareRules rules;
    private final int colour, accent;
    private int loosed;

    public FanfareSkill(FanfareRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public FanfareRules rules() { return rules; }

    /** How many bolts this use has loosed. */
    public int loosed() { return loosed; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        loosed = 0;
        SkillShows.sound(boss, SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(2).value(), 4, .8F);
        if (boss.level() instanceof ServerLevel level)
            SkillShows.show(level, from(boss), "resonance", 1.4, 1, rules.timing().windup() + 4, colour, accent, .1, boss.getYRot(), false);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(.3, 1, .3));
        if (target != null) {
            MeleeSkill.face(boss, target.position());
            boss.getLookControl().setLookAt(target, 40, 40);
        }
        if (!rules.playing(elapsed) || !(boss.level() instanceof ServerLevel level)) return;
        Vec3 from = from(boss);
        Vec3 aim = target != null ? target.getBoundingBox().getCenter().subtract(from) : Vec3.directionFromRotation(0, boss.getYRot());
        if (aim.lengthSqr() < 1e-6) aim = Vec3.directionFromRotation(0, boss.getYRot());
        float yaw = (float) (Mth.atan2(-aim.x, aim.z) * Mth.RAD_TO_DEG), pitch = (float) (-Mth.atan2(aim.y, aim.horizontalDistance()) * Mth.RAD_TO_DEG);
        for (int index = 0; index < rules.perTick(); index++) {
            double[] off = rules.spray(elapsed, index);
            loose(level, boss, from, yaw + (float) off[0], pitch - (float) off[1]);
        }
        if (rules.volleys(elapsed))
            for (int index = 0; index < rules.aimed(); index++) loose(level, boss, from, yaw + (float) rules.volleyAcross(index), pitch);
        if ((elapsed - rules.timing().windup()) % 10 == 0) {
            SkillShows.sound(boss, SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(5).value(), 3, .7F + boss.getRandom().nextFloat() * .2F);
            level.sendParticles(ParticleTypes.NOTE, from.x, from.y + .3, from.z, 0, boss.getRandom().nextDouble(), 0, 0, 1);
        }
    }

    private void loose(ServerLevel level, Mob boss, Vec3 from, float yaw, float pitch) {
        Vec3 velocity = Vec3.directionFromRotation(pitch, yaw).scale(rules.speed());
        if (DemonBolt.loose(level, boss, from, velocity, rules.damage(), rules.splash(), 0, colour) != null) loosed++;
    }

    /** Where its bolts leave it. */
    private static Vec3 from(Mob boss) {
        return boss instanceof SpitCaster caster ? caster.spitFrom(0) : boss.getEyePosition().add(Vec3.directionFromRotation(0, boss.getYRot()).scale(boss.getBbWidth() * .6));
    }
    @Override public BossSkillRegistry.Type<FanfareRules> type() { return BossSkillApi.FANFARE; }
    @Override public FanfareRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(colour, accent, mob -> Integer.MAX_VALUE, false); }
}
