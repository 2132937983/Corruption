package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.api.demon.skill.BossSkillApi;
import com.miophas.corruption.api.demon.skill.BossSkillRegistry;
import com.miophas.corruption.api.demon.skill.RegisteredSkill;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import com.miophas.corruption.core.demon.skill.SpinRules;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * A spinning charge ({@link SpinRules}) as the mod carries it out for any boss that is a wheel of blades. Through the
 * wind-up it spins up where it hangs, facing its quarry; then it flies at the quarry, after it wherever it goes, and on
 * every beat everything within its reach is struck and thrown away from it, in a sweep of light. While it runs, it
 * moves the boss itself.
 */
public final class SpinSkill implements RegisteredSkill<SpinRules> {
    private final SpinRules rules;
    private int blows;

    public SpinSkill(SpinRules rules) { this.rules = rules; }

    public SpinRules rules() { return rules; }

    /** How many creatures this use has struck, counting each blow. */
    public int blows() { return blows; }

    @Override public SkillTiming timing() { return rules.timing(); }

    @Override public boolean usable(Mob boss, LivingEntity target) { return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target); }

    @Override public void begin(Mob boss, LivingEntity target) {
        blows = 0;
        boss.getNavigation().stop();
        SkillShows.sound(boss, SoundEvents.AMETHYST_BLOCK_RESONATE, 2, .8F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        if (rules.timing().windingUp(elapsed)) {
            boss.setDeltaMovement(boss.getDeltaMovement().scale(.5));
            if (target != null) MeleeSkill.face(boss, target.position());
            return;
        }
        if (target != null) {
            Vec3 to = target.getBoundingBox().getCenter().subtract(boss.getBoundingBox().getCenter());
            boss.setDeltaMovement(to.lengthSqr() < 1e-4 ? Vec3.ZERO : to.normalize().scale(Math.min(rules.speed(), to.length())));
            boss.hurtMarked = true;
            MeleeSkill.face(boss, target.position());
        }
        if (!rules.strikes(elapsed) || !(boss.level() instanceof ServerLevel level)) return;
        Vec3 heart = boss.getBoundingBox().getCenter();
        for (LivingEntity met : level.getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(rules.reach()), met -> met != boss && met.isAlive()
                && !boss.isAlliedTo(met) && met.getBoundingBox().distanceToSqr(heart) <= rules.reach() * rules.reach())) {
            met.invulnerableTime = 0;
            if (!met.hurt(boss.damageSources().mobAttack(boss), (float) rules.damage())) continue;
            blows++;
            Vec3 away = met.position().subtract(boss.position()).multiply(1, 0, 1);
            away = away.lengthSqr() < 1e-4 ? new Vec3(1, 0, 0) : away.normalize();
            met.push(away.x * rules.knockback(), .25, away.z * rules.knockback());
            met.hurtMarked = true;
        }
        level.sendParticles(ParticleTypes.SWEEP_ATTACK, heart.x, heart.y, heart.z, 3, rules.reach() / 2, .3, rules.reach() / 2, 0);
        SkillShows.sound(boss, SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, .7F);
    }
    @Override public BossSkillRegistry.Type<SpinRules> type() { return BossSkillApi.SPIN; }
    @Override public SpinRules configuration() { return rules; }
    @Override public BossSkillRegistry.Options options() { return new BossSkillRegistry.Options(0, 0, mob -> Integer.MAX_VALUE, false); }
}
