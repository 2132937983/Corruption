package com.miophas.corruption.common.entity.skill;

import com.miophas.corruption.api.demon.skill.BossSkill;
import com.miophas.corruption.core.demon.skill.PounceRules;
import com.miophas.corruption.core.demon.skill.SkillTiming;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * A pounce ({@link PounceRules}) as the mod carries it out for any creature that walks. Through the wind-up it crouches,
 * holding still and facing its quarry; then it springs at where its quarry stands, along a low arc it is carried along
 * tick by tick (so it comes down where it meant to, unless something stops it). The first thing not of its side it meets
 * on the way is struck and thrown on along the leap, and the pounce is over; otherwise it is over when it is down, the
 * ground shaken where it lands.
 */
public final class PounceSkill implements BossSkill<Mob> {
    private final PounceRules rules;
    private final int colour, accent;
    @Nullable private Vec3 from, aim;
    @Nullable private LivingEntity struck;
    private boolean down;

    public PounceSkill(PounceRules rules, int colour, int accent) {
        this.rules = rules;
        this.colour = colour;
        this.accent = accent;
    }

    public PounceRules rules() { return rules; }

    /** Where this use leaps to, once it has sprung; and what it struck, if it struck anything. */
    @Nullable public Vec3 aim() { return aim; }
    @Nullable public LivingEntity struck() { return struck; }

    @Override public SkillTiming timing() { return rules.timing(); }

    /** At a quarry in range and in sight, from the ground (or the water's floor). */
    @Override public boolean usable(Mob boss, LivingEntity target) {
        return rules.inRange(boss.distanceTo(target)) && boss.hasLineOfSight(target) && (boss.onGround() || boss.isInWater());
    }

    @Override public void begin(Mob boss, LivingEntity target) {
        from = aim = null;
        struck = null;
        down = false;
        boss.getNavigation().stop();
        SkillShows.sound(boss, SoundEvents.RAVAGER_STEP, 1.5F, .6F);
        SkillShows.sound(boss, SoundEvents.SPIDER_AMBIENT, 1.5F, .5F);
    }

    @Override public void tick(Mob boss, @Nullable LivingEntity target, int elapsed) {
        boss.getNavigation().stop();
        if (rules.timing().windingUp(elapsed)) {
            boss.setDeltaMovement(boss.getDeltaMovement().multiply(.2, 1, .2));
            if (target != null) MeleeSkill.face(boss, target.position());
            return;
        }
        if (rules.timing().strikes(elapsed)) spring(boss, target);
        if (aim == null || from == null || struck != null || down) return;
        int leapt = elapsed - rules.timing().windup() + 1;
        Vec3 line = aim.subtract(from);
        Vec3 wanted = from.add(line.scale(rules.along(leapt))).add(0, rules.over(leapt), 0);
        boss.setDeltaMovement(wanted.subtract(boss.position()));
        boss.hurtMarked = true;
        boss.fallDistance = 0;
        MeleeSkill.face(boss, aim);
        if (boss.level() instanceof ServerLevel level) {
            if (meet(level, boss, line)) return;
            if (leapt >= rules.flight()) land(level, boss);
        }
    }

    /** It springs at where its quarry stands now (or straight ahead, if its quarry is gone). */
    private void spring(Mob boss, @Nullable LivingEntity target) {
        from = boss.position();
        aim = target != null ? target.position() : from.add(Vec3.directionFromRotation(0, boss.getYRot()).scale(rules.maxRange() / 2));
        SkillShows.sound(boss, SoundEvents.RAVAGER_ATTACK, 2, .7F);
        if (boss.level() instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.CLOUD, from.x, from.y + .2, from.z, 8, boss.getBbWidth() / 2, .1, boss.getBbWidth() / 2, .05);
    }

    /** Whether it meets something on the way: the first thing not of its side within its reach is struck and thrown on along the leap. */
    private boolean meet(ServerLevel level, Mob boss, Vec3 line) {
        Vec3 heart = boss.getBoundingBox().getCenter();
        LivingEntity met = level.getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(rules.reach()), creature -> creature != boss
                        && creature.isAlive() && !boss.isAlliedTo(creature) && !(creature instanceof Player player && (player.isCreative() || player.isSpectator())))
                .stream().min(java.util.Comparator.comparingDouble(creature -> creature.getBoundingBox().getCenter().distanceToSqr(heart))).orElse(null);
        if (met == null) return false;
        struck = met;
        if (met.hurt(boss.damageSources().mobAttack(boss), (float) rules.damage())) {
            Vec3 on = line.multiply(1, 0, 1);
            on = on.lengthSqr() < 1e-6 ? Vec3.directionFromRotation(0, boss.getYRot()) : on.normalize();
            met.push(on.x * rules.knockback(), .35, on.z * rules.knockback());
            met.hurtMarked = true;
        }
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(.2, .5, .2));
        SkillShows.sound(boss, SoundEvents.PLAYER_ATTACK_CRIT, 1.5F, .6F);
        SkillShows.sound(boss, SoundEvents.RAVAGER_ATTACK, 1.5F, 1F);
        level.sendParticles(ParticleTypes.CRIT, met.getX(), met.getY() + met.getBbHeight() / 2, met.getZ(), 12, .4, .4, .4, .2);
        return true;
    }

    /** It is down: the ground shaken where it lands, a little ring of force over it. */
    private void land(ServerLevel level, Mob boss) {
        down = true;
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(.2, 1, .2));
        Vec3 at = boss.position();
        BlockState ground = level.getBlockState(BlockPos.containing(at).below());
        if (!ground.isAir()) level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), at.x, at.y + .1, at.z, 24, .8, .1, .8, .15);
        SkillShows.show(level, at, "shockwave", 2.5, 1, 14, colour, accent, .2, boss.getYRot(), true);
        SkillShows.sound(boss, SoundEvents.RAVAGER_STEP, 2, .5F);
    }

    /** Over as soon as it has struck something, or is down. */
    @Override public boolean finished(Mob boss) { return struck != null || down; }

    @Override public void end(Mob boss, boolean broken) {
        from = aim = null;
        boss.fallDistance = 0;
    }
}
